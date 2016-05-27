package net.kopeph.icecube.entity;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;

import net.kopeph.icecube.ICECUBE;
import net.kopeph.icecube.util.Vector2;

public final class Player extends Entity {
	private static final int color = 0xFFFFFFFF; //white

	private boolean dead = false;
	private int deathFrame = 0; //used to drive the death animation; incremented every frame upon death

	private int sensorCollisions;

	private Fixture sensor;

	public Player(Player other) {
		super(other);

		initSensor();
	}

	public Player(float s, float v) {
		super(new Vector2(0, 0), s, v, color);

		initSensor();
	}

	private void initSensor() {
		//create a sensor fixture at the bottom of the player
		//used to detect whether the player is currently grounded, for sick jumpz
		PolygonShape sensorShape = new PolygonShape();
		sensorShape.setAsBox(size/2.1f, 0.1f, new Vec2(0, size/2), 0);

		FixtureDef sensorFixture = new FixtureDef();
		sensorFixture.shape = sensorShape;
		sensorFixture.isSensor = true;

		sensor = body.createFixture(sensorFixture);

		//collision handler for sensor
		ICECUBE.world.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				if (contact.getFixtureA().isSensor() || contact.getFixtureB().isSensor()) {
					ICECUBE.game.player.sensorCollisions++;
					System.out.println("++" + ICECUBE.game.player.sensorCollisions);
				}
			}

			@Override
			public void endContact(Contact contact) {
				if (contact.getFixtureA().isSensor() || contact.getFixtureB().isSensor()) {
					ICECUBE.game.player.sensorCollisions--;
					System.out.println("--" + ICECUBE.game.player.sensorCollisions);
				}
			}

			@Override
			public void preSolve(Contact contact, Manifold manifold) {
				//no-op
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				//no-op
			}
		});
	}

	public void moveTo(float x, float y) {
		body.setTransform(new Vec2(x, y), 0);
	}

	private static final float SP = 0.15f*60;

	public void move(boolean left, boolean right, boolean up, boolean down, boolean space) {
		float velocity = body.getLinearVelocity().x;
//		if (left && !right)
//			body.applyLinearImpulse(new Vec2(-SP - velocity, 0), body.getPosition());
//		else if (right && !left)
//			body.applyLinearImpulse(new Vec2(SP - velocity, 0), body.getPosition());
//		else
//			body.applyLinearImpulse(new Vec2(-velocity, 0), body.getPosition());

		if (left && !right)
			body.setLinearVelocity(new Vec2(-SP, body.getLinearVelocity().y));
		else if (right && !left)
			body.setLinearVelocity(new Vec2(SP, body.getLinearVelocity().y));
		else
			body.setLinearVelocity(new Vec2(0, body.getLinearVelocity().y));

		if (up)
			grow();
		else if (down)
			shrink();

		//replace sensor
		//TODO: make this contingent on shrinkage or (successful) growth
		body.destroyFixture(sensor);
		initSensor();

		float jumpStrength = 0.23f + 0.11f*size;

		if (space && sensorCollisions > 1) //XXX: investigate this!
			body.setLinearVelocity(new Vec2(body.getLinearVelocity().x, -jumpStrength*60));
//			body.applyLinearImpulse(new Vec2(0, -jumpStrength*60), body.getPosition());

//		System.out.println(sensorCollisions);

		super.tick(new Vector2());

		//TODO: re-enable all this stuff

//		//closes the game after the last level
//		if (dead) {
//			if (game.levelName.equals("end")) //$NON-NLS-1$
//				game.exit();
//			return;
//		}
//
//		Vector2 offset = new Vector2(0, 0);
//		if (left)  offset.addEquals(-SP, 0);
//		if (right) offset.addEquals( SP, 0);
//		//I'm adding small y-offset to the movement so the player doesn't get stuck on the ground
//		//this is DUCT TAPE! once the jam is over, the actual problem needs to be diagnosed and addressed
//		if ((left || right) && onFloor) pos.addEquals(0, -0.00001f);
//
//		//debug growth
//		if (up)
//			grow();
//		else if (down)
//			shrink();
//
//		//my size gives me strength!
//		float jumpStrength = 0.23f + 0.11f*size;
//
//		if (space && onFloor && !verticalSlide)
//			vel = -jumpStrength; //jump!
//
//		super.tick(offset);
//
//		if (size <= 0.01f) {
//			dead = true;
//			deathFrame = 0;
//		}

		//TODO: add death condition for if player gets outside of level

		//PApplet.println("player: " + pos + "\tvelocity: " + vel + "\tsize: " + size + "\t" + offset); //DEBUG
	}
}
