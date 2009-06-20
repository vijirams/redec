 package org.dyndns.dainichi.redec;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import JMyron.JMyron;

public class ReDec extends PApplet
{
	public final boolean	DEBUG		= true;
	private final int		CAM_WIDTH	= 320;
	private final int		CAM_HEIGHT	= 240;
	private final int		STEP_X		= 5;
	private final int		STEP_Y		= 8;
	private PImage			bg1			= new PImage(CAM_WIDTH, CAM_HEIGHT, ARGB);
	private PImage			bg2			= new PImage(CAM_WIDTH, CAM_HEIGHT, ARGB);
	private PFont			font;
	protected JMyron		cam;													// a
	private Handle[]		handles;
	private int[]			vals		= { 0, 0, 0, 0, 216, 144, 48, 0 };
	private float			a;
	private Histogram[]		hist		= new Histogram[3];
	private int				test		= 0x78fe5a30;

	// private Color bg = new Color(this, new float[] { 52.3f, 62.4f, 58.4f },
	// new float[] { 2.02f, 1.68f, 1.96f });
	int						colorMode	= HSB;
	private Color			silver;
	private Color			gold;
	private Color			black;
	private Color			brown;
	private Color			red;
	private Color			orange;
	private Color			yellow;
	private Color			green;
	private Color			blue;
	private Color			violet;
	private Color			white;
	private Color			grey;

	public void setup()
	{
		if (colorMode == RGB)
		{
			silver = new Color(this, new float[] { 176f, 193f, 190f }, new float[] { 33.7f, 32.7f, 31f });
			gold = new Color(this, new float[] { 184f, 185f, 154f }, new float[] { 35f, 35f, 34f });
			black = new Color(this, new float[] { 92f, 102f, 110f }, new float[] { 20f, 24.5f, 28.5f });
			brown = new Color(this, new float[] { 113f, 114f, 112f }, new float[] { 15.2f, 17f, 20.4f });
			red = new Color(this, new float[] { 185f, 95f, 95f }, new float[] { 21f, 15f, 14.5f });
			orange = new Color(this, new float[] { 208.5f, 126.5f, 93.5f }, new float[] { 26.7f, 19.7f, 20.5f });
			yellow = new Color(this, new float[] { 206.5f, 197.5f, 51f }, new float[] { 26f, 25.5f, 26.5f });
			green = new Color(this, new float[] { 82.5f, 147.7f, 103.7f }, new float[] { 13.5f, 16.6f, 18.2f });
			blue = new Color(this, new float[] { 84.2f, 116.7f, 191.7f }, new float[] { 17.6f, 21f, 23.3f });
			violet = new Color(this, new float[] { 109f, 92f, 130f }, new float[] { 13.2f, 13.2f, 16.5f });
			white = new Color(this, new float[] { 0f, 0f, 0f }, new float[] { 2.02f, 1.68f, 1.96f });
			grey = new Color(this, new float[] { 138.1f, 160.7f, 166.2f }, new float[] { 20f, 23f, 24.5f });
		} else
		{
			silver = new Color(this, new float[] { 52.3f, 62.4f, 58.4f }, new float[] { 2.02f, 1.68f, 1.96f });
			gold = new Color(this, new float[] { 184f, 185f, 154f }, new float[] { 35f, 35f, 34f });
			black = new Color(this, new float[] { 92f, 102f, 110f }, new float[] { 20f, 24.5f, 28.5f });
			brown = new Color(this, new float[] { 113f, 114f, 112f }, new float[] { 15.2f, 17f, 20.4f });
			red = new Color(this, new float[] { 185f, 95f, 95f }, new float[] { 21f, 15f, 14.5f });
			orange = new Color(this, new float[] { 52.3f, 62.4f, 58.4f }, new float[] { 2.02f, 1.68f, 1.96f });
			yellow = new Color(this, new float[] { 52.3f, 62.4f, 58.4f }, new float[] { 2.02f, 1.68f, 1.96f });
			green = new Color(this, new float[] { 52.3f, 62.4f, 58.4f }, new float[] { 2.02f, 1.68f, 1.96f });
			blue = new Color(this, new float[] { 52.3f, 62.4f, 58.4f }, new float[] { 2.02f, 1.68f, 1.96f });
			violet = new Color(this, new float[] { 52.3f, 62.4f, 58.4f }, new float[] { 2.02f, 1.68f, 1.96f });
			white = new Color(this, new float[] { 52.3f, 62.4f, 58.4f }, new float[] { 2.02f, 1.68f, 1.96f });
			grey = new Color(this, new float[] { 52.3f, 62.4f, 58.4f }, new float[] { 2.02f, 1.68f, 1.96f });
		}
		size(3 * 320, 240 + 1 * 240);
		colorMode(RGB, 255, 255, 255, 255);
		background(0xff7f7f7f);

		vals[0] = Color.alpha(test);
		vals[1] = Color.red(test);
		vals[2] = Color.green(test);
		vals[3] = Color.blue(test);
		cam = new JMyron();// make a new instance of the object
		cam.start(CAM_WIDTH, CAM_HEIGHT);// start a capture at 320x240
		cam.findGlobs(0);
		font = loadFont("CenturySchoolbook-16.vlw");
		textFont(font);
		handles = new Handle[4];
		handles[0] = new Handle(320, 0 + 5, vals[0], 10, handles, this);
		handles[1] = new Handle(320, 0 + 30, vals[1], 10, handles, this);
		handles[2] = new Handle(320, 0 + 55, vals[2], 10, handles, this);
		handles[3] = new Handle(320, 0 + 80, vals[3], 10, handles, this);

		hist[0] = new Histogram(0 * 320, 1 * 240, 256, 240, this);
		hist[1] = new Histogram(1 * 320, 1 * 240, 128, 240, this);
		hist[2] = new Histogram(2 * 320, 1 * 240, 128, 240, this);

	}

