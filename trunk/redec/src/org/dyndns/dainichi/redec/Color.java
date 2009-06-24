package org.dyndns.dainichi.redec;

import processing.core.PApplet;
import processing.xml.XMLElement;

public class Color
{
	public ReDec	parent;
	
	public String	name;
	public float	hue;
	public float	saturation;
	public float	brightness;
	public float	red;
	public float	green;
	public float	blue;
	public float[]	sd;

	public Color(ReDec theParent,String name, float[] color, float[] sd)
	{
		this.name = name;
		parent = theParent;
		hue = color[0];
		saturation = color[1];
		brightness = color[2];
		red = color[3];
		green = color[4];
		blue = color[5];
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

	public boolean[] isInRange(Color color)
	{

		return new boolean[] {
				(ReDec.abs(difference(hue, color.hue, 256)) <= sd[0]) && (ReDec.abs(saturation - color.saturation) <= sd[1]) && (ReDec.abs(brightness - color.brightness) <= sd[2]),
				(ReDec.abs(red - color.red) <= sd[3]) && (ReDec.abs(green - color.green) <= sd[4]) && (ReDec.abs(blue - color.blue) <= sd[5]) };
	}

	public int color()
	{
		return parent.color(red, green, blue);
	}
	public XMLElement getXML()
	{ 
		//String namespace = "";
		XMLElement values = new XMLElement();
		XMLElement me = new XMLElement();
		me.setName(name);
		values.setName("HSB");
		me.addChild(values);
		values = new XMLElement();
		values.setName("RGB");
		me.addChild(values);
		XMLElement hsb = me.getChild("HSB");
		XMLElement rgb = me.getChild("RGB");
		
		values = new XMLElement();
		values.setName("components");
		values.setAttribute("a", Float.toString(hue));
		values.setAttribute("b", Float.toString(saturation));
		values.setAttribute("c", Float.toString(brightness));
		hsb.addChild(values);
		values = new XMLElement();
		values.setName("error");
		values.setAttribute("a", Float.toString(sd[0]));
		values.setAttribute("b", Float.toString(sd[1]));
		values.setAttribute("c", Float.toString(sd[2]));
		hsb.addChild(values);
		values = new XMLElement();
		values.setName("components");
		values.setAttribute("a", Float.toString(red));
		values.setAttribute("b", Float.toString(green));
		values.setAttribute("c", Float.toString(blue));
		rgb.addChild(values);
		values = new XMLElement();
		values.setName("error");
		values.setAttribute("a", Float.toString(sd[3]));
		values.setAttribute("b", Float.toString(sd[4]));
		values.setAttribute("c", Float.toString(sd[5]));
		rgb.addChild(values);
		return me;	
	}
	public void loadFromXML(XMLElement x){
		//name = x.getName();
		hue = x.getChild("HSB").getChild("components").getFloatAttribute("a");
		saturation = x.getChild("HSB").getChild("components").getFloatAttribute("b");
		brightness = x.getChild("HSB").getChild("components").getFloatAttribute("c");
		sd[0] = x.getChild("HSB").getChild("error").getFloatAttribute("a");
		sd[1] = x.getChild("HSB").getChild("error").getFloatAttribute("b");
		sd[2] = x.getChild("HSB").getChild("error").getFloatAttribute("c");
		
		red = x.getChild("RGB").getChild("components").getFloatAttribute("a");
		green = x.getChild("RGB").getChild("components").getFloatAttribute("b");
		blue = x.getChild("RGB").getChild("components").getFloatAttribute("c");
		sd[3] = x.getChild("RGB").getChild("error").getFloatAttribute("a");
		sd[4] = x.getChild("RGB").getChild("error").getFloatAttribute("b");
		sd[5] = x.getChild("RGB").getChild("error").getFloatAttribute("c");
		
		
		
	}

}
