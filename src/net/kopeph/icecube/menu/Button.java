package net.kopeph.icecube.menu;

import processing.core.PConstants;

public class Button extends Widget {
	private final String text;
	private float x, y, w, h;

	public Button(String text, float x, float y, float w, float h) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	@Override
	public void draw() {
		game.textAlign(PConstants.CENTER, PConstants.CENTER);
		game.fill(0);
		game.rect(x, y, w, h);
		game.fill(255);
		game.text(text, x, y - 8, w, h);
	}
}
