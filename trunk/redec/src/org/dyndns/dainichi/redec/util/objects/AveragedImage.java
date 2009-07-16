package org.dyndns.dainichi.redec.util.objects;

import org.dyndns.dainichi.redec.applet.ReDec;

import processing.core.PImage;

/**
 * Stores a PImage that is the average of the last few images stored.
 * @author dejagerd
 *
 */
public class AveragedImage
{
	ReDec		parent;
	int			width;
	int			height;
	int			index	= 0;
	private int	frames;
	byte[][][]	images;

	/**
	 * Instatiates a new Average Image.
	 * @param parent Parent ReDec
	 * @param width Width of image
	 * @param height Height of image
	 * @param framesInHistory Number of images to average together.
	 */
	public AveragedImage(ReDec parent, int width, int height, int framesInHistory)
	{
		this.parent = parent;
		this.width = width;
		this.height = height;
		frames = framesInHistory;
		images = new byte[4][width * height][framesInHistory];
	}

	/**
	 * Adds a new image to the average, replacing the oldest.
	 * @param img Image to add.
	 */
	public synchronized void addImage(int[] img)
	{

		index = (index + 1) % frames;
		assert index < frames && index >= 0;
		int pixel = 0;
		for (int i = 0; i < img.length; i++)
		{
			pixel = img[i];
			images[0][i][index] = (byte) (pixel >> 24);
			images[1][i][index] = (byte) (pixel >> 16 & 0xff);
			images[2][i][index] = (byte) (pixel >> 8 & 0xff);
			images[3][i][index] = (byte) (pixel & 0xff);
		}
	}

	/**
	 * Same as addImage(int[])
	 * @param img Pimage to use.
	 */
	public void addImage(PImage img)
	{
		img.loadPixels();
		index = (index + 1) % frames;
		int pixel = 0;
		for (int i = 0; i < img.pixels.length; i++)
		{
			pixel = img.pixels[i];
			images[0][i][index] = (byte) (pixel >> 24);
			images[1][i][index] = (byte) (pixel >> 16 & 0xff);
			images[2][i][index] = (byte) (pixel >> 8 & 0xff);
			images[3][i][index] = (byte) (pixel & 0xff);
		}
	}

	/**
	 * Averages the previous Images added, and returns it.
	 * @return new Pimage containing the average of the images stored herein.
	 */
	public synchronized PImage getImage()
	{
		PImage out = new PImage(width, height, ReDec.ARGB);
		out.loadPixels();
		int suma = 0;
		int sumb = 0;
		int sumc = 0;
		int sumd = 0;
		for (int i = 0; i < out.pixels.length; i++)
		{
			suma = 0;
			sumb = 0;
			sumc = 0;
			sumd = 0;
			for (byte b : images[0][i])
			{
				suma += b;
			}
			for (byte b : images[1][i])
			{
				sumb += b;
			}
			for (byte b : images[2][i])
			{
				sumc += b;
			}
			for (byte b : images[3][i])
			{
				sumd += b;
			}
			suma /= frames;
			sumb /= frames;
			sumc /= frames;
			sumd /= frames;
			out.pixels[i] = suma << 24 | sumb << 16 | sumc << 8 | sumd;
		}
		out.updatePixels();
		return out;
	}

	/**
	 * Same as getImage() except returns image in int[] format.
	 * @return int[] containing averaged image.
	 *
	 */
	public synchronized int[] getImageArray()
	{
		int[] out = new int[width * height];

		int suma = 0;
		int sumb = 0;
		int sumc = 0;
		int sumd = 0;
		for (int i = 0; i < out.length; i++)
		{
			suma = 0;
			sumb = 0;
			sumc = 0;
			sumd = 0;
			for (byte b : images[0][i])
			{
				suma += b;
			}
			for (byte b : images[1][i])
			{
				sumb += b;
			}
			for (byte b : images[2][i])
			{
				sumc += b;
			}
			for (byte b : images[3][i])
			{
				sumd += b;
			}
			suma /= frames;
			sumb /= frames;
			sumc /= frames;
			sumd /= frames;
			assert suma >= 0 && suma <= 255;
			assert sumb >= 0 && sumb <= 255;
			assert sumc >= 0 && sumc <= 255;
			assert sumd >= 0 && sumd <= 255;
			out[i] = suma << 24 | sumb << 16 | sumc << 8 | sumd;
		}

		return out;
	}
}
