package net.kopeph.icecube.menu;

import net.kopeph.icecube.ICECUBE;

public class MainMenu extends Menu {
	private Button play, newGame, settings, language;

	public MainMenu() {
		widgets.add(play     = new Button("Play",     -150, () -> { game.gameState = ICECUBE.ST_GAME; }));
		widgets.add(newGame  = new Button("New Game",  -50, () -> { game.gameState = ICECUBE.ST_GAME; }));
		widgets.add(settings = new Button("Settings",   50, () -> { /* not implemented yet */ }));
		widgets.add(language = new Button("Language",  150, () -> { /* not implemented yet */ }));
	}
}
