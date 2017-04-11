package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Class ElementConstantDuble represents one decimal number. Contains only one read-only value.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class ElementConstantDouble extends Element {

    /**
     * Represents the value of the element.
     */
    private double value;

    /**
     * Public constructor.
     * 
     * @param value
     *            The value to be set.
     */
    public ElementConstantDouble(double value) {
        this.value = value;
    }

    /**
     * The value getter.
     * 
     * @return double Gets the value.
     */
    public double getValue() {

        return value;
    }

    /**
     * Overridden method.
     * 
     * @see hr.fer.zemris.java.custom.scripting.elems.Element#asText()
     */
    @Override
    public String asText() {

        return new Double(value).toString();
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visitConstantDouble(this);
    }

}
