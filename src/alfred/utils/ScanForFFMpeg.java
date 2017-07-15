package alfred.utils;

import java.io.InputStreamReader;
import java.util.Map;

public class ScanForFFMpeg {
	
	public static boolean scan() {
		try {
			
			ProcessBuilder pb = new ProcessBuilder("CMD", "/C", "SET");
		    Map<String, String> env = pb.environment();
		    Process p = pb.start();
		    InputStreamReader isr = new InputStreamReader(p.getInputStream());
		    char[] buf = new char[1024];
		    while (!isr.ready()) {
		        ;
		    }
		    boolean isThere = false;
		    String line = null;
		    while (isr.read(buf) != -1) {
		    	line = new String(buf).toLowerCase();
		    	if(line.contains("ffmpeg")) isThere = true;
		    	if(isThere) break;
		    }
		    
		    return isThere;
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
