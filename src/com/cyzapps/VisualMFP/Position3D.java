package com.cyzapps.VisualMFP;

public class Position3D {
	private double mdX = 0;	// read only
	public double getX()	{return mdX;}
	
	private double mdY = 0; // read only
	public double getY()	{return mdY;}
	
	private double mdZ = 0; // read only
	public double getZ()	{return mdZ;}

	public double getDim(int nDim)	{
		return (nDim == 1)?mdY:(nDim == 2)?mdZ:mdX;
	}
	public double getDim(String strDim)	{
		return (strDim.trim().compareToIgnoreCase("y") == 0)?mdY:(strDim.trim().compareToIgnoreCase("z") == 0)?mdZ:mdX;
	}
	
	public Position3D()	{}
	
	public Position3D (double dX, double dY)	{
		setPosition3D(dX, dY);
	}
	
	private void setPosition3D(double dX, double dY)	{
		mdX = dX;
		mdY = dY;
		mdZ = 0;
	}
	
	public Position3D (double dX, double dY, double dZ)	{
		setPosition3D(dX, dY, dZ);
	}
	
	private void setPosition3D(double dX, double dY, double dZ)	{
		mdX = dX;
		mdY = dY;
		mdZ = dZ;
	}
	
	public boolean isEqual(Position3D pnt)	{
		if (mdX != pnt.mdX || mdY != pnt.mdY || mdZ != pnt.mdZ)	{
			return false;
		} else	{
			return true;
		}
	}
	
	public Position3D cloneSelf()	{
		Position3D pnt = new Position3D();
		pnt.mdX = mdX;
		pnt.mdY = mdY;
		pnt.mdZ = mdZ;
		return pnt;
	}
	
	public Position3D(Position3D pnt)	{
		copy(pnt);
	}
	
	public void copy(Position3D pnt)	{
		mdX = pnt.mdX;
		mdY = pnt.mdY;
		mdZ = pnt.mdZ;
	}

	public Position3D add(Position3D p3ToAdd)	{
		return new Position3D(mdX + p3ToAdd.getX(), mdY + p3ToAdd.getY(), mdZ + p3ToAdd.getZ());
	}
	
	public Position3D add(Vector3D v3ToAdd)	{
		return new Position3D(mdX + v3ToAdd.mp3To.getX() - v3ToAdd.mp3From.getX(),
				mdY + v3ToAdd.mp3To.getY() - v3ToAdd.mp3From.getY(),
				mdZ + v3ToAdd.mp3To.getZ() - v3ToAdd.mp3From.getZ());
	}
	
	public Position3D subtract(Position3D p3ToAdd)	{
		return new Position3D(mdX - p3ToAdd.getX(), mdY - p3ToAdd.getY(), mdZ - p3ToAdd.getZ());
	}
	
	public Position3D subtract(Vector3D v3ToAdd)	{
		return new Position3D(mdX - v3ToAdd.mp3To.getX() + v3ToAdd.mp3From.getX(),
				mdY - v3ToAdd.mp3To.getY() + v3ToAdd.mp3From.getY(),
				mdZ - v3ToAdd.mp3To.getZ() + v3ToAdd.mp3From.getZ());
	}
	
	public double getDistance(Position3D p3Another)	{
		double dXDistance = mdX - p3Another.mdX;
		double dYDistance = mdY - p3Another.mdY;
		double dZDistance = mdZ - p3Another.mdZ;
		
		return Math.sqrt(dXDistance * dXDistance + dYDistance * dYDistance + dZDistance * dZDistance);
	}
	
	@Override
	public String toString()	{
		return "(" + mdX + "," + mdY + "," + mdZ + ")";
	}
}
