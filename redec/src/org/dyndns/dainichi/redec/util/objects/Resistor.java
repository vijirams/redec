package org.dyndns.dainichi.redec.util.objects;

import java.util.Vector;

import org.dyndns.dainichi.redec.applet.ReDec;
import org.dyndns.dainichi.redec.util.PrintfFormat;

/**
 * This Class is used to find the resistor stripes in the image.
 * @author dejagerd
 *
 */
public class Resistor {
	static char							OHM	= '\u03A9';
	Color[]			stripes;
	private Vector<Integer> codes;

	private ReDec	parent;
	Color			t			= new Color(parent, "bg");
	private static final PrintfFormat[]	pf	= new PrintfFormat[] { new PrintfFormat("0.%d%d%c"), // 0.xx
		new PrintfFormat("% d.%d%c"), // x.x
		new PrintfFormat("%  d%d%c"), // xx
		new PrintfFormat("% d%d0%c"), // xx0
		new PrintfFormat("%d.%dK%c"), // x.xK
		new PrintfFormat("% d%dK%c"), // xxK
		new PrintfFormat("%d%d0K%c"), // xx0K
		new PrintfFormat("%d.%dM%c"), // x.xM
		new PrintfFormat(" %d%dM%c"), // xxM
		new PrintfFormat("%d%d0M%c"), // xx0M
		new PrintfFormat("%d.%dG%c"), // x.xG
		new PrintfFormat(" %d%dG%c"), // xxG
		new PrintfFormat("     ")		};			// ???

	/**
	 * Create a new resistor.
	 * @param parent Parent reference.
	 */
	public Resistor(ReDec parent) {
		this.parent = parent;

		stripes = new Color[parent.CAM_WIDTH / parent.STEP_X];
		for (int i = 0; i < stripes.length; i++) {
			stripes[i] = t;
		}

		codes = new Vector<Integer>();
		assert stripes[0] != null && stripes[1] != null && stripes[2] != null : "stripes not allocated";
	}

	/**
	 * private method called to run the decoding.
	 */
	private void decode()
	{

		for(int i = 0; i< codes.size();i++)
		{
			stripes[i] = getColorfromOneHot(codes.get(i));
		}
	}

	/**
	 * Add a stripe (as a bitfield)
	 * @param c Bitfield used to indicate color detection.
	 */
	public void add(int c)
	{
		codes.add(c);
	}

	/**
	 * Get the String representation of the detected Resistor.
	 * @return String containing the value of this resistor.
	 */
	public String getValue()
	{
		assert stripes[0] != null && stripes[1] != null && stripes[2] != null;
		decode();
		int stripe1 = getIndex(stripes[0]);
		int stripe2 = getIndex(stripes[1]);
		int stripe3 = getIndex(stripes[2]);
		return pf[stripe3 + 2].sprintf(new Object[] { stripe1, stripe2, OHM });
	}

	/**
	 * Converts a detected color to a index value.
	 * @param c Color to decode.
	 * @return index of the color.
	 */
	private int getIndex(Color c)
	{

		if (c.equals(parent.silver))
			return -2;
		if (c.equals(parent.gold))
			return -1;
		if (c.equals(parent.black))
			return 0;
		if (c.equals(parent.brown))
			return 1;
		if (c.equals(parent.red))
			return 2;
		if (c.equals(parent.orange))
			return 3;
		if (c.equals(parent.yellow))
			return 4;
		if (c.equals(parent.green))
			return 5;
		if (c.equals(parent.blue))
			return 6;
		if (c.equals(parent.violet))
			return 7;
		if (c.equals(parent.grey))
			return 8;
		if (c.equals(parent.white))
			return 9;
		return 10;
	}

/*	private Color getColor(int index)
	{
		switch (index)
		{
		case -2:
			return parent.silver;
		case -1:
			return parent.gold;
		case 0:
			return parent.black;
		case 1:
			return parent.brown;
		case 2:
			return parent.red;
		case 3:
			return parent.orange;
		case 4:
			return parent.yellow;
		case 5:
			return parent.green;
		case 6:
			return parent.blue;
		case 7:
			return parent.violet;
		case 8:
			return parent.grey;
		case 9:
			return parent.white;

		}
		return t;
	}
	*/

	/**
	 * Converts a one-hot encoded color input into it's corrisponding Color.
	 * @param c Bitfield decoded
	 * @return the color corresponding to <code>c</code>
	 */
	private Color getColorfromOneHot(int c)
	{
		switch (c)
		{
		case 0x001:
			return parent.silver;
		case 0x002:
			return parent.gold;
		case 0x004:
			return parent.black;
		case 0x008:
			return parent.brown;
		case 0x010:
			return parent.red;
		case 0x020:
			return parent.orange;
		case 0x040:
			return parent.yellow;
		case 0x080:
			return parent.green;
		case 0x100:
			return parent.blue;
		case 0x200:
			return parent.violet;
		case 0x400:
			return parent.grey;
		case 0x800:
			return parent.white;
		}
		return t;
	}

	/**
	 * Used to access the bitfield array of detected color stripes.
	 * @return the codes
	 */
	public Integer[] getCodes()
	{
		return codes.toArray(new Integer[codes.size()]);
	}
}
