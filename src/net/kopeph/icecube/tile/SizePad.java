package net.kopeph.icecube.tile;

import net.kopeph.icecube.util.Vector2;

/** common superclass of RedTile and BlueTile */
public class SizePad extends Tile {
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
		context.fill(0xFF000000); //black
		context.rect(pos.x*TILE_SIZE - context.origin.x, pos.y*TILE_SIZE - context.origin.y, TILE_SIZE, TILE_SIZE);

		//draws a half height tile
		context.fill(color);
		int x = Math.round(pos.x), y = Math.round(pos.y);
		boolean neighborLeft = context.level.tileAt(x - 1, y) instanceof SizePad;
		boolean neighborRight = context.level.tileAt(x + 1, y) instanceof SizePad;

		//draw the pad into the top half of the tile, rounding bottom corners where applicable
		context.rect(pos.x*TILE_SIZE - context.origin.x, pos.y*TILE_SIZE - context.origin.y, TILE_SIZE, TILE_SIZE/2,
					 0, 0, neighborRight? 0 : TILE_SIZE, neighborLeft? 0 : TILE_SIZE);
	}
}
