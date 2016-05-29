package net.kopeph.icecube.entity;

import net.kopeph.icecube.util.Vector2;

public final class Player extends Entity {
	private static final int color = 0xFFFFFFFF; //white

	private boolean dead = false;
	private int deathFrame = 0; //used to drive the death animation; incremented every frame upon death

	public Player(Player other) {
		super(other);
	}

	public Player(float s, float v) {
		super(new Vector2(0, 0), s, v, color);
	}

	public void moveTo(float x, float y) {
		pos = new Vector2(x - size/2, y - size/2);
	}

	private static final float SP = 0.15f;

	public void move(boolean left, boolean right, boolean up, boolean down, boolean space) {
		//closes the game after the last level
		if (dead) {
			if (game.levelName.equals("end")) //$NON-NLS-1$
				game.exit();
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

		super.tick(offset);

		if (size <= 0.01f) {
			dead = true;
			deathFrame = 0;
		}

		//TODO: add death condition for if player gets outside of level

		//PApplet.println("player: " + pos + "\tvelocity: " + vel + "\tsize: " + size + "\t" + offset); //DEBUG
	}
}
