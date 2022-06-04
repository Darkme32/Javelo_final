
package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

import java.util.StringJoiner;
/**
 * Represents a set of OpenStreetMap attributes
 *
 * @author Menzo Bouaissi (340377)
 */
public record AttributeSet(long bits) {

    /**
     * Allows to create an AttributeSet object
     * only if the attribute is in the set
     *
     * @param bits the vector of bits
     */
    public AttributeSet {
        Preconditions.checkArgument(bits >>> Attribute.COUNT == 0);
    }

    /**
     * Creates a new AttributeSet, given some attributes.
     * With this method, we create a long variable, which
     * will be passed as an argument to the new AttributeSet
     *
     *
     * @param attributes list of attribute
     * @return a new AttributeSet with a long corresponding to
     * the attributes
     */
    public static AttributeSet of(Attribute... attributes) {
        long bits = 0;
        for (Attribute element : attributes) {
            bits = bits |  1L << (long) element.ordinal();
        }
        return new AttributeSet(bits);

    }

    /**
     * Checks if an attribute is in the given long
     * In order to do that, we create a mask by
     * eliminating every digit in the bits vector,
     * except the one we want.
     *
     * @param attribute the one we are looking for
     * @return true if the attribute is in the bits
     * vector
     */
    public boolean contains(Attribute attribute) {
        long formatedBits = (this.bits >>> (attribute.ordinal())) << (Long.SIZE-1);

        return formatedBits != 0;
    }

    /**
     * Checks if an attribute is in both bits
     * by doing the conjunction
     *
     * @param that the AttributeSet we want to compare
     * @return whether there is an intersection between both bits
     */
    public boolean intersects(AttributeSet that) {

        return (this.bits & that.bits) != 0;

    }

    /**
     * Returns a string with the value and the key of some attribute
     *
     * @return a string with the value and the key of some attribute
     */
    @Override
    public String toString() {
        StringJoiner j = new StringJoiner(",", "{", "}");

        for (Attribute element : Attribute.values()) {
            if (contains(element)) {
                j.add(element.key() + "=" + element.value());
            }
        }
        return j.toString();

    }
}
