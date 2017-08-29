package alfred.main;

import java.io.Serializable;

import alfred.utils.AlfredColor;

/*
 * Player controls the stock coordinates when switched
 * and also controls which team they are on.
 * Either teamOne = true or teamOne = false.
 */
public class Player implements Serializable {

private static final long serialVersionUID = 1L;
	
	private int[] screenCoords;
	private AlfredColor[][] color;
	private int ticks = 0;
	
	private int patience = 2;
	private double error = 30;
	private int offset = 30;

	private int assigned_number = 0;
	
	public Player() {
		ticks = 0;
	}
	
	public void setPatience(int i) {
		patience = i;
	}
	
	public int getPatience() {
		return patience;
	}
	
	public void setError(double d) {
		error = d;
	}
	
	public double getError() {
		return error;
	}
	
	public void setAssignedNumber(int i) {
		assigned_number = i;
	}
	
	public void setScreenColor(AlfredColor[][] c) {
		color = c;
	}
	
	public void setScreenCoords(int[] screen) {
		screenCoords = screen;
	}

	public void setScreen(int[] screen, AlfredColor[][] rgb) {
		screenCoords = screen;
		this.color = rgb;
	}
	
	public int getAssignedNumber() {
		return assigned_number;
	}
	
	public int[] getScreenCoords() {
		return screenCoords;
	}
	
	public AlfredColor[][] getColor() {
		return color;
	}
	
	public int getTick() {
		return ticks;
	}
	
	public void setTick(int i) {
		ticks = i;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public void setOffset(int i) {
		offset = i;
	}
	
}
