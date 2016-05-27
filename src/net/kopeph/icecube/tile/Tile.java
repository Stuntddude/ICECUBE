package net.kopeph.icecube.tile;

import processing.core.PConstants;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import net.kopeph.icecube.ICECUBE;
import net.kopeph.icecube.util.Rectangle;
import net.kopeph.icecube.util.Vector2;

/** The common superclass of all tiles in the game. */
public abstract class Tile {
	public static float TILE_SIZE = 24;

	protected static final ICECUBE game = ICECUBE.game;

	public final Body body;

	public final Vector2 pos;
	public final int color;

	public Tile(Vector2 pos, int color) {
		this.pos = pos;
		this.color = color;

		//define physics body
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.STATIC;
		bodyDef.position.set(pos.x, pos.y);

		//create collision fixture
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(.5f, .5f);

		FixtureDef fixture = new FixtureDef();
		fixture.shape = shape;

		//make!
		body = ICECUBE.world.createBody(bodyDef);
		body.createFixture(fixture);
	}

	public Tile(float x, float y, int color) {
		this(new Vector2(x, y), color);
	}

	public boolean hasCollision() {
		return false;
	}

	/** @return an AABB in world-space representing the hitbox of this tile */
	public Rectangle getHitbox() {
		return new Rectangle(pos.x, pos.y, 1, 1);
	}

	//this should be overridden for anything planning to be non-square!
	//like ramps and buttons and shit
	public void draw() {
		game.fill(color);
		game.rectMode(PConstants.CENTER);
		game.rect(pos.x*TILE_SIZE - game.origin.x, pos.y*TILE_SIZE - game.origin.y, TILE_SIZE, TILE_SIZE);
	}
}
