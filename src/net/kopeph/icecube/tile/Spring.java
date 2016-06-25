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
		boolean neighborLeft = game.level.tileAt(x - 1, y) instanceof Spring;
		boolean neighborRight = game.level.tileAt(x + 1, y) instanceof Spring;

		//adjust the hitbox width and xpos to join up with its neighbors, if it has any
		if (neighborLeft && neighborRight)
			return new Rectangle(pos.x, pos.y, 1.0f, 0.25f).move(0.0f, 0.75f);
		if (neighborRight)
			return new Rectangle(pos.x, pos.y, 0.5f, 0.25f).move(0.5f, 0.75f);
		if (neighborLeft)
			return new Rectangle(pos.x, pos.y, 0.5f, 0.25f).move(0.0f, 0.75f);
		return new Rectangle(pos.x, pos.y, 0.5f, 0.25f).move(0.25f, 0.75f);
	}

	@Override
	public void draw() {
		game.fill(color);

		int x = Math.round(pos.x), y = Math.round(pos.y);
		boolean neighborLeft = game.level.tileAt(x - 1, y) instanceof Spring;
		boolean neighborRight = game.level.tileAt(x + 1, y) instanceof Spring;

		//draw the spring into the bottom 1/4 of the tile, rounding top corners where applicable
		game.rect(pos.x, pos.y + 0.75f, 1, 0.25f, neighborLeft? 0 : 1, neighborRight? 0 : 1, 0, 0);

		if (game.debug)
			game.drawDebugHitbox(getHitbox(), getHitbox().intersects(game.player.getHitbox()));
	}
}
