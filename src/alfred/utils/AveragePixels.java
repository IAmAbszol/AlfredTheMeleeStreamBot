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
	
	/*
	 * @params
	 * bi --> BufferedImage
	 * x0 --> Starting place for x read
	 * y0 --> Starting place for y read
	 * w --> Width of image being read in
	 * h --> Height of image being read in
	 * returns RGB array of integers
	 */
	public static int[] averageColor(BufferedImage bi, int x0, int y0, int w,
	        int h) throws IOException {
	    int x1 = x0 + w;
	    int y1 = y0 + h;
	    long sumr = 0, sumg = 0, sumb = 0;
	    for (int x = x0; x < x1; x++) {
	        for (int y = y0; y < y1; y++) {
	            Color pixel = new Color(bi.getRGB(x, y));
	            sumr += pixel.getRed();
	            sumg += pixel.getGreen();
	            sumb += pixel.getBlue();
	        }
	    }
	    int num = w * h;
	    int r = (int)(sumr/num);
	    int g = (int)(sumg/num);
	    int b = (int)(sumb/num);
	    int[] color = {r,g,b};
	    return color;
	}
	
}
