package alfred.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/*
 * Just to interface with the actual FFmpeg application native to the system that it's on
 * Makes life a lot more simple.
 * 
 * ProcMon is also set to default on runCommand due to the amount of "build up"
 * that may occur on consecutive commands.
 */
public class FFmpeg {
	
	public void runCommand(String command) {
		
		try {
		
			ProcessBuilder builder = new 
					 ProcessBuilder(
							 "cmd", "/c", command);
			
			builder.redirectErrorStream(true);
			Process p = builder.start();
			
			ProcMon.create(p);
			while(!ProcMon.isComplete()) {
				System.out.print("");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public long getDuration(String streamPath) {
		
		if(streamPath != null) {
			try {
			
				long duration = 0;
				String n = "";
				
		        String arg = "ffmpeg -i " + "\"" + streamPath + "\"";
		        
				ProcessBuilder builder = new 
						 ProcessBuilder(
								 "cmd", "/c", arg);

				builder.redirectErrorStream(true);
				Process p = builder.start();
				
		        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		        String line;
		        while (true) {
		            line = r.readLine();
		            if (line == null) { break; }
		            if(line.contains("Duration"))
		            	n = line;
		        }	
				
				// remove front stuff
				n = n.replace("Duration: ", "");
				n = n.replaceAll("\\s", "");
				// remove after duration stuff
				boolean read = true;
				String build = "";
				for(int i = 0; i < n.length(); i++) {
					if(n.charAt(i) ==',') read = false;
					if(read) {
						build = build + n.charAt(i);
					}
				}
				//handle times hh:mm:ss:ms
				String[] parses = build.split(":");
				String tmp = "" + parses[2].charAt(0) + parses[2].charAt(1);
				int s = Integer.parseInt(tmp);
				int m = Integer.parseInt(parses[1]);
				int h = Integer.parseInt(parses[0]);
				duration = (h * 3600) + (m * 60) + s;
				return duration;
				
			} catch (Exception e) {
				e.printStackTrace();
				
				return -1;
			}
			
		} else
			return -1;
		
	}
	
}
