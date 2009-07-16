package org.dyndns.dainichi.redec.util.objects;

import java.util.Vector;

import org.dyndns.dainichi.redec.applet.ReDec;
import org.dyndns.dainichi.redec.util.Statistics;

import processing.xml.XMLElement;

/**
 * Stores a color in both it's RGB format and it's HSB format, along with some std deviation
 * values to indicate allowable error ranges.
 * @author dejagerd
 *
 */
public class Color {
	public ReDec	parent;

	public String	name;
	public Vector<Float>	hue = new Vector<Float>();
	public Vector<Float>	saturation= new Vector<Float>();
	public Vector<Float>	brightness= new Vector<Float>();
	public Vector<Float>	red= new Vector<Float>();
	public Vector<Float>	green= new Vector<Float>();
	public Vector<Float>	blue= new Vector<Float>();


	/**
	 * Same as Color(ReDec,String,float[],float[])
	 * @param parent reference to Parent.
	 * @param name Name of this color.
	 */
	public Color(ReDec parent,String name)
	{
		this(parent, name, new float[] { 0, 0, 0, 0, 0, 0 }, new float[] { 0, 0, 0, 0, 0, 0 });
	}
	/**
	 * Creates a new <code>Color</code> object with the specified  parent, name, values, and Std Deviations.
	 * @param theParent reference to parent
	 * @param name Name of this color instance
	 * @param color float array  containing mean values for this color.
	 * @param sd float array containing allowable variation for this color.
	 */
	public Color(ReDec theParent, String name, float[] color, float[] sd) {
		this.name = name;
		parent = theParent;
		hue.add(color[0]);
		hue.add(sd[0]);
		saturation.add(color[1]);
		saturation.add(sd[1]);
		brightness.add(color[2]);
		brightness.add(sd[2]);
		red.add(color[3]);
		red.add(sd[3]);
		green.add(color[4]);
		green.add(sd[4]);
		blue.add(color[5]);
		blue.add(sd[5]);
	}

	/**
	 * Static method that pulls out the alpha value embedded in a pixel.
	 * @param color input pixel
	 * @return Alpha Value of supplied pixel.
	 */
	public static int alpha(int color)
	{
		return color >> 24 & 0xff;
	}
	/**
	 * Static method that pulls out the red value embedded in a pixel.
	 * @param color input pixel
	 * @return Red Value of supplied pixel.
	 */
	public static int red(int color)
	{
		return color >> 16 & 0xff;
	}
	/**
	 * Static method that pulls out the green value embedded in a pixel.
	 * @param color input pixel
	 * @return green Value of supplied pixel.
	 */
	public static int green(int color)
	{
		return color >> 8 & 0xff;
	}
	/**
	 * Static method that pulls out the blue value embedded in a pixel.
	 * @param color input pixel
	 * @return Blue Value of supplied pixel.
	 */
	public static int blue(int color)
	{
		return color & 0xff;
	}

	/**
	 * returns the <code>int</code> equivalent color value of the combined parameters.
	 * @param a Alpha
	 * @param r Red
	 * @param g Green
	 * @param b Blue
	 * @return int equivalent of the above. (returned as 0xaarrggbb)
	 */
	public static int color(int a, int r, int g, int b)
	{
		return (a & 0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | b & 0xff;
	}

	/**
	 * Returns the absolute difference between two values, allowing for wrapping.
	 * Example (using byte values to help show the issue resolved)
	 * <code><br/>
	 * byte a = 0;<br/>
	 * byte b = 255;<br/></code>
	 * Using usual arithmetic, we end up with 255 or -1 I need to know what the difference is
	 * taking in to account wrapping. If I do the modified arithmetic on the above values, I want to get 1
	 * This method allows for that.
	 * the <code> max</code> parameter is the number of descrete value used if this was normal integer math.
	 * using the above example, we would set <code> max = 256</code>
	 *
	 * @param a operator a
	 * @param b operator b
	 * @param max value to wrap at.
	 * @return absolute wrapped difference.
	 */
	public static float difference(float a, float b, float max)
	{
		float result = 0f;
		if (a > b) {
			result = max - (a + max - b) % max;
		} else if (b > a) {
			result = max - (b + max - a) % max;
		}
		if (result > max / 2) {
			result = max - result;
		}
		return result;
	}


	/**
	 * Since each <code>Color</code> stores both a RGB value and a HSB value, we need to know the result of
	 * both comparisons.
	 * @param color <code> Color</code> to compare to.
	 * @return result of comparison on the RGB values and the comparison to the HSB values.
	 */
	public boolean[] isInRange(Color color)
	{
		boolean[] ret = {false,false};
		for(int i = 0; i< hue.size();i= i+2)
		{
			ret[0] |=
			ReDec.abs(difference(hue.get(i), color.hue.get(0), 256)) <= hue.get(i+1) &&
			ReDec.abs(saturation.get(i) - color.saturation.get(0)) <= saturation.get(i+1) &&
			ReDec.abs(brightness.get(i) - color.brightness.get(0)) <= brightness.get(i+1);
			ret[1] |=
			ReDec.abs(red.get(i) - color.red.get(0)) <= red.get(i+1) &&
			ReDec.abs(green.get(i) - color.green.get(0)) <= green.get(i+1) &&
			ReDec.abs(blue.get(i) - color.blue.get(0)) <= blue.get(i+1);
		}
		return ret;
	}


	/**
	 * Saves this color to the specified XML tree.
	 * @param root root of the tree to same this color in.
	 */
	public void saveColor(XMLElement root)
	{
		XMLElement[] children = root.getChildren(name);
		for(XMLElement x: children)
		{
			root.removeChild(x);
		}
		for(int i = 0; i < hue.size()/2;i++)
		{
			root.addChild(getXML(i));
		}
	}
	/**
	 * Returns the XML for this color, assigned the id supplied in <code>id</code>
	 * @param id The id number to assign this color
	 * @return New XMLElement corresponding to this color.
	 */
	public XMLElement getXML(int id)
	{
		XMLElement values = new XMLElement();
		XMLElement me = new XMLElement();
		me.setName(name);
		me.setAttribute("id", Integer.toString(id));
		values.setName("HSB");
		me.addChild(values);
		values = new XMLElement();
		values.setName("RGB");
		me.addChild(values);
		XMLElement hsb = me.getChild("HSB");
		XMLElement rgb = me.getChild("RGB");

		values = new XMLElement();
		values.setName("components");
		values.setAttribute("a", Float.toString(hue.get(2*id)));
		values.setAttribute("b", Float.toString(saturation.get(2*id)));
		values.setAttribute("c", Float.toString(brightness.get(2*id)));
		hsb.addChild(values);
		values = new XMLElement();
		values.setName("error");
		values.setAttribute("a", Float.toString(hue.get(id*2+1)));
		values.setAttribute("b", Float.toString(saturation.get(id*2+1)));
		values.setAttribute("c", Float.toString(brightness.get(id*2+1)));
		hsb.addChild(values);
		values = new XMLElement();
		values.setName("components");
		values.setAttribute("a", Float.toString(red.get(2*id)));
		values.setAttribute("b", Float.toString(green.get(2*id)));
		values.setAttribute("c", Float.toString(blue.get(2*id)));
		rgb.addChild(values);
		values = new XMLElement();
		values.setName("error");
		values.setAttribute("a", Float.toString(red.get(2*id+1)));
		values.setAttribute("b", Float.toString(green.get(2*id+1)));
		values.setAttribute("c", Float.toString(blue.get(2*id+1)));
		rgb.addChild(values);
		return me;
	}

	/**
	 * When supplied with an XMLElement, this method sets the values contained in this Color,
	 * to the values contained in the XMLElement.
	 * @param x THe XMLElement to use.
	 */
	public void loadFromXMLElement(XMLElement x)
	{
		int id = x.getIntAttribute("id");
		hue.add(x.getChild("HSB").getChild("components").getFloatAttribute("a"));
		hue.add(x.getChild("HSB").getChild("error").getFloatAttribute("a"));
		saturation.add(x.getChild("HSB").getChild("components").getFloatAttribute("b"));
		saturation.add(x.getChild("HSB").getChild("error").getFloatAttribute("b"));
		brightness.add(x.getChild("HSB").getChild("components").getFloatAttribute("c"));
		brightness.add(x.getChild("HSB").getChild("error").getFloatAttribute("c"));
		red.add(x.getChild("RGB").getChild("components").getFloatAttribute("a"));
		red.add(x.getChild("RGB").getChild("error").getFloatAttribute("a"));
		green.add(x.getChild("RGB").getChild("components").getFloatAttribute("b"));
		green.add(x.getChild("RGB").getChild("error").getFloatAttribute("b"));
		blue.add(x.getChild("RGB").getChild("components").getFloatAttribute("c"));
		blue.add(x.getChild("RGB").getChild("error").getFloatAttribute("c"));



	}
	/**
	 * When supplied with the root of the XML tree, thi method walks the tree and adds and elements named
	 * the same as this color to this color.
	 * @param root root of the tree.
	 */
	public void loadFromXML(XMLElement root)
	{
		XMLElement[] children = root.getChildren(name);
		for(XMLElement x: children)
		{
			loadFromXMLElement(x);
		}
	}
	/**
	 * Compares the supplied Color (as retrieved from <code>Statistics</code> to this Color
	 * based on both RGB and HSB
	 * @param s Color Statistics to compare
	 * @return boolean{HSBMatches, RGBMatches};
	 */
	public boolean[] isSimilar(float[][] s)
	{
		boolean[] ret = {false,false};
		for(int i = 0; i< hue.size();i= i+2)
		{
			ret[0] |=
			ReDec.abs(difference(hue.get(i),  s[0][Statistics.MEAN], 256)) <= hue.get(i+1) &&
			ReDec.abs(saturation.get(i) -  s[1][Statistics.MEAN]) <= saturation.get(i+1) &&
			ReDec.abs(brightness.get(i) -  s[2][Statistics.MEAN]) <= brightness.get(i+1);
			ret[1] |=
			ReDec.abs(red.get(i) -  s[3][Statistics.MEAN]) <= red.get(i+1) &&
			ReDec.abs(green.get(i) -  s[4][Statistics.MEAN]) <= green.get(i+1) &&
			ReDec.abs(blue.get(i) -  s[5][Statistics.MEAN]) <= blue.get(i+1);
		}
		return ret;
	}

}
