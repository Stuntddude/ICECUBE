package net.kopeph.icecube.tile;

import net.kopeph.icecube.ICECUBE;
import net.kopeph.icecube.util.Rectangle;
import net.kopeph.icecube.util.Vector2;
import processing.core.PGraphics;

public class Tile {
	public static float REAL_TILE_SIZE = 24;
	public static float TILE_SIZE = 24; //REAL_TILE_SIZE rounded to the nearest int

	protected final ICECUBE context = ICECUBE.getContext();

	public final Vector2 pos;
	public final int color;

	//maybe make protected
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

	public boolean hasAnimation() {
		return false;
	}

	/** @return an AABB in world-space representing the hitbox of this tile */
	public Rectangle toRect() {
		return new Rectangle(pos.x, pos.y, 1, 1);
	}

	//this should be overridden for anything planning to be non-square!
	//like ramps and buttons and shit
	//if hasAnimation() is not overridden, there's currently no reason to override this funciton
	public void draw() {
		context.fill(color);
		context.rect(pos.x*TILE_SIZE - context.origin.x, pos.y*TILE_SIZE - context.origin.y, TILE_SIZE, TILE_SIZE);
	}

	//if hasAnimation() returns false, the tile will be baked onto a pre-rendered canvas for performance reasons
	//this method handles that. If you are overriding hasAnimation() to return true, you do NOT need to override this
	public void draw(PGraphics canvas) {
		canvas.fill(color);
		canvas.rect(pos.x*TILE_SIZE, pos.y*TILE_SIZE, TILE_SIZE, TILE_SIZE);
	}
}
