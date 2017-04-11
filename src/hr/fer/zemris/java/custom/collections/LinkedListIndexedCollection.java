package hr.fer.zemris.java.custom.collections;

/**
 * Class LinkedListIndexedCollection represents a collection which main property is the flexible
 * size. Collection is not static, which means memory allocates only while adding one by one
 * element. Duplicates are allowed to be add, null references not.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class LinkedListIndexedCollection extends Collection {

    /**
     * Represents the size of the collection, must be integer greater than or equal to 0.
     */
    private int size;

    /**
     * Represents the first element of the list.
     */
    private ListNode first;

    /**
     * Represents the last element of the list.
     */
    private ListNode last;

    /**
     * Class ListNode represents one node of the list. Contains references to the previous and next
     * element in the list.
     * 
     * @author Marin Grbić
     * @version 1.0
     */
    private static class ListNode {

        /**
         * Represents the value of the list node.
         */
        Object value;

        /**
         * Represents the previous node in the list.
         */
        ListNode previous;

        /**
         * Represents the next node in the list.
         */
        ListNode next;
    }

    /**
     * Public constructor. Creates new collections with elements of the collection given in
     * argument.
     * 
     * @param collection
     *            The collection with elements to be copied.
     */
    public LinkedListIndexedCollection(Collection collection) {

        size = 0;
        this.addAll(collection);
    }

    /**
     * Public default constructor.
     * 
     */
    public LinkedListIndexedCollection() {

        size = 0;
        first = last = null;
    }

    /**
     * Overriden method.
     * 
     * @see hr.fer.zemris.java.custom.collections.Collection#isEmpty()
     */
    @Override
    public boolean isEmpty() {

        if (first == null && last == null) {
	  return true;
        }

        return false;
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
     * @see hr.fer.zemris.java.custom.collections.Collection#add(java.lang.Object)
     */
    @Override
    public void add(Object value) {

        if (value == null) {
	  throw new IllegalArgumentException("You can not add null to the list!");
        }

        ListNode node = new ListNode();

        node.value = value;
        node.next = null;
        if (first == null) {
	  first = node;
	  last = node;
	  node.previous = null;
        } else {
	  last.next = node;
	  node.previous = last;
	  last = node;
        }

        size++;
    }

    /**
     * Overriden method.
     * 
     * @see hr.fer.zemris.java.custom.collections.Collection#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object value) {

        for (ListNode temp = first; temp != null; temp = temp.next) {

	  if (temp.value.equals(value)) {
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

    public boolean remove(Object value) {

        for (ListNode temp = first; temp != null; temp = temp.next) {

	  if (temp.value.equals(value)) {

	      try {
		temp.previous.next = temp.next;
	      } catch (NullPointerException e1) {
		first = temp.next;
	      }
	      try {
		temp.next.previous = temp.previous;
	      } catch (NullPointerException e2) {
		last = temp.previous;
	      }
	      size--;
	      return true;
	  }
        }

        return false;
    }

    /**
     * Overriden method.
     * 
     * @see hr.fer.zemris.java.custom.collections.Collection#toArray()
     */
    @Override
    public Object[] toArray() {

        if (size == 0) {
	  throw new UnsupportedOperationException(
		"Collection is empty, can not be represented as array!");
        }

        ListNode temp = first;
        Object[] array = new Object[this.size];
        for (int i = 0; temp != null; temp = temp.next, i++) {
	  array[i] = temp.value;
        }

        return array;
    }

    /**
     * Overriden method.
     * 
     * @see hr.fer.zemris.java.custom.collections.Collection#forEach(hr.fer.zemris.java.custom.collections.Processor)
     */
    public void forEach(Processor processor) {

        for (ListNode temp = first; temp != null; temp = temp.next) {

	  processor.process(temp.value);
        }
    }

    /**
     * Overriden method.
     * 
     * @see hr.fer.zemris.java.custom.collections.Collection#clear()
     */
    @Override
    public void clear() {

        size = 0;
        first = last = null;
    }

    /**
     * Gets the element at the given index. Index must be in range 0 to size - 1.
     * 
     * @param index
     *            The index of the element.
     * @throws IndexOutOfBoundsException
     *             In case of illegal index.
     * @return Object The object at the given index.
     */
    public Object get(int index) {

        if (index < 0 || index >= size) {

	  throw new IndexOutOfBoundsException("Index out of bounds!");
        }

        return this.toArray()[index];
    }

    /**
     * Inserts a value to the given position. Position must be in range [0,size].
     * 
     * @param value
     *            The value to be inserted.
     * @param position
     *            Position where to insert.
     * @throws IndexOutOfBoundsException
     *             In case of illegal position.
     */
    public void insert(Object value, int position) {

        if (value == null) {

	  throw new IllegalArgumentException("You can not add null to the list!");
        }

        if (position < 0 || position > size) {

	  throw new IndexOutOfBoundsException(
		"Index must be in range [0," + size + "] and you provided " + position + ".");
        }

        ListNode newNode = new ListNode();
        newNode.value = value;

        if (position == 0) {
	  newNode.next = first;
	  first = newNode;
	  size++;
        } else if (position == size) {
	  this.add(value);
        } else {
	  ListNode temp;

	  if (position < (size / 2)) {
	      temp = first;
	      for (int i = 0; i < position; i++, temp = temp.next)
		;
	  } else {
	      temp = last;
	      for (int i = size - 1; i > position; i--, temp = temp.previous)
		;
	  }

	  newNode.next = temp;
	  newNode.previous = temp.previous;
	  temp.previous.next = newNode;
	  temp.previous = newNode;
	  size++;
        }

    }

    /**
     * Gets the index of the value stored in the collection. If there is no such value, returns -1.
     * 
     * @param value
     *            The value which index is going to be returned.
     * @return int Index of the given value.
     */
    public int indexOf(Object value) {

        Object[] array = this.toArray();

        for (int i = 0; i < array.length; i++) {

	  if (array[i].equals(value)) {
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

        ListNode temp;
        if (index < (size / 2)) {
	  temp = first;
	  for (int i = 0; i < index; i++, temp = temp.next)
	      ;
        } else {
	  temp = last;
	  for (int i = size - 1; i > index; i--, temp = temp.previous)
	      ;
        }

        temp.previous.next = temp.next;
        temp.next.previous = temp.previous;
        size--;
    }
}
