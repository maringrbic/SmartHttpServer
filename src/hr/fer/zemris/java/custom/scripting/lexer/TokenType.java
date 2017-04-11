package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * Enumeration TokenType represents several token types of which the tokens can be. The type depends
 * of the loaded value, like words, numbers, symbols and EOF.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public enum TokenType {

    /**
     * Represents the textual token.
     */
    TEXT,

    /**
     * Represents the tag token.
     */
    TAG,

    /**
     * Represents the end of file token.
     */
    EOF
}
