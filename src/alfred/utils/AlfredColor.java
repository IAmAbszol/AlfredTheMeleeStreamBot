package alfred.utils;

import java.io.Serializable;

public class AlfredColor implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int red = 0;
	private int green = 0;
	private int blue = 0;
	
	public AlfredColor() {}
	
	public AlfredColor(int r, int g, int b) {
		red = r;
		green = g;
		blue = b;
	}
	
	public int[] getColor() {
		return new int[] { red,green,blue };
	}
	
	public void setColor(int r, int g, int b) {
		red = r;
		green  =b;
		blue = b;
	}

}
