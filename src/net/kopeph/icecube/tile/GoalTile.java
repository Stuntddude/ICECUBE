package net.kopeph.icecube.tile;

import net.kopeph.icecube.util.Vector2;

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
		game.fill(color); //draws a square at 45 deg
		game.quad(pos.x*TILE_SIZE + TILE_SIZE/2 - game.origin.x, pos.y*TILE_SIZE               - game.origin.y,
		          pos.x*TILE_SIZE + TILE_SIZE   - game.origin.x, pos.y*TILE_SIZE + TILE_SIZE/2 - game.origin.y,
		          pos.x*TILE_SIZE + TILE_SIZE/2 - game.origin.x, pos.y*TILE_SIZE + TILE_SIZE   - game.origin.y,
		          pos.x*TILE_SIZE               - game.origin.x, pos.y*TILE_SIZE + TILE_SIZE/2 - game.origin.y);
	}
}
