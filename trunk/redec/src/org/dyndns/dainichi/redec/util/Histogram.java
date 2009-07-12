package org.dyndns.dainichi.redec.util;

import java.util.ArrayList;
import java.util.Arrays;

import org.dyndns.dainichi.redec.applet.ReDec;

import processing.core.PApplet;
import processing.core.PImage;

public class Histogram {
	private ReDec parent;
	private int width;
	private int height;
	private int x,y;
	//private Statistics stats;
	private boolean updated = false;
	private ArrayList<Float> data;

	public Histogram(int x, int y,int w,int h,ReDec theParent)
	{
		this.x = x;
		this.y = y;
		parent = theParent;
		width = w;
		height = h;
		data = new ArrayList<Float>();
		
		clear();
	}
	public void clear() {
		data.clear();
		
	}
	public void increment(float value,float m)
	{
	    updated = false;
		data.add(value);
	}
	public void drawHist(PImage img,int color,boolean mode,float scale,int colorBand)
	{
		float[]data = Statistics.process(parent, img,colorBand);
		float mean = data[Statistics.MEAN];
		float median = data[Statistics.MEDIAN];
		parent.pushStyle();
		parent.colorMode(PApplet.HSB,width,1,1,1);
		if(!mode)
			parent.stroke(color);
		for(int i = 1; i < width;i++)
		{
			if(mode)
			{
				parent.stroke(parent.color(i,1,1));
			}
			parent.line(x+i, 
					y+height,
					x+i,
					y+height-PApplet.max(PApplet.min((data[i+Statistics.BINS]*height/data[Statistics.MAX]),
							height),
							0));
		}
		parent.stroke(0);
		//parent.fill(parent.g.);
		parent.rect(x, y, width, height);
		parent.stroke(0xff00ff00);
		parent.line(x+mean, y, x+mean, y+height);
		parent.stroke(0xff0000ff);
		parent.line(x+median, y, x+median, y+height);
		parent.fill(0xffffffff);
		parent.text("median:" + median,x+5,y+15);
		parent.text("mean:" + mean,x+5,y+30);
		parent.popStyle();
	}
	float[]blah = new float[]{0,0,0}; 
	
	
	
}
