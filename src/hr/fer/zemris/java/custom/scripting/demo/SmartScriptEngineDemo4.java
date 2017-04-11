package hr.fer.zemris.java.custom.scripting.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.fer.zemris.java.custom.scripting.exec.SmartScriptEngine;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.webserver.RequestContext;
import hr.fer.zemris.java.webserver.RequestContext.RCCookie;

/**
 * Represents the demonstration class for {@link SmartScriptEngine}. Each class represents one
 * specific case of smartscript testing.
 * 
 * Smart script is a script file with extension .smsrcr.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class SmartScriptEngineDemo4 {

    /**
     * Demonstration of the smart sript instance. Entry point of the program.
     * 
     * Sets the {@link RequestContext}, {@link SmartScriptParser} and all of the necessary
     * environment.
     * 
     * @param args
     *            Command line arguments - not used.
     * @throws IOException
     *             In case of IO error.
     */
    public static void main(String[] args) throws IOException {
        StringBuilder sb = new StringBuilder();
        Files.readAllLines(Paths.get("webroot/scripts/fibonacci.smscr"))
	      .forEach(l -> sb.append(l + "\r\n"));
        String documentBody = sb.toString();

        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> persistentParameters = new HashMap<String, String>();
        List<RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
        // create engine and execute it
        new SmartScriptEngine(new SmartScriptParser(documentBody).getDocumentNode(),
	      new RequestContext(System.out, parameters, persistentParameters, cookies))
		    .execute();
    }
}
