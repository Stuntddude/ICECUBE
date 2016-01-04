package net.kopeph.icecube.entity;

import net.kopeph.icecube.ICECUBE;
import net.kopeph.icecube.tile.*;
import net.kopeph.icecube.util.Rectangle;
import net.kopeph.icecube.util.Vector2;
import processing.core.PApplet;

public final class Player {
	private final ICECUBE context = ICECUBE.getContext();

	private Vector2 pos, size, vel;
	private int color = 0xFFFFFFFF; //white
	private boolean dead = false;

	public Player(Player other) {
		pos = other.pos;
		size = other.size;
		vel = other.vel;
	}

	public Player(Vector2 pos, Vector2 size) {
		this.pos = pos;
		this.size = size;
		vel = new Vector2(0, 0);
	}

	public Player(float x, float y, float w, float h) {
		this(new Vector2(x, y), new Vector2(w, h));
	}

	public Rectangle getHitbox() {
		return new Rectangle(pos, size.sub(new Vector2(BREATHING_ROOM, BREATHING_ROOM)));
	}

	public void moveTo(Vector2 center) {
		pos = center.sub(size.mul(0.5f));
	}

	public void moveTo(float x, float y) {
		moveTo(new Vector2(x, y));
	}

	private static final float SP = 0.15f;
	private static final float GRAVITY = 0.02f;

	public void move(boolean left, boolean right, boolean up, boolean down, boolean space) {
		//handle death by blipping out of existence
		if (dead) {
			//TODO: death animation
			if (context.levelName.equals("end"))
				context.exit();
			else
				context.resetLevel();
			return;
		}

		Vector2 offset = new Vector2(0, 0);
		if (left)  offset = offset.add(new Vector2(-SP, 0));
		if (right) offset = offset.add(new Vector2( SP, 0));
		//I'm adding small y-offset to the movement so the player doesn't get stuck on the ground
		//this is DUCT TAPE! once the jam is over, the actual problem needs to be diagnosed and addressed
		if ((left || right) && onFloor) pos = pos.add(new Vector2(0, -0.00001f));

		//debug growth
//		if (up)
//			grow();
//		else if (down)
//			shrink();

		//my size gives me strength!
		float jumpStrength = 0.23f + 0.11f*size.x;

		if (space && onFloor && !verticalSlide)
			vel = vel.add(new Vector2(0, -jumpStrength)); //jump!

		//do gravity
		vel = vel.add(new Vector2(0, GRAVITY));

		//so that intersecting multiple interactive tiles doesn't multiply their effect
		boolean shouldGrow = false, shouldShrink = false, boing = false;

		//handle interaction with interactive tiles in the level
		//only loop through tiles near the player, for efficiency
		Rectangle hb = getHitbox();
		int minx = Math.max(0, PApplet.floor(hb.pos.x));
		int maxx = Math.min(context.level.width, PApplet.ceil(hb.right()));
		int miny = Math.max(0, PApplet.floor(hb.pos.y));
		int maxy = Math.min(context.level.height - 1, PApplet.ceil(hb.bottom()));
		for (int y = miny; y <= maxy; ++y) {
			for (int x = minx; x < maxx; ++x) {
				Tile tile = context.level.tileAt(x, y);
				if (tile instanceof TransportTile) {
					if (hb.intersects(tile.getHitbox())) {
						context.changeLevel(((TransportTile)tile).level);
						return;
					}
				} else if (tile instanceof SizePad) {
					if (hb.intersects(tile.getHitbox().move(new Vector2(0, -0.5f)))) {
						if (tile instanceof BluePad) {
							shouldGrow = true;
							//XXX: MORE DUCT TAPE
							pos = pos.sub(new Vector2(0, BREATHING_ROOM));
						} else {
							shouldShrink = true;
						}
					}
				} else if (tile instanceof Spring) {
					if (hb.intersects(tile.getHitbox())) {
						boing = true;
					}
				}
			}
		}

		if (boing && onFloor)
			vel = new Vector2(0, -0.6/PApplet.max(size.x, 0.38f));

		if (shouldShrink)
			shrink();
		if (shouldGrow)
			grow();

		offset = offset.add(vel);

		Vector2 oldPos = pos;
		moveWithCollision(offset);
		Vector2 deltap = pos.sub(oldPos);
		vel = new Vector2(0, deltap.y);

		if (size.x <= 0.0f)
			dead = true;

		//PApplet.println("player: " + pos + "\tvelocity: " + vel + "\tsize: " + size + "\t" + offset); //DEBUG
	}

	private static final float GROWTH = 0.01f;

	private void grow() {
		//XXX: wet code smell
		//try to grow player from the bottom center of their hitbox, if possible
		//otherwise, try growing from the bottom left or bottom right
		if (growImpl(GROWTH/2)) {
			pos = pos.sub(new Vector2(GROWTH/2, GROWTH));
			size = size.add(new Vector2(GROWTH, GROWTH));
		} else if (growImpl(GROWTH)) {
			pos = pos.sub(new Vector2(GROWTH, GROWTH));
			size = size.add(new Vector2(GROWTH, GROWTH));
		} else if (growImpl(0)) {
			pos = pos.sub(new Vector2(0, GROWTH));
			size = size.add(new Vector2(GROWTH, GROWTH));
		}
	}

	private boolean growImpl(float xcomp) {
		Rectangle hb = new Rectangle(pos.sub(new Vector2(xcomp, GROWTH)),
				 					 getHitbox().dim.add(new Vector2(GROWTH, GROWTH)));
		return findIntersection(hb) == null;
	}

