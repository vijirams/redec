package org.dyndns.dainichi.redec;

import java.io.PrintWriter;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.xml.XMLElement;
import JMyron.JMyron;

public class ReDec extends PApplet
{
	public Statistics[]		stats		= new Statistics[] { new Statistics(this), new Statistics(this), new Statistics(this), new Statistics(this), new Statistics(this),
			new Statistics(this)		};
	public final boolean	DEBUG		= true;
	private final int		CAM_WIDTH	= 320;
	private final int		CAM_HEIGHT	= 240;
	private final int		STEP_X		= 4;
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
	int						colorNumber	= HSB;
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
	private PrintWriter		pr;
	private PGraphics		g1;
	private PGraphics		g2;
	private PGraphics		g3;

	public void setup()
	{
		size(3 * 320, 3 * 240);
		g1 = createGraphics(320, 240, P3D);
		g2 = createGraphics(320, 240, P3D);
		g3 = createGraphics(320, 240, P3D);
		String[] temp = loadStrings("ColorLines.txt");
		pr = createWriter("data/ColorLines.txt");
		silver = new Color(this, "silver", new float[] { 0, 0, 0, 0, 0, 0 }, new float[] { 0, 0, 0, 0, 0, 0 });
		gold = new Color(this, "gold", new float[] { 0, 0, 0, 0, 0, 0 }, new float[] { 0, 0, 0, 0, 0, 0 });
		black = new Color(this, "black", new float[] { 0, 0, 0, 0, 0, 0 }, new float[] { 0, 0, 0, 0, 0, 0 });
		brown = new Color(this, "brown", new float[] { 0, 0, 0, 0, 0, 0 }, new float[] { 0, 0, 0, 0, 0, 0 });
		red = new Color(this, "red", new float[] { 0, 0, 0, 0, 0, 0 }, new float[] { 0, 0, 0, 0, 0, 0 });
		orange = new Color(this, "orange", new float[] { 0, 0, 0, 0, 0, 0 }, new float[] { 0, 0, 0, 0, 0, 0 });
		yellow = new Color(this, "yellow", new float[] { 0, 0, 0, 0, 0, 0 }, new float[] { 0, 0, 0, 0, 0, 0 });
		green = new Color(this, "green", new float[] { 0, 0, 0, 0, 0, 0 }, new float[] { 0, 0, 0, 0, 0, 0 });
		blue = new Color(this, "blue", new float[] { 0, 0, 0, 0, 0, 0 }, new float[] { 0, 0, 0, 0, 0, 0 });
		violet = new Color(this, "violet", new float[] { 0, 0, 0, 0, 0, 0 }, new float[] { 0, 0, 0, 0, 0, 0 });
		grey = new Color(this, "grey", new float[] { 0, 0, 0, 0, 0, 0 }, new float[] { 0, 0, 0, 0, 0, 0 });
		white = new Color(this, "white", new float[] { 0, 0, 0, 0, 0, 0 }, new float[] { 0, 0, 0, 0, 0, 0 });
		if (temp != null)
		{

			for (String s : temp)
				pr.println(s);
		}

		root = new XMLElement(this, "colors.xml");
		parseXml(root);
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
		colorMode(colorNumber, 255);
		fill(color(0xff000000));
		stroke(color(0xff000000));
		rect(0, 0, width, height);
		background(color(0xff7f7f7f));
		Handle.processSliders(handles, vals);
		cam.update();// update the camera view
		int[] img = cam.image(); // get the normal image of the camera
		fillOffscreen(g1);
		fillOffscreen(g2);
		fillOffscreen(g3);
		image(g1, 0, 480);
		image(g2, 320, 480);
		image(g3, 640, 480);
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
			valsHSB = new float[][] { Statistics.process(this, bg2.get(x, 0, STEP_X, CAM_HEIGHT), Statistics.RED),
					Statistics.process(this, bg2.get(x, 0, STEP_X, CAM_HEIGHT), Statistics.GREEN), Statistics.process(this, bg2.get(x, 0, STEP_X, CAM_HEIGHT), Statistics.BLUE) };
			float[] colorHSB = new float[] { valsHSB[0][0], valsHSB[1][0], valsHSB[2][0] };
			float[] colorRGB = new float[] { valsRGB[0][0], valsRGB[1][0], valsRGB[2][0] };
			/*
			 * if (gold.isInRange(colorHSB, colorMode)) println(gold); if
			 * (silver.isInRange(colorHSB, colorMode)) print(silver); if
			 * (black.isInRange(colorHSB, colorMode)) print(black); if
			 * (brown.isInRange(colorHSB, colorMode)) print(brown); if
			 * (red.isInRange(colorRGB, colorMode)) print(red); if
			 * (orange.isInRange(colorRGB, colorMode)) print(orange); if
			 * (yellow.isInRange(colorHSB, colorMode)) print(yellow); if
			 * (green.isInRange(colorHSB, colorMode)) print(green); if
			 * (blue.isInRange(colorHSB, colorMode)) print(blue); if
			 * (violet.isInRange(colorHSB, colorMode)) print(violet); if
			 * (white.isInRange(colorRGB, colorMode)) print(white); if
			 * (grey.isInRange(colorHSB, colorMode)) print(grey);
			 */
			int hue = (int) valsHSB[0][Statistics.MEAN];
			int sat = (int) valsHSB[1][Statistics.MEAN];
			int brt = (int) valsHSB[2][Statistics.MEAN];
			// colorMode(HSB, 255);
			int cHSB = color(hue, sat, brt, 255);
			// cHSB = color(hue())
			fill(cHSB);
			stroke(cHSB);
			rect(x + 640, 240, STEP_X, CAM_HEIGHT);
			int red = (int) valsRGB[0][Statistics.MEAN];
			int green = (int) valsRGB[1][Statistics.MEAN];
			int blue = (int) valsRGB[2][Statistics.MEAN];
			colorMode(RGB, 255);
			int cRGB = color(red, green, blue, 255);
			fill(cRGB);
			stroke(cRGB);
			rect(x, 240, STEP_X, CAM_HEIGHT);
			float l = 16;
			float m = l - 1;
			if (mouseX != pmouseX)
			{
				stats[0].zero();
				stats[1].zero();
				stats[2].zero();
				stats[3].zero();
				stats[4].zero();
				stats[5].zero();
			}
			if ((mouseX >= x && mouseX <= x + STEP_X) || (mouseX >= x + 640 && mouseX <= x + STEP_X + 640))
			{
				r[0] = stats[0].add(valsRGB[0][Statistics.MEAN])[Statistics.MEAN];
				r[1] = stats[0].add(valsRGB[0][Statistics.MEAN])[Statistics.SD];
				g[0] = stats[1].add(valsRGB[1][Statistics.MEAN])[Statistics.MEAN];
				g[1] = stats[1].add(valsRGB[1][Statistics.MEAN])[Statistics.SD];
				b[0] = stats[2].add(valsRGB[2][Statistics.MEAN])[Statistics.MEAN];
				b[1] = stats[2].add(valsRGB[2][Statistics.MEAN])[Statistics.SD];

				h[0] = stats[3].add(hue(cHSB))[Statistics.MEAN];
				h[1] = stats[3].add(hue(cHSB))[Statistics.SD];
				s[0] = stats[4].add(saturation(cHSB))[Statistics.MEAN];
				s[1] = stats[4].add(saturation(cHSB))[Statistics.SD];
				bb[0] = stats[5].add(brightness(cHSB))[Statistics.MEAN];
				bb[1] = stats[5].add(brightness(cHSB))[Statistics.SD];
			}
			if (mouseX >= x && mouseX <= x + STEP_X)
			{
				System.out.printf("RGB: % 5.3f % 5.3f % 5.3f:% 5.3f % 5.3f % 5.3f\n SNR:% 5.3f % 5.3f % 5.3f\n", r[0], g[0], b[0], r[1], g[1], b[1], r[0] / r[1], g[0] / g[1], b[0]
						/ b[1]);
			}
			if (mouseX >= x + 640 && mouseX <= x + STEP_X + 640)
			{
				System.out.printf("HSB: % 5.3f % 5.3f % 5.3f:% 5.3f % 5.3f % 5.3f\n SNR:% 5.3f % 5.3f % 5.3f\n", h[0], s[0], bb[0], h[1], s[1], bb[1], h[0] / h[1], s[0] / s[1],
						bb[0] / bb[1]);
			}

		}
		textAndColors();
	}

	public void fillOffscreen(PGraphics device)
	{
		if (frameCount < 2)
		{

			device.camera(160, 300, 300, 160, 120, 128, 0, 0, -1);

		}
		device.background(0xff000000);
		device.ambient(255, 255, 255);
		device.beginDraw();
		device.colorMode(HSB, 255);
		int dev = (device.equals(g3)) ? 3 : (device.equals(g2)) ? 2 : (device.equals(g1)) ? 1 : 0;

		for (int y = 0; y < CAM_HEIGHT; y++)
		{
			for (int x = 0; x < CAM_WIDTH; x++)
			{
				int pixel = bg1.pixels[x + y * CAM_WIDTH];
				device.stroke(pixel);
				switch (dev)
				{
				case 1:
					device.point(x, y, hue(pixel));
					break;
				case 2:
					device.point(x, y, saturation(pixel));
					break;
				case 3:
					device.point(x, y, brightness(pixel));
					break;
				}
			}
		}

		device.stroke(0xffff0000);
		device.line(0, 0, 0, 1000, 0, 0);
		device.stroke(0xff00ff00);
		device.line(0, 0, 0, 0, 1000, 0);
		device.stroke(0xff0000ff);
		device.line(0, 0, 0, 0, 0, 1000);
		device.endDraw();
	}

	public void processPixels(int[] src, int[] dest)
	{
		pushStyle();
		colorMode(HSB, 255);
		for (int y = 0; y < CAM_HEIGHT; y++)
		{
			for (int x = 0; x < CAM_WIDTH; x++)
			{
				int srci = x + y * CAM_WIDTH;
				int desti = x + y * CAM_WIDTH;
				int c = src[srci];
				a = brightness(c) - 110;
				// a = red(c) + green(c) + blue(c) - 260;
				// a = (bg.isInRange(new float[]{red(c),green(c),blue(c)}))?
				// 0:1;
				// a = 1;
				if (a >= 0)
					dest[desti] = c;
				else
					dest[desti] = 0;
			}
		}
		popStyle();
	}

	private String	colorString;

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
		String s;
		switch (colorNumber)
		{
		case 0x0:
			s = "Silver";
			break;
		case 0x1:
			s = "Gold";
			break;
		case 0x2:
			s = "Black";
			break;
		case 0x3:
			s = "Brown";
			break;
		case 0x4:
			s = "Red";
			break;
		case 0x5:
			s = "Orange";
			break;
		case 0x6:
			s = "Yellow";
			break;
		case 0x7:
			s = "Green";
			break;
		case 0x8:
			s = "Blue";
			break;
		case 0x9:
			s = "Violet";
			break;
		case 0xa:
			s = "Grey";
			break;
		case 0xb:
			s = "White";
			break;
		default:
			s = "Invalid Mode";
		}
		colorString = s;
		text(s, 320, 100);
	}

	public void mousePressed()
	{
		super.mousePressed();
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
		pr.flush();
		pr.close();
		super.stop();
	}

	private int	index	= 0;

	private void print(Color c)
	{
		stroke(0);
		fill(c.color());
		rect(320 + 10 * index++, 240, 10, 10);
	}

	int	mode	= 0;

	@Override
	public void keyPressed()
	{
		super.keyPressed();
		if (key == CODED && keyPressed)
		{
			switch (keyCode)
			{
			case DOWN:
			case RIGHT:
				colorNumber = (colorNumber + 1) % 12;
				break;
			case LEFT:
			case UP:
				if (colorNumber == 0)
				{
					colorNumber = 11;

				} else
				{
					colorNumber--;
				}
				break;
			}
		} else
		{
			switch (key)
			{
			case ENTER:
			case RETURN:
				switch (colorNumber)
				{
				case 0x0:
					local = silver;
					break;
				case 0x1:
					local = gold;
					break;
				case 0x2:
					local = black;
					break;
				case 0x3:
					local = brown;
					break;
				case 0x4:
					local = red;
					break;
				case 0x5:
					local = orange;
					break;
				case 0x6:
					local = yellow;
					break;
				case 0x7:
					local = green;
					break;
				case 0x8:
					local = blue;
					break;
				case 0x9:
					local = violet;
					break;
				case 0xa:
					local = grey;
					break;
				case 0xb:
					local = white;
					break;
				default:
					local = null;
				}
				saveColor(local);
				pr.printf("%s\tHSB% 5.2f\t% 5.2f\t% 5.2f:% 5.2f\t% 5.2f\t% 5.2f\r\n", colorString, h[0], s[0], bb[0], h[1], s[1], bb[1]);
				pr.printf("%s\tRGB% 5.2f\t% 5.2f\t% 5.2f :% 5.2f % 5.2f % 5.2f\r\n", colorString, r[0], g[0], b[0], r[1], g[1], b[1]);
				break;
			}
		}
	}

	Color	local;

	private void parseXml(XMLElement root)
	{
		silver.loadFromXML(root.getChild("silver"));
		gold.loadFromXML(root.getChild("gold"));
		black.loadFromXML(root.getChild("black"));
		brown.loadFromXML(root.getChild("brown"));
		red.loadFromXML(root.getChild("red"));
		orange.loadFromXML(root.getChild("orange"));
		yellow.loadFromXML(root.getChild("yellow"));
		green.loadFromXML(root.getChild("green"));
		blue.loadFromXML(root.getChild("blue"));
		violet.loadFromXML(root.getChild("violet"));
		grey.loadFromXML(root.getChild("grey"));
		white.loadFromXML(root.getChild("white"));
	}

	XMLElement	root;

	private void saveColor(Color c)
	{
		c.hue = h[0];
		c.saturation = s[0];
		c.brightness = bb[0];
		c.red = r[0];
		c.green = g[0];
		c.blue = b[0];
		c.sd[0] = h[1];
		c.sd[1] = s[1];
		c.sd[2] = bb[1];
		c.sd[3] = r[1];
		c.sd[4] = g[1];
		c.sd[5] = b[1];
		XMLElement x = root.getChild(c.name);
		x = c.getXML();
		System.err.println(x.toString(true));
	}

}
