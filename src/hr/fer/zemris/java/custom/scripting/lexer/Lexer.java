package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * Class Lexer represents a simple lexer which processes a given text into tokens. Lexer can work in
 * different states (basic and extended) and can produce two types of tokens: text and tag. Tag
 * token is created of the elements between the brackets {}.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class Lexer {

    /**
     * Represents the input text represented as char array.
     */
    private char[] data;

    /**
     * Represents the last generated token.
     */
    private Token token;

    /**
     * Represents the current index in the character array.
     */
    private int currentIndex;

    /**
     * Represents the current state lexer is working in.
     */
    private LexerState state;

    /**
     * Public constructor. Initialy sets the lexer state to basic.
     * 
     * @param text
     *            The given text to be processed.
     * @throws IllegalArgumentException
     *             If the text is null.
     */
    public Lexer(String text) {
        if (text == null) {
	  throw new IllegalArgumentException("Text must not be null!");
        }
        this.data = text.trim().toCharArray();
        this.token = null;
        this.currentIndex = 0;
        this.state = LexerState.BASIC;
    }

    /**
     * Generates next token found in the data text. In case of lexer's state is basic, generates
     * basic token. If the lexer's state is extended, generates extended token.
     * 
     * @throws LexerException
     *             In case of wrong escaping, EOF, unsupported parsing...
     * @return Token Next token found in the given text.
     */
    public Token nextToken() {

        if (currentIndex == data.length) {
	  if (token != null && token.getType().equals(TokenType.EOF)) {
	      throw new LexerException("There are no more tokens after EOF!");
	  }
	  return token = new Token(TokenType.EOF, null);
        }

        while (Character.isSpaceChar(data[currentIndex])) {
	  currentIndex++;
        }

        if (this.state.equals(LexerState.BASIC)) {
	  return basicToken();
        } else {
	  return extendedToken();
        }
    }

    /**
     * Returns new token created in the basic state. Does not manipulate with the original data
     * array.
     * 
     * @throws LexerException
     *             In case of wrong escaping, EOF, unsupported parsing...
     * @return Token Next token.
     */
    private Token basicToken() {

        if (data[currentIndex] != '{') {
	  int i;
	  for (i = currentIndex; i < data.length && data[i] != '{'; i++) {
	      if (data[i] == '\\') {
		if (i + 1 >= data.length) {
		    throw new LexerException("Wrong escaping!");
		}
		if (data[i + 1] == '\\' || data[i + 1] == '{') {
		    i++;
		} else {
		    throw new LexerException("Wrong escaping!");
		}

	      }
	  }

	  char[] outputAsArray = new String(data, currentIndex, i - currentIndex).toCharArray();
	  String output = "";

	  // removes all unnecessary escapes from the string
	  for (int j = 0; j < outputAsArray.length; j++) {
	      if (outputAsArray[j] == '\\' && !Character.isWhitespace(outputAsArray[j + 1])
		    && (outputAsArray[j + 1] == '\\' || outputAsArray[j + 1] == '{')) {
		output += outputAsArray[j + 1];
		j++;
	      } else {
		output += outputAsArray[j];
	      }
	  }

	  this.currentIndex = i;

	  token = new Token(TokenType.TEXT, output);
        } else if (data[currentIndex] == '{') {
	  this.setState(LexerState.EXTENDED);
	  token = extendedToken();
        }

        return token;
    }

    /**
     * Returns new token created in the extended state. Does not manipulate with the original data
     * array.
     * 
     * @return Token Next token.
     */
    private Token extendedToken() {

        int i = currentIndex;

        if (data[i] == '{') {

	  i++;
	  try {
	      do {
		i++;
	      } while (data[i] != '}');
	      i++;
	  } catch (IndexOutOfBoundsException e) {
	      throw new LexerException("Tag was never closed!");
	  }

	  String output = new String(data, currentIndex, i - currentIndex).trim();
	  currentIndex = i;

	  token = new Token(TokenType.TAG, output);
        } else {
	  this.setState(LexerState.BASIC);
	  token = basicToken();
        }

        return token;
    }

    /**
     * The token getter.
     * 
     * @return Token Gets the token.
     */
    public Token getToken() {

        return token;
    }

    /**
     * The token setter.
     * 
     * @param state
     *            The state to be set.
     */
    public void setState(LexerState state) {

        if (state == null) {
	  throw new IllegalArgumentException("State can not be null!");
        }
        this.state = state;
    }
}
