package com.cyzapps.VisualMFP;

public class Parallelogram extends Plane {
	
	// Plane point1 ------------------------ Plane point2
	//   |
	//   |
	//   |
	// Plane point3
	public Color mclr = new Color();
	
	public double getBorder1Len()	{
		return mp3Pnt1.getDistance(mp3Pnt2);
	}
	
	public double getBorder2Len()	{
		return mp3Pnt1.getDistance(mp3Pnt3);
	}
	
	public boolean isDiamond()	{
		return (MathLib.isEqual(getBorder1Len(), getBorder2Len()));
	}
	
	public boolean isRectangle()	{
		Vector3D v3Pnt12 = new Vector3D(mp3Pnt1, mp3Pnt2);
		Vector3D v3Pnt23 = new Vector3D(mp3Pnt2, mp3Pnt3);
		return v3Pnt12.isUpRight(v3Pnt23);
	}

	public boolean isSquare()	{
		return isDiamond() && isRectangle();
	}
}
