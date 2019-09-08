/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyzapps.imgmatrixproc;

import com.cyzapps.VisualMFP.Position3D;

/**
 *
 * @author tonyc
 */
public class ImgRectifier {
    public static Position3D adjustPntByAngle(Position3D pnt, double dAngle)   {
        double dX = pnt.getX();
        double dY = pnt.getY();
        if (dX == 0 && dY == 0) {
            return pnt;
        } else  {
            double dRadius = Math.sqrt(dX * dX + dY * dY);
            double dAngleOriginal = Math.atan2(dY, dX);
            double dAngleNew = dAngleOriginal + dAngle;
            double dXNew = Math.cos(dAngleNew) * dRadius;
            double dYNew = Math.sin(dAngleNew) * dRadius;
            return new Position3D(dXNew, dYNew);
        }
    }
    
    public static Position3D adjustPntByAngle(Position3D pnt, double dAngle, Position3D pnt0)   {
        double dX = pnt.getX() - pnt0.getX();
        double dY = pnt.getY() - pnt0.getY();
        if (dX == 0 && dY == 0) {
            return pnt;
        } else  {
            double dRadius = Math.sqrt(dX * dX + dY * dY);
            double dAngleOriginal = Math.atan2(dY, dX);
            double dAngleNew = dAngleOriginal + dAngle;
            double dXNew = Math.cos(dAngleNew) * dRadius;
            double dYNew = Math.sin(dAngleNew) * dRadius;
            return new Position3D(dXNew + pnt0.getX(), dYNew + pnt0.getY());
        }
    }
    
    // returns min-containning angle adjusted array.
    // darrayNewX0Y0 must be an allocated array whose size is no less than 2.
    public static byte[][] adjustMatrixByAngle(byte[][] biArray, double dAngle, double[] darrayNewX0Y0) {
        double dNewXMax = -Double.MAX_VALUE, dNewXMin = Double.MAX_VALUE, dNewYMax = -Double.MAX_VALUE, dNewYMin = Double.MAX_VALUE;
        int nUnitDiv = 2;
        // have to calculate denser matrix otherwise we may see holes in stroke.
        Position3D[][] pntarrayRotated = new Position3D[biArray.length * nUnitDiv][biArray[0].length * nUnitDiv];
        for (int col = 0; col < biArray.length; col ++) {
            for (int row = 0; row < biArray[0].length; row ++)  {
                if (biArray[col][row] == 1) {
                    for (int nDivIdx = 0; nDivIdx < nUnitDiv; nDivIdx ++)  {
                        for (int nDivIdx1 = 0; nDivIdx1 < nUnitDiv; nDivIdx1 ++)  {
                            int nColNew = nUnitDiv * col + nDivIdx, nRowNew = nUnitDiv * row + nDivIdx;
                            Position3D pnt = new Position3D((double)nColNew/(double)nUnitDiv, (double)nRowNew/(double)nUnitDiv);
                            Position3D pntRotated = adjustPntByAngle(pnt, dAngle);
                            pntarrayRotated[nColNew][nRowNew] = pntRotated;
                            if (pntRotated.getX() > dNewXMax)   {
                                dNewXMax = pntRotated.getX();
                            }
                            if (pntRotated.getX() < dNewXMin)   {
                                dNewXMin = pntRotated.getX();
                            }
                            if (pntRotated.getY() > dNewYMax)   {
                                dNewYMax = pntRotated.getY();
                            }
                            if (pntRotated.getY() < dNewYMin)   {
                                dNewYMin = pntRotated.getY();
                            }
                        }
                    }
                }
            }
        }
        if (dNewXMax == -Double.MAX_VALUE || dNewXMin == Double.MAX_VALUE || dNewYMax == -Double.MAX_VALUE || dNewYMin == Double.MAX_VALUE)   {
            // no point in the array.
            darrayNewX0Y0[0] = 0;
            darrayNewX0Y0[1] = 0;
            return new byte[0][0];
        } else  {
            byte[][] result = new byte[(int)dNewXMax - (int)dNewXMin + 1][(int)dNewYMax - (int)dNewYMin + 1];
            for (int col = 0; col < pntarrayRotated.length; col ++) {
                for (int row = 0; row < pntarrayRotated[0].length; row ++)  {
                    if (pntarrayRotated[col][row] != null)  {
                        int nXNew = (int)pntarrayRotated[col][row].getX() - (int)dNewXMin;
                        int nYNew = (int)pntarrayRotated[col][row].getY() - (int)dNewYMin;
                        result[nXNew][nYNew] = 1;
                    }
                }
            }
            darrayNewX0Y0[0] = -(int)dNewXMin;
            darrayNewX0Y0[1] = -(int)dNewYMin;
            return result;
        }
    }  
    
