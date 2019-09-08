/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyzapps.imgmatrixproc;

import com.cyzapps.VisualMFP.Position3D;
import java.util.LinkedList;

/**
 *
 * @author tonyc
 */
public class ImgNoiseFilter {

    // returned imagechops are all in minimum container.
    public static byte[][] filterNoisePoints4Bi(byte[][] biMatrix, int nPntSize) {
        
        byte[][] barrayCpy = new byte[biMatrix.length][biMatrix[0].length];
        byte[][] barrayResult = new byte[biMatrix.length][biMatrix[0].length];
        for (int j = 0; j < biMatrix.length; j ++)  {
            System.arraycopy(biMatrix[j], 0, barrayCpy[j], 0, biMatrix[0].length);
        }
        
        byte[][] barrayChop = new byte[biMatrix.length][biMatrix[0].length];
        byte[][] barrayChopInit = new byte[biMatrix.length][biMatrix[0].length];
        while(true) {
            for (int j = 0; j < biMatrix.length; j ++)  {
                System.arraycopy(barrayChopInit[j], 0, barrayChop[j], 0, barrayChop[0].length);
            }
            LinkedList<Position3D> listConnected = new LinkedList<Position3D>();
            int nNewTop = biMatrix[0].length , nNewLeft = biMatrix.length, nNewBottom = -1, nNewRight = -1;
            boolean bHasPnts = false;
            for (int idx = 0; idx < biMatrix[0].length; idx ++)    {
                for (int idx1 = 0; idx1 < biMatrix.length; idx1 ++)  {
                    if (barrayCpy[idx1][idx] == 1)   {
                        Position3D pnt = new Position3D(idx1, idx);
                        barrayChop[idx1][idx] = 1;
                        barrayCpy[idx1][idx] = 0;
                        listConnected.add(pnt);
                        bHasPnts = true;
                        break;
                    }
                }
                if (bHasPnts)   {
                    break;
                }
            }           

            while(listConnected.size() > 0) {
                Position3D p3Head = listConnected.removeFirst();
                int x = (int)p3Head.getX(), y = (int)p3Head.getY();
                if (x < nNewLeft)   {
                    nNewLeft = x;
                }
                if (x > nNewRight)  {
                    nNewRight = x;
                }
                if (y < nNewTop)    {
                    nNewTop = y;
                }
                if (y > nNewBottom) {
                    nNewBottom = y;
                }
                if (x < biMatrix.length - 1 && barrayCpy[x + 1][y] == 1)   {
                    Position3D pnt = new Position3D(x + 1, y);
                    barrayChop[x + 1][y] = 1;
                    barrayCpy[x + 1][y] = 0;
                    listConnected.add(pnt);
                }
                if (x < biMatrix.length - 1 && y > 0 && barrayCpy[x + 1][y - 1] == 1)   {
                    Position3D pnt = new Position3D(x + 1, y - 1);
                    barrayChop[x + 1][y - 1] = 1;
                    barrayCpy[x + 1][y - 1] = 0;
                    listConnected.add(pnt);
                }
                if (y > 0 && barrayCpy[x][y - 1] == 1)   {
                    Position3D pnt = new Position3D(x, y - 1);
                    barrayChop[x][y - 1] = 1;
                    barrayCpy[x][y - 1] = 0;
                    listConnected.add(pnt);
                }
                if (x > 0 && y > 0 && barrayCpy[x - 1][y - 1] == 1)   {
                    Position3D pnt = new Position3D(x - 1, y - 1);
                    barrayChop[x - 1][y - 1] = 1;
                    barrayCpy[x - 1][y - 1] = 0;
                    listConnected.add(pnt);
                }
                if (x > 0 && barrayCpy[x - 1][y] == 1)   {
                    Position3D pnt = new Position3D(x - 1, y);
                    barrayChop[x - 1][y] = 1;
                    barrayCpy[x - 1][y] = 0;
                    listConnected.add(pnt);
                }
                if (x > 0 && y < biMatrix[0].length - 1 && barrayCpy[x - 1][y + 1] == 1)   {
                    Position3D pnt = new Position3D(x - 1, y + 1);
                    barrayChop[x - 1][y + 1] = 1;
                    barrayCpy[x - 1][y + 1] = 0;
                    listConnected.add(pnt);
                }
                if (y < biMatrix[0].length - 1 && barrayCpy[x][y + 1] == 1)   {
                    Position3D pnt = new Position3D(x, y + 1);
                    barrayChop[x][y + 1] = 1;
                    barrayCpy[x][y + 1] = 0;
                    listConnected.add(pnt);
                }
                if (x < biMatrix.length - 1 && y < biMatrix[0].length - 1 && barrayCpy[x + 1][y + 1] == 1)   {
                    Position3D pnt = new Position3D(x + 1, y + 1);
                    barrayChop[x + 1][y + 1] = 1;
                    barrayCpy[x + 1][y + 1] = 0;
                    listConnected.add(pnt);
                }
            }

            if (bHasPnts)   {
                int nChopWidth = nNewRight - nNewLeft + 1, nChopHeight = nNewBottom - nNewTop + 1;
                if ((nChopWidth <= nPntSize / 2 && nChopHeight <= nPntSize * 2)
                        || (nChopWidth <= nPntSize * 2 && nChopHeight <= nPntSize / 2)
                        || (nChopWidth * nChopHeight <= nPntSize * nPntSize / 2))   {
                    // this is a noise point
                    continue;
                }
                for (int idx = nNewLeft; idx <= nNewRight; idx ++)  {
                    for (int idx1 = nNewTop; idx1 <= nNewBottom; idx1 ++)   {
                        if (barrayChop[idx][idx1] == 1) {
                            barrayResult[idx][idx1] = 1;
                        }
                    }
                }
            } else  {
                break;
            }
        }
        
        return barrayResult;
    }
    
