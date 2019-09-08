package com.cyzapps.VisualMFP;

public class PointStyle {
	static public enum POINTSHAPE
	{
		POINTSHAPE_DOT(0),
		POINTSHAPE_CIRCLE(1),
		POINTSHAPE_SQUARE(2),
		POINTSHAPE_DIAMOND(3),
		POINTSHAPE_UPTRIANGLE(4),
		POINTSHAPE_DOWNTRIANGLE(5),
		POINTSHAPE_CROSS(6),
		POINTSHAPE_X(7),
		POINTSHAPE_OTHERS(1000);
		
		private int value; 

		private POINTSHAPE(int i) { 
			value = i; 
		} 

		public int getValue() { 
			return value; 
		}
	};

	public POINTSHAPE menumPointShape = POINTSHAPE.POINTSHAPE_DOT;
	public double mdSize = 1;
	public Color mclr = new Color();

}
