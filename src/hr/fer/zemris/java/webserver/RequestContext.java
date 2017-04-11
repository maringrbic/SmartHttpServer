package hr.fer.zemris.java.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents the request context which can store all necesarry informations about client's request.
 * 
 * Context contains client's stream and some additional informations like charset, status code,
 * mime type, parameters, cookies and others.
 * 
 * Provides two methods for writing. Both of these write methods write its data into output stream 
 * that was given to RequestContext in constructor. Assures that during any writing, an 
 * appropriate header will be created.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class RequestContext {

    /**
     * Represents the output stream used for writing the response to the client.
     */
    private OutputStream outputStream;

    /**
     * Represents the charset used for encoding.
     */
    private Charset charset;

    /**
     * Represents the name of encoding.
     */
    private String encoding = "UTF-8";

    /**
     * Represents the status code of the request.
     */
    private int statusCode = 200;

    /**
     * Represents the status text of the request.
     */
    private String statusText = "OK";

    /**
     * Represents the mime type of the request.
     */
    private String mimeType = "text/html";

    /**
     * Represents the parameters of the request.
     */
    private final Map<String, String> parameters;

    /**
     * Represents the temporary parameters of the request.
     */
    private Map<String, String> temporaryParameters = new HashMap<>();

    /**
     * Represents the persistent parameters of the request.
     */
    private Map<String, String> persistentParameters;

    /**
     * Represents the output cookies of the request.
     */
    private List<RCCookie> outputCookies;

    /**
     * Represents the flag which indicates if the header is already generated.
     */
    private boolean headerGenerated;

    /**
     * Public constructor.
     * Sets fields to the given values. 
     * 
     * @param outputStream The client's output stream, must not be <code>null</code>.
     * @param parameters Parameters of the request, if <code>null</code> will be considered
     * as an empty {@link Map}.
     * @param persistentParameters Persistent parameters of the request, if <code>null</code> 
     * will be considered as an empty {@link Map}.
     * @param outputCookies Output cookies of the request, if <code>null</code> will be considered
     * as an empty list.
     */
    public RequestContext(OutputStream outputStream, Map<String, String> parameters,
	  Map<String, String> persistentParameters, List<RCCookie> outputCookies) {
        if (outputStream == null) {
	  throw new IllegalArgumentException("Output stream must not be null!");
        }

        this.outputStream = outputStream;
        this.parameters = 
	      parameters == null ? new HashMap<String, String>() : parameters;
        this.persistentParameters = 
	      persistentParameters == null ? new HashMap<String, String>() : persistentParameters;
        this.outputCookies = 
	      outputCookies == null ? new ArrayList<RCCookie>() : outputCookies;
    }

    /**
     * The encoding setter. 
     * Can be invoked only before the header of the response was generated.
     * 
     * @param encoding The encoding to set.
     */
    public void setEncoding(String encoding) {
        assumeHeaderNotGenerated();
        this.encoding = encoding;
    }

   
    /**
     * The status code setter.
     * Can be invoked only before the header of the response was generated.
     * 
     * @param statusCode The status code to set.
     */
    public void setStatusCode(int statusCode) {
        assumeHeaderNotGenerated();
        this.statusCode = statusCode;
    }

    /**
     * The status text setter.
     * Can be invoked only before the header of the response was generated.
     * 
     * @param statusText The status text to set.
     */
    public void setStatusText(String statusText) {
        assumeHeaderNotGenerated();
        this.statusText = statusText;
    }

    /**
     * The mime type setter.
     * Can be invoked only before the header of the response was generated.
     * 
     * @param mimeType The mime type to set.
     */
    public void setMimeType(String mimeType) {
        assumeHeaderNotGenerated();
        this.mimeType = mimeType;
    }

    /**
     * Retrieves value from parameters map (or null if no association exists).
     * 
     * @param name Key used for fetching the value.
     * @return The parameter to retrieve.
     */
    public String getParameter(String name) {
        return parameters.get(name);
    }

    /**
     * Retrieves names of all parameters in parameters map.
     * The returned set is read-only.
     * 
     * @return Set of keys used for storing the parameters.
     */
    public Set<String> getParameterNames() {
        return Collections.unmodifiableSet(parameters.keySet());
    }

    /**
     * Retrieves value from persistent parameters map (or null if no association exists).
     * 
     * @param name Key used for fetching the value.
     * @return The parameter to retrieve.
     */
    public String getPersistentParameter(String name) {
        return persistentParameters.get(name);
    }

    /**
     * Retrieves names of all parameters in persistent parameters map.
     * The returned set is read-only.
     * 
     * @return Set of keys used for storing the parameters.
     */
    public Set<String> getPersistentParameterNames() {
        return Collections.unmodifiableSet(persistentParameters.keySet());
    }

    /** 
     * Stores a value to persistent parameters map. 
     * 
     * @param name Key used for fetching the value.
     * @param value Value of the parameter.
     */
    public void setPersistentParameter(String name, String value) {
        persistentParameters.put(name, value);
    }

    /** 
     * Removes a value from persistent parameters map. 
     * 
     * @param name Name of the parameter to remove.
     */
    public void removePersistentParameter(String name) {
        persistentParameters.remove(name);
    }

    /**
     * Retrieves value from temporary parameters map (or null if no association exists).
     * 
     * @param name Key used for fetching the value.
     * @return The parameter to retrieve.
     */
    public String getTemporaryParameter(String name) {
        return temporaryParameters.get(name);
    }

    /**
     * Retrieves names of all parameters in temporary parameters map (note, this set
     * The returned set is read-only.
     * 
     * @return Set of keys used for storing the parameters.
     */
    public Set<String> getTemporaryParameterNames() {
        return Collections.unmodifiableSet(temporaryParameters.keySet());
    }

    /** 
     * Stores a value to temporary parameters map. 
     * 
     * @param name Key used for fetching the value.
     * @param value Value of the parameter.
     */
    public void setTemporaryParameter(String name, String value) {
        temporaryParameters.put(name, value);
    }

    /** 
     * Removes a value from temporary parameters map. 
     * 
     * @param name Name of the parameter to remove.
     */
    public void removeTemporaryParameter(String name) {
        temporaryParameters.remove(name);
    }

    /**
     * Writes a byte array to the clients output stream. 
     * This is a simple action which writes pure bytes to the {@link OutputStream}.
     * 
     * @param data Array of bytes to be written to the {@link OutputStream}.
     * @return Returns this.
     * @throws IOException In case of IO error.
     */
    public RequestContext write(byte[] data) throws IOException {

        if(!headerGenerated) {
	  charset = Charset.forName(encoding);
	  generateHeader();
	  headerGenerated = true;
        }
        
        outputStream.write(data);
        outputStream.flush();
        
        return this;
    }

    /**
     * Writes a string to the clients output stream.
     * 
     * Depending on which part of response is  being created, uses different 
     * encoding: ISO-8859-1 for header or UTF-8 for the response.
     * 
     * @param text Text to be written to the {@link OutputStream}.
     * @return Returns this.
     * @throws IOException In case of IO error.
     */
    public RequestContext write(String text) throws IOException {
        if(!headerGenerated) {
	  charset = Charset.forName(encoding);
	  generateHeader();
	  headerGenerated = true;
        }
        return write(text.getBytes(charset));
    }
    
    /**
     * Adds a new {@link RCCookie} to the current collection.
     * Can be invoked only before the header of the response was generated.
     * 
     * @param rcCookie Instane of {@link RCCookie} to be added to the collection.
     */
    public void addRCCookie(RCCookie rcCookie) {
        assumeHeaderNotGenerated();
        outputCookies.add(rcCookie);
    }
    
    /**
     * Writes a predefined header layout to the client's {@link OutputStream}.
     * 
     * Header contains informations about the response, such as HTTP version, mime type, 
     * cookies and etc.
     *s
     * @throws IOException In case of IO error during writing.
     */
    private void generateHeader() throws IOException {

        StringBuilder sb = new StringBuilder();
        
        sb.append("HTTP/1.1 "+statusCode+" "+statusText+"\r\n");
        sb.append("Content-Type: "+mimeType);
        sb.append(mimeType.startsWith("text/") ? "; charset=" + encoding : "");
        sb.append("\r\n");
        
        if(!this.outputCookies.isEmpty()) {
	  for(RCCookie cookie : this.outputCookies) {
	      sb.append("Set-Cookie: ");
	      if(cookie.name != null && cookie.value != null) {
		sb.append(cookie.name+"=\""+cookie.value+"\"");
	      }
	      if(cookie.domain != null) {
		sb.append("; Domain="+cookie.domain);
	      }
	      if(cookie.path != null) {
		sb.append("; Path="+cookie.path);
	      }
    	      if(cookie.maxAge != null) {
    		sb.append("; Max-Age="+cookie.maxAge);
    	      }
    	      sb.append("; HttpOnly");
    	      sb.append("\r\n");
	  }
        }
        sb.append("\r\n");

        outputStream.write(sb.toString().getBytes(StandardCharsets.ISO_8859_1));
    }

    /**
     *  Checks if the header is already generated. 
     *  This method is used to prevent any changes after the header was created.
     *  
     *  If any change was made after header creation, {@link RuntimeException} will be thrown.
     */
    private void assumeHeaderNotGenerated() {
        if(headerGenerated) {
	  throw new RuntimeException("Can not change the value after header was generated.");
        }
    }


    /**
     * Represents the cookie used in this type of request - {@link RequestContext}.
     * This cookie is available to store informations such as name, value, domain, path and maxAge
     * which are the informations needed for storing a cookie to a web browser.
     * 
     * When a server wishes to store a cookie in client's browser, it must write a "Set-Cookie:"
     * command in the generated header.
     * 
     * @author Marin Grbić
     * @version 1.0
     */
    public static class RCCookie {

        /**
         * Represents the name of the cookie.
         */
        private final String name;

        /**
         * Represents the value of the cookie.
         */
        private final String value;

        /**
         * Represents the domain of the cookie.
         */
        private final String domain;

        /**
         * Represents the path of the cookie.
         */
        private final Path path;

        /**
         * Represents the maximal age of the cookie.
         */
        private final Integer maxAge;

        /**
         * Public constructor.
         * Sets fields to the given values.
         * 
         * @param name Name of the cookie.
         * @param value Value of the cookie.
         * @param domain Domain of the cookie.
         * @param path Path of the cookie.
         * @param maxAge Maximal age of the cookie.
         */
        public RCCookie(String name, String value, Integer maxAge, String domain, String path) {
	  super();
	  this.name = name;
	  this.value = value;
	  this.domain = domain;
	  this.path = Paths.get(path);
	  this.maxAge = maxAge;
        }

        /**
         * The name getter.
         * 
         * @return String Gets the name.
         */
        public String getName() {
	  return name;
        }

        /**
         * The value getter.
         * 
         * @return String Gets the value.
         */
        public String getValue() {
	  return value;
        }

        /**
         * The domain getter.
         * 
         * @return String Gets the domain.
         */
        public String getDomain() {
	  return domain;
        }

        /**
         * The path getter.
         * 
         * @return Path Gets the path.
         */
        public Path getPath() {
	  return path;
        }

        /**
         * The maximal age getter.
         * 
         * @return Integer Gets the maximal age.
         */
        public Integer getMaxAge() {
	  return maxAge;
        }
    }
}
