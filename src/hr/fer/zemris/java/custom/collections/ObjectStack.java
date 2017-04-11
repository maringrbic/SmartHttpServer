/**
 * 
 */
package hr.fer.zemris.java.custom.collections;

/**
 * Class ObjectStack represents a collection with methods used to create a stack, pop elements, push
 * elements and etc. Adapts array indexed collection to work as a stack.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class ObjectStack {

    /**
     * Represents the default size of stack.
     */
    private static final int DEFAULT_SIZE = 16;
    
    /**
     * Represents the actual collection where elements will be stored.
     */
    private ArrayIndexedCollection stack = null;

    /**
     * Public default constructor. Sets the stack capacity to 16.
     */
    public ObjectStack() {

        this(DEFAULT_SIZE);
    }

    /**
     * Public constructor. Sets the stack capacity to the given value.
     * 
     * @param initialCapacity
     *            The given value for the stack capacity.
     */
    public ObjectStack(int initialCapacity) {

        stack = new ArrayIndexedCollection(initialCapacity);
    }

    /**
     * Checks if the stack is empty or not. If the stack is empty, returns true.
     * 
     * @return boolean True if the stack is empty, elseway false.
     */
    public boolean isEmpty() {

        return stack.isEmpty();
    }

    /**
     * Returns the actual number of elements stored on the stack. Can not be a value less than 0.
     * 
     * @return int Stack size.
     */
    public int size() {

        return stack.size();
    }

    /**
     * Pushes a value to the top of stack. Value must be an object different from null.
     * 
     * @param value
     *            The value to be pushed.
     * @throws IllegalArgumentException
     *             In case the value is null.
     */
    public void push(Object value) {

        try {
	  stack.add(value);
        } catch (IllegalArgumentException e) {
	  throw new IllegalArgumentException("Can not push null to the stack.");
        }

        return;
    }

    /**
     * Pops a value from the top of stack. Can not pop a value from an empty stack.
     * 
     * @throws EmptyStackException
     *             In case the stack is empty.
     * @return Object The popped object.
     */
    public Object pop() {

        Object object;
        try {
	  object = this.peek();
        } catch (EmptyStackException e) {
	  throw new EmptyStackException("Can not pop from an empty stack.");
        }

        stack.remove(stack.size() - 1);

        return object;
    }

    /**
     * Gets the value from the top of stack but does not remove it. Can not peek a value from an
     * empty stack.
     * 
     * @throws EmptyStackException
     *             In case the stack is empty.
     * @return Object The object to get.
     */
    public Object peek() {

        if (stack.size() == 0) {
	  throw new EmptyStackException("Can not peek from an empty stack.");
        }

        return stack.get(stack.size() - 1);
    }

    /**
     * Removes all previously popped elements from the stack.
     */
    public void clear() {

        stack.clear();
        return;
    }
    
    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        String s = "[";
        for(int i = 0; i < stack.size(); i++) {
	 s += stack.get(i);
	 if(i == stack.size() - 1) break;
	 s += ",";
        }
        s += "]";
        return s;
    }
}
