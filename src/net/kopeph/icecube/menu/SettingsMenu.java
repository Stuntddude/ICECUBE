package net.kopeph.icecube.menu;

public final class SettingsMenu extends Menu {
	private Button color, back;

	public SettingsMenu() {
		widgets.add(color = new Button("Colorblind Mode: Off", -50, () -> {
			game.colorBlindMode = !game.colorBlindMode;
			color.text = "Colorblind Mode: " + (game.colorBlindMode? "On" : "Off");
		}));
		widgets.add(back = new Button("Back", 50, () -> { game.currentMenu = game.mainMenu; }));
	}
}
