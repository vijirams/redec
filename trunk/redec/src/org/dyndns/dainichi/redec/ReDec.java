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
	Statistics[]		stats		= new Statistics[] { new Statistics(this), new Statistics(this), new Statistics(this), new Statistics(this), new Statistics(this),
			new Statistics(this)	};
	final boolean		DEBUG		= true;
	final int			CAM_WIDTH	= 320;
	final int			CAM_HEIGHT	= 240;
	final int			STEP_X		= 4;
	PImage				bg1			= new PImage(CAM_WIDTH, CAM_HEIGHT, ARGB);
	PImage				bg2			= new PImage(CAM_WIDTH, CAM_HEIGHT, ARGB);
	PFont				font;
	JMyron				cam;																					// a
	int[]				vals		= { 0, 0, 0, 0, 216, 144, 48, 0 };
	int					test		= 0x78fe5a30;

	// private Color bg = new Color(this, new float[] { 52.3f, 62.4f, 58.4f },
	// new float[] { 2.02f, 1.68f, 1.96f });
	int					colorNumber	= HSB;
	Color				silver;
	Color				gold;
	Color				black;
	Color				brown;
	Color				red;
	Color				orange;
	Color				yellow;
	Color				green;
	Color				blue;
	Color				violet;
	Color				white;
	Color				grey;
	PrintWriter			pr;
	PGraphics			g1;
	PGraphics			g2;
	PGraphics			g3;
	PGraphics			g4;
	PGraphics			g5;
	PGraphics			g6;
	AveragedImage		i1;
	float[]				rd			= new float[] { 0, 0 };

	float[]				gn			= new float[] { 0, 0 };
	float[]				bu			= new float[] { 0, 0 };
	float[]				hu			= new float[] { 0, 0 };
	float[]				st			= new float[] { 0, 0 };
	float[]				bt			= new float[] { 0, 0 };
	String				rgb			= "";
	String				hsb			= "";
	String				colorString;

	Color				local;

	XMLElement			root;

	Graphics3DThread	thread;

	public void chromaKey(int[] src, PImage dest)
	{
		dest.loadPixels();
		pushStyle();
		colorMode(HSB, 255);
		for (int y = 0; y < CAM_HEIGHT; y++)
		{
			for (int x = 0; x < CAM_WIDTH; x++)
			{
				int srci = x + y * CAM_WIDTH;
				int desti = x + y * CAM_WIDTH;
				int p = src[srci];
				float[] c = new float[] { hue(p), saturation(p), brightness(p), alpha(p) };
				// alpha = c[1] - 110;
				// alpha = red(c) + green(c) + blue(c) - 260;
				// alpha = 1;
				if (c[1] >= 0 && c[2] >= 110)
				{
					dest.pixels[desti] = color(c[0], c[1], c[2], c[3]);
				} else
				{
					dest.pixels[desti] = 0;
				}
			}
		}
		popStyle();
		dest.updatePixels();
	}

	@Override
	public void draw()
	{
		colorMode(colorNumber, 255);
		fill(color(0xff000000));
		stroke(color(0xff000000));
		rect(0, 0, width, height);
		background(color(0xff7f7f7f));
		cam.update();// update the camera view
		thread.paused = true;
		synchronized (bg1){chromaKey(cam.retinaImage(), bg1);}
		thread.paused = false;
		image(bg1, 0, 0);
		// i1.addImage(cam.image());
		// int[] img = cam.image(); // get the normal image of the camera

		if (frameCount  == 2)
		{
			thread.start();
			//			fillOffscreen(g1);
			//			fillOffscreen(g2);
			//			fillOffscreen(g3);
			//			fillOffscreen(g4);
			//			fillOffscreen(g5);
			//			fillOffscreen(g6);
		}
		synchronized (bg1){
			image(g1, 0, 240);
			image(g2, 320, 240);
			image(g3, 640, 240);
			image(g4, 0, 480);
			image(g5, 320, 480);
			image(g6, 640, 480);
		}
		loadPixels();
		if (frameCount < 3)
		{
			cam.adapt();
		}
		// chromaKey(i1.getImageArray(), bg1);

		bg2.loadPixels();
		bg2.copy(bg1, 0, 0, CAM_WIDTH, CAM_HEIGHT, 0, 0, CAM_WIDTH, CAM_HEIGHT);
		bg2.updatePixels();
		image(bg2, CAM_WIDTH * 2, 0);

		processPixels();

		textAndColors();
	}

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
			case 'a':
			case 'A':
				cam.adapt();
				break;
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
				pr.printf("%s\tHSB% 5.2f\t% 5.2f\t% 5.2f:% 5.2f\t% 5.2f\t% 5.2f\r\n", colorString, hu[0], st[0], bt[0], hu[1], st[1], bt[1]);
				pr.printf("%s\tRGB% 5.2f\t% 5.2f\t% 5.2f :% 5.2f % 5.2f % 5.2f\r\n", colorString, rd[0], gn[0], bu[0], rd[1], gn[1], bu[1]);
				break;
			}
		}
	}

	@Override
	public void mousePressed()
	{
		super.mousePressed();
		if (mouseButton == RIGHT)
		{
			cam.settings();// click the window to get the settings
		} else if (mouseButton == LEFT)
		{

		}
	}

	public void parseXml(XMLElement root)
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

	public void processPixels()
	{
		for (int x = 0; x < CAM_WIDTH; x += STEP_X)
		{

			float[][] valsRGB;
			float[][] valsHSB;
			valsRGB = new float[][] { Statistics.process(this, bg2.get(x, 0, STEP_X, CAM_HEIGHT), Statistics.RED),
					Statistics.process(this, bg2.get(x, 0, STEP_X, CAM_HEIGHT), Statistics.GREEN), Statistics.process(this, bg2.get(x, 0, STEP_X, CAM_HEIGHT), Statistics.BLUE) };
			valsHSB = new float[][] { Statistics.process(this, bg2.get(x, 0, STEP_X, CAM_HEIGHT), Statistics.RED),
					Statistics.process(this, bg2.get(x, 0, STEP_X, CAM_HEIGHT), Statistics.GREEN), Statistics.process(this, bg2.get(x, 0, STEP_X, CAM_HEIGHT), Statistics.BLUE) };
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
			int cHSB = color(hue, sat, brt, 128);
			// cHSB = color(hue())
			fill(cHSB);
			stroke(cHSB);
			// rect(x + 640, 0, STEP_X, CAM_HEIGHT);
			int red = (int) valsRGB[0][Statistics.MEAN];
			int green = (int) valsRGB[1][Statistics.MEAN];
			int blue = (int) valsRGB[2][Statistics.MEAN];
			colorMode(RGB, 255);
			int cRGB = color(red, green, blue, 128);
			fill(cRGB);
			stroke(cRGB);
			if (mouseX != pmouseX)
			{
				stats[0].zero();
				stats[1].zero();
				stats[2].zero();
				stats[3].zero();
				stats[4].zero();
				stats[5].zero();
			}
			if (mouseX >= x && mouseX <= x + STEP_X || mouseX >= x + 640 && mouseX <= x + STEP_X + 640)
			{
				rd[0] = stats[0].add(valsRGB[0][Statistics.MEAN])[Statistics.MEAN];
				rd[1] = stats[0].add(valsRGB[0][Statistics.MEAN])[Statistics.SD];
				gn[0] = stats[1].add(valsRGB[1][Statistics.MEAN])[Statistics.MEAN];
				gn[1] = stats[1].add(valsRGB[1][Statistics.MEAN])[Statistics.SD];
				bu[0] = stats[2].add(valsRGB[2][Statistics.MEAN])[Statistics.MEAN];
				bu[1] = stats[2].add(valsRGB[2][Statistics.MEAN])[Statistics.SD];

				hu[0] = stats[3].add(hue(cHSB))[Statistics.MEAN];
				hu[1] = stats[3].add(hue(cHSB))[Statistics.SD];
				st[0] = stats[4].add(saturation(cHSB))[Statistics.MEAN];
				st[1] = stats[4].add(saturation(cHSB))[Statistics.SD];
				bt[0] = stats[5].add(brightness(cHSB))[Statistics.MEAN];
				bt[1] = stats[5].add(brightness(cHSB))[Statistics.SD];

				rgb = new PrintfFormat("\nR:% 6.2f\t\u00b1% 6.2f\nG:% 6.2f\t\u00b1% 6.2f\nB:% 6.2f\t\u00b1% 6.2f")
				.sprintf(new Object[] { rd[0], rd[1], gn[0], gn[1], bu[0], bu[1] });

				hsb = new PrintfFormat("\nH:% 6.2f\t\u00b1% 6.2f\nS:% 6.2f\t\u00b1% 6.2f\nB:% 6.2f\t\u00b1% 6.2f")
				.sprintf(new Object[] { hu[0], hu[1], st[0], st[1], bt[0], bt[1] });

			}
		}
	}

	public void saveColor(Color c)
	{
		c.hue = hu[0];
		c.saturation = st[0];
		c.brightness = bt[0];
		c.red = rd[0];
		c.green = gn[0];
		c.blue = bu[0];
		c.sd[0] = hu[1];
		c.sd[1] = st[1];
		c.sd[2] = bt[1];
		c.sd[3] = rd[1];
		c.sd[4] = gn[1];
		c.sd[5] = bu[1];
		XMLElement[] x = root.getChildren(c.name);
		int max = 0;
		// float avg;
		for (XMLElement e : x)
		{
			max = max(max, e.getIntAttribute("id"));
		}

		root.addChild(c.getXML(max + 1));
		System.out.println(root.toString(true));
	}

	@Override
	public void setup()
	{
		size(3 * 320, 3 * 240);
		g1 = createGraphics(320, 240, P3D);
		g2 = createGraphics(320, 240, P3D);
		g3 = createGraphics(320, 240, P3D);
		g4 = createGraphics(320, 240, P3D);
		g5 = createGraphics(320, 240, P3D);
		g6 = createGraphics(320, 240, P3D);
		i1 = new AveragedImage(this, CAM_WIDTH, CAM_HEIGHT, 8);
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
			{
				pr.println(s);
			}
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
		cam.adaptivity(16F);

		font = loadFont("CenturySchoolbook-16.vlw");
		textFont(font);
		thread = new Graphics3DThread(this, new PGraphics[] { g1, g2, g3, g4, g5, g6 });
		//thread.start();

	}

	@Override
	public void stop()
	{
		cam.stop();
		pr.flush();
		pr.close();
		PrintWriter p = createWriter("data/colors2.xml");
		p.print(root.toString(true));
		p.flush();
		p.close();
		super.stop();
	}

	public void textAndColors()
	{
		pushStyle();
		colorMode(RGB, 255);

		text(rgb, 400, 0);
		text(hsb, 400, 3 * g.textLeading);

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
		text(frameRate,0,10);
		popStyle();
	}

}
