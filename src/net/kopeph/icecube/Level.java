package net.kopeph.icecube;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

import net.kopeph.icecube.entity.Entity;
import net.kopeph.icecube.tile.*;
import net.kopeph.icecube.util.Rectangle;

public final class Level {
	private final ICECUBE game = ICECUBE.game;
	PGraphics canvas;

	public final int width, height;
	public final Tile[] tiles;
	private final List<Entity> originalBoxes = new ArrayList<>();
	public List<Entity> entities;

	//rectangles to simulate collision with the borders of the level
	public final Rectangle top;
	public final Rectangle bottom;
	public final Rectangle left;
	public final Rectangle right;

	public Level(String levelName) {
		PImage img = game.loadImage("level/" + levelName + ".png"); //$NON-NLS-1$ //$NON-NLS-2$
		Map<Point, String> meta = parseMeta(game.loadStrings("level/" + levelName + ".txt")); //$NON-NLS-1$ //$NON-NLS-2$

		width = img.width;
		height = img.height;

		tiles = new Tile[img.pixels.length];
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				int i = y*width + x;
				switch (img.pixels[i]) {
					case 0xFF000000:
						tiles[i] = new WallTile(x, y);
						break;
					case 0xFFFF0000:
						tiles[i] = new RedPad(x, y);
						break;
					case 0xFF0000FF:
						tiles[i] = new BluePad(x, y);
						break;
					case 0xFF00FF00:
						tiles[i] = new GoalTile(x, y, meta.get(new Point(x, y)));
						break;
					case 0xFF808080:
						tiles[i] = new TopHalfWall(x, y);
						break;
					case 0xFFFFFF00:
						tiles[i] = new Door(x, y, meta.get(new Point(x, y)));
						break;
					case 0xFFFF8000:
						tiles[i] = new Spring(x, y);
						break;
					case 0xFF00FFFF:
						Entity entity = new Entity(x, y, Float.parseFloat(meta.get(new Point(x, y))), 0, 0xFFDDDDEE); //off-white
						entity.moveTo(x + 0.5f, y + 0.5f);
						originalBoxes.add(entity);
						break;
					case 0xFFFF00FF:
						game.player.moveTo(x + 0.5f, y + 0.5f);
						break;
				}
			}
		}

		//initialize level borders
		top = new Rectangle(-width, -height, width*3, height);
		bottom = new Rectangle(-width, height, width*3, height);
		left = new Rectangle(-width, 0, width, height);
		right = new Rectangle(width, 0, width, height);

		reset();
	}

	public void reset() {
		entities = new ArrayList<>();
		for (Entity entity : originalBoxes)
			entities.add(new Entity(entity));
		entities.add(game.player);
	}

	/** parses a newline-separated list of door-to-level mappings in the format "x,y:name", ignoring all whitespace */
	private Map<Point, String> parseMeta(String[] lines) {
		Map<Point, String> doors = new HashMap<>();

		for (String line : lines) {
			if (line.trim().equals("")) //$NON-NLS-1$
				break;
			String[] pair = line.split(":"); //$NON-NLS-1$
			String[] coords = pair[0].split(","); //$NON-NLS-1$
			doors.put(new Point(Integer.parseInt(coords[0].trim()),
			                    Integer.parseInt(coords[1].trim())),
			          pair[1].trim());
		}

		return doors;
	}

	public Tile tileAt(int x, int y) {
		//four-directional bounds checking is necessary so we don't wrap around
		//otherwise I would precompute y*width + x and just bounds-check the array
		if (x < 0 || y < 0 || x >= width || y >= height)
			return null;
		return tiles[y*width + x];
	}

	/** @return true iff the given hitbox intersects with any entity */
	public boolean occupied(Rectangle hb) {
		for (Entity entity : entities)
			if (hb.intersects(entity.getHitbox()))
				return true;
		return false;
	}

	public void draw() {
		game.fill(0xFFAAAAAA); //a neutral grey
		game.rect(0, 0, width, height);

		float vw = game.width/Tile.TILE_SIZE/2; //half viewport width in world coordinates
		float vh = game.height/Tile.TILE_SIZE/2; //half viewport height in world coordinates
		int minx = Math.max(0, PApplet.floor(game.origin.x - vw));
		int maxx = Math.min(width, PApplet.ceil(game.origin.x + vw));
		int miny = Math.max(0, PApplet.floor(game.origin.y - vh));
		int maxy = Math.min(height, PApplet.ceil(game.origin.y + vh));

		for (int y = miny; y < maxy; ++y) {
			for (int x = minx; x < maxx; ++x) {
				Tile tile = tileAt(x, y);
				if (tile != null)
					tile.draw();
			}
		}

		for (Entity entity : entities)
			entity.draw();

		//fucking thank god for functional programming making my life easier
		entities.removeIf(e -> e.deathFrame > 36);
	}
}
