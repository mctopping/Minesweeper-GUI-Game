
public class Minesweeper implements Runnable {

	public GUI gui = new GUI();
	
	public static void main(String[] args) {
		
		new Thread(new Minesweeper()).start();
	}
	
	public void run() 
	{
	    long lastLoopTime = System.nanoTime();
	    
	    final double TARGET_FPS = 60.0;
	    final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;

	    while (true) 
	    {
	        long now = System.nanoTime();
	        lastLoopTime = now;

	        gui.repaint();

	        // Cap the FPS
	        try 
	        {
	            long sleepTime = (long) (lastLoopTime - System.nanoTime() + TARGET_TIME_BETWEEN_RENDERS) / 1000000;
	            
	            if (sleepTime > 0) 
	            {
	                Thread.sleep(sleepTime);
	            }
	            
	        } 
	        catch (InterruptedException e) 
	        {
	            e.printStackTrace();
	        }
	    }
	}
}
