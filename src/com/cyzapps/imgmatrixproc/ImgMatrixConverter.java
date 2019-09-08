/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyzapps.imgmatrixproc;

/**
 *
 * @author tonyc
 */
public class ImgMatrixConverter {
    public static int convertRGB2Int(int red, int green, int blue)  {
        return (red << 16) | (green << 8) | blue;
    }
    
    public static int[] convertInt2RGB(int nColor)  {
        int[] rgb = new int[3];
        rgb[0] = (nColor >> 16) & 0x000000FF;   // red
        rgb[1] = (nColor >>8 ) & 0x000000FF;    // green
        rgb[2] = (nColor) & 0x000000FF; // blue
        return rgb;
    }
    
    public static int[][] scaleColorMatrix(int[][] colorMatrix, int nNewWidth, int nNewHeight)    {
        int[][] result = new int[nNewWidth][nNewHeight];
        double dXRatio = (double)colorMatrix.length / (double)nNewWidth;
        double dYRatio = (double)colorMatrix[0].length / (double)nNewHeight;
        for (int idx = 0; idx < nNewWidth; idx ++)  {
            for (int idx1 = 0; idx1 < nNewHeight; idx1 ++)  {
                int nMappedX = (int)Math.min(idx * dXRatio, colorMatrix.length - 1);
                int nMappedY = (int)Math.min(idx1 * dYRatio, colorMatrix[0].length - 1);
                result[idx][idx1] = colorMatrix[nMappedX][nMappedY];
            }
        }
        return result;
    }
    
	public static int[][] convertColor2Gray(int[][] colorMatrix)	{
		int[][] result = new int[colorMatrix.length][];
		for (int col = 0; col < colorMatrix.length; col ++)	{
			result[col] = new int[colorMatrix[col].length];
			for (int row = 0; row < colorMatrix[col].length; row ++)	{
                int[] rgb = convertInt2RGB(colorMatrix[col][row]);
                result[col][row] = (rgb[0] * 19595 + rgb[1] * 38469 + rgb[2] * 7472) >> 16;
			}
		}
		return result;
	}
	

    public static byte[][] convertColor2Bi(int[][] colorMatrix)	{
		byte[][] result = new byte[colorMatrix.length][];
		for (int col = 0; col < colorMatrix.length; col ++)	{
			result[col] = new byte[colorMatrix[col].length];
			for (int row = 0; row < colorMatrix[col].length; row ++)	{
				if (colorMatrix[col][row] == convertRGB2Int(255, 255, 255))	{   // if color is white
					result[col][row] = 0;
				} else	{
					result[col][row] = 1;
				}
			}
		}
		return result;
	}
}
