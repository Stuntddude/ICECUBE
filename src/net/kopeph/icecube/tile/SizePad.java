package net.kopeph.icecube.tile;

import net.kopeph.icecube.util.Rectangle;
import net.kopeph.icecube.util.Vector2;

/** common superclass of RedTile and BlueTile */
public abstract class SizePad extends Tile {
	public SizePad(Vector2 pos, int color) {
		super(pos, color);
	}

	public SizePad(float x, float y, int color) {
		super(x, y, color);
	}

	@Override
	public boolean hasCollision() {
		return true;
	}

	@Override
	public void draw() {
		game.fill(0xFF000000); //black
		game.rect(pos.x, pos.y, 1, 1);

		//draws a half height tile
		game.fill(color);
		int x = Math.round(pos.x), y = Math.round(pos.y);
		boolean neighborLeft = game.level.tileAt(x - 1, y) instanceof SizePad;
		boolean neighborRight = game.level.tileAt(x + 1, y) instanceof SizePad;

		//draw the pad into the top half of the tile, rounding bottom corners where applicable
		game.rect(pos.x, pos.y, 1, 0.5f, 0, 0, neighborRight? 0 : 1, neighborLeft? 0 : 1);

		if (game.debug) {
			Rectangle hb = getHitbox().move(0, -0.5f);
			game.drawDebugHitbox(hb, game.level.occupied(hb));
		}
	}
}
