package com.cyzapps.VisualMFP;

public class Vector3D {
	
	public Position3D mp3From = new Position3D();	// means (0,0,0)
	
	public Position3D mp3To = new Position3D();
	
	public Vector3D()	{
		
	}
	
	public Vector3D(Vector3D v3Another)	{
		mp3From = v3Another.mp3From;
		mp3To = v3Another.mp3To;
	}

	public Vector3D(Position3D p3To)	{
		mp3To = p3To;	// Position3D is read-only
	}
	
	public Vector3D(Position3D p3From, Position3D p3To)	{
		setVector3D(p3From, p3To);
	}
	
	public Vector3D cloneSelf()	{
		return new Vector3D(mp3From, mp3To);
	}
	
	public void copy(Vector3D v3Another)	{
		mp3From = v3Another.mp3From;
		mp3To = v3Another.mp3To;
	}

	public void setVector3D(Position3D p3From, Position3D p3To)	{
		mp3From = p3From;
		mp3To = p3To;
	}
	
	public void shift(Position3D p3NewFrom)	{
		mp3To = new Position3D(mp3To.getX() + p3NewFrom.getX() - mp3From.getX(),
							mp3To.getY() + p3NewFrom.getY() - mp3From.getY(),
							mp3To.getZ() + p3NewFrom.getZ() - mp3From.getZ());
		mp3From = p3NewFrom;	// need not deep copy because Position3D is read only.
	}
	
	public void add(Position3D p3ToAdd)	{
		mp3From = new Position3D(mp3From.getX() + p3ToAdd.getX(),
				mp3From.getY() + p3ToAdd.getY(),
				mp3From.getZ() + p3ToAdd.getZ());
		mp3To = new Position3D(mp3To.getX() + p3ToAdd.getX(),
				mp3To.getY() + p3ToAdd.getY(),
				mp3To.getZ() + p3ToAdd.getZ());
	}
	
	public void add(Vector3D v3ToAdd)	{
		mp3From = new Position3D(mp3From.getX() + v3ToAdd.mp3To.getX() - v3ToAdd.mp3From.getX(),
				mp3From.getY() + v3ToAdd.mp3To.getY() - v3ToAdd.mp3From.getY(),
				mp3From.getZ() + v3ToAdd.mp3To.getZ() - v3ToAdd.mp3From.getZ());
		mp3To = new Position3D(mp3To.getX() + v3ToAdd.mp3To.getX() - v3ToAdd.mp3From.getX(),
				mp3To.getY() + v3ToAdd.mp3To.getY() - v3ToAdd.mp3From.getY(),
				mp3To.getZ() + v3ToAdd.mp3To.getY() - v3ToAdd.mp3From.getY());
	}
	
	public void subtract(Position3D p3ToAdd)	{
		mp3From = new Position3D(mp3From.getX() - p3ToAdd.getX(),
				mp3From.getY() - p3ToAdd.getY(),
				mp3From.getZ() - p3ToAdd.getZ());
		mp3To = new Position3D(mp3To.getX() - p3ToAdd.getX(),
				mp3To.getY() - p3ToAdd.getY(),
				mp3To.getZ() - p3ToAdd.getZ());
	}
	
	public void subtract(Vector3D v3ToAdd)	{
		mp3From = new Position3D(mp3From.getX() - v3ToAdd.mp3To.getX() + v3ToAdd.mp3From.getX(),
				mp3From.getY() - v3ToAdd.mp3To.getY() + v3ToAdd.mp3From.getY(),
				mp3From.getZ() - v3ToAdd.mp3To.getZ() + v3ToAdd.mp3From.getZ());
		mp3To = new Position3D(mp3To.getX() - v3ToAdd.mp3To.getX() + v3ToAdd.mp3From.getX(),
				mp3To.getY() - v3ToAdd.mp3To.getY() + v3ToAdd.mp3From.getY(),
				mp3To.getZ() - v3ToAdd.mp3To.getY() + v3ToAdd.mp3From.getY());
	}
	
	public double getXLen()	{
		return mp3To.getX() - mp3From.getX();
	}
	
	public double getYLen()	{
		return mp3To.getY() - mp3From.getY();
	}
	
	public double getZLen()	{
		return mp3To.getZ() - mp3From.getZ();
	}
	
	public double getLength()	{
		double dXLen = getXLen();
		double dYLen = getYLen();
		double dZLen = getZLen();
		return Math.sqrt(dXLen * dXLen + dYLen * dYLen + dZLen * dZLen);
	}
	
	public boolean isUpRight(Vector3D v3Another)	{
		Vector3D v31Plus2 = cloneSelf();
		v31Plus2.add(v3Another);
		double dXLen1 = getXLen();
		double dYLen1 = getYLen();
		double dZLen1 = getZLen();
		
		double dXLen2 = v3Another.getXLen();
		double dYLen2 = v3Another.getYLen();
		double dZLen2 = v3Another.getZLen();
		
		double dXLen12 = v31Plus2.getXLen();
		double dYLen12 = v31Plus2.getYLen();
		double dZLen12 = v31Plus2.getZLen();
		
		if (MathLib.isEqual(dXLen1 * dXLen1 + dYLen1 * dYLen1 + dZLen1 * dZLen1 + dXLen2 * dXLen2 + dYLen2 * dYLen2 + dZLen2 * dZLen2,
				dXLen12 * dXLen12 + dYLen12 * dYLen12 + dZLen12 * dZLen12))	{
			return true;
		}
		return false;
	}
}
