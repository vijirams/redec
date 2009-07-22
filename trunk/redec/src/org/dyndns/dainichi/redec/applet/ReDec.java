package org.dyndns.dainichi.redec.applet;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D.Float;
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
				if (c[2] > 70) {
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
		bands = new Vector<Polygon>();
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
		Vector<Point> coords = new Vector<Point>(coordsSize);
		float localMax = 0;
		pushStyle();
		convertToBW(grey_img);
		colorMode(HSB, 1);
		float[] mag = new float[temp.length];
		float[] theta = new float[temp.length];
		localMax = sobel(grey_img, mag, theta);

		// add coordinate of all edges for lsr guess.
		for (int y = 1; y < i1.height - 1; y++) {
			for (int x = 1; x < i1.width - 1; x++) {
				int i = x + y * i1.width;
				if (mag[i] > localMax * .3) {
					coords.add(new Point(x, y));
				}
			}
		}
		LSRLineFit(coords);
		Line2D.Float center = new Line2D.Float(0, lsrLine(0, m, b), 320, lsrLine(320, m, b));
		Vector<Line2D.Float> lines = new Vector<Line2D.Float>();
		lines.add((java.awt.geom.Line2D.Float) relative(center, -30));
		lines.add((java.awt.geom.Line2D.Float) relative(center, 30));
		lines.add(center);
		float lineangle = (float) Math.atan(m) + PI / 2f;

		for (int i = 0; i < temp.length; i++) {
			int v;
			if (Color.difference(theta[i] + PI / 2f, lineangle, PI) < Math.toRadians(10d) && mag[i] > .07 * localMax) {
				v = 255;// (int) map(theta[i],-HALF_PI,HALF_PI,0,255);
			} else {
				v = 0;
			}
			// v = v| v<<8|v<<16;
			temp[i] = v == 255 ? -1 : 0;// | v;

		}
		int polyLength = 0;
		Polygon bnd = new Polygon();
		boolean startOfBand = true;
		boolean midOfBand = false;
		boolean prev = false;
		boolean extend = false;
		boolean current = false;
		int edgeCount = 0;
		int step = 3;
		Line2D.Float front;// = normalLine2D(0);
		for (int x = 0; x < CAM_WIDTH; x+=step) {
			front = normalLine2D(x);
			Line2D.Float front2 = (Float) relative(front, -step);
			Polygon p = new Polygon();
			Point2D.Float pp = (java.awt.geom.Point2D.Float) intersect(front, lines.get(0));
			p.addPoint((int)pp.x,(int) pp.y);
			pp = (java.awt.geom.Point2D.Float) intersect(front2, lines.get(0));
			p.addPoint((int)pp.x,(int) pp.y);
			pp = (java.awt.geom.Point2D.Float) intersect(front2, lines.get(1));
			p.addPoint((int)pp.x,(int) pp.y);
			pp = (java.awt.geom.Point2D.Float) intersect(front, lines.get(1));
			p.addPoint((int)pp.x,(int) pp.y);
			prev = current;
			current = getNumInQuad(p, temp, CAM_WIDTH) > 15;
			if(current) {
				lines.add(front);
				lines.add(front2);
			}
			if(!prev && !current) //zero
			{

			}
			else if(!prev && current)//pos edge
			{
				if(startOfBand )
				{
					bnd = new Polygon();
					pp = (java.awt.geom.Point2D.Float) intersect(front, lines.get(0));
					bnd.addPoint((int)pp.x, (int)pp.y);
					pp = (java.awt.geom.Point2D.Float) intersect(front, lines.get(1));
					bnd.addPoint((int)pp.x, (int)pp.y);
					startOfBand = false;
					midOfBand = true;

				}
				edgeCount++;
			}
			else if(prev && !current)//neg edge
			{
				if(edgeCount%2==0)
				{
					startOfBand = true;
					extend = false;
					midOfBand = false;
					pp = (java.awt.geom.Point2D.Float) intersect(front2, lines.get(1));
					bnd.addPoint((int)pp.x, (int)pp.y);
					pp = (java.awt.geom.Point2D.Float) intersect(front2, lines.get(0));
					bnd.addPoint((int)pp.x, (int)pp.y);
					bands.add(bnd);
				}
			}
			else//(prev && current) // one
			{

			}


		}

		popStyle();
		coordsSize = max(coordsSize, coords.capacity());
		max = localMax;


		grey_img.pixels = temp;grey_img.updatePixels();image(grey_img, 0, 0);

		//}



		processPixels(bg2);
		textAndColors();
//		for (Line2D l : lines) {
//			drawLine(l, 0xff7f7f7f);
//		}

	}

	private Line2D relative(Line2D input, float L)
	{
		float h2 = L;
		float m = (float) ((input.getY2() - input.getY1()) / (input.getX2() - input.getX1()));
		if (m != 0) {
			m = -1 / m;
			h2 = (float) (L * Math.sqrt(m * m + 1) / m);
		}
		Line2D.Float l = new Line2D.Float((float) input.getX1(), (float) input.getY1() + h2, (float) input.getX2(), (float) input.getY2() + h2);

		return l;
	}
	private Line2D.Float normalLine2D(float x)
	{
		return new Line2D.Float(0,normalLine(0, x, (float) (m*x+b), m),320,normalLine(320, x, (float) (m*x+b), m));
	}
	private float sobel(PImage input, float[] mag, float[] theta)
	{
		input.loadPixels();
		int h = input.width;
		float localMax = 0;
		for (int y = 1; y < input.height - 1; y++) {
			for (int x = 1; x < input.width - 1; x++) {
				int i = x + y * input.width;
				float tempx = 3 * (0xff & input.pixels[i - 1 - h]) + 10 * (0xff & input.pixels[i - 1]) + 3 * (0xff & input.pixels[i - 1 + h]) - 3
						* (0xff & input.pixels[i + 1 - h]) - 10 * (0xff & input.pixels[i + 1]) - 3 * (0xff & input.pixels[i + 1 + h]);
				float tempy = 3 * (0xff & input.pixels[i - 1 - h]) + 10 * (0xff & input.pixels[i - h]) + 3 * (0xff & input.pixels[i - h + 1]) - 3
						* (0xff & input.pixels[i - 1 + h]) - 10 * (0xff & input.pixels[i + h]) - 3 * (0xff & input.pixels[i + 1 + h]);
				mag[i] = (float) Math.sqrt(tempx * tempx + tempy * tempy);
				localMax = Math.max(localMax, mag[i]);
				theta[i] = (float) Math.atan(tempy / tempx);
			}
		}
		input.updatePixels();
		return localMax;
	}

	/**
	 * Converts a given image to black and white in a manner that saves gold and
	 * yellow.
	 *
	 * @param input
	 *            image to convert.
	 */
	private void convertToBW(PImage input)
	{
		input.loadPixels();
		pushStyle();
		colorMode(HSB, 1);
		for (int i = 0; i < input.pixels.length; i++) {
			int pixel = input.pixels[i];
			int v = constrain((int) (255 * brightness(pixel) - 196 * saturation(pixel)), 0, 255);
			input.pixels[i] = 0xff000000 | (v & 0xff) << 16 | (0xff & v) << 8 | v & 0xff;
		}
		popStyle();
		input.updatePixels();
	}

	Vector<Polygon>	bands;

	private void drawLine(Line2D l, int color)
	{
		pushStyle();
		stroke(color);
		line((float) l.getX1(), (float) l.getY1(), (float) l.getX2(), (float) l.getY2());
		popStyle();

	}

	private Point2D intersect(Line2D.Float l1, Line2D.Float l2)
	{

		return intersect(l1.x1, l1.y1, l1.x2, l1.y2, l2.x1, l2.y1, l2.x2, l2.y2);

	}

	/**
	 * Returns the intersection of two lines defined with a set of coordinates
	 *
	 * @param x1
	 *            Line1 x1
	 * @param y1
	 *            Line1 y1
	 * @param x2
	 *            Line1 x2
	 * @param y2
	 *            Line1 y2
	 * @param x3
	 *            Line2 x1
	 * @param y3
	 *            Line2 y1
	 * @param x4
	 *            Line2 x2
	 * @param y4
	 *            Line2 y2
	 * @return the intersection of these two lines.
	 */
	private Point2D.Float intersect(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4)
	{
		float x = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
		float y = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
		return new Point2D.Float(x, y);
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

	private int lsrLine(float x, double m2, double b2)
	{
		return (int) (m2 * x + b2);
	}

	private int lsrLineInv(float y, double m, double b)
	{
		return (int) ((y - b) / m);
	}

	private int normalLine(float x, float ix, float iy, double m)
	{
		return (int) (iy + (ix - x) / m);
	}

	private int normalLineInv(float y, float ix, float iy, double m)
	{
		return (int) (-(y - m * iy - ix) / m);
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

	public int getNumInQuad(Polygon p, int[] src, int width)
	{
		int count = 0;
		Rectangle bounds = p.getBounds();
		bounds.x = bounds.x < 0 ? 0 : bounds.x >= CAM_WIDTH ? CAM_WIDTH - 1 : bounds.x;
		bounds.y = bounds.y < 0 ? 0 : bounds.y >= CAM_HEIGHT ? CAM_HEIGHT - 1 : bounds.y;
		bounds.width = bounds.x + bounds.width >= CAM_WIDTH ? CAM_WIDTH - bounds.x - 1 : bounds.width;
		bounds.height = bounds.y + bounds.height >= CAM_HEIGHT ? CAM_HEIGHT - bounds.y - 1 : bounds.height;
		for (int y = bounds.y; y <= bounds.y + bounds.height; y++) {
			for (int x = bounds.x; x <= bounds.x + bounds.width; x++) {
				if (p.contains(x, y)) {
					count = src[x + y * width] < 0 ? count + 1 : count;
				}
			}
		}
		return count;
	}

	public PImage getQuad(Polygon p, PImage src)
	{
		src.loadPixels();
		Rectangle bounds = p.getBounds();
		bounds.x = bounds.x < 0 ? 0 : bounds.x >= CAM_WIDTH ? CAM_WIDTH - 1 : bounds.x;
		bounds.y = bounds.y < 0 ? 0 : bounds.y >= CAM_HEIGHT ? CAM_HEIGHT - 1 : bounds.y;
		bounds.width = bounds.x + bounds.width >= CAM_WIDTH ? CAM_WIDTH - bounds.x - 1 : bounds.width < 0 ? 0 : bounds.width;
		bounds.height = bounds.y + bounds.height >= CAM_HEIGHT ? CAM_HEIGHT - bounds.y - 1 : bounds.height < 0 ? 0 : bounds.height;
		PImage ret = createImage(bounds.width, bounds.height, ARGB);
		ret.loadPixels();
		for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
			for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
				ret.pixels[x - bounds.x + (y - bounds.y) * ret.width] = p.contains(x, y) ? src.pixels[x + y * src.width] : 0;
			}
		}
		ret.updatePixels();
		return ret;
	}

	/**
	 * set too true to allow running without a serial device.
	 */
	public boolean	standalone	= false;

	private void drawPoly(Polygon p, int color)
	{
		pushStyle();

		stroke(color);
		fill(color&0xffffff,0x7f);
		beginShape();
		for (int i = 0; i < p.npoints; i++) {
			vertex(p.xpoints[i], p.ypoints[i]);
		}
		endShape(CLOSE);
		popStyle();
	}

	/**
	 * Worker method that does the actual analysis of the pixels in the image.
	 */
	public void processPixels(PImage img)
	{
		boolean[][] colors = new boolean[12][2];
		res1 = new Resistor(this);
		res2 = new Resistor(this);

		for (int i = 0; i < bands.size(); i++) {
			stroke(0xffffff00);
			drawPoly(bands.get(i), -1);
			PImage band = getQuad(bands.get(i), img);
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
			if (bands.get(i).contains(mouseX, mouseY)) {
				rd[0] = stats[0].add(valsRGB[0][Statistics.MEAN])[Statistics.MEAN];
				rd[1] = stats[0].add(valsRGB[0][Statistics.MEAN])[Statistics.SD];
				gn[0] = stats[1].add(valsRGB[1][Statistics.MEAN])[Statistics.MEAN];
				gn[1] = stats[1].add(valsRGB[1][Statistics.MEAN])[Statistics.SD];
				bu[0] = stats[2].add(valsRGB[2][Statistics.MEAN])[Statistics.MEAN];
				bu[1] = stats[2].add(valsRGB[2][Statistics.MEAN])[Statistics.SD];

				hu[0] = stats[3].add(valsHSB[0][Statistics.MEAN])[Statistics.MEAN];
				hu[1] = stats[3].add(valsHSB[0][Statistics.MEAN])[Statistics.SD];
				st[0] = stats[4].add(valsHSB[1][Statistics.MEAN])[Statistics.MEAN];
				st[1] = stats[4].add(valsHSB[1][Statistics.MEAN])[Statistics.SD];
				bt[0] = stats[5].add(valsHSB[2][Statistics.MEAN])[Statistics.MEAN];
				bt[1] = stats[5].add(valsHSB[2][Statistics.MEAN])[Statistics.SD];

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
