package hr.fer.zemris.java.custom.scripting.exec;

import java.util.HashMap;
import java.util.Map;

import hr.fer.zemris.java.custom.scripting.exec.ValueWrapper;

/**
 * Represents a collection which is a special type of a simple map.
 * It contains elemented stored like pair key-value, but in a special direction.
 * Each key provides a stack with functions for pushing, poping and peeking from the stack.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class ObjectMultistack {

    /**
     * Represents the map of the object multistack which stores the multistack entries.
     */
    private Map<String, MultistackEntry> map = new HashMap<>();
    
    /**
     * Pushes an object to the stack.
     * The stack where to push is defined by it's key.
     * @param name The key of the stack where to push.
     * @param valueWrapper <b>void</b> The value to push.
     */
    public void push(String name, ValueWrapper valueWrapper) {
	
	MultistackEntry temporary = map.get(name);
	MultistackEntry newEntry = new MultistackEntry(valueWrapper, null);
	
	if(temporary == null) {
	    map.put(name, newEntry);
	} else {
	    while(temporary.next != null) {
		temporary = temporary.next;
	    }
	    temporary.next = newEntry;
	}
    }
    
    /**
     * Pops an object from the stack.
     * The stack from where to pop is defined by it's key.
     * @param name The key of the stack from where to pop.
     * @return <b>ValueWrapper</b> The poped value.
     */
    public ValueWrapper pop(String name) {
	MultistackEntry entry = map.get(name);
	ValueWrapper value = null;
	
	try {
	    value = this.peek(name);
	} catch(EmptyStackException e) {
	    throw new EmptyStackException("Can not pop from an empty stack!");
	}
	
	MultistackEntry previous = entry;
	while(entry.next != null) {
	    previous = entry;
	    entry = entry.next;
	}
	previous.next = null;
	entry = null;
	
	return value;
    }
    
    /**
     * Peeks an object from the stack.
     * The stack from where to peek is defined by it's key.
     * @param name The key of te stack from where to peek.
     * @return <b>ValueWrapper</b> The peeked value.
     */
    public ValueWrapper peek(String name) {
	
	MultistackEntry entry = map.get(name);
	
	if(entry == null) {
	    throw new EmptyStackException("Can not peek from an empty stack!");
	}
	
	while(entry.next != null) {
	    entry = entry.next;
	}
	
	return entry.getValue();
    }
    
    /**
     * Checks if the stack from the given key is empty.
     * Stack is empty if there are no elements currently stored.
     * @param name The key where to check.
     * @return <b>boolean</b> True if the stack is empty, elseway false.
     */
    public boolean isEmpty(String name) {
	return map.get(name) == null;
    }
    
    /**
     * Represents a single entry of the stack. 
     * Contains a stored value and a reference to the next entry which is stored upon the current one.
     * @author Marin Grbić
     * @version 1.0
     */
    public static class MultistackEntry {
	
	/**
	 * Represents the value of the entry.
	 */
	private ValueWrapper value;
	
	/**
	 * Represents the next entry of the current one. 
	 */
	private MultistackEntry next;

	/** Public constructor.
	 * Sets fields to the given values.
	 * @param value The value to be set, allowed to be null.
	 * @param next Next entry in the stack.
	 */
	public MultistackEntry(ValueWrapper value, MultistackEntry next) {
	    this.value = value;
	    this.next = next;
	}

	/**
	 * The value getter.
	 * @return ValueWrapper gets the value
	 */
	public ValueWrapper getValue() {
	    return value;
	}

	/**
	 * The value setter.
	 * @param value The value to set.
	 */
	public void setValue(ValueWrapper value) {
	    this.value = value;
	}

	/**
	 * The next getter.
	 * @return MultistackEntry gets the next
	 */
	public MultistackEntry getNext() {
	    return next;
	}

	/**
	 * The next setter.
	 * @param next The next to set.
	 */
	public void setNext(MultistackEntry next) {
	    this.next = next;
	}	
	
	
    }
}
