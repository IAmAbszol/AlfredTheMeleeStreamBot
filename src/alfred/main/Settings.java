package alfred.main;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import alfred.utils.AlfredColor;
import alfred.utils.Save;

public class Settings {

	private ArrayList<Player> playerOne;
	private ArrayList<Player> playerTwo;
	private ArrayList<Player> playerThree;
	private ArrayList<Player> playerFour;
	private String streamPath;
	private int offset;
	private File saveFile;
	
	// logging
	private StringBuffer log = new StringBuffer("");
	private JTextArea ta;
	private JFrame textFrame;
	private Thread logThread;
	private boolean killThread = false;
	private String threadLog = "";
	
	// interfacing
	private JFileChooser jfc;
	private JFrame frame;
	private JPanel contentPane;
	private JTextField offsetField;
	private JTextField patienceField;
	private JTextField errorField;
	
	private Save save;
	
	public Settings() {
		
		playerOne = new ArrayList<Player>();
		playerTwo = new ArrayList<Player>();
		playerThree = new ArrayList<Player>();
		playerFour = new ArrayList<Player>();
		streamPath = null;
		offset = 0;
		
	}
	
	public Save getSave() {
		return save;
	}
	
	public void setSaveFile(File f) {
		saveFile = f;
	}
	
	public File getSaveFile() {
		return saveFile;
	}
	
	public void setStreamPath(String path) {
		streamPath = path;
	}
	
	public String getStreamPath() {
		return streamPath;
	}
	
	public void setOffset(int o) {
		offset = o;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public void setPlayerOne(ArrayList<Player> p) {
		playerOne = p;
	}
	
	public ArrayList<Player> getPlayerOne() {
		return playerOne;
	}
	
	public void setPlayerTwo(ArrayList<Player> p) {
		playerTwo = p;
	}
	
	public ArrayList<Player> getPlayerTwo() {
		return playerTwo;
	}
	
	public void setPlayerThree(ArrayList<Player> p) {
		playerThree = p;
	}
	
	public ArrayList<Player> getPlayerThree() {
		return playerThree;
	}
	
	public void setPlayerFour(ArrayList<Player> p) {
		playerFour = p;
	}
	
	public ArrayList<Player> getPlayerFour() {
		return playerFour;
	}
	
	public void addPlayerOne(int x, int y, int w, int h, AlfredColor[][] color) {
		
		Player p = new Player();
		p.setScreen(new int[] { x,y,w,h }, color);
		p.setPatience(2);
		p.setError(30);
		playerOne.add(p);
	}
	
	public void addPlayerColorOne(AlfredColor[][] color) {
		Player p = new Player();
		p.setScreenColor(color);
		p.setPatience(2);
		p.setError(30);
		playerOne.add(p);
	}
	
	public void addPlayerTwo(int x, int y, int w, int h, AlfredColor[][] color) {
		Player p = new Player();
		p.setScreen(new int[] { x,y,w,h }, color);
		p.setPatience(2);
		p.setError(30);
		playerTwo.add(p);
	}
	
	public void addPlayerThree(int x, int y, int w, int h, AlfredColor[][] color) {
		Player p = new Player();
		p.setScreen(new int[] { x,y,w,h }, color);
		p.setPatience(2);
		p.setError(30);
		playerThree.add(p);
	}
	
	public void addPlayerFour(int x, int y, int w, int h, AlfredColor[][] color) {
		Player p = new Player();
		p.setScreen(new int[] { x,y,w,h }, color);
		p.setPatience(2);
		p.setError(30);
		playerFour.add(p);
	}
	
	public void addPlayerColorTwo(AlfredColor[][] color) {
		Player p = new Player();
		p.setScreenColor(color);
		p.setPatience(2);
		p.setError(30);
		playerTwo.add(p);
	}
	
	public void addPlayerColorThree(AlfredColor[][] color) {
		Player p = new Player();
		p.setScreenColor(color);
		p.setPatience(2);
		p.setError(30);
		playerThree.add(p);
	}
	
	public void addPlayerColorFour(AlfredColor[][] color) {
		Player p = new Player();
		p.setScreenColor(color);
		p.setPatience(2);
		p.setError(30);
		playerFour.add(p);
	}
	
	public void save() {
		save = new Save();
		save.save(playerOne, playerTwo, playerThree, playerFour, offset, streamPath);
		System.out.println(streamPath);
	}
	
	public void loadSettings(String s) {
		save = new Save();
		saveFile = save.load(s);
		this.setPlayerOne(save.getPlayerOne());
		this.setPlayerTwo(save.getPlayerTwo());
		this.setPlayerThree(save.getPlayerThree());
		this.setPlayerFour(save.getPlayerFour());
		this.setOffset(save.getOffset());
		this.setStreamPath(save.getStreamPath());
	}
	
	public void loadSettings() {
		save = new Save();
		saveFile = save.load();
		this.setPlayerOne(save.getPlayerOne());
		this.setPlayerTwo(save.getPlayerTwo());
		this.setPlayerThree(save.getPlayerThree());
		this.setPlayerFour(save.getPlayerFour());
		this.setOffset(save.getOffset());
		this.setStreamPath(save.getStreamPath());
	}
	
	public void setupSystemLog() {
		logThread = new Thread(new Runnable() {
			public void run() {
				while(!killThread) {
					if(!threadLog.equals("")) {
						System.out.println(threadLog);
						threadLog = "";
					}
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				killThread = false;
			}
		});
		logThread.start();
	}
	
	public void killSystemLog() {
		killThread = true;
	}
	
	public void log(String n) {
		if(ta != null) {
			ta.append(n + "\n");
			ta.setCaretPosition(ta.getDocument().getLength());
		}
		threadLog = n;
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
	
	public void showLog() {
		textFrame = new JFrame("Logs");
		textFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		textFrame.setBounds(100, 100, 450, 300);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		textFrame.setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 13, 408, 227);
		contentPane.add(scrollPane);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		ta = new JTextArea();
		ta.setEditable(false);
		scrollPane.setViewportView(ta);
		ta.setText(log.toString());
		ta.setBounds(0,0,640,480);
		textFrame.add(scrollPane);
		textFrame.setResizable(false);
		textFrame.setVisible(true);
	}
	
}
