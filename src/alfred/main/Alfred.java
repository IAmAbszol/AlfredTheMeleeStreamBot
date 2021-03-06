package alfred.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import alfred.utils.AlfredColor;
import alfred.utils.AveragePixels;
import alfred.utils.FFmpeg;
import alfred.utils.ProcMon;
import alfred.utils.ScanForFFMpeg;

/*
 * Main part of the class, init some things
 * 
 * Alfred is a simple stream watching bot for Melee.
 * All Alfred does it monitor the stocks and states of the screens,
 * he will keep track of score and thats about it.
 * Other incorporations of Alfred will be introduced soon.
 * Eventually hoping to hop off of color recognition and into
 * other aspects of recognition.
 * 
 * Simply follow the setup instructions for Alfred and everything should run smoothly
 * 
 * Alfred will eventually be made into a library in order to incorporate "himself" into
 * the StreamUpdater project as a module.
 * This should come soon but thats why the structure of Alfred may seem a tad odd.
 */
@SuppressWarnings("serial")
public class Alfred {
	
	private Settings settings;
	private FFmpeg ffmpeg;
	private ArrayList<Player> playerOne = new ArrayList<Player>();
	private ArrayList<Player> playerTwo = new ArrayList<Player>();
	private ArrayList<Player> playerThree = new ArrayList<Player>();
	private ArrayList<Player> playerFour = new ArrayList<Player>();
	private String streamPath = null;
	
	private JPanel panel;
	private JLabel status;
	private JLabel playerOneScore;
	private JLabel playerTwoScore;
	private JLabel playerThreeScore;
	private JLabel playerFourScore;
	
	private int p1Score = 0;
	private int p2Score = 0;
	private int p3Score = 0;
	private int p4Score = 0;
	private boolean running = false;
	private boolean foundPlayerOneStock = false;
	private boolean foundPlayerTwoStock = false;
	private boolean foundPlayerThreeStock = false;
	private boolean foundPlayerFourStock = false;
	
	private int maxStockCount = 4;
	private int p1StockCount = maxStockCount;
	private int p2StockCount = maxStockCount;
	private int p3StockCount = maxStockCount;
	private int p4StockCount = maxStockCount;
	
	private Thread runningThread;
	
	private enum ALFRED_STATE { SCREEN, PLAYER };
	private ALFRED_STATE theState = ALFRED_STATE.SCREEN;
	
	private int[] rounds = { 1, 3, 5, 7, 9, 11, 13, 15 }; // could've used mod but yolo
	
	public Alfred() {
		
		ffmpeg = new FFmpeg();
		settings = new Settings();
		
		try {
			
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
		} catch (Exception e) { };
		
	}
	
	public void update() {
		playerOne.clear();
		playerOne.addAll(settings.getPlayerOne());
		playerTwo.clear();
		playerTwo.addAll(settings.getPlayerTwo());
		playerThree.clear();
		playerThree.addAll(settings.getPlayerThree());
		playerFour.clear();
		playerFour.addAll(settings.getPlayerFour());
		streamPath = settings.getStreamPath();
		settings.setStreamPath(streamPath);
	}
	
	public void load() {
		settings.loadSettings();
		playerOne.clear();
		playerOne.addAll(settings.getPlayerOne());
		playerTwo.clear();
		playerTwo.addAll(settings.getPlayerTwo());
		playerThree.clear();
		playerThree.addAll(settings.getPlayerThree());
		playerFour.clear();
		playerFour.addAll(settings.getPlayerFour());
		streamPath = settings.getStreamPath();
		settings.setStreamPath(streamPath);
	}
	
	public void load(String s) {
		settings.loadSettings(s);
		playerOne.clear();
		playerOne.addAll(settings.getPlayerOne());
		playerTwo.clear();
		playerTwo.addAll(settings.getPlayerTwo());
		playerThree.clear();
		playerThree.addAll(settings.getPlayerThree());
		playerFour.clear();
		playerFour.addAll(settings.getPlayerFour());
		streamPath = settings.getStreamPath();
	}
	
	public void save() {
		settings.save();
	}