    // returns min-containning angle adjusted array.
    // darrayNewX0Y0 must be an allocated array whose size is no less than 2.
    public static byte[][] adjustMatrixByAngle(byte[][] biArray, double dAngle, double dX0, double dY0, double[] darrayNewX0Y0) {
        double dNewXMax = -Double.MAX_VALUE, dNewXMin = Double.MAX_VALUE, dNewYMax = -Double.MAX_VALUE, dNewYMin = Double.MAX_VALUE;
        int nUnitDiv = 2;
        Position3D pnt0 = new Position3D(dX0, dY0);
        // have to calculate denser matrix otherwise we may see holes in stroke.
        Position3D[][] pntarrayRotated = new Position3D[biArray.length * nUnitDiv][biArray[0].length * nUnitDiv];
        for (int col = 0; col < biArray.length; col ++) {
            for (int row = 0; row < biArray[0].length; row ++)  {
                if (biArray[col][row] == 1) {
                    for (int nDivIdx = 0; nDivIdx < nUnitDiv; nDivIdx ++)  {
                        for (int nDivIdx1 = 0; nDivIdx1 < nUnitDiv; nDivIdx1 ++)  {
                            int nColNew = nUnitDiv * col + nDivIdx, nRowNew = nUnitDiv * row + nDivIdx;
                            Position3D pnt = new Position3D((double)nColNew/(double)nUnitDiv, (double)nRowNew/(double)nUnitDiv);
                            Position3D pntRotated = adjustPntByAngle(pnt, dAngle, pnt0);
                            pntarrayRotated[nColNew][nRowNew] = pntRotated;
                            if (pntRotated.getX() > dNewXMax)   {
                                dNewXMax = pntRotated.getX();
                            }
                            if (pntRotated.getX() < dNewXMin)   {
                                dNewXMin = pntRotated.getX();
                            }
                            if (pntRotated.getY() > dNewYMax)   {
                                dNewYMax = pntRotated.getY();
                            }
                            if (pntRotated.getY() < dNewYMin)   {
                                dNewYMin = pntRotated.getY();
                            }
                        }
                    }
                }
            }
        }
        if (dNewXMax == -Double.MAX_VALUE || dNewXMin == Double.MAX_VALUE || dNewYMax == -Double.MAX_VALUE || dNewYMin == Double.MAX_VALUE)   {
            // no point in the array.
            darrayNewX0Y0[0] = dX0;
            darrayNewX0Y0[1] = dY0;
            return new byte[0][0];
        } else  {
            byte[][] result = new byte[(int)dNewXMax - (int)dNewXMin + 1][(int)dNewYMax - (int)dNewYMin + 1];
            for (int col = 0; col < pntarrayRotated.length; col ++) {
                for (int row = 0; row < pntarrayRotated[0].length; row ++)  {
                    if (pntarrayRotated[col][row] != null)  {
                        int nXNew = (int)pntarrayRotated[col][row].getX() - (int)dNewXMin;
                        int nYNew = (int)pntarrayRotated[col][row].getY() - (int)dNewYMin;
                        result[nXNew][nYNew] = 1;
                    }
                }
            }
            darrayNewX0Y0[0] = dX0 - (int)dNewXMin;
            darrayNewX0Y0[1] = dY0 - (int)dNewYMin;
            return result;
        }
    }
    
    public static double calcRectifyAngleHough(byte[][] biArray)    {   // biArray should be minimum container
        // assume the rectify angle should be less than 5 degree.
        int nDegreeAdjRange = 5;
        int nDegreeStepDiv = 2;
        int nMinPntInOneCellOnAvg = 2;
        int nListIdx = 0;
        int nDegreeRangeCnt = nDegreeAdjRange * nDegreeStepDiv * 2 + 1;
        double dMaxRX = -Double.MAX_VALUE, dMinRX = Double.MAX_VALUE;
        double dMaxRY = -Double.MAX_VALUE, dMinRY = Double.MAX_VALUE;
        int nNumOfPnts = 0;
        for (int col = 0; col < biArray.length; col ++) {
            for (int row = 0; row < biArray[0].length; row ++)  {
                if (biArray[col][row] == 1) {
                    nNumOfPnts ++;
                }
            }
        }
        int[] narrayDegree = new int[nNumOfPnts * nDegreeRangeCnt];
        double[] darrayRX = new double[nNumOfPnts * nDegreeRangeCnt];
        double[] darrayRY = new double[nNumOfPnts * nDegreeRangeCnt];
        for (int col = 0; col < biArray.length; col ++) {
            for (int row = 0; row < biArray[0].length; row ++)  {
                if (biArray[col][row] == 1) {
                    int nDegreeIdx = -nDegreeAdjRange * nDegreeStepDiv;
                    while(nDegreeIdx <= nDegreeAdjRange * nDegreeStepDiv)   {
                        double dAngle =  nDegreeIdx * 0.017453293 / nDegreeStepDiv;
                        double dRX = col + row * Math.tan(dAngle);
                        double dRY = row - col * Math.tan(dAngle);
                        narrayDegree[nListIdx] = nDegreeIdx;
                        darrayRX[nListIdx] = dRX;
                        darrayRY[nListIdx] = dRY;
                        nListIdx ++;
                        if (dRX > dMaxRX)   {
                            dMaxRX = dRX;
                        }
                        if (dRX < dMinRX)   {
                            dMinRX = dRX;
                        }
                        if (dRY > dMaxRY) {
                            dMaxRY = dRY;
                        }
                        if (dRY < dMinRY) {
                            dMinRY = dRY;
                        }
                        nDegreeIdx ++;
                    }
                }
            }
        }
        
        int nNumRRangesX = (int)dMaxRX - (int)dMinRX + 1;
        int nNumRRangesY = (int)dMaxRY - (int)dMinRY + 1;
        if ((dMaxRX == dMinRX) || (dMaxRY == dMinRY)) {
            return 0;
        } else if (nListIdx < nMinPntInOneCellOnAvg * nDegreeRangeCnt * nNumRRangesX
                || nListIdx < nMinPntInOneCellOnAvg * nDegreeRangeCnt * nNumRRangesY)  {
            // too few points.
            return 0;
        } else  {
            int[][] narrayPntCntsX = new int[nDegreeRangeCnt][nNumRRangesX];
            int[][] narrayPntCntsY = new int[nDegreeRangeCnt][nNumRRangesY];
            for (int idx = 0; idx < nListIdx; idx ++)   {
                int nRIdxX = (int)darrayRX[idx] - (int)dMinRX;
                int nRIdxY = (int)darrayRY[idx] - (int)dMinRY;
                int nDegreeIdx = narrayDegree[idx] + nDegreeAdjRange * nDegreeStepDiv;
                narrayPntCntsX[nDegreeIdx][nRIdxX] ++;
                narrayPntCntsY[nDegreeIdx][nRIdxY] ++;
            }
            int nMaxPntCntX = 0, nMaxPntCntY = 0;
            int nMaxPntDegreeIdxX = -1, nMaxPntDegreeIdxY = -1;
            int idx = 0, idx1 = 0;
            for (idx = 0; idx < nDegreeRangeCnt; idx ++)    {
                for (idx1 = 0; idx1 < nNumRRangesX; idx1 ++) {
                    if (narrayPntCntsX[idx][idx1] > nMaxPntCntX)  {
                        nMaxPntCntX = narrayPntCntsX[idx][idx1];
                        nMaxPntDegreeIdxX = idx;
                    }
                }
                for (idx1 = 0; idx1 < nNumRRangesY; idx1 ++) {
                    if (narrayPntCntsY[idx][idx1] > nMaxPntCntY)  {
                        nMaxPntCntY = narrayPntCntsY[idx][idx1];
                        nMaxPntDegreeIdxY = idx;
                    }
                }
            }
            int nMaxPntDegreeIdx =(nMaxPntCntX > nMaxPntCntY)?nMaxPntDegreeIdxX:nMaxPntDegreeIdxY;
            double dBestDegree = nMaxPntDegreeIdx / (double)nDegreeStepDiv - nDegreeAdjRange;
            return Math.toRadians(dBestDegree);
        }
    }
}
