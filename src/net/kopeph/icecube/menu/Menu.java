package net.kopeph.icecube.menu;

import java.util.ArrayList;
import java.util.List;

import net.kopeph.icecube.ICECUBE;

public class Menu {
	protected final ICECUBE game = ICECUBE.game;

	protected final List<Widget> widgets = new ArrayList<>();

	public void draw() {
		game.background(255);
		for (Widget widget : widgets)
			widget.draw();
	}
}
