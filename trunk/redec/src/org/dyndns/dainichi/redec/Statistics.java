package org.dyndns.dainichi.redec;

import java.util.Arrays;

import processing.core.PImage;

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

	public Statistics(ReDec theParent)
	{
		parent = theParent;
	}

	public static float[][] process(ReDec parent, PImage data)
	{

		float[][] values = new float[6][parent.pixels.length];
		float[][] bins = new float[6][256];
		float[] binsMax = { 0, 0, 0, 0, 0, 0 };
		float[] sum = { 0, 0, 0, 0, 0, 0 };
		float[] sumOfSquares = { 0, 0, 0, 0, 0, 0 };
		float[] mean = { 0, 0, 0, 0, 0, 0 };
		float[] median = { 0, 0, 0, 0, 0, 0 };
		float[] sd = { 0, 0, 0, 0, 0, 0 };
		float[] max = { 0, 0, 0, 0, 0, 0 };
		float n = 0;
		for (float[] f : values)
		{
			Arrays.fill(f, 0f);
		}
		data.loadPixels();

		parent.colorMode(ReDec.HSB, 255);
		int i = 0;
		for (int pixel : data.pixels)
		{
			if (parent.alpha(pixel) > 0)
			{
				values[HUE][i] = parent.hue(pixel);
				values[SATURATION][i] = parent.saturation(pixel);
				values[BRIGHTNESS][i] = parent.brightness(pixel);
				values[RED][i] = parent.red(pixel);
				values[GREEN][i] = parent.green(pixel);
				values[BLUE][i++] = parent.blue(pixel);
				n += 1;
				sum[HUE] += parent.hue(pixel);
				sum[SATURATION] += parent.saturation(pixel);
				sum[BRIGHTNESS] += parent.brightness(pixel);
				sum[RED] += parent.red(pixel);
				sum[GREEN] += parent.green(pixel);
				sum[BLUE] += parent.blue(pixel);
				sumOfSquares[HUE] += ReDec.sq(parent.hue(pixel));
				sumOfSquares[SATURATION] += ReDec.sq(parent.saturation(pixel));
				sumOfSquares[BRIGHTNESS] += ReDec.sq(parent.brightness(pixel));
				sumOfSquares[RED] += ReDec.sq(parent.red(pixel));
				sumOfSquares[GREEN] += ReDec.sq(parent.green(pixel));
				sumOfSquares[BLUE] += ReDec.sq(parent.blue(pixel));
				mean[HUE] = sum[HUE] / n;
				mean[SATURATION] = sum[SATURATION] / n;
				mean[BRIGHTNESS] = sum[BRIGHTNESS] / n;
				mean[RED] = sum[RED] / n;
				mean[GREEN] = sum[GREEN] / n;
				mean[BLUE] = sum[BLUE] / n;
				sd[HUE] = ReDec.sqrt((sumOfSquares[HUE] - ReDec.sq(sum[HUE]) / n) / (n - 1));
				sd[SATURATION] = ReDec.sqrt((sumOfSquares[SATURATION] - ReDec.sq(sum[SATURATION]) / n) / (n - 1));
				sd[BRIGHTNESS] = ReDec.sqrt((sumOfSquares[BRIGHTNESS] - ReDec.sq(sum[BRIGHTNESS]) / n) / (n - 1));
				sd[RED] = ReDec.sqrt((sumOfSquares[RED] - ReDec.sq(sum[RED]) / n) / (n - 1));
				sd[GREEN] = ReDec.sqrt((sumOfSquares[GREEN] - ReDec.sq(sum[GREEN]) / n) / (n - 1));
				sd[BLUE] = ReDec.sqrt((sumOfSquares[BLUE] - ReDec.sq(sum[BLUE]) / n) / (n - 1));
				bins[HUE][(int) parent.hue(pixel)]++;
				bins[SATURATION][(int) parent.saturation(pixel)]++;
				bins[BRIGHTNESS][(int) parent.brightness(pixel)]++;
				bins[RED][(int) parent.red(pixel)]++;
				bins[GREEN][(int) parent.green(pixel)]++;
				bins[BLUE][(int) parent.blue(pixel)]++;
			}
		}
		for (float[] f : values)
		{
			Arrays.sort(f);
		}
		if (n % 2 == 1)
		{
			median[HUE] = values[HUE][(int) (n / 2 + 1)];
			median[SATURATION] = values[SATURATION][(int) (n / 2 + 1)];
			median[BRIGHTNESS] = values[BRIGHTNESS][(int) (n / 2 + 1)];
			median[RED] = values[RED][(int) (n / 2 + 1)];
			median[GREEN] = values[GREEN][(int) (n / 2 + 1)];
			median[BLUE] = values[BLUE][(int) (n / 2 + 1)];
		} else
		{
			median[HUE] = (values[HUE][(int) (n / 2)] + values[HUE][(int) (n / 2 + 1)]) / 2f;
			median[SATURATION] = (values[SATURATION][(int) (n / 2)] + values[SATURATION][(int) (n / 2 + 1)]) / 2f;
			median[BRIGHTNESS] = (values[BRIGHTNESS][(int) (n / 2)] + values[BRIGHTNESS][(int) (n / 2 + 1)]) / 2f;
			median[RED] = (values[RED][(int) (n / 2)] + values[RED][(int) (n / 2 + 1)]) / 2f;
			median[GREEN] = (values[GREEN][(int) (n / 2)] + values[GREEN][(int) (n / 2 + 1)]) / 2f;
			median[BLUE] = (values[BLUE][(int) (n / 2)] + values[BLUE][(int) (n / 2 + 1)]) / 2f;
		}
		for (int i1 = 0; i1 < 6; i1++)
		{
			binsMax[i1] = ReDec.max(bins[i1]);
			max[i1] = ReDec.max(values[i1]);
		}

		return new float[][] { mean, median, sd, max, binsMax, bins[HUE], bins[SATURATION], bins[BRIGHTNESS], bins[RED], bins[GREEN], bins[BLUE] };
	}

	public static float[] process(ReDec parent, PImage data, int colorBand)
	{
		assert (colorBand >= 0 && colorBand < 6);
		parent.pushStyle();
		if (colorBand > SATURATION)
			parent.colorMode(ReDec.RGB, 255);
		if (colorBand <= SATURATION)
			parent.colorMode(ReDec.HSB, 255);
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
				if (n <= 1)
					continue;
				if (colorBand == HUE)
					sd = ReDec.sqrt(Color.difference(sumOfSquares, ReDec.sq(sum) / n, 256) / (n - 1));
				else
					sd = ReDec.sqrt((sumOfSquares - ReDec.sq(sum) / n) / (n - 1));

			}
		}

		Arrays.sort(values);

		if (n % 2 == 1)
		{
			median = values[(int) (n / 2 + 1)];
		} else
		{
			median = (values[(int) (n / 2)] + values[(int) (n / 2 + 1)]) / 2f;
		}
		binsMax = ReDec.max(bins);
		max = ReDec.max(values);
		parent.popStyle();

		return ReDec.concat(new float[] { mean, median, sd, max, binsMax }, bins);
	}

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
