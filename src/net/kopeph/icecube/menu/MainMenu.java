package net.kopeph.icecube.menu;

public class MainMenu extends Menu {
	private Button play, newGame, settings, language;

	public MainMenu() {
		widgets.add(play     = new Button("Play",     -150, 75));
		widgets.add(newGame  = new Button("New Game",  -50, 75));
		widgets.add(settings = new Button("Settings",   50, 75));
		widgets.add(language = new Button("Language",  150, 75));
	}
}
