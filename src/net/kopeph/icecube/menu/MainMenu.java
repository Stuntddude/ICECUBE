package net.kopeph.icecube.menu;

public final class MainMenu extends Menu {
	private final Button play, newGame, settings, language;

	public MainMenu() {
		widgets.add(play     = new Button("Play"    , -150, () -> { game.loadGame(); }));
		widgets.add(newGame  = new Button("New Game",  -50, () -> { game.newGame(); }));
		widgets.add(settings = new Button("Settings",   50, () -> { game.currentMenu = game.settingsMenu; }));
		widgets.add(language = new Button("Language",  150, () -> { game.currentMenu = game.languageMenu; }));
	}
}
