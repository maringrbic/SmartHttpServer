package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * Class LexerException represents an exception which the lexer will throw in case of illegal
 * arguments. Illegal arguments can cause illegal escaping, unsupported parsing and etc.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class LexerException extends RuntimeException {

    /**
     * Represents the serialVersionUID of the exception.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Public constructor.
     * 
     * @param string
     *            The warning text to be shown if the exception has been thrown.
     */
    public LexerException(String string) {
        super(string);
    }

}
