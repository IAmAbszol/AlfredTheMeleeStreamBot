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
	private ArrayList<Player> playerThree;
	private ArrayList<Player> playerFour;
	private String streamPath;
	private int offset;
	
	
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
	
	public void setPlayerThree(ArrayList<Player> p) {
		if(playerThree != null)
			playerThree.clear();
		playerThree = new ArrayList<Player>();
		playerThree.addAll(p);
	}
	
	public void setPlayerFour(ArrayList<Player> p) {
		if(playerFour != null)
			playerFour.clear();
		playerFour = new ArrayList<Player>();
		playerFour.addAll(p);
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public void setStreamPath(String s) {
		streamPath = s;
	}
	
	public int getOffset() {
		return offset;
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
	
	public ArrayList<Player> getPlayerThree() {
		return playerThree;
	}
	
	public ArrayList<Player> getPlayerFour() {
		return playerFour;
	}
	
	public void save(ArrayList<Player> p1, ArrayList<Player> p2, ArrayList<Player> p3, ArrayList<Player> p4, int offset, String sp) {
		this.setPlayerOne(p1);
		this.setPlayerTwo(p2);
		this.setPlayerThree(p3);
		this.setPlayerFour(p4);
		this.setOffset(offset);
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
			this.setPlayerThree(save.getPlayerThree());
			this.setPlayerFour(save.getPlayerFour());
			this.setOffset(save.getOffset());
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
			this.setPlayerThree(save.getPlayerThree());
			this.setPlayerFour(save.getPlayerFour());
			this.setOffset(save.getOffset());
			this.setStreamPath(save.getStreamPath());
			return f;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void update(File fs, ArrayList<Player> p1, ArrayList<Player> p2, ArrayList<Player> p3, ArrayList<Player> p4, int offset, String sp) {
		// overwrite gathered save with new settings
		String path = fs.getPath();
		fs.delete();
		this.setPlayerOne(p1);
		this.setPlayerTwo(p2);
		this.setPlayerThree(p3);
		this.setPlayerFour(p4);
		this.setOffset(offset);
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