    // nMode is 1, 0->1 is allowed, is 2, 1-> 0 is allowed.
    public static byte[][] filterNoiseNbAvg4Bi(byte[][] biMatrix, int nRadius, int nMode)	{
        if (nRadius == 0)   {
            // this means stroke width must be 1.
            return biMatrix;
        }
        
        int nWidth = biMatrix.length, nHeight = biMatrix[0].length;
        long[][] lnarraySumArea = new long[nWidth][nHeight];
        int[][] narrayPntCnts = new int[nWidth][nHeight];
        int[][] narraySumLeftEdgeVLn = new int[nWidth][nHeight];
        int[] narraySumTopEdgeHLn = new int[nHeight];
        for (int j = 0; j < nHeight; j ++)   {
            for (int i = 0; i < nWidth; i ++)  {
                int nThisLeft = Math.max(0, i - nRadius);
                int nThisRight = Math.min(nWidth - 1, i + nRadius);
                int nThisTop = Math.max(0, j - nRadius);
                int nThisBottom = Math.min(nHeight - 1, j + nRadius);
                if (i > 0) {
                    int nRightEdgeVLn = 0;
                    if (nThisRight - i == nRadius)  {
                        for (int idx = nThisTop; idx <= nThisBottom; idx ++)    {
                            nRightEdgeVLn += biMatrix[nThisRight][idx];
                        }
                        int nThisRightLeftEdgeIdx = 2 * nThisRight - i;
                        if (nThisRightLeftEdgeIdx < nWidth)    {
                            narraySumLeftEdgeVLn[nThisRightLeftEdgeIdx][j] = nRightEdgeVLn;
                        }
                    }
                    lnarraySumArea[i][j] = lnarraySumArea[i - 1][j] - narraySumLeftEdgeVLn[i - 1][j] + nRightEdgeVLn;
                    if (i - nThisLeft == nRadius && i <= 2 * nRadius)    {   // if i > 2 * nRadius, narraySumLeftEdgeVLn[i][j] has been calculated.
                        int nLeftEdgeVLn = 0;
                        for (int idx = nThisTop; idx <= nThisBottom; idx ++)    {
                            nLeftEdgeVLn += biMatrix[nThisLeft][idx];
                        }
                        narraySumLeftEdgeVLn[i][j] = nLeftEdgeVLn;
                    }
                } else if (j > 0)   {
                    int nBottomEdgeHLn = 0;
                    if (nThisBottom - j == nRadius) {
                        for (int idx = nThisLeft; idx <= nThisRight; idx ++)    {
                            nBottomEdgeHLn += biMatrix[idx][nThisBottom];
                        }
                        int nThisBottomTopEdgeIdx = 2 * nThisBottom - j;
                        if (nThisBottomTopEdgeIdx < nHeight)    {
                            narraySumTopEdgeHLn[nThisBottomTopEdgeIdx] = nBottomEdgeHLn;
                        }
                    }
                    lnarraySumArea[i][j] = lnarraySumArea[i][j - 1] - narraySumTopEdgeHLn[j - 1] + nBottomEdgeHLn;
                    if (j - nThisTop == nRadius && j <= 2 * nRadius)    {   // if j > 2 * nRadius, narraySumTopEdgeHLn[j] has been calculated.
                        int nTopEdgeHLn = 0;
                        for (int idx = nThisLeft; idx <= nThisRight; idx ++)    {
                            nTopEdgeHLn += biMatrix[idx][nThisTop];
                        }
                        narraySumTopEdgeHLn[j] = nTopEdgeHLn;
                    }
                } else  {   // i == 0 && j == 0
                    for (int idx = nThisLeft; idx <= nThisRight; idx ++)    {
                        for (int idx1 = nThisTop; idx1 <= nThisBottom; idx1 ++) {
                            lnarraySumArea[i][j] += biMatrix[idx][idx1];
                        }
                    }
                    int nRightEdgeVLn = 0;
                    if (nThisRight == nRadius && 2*nThisRight < nWidth)  {
                        for (int idx = nThisTop; idx <= nThisBottom; idx ++)    {
                            nRightEdgeVLn += biMatrix[nThisRight][idx];
                        }
                        int nThisRightLeftEdgeIdx = 2 * nThisRight;
                        narraySumLeftEdgeVLn[nThisRightLeftEdgeIdx][j] = nRightEdgeVLn;
                    }
                    int nBottomEdgeHLn = 0;
                    if (nThisBottom == nRadius && 2*nThisBottom < nHeight) {
                        for (int idx = nThisLeft; idx <= nThisRight; idx ++)    {
                            nBottomEdgeHLn += biMatrix[idx][nThisBottom];
                        }
                        int nThisBottomTopEdgeIdx = 2 * nThisBottom;
                        narraySumTopEdgeHLn[nThisBottomTopEdgeIdx] = nBottomEdgeHLn;
                    }
                }
                narrayPntCnts[i][j] = (nThisRight - nThisLeft + 1) * (nThisBottom - nThisTop + 1);
            }
        }
        byte[][] barrayReturn = new byte[nWidth][nHeight];  // initialize a zero array.
        for (int i = 0; i < nWidth; i ++)  {
            for (int j = 0; j < nHeight; j ++)  {
                barrayReturn[i][j] = biMatrix[i][j];
                if ((nMode & 2) == 2 && biMatrix[i][j] == 1 && narrayPntCnts[i][j] > (lnarraySumArea[i][j] * 2)) {
                    barrayReturn[i][j] = 0;
                } else if ((nMode & 1) == 1 && biMatrix[i][j] == 0 && narrayPntCnts[i][j] < (lnarraySumArea[i][j] * 2)) {
                    barrayReturn[i][j] = 1;
                }
            }
        }
        return barrayReturn;
    }

