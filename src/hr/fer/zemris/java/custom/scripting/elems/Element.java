package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Class Element represents one element of a token. Does not contain any public constructor.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public abstract class Element{

    /**
     * Returns a value as represented as a string. Usually delegates the conversion to other
     * methods.
     * 
     * @return String The value of the element as a string.
     */
    public String asText() {

        return new String("");
    };

    /**
     * Method used for accepting the visitor.
     * 
     * Each type of element provides it's own implementation for this method,
     * usually consisted just of calling the appropriate visitor method.
     * 
     * @param visitor Visitor of the visitor pattern.
     */
    public abstract void accept(IElementVisitor visitor);
}
