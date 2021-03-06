package net.kopeph.icecube;

import java.awt.event.KeyEvent;

public final class Input {
	public static final Input handler = new Input();

	//enumeration of control codes
	public static final int
		UP     =  0,
		DOWN   =  1,
		LEFT   =  2,
		RIGHT  =  3,
		JUMP   =  4,
		RESET  =  5,
		SELECT =  6,
		ESC    =  7,
		DEBUG  =  8,
		SLOWMO =  9,
		OTHER  = 10,
		LENGTH = 11;

	//counts the number of applicable keys currently down for a given control code
	//so that if e.g. the player presses A, then presses LEFT, then releases A, they won't stall
	//this won't work if key-repeat is enabled, but it looks like in P3D mode it isn't
	private final int[] keysDown = new int[LENGTH];

	public boolean isDown(int control) {
		return keysDown[control] > 0;
	}

	private int translateKey(int keyCode) {
		switch(keyCode) {
			case 'W': case KeyEvent.VK_UP:    return UP;
			case 'S': case KeyEvent.VK_DOWN:  return DOWN;
			case 'A': case KeyEvent.VK_LEFT:  return LEFT;
			case 'D': case KeyEvent.VK_RIGHT: return RIGHT;
			case ' ':                         return JUMP;
			case 'R':                         return RESET;
			case '\r': case '\n':             return SELECT;
			case KeyEvent.VK_ESCAPE:          return ESC;
			case '`':                         return DEBUG;
			case 'F':                         return SLOWMO;
		}
		return OTHER;
	}

	public void handleKey(int keyCode, boolean down) {
		int control = translateKey(keyCode);

		if (down != isDown(control))
			ICECUBE.game.keyChanged(control, down);

		keysDown[control] += down? 1 : -1;
	}
}
