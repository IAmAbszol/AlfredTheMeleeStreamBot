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
	private String streamPath;
	private int offset;
	private int patience;
	private double error;
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
		streamPath = null;
		offset = 0;
		patience = 0;
		error = 0;
		
	}
	
	public void setSaveFile(File f) {
		saveFile = f;
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
	
	public void setPatience(int p) {
		patience = p;
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
	
	public void addPlayerOne(int x, int y, int w, int h, AlfredColor[][] color) {
		
		Player p = new Player();
		p.setScreen(new int[] { x,y,w,h }, color);
		playerOne.add(p);
	}
	
	public void addPlayerColorOne(AlfredColor[][] color) {
		Player p = new Player();
		p.setScreenColor(color);
		playerOne.add(p);
	}
	
	public void addPlayerTwo(int x, int y, int w, int h, AlfredColor[][] color) {
		
		Player p = new Player();
		p.setScreen(new int[] { x,y,w,h }, color);
		playerTwo.add(p);
	}
	
	public void addPlayerColorTwo(AlfredColor[][] color) {
		Player p = new Player();
		p.setScreenColor(color);
		playerTwo.add(p);
	}
	
	public void save() {
		save = new Save();
		save.save(playerOne, playerTwo, offset, patience, error, streamPath);
		System.out.println(streamPath);
	}
	
	public void loadSettings(String s) {
		save = new Save();
		saveFile = save.load(s);
		this.setPlayerOne(save.getPlayerOne());
		this.setPlayerTwo(save.getPlayerTwo());
		this.setOffset(save.getOffset());
		this.setPatience(save.getPatience());
		this.setError(save.getError());
		this.setStreamPath(save.getStreamPath());
	}
	
	public void loadSettings() {
		save = new Save();
		saveFile = save.load();
		this.setPlayerOne(save.getPlayerOne());
		this.setPlayerTwo(save.getPlayerTwo());
		this.setOffset(save.getOffset());
		this.setPatience(save.getPatience());
		this.setError(save.getError());
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
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		logThread.start();
	}
	
	public void log(String n) {
		log.append(n + "\n");
		if(ta != null) {
			ta.setText(log.toString());
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
	
	public void settingsInterface() {
		frame = new JFrame("Settings Interface");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBounds(100, 100, 530, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel settingsLabel = new JLabel("Settings");
		settingsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		settingsLabel.setFont(new Font("Arial", Font.BOLD, 18));
		settingsLabel.setBounds(12, 13, 488, 40);
		contentPane.add(settingsLabel);
		
		JButton viewLogs = new JButton("View Logs");
		viewLogs.setFont(new Font("Arial", Font.BOLD, 14));
		viewLogs.setBounds(12, 66, 150, 40);
		contentPane.add(viewLogs);
		
		JLabel lblNewLabel = new JLabel("Offset");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel.setFont(new Font("Arial", Font.BOLD, 14));
		lblNewLabel.setBounds(174, 66, 198, 40);
		contentPane.add(lblNewLabel);
		
		offsetField = new JTextField();
		offsetField.setHorizontalAlignment(SwingConstants.CENTER);
		offsetField.setFont(new Font("Arial", Font.BOLD, 14));
		offsetField.setBounds(384, 75, 116, 22);
		contentPane.add(offsetField);
		offsetField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Patience");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_1.setFont(new Font("Arial", Font.BOLD, 14));
		lblNewLabel_1.setBounds(174, 119, 198, 40);
		contentPane.add(lblNewLabel_1);
		
		patienceField = new JTextField();
		patienceField.setHorizontalAlignment(SwingConstants.CENTER);
		patienceField.setFont(new Font("Arial", Font.BOLD, 14));
		patienceField.setBounds(384, 128, 116, 22);
		contentPane.add(patienceField);
		patienceField.setColumns(10);
		
		JLabel lblError = new JLabel("Error");
		lblError.setHorizontalAlignment(SwingConstants.RIGHT);
		lblError.setFont(new Font("Arial", Font.BOLD, 14));
		lblError.setBounds(174, 172, 198, 40);
		contentPane.add(lblError);
		
		errorField = new JTextField();
		errorField.setHorizontalAlignment(SwingConstants.CENTER);
		errorField.setFont(new Font("Arial", Font.BOLD, 14));
		errorField.setBounds(384, 181, 116, 22);
		contentPane.add(errorField);
		errorField.setColumns(10);
		
		JButton changeStream = new JButton("Change Stream");
		changeStream.setFont(new Font("Arial", Font.BOLD, 14));
		changeStream.setBounds(12, 119, 150, 40);
		contentPane.add(changeStream);
		
		JButton updateButton = new JButton("Update");
		updateButton.setFont(new Font("Arial", Font.BOLD, 14));
		updateButton.setBounds(12, 172, 150, 40);
		contentPane.add(updateButton);
		
		frame.setResizable(false);
		frame.setVisible(true);
		
		offsetField.setText(""+offset);
		patienceField.setText(""+patience);
		errorField.setText(""+error);
		
		viewLogs.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showLog();
			}
			
		});
		
		changeStream.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				streamSelectionInterface();
			}
			
		});
		
		updateButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				offset = Integer.parseInt(offsetField.getText());
				patience = Integer.parseInt(patienceField.getText());
				error = Double.parseDouble(errorField.getText());
				save = new Save();
				save.update(saveFile, playerOne, playerTwo, offset, patience, error, streamPath);
				
			}
			
		});
		
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
