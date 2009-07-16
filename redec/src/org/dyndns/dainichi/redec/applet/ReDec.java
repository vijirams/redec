package org.dyndns.dainichi.redec.applet;

import java.io.File;
import java.io.PrintWriter;

import org.dyndns.dainichi.redec.util.PrintfFormat;
import org.dyndns.dainichi.redec.util.Statistics;
import org.dyndns.dainichi.redec.util.objects.Color;
import org.dyndns.dainichi.redec.util.objects.Resistor;
import org.dyndns.dainichi.redec.util.threading.Graphics3DThread;
import org.dyndns.dainichi.redec.util.threading.ImageAquisition;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.xml.XMLElement;

/**
 * @author dejagerd
 *
 */
public class ReDec extends PApplet {

	public final int				CAM_HEIGHT	= 240;
	public final int				CAM_WIDTH	= 320;
	public final int				STEP_X		= 2;
	//final boolean			DEBUG		= true;
	PImage					bg1			= new PImage(CAM_WIDTH, CAM_HEIGHT, ARGB);
	PImage					bg2			= new PImage(CAM_WIDTH, CAM_HEIGHT, ARGB);
	float[]					bt			= new float[] { 0, 0 };
	float[]					bu			= new float[] { 0, 0 };

	int						colorNumber	= 0;
	String					colorString;


	PFont					font;
	public PGraphics				g1;
	public PGraphics				g2;
	public PGraphics				g3;
	public PGraphics				g4;
	public PGraphics				g5;
	public PGraphics				g6;
	float[]					gn			= new float[] { 0, 0 };
	String					hsb			= "";
	float[]					hu			= new float[] { 0, 0 };

	private ImageAquisition	img;
	PrintWriter				pr;
	float[]					rd			= new float[] { 0, 0 };
	String					rgb			= "";

	XMLElement				root;
	float[]					st			= new float[] { 0, 0 };
	Statistics[]			stats		= new Statistics[] { new Statistics(this), new Statistics(this), new Statistics(this), new Statistics(this), new Statistics(this),
			new Statistics(this)		};


	Graphics3DThread		thread1;
	Graphics3DThread		thread2;
	Graphics3DThread		thread3;
	Graphics3DThread		thread4;
	Graphics3DThread		thread5;
	Graphics3DThread		thread6;
	PImage	i1	= new PImage(CAM_WIDTH, CAM_HEIGHT, ARGB);
	PImage	i2	= new PImage(CAM_WIDTH, CAM_HEIGHT, ARGB);
	PImage	i3	= new PImage(CAM_WIDTH, CAM_HEIGHT, ARGB);



	public Color					violet;
	public Color					silver;
	public Color					black;
	public Color					blue;
	public Color					brown;
	public Color					gold;
	public Color					green;
	public Color					grey;
	public Color					local;
	public Color					orange;
	public Color					red;
	public Color					white;
	public Color					yellow;
	public boolean					freeze;
	Resistor	res1;	// = new Resistor(this);
	Resistor	res2;	// = new Resistor(this);
	File	projectPath;
	String	colorsXml;
	/**
	 * Called to shut down the program.
	 * Duties include proper termination of various threads
	 * and writing the color tree to persistant storage.
	 */
	public void endProgram()
	{
		img.setRun(false);
		try {
			img.join();
		} catch (InterruptedException e) {}
		PrintWriter pr = createWriter(colorsXml);
		pr.print(root.toString(true));
		pr.flush();
		pr.close();
		exit();
	}

