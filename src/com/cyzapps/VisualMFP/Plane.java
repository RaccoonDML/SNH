package com.cyzapps.VisualMFP;

public class Plane {
	
	Position3D mp3Pnt1 = new Position3D();
	Position3D mp3Pnt2 = new Position3D();
	Position3D mp3Pnt3 = new Position3D();

	public Plane()	{}
	
	public Plane(Position3D p3Pnt1, Position3D p3Pnt2, Position3D p3Pnt3)	{
		mp3Pnt1 = p3Pnt1;
		mp3Pnt2 = p3Pnt2;
		mp3Pnt3 = p3Pnt3;
	}
	
	public boolean isValidPlane()	{
		double dPnt12X = mp3Pnt1.getX() - mp3Pnt2.getX();
		double dPnt23X = mp3Pnt2.getX() - mp3Pnt3.getX();
		double dPnt12Y = mp3Pnt1.getY() - mp3Pnt2.getY();
		double dPnt23Y = mp3Pnt2.getY() - mp3Pnt3.getY();
		double dPnt12Z = mp3Pnt1.getZ() - mp3Pnt2.getZ();
		double dPnt23Z = mp3Pnt2.getZ() - mp3Pnt3.getZ();
		
		if (dPnt12X != 0 && dPnt12Y != 0 && dPnt12Z != 0)	{
			if (dPnt23X/dPnt12X == dPnt23Y/dPnt12Y && dPnt23Z/dPnt12Z == dPnt23Y/dPnt12Y)	{
				return false;
			}
		} else if (dPnt12X == 0 && dPnt12Y != 0 && dPnt12Z != 0)	{
			if (dPnt23X == 0 && dPnt23Z/dPnt12Z == dPnt23Y/dPnt12Y)	{
				return false;
			}
		} else if (dPnt12X != 0 && dPnt12Y == 0 && dPnt12Z != 0)	{
			if (dPnt23X/dPnt12X == dPnt23Z/dPnt12Z && dPnt23Y == 0)	{
				return false;
			}
		} else if (dPnt12X != 0 && dPnt12Y != 0 && dPnt12Z == 0)	{
			if (dPnt23X/dPnt12X == dPnt23Y/dPnt12Y && dPnt23Z == 0)	{
				return false;
			}
		} else if (dPnt12X == 0 && dPnt12Y == 0 && dPnt12Z != 0)	{
			if (dPnt23X == 0 && dPnt23Y == 0)	{
				return false;
			}
		} else if (dPnt12X == 0 && dPnt12Y != 0 && dPnt12Z == 0)	{
			if (dPnt23X == 0 && dPnt23Z == 0)	{
				return false;
			}
		} else if (dPnt12X != 0 && dPnt12Y == 0 && dPnt12Z == 0)	{
			if (dPnt23Y == 0 && dPnt23Z == 0)	{
				return false;
			}
		} else	{	// dPnt12X == dPnt12Y == dPnt12Z == 0
			return false;
		}
		return true;
	}
}
