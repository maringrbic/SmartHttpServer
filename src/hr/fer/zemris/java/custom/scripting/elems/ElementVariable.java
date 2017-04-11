package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Class ElementVariable represents one variable. Contains only one read-only value.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class ElementVariable extends Element {

    /**
     * Represents the name of the variable.
     */
    private String name;

    /**
     * Public constructor.
     * 
     * @param name
     *            The name to set.
     */
    public ElementVariable(String name) {
        this.name = name;
    }

    /**
     * The name getter.
     * 
     * @return String Gets the name.
     */
    public String getName() {

        return name;
    }

    /**
     * Overridden method.
     * 
     * @see hr.fer.zemris.java.custom.scripting.elems.Element#asText()
     */
    @Override
    public String asText() {

        return name;
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visitVariable(this);
    }

}
