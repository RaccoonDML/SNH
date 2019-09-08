package com.cyzapps.VisualMFP;

public class LineStyle {
	static public enum LINEPATTERN
	{
		LINEPATTERN_NON(0),
		LINEPATTERN_SOLID(1),
		LINEPATTERN_DASH(2),
		LINEPATTERN_DOT(3),
		LINEPATTERN_DASH_DOT(4),
		LINEPATTERN_OTHERS(1000);
		
		private int value; 

		private LINEPATTERN(int i) { 
			value = i; 
		} 

		public int getValue() { 
			return value; 
		}
	};

	public LINEPATTERN menumLinePattern = LINEPATTERN.LINEPATTERN_SOLID;
	public double mdLineWidth = 1;
	public Color mclr = new Color();
	
}
