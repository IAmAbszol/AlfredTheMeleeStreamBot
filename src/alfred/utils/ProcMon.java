package alfred.utils;

/*
 * Thanks to jtahlborn for the code, stackoverflow http://stackoverflow.com/questions/11510409/how-to-monitor-external-process-ran-by-processbuilder
 * 
 * This class simply returns when a process has finished.
 */
public class ProcMon implements Runnable {

	  private Process _proc = null;
	  private volatile static boolean _complete;

	  public static boolean isComplete() { return _complete; }

	  public ProcMon(Process p) {
		  _proc = p;
		  _complete = false;
	  }
	  
	  public static ProcMon create(Process proc) {
		    ProcMon procMon = new ProcMon(proc);
		    Thread t = new Thread(procMon);
		    t.start();
		    return procMon;
		  }
	  
	  public void run() {
	    try {
			_proc.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    _complete = true;
	  }

}