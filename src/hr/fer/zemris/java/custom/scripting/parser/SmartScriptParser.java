package hr.fer.zemris.java.custom.scripting.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hr.fer.zemris.java.custom.collections.EmptyStackException;
import hr.fer.zemris.java.custom.collections.ObjectStack;
import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantDouble;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantInteger;
import hr.fer.zemris.java.custom.scripting.elems.ElementFunction;
import hr.fer.zemris.java.custom.scripting.elems.ElementOperator;
import hr.fer.zemris.java.custom.scripting.elems.ElementString;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;
import hr.fer.zemris.java.custom.scripting.lexer.Lexer;
import hr.fer.zemris.java.custom.scripting.lexer.LexerException;
import hr.fer.zemris.java.custom.scripting.lexer.Token;
import hr.fer.zemris.java.custom.scripting.lexer.TokenType;
import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.Node;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;

/**
 * Class SmartScriptParser represents a parser for a previously defined language. The parser can
 * parse text and tags, has it's own lexer and has several rules for parsing.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class SmartScriptParser {

    /**
     * Represents the text to parse.
     */
    private String text;

    /**
     * Represents the document node of the node tree.
     */
    private DocumentNode documentNode;

    /**
     * Represents the lexer of the parser.
     */
    private Lexer lexer;

    /**
     * Public constructor. Creates an instance of lexer and initializes it with obtained text.
     * Starts the parsing by calling the appropriate method.
     * 
     * @param text
     *            The given text to parse.
     */
    public SmartScriptParser(String text) {

        if (text == null) {
	  throw new SmartScriptParserException("Given text is null!");
        }

        this.text = text;
        this.lexer = new Lexer(this.text);
        this.documentNode = new DocumentNode();
        parse();
    }

    /**
     * The method which parses the text and creates the document node, checks if the text obeys all
     * rules. The node tree consists of several nodes, and some of them can have its' own children,
     * like for loop node. Every node keeps its' elements and can be represented as a text. Parser
     * accepts different elements in tags.
     * <p>
     * To parse succesfully, text must not have lexical mistakes and logical mistakes like unclosed
     * tags, unacceptable elements in tags and etc.
     * </p>
     * 
     * @throws SmartScriptParserException
     *             In case of unparsable documents.
     */
    private void parse() {

        ObjectStack stack = new ObjectStack();
        stack.push(documentNode);

        Token token = null;

        while (lexer.nextToken().getType() != TokenType.EOF) {
	  try {
	      token = lexer.getToken();
	  } catch (LexerException e) {
	      throw new SmartScriptParserException(e.getLocalizedMessage());
	  }

	  Node temp;
	  try {
	      temp = (Node) stack.peek();
	  } catch (EmptyStackException e) {
	      throw new SmartScriptParserException(
		    "Error while parsing! " + e.getLocalizedMessage());
	  }

	  Node value;
	  if (token.getType().equals(TokenType.TEXT)) {

	      value = NodeUtil.createTextNode(token.getValue());

	      temp.addChildNode(value);
	  } else if (token.getType().equals(TokenType.TAG)) {

	      String tag = token.getValue();
	      Pattern pattern = Pattern.compile(
		    "\\s*[{]{1}\\s*[$]{1}\\s*END\\s*[$]{1}\\s*[}]{1}\\s*",
		    Pattern.CASE_INSENSITIVE); // END TAG
	      Matcher matcher = pattern.matcher(tag);

	      if (matcher.matches()) {
		if (stack.size() > 1) {
		    stack.pop();
		} else {
		    throw new SmartScriptParserException(
			  "Error while parsing, there are more END tags than it should be!");
		}
	      } else {

		pattern = Pattern.compile("\\s*[{]{1}\\s*[$]{1}\\s*=.+\\s*[$]{1}\\s*[}]{1}\\s*",
		        Pattern.CASE_INSENSITIVE); // ECHO TAG
		matcher = pattern.matcher(tag);

		if (matcher.matches()) {

		    try {
		        temp = (Node) stack.peek();
		    } catch (EmptyStackException e) {
		        throw new SmartScriptParserException(
			      "Error while parsing! " + e.getLocalizedMessage());
		    }

		    try {
		        value = NodeUtil.createEchoNode(tag);
		    } catch (IllegalArgumentException e) {
		        throw new SmartScriptParserException(
			      "Problem during parsing: " + e.getLocalizedMessage());
		    }

		    temp.addChildNode(value);
		} else {

		    pattern = Pattern.compile(
			  "\\s*[{]{1}\\s*[$]{1}\\s*FOR.+\\s*[$]{1}\\s*[}]{1}\\s*",
			  Pattern.CASE_INSENSITIVE); // FOR TAG
		    matcher = pattern.matcher(tag);

		    if (matcher.matches()) {
		        try {
			  temp = (Node) stack.peek();
		        } catch (EmptyStackException e) {
			  throw new SmartScriptParserException(
				"Error while parsing! " + e.getLocalizedMessage());
		        }

		        try {
			  value = NodeUtil.createForLoopNode(tag);
		        } catch (IllegalArgumentException e) {
			  throw new SmartScriptParserException(
				"Problem during parsing: " + e.getLocalizedMessage());
		        }

		        temp.addChildNode(value);

		        stack.push(value);
		    } else {
		        throw new SmartScriptParserException("Unknown tag name!" + tag);
		    }
		}
	      }
	  } else {
	      throw new SmartScriptParserException("Unknown token type!");
	  }
        }
    }
    
    /**
     * The document node getter.
     * 
     * @return DocumentNode Gets the documentNode.
     */
    public DocumentNode getDocumentNode() {

        return documentNode;
    }
    
    /**
     * Class NodeCreator consists of methods for creating a node and checking the correctness.
     * Can create new nodes, checks if the elements are correct and etc.
     * 
     * @author Marin Grbić
     * @version 1.0
     */
    private static class NodeUtil {

	  /**
	   * Represents all allowed operators of the parser.
	   */
	  final static String operators = "+-*/^";

	  /**
	   * Creates new text node from the given text.
	   * 
	   * @param value
	   *            The text to be used for the creation of a text node.
	   * @return Node The created node.
	   */
	  private static TextNode createTextNode(String value) {

	      return new TextNode(value);
	  }

	  /**
	   * Creates new echo node from the given tag. Echo node paramaters can be all type of
	   * elements: numbers, functions, operators, strings and variables.
	   * 
	   * @param tag
	   *            The tag represented as a text.
	   * @throws IllegalArgumentException
	   *             In case of illegal element, a tag without elements...
	   * @return Node The created node.
	   */
	  private static EchoNode createEchoNode(String tag) {

	      String[] text = tag.replaceAll("\\s*[{]{1}\\s*[$]{1}\\s*=\\s*", "")
		    .replaceAll("\\s*[$]{1}\\s*[}]{1}\\s*", "").split("\\s+");

	      //if the text was splitted between two '"', it will stick it together
	      int numOfNulls = 0;
	      for (int i = 0; i < text.length; i++) {
		if(text[i] == null) {
		    numOfNulls++;
		    continue;
		}
		if(text[i].startsWith("\"") && text[i].endsWith("\"") && text[i].length() > 1) continue;
		if(!text[i].startsWith("\"") && !text[i].endsWith("\"")) continue;
		for(int j = i + 1;; j++) {
		    text[i] = new String(text[i] + " " + text[j]);
		    if(text[j].endsWith("\"")) {
		        text[j] = null;
		        break;
		    }
		    text[j] = null;
		}
	      }
	      Element[] elements = new Element[text.length - numOfNulls];

	      if (text.length > 0) {
		int k = 0;
		for (int i = 0; i < text.length; i++) {
		    
		    if(text[i] == null) {
		        k++;
		        continue;
		    }
		    if (Character.isLetter(text[i].charAt(0))) { // VARIABLE

		        try {
			  confirmVariable(text[i]);
		        } catch (IllegalArgumentException e) {
			  throw new IllegalArgumentException(e.getLocalizedMessage());
		        }

		        elements[i-k] = new ElementVariable(text[i]);
		    } else if (text[i].charAt(0) == '@') { // FUNCTION

		        try {
			  confirmFunction(text[i]);
		        } catch (IllegalArgumentException e) {
			  throw new IllegalArgumentException(e.getLocalizedMessage());
		        }

		        elements[i-k] = new ElementFunction(text[i].substring(1));
		    } else if (operators.contains(String.valueOf(text[i].charAt(0)))
			  && text[i].length() == 1) { // OPERATOR

		        elements[i-k] = new ElementOperator(String.valueOf(text[i].charAt(0)));
		    } else if (text[i].startsWith("\"") && text[i].endsWith("\"")) { // STRING
		        elements[i-k] = new ElementString(
			      text[i].substring(1, text[i].length() - 1));
		    } else {

		        try {
			  elements[i-k] = new ElementConstantInteger(Integer.parseInt(text[i])); // INTEGER
		        } catch (NumberFormatException e1) {
			  try {
			      elements[i-k] = new ElementConstantDouble(
				    Double.parseDouble(text[i])); // DOUBLE
			  } catch (NumberFormatException e2) {
			      throw new IllegalArgumentException(text[i]
				    + " is neither a number, function, operator, string or variable!");
			  }
		        }
		    }
		}
	      } else {
		throw new IllegalArgumentException("Tag must have some elements!");
	      }

	      return new EchoNode(elements);
	  }

	  /**
	   * Creates new for loop node from the given tag. For loop node can have three or four
	   * paramaters. It has one ElementVariable, two or three Elements of type variable,
	   * number or string.
	   * 
	   * @param tag
	   *            The tag represented as a text.
	   * @throws IllegalArgumentException
	   *             In case of illegal element, a tag with an uncorrect number of
	   *             parameters...
	   * @return Node The created node.
	   */
	  private static ForLoopNode createForLoopNode(String tag) {

	      String[] text = tag.replaceAll("\\s*[{]{1}\\s*[$]{1}\\s*FOR\\s*", "")
		    .replaceAll("\\s*[$]{1}\\s*[}]{1}\\s*", "").split("\\s+");

	      ElementVariable variable;
	      Element[] elements = new Element[3];

	      if (text.length == 3 || text.length == 4) {

		try {
		    confirmVariable(text[0]);
		} catch (IllegalArgumentException e) {
		    throw new IllegalArgumentException(e.getLocalizedMessage());
		}

		variable = new ElementVariable(text[0]);

		for (int i = 1; i < text.length; i++) {

		    if (Character.isLetter(text[i].charAt(0))) { // VARIABLE

		        try {
			  confirmVariable(text[i]);
		        } catch (IllegalArgumentException e) {
			  throw new IllegalArgumentException(e.getLocalizedMessage());
		        }

		        elements[i - 1] = new ElementVariable(text[i]);
		    } else if (text[i].startsWith("\"") && text[i].endsWith("\"")) { // STRING

		        elements[i - 1] = new ElementString(
			      text[i].substring(1, text[i].length() - 1));
		    } else {

		        try {
			  elements[i - 1] = new ElementConstantInteger(
				Integer.parseInt(text[i])); // INTEGER
		        } catch (NumberFormatException e1) {
			  throw new IllegalArgumentException(
				    text[i] + " is neither a number, string or variable!");
			  }
		    }
		}
	      } else {
		throw new IllegalArgumentException("For-loop has " + text.length
		        + " elements, but it should have 3 or 4!");
	      }

	      return new ForLoopNode(
		    variable, 
		    (ElementConstantInteger)elements[0],
		    (ElementConstantInteger)elements[1], 
		    (ElementConstantInteger)elements[2]
			  );
	  }

	  /**
	   * Confirms if a given text is a variable. Valid variable name starts by letter and
	   * after follows zero or more letters, digits or underscores.
	   * 
	   * @param text
	   *            The text to be checked.
	   * @throws IllegalArgumentException
	   *             If the variable is not valid.
	   */
	  private static void confirmVariable(String text) {

	      if (text.length() > 1) {

		for (int j = 1; j < text.length(); j++) {

		    if (Character.isLetter(text.charAt(j)) || Character.isDigit(text.charAt(j))
			  || text.charAt(j) == '_') {
		        continue;
		    } else {
		        throw new IllegalArgumentException(text
			      + " is not a valid variable name! Symbols must be letters, digits or _.");
		    }
		}
	      }
	  }

	  /**
	   * Confirms if a given text is a function. Valid function name starts with '@' after
	   * which follows a letter and after follow zero or more letters, digits or underscores.
	   * 
	   * @param text
	   *            The text to be checked.
	   * @throws IllegalArgumentException
	   *             If the variable is not valid.
	   */
	  private static void confirmFunction(String text) {

	      if (text.length() > 1) {

		if (!Character.isLetter(text.charAt(1))) {
		    throw new IllegalArgumentException(text
			  + " is not a valid function name! First symbol must be a letter.");
		}

		for (int j = 2; j < text.length(); j++) {
		    if (Character.isLetter(text.charAt(j)) || Character.isDigit(text.charAt(j))
			  || text.charAt(j) == '_') {
		        continue;
		    } else {
		        throw new IllegalArgumentException(text
			      + " is not a valid function name! Symbols must be letters, digits or _.");
		    }
		}

	      } else {
		throw new IllegalArgumentException("Undefined function name!");
	      }
	  }
    }
}
