package hr.fer.zemris.java.custom.collections;

/**
 * Class ArrayIndexedCollection represents a collection of objects stored as array. Collection
 * allows duplicate elements, but does not null references.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class ArrayIndexedCollection extends Collection {

    /**
     * Represents the size of the collection, must be integer greater than or equal to 0.
     */
    private int size;

    /**
     * Represents the capacity of the collection, must be integer greater than or equal to 1.
     */
    private int capacity;

    /**
     * Represents the elements of the collection stored as array.
     */
    private Object[] elements;

    /**
     * Public constructor. Sets the collection capacity to the given value.
     * 
     * @param initialCapacity
     *            the capacity to be set
     */
    public ArrayIndexedCollection(int initialCapacity) {

        if (initialCapacity < 1) {
	  throw new IllegalArgumentException("Initial capacity must not be less than 1!");
        }

        size = 0;
        capacity = initialCapacity;
        elements = new Object[capacity];
    }

    /**
     * Public default constructor. Sets the collection capacity to 16.
     */
    public ArrayIndexedCollection() {

        this(16);
    }

    /**
     * Public constructor. Copies values from the given collection to the collection going to be
     * created.
     * 
     * @param collection
     *            The collection to copy.
     * @param initialCapacity
     *            The capacity to be set.
     */
    public ArrayIndexedCollection(Collection collection, int initialCapacity) {

        if (collection.size() > capacity) {

	  throw new IllegalArgumentException(
		"You specified a collection with capacity " + collection.size()
		        + " to be copied into a collection of size " + initialCapacity);
        }

        size = collection.size();
        capacity = initialCapacity;
        this.addAll(collection);
    }

    /**
     * Public constructor. Copies values from the given collection to the collection going to be
     * created. Sets the capacity to 16.
     * 
     * @param collection
     *            The collection to copy.
     */
    public ArrayIndexedCollection(Collection collection) {

        this(collection, 16);
    }

    /**
     * Overriden method.
     * 
     * @see hr.fer.zemris.java.custom.collections.Collection#isEmpty()
     */
    @Override
    public boolean isEmpty() {

        return size == 0 ? true : false;
    }

    /**
     * Overriden method.
     * 
     * @see hr.fer.zemris.java.custom.collections.Collection#size()
     */
    @Override
    public int size() {

        return size;
    }

    /**
     * Overriden method.
     * 
     * @see hr.fer.zemris.java.custom.collections.Collection#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object value) {

        for (int i = 0; i < size; i++) {
	  if (elements[i].equals(value)) {
	      return true;
	  }
        }

        return false;
    }

    /**
     * Overriden method.
     * 
     * @see hr.fer.zemris.java.custom.collections.Collection#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object value) {

        if (this.contains(value)) {
	  int index = this.indexOf(value);

	  for (int i = index; i < size; i++) {
	      elements[i] = elements[i + 1];
	  }

	  this.size--;

	  return true;
        }

        return false;
    }

    /**
     * Overriden method.
     * 
     * @see hr.fer.zemris.java.custom.collections.Collection#toArray()
     * 
     */
    @Override
    public Object[] toArray() {

        if (size == 0) {
	  throw new UnsupportedOperationException(
		"Can not represent an empty collection as an array!");
        }

        Object[] temp = new Object[size];

        for (int i = 0; i < size; i++) {
	  temp[i] = elements[i];
        }

        return temp;
    }

    /**
     * Overriden method.
     * 
     * @see hr.fer.zemris.java.custom.collections.Collection#add(java.lang.Object)
     */
    @Override
    public void add(Object value) {

        if (value == null) {
	  throw new IllegalArgumentException("You can not add null to the collection!");
        }

        if (size == capacity) {
	  capacity *= 2;
	  Object[] temp = new Object[capacity];

	  for (int i = 0; i < size; i++) {
	      temp[i] = elements[i];
	  }

	  elements = temp;
        }

        elements[size] = value;
        size++;
    }

    /**
     * Gets an element at the given index. Does not remove the element from the collection!
     * 
     * @param index
     *            The index of the element.
     * @throws IndexOutOfBoundsException
     *             In case of an illegal index.
     * @return Object The object at the given index.
     */
    public Object get(int index) {

        if (index >= size || index < 0) {
	  throw new IndexOutOfBoundsException("No element at the given index.");
        }

        return elements[index];
    }

    /**
     * Overriden method.
     * 
     * @see hr.fer.zemris.java.custom.collections.Collection#forEach(hr.fer.zemris.java.custom.collections.Processor)
     */
    public void forEach(Processor processor) {

        for (int i = 0; i < size; i++) {
	  processor.process(elements[i]);
        }
    }

    /**
     * Removes all elements from the collection. Sets the collection size to 0 and all elements to
     * null.
     */
    @Override
    public void clear() {

        elements = new Object[size];
        size = 0;
    }

    /**
     * Inserts value to the given position. Position must be in range 0 to size - 1.
     * 
     * @param value
     *            The value to be inserted.
     * @param position
     *            Position where to insert.
     * @throws IndexOutOfBoundsException
     *             In case of illegal position.
     */
    public void insert(Object value, int position) {

        if (position >= size || position < 0) {
	  throw new IndexOutOfBoundsException("Tried to insert out of bounds.");
        }

        for (int i = size; i > position; i--) {
	  elements[i] = elements[i - 1];
        }

        elements[position] = value;
        size++;
    }

    /**
     * Gets the index of the value stored in the collection. If there is no such value, returns -1.
     * 
     * @param value
     *            The value which index is going to be returned.
     * @return int Index of the given value.
     */
    public int indexOf(Object value) {

        for (int i = 0; i < size; i++) {
	  if (elements[i].equals(value)) {
	      return i;
	  }
        }

        return -1;
    }

    /**
     * Removes an element at the given index. Index must be in range 0 to size - 1.
     * 
     * @param index
     *            The given index.
     * @throws IndexOutOfBoundsException
     *             In case of illegal index.
     */
    public void remove(int index) {

        if (index >= size || index < 0) {
	  throw new IndexOutOfBoundsException("No element at the given index.");
        }

        for (int i = index; i < size; i++) {
	  elements[i] = elements[i + 1];
        }

        elements[size] = null;
        size--;
    }
}
