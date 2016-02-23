package net.kopeph.icecube;

import com.jogamp.newt.event.KeyEvent;

public final class Input {
	public static final Input handler = new Input();

	//enumeration of control codes
	public static final int
		UP     = 0,
		DOWN   = 1,
		LEFT   = 2,
		RIGHT  = 3,
		JUMP   = 4,
		RESET  = 5,
		SELECT = 6,
		ESC    = 7,
		OTHER  = 8,
		LENGTH = 9;

	//counts the number of applicable keys currently down for a given control code
	//so that if e.g. the player presses A, then presses LEFT, then releases A, they won't stall
	//XXX: this shouldn't work well with key repeats or when the window loses focus,
	//yet somehow it does and I'm really confused.
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
			case 'r':                         return RESET;
			case '\r': case '\n':             return SELECT;
			case KeyEvent.VK_ESCAPE:          return ESC;
		}
		return OTHER;
	}

	public void handleKey(int keyCode, boolean down) {
		int control = translateKey(keyCode);

		if (down != isDown(control))
			keyChange(control, down);

		keysDown[control] += down? 1 : -1;
	}

	private void keyChange(int keyCode, boolean down) {
		//TODO: implement callbacks here
	}
}
