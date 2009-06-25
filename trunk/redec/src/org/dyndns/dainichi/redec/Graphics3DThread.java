package org.dyndns.dainichi.redec;

import processing.core.PGraphics;

public class Graphics3DThread extends Thread
{
	ReDec		parent;
	PGraphics[]	devices;
	boolean		running;
	boolean		paused;

	public Graphics3DThread(ReDec parent, PGraphics[] devices)
	{
		// TODO Auto-generated constructor stub
		super();
		this.parent = parent;
		this.devices = devices;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#destroy()
	 */
	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub
		super.destroy();
	}

	public void fillOffscreen(PGraphics device)
	{
		if (parent.frameCount <= 16)
		{

			device.camera(160, 300, 300, 160, 120, 128, 0, 0, -1);
		}
		device.background(0xff000000);
		device.ambient(255, 255, 255);
		device.beginDraw();
		device.colorMode(parent.HSB, 255);
		int dev = device.equals(parent.g6) ? 6 : device.equals(parent.g5) ? 5 : device.equals(parent.g4) ? 4 : device.equals(parent.g3) ? 3 : device.equals(parent.g2) ? 2 : device
				.equals(parent.g1) ? 1 : 0;

		for (int y = 0; y < parent.CAM_HEIGHT; y++)
		{

			while(paused)
			{
				;
			}
			for (int x = 0; x < parent.CAM_WIDTH; x++)
			{
				while(paused)
				{
					;
				}
				int pixel = parent.bg1.pixels[x + y * parent.CAM_WIDTH];
				float z = 0;
				switch (dev)
				{
				case 1:
					z = parent.hue(pixel);
					break;
				case 2:
					z = parent.saturation(pixel);
					break;
				case 3:
					z = parent.brightness(pixel);
					break;
				case 4:
					z = parent.red(pixel);
					break;
				case 5:
					z = parent.green(pixel);
					break;
				case 6:
					z = parent.blue(pixel);
					break;
				}
				if (x >= parent.pmouseX % parent.CAM_WIDTH && x <= parent.pmouseX % parent.CAM_WIDTH + parent.STEP_X)
				{
					device.stroke(pixel);
				} else
				{
					device.stroke(pixel, 0x40);
				}
				device.point(x, y, z);
			}

		}
		// device.fill(0x00ffffff);
		device.noFill();
		device.stroke(-1);
		device.beginShape();
		device.vertex(parent.pmouseX % parent.CAM_WIDTH, 0, 0);
		device.vertex(parent.pmouseX % parent.CAM_WIDTH, parent.CAM_HEIGHT, 0);
		device.vertex(parent.pmouseX % parent.CAM_WIDTH, parent.CAM_HEIGHT, 256);
		device.vertex(parent.pmouseX % parent.CAM_WIDTH, 0, 256);
		device.endShape(parent.CLOSE);

		device.stroke(0xffff0000);
		device.line(0, 0, 0, 1000, 0, 0);
		device.stroke(0xff00ff00);
		device.line(0, 0, 0, 0, 1000, 0);
		device.stroke(0xff0000ff);
		device.line(0, 0, 0, 0, 0, 1000);
		device.endDraw();
	}

	public synchronized void quit()
	{
		running = false;
		interrupt();
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
		while (running)
		{
			for (PGraphics p : devices)
			{
				fillOffscreen(p);
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
		super.start();
		running = true;
		paused = false;
	}

}
