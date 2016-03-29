package net.kopeph.icecube.menu;

import net.kopeph.icecube.ICECUBE;

public final class MainMenu extends Menu {
	private final Button play, newGame, settings, language;

	public MainMenu() {
		widgets.add(play     = new Button("Play",     -150, () -> { game.gameState = ICECUBE.ST_GAME; }));
		widgets.add(newGame  = new Button("New Game",  -50, () -> { game.gameState = ICECUBE.ST_GAME; }));
		widgets.add(settings = new Button("Settings",   50, () -> { game.currentMenu = game.settingsMenu; }));
		widgets.add(language = new Button("Language",  150, () -> { game.currentMenu = game.languageMenu; }));
	}
}
