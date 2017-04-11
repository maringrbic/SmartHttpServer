package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Class ElementConstantInteger represents one integer. Contains only one read-only value.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class ElementConstantInteger extends Element {

	/**
	 * Represents the value of the element.
	 */
	private int value;

	/**
	 * Public constructor.
	 * 
	 * @param value The value to be set.
	 */
	public ElementConstantInteger(int value) {
		this.value = value;
	}

	/**
	 * The value getter.
	 * 
	 * @return int Gets the value.
	 */
	public int getValue() {

		return value;
	}

	/**
	 * Overridden method.
	 * 
	 * @see hr.fer.zemris.java.custom.scripting.elems.Element#asText()
	 */
	@Override
	public String asText() {

		return new Integer(value).toString();
	}


        @Override
        public void accept(IElementVisitor visitor) {
	  visitor.visitConstantInteger(this);
        }

}
