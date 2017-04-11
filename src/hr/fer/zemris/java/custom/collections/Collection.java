package hr.fer.zemris.java.custom.collections;

/**
 * Class Collection represents an abstract collection with methods for adding, removing and etc.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class Collection {

    /**
     * Public constructor.
     * 
     */
    protected Collection() {}

    /**
     * Checks if the collection is empty and returns a value.
     * 
     * @return boolean True if collection is empty, false if contains some elements.
     */
    public boolean isEmpty() {

        return false;
    }

    /**
     * Returns the actual size of the collection. Must be integer greater than or equal to 0.
     * 
     * @return int The size of collection.
     */
    public int size() {

        return 0;
    }

    /**
     * Adds one element to the first free place in the collection.
     * 
     * @param value
     *            The value to add.
     * @throws IllegalArgumentException
     *             If the value is null.
     */
    public void add(Object value) {

    }

    /**
     * Checks the collection and returns a boolean if collection contains the given value.
     * 
     * @param value
     *            The value to be checked.
     * @return boolean True if collection contains the given value, elseway false.
     */
    public boolean contains(Object value) {

        return false;
    }

    /**
     * Removes the given element from the collection.
     * 
     * @param value
     *            The value to be removed.
     * @return boolean True if removing was succesful, elseway false.
     */
    public boolean remove(Object value) {

        return false;
    }

    /**
     * Returns a new array of objects representing the collection as an array.
     * 
     * @throws UnsupportedOperationException
     *             In case if an empty collection is tried to be represented as array.
     * @return Object[] Collection as array of objects.
     */
    public Object[] toArray() {

        return null;
    }

    /**
     * Processes all of the collection elements. For each element calls the process method.
     * 
     * @param processor
     *            The processor with its process method.
     */
    public void forEach(Processor processor) {

    }

    /**
     * Adds all elements from the given collection to the current one.
     * 
     * @param other
     *            The collection which elements are going to be copied.
     */
    public void addAll(Collection other) {

        /**
         * Class AddProcess represents a processor which task is to add every value to the current
         * collection. Contains only one method: process.
         * 
         * @author Marin Grbić
         * @version 1.0
         */
        class AddProcess extends Processor {

	  /**
	   * Adds a value to the current collection.
	   */
	  @Override
	  public void process(Object value) {

	      add(value);
	  }
        }

        other.forEach(new AddProcess());
    }

    /**
     * Removes all elements from the collection. Sets the size to 0.
     */
    public void clear() {

    }

}
