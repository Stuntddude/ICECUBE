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
		game.quad(pos.x + 0.5f, pos.y, pos.x + 1, pos.y + 0.5f, pos.x + 0.5f, pos.y + 1, pos.x, pos.y + 0.5f);
	}
}
