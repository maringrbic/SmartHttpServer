package hr.fer.zemris.java.webserver.workers;

import java.io.IOException;
import java.util.Set;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Represents a type of worker.
 * 
 * This worker has a task to create a simple HTML document.
 * Document contains a table with provided parameters and their values.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class EchoParams implements IWebWorker {

    @Override
    public void processRequest(RequestContext context) {
        Set<String> parameterNames = context.getParameterNames();
        
        StringBuilder sb = new StringBuilder();
        sb.append("<table border=\"1\" style=\"border: 2px solid black;\">\r\n");
        sb.append("  <tr>\r\n");
	  sb.append("    <td><b>Param</b></td>\r\n");
	  sb.append("    <td><b>Value<b> </td>\r\n");
	  sb.append("  </tr>\r\n");
	  
        parameterNames.forEach(name -> {
	  sb.append("  <tr>\r\n");
	  sb.append("    <td>" + name + "</td>\r\n");
	  sb.append("    <td>" + context.getParameter(name) + "</td>\r\n");
	  sb.append("  </tr>\r\n");
        });
        sb.append("</table>\r\n");
        try {
	  context.write(sb.toString());
        } catch (IOException e) {
	  e.printStackTrace();
        }

    }

}
