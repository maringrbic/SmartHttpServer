package hr.fer.zemris.java.custom.scripting.parser;


/**
 * Class SmartScriptParserException represents an exception which is going to be thrown if the text 
 * is not parsable.  
 * 
 * There are several reasons for an unparsable text: lexical mistakes, 
 * undefined tags, undefined elements, incorrect escaping and etc.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class SmartScriptParserException extends RuntimeException {

	/**
	 * Represents the serialVersionUID of this exception.
	 */
	private static final long serialVersionUID = 1L;

	/** Public constructor.
	 * Has one argument: the text to be shown in case the exception is thrown.
	 * @param text The text to be shown.
	 */
	public SmartScriptParserException(String text) {
		super(text);
	}
}
