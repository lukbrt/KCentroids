package pl.lmb.centroids;

import java.awt.Point;

public class GroupedPoint extends Point
{
	private int group;

	public GroupedPoint(int x, int y)
	{
		super(x, y);
	}
	
	public GroupedPoint(int x, int y, int group)
	{
		super(x, y);
		this.group = group;
	}

	public int getGroup()
	{
		return group;
	}

	public void setGroup(int group)
	{
		this.group = group;
	}
	
	
}
