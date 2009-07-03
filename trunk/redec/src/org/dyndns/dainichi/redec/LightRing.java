package org.dyndns.dainichi.redec;

import java.util.Arrays;

import processing.serial.Serial;

public class LightRing extends Thread
{
	byte[] values;
	byte[] pvalues;
	private ReDec	parent;
	private Serial	serial;
	boolean run = false;
	private boolean	pause;
	public LightRing(ReDec parent)
	{
		this.parent = parent;

		serial = new Serial(parent,Serial.list()[1],38400);
		values = new byte[]{0,0,0};

	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		super.run();


		while(run)
		{
			// Check if should wait
			synchronized (this) {
				while (pause) {
					try {
						wait(10);
					} catch (Exception e) {
					}
				}
			}
			if(values == null)
			{
				values = new byte[]{0,0,0};
			}
			serial.write(values);
			serial.readString();
			pause = true;

		}
		serial.write(0);
		serial.write(0);
		serial.write(0);
		try
		{
			sleep(100);
			serial.stop();
			sleep(500);
		} catch (InterruptedException e)
		{
		}

	}

	@Override
	public synchronized void start()
	{
		// TODO Auto-generated method stub
		System.out.println(Arrays.toString(serial.list()));
		run = true;
		super.start();
	}
	public synchronized void set(int[] values)
	{
		pvalues = this.values;
		for(int i =0; i < 3;i++)
		{
			this.values[i] = (byte) values[i];
		}
		unpause();
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
