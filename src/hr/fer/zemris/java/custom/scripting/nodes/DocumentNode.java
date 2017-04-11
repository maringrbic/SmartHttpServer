package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * Class DocumentNode represents the main node in the node tree. Has several types of children,
 * depending of the document.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class DocumentNode extends Node {

    @Override
    public void accept(INodeVisitor visitor) {
        visitor.visitDocumentNode(this);
    }

    @Override
    public String asText() {
        return "";
    }

}
