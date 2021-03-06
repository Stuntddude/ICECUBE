package net.kopeph.icecube.tile;

import net.kopeph.icecube.util.Vector2;

public final class BluePad extends SizePad {
	public static final int COLOR = 0xFF1133FF; //blue

	public BluePad(Vector2 pos) {
		super(pos, COLOR);
	}

	public BluePad(float x, float y) {
		super(x, y, COLOR);
	}
}
