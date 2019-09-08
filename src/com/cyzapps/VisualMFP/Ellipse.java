package com.cyzapps.VisualMFP;

public class Ellipse extends Plane{
	// plain point 1 is the centre of the ellipse
	// plain point 2 is one axis end of the ellipse
	// plain point 3 is the other axis end of the ellipse
	
	public Color mclr = new Color();

	public boolean isValidEllipse()	{
		if (isValidPlane() == false)	{
			return false;
		}
		Vector3D v3Axis1 = new Vector3D(mp3Pnt1, mp3Pnt2);
		Vector3D v3Axis2 = new Vector3D(mp3Pnt1, mp3Pnt3);
		if (v3Axis1.isUpRight(v3Axis2) == false)	{
			return false;
		} else	{
			return true;
		}
	}
	
	public double getAxis1Len()	{
		return mp3Pnt1.getDistance(mp3Pnt2);
	}
	
	public double getAxis2Len()	{
		return mp3Pnt1.getDistance(mp3Pnt3);
	}
	
	public boolean isCircle()	{
		return getAxis1Len() == getAxis2Len();
	}
}
