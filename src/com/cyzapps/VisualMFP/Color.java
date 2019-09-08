package com.cyzapps.VisualMFP;

public class Color {
	
	public int mnAlpha = 255;	// 0 means transparent, 
	public int mnR = 0;	// from 0 to 255
	public int mnG = 0;	// from 0 to 255
	public int mnB = 0;	// from 0 to 255
	
	public static final Color RED = new Color(255, 0, 0);
	public static final Color GREEN = new Color(0, 255, 0);
	public static final Color BLUE = new Color(0, 0, 255);
	public static final Color BLACK = new Color(0, 0, 0);
	public static final Color CYAN = new Color(0, 255, 255);
	public static final Color DKGRAY = new Color(68, 68, 68);
	public static final Color GRAY = new Color(136, 136, 136);
	public static final Color LTGRAY = new Color(204, 204, 204);
	public static final Color MAGENTA = new Color(255, 0, 255);
	public static final Color WHITE = new Color(255, 255, 255);
	public static final Color YELLOW = new Color(255, 255, 0);
	
	public Color()	{
		mnAlpha = 255;
		mnR = mnG = mnB = 0;
	}
	
	public Color(Color color)	{
		mnAlpha = color.mnAlpha;
		mnR = color.mnR;
		mnG = color.mnG;
		mnB = color.mnB;
	}
	
	public Color(int nR, int nG, int nB)	{
		mnAlpha = 255;
		mnR = nR;
		mnG = nG;
		mnB = nB;
        if (mnR < 0)    {
            mnR = 0;
        } else if (mnR > 255)   {
            mnR = 255;
        }
        if (mnG < 0)    {
            mnG = 0;
        } else if (mnG > 255)   {
            mnG = 255;
        }
        if (mnB < 0)    {
            mnB = 0;
        } else if (mnB > 255)   {
            mnB = 255;
        }
	}
	
	public Color(int nAlpha, int nR, int nG, int nB)	{
		mnAlpha = nAlpha;
		mnR = nR;
		mnG = nG;
		mnB = nB;
        if (mnAlpha < 0)    {
            mnAlpha = 0;
        } else if (mnAlpha > 255)   {
            mnAlpha = 255;
        }
        if (mnR < 0)    {
            mnR = 0;
        } else if (mnR > 255)   {
            mnR = 255;
        }
        if (mnG < 0)    {
            mnG = 0;
        } else if (mnG > 255)   {
            mnG = 255;
        }
        if (mnB < 0)    {
            mnB = 0;
        } else if (mnB > 255)   {
            mnB = 255;
        }
	}
	
	public void copy(Color color)	{
		mnAlpha = color.mnAlpha;
		mnR = color.mnR;
		mnG = color.mnG;
		mnB = color.mnB;
	}
	
	public boolean isEqual(Color color)	{
		if (mnAlpha == color.mnAlpha && mnR == color.mnR && mnG == color.mnG && mnB == color.mnB)	{
			return true;
		}
		return false;
	}
	
	public int getARGB()	{
		return (int)mnAlpha * 256 * 256 * 256 + (int)mnR * 256 * 256 + (int)mnG * 256 + (int)mnB;
	}
	
	public void setARGB(long nARGB)	{
		mnAlpha= (int)((nARGB >> 24) & 0xFF);
		mnR = (int)((nARGB >> 16) & 0xFF);
		mnG = (int)((nARGB >> 8) & 0xFF);
		mnB = (int)((nARGB >> 0) & 0xFF);
	}
	
	public float getF1R()	{
		float fR = ((float)mnR) / 255.0f;
		return fR;
	}
	
	public float getF1G()	{
		float fG = ((float)mnG) / 255.0f;
		return fG;
	}
	
	public float getF1B()	{
		float fB = ((float)mnB) / 255.0f;
		return fB;
	}
	
	public float getF1Alpha()	{
		float fAlpha = ((float)mnAlpha) / 255.0f;
		return fAlpha;
	}
    
    public Color getRGBInverseColor()  {   // reverse R G B and keep alpha
        return new Color(mnAlpha, 255 - mnR, 255 - mnG, 255 - mnB);
    }
    
    public Color getARGBInverseColor()  {   // reverse R G B and keep alpha
        return new Color(255 - mnAlpha, 255 - mnR, 255 - mnG, 255 - mnB);
    }
    
    public Color getAInverseColor()  {   // reverse R G B and keep alpha
        return new Color(255 - mnAlpha, mnR, mnG, mnB);
    }
}
