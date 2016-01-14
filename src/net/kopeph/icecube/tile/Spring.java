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
	public void draw(float x, float y, float w, float h) {
		context.fill(color);

		int wx = Math.round(pos.x), wy = Math.round(pos.y);
		boolean neighborLeft = context.level.tileAt(wx - 1, wy) instanceof Spring;
		boolean neighborRight = context.level.tileAt(wx + 1, wy) instanceof Spring;

		//draw the spring into the bottom 1/4 of the tile, rounding top corners where applicable
		context.rect(x, y + w*3/4, w, h/4,
					 neighborLeft? 0 : w, neighborRight? 0 : w, 0, 0);
	}
}
