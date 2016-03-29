package net.kopeph.icecube.menu;

public final class LanguageMenu extends Menu {
	private final Button english, back;

	public LanguageMenu() {
		widgets.add(english = new Button("English", -50, () -> { /* not implemented yet */ }));
		widgets.add(back    = new Button("Back",     50, () -> { game.currentMenu = game.mainMenu; }));
	}
}
