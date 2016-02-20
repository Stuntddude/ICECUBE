package net.kopeph.icecube.util;

/** @immutable */
public final class Rectangle {
	public final Vector2 pos, dim;

	public Rectangle(Vector2 position, Vector2 dimensions) {
		pos = position;
		dim = dimensions;
	}

	public Rectangle(float x, float y, float w, float h) {
		this(new Vector2(x, y), new Vector2(w, h));
	}

	public Rectangle move(Vector2 offset) {
		return new Rectangle(pos.add(offset), dim);
	}

	public Vector2 center() {
		return new Vector2(pos.x + (dim.x/2), pos.y + (dim.y/2));
	}

	public float right() {
		return pos.x + dim.x;
	}

	public float bottom() {
		return pos.y + dim.y;
	}

	public boolean contains(float x, float y) {
		return (x > pos.x && y > pos.y && x < pos.x + dim.x && y < pos.y + dim.y);
	}

	public boolean contains(Vector2 v) {
		return contains(v.x, v.y);
	}

	public boolean intersects(Rectangle other) {
		return (pos.x < other.pos.x + other.dim.x && other.pos.x < pos.x + dim.x &&
		        pos.y < other.pos.y + other.dim.y && other.pos.y < pos.y + dim.y);
	}
}
