package net.kopeph.icecube.tile;

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
	public void draw(float x, float y, float w, float h) {
		context.fill(0xFF000000); //black
		context.rect(x, y, w, h);

		//draws a half height tile
		context.fill(color);
		int wx = Math.round(pos.x), wy = Math.round(pos.y);
		boolean neighborLeft = context.level.tileAt(wx - 1, wy) instanceof SizePad;
		boolean neighborRight = context.level.tileAt(wx + 1, wy) instanceof SizePad;

		//draw the pad into the top half of the tile, rounding bottom corners where applicable
		context.rect(x, y, w, h/2,
					 0, 0, neighborRight? 0 : h/4, neighborLeft? 0 : h/4);
	}
}
