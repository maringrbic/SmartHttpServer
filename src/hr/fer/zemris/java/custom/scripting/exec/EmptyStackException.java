package hr.fer.zemris.java.custom.scripting.exec;

/**
 * Class EmptyStackException represents an exception while working with stack. 
 * Exception is thrown if there are no elements at the stack during the operations pop and peek.
 * 
 * @author Marin Grbić
 * @version 1.0
 */


@SuppressWarnings("serial")
public class EmptyStackException extends RuntimeException {
	
	/**
	 * Public default constructor (without arguments).
	 * 
	 */
	public EmptyStackException() {

		super();
	}

	/**
	 * Public constructor. Displays the warning message.
	 * 
	 * @param arg Message to be displayed if the exception is thrown.
	 */
	public EmptyStackException(String arg) {

		super(arg);
	}

}
