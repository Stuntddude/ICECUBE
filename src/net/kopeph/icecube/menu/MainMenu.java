package net.kopeph.icecube.menu;

public class MainMenu extends Menu {
	private final Button button = new Button("Main Level", 10, 20, 200, 60);

	@Override
	public void draw() {
		game.background(0xCCAACC);
		button.draw();
	}
}
