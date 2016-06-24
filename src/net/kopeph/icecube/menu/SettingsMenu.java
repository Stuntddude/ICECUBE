package net.kopeph.icecube.menu;

import net.kopeph.icecube.ICECUBE;

public final class SettingsMenu extends Menu {
	private Button color, back;

	public SettingsMenu() {
		widgets.add(color = new Button(getColorBlindModeText(), -50, () -> {
			game.colorBlindMode = !game.colorBlindMode;
			game.diskStore.putBoolean(ICECUBE.KEY_COLORBLIND_MODE, game.colorBlindMode);
			color.text = getColorBlindModeText();
		}));
		widgets.add(back = new Button("Back", 50, () -> { game.currentMenu = game.mainMenu; }));
	}

	private String getColorBlindModeText() {
		return "Colorblind Mode: " + (game.colorBlindMode? "On" : "Off");
	}
}
