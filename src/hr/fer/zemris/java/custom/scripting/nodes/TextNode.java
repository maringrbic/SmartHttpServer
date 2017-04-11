package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * Class TextNode represents a node created from a text. Contains the text stored as a string.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class TextNode extends Node {

    /**
     * Represents the text of the node.
     */
    private String text;

    /**
     * Public constructor. Sets the given text.
     * 
     * @param text The text to set.
     */
    public TextNode(String text) {
        this.text = text;
    }

    /**
     * The text getter.
     * 
     * @return String Gets the text.
     */
    public String getText() {

        return text;
    }

    @Override
    public String asText() {
        return text;
    }

    @Override
    public void accept(INodeVisitor visitor) {
        visitor.visitTextNode(this);
    }
}
