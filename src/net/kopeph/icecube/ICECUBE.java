package net.kopeph.icecube;

import net.kopeph.icecube.entity.Player;
import net.kopeph.icecube.tile.Tile;
import net.kopeph.icecube.util.Vector2;
import processing.core.PApplet;
import processing.event.MouseEvent;

/**
 * A note about the game's coordinate system:
 * World coordinates are based on a 1:1 scale with tiles, i.e. every tile is 1x1 size in world coordinates
 * All game physics and logic is based on this scale.
 * When the game world is drawn to the screen it is drawn scaled up by a factor defined by Tile.TILE_SIZE
 * This way, we can effortlessly scale the game up and down for different levels, pixel densities, user preference, etc.
 * without needing to adjust the physics step, because world coordinates are in no way based on pixel scale
 */
public final class ICECUBE extends PApplet {
	public Level level;
	public Player player;
	private Player backup; //used to neatly reset the player's position and size if they die in a level

	private boolean left, right, up, down, space; //for player movement TODO: move elsewhere
	private static ICECUBE context;

	@Override
	public void setup() {
		context = this;

		size(1024, 768);
		frameRate(60);
		noStroke();
		//TODO: move the origin when the screen is resized, similar to how I do it for mousewheel zoom
		frame.setResizable(true);
		frame.setTitle("ICECUBE         CONTROLS: A-LEFT  D-RIGHT  SPACE-JUMP  R-RESET  MOUSEWHEEL-ZOOM");

		player = new Player(0, 0, 1, 1);
		changeLevel("menu");
	}

	public static ICECUBE getContext() {
		return context;
	}

	public String levelName;

	public void changeLevel(String name) {
		level = new Level(name);
		levelName = name;
		backup = player.clone();
	}

	public void resetLevel() {
		player = backup.clone();
	}

	private static final float MAX_FOLLOW_DISTANCE = 30.0f;
	public Vector2 originf = new Vector2(0, 0); //the top left corner of the screen in pixel coordinate (NOT world coordinates!)
	public Vector2 origin = new Vector2(0, 0); //the origin, rounded to int to avoid weird aliasing between tiles

	private void updateOrigin(Vector2 newOrigin) {
		originf = newOrigin;
		origin = new Vector2(originf.x, originf.y);
	}

	@Override
	public void draw() {
		//move player
		player.move(left, right, up, down, space);

		//update follow cam origin
		Vector2 screenCenter = originf.add(new Vector2(width/2, height/2));
		Vector2 playerCenter = player.toRect().center().mul(Tile.TILE_SIZE);
		float distance = playerCenter.sub(screenCenter).mag();
		if (distance > MAX_FOLLOW_DISTANCE)
			originf = originf.add(Vector2.polar(distance - MAX_FOLLOW_DISTANCE, playerCenter.thetaTo(screenCenter)).mul(0.1f));
		origin = new Vector2(Math.round(originf.x), Math.round(originf.y));

		//draw everything
		background(0xFF000000); //everything around me is black, the color of my soul
		level.draw();
		player.draw();
	}

	@Override
	public void keyPressed() {
		if (key == CODED) {
			switch(keyCode) {
				case LEFT:  left  = true; break;
				case RIGHT: right = true; break;
				case UP:    up    = true; break;
				case DOWN:  down  = true; break;
			}
		} else {
			switch(Character.toUpperCase(key)) {
				case 'A': left  = true; break;
				case 'D': right = true; break;
				case 'W': up    = true; break;
				case 'S': down  = true; break;
				case ' ': space = true; break;
				case 'R': resetLevel(); break;
			}
		}
	}

	@Override
	public void keyReleased() {
		//XXX: wet code smell
		if (key == CODED) {
			switch(keyCode) {
				case LEFT:  left  = false; break;
				case RIGHT: right = false; break;
				case UP:    up    = false; break;
				case DOWN:  down  = false; break;
			}
		} else {
			switch(Character.toUpperCase(key)) {
				case 'A': left  = false; break;
				case 'D': right = false; break;
				case 'W': up    = false; break;
				case 'S': down  = false; break;
				case ' ': space = false; break;
			}
		}
	}

	@Override
	public void mouseWheel(MouseEvent e) {
		//find the center before the scale change and convert to world coordinates
		Vector2 screenCenter = origin.add(new Vector2(width/2, height/2));
		Vector2 worldCenter = screenCenter.mul(1/Tile.TILE_SIZE);

		//scale the tile size
		Tile.REAL_TILE_SIZE *= pow(1.1f, -e.getCount());
		Tile.TILE_SIZE = Math.max(1, Math.round(Tile.REAL_TILE_SIZE));

		//use the world center from before and convert back into screen coords using the new tile size
		Vector2 newScreenCenter = worldCenter.mul(Tile.TILE_SIZE);
		Vector2 newScreenOrigin = newScreenCenter.sub(new Vector2(width/2, height/2));

		//update the real origin
		updateOrigin(newScreenOrigin);
	}

	public static void main(String[] args) {
		PApplet.main(ICECUBE.class.getName());
	}
}