	float[]	r	= new float[] { 0, 0 };
	float[]	g	= new float[] { 0, 0 };
	float[]	b	= new float[] { 0, 0 };
	float[]	h	= new float[] { 0, 0 };
	float[]	s	= new float[] { 0, 0 };
	float[]	bb	= new float[] { 0, 0 };

	public void draw()
	{
		colorMode(colorMode, 255);
		fill(color(0xff000000));
		stroke(color(0xff000000));
		rect(0, 0, width, height);
		background(color(0xff7f7f7f));
		Handle.processSliders(handles, vals);
		cam.update();// update the camera view
		int[] img = cam.image(); // get the normal image of the camera
		loadPixels();
		bg1.loadPixels();
		processPixels(img, bg1.pixels);
		bg1.updatePixels();
		image(bg1, 0, 0);
		bg2.loadPixels();
		bg2.copy(bg1, 0, 0, CAM_WIDTH, CAM_HEIGHT, 0, 0, CAM_WIDTH, CAM_HEIGHT);
		bg2.updatePixels();
		image(bg2, CAM_WIDTH * 2, 0);
		index = 0;
		for (int x = 0; x < CAM_WIDTH; x += STEP_X)
		{

			float[][] valsRGB;
			float[][] valsHSB;
			valsRGB = new float[][] { Statistics.process(this, bg2.get(x, 0, STEP_X, CAM_HEIGHT), Statistics.RED),
					Statistics.process(this, bg2.get(x, 0, STEP_X, CAM_HEIGHT), Statistics.GREEN), Statistics.process(this, bg2.get(x, 0, STEP_X, CAM_HEIGHT), Statistics.BLUE) };
			valsHSB = new float[][] { Statistics.process(this, bg2.get(x, 0, STEP_X, CAM_HEIGHT), Statistics.HUE),
					Statistics.process(this, bg2.get(x, 0, STEP_X, CAM_HEIGHT), Statistics.SATURATION),
					Statistics.process(this, bg2.get(x, 0, STEP_X, CAM_HEIGHT), Statistics.BRIGHTNESS) };
			float[] colorHSB = new float[] { valsHSB[0][0], valsHSB[1][0], valsHSB[2][0] };
			float[] colorRGB = new float[] { valsRGB[0][0], valsRGB[1][0], valsRGB[2][0] };

			if (gold.isInRange(colorHSB, colorMode))
				println(gold);
			if (silver.isInRange(colorHSB, colorMode))
				print(silver);
			if (black.isInRange(colorHSB, colorMode))
				print(black);
			if (brown.isInRange(colorHSB, colorMode))
				print(brown);
			if (red.isInRange(colorRGB, colorMode))
				print(red);
			if (orange.isInRange(colorRGB, colorMode))
				print(orange);
			if (yellow.isInRange(colorHSB, colorMode))
				print(yellow);
			if (green.isInRange(colorHSB, colorMode))
				print(green);
			if (blue.isInRange(colorHSB, colorMode))
				print(blue);
			if (violet.isInRange(colorHSB, colorMode))
				print(violet);
			if (white.isInRange(colorRGB, colorMode))
				print(white);
			if (grey.isInRange(colorHSB, colorMode))
				print(grey);

			int hue = (int) valsHSB[0][Statistics.MEAN];
			int sat = (int) valsHSB[1][Statistics.MEAN];
			int brt = (int) valsHSB[2][Statistics.MEAN];
			colorMode(HSB, 255);
			int cHSB = color(hue, sat, brt, 255);
			fill(cHSB);
			stroke(cHSB);
			rect(x+640, 240, STEP_X, CAM_HEIGHT);
			int red = (int) valsRGB[0][Statistics.MEAN];
			int green = (int) valsRGB[1][Statistics.MEAN];
			int blue = (int) valsRGB[2][Statistics.MEAN];
			colorMode(RGB, 255);
			int cRGB = color(red, green, blue, 255);
			fill(cRGB);
			stroke(cRGB);
			rect(x , 240, STEP_X, CAM_HEIGHT);
			float l = 16;
			float m = l - 1;
			if (mouseX >= x && mouseX <= x + STEP_X)
			{
				r[0] = (valsRGB[0][Statistics.MEAN] + m * r[0]) / l;
				r[1] = (valsRGB[0][Statistics.SD] + m * r[1]) / l;
				g[0] = (valsRGB[1][Statistics.MEAN] + m * g[0]) / l;
				g[1] = (valsRGB[1][Statistics.SD] + m * g[1]) / l;
				b[0] = (valsRGB[2][Statistics.MEAN] + m * b[0]) / l;
				b[1] = (valsRGB[2][Statistics.SD] + m * b[1]) / l;

				// System.out.printf("%03d:%03d:%03d\t%06x\n",red,green,blue,color);
				// println();
				System.out.printf("RGB: % 5.3f % 5.3f % 5.3f:% 5.3f % 5.3f % 5.3f\n SNR:% 5.3f % 5.3f % 5.3f\n", r[0], g[0], b[0], r[1], g[1], b[1], r[0] / r[1], g[0] / g[1], b[0]
						/ b[1]);
			}
			if (mouseX >= x + 640 && mouseX <= x + STEP_X + 640)
			{
				h[0] = (valsHSB[0][Statistics.MEAN] + m * h[0]) / l;
				h[1] = (valsHSB[0][Statistics.SD] + m * h[1]) / l;
				s[0] = (valsHSB[1][Statistics.MEAN] + m * s[0]) / l;
				s[1] = (valsHSB[1][Statistics.SD] + m * s[1]) / l;
				bb[0] = (valsHSB[2][Statistics.MEAN] + m * bb[0]) / l;
				bb[1] = (valsHSB[2][Statistics.SD] + m * bb[1]) / l;

				// System.out.printf("%03d:%03d:%03d\t%06x\n",red,green,blue,color);
				// println();
				System.out.printf("HSB: % 5.3f % 5.3f % 5.3f:% 5.3f % 5.3f % 5.3f\n SNR:% 5.3f % 5.3f % 5.3f\n", h[0], s[0], bb[0], h[1], s[1], bb[1], h[0] / h[1], s[0] / s[1],
						bb[0] / bb[1]);
			}
		}
		textAndColors();
	}

