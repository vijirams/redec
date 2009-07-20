package org.dyndns.dainichi.redec.applet;

import java.awt.Point;
import java.io.File;
import java.io.PrintWriter;
import java.util.Vector;

import org.dyndns.dainichi.redec.util.PrintfFormat;
import org.dyndns.dainichi.redec.util.Statistics;
import org.dyndns.dainichi.redec.util.objects.Color;
import org.dyndns.dainichi.redec.util.objects.Resistor;
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

	public final int		CAM_HEIGHT	= 240;
	public final int		CAM_WIDTH	= 320;
	public final int		STEP_X		= 1;
	// final boolean DEBUG = true;
	PImage					bg1			= new PImage(CAM_WIDTH, CAM_HEIGHT, ARGB);
	PImage					bg2			= new PImage(CAM_WIDTH, CAM_HEIGHT, ARGB);
	float[]					bt			= new float[] { 0, 0 };
	float[]					bu			= new float[] { 0, 0 };

	int						colorNumber	= 0;
	String					colorString;

	PFont					font;
	public PGraphics		g1;
	public PGraphics		g2;
	public PGraphics		g3;
	public PGraphics		g4;
	public PGraphics		g5;
	public PGraphics		g6;
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

	PImage					i1			= new PImage(CAM_WIDTH, CAM_HEIGHT, ARGB);
	PImage					i2			= new PImage(CAM_WIDTH, CAM_HEIGHT, ARGB);
	PImage					i3			= new PImage(CAM_WIDTH, CAM_HEIGHT, ARGB);

	public Color			violet;
	public Color			silver;
	public Color			black;
	public Color			blue;
	public Color			brown;
	public Color			gold;
	public Color			green;
	public Color			grey;
	public Color			local;
	public Color			orange;
	public Color			red;
	public Color			white;
	public Color			yellow;
	public boolean			freeze;
	Resistor				res1;													// =
																					// new
																					// Resistor(this);
	Resistor				res2;													// =
																					// new
																					// Resistor(this);
	File					projectPath;
	String					colorsXml;

	/**
	 * Called to shut down the program. Duties include proper termination of
	 * various threads and writing the color tree to persistant storage.
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
	 * Dees a ChromaKey of the source image. Also doubles as an array copy
	 * utility. copies pixels from <code>src</code> to <code>dest</code> if the
	 * input pixels obey some preset rules. Otherwise the equivalent pixel in
	 * <code>dest</code> is set to all black, and maximum transparency.
	 *
	 * @param src
	 *            Input image.
	 * @param dest
	 *            Output PImage.
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
				if (c[2] > 64) {
					// true) {
					dest.pixels[desti] = color(c[0], c[1], c[2], c[3]);
				} else {
					dest.pixels[desti] = 0;
				}
			}
		}
		popStyle();
		dest.updatePixels();
	}

	private PImage	grey_img;
	double			max			= 0;
	double			m			= 0;
	double			b			= 0;
	int				coordsSize	= 32000;

	/*
	 * (non-Javadoc)
	 *
	 * @see processing.core.PApplet#draw()
	 */
	@Override
	public void draw()
	{
		colorMode(RGB, 255);
		fill(color(0xff000000));
		stroke(color(0xff000000));
		// rect(0, 0, width, height);
		background(color(0xff000000));
		img.pause();

		chromaKey(img.getImage(), i1);
		img.unpause();
		// image(i1, 0, 0);
		// image(i2, CAM_WIDTH, 0);
		// image(i3, 2 * CAM_WIDTH, 0);
		i1.loadPixels();
		i2.loadPixels();
		i3.loadPixels();

		image(i1, 0, 0);// CAM_HEIGHT);
		bg2 = i1.get();
		grey_img = i1.get();
		// grey_img.filter(GRAY);
		grey_img.loadPixels();

		int[] temp = new int[CAM_WIDTH * CAM_HEIGHT];

		// 3 10 3
		// 0 0 0
		// -3 -10 -3
		int h = i1.width;
		Vector<Point> coords = new Vector<Point>(coordsSize);
		double localMax = 0;
		pushStyle();
		colorMode(HSB, 1);
		for (int y = 1; y < i1.height - 1; y++) {
			for (int x = 1; x < i1.width - 1; x++) {

				double tempx = 3 * saturation(grey_img.pixels[x - 1 + (y - 1) * h]) + 10 * saturation(grey_img.pixels[x - 1 + y * h]) + 3
						* saturation(grey_img.pixels[x - 1 + (y + 1) * h]) + -3 * saturation(grey_img.pixels[x + 1 + (y - 1) * h]) + -10
						* saturation(grey_img.pixels[x + 1 + y * h]) + -3 * saturation(grey_img.pixels[x + 1 + (y + 1) * h]);
				double tempy = 3 * saturation(grey_img.pixels[x - 1 + (y - 1) * h]) + 10 * saturation(grey_img.pixels[x + (y - 1) * h]) + 3
						* saturation(grey_img.pixels[x + 1 + (y - 1) * h]) + -3 * saturation(grey_img.pixels[x - 1 + (y + 1) * h]) + -10
						* saturation(grey_img.pixels[x + (y + 1) * h]) + -3 * saturation(grey_img.pixels[x + 1 + (y + 1) * h]);
				double mag = Math.sqrt(tempx * tempx + tempy * tempy);
				localMax = Math.max(localMax, mag);
				// double theta = Math.atan(tempy/tempx);
				int v = (int) map(mag, 0, max, 0, 255);
				if (/* (Math.abs(theta)<=0.3&& */mag > max * .1) {
					temp[x + y * h] = 0xff000000 | (v & 0xff) << 16 | (0xff & v) << 8 | v & 0xff;
					coords.add(new Point(x, y));
				} else {
					temp[x + y * h] = 0 | (v & 0xff) << 16 | (0xff & v) << 8 | v & 0xff;
				}

			}

		}
		popStyle();
		coordsSize = max(coordsSize, coords.capacity());
		max = localMax;
		LSRLineFit(coords);

		grey_img.pixels = temp;
		grey_img.updatePixels();
		// grey_img.filter(THRESHOLD, 0.5f);
		image(grey_img, 0, 0);

		Vector<Integer> trans = new Vector<Integer>();
		double justadded = 0;
		double rate = .05;
		if (m < 1 || m > -1) {
			int y;
			for (int x = 0; x < CAM_WIDTH; x++) {
				y = lsrLine(x);
				y = constrain(y, 0, CAM_HEIGHT - 1);
				if (temp[x + h * y] < 0 && justadded < .5) {
					trans.add(x);
					justadded += 1;
				}
				if (justadded > 0) {
					justadded -= rate;
				}

			}
		} else {
			int x;
			for (int y = 0; y < CAM_HEIGHT; y++) {
				x = (int) ((y - b) / m);
				x = constrain(x, 0, CAM_HEIGHT - 1);
				if (temp[x + h * y] < 0 && justadded < .5) {
					trans.add(x);
					justadded += 1;
				}
				if (justadded > 0) {
					justadded -= rate;
				}
			}
		}

		bands = new Vector<Point>();
		int offset =30;// (int) (30 * Math.cos(Math.atan(m)));
		for (int i : trans) {
			bands.add(intersect(0, lsrLine(0) - offset, 320, lsrLine(320) - offset, 0, normalLine(0, i), 320, normalLine(320, i)));
			bands.add(intersect(0, lsrLine(0) + offset, 320, lsrLine(320) + offset, 0, normalLine(0, i), 320, normalLine(320, i)));

		}

		processPixels(bg2);
		textAndColors();
		stroke(-1);
		line(0, lsrLine(0) - offset, 320, lsrLine(320) - offset);
		line(0, lsrLine(0), 320, lsrLine(320));
		line(0, lsrLine(0) + offset, 320, lsrLine(320) + offset);

	}

	Vector<Point>	bands;

	private Point intersect(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4)
	{
		int x = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
		int y = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
		x = constrain(x, 0, CAM_WIDTH-1);
		y = constrain(y, 0, CAM_HEIGHT-1);
		assert x>=0 && y>=0:"undershoot";
		assert x< CAM_WIDTH&&  y< CAM_HEIGHT:"overshoot";
		return new Point(x,y);
	}

	/**
	 * @param coords
	 */
	private void LSRLineFit(Vector<Point> coords)
	{
		double x = 0, y = 0, xx = 0, xy = 0;
		for (Point p : coords) {
			x += p.x;
			y += p.y;
			xx += p.x * p.x;
			xy += p.x * p.y;
		}
		double n = coords.size();
		m = (n * xy - y * x) / (n * xx - x * x);
		b = y / n - m * x / n;
	}

	private float intersectX(float x, float y)
	{
		return (float) (y * m - b * m + x);
	}

	private int lsrLine(float x)
	{
		return (int) (m * x + b);
	}

	private int normalLine(float x, float intersect)
	{
		return (int) ((intersect - x) / m + b);
	}

	/*
	 * (non-Javadoc)
	 *
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
			case DOWN:// if DOWN or RIGHT arrow is pressed increment color
						// selector.
			case RIGHT:
				colorNumber = (colorNumber + 1) % 12;
				break;
			case LEFT: // if UP or LEFT arrow is pressed, decrement color
						// selector.
			case UP:
				colorNumber = colorNumber <= 0 ? 11 : colorNumber - 1;
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
			case 'r':
			case 'R':
				zeroColors();
				break;
			case 'p':// pause capture
			case 'P':
				freeze = !freeze;
				break;
			case ENTER:// press enter to save a calibration point.
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
		if (mouseButton == LEFT) {
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
		}
	}

	/**
	 * This method is used to parse the XML file that holds the color
	 * calibration data.
	 *
	 * @param root
	 *            <code>XMLElement</code> that is the root node of the XML tree.
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

	public PImage getQuad(Point[] pts, PImage src)
	{
		assert pts.length == 4;
		src.loadPixels();
		float[] xs = new float[] { pts[0].x, pts[1].x, pts[2].x, pts[3].x };
		float[] ys = new float[] { pts[0].y, pts[1].y, pts[2].y, pts[3].y };

		float[] bounds = new float[] { max(0,min(xs)), max(0,min(ys)), min(src.width,max(xs)), max(src.height,max(ys)) };
		PImage ret = createImage((int)(bounds[2] - bounds[0]), (int)(bounds[3] - bounds[1]), ARGB);
		ret.loadPixels();
		float[] m = new float[] { (ys[1] - ys[0]) / (xs[1] - xs[0]), (ys[2] - ys[1]) / (xs[2] - xs[1]), (ys[3] - ys[2]) / (xs[3] - xs[2]), (ys[0] - ys[3]) / (xs[0] - xs[3]) };
		for (int y = (int) bounds[1];y< bounds[3];y++) {
			for (int x = (int) bounds[0]; x < bounds[2]; x++) {

				ret.pixels[(int) (x - bounds[0] + (y - bounds[1]) * ret.width)] = isInside(pts, x, y) ? src.pixels[x + y * src.width] : 0;
			}
		}
		ret.updatePixels();
		return ret;
	}
	/**
	 * set too true to allow running without a serial device.
	 */
	public boolean standalone = false;
	private boolean isInside(Point[] pts,int x,int y)
	{
		assert pts.length ==4;
		float[] xs = new float[]{pts[0].x,pts[1].x,pts[2].x,pts[3].x};
		float[] ys = new float[]{pts[0].y,pts[1].y,pts[2].y,pts[3].y};

		float[] bounds = new float[]{min(xs),min(ys),max(xs),max(ys)};
		float[] m = new float[]{
				(ys[1]-ys[0])/(xs[1]-xs[0]),
				(ys[2]-ys[1])/(xs[2]-xs[1]),
				(ys[3]-ys[2])/(xs[3]-xs[2]),
				(ys[0]-ys[3])/(xs[0]-xs[3])};
		boolean inside = false;
		if(line(x,xs[0],ys[0],xs[1],ys[1])<y)
		{
			if(m[3] < 0)
			{
				if(line(x,xs[0],ys[0],xs[3],ys[3])<y)
				{
					if(m[1]<0)
					{
						if(line(x,xs[1],ys[1],xs[2],ys[2])>y)
						{
							if(line(x,xs[2],ys[2],xs[3],ys[3])>y)
							{
								inside = true;
							}
						}
					}
					else
					{
						if(line(x,xs[1],ys[1],xs[2],ys[2])<y)
						{
							if(line(x,xs[2],ys[2],xs[3],ys[3])>y)
							{
								inside = true;
							}
						}
					}
				}

			}
			else
			{
				if(m[1]<0)
				{
					if(line(x,xs[1],ys[1],xs[2],ys[2])>y)
					{
						if(line(x,xs[2],ys[2],xs[3],ys[3])>y)
						{
							inside = true;
						}
					}
				}
				else
				{
					if(line(x,xs[1],ys[1],xs[2],ys[2])<y)
					{
						if(line(x,xs[2],ys[2],xs[3],ys[3])>y)
						{
							inside = true;
						}
					}
				}
			}
		}
		return inside;
	}

	private float line(float x, float x1, float y1, float x2, float y2)
	{
		return y1 + (x - x1) * (y2 - y1) / (x2 - x1);
	}

	/**
	 * Worker method that does the actual analysis of the pixels in the image.
	 */
	public void processPixels(PImage img)
	{
		boolean[][] colors = new boolean[12][2];
		res1 = new Resistor(this);
		res2 = new Resistor(this);

		for (int i = 0; i+3 < bands.size()&& i+3<12; i += 4) {
			Point[] points = new Point[] { bands.get(i), bands.get(i + 1), bands.get(i + 2), bands.get(i + 3) };
			PImage band = getQuad(points, img);
			float[][] valsRGB = new float[][] { Statistics.process(this, band, Statistics.RED), Statistics.process(this, band, Statistics.GREEN),
					Statistics.process(this, band, Statistics.BLUE) };
			float[][] valsHSB = new float[][] { Statistics.process(this, band, Statistics.HUE), Statistics.process(this, band, Statistics.SATURATION),
					Statistics.process(this, band, Statistics.BRIGHTNESS) };
			float[][] valss = new float[valsHSB.length + valsRGB.length][];
			for (int j = 0; j < valss.length; j++) {
				if (j < valsHSB.length) {
					valss[j] = valsHSB[j];
				} else {
					valss[j] = valsRGB[j % valsHSB.length];
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
			if (isInside(points, mouseX, mouseY)) {
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
			for (int j = 0; j < 12; j++) {
				temp1 |= colors[j][0] ? 1 << j : 0;
				temp2 |= colors[j][1] ? 1 << j : 0;
			}
			res1.add(temp1);
			res2.add(temp2);

		}

	}

	/**
	 * Method to same the current color to the XML file
	 *
	 * @param c
	 *            color to save.
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

	/*
	 * (non-Javadoc)
	 *
	 * @see processing.core.PApplet#setup()
	 */
	@Override
	public void setup()
	{

		size(2 * CAM_WIDTH, 1 * CAM_HEIGHT);
		projectPath = sketchFile("").getParentFile();
		colorsXml = projectPath.getPath() + "/colors.xml";
		zeroColors();

		root = new XMLElement(this, colorsXml);
		parseXml(root);
		colorMode(RGB, 255, 255, 255, 255);
		background(0xff7f7f7f);

		font = loadFont("../CenturySchoolbook-16.vlw");
		textFont(font);

		img = new ImageAquisition(this, CAM_WIDTH, CAM_HEIGHT);
		img.start();
		// thread.start();

	}

	private void zeroColors()
	{
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
	}

	/**
	 * Prints relevant info to the screen.
	 */
	public void textAndColors()
	{
		pushStyle();
		colorMode(RGB, 255);
		for (int x = 0; x < res1.getCodes().length; x++) {
			for (int i = 0; i < 12; i++) {
				stroke((res1.getCodes()[x] & 1 << i) != 0 ? 0xffffffff : 0xff000000);
				point(x * STEP_X, i);
				stroke((res2.getCodes()[x] & 1 << i) != 0 ? 0xffffffff : 0xff000000);
				point(x * STEP_X, CAM_HEIGHT - 12 + i);
			}
		}

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
		stroke(-1);
		fill(-1);

		text(rgb, 400, 0);// CAM_HEIGHT);
		text(hsb, 400, 0 + 3 * g.textLeading);

		text(s, CAM_WIDTH, 100);
		text(res1.getValue(), CAM_WIDTH, 100 + g.textLeading);
		text(res2.getValue(), CAM_WIDTH, 100 + 2 * g.textLeading);
		text(frameRate, 400, 7 * g.textLeading);
		popStyle();
	}

	/**
	 * Converts a standard PImage type pixel array(in RGB) to a 3x pixel array
	 * containing HSB values. output if formatted thus (n is an individual pixel
	 * number): output[0][n] is alpha, output[1][n] is hue, output[2][n] is
	 * saturation, output[3][n] is brightness, output[4][n] is red, output[5][n]
	 * is green, output[6][n] is blue.
	 *
	 * @param input
	 *            Pixels input
	 * @param output
	 *            Pixels output
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