    public static int[][] filterNoiseNbAvg4Gray(int[][] grayMatrix, int nRadius)	{
        if (nRadius == 0)   {
            // this means stroke width must be 1.
            return grayMatrix;
        }
        
        int nWidth = grayMatrix.length, nHeight = grayMatrix[0].length;
        long[][] lnarraySumArea = new long[nWidth][nHeight];
        int[][] narrayPntCnts = new int[nWidth][nHeight];
        int[][] narraySumLeftEdgeVLn = new int[nWidth][nHeight];
        int[] narraySumTopEdgeHLn = new int[nHeight];
        for (int j = 0; j < nHeight; j ++)   {
            for (int i = 0; i < nWidth; i ++)  {
                int nThisLeft = Math.max(0, i - nRadius);
                int nThisRight = Math.min(nWidth - 1, i + nRadius);
                int nThisTop = Math.max(0, j - nRadius);
                int nThisBottom = Math.min(nHeight - 1, j + nRadius);
                if (i > 0) {
                    int nRightEdgeVLn = 0;
                    if (nThisRight - i == nRadius)  {
                        for (int idx = nThisTop; idx <= nThisBottom; idx ++)    {
                            nRightEdgeVLn += grayMatrix[nThisRight][idx];
                        }
                        int nThisRightLeftEdgeIdx = 2 * nThisRight - i;
                        if (nThisRightLeftEdgeIdx < nWidth)    {
                            narraySumLeftEdgeVLn[nThisRightLeftEdgeIdx][j] = nRightEdgeVLn;
                        }
                    }
                    lnarraySumArea[i][j] = lnarraySumArea[i - 1][j] - narraySumLeftEdgeVLn[i - 1][j] + nRightEdgeVLn;
                    if (i - nThisLeft == nRadius && i <= 2 * nRadius)    {   // if i > 2 * nRadius, narraySumLeftEdgeVLn[i][j] has been calculated.
                        int nLeftEdgeVLn = 0;
                        for (int idx = nThisTop; idx <= nThisBottom; idx ++)    {
                            nLeftEdgeVLn += grayMatrix[nThisLeft][idx];
                        }
                        narraySumLeftEdgeVLn[i][j] = nLeftEdgeVLn;
                    }
                } else if (j > 0)   {
                    int nBottomEdgeHLn = 0;
                    if (nThisBottom - j == nRadius) {
                        for (int idx = nThisLeft; idx <= nThisRight; idx ++)    {
                            nBottomEdgeHLn += grayMatrix[idx][nThisBottom];
                        }
                        int nThisBottomTopEdgeIdx = 2 * nThisBottom - j;
                        if (nThisBottomTopEdgeIdx < nHeight)    {
                            narraySumTopEdgeHLn[nThisBottomTopEdgeIdx] = nBottomEdgeHLn;
                        }
                    }
                    lnarraySumArea[i][j] = lnarraySumArea[i][j - 1] - narraySumTopEdgeHLn[j - 1] + nBottomEdgeHLn;
                    if (j - nThisTop == nRadius && j <= 2 * nRadius)    {   // if j > 2 * nRadius, narraySumTopEdgeHLn[j] has been calculated.
                        int nTopEdgeHLn = 0;
                        for (int idx = nThisLeft; idx <= nThisRight; idx ++)    {
                            nTopEdgeHLn += grayMatrix[idx][nThisTop];
                        }
                        narraySumTopEdgeHLn[j] = nTopEdgeHLn;
                    }
                } else  {   // i == 0 && j == 0
                    for (int idx = nThisLeft; idx <= nThisRight; idx ++)    {
                        for (int idx1 = nThisTop; idx1 <= nThisBottom; idx1 ++) {
                            lnarraySumArea[i][j] += grayMatrix[idx][idx1];
                        }
                    }
                    int nRightEdgeVLn = 0;
                    if (nThisRight == nRadius && 2*nThisRight < nWidth)  {
                        for (int idx = nThisTop; idx <= nThisBottom; idx ++)    {
                            nRightEdgeVLn += grayMatrix[nThisRight][idx];
                        }
                        int nThisRightLeftEdgeIdx = 2 * nThisRight;
                        narraySumLeftEdgeVLn[nThisRightLeftEdgeIdx][j] = nRightEdgeVLn;
                    }
                    int nBottomEdgeHLn = 0;
                    if (nThisBottom == nRadius && 2*nThisBottom < nHeight) {
                        for (int idx = nThisLeft; idx <= nThisRight; idx ++)    {
                            nBottomEdgeHLn += grayMatrix[idx][nThisBottom];
                        }
                        int nThisBottomTopEdgeIdx = 2 * nThisBottom;
                        narraySumTopEdgeHLn[nThisBottomTopEdgeIdx] = nBottomEdgeHLn;
                    }
                }
                narrayPntCnts[i][j] = (nThisRight - nThisLeft + 1) * (nThisBottom - nThisTop + 1);
            }
        }
        int[][] narrayReturn = new int[nWidth][nHeight];  // initialize a zero array.
        for (int i = 0; i < nWidth; i ++)  {
            for (int j = 0; j < nHeight; j ++)  {
                narrayReturn[i][j] = (int)(lnarraySumArea[i][j] / narrayPntCnts[i][j]);
            }
        }
        return narrayReturn;
    }

