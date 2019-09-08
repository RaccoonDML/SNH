package com.cyzapps.VisualMFP;

public class SurfaceStyle {
	static public enum SURFACETYPE
	{
		SURFACETYPE_GRID(0),
		SURFACETYPE_SURFACE(1),
		SURFACETYPE_OTHERS(1000);
		
		private int value; 

		private SURFACETYPE(int i) { 
			value = i; 
		} 

		public int getValue() { 
			return value; 
		}
	};

	public SURFACETYPE menumSurfaceType = SURFACETYPE.SURFACETYPE_GRID;
	public Color mclrUpFaceMin = new Color();
	public double mdUpFaceMinValue = 0;
	public Color mclrUpFaceMax = new Color();
	public double mdUpFaceMaxValue = 0;
	public Color mclrDownFaceMin = new Color();
	public double mdDownFaceMinValue = 0;
	public Color mclrDownFaceMax = new Color();
	public double mdDownFaceMaxValue = 0;
	
	public Color getUpFaceColorAt(double dValue)	{
		if (dValue >= mdUpFaceMaxValue)	{
			return mclrUpFaceMax;
		} else if (dValue <= mdUpFaceMinValue)	{
			return mclrUpFaceMin;
		} else	{
			double ratio = (dValue - mdUpFaceMinValue) / (mdUpFaceMaxValue - mdUpFaceMinValue); 
			double dRed = (mclrUpFaceMax.mnR - mclrUpFaceMin.mnR) * ratio + mclrUpFaceMin.mnR;
			double dGreen = (mclrUpFaceMax.mnG - mclrUpFaceMin.mnG) * ratio + mclrUpFaceMin.mnG;
			double dBlue = (mclrUpFaceMax.mnB - mclrUpFaceMin.mnB) * ratio + mclrUpFaceMin.mnB;
			double dAlpha = (mclrUpFaceMax.mnAlpha - mclrUpFaceMin.mnAlpha) * ratio + mclrUpFaceMin.mnAlpha;
			return new Color((int)dAlpha, (int)dRed, (int)dGreen, (int)dBlue);
		}
	}

	public Color getDownFaceColorAt(double dValue)	{
		if (dValue >= mdDownFaceMaxValue)	{
			return mclrDownFaceMax;
		} else if (dValue <= mdDownFaceMinValue)	{
			return mclrDownFaceMin;
		} else	{
			double ratio = (dValue - mdDownFaceMinValue) / (mdDownFaceMaxValue - mdDownFaceMinValue); 
			double dRed = (mclrDownFaceMax.mnR - mclrDownFaceMin.mnR) * ratio + mclrDownFaceMin.mnR;
			double dGreen = (mclrDownFaceMax.mnG - mclrDownFaceMin.mnG) * ratio + mclrDownFaceMin.mnG;
			double dBlue = (mclrDownFaceMax.mnB - mclrDownFaceMin.mnB) * ratio + mclrDownFaceMin.mnB;
			double dAlpha = (mclrDownFaceMax.mnAlpha - mclrDownFaceMin.mnAlpha) * ratio + mclrDownFaceMin.mnAlpha;
			return new Color((int)dAlpha, (int)dRed, (int)dGreen, (int)dBlue);
		}
	}
}
