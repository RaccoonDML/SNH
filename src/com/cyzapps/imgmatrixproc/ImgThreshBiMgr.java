/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyzapps.imgmatrixproc;

/**
 *
 * @author tonyc
 */
public class ImgThreshBiMgr {
    
    public static byte[][] convertGray2Bi2ndD(int[][] grayMatrix, int nRadius)  {
        // for black ink only
        int[][] narray2ndDMin = new int[grayMatrix.length][grayMatrix[0].length];
        int[][] narray2ndDMinProc = new int[grayMatrix.length][grayMatrix[0].length];
        int nTotalPntCnt = grayMatrix.length * grayMatrix[0].length;
        long lnSumof2ndDMin = 0, lnSumofSq2ndDMin = 0;
        int nMax2ndMin = Integer.MIN_VALUE, nMin2ndMin = Integer.MAX_VALUE;
        for (int col = nRadius; col < grayMatrix.length - nRadius; col ++) {
            for (int row = nRadius; row < grayMatrix[0].length - nRadius; row ++)  {
                int nLeft = col - nRadius,  //Math.max(0, col - nRadius),
                    nRight = col + nRadius; //Math.min(grayMatrix.length - 1, col + nRadius);
                int nTop = row - nRadius,   //Math.max(0, row - nRadius),
                    nBottom = row + nRadius;//Math.min(grayMatrix[0].length - 1, row + nRadius);
                int nH2ndD = (grayMatrix[col][row] - grayMatrix[nLeft][row]) + (grayMatrix[col][row] - grayMatrix[nRight][row]);
                int nV2ndD = (grayMatrix[col][row] - grayMatrix[col][nTop]) + (grayMatrix[col][row] - grayMatrix[col][nBottom]);
                int nF2ndD = (grayMatrix[col][row] - grayMatrix[nLeft][nTop]) + (grayMatrix[col][row] - grayMatrix[nRight][nBottom]);
                int nB2ndD = (grayMatrix[col][row] - grayMatrix[nLeft][nBottom]) + (grayMatrix[col][row] - grayMatrix[nRight][nTop]);
                narray2ndDMin[col][row] = Math.min(Math.min(nV2ndD, nH2ndD), Math.min(nF2ndD, nB2ndD));
                if (narray2ndDMin[col][row] > nMax2ndMin)   {
                    nMax2ndMin = narray2ndDMin[col][row];
                }
                if (narray2ndDMin[col][row] < nMin2ndMin)   {
                    nMin2ndMin = narray2ndDMin[col][row];
                }
                lnSumof2ndDMin += narray2ndDMin[col][row];
                lnSumofSq2ndDMin += narray2ndDMin[col][row] * narray2ndDMin[col][row];
            }
        }
        double dAvg = lnSumof2ndDMin / (double)nTotalPntCnt;
        double dStdev = Math.sqrt(lnSumofSq2ndDMin / (double)nTotalPntCnt - dAvg * dAvg);
        //ImgMatrixOutput.printMatrix(narray2ndDMin);
        /*double[] darrayGs = new double[nMax2ndMin - nMin2ndMin + 1];
        for (int col = 0; col < grayMatrix.length; col ++) {
            for (int row = 0; row < grayMatrix[0].length; row ++)  {
                narray2ndDMinProc[col][row] = narray2ndDMin[col][row] - nMin2ndMin;
            }
        }
        // make it faster, use dAvg - dStdev.
        int nOTSUThresh = calcOTSUThresh(narray2ndDMinProc, 0, 0, narray2ndDMinProc.length, narray2ndDMinProc[0].length, darrayGs) + nMin2ndMin;
        */
        //todo dml _change 2.2
        int nThresh = (int)((dAvg - dStdev)*1.5);
        byte[][] biMatrix = new byte[grayMatrix.length][grayMatrix[0].length], biMatrix1 = new byte[grayMatrix.length][grayMatrix[0].length];
        for (int col = 0; col < grayMatrix.length; col ++) {
            int row = grayMatrix[0].length - 1;
            int nLast2ndDMinThresh = Integer.MIN_VALUE;
            while(row >= 0)   {
                if (narray2ndDMin[col][row] > nThresh)  {
                    if (grayMatrix[col][row] <= nLast2ndDMinThresh)    {
                        biMatrix[col][row] += 1;
                    } else  {
                        // on line is broken here.
                        nLast2ndDMinThresh = Integer.MIN_VALUE;
                    }
                } else  {   // if (narray2ndDMin[col][row] <= nThresh)  {
                    biMatrix[col][row] = 4;
                    if (nLast2ndDMinThresh == Integer.MIN_VALUE)    {
                        nLast2ndDMinThresh = grayMatrix[col][row];
                    }
                }
                row --;
            }
            row = 0;
            nLast2ndDMinThresh = Integer.MIN_VALUE;
            while(row < grayMatrix[0].length)   {
                if (narray2ndDMin[col][row] > nThresh)  {
                    if (grayMatrix[col][row] <= nLast2ndDMinThresh && biMatrix[col][row] == 1)    {
                        biMatrix[col][row] += 1;
                    } else  {
                        // on line is broken here.
                        nLast2ndDMinThresh = Integer.MIN_VALUE;
                    }
                } else  {   // if (narray2ndDMin[col][row] <= nThresh)  {
                    //biMatrix[col][row] = 4;
                    if (nLast2ndDMinThresh == Integer.MIN_VALUE)    {
                        nLast2ndDMinThresh = grayMatrix[col][row];
                    }
                }
                row ++;
            }
            row = grayMatrix[0].length - 1;
            nLast2ndDMinThresh = Integer.MIN_VALUE;
            while(row >= 0)   {
                if (narray2ndDMin[col][row] > nThresh)  {
                    if (grayMatrix[col][row] <= nLast2ndDMinThresh && biMatrix[col][row] == 2)    {
                        biMatrix[col][row] += 2;
                    } else  {
                        // on line is broken here.
                        nLast2ndDMinThresh = Integer.MIN_VALUE;
                    }
                } else  {   // if (narray2ndDMin[col][row] <= nThresh)  {
                    //biMatrix[col][row] = 4;
                    if (nLast2ndDMinThresh == Integer.MIN_VALUE)    {
                        nLast2ndDMinThresh = grayMatrix[col][row];
                    }
                }
                row --;
            }
        }
        for (int row = 0; row < grayMatrix[0].length; row ++) {
            int col = grayMatrix.length - 1;
            int nLast2ndDMinThresh = Integer.MIN_VALUE;
            while(col >= 0)   {
                if (narray2ndDMin[col][row] > nThresh)  {
                    if (grayMatrix[col][row] <= nLast2ndDMinThresh)    {
                        biMatrix1[col][row] += 1;
                    } else  {
                        // on line is broken here.
                        nLast2ndDMinThresh = Integer.MIN_VALUE;
                    }
                } else  {   // if (narray2ndDMin[col][row] <= nThresh)  {
                    biMatrix1[col][row] = 4;
                    if (nLast2ndDMinThresh == Integer.MIN_VALUE)    {
                        nLast2ndDMinThresh = grayMatrix[col][row];
                    }
                }
                col --;
            }
            col = 0;
            nLast2ndDMinThresh = Integer.MIN_VALUE;
            while(col < grayMatrix.length)   {
                if (narray2ndDMin[col][row] > nThresh)  {
                    if (grayMatrix[col][row] <= nLast2ndDMinThresh && biMatrix1[col][row] == 1)    {
                        // if biMatrix1[col][row] is not 1, then must be a single edge.
                        biMatrix1[col][row] += 1;
                    } else  {
                        // on line is broken here. seems that this is a single edge, revert all the 2 value to 0 because it is not in stroke.
                        nLast2ndDMinThresh = Integer.MIN_VALUE;
                    }
                } else  {   // if (narray2ndDMin[col][row] <= nThresh)  {
                    //biMatrix1[col][row] = 4;
                    if (nLast2ndDMinThresh == Integer.MIN_VALUE)    {
                        nLast2ndDMinThresh = grayMatrix[col][row];
                    }
                }
                col ++;
            }
            col = grayMatrix.length - 1;
            nLast2ndDMinThresh = Integer.MIN_VALUE;
            while(col >= 0)   {
                if (narray2ndDMin[col][row] > nThresh)  {
                    if (grayMatrix[col][row] <= nLast2ndDMinThresh && biMatrix1[col][row] == 2)    {
                        biMatrix1[col][row] += 2;
                    } else  {
                        // on line is broken here.
                        nLast2ndDMinThresh = Integer.MIN_VALUE;
                    }
                } else  {   // if (narray2ndDMin[col][row] <= nThresh)  {
                    //biMatrix1[col][row] = 4;
                    if (nLast2ndDMinThresh == Integer.MIN_VALUE)    {
                        nLast2ndDMinThresh = grayMatrix[col][row];
                    }
                }
                col --;
            }
        }
        for (int row = 0; row < grayMatrix[0].length; row ++) {
            for (int col = 0; col < grayMatrix.length; col ++)   {
                biMatrix[col][row] = (byte)((biMatrix[col][row] >> 2)|(biMatrix1[col][row] >> 2));
            }
        }
        return biMatrix;
    }
    
