package hr.fer.zemris.java.webserver;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import hr.fer.zemris.java.webserver.RequestContext.RCCookie;


/**
 * Represents the testing class.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class RequestContextTests {

    @Test
    (expected = IllegalArgumentException.class)
    public void testConstructor1() {
        new RequestContext(null, new HashMap<>(), new HashMap<>(), new ArrayList<>());
    }
    
    @Test
    public void testConstructor2() throws IOException {
        RequestContext rc1 = new RequestContext(
	      new FileOutputStream("primjer1.txt"),
	      null, // -> parameters as null
	      new HashMap<>(), 
	      new ArrayList<>()
	      );
        RequestContext rc2 = new RequestContext(
	      Files.newOutputStream(Paths.get("primjer1.txt")), 
	      new HashMap<>(), 
	      null, // -> persistent parameters as null
	      new ArrayList<>()
	      );

        //assert that if null was provided, a new empty hashmap will be created
        assertEquals(0, rc1.getParameterNames().size());
        assertEquals(0, rc2.getPersistentParameterNames().size());
    }
    
    @Test
    public void testWrite() throws IOException {
        RequestContext rc = new RequestContext(
	      new FileOutputStream("test.txt"),
	      null, // -> parameters as null
	      new HashMap<>(), 
	      new ArrayList<>()
	      );
        
        assertEquals(rc, rc.write("some string"));
        
        InputStream is = new FileInputStream("test.txt");
        
        int r;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((r = is.read()) > 0) {
	  bos.write(r);
        }
        
        String input = bos.toString("UTF-8");
        assertTrue(
	      input.equals(
		"HTTP/1.1 200 OK\r\n" +
		"Content-Type: text/html; charset=UTF-8\r\n\r\n" +
		"some string"
		)
	      );
        is.close();
    }
    
    @Test
    (expected = RuntimeException.class)
    public void testHeaderNotGenerated() throws IOException {
        RequestContext rc = new RequestContext(
	      new FileOutputStream("test.txt"),
	      null, // -> parameters as null
	      new HashMap<>(), 
	      new ArrayList<>()
	      );
        
        rc.write("some text");
        
        //try to add cookie after creating header
        rc.addRCCookie(new RCCookie("name", "value", 0, "domain", "path"));
    }
    
    @Test
    public void testAddingCookies() throws IOException {
        RequestContext rc = new RequestContext(
	      new FileOutputStream("test.txt"),
	      null, // -> parameters as null
	      new HashMap<>(), 
	      new ArrayList<>()
	      );
  
        rc.addRCCookie(new RCCookie("name1", "value1", 0, "domain1", "path1"));
        rc.addRCCookie(new RCCookie("name2", "value2", 0, "domain2", "path2"));
        
        rc.write(new byte[]{});
        
        InputStream is = new FileInputStream("test.txt");
        
        int r;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((r = is.read()) > 0) {
	  bos.write(r);
        }

        byte[] expected = new String(
	      "HTTP/1.1 200 OK\r\n" +
		"Content-Type: text/html; charset=UTF-8\r\n"+
		"Set-Cookie: name1=\"value1\"; Domain=domain1; Path=path1; Max-Age=0; HttpOnly\r\n"+
		"Set-Cookie: name2=\"value2\"; Domain=domain2; Path=path2; Max-Age=0; HttpOnly\r\n\r\n"
		).getBytes("UTF-8");

        assertArrayEquals(expected, bos.toByteArray());
        is.close();
    }
}
