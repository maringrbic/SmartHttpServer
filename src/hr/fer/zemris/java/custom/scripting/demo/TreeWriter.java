package hr.fer.zemris.java.custom.scripting.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.INodeVisitor;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

/**
 * Represents the simple demonstration of the visitor pattern.
 * 
 * This pattern consists of definining an interface that describes Visitor object: 
 * a single Visitor will usually perform a single operation and it will contain a 
 * dedicated method for performing this operation on every different domain object.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class TreeWriter {

    /**
     * Entry point of the program, loads the smart script from the provided path.
     * 
     * Initializes the {@link SmartScriptParser} and executes the parsing.
     * 
     * @param args Command line argument - expected one path to the file.
     * @throws IOException In case of IO error.
     */
    public static void main(String[] args) throws IOException {
        
        if(args.length != 1) {
	  throw new IllegalArgumentException("One path expected.");
        }
        
        Path path = Paths.get(args[0]);
        
        if(path == null) {
	  throw new IllegalArgumentException("Path not existing.");
        }
        
        StringBuilder sb = new StringBuilder();
        Files.readAllLines(path).forEach(l -> sb.append(l + "\r\n"));
        
        SmartScriptParser p = new SmartScriptParser(sb.toString());
        WriterVisitor visitor = new WriterVisitor();
        p.getDocumentNode().accept(visitor);
    }
    
    /**
     * Represents the visitor used in visitor pattern.
     * 
     * This visitor implements all necessary methods used while 'walking' 
     * through the smart script.
     * 
     * @author Marin Grbić
     * @version 1.0
     */
    private static class WriterVisitor implements INodeVisitor {

        /**
         * Represents the symbol used for ending tag in the smart script.
         */
        private static final String END_TAG = "{$END$}";
        
        @Override
        public void visitTextNode(TextNode node) {
    	  System.out.print(node.asText());
        }

        @Override
        public void visitForLoopNode(ForLoopNode node) {
	  System.out.print(node.asText());
	  
	  int size = node.numberOfChildren();
	  for(int i = 0; i < size; i++) {
	      node.getChild(i).accept(this);
	  }
	  System.out.print(END_TAG);
        }

        @Override
        public void visitEchoNode(EchoNode node) {
	  System.out.print(node.asText());
        }

        @Override
        public void visitDocumentNode(DocumentNode node) {
	  
	  int size = node.numberOfChildren();
	  for(int i = 0; i < size; i++) {
	      node.getChild(i).accept(this);
	  }
        }
    }

}
