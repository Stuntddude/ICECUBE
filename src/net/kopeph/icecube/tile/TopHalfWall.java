package net.kopeph.icecube.tile;

import net.kopeph.icecube.util.Rectangle;
import net.kopeph.icecube.util.Vector2;

public final class TopHalfWall extends Tile {
	private static final int COLOR = 0xFF000000;

	public TopHalfWall(Vector2 pos) {
		super(pos, COLOR);
	}

	public TopHalfWall(float x, float y) {
		super(x, y, COLOR);
	}

	@Override
	public boolean hasCollision() {
		return true;
	}

	@Override
	public Rectangle getHitbox() {
		return new Rectangle(pos.x, pos.y, 1.0f, 0.5f);
	}

	@Override
	public void draw() {
		game.fill(color);
		game.rect(pos.x*TILE_SIZE - game.origin.x, pos.y*TILE_SIZE - game.origin.y, TILE_SIZE, TILE_SIZE/2);
	}
}
