package org.dyndns.dainichi.redec.util.objects;

import java.util.Vector;

import org.dyndns.dainichi.redec.applet.ReDec;
import org.dyndns.dainichi.redec.util.PrintfFormat;

public class Resistor {
	Color[]			stripes;
	private int[]			codes;
	int				stripeIndex	= 0;
	private ReDec	parent;
	private int		bits		= 0;
	private int		prevBits	= 0;
	Color			t			= new Color(parent, "bg", new float[] { 0, 0, 0, 0, 0, 0 }, new float[] { 0, 0, 0, 0, 0, 0 });

	public Resistor(ReDec parent) {
		this.parent = parent;

		stripes = new Color[parent.CAM_WIDTH / parent.STEP_X];
		for (int i = 0; i < stripes.length; i++) {
			stripes[i] = t;
		}

		setCodes(new int[stripes.length]);
		assert stripes[0] != null && stripes[1] != null && stripes[2] != null : "stripes not allocated";
	}

	private void decode()
	{
		for (int i = 1; i < getCodes().length; i++) {
			if (getCodes()[i] == 0) {
				getCodes()[i] = getCodes()[i - 1];
			}
		}
		Vector<Integer> transitions = new Vector<Integer>(5);
		int prev = 0;
		for (int i = 0; i < getCodes().length; i++) {
			if (prev != getCodes()[i]) {
				transitions.add(i);
			}

			prev = getCodes()[i];
		}
		int index = 0;
		for (int i : transitions) {
			stripes[index++] = getColorfromOneHot(getCodes()[i]);
		}
	}

	public void add(int c)
	{

		getCodes()[stripeIndex++] = c;
	}

	static char							OHM	= '\u03A9';
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

	public String getValue()
	{
		assert stripes[0] != null && stripes[1] != null && stripes[2] != null;
		decode();
		int stripe1 = getIndex(stripes[0]);
		int stripe2 = getIndex(stripes[1]);
		int stripe3 = getIndex(stripes[2]);
		return pf[stripe3 + 2].sprintf(new Object[] { stripe1, stripe2, OHM });
	}

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

	private Color getColor(int index)
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
	 * @param codes the codes to set
	 */
	public void setCodes(int[] codes)
	{
		this.codes = codes;
	}

	/**
	 * @return the codes
	 */
	public int[] getCodes()
	{
		return codes;
	}
}
