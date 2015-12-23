package net.kopeph.icecube.tile;

import net.kopeph.icecube.util.Vector2;
import processing.core.PGraphics;

public final class GoalTile extends TransportTile {
	private static final int COLOR = 0xFF00FF00; //green

	public GoalTile(Vector2 pos, String levelName) {
		super(pos, COLOR, levelName);
	}

	public GoalTile(float x, float y, String levelName) {
		super(x, y, COLOR, levelName);
	}

	@Override
	public void draw() {
		context.fill(color); //draws a square at 45 deg
		context.quad(pos.x*TILE_SIZE + TILE_SIZE/2 - context.origin.x, pos.y*TILE_SIZE               - context.origin.y,
					 pos.x*TILE_SIZE + TILE_SIZE   - context.origin.x, pos.y*TILE_SIZE + TILE_SIZE/2 - context.origin.y,
					 pos.x*TILE_SIZE + TILE_SIZE/2 - context.origin.x, pos.y*TILE_SIZE + TILE_SIZE   - context.origin.y,
					 pos.x*TILE_SIZE               - context.origin.x, pos.y*TILE_SIZE + TILE_SIZE/2 - context.origin.y);
	}

	@Override
	public void draw(PGraphics canvas) {
		canvas.fill(color);
		canvas.quad(pos.x*TILE_SIZE + TILE_SIZE/2, pos.y*TILE_SIZE,
					pos.x*TILE_SIZE + TILE_SIZE,   pos.y*TILE_SIZE + TILE_SIZE/2,
					pos.x*TILE_SIZE + TILE_SIZE/2, pos.y*TILE_SIZE + TILE_SIZE,
					pos.x*TILE_SIZE,               pos.y*TILE_SIZE + TILE_SIZE/2);
	}
}
