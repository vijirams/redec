package org.dyndns.dainichi.redec.util.threading;

import org.dyndns.dainichi.redec.applet.ReDec;

import processing.serial.Serial;
import JMyron.JMyron;

/**
 * Image capture has been moved to this thread to help keep interface smooth.
 * @author dejagerd
 *
 */
public class ImageAquisition extends Thread {
//	private static final int	FRAMES	= 3;
	private boolean						run;
	private int[]				image1;
	private JMyron						cam;
	byte[][]					R		= { { (byte) 10 }};//, { 0 }, { 0 } };
	byte[][]					G		= { { (byte) 10 }};
	byte[][]					B		= { { (byte) 10  }};
	private ReDec				parent;
	private boolean				pause;
	private Serial				serial;

	/**
	 * Create a new ImageAquasition thread.
	 * @param parent parent reference.
	 * @param width width
	 * @param height height
	 */
	public ImageAquisition(ReDec parent, int width, int height) {

		this.parent = parent;
		image1 = new int[width * height];
		if(parent.standalone == false) {
			serial = new Serial(parent, Serial.list()[1], 38400);
		}
		cam = new JMyron();
		cam.start(width, height);

		cam.findGlobs(0);
		cam.adaptivity(0f);

		//System.out.println("cam forced to:"+cam.getForcedWidth()+","+cam.getForcedHeight());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		super.run();

		while (isRun()) {
			// Check if should wait
			synchronized (this) {
				while (pause) {
					try {
						wait();
					} catch (Exception e) {}
				}
			}
			if(parent.standalone == false){
			try {
				serial.write(R[0]);
				sleep(1);
				serial.write(G[0]);
				sleep(1);
				serial.write(B[0]);
				sleep(1);

			} catch (Exception e) {
				// TODO Auto-generated catch block

			}
			}

			cam.cameraImageCopy(image1);
			cam.update();
		}
		if(parent.standalone == false) {
			try {
				serial.write("\0\0\0");
			} catch (Exception e) {
				// TODO Auto-generated catch block

			}
		}
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Thread#start()
	 */
	@Override
	public synchronized void start()
	{
		// TODO Auto-generated method stub
		setRun(true);

		super.start();
	}

	/**
	 * returns the captured images
	 * @param i image to return
	 * @return
	 */
	public synchronized int[] getImage()
	{
		return image1;
	}

	/**
	 * Pause
	 */
	public void pause()
	{
		synchronized (this) {
			pause = true;
		}
	}

	/**
	 * Unpause.
	 */
	public void unpause()
	{
		synchronized (this) {
			pause = false;
			notify();
		}
	}

	/**
	 * @param run the run to set
	 */
	public void setRun(boolean run)
	{
		this.run = run;
	}

	/**
	 * @return the run
	 */
	public boolean isRun()
	{
		return run;
	}


	/**
	 * @return the cam object
	 */
	public JMyron getCam()
	{
		return cam;
	}

}
