package net.kopeph.icecube.menu;

import processing.core.PConstants;

public class Button extends Widget {
	private final String text;
	private final float y;
	public final Runnable behavior;

	public Button(String text, float yOffset, Runnable behavior) {
		this.text = text;
		this.y = yOffset;
		this.behavior = behavior;
	}

	@Override
	public void interact() {
		behavior.run();
	}

	@Override
	public void draw(boolean selected) {
		game.textAlign(PConstants.CENTER, PConstants.CENTER);
		game.fill(selected? 0xFFAAAAAA : 0xFF000000);
		game.text(text, game.width/2, game.height/2 + y - 12);
	}
}
