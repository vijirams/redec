package org.dyndns.dainichi.redec;

import java.io.PrintWriter;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.xml.XMLElement;
import JMyron.JMyron;

public class ReDec extends PApplet {
	final int				CAM_HEIGHT	= 240;
	final int				CAM_WIDTH	= 320;
	PImage					bg1			= new PImage(CAM_WIDTH, CAM_HEIGHT, ARGB);
	PImage					bg2			= new PImage(CAM_WIDTH, CAM_HEIGHT, ARGB);
	Color					black;
	Color					blue;
	Color					brown;
	float[]					bt			= new float[] { 0, 0 };
	float[]					bu			= new float[] { 0, 0 };
	JMyron					cam;													// a

	// private Color bg = new Color(this, new float[] { 52.3f, 62.4f, 58.4f },
	// new float[] { 2.02f, 1.68f, 1.96f });
	int						colorNumber	= HSB;
	String					colorString;

	final boolean			DEBUG		= true;
	PFont					font;
	PGraphics				g1;
	PGraphics				g2;
	PGraphics				g3;
	PGraphics				g4;
	PGraphics				g5;
	PGraphics				g6;
	float[]					gn			= new float[] { 0, 0 };
	Color					gold;
	Color					green;
	Color					grey;
	String					hsb			= "";
	float[]					hu			= new float[] { 0, 0 };
	AveragedImage			i1;
	private ImageAquisition	img;
	Color					local;
	Color					orange;
	PrintWriter				pr;
	float[]					rd			= new float[] { 0, 0 };
	Color					red;
	String					rgb			= "";

	XMLElement				root;
	// Serial serial;
	Color					silver;
	float[]					st			= new float[] { 0, 0 };
	Statistics[]			stats		= new Statistics[] { new Statistics(this), new Statistics(this), new Statistics(this), new Statistics(this), new Statistics(this),
			new Statistics(this)		};
	final int				STEP_X		= 4;
	int						test		= 0x78fe5a30;
	Graphics3DThread		thread1;
	Graphics3DThread		thread2;
	Graphics3DThread		thread3;
	Graphics3DThread		thread4;
	Graphics3DThread		thread5;
	Graphics3DThread		thread6;

	int[]					vals		= { 0, 0, 0, 0, 216, 144, 48, 0 };

	Color					violet;

	Color					white;

	Color					yellow;

	public void _stop()
	{
		pr.flush();
		pr.close();
		PrintWriter p = createWriter("data/colors2.xml");
		p.print(root.toString(true));
		p.flush();
		p.close();
		img.run = false;
		try {
			img.join();
		} catch (InterruptedException e) {}
		exit();
	}

