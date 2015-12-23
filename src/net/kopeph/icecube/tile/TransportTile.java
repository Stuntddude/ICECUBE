package net.kopeph.icecube.tile;

import net.kopeph.icecube.util.Vector2;

/** common superclass of Door and GoalTile */
public class TransportTile extends Tile {
	public final String level;

	public TransportTile(Vector2 pos, int color, String levelName) {
		super(pos, color);
		level = levelName;
	}

	public TransportTile(float x, float y, int color, String levelName) {
		super(x, y, color);
		level = levelName;
	}

}
