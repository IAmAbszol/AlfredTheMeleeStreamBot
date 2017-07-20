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
public class Alfred extends JPanel {
	
	private Settings settings;
	private FFmpeg ffmpeg;
	private ArrayList<Player> playerOne = new ArrayList<Player>();
	private ArrayList<Player> playerTwo = new ArrayList<Player>();
	private int offset = 0;
	private int patience = 0;
	private double error = .0;
	private String streamPath = null;
	
	private JLabel status;
	private JLabel roundsLabel;
	private JLabel playerOneScore;
	private JLabel playerTwoScore;
	private JComboBox roundsCombo;
	
	private int p1Score = 0;
	private int p2Score = 0;
	private boolean running = false;
	
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
		
		//setup();
		
		//Setup s = new Setup(settings, ffmpeg);
		//s.setStreamPath("C:/Users/Kyle/Desktop/Recording/NAIR.flv");
		//s.setupInterface();
		
		load("C:\\Projects\\AlfredTheMeleeStreamBot\\template.alfred");
		constructAlfredInterface();
		
		
	}
	
	public static void main(String[] args) {
		
		Alfred alfred = new Alfred();

	}
	
	public void load() {
		settings.loadSettings();
		playerOne.clear();
		playerOne.addAll(settings.getPlayerOne());
		playerTwo.clear();
		playerTwo.addAll(settings.getPlayerTwo());
		offset = settings.getOffset();
		patience = settings.getPatience();
		error = settings.getError();
		streamPath = settings.getStreamPath();
		settings.setStreamPath(streamPath);
	}
	
	public void load(String s) {
		settings.loadSettings(s);
		playerOne.clear();
		playerOne.addAll(settings.getPlayerOne());
		playerTwo.clear();
		playerTwo.addAll(settings.getPlayerTwo());
		offset = settings.getOffset();
		patience = settings.getPatience();
		error = settings.getError();
		streamPath = settings.getStreamPath();
	}
	
	public void save() {
		settings.save();
	}
	/*
	public void watch() {
		running = true;
		players.clear();
		players.addAll(settings.getPlayers());
		screens.clear();
		screens.addAll(settings.getScreens());
		offset = settings.getOffset();
		patience = settings.getPatience();
		error = settings.getError();
		streamPath = settings.getStreamPath();
		settings.setStreamPath(streamPath);
		runningThread = new Thread(new Runnable() {
			public void run() {
				try {
					settings.log("Starting Alfred");
					int ticks = 0;
					boolean changed = false;
					int[] before = null;
					int[] compare = null;
					int[] current = null;
					boolean comparing = false;
					
					// player stuff
					int[] playerOneBefore = null;
					int[] playerTwoBefore = null;
					int[] playerOneCompare = null;
					int[] playerTwoCompare = null;
					int[] playerOneCurrent = null;
					int[] playerTwoCurrent = null;
					boolean playerOneComparing = false;
					boolean playerTwoComparing = false;
					int playerOneStockCount = 4;
					int playerTwoStockCount = 4;
					int playerOneTicks = 0;
					int playerTwoTicks = 0;
					while(running) {
						switch(theState) {
						case PLAYER:
							playerOneBefore = playerOneCurrent;
							playerTwoBefore = playerTwoCurrent;
							
							// screen algorithm
							long pos2 = ffmpeg.getDuration(settings.getStreamPath());
							
							// capture the image
							String arg2 = "ffmpeg -ss " + pos2 + " -i \"" + streamPath + "\" -y -vframes 1 -q:v 2 alfred.png";
							ProcessBuilder builder2 = new 
									 ProcessBuilder(
											 "cmd", "/c", arg2);
							
							builder2.redirectErrorStream(true);
							Process p2 = builder2.start();
							
							ProcMon.create(p2);
							while(!ProcMon.isComplete()) {
								System.out.print("");
							}
							
							// grab the image
							BufferedImage image2 = ImageIO.read(new File("alfred.png"));
							
							int[] playerOneStock = players.get(0).getStockCoordinates();
							int[] playerTwoStock = players.get(1).getStockCoordinates();
							playerOneCurrent = AveragePixels.averageColor(image2, playerOneStock[0], playerOneStock[1], playerOneStock[2], playerOneStock[3]);
							playerTwoCurrent = AveragePixels.averageColor(image2, playerTwoStock[0], playerTwoStock[1], playerTwoStock[2], playerTwoStock[3]);
							
							// I don't want that first frame
							if(pos2 != 1) {
								
								double playerOneDifference = 0;
								double playerTwoDifference = 0;
								if(playerOneCurrent != null && playerOneBefore != null) {
									if(playerOneComparing)
										playerOneDifference = myError(playerOneCompare, playerOneCurrent);
									else
										playerOneDifference = myError(playerOneBefore, playerOneCurrent);
								}
								if(playerTwoCurrent != null && playerTwoBefore != null) {
									if(playerTwoComparing) 
										playerTwoDifference = myError(playerTwoCompare, playerTwoCurrent);
									else
										playerTwoDifference = myError(playerTwoBefore, playerTwoCurrent);
								}
								settings.log(pos2 + ": Player 1 Dif: " + playerOneDifference + " Player 2 Dif: " + playerTwoDifference);
								if(playerOneCurrent != null && playerOneDifference > error) {
									if(!playerOneComparing) {
										playerOneComparing = true;
										playerOneCompare = playerOneBefore;
									}
									playerOneTicks++;
									if(playerOneTicks >= patience && playerOneComparing) {
										playerOneTicks = 0;
										players.get(0).getStockCoordinates()[0] -= offset;
									}
								} else {
									playerOneTicks--;
									if(playerOneTicks < 0) playerOneTicks = 0;
									if(playerOneComparing) comparing = false;
								}
								if(current != null && difference2 > error) {
									if(!comparing) {
										// this is to have an "intelligence" that recognizes a change
										comparing = true;
										compare = before;
										ImageIO.write(image2, "png", new File("tmp.png"));
									}
									ticks++;
									if(ticks >= patience && comparing) {
										
										ticks = 0;
										theState = ALFRED_STATE.PLAYER;
										settings.log("Changed!");
										comparing = false;
										
									}
								} else {
									ticks--;
									if(ticks < 0) ticks = 0;
									// as soon as a condition is not met, comparing is none
									if(comparing) comparing = false;
								}
								
							}
							break;
						case SCREEN:
							
							before = current;
							
							// screen algorithm
							long pos = ffmpeg.getDuration(settings.getStreamPath());
							
							// capture the image
							String arg = "ffmpeg -ss " + pos + " -i \"" + streamPath + "\" -y -vframes 1 -q:v 2 alfred.png";
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
							
							int[] coords = screens.get(0).getScreenCoords();
							current = AveragePixels.averageColor(image, coords[0], coords[1], coords[2], coords[3]);
							
							// I don't want that first frame
							if(pos != 1) {
								
								// lets compute the difference
								double difference = 0;
								if(current != null && before != null) {
									if(comparing)
										difference = myError(compare,current);
									else
										difference = myError(before,current);
								}
								if(!comparing)
									settings.log(pos + ": " + difference);
								else
									settings.log(pos + ": Comparing -> " + difference);
								/*
								if(current != null && difference < error) {
									if(!comparing) {
										// this is to have an "intelligence" that recognizes a change
										comparing = true;
										compare = before;
										ImageIO.write(image, "png", new File("tmp.png"));
									}
									ticks++;
									if(ticks >= patience && comparing) {
										
										ticks = 0;
										theState = ALFRED_STATE.PLAYER;
										settings.log("Changed!");
										comparing = false;
										
									}
								} else {
									ticks--;
									if(ticks < 0) ticks = 0;
									// as soon as a condition is not met, comparing is none
									if(comparing) comparing = false;
								}
							}
							
							break;
						default:
							break;
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
		offset = settings.getOffset();
		patience = settings.getPatience();
		error = settings.getError();
		streamPath = settings.getStreamPath();
		settings.setStreamPath(streamPath);
		runningThread = new Thread(new Runnable() {
			public void run() {
				try {
					settings.log("Starting Alfred");
					AlfredColor[][] compare = null;
					AlfredColor[][] current = null;
					
					ArrayList<Integer> p1 = new ArrayList<Integer>();
					ArrayList<Integer> p2 = new ArrayList<Integer>();
					for(int i = 0; i < playerOne.size(); i++) {
						AlfredColor[][] p1Color = playerOne.get(i).getColor();
						AlfredColor[][] p2Color = playerTwo.get(i).getColor();
					//	System.out.println("Player One " + i + ": " + p1Color[0] + ", " + p1Color[1] + ", " + p1Color[2]);
					//	System.out.println("Player Two " + i + ": " + p2Color[0] + ", " + p2Color[1] + ", " + p2Color[2]);
					}
					while(running) {
					//for(int z = 1; z < 99; z++) {
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
							compare = playerOne.get(i).getColor();
							//settings.log("" + c1 + " and " + c2);
							// I don't want that first frame
							if(pos != 1) {
								
								double difference = calculateGridError(compare, current);
								System.out.println("P1: " + pos + ": " + i + ": " + difference);
								//settings.log(""+difference);
								settings.log("P1: " + pos + ": " + i + ": " + difference);
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
						//System.out.println(pos + " Player Two: " + current[0] + ", " + current[1] + ", " + current[2]);
						for(int i = 0; i < playerTwo.size(); i++) {
							compare = playerTwo.get(i).getColor();
							//settings.log("" + c1 + " and " + c2);
							// I don't want that first frame
							if(pos != 1) {
								
								double difference = calculateGridError(compare, current);
								System.out.println("P2: " + pos + ": " + i + ": " + difference);
								//settings.log(""+difference);
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
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		runningThread.start();
	}
	
	private void handlePlayer(BufferedImage image, String n, String s, ArrayList<Integer> p) {
		try {
			int score = p.get(p.size() - 1);
			settings.log(s + "->" + p.get(p.size() - 1));
			System.out.println(s + "->" + p.get(p.size() - 1));
			String path = "C:/Users/Kyle/Desktop/Recording/"+n+".png";
			if(s.contains("One")) { 
				int[] p1 = playerOne.get(0).getScreenCoords();
				p1Score = score;
				playerOneScore.setText("Player One Score: " + p1Score);
			} else {
				int[] p2 = playerTwo.get(0).getScreenCoords();
				p2Score = score;
				playerTwoScore.setText("Player Two Score: " + p2Score);
			}
			repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void drawBox(String path, BufferedImage image , int x, int y, int w, int h) throws IOException {
		
		Graphics g = image.getGraphics();
		g.setColor(Color.red);
		g.drawRect(x, y, w, h);
		ImageIO.write(image, "png", new File(path));
		
	}
	public void setup() {
		
		if(!ScanForFFMpeg.scan()) {
			JOptionPane.showMessageDialog(null, "ERROR!\nThe program FFmpeg is either not installed or hasn't been installed properly!\nNotes on installation for FFmpeg is provided in the README.txt.\nThis is to speed up the streaming process, Alfred can't carry all the weight.");
			System.exit(1);
		}
		
		if(!checkForSave()) {
			 if (JOptionPane.showConfirmDialog(null, 
	            "Template not detected!\nPlease load NAME.alfred into this current directory.\nClick yes if you wish to continue and create a new template.\nIf no then the program will exit, just relaunch with the template in this directory=]", null,
	            JOptionPane.YES_NO_OPTION,
	            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
				 
				 // setup
				 Setup setup = new Setup(settings, ffmpeg);
			 } else {
				 System.exit(1);
			 }
		} else 
			 load();
	}
	
	public void constructAlfredInterface() {
		
		// lets build alfreds panel
		JFrame frame = new JFrame("Alfred by Kyle Darling (Abszol)");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 300);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(this);
		setLayout(null);
		
		status = new JLabel("<html>Status: <font color='red'>Idle</font></html>");
		status.setBackground(Color.BLACK);
		status.setHorizontalAlignment(SwingConstants.CENTER);
		status.setFont(new Font("Arial", Font.BOLD, 24));
		status.setBounds(12, 13, 408, 50);
		add(status);
		
		roundsLabel = new JLabel("Rounds To Win");
		roundsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		roundsLabel.setFont(new Font("Arial", Font.BOLD, 18));
		roundsLabel.setBounds(12, 76, 150, 50);
		add(roundsLabel);
		
		JComboBox roundsCombo = new JComboBox();
		roundsCombo.setFont(new Font("Arial", Font.BOLD, 14));
		roundsCombo.setBounds(320, 88, 100, 29);
		add(roundsCombo);
		
		for(int i = 0; i < rounds.length; i++) roundsCombo.addItem(rounds[i]);
		
		JButton start = new JButton("Start Alfred");
		start.setFont(new Font("Arial", Font.BOLD, 14));
		start.setBounds(12, 139, 150, 40);
		add(start);
		
		JButton viewSettings = new JButton("View Settings");
		viewSettings.setFont(new Font("Arial", Font.BOLD, 14));
		viewSettings.setBounds(12, 192, 150, 40);
		add(viewSettings);
		
		JButton reset = new JButton("Reset");
		reset.setFont(new Font("Arial", Font.BOLD, 14));
		reset.setBounds(270, 139, 150, 40);
		add(reset);
		
		playerOneScore = new JLabel("Player One Score: " + p1Score);
		playerOneScore.setFont(new Font("Arial", Font.BOLD, 14));
		playerOneScore.setBounds(270, 192, 150, 16);
		add(playerOneScore);
		
		playerTwoScore = new JLabel("Player Two Score: " + p2Score);
		playerTwoScore.setFont(new Font("Arial", Font.BOLD, 14));
		playerTwoScore.setBounds(270, 221, 150, 16);
		add(playerTwoScore);
		
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
				settings.settingsInterface();
			}
			
		});
	}
	
	private double myError(int[] c1, int[] c2) {
		try {
			// first we calculate RGB difference
			double difference = 35;				// assign a difference of 30 or under
			double rgbSquare = (Math.pow((c1[0] - c2[0]), 2) + Math.pow((c1[1] - c2[1]), 2) + Math.pow((c1[2] - c2[2]), 2));
			double result = Math.sqrt(rgbSquare);
			settings.log("Found difference between this iteration: " + result);
			if(result < difference) {
				// were in the clear
				double b4Square = Math.pow((c1[1] - c2[1]), 2);
				return Math.sqrt(b4Square);
			} else
				return result;
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
	
}
