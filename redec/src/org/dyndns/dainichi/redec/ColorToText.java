package org.dyndns.dainichi.redec;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class ColorToText {
	public int color;
	private ReDec parent;
	private String s;
	public PFont font;

	public ColorToText(ReDec parent, int c, String s) {

		this.parent = parent;
		this.color = c;
		this.s = s;
		font = parent.loadFont("CenturySchoolbook-16.vlw");
		// parent.textFont(font);

	}

	public boolean analyse(PImage src, int x, int y, int w, int height, int color, float th) {
		// parent.textFont(font);
		int c = average(src.get(x, y, w, height));
		parent.stroke(0,0xff);
		parent.fill(c);
//		float h = parent.red(color) - parent.red(c);
//		float s = parent.green(color) - parent.green(c);
//		float b = parent.blue(color) - parent.blue(c);

		 float h = Color.difference(parent.hue(c), parent.hue(color),0);
		h *= h;
		 float s = parent.saturation(c) - parent.saturation(color);
		s *= s;
		 float b = parent.brightness(c) - parent.brightness(color);
		b *= b;
		float d = PApplet.sqrt(h + s + b);
		// if(x==160&&y==120)
		// PApplet.println();
		if (PApplet.abs(d) <= th) {
			if (((ReDec) parent).DEBUG) {
				parent.rect(x, y, w, height);
				// parent.text(s,x+5,y+16);
				// parent.line(x, y, x + w, y);
				// parent.line(x, y, x, y + height);
				// parent.line(x + w, y + height, x, y + height);
				// parent.line(x + w, y + height, x + w, y);
			}
			return true;
		} else
			return false;

	}

	private int average(PImage p) {
		int counter = 0;
		int red = 0;
		int green = 0;
		int blue = 0;
		p.loadPixels();
		int c = 0;
		for (int i = 0; i < p.width * p.height; i++) {
			c = p.pixels[i];
			red += Color.red(c);
			green += Color.green(c);
			blue += Color.blue(c);
			counter++;
		}
		if (counter == 0)
			return 0xff000000;
		return parent.color(red / counter, green / counter, blue / counter);
	}

}
