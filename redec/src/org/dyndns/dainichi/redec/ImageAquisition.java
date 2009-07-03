package org.dyndns.dainichi.redec;

import JMyron.JMyron;

public class ImageAquisition extends Thread
{
	private static final int FRAMES = 3;
	private int index;
	boolean run;
	private int[][] images;
	JMyron cam;
	private LightRing lights;
	//	private int[][] lighting = {
	//			{255,255,0},
	//			{0,255,255},
	//			{255,0,255}};
	private int val = 5;
	private int[][] lighting = {
			{val,val,val},
			{val,val,val},
			{val,val,val}};
	private ReDec	parent;
	private boolean	pause;
	public ImageAquisition(ReDec parent,int width, int height)
	{

		this.parent = parent;
		index = 0;
		images = new int[FRAMES][width*height];
		cam = new JMyron();
		cam.start(width, height);
		cam.findGlobs(0);
		lights = new LightRing(parent);
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		super.run();
		while(run)
		{
			// Check if should wait
			synchronized (this)
			{
				while (pause)
				{
					try
					{
						wait();
					} catch (Exception e)
					{
					}
				}
			}
			cam.update();
			yield();
			lights.set(lighting[index]);
			cam.cameraImageCopy(images[index]);
			yield();
			index = (index+1)% FRAMES;
		}
		lights.run = false;
		try
		{
			lights.join(1000);
		} catch (InterruptedException e)
		{

		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#start()
	 */
	@Override
	public synchronized void start()
	{
		// TODO Auto-generated method stub
		run = true;
		lights.start();
		super.start();
	}
	public synchronized int[][] getImages()
	{
		return images;
	}
	public void pause(){
		synchronized (this)
		{
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
