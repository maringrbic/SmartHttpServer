package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * Enumeration LexerState represents two states in which the lexer can work. Basic state can produce
 * several tokens. Extended state can produce only text and symbols.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public enum LexerState {
    /**
     * Represents the basic state of the lexer.
     */
    BASIC,

    /**
     * Represents the extended state of the lexer.
     */
    EXTENDED
}
