package net.kopeph.icecube.tile;

import net.kopeph.icecube.ICECUBE;
import net.kopeph.icecube.util.Rectangle;
import net.kopeph.icecube.util.Vector2;
import processing.core.PApplet;

/** The common superclass of all tiles in the game. */
public abstract class Tile {
	public static float REAL_TILE_SIZE = 24;
	public static float TILE_SIZE = 24; //REAL_TILE_SIZE rounded to the nearest int

	protected final ICECUBE context = ICECUBE.getContext();

	public final Vector2 pos;
	public final int color;

	public Tile(Vector2 pos, int color) {
		this.pos = pos;
		this.color = color;
	}

	public Tile(float x, float y, int color) {
		this(new Vector2(x, y), color);
	}

	public boolean hasCollision() {
		return false;
	}

	/** @return an AABB in world-space representing the hitbox of this tile */
	public Rectangle getHitbox() {
		return new Rectangle(pos.x, pos.y, 1, 1);
	}

	//this should be overridden for anything planning to be non-square!
	//like half-tiles and springs and shit
	public void draw() {
		float x = PApplet.round(pos.x*REAL_TILE_SIZE - context.originf.x);
		float y = PApplet.round(pos.y*REAL_TILE_SIZE - context.originf.y);
		float w = PApplet.round(pos.x*REAL_TILE_SIZE + REAL_TILE_SIZE - context.originf.x) - x;
		float h = PApplet.round(pos.y*REAL_TILE_SIZE + REAL_TILE_SIZE - context.originf.y) - y;
		draw(x, y, w, h);
	}

	//this should be overridden by anything planning to not be an actual solid-color square
	//including size tiles and goal tiles
	protected void draw(float x, float y, float w, float h) {
		context.fill(color);
		context.rect(x, y, w, h);
	}
}
