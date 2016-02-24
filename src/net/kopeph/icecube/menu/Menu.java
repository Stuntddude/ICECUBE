package net.kopeph.icecube.menu;

import java.util.ArrayList;
import java.util.List;

import net.kopeph.icecube.ICECUBE;

public class Menu {
	protected final ICECUBE game = ICECUBE.game;

	protected final List<Widget> widgets = new ArrayList<>();
	private int selection = 0;

	/** @param dir -1 to move selection up one element, 1 to move it down */
	public void spinSelection(int dir) {
		//wrap the selection to be within the bounds of the array
		selection = Math.floorMod(selection + dir, widgets.size());
	}

	public void draw() {
		game.background(255);
		for (int i = 0; i < widgets.size(); ++i)
			widgets.get(i).draw(i == selection);
	}
}
