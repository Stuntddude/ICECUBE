package net.kopeph.icecube;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import processing.core.PApplet;
import processing.core.PFont;
import processing.event.MouseEvent;

import net.kopeph.icecube.entity.Box;
import net.kopeph.icecube.entity.Player;
import net.kopeph.icecube.menu.LanguageMenu;
import net.kopeph.icecube.menu.MainMenu;
import net.kopeph.icecube.menu.Menu;
import net.kopeph.icecube.menu.SettingsMenu;
import net.kopeph.icecube.tile.Tile;
import net.kopeph.icecube.util.Rectangle;
import net.kopeph.icecube.util.Vector2;

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

	public static final String
		PREFERENCES_NODE    = "settings", //$NON-NLS-1$
		KEY_LEVEL_NAME      = "level", //$NON-NLS-1$
		KEY_PLAYER_INFO     = "player", //$NON-NLS-1$
		KEY_COLORBLIND_MODE = "colorblind", //$NON-NLS-1$
		KEY_INVALID         = "/null/"; //$NON-NLS-1$

	public final Preferences diskStore = Preferences.userNodeForPackage(getClass()).node(PREFERENCES_NODE);

	public static final int
		ST_GAME = 0,
		ST_MENU = 1;

	public int gameState = ST_MENU;

	public boolean colorBlindMode = diskStore.getBoolean(KEY_COLORBLIND_MODE, false);
	public PFont menuFont;
	public PFont debugFont;
	public boolean debug;

	public Level level;
	public Player player;
	private Player backup; //used to neatly reset the player's position and size if they die in a level
	public Menu mainMenu, settingsMenu, languageMenu, currentMenu;

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

		menuFont = createFont("Montserrat-Bold.ttf", 72); //$NON-NLS-1$
		debugFont = createFont("DroidSansMono.ttf", 32); //$NON-NLS-1$
		mainMenu = new MainMenu();
		settingsMenu = new SettingsMenu();
		languageMenu = new LanguageMenu();
		currentMenu = mainMenu;
	}

	public String levelName;

	public void loadGame() {
		String name = diskStore.get(KEY_LEVEL_NAME , KEY_INVALID);
		String info = diskStore.get(KEY_PLAYER_INFO, KEY_INVALID);

		//if the information is invalid, start a new game
		if (name.equals(KEY_INVALID) || info.equals(KEY_INVALID)) {
			newGame();
		} else {
			String[] infos = info.split(" "); //$NON-NLS-1$
			player = new Player(Float.parseFloat(infos[0]),
			                    Float.parseFloat(infos[1]));
			changeLevel(name);
			gameState = ST_GAME;
		}
	}

	public void newGame() {
		player = new Player(1, 0);
		changeLevel("old/menu"); //$NON-NLS-1$
		gameState = ST_GAME;
	}

	public void changeLevel(String name) {
		level = new Level(name);
		levelName = name;
		backup = new Player(player);

		//auto-save the game at the beginning of each level
		diskStore.put(KEY_LEVEL_NAME, levelName);
		diskStore.put(KEY_PLAYER_INFO, player.toString());
	}

	public void resetLevel() {
		player = new Player(backup);
		level.reset();
	}

	private static final float MAX_FOLLOW_DISTANCE = 30.0f; //specified in pixels
	public Vector2 origin = new Vector2(0, 0); //the top left corner of the screen in pixel coordinates (NOT world coordinates!)

	@Override
	public void draw() {
		if (gameState == ST_GAME)
			drawGame();
		else if (gameState == ST_MENU)
			currentMenu.draw();
	}

	public void drawGame() {
		//player movement
		player.move(Input.handler.isDown(Input.LEFT),
		            Input.handler.isDown(Input.RIGHT),
		            Input.handler.isDown(Input.UP),
		            Input.handler.isDown(Input.DOWN),
		            Input.handler.isDown(Input.JUMP));

		//physics tick
		for (Box box : level.boxes)
			box.tick(new Vector2());

		//update follow cam origin
		Vector2 playerCenter = player.getHitbox().center();
		float distance = origin.distanceTo(playerCenter);
		float maxFollowDistance = MAX_FOLLOW_DISTANCE/Tile.TILE_SIZE; //convert follow cam radius to world coordinates
		if (distance > maxFollowDistance)
			origin.subEquals(Vector2.polar(distance - maxFollowDistance, origin.thetaTo(playerCenter)).mulEquals(0.1f));

		//apply worldspace transform
		pushMatrix();
		translate(width/2, height/2);
		scale(Tile.TILE_SIZE, Tile.TILE_SIZE);
		translate(-origin.x, -origin.y);

		background(0xFF000000); //everything around me is black, the color of my soul
		level.draw();
		player.draw();

		if (debug) {
			for (Map.Entry<Rectangle, Boolean> entry : debugHitboxes.entrySet()) {
				fill(entry.getValue()? 0xAAFFAA22 : 0xAA22AAFF); //transparent light blue : transparent light red
				Rectangle hb = entry.getKey();
				rect(hb.x, hb.y, hb.w, hb.h);
			}

			//reset the map for the next frame
			debugHitboxes = new HashMap<>();
		}

		//reset transfrom
		popMatrix();
	}

	//the reason to store these as a Map is because a Pair class does not natively exist in Java
	//and I didn't want to pollute the codebase by writing my own
	private Map<Rectangle, Boolean> debugHitboxes = new HashMap<>();

	/** queues the given hitbox to be drawn after everything else */
	public void drawDebugHitbox(Rectangle hb, boolean active) {
		debugHitboxes.put(hb, active);
	}

	@Override
	public void keyPressed() {
		Input.handler.handleKey(keyCode, true);
		key = 0; //stop ESC from always closing the program
	}

	@Override
	public void keyReleased() {
		Input.handler.handleKey(keyCode, false);
	}

	public void keyChanged(int control, boolean down) {
		if (down) {
			if (gameState == ST_MENU) {
				if (control == Input.UP)
					currentMenu.spinSelection(-1);
				else if (control == Input.DOWN)
					currentMenu.spinSelection(1);
				else if (control == Input.SELECT)
					currentMenu.interact();
				else if (control == Input.ESC && currentMenu != mainMenu)
					currentMenu = mainMenu;
				else if (control == Input.ESC)
					exit();
			} else {
				if (control == Input.DEBUG)
					debug = !debug;
				else if (control == Input.RESET)
					resetLevel();
				else if (control == Input.ESC) {
					gameState = ST_MENU;
					mainMenu.selection = 0;
				}
			}
		} else {
			//key release callbacks go here
		}
	}

	@Override
	public void mouseWheel(MouseEvent e) {
		//scale by 2^(1/n) where n is the number of scale increments between each power-of-two zoom level
		Tile.TILE_SIZE *= pow(pow(2.0f, 0.25f), -e.getCount());
	}

	public static void main(String[] args) {
		PApplet.runSketch(new String[] { ICECUBE.class.getName() }, game);
	}
}