	/**
	 * Dees a ChromaKey of the source image. Also doubles as an array copy utility.
	 * copies pixels from <code>src</code> to <code>dest</code> if the input pixels
	 * obey some preset rules. Otherwise the equivalent pixel in <code>dest</code>
	 * is set to all black, and maximum transparency.
	 * @param src Input image.
	 * @param dest Output PImage.
	 */
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
				if (
				// c[2] > 64) {
				true) {
					dest.pixels[desti] = color(c[0], c[1], c[2], c[3]);
				} else {
					dest.pixels[desti] = 0;
				}
			}
		}
		popStyle();
		dest.updatePixels();
	}


	/* (non-Javadoc)
	 * @see processing.core.PApplet#draw()
	 */
	@Override
	public void draw()
	{
		colorMode(RGB, 255);
		fill(color(0xff000000));
		stroke(color(0xff000000));
		rect(0, 0, width, height);
		background(color(0xff7f7f7f));
		img.pause();
		chromaKey(img.getImage(0), i1);
		chromaKey(img.getImage(1), i2);
		chromaKey(img.getImage(2), i3);
		img.unpause();
		//image(i1, 0, 0);
		//image(i2, CAM_WIDTH, 0);
		//image(i3, 2 * CAM_WIDTH, 0);
		PImage a = new PImage(CAM_WIDTH, CAM_HEIGHT, ARGB);
		a.loadPixels();
		i1.loadPixels();
		i2.loadPixels();
		i3.loadPixels();

		a.updatePixels();
		image(i1,0,CAM_HEIGHT);
		bg2 = i1.get();


		processPixels();
		textAndColors();
	}

	/* (non-Javadoc)
	 * @see processing.core.PApplet#keyPressed()
	 */
	@Override
	public void keyPressed()
	{
		super.keyPressed();
		if (key == CODED && keyPressed) {
			switch (keyCode)
			{
			case ESC: // if ESC is pressed exit program.
				key = 0;
				keyCode = 0;
				endProgram();
				break;
			case DOWN:// if DOWN or RIGHT arrow is pressed increment color selector.
			case RIGHT:
				colorNumber = (colorNumber + 1) % 12;
				break;
			case LEFT: // if UP or LEFT arrow is pressed, decrement color selector.
			case UP:
				colorNumber = colorNumber <= 0? 11: colorNumber - 1;
				break;
			}
		} else {
			switch (key)
			{
			case ESC: // if ESC is pressed exit program.
				key = 0;
				endProgram();
				break;

			case 's':// save current Image to program directory
			case 'S':
				bg1.save(projectPath.getAbsolutePath() + "/" + System.currentTimeMillis() + ".png");
				break;
			case 'p'://pause capture
			case 'P':
				freeze = !freeze;
				break;
			case ENTER://press enter to save a calibration point.
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
				break;
			}
		}
	}

	@Override
	public void mousePressed()
	{
		super.mousePressed();
		if (mouseButton == RIGHT) {
			img.getCam().settings();// click the window to get the settings
		}
	}

	/**
	 * This method is used to parse the XML file that holds the color calibration data.
	 * @param root <code>XMLElement</code> that is the root node of the XML tree.
	 */
	public void parseXml(XMLElement root)
	{
		silver.loadFromXML(root);
		gold.loadFromXML(root);
		black.loadFromXML(root);
		brown.loadFromXML(root);
		red.loadFromXML(root);
		orange.loadFromXML(root);
		yellow.loadFromXML(root);
		green.loadFromXML(root);
		blue.loadFromXML(root);
		violet.loadFromXML(root);
		grey.loadFromXML(root);
		white.loadFromXML(root);
	}



	/**
	 * Worker method that does the actual analysis of the pixels in the image.
	 */
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
			int cHSB = color(hue, sat, brt, 128);
			// cHSB = color(hue())
			fill(cHSB);
			stroke(cHSB);
			// rect(x, 0, STEP_X, CAM_HEIGHT);
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
				temp1 |= colors[i][0] ? 1 << i : 0;
				temp2 |= colors[i][1] ? 1 << i : 0;
			}
			res1.add(temp1);
			res2.add(temp2);
		}

	}

	/**
	 * Method to same the current color to the XML file
	 * @param c color to save.
	 */
	public void saveColor(Color c)
	{
		c.hue.add(hu[0]);
		c.hue.add(hu[1]);
		c.saturation.add(st[0]);
		c.saturation.add(st[1]);
		c.brightness.add(bt[0]);
		c.brightness.add(bt[1]);
		c.red.add(rd[0]);
		c.red.add(rd[1]);
		c.green.add(gn[0]);
		c.green.add(gn[1]);
		c.blue.add(bu[0]);
		c.blue.add(bu[1]);
		c.saveColor(root);
	}



	/* (non-Javadoc)
	 * @see processing.core.PApplet#setup()
	 */
	@Override
	public void setup()
	{

		size(3 * CAM_WIDTH, 2 * CAM_HEIGHT);
		projectPath = sketchFile("").getParentFile();
		colorsXml = projectPath.getPath() + "/colors.xml";
		silver = new Color(this, "silver");
		gold = new Color(this, "gold");
		black = new Color(this, "black");
		brown = new Color(this, "brown");
		red = new Color(this, "red");
		orange = new Color(this, "orange");
		yellow = new Color(this, "yellow");
		green = new Color(this, "green");
		blue = new Color(this, "blue");
		violet = new Color(this, "violet");
		grey = new Color(this, "grey");
		white = new Color(this, "white");

		root = new XMLElement(this, colorsXml);
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

	/**
	 * Prints relevant info to the screen.
	 */
	public void textAndColors()
	{
		pushStyle();
		colorMode(RGB, 255);
		for(int x=0; x <CAM_WIDTH/STEP_X;x++)
		{
			for (int i = 0; i < 12; i++) {
				stroke((res1.getCodes()[x]&1<<i)!=0 ? 0xffffffff : 0xff000000);
				point(x*STEP_X, i);
				stroke((res2.getCodes()[x]&1<<i)!=0 ? 0xffffffff : 0xff000000);
				point(x*STEP_X + 1, i);
			}
		}

		stroke(-1);
		fill(-1);

		text(rgb, 400, CAM_HEIGHT);
		text(hsb, 400, CAM_HEIGHT + 3 * g.textLeading);

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
		text(s, CAM_WIDTH, CAM_HEIGHT + 100);
		text(res1.getValue(), CAM_WIDTH, CAM_HEIGHT + 100 + g.textLeading);
		text(res2.getValue(), CAM_WIDTH, CAM_HEIGHT + 100 + 2 * g.textLeading);
		// text(frameRate,0,10);
		popStyle();
	}

	/**
	 * Converts a standard PImage type pixel array(in RGB) to a 3x pixel array containing HSB values.
	 * output if formatted thus (n is an individual pixel number):
	 * output[0][n] is alpha,
	 * output[1][n] is hue,
	 * output[2][n] is saturation,
	 * output[3][n] is brightness,
	 * output[4][n] is red,
	 * output[5][n] is green,
	 * output[6][n] is blue.
	 * @param input Pixels input
	 * @param output Pixels output
	 */
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
