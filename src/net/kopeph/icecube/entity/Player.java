package net.kopeph.icecube.entity;

import processing.core.PApplet;

import net.kopeph.icecube.ICECUBE;
import net.kopeph.icecube.tile.*;
import net.kopeph.icecube.util.Rectangle;
import net.kopeph.icecube.util.Vector2;

public final class Player {
	private static final int color = 0xFFFFFFFF; //white
	private final ICECUBE game = ICECUBE.game;

	private Vector2 pos;
	private float size;
	private float vel;
	private boolean dead = false;
	private int deathFrame = 0; //used to drive the death animation; incremented every frame upon death

	public Player(Player other) {
		pos = new Vector2(other.pos);
		size = other.size;
		vel = other.vel;
	}

	public Player(float s, float v) {
		pos = new Vector2();
		size = s;
		vel = v;
	}

	@Override
	public String toString() {
		return size + " " + vel; //$NON-NLS-1$
	}

	public Rectangle getHitbox() {
		return new Rectangle(pos.x, pos.y, size - BREATHING_ROOM, size - BREATHING_ROOM);
	}

	public void moveTo(float x, float y) {
		pos = new Vector2(x - size/2, y - size/2);
	}

	private static final float SP = 0.15f;
	private static final float GRAVITY = 0.02f;