    public static byte[][] convertGray2BiImpPOTSU(int[][] grayMatrix, int nXRadius, int nYRadius, boolean bOptimizeG4Pnt)  {
    	int nOverallThresh = calcOTSUThresh(grayMatrix, 0, 0, grayMatrix.length, grayMatrix[0].length, null);
        byte[][] biMatrix = new byte[grayMatrix.length][grayMatrix[0].length];
        for (int col = 0; col < grayMatrix.length; col ++) {
            for (int row = 0; row < grayMatrix[0].length; row ++)  {
                if (grayMatrix[col][row] == 0)  {
                    // must be black
                    biMatrix[col][row] = 1;
                } else if (grayMatrix[col][row] >= nOverallThresh)  {
                    // must be white
                    biMatrix[col][row] = 0;
                } else  {
                    int nLeft = Math.max(0, col - nXRadius);
                    int nRight = Math.min(col + nXRadius, grayMatrix.length - 1);
                    int nWidth = nRight - nLeft + 1;
                    int nTop = Math.max(0, row - nYRadius);
                    int nBottom = Math.min(row + nYRadius, grayMatrix[0].length - 1);
                    int nHeight = nBottom - nTop + 1;
                    if (!bOptimizeG4Pnt)	{
	                    double dOTSUG = calcOTSUGAtThresh(grayMatrix, nLeft, nTop, nWidth, nHeight, grayMatrix[col][row]);
	                    double dOTSUGPrev = calcOTSUGAtThresh(grayMatrix, nLeft, nTop, nWidth, nHeight, grayMatrix[col][row] - 1);
	                    double dOTSUGNext = calcOTSUGAtThresh(grayMatrix, nLeft, nTop, nWidth, nHeight, grayMatrix[col][row] + 1);
	                    double dOTSUGMax = Math.max(Math.max(dOTSUGPrev, dOTSUGNext), dOTSUG);
	                    if (dOTSUGMax == dOTSUG) {
	                        // grayMatrix[col][row] is thresh
	                        biMatrix[col][row] = 0;
	                    } else if (dOTSUGMax == dOTSUGPrev) {
	                        // seems like thresh is before grayMatrix[col][row]
	                        biMatrix[col][row] = 0;
	                    } else  {
	                        // seems like thresh is after grayMatrix[col][row]
	                        biMatrix[col][row] = 1;
	                    }
                    } else	{
                    	int thresh4Pnt = calcOTSUThresh(grayMatrix, nLeft, nTop, nWidth, nHeight, null);
                    	if (grayMatrix[col][row] < thresh4Pnt)	{
                    		biMatrix[col][row] = 1;
                    	} else	{
                    		biMatrix[col][row] = 0;
                    	}
                    }
                }
            }
        }
        return biMatrix;
    }
    
