package hr.fer.zemris.java.webserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import hr.fer.zemris.java.custom.scripting.exec.SmartScriptEngine;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.webserver.RequestContext.RCCookie;
import hr.fer.zemris.java.webserver.workers.CircleWorker;
import hr.fer.zemris.java.webserver.workers.EchoParams;
import hr.fer.zemris.java.webserver.workers.HelloWorker;

/**
 * Represents a simple but functional web server which uses HTTP protocol for serving the clients.
 * 
 * <p>Some of the functionalities of this server are:
 * </br>
 * * responsing to simple requests, such as html, text and image files
 * </br>
 * * parsing and executing smart scripts, using {@link SmartScriptEngine} and {@link SmartScriptParser}
 * </br>
 * * responsing to a worker request, this server provides three types of workers:</br>
 *    - {@link CircleWorker} - draws a simple 200x200 image with a circle in it</br>
 *    - {@link EchoParams} - creates a simple html table containing parameter names and values</br>
 *    - {@link HelloWorker} - writes an appropriate 'hello' message to the user
 * </p><p>
 * Server listens on port 5721. Other attributes can be configured through the configuration files.
 * </p>
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class SmartHttpServer {

    /**
     * Represents the path where the worker classes are stored.
     */
    private static final String WORKERS_PACKAGE = "hr.fer.zemris.java.webserver.workers";

    /**
     * Represents the key used for server address.
     */
    private static final String ADRESS_KEY = "server.address";

    /**
     * Represents the key used for server port.
     */
    private static final String PORT_KEY = "server.port";

    /**
     * Represents the key used for number of worker threads.
     */
    private static final String WORKER_THREADS_KEY = "server.workerThreads";

    /**
     * Represents the key used for document root.
     */
    private static final String DOCUMENT_ROOT_KEY = "server.documentRoot";

    /**
     * Represents the key used for fetching the mime configuration file.
     */
    private static final String MIME_CONFIG_KEY = "server.mimeConfig";

    /**
     * Represents the key used for session timeout of the server.
     */
    private static final String TIMEOUT_KEY = "session.timeout";

    /**
     * Represents the key used for fetching the workers configuration file.
     */
    private static final String WORKERS_KEY = "server.workers";

    /**
     * Represents the default buffer size. 
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024;

    /**
     * Represents the number of seconds per day.
     */
    private static final int SECS_PER_DAY = 24 * 60 * 60;

    /**
     * Represents the predefined length of session ID.
     */
    private static final int SID_LENGTH = 20;

    /**
     * Represents the number of milliseconds between two checking of expired sessions (5 minutes).
     */
    private static final int SESSION_CHECKING_RATE = 5 * 60 * 1000;

    /**
     * Represents the address of the server.
     */
    private String address;

    /**
     * Represents the port where the server listens.
     */
    private int port;

    /**
     * Represents the number of worker threads.
     */
    private int workerThreads;

    /**
     * Represents the session timeout of the server.
     */
    private int sessionTimeout;

    /**
     * Represents the provided mime types of the server.
     */
    private Map<String, String> mimeTypes = new HashMap<String, String>();

    /**
     * Represents the main server thread which starts the server.
     */
    private ServerThread serverThread;

    /**
     * Represents the thread pool used for scheduling the threads used for responsig to clients.
     */
    private ExecutorService threadPool;

    /**
     * Represents the document root of the server.
     */
    private Path documentRoot;

    /**
     * Represents the map of all sessions of the server.
     */
    private Map<String, SessionMapEntry> sessions = new HashMap<String, SessionMapEntry>();

    /**
     * Represents the session random of the server, used to create random session IDs.
     */
    private static Random sessionRandom = new Random();

    /**
     * Represents the workers map of the server.
     */
    private Map<String, IWebWorker> workersMap = new HashMap<>();

    /**
     * Represents the thread which periodically (every 5 minutes) removes expired sessions.
     */
    private Thread expiredSessionRemover = new Thread() {

        @Override
        public void run() {
	  try {
	      Thread.sleep(SESSION_CHECKING_RATE);
	  } catch (InterruptedException e) {}

	  Iterator<Map.Entry<String, SessionMapEntry>> it = sessions.entrySet().iterator();
	  while (it.hasNext()) {
	      Map.Entry<String, SessionMapEntry> entry = it.next();

	      long currentTime = LocalTime.now().toSecondOfDay();
	      if (entry.getValue().validUntil < currentTime) {
		it.remove();
	      }
	  }
        }
    };

    /**
     * Public constructor. 
     * Opens the server configuration files and sets the fields to the loaded values.
     * Initializes mime types and reachable workers.
     * 
     * Will throw appropriate exceptions in case of not correctly formatted configuration files.
     * 
     * @param configFileName Path to the main configuration file.
     */
    public SmartHttpServer(String configFileName) {

        Path serverConfigPath = Paths.get(configFileName);

        if (serverConfigPath == null) {
	  throw new IllegalArgumentException("Config file not found.");
        }

        Properties serverProperties = new Properties();
        try {
	  serverProperties.load(Files.newInputStream(serverConfigPath));
        } catch (IOException e) {
	  throw new IllegalArgumentException("Server configuration file not opened.");
        }

        try {
	  this.address = serverProperties.getProperty(ADRESS_KEY);
	  this.port = Integer.parseInt(serverProperties.getProperty(PORT_KEY));
	  this.workerThreads = Integer.parseInt(serverProperties.getProperty(WORKER_THREADS_KEY));
	  this.documentRoot = Paths.get(serverProperties.getProperty(DOCUMENT_ROOT_KEY));
	  this.sessionTimeout = Integer.parseInt(serverProperties.getProperty(TIMEOUT_KEY));
        } catch (NumberFormatException e) {
	  throw new IllegalArgumentException(
		"Problem during reading properties. " + e.getMessage());
        }

        Path mimeConfigPath = Paths.get(serverProperties.getProperty(MIME_CONFIG_KEY));
        Properties mimeProperties = new Properties();
        try {
	  mimeProperties.load(Files.newInputStream(mimeConfigPath));
        } catch (IOException e) {
	  throw new IllegalArgumentException("Mime configuration file not opened.");
        }

        Set<String> propertyKeys = mimeProperties.stringPropertyNames();
        propertyKeys.forEach(k -> mimeTypes.put(k, mimeProperties.getProperty(k)));

        Path workerConfigPath = Paths.get(serverProperties.getProperty(WORKERS_KEY));
        Properties workerProperties = new Properties();
        try {
	  workerProperties.load(Files.newInputStream(workerConfigPath));
        } catch (IOException e) {
	  throw new IllegalArgumentException("Worker configuration file not opened.");
        }

        Set<String> workerPaths = workerProperties.stringPropertyNames();
        workerPaths.forEach(path -> {
	  String fqcn = workerProperties.getProperty(path);
	  IWebWorker iww = createWorker(fqcn);
	  workersMap.put(path, iww);
        });

        expiredSessionRemover.setDaemon(true);
        expiredSessionRemover.start();
    }

    /**
     * Creates a worker depending on the specified fqcn. 
     * 
     * Fqcn is the relative path to the package where the JVM can find the {@link IWebWorker}
     * implementation. Instance of that implementation is then going to be created.
     * 
     * @param fqcn Relative path to the worker class.
     * @return Instance of the created worker.
     */
    private IWebWorker createWorker(String fqcn) {

        Class<?> referenceToClass;
        Object newObject = null;
        try {
	  referenceToClass = this.getClass().getClassLoader().loadClass(fqcn);
	  newObject = referenceToClass.newInstance();
        } catch (Exception e) {
	  System.err.println("Can not create worker.");
        }

        return (IWebWorker) newObject;
    }

    /**
     * Starts the server.
     * Initializes the thread pool to a new fixed-size thread pool.
     * 
     * Starts the main {@link ServerThread}.
     */
    protected synchronized void start() {
        threadPool = Executors.newFixedThreadPool(workerThreads);
        this.serverThread = new ServerThread();
        this.serverThread.setDaemon(true);
        this.serverThread.start();
    }

    /**
     * Stops the server. Shuts down the active thread pool.
     */
    protected synchronized void stop() {
        threadPool.shutdown();
    }

    /**
     * Represents the main server thread.
     * 
     * This thread is obligated to create all necessary resources.
     * Opens and initializes the server socket which will wait for client's request.
     * Once the request was submitted, this thread is obligated to delegate the job 
     * to the {@link ClientWorker} and submit that worker to the {@link ThreadPoolExecutor}. 
     * 
     * @author Marin Grbić
     * @version 1.0
     */
    protected class ServerThread extends Thread {

        @Override
        public void run() {

	  try (ServerSocket serverSocket = new ServerSocket(port)) {

	      while (true) {
		Socket client = null;
		try {
		    client = serverSocket.accept();
		} catch (IOException e) {
		    System.err
			  .println("Error accepting new socket, waiting for another one...");
		    continue;
		}
		if (threadPool.isShutdown()) break;

		ClientWorker cw = new ClientWorker(client);
		threadPool.submit(cw);
	      }
	  } catch (IOException e) {
	      System.err.println("Error opening server socket.");
	      return;
	  }
        }
    }

    /**
     * Represents the client worker who serves the client.
     * 
     * It's obligation is to open the client's socket and to establish the connection
     * between server and client through compatible streams, which means this class
     * contains implementation which creates the final response to the client.
     * 
     * @author Marin Grbić
     * @version 1.0
     */
    private class ClientWorker implements Runnable {

        /**
         * Represents the default mime type in case none was provided.
         */
        private static final String DEFAULT_MIME_TYPE = "application/octet-stream";

        /**
         * Represents the extension of a smart script file.
         */
        private static final String SMART_SCRIPT_EXTENSION = "smscr";

        /**
         * Represents the default response status.
         */
        private static final int DEFAULT_STATUS = 200;

        /**
         * Represents the client's socket.
         */
        private Socket csocket;

        /**
         * Represents the client's input stream.
         */
        private PushbackInputStream istream;

        /**
         * Represents the client's output stream.
         */
        private OutputStream ostream;

        /**
         * Represents the HTML version used in the request.
         */
        private String version;

        /**
         * Represents the method used in the request.
         */
        private String method;

        /**
         * Represents the parameters of the request.
         */
        private Map<String, String> params = new HashMap<String, String>();

        /**
         * Represents the persistent parameters of the request.
         */
        private Map<String, String> permParams = null;

        /**
         * Represents the output cookies of the request.
         */
        private List<RCCookie> outputCookies = new ArrayList<RequestContext.RCCookie>();

        /**
         * Represents the session ID of the request.
         */
        private String SID;

        /**
         * Public constructor. Sets the given client's socket.
         * 
         * @param csocket Client's socket.
         */
        public ClientWorker(Socket csocket) {
	  super();
	  this.csocket = csocket;
        }

        @Override
        public void run() {

	  // obtain input stream from socket and wrap it to pushback input stream
	  try {
	      this.istream = new PushbackInputStream(csocket.getInputStream());
	  } catch (IOException e) {
	      System.err.println("Error creating socket input stream.");
	      e.printStackTrace();
	  }

	  // obtain output stream from socket
	  try {
	      ostream = new BufferedOutputStream(csocket.getOutputStream());
	  } catch (IOException e) {
	      System.err.println("Error getting output stream.");
	      e.printStackTrace();
	  }

	  // Then read complete request header from your client in separate method...
	  List<String> request = readRequest(istream);

	  // If header is invalid (less then a line at least) return response status 400
	  if (request.isEmpty()) {
	      System.err.println("Empty list.");
	      sendError(ostream, 400, "Bad request.", method, version);
	      return;
	  }
	  String firstLine = request.get(0);
	  String[] firstLineArguments = firstLine.isEmpty() ? null : firstLine.split(" ");

	  if (firstLineArguments == null || firstLineArguments.length != 3) {
	      sendError(ostream, 400, "Bad request", method, version);
	      return;
	  }

	  // Extract (method, requestedPath, version) from firstLine
	  // if method not GET or version not HTTP/1.0 or HTTP/1.1 return response status 400
	  String method = firstLineArguments[0].toUpperCase();
	  if (!method.equals("GET")) {
	      sendError(ostream, 400, "Method not allowed", method, version);
	      return;
	  }
	  this.method = method;

	  String version = firstLineArguments[2].toUpperCase();
	  if (!version.equals("HTTP/1.1") && !version.equals("HTTP/1.0")) {
	      sendError(ostream, 400, "HTTP version not supported", method, version);
	      return;
	  }
	  this.version = version;

	  String[] requestedPaths = firstLineArguments[1].split("\\?");
	  String path = requestedPaths[0];

	  //loads cookies (if exist)

	  // (path, paramString) = split requestedPath to path and parameterString 
	  String paramString = requestedPaths.length == 1 ? null : requestedPaths[1];

	  // parseParameters(paramString); ==> your method to fill map parameters
	  parseParameters(paramString);
	  loadCookies(request, path);
	  // requestedPath = resolve path with respect to documentRoot
	  // if requestedPath is not below documentRoot, return response status 403 forbidden
	  IWebWorker worker = workersMap.get(path);
	  if (worker != null || path.startsWith("/ext")) {

	      if (worker == null) {
		String workerName = Paths.get(path).getFileName().toString();
		String fqcn = WORKERS_PACKAGE + "." + workerName;
		worker = SmartHttpServer.this.createWorker(fqcn);
	      }

	      RequestContext rc = new RequestContext(ostream, params, permParams, outputCookies);

	      if (worker instanceof HelloWorker) {
		rc.setMimeType(mimeTypes.get("html"));
	      } else if (worker instanceof CircleWorker) {
		rc.setMimeType(mimeTypes.get("png"));
	      }

	      rc.setStatusCode(DEFAULT_STATUS);
	      worker.processRequest(rc);
	  } else {
	      Path requestedPath = null;
	      try {
		requestedPath = documentRoot.resolve(path.substring(1, path.length()));
	      } catch (InvalidPathException e) {
		sendError(ostream, 403, "Forbidden.", method, version);
		return;
	      }

	      // check if requestedPath exists, is file and is readable; if not, return status 404
	      if (!Files.exists(requestedPath) || !Files.isReadable(requestedPath)) {
		sendError(ostream, 404, "File not accessible.", method, version);
		return;
	      }
	      // else extract file extension
	      int indexOfDot = requestedPath.toString().lastIndexOf('.');
	      String fileExtension = requestedPath.toString().substring(indexOfDot + 1);

	      // find in mimeTypes map appropriate mimeType for current file extension
	      // (you filled that map during the construction of SmartHttpServer from mime.properties)
	      String mimeType = mimeTypes.get(fileExtension);

	      // if no mime type found, assume application/octet-stream
	      // create a rc = new RequestContext(...); set mime-type; set status to 200

	      RequestContext rc = new RequestContext(ostream, params, permParams, outputCookies);
	      rc.setMimeType(mimeType == null ? DEFAULT_MIME_TYPE : mimeType);
	      rc.setStatusCode(DEFAULT_STATUS);

	      try {
		if (fileExtension.equals(SMART_SCRIPT_EXTENSION)) {
		    smartScriptResponse(rc, requestedPath);
		} else {
		    defaultResponse(rc, requestedPath);
		}
	      } catch (IOException e) {
		System.err.println("Exception during responsing.");
		e.printStackTrace();
	      }
	  }
	  try {
	      csocket.close();
	  } catch (IOException e) {}
        }

        /**
         * Parses the parameters which are have their own predefined declaration.
         * 
         * Parameters are the part of path after the '?' character, each parameter has it's name 
         * and attached value. 
         * 
         * Pair name-value is separated by one '=', and two pairs are separated by '&'.
         * 
         * This method also puts the parsed parameters to the parameters map.
         * 
         * @param paramString String used for parsing to parameters.
         */
        private void parseParameters(String paramString) {
	  if (paramString == null) return;

	  String[] pairs = paramString.split("&");
	  for (String p : pairs) {
	      String[] pair = p.split("=");
	      if (pair.length != 2) continue;

	      params.put(pair[0], pair[1]);
	  }
        }

        /**
         * Obligated to start the cookie transfer between server and web browser.
         * 
         * At the first client's request, this method will store the pair contained of session ID
         * and a simple value wrapper {@link SessionMapEntry} which stores the map of client's
         * parameters.
         * 
         * This way, the second time client tries to attempt the request, the entry from 
         * the map will be fetched with the session ID, which means client can reuse it's parameters.
         */
        private synchronized void loadCookies(List<String> request, String path) {
	  String sidCandidate = null;
	  SessionMapEntry entry = new SessionMapEntry();
	  entry.validUntil = (LocalTime.now().toSecondOfDay() + sessionTimeout) % SECS_PER_DAY;
	  l: for (String singleLine : request) {

	      if (!singleLine.startsWith("Cookie: ")) continue;
	      //creates new entry, may not be used in the future if entry with the loaded SID
	      //already exists

	      String[] cookies = singleLine.replaceAll("Cookie: ", "").split(";");
	      for (String cookie : cookies) {
		String[] pair = cookie.split("=");

		if (pair.length != 2) {
		    throw new IllegalArgumentException("Wrong cookie format: " + cookie);
		}

		//if loaded cookie is sid set the sitCandidate
		if (pair[0].equals("sid")) {
		    sidCandidate = pair[1].replaceAll("\"", "");
		    break l;
		    //elseway, it is some type of a cookie: store it to the entry map and to
		    //output cookies
		} else {

		    outputCookies.add(new RCCookie(pair[0], pair[1], sessionTimeout, address, path));
		}
	      }
	  }

	  //if no SID was provided, generate some random SID -> this automatically means
	  //that no such SID is in the session map
	  if (sidCandidate == null) {

	      sidCandidate = generateRandomSid();
	      entry.sid = sidCandidate;

	      outputCookies.add(new RCCookie("sid", sidCandidate, null, address, "/"));
	      //elseway, sid was provided -> such session already exists in the map
	  } else {

	      SessionMapEntry existingEntry = sessions.get(sidCandidate);
	      long entryValidUntil = existingEntry.validUntil;
	      long currentTime = LocalTime.now().toSecondOfDay() % SECS_PER_DAY;

	      //if session time did not expire, refresh the validUntil value and and store
	      //it to the current session map (rewrite it)
	      if (currentTime < entryValidUntil) {
		existingEntry.validUntil = (LocalTime.now().toSecondOfDay() + sessionTimeout)
		        % SECS_PER_DAY;
		entry = existingEntry;
	      }
	      //elseway, new entry should be put in the map -> the one which was created during
	      //the for-loop (variable entry will not be re-referenced)
	  }
	  SID = entry.sid;
	  permParams = entry.map;

	  sessions.put(SID, entry);
        }
    }

    /**
     * Reads the client's request using it's {@link InputStream}.
     * Request consists header lines which will be read and returned.
     * 
     * Standard charset used for encoding HTML headers is ISO-8859-1.
     * 
     * @return Lines of the read header.
     */
    private static List<String> readRequest(InputStream istream) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int state = 0;

        l: while (true) {
	  int b = 0;
	  try {
	      b = istream.read();
	  } catch (IOException e) {
	      continue;
	  }
	  if (b == -1) return null;
	  if (b != 13) {
	      bos.write(b);
	  }

	  switch (state) {
	  case 0:
	      if (b == 13) {
		state = 1;
	      } else if (b == 10) state = 4;
	      break;
	  case 1:
	      if (b == 10) {
		state = 2;
	      } else state = 0;
	      break;
	  case 2:
	      if (b == 13) {
		break l;
	      } else state = 0;
	      break;
	  case 3:
	      if (b == 10) {
		break l;
	      } else state = 0;
	      break;
	  case 4:
	      if (b == 10) {
		break l;
	      } else state = 0;
	      break;
	  }

        }
        String[] lines = new String(bos.toByteArray(), StandardCharsets.ISO_8859_1)
	      .split("\\r?\\n");
        return Arrays.asList(lines);
    }

    /**
     * Generates a completely randomized session ID with the provided session ID length.
     * 
     * This method does not assure that two same session IDs will ever be created, but it
     * assures that chances for that happening are minor.
     * 
     * @return Randomly generated session ID.
     */
    private static String generateRandomSid() {
        byte[] sidChars = new byte[SID_LENGTH];
        for (int i = 0; i < sidChars.length; i++) {
	  sidChars[i] = (byte) ('A' + sessionRandom.nextInt('Z' - 'A'));
        }
        return new String(sidChars, StandardCharsets.US_ASCII);
    }

    /**
     * Represents a type of response where the client requested to execute the smart script.
     * 
     * This method is obligated to read the script and reproduce the result to the client.
     * Relays on implementations of {@link SmartScriptEngine} and {@link SmartScriptParser}
     * as the basic tool for parsing and executing the script.
     * 
     * @param rc Request context used in this request.
     * @param requestedPath Path to the smart script.
     * @throws IOException In case of IO error during reading.
     */
    private static void smartScriptResponse(RequestContext rc, Path requestedPath)
	  throws IOException {

        StringBuilder sb = new StringBuilder();
        Files.readAllLines(requestedPath).forEach(l -> sb.append(l + "\r\n"));
        String documentBody = sb.toString();

        new SmartScriptEngine(new SmartScriptParser(documentBody).getDocumentNode(), rc).execute();
    }

    /**
     * Sends a header containing an error report to the client.
     * 
     * Depending on error description, each error has it's status number (for example, Error 400).
     * 
     * @param cos Client's output stream.
     * @param i Number of the error.
     * @param string Method used when the error occurred.
     * @param version HTML version used when the error occured.
     */
    private static void sendError(OutputStream cos, int i, String string, String method,
	  String version) {

        try {
	  cos.write((i + method + " OK\r\n" + "Server: simple Java server\r\n" + "HTML Version: "
		+ version + "\r\n" + "Content-Type: text/html;charset=UTF-8\r\n"
		+ "Content-Length: " + string + "\r\n" + "Connection: close\r\n" + "\r\n")
		        .getBytes(StandardCharsets.UTF_8));
	  cos.flush();
        } catch (IOException e) {
	  sendError(cos, i, string, method, version);
        }
    }

    /**
     * Creates a default response which means user requested to fetch a file from the server.
     * 
     * Depending on mime type, user will see the file in different ways. For example, HTML files 
     * will be rendered as a html web site, png files will be rendered as images and etc.
     * 
     * @param rc Request context used in this request.
     * @param requestedPath Path to the smart script.
     * @throws IOException In case of IO error during reading.
     */
    private static void defaultResponse(RequestContext rc, Path requestedPath) throws IOException {

        InputStream is = new BufferedInputStream(Files.newInputStream(requestedPath));

        byte[] data = new byte[DEFAULT_BUFFER_SIZE];
        while (true) {
	  int r = is.read(data);

	  if (r == -1) break;
	  //if read less then full buffer
	  if (r < DEFAULT_BUFFER_SIZE) {
	      data = Arrays.copyOf(data, r);
	  }
	  rc.write(data);
        }
    }

    /**
     * Represents a single entry of the parameters map.
     *  
     * This entry contains basic session informations like session ID, number of seconds
     * session is still valid and the map containing session parameters.
     * 
     * @author Marin Grbić
     * @version 1.0
     */
    private static class SessionMapEntry {
        /**
         * Represents the session ID.
         */
        String sid;

        /**
         * Represents number of seconds session is still valid.
         */
        long validUntil;

        /**
         * Represents the map containing session parameters.
         */
        Map<String, String> map = new ConcurrentHashMap<>();
    }

    /**
     * Entry point of the program, starts the server by calling the method for starting.
     * User is allowed to terminate the server by writing "exit" to the stanard input.
     * 
     * @param args Command line argument, 
     * expected to be a path to the server's main configuration file.
     */
    public static void main(String[] args) {

        if (args.length != 1) {
	  throw new IllegalArgumentException("Path to config file expected.");
        }

        System.out.println("Server started: enter \"exit\" to terminate.");
        SmartHttpServer server = new SmartHttpServer(args[0]);
        server.start();

        while (true) {
	  System.out.print("> ");
	  @SuppressWarnings("resource")
	  String input = new Scanner(System.in).nextLine();
	  if (input.toLowerCase().equals("exit")) break;
        }
        System.out.println("Thank you for using this server, goodbye!");
        server.stop();
    }
}