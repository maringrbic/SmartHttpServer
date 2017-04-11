package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Class ElementFunction represents a function. Contains only one read-only value.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class ElementFunction extends Element {

    /**
     * Represents the value of the element.
     */
    private String value;

    /**
     * Public constructor.
     * 
     * @param value
     *            The value to be set.
     */
    public ElementFunction(String value) {
        this.value = value;
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
     * Overridden method.
     * 
     * @see hr.fer.zemris.java.custom.scripting.elems.Element#asText()
     */
    @Override
    public String asText() {

        return "@" + value;
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visitFunction(this);
    }

}