    public static byte[][] convertGray2BiSepOTSU(int[][] grayMatrix, int nSepSize, double dSepWeight)  {
        int nOverallThresh = calcOTSUThresh(grayMatrix, 0, 0, grayMatrix.length, grayMatrix[0].length, null);
        byte[][] biMatrix = new byte[grayMatrix.length][grayMatrix[0].length];
        int col = 0, row = 0;
        while (col < grayMatrix.length) {
            int nLeft = col;
            int nWidth = Math.min(nLeft + nSepSize, grayMatrix.length) - nLeft;
            if (nWidth == 0)    {
                break;
            }
            row = 0;
            while (row < grayMatrix[0].length)  {
                int nTop = row;
                int nHeight = Math.min(nTop + nSepSize, grayMatrix[0].length) - nTop;
                if (nHeight == 0)   {
                    break;
                }
                double dPartThresh = calcOTSUThresh(grayMatrix, nLeft, nTop, nWidth, nHeight, null);
                dPartThresh = dPartThresh * dSepWeight + (1 - dSepWeight) * nOverallThresh;
                for (int col1 = nLeft; col1 < nLeft + nWidth; col1 ++)  {
                    for (int row1 = nTop; row1 < nTop + nHeight; row1 ++) {
                        if (grayMatrix[col1][row1] < dPartThresh)  {
                            // black
                            biMatrix[col1][row1] = 1;
                        } else  {
                            // white
                            biMatrix[col1][row1] = 0;
                        }
                    }
                }
                
                row += nSepSize;
            }
            col += nSepSize;
        }
        return biMatrix;
    }
    
