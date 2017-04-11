package hr.fer.zemris.java.webserver;

/**
 * Represents the interface toward any object that can process a {@link RequestContext}.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public interface IWebWorker {
    
    /**
     * Obligated to get a {@link RequestContext} as parameter and it is expected to 
     * create a content for client.
     * 
     * @param context The given context.
     */
    public void processRequest(RequestContext context);
}
