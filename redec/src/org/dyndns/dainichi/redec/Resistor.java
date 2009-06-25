package org.dyndns.dainichi.redec;

import java.util.BitSet;

public class Resistor
{
	private ReDec	parent;
	private BitSet bits = new BitSet(12);

	public Resistor(ReDec parent)
	{
		this.parent = parent;
	}
	public void add(boolean[] c)
	{
		for(int i = 0; i < bits.length(); i++)
		{
			bits.set(i,c[i]);
		}
	}
}
