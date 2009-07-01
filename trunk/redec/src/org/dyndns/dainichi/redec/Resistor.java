package org.dyndns.dainichi.redec;


public class Resistor
{
	private Color[] stripes;
	int stripeIndex = 0;
	private ReDec	parent;
	private int bits = 0;
	private int prevBits = 0;

	public Resistor(ReDec parent)
	{
		this.parent = parent;
		stripes = new Color[4];
	}
	public void add(int c)
	{
		prevBits = bits;
		bits = c;
		if(prevBits>0 && bits ==0)
		{
			registerStripe();
		}
	}
	private void registerStripe()
	{
		if(Integer.bitCount(prevBits) ==1)
		{
			if(stripeIndex>=stripes.length)
			{
				ReDec.expand(stripes);
			}

			stripes[stripeIndex++]=getColor(stripeIndex);
		}

	}
	char OHM = '\u03A9';
	private static final PrintfFormat[] pf = new PrintfFormat[]{
		new PrintfFormat("0.%d%d\u03A9"),
		new PrintfFormat("%d.%d\u03A9"),
		new PrintfFormat("%d%d\u03A9"),
		new PrintfFormat("%d.%d0\u03A9"),
		new PrintfFormat("%d.%dK\u03A9"),
		new PrintfFormat("%d%dK\u03A9"),
		new PrintfFormat("%d%d0K\u03A9"),
		new PrintfFormat("%d.%dM\u03A9"),
		new PrintfFormat("%d%dM\u03A9"),
		new PrintfFormat("%d%d0M\u03A9"),
		new PrintfFormat("%d.%dG\u03A9"),
		new PrintfFormat("%d%dG\u03A9")};
	public String getValue()
	{
		return pf[getIndex(stripes[2])+2].sprintf(new Object[]{getIndex(stripes[0]),getIndex(stripes[1])});
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
		return Integer.MAX_VALUE;
	}
	private Color getColor(int index)
	{
		switch(index)
		{
		case -2: return parent.silver;
		case -1: return parent.gold;
		case 0: return parent.black;
		case 1: return parent.brown;
		case 2: return parent.red;
		case 3: return parent.orange;
		case 4: return parent.yellow;
		case 5: return parent.green;
		case 6: return parent.blue;
		case 7: return parent.violet;
		case 8: return parent.grey;
		case 9: return parent.white;
		}
		return null;
	}
}
