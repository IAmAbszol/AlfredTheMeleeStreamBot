package alfred.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import alfred.utils.AlfredColor;
import alfred.utils.AveragePixels;
import alfred.utils.FFmpeg;

@SuppressWarnings("serial")
public class Setup extends JPanel implements MouseListener, KeyListener, Runnable {
	
	private Settings setting;
	private FFmpeg ffmpeg;
	
	public static int WIDTH = 1280;
	public static int HEIGHT = 720;
	
	private Thread thread;
	private boolean running;
	private int FPS = 200;
	
	private BufferedImage image;
	private BufferedImage ss;
	private Graphics2D g;
	
	private JFileChooser jfc;
	private String streamPath;
	
	private JFrame f;
	private Preview preview;
	private JLabel instructions;
	private JSlider offsetSlider;
	private JSpinner offsetSpinner;
	
	// for menu
	private ArrayList<Integer> xm = new ArrayList<Integer>();
	private ArrayList<Integer> ym = new ArrayList<Integer>();
	private ArrayList<Integer> wm = new ArrayList<Integer>();
	private ArrayList<Integer> hm = new ArrayList<Integer>();
	private ArrayList<BufferedImage> mm = new ArrayList<BufferedImage>();
	private boolean drawn = false;
	private boolean locked = false;
	
	// for stocks
	private ArrayList<Integer> xs = new ArrayList<Integer>();
	private ArrayList<Integer> ys = new ArrayList<Integer>();
	private ArrayList<Integer> ws = new ArrayList<Integer>();
	private ArrayList<Integer> hs = new ArrayList<Integer>();
	private ArrayList<Integer> tx = new ArrayList<Integer>();
	private boolean testing = false;
	private int curPos = 0;
	private int offset = 0;
	
	private enum selectionState { P1, P2, P3, P4};
	private selectionState theState = selectionState.P1;
	
	private String[] links = { "", "" };			// for video help, linking to youtube
		
	private int px = 0;
	private int py = 0;
	private int x = 0;
	private int y = 0;
	private int w = 0;
	private int h = 0;
	
	public Setup(Settings setting, FFmpeg ffmpeg) {

		this.setting = setting;
		this.ffmpeg = ffmpeg;
		
	}
	
	public void setStreamPath(String p) {
		streamPath = p;
	}
	
