package com.cyzapps.VisualMFP;

public class PolarXYMapper extends PointMapper {
	
	protected Position3D mp3CoordOriginInTO = new Position3D();	// TO's coordinate of origin point of FROM's coordinate
	protected double mdScalingRatio = 1;	// mdCoordWidthInTO / mdCoordWidthInFROM;
	
	public PolarXYMapper()	{
	}
	
	public PolarXYMapper(Position3D p3CoordOriginInTO, double dScalingRatio)	{
		mp3CoordOriginInTO = p3CoordOriginInTO;
		mdScalingRatio = dScalingRatio;
	}
	
	public void setPolarXYMapper(Position3D p3CoordOriginInTO, double dScalingRatio)	{
		mp3CoordOriginInTO = p3CoordOriginInTO;
		mdScalingRatio = dScalingRatio;
	}
	
	@Override
	public Position3D mapFrom2To(Position3D p3From)	{
		double r = p3From.getX();
		double theta = p3From.getY();
		double x = r * Math.cos(theta);
		double y = r * Math.sin(theta);
		return new Position3D(mp3CoordOriginInTO.getX() + x * mdScalingRatio,
							mp3CoordOriginInTO.getY() - y * mdScalingRatio);
	}
	
	@Override
	public Position3D mapTo2From(Position3D p3To)	{
		double x = (p3To.getX() - mp3CoordOriginInTO.getX())/mdScalingRatio;
		double y = (-p3To.getY() + mp3CoordOriginInTO.getY())/mdScalingRatio;
		double r = Math.sqrt(x*x + y*y);
		double theta = Math.atan2(y, x);
		return new Position3D(r, theta);
	}

}
