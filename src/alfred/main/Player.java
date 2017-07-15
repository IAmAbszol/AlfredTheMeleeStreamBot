package alfred.main;

import java.io.Serializable;

/*
 * Player controls the stock coordinates when switched
 * and also controls which team they are on.
 * Either teamOne = true or teamOne = false.
 */
public class Player implements Serializable {

private static final long serialVersionUID = 1L;
	
	private int[] screenCoords;
	private int[] color;
	private int ticks = 0;

	public Player() {
		ticks = 0;
	}
	
	public void setScreenColor(int[] c) {
		color = c;
	}
	
	public void setScreenCoords(int[] screen) {
		screenCoords = screen;
	}

	public void setScreen(int[] screen, int[] rgb) {
		screenCoords = screen;
		this.color = rgb;
	}
	
	public int[] getScreenCoords() {
		return screenCoords;
	}
	
	public int[] getColor() {
		return color;
	}
	
	public int getTick() {
		return ticks;
	}
	
	public void setTick(int i) {
		ticks = i;
	}
	
}
