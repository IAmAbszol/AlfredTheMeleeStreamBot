package alfred.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class AveragePixels {
	
	public static AlfredColor[][] averageColor(BufferedImage bi, int x0, int y0, int w, int h) {
		
		AlfredColor[][] result = new AlfredColor[h][w];
		
		int x1 = x0 + w;
		int y1 = y0 + h;
		
		 for (int x = x0, a = 0; x < x1; x++, a++) {
		        for (int y = y0, b = 0; y < y1; y++, b++) {
		        	Color tmp = new Color(bi.getRGB(x, y));
		        	AlfredColor c = new AlfredColor(tmp.getRed(), tmp.getGreen(), tmp.getBlue());
		        	result[b][a] = c;
		        }
		 }
		
		return result;
		
	}
	
}
