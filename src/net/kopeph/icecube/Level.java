package net.kopeph.icecube;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import net.kopeph.icecube.tile.*;
import net.kopeph.icecube.util.Rectangle;
import net.kopeph.icecube.util.Vector2;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public final class Level {
	private final ICECUBE context = ICECUBE.getContext();
	PGraphics canvas;

	public final int width, height;
	public final Tile[] tiles;

	//rectangles to simulate collision with the borders of the level
	public final Rectangle top;
	public final Rectangle bottom;
	public final Rectangle left;
	public final Rectangle right;

	public Level(String levelName) {
		PImage img = context.loadImage("res/" + levelName + ".png");
		Map<Point, String> doors = parseMeta(context.loadStrings("res/" + levelName + ".txt"));

		width = img.width;
		height = img.height;

		tiles = new Tile[img.pixels.length];
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				int i = y*width + x;
				switch (img.pixels[i]) {
					//XXX: wet code smell
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
						tiles[i] = new GoalTile(x, y, doors.get(new Point(x, y)));
						break;
					case 0xFF808080:
						tiles[i] = new TopHalfWall(x, y);
						break;
					case 0xFFFFFF00:
						tiles[i] = new Door(x, y, doors.get(new Point(x, y)));
						break;
					case 0xFFFF8000:
						tiles[i] = new Spring(x, y);
						break;
					case 0xFFFF00FF:
						//XXX: player may end up inside a wall if there is no magenta pixel in level
						context.player.moveTo(x + 0.5f, y + 0.5f);
						break;
				}
			}
		}

		//initialize level borders
		top = new Rectangle(-width, -height, width*3, height);
		bottom = new Rectangle(-width, height, width*3, height);
		left = new Rectangle(-width, 0, width, height);
		right = new Rectangle(width, 0, width, height);

		//TODO: better level validation maybe?
	}

	/** parses a newline-separated list of door-to-level mappings in the format "x,y:name", ignoring all whitespace */
	private Map<Point, String> parseMeta(String[] lines) {
		Map<Point, String> doors = new HashMap<>();

		for (String line : lines) {
			if (line.trim().equals(""))
				break;
			String[] pair = line.split(":");
			String[] coords = pair[0].split(",");
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

	public void draw() {
		context.fill(0xFFAAAAAA); //a neutral grey
		context.rect(-context.origin.x, -context.origin.y, width*Tile.TILE_SIZE, height*Tile.TILE_SIZE);

		//TODO: consider a loop to only draw the tiles that are currently on-screen
		//the above will be the final step in making off-screen tiles basically free in performance terms
		Vector2 worldOrigin = context.origin.mul(1.0f/Tile.TILE_SIZE);
		int minx = Math.max(0, PApplet.floor(worldOrigin.x));
		int maxx = Math.min(width, PApplet.ceil(worldOrigin.x + context.width/Tile.TILE_SIZE));
		int miny = Math.max(0, PApplet.floor(worldOrigin.y));
		int maxy = Math.min(height, PApplet.ceil(worldOrigin.y + context.height/Tile.TILE_SIZE));

		for (int y = miny; y < maxy; ++y) {
			for (int x = minx; x < maxx; ++x) {
				Tile tile = tileAt(x, y);
				if (tile != null)
					tile.draw();
			}
		}
	}
}