    public static byte[][] convertGray2BiOTSU(int[][] grayMatrix)  {
        int nThresh = calcOTSUThresh(grayMatrix, 0, 0, grayMatrix.length, grayMatrix[0].length, null);
        // ok, now thresh is nMaxGIdx + 1
        byte[][] biMatrix = new byte[grayMatrix.length][];
        for (int col = 0; col < grayMatrix.length; col ++)  {
            biMatrix[col] = new byte[grayMatrix[0].length];
            for (int row = 0; row < grayMatrix[0].length; row ++) {
                if (grayMatrix[col][row] < nThresh)  {
                    // black
                    biMatrix[col][row] = 1;
                } else  {
                    // white
                    biMatrix[col][row] = 0;
                }
            }
        }
        return biMatrix;
        
    }
	
	public static byte[][] convertGray2BiWellner(int[][] grayMatrix, int nXRadius, int nYRadius, double dThresh)	{
        byte[][] biMatrix = new byte[grayMatrix.length][];
        for (int col = 0; col < grayMatrix.length; col ++)  {
            biMatrix[col] = new byte[grayMatrix[col].length];
            for (int row = 0; row < grayMatrix[col].length; row ++) {
                double dAvg = calcGrayAvg(grayMatrix, col, row, nXRadius, nYRadius);
                if (grayMatrix[col][row] < dAvg * dThresh)  {
                    // black
                    biMatrix[col][row] = 1;
                } else  {
                    // white
                    biMatrix[col][row] = 0;
                }
            }
        }
        return biMatrix;
    }
    
    public static double calcGrayAvg(int[][] grayMatrix, int x, int y, int nXRadius, int nYRadius)    {
        double dSum = 0;
        for (int col = x - nXRadius; col <= x + nXRadius; col ++)    {
            for (int row = y - nYRadius; row <= y + nYRadius; row ++)    {
                if (col < 0 && row >= 0 && row < grayMatrix[0].length)  {
                    dSum += grayMatrix[0][row];
                } else if (col >= grayMatrix.length && row >= 0 && row < grayMatrix[0].length)  {
                    dSum += grayMatrix[grayMatrix.length - 1][row];
                } else if (col >= 0 && col < grayMatrix.length && row < 0)  {
                    dSum += grayMatrix[col][0];
                } else if (col >= 0 && col < grayMatrix.length && row >= grayMatrix[0].length)  {
                    dSum += grayMatrix[col][grayMatrix[0].length - 1];
                } else if (col < 0 && row < 0)  {
                    dSum += grayMatrix[0][0];
                } else if (col < 0 && row >= grayMatrix[0].length)  {
                    dSum += grayMatrix[0][grayMatrix[0].length - 1];
                } else if (col >= grayMatrix.length && row < 0)    {
                    dSum += grayMatrix[grayMatrix.length - 1][0];
                } else if (col >= grayMatrix.length && row >= grayMatrix[0].length) {
                    dSum += grayMatrix[grayMatrix.length - 1][grayMatrix[0].length - 1];
                } else  {
                    dSum += grayMatrix[col][row];
                }
            }
        }
        return dSum / ((2 * nXRadius + 1) * (2 * nYRadius + 1));
    }
    
