package net.kopeph.icecube.entity;

import processing.core.PApplet;

import net.kopeph.icecube.tile.Tile;
import net.kopeph.icecube.tile.TransportTile;
import net.kopeph.icecube.util.Rectangle;
import net.kopeph.icecube.util.Vector2;

public final class Player extends Entity {
	private static final int color = 0xFFFFFFFF; //white

	public Player(Player other) {
		super(other);
	}

	public Player(float s, float v) {
		super(new Vector2(0, 0), s, v, color);
	}

	private static final float SP = 0.15f;

	public void move(boolean left, boolean right, boolean up, boolean down, boolean space) {
		//closes the game after the last level
		if (dead) {
			if (game.levelName.equals("old/end")) //$NON-NLS-1$
				game.exit();

			if (deathFrame > 36) //XXX: magic framerate-dependent constant
				game.resetLevel();

			return;
		}

		Vector2 offset = new Vector2(0, 0);
		if (left)  offset.addEquals(-SP, 0);
		if (right) offset.addEquals( SP, 0);
		//I'm adding small y-offset to the movement so the player doesn't get stuck on the ground
		//this is DUCT TAPE! once the jam is over, the actual problem needs to be diagnosed and addressed
		if ((left || right) && onFloor) pos.addEquals(0, -0.00001f);

		//debug growth
		if (up)
			grow();
		else if (down)
			shrink();

		//my size gives me strength!
		float jumpStrength = 0.23f + 0.11f*size;

		if (space && onFloor)
			vel = -jumpStrength; //jump!

		//interact with TransportTiles
		//only loop through tiles near the player, for efficiency
		Rectangle hb = getHitbox();
		int minx = Math.max(0, PApplet.floor(hb.x));
		int maxx = Math.min(game.level.width, PApplet.ceil(hb.right()));
		int miny = Math.max(0, PApplet.floor(hb.y));
		int maxy = Math.min(game.level.height, PApplet.ceil(hb.bottom()));
		for (int y = miny; y < maxy; ++y) {
			for (int x = minx; x < maxx; ++x) {
				Tile tile = game.level.tileAt(x, y);
				if (tile instanceof TransportTile) {
					game.changeLevel(((TransportTile)tile).level);
					return;
				}
			}
		}

		super.tick(offset);

		//TODO: add death condition for if player gets outside of level

		//PApplet.println("player: " + pos + "\tvelocity: " + vel + "\tsize: " + size + "\t" + offset); //DEBUG
	}
}
