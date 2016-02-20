package net.kopeph.icecube.menu;

public class Button extends Widget {
	private final String text;
	private final float x, y, w, h;

	public Button(String text, int x, int y, int w, int h) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	@Override
	public void draw() {
		game.fill(0);
		game.rect(x, y, w, h);
	}
}
