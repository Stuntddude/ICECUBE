package net.kopeph.icecube.menu;

import processing.core.PConstants;

public class Button extends Widget {
	private final String text;
	private float y, h;

	public Button(String text, float yOffset, float height) {
		this.text = text;
		this.y = yOffset;
		this.h = height;
	}

	@Override
	public void draw() {
		game.fill(0);
		game.rect(game.width*0.1f, game.height/2 + y - h/2, game.width*0.8f, h);

		game.textAlign(PConstants.CENTER, PConstants.CENTER);
		game.fill(255);
		game.text(text, game.width/2, game.height/2 + y - 12);
	}
}
