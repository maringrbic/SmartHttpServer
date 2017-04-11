package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * Class INodeVisitor represents the visitor in the visitor pattern.
 * 
 * This type of visitor declares all methods needed for the correct visiting of all types
 * of {@link Node}s. Due to that, it will asure the correct compiling and executing of the
 * smart script file.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public interface INodeVisitor {

    /**
     * Method which accomplishes an action on the {@link TextNode}.
     * 
     * @param node The node used for executing the action.
     */
    public void visitTextNode(TextNode node);

    /**
     * Method which accomplishes an action on the {@link ForLoopNode}.
     * 
     * @param node The node used for executing the action.
     */
    public void visitForLoopNode(ForLoopNode node);

    /**
     * Method which accomplishes an action on the {@link EchoNode}.
     * 
     * @param node The node used for executing the action.
     */
    public void visitEchoNode(EchoNode node);

    /**
     * Method which accomplishes an action on the {@link DocumentNode}.
     * 
     * @param node The node used for executing the action.
     */
    public void visitDocumentNode(DocumentNode node);
}