	public void processPixels(int[] src, int[] dest)
	{
		pushStyle();
		colorMode(RGB, 255);
		for (int y = 0; y < CAM_HEIGHT; y++)
		{
			for (int x = 0; x < CAM_WIDTH; x++)
			{
				int srci = x + y * CAM_WIDTH;
				int desti = x + y * CAM_WIDTH;
				int c = src[srci];
				a = red(c) + green(c) + blue(c) - 260;
				// a = (bg.isInRange(new float[]{red(c),green(c),blue(c)}))?
				// 0:1;
				// a = 1;
				if (a >= 0)
					dest[desti] = src[srci];
				else
					dest[desti] = 0;
			}
		}
		popStyle();
	}

	private void textAndColors()
	{
		int c = color(vals[1], vals[2], vals[3]);
		pushStyle();
		colorMode(RGB, 255);

		fill(c);
		stroke(0x00000000);
		rect(320, 120, 20, 20);
		fill(color(red(c) + vals[0], green(c) + vals[0], blue(c) + vals[0]));
		rect(320, 140, 20, 20);
		fill(color(red(c) - vals[0], green(c) - vals[0], blue(c) - vals[0]));
		rect(320, 100, 20, 20);
		text((float) vals[3], 400, 240);
		text(hue(color(vals[1], vals[2], vals[3])), 400, 256);
		text(saturation(color(vals[1], vals[2], vals[3])), 400, 272);
		text(brightness(color(vals[1], vals[2], vals[3])), 400, 288);
		popStyle();
	}

	public void mousePressed()
	{
		if (mouseButton == RIGHT)
			cam.settings();// click the window to get the settings
		else if (mouseButton == LEFT)
		{
			if (mouseX < CAM_WIDTH && mouseY < CAM_HEIGHT)
			{
				loadPixels();
				handles[1].length = (int) red(bg2.get(mouseX, mouseY));
				handles[2].length = (int) green(bg2.get(mouseX, mouseY));
				handles[3].length = (int) blue(bg2.get(mouseX, mouseY));
			}
		}
	}

	public void stop()
	{
		cam.stop();
		super.stop();
	}

	private int	index	= 0;

	private void print(Color c)
	{
		stroke(0);
		fill(c.color());
		rect(320 + 10 * index++, 240, 10, 10);
	}

}
