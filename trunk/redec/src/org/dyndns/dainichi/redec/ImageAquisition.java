package org.dyndns.dainichi.redec;

import processing.serial.Serial;
import JMyron.JMyron;

public class ImageAquisition extends Thread {
	private static final int	FRAMES	= 3;
	private int					index;
	boolean						run;
	private int[]				image1;
	private int[]				image2;
	private int[]				image3;
	JMyron						cam;
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

	public ImageAquisition(ReDec parent, int width, int height) {

		this.parent = parent;
		average = new AveragedImage(parent, width, height, FRAMES);
		index = 0;
		image1 = new int[width * height];
		image2 = new int[width * height];
		image3 = new int[width * height];
		serial = new Serial(parent, Serial.list()[1], 38400);
		cam = new JMyron();
		cam.start(width, height);
		cam.findGlobs(0);
		cam.adaptivity(0);

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

		while (run) {
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
				cam.update();
			}


			switch (index)
			{
			case 0:
				cam.cameraImageCopy(image1);
				break;
			case 1:
				cam.cameraImageCopy(image2);
				break;
			case 2:
				cam.cameraImageCopy(image3);
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
		run = true;

		super.start();
	}

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

	public void pause()
	{
		synchronized (this) {
			pause = true;
		}
	}

	public void unpause()
	{
		synchronized (this) {
			pause = false;
			notify();
		}
	}

}
