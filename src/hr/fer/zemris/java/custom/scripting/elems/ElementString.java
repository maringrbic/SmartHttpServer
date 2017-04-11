package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Class ElementString represents one string. Contains only one read-only value.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class ElementString extends Element {

    /**
     * Represents the value of the element.
     */
    private String value;

    /**
     * Public constructor.
     * 
     * @param value
     *            The value to set.
     */
    public ElementString(String value) {
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

        return value == null ? "" : "\"" + value + "\"";
    }

    /** 
     * @see hr.fer.zemris.java.custom.scripting.elems.Element#accept(hr.fer.zemris.java.custom.scripting.elems.IElementVisitor)
     */
    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visitString(this);
    }

}
