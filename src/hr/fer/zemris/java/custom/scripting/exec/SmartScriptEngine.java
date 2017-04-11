package hr.fer.zemris.java.custom.scripting.exec;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import hr.fer.zemris.java.custom.collections.ObjectStack;
import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantDouble;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantInteger;
import hr.fer.zemris.java.custom.scripting.elems.ElementFunction;
import hr.fer.zemris.java.custom.scripting.elems.ElementOperator;
import hr.fer.zemris.java.custom.scripting.elems.ElementString;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;
import hr.fer.zemris.java.custom.scripting.elems.IElementVisitor;
import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.INodeVisitor;
import hr.fer.zemris.java.custom.scripting.nodes.Node;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;
import hr.fer.zemris.java.webserver.RequestContext;
import hr.fer.zemris.java.custom.collections.EmptyStackException;

/**
 * Represents the engine which can understand, compile and execute a smart script file.
 * 
 * Smart scripts are consisted of several methods and functionalities, such as for-loops,
 * echos, simple text and etc.
 * 
 * This engine is also able to make a distinction between several types of elements.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class SmartScriptEngine {
    
    /**
     * Represents the document node of the smart script.
     */
    private DocumentNode documentNode;
    
    /**
     * Represents the request context of the engine.
     */
    private RequestContext requestContext;
    
    /**
     * Represents the multistack of the engine.
     */
    private ObjectMultistack multistack = new ObjectMultistack();
    
    /**
     * Represents the visitor of the engine.
     * It's main task is to provide actions for visiting each type of {@link Node}.
     * 
     * If a {@link Node} has it's children, this visitor must do the traversal part of job.
     */
    private INodeVisitor visitor = new INodeVisitor() {

        @Override
        public void visitTextNode(TextNode node) {
	  try {
	      requestContext.write(node.getText());
	  } catch (IOException e) {
	      System.err.println("Exception caught during writing to Output Stream.");
	      e.printStackTrace();
	  }
        }

        @Override
        public void visitForLoopNode(ForLoopNode node) {
	  
	  ElementVariable variable = node.getVariable();
	  int start = node.getStartExpression().getValue();
	  int end = node.getEndExpression().getValue();
	  int step = node.getStepExpression().getValue();
	  
	  int size = node.numberOfChildren();
	  
	  multistack.push(variable.getName(), new ValueWrapper(start));
	  int currentValue;
	  while(true) {
	      currentValue = (int)multistack.pop(variable.getName()).getValue();
	      
	      if(currentValue > end) break;
	      
	      multistack.push(variable.getName(), new ValueWrapper(currentValue));
	      for(int i = 0; i < size; i++) {
		node.getChild(i).accept(this);
	      }

	      currentValue = (int)multistack.pop(variable.getName()).getValue();
	      currentValue += step;
	      
	      multistack.push(variable.getName(), new ValueWrapper(currentValue));
	  }
	  
        }

        @Override
        public void visitEchoNode(EchoNode node) {

	  ObjectStack temporaryStack = new ObjectStack();
	  IElementVisitor elementProcessor = new ElementProcessor(temporaryStack);
	  
	  Element[] elements = node.getElements();
	  for(Element e : elements) {
	      e.accept(elementProcessor);
	  }

	  ObjectStack reversedStack = new ObjectStack();
	  
	  while(true) {
	      Object poppedValue;
	      try {
		poppedValue = temporaryStack.pop();
	      }catch(EmptyStackException e) {
		break;
	      }
	      reversedStack.push(poppedValue);
	  }
	  
	  while(true) {
	      Object poppedValue;
	      try {
		poppedValue = reversedStack.pop();
	      }catch(EmptyStackException e) {
		break;
	      }
	      try {
		requestContext.write(poppedValue.toString());
	      } catch (IOException e1) {
		System.err.println("Error during writing request context.");
		e1.printStackTrace();
		return;
	      }
	  }
        }

        @Override
        public void visitDocumentNode(DocumentNode node) {
	  int size = node.numberOfChildren();
	  for(int i = 0; i < size; i++) {
	      node.getChild(i).accept(this);
	  }
	  
        }
    };

    /** 
     * Public constructor.
     * Sets fields to the given values.
     * 
     * @param documentNode The main node of the document, contains all other nodes.
     * @param requestContext The request context attached to this engine.
     */
    public SmartScriptEngine(DocumentNode documentNode, RequestContext requestContext) {
        if(documentNode == null || requestContext == null) {
	  throw new IllegalArgumentException("Some of the arguments was null which is not allowed.");
        }
        this.documentNode = documentNode;
        this.requestContext = requestContext;
    }

    /**
     * Method which starts the actual execution of the smart script.
     * It's task is to compile the code and execute each command correctly.
     * 
     * This method assures that all of the nodes will be reproduced.
     */
    public void execute() {
        documentNode.accept(visitor);
    }
    
    /**
     * Represents the concrete implementation of the {@link IElementVisitor}.
     * Assures that each type of {@link Element} will be processed on the correct way.
     * 
     * Offers methods for visiting each type of {@link Element}.
     * 
     * @author Marin Grbić
     * @version 1.0
     */
    private class ElementProcessor implements IElementVisitor {
        
        /**
         * Represents the temporary stack of the visitor.
         */
        private ObjectStack temporaryStack;
        
        /** 
         * Public constructor. Sets the temporary stack.
         * 
         * @param temporaryStack Temporary stack used for visiting the elements.
         */
        public ElementProcessor(ObjectStack temporaryStack) {
	  this.temporaryStack = temporaryStack;
        }

        @Override
        public void visitVariable(ElementVariable element) {
            ValueWrapper value = multistack.peek(element.getName());
            temporaryStack.push(value.getValue());
        }
        
        @Override
        public void visitString(ElementString element) {
            temporaryStack.push(element.getValue());
        }
        
        @Override
        public void visitOperator(ElementOperator element) {
            BiFunction<Double,Double,Double> function = ElementOperator.resolve(element);
            
            Object firstArgumentObject = temporaryStack.pop();
            Object secondArgumentObject = temporaryStack.pop();
            
            double firstArgument, secondArgument;
            if(firstArgumentObject instanceof Number) {
                firstArgument = ((Number)firstArgumentObject).doubleValue();
            } else {
                firstArgument = Double.parseDouble((String) firstArgumentObject);
            }
            if(secondArgumentObject instanceof Number) {
                secondArgument = ((Number)secondArgumentObject).doubleValue();
            } else {
                secondArgument = Double.parseDouble((String) secondArgumentObject);
            }
            temporaryStack.push(function.apply(firstArgument, secondArgument));
        }
        
        @Override
        public void visitFunction(ElementFunction element) {
            BiConsumer<RequestContext, ObjectStack> action = OperationsUtil.resolve(element.getValue());
            action.accept(SmartScriptEngine.this.requestContext, temporaryStack);
        }
        
        @Override
        public void visitConstantInteger(ElementConstantInteger element) {
	  temporaryStack.push(element.getValue());
        }
        
        @Override
        public void visitConstantDouble(ElementConstantDouble element) {
	  temporaryStack.push(element.getValue());
        }

    }
}
