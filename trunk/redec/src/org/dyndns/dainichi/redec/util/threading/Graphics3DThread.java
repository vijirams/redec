package org.dyndns.dainichi.redec.util.threading;

import org.dyndns.dainichi.redec.applet.ReDec;

import processing.core.PGraphics;
import processing.core.PImage;

public class Graphics3DThread extends Thread
{
	ReDec		parent;
	PGraphics	device;
	boolean		running;
	boolean	 	pause = false;
	PImage	img;

	public Graphics3DThread(ReDec parent, PGraphics device,PImage src)
	{
		// TODO Auto-generated constructor stub
		super();
		img = src;
		this.parent = parent;
		this.device = device;
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
		int dev = device.equals(parent.g6) ? 6 : device.equals(parent.g5) ? 5 : device.equals(parent.g4) ? 4 : device.equals(parent.g3) ? 3 : device.equals(parent.g2) ? 2 : device
				.equals(parent.g1) ? 1 : 0;
		int[][] pixels = new int[7][img.pixels.length];
		while (running)
		{

			if (parent.frameCount <= 16)
			{

				device.camera(160, 300, 300, 160, 120, 128, 0, 0, -1);
			}
			device.background(0xff000000);
			device.ambient(255, 255, 255);
			device.beginDraw();
			device.colorMode(parent.HSB, 255);
			int[] img2;
			synchronized (img) {
				img2 = img.get().pixels;
			}
			parent.rgbToHsb(img2, pixels);
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
			for (int y = 0; y < parent.CAM_HEIGHT; y++)
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
				for (int x = 0; x < parent.CAM_WIDTH; x++)
				{
					int i = x + y * parent.CAM_WIDTH;
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
					float z = 0;
					z = pixels[dev][i];
					if(pixels[0][i]>0){
						if (x >= parent.pmouseX % parent.CAM_WIDTH && x <= parent.pmouseX % parent.CAM_WIDTH + parent.STEP_X)
						{
							device.stroke(-1);
						} else
						{
							device.stroke(pixels[1][i],pixels[2][i],pixels[3][i]);
						}
						device.point(x, y, z);
					}
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
			device.endShape(ReDec.CLOSE);
			device.stroke(-1);
			device.beginShape();
			device.vertex(parent.pmouseX % parent.CAM_WIDTH, 0, 0);
			device.vertex(parent.pmouseX % parent.CAM_WIDTH, parent.CAM_HEIGHT, 0);
			device.vertex(parent.pmouseX % parent.CAM_WIDTH, parent.CAM_HEIGHT, 256);
			device.vertex(parent.pmouseX % parent.CAM_WIDTH, 0, 256);
			device.endShape(ReDec.CLOSE);
			device.stroke(0xffff0000);
			device.line(0, 0, 0, 1000, 0, 0);
			device.stroke(0xff00ff00);
			device.line(0, 0, 0, 0, 1000, 0);
			device.stroke(0xff0000ff);
			device.line(0, 0, 0, 0, 0, 1000);
			device.endDraw();

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