    public static int[][] filterNoiseNbAvg4Color(int[][] colorMatrix, int nRadius)	{
        if (nRadius == 0)   {
            // this means stroke width must be 1.
            return colorMatrix;
        }
        
        int nWidth = colorMatrix.length, nHeight = colorMatrix[0].length;
        long[][] lnarraySumArea1 = new long[nWidth][nHeight];
        long[][] lnarraySumArea2 = new long[nWidth][nHeight];
        long[][] lnarraySumArea3 = new long[nWidth][nHeight];
        int[][] narrayPntCnts = new int[nWidth][nHeight];
        int[][] narraySumLeftEdgeVLn1 = new int[nWidth][nHeight];
        int[][] narraySumLeftEdgeVLn2 = new int[nWidth][nHeight];
        int[][] narraySumLeftEdgeVLn3 = new int[nWidth][nHeight];
        int[] narraySumTopEdgeHLn1 = new int[nHeight];
        int[] narraySumTopEdgeHLn2 = new int[nHeight];
        int[] narraySumTopEdgeHLn3 = new int[nHeight];
        for (int j = 0; j < nHeight; j ++)   {
            for (int i = 0; i < nWidth; i ++)  {
                int nThisLeft = Math.max(0, i - nRadius);
                int nThisRight = Math.min(nWidth - 1, i + nRadius);
                int nThisTop = Math.max(0, j - nRadius);
                int nThisBottom = Math.min(nHeight - 1, j + nRadius);
                if (i > 0) {
                    int nRightEdgeVLn1 = 0;
                    int nRightEdgeVLn2 = 0;
                    int nRightEdgeVLn3 = 0;
                    if (nThisRight - i == nRadius)  {
                        for (int idx = nThisTop; idx <= nThisBottom; idx ++)    {
							int rgb[] = ImgMatrixConverter.convertInt2RGB(colorMatrix[nThisRight][idx]);
                            nRightEdgeVLn1 += rgb[0];
                            nRightEdgeVLn2 += rgb[1];
                            nRightEdgeVLn3 += rgb[2];
                        }
                        int nThisRightLeftEdgeIdx = 2 * nThisRight - i;
                        if (nThisRightLeftEdgeIdx < nWidth)    {
                            narraySumLeftEdgeVLn1[nThisRightLeftEdgeIdx][j] = nRightEdgeVLn1;
                            narraySumLeftEdgeVLn2[nThisRightLeftEdgeIdx][j] = nRightEdgeVLn2;
                            narraySumLeftEdgeVLn3[nThisRightLeftEdgeIdx][j] = nRightEdgeVLn3;
                        }
                    }
                    lnarraySumArea1[i][j] = lnarraySumArea1[i - 1][j] - narraySumLeftEdgeVLn1[i - 1][j] + nRightEdgeVLn1;
                    lnarraySumArea2[i][j] = lnarraySumArea2[i - 1][j] - narraySumLeftEdgeVLn2[i - 1][j] + nRightEdgeVLn2;
                    lnarraySumArea3[i][j] = lnarraySumArea3[i - 1][j] - narraySumLeftEdgeVLn3[i - 1][j] + nRightEdgeVLn3;
                    if (i - nThisLeft == nRadius && i <= 2 * nRadius)    {   // if i > 2 * nRadius, narraySumLeftEdgeVLn[i][j] has been calculated.
                        int nLeftEdgeVLn1 = 0;
                        int nLeftEdgeVLn2 = 0;
                        int nLeftEdgeVLn3 = 0;
                        for (int idx = nThisTop; idx <= nThisBottom; idx ++)    {
							int rgb[] = ImgMatrixConverter.convertInt2RGB(colorMatrix[nThisLeft][idx]);
                            nLeftEdgeVLn1 += rgb[0];
                            nLeftEdgeVLn2 += rgb[1];
                            nLeftEdgeVLn3 += rgb[2];
                        }
                        narraySumLeftEdgeVLn1[i][j] = nLeftEdgeVLn1;
                        narraySumLeftEdgeVLn2[i][j] = nLeftEdgeVLn2;
                        narraySumLeftEdgeVLn3[i][j] = nLeftEdgeVLn3;
                    }
                } else if (j > 0)   {
                    int nBottomEdgeHLn1 = 0;
                    int nBottomEdgeHLn2 = 0;
                    int nBottomEdgeHLn3 = 0;
                    if (nThisBottom - j == nRadius) {
                        for (int idx = nThisLeft; idx <= nThisRight; idx ++)    {
							int rgb[] = ImgMatrixConverter.convertInt2RGB(colorMatrix[idx][nThisBottom]);
                            nBottomEdgeHLn1 += rgb[0];
                            nBottomEdgeHLn2 += rgb[1];
                            nBottomEdgeHLn3 += rgb[2];
                        }
                        int nThisBottomTopEdgeIdx = 2 * nThisBottom - j;
                        if (nThisBottomTopEdgeIdx < nHeight)    {
                            narraySumTopEdgeHLn1[nThisBottomTopEdgeIdx] = nBottomEdgeHLn1;
                            narraySumTopEdgeHLn2[nThisBottomTopEdgeIdx] = nBottomEdgeHLn2;
                            narraySumTopEdgeHLn3[nThisBottomTopEdgeIdx] = nBottomEdgeHLn3;
                        }
                    }
                    lnarraySumArea1[i][j] = lnarraySumArea1[i][j - 1] - narraySumTopEdgeHLn1[j - 1] + nBottomEdgeHLn1;
                    lnarraySumArea2[i][j] = lnarraySumArea2[i][j - 1] - narraySumTopEdgeHLn2[j - 1] + nBottomEdgeHLn2;
                    lnarraySumArea3[i][j] = lnarraySumArea3[i][j - 1] - narraySumTopEdgeHLn3[j - 1] + nBottomEdgeHLn3;
                    if (j - nThisTop == nRadius && j <= 2 * nRadius)    {   // if j > 2 * nRadius, narraySumTopEdgeHLn[j] has been calculated.
                        int nTopEdgeHLn1 = 0;
                        int nTopEdgeHLn2 = 0;
                        int nTopEdgeHLn3 = 0;
                        for (int idx = nThisLeft; idx <= nThisRight; idx ++)    {
							int rgb[] = ImgMatrixConverter.convertInt2RGB(colorMatrix[idx][nThisTop]);
                            nTopEdgeHLn1 += rgb[0];
                            nTopEdgeHLn2 += rgb[1];
                            nTopEdgeHLn3 += rgb[2];
                        }
                        narraySumTopEdgeHLn1[j] = nTopEdgeHLn1;
                        narraySumTopEdgeHLn2[j] = nTopEdgeHLn2;
                        narraySumTopEdgeHLn3[j] = nTopEdgeHLn3;
                    }
                } else  {   // i == 0 && j == 0
                    for (int idx = nThisLeft; idx <= nThisRight; idx ++)    {
                        for (int idx1 = nThisTop; idx1 <= nThisBottom; idx1 ++) {
							int rgb[] = ImgMatrixConverter.convertInt2RGB(colorMatrix[idx][idx1]);
                            lnarraySumArea1[i][j] += rgb[0];
                            lnarraySumArea2[i][j] += rgb[1];
                            lnarraySumArea3[i][j] += rgb[2];
                        }
                    }
                    int nRightEdgeVLn1 = 0;
                    int nRightEdgeVLn2 = 0;
                    int nRightEdgeVLn3 = 0;
                    if (nThisRight == nRadius && 2*nThisRight < nWidth)  {
                        for (int idx = nThisTop; idx <= nThisBottom; idx ++)    {
							int rgb[] = ImgMatrixConverter.convertInt2RGB(colorMatrix[nThisRight][idx]);
                            nRightEdgeVLn1 += rgb[0];
                            nRightEdgeVLn2 += rgb[1];
                            nRightEdgeVLn3 += rgb[2];
                        }
                        int nThisRightLeftEdgeIdx = 2 * nThisRight;
                        narraySumLeftEdgeVLn1[nThisRightLeftEdgeIdx][j] = nRightEdgeVLn1;
                        narraySumLeftEdgeVLn2[nThisRightLeftEdgeIdx][j] = nRightEdgeVLn2;
                        narraySumLeftEdgeVLn3[nThisRightLeftEdgeIdx][j] = nRightEdgeVLn3;
                    }
                    int nBottomEdgeHLn1 = 0;
                    int nBottomEdgeHLn2 = 0;
                    int nBottomEdgeHLn3 = 0;
                    if (nThisBottom == nRadius && 2*nThisBottom < nHeight) {
                        for (int idx = nThisLeft; idx <= nThisRight; idx ++)    {
							int rgb[] = ImgMatrixConverter.convertInt2RGB(colorMatrix[idx][nThisBottom]);
                            nBottomEdgeHLn1 += rgb[0];
                            nBottomEdgeHLn2 += rgb[1];
                            nBottomEdgeHLn3 += rgb[2];
                        }
                        int nThisBottomTopEdgeIdx = 2 * nThisBottom;
                        narraySumTopEdgeHLn1[nThisBottomTopEdgeIdx] = nBottomEdgeHLn1;
                        narraySumTopEdgeHLn2[nThisBottomTopEdgeIdx] = nBottomEdgeHLn2;
                        narraySumTopEdgeHLn3[nThisBottomTopEdgeIdx] = nBottomEdgeHLn3;
                    }
                }
                narrayPntCnts[i][j] = (nThisRight - nThisLeft + 1) * (nThisBottom - nThisTop + 1);
            }
        }
        int[][] narrayReturn = new int[nWidth][nHeight];  // initialize a zero array.
        for (int i = 0; i < nWidth; i ++)  {
            for (int j = 0; j < nHeight; j ++)  {
                narrayReturn[i][j] = ImgMatrixConverter.convertRGB2Int(
										(int)(lnarraySumArea1[i][j] / narrayPntCnts[i][j]),
										(int)(lnarraySumArea2[i][j] / narrayPntCnts[i][j]),
										(int)(lnarraySumArea3[i][j] / narrayPntCnts[i][j]));
            }
        }
        return narrayReturn;
    }

    public static int[][] filterNoiseAllMedium4Gray(int[][] grayMatrix, int nWinRadius)  {
        int[][] result = new int[grayMatrix.length][grayMatrix[0].length];
        for (int col = 0; col < grayMatrix.length; col ++)  {
            for (int row = 0; row < grayMatrix[0].length; row ++)   {
                LinkedList<Integer> listAll = new LinkedList<Integer>();
                for (int idx = col - nWinRadius; idx <= col + nWinRadius; idx ++)   {
                    for (int idx1 = row - nWinRadius; idx1 <= row + nWinRadius; idx1 ++)    {
                        if (idx >= 0 && idx < grayMatrix.length && idx1 >= 0 && idx1 < grayMatrix[0].length)    {
                            int idx3 = 0;
                            for (; idx3 < listAll.size(); idx3 ++)  {
                                if (listAll.get(idx3) > grayMatrix[idx][idx1])  {
                                    listAll.add(idx3, grayMatrix[idx][idx1]);
                                    break;
                                }
                            }
                            if (idx3 == listAll.size()) {
                                listAll.add(grayMatrix[idx][idx1]);
                            }
                        }
                    }
                }
                
                result[col][row] = listAll.get(listAll.size() / 2);
            }
        }
        return result;
    }
}
