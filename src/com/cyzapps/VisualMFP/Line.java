package com.cyzapps.VisualMFP;

public class Line extends Vector3D {

	public Color mclr = new Color();
	
	public Line()	{
	}
	
	public Line(Line line)	{
		super((Vector3D)line);
		mclr = new Color(line.mclr);
	}
}
