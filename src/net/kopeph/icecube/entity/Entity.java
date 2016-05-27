package net.kopeph.icecube.entity;

import processing.core.PApplet;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.*;

import net.kopeph.icecube.ICECUBE;
import net.kopeph.icecube.tile.Tile;
import net.kopeph.icecube.util.Rectangle;
import net.kopeph.icecube.util.Vector2;

public class Entity {
	protected static final ICECUBE game = ICECUBE.game;

	public final Body body;
	public Fixture fixture, sensor;

	protected Vector2 pos;
	protected float size;
	protected float vel;

	private final int color;

	private boolean dead = false;
	private int deathFrame = 0; //used to drive the death animation; incremented every frame upon death

	public Entity(float x, float y, float s, float v, int c) {
		pos = new Vector2(x, y);
		size = s;
		vel = v;
		color = c;

		//define physics body
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.fixedRotation = true;
		bodyDef.position.set(x, y);
		bodyDef.userData = this;

		//create main fixture (a 1x1 square with the corners chopped off)
		PolygonShape shape = new PolygonShape();
		//XXX: actually just a square for now
		shape.setAsBox(size/2f, size/2f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 0.0f;
		fixtureDef.restitution = 0.0f;

		//make!
		body = ICECUBE.world.createBody(bodyDef);
		fixture = body.createFixture(fixtureDef);
	}

	public Entity(Vector2 p, float s, float v, int c) {
		this(p.x, p.y, s, v, c);
	}

	public Entity(Entity other) {
		this(other.pos.x, other.pos.y, other.size, other.vel, other.color);
	}

	@Override
	public String toString() {
		return size + " " + vel; //$NON-NLS-1$
	}

	public Rectangle getHitbox() {
		return new Rectangle(pos.x, pos.y, size - BREATHING_ROOM, size - BREATHING_ROOM);
	}

	protected static final float GRAVITY = 0.02f;

	public void tick(Vector2 offset) {
		//currently no-op
		pos = new Vector2(body.getPosition().x, body.getPosition().y);

//		//do gravity
//		vel += GRAVITY;
//
//		//so that intersecting multiple interactive tiles doesn't multiply their effect
//		boolean shouldGrow = false, shouldShrink = false, boing = false;
//
//		//handle interaction with interactive tiles in the level
//		//only loop through tiles near the player, for efficiency
//		Rectangle hb = getHitbox();
//		int minx = Math.max(0, PApplet.floor(hb.x));
//		int maxx = Math.min(game.level.width, PApplet.ceil(hb.right()));
//		int miny = Math.max(0, PApplet.floor(hb.y));
//		int maxy = Math.min(game.level.height - 1, PApplet.ceil(hb.bottom()));
//		for (int y = miny; y <= maxy; ++y) {
//			for (int x = minx; x < maxx; ++x) {
//				Tile tile = game.level.tileAt(x, y);
//				if (tile instanceof TransportTile) {
//					if (hb.intersects(tile.getHitbox())) {
//						game.changeLevel(((TransportTile)tile).level);
//						return;
//					}
//				} else if (tile instanceof SizePad) {
//					if (hb.intersects(tile.getHitbox().move(0, -0.5f))) {
//						if (tile instanceof BluePad) {
//							shouldGrow = true;
//							//XXX: MORE DUCT TAPE
//							pos.subEquals(0, BREATHING_ROOM);
//						} else {
//							shouldShrink = true;
//						}
//					}
//				} else if (tile instanceof Spring) {
//					if (hb.intersects(tile.getHitbox())) {
//						boing = true;
//					}
//				}
//			}
//		}
//
//		if (boing && onFloor)
//			vel = -0.6f/PApplet.max(size, 0.38f);
//
//		if (shouldShrink)
//			shrink();
//		if (shouldGrow)
//			grow();
//
//		offset.addEquals(0, vel);
//
//		float oldPosY = pos.y;
//		moveWithCollision(offset);
//		vel = pos.y - oldPosY;
	}

	private static final float GROWTH = 0.01f;

	protected void grow() {
		//TODO: find a way to limit growth in box2d
		size += GROWTH;

		//how many subroutines does it take to replace a light bulb?
		replaceFixture();

//		//try to grow player from the bottom center of their hitbox, if possible
//		//otherwise, try growing from the bottom left or bottom right
//		if (growImpl(GROWTH/2)) {
//			pos.subEquals(GROWTH/2, GROWTH);
//			size += GROWTH;
//		} else if (growImpl(GROWTH)) {
//			pos.subEquals(GROWTH, GROWTH);
//			size += GROWTH;
//		} else if (growImpl(0)) {
//			pos.subEquals(0, GROWTH);
//			size += GROWTH;
//		}
	}

	private void replaceFixture() {
		//de_stroy'd
		body.destroyFixture(fixture);

		//create a newer, larger, better-than-ever sqaure!
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(size/2f, size/2f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 0.0f;
		fixtureDef.restitution = 0.1f;

		fixture = body.createFixture(fixtureDef);
	}

	private boolean growImpl(float xcomp) {
		return findIntersection(getHitbox().move(-xcomp, -GROWTH).grow(GROWTH, GROWTH)) == null;
	}

	protected void shrink() {
		size -= GROWTH;

		replaceFixture();

//		if (size > 0.5f) {
//			pos.addEquals(GROWTH/2, GROWTH);
//			size -= GROWTH;
//		} else {
//			pos.addEquals(GROWTH/4, GROWTH/2);
//			size -= GROWTH/2;
//		}
	}

	//the factor by which to over-eject the entity to avoid potential floating point weirdness
	protected static final float EJECTION_EPSILON = 1.00001f;

	//the amount by which to shrink the player's hitbox, also to avoid floating point weirdness
	//this may not be necessary, since the player is supposed to be constantly changing size anyway
	//but I'm keeping it here for now because I don't think it hurts anything to have this safeguard
	protected static final float BREATHING_ROOM = 0.0001f; //should be greater than (EJECTION_EPSILON - 1.0)

	//XXX: code smell using class variables for functionality of method internals
	protected boolean hasCollided, onFloor, verticalSlide;

	//TODO: contingency plan for if the player does somehow get stuck inside of a tile they can't be ejected out of
	//I assume players will mostly prefer an apparent glitch over the game freezing seemingly for no reason
	//we can do this by keeping a list of tiles we've ejected from, and breaking out if we
	protected void moveWithCollision(Vector2 offset) {
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

	//TODO: make boxes find intersection with the player (override this method)
	protected Rectangle findIntersection(Rectangle hb) {
		//check for collision with the level borders as well as with tiles within the level
//		if (hb.intersects(game.level.top))
//			return game.level.top;
//		if (hb.intersects(game.level.bottom))
//			return game.level.bottom;
//		if (hb.intersects(game.level.left))
//			return game.level.left;
//		if (hb.intersects(game.level.right))
//			return game.level.right;

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

	private Vector2 eject(Rectangle collision, Vector2 offset) {
		//XXX: do we need to protect against NaNs?
		if (offset.x == 0.0f && offset.y == 0.0f)
			ICECUBE.println("NaN IN THE DUNGEON! THERE'S A NaN IN THE DUNGEON! Just thought you ought to know..."); //$NON-NLS-1$

		//the projected position after offset, to test for intersections
		Rectangle hb = getHitbox().move(offset);

		//find the shortest path to backtrack that gets the entity to where they're not colliding
		//the minimum distance straight along x or y axis the player must be ejected to exit collision
		float dx = offset.x > 0? hb.right()  - collision.x : hb.x - collision.right();
		float dy = offset.y > 0? hb.bottom() - collision.y : hb.y - collision.bottom();

		//XXX: consider changing this so that the slope is only calculated once before all ejections (may remove infinite loop behavior)
		float slope = offset.y/offset.x; //division by 0 should not be an issue since we get infinity, which plays nicely with the next step

		//trace backward along the entity's path using each of the supplied ejection distances, and compare their length
		Vector2 ex = new Vector2(dx, dx*slope);
		Vector2 ey = new Vector2(dy/slope, dy); //division by 0 should not be an issue since we get infinity, and that gets discarded at the next step

		//we know that the shorter of the two ejection paths must be correct, so pick that one to work with
		Vector2 ejection = ex.mag() < ey.mag()? ex : ey;
		//whichever path was shorter should also tell us what direction (vertical or horizontal) the entity should slide along the wall
		verticalSlide = ex.mag() < ey.mag();

		return offset.subEquals(ejection.mulEquals(EJECTION_EPSILON));
	}

	public void draw() {
		game.fill(color);
		game.rect(body.getPosition().x*Tile.TILE_SIZE - game.origin.x,
		          body.getPosition().y*Tile.TILE_SIZE - game.origin.y,
		          size*Tile.TILE_SIZE,
		          size*Tile.TILE_SIZE);

//		game.fill(color);
//		game.rect(pos.x*Tile.TILE_SIZE - game.origin.x, pos.y*Tile.TILE_SIZE - game.origin.y, size*Tile.TILE_SIZE, size*Tile.TILE_SIZE);
//
//		//TODO: extract to generic animations system
//		if (dead) {
//			//cosine is used here to provide a smooth easing for the animation, not for any trigonometric purpose
//			float start = PApplet.cos(PApplet.constrain(deathFrame/24.0f       , 0.0f, 1.0f)*PApplet.PI)*0.5f - 0.5f; //XXX: magic framerate-dependent constant
//			float end   = PApplet.cos(PApplet.constrain(deathFrame/24.0f - 0.5f, 0.0f, 1.0f)*PApplet.PI)*0.5f - 0.5f; //XXX: magic framerate-dependent constant
//
//			//setup style for line-drawing
//			game.stroke(0xFFFFFFFF); //white
//			game.strokeWeight(Tile.TILE_SIZE/8.0f); //XXX: magic constant
//			game.strokeCap(PApplet.SQUARE);
//
//			//use <s>the force</s> trigonometry to draw the line radially 6 times
//			for (int i = 0; i < 6; ++i) {
//				float sx = PApplet.sin(PApplet.PI*i/3.0f) * start;
//				float sy = PApplet.cos(PApplet.PI*i/3.0f) * start;
//				float ex = PApplet.sin(PApplet.PI*i/3.0f) * end;
//				float ey = PApplet.cos(PApplet.PI*i/3.0f) * end;
//
//				game.line((pos.x + sx)*Tile.TILE_SIZE - game.origin.x, (pos.y + sy)*Tile.TILE_SIZE - game.origin.y,
//				          (pos.x + ex)*Tile.TILE_SIZE - game.origin.x, (pos.y + ey)*Tile.TILE_SIZE - game.origin.y);
//			}
//
//			//reset style for rect-drawing
//			game.noStroke();
//
//			if (deathFrame > 36) //XXX: magic framerate-dependent constant
//				game.resetLevel();
//
//			++deathFrame;
//		}
	}
}
