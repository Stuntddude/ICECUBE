package net.kopeph.icecube.entity;

import processing.core.PApplet;
import processing.core.PConstants;

import net.kopeph.icecube.ICECUBE;
import net.kopeph.icecube.tile.BluePad;
import net.kopeph.icecube.tile.SizePad;
import net.kopeph.icecube.tile.Spring;
import net.kopeph.icecube.tile.Tile;
import net.kopeph.icecube.util.Rectangle;
import net.kopeph.icecube.util.Vector2;

public class Entity {
	protected static final ICECUBE game = ICECUBE.game;

	protected Vector2 pos;
	protected float size;
	protected float vel;

	private final int color;

	public boolean dead = false;
	public int deathFrame = 0; //used to drive the death animation; incremented every frame upon death

	public Entity(Entity other) {
		pos = new Vector2(other.pos);
		size = other.size;
		vel = other.vel;
		color = other.color;
	}

	public Entity(Vector2 p, float s, float v, int c) {
		pos = p;
		size = s;
		vel = v;
		color = c;
	}

	public Entity(float x, float y, float s, float v, int c) {
		pos = new Vector2(x, y);
		size = s;
		vel = v;
		color = c;
	}

	@Override
	public String toString() {
		return size + " " + vel; //$NON-NLS-1$
	}

	public Rectangle getHitbox() {
		return new Rectangle(pos.x, pos.y, size, size, this);
	}

	public Rectangle getGroundSensor() {
		return new Rectangle(pos.x, pos.y + size, size, 0.01f);
	}

	public void moveTo(float x, float y) {
		pos = new Vector2(x - size/2, y - size/2);
	}

	public boolean onGround() {
		return findIntersection(getGroundSensor()) != null;
	}

	protected static final float GRAVITY = 0.02f;

	public void tick(Vector2 offset) {
		if (dead)
			return;

		//do gravity
		if (!grounded)
			vel += GRAVITY;

		//so that intersecting multiple interactive tiles doesn't multiply their effect
		boolean shouldGrow = false, shouldShrink = false, boing = false;

		//handle interaction with interactive tiles in the level
		//only loop through tiles near the entity, for efficiency
		Rectangle hb = getHitbox();
		int minx = Math.max(0, PApplet.floor(hb.x));
		int maxx = Math.min(game.level.width, PApplet.ceil(hb.right()));
		int miny = Math.max(0, PApplet.floor(hb.y));
		int maxy = Math.min(game.level.height - 1, PApplet.ceil(hb.bottom()));
		for (int y = miny; y <= maxy; ++y) {
			for (int x = minx; x < maxx; ++x) {
				Tile tile = game.level.tileAt(x, y);
				if (tile instanceof Spring && hb.intersects(tile.getHitbox())) {
					boing = true;
				} else if (tile instanceof SizePad && hb.intersects(tile.getHitbox().move(0, -0.5f))) {
					if (tile instanceof BluePad)
						shouldGrow = true;
					else
						shouldShrink = true;
				}
			}
		}

		if (boing && grounded) {
			vel = -1.0f/PApplet.sqrt(PApplet.max(size, 0.25f));
			grounded = false;
		}

		if (shouldShrink && !shouldGrow)
			shrink();
		if (shouldGrow && !shouldShrink)
			grow();

		offset.addEquals(0, vel);
		debugVel = offset.mul(2);
		moveWithCollision(offset);

		if (size <= 0.01f) {
			dead = true;
			deathFrame = 0;
		}

		//TODO: add death condition for if player gets outside of level

		//if the ground sensor is colliding and vertical velocity drops to zero, then mark the entity as grounded
		//and unmark as soon as the ground sensor is no longer colliding with anything
		if (grounded)
			grounded = onGround();
		else if (vel > -0.001 && onGround())
			grounded = true;
	}

	private static final float GROWTH = 0.015f;
	private static final float EPSILON = 0.000002f;

