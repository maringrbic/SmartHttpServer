package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * Class Token represents a token which contains a value. The value can be any object, like strings,
 * characters, integers and etc.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class Token {

    /**
     * Represents the token type.
     */
    private TokenType type;

    /**
     * Represents the token value.
     */
    private String value;

    /**
     * Public constructor. Sets the type and value.
     * 
     * @param type
     *            The type to be set.
     * @param value
     *            The value to be set.
     */
    public Token(TokenType type, String value) {
        if (type == null) {
	  throw new IllegalArgumentException("Illegal argument given, type can not be null!");
        }
        this.type = type;
        this.value = value;
    }

    /**
     * The value getter.
     * 
     * @return Object Gets the value.
     */
    public String getValue() {

        return value;
    }

    /**
     * The type getter.
     * 
     * @return TokenType Gets the token type.
     */
    public TokenType getType() {

        return type;
    }
}
