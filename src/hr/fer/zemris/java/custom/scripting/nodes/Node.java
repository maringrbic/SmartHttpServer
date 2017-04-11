package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.collections.ArrayIndexedCollection;

/**
 * Class Node represents one abstract node. A node can have its' own children and it's value.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public abstract class Node {

    /**
     * Represents the children of the node.
     */
    private ArrayIndexedCollection children = null;

    /**
     * Represents if the collection of children is empty.
     */
    private boolean collectionEmpty = true;

    /**
     * Adds new child to the collection of children. If the child is null, <b>will not throw</b> an
     * exception, but also will not add the value to the collection.
     * 
     * @param child
     *            The child to be added to the children collection.
     */
    public void addChildNode(Node child) {

        if (collectionEmpty) {
	  collectionEmpty = false;
	  children = new ArrayIndexedCollection();
        }

        try {
	  children.add(child);
        } catch (IllegalArgumentException e) {
	  // does nothing
        }
    }

    /**
     * Returns the size of the collection - number of children.
     * 
     * @return int The number of children.
     */
    public int numberOfChildren() {

        return children.size();
    }

    /**
     * Gets a child at the specified index. Index must not be greater than the 'number of children'
     * - 1.
     * 
     * @param index
     *            Index of the child.
     * 
     * @throws IndexOutOfBoundsException
     *             In case of an invalid index.
     * @return Node The child at the given index.
     */
    public Node getChild(int index) {

        Node node;

        try {
	  node = (Node) children.get(index);
        } catch (IndexOutOfBoundsException e) {
	  throw new IndexOutOfBoundsException(
		"You reqeusted a child at an illegal index: " + index);
        }

        return node;
    }

    /**
     * Returns the string representation of the node. Can be text or tag. Does not guarantee the
     * output to be exactly like the input. Correct number of spaces are not guaranted. The text in
     * the output is <b>always</b> uppercase.
     * 
     * @return String Node as a string.
     */
    public abstract String asText();

    /**
     * Method used for accepting the visitor.
     * 
     * Each type of node provides it's own implementation for this method,
     * usually consisted just of calling the appropriate visitor method.
     * 
     * @param visitor Visitor of the visitor pattern.
     */
    public abstract void accept(INodeVisitor visitor);
}
