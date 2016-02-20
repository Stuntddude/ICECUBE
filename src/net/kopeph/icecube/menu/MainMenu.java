package net.kopeph.icecube.menu;

public class MainMenu extends Menu {
	private Button button;

	@Override
	public void draw() {
		button = new Button("Main Level", game.width*0.1f, game.height*0.5f - 50, game.width*0.8f, 100);
		game.background(0xCCAACC);
		button.draw();
	}
}
