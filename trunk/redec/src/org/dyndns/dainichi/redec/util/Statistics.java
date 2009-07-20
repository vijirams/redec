package org.dyndns.dainichi.redec.util;

import java.util.Arrays;

import org.dyndns.dainichi.redec.applet.ReDec;
import org.dyndns.dainichi.redec.util.objects.Color;

import processing.core.PImage;

/**
 * Class used to do statistics.
 * @author dejagerd
 *
 */
public class Statistics
{
	private ReDec			parent;
	public int[]			bins;
	public Float[]			sample;

	public static final int	HUE			= 0;
	public static final int	SATURATION	= 1;
	public static final int	BRIGHTNESS	= 2;
	public static final int	RED			= 3;
	public static final int	GREEN		= 4;
	public static final int	BLUE		= 5;
	public static final int	MEAN		= 0;
	public static final int	MEDIAN		= 1;
	public static final int	SD			= 2;
	public static final int	MAX			= 3;
	public static final int	BINS		= 4;
	public static final int	BINS_MAX	= 5;

	/**
	 * Create a new Statistics object.
	 * @param theParent
	 */
	public Statistics(ReDec theParent)
	{
		parent = theParent;
	}

	/**
	 * Static method used to perform my statistical analysis on a whole image at once, but only in one color band.
	 * @param parent reference to the parent object
	 * @param data Image to process
	 * @param colorBand band to process in.
	 * @return analysis
	 */
	public static float[] process(ReDec parent, PImage data, int colorBand)
	{
		assert colorBand >= 0 && colorBand < 6;
		parent.pushStyle();
		if (colorBand > SATURATION) {
			parent.colorMode(ReDec.RGB, 255);
		}
		if (colorBand <= SATURATION) {
			parent.colorMode(ReDec.HSB, 255);
		}
		float[] values = new float[data.pixels.length];
		float[] bins = new float[256];
		float binsMax = 0;
		float sum = 0;
		float sumOfSquares = 0;
		float mean = 0;
		float median = 0;
		float sd = 0;
		float snr = 0;
		float max = 0;
		float n = 0;

		data.loadPixels();

		parent.colorMode(ReDec.HSB, 255);
		float localVal;
		for (int pixel : data.pixels)
		{
			if (pixel < 0)
			{
				switch (colorBand)
				{
				case HUE:
					localVal = parent.hue(pixel);
					break;
				case SATURATION:
					localVal = parent.saturation(pixel);
					break;
				case BRIGHTNESS:
					localVal = parent.brightness(pixel);
					break;
				case RED:
					localVal = parent.red(pixel);
					break;
				case GREEN:
					localVal = parent.green(pixel);
					break;
				case BLUE:
					localVal = parent.blue(pixel);
					break;
				default:
					localVal = Float.NaN;
				}
				values[(int) n] = localVal;
				n += 1;
				sum += localVal;
				sumOfSquares += ReDec.sq(localVal);
				mean = sum / n;
				bins[(int) localVal]++;
				if (n <= 1) {
					continue;
				}
				if (colorBand == HUE) {
					sd = ReDec.sqrt(Color.difference(sumOfSquares, ReDec.sq(sum) / n, 256) / (n - 1));
				} else {
					sd = ReDec.sqrt((sumOfSquares - ReDec.sq(sum) / n) / (n - 1));
				}

			}
		}

		Arrays.sort(values);
		if(values.length >0){
		if (n % 2 == 1)
		{
			median = values[(int) (n / 2 + 1)];
		} else
		{
			median = (values[(int) (n / 2)] + values[(int) (n / 2 + 1)]) / 2f;
		}
		binsMax = ReDec.max(bins);
		max = ReDec.max(values);
		}
		parent.popStyle();

		return ReDec.concat(new float[] { mean, median, sd, max, binsMax }, bins);
	}

	/**
	 * zeros out the accumulators for the stats class.
	 */
	public void zero()
	{
		sum = 0;
		sumOfSquares = 0;
		mean = 0;
		sd = 0;
		max = 0;
		n = 0;

	}

	public float	sum				= 0;
	public float	sumOfSquares	= 0;
	public float	mean			= 0;
	float			sd				= 0;
	float			max				= 0;
	float			n				= 0;

	/**
	 * Adds a datapoint to this Statistics object.
	 * @param dp Datapoint to add.
	 * @return results of the addition to this stats.
	 */
	public float[] add(float dp)
	{
		n += 1;
		sum += dp;
		sumOfSquares += ReDec.sq(dp);
		mean = sum / n;
		if (n > 1)
		{
			sd = ReDec.sqrt((sumOfSquares - ReDec.sq(sum) / n) / (n - 1));
		}
		return new float[] { mean, Float.NaN, sd, Float.NaN, Float.NaN, Float.NaN, Float.NaN };
	}
}
