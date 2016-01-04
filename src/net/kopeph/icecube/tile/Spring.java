package net.kopeph.icecube.tile;

import net.kopeph.icecube.util.Rectangle;
import net.kopeph.icecube.util.Vector2;

public final class Spring extends Tile {
	private static final int COLOR = 0xFFFF8000; //orange

	public Spring(Vector2 pos) {
		super(pos, COLOR);
	}

	public Spring(float x, float y) {
		super(x, y, COLOR);
	}

	@Override
	public Rectangle getHitbox() {
		//XXX: wet code smell
		int x = Math.round(pos.x), y = Math.round(pos.y);
		boolean neighborLeft = context.level.tileAt(x - 1, y) instanceof Spring;
		boolean neighborRight = context.level.tileAt(x + 1, y) instanceof Spring;

		//adjust the hitbox width and xpos to join up with its neighbors, if it has any
		if (neighborLeft && neighborRight)
			return new Rectangle(pos, new Vector2(1.0f, 0.25f)).move(new Vector2(0.0f, 0.75f));
		if (neighborRight)
			return new Rectangle(pos, new Vector2(0.5f, 0.25f)).move(new Vector2(0.5f, 0.75f));
		if (neighborLeft)
			return new Rectangle(pos, new Vector2(0.5f, 0.25f)).move(new Vector2(0.0f, 0.75f));
		return new Rectangle(pos, new Vector2(0.5f, 0.25f)).move(new Vector2(0.25f, 0.75f));
	}

	@Override
	public void draw() {
		context.fill(color);

		int x = Math.round(pos.x), y = Math.round(pos.y);
		boolean neighborLeft = context.level.tileAt(x - 1, y) instanceof Spring;
		boolean neighborRight = context.level.tileAt(x + 1, y) instanceof Spring;

		//draw the spring into the bottom 1/4 of the tile, rounding top corners where applicable
		context.rect(pos.x*TILE_SIZE - context.origin.x, pos.y*TILE_SIZE + TILE_SIZE*3/4 - context.origin.y, TILE_SIZE, TILE_SIZE/4,
					 neighborLeft? 0 : TILE_SIZE, neighborRight? 0 : TILE_SIZE, 0, 0);
	}
}
