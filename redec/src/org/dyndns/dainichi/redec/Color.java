package org.dyndns.dainichi.redec;

import processing.core.PApplet;

public class Color
{
	private ReDec	parent;
	private float	red;
	private float	green;
	private float	blue;
	private float[]	sd;

	public Color(ReDec theParent, float[] color, float[] sd)
	{
		parent = theParent;
		red = color[0];
		green = color[1];
		blue = color[2];
		this.sd = sd;
	}

	public static int alpha(int color)
	{
		return (color >> 24) & 0xff;
	}

	public static int red(int color)
	{
		return (color >> 16) & 0xff;
	}

	public static int green(int color)
	{
		return (color >> 8) & 0xff;
	}

	public static int blue(int color)
	{
		return color & 0xff;
	}

	public static int color(int a, int r, int g, int b)
	{
		return (a & 0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
	}

	public static float difference(float a, float b, float max)
	{
		float result = 0f;
		if (a > b)
			result = max - ((a + max - b) % max);
		else if (b > a)
			result = max - ((b + max - a) % max);
		if (result > max / 2)
			result = (max) - result;
		return (result);
	}

	public boolean isInRange(float[] color, int colorMode)
	{
		if (colorMode == ReDec.RGB)
			return (ReDec.abs(red - color[0]) <= sd[0]) && (ReDec.abs(green - color[1]) <= sd[1]) && (ReDec.abs(blue - color[2]) <= sd[2]);
		else
			return (ReDec.abs(difference(red, color[0], 256)) <= sd[0]) && (ReDec.abs(green - color[1]) <= sd[1]) && (ReDec.abs(blue - color[2]) <= sd[2]);
	}

	public int color()
	{
		return parent.color(red, green, blue);
	}

}