	/*
	public void watch() {
		running = true;
		playerOne.clear();
		playerOne.addAll(settings.getPlayerOne());
		playerTwo.clear();
		playerTwo.addAll(settings.getPlayerTwo());
		playerThree.clear();
		playerThree.addAll(settings.getPlayerThree());
		playerFour.clear();
		playerFour.addAll(settings.getPlayerFour());
		streamPath = settings.getStreamPath();
		settings.setStreamPath(streamPath);
		if(status != null)
			status.setText("<html>Status: <font color='green'>Running</font></html>");
		runningThread = new Thread(new Runnable() {
			public void run() {
				try {
					settings.log("Starting Alfred");
					AlfredColor[][] compare = null;
					AlfredColor[][] current = null;
					
					ArrayList<Integer> p1 = new ArrayList<Integer>();
					ArrayList<Integer> p2 = new ArrayList<Integer>();
					ArrayList<Integer> p3 = new ArrayList<Integer>();
					ArrayList<Integer> p4 = new ArrayList<Integer>();
					
					int patience = 0;
					double error = 0;
					
					while(running) {
						// screen algorithm
						long pos = ffmpeg.getDuration(settings.getStreamPath());
						//long pos = z;
						// capture the image
						String arg = "ffmpeg -ss " + pos + " -i \"" + streamPath + "\" -y -vframes 1 -q:v 1 alfred.png";
						ProcessBuilder builder = new 
								 ProcessBuilder(
										 "cmd", "/c", arg);
						
						builder.redirectErrorStream(true);
						Process p = builder.start();
						
						ProcMon.create(p);
						while(!ProcMon.isComplete()) {
							System.out.print("");
						}
						
						// grab the image
						BufferedImage image = ImageIO.read(new File("alfred.png"));
						// compare each screen which is actually each star
						int[] coords = playerOne.get(0).getScreenCoords();
						current = AveragePixels.averageColor(image, coords[0], coords[1], coords[2], coords[3]);
						//System.out.println(pos + " Player One: " + current[0] + ", " + current[1] + ", " + current[2]);
						for(int i = 0; i < playerOne.size(); i++) {
							error = playerOne.get(i).getError();
							patience = playerOne.get(i).getPatience();
							compare = playerOne.get(i).getColor();
							//settings.log("" + c1 + " and " + c2);
							// I don't want that first frame
							if(pos != 1) {
								
								double difference = calculateGridError(compare, current);
								settings.log("P1 " + pos + ": " + i + ": " + difference);
								if(current != null && difference < error) {
									int t = playerOne.get(i).getTick();
									t++;
									playerOne.get(i).setTick(t);
									if(playerOne.get(i).getTick() >= patience) {
										// clear all remaining ticks, this causes it to revert back
										for(int k = 0; k < playerOne.size(); k++)
											playerOne.get(k).setTick(0);
										p1.add(i);
									}
								} else {
									int t = playerOne.get(i).getTick();
									t--;
									playerOne.get(i).setTick(t);
									if(playerOne.get(i).getTick() < 0) playerOne.get(i).setTick(0);;
								}
								
							}
						}
						
						coords = playerTwo.get(0).getScreenCoords();
						current = AveragePixels.averageColor(image, coords[0], coords[1], coords[2], coords[3]);
						for(int i = 0; i < playerTwo.size(); i++) {
							error = playerTwo.get(i).getError();
							patience = playerTwo.get(i).getPatience();
							compare = playerTwo.get(i).getColor();
							// I don't want that first frame
							if(pos != 1) {
								
								double difference = calculateGridError(compare, current);
								settings.log("P2 " + pos + ": " + i + ": " + difference);
								if(current != null && difference < error) {
									int t = playerTwo.get(i).getTick();
									t++;
									playerTwo.get(i).setTick(t);
									if(playerTwo.get(i).getTick() >= patience) {
										
										for(int k = 0; k < playerTwo.size(); k++) 
											playerTwo.get(k).setTick(0);
										p2.add(i);
										
									}
								} else {
									int t = playerTwo.get(i).getTick();
									t--;
									playerTwo.get(i).setTick(t);
									if(playerTwo.get(i).getTick() < 0) playerTwo.get(i).setTick(0);
								}
								
							}
						}
						
						coords = playerThree.get(0).getScreenCoords();
						current = AveragePixels.averageColor(image, coords[0], coords[1], coords[2], coords[3]);
						for(int i = 0; i < playerThree.size(); i++) {
							error = playerThree.get(i).getError();
							patience = playerThree.get(i).getPatience();
							compare = playerThree.get(i).getColor();
							// I don't want that first frame
							if(pos != 1) {
								
								double difference = calculateGridError(compare, current);
								settings.log("P3" + pos + ": " + i + ": " + difference);
								if(current != null && difference < error) {
									int t = playerThree.get(i).getTick();
									t++;
									playerThree.get(i).setTick(t);
									if(playerThree.get(i).getTick() >= patience) {
										
										for(int k = 0; k < playerThree.size(); k++) 
											playerThree.get(k).setTick(0);
										p3.add(i);
										
									}
								} else {
									int t = playerThree.get(i).getTick();
									t--;
									playerThree.get(i).setTick(t);
									if(playerThree.get(i).getTick() < 0) playerThree.get(i).setTick(0);
								}
								
							}
						}
						
						coords = playerFour.get(0).getScreenCoords();
						current = AveragePixels.averageColor(image, coords[0], coords[1], coords[2], coords[3]);
						for(int i = 0; i < playerFour.size(); i++) {
							error = playerFour.get(i).getError();
							patience = playerFour.get(i).getPatience();
							compare = playerFour.get(i).getColor();
							// I don't want that first frame
							if(pos != 1) {
								
								double difference = calculateGridError(compare, current);
								settings.log("P4 " + pos + ": " + i + ": " + difference);
								if(current != null && difference < error) {
									int t = playerFour.get(i).getTick();
									t++;
									playerFour.get(i).setTick(t);
									if(playerFour.get(i).getTick() >= patience) {
										
										for(int k = 0; k < playerFour.size(); k++) 
											playerFour.get(k).setTick(0);
										p4.add(i);
										
									}
								} else {
									int t = playerFour.get(i).getTick();
									t--;
									playerFour.get(i).setTick(t);
									if(playerFour.get(i).getTick() < 0) playerFour.get(i).setTick(0);
								}
								
							}
						}
						
						// handle event here. 
						// Basically, it takes the last matched error
						// then looks at what it's score should be + 1 for the screen pos
						// then it will check that players score, if it's not up to par
						// then it will set it to that last score
						if(p1.size() > 0) {
							handlePlayer(image, pos + "P1",pos + ": Player One",p1);
							p1.clear();
						}
						if(p2.size() > 0) {
							handlePlayer(image, pos + "P2", pos + ": Player Two",p2);
							p2.clear();
						}
						if(p3.size() > 0) {
							handlePlayer(image, pos  + "P3" , pos + ": Player Three", p3);
							p3.clear();
						}
						if(p4.size() > 0) {
							handlePlayer(image, pos + "P4", pos + ": Player Four", p4);
							p4.clear();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		runningThread.start();
	}
	*/
	