	protected void grow() {
		//try to grow entity from the bottom center of their hitbox, if possible
		//otherwise, try growing from the bottom left or bottom right
		if (growImpl(GROWTH/2)) {
			pos.subEquals(GROWTH/2, GROWTH + EPSILON);
			size += GROWTH;
		} else if (growImpl(GROWTH)) {
			pos.subEquals(GROWTH, GROWTH + EPSILON);
			size += GROWTH;
		} else if (growImpl(0)) {
			pos.subEquals(0, GROWTH + EPSILON);
			size += GROWTH;
		}
	}

	protected boolean growImpl(float xcomp) {
		return findIntersection(getHitbox().move(-xcomp, -GROWTH - EPSILON).grow(GROWTH, GROWTH)) == null;
	}

	protected void shrink() {
		if (size > 0.5f) {
			pos.addEquals(GROWTH/2, GROWTH - EPSILON);
			size -= GROWTH;
		} else {
			pos.addEquals(GROWTH/4, GROWTH/2 - EPSILON);
			size -= GROWTH/2;
		}
	}

	//XXX: code smell: using class variables for functionality of method internals
	protected boolean verticalSlide, grounded;

	//TODO: contingency plan for if the entity does somehow get stuck inside of a tile they can't be ejected out of
	//I assume players will mostly prefer an apparent glitch over the game freezing seemingly for no reason
	//we can do this by keeping a list of tiles we've ejected from, and breaking out if we hit a repeat
	//or simply limiting the number of ejections allowed to some reasonable upper bound, like 100
	protected void moveWithCollision(Vector2 offset) {
		Vector2 projected = pos.add(offset); //projected position of entity after applying offset (used for wall sliding)

		moveWithCollisionImpl(offset, offset.y/offset.x);

		//if we haven't collided, we obviously don't need to slide, so we might as well exit early
		if (pos.equals(projected))
			return;

		//slide along whatever wall we were last ejected from
		offset = projected.sub(pos);
		offset = verticalSlide? new Vector2(0, offset.y) : new Vector2(offset.x, 0);

		moveWithCollisionImpl(offset, offset.y/offset.x);
	}

	/** find and resolve all collisions for a given offset */
	private Vector2 moveWithCollisionImpl(Vector2 offset, float slope) {
		Rectangle collision = findIntersection(getHitbox().move(offset));
		//handle collisions until the entity is free from all tiles
		while (collision != null) {
			offset = eject(collision, offset, slope);
			collision = findIntersection(getHitbox().move(offset));
		}

		pos.addEquals(offset);
		return offset;
	}

	protected Rectangle findIntersection(Rectangle hb) {
		//check for collision with the level borders as well as with tiles within the level
		if (hb.intersects(game.level.top))
			return game.level.top;
		if (hb.intersects(game.level.bottom))
			return game.level.bottom;
		if (hb.intersects(game.level.left))
			return game.level.left;
		if (hb.intersects(game.level.right))
			return game.level.right;

		//only loop through tiles near the entity, for efficiency
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

		//check collision with entities, which could be anywhere
		for (Entity entity : game.level.entities)
			if (entity != this && hb.intersects(entity.getHitbox()))
				return entity.getHitbox();

		return null;
	}

