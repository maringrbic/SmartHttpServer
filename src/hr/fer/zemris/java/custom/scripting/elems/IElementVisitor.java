package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Represents the visitor interface for visiting {@link Element}s.
 * 
 * Due to different element types, there are different methods used for visiting.
 * In fact, each type of element has it's own visitor method.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public interface IElementVisitor {

    /**
     * Method which accomplishes an action on the {@link ElementConstantDouble}.
     * 
     * @param element The element used for executing the action.
     */
    void visitConstantDouble(ElementConstantDouble element);

    /**
     * Method which accomplishes an action on the {@link ElementConstantInteger}.
     * 
     * @param element The element used for executing the action.
     */
    void visitConstantInteger(ElementConstantInteger element);
    
    /**
     * Method which accomplishes an action on the {@link ElementFunction}.
     * 
     * @param element The element used for executing the action.
     */
    void visitFunction(ElementFunction element);
    
    /**
     * Method which accomplishes an action on the {@link ElementOperator}.
     * 
     * @param element The element used for executing the action.
     */
    void visitOperator(ElementOperator element);
    
    /**
     * Method which accomplishes an action on the {@link ElementString}.
     * 
     * @param element The element used for executing the action.
     */
    void visitString(ElementString element);
    
    /**
     * Method which accomplishes an action on the {@link ElementVariable}.
     * 
     * @param element The element used for executing the action.
     */
    void visitVariable(ElementVariable element);
}