	public void streamSelectionInterface() {
		JFrame frame = new JFrame("Stream URL");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 200);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 11, 414, 140);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Streaming File URL");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 14));
		lblNewLabel.setBounds(10, 11, 394, 30);
		panel.add(lblNewLabel);
		
		JTextField textField = new JTextField();
		textField.setFont(new Font("Dialog", Font.PLAIN, 12));
		textField.setBounds(10, 52, 295, 20);
		panel.add(textField);
		textField.setColumns(10);
		
		JButton browse = new JButton("Browse");
		browse.setFont(new Font("Dialog", Font.PLAIN, 12));
		browse.setBounds(315, 51, 89, 23);
		panel.add(browse);
		
		JButton launch = new JButton("Launch");
		launch.setFont(new Font("Dialog", Font.PLAIN, 12));
		launch.setBounds(315, 85, 89, 23);
		panel.add(launch);
		
		
		browse.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				jfc = new JFileChooser();
				jfc.setCurrentDirectory(new java.io.File("user.home"));
				jfc.setDialogTitle("Select your streaming file");
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if (jfc.showOpenDialog(browse) == JFileChooser.APPROVE_OPTION) {
					textField.setText(jfc.getSelectedFile().getAbsolutePath().replace("\\", "\\\\"));
				}
			}
			
		});
		
		launch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				while(true) {
					if(!textField.getText().equals("")) {
						if(textField.getText().contains(".flv")) {
							streamPath = textField.getText();
							setting.setStreamPath(streamPath);
							break;
						} else {
							JOptionPane.showMessageDialog(null, "Invalid extension, please select a .flv selection");
							return;
						}
					} else {
						JOptionPane.showMessageDialog(null, "Please fill out the box");
						return;
					}
				}
				frame.dispose();

			}
		});
		frame.setVisible(true);
	}
	
	public void setupInterface() {
		// preview
		f = new JFrame("Preview Panel");
		preview = new Preview();
		f.setLayout(null);
		f.setContentPane(preview);
		f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		f.setResizable(false);
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 640, 250);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(this);
		setLayout(null);
		
		instructions = new JLabel("Player One: Highlight the star position, watch video for example.");
		instructions.setHorizontalAlignment(SwingConstants.CENTER);
		instructions.setFont(new Font("Arial", Font.BOLD, 14));
		instructions.setBounds(12, 13, 598, 50);
		add(instructions);
		
		JButton capture = new JButton("Capture");
		capture.setFont(new Font("Arial", Font.BOLD, 14));
		capture.setBounds(12, 76, 130, 61);
		add(capture);
		
		JButton next = new JButton("Continue");
		next.setFont(new Font("Arial", Font.BOLD, 14));
		next.setBounds(482, 76, 130, 61);
		add(next);
		
		JButton videoHelp = new JButton("Video Help");
		videoHelp.setFont(new Font("Arial", Font.BOLD, 14));
		videoHelp.setBounds(156, 76, 314, 61);
		add(videoHelp);
		
		JLabel offsetting = new JLabel("Stock Offset");
		offsetting.setFont(new Font("Arial", Font.BOLD, 14));
		offsetting.setBounds(12, 150, 130, 40);
		add(offsetting);
		
		offsetSlider = new JSlider();
		offsetSlider.setValue(30);
		offsetSlider.setSnapToTicks(true);
		offsetSlider.setPaintTicks(true);
		offsetSlider.setPaintLabels(true);
		offsetSlider.setMinorTickSpacing(1);
		offsetSlider.setFont(new Font("Arial", Font.BOLD, 14));
		offsetSlider.setBounds(156, 150, 314, 40);
		add(offsetSlider);
		
		offsetSpinner = new JSpinner();
		offsetSpinner.setBounds(482, 150, 55, 40);
		add(offsetSpinner);
		
		JButton test = new JButton("Test");
		test.setFont(new Font("Arial", Font.BOLD, 12));
		test.setBounds(549, 150, 61, 40);
		add(test);
		
		offsetSlider.setEnabled(false);
		offsetSpinner.setEnabled(false);
		
		frame.setResizable(false);
		frame.setVisible(true);
		
		test.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				offset = offsetSlider.getValue();
				tx.clear();
				tx.addAll(xs);
				curPos = 0;
				testing = true;
			}
			
		});
		
		offsetSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				offsetSpinner.setValue(offsetSlider.getValue());
			}
			
		});
		
		offsetSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if((int) offsetSpinner.getValue() > offsetSlider.getMaximum() || (int) offsetSpinner.getValue() < offsetSlider.getMinimum()) {
					JOptionPane.showMessageDialog(null, "Please input a value between " + offsetSlider.getMinimum() + " and " + offsetSlider.getMaximum());
					return;
				}
				offsetSlider.setValue((int) offsetSpinner.getValue());
			}
			
		});
		
		next.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				switch(theState) {
				case P1:
					if(xm.size() == 0) return;
					for(int i = 0; i < xm.size(); i++) {
						AlfredColor[][] tmp = AveragePixels.averageColor(mm.get(i), xm.get(i), ym.get(i), wm.get(i), hm.get(i));
						setting.addPlayerOne(xm.get(i), ym.get(i), wm.get(i), hm.get(i), tmp);
					}
					instructions.setText("Player Two: Highlight the star position, watch video for example.");
					px = py = 0;
					xm.clear();
					ym.clear();
					wm.clear();
					hm.clear();
					mm.clear();
					drawn = false;
					locked = false;
					repaint();
					theState = selectionState.P2;
					break;
				case P2:
					if(xm.size() == 0) return;
					for(int i = 0; i < xm.size(); i++) {
						AlfredColor[][] tmp = AveragePixels.averageColor(mm.get(i), xm.get(i), ym.get(i), wm.get(i), hm.get(i));
						setting.addPlayerTwo(xm.get(i), ym.get(i), wm.get(i), hm.get(i), tmp);
					}
					instructions.setText("Player Two: Highlight the star position, watch video for example.");
					px = py = 0;
					xm.clear();
					ym.clear();
					wm.clear();
					hm.clear();
					mm.clear();
					drawn = false;
					locked = false;
					repaint();
					theState = selectionState.P3;
					break;
					
				case P3:
					if(xm.size() == 0) return;
					for(int i = 0; i < xm.size(); i++) {
						AlfredColor[][] tmp = AveragePixels.averageColor(mm.get(i), xm.get(i), ym.get(i), wm.get(i), hm.get(i));
						setting.addPlayerThree(xm.get(i), ym.get(i), wm.get(i), hm.get(i), tmp);
					}
					instructions.setText("Player Two: Highlight the star position, watch video for example.");
					px = py = 0;
					xm.clear();
					ym.clear();
					wm.clear();
					hm.clear();
					mm.clear();
					drawn = false;
					locked = false;
					repaint();
					theState = selectionState.P4;
					break;
				case P4:
					if(xm.size() == 0) return;
					for(int i = 0; i < xm.size(); i++) {
						AlfredColor[][] tmp = AveragePixels.averageColor(mm.get(i), xm.get(i), ym.get(i), wm.get(i), hm.get(i));
						setting.addPlayerFour(xm.get(i), ym.get(i), wm.get(i), hm.get(i), tmp);
					}
					setting.setOffset(offset);
					setting.setError(30);
					setting.setPatience(2);
					setting.setStreamPath(streamPath);
					frame.dispose();
					f.dispose();
					setting.save();
					break;
				default:
					break;
				
				}
			}
			
		});
		
		capture.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				try { 
					// take snapshot -> Program may hault till execution is complete
					ffmpeg.runCommand("ffmpeg -ss " + ffmpeg.getDuration(streamPath) + " -i \"" + streamPath + "\" -y -vframes 1 -q:v 2 \"setup.png\"");
					// grab screenshot
					ss = ImageIO.read(new File("setup.png"));
					WIDTH = ss.getWidth();
					HEIGHT = ss.getHeight();
					init();
					preview.setPreferredSize(new Dimension(WIDTH, HEIGHT));
					f.pack();
					//preview.setSize(ss.getWidth(), ss.getHeight());
					//f.setSize(ss.getWidth(), ss.getHeight());
					f.setVisible(true);
					
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			
		});
				
	}
	
	public void addNotify() {
		super.addNotify();
		if(thread == null) {
			thread = new Thread(this);
			preview.addKeyListener(this);
			preview.addMouseListener(this);
			thread.start();
		}
	}
	
	private void init() {
		image = new BufferedImage(WIDTH, HEIGHT,
				BufferedImage.TYPE_INT_ARGB);
		g = (Graphics2D) image.getGraphics();
		running = true;
	}
	
	private long redraw() {
		
		long t = System.currentTimeMillis();
		
		update();
		draw();
		preview.repaint();
		
		return System.currentTimeMillis() - t;
	}
	
	public void run() {
		
		init();
		
		while(running) {
			
			long durationMs = redraw();
			
			try {
				Thread.sleep(Math.max(0, FPS - durationMs));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void update () {
		if(curPos > 3) {
			testing = false;
		}
	}
	
	private void draw() {
		g.setColor(Color.black);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		if(ss != null) 
			g.drawImage(ss,0,0,null);

		switch(theState) {
		case P1:
			if(drawn) {
				g.setColor(Color.red);
				g.drawRect(x,y,w,h);
			}
			break;
		case P2:
			if(drawn) {
				g.setColor(Color.blue);
				g.drawRect(x,y,w,h);
			}
			break;
		case P3:
			if(drawn) {
				g.setColor(Color.green);
				g.drawRect(x, y, w, h);
			}
			
		case P4:
			if(drawn) {
				g.setColor(Color.orange);
				g.drawRect(x, y, w, h);
			}
			break;
		default:
			break;
			
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3 && !locked) {
			x = y = w = h = px = py = 0;
			drawn = false;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1 && !locked) {
			px = e.getX();
			py = e.getY();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1 && !locked) {
			int curx = e.getX();
			int cury = e.getY();
			x = px;
			y = py;
			w = Math.abs(px - curx);
			h = Math.abs(py - cury);
			drawn = true;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			if((x == 0 && y == 0) || (w == 0 && h == 0)) return;
			xm.add(x);
			ym.add(y);
			wm.add(w);
			hm.add(h);
			mm.add(image);
			locked = true;
			System.out.println("Added Image");
		}
	}
	
	class Preview extends JPanel {
		
		public Preview() {
			requestFocus(true);
			setFocusable(true);
		}
		
		@Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if(image != null)
            	g.drawImage(image, 0, 0, 1280, 720, null);
        }
		
	}

}