	//find the shortest path to backtrack that gets the entity to where they're not colliding
	private Vector2 eject(Rectangle collision, Vector2 offset, float slope) {
		//XXX: do we need to protect against NaNs?
		if (offset.x == 0.0f && offset.y == 0.0f)
			ICECUBE.println("NaN IN THE DUNGEON! THERE'S A NaN IN THE DUNGEON! Just thought you ought to know..."); //$NON-NLS-1$

		//the projected position after offset, to test for intersections
		Rectangle hb = getHitbox().move(offset);

		//the minimum distance straight along x or y axis the entity must be ejected to exit collision
		float dx = offset.x > 0? hb.right()  - collision.x : hb.x - collision.right();
		float dy = offset.y > 0? hb.bottom() - collision.y : hb.y - collision.bottom();

		//trace backward along the entity's path using each of the supplied ejection distances, and compare their length
		Vector2 ex = new Vector2(dx, dx*slope);
		Vector2 ey = new Vector2(dy/slope, dy); //division by 0 should not be an issue since we get infinity, and that gets discarded at the next step

		//we know that the shorter of the two ejection paths must be correct, so pick that one to work with
		Vector2 ejection = ex.mag() < ey.mag()? ex : ey;
		//whichever path was shorter should also tell us what direction (vertical or horizontal) the entity should slide along the wall
		verticalSlide = ex.mag() < ey.mag();

		if (!verticalSlide)
			vel = 0;

		//handle Entities pushing Entities pushing Entities pushing Entities pushing...
		if (collision.owner != null) {
			if (verticalSlide) {
				collision.owner.moveWithCollision(new Vector2(offset.x/2, 0));
				collision = collision.owner.getHitbox(); //update for the box's new position after being pushed

				//adjust ejection for the new position of the box after being pushed
				//the vertical slide direction can't change in this case, but the ejection distance can change
				if (hb.intersects(collision)) {
					dx = offset.x > 0? hb.right()  - collision.x : hb.x - collision.right();
					ejection = new Vector2(dx, dx*slope);
				} else {
					//if we're not even colliding anymore, then hey, just ignore it
					return offset;
				}
			} else {
				//TODO: vertical push code
			}
		}

		return offset.subEquals(ejection);
	}

	private Vector2 debugVel = new Vector2();

	public void draw() {
		game.fill(color);
		game.rect(pos.x, pos.y, size, size);

		//TODO: extract to generic animations system
		if (dead) {
			//cosine is used here to provide a smooth easing for the animation, not for any trigonometric purpose
			float start = PApplet.cos(PApplet.constrain(deathFrame/24.0f       , 0.0f, 1.0f)*PApplet.PI)*0.5f - 0.5f; //XXX: magic framerate-dependent constant
			float end   = PApplet.cos(PApplet.constrain(deathFrame/24.0f - 0.5f, 0.0f, 1.0f)*PApplet.PI)*0.5f - 0.5f; //XXX: magic framerate-dependent constant

			//setup style for line-drawing
			game.stroke(color);
			game.strokeWeight(0.25f);
			game.strokeCap(PApplet.SQUARE);

			//use <s>the force</s> trigonometry to draw the line radially 6 times
			for (int i = 0; i < 6; ++i) {
				float sx = PApplet.sin(PApplet.PI*i/3.0f) * start;
				float sy = PApplet.cos(PApplet.PI*i/3.0f) * start;
				float ex = PApplet.sin(PApplet.PI*i/3.0f) * end;
				float ey = PApplet.cos(PApplet.PI*i/3.0f) * end;

				game.line(pos.x + sx, pos.y + sy, pos.x + ex, pos.y + ey);
			}

			//reset style for rect-drawing
			game.noStroke();

			++deathFrame;
		}

		if (game.debug) {
			game.drawDebugHitbox(new Rectangle(pos.x, pos.y + size - 0.04f, size, 0.05f), onGround());

			game.textFont(game.debugFont);
			game.textSize(0.4999f); //must be below 0.5 (see Processing GitHub issue #4548)
			game.textAlign(PConstants.CENTER, PConstants.CENTER);
			game.fill(0xFFFFFFFF); //white
			game.text(pos.toString(), pos.x + size/2, pos.y - 0.5f);
			game.text(String.format("%.6f", size), pos.x + size/2, pos.y - 1); //$NON-NLS-1$

			//TODO: give this its own layer, like with debug hitboxes
			game.stroke(0xFFFF00FF); //magenta
			game.strokeWeight(0.25f);
			game.line(pos.x + size/2, pos.y + size/2, pos.x + size/2 + debugVel.x, pos.y + size/2 + debugVel.y);
			game.noStroke();
		}
	}
}