	public void watch() {
		
		running = true;
		playerOne.clear();
		playerOne.addAll(settings.getPlayerOne());
		playerTwo.clear();
		playerTwo.addAll(settings.getPlayerTwo());
		playerThree.clear();
		playerThree.addAll(settings.getPlayerThree());
		playerFour.clear();
		playerFour.addAll(settings.getPlayerFour());
		streamPath = settings.getStreamPath();
		settings.setStreamPath(streamPath);
		if(status != null)
			status.setText("<html>Status: <font color='green'>Running</font></html>");

		runningThread = new Thread(new Runnable() {
			public void run() {
				
				settings.log("Starting Alfred");
				AlfredColor[][] playerOneCompare = null;
				AlfredColor[][] playerTwoCompare = null;
				AlfredColor[][] playerThreeCompare = null;
				AlfredColor[][] playerFourCompare = null;
				
				ArrayList<AlfredColor[][]> current = new ArrayList<AlfredColor[][]>();
				
				ArrayList<Integer> p1 = new ArrayList<Integer>();
				ArrayList<Integer> p2 = new ArrayList<Integer>();
				ArrayList<Integer> p3 = new ArrayList<Integer>();
				ArrayList<Integer> p4 = new ArrayList<Integer>();
				
				int patience = 0;
				double error = 0;
				int offset = 0;
				
				p1StockCount = p2StockCount = p3StockCount = p4StockCount = maxStockCount;
				
				while(running) {
				
					try {
					
						// screen algorithm
						long pos = ffmpeg.getDuration(settings.getStreamPath());
						//long pos = z;
						// capture the image
						String arg = "ffmpeg -ss " + pos + " -i \"" + streamPath + "\" -y -vframes 1 -q:v 1 alfred.png";
						ProcessBuilder builder = new 
								 ProcessBuilder(
										 "cmd", "/c", arg);
						
						builder.redirectErrorStream(true);
						Process p = builder.start();
						
						ProcMon.create(p);
						while(!ProcMon.isComplete()) {
							System.out.print("");
						}
						
						// grab the image
						BufferedImage image = ImageIO.read(new File("alfred.png"));
						
						/*
						 * First to analyze player one
						 */
						patience = playerOne.get(0).getPatience();
						error = playerOne.get(0).getError();
						offset = playerOne.get(0).getOffset();
						
						// grab the first screen coordinate where the stock is presumed to be.
						// color differences in any other menu should trigger a "no stock found"
						if(!foundPlayerOneStock) {
							playerOneCompare = AveragePixels.averageColor(image, playerOne.get(0).getScreenCoords()[0], 
									playerOne.get(0).getScreenCoords()[1], 
									playerOne.get(0).getScreenCoords()[2], 
									playerOne.get(0).getScreenCoords()[3]);
							
							// now to compare to the presumed three other stocks, assumin it hasn't been found yet
							for(int i = 1; i < maxStockCount; i++) {
								AlfredColor[][] currentSingle = AveragePixels.averageColor(image, playerOne.get(0).getScreenCoords()[0] - (i * offset), 
										playerOne.get(0).getScreenCoords()[1], 
										playerOne.get(0).getScreenCoords()[2], 
										playerOne.get(0).getScreenCoords()[3]);
								current.add(currentSingle);
							}
							
							// current is loaded up for comparison, now to compare then average
							double result = 0;
							for(int i = 0; i < current.size(); i++) {
								result += calculateGridError(current.get(i), playerOneCompare);
							}
							double calculatedError = result / current.size();
							
							if(calculatedError < error) {
								foundPlayerOneStock = true;
							}
							
							current.clear();
							
						} else {
							// assuming that we have found the stock area/we are in game
							// the comparator value is stored inside p1
							
							// first reload current to extract each stock, even the first this time as it's getting compared
							for(int i = 0; i < maxStockCount; i++) {
								AlfredColor[][] currentSingle = AveragePixels.averageColor(image, playerOne.get(0).getScreenCoords()[0] - (i * offset), 
										playerOne.get(0).getScreenCoords()[1], 
										playerOne.get(0).getScreenCoords()[2], 
										playerOne.get(0).getScreenCoords()[3]);
								current.add(currentSingle);
							}
							
							// now to compare each stock to determine current stock loss
							for(int i = 0; i < current.size(); i++) {
								double result = calculateGridError(current.get(i), playerOneCompare);
								if(result < error) {
									p1StockCount = maxStockCount - i;
									break;
								}
							}
							
						}
						
						
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
					
			}
		});
		
	}
	
	private void handlePlayer(BufferedImage image, String n, String s, ArrayList<Integer> p) {
		try {
			int score = p.get(p.size() - 1);
			settings.log(s + "->" + p.get(p.size() - 1));
			if(s.contains("One")) { 
				p1Score = score;
				if(playerOneScore != null)
					playerOneScore.setText("Player One Score: " + p1Score);
			}
			if(s.contains("Two")) {
				p2Score = score;
				if(playerTwoScore != null)
					playerTwoScore.setText("Player Two Score: " + p2Score);
			}
			if(s.contains("Three")) {
				p3Score = score;
				if(playerThreeScore != null)
					playerThreeScore.setText("Player Three Score: " + p3Score);
			}
			if(s.contains("Four")) {
				p4Score = score;
				if(playerFourScore != null) 
					playerFourScore.setText("Player Four Score: " + p4Score);
			}
			if(panel != null)
				panel.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void constructAlfredInterface() {
		
		// lets build alfreds panel
		JFrame frame = new JFrame("Alfred by Kyle Darling (Abszol)");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 300);
		panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(panel);
		panel.setLayout(null);
		
		status = new JLabel("<html>Status: <font color='red'>Idle</font></html>");
		status.setBackground(Color.BLACK);
		status.setHorizontalAlignment(SwingConstants.CENTER);
		status.setFont(new Font("Arial", Font.BOLD, 24));
		status.setBounds(12, 13, 408, 50);
		panel.add(status);
		
		JButton start = new JButton("Start Alfred");
		start.setFont(new Font("Arial", Font.BOLD, 14));
		start.setBounds(12, 76, 150, 40);
		panel.add(start);
		
		JButton viewSettings = new JButton("View Settings");
		viewSettings.setFont(new Font("Arial", Font.BOLD, 14));
		viewSettings.setBounds(12, 139, 150, 40);
		panel.add(viewSettings);
		
		JButton reset = new JButton("Reset");
		reset.setFont(new Font("Arial", Font.BOLD, 14));
		reset.setBounds(270, 139, 150, 40);
		panel.add(reset);
		
		playerOneScore = new JLabel("Player One Score: " + p1Score);
		playerOneScore.setFont(new Font("Arial", Font.BOLD, 14));
		playerOneScore.setBounds(12, 192, 150, 16);
		panel.add(playerOneScore);
		
		playerTwoScore = new JLabel("Player Two Score: " + p2Score);
		playerTwoScore.setFont(new Font("Arial", Font.BOLD, 14));
		playerTwoScore.setBounds(12, 221, 150, 16);
		panel.add(playerTwoScore);
		
		playerThreeScore = new JLabel("Player Three Score: " + p3Score);
		playerThreeScore.setFont(new Font("Arial", Font.BOLD, 14));
		playerThreeScore.setBounds(270, 192, 150, 16);
		panel.add(playerThreeScore);
		
		playerFourScore = new JLabel("Player Four Score: " + p4Score);
		playerFourScore.setFont(new Font("Arial", Font.BOLD, 14));
		playerFourScore.setBounds(270, 221, 150, 16);
		panel.add(playerFourScore);
		
		frame.setResizable(false);
		frame.setVisible(true);
		
		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(running) {
					running = false;
					start.setText("Start Alfred");
					settings.log("Stopping Alfred");
				} else {
					start.setText("Stop Alfred");
					watch();
				}
			}
			
		});
		
		reset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				p1Score = 0;
				p2Score = 0;
				playerOneScore.setText("Player One Score: " + p1Score);
				playerTwoScore.setText("Player Two Score: " + p2Score);
				status.setText("<html>Status: <font color='red'>Idle</font></html>");
				running = false;
				start.setText("Start Alfred");
			}
			
		});
		
		viewSettings.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
			
		});
	}
	
	private double myError(int[] c1, int[] c2) {
		try {
			double b4Square = Math.pow((c1[0] - c2[0]), 2) + Math.pow((c1[1] - c2[1]), 2) + Math.pow((c1[2] - c2[2]), 2);
			return Math.sqrt(b4Square);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	private boolean checkForSave() {
		File f = new File(".");
		File[] ls = f.listFiles();
		boolean located = false;
		for(int i = 0; i < ls.length; i++) 
			if(ls[i].getName().contains(".alfred")) {
				located = true;
				break;
			}
		return located;
	}
	
	/*
	 * @params
	 * c1 --> comparative array #1, order matters.
	 * c2 --> comparative array #2, order matters.
	 */
	public double calculateGridError(AlfredColor[][] c1, AlfredColor[][] c2) {
		double result = 0;
		double total = 0;
		// throw no result due to error
		if(c1.length != c2.length || c1[0].length != c2[0].length) return -1;
		
		// calculate total error
		for(int row = 0; row < c1.length; row++) {
			for(int col = 0; col < c1[0].length; col++) {
				total += myError(c1[row][col].getColor(), c2[row][col].getColor());
			}
		}
		
		// now average, divide by matrix row * column
		result = total / (c1.length * c1[0].length);
		return result;
		
	}
	
	// grabbing some things
	public int getPlayerOneScore() {
		return p1Score;
	}
	
	public int getPlayerTwoScore() {
		return p2Score;
	}
	
	public int getPlayerThreeScore() {
		return p3Score;
	}
	
	public int getPlayerFourScore() {
		return p4Score;
	}
	
	public void setPlayerOneScore(int s) {
		p1Score = s;
	}
	
	public void setPlayerTwoScore(int s) {
		p2Score = s;
	}
	
	public void setPlayerThreeScore(int s) {
		p3Score = s;
	}
	
	public void setPlayerFourScore(int s) {
		p4Score = s;
	}
	
	public Settings getSettings() {
		return settings;
	}
	
	public FFmpeg getFFmpeg() {
		return ffmpeg;
	}
	
	public void setSettings(Settings s) {
		settings = s;
	}
	
	public ArrayList<Player> getPlayerOne() {
		return playerOne;
	}
	
	public ArrayList<Player> getPlayerTwo() {
		return playerTwo;
	}
	
	public ArrayList<Player> getPlayerThree() {
		return playerThree;
	}
	
	public ArrayList<Player> getPlayerFour() {
		return playerFour;
	}
	
	public void changeError(ArrayList<Player> p, double error) {
		for(int i = 0; i < p.size(); i++) 
			p.get(i).setError(error);
	}
	
	public void changePatience(ArrayList<Player> p, int patience) {
		for(int i = 0; i < p.size(); i++) 
			p.get(i).setPatience(patience);
	}
	
}