    public static double calcOTSUGAtThresh(int[][] grayMatrix, int nLeft, int nTop, int nWidth, int nHeight, int thresh)  {
        long lnTotalPntCnt = nWidth * nHeight;
        long lnTotalBlackCnt = 0, lnTotalWhiteCnt = 0;
        long lnTotalBlackGrayLvl = 0, lnTotalWhiteGrayLvl = 0;
        for (int col = nLeft; col < nLeft + nWidth; col ++)    {
            for (int row = nTop; row < nTop + nHeight; row ++)  {
                if (grayMatrix[col][row] < thresh)   {
                    lnTotalBlackCnt ++;
                    lnTotalBlackGrayLvl += grayMatrix[col][row];
                } else  {
                    lnTotalWhiteCnt ++;
                    lnTotalWhiteGrayLvl += grayMatrix[col][row];
                }
            }
        }
        
        double dOTSUG = 0;
        if (lnTotalBlackCnt != 0 && lnTotalWhiteCnt != 0)	{
            double dAvgBlackGrayLvl = (double)lnTotalBlackGrayLvl / (double)lnTotalBlackCnt;
            double dAvgWhiteGrayLvl = (double)lnTotalWhiteGrayLvl / (double)lnTotalWhiteCnt;
        	dOTSUG = (dAvgWhiteGrayLvl - dAvgBlackGrayLvl)
        			* (dAvgWhiteGrayLvl - dAvgBlackGrayLvl)
        			* lnTotalBlackCnt * lnTotalWhiteCnt
        			/ (double)(lnTotalPntCnt * lnTotalPntCnt);
        }
        return dOTSUG;
    }
    
    public static int calcOTSUThresh(int[][] grayMatrix, int nLeft, int nTop, int nWidth, int nHeight, double[] darrayG)  {
        int nGrayLevels = 256;
        if (darrayG == null)	{
        	darrayG = new double[nGrayLevels];
        } else	{
        	nGrayLevels = darrayG.length;
        }
        long[] lnarrayCnts = new long[nGrayLevels];
        long[] lnarrayCntTimesLvl = new long[nGrayLevels];
        for (int col = nLeft; col < nLeft + nWidth; col ++)	{
        	for (int row = nTop; row < nTop + nHeight; row ++)	{
        		lnarrayCnts[grayMatrix[col][row]] ++;
        	}
        }
        long lnTotalPntCnt = nWidth * nHeight;	// lnTotalPntCnt * lnTotalPntCnt may overflow integer.
        long lnSumCntTimesLvl = 0;
        for (int idx = 0; idx < nGrayLevels; idx ++)	{
        	lnarrayCntTimesLvl[idx] = lnarrayCnts[idx] * idx;
        	lnSumCntTimesLvl += lnarrayCntTimesLvl[idx];
        }
        
        int nMaxGIdx = 0;
        long lnBlackCnt = 0, lnWhiteCnt = nWidth * nHeight;
        long lnSumBlackCTimesL = 0, lnSumWhiteCTimesL = lnSumCntTimesLvl;
        for (int thresh = 0; thresh < nGrayLevels; thresh ++)   {
            if (thresh > 0) {
                lnBlackCnt += lnarrayCnts[thresh - 1];
                lnWhiteCnt -= lnarrayCnts[thresh - 1];
                lnSumBlackCTimesL += lnarrayCntTimesLvl[thresh - 1];
                lnSumWhiteCTimesL -= lnarrayCntTimesLvl[thresh - 1];
            }
        	if (lnBlackCnt == 0 || lnWhiteCnt == 0)	{
        		darrayG[thresh] = 0;
        	} else	{
                double dAvgBlackGrayLvl = (double)lnSumBlackCTimesL / (double)lnBlackCnt;
                double dAvgWhiteGrayLvl = (double)lnSumWhiteCTimesL / (double)lnWhiteCnt;
	        	darrayG[thresh] = (dAvgWhiteGrayLvl - dAvgBlackGrayLvl)
	        					* (dAvgWhiteGrayLvl - dAvgBlackGrayLvl)
	        					* lnBlackCnt * lnWhiteCnt
	        					/(double)(lnTotalPntCnt * lnTotalPntCnt);
        	}
            if (darrayG[thresh] > darrayG[nMaxGIdx])    {
                nMaxGIdx = thresh;
            }
        }
        
        return nMaxGIdx;
    }
}
