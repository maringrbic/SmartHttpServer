package hr.fer.zemris.java.custom.scripting.exec;

/**
 * Represents a simple wrapper for a value which represents a number.
 * The stored value is allowed to be null, Double, Integer or a String representation of a Double or Integer.
 * If the stored value is null, it is considered to be a number with value of 0.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class ValueWrapper {

    /**
     * Represents the value of the value wrapper.
     */
    private Object value;

    /** Public constructor.
     * Sets fields to the given values.
     * @param value The value to set.
     */
    public ValueWrapper(Object value) {
	if(argumentValidity(value)) {
	    this.value = value;
	} else {
	    throw new RuntimeException("Value can be only integers, doubles or strings!");
	}
    }
    
    /**
     * Increments the actual value with the given one.
     * The given argument must be Integer, Double, String representation of Integer or null.

     * @param incValue A value used for incrementation.
     */
    public void increment(Object incValue) {
	
	if(incValue == null) return; // current value + 0 = current value
	
	Number argument;
	try {
	    argument = createNumber(incValue);
	} catch(RuntimeException e) {
	    throw e;
	}
	
	if(this.value == null) {
	    if(argument instanceof Integer) { //0 + current value = current value
		this.value = new Integer((Integer)argument);
	    } else {
		this.value = new Double((Double)argument);
	    }
	    return;
	} else if(this.value instanceof Integer && argument instanceof Integer) {
	    this.value = new Integer((Integer)this.value + (Integer)argument);
	} else {
	    this.value = new Double(Double.valueOf(this.value.toString()) + Double.valueOf(argument.toString()));
	}
	
    }

    /**
     * Decrements the actual value with the given one.
     * The given argument must be Integer, Double, String representation of Integer or null.

     * @param decValue A value used for decrementation.
     */
    public void decrement(Object decValue) {
	if(decValue == null) return; // current value - 0 = current value
	
	Number argument;
	try {
	    argument = createNumber(decValue);
	} catch(RuntimeException e) {
	    throw e;
	}
	
	if(argument instanceof Integer) {
	    this.increment(- new Integer((Integer) argument));
	} else {
	    this.increment(- new Double((Double) argument));
	}

	
    }
    
    /**
     * Multiplies the actual value with the given one.
     * The given argument must be Integer, Double, String representation of Integer or null.

     * @param mulValue A value used for multiplying.
     */
    public void multiply(Object mulValue) {
	
	if(mulValue == null) {
	    this.value = Integer.valueOf(0);
	    return; // current value * 0 = 0
	}
	
	Number argument;
	try {
	    argument = createNumber(mulValue);
	} catch(RuntimeException e) {
	    throw e;
	}
	
	if(this.value == null) { //0 * current value = 0
	    this.value = Integer.valueOf(0);
	    return;
	} else if(this.value instanceof Integer && argument instanceof Integer) {
	    this.value = new Integer((Integer)this.value * (Integer)argument);
	} else {
	    this.value = new Double(Double.valueOf(this.value.toString()) * Double.valueOf(argument.toString()));
	}
	
    }
    
    /**
     * Divides the actual value with the given one.
     * The given argument must be Integer, Double, String representation of Integer different from 0.
     * @param divValue  A value used for division.
     */
    public void divide(Object divValue) {
	
	if(divValue == null) {
	    throw new ArithmeticException("You can not divide by zero!");
	}
	
	Number argument;
	try {
	    argument = createNumber(divValue);
	} catch(RuntimeException e) {
	    throw e;
	}
	
	if(this.value == null) { //0 / current value = 0
	    this.value = Integer.valueOf(0);
	    return;
	} else if (argument.intValue() == 0) {
	    throw new ArithmeticException("You can not divide by zero!");
	}
	else {
	    this.value = new Double(Double.valueOf(this.value.toString()) / Double.valueOf(argument.toString()));
	}
    }
    
    /**
     * Compares two value wrappers.
     * Returns a value calculated from the comparison. If two value wrappers are equal, returns 0.
     * If the first argument is greater than the second, returns 1, elseway -1.
     * @param withValue The value to compare.
     * @return <b>int</b> Integer representation of the comparison.
     */
    public int numCompare(Object withValue) {
	
	Double first = this.value != null ? Double.valueOf(this.value.toString()) : 0.0;
	Double second = withValue != null ? Double.valueOf(withValue.toString()) : 0.0;
	
	if(first > second) return 1;
	if(first < second) return -1;
	return 0;
    }
    
    /**
     * Checks the argument validity.
     * Valid arguments are Integers, Doubles, Strings which can be parsed to Integer or Double and null references.
     * @param incValue 
     * @return <b>boolean</b> True if argument is valid, elseway false.
     */
    private boolean argumentValidity(Object incValue) {
	if(incValue == null) return true;
	return incValue instanceof Integer || incValue instanceof Double || incValue instanceof String;
    }
    
    /**
     * Creates a new number from the given object.
     * Can not create a number from a non-valid argument.
     * 
     * 
     * @param number The given value to be parsed to number.
     * @throws RuntimeException In case of a non-valid argument.
     * @return <b>Number</b> The created number from the given object.
     */
    private Number createNumber(Object number) {
	
	if(!argumentValidity(number)) {
	    throw new RuntimeException("Arithmetic operations are allowed only with integers, doubles or strings!");
	}
	
	Number argument = null;
	if(number instanceof String) {    
	    String value = (String) number;
	    if(value.contains(".") || value.contains("E")) {
		try {
		    argument = Double.parseDouble(value);
		} catch(NumberFormatException e) {
		    throw new RuntimeException(argument + " can not be parsed to double!");
		}
	    } else {
		try {
		    argument = Integer.parseInt(value);
		} catch(NumberFormatException e) {
		    throw new RuntimeException(argument + " can not be parsed to integer!");
		}
	    }
	} else {
	    argument = (Number) number;
	}
	
	return argument;
    }

    /**
     * The value getter.
     * @return Object gets the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * The value setter.
     * @param value The value to set.
     */
    public void setValue(Object value) {
        this.value = value;
    }
}
