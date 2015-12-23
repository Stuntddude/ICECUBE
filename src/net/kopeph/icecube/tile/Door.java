package net.kopeph.icecube.tile;

import net.kopeph.icecube.util.Vector2;

public final class Door extends TransportTile {
	private static final int COLOR = 0xFFFFFF00; //yellow

	public Door(Vector2 pos, String levelName) {
		super(pos, COLOR, levelName);
	}

	public Door(float x, float y, String levelName) {
		super(x, y, COLOR, levelName);
	}
}
