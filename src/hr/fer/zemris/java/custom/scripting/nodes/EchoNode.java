package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.scripting.elems.Element;

/**
 * Class EchoNode represents an echo node which is created from an echo tag. Has its' own elements
 * which are variables, strings, numbers and etc.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class EchoNode extends Node {

    /**
     * Represents the elements of the node.
     */
    private Element[] elements;

    /**
     * Public constructor. Sets the given array of elements.
     * 
     * @param elements
     *            The elements to be set.
     */
    public EchoNode(Element[] elements) {
        this.elements = elements;
    }

    /**
     * The elements getter.
     * 
     * @return Element[] Gets the elements.
     */
    public Element[] getElements() {

        return elements;
    }

    @Override
    public String asText() {

        String text = "{$=";

        for (Element e : elements) {
	  text += e.asText() + " ";
        }

        text += "$}";
        return text;
    }

    @Override
    public void accept(INodeVisitor visitor) {
        visitor.visitEchoNode(this);
    }
}
