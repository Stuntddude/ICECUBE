package net.kopeph.icecube.tile;

import net.kopeph.icecube.util.Vector2;

public final class WallTile extends Tile {
	private static final int COLOR = 0xFF000000;

	public WallTile(Vector2 pos) {
		super(pos, COLOR);
	}

	public WallTile(float x, float y) {
		super(x, y, COLOR);
	}

	@Override
	public boolean hasCollision() {
		return true;
	}
}
