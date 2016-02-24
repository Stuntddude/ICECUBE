package net.kopeph.icecube.menu;

import net.kopeph.icecube.ICECUBE;

public abstract class Widget {
	protected final ICECUBE game = ICECUBE.game;

	public abstract void draw(boolean selected);
}
