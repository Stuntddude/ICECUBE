package net.kopeph.icecube.tile;

import net.kopeph.icecube.util.Vector2;

public final class RedPad extends SizePad {
	public static final int COLOR = 0xFF1133FF; //blue

	public RedPad(Vector2 pos) {
		super(pos, COLOR);
	}

	public RedPad(float x, float y) {
		super(x, y, COLOR);
	}
}
