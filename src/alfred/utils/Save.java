package alfred.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import alfred.main.Player;
import alfred.main.Screen;

public class Save implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private ArrayList<Player> playerOne;
	private ArrayList<Player> playerTwo;
	private String streamPath;
	private int offset;
	private int patience;
	private double error;
	
	
	public Save() {
		
		
	}
	
	public void setPlayerOne(ArrayList<Player> p) {
		if(playerOne != null)
			playerOne.clear();
		playerOne = new ArrayList<Player>();
		playerOne.addAll(p);
	}
	
	public void setPlayerTwo(ArrayList<Player> p) {
		if(playerTwo != null)
			playerTwo.clear();
		playerTwo = new ArrayList<Player>();
		playerTwo.addAll(p);
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public void setPatience(int p) {
		patience = p;
	}
	
	public void setError(double e) {
		error = e;
	}
	
	public void setStreamPath(String s) {
		streamPath = s;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public int getPatience() {
		return patience;
	}
	
	public double getError() {
		return error;
	}
	
	public String getStreamPath() {
		return streamPath;
	}
	
	public ArrayList<Player> getPlayerOne() {
		return playerOne;
	}
	
	public ArrayList<Player> getPlayerTwo() {
		return playerTwo;
	}
	
	public void save(ArrayList<Player> p1, ArrayList<Player> p2, int offset, int patience, double error, String sp) {
		this.setPlayerOne(p1);
		this.setPlayerTwo(p2);
		this.setOffset(offset);
		this.setPatience(patience);
		this.setError(error);
		this.setStreamPath(sp);
		try {
			DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
			// Get the date today using Calendar object.
			Date today = Calendar.getInstance().getTime();        
			// Using DateFormat format method we can create a string 
			// representation of a date with the defined format.
			String reportDate = df.format(today).replace(" ", "-").replace(":", "-").replace("\\", "-");
			File f = new File(reportDate + ".alfred");		// returns desktop location
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.close();	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public File load(String url) {
		try {
			Save save= null;
			File f = new File(url);
			if(f != null) {
				FileInputStream fis = new FileInputStream(f);
				ObjectInputStream ois = new ObjectInputStream(fis);
				save = (Save) ois.readObject();
				ois.close();
			}
			if(save == null) return null;
			this.setPlayerOne(save.getPlayerOne());
			this.setPlayerTwo(save.getPlayerTwo());
			this.setOffset(save.getOffset());
			this.setPatience(save.getPatience());
			this.setError(save.getError());
			this.setStreamPath(save.getStreamPath());
			return f;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public File load() {
		try {
			Save save= null;
			File f = new Selection().selectedLoad();		// opens a jfilechooser to select the file/object being loaded
			if(f != null) {
				FileInputStream fis = new FileInputStream(f);
				ObjectInputStream ois = new ObjectInputStream(fis);
				save = (Save) ois.readObject();
				ois.close();
			}
			if(save == null) return null;
			this.setPlayerOne(save.getPlayerOne());
			this.setPlayerTwo(save.getPlayerTwo());
			this.setOffset(save.getOffset());
			this.setPatience(save.getPatience());
			this.setError(save.getError());
			this.setStreamPath(save.getStreamPath());
			return f;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void update(File fs, ArrayList<Player> p1, ArrayList<Player> p2, int offset, int patience, double error, String sp) {
		// overwrite gathered save with new settings
		String path = fs.getPath();
		fs.delete();
		this.setPlayerOne(p1);
		this.setPlayerTwo(p2);
		this.setOffset(offset);
		this.setPatience(patience);
		this.setError(error);
		this.setStreamPath(sp);
		try {
			File f = new File(path);		// returns desktop location
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.close();	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
