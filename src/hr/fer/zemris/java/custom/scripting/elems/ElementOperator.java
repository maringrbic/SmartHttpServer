package hr.fer.zemris.java.custom.scripting.elems;

import java.util.function.BiFunction;

/**
 * Class ElementOperator represents one mathematical operator. Contains only one read-only value.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class ElementOperator extends Element {

    /**
     * Represents the SUMMING of the 
     */
    private static final BiFunction<Double, Double, Double> SUM = (a,b) -> a + b;
    
    /**
     * Represents the SUMMING of the 
     */
    private static final BiFunction<Double, Double, Double> SUB = (a,b) -> a - b;
    
    /**
     * Represents the MUL of the 
     */
    private static final BiFunction<Double, Double, Double> MUL = (a,b) -> a * b;
    
    /**
     * Represents the DIV of the 
     */
    private static final BiFunction<Double, Double, Double> DIV = (a,b) -> a / b;
    
    /**
     * Represents the symbol (operator) of the element.
     */
    private String symbol;

    /**
     * Public constructor.
     * 
     * @param symbol
     *            The symbol to be set.
     */
    public ElementOperator(String symbol) {
        this.symbol = symbol;
    }

    /**
     * The symbol getter.
     * 
     * @return String Gets the symbol.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Overridden method.
     * 
     * @see hr.fer.zemris.java.custom.scripting.elems.Element#asText()
     */
    @Override
    public String asText() {
        return symbol;
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visitOperator(this);
    }

    /**
     * @param element
     * @return <b>BiFunction<Double,Double,Double></b>
     */
    public static BiFunction<Double, Double, Double> resolve(ElementOperator element) {
        
        if(element.symbol.equals("+")) return SUM;
        if(element.symbol.equals("-")) return SUB;
        if(element.symbol.equals("*")) return MUL;
        if(element.symbol.equals("/")) return DIV;
        return null;
    }

}
