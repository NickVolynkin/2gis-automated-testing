package io.github.nickvolynkin.dgis;

/**
 * A utility object for storing groups of 3 integers, usually representing coordinates in a 3d-space.
 * <br>
 * Instances are immutable.
 *
 * @author Nick Volynkin nick.volynkin@gmail.com
 */
public class Vector3d {

    public static final Vector3d ZERO = new Vector3d(0, 0, 0);
    public static final Vector3d ORIGIN = new Vector3d(1, 1, 1);

    public final int x;
    public final int y;
    public final int z;

    public Vector3d(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Sum vectors and return a new one.
     *
     * @param other the vector to be summed with this
     * @return a new vector, being a sum of two
     */
    public Vector3d plus(Vector3d other) {
        return new Vector3d(x + other.x, y + other.y, z + other.z);
    }

    /**
     * Substract vectors and return a new one.
     *
     * @param other the vector to be substracted from this
     * @return a new vector, being a difference of two
     */
    public Vector3d minus(Vector3d other) {
        return new Vector3d(x - other.x, y - other.y, z - other.z);
    }


    /**
     * Make a scalar (by-component) multiplication of this vector by a number and return a new one.
     *
     * @param multiplier the number to multiply by.
     * @return a new vector, being a scalar multiplication of this by multiplier
     */
    public Vector3d scalar(int multiplier) {
        return new Vector3d(x * multiplier, y * multiplier, z * multiplier);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("x= ").append(x).append(", y=").append(y).append(", z=").append(z);
        return sb.toString();
    }

    @Override
    public int hashCode() {
        //todo rewrite by Josh Bloch
        return toString().hashCode();
    }

    @Override
    public boolean equals(final Object other) {

        if (other == null || !other.getClass().equals(Vector3d.class)) {
            return false;
        } else {
            Vector3d otherVector = (Vector3d) other;
            return (x == otherVector.x && y == otherVector.y && z == otherVector.z);
        }
    }


    /**
     * Check if each two dimensions of this and other vector are withing range from each other (inclusive).
     *
     * @param other the vector to compare with
     * @param range the allowed range (inclusive)
     * @return true if vectors are within specified range
     */
    public boolean inRange(final Vector3d other, final int range) {

        return (Math.abs(x - other.x) <= range
                && Math.abs(y - other.y) <= range
                && Math.abs(z - other.z) <= range
        );

    }


}
