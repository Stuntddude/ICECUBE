package net.kopeph.icecube;

import net.kopeph.icecube.entity.Player;
import net.kopeph.icecube.menu.Menu;
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
	public static final ICECUBE game = new ICECUBE();

	public Level level;
	public Player player;
	private Player backup; //used to neatly reset the player's position and size if they die in a level

	private boolean left, right, up, down, space;
	private boolean w, a, s, d;

	private final Menu mainMenu = new Menu();

	private static final int
		ST_GAME = 0,
		ST_MENU = 1;

	private int gameState = ST_MENU;

	@Override
	public void settings() {
		size(1024, 768, P3D);
		smooth(8);
	}

	@Override
	public void setup() {
		frameRate(60);
		noStroke();
		surface.setResizable(true);
		surface.setTitle("ICECUBE         CONTROLS: A-LEFT  D-RIGHT  SPACE-JUMP  R-RESET  MOUSEWHEEL-ZOOM");

		player = new Player(0, 0, 1, 1);
		changeLevel("menu");
	}

	public String levelName;

	public void changeLevel(String name) {
		level = new Level(name);
		levelName = name;
		backup = new Player(player);
	}

	public void resetLevel() {
		player = new Player(backup);
	}

	private static final float MAX_FOLLOW_DISTANCE = 30.0f;
	public Vector2 origin = new Vector2(0, 0); //the top left corner of the screen in pixel coordinates (NOT world coordinates!)

	@Override
	public void draw() {
		switch(gameState) {
			case ST_GAME: drawGame(); break;
			case ST_MENU: drawMenu(); break;
		}
	}

	public void drawGame() {
		player.move(left || a, right || d, up || w, down || s, space);

		//update follow cam origin
		Vector2 screenCenter = origin.add(new Vector2(width/2, height/2));
		Vector2 playerCenter = player.getHitbox().center().mul(Tile.TILE_SIZE);
		float distance = playerCenter.sub(screenCenter).mag();
		if (distance > MAX_FOLLOW_DISTANCE)
			origin = origin.add(Vector2.polar(distance - MAX_FOLLOW_DISTANCE, playerCenter.thetaTo(screenCenter)).mul(0.1f));

		background(0xFF000000); //everything around me is black, the color of my soul
		level.draw();
		player.draw();
	}

	public void drawMenu() {
		mainMenu.draw();
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
				case 'A': a = true; break;
				case 'D': d = true; break;
				case 'W': w = true; break;
				case 'S': s = true; break;
				case ' ': space = true; break;
				case 'R': resetLevel(); break;
				//placeholder menu break code
				case ENTER: gameState = ST_GAME;
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
				case 'A': a = false; break;
				case 'D': d = false; break;
				case 'W': w = false; break;
				case 'S': s = false; break;
				case ' ': space = false; break;
			}
		}
	}

	@Override
	public void mouseWheel(MouseEvent e) {
		//find the center before the scale change and convert to world coordinates
		Vector2 screenCenter = origin.add(new Vector2(width/2, height/2));
		Vector2 worldCenter = screenCenter.mul(1/Tile.TILE_SIZE);

		//scale by 2^(1/n) where n is the number of scale increments between each power-of-two zoom level
		Tile.TILE_SIZE *= pow(pow(2.0f, 0.25f), -e.getCount());

		//use the world center from before and convert back into screen coords using the new tile size
		Vector2 newScreenCenter = worldCenter.mul(Tile.TILE_SIZE);
		Vector2 newScreenOrigin = newScreenCenter.sub(new Vector2(width/2, height/2));

		//update the real origin
		origin = newScreenOrigin;
	}

	public static void main(String[] args) {
		PApplet.runSketch(new String[] { ICECUBE.class.getName() }, game);
	}
}