	public void move(boolean left, boolean right, boolean up, boolean down, boolean space) {
		//handle death by blipping out of existence
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

		if (space && onFloor && !verticalSlide)
			vel = -jumpStrength; //jump!

		//do gravity
		vel += GRAVITY;

		//so that intersecting multiple interactive tiles doesn't multiply their effect
		boolean shouldGrow = false, shouldShrink = false, boing = false;

		//handle interaction with interactive tiles in the level
		//only loop through tiles near the player, for efficiency
		Rectangle hb = getHitbox();
		int minx = Math.max(0, PApplet.floor(hb.x));
		int maxx = Math.min(game.level.width, PApplet.ceil(hb.right()));
		int miny = Math.max(0, PApplet.floor(hb.y));
		int maxy = Math.min(game.level.height - 1, PApplet.ceil(hb.bottom()));
		for (int y = miny; y <= maxy; ++y) {
			for (int x = minx; x < maxx; ++x) {
				Tile tile = game.level.tileAt(x, y);
				if (tile instanceof TransportTile) {
					if (hb.intersects(tile.getHitbox())) {
						game.changeLevel(((TransportTile)tile).level);
						return;
					}
				} else if (tile instanceof SizePad) {
					if (hb.intersects(tile.getHitbox().move(0, -0.5f))) {
						if (tile instanceof BluePad) {
							shouldGrow = true;
							//XXX: MORE DUCT TAPE
							pos.subEquals(0, BREATHING_ROOM);
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
			vel = -0.6f/PApplet.max(size, 0.38f);

		if (shouldShrink)
			shrink();
		if (shouldGrow)
			grow();

		offset.addEquals(0, vel);

		float oldPosY = pos.y;
		moveWithCollision(offset);
		vel = pos.y - oldPosY;

		if (size <= 0.0f) {
			dead = true;
			deathFrame = 0;
		}

		//PApplet.println("player: " + pos + "\tvelocity: " + vel + "\tsize: " + size + "\t" + offset); //DEBUG
	}

	private static final float GROWTH = 0.01f;

	private void grow() {
		//try to grow player from the bottom center of their hitbox, if possible
		//otherwise, try growing from the bottom left or bottom right
		if (growImpl(GROWTH/2)) {
			pos.subEquals(GROWTH/2, GROWTH);
			size += GROWTH;
		} else if (growImpl(GROWTH)) {
			pos.subEquals(GROWTH, GROWTH);
			size += GROWTH;
		} else if (growImpl(0)) {
			pos.subEquals(0, GROWTH);
			size += GROWTH;
		}
	}

	private boolean growImpl(float xcomp) {
		return findIntersection(getHitbox().move(-xcomp, -GROWTH).grow(GROWTH, GROWTH)) == null;
	}

	private void shrink() {
		if (size > 0.5f) {
			pos.addEquals(GROWTH/2, GROWTH);
			size -= GROWTH;
		} else {
			pos.addEquals(GROWTH/4, GROWTH/2);
			size -= GROWTH;
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
		Rectangle collision = findIntersection(getHitbox().move(offset));
		//handle collisions until the player is free from all tiles
		while (collision != null) {
			offset = eject(collision, offset);
			collision = findIntersection(getHitbox().move(offset));
		}

		pos.addEquals(offset);
		return offset;
	}

	private Rectangle findIntersection(Rectangle hb) {
		//check for collision with the level borders as well as with tiles within the level
		if (hb.intersects(game.level.top))
			return game.level.top;
		if (hb.intersects(game.level.bottom))
			return game.level.bottom;
		if (hb.intersects(game.level.left))
			return game.level.left;
		if (hb.intersects(game.level.right))
			return game.level.right;

		//only loop through tiles near the player, for efficiency
		int minx = Math.max(0, PApplet.floor(hb.x));
		int maxx = Math.min(game.level.width, PApplet.ceil(hb.right()));
		int miny = Math.max(0, PApplet.floor(hb.y));
		int maxy = Math.min(game.level.height, PApplet.ceil(hb.bottom()));
		for (int y = miny; y < maxy; ++y) {
			for (int x = minx; x < maxx; ++x) {
				Tile tile = game.level.tileAt(x, y);
				if (tile != null && tile.hasCollision() && hb.intersects(tile.getHitbox()))
					return tile.getHitbox();
			}
		}
		return null;
	}

	private Vector2 eject(Rectangle collision, Vector2 offset) {
		//XXX: do we need to protect against NaNs?
		if (offset.x == 0.0f && offset.y == 0.0f)
			ICECUBE.println("NaN IN THE DUNGEON! THERE'S A NaN IN THE DUNGEON! Just thought you ought to know..."); //$NON-NLS-1$

		//the projected position after offset, to test for intersections
		Rectangle hb = getHitbox().move(offset);

		//find the shortest path to backtrack that gets the player to where they're not colliding
		//the minimum distance straight along x or y axis the player must be ejected to exit collision
		float dx = offset.x > 0? hb.right()  - collision.x : hb.x - collision.right();
		float dy = offset.y > 0? hb.bottom() - collision.y : hb.y - collision.bottom();

		//XXX: consider changing this so that the slope is only calculated once before all ejections (may remove infinite loop behavior)
		float slope = offset.y/offset.x; //division by 0 should not be an issue since we get infinity, which plays nicely with the next step

		//trace backward along the player's path using each of the supplied ejection distances, and compare their length
		Vector2 ex = new Vector2(dx, dx*slope);
		Vector2 ey = new Vector2(dy/slope, dy); //division by 0 should not be an issue since we get infinity, and that gets discarded at the next step

		//we know that the shorter of the two ejection paths must be correct, so pick that one to work with
		Vector2 ejection = ex.mag() < ey.mag()? ex : ey;
		//whichever path was shorter should also tell us what direction (vertical or horizontal) the player should slide along the wall
		verticalSlide = ex.mag() < ey.mag();

		return offset.subEquals(ejection.mulEquals(EJECTION_EPSILON));
	}

	public void draw() {
		game.fill(color);
		game.rect(pos.x*Tile.TILE_SIZE - game.origin.x, pos.y*Tile.TILE_SIZE - game.origin.y, size*Tile.TILE_SIZE, size*Tile.TILE_SIZE);

		//TODO: extract to generic animations system
		if (dead) {
			//cosine is used here to provide a smooth easing for the animation, not for any trigonometric purpose
			float start = PApplet.cos(PApplet.constrain(deathFrame/24.0f       , 0.0f, 1.0f)*PApplet.PI)*0.5f - 0.5f; //XXX: magic framerate-dependent constant
			float end   = PApplet.cos(PApplet.constrain(deathFrame/24.0f - 0.5f, 0.0f, 1.0f)*PApplet.PI)*0.5f - 0.5f; //XXX: magic framerate-dependent constant

			//setup style for line-drawing
			game.stroke(0xFFFFFFFF); //white
			game.strokeWeight(Tile.TILE_SIZE/8.0f); //XXX: magic constant
			game.strokeCap(PApplet.SQUARE);

			//use <s>the force</s> trigonometry to draw the line radially 6 times
			for (int i = 0; i < 6; ++i) {
				float sx = PApplet.sin(PApplet.PI*i/3.0f) * start;
				float sy = PApplet.cos(PApplet.PI*i/3.0f) * start;
				float ex = PApplet.sin(PApplet.PI*i/3.0f) * end;
				float ey = PApplet.cos(PApplet.PI*i/3.0f) * end;

				game.line((pos.x + sx)*Tile.TILE_SIZE - game.origin.x, (pos.y + sy)*Tile.TILE_SIZE - game.origin.y,
				          (pos.x + ex)*Tile.TILE_SIZE - game.origin.x, (pos.y + ey)*Tile.TILE_SIZE - game.origin.y);
			}

			//reset style for rect-drawing
			game.noStroke();

			if (deathFrame > 36) //XXX: magic framerate-dependent constant
				game.resetLevel();

			++deathFrame;
		}
	}
}
