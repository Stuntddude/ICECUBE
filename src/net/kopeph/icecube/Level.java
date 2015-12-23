package net.kopeph.icecube;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import net.kopeph.icecube.tile.*;
import net.kopeph.icecube.util.Rectangle;
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
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
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

	//draws all the non-animated tiles onto a static canvas which is then drawn onto the screen
	//this way we are drawing all w*h tiles only once per level load, rather than every frame
	public void bake() {
		canvas = context.createGraphics(Math.round(width*Tile.TILE_SIZE), Math.round(height*Tile.TILE_SIZE));

		canvas.beginDraw();
		canvas.noStroke();
		canvas.background(0xFFAAAAAA); //a neutral grey

		for (Tile tile : tiles)
			if (tile != null)
				tile.draw(canvas);

		canvas.endDraw();

		//context.println("I'm so baked right now!"); //debug
	}

	private float lastTileSize = 0; //so we know when to re-bake

	public void draw() {
		if (Tile.TILE_SIZE != lastTileSize)
			bake();
		lastTileSize = Tile.TILE_SIZE;

		context.image(canvas, -context.origin.x, -context.origin.y);
	}
}