	public void chromaKey(int[] src, PImage dest)
	{
		dest.loadPixels();
		pushStyle();
		colorMode(HSB, 255);
		for (int y = 0; y < CAM_HEIGHT; y++) {
			for (int x = 0; x < CAM_WIDTH; x++) {
				int srci = x + y * CAM_WIDTH;
				int desti = x + y * CAM_WIDTH;
				int p = src[srci];
				float[] c = new float[] { hue(p), saturation(p), brightness(p), alpha(p) };
				// alpha = c[1] - 110;
				// alpha = red(c) + green(c) + blue(c) - 260;
				// alpha = 1;
				if (true) {// c[2] > 50) {
					dest.pixels[desti] = color(c[0], c[1], c[2], c[3]);
				} else {
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
//		if (frameCount == 2) {
//			thread1.start();
//			thread2.start();
//			thread3.start();
//			thread4.start();
//			thread5.start();
//			thread6.start();
//		}
		colorMode(colorNumber, 255);
		fill(color(0xff000000));
		stroke(color(0xff000000));
		rect(0, 0, width, height);
		background(color(0xff7f7f7f));
		img.pause();
		chromaKey(img.getImages()[0], bg1);
		image(bg1, 0, 0);
		img.unpause();

		synchronized (bg2) {
			System.arraycopy(bg1.pixels, 0, bg2.pixels, 0, CAM_HEIGHT * CAM_WIDTH);
		}
		bg1.updatePixels();
		//image(bg2, CAM_WIDTH * 2, 0);

//		image(g1, 0, 240);
//		image(g2, 320, 240);
//		image(g3, 640, 240);
//		image(g4, 0, 480);
//		image(g5, 320, 480);
//		image(g6, 640, 480);
		loadPixels();

		processPixels();
		textAndColors();
	}

	private void threadPause()
	{
		thread1.pause();
		thread2.pause();
		thread3.pause();
		thread4.pause();
		thread5.pause();
		thread6.pause();

	}

	private void threadUnPause()
	{

		thread1.unpause();
		thread2.unpause();
		thread3.unpause();
		thread4.unpause();
		thread5.unpause();
		thread6.unpause();
	}

	@Override
	public void keyPressed()
	{
		super.keyPressed();
		if (key == CODED && keyPressed) {
			switch (keyCode)
			{
			case ESC:
				key = 0;
				keyCode = 0;
				_stop();
				break;
			case DOWN:
			case RIGHT:
				colorNumber = (colorNumber + 1) % 12;
				break;
			case LEFT:
			case UP:
				if (colorNumber == 0) {
					colorNumber = 11;

				} else {
					colorNumber--;
				}
				break;
			}
		} else {
			switch (key)
			{
			case ESC:
				key = 0;
				_stop();
				break;
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
		if (mouseButton == RIGHT) {
			img.cam.settings();// click the window to get the settings
		} else if (mouseButton == LEFT) {

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
	Resistor res1;// = new Resistor(this);
	Resistor res2;// = new Resistor(this);
	public void processPixels()
	{
		boolean[][] colors = new boolean[12][2];
		res1 = new Resistor(this);
		res2 = new Resistor(this);

		for (int x = 0; x < CAM_WIDTH; x += STEP_X) {

			float[][] valsRGB;
			float[][] valsHSB;
			valsRGB = new float[][] { Statistics.process(this, bg2.get(x, 0, STEP_X, CAM_HEIGHT), Statistics.RED),
					Statistics.process(this, bg2.get(x, 0, STEP_X, CAM_HEIGHT), Statistics.GREEN), Statistics.process(this, bg2.get(x, 0, STEP_X, CAM_HEIGHT), Statistics.BLUE) };
			valsHSB = new float[][] { Statistics.process(this, bg2.get(x, 0, STEP_X, CAM_HEIGHT), Statistics.HUE),
					Statistics.process(this, bg2.get(x, 0, STEP_X, CAM_HEIGHT), Statistics.SATURATION),
					Statistics.process(this, bg2.get(x, 0, STEP_X, CAM_HEIGHT), Statistics.BRIGHTNESS) };
			float[][] valss = new float[valsHSB.length + valsRGB.length][];
			for (int i = 0; i < valss.length; i++) {
				if (i < valsHSB.length) {
					valss[i] = valsHSB[i];
				} else {
					valss[i] = valsRGB[i % valsHSB.length];
				}
			}
			colors[0] = silver.isSimilar(valss);
			colors[1] = gold.isSimilar(valss);
			colors[2] = black.isSimilar(valss);
			colors[3] = brown.isSimilar(valss);
			colors[4] = red.isSimilar(valss);
			colors[5] = orange.isSimilar(valss);
			colors[6] = yellow.isSimilar(valss);
			colors[7] = green.isSimilar(valss);
			colors[8] = blue.isSimilar(valss);
			colors[9] = violet.isSimilar(valss);
			colors[10] = grey.isSimilar(valss);
			colors[11] = white.isSimilar(valss);
			int hue = (int) valsHSB[0][Statistics.MEAN];
			int sat = (int) valsHSB[1][Statistics.MEAN];
			int brt = (int) valsHSB[2][Statistics.MEAN];
			 colorMode(HSB, 255);
			int cHSB = color(hue, sat, brt,128);
			// cHSB = color(hue())
			fill(cHSB);
			stroke(cHSB);
			 rect(x, 0, STEP_X, CAM_HEIGHT);
			int red = (int) valsRGB[0][Statistics.MEAN];
			int green = (int) valsRGB[1][Statistics.MEAN];
			int blue = (int) valsRGB[2][Statistics.MEAN];
			colorMode(RGB, 255);
			int cRGB = color(red, green, blue, 128);
			fill(cRGB);
			stroke(cRGB);
			if (mouseX != pmouseX) {
				stats[0].zero();
				stats[1].zero();
				stats[2].zero();
				stats[3].zero();
				stats[4].zero();
				stats[5].zero();
			}
			if (mouseX >= x && mouseX <= x + STEP_X || mouseX >= x + 640 && mouseX <= x + STEP_X + 640) {
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
			int temp1 = 0;
			int temp2 = 0;
			for (int i = 0; i < 12; i++) {
				stroke(colors[i][0] ? 0xffffffff : 0xff000000);
				point(x, i);
				stroke(colors[i][1] ? 0xffffffff : 0xff000000);
				point(x + 1, i);
				temp1 |= colors[i][0] ? 1 << i : 0;
				temp2 |= colors[i][1] ? 1 << i : 0;
			}
			 res1.add(temp1);
			 res2.add(temp2);
		}
		// println(res1.getValue());
		// println(res2.getValue());

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
		for (XMLElement e : x) {
			max = max(max, e.getIntAttribute("id"));
		}

		root.addChild(c.getXML(max + 1));
		System.out.println(root.toString(true));
	}

	@Override
	public void setup()
	{
		size(3 * CAM_WIDTH, 3 * CAM_HEIGHT);

		g1 = createGraphics(CAM_WIDTH, CAM_HEIGHT, P3D);
		g2 = createGraphics(CAM_WIDTH, CAM_HEIGHT, P3D);
		g3 = createGraphics(CAM_WIDTH, CAM_HEIGHT, P3D);
		g4 = createGraphics(CAM_WIDTH, CAM_HEIGHT, P3D);
		g5 = createGraphics(CAM_WIDTH, CAM_HEIGHT, P3D);
		g6 = createGraphics(CAM_WIDTH, CAM_HEIGHT, P3D);
		i1 = new AveragedImage(this, CAM_WIDTH, CAM_HEIGHT, 8);
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

		root = new XMLElement(this, "../colors.xml");
		parseXml(root);
		colorMode(RGB, 255, 255, 255, 255);
		background(0xff7f7f7f);

		font = loadFont("../CenturySchoolbook-16.vlw");
		textFont(font);

		thread1 = new Graphics3DThread(this, g1, bg2);
		thread2 = new Graphics3DThread(this, g2, bg2);
		thread3 = new Graphics3DThread(this, g3, bg2);
		thread4 = new Graphics3DThread(this, g4, bg2);
		thread5 = new Graphics3DThread(this, g5, bg2);
		thread6 = new Graphics3DThread(this, g6, bg2);
		img = new ImageAquisition(this, CAM_WIDTH, CAM_HEIGHT);
		img.start();
		// thread.start();

	}

	public void textAndColors()
	{
		pushStyle();
		colorMode(RGB, 255);
		stroke(-1);
		fill(-1);

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
		text(s, CAM_WIDTH, 100);
		text(res1.getValue(), CAM_WIDTH, 100+g.textLeading);
		text(res2.getValue(), CAM_WIDTH, 100+2*g.textLeading);
		// text(frameRate,0,10);
		popStyle();
	}

	public void rgbToHsb(int[] input, int[][] output)
	{
		pushStyle();
		colorMode(HSB, 255, 255, 255, 255);
		int hh, ss, bb;
		int pixel;
		for (int i = 0; i < input.length; i++) {
			pixel = input[i];
			hh = (int) hue(pixel);
			ss = (int) saturation(pixel);
			bb = (int) brightness(pixel);

			output[0][i] = pixel & 255;
			output[1][i] = hh;
			output[2][i] = ss;
			output[3][i] = bb;
			output[4][i] = pixel >> 24 & 255;
			output[5][i] = pixel >> 16 & 255;
			output[6][i] = pixel >> 8 & 255;
		}
		popStyle();
	}

}
