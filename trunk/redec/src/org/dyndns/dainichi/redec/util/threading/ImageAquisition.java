package org.dyndns.dainichi.redec.util.threading;

import org.dyndns.dainichi.redec.applet.ReDec;
import org.dyndns.dainichi.redec.util.objects.AveragedImage;

import processing.serial.Serial;
import JMyron.JMyron;

/**
 * Image capture has been moved to this thread to help keep interface smooth.
 * @author dejagerd
 *
 */
public class ImageAquisition extends Thread {
	private static final int	FRAMES	= 3;
	private int					index;
	private boolean						run;
	private int[]				image1;
	private int[]				image2;
	private int[]				image3;
	private JMyron						cam;
	byte[][]					R		= { { (byte) 10 }};//, { 0 }, { 0 } };
	byte[][]					G		= { { (byte) 10 }};
	byte[][]					B		= { { (byte) 10 } };
	// private byte[][] lighting = { { (byte) 255, (byte) 255, 0 }, { 0, (byte)
	// 255, (byte) 255 }, { (byte) 255, 0, (byte) 255 } };
	private int					val		= 10;
	// private int[][] lighting = {
	// {8,10,8}};
	private ReDec				parent;
	private boolean				pause;
	private Serial				serial;
	AveragedImage				average;

	/**
	 * Create a new ImageAquasition thread.
	 * @param parent parent reference.
	 * @param width width
	 * @param height height
	 */
	public ImageAquisition(ReDec parent, int width, int height) {

		this.parent = parent;
		average = new AveragedImage(parent, width, height, FRAMES);
		index = 0;
		image1 = new int[width * height];
		image2 = new int[width * height];
		image3 = new int[width * height];
		serial = new Serial(parent, Serial.list()[1], 38400);
		setCam(new JMyron());
		getCam().start(width, height);
		getCam().findGlobs(0);
		getCam().adaptivity(0);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		// TODO Auto-generated method stub
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

			try {
				serial.write(R[0]);
				sleep(1);
				serial.write(G[0]);
				sleep(1);
				serial.write(B[0]);
				sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (!parent.freeze) {
				getCam().update();
			}


			switch (index)
			{
			case 0:
				getCam().cameraImageCopy(image1);
				break;
			case 1:
				getCam().cameraImageCopy(image2);
				break;
			case 2:
				getCam().cameraImageCopy(image3);
				break;
			}
			index = (index + 1) % FRAMES;

		}

		serial.write("\0\0\0");

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
	public synchronized int[] getImage(int i)
	{
		switch (i)
		{
		case 0:
			return image1;
		case 1:
			return image2;
		}
		return image3;
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
	 * @param cam the cam to set
	 */
	public void setCam(JMyron cam)
	{
		this.cam = cam;
	}

	/**
	 * @return the cam
	 */
	public JMyron getCam()
	{
		return cam;
	}

}