	private void shrink() {
		if (size.x > 0.5f) {
			pos = pos.add(new Vector2(GROWTH/2, GROWTH));
			size = size.sub(new Vector2(GROWTH, GROWTH));
		} else {
			pos = pos.add(new Vector2(GROWTH/4, GROWTH/2));
			size = size.sub(new Vector2(GROWTH/2, GROWTH/2));
		}
	}

	//the factor by which to over-eject the entity to avoid potential floating point weirdness
	private static final float EJECTION_EPSILON = 1.00001f;

	//the amount by which to shrink the player's hitbox, also to avoid floating point weirdness
	//this may not be necessary, since the player is supposed to be constantly changing size anyway
	//but I'm keeping it here for now because I don't think it hurts anything to have this safeguard
	private static final float BREATHING_ROOM = 0.0001f; //should be greater than (EJECTION_EPSILON - 1.0)

	//XXX: code smell using class variables for functionality of method internals
	private boolean hasCollided, onFloor, verticalSlide;

	//TODO: contingency plan for if the player does somehow get stuck inside of a tile they can't be ejected out of
	//I assume players will mostly prefer an apparent glitch over the game freezing seemingly for no reason
	//we can do this by keeping a list of tiles we've ejected from, and breaking out if we
	private void moveWithCollision(Vector2 offset) {
		Vector2 projected = pos.add(offset); //projected position of player after applying offset (used for wall sliding)

		offset = moveWithCollisionImpl(offset);

		//keep track of this for jumping logic
		hasCollided = !pos.equals(projected);
		onFloor = hasCollided && projected.y - pos.y >= 0.0f;
		//if we haven't collided, we obviously don't need to slide, so we might as well exit early
		if (!hasCollided)
			return;

		//slide along whatever wall we were last ejected from
		offset = projected.sub(pos);
		offset = verticalSlide? new Vector2(0, offset.y) : new Vector2(offset.x, 0);

		offset = moveWithCollisionImpl(offset);
	}

	/** find and resolve all collisions for a given offset */
	private Vector2 moveWithCollisionImpl(Vector2 offset) {
		Rectangle collision = null;
		do { //handle collisions until the player is free from all tiles
			collision = findIntersection(getHitbox().move(offset));
			if (collision != null)
				offset = eject(collision, offset);
		} while (collision != null);

		pos = pos.add(offset);
		return offset;
	}

	private Rectangle findIntersection(Rectangle hb) {
		//check for collision with the level borders as well as with tiles within the level
		if (hb.intersects(context.level.top))
			return context.level.top;
		if (hb.intersects(context.level.bottom))
			return context.level.bottom;
		if (hb.intersects(context.level.left))
			return context.level.left;
		if (hb.intersects(context.level.right))
			return context.level.right;

		//only loop through tiles near the player, for efficiency
		int minx = Math.max(0, PApplet.floor(hb.pos.x));
		int maxx = Math.min(context.level.width, PApplet.ceil(hb.right()));
		int miny = Math.max(0, PApplet.floor(hb.pos.y));
		int maxy = Math.min(context.level.height, PApplet.ceil(hb.bottom()));
		for (int y = miny; y < maxy; ++y) {
			for (int x = minx; x < maxx; ++x) {
				Tile tile = context.level.tileAt(x, y);
				if (tile != null && tile.hasCollision() && hb.intersects(tile.getHitbox()))
					return tile.getHitbox();
			}
		}
		return null;
	}

	private Vector2 eject(Rectangle collision, Vector2 offset) {
		//XXX: do we need to protect against NaNs?
		if (offset.x == 0.0f && offset.y == 0.0f)
			ICECUBE.println("NaN IN THE DUNGEON! THERE'S A NaN IN THE DUNGEON! Just thought you ought to know...");

		//the projected position after offset, to test for intersections
		Rectangle hb = getHitbox().move(offset);

		//find the shortest path to backtrack that gets the player to where they're not colliding
		//the minimum distance straight along x or y axis the player must be ejected to exit collision
		float dx = offset.x > 0? hb.right()  - collision.pos.x : hb.pos.x - collision.right();
		float dy = offset.y > 0? hb.bottom() - collision.pos.y : hb.pos.y - collision.bottom();

		//XXX: consider changing this so that the slope is only calculated once before all ejections (may remove infinite loop behavior)
		float slope = offset.y/offset.x; //division by 0 should not be an issue since we get infinity, which plays nicely with the next step

		//trace backward along the player's path using each of the supplied ejection distances, and compare their length
		Vector2 ex = new Vector2(dx, dx*slope);
		Vector2 ey = new Vector2(dy/slope, dy); //division by 0 should not be an issue since we get infinity, and that gets discarded at the next step

		//we know that the shorter of the two ejection paths must be correct, so pick that one to work with
		Vector2 ejection = ex.mag() < ey.mag()? ex : ey;
		//whichever path was shorter should also tell us what direction (vertical or horizontal) the player should slide along the wall
		verticalSlide = ex.mag() < ey.mag();

		return offset.sub(ejection.mul(EJECTION_EPSILON));
	}

	public void draw() {
		context.fill(color);
		context.rect(pos.x*Tile.TILE_SIZE - context.origin.x, pos.y*Tile.TILE_SIZE - context.origin.y, size.x*Tile.TILE_SIZE, size.y*Tile.TILE_SIZE);
	}
}
