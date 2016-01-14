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
	public void draw(float x, float y, float w, float h) {
		context.fill(color); //draws a square at 45 deg
		context.quad(x + w/2, y, x + w, y + h/2, x + w/2, y + h, x, y + h/2);
	}
}
