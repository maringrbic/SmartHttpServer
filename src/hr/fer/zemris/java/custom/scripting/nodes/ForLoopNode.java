package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.scripting.elems.ElementConstantInteger;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;

/**
 * Class ForLoopNode represents a foor loop node which is created from a for loop tag. It has one
 * ElementVariable, two or three Elements of type variable, number or string.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class ForLoopNode extends Node {

    /**
     * Represents the variable of the node.
     */
    private ElementVariable variable;

    /**
     * Represents the start expression of the node.
     */
    private ElementConstantInteger startExpression;

    /**
     * Represents the end expression of the node.
     */
    private ElementConstantInteger endExpression;

    /**
     * Represents the step expression of the node.
     */
    private ElementConstantInteger stepExpression;

    /**
     * Public constructor. Sets the elements to the given values.
     * 
     * @param variable
     *            The variable of the node.
     * @param startExpression
     *            The start expression of the node.
     * @param endExpression
     *            The end expression of the node.
     * @param stepExpression
     *            The step expression of the node.
     */
    public ForLoopNode(ElementVariable variable, ElementConstantInteger startExpression, 
	  ElementConstantInteger endExpression, ElementConstantInteger stepExpression) {

        if (variable == null || startExpression == null || endExpression == null) {
	  throw new IllegalArgumentException("Only step expression can be null!");
        }

        this.variable = variable;
        this.startExpression = startExpression;
        this.endExpression = endExpression;
        this.stepExpression = stepExpression;
    }

    /**
     * The variable getter.
     * 
     * @return ElementVariable Gets the variable.
     */
    public ElementVariable getVariable() {

        return variable;
    }

    /**
     * The start expression getter.
     * 
     * @return Element Gets the startExpression.
     */
    public ElementConstantInteger getStartExpression() {

        return startExpression;
    }

    /**
     * The end expression getter.
     * 
     * @return Element Gets the endExpression.
     */
    public ElementConstantInteger getEndExpression() {

        return endExpression;
    }

    /**
     * The step expression getter.
     * 
     * @return Element Gets the stepExpression.
     */
    public ElementConstantInteger getStepExpression() {

        return stepExpression;
    }

    @Override
    public String asText() {

        String text = "{$FOR " + variable.asText() + " " + startExpression.asText() + " "
	      + endExpression.asText() + " " + stepExpression.asText() + " $}";
        return text;
    }

    @Override
    public void accept(INodeVisitor visitor) {
        visitor.visitForLoopNode(this);
    }
}
