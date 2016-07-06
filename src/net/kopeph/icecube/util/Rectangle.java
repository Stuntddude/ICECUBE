package net.kopeph.icecube.util;

import net.kopeph.icecube.entity.Entity;

/** @immutable */
public final class Rectangle {
	public final float x, y, w, h;
	public final Entity owner; //the Entity whose hitbox this is, or null if this is not an Entity's hitbox

	public Rectangle(float x, float y, float w, float h, Entity owner) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.owner = owner;
	}

	public Rectangle(Vector2 position, Vector2 dimensions, Entity owner) {
		x = position.x;
		y = position.y;
		w = dimensions.x;
		h = dimensions.y;
		this.owner = owner;
	}

	public Rectangle(float x, float y, float w, float h) {
		this(x, y, w, h, null);
	}

	public Rectangle(Vector2 position, Vector2 dimensions) {
		this(position, dimensions, null);
	}

	public Rectangle move(float dx, float dy) {
		return new Rectangle(x + dx, y + dy, w, h);
	}

	public Rectangle move(Vector2 offset) {
		return new Rectangle(x + offset.x, y + offset.y, w, h);
	}

	public Rectangle grow(float dw, float dh) {
		return new Rectangle(x, y, w + dw, h + dh);
	}

	public Rectangle grow(Vector2 offset) {
		return new Rectangle(x, y, w + offset.x, h + offset.y);
	}

	public Vector2 center() {
		return new Vector2(x + w/2, y + h/2);
	}

	public float top() {
		return x;
	}

	public float left() {
		return y;
	}

	public float right() {
		return x + w;
	}

	public float bottom() {
		return y + h;
	}

	public boolean contains(float x0, float y0) {
		return x0 > x && y0 > y && x0 < x + w && y0 < y + h;
	}

	public boolean contains(Vector2 v) {
		return contains(v.x, v.y);
	}

	public boolean intersects(Rectangle other) {
		return x < other.x + other.w && other.x < x + w &&
		       y < other.y + other.h && other.y < y + h;
	}
}
