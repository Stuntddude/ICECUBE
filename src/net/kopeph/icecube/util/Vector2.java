package net.kopeph.icecube.util;

import java.util.Arrays;

/**
 * @immutable
 * @author alexg
 */
public final class Vector2  {
	public final float x;
	public final float y;

	/** constructs the <0, 0> vector */
	public Vector2() {
		this(0, 0);
	}

	/** Copy constructor */
	public Vector2(Vector2 other) {
		this(other.x, other.y);
	}

	/** constructs an <x, y> vector */
	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/** constructs an <x, y> vector */
	public Vector2(double x, double y) {
		this.x = (float)x;
		this.y = (float)y;
	}

	/**
	 * Constructs a vector based polar coordinates.
	 * @param m magnitude, or length
	 * @param t theta, or angle (in radians)
	 */
	public static Vector2 polar(float m, float t) {
		return new Vector2(m * Math.cos(t), m * Math.sin(t));
	}

	/** @return the magnitude or length of this vector */
	public float mag() {
		return (float)Math.sqrt(x * x + y * y);
	}

	/** @return the angle of this vector */
	public float theta() {
		return (float)Math.atan2(y, x);
	}

	/**
	 * Adds this and the supplied vector together.
	 * @return the resultant vector
	 */
	public Vector2 add(Vector2 rhs) {
		return new Vector2(x + rhs.x, y + rhs.y);
	}

	/**
	 * Subtracts the supplied vector from this
	 * @return the resultant vector
	 */
	public Vector2 sub(Vector2 rhs) {
		return new Vector2(x - rhs.x, y - rhs.y);
	}

	@Override
	public boolean equals(Object rhs) {
		if (this == rhs) return true;
		if (rhs.getClass() != this.getClass()) return false;
		return equals((Vector2) rhs);
	}

	/** See: equals(Object) */
	public boolean equals(Vector2 rhs) {
		return x == rhs.x && y == rhs.y;
	}

	@Override
	public int hashCode() {
		return Arrays.asList(x, y).hashCode();
	}

	/**
	 * Scales this vector by the supplied scalar.
	 * @return the resultant vector
	 */
	public Vector2 mul(double scalar) {
		return new Vector2(scalar * x, scalar * y);
	}

	/**
	 * Performs the dot product of this and the supplied vector.
	 * @return the resultant scalar
	 */
	public float dotMul(Vector2 rhs) {
		return x * rhs.x + y * rhs.y;
	}

	/**
	 * Performs the cross product of this and the supplied vector.
	 * @return the resultant scalar
	 */
	public float crossMul(Vector2 rhs) {
		return x * rhs.y - y * rhs.x;
	}

	/**
	 * Multiplies the vector components together.
	 * @return the resultant scalar
	 */
	public float compMul() {
		return x * y;
	}

	/**
	 * Multiplies this vector with the supplied vector by their components.
	 * @return the resultant scalar
	 */
	public Vector2 compMul(Vector2 rhs) {
		return new Vector2(x * rhs.x, y * rhs.y);
	}

	/**
	 * Adjusts the length of the vector (without changing the angle) such that it is 1 unit long.
	 * @return the resultant vector
	 */
	public Vector2 normalize() {
		if (mag() != 0) {
			return new Vector2(x / mag(), y / mag());
		}
		return new Vector2(0, 0);
	}

	/** @return the angle from this vector to to the supplied vector */
	public float thetaTo(Vector2 other) {
		return sub(other).theta();
	}

	@Override
	public String toString() {
		return String.format("<%.6f, %.6f>", x, y); //$NON-NLS-1$
	}
}
