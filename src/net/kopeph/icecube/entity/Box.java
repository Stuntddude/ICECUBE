package net.kopeph.icecube.entity;

import net.kopeph.icecube.util.Rectangle;
import net.kopeph.icecube.util.Vector2;

public final class Box extends Entity {
	private static final int color = 0xFFDDDDEE; //off-white

	public Box(Box other) {
		super(other);
	}

	public Box(Vector2 p, float s, float v) {
		super(p, s, v, color);
	}

	public Box(float x, float y, float s, float v) {
		super(x, y, s, v, color);
	}

	//make boxes able to properly collide with the player
	@Override
	protected Rectangle findIntersection(Rectangle hb) {
		Rectangle other = super.findIntersection(hb);
		if (other != null)
			return other;

		if (hb.intersects(game.player.getHitbox()))
			return game.player.getHitbox();

		return null;
	}
}
