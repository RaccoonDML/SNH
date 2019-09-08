package com.cyzapps.mathrecog;

import com.cyzapps.VisualMFP.Position3D;
import java.util.Arrays;
import java.util.LinkedList;

//表达式分离器
public class ExprSeperator {

    // should be calculated dynamically. To ensure thread save, should not make them static.
    //public static double msdAvgCharHeight = 6;  // average character height.
    //public static double msdAvgStrokeWidth = 3; // average stroke width
    
    public static class CutPath {
        public Position3D[] mp3Path = new Position3D[0];
        public double mdLen = 0;
        public double mdLenExtraMeasure1 = 0;   // number of cut strokes
        public double mdLenExtraMeasure2 = 0;   // max cut stroke width
        public double mdLenExtraMeasure3 = 0;   // number of cut points
        public double mdLenExtraMeasure4 = 0;   // real cut length (inclined cut means 1.414 points)
    }

    //计算切割距离
    //========================================================================================================
    // calculate Horizontal cut path in the band area defined nLeft, nTop, nWidth and nWeight
    public static CutPath calcHorizontalCutPath(byte[][] barrayImg, int nLeft, int nTop, int nWidth, int nHeight, boolean bUseRealLen)    {
        // do not check parameter to save calculating time.
        int[][] narrayMinRoutes = new int[nWidth][nHeight];
        int[][] narrayMinRoutesPrev = new int[nWidth][nHeight];
        int[][] narrayMinRoutesCuts = new int[nWidth][nHeight];
        int[][] narrayMinRoutesCutsPrev = new int[nWidth][nHeight];
        double[] darrayMinRouteLens  = new double[nHeight];
        double[] darrayMinRouteLensPrev = new double[nHeight];
        double[] darrayMinRoutePntLens  = new double[nHeight];
        double[] darrayMinRoutePntLensPrev = new double[nHeight];
        double[] darrayMinRouteRealLens  = new double[nHeight];
        double[] darrayMinRouteRealLensPrev = new double[nHeight];
        double dInclinedStepRealLen = 1.4142135623731;
        double dInclinedStepLen = bUseRealLen?dInclinedStepRealLen:1;
        double dStraightStepLen = 1;
        for (int idx = nLeft; idx < nLeft + nWidth; idx ++)  {
            // swap prev and this
            double[] darrayLenSwapTmp = darrayMinRouteLensPrev;
            darrayMinRouteLensPrev = darrayMinRouteLens;
            darrayMinRouteLens = darrayLenSwapTmp;
            
            darrayLenSwapTmp = darrayMinRoutePntLensPrev;
            darrayMinRoutePntLensPrev = darrayMinRoutePntLens;
            darrayMinRoutePntLens = darrayLenSwapTmp;
            
            darrayLenSwapTmp = darrayMinRouteRealLensPrev;
            darrayMinRouteRealLensPrev = darrayMinRouteRealLens;
            darrayMinRouteRealLens = darrayLenSwapTmp;
            
            int[][] narraySwapTmp = narrayMinRoutesPrev;
            narrayMinRoutesPrev = narrayMinRoutes;
            narrayMinRoutes = narraySwapTmp;
            int[][] narraySwapCutsTmp = narrayMinRoutesCutsPrev;
            narrayMinRoutesCutsPrev = narrayMinRoutesCuts;
            narrayMinRoutesCuts = narraySwapCutsTmp;
            for (int idx1 = nTop; idx1 < nTop + nHeight; idx1 ++)  {
                // now assume we have found the minimum paths from nLeft to idx
                // now we try to find the next step.
                double dTopLen = Double.MAX_VALUE, dMidLen, dBottomLen = Double.MAX_VALUE;
                double dTopPntLen = Double.MAX_VALUE, dMidPntLen, dBottomPntLen = Double.MAX_VALUE;
                double dTopRealLen = Double.MAX_VALUE, dMidRealLen, dBottomRealLen = Double.MAX_VALUE;
                if (idx1 > nTop)   {
                    dTopLen = darrayMinRouteLensPrev[idx1 - nTop - 1];
                    dTopPntLen = darrayMinRoutePntLensPrev[idx1 - nTop - 1];
                    dTopRealLen = darrayMinRouteRealLensPrev[idx1 - nTop - 1];
                }
                dMidLen = darrayMinRouteLensPrev[idx1 - nTop];
                dMidPntLen = darrayMinRoutePntLensPrev[idx1 - nTop];
                dMidRealLen = darrayMinRouteRealLensPrev[idx1 - nTop];
                if (idx1< nTop + nHeight - 1)   {
                    dBottomLen = darrayMinRouteLensPrev[idx1 - nTop + 1];
                    dBottomPntLen = darrayMinRoutePntLensPrev[idx1 - nTop + 1];
                    dBottomRealLen = darrayMinRouteRealLensPrev[idx1 - nTop + 1];
                }
                boolean bTopLenCut = false, bMidLenCut = false, bBottomLenCut = false;
                if (barrayImg[idx][idx1] == 1)  {
                    dTopLen += dInclinedStepLen;
                    dTopPntLen ++;
                    dTopRealLen += dInclinedStepRealLen;
                    bTopLenCut = true;
                    dMidLen += dStraightStepLen;
                    dMidPntLen ++;
                    dMidRealLen += dInclinedStepRealLen;
                    bMidLenCut = true;
                    dBottomLen += dInclinedStepLen;
                    dBottomPntLen ++;
                    dBottomRealLen += dInclinedStepRealLen;
                    bBottomLenCut = true;
                } else if (idx > nLeft)  {
                    byte bLeft = barrayImg[idx - 1][idx1];
                    byte bTop = (idx1 > nTop)?barrayImg[idx][idx1 - 1]:0;
                    byte bBottom = (idx1 < nTop + nHeight - 1)?barrayImg[idx][idx1 + 1]:0;
                    if (bLeft == 1 && bTop == 1) {
                        dTopLen += dInclinedStepLen;
                        dTopPntLen ++;
                        dTopRealLen += dInclinedStepRealLen;
                        bTopLenCut = true;
                    }
                    if (bLeft == 1 && bBottom == 1)    {
                        dBottomLen += dInclinedStepLen;
                        dBottomPntLen ++;
                        dBottomRealLen += dInclinedStepRealLen;
                        bBottomLenCut = true;
                    }
                }
                // now use real line len to determin min cut path.
                if (dTopLen < dMidLen && dTopLen <= dBottomLen) {
                    // ok, route select top len
                    darrayMinRouteLens[idx1 - nTop] = dTopLen;
                    darrayMinRoutePntLens[idx1 - nTop] = dTopPntLen;
                    darrayMinRouteRealLens[idx1 - nTop] = dTopRealLen;                    
                    for (int idx2 = 0; idx2 < idx - nLeft; idx2 ++) {
                        narrayMinRoutes[idx2][idx1 - nTop] = narrayMinRoutesPrev[idx2][idx1 - nTop - 1];
                        narrayMinRoutesCuts[idx2][idx1 - nTop] = narrayMinRoutesCutsPrev[idx2][idx1 - nTop - 1];
                    }
                    narrayMinRoutes[idx - nLeft][idx1 - nTop] = idx1;
                    if (bTopLenCut) {
                        narrayMinRoutesCuts[idx - nLeft][idx1 - nTop] = 1;
                    }
                } else if (dBottomLen < dMidLen && dBottomLen <= dTopLen) {
                    // ok, route select bottom len
                    darrayMinRouteLens[idx1 - nTop] = dBottomLen;
                    darrayMinRoutePntLens[idx1 - nTop] = dBottomPntLen;
                    darrayMinRouteRealLens[idx1 - nTop] = dBottomRealLen;                    
                    for (int idx2 = 0; idx2 < idx - nLeft; idx2 ++) {
                        narrayMinRoutes[idx2][idx1 - nTop] = narrayMinRoutesPrev[idx2][idx1 - nTop + 1];
                        narrayMinRoutesCuts[idx2][idx1 - nTop] = narrayMinRoutesCutsPrev[idx2][idx1 - nTop + 1];
                    }
                    narrayMinRoutes[idx - nLeft][idx1 - nTop] = idx1;
                    if (bBottomLenCut) {
                        narrayMinRoutesCuts[idx - nLeft][idx1 - nTop] = 1;
                    }
                } else {
                    // ok, route select middle len
                    darrayMinRouteLens[idx1 - nTop] = dMidLen;
                    darrayMinRoutePntLens[idx1 - nTop] = dMidPntLen;
                    darrayMinRouteRealLens[idx1 - nTop] = dMidRealLen;                    
                    for (int idx2 = 0; idx2 < idx - nLeft; idx2 ++) {
                        narrayMinRoutes[idx2][idx1 - nTop] = narrayMinRoutesPrev[idx2][idx1 - nTop];
                        narrayMinRoutesCuts[idx2][idx1 - nTop] = narrayMinRoutesCutsPrev[idx2][idx1 - nTop];
                    }
                    narrayMinRoutes[idx - nLeft][idx1 - nTop] = idx1;
                    if (bMidLenCut) {
                        narrayMinRoutesCuts[idx - nLeft][idx1 - nTop] = 1;
                    }
                }
            }
        }
        
        int nIdxMin = 0;
        for (int idx = 1; idx < nHeight; idx ++) {
            if (darrayMinRouteLens[nIdxMin] >= darrayMinRouteLens[idx])  {  // use >= instead > to guaretee that we always select bottom most.
                nIdxMin = idx;
            }
        }
        
        CutPath cutPath = new CutPath();
        cutPath.mdLen = darrayMinRouteLens[nIdxMin];
        cutPath.mdLenExtraMeasure3 = darrayMinRoutePntLens[nIdxMin];
        cutPath.mdLenExtraMeasure4 = darrayMinRouteRealLens[nIdxMin];
        cutPath.mp3Path = new Position3D[nWidth];
        for (int idx = 0; idx < nWidth; idx ++)    {
            cutPath.mp3Path[idx] = new Position3D(idx + nLeft, narrayMinRoutes[idx][nIdxMin]);
        }
        
        // now calculate extra len metrics
        // 1. number of cut strokes, 2. max cut stroke width
        int nMaxCutStrokeWidth = 0;
        int nThisCutStrokeWidth = 0;
        for (int idx = 0; idx < nWidth; idx ++) {
            if ((idx == 0 && narrayMinRoutesCuts[idx][nIdxMin] == 1)
                    || (idx > 0 && narrayMinRoutesCuts[idx - 1][nIdxMin] == 0 && narrayMinRoutesCuts[idx][nIdxMin] == 1))   {
                cutPath.mdLenExtraMeasure1 ++;
                nThisCutStrokeWidth = 0;
            }
            if (narrayMinRoutesCuts[idx][nIdxMin] == 1)   {
                nThisCutStrokeWidth ++;
                if (nThisCutStrokeWidth > nMaxCutStrokeWidth)   {
                    nMaxCutStrokeWidth = nThisCutStrokeWidth;
                }
            }
        }
        
        cutPath.mdLenExtraMeasure2 = nMaxCutStrokeWidth;
        
        return cutPath;
    }
        
    // calculate vertical cut path in the band area defined nLeft, nTop, nWidth and nWeight
    public static CutPath calcVerticalCutPath(byte[][] barrayImg, int nLeft, int nTop, int nWidth, int nHeight, boolean bUseRealLen)    {
        // do not check parameter to save calculating time.
        int[][] narrayMinRoutes = new int[nWidth][nHeight];
        int[][] narrayMinRoutesPrev = new int[nWidth][nHeight];
        int[][] narrayMinRoutesCuts = new int[nWidth][nHeight];
        int[][] narrayMinRoutesCutsPrev = new int[nWidth][nHeight];
        double[] darrayMinRouteLens  = new double[nWidth];
        double[] darrayMinRouteLensPrev = new double[nWidth];
        double[] darrayMinRoutePntLens  = new double[nWidth];
        double[] darrayMinRoutePntLensPrev = new double[nWidth];
        double[] darrayMinRouteRealLens  = new double[nWidth];
        double[] darrayMinRouteRealLensPrev = new double[nWidth];
        double dInclinedStepRealLen = 1.4142135623731;
        double dInclinedStepLen = bUseRealLen?dInclinedStepRealLen:1;

        double dStraightStepLen = 1;
        for (int idx = nTop; idx < nTop + nHeight; idx ++)  {
            // swap prev and this
            double[] darrayLenSwapTmp = darrayMinRouteLensPrev;
            darrayMinRouteLensPrev = darrayMinRouteLens;
            darrayMinRouteLens = darrayLenSwapTmp;
            
            darrayLenSwapTmp = darrayMinRoutePntLensPrev;
            darrayMinRoutePntLensPrev = darrayMinRoutePntLens;
            darrayMinRoutePntLens = darrayLenSwapTmp;
            
            darrayLenSwapTmp = darrayMinRouteRealLensPrev;
            darrayMinRouteRealLensPrev = darrayMinRouteRealLens;
            darrayMinRouteRealLens = darrayLenSwapTmp;
            
            int[][] narraySwapTmp = narrayMinRoutesPrev;
            narrayMinRoutesPrev = narrayMinRoutes;
            narrayMinRoutes = narraySwapTmp;
            int[][] narraySwapCutsTmp = narrayMinRoutesCutsPrev;
            narrayMinRoutesCutsPrev = narrayMinRoutesCuts;
            narrayMinRoutesCuts = narraySwapCutsTmp;
            for (int idx1 = nLeft; idx1 < nLeft + nWidth; idx1 ++)  {
                // now assume we have found the minimum paths from nTop to idx
                // now we try to find the next step.
                double dLeftLen = Double.MAX_VALUE, dMidLen, dRightLen = Double.MAX_VALUE;
                double dLeftPntLen = Double.MAX_VALUE, dMidPntLen, dRightPntLen = Double.MAX_VALUE;
                double dLeftRealLen = Double.MAX_VALUE, dMidRealLen, dRightRealLen = Double.MAX_VALUE;
                if (idx1 > nLeft)   {
                    dLeftLen = darrayMinRouteLensPrev[idx1 - nLeft - 1];
                    dLeftPntLen = darrayMinRoutePntLensPrev[idx1 - nLeft - 1];
                    dLeftRealLen = darrayMinRouteRealLensPrev[idx1 - nLeft - 1];
                }
                dMidLen = darrayMinRouteLensPrev[idx1 - nLeft];
                dMidPntLen = darrayMinRoutePntLensPrev[idx1 - nLeft];
                dMidRealLen = darrayMinRouteRealLensPrev[idx1 - nLeft];
                if (idx1< nLeft + nWidth - 1)   {
                    dRightLen = darrayMinRouteLensPrev[idx1 - nLeft + 1];
                    dRightPntLen = darrayMinRoutePntLensPrev[idx1 - nLeft + 1];
                    dRightRealLen = darrayMinRouteRealLensPrev[idx1 - nLeft + 1];
                }
                boolean bLeftLenCut = false, bMidLenCut = false, bRightLenCut = false;
                if (barrayImg[idx1][idx] == 1)  {
                    dLeftLen += dInclinedStepLen;
                    dLeftPntLen ++;
                    dLeftRealLen += dInclinedStepRealLen;
                    bLeftLenCut = true;
                    dMidLen += dStraightStepLen;
                    dMidPntLen ++;
                    dMidRealLen += dInclinedStepRealLen;
                    bMidLenCut = true;
                    dRightLen += dInclinedStepLen;
                    dRightPntLen ++;
                    dRightRealLen += dInclinedStepRealLen;
                    bRightLenCut = true;
                } else if (idx > nTop)  {
                    byte bUp = barrayImg[idx1][idx - 1];
                    byte bLeft = (idx1 > nLeft)?barrayImg[idx1 - 1][idx]:0;
                    byte bRight = (idx1 < nLeft + nWidth - 1)?barrayImg[idx1 + 1][idx]:0;
                    if (bUp == 1 && bLeft == 1) {
                        dLeftLen += dInclinedStepLen;
                        dLeftPntLen ++;
                        dLeftRealLen += dInclinedStepRealLen;
                        bLeftLenCut = true;
                    }
                    if (bUp == 1 && bRight == 1)    {
                        dRightLen += dInclinedStepLen;
                        dRightPntLen ++;
                        dRightRealLen += dInclinedStepRealLen;
                        bRightLenCut = true;
                    }
                }
                if (dLeftLen < dMidLen && dLeftLen <= dRightLen) {
                    // ok, route select left len
                    darrayMinRouteLens[idx1 - nLeft] = dLeftLen;
                    darrayMinRoutePntLens[idx1 - nLeft] = dLeftPntLen;
                    darrayMinRouteRealLens[idx1 - nLeft] = dLeftRealLen;
                    System.arraycopy(narrayMinRoutesPrev[idx1 - nLeft - 1], 0, narrayMinRoutes[idx1 - nLeft], 0, idx - nTop);
                    System.arraycopy(narrayMinRoutesCutsPrev[idx1 - nLeft - 1], 0, narrayMinRoutesCuts[idx1 - nLeft], 0, idx - nTop);
                    narrayMinRoutes[idx1 - nLeft][idx - nTop] = idx1;
                    if (bLeftLenCut) {
                        narrayMinRoutesCuts[idx1 - nLeft][idx - nTop] = 1;
                    }
                } else if (dRightLen < dMidLen && dRightLen <= dLeftLen) {
                    // ok, route select right len
                    darrayMinRouteLens[idx1 - nLeft] = dRightLen;
                    darrayMinRoutePntLens[idx1 - nLeft] = dRightPntLen;
                    darrayMinRouteRealLens[idx1 - nLeft] = dRightRealLen;
                    System.arraycopy(narrayMinRoutesPrev[idx1 - nLeft + 1], 0, narrayMinRoutes[idx1 - nLeft], 0, idx - nTop);
                    System.arraycopy(narrayMinRoutesCutsPrev[idx1 - nLeft + 1], 0, narrayMinRoutesCuts[idx1 - nLeft], 0, idx - nTop);
                    narrayMinRoutes[idx1 - nLeft][idx - nTop] = idx1;
                    if (bRightLenCut) {
                        narrayMinRoutesCuts[idx1 - nLeft][idx - nTop] = 1;
                    }
                } else {
                    // ok, route select middle len
                    darrayMinRouteLens[idx1 - nLeft] = dMidLen;
                    darrayMinRoutePntLens[idx1 - nLeft] = dMidPntLen;
                    darrayMinRouteRealLens[idx1 - nLeft] = dMidRealLen;
                    System.arraycopy(narrayMinRoutesPrev[idx1 - nLeft], 0, narrayMinRoutes[idx1 - nLeft], 0, idx - nTop);
                    System.arraycopy(narrayMinRoutesCutsPrev[idx1 - nLeft], 0, narrayMinRoutesCuts[idx1 - nLeft], 0, idx - nTop);
                    narrayMinRoutes[idx1 - nLeft][idx - nTop] = idx1;
                    if (bMidLenCut) {
                        narrayMinRoutesCuts[idx1 - nLeft][idx - nTop] = 1;
                    }
                }
            }
        }
        
        int nIdxMin = 0;
        for (int idx = 1; idx < nWidth; idx ++) {
            if (darrayMinRouteLens[nIdxMin] >= darrayMinRouteLens[idx])  {  // use >= instead > to guaretee that we always select right most.
                nIdxMin = idx;
            }
        }
        
        CutPath cutPath = new CutPath();
        cutPath.mdLen = darrayMinRouteLens[nIdxMin];
        cutPath.mdLenExtraMeasure3 = darrayMinRoutePntLens[nIdxMin];
        cutPath.mdLenExtraMeasure4 = darrayMinRouteRealLens[nIdxMin];
        cutPath.mp3Path = new Position3D[narrayMinRoutes[nIdxMin].length];
        for (int idx = 0; idx < narrayMinRoutes[nIdxMin].length; idx ++)    {
            cutPath.mp3Path[idx] = new Position3D(narrayMinRoutes[nIdxMin][idx], idx + nTop);
        }
        
        // now calculate extra len metrics
        // 1. number of cut strokes, 2. max cut stroke width
        int nMaxCutStrokeWidth = 0;
        int nThisCutStrokeWidth = 0;
        for (int idx = 0; idx < nHeight; idx ++) {
            if ((idx == 0 && narrayMinRoutesCuts[nIdxMin][idx] == 1)
                    || (idx > 0 && narrayMinRoutesCuts[nIdxMin][idx - 1] == 0 && narrayMinRoutesCuts[nIdxMin][idx] == 1))   {
                cutPath.mdLenExtraMeasure1 ++;
                nThisCutStrokeWidth = 0;
            }
            if (narrayMinRoutesCuts[nIdxMin][idx] == 1)   {
                nThisCutStrokeWidth ++;
                if (nThisCutStrokeWidth > nMaxCutStrokeWidth)   {
                    nMaxCutStrokeWidth = nThisCutStrokeWidth;
                }
            }
        }
        
        cutPath.mdLenExtraMeasure2 = nMaxCutStrokeWidth;

        return cutPath;
    }


    //========================================================================================================
    // assume barrayImg is not null and size is not 0, nleft, nTop, nWidth, nHeight are positive. not do any
    // validation here to save computing time. nMode is valuation mode, 0 means if there is a 0 in the area,
    // the value is 0, otherwise is 1; 1 means if there is a 1 in the area, the value is 1, otherwise is 0,
    // other values means average.
    public static double getImgElemValue(byte[][] barrayImg, int nLeft, int nTop, int nWidth, int nHeight, int nMode)    {
        int nWholeImgWidth = barrayImg.length, nWholeImgHeight = barrayImg[0].length;
        double dReturn = 0;
        int nPixelCount = 0;;
        for (int idx = nLeft; idx < Math.min(nLeft + nWidth, nWholeImgWidth); idx ++)    {
            for (int idx1 = nTop; idx1 < Math.min(nTop + nHeight, nWholeImgHeight); idx1 ++)    {
                if (nMode == 0)    {
                    if (barrayImg[idx][idx1] == 0)    {
                        return 0.0;
                    }
                } else if (nMode == 1)    {
                    if (barrayImg[idx][idx1] == 1)    {
                        return 1.0;
                    }
                } else    {
                    dReturn += barrayImg[idx][idx1];
                    nPixelCount ++;
                }
            }
        }
        if (nMode == 0)    {
            // cannot find 0 value.
            return 1.0;
        } else if (nMode == 1)    {
            // cannot find 1 value.
            return 0.0;
        } else if (nPixelCount == 0)    {
            return 0.0;
        } else    {
            return dReturn/nPixelCount;
        }
    }
    
    // identify if a row (idx in original img) could be the top of a line cut. However, to identify a line cut
    // we need some further analysis.
    public static boolean isPotentialLnCutTop(ImageChop imgChop2Cut, int nRow, double dAvgStrokeWidth, int nMaxLnCutHeight,
            int nWorstCaseLineDivOrOnLen, int nWorstCaseNumOfDisconnects, int nWorstCaseDisconnectLen)   {  
        int nTopInOriginalImg = nRow, nBottomInOriginalImg = nRow + nMaxLnCutHeight;
        if (nBottomInOriginalImg >= imgChop2Cut.getBottomP1InOriginalImg()) {
            nBottomInOriginalImg = imgChop2Cut.getBottomInOriginalImg();
        }
        int nTopInThisImg = imgChop2Cut.mapOriginalYIdx2This(nTopInOriginalImg);
        int nBottomInThisImg = imgChop2Cut.mapOriginalYIdx2This(nBottomInOriginalImg);
        int nHeight = nBottomInThisImg + 1 - nTopInThisImg;
        
        int nTotalRoughlyOrOnCnt = 0;    /* if nThisDisconnectLen < a threshhold, it is still roughly connected */
        int nNumOfDisconnects = 0;
        int nMaxDisconnectLen = 0;
        int nThisDisconnectLen = 0;
        int nLastOrOnIdx = imgChop2Cut.mnLeft - 1;
        byte[] barrayAllZeros = new byte[nHeight];
        byte[] barrayThisColumn = new byte[nHeight];
        int nMaxDisconnectLenTolerance = (int)Math.min(dAvgStrokeWidth, ConstantsMgr.msdDisconnectPoss2Width * imgChop2Cut.mnWidth);
        for (int idx = imgChop2Cut.mnLeft; idx < imgChop2Cut.getRightPlus1(); idx ++)   {
            System.arraycopy(imgChop2Cut.mbarrayImg[idx], nTopInThisImg, barrayThisColumn, 0, nHeight);
            int nThisColOrOn = Arrays.equals(barrayAllZeros, barrayThisColumn)?0:1;
            if (nThisColOrOn == 1) {
                nThisDisconnectLen = idx - nLastOrOnIdx - 1;
                if (nThisDisconnectLen > nMaxDisconnectLen) {
                    nMaxDisconnectLen = nThisDisconnectLen;
                }
                if (nThisDisconnectLen > 0) {
                    nNumOfDisconnects ++;
                }
                if (nLastOrOnIdx >= imgChop2Cut.mnLeft && nThisDisconnectLen <= nMaxDisconnectLenTolerance) {   // first disconnect should not be ignored.
                    nTotalRoughlyOrOnCnt += nThisDisconnectLen; // we may ignore very small disconnects.
                }
                nLastOrOnIdx = idx;
            }
            
            nTotalRoughlyOrOnCnt += nThisColOrOn;
        }
        // for the last disconnect.
        nThisDisconnectLen = imgChop2Cut.getRightPlus1() - nLastOrOnIdx - 1;
        if (nThisDisconnectLen > nMaxDisconnectLen) {
            nMaxDisconnectLen = nThisDisconnectLen;
        }
        if (nThisDisconnectLen > 0) {
            nNumOfDisconnects ++;
        }
        // last disconnect should not be ignored.
        
        return (nTotalRoughlyOrOnCnt >= nWorstCaseLineDivOrOnLen)
                && (nNumOfDisconnects <= nWorstCaseNumOfDisconnects)
                && (nMaxDisconnectLen <= nWorstCaseDisconnectLen);
    }

    // extract a line div chop from imchop2cut, the searching range is from nStartFindingRow to nEndFindingRow.
    // note that nStartFindingRow and nEndFindingRow are idices in original img
    public static ImageChop extractLnDivChop(ImageChop imgChop2Cut, int nStartFindingRow, int nEndFindingRow,
            double dAvgStrokeWidth, int nMaxLnCutHeight, int nMinLnCutHeight, int nWorstCaseLineDivOrOnLen,
            int nWorstCaseLineDivOnLen, int nWorstCaseNumOfDisconnects, int nWorstCaseDisconnectLen,
            int nWorstCaseNumOfCutStrokes, int nWorstCaseCutStrokeWidth) {
        int nSearchingHeight = nEndFindingRow + 1 - nStartFindingRow;
        int[] narrayNumOfOns = new int[nSearchingHeight];  // number of ons in each row.
        int nStartFRowInThis = imgChop2Cut.mapOriginalYIdx2This(nStartFindingRow);
        int nEndFRowInThis = imgChop2Cut.mapOriginalYIdx2This(nEndFindingRow);
        int nMaxOnRowIdx = nStartFRowInThis;
        int nMaxOnRowOnCnt = 0;
        for (int idx = nStartFRowInThis; idx <= nEndFRowInThis; idx ++) {
            for (int idx1 = imgChop2Cut.mnLeft; idx1 < imgChop2Cut.getRightPlus1(); idx1 ++)    {
                narrayNumOfOns[idx - nStartFRowInThis] += imgChop2Cut.mbarrayImg[idx1][idx];
            }
            if (narrayNumOfOns[idx - nStartFRowInThis] > nMaxOnRowOnCnt)    {
                nMaxOnRowIdx = idx;
                nMaxOnRowOnCnt = narrayNumOfOns[idx - nStartFRowInThis];
            }
        }
        if (nMaxOnRowOnCnt < nWorstCaseLineDivOnLen) {
            // the line which includes most ons still has less than nWorstCaseLineDivOnLen, this means it's not a line cut
            return null;
        }

        int nUpperMinCutStrokeTotalWidth = Integer.MAX_VALUE;
        int nUpperMinCSTWIdx = nMaxOnRowIdx;
        int nUpperMinCSTWMaxWidth = 0;
        int nUpperMinCSTWCutCnts = 0;
        // now calculate upper edge cut stroke cnt, cut stroke total width and cut stroke max width.
        for (int idx = nMaxOnRowIdx; idx >= nStartFRowInThis; idx --)  {
            int nThisRowCutStrokeCnt = 0;
            int nThisRowCutStrokeTotalWidth = 0;
            int nThisRowCutStrokeMaxWidth = 0;
            if (idx > imgChop2Cut.mnTop)   {
                // not top line
                int nThisCutWidth = 0;
                for (int idx1 = imgChop2Cut.mnLeft; idx1 < imgChop2Cut.getRightPlus1(); idx1 ++)    {
                    if ((imgChop2Cut.mbarrayImg[idx1][idx - 1] == 1)
                            && ((idx1 > imgChop2Cut.mnLeft && imgChop2Cut.mbarrayImg[idx1 - 1][idx] == 1)
                                || imgChop2Cut.mbarrayImg[idx1][idx] == 1
                                || (idx1 < imgChop2Cut.getRight() && imgChop2Cut.mbarrayImg[idx1 + 1][idx] == 1)))   {
                        // ok, we cut the stroke.
                        if (nThisCutWidth == 0)   {
                            // beginning of a new stroke cut
                            nThisRowCutStrokeCnt ++;
                        }
                        nThisCutWidth ++;
                        nThisRowCutStrokeTotalWidth ++;
                        if (nThisCutWidth > nThisRowCutStrokeMaxWidth)  {
                            nThisRowCutStrokeMaxWidth = nThisCutWidth;
                        }
                    } else  {
                        // we are not cutting the stroke
                        nThisCutWidth = 0;
                    }
                }
            }
            if (nUpperMinCutStrokeTotalWidth > nThisRowCutStrokeTotalWidth) {
                nUpperMinCutStrokeTotalWidth = nThisRowCutStrokeTotalWidth;
                nUpperMinCSTWIdx = idx;
                nUpperMinCSTWMaxWidth = nThisRowCutStrokeMaxWidth;
                nUpperMinCSTWCutCnts = nThisRowCutStrokeCnt;
            }
        }
        
        int nLowerMinCutStrokeTotalWidth = Integer.MAX_VALUE;
        int nLowerMinCSTWIdx = nMaxOnRowIdx;
        int nLowerMinCSTWMaxWidth = 0;
        int nLowerMinCSTWCutCnts = 0;
        // now calculate upper edge cut stroke cnt, cut stroke total width and cut stroke max width.
        for (int idx = nMaxOnRowIdx; idx <= nEndFRowInThis; idx ++)  {
            int nThisRowCutStrokeCnt = 0;
            int nThisRowCutStrokeTotalWidth = 0;
            int nThisRowCutStrokeMaxWidth = 0;
            if (idx < imgChop2Cut.getBottom())   {
                // not bottom line
                int nThisCutWidth = 0;
                for (int idx1 = imgChop2Cut.mnLeft; idx1 < imgChop2Cut.getRightPlus1(); idx1 ++)    {
                    if ((imgChop2Cut.mbarrayImg[idx1][idx + 1] == 1)
                            && ((idx1 > imgChop2Cut.mnLeft && imgChop2Cut.mbarrayImg[idx1 - 1][idx] == 1)
                                || imgChop2Cut.mbarrayImg[idx1][idx] == 1
                                || (idx1 < imgChop2Cut.getRight() && imgChop2Cut.mbarrayImg[idx1 + 1][idx] == 1)))   {
                        // ok, we cut the stroke.
                        if (nThisCutWidth == 0)   {
                            // beginning of a new stroke cut
                            nThisRowCutStrokeCnt ++;
                        }
                        nThisCutWidth ++;
                        nThisRowCutStrokeTotalWidth ++;
                        if (nThisCutWidth > nThisRowCutStrokeMaxWidth)  {
                            nThisRowCutStrokeMaxWidth = nThisCutWidth;
                        }
                    } else  {
                        // we are not cutting the stroke
                        nThisCutWidth = 0;
                    }
                }
            }
            if (nLowerMinCutStrokeTotalWidth > nThisRowCutStrokeTotalWidth) {
                nLowerMinCutStrokeTotalWidth = nThisRowCutStrokeTotalWidth;
                nLowerMinCSTWIdx = idx;
                nLowerMinCSTWMaxWidth = nThisRowCutStrokeMaxWidth;
                nLowerMinCSTWCutCnts = nThisRowCutStrokeCnt;
            }
        }
        
        // now upper edge and lower edge have been found, we need to verify that the number of cut strokes
        // maximum cut stroke width and total cut stroke width satisfy the condition.
        if (nUpperMinCSTWCutCnts > nWorstCaseNumOfCutStrokes
                || nUpperMinCSTWMaxWidth > nWorstCaseCutStrokeWidth
                || nLowerMinCSTWCutCnts > nWorstCaseNumOfCutStrokes
                || nLowerMinCSTWMaxWidth > nWorstCaseCutStrokeWidth)    {
            // number of cut strokes or maximum cut stroke width is beyond the limit, this is not a line div.
            return null;
        }
        int nChopThickness = nLowerMinCSTWIdx + 1 - nUpperMinCSTWIdx;
        if (nChopThickness > nMaxLnCutHeight || nChopThickness < nMinLnCutHeight)  {
            // this chop is toooo thick or toooo thin, it should not be a line div.
            return null;
        }

        if (!isPotentialLnCutTop(imgChop2Cut, imgChop2Cut.mapThisYIdx2Original(nUpperMinCSTWIdx),
                                dAvgStrokeWidth, nChopThickness, nWorstCaseLineDivOrOnLen, 
                                nWorstCaseNumOfDisconnects, nWorstCaseDisconnectLen)) {
            // too many disconnections or the total or on length is too short.
            return null;
        }
        
        ImageChop chopDiv = new ImageChop();
        if (dAvgStrokeWidth < 3)    {   // line is too thin.
            //#set LINE_DIV1
            chopDiv.setImageChop(imgChop2Cut.mbarrayImg, imgChop2Cut.mnLeft, nUpperMinCSTWIdx,
                                imgChop2Cut.mnWidth, nChopThickness, imgChop2Cut.mbarrayOriginalImg,
                                imgChop2Cut.mnX0InOriginalImg, imgChop2Cut.mnY0InOriginalImg,
                                ImageChop.TYPE_LINE_DIV);
        } else {
            int nTop = nUpperMinCSTWIdx;
            int nBottom = nTop + nChopThickness - 1;
            int nPathRange = (int)(dAvgStrokeWidth / 2.0);
            // the cut path should not include upper and lower edges of the chop div.
            int nTopPathUpperEdge = Math.max(imgChop2Cut.mnTop, nTop - nPathRange);
            int nTopPathLowerEdgeP1 = Math.min(imgChop2Cut.getBottomPlus1(), nTop + 1);
            int nBottomPathUpperEdge = Math.max(imgChop2Cut.mnTop, nBottom);
            int nBottomPathLowerEdgeP1 = Math.min(imgChop2Cut.getBottomPlus1(), nBottom + nPathRange + 1);
            CutPath cutPathTop = calcHorizontalCutPath(imgChop2Cut.mbarrayImg, imgChop2Cut.mnLeft, nTopPathUpperEdge,
                    imgChop2Cut.mnWidth, nTopPathLowerEdgeP1 - nTopPathUpperEdge, true);
            int nNewTop = imgChop2Cut.mbarrayImg[0].length;
            for (int idx = 0; idx < cutPathTop.mp3Path.length; idx ++)  {
                if (nNewTop > cutPathTop.mp3Path[idx].getY())   {
                    nNewTop = (int)cutPathTop.mp3Path[idx].getY();
                }
            }
            CutPath cutPathBottom = calcHorizontalCutPath(imgChop2Cut.mbarrayImg, imgChop2Cut.mnLeft, nBottomPathUpperEdge,
                    imgChop2Cut.mnWidth, nBottomPathLowerEdgeP1 - nBottomPathUpperEdge, true);
            int nNewBottom = -1;
            for (int idx = 0; idx < cutPathBottom.mp3Path.length; idx ++)  {
                if (nNewBottom < cutPathBottom.mp3Path[idx].getY())   {
                    nNewBottom = (int)cutPathBottom.mp3Path[idx].getY();
                }
            }
            //#set LINE_DIV 2
            chopDiv.setImageChop(imgChop2Cut.mbarrayImg, imgChop2Cut.mnLeft, nNewTop,
                                imgChop2Cut.mnWidth, nNewBottom - nNewTop + 1, imgChop2Cut.mbarrayOriginalImg,
                                imgChop2Cut.mnX0InOriginalImg, imgChop2Cut.mnY0InOriginalImg,
                                ImageChop.TYPE_LINE_DIV);
            //chopDiv = extractHChopFromCutPaths(imgChop2Cut, cutPathTop, cutPathBottom);
            //chopDiv.mnChopType = ImageChop.TYPE_LINE_DIV;
        }
                        
        chopDiv = chopDiv.convert2MinContainer();
        return chopDiv;
    }
    
    public static ImageChop extractHChopFromCutPaths(ImageChop chop2Extract, CutPath cutPathTop, CutPath cutPathBottom)   {
        byte[][] barrayPiece = new byte[chop2Extract.mnWidth][chop2Extract.mnHeight];
        for (int idx = 0; idx < chop2Extract.mnWidth; idx ++)   {
            int nFrom = chop2Extract.mnTop;
            if (cutPathTop != null) {
                nFrom = (int)cutPathTop.mp3Path[idx].getY() + 1;
            }
            int nToP1 = chop2Extract.getBottomPlus1();
            if (cutPathBottom != null)  {
                nToP1 = (int)cutPathBottom.mp3Path[idx].getY();
            }
            int nLength = nToP1 - nFrom;
            int nFromInThis = chop2Extract.mapConfinedYIdx2This(nFrom);
            int nIdxInThis = chop2Extract.mapConfinedXIdx2This(idx);
            if (nLength > 0)    {
                System.arraycopy(chop2Extract.mbarrayImg[nIdxInThis], nFromInThis, barrayPiece[idx], nFrom, nLength);
            }
        }
        ImageChop imgChop = new ImageChop();
        imgChop.setImageChop(barrayPiece, 0, 0, chop2Extract.mnWidth, chop2Extract.mnHeight,
                chop2Extract.mbarrayOriginalImg, 
                chop2Extract.mnX0InOriginalImg + chop2Extract.mnLeft, 
                chop2Extract.mnY0InOriginalImg + chop2Extract.mnTop, ImageChop.TYPE_UNKNOWN);
        return imgChop;
    }

    //计算最坏情况下的分数线长度
    public static int calcWorstCaseLnDivOrOnLen(int nChopWidth, int nChopHeight) {
        //return (int)(nChopWidth*ConstantsMgr.msdWorstCaseLineDivOnLenRatio);
        return (int)Math.max(nChopWidth * ConstantsMgr.msdWorstCaseLineDivOnLenRatio,
                                (nChopWidth - Math.max(2.0 * ConstantsMgr.msnMinNormalCharWidthInUnit,
                                                        nChopHeight * ConstantsMgr.msdHeightSkewRatio)));  //  Line div width should be long enough
    }

    public static int calcWorstCaseLnDiv1RowOnLen(double dAvgStrokeWidth, int nWorstCaseLineDivOrOnLen) {
        return (int)Math.min((int)(Math.max(dAvgStrokeWidth - 1, 1) / ConstantsMgr.msdMaxHorizontalSlope), // using avgstrokewidth - 1 for relax
                                                        nWorstCaseLineDivOrOnLen);
    }    

    //水平切
    //========================================================================================================
    // here assume the input is minimum container and barrayImg has been horizontally adjusted.
    // the output imgchops are also minimum container adjusted (except the blank div).
    // if cannot be horizontally cut, return imgchops which includes only one imagechop, i.e. the whole image.
    public static ImageChops cutHorizontallyProj(ImageChop imgChop2Cut, double dAvgStrokeWidth, double dMaxEstCharWidth, double dMaxEstCharHeight)    {
        ImageChops imgChops = new ImageChops();
        //int nBlankDivNum = 0, nLnDivNum = 0;
        int[] narrayRowOnCount = new int[imgChop2Cut.mnHeight];
        int nThinnestChopHeight = (int)Math.ceil(dAvgStrokeWidth/2.0);
        int nMaxLnCutHeight = (int)Math.min(
                Math.max(2 * dAvgStrokeWidth - 1, Math.ceil(imgChop2Cut.mnWidth * ConstantsMgr.msdMaxHorizontalSlope) + Math.ceil(dAvgStrokeWidth)),
                imgChop2Cut.mnWidth * ConstantsMgr.msdCharWOverHMaxSkewRatio / ConstantsMgr.msdExtendableCharWOverHThresh); // should be like a line, not a point

        int nWorstCaseLineDivOrOnLen = calcWorstCaseLnDivOrOnLen(imgChop2Cut.mnWidth, imgChop2Cut.mnHeight);  // Line div width should be long enough
        // allow at most 3 disconnects in one line div (including front and end disconnects)
        // longer line means more possible disconnects, thicker line means less possible disconnects.
        int nWorstCaseNumOfDisconnects = Math.max(2, (int)(imgChop2Cut.mnWidth / dAvgStrokeWidth / ConstantsMgr.msnDisconnectLnCutPerStrokeWidth));   // leftmost and right most are also considered as disconnects.
        /*if (nWorstCaseNumOfDisconnects > 5) {
            nWorstCaseNumOfDisconnects = 5;
        }*/
        int nWorstCaseLineDiv1RowOnLen = calcWorstCaseLnDiv1RowOnLen(dAvgStrokeWidth, nWorstCaseLineDivOrOnLen);
        // Cut through at most 4 connected strokes for line div. Moreover, for single char like 7 or
        // +, cut throught at most 1 connected stroke. Since dMaxEstCharWidth always overesimate
        // single char height, so we assume cut through thresh is floor(totalwidth/dMaxestcharWdith)
        int nWorstCaseNumOfCutStrokes = (int)Math.ceil(Math.min(4,   // at most 4 connected strokes
                                            imgChop2Cut.mnWidth/dMaxEstCharWidth));
        int nWorstCaseCutStrokeWidth = (int)Math.min(imgChop2Cut.mnWidth * 0.05, 1.5 * dAvgStrokeWidth);
        
        int nScanningMode = 0;  // 1 means in line div scanning mode, 2 means in blank div scanning mode.
        int nPrevScanningMode = 0;
        int nNewChopStartRow = imgChop2Cut.getTopInOriginalImg();   // the bottom row of last div.
        int nBlankDivStartRow = imgChop2Cut.getTopInOriginalImg();
        int nImg2CutBottomP1InOriginal = imgChop2Cut.getBottomP1InOriginalImg();    // it is faster to store the value.
        int nImg2CutRightP1 = imgChop2Cut.getRightPlus1();

        for (int idx1 = imgChop2Cut.getTopInOriginalImg(); idx1 < nImg2CutBottomP1InOriginal; idx1 ++)    {
            int nIdx1InThis = imgChop2Cut.mapOriginalYIdx2This(idx1);
            int nIdx1Confined = imgChop2Cut.mapOriginalYIdx2Confined(idx1);
            for (int idx = imgChop2Cut.mnLeft; idx < nImg2CutRightP1; idx ++)    {
                narrayRowOnCount[nIdx1Confined] += imgChop2Cut.mbarrayImg[idx][nIdx1InThis];
            }
            
            if (narrayRowOnCount[imgChop2Cut.mapOriginalYIdx2Confined(idx1)] == 0)   {
                // could be a blank div. need to check blank line first.
                // assume that for a blank div, there must be a line which has no On point. We should not
                // allow any on point exist, otherwise, number 7 may be cutted into two  parts by "blank"
                // div.
                nScanningMode = 2;
                if (nPrevScanningMode != nScanningMode) {
                    // this could be the start of a new blank div.
                    nBlankDivStartRow = idx1;
                }
            } else if (isPotentialLnCutTop(imgChop2Cut, idx1, dAvgStrokeWidth, nMaxLnCutHeight,
                                nWorstCaseLineDivOrOnLen, nWorstCaseNumOfDisconnects,
                                imgChop2Cut.mnWidth - nWorstCaseLineDivOrOnLen))   {   // could be a line div.
                nScanningMode = 1;
            } else  {
                nScanningMode = 0;
            }
            boolean bFindLnDiv = false;
            if (nScanningMode == 1) {
                // try to get line div. search row idx1 to row (int)(idx1 + nMaxLnCutHeight + dAvgStrokeWidth) (wider area than
                // nMaxLnCutHeight to ensure no line cut is omitted).
                int nBottomRowInOriginalImg = (int)Math.min(imgChop2Cut.getBottomInOriginalImg(), Math.ceil(idx1 + nMaxLnCutHeight + dAvgStrokeWidth));
                ImageChop chopDiv = extractLnDivChop(imgChop2Cut, idx1, nBottomRowInOriginalImg,
                                                    dAvgStrokeWidth, nMaxLnCutHeight, 1, nWorstCaseLineDivOrOnLen,
                                                    nWorstCaseLineDiv1RowOnLen, nWorstCaseNumOfDisconnects,
                                                    imgChop2Cut.mnWidth - nWorstCaseLineDivOrOnLen,
                                                    nWorstCaseNumOfCutStrokes, nWorstCaseCutStrokeWidth);
                if (chopDiv != null)   {
                    // this is a line div chop
                    if (chopDiv.getTopInOriginalImg() - nNewChopStartRow >= nThinnestChopHeight) {  // above chop is thicker than thinnest chop height
                        ImageChop chopAbove = new ImageChop();
                        chopAbove.setImageChop(imgChop2Cut.mbarrayImg,
                                imgChop2Cut.mnLeft, imgChop2Cut.mapOriginalYIdx2This(nNewChopStartRow), imgChop2Cut.mnWidth, chopDiv.getTopInOriginalImg() - nNewChopStartRow,
                                imgChop2Cut.mbarrayOriginalImg, imgChop2Cut.mnX0InOriginalImg, imgChop2Cut.mnY0InOriginalImg,
                                ImageChop.TYPE_UNKNOWN);
                        chopAbove = chopAbove.convert2MinContainer();
                        imgChops.mlistChops.add(chopAbove);
                    }
                    imgChops.mlistChops.add(chopDiv);
                    //nLnDivNum ++;
                    nNewChopStartRow = chopDiv.getBottomP1InOriginalImg();
                    idx1 = nNewChopStartRow - 1;
                    bFindLnDiv = true;
                } else  {
                    nScanningMode = 0;  // it is not a line div, so it is a normal div.
                    bFindLnDiv = false;
                }
            } 
            if (!bFindLnDiv && nScanningMode != nPrevScanningMode && nPrevScanningMode == 2)  {
                // get blank div.
                // Div End Row is idx1 - 1 for blank div.
                if (nBlankDivStartRow - nNewChopStartRow >= nThinnestChopHeight) {
                    ImageChop chopAbove = new ImageChop();
                    chopAbove.setImageChop(imgChop2Cut.mbarrayImg,
                            imgChop2Cut.mnLeft, imgChop2Cut.mapOriginalYIdx2This(nNewChopStartRow), imgChop2Cut.mnWidth, nBlankDivStartRow - nNewChopStartRow,
                            imgChop2Cut.mbarrayOriginalImg, imgChop2Cut.mnX0InOriginalImg, imgChop2Cut.mnY0InOriginalImg,
                            ImageChop.TYPE_UNKNOWN);
                    chopAbove = chopAbove.convert2MinContainer();
                    imgChops.mlistChops.add(chopAbove);
                }
                nNewChopStartRow = idx1;
            }
            
            nPrevScanningMode = nScanningMode;
        }
        
        if (imgChop2Cut.getBottomP1InOriginalImg() - nNewChopStartRow >= nThinnestChopHeight || nNewChopStartRow == imgChop2Cut.getTopInOriginalImg()) {
            int nAddChopType = 0;
            //need not to worry about line div now because if it is a line div, it has been recognized.
            if (nPrevScanningMode == 2)  {
                // get blank div.
                // Div End Row is the last row for blank div.
                if (nBlankDivStartRow - nNewChopStartRow >= nThinnestChopHeight) {
                    ImageChop chopAbove = new ImageChop();
                    chopAbove.setImageChop(imgChop2Cut.mbarrayImg,
                            imgChop2Cut.mnLeft, imgChop2Cut.mapOriginalYIdx2This(nNewChopStartRow), imgChop2Cut.mnWidth, nBlankDivStartRow - nNewChopStartRow,
                            imgChop2Cut.mbarrayOriginalImg, imgChop2Cut.mnX0InOriginalImg, imgChop2Cut.mnY0InOriginalImg,
                            ImageChop.TYPE_UNKNOWN);
                    chopAbove = chopAbove.convert2MinContainer();
                    imgChops.mlistChops.add(chopAbove);
                }
                // need not to add a blank div at the bottom. but still need to le
                nAddChopType = 2;
                /*if (imgChops.mlistChops.getLast().mnChopType != ImageChop.TYPE_LINE_DIV) {
                    imgChops.mlistChops.add(chopDiv);
                }*/
                //nNewChopStartRow = idx1;
            }
            if (nAddChopType == 0) {
                // if height of this chop is larger than average stroke width or this chop is the only chop (new chop starts from top)
                ImageChop chopBottom = new ImageChop();
                chopBottom.setImageChop(imgChop2Cut.mbarrayImg,
                        imgChop2Cut.mnLeft, imgChop2Cut.mapOriginalYIdx2This(nNewChopStartRow), imgChop2Cut.mnWidth, imgChop2Cut.getBottomP1InOriginalImg() - nNewChopStartRow,
                        imgChop2Cut.mbarrayOriginalImg, imgChop2Cut.mnX0InOriginalImg, imgChop2Cut.mnY0InOriginalImg,
                        ImageChop.TYPE_UNKNOWN);
                chopBottom = chopBottom.convert2MinContainer();
                imgChops.mlistChops.add(chopBottom);
            }
        }
        
        // remove empty imgchops
        for (int idx = imgChops.mlistChops.size() - 1; idx >= 0; idx --) {
            if (imgChops.mlistChops.get(idx).mnChopType == ImageChop.TYPE_UNKNOWN && imgChops.mlistChops.get(idx).isEmptyImage())   {
                imgChops.mlistChops.remove(idx);
            }
        }
                
        // identifiy cap or undercore or expression gap and merge some chops
        ImageChops imgChopsProc = new ImageChops();
        for (int idx = 0; idx < imgChops.mlistChops.size(); idx ++) {
            if (idx == 0)   {
                imgChopsProc.mlistChops.add(imgChops.mlistChops.getFirst());
                continue;
            }
            ImageChop chopLastNBD = imgChopsProc.mlistChops.getLast();  //imgChops.mlistChops.get(idx - 1) could have been merged, so do not use it.
            ImageChop chopThisNBD = imgChops.mlistChops.get(idx);
            int nGap = chopThisNBD.getTopInOriginalImg() - chopLastNBD.getBottomP1InOriginalImg();

            int nHCutsStyle = identHCutsStyle(imgChops.mlistChops, idx, chopLastNBD, nWorstCaseLineDivOrOnLen, dAvgStrokeWidth, dMaxEstCharWidth, dMaxEstCharHeight);
            if (nHCutsStyle == MERGE_H_DIV_STYLE)   {
                // merge
                int nNewLeft = Math.min(chopLastNBD.getLeftInOriginalImg(), chopThisNBD.getLeftInOriginalImg());
                int nNewRightPlus1 = Math.max(chopLastNBD.getRightP1InOriginalImg(), chopThisNBD.getRightP1InOriginalImg());
                int nNewTop = chopLastNBD.getTopInOriginalImg();//Math.min(chopLastNBD.getTopInOriginalImg(), chopThisNBD.getTopInOriginalImg());
                int nNewBottomPlus1 = chopThisNBD.getBottomP1InOriginalImg();//Math.max(chopLastNBD.getBottomP1InOriginalImg(), chopThisNBD.getBottomP1InOriginalImg());
                ImageChop chopMerged = new ImageChop();
                chopMerged.setImageChop(imgChop2Cut.mbarrayImg,
                        imgChop2Cut.mapOriginalXIdx2This(nNewLeft), imgChop2Cut.mapOriginalYIdx2This(nNewTop), nNewRightPlus1 - nNewLeft, nNewBottomPlus1 - nNewTop,
                        imgChop2Cut.mbarrayOriginalImg, imgChop2Cut.mnX0InOriginalImg, imgChop2Cut.mnY0InOriginalImg, ImageChop.TYPE_UNKNOWN);
                imgChopsProc.mlistChops.removeLast();
                imgChopsProc.mlistChops.add(chopMerged);
            } else if (nHCutsStyle == UNDER_H_DIV_STYLE)    {
                // under, insert a gap.
                ImageChop chopGap = new ImageChop();
                //todo：dml_changed1 我先试试把这里的under_DIV 直接改成 LINE_DIV ---简单粗暴，直接奏效
                chopGap.setImageChop(imgChop2Cut.mbarrayImg,
                        imgChop2Cut.mnLeft, imgChop2Cut.mapOriginalYIdx2This(chopLastNBD.getBottomP1InOriginalImg()), imgChop2Cut.mnWidth, nGap,
                        imgChop2Cut.mbarrayOriginalImg, imgChop2Cut.mnX0InOriginalImg, imgChop2Cut.mnY0InOriginalImg, ImageChop.TYPE_LINE_DIV);
                imgChopsProc.mlistChops.add(chopGap);
                imgChopsProc.mlistChops.add(imgChops.mlistChops.get(idx));
            } else if (nHCutsStyle == CAP_H_DIV_STYLE)    {
                // cap, insert a gap.
                ImageChop chopGap = new ImageChop();
                chopGap.setImageChop(imgChop2Cut.mbarrayImg,
                        imgChop2Cut.mnLeft, imgChop2Cut.mapOriginalYIdx2This(chopLastNBD.getBottomP1InOriginalImg()), imgChop2Cut.mnWidth, nGap,
                        imgChop2Cut.mbarrayOriginalImg, imgChop2Cut.mnX0InOriginalImg, imgChop2Cut.mnY0InOriginalImg, ImageChop.TYPE_LINE_DIV);
                imgChopsProc.mlistChops.add(chopGap);
                imgChopsProc.mlistChops.add(imgChops.mlistChops.get(idx));
            } else if (nHCutsStyle == BLANK_H_DIV_STYLE) {
                // They are two expressions. insert a gap.
                ImageChop chopGap = new ImageChop();
                chopGap.setImageChop(imgChop2Cut.mbarrayImg,
                        imgChop2Cut.mnLeft, imgChop2Cut.mapOriginalYIdx2This(chopLastNBD.getBottomP1InOriginalImg()), imgChop2Cut.mnWidth, nGap, 
                        imgChop2Cut.mbarrayOriginalImg, imgChop2Cut.mnX0InOriginalImg, imgChop2Cut.mnY0InOriginalImg, ImageChop.TYPE_BLANK_DIV);
                imgChopsProc.mlistChops.add(chopGap);
                imgChopsProc.mlistChops.add(imgChops.mlistChops.get(idx));
            } else  {   // divide_h_div_style
                imgChopsProc.mlistChops.add(imgChops.mlistChops.get(idx));
            }
        }
        
        return imgChopsProc;
    }

    //竖直切
    // here, we assume that the barrayImg has been preprocessed, i.e. the units are not necessarily one
    // pixel, but a minimum identible area (nStep * nStep) and the barrayImg is in the minimum container.
    // This function return a imagechops set includes all the chops with minimum container. If cannot be
    // cut vertically, return imgeChops which includes only one imgchop, i.e. the whole image.
    public static ImageChops cutVerticallyProj(ImageChop imgChop2Cut, double dAvgStrokeWidth)    {
        // Do not check parameter to save calculating time.
        ImageChops imgChops = new ImageChops();
        LinkedList<CutPath> listCutPaths = new LinkedList<CutPath>();
        int[] narrayColOnExpands = new int[imgChop2Cut.mnWidth];
        double[] darrayMAOnExpands = new double[imgChop2Cut.mnWidth];
        int nNumberRevertCnts = 1;
        for (int idx = imgChop2Cut.getLeftInOriginalImg(); idx < imgChop2Cut.getRightP1InOriginalImg(); idx ++)    {
            int nOnStart = -1, nOnEnd = -2;
            int nIdxInThis = imgChop2Cut.mapOriginalXIdx2This(idx);
            for (int idx1 = imgChop2Cut.getTopInOriginalImg(); idx1 < imgChop2Cut.getBottomP1InOriginalImg(); idx1 ++)    {
                int nThisValue = imgChop2Cut.mbarrayImg[nIdxInThis][imgChop2Cut.mapOriginalYIdx2This(idx1)];
                if (nThisValue == 1)    {
                    if (nOnStart == -1)  {
                        nOnStart = idx1;
                    }
                    nOnEnd = idx1;
                }
            }
            int nCvtIdx = imgChop2Cut.mapOriginalXIdx2Confined(idx);
            narrayColOnExpands[nCvtIdx] = nOnEnd + 1 - nOnStart;
            // smooth.
            double dSmoothRatio = 0.5;
            if (nCvtIdx == 0)   {
                darrayMAOnExpands[nCvtIdx] = narrayColOnExpands[nCvtIdx];
            } else  {
                darrayMAOnExpands[nCvtIdx] = (1.0 - dSmoothRatio)*narrayColOnExpands[nCvtIdx] + dSmoothRatio*darrayMAOnExpands[nCvtIdx - 1];
            }
            if (nCvtIdx > 1
                    && (darrayMAOnExpands[nCvtIdx - 2] - darrayMAOnExpands[nCvtIdx - 1]) * (darrayMAOnExpands[nCvtIdx - 1] - darrayMAOnExpands[nCvtIdx]) <= 0
                    && (Math.abs(darrayMAOnExpands[nCvtIdx - 2] - darrayMAOnExpands[nCvtIdx - 1]) + Math.abs(darrayMAOnExpands[nCvtIdx - 1] - darrayMAOnExpands[nCvtIdx]) != 0))    {
                nNumberRevertCnts ++;
            }
        }
        
        int nWindowWidth = (int)Math.ceil(Math.max(imgChop2Cut.mnWidth/2.0/(double)nNumberRevertCnts, ConstantsMgr.msnMinNormalCharWidthInUnit/4.0));
        int nWindowSideEnlarge = (int)Math.ceil(imgChop2Cut.mnHeight * ConstantsMgr.msdMaxHorizontalSlope /2.0);
        int[] narrayAndColOnCnts = new int[imgChop2Cut.mnWidth];
        double[] darrayAvgColOnExpands = new double[imgChop2Cut.mnWidth];
        for (int idx = imgChop2Cut.getLeftInOriginalImg(); idx < imgChop2Cut.getRightP1InOriginalImg(); idx ++)    {
            // sample 2 columns here.
            int[] narraySelCols = new int[2];
            narraySelCols[0] = idx;
            narraySelCols[1] = ((idx - nWindowWidth) < imgChop2Cut.getLeftInOriginalImg())?
                            imgChop2Cut.getLeftInOriginalImg():(idx - nWindowWidth);
            int nIdxInConfined = imgChop2Cut.mapOriginalXIdx2Confined(idx);
            for (int idx1 = imgChop2Cut.getTopInOriginalImg(); idx1 < imgChop2Cut.getBottomP1InOriginalImg(); idx1 ++)    {
                if (idx == imgChop2Cut.getLeftInOriginalImg())  {
                    darrayAvgColOnExpands[nIdxInConfined] = narrayColOnExpands[nIdxInConfined];
                } else  {
                    darrayAvgColOnExpands[nIdxInConfined] = (double)(narrayColOnExpands[nIdxInConfined]
                            + nWindowWidth * darrayAvgColOnExpands[nIdxInConfined - 1])/(1.0 + nWindowWidth);
                }
                int nAndValue = 1;
                for (int idx2 = 0; idx2 < narraySelCols.length; idx2 ++) {
                    nAndValue &= imgChop2Cut.mbarrayImg[imgChop2Cut.mapOriginalXIdx2This(narraySelCols[idx2])][imgChop2Cut.mapOriginalYIdx2This(idx1)];
                }
                narrayAndColOnCnts[nIdxInConfined] += nAndValue;
            }
        }
        
        double dCutThreshold = 0;   // 2 * dAvgStrokeWidth
        byte[][] barrayImgCutPart = new byte[imgChop2Cut.mnWidth][imgChop2Cut.mnHeight];
        for (int idx = imgChop2Cut.getLeftInOriginalImg(); idx < imgChop2Cut.getRightP1InOriginalImg(); idx ++)    {            
            // use an enlarged window size to find path.
            int nIdxConfined = imgChop2Cut.mapOriginalXIdx2Confined(idx);
            if (nIdxConfined >= Math.max(2, nWindowWidth)) {
                if ((darrayAvgColOnExpands[nIdxConfined] >= darrayAvgColOnExpands[nIdxConfined - 1]
                        && darrayAvgColOnExpands[nIdxConfined - 2] > darrayAvgColOnExpands[nIdxConfined - 1])
                    || (narrayAndColOnCnts[nIdxConfined - 1] <= dCutThreshold))   {
                    // get extreme value. and find a cut place.
                    int nCutPathSearchLeft = (idx - nWindowWidth - nWindowSideEnlarge < imgChop2Cut.getLeftInOriginalImg())?imgChop2Cut.getLeftInOriginalImg():(idx - nWindowWidth - nWindowSideEnlarge);
                    int nCutPathSearchWidth = (idx + nWindowSideEnlarge >= imgChop2Cut.getRightP1InOriginalImg())?(imgChop2Cut.getRightP1InOriginalImg() - nCutPathSearchLeft)
                            :(idx + nWindowSideEnlarge + 1 - nCutPathSearchLeft);
                    CutPath cutPath = calcVerticalCutPath(imgChop2Cut.mbarrayImg,
                                                        imgChop2Cut.mapOriginalXIdx2This(nCutPathSearchLeft),
                                                        imgChop2Cut.mnTop, nCutPathSearchWidth,
                                                        imgChop2Cut.mnHeight, true);
                    if (cutPath.mdLen > dCutThreshold)  {
                        continue;
                    }
                    CutPath cutPathLast = (listCutPaths.size() > 0)?listCutPaths.getLast():null;
                    listCutPaths.add(cutPath);
                    int nThisLeft = imgChop2Cut.getRightP1InOriginalImg(),
                            nThisRight = imgChop2Cut.getLeftInOriginalImg() - 1,
                            nThisTop = imgChop2Cut.getBottomP1InOriginalImg(),
                            nThisBottom = imgChop2Cut.getTopInOriginalImg() - 1;
                    for (int idx2 = imgChop2Cut.getTopInOriginalImg(); idx2 < imgChop2Cut.getBottomP1InOriginalImg(); idx2 ++)   {
                        int nIdx2InConfined = imgChop2Cut.mapOriginalYIdx2Confined(idx2), nIdx2InThis = imgChop2Cut.mapOriginalYIdx2This(idx2);
                        int nLeftMost = (cutPathLast == null)?imgChop2Cut.mnLeft:(int)(cutPathLast.mp3Path[nIdx2InConfined].getX() + 1);
                        nLeftMost = imgChop2Cut.mapThisXIdx2Original(nLeftMost);
                        int nRightMost = (int)cutPath.mp3Path[nIdx2InConfined].getX() - 1;
                        nRightMost = imgChop2Cut.mapThisXIdx2Original(nRightMost);
                        for (int idx3 = nLeftMost; idx3 <= nRightMost; idx3 ++) {
                            barrayImgCutPart[imgChop2Cut.mapOriginalXIdx2Confined(idx3)][nIdx2InConfined]
                                    = imgChop2Cut.mbarrayImg[imgChop2Cut.mapOriginalXIdx2This(idx3)][nIdx2InThis];
                            if (imgChop2Cut.mbarrayImg[imgChop2Cut.mapOriginalXIdx2This(idx3)][nIdx2InThis] == 1)  {
                                if (idx3 < nThisLeft)   {
                                    nThisLeft = idx3;
                                }
                                if (idx3 > nThisRight)  {
                                    nThisRight = idx3;
                                }
                                if (idx2 < nThisTop)    {
                                    nThisTop = idx2;
                                }
                                if (idx2 > nThisBottom) {
                                    nThisBottom = idx2;
                                }
                            }
                        }
                    }
                    
                    if (nThisLeft < imgChop2Cut.getRightP1InOriginalImg()
                           || nThisRight > imgChop2Cut.getLeftInOriginalImg() - 1
                           || nThisTop < imgChop2Cut.getBottomP1InOriginalImg()
                           || nThisBottom > imgChop2Cut.getTopInOriginalImg() - 1)  {
                        // there must be a point which is 1.
                        ImageChop imgChopCutPart = new ImageChop();
                        int nConfinedWidth = nThisRight - nThisLeft + 1, nConfinedHeight = nThisBottom - nThisTop + 1;
                        byte[][] barrayImgCutPartConfined = new byte[nConfinedWidth][nConfinedHeight];
                        int nConfinedLeft = imgChop2Cut.mapOriginalXIdx2Confined(nThisLeft);
                        int nConfinedTop = imgChop2Cut.mapOriginalYIdx2Confined(nThisTop);
                        for (int idxA = 0; idxA < nConfinedWidth; idxA ++) {
                            // copy to a small array to save memory
                            int nSrcIdx = idxA + nConfinedLeft;
                            System.arraycopy(barrayImgCutPart[nSrcIdx], nConfinedTop, barrayImgCutPartConfined[idxA], 0, nConfinedHeight);
                            Arrays.fill(barrayImgCutPart[nSrcIdx], (byte)0);  // set barrayImgCutPart back to zeros.
                        }
                        imgChopCutPart.setImageChop(barrayImgCutPartConfined, 0, 0, nConfinedWidth, nConfinedHeight,
                                imgChop2Cut.mbarrayOriginalImg, nThisLeft, nThisTop, ImageChop.TYPE_UNKNOWN);
                        imgChopCutPart = imgChopCutPart.convert2MinContainer();
                        imgChops.mlistChops.add(imgChopCutPart);
                    }   // otherwise, it is an empty chop.
                }
            }
        }
        if (listCutPaths.size() > 0)    {   // note that listCutPaths size can be larger than number of children in imgChops.
            CutPath cutPathLast = listCutPaths.getLast();
            int nThisLeft = imgChop2Cut.getRightP1InOriginalImg(),
                    nThisRight = imgChop2Cut.getLeftInOriginalImg() - 1,
                    nThisTop = imgChop2Cut.getBottomP1InOriginalImg(),
                    nThisBottom = imgChop2Cut.getTopInOriginalImg() - 1;
            for (int idx2 = imgChop2Cut.getTopInOriginalImg(); idx2 < imgChop2Cut.getBottomP1InOriginalImg(); idx2 ++)   {
                int nIdx2InConfined = imgChop2Cut.mapOriginalYIdx2Confined(idx2), nIdx2InThis = imgChop2Cut.mapOriginalYIdx2This(idx2);
                int nLeftMost = (int)(cutPathLast.mp3Path[nIdx2InConfined].getX() + 1);
                nLeftMost = imgChop2Cut.mapThisXIdx2Original(nLeftMost);
                int nRightMost = imgChop2Cut.getRightInOriginalImg();
                for (int idx3 = nLeftMost; idx3 <= nRightMost; idx3 ++) {
                    barrayImgCutPart[imgChop2Cut.mapOriginalXIdx2Confined(idx3)][nIdx2InConfined]
                            = imgChop2Cut.mbarrayImg[imgChop2Cut.mapOriginalXIdx2This(idx3)][nIdx2InThis];
                    if (imgChop2Cut.mbarrayImg[imgChop2Cut.mapOriginalXIdx2This(idx3)][nIdx2InThis] == 1)  {
                        if (idx3 < nThisLeft)   {
                            nThisLeft = idx3;
                        }
                        if (idx3 > nThisRight)  {
                            nThisRight = idx3;
                        }
                        if (idx2 < nThisTop)    {
                            nThisTop = idx2;
                        }
                        if (idx2 > nThisBottom) {
                            nThisBottom = idx2;
                        }
                    }
                }
            }
            ImageChop imgChopCutPart = new ImageChop();
            int nConfinedWidth = nThisRight - nThisLeft + 1, nConfinedHeight = nThisBottom - nThisTop + 1;
            byte[][] barrayImgCutPartConfined = new byte[nConfinedWidth][nConfinedHeight];
            int nConfinedLeft = imgChop2Cut.mapOriginalXIdx2Confined(nThisLeft);
            int nConfinedTop = imgChop2Cut.mapOriginalYIdx2Confined(nThisTop);
            for (int idxA = 0; idxA < nConfinedWidth; idxA ++) {
                // copy to a small array to save memory
                int nSrcIdx = idxA + nConfinedLeft;
                System.arraycopy(barrayImgCutPart[nSrcIdx], nConfinedTop, barrayImgCutPartConfined[idxA], 0, nConfinedHeight);
                // Arrays.fill(barrayImgCutPart[nSrcIdx], (byte)0);  // no need to set barrayImgCutPart back to zeros because it is no longer used.
            }
            imgChopCutPart.setImageChop(barrayImgCutPartConfined, 0, 0, nConfinedWidth, nConfinedHeight,
                    imgChop2Cut.mbarrayOriginalImg, nThisLeft, nThisTop, ImageChop.TYPE_UNKNOWN);
            imgChopCutPart = imgChopCutPart.convert2MinContainer();
            imgChops.mlistChops.add(imgChopCutPart);
        } else  {
            // need not to convert to min container because the whole chart has been in a min container.
            imgChops.mlistChops.add(imgChop2Cut);
        }
        return imgChops;
    }


    //最短路径切割
    //========================================================================================================
    // here, we try to cut imgChop2Cut to 2 pieces while minimum path, note that if the minimum path
    // is beyond the number of strokes or total cut length, the image chop will not be cut, i.e. return
    // a single element imageChops
    public static ImageChops cutVerticallyViaMinPath(ImageChop imgChop2Cut, int nWindowWidth, double dAvgStrokeWidth, boolean bComparePnts, int nMaxCutPnts, int nMaxCutStrokes)    {
        // Do not check parameter to save calculating time.
        ImageChops imgChops = new ImageChops();
                
        CutPath cutMinPath = null;
        int nCutMinStrokes = Integer.MAX_VALUE;
        int nLeftFrom = imgChop2Cut.getLeftInOriginalImg() + (int)Math.ceil(dAvgStrokeWidth);
        int nRightTo = imgChop2Cut.getRightP1InOriginalImg() - (int)Math.ceil(dAvgStrokeWidth);
        for (int idx = nLeftFrom; idx < nRightTo; idx ++)    {            
            // use an enlarged window size to find path.
            // get extreme value. and find a cut place.
            int nCutPathSearchLeft = idx;
            int nCutPathSearchWidth = (idx + nWindowWidth >= nRightTo)?(nRightTo - nCutPathSearchLeft):nWindowWidth;
            CutPath cutPath = calcVerticalCutPath(imgChop2Cut.mbarrayImg,
                                                imgChop2Cut.mapOriginalXIdx2This(nCutPathSearchLeft),
                                                imgChop2Cut.mnTop, nCutPathSearchWidth,
                                                imgChop2Cut.mnHeight, true);
            if (cutPath.mdLenExtraMeasure3 > nMaxCutPnts)  {    // use extra measure 3 because we want to see the number of cut pnts.
                continue;
            }
            int nCutStrokes = 0;
            for (int idx1 = 0; idx1 < cutPath.mp3Path.length; idx1 ++)  {
                if (idx1 == 0)  {
                    if (imgChop2Cut.mbarrayImg[(int)cutPath.mp3Path[idx1].getX()][(int)cutPath.mp3Path[idx1].getY()] == 1)  {
                        nCutStrokes ++;
                    }
                } else  {
                    if (imgChop2Cut.mbarrayImg[(int)cutPath.mp3Path[idx1 - 1].getX()][(int)cutPath.mp3Path[idx1 - 1].getY()] == 0
                            && imgChop2Cut.mbarrayImg[(int)cutPath.mp3Path[idx1].getX()][(int)cutPath.mp3Path[idx1].getY()] == 1)  {
                        nCutStrokes ++;
                    }
                }
            }
            if (nCutStrokes > nMaxCutStrokes)   {
                continue;
            }
            if (cutMinPath == null) {
                cutMinPath = cutPath;
                nCutMinStrokes = nCutStrokes;
            } else if (bComparePnts)    {
                if (cutPath.mdLen < cutMinPath.mdLen)    {
                    cutMinPath = cutPath;
                    nCutMinStrokes = nCutStrokes;
                } else if (cutPath.mdLen == cutMinPath.mdLen && nCutStrokes < nCutMinStrokes)   {
                    cutMinPath = cutPath;
                    nCutMinStrokes = nCutStrokes;
                }
            } else  {
                if (nCutStrokes < nCutMinStrokes)    {
                    cutMinPath = cutPath;
                    nCutMinStrokes = nCutStrokes;
                } else if (cutPath.mdLen < cutMinPath.mdLen && nCutStrokes == nCutMinStrokes)   {
                    cutMinPath = cutPath;
                    nCutMinStrokes = nCutStrokes;
                }
            }
        }
        
        if (cutMinPath == null) {
            imgChops.mlistChops.add(imgChop2Cut);
            return imgChops;
        }
        
        byte[][] barrayImgCutLeft = new byte[imgChop2Cut.mnWidth][imgChop2Cut.mnHeight];
        byte[][] barrayImgCutRight = new byte[imgChop2Cut.mnWidth][imgChop2Cut.mnHeight];
        int nLeftPartTop = Integer.MAX_VALUE, nLeftPartBottom = Integer.MIN_VALUE, nLeftPartRight = Integer.MIN_VALUE;
        int nRightPartTop = Integer.MAX_VALUE, nRightPartBottom = Integer.MIN_VALUE, nRightPartLeft = Integer.MAX_VALUE;
        for (int idx = 0; idx < imgChop2Cut.mnHeight; idx ++)   {
            for (int idx1 = 0; idx1 < imgChop2Cut.mnWidth; idx1 ++) {
                if (idx1 < imgChop2Cut.mapThisXIdx2Confined((int)cutMinPath.mp3Path[idx].getX())
                        && (imgChop2Cut.mbarrayImg[imgChop2Cut.mapConfinedXIdx2This(idx1)][imgChop2Cut.mapConfinedYIdx2This(idx)] == 1)) {
                    barrayImgCutLeft[idx1][idx] = 1;    
                    if (idx1 > nLeftPartRight)  {
                        nLeftPartRight = idx1;
                    }
                    if (idx < nLeftPartTop) {
                        nLeftPartTop = idx;
                    }
                    if (idx > nLeftPartBottom)  {
                        nLeftPartBottom = idx;
                    }
                } else if (idx1 > imgChop2Cut.mapThisXIdx2Confined((int)cutMinPath.mp3Path[idx].getX())
                        && (imgChop2Cut.mbarrayImg[imgChop2Cut.mapConfinedXIdx2This(idx1)][imgChop2Cut.mapConfinedYIdx2This(idx)] == 1))    {
                    barrayImgCutRight[idx1][idx] = 1;
                    if (idx1 < nRightPartLeft)  {
                        nRightPartLeft = idx1;
                    }
                    if (idx < nRightPartTop) {
                        nRightPartTop = idx;
                    }
                    if (idx > nRightPartBottom)  {
                        nRightPartBottom = idx;
                    }
                }
            }
        }
        ImageChop imgChopLeft = new ImageChop();
        imgChopLeft.setImageChop(barrayImgCutLeft, 0, nLeftPartTop, nLeftPartRight + 1, nLeftPartBottom - nLeftPartTop + 1, imgChop2Cut.mbarrayOriginalImg,
                imgChop2Cut.getLeftInOriginalImg(), imgChop2Cut.getTopInOriginalImg(), ImageChop.TYPE_UNKNOWN);
        ImageChop imgChopRight = new ImageChop();
        imgChopRight.setImageChop(barrayImgCutRight, nRightPartLeft, nRightPartTop, imgChop2Cut.mnWidth  - nRightPartLeft, nRightPartBottom - nRightPartTop + 1, imgChop2Cut.mbarrayOriginalImg,
                imgChop2Cut.getLeftInOriginalImg(), imgChop2Cut.getTopInOriginalImg(), ImageChop.TYPE_UNKNOWN);
        
        imgChops.mlistChops.add(imgChopLeft);
        imgChops.mlistChops.add(imgChopRight);
        return imgChops;
    }


    //返回元素的最小单元列表
    //========================================================================================================
    // returned imagechops are all in minimum container.
    public static ImageChops extractConnectedPieces(ImageChop chop2Extract) {
        
        byte[][] barrayImgCpy = new byte[chop2Extract.mnWidth][chop2Extract.mnHeight];  // initialize a zero array.
        for (int j = 0; j < chop2Extract.mnWidth; j ++)  {
            barrayImgCpy[j] = new byte[chop2Extract.mnHeight];
            System.arraycopy(chop2Extract.mbarrayImg[chop2Extract.mnLeft + j], chop2Extract.mnTop, barrayImgCpy[j], 0, chop2Extract.mnHeight);
        }
        
        ImageChops chops = new ImageChops();
        byte[][] barrayChop = new byte[chop2Extract.mnWidth][chop2Extract.mnHeight];
        byte[] barrayZerosCol = new byte[chop2Extract.mnHeight];
        while(true) {
            // barrayChop has been used before, so reinitialize it to all zero.
            for (int j = 0; j < chop2Extract.mnWidth; j ++)  {
                System.arraycopy(barrayZerosCol, 0, barrayChop[j], 0, chop2Extract.mnHeight);
            }

            LinkedList<Position3D> listConnected = new LinkedList<Position3D>();
            int nNewTop = chop2Extract.mnHeight , nNewLeft = chop2Extract.mnWidth, nNewBottom = -1, nNewRight = -1;
            boolean bHasPnts = false;
            for (int idx = 0; idx < chop2Extract.mnHeight; idx ++)    {
                for (int idx1 = 0; idx1 < chop2Extract.mnWidth; idx1 ++)  {
                    if (barrayImgCpy[idx1][idx] == 1)   {
                        Position3D pnt = new Position3D(idx1, idx);
                        barrayChop[idx1][idx] = 1;
                        barrayImgCpy[idx1][idx] = 0;
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
                if (x < chop2Extract.mnWidth - 1 && barrayImgCpy[x + 1][y] == 1)   {
                    Position3D pnt = new Position3D(x + 1, y);
                    barrayChop[x + 1][y] = 1;
                    barrayImgCpy[x + 1][y] = 0;
                    listConnected.add(pnt);
                }
                if (x < chop2Extract.mnWidth - 1 && y > 0 && barrayImgCpy[x + 1][y - 1] == 1)   {
                    Position3D pnt = new Position3D(x + 1, y - 1);
                    barrayChop[x + 1][y - 1] = 1;
                    barrayImgCpy[x + 1][y - 1] = 0;
                    listConnected.add(pnt);
                }
                if (y > 0 && barrayImgCpy[x][y - 1] == 1)   {
                    Position3D pnt = new Position3D(x, y - 1);
                    barrayChop[x][y - 1] = 1;
                    barrayImgCpy[x][y - 1] = 0;
                    listConnected.add(pnt);
                }
                if (x > 0 && y > 0 && barrayImgCpy[x - 1][y - 1] == 1)   {
                    Position3D pnt = new Position3D(x - 1, y - 1);
                    barrayChop[x - 1][y - 1] = 1;
                    barrayImgCpy[x - 1][y - 1] = 0;
                    listConnected.add(pnt);
                }
                if (x > 0 && barrayImgCpy[x - 1][y] == 1)   {
                    Position3D pnt = new Position3D(x - 1, y);
                    barrayChop[x - 1][y] = 1;
                    barrayImgCpy[x - 1][y] = 0;
                    listConnected.add(pnt);
                }
                if (x > 0 && y < chop2Extract.mnHeight - 1 && barrayImgCpy[x - 1][y + 1] == 1)   {
                    Position3D pnt = new Position3D(x - 1, y + 1);
                    barrayChop[x - 1][y + 1] = 1;
                    barrayImgCpy[x - 1][y + 1] = 0;
                    listConnected.add(pnt);
                }
                if (y < chop2Extract.mnHeight - 1 && barrayImgCpy[x][y + 1] == 1)   {
                    Position3D pnt = new Position3D(x, y + 1);
                    barrayChop[x][y + 1] = 1;
                    barrayImgCpy[x][y + 1] = 0;
                    listConnected.add(pnt);
                }
                if (x < chop2Extract.mnWidth - 1 && y < chop2Extract.mnHeight - 1 && barrayImgCpy[x + 1][y + 1] == 1)   {
                    Position3D pnt = new Position3D(x + 1, y + 1);
                    barrayChop[x + 1][y + 1] = 1;
                    barrayImgCpy[x + 1][y + 1] = 0;
                    listConnected.add(pnt);
                }
            }

            if (bHasPnts)   {
                ImageChop imageChop = new ImageChop();
                int nConfinedChopWidth = nNewRight - nNewLeft + 1;
                int nConfinedChopHeight = nNewBottom - nNewTop + 1;
                byte[][] barrayChopConfined = new byte[nConfinedChopWidth][nConfinedChopHeight];
                for (int idx = nNewLeft; idx <= nNewRight; idx ++)  {
                    System.arraycopy(barrayChop[idx], nNewTop, barrayChopConfined[idx - nNewLeft], 0, nConfinedChopHeight);
                }
                imageChop.setImageChop(barrayChopConfined, 0, 0, nConfinedChopWidth, nConfinedChopHeight, chop2Extract.mbarrayOriginalImg, 
                        chop2Extract.getLeftInOriginalImg() + nNewLeft, chop2Extract.getTopInOriginalImg() + nNewTop, ImageChop.TYPE_UNKNOWN);
                chops.mlistChops.add(imageChop);
            } else  {
                break;
            }
        }
        
        return chops;
    }


    // All the image chops should have same mbarrayOriginalImg, merged image also has this mbarrayOriginalImg, otherwise,
    // return an empty image chop. The returned image chop is minimum contained.
    public static ImageChop mergeImgChopsWithSameOriginal(LinkedList<ImageChop> listImgChops)    {
        ImageChop imgChopReturn = new ImageChop();
        if (listImgChops.size() == 0)   {
            return imgChopReturn;
        } else if (listImgChops.size() == 1) {
            return listImgChops.getFirst().convert2MinContainer();
        }
        byte[][] barrayOriginalImg = listImgChops.getFirst().mbarrayOriginalImg;
        if (barrayOriginalImg == null) {
            return imgChopReturn;
        }
        int nLeft = barrayOriginalImg.length, nRight = -1, nTop = barrayOriginalImg[0].length, nBottom = -1;
        boolean bHMonoInc = true, bHMonoDec = true, bVMonoInc = true, bVMonoDec = true;
        ImageChop imgChopLast = null;
        for (ImageChop imgChop: listImgChops)   {
            if (imgChop.mbarrayOriginalImg != barrayOriginalImg)   {
                return imgChopReturn;
            }
            if (imgChop.getLeftInOriginalImg() < nLeft)   {
                nLeft = imgChop.getLeftInOriginalImg();
            }
            if (imgChop.getRightInOriginalImg() > nRight)    {
                nRight = imgChop.getRightInOriginalImg();
            }
            if (imgChop.getTopInOriginalImg() < nTop)   {
                nTop = imgChop.getTopInOriginalImg();
            }
            if (imgChop.getBottomInOriginalImg() > nBottom)    {
                nBottom = imgChop.getBottomInOriginalImg();
            }
            if (imgChopLast != null) {
                if (bHMonoInc && imgChop.getLeftInOriginalImg() < imgChopLast.getRightP1InOriginalImg()) {
                    bHMonoInc = false;
                }
                if (bHMonoDec && imgChopLast.getLeftInOriginalImg() < imgChop.getRightP1InOriginalImg()) {
                    bHMonoDec = false;
                }
                if (bVMonoInc && imgChop.getTopInOriginalImg() < imgChopLast.getBottomP1InOriginalImg()) {
                    bVMonoInc = false;
                }
                if (bVMonoDec && imgChopLast.getTopInOriginalImg() < imgChop.getBottomP1InOriginalImg()) {
                    bVMonoDec = false;
                }
            }
            imgChopLast = imgChop;
        }
        byte[][] barrayMergedImg  = new byte[nRight + 1 - nLeft][nBottom + 1 - nTop];
        if (bHMonoInc || bHMonoDec || bVMonoInc || bVMonoDec) {
            // no overlap.
            for (ImageChop imgChop: listImgChops)   {
                for (int idx = imgChop.mnLeft; idx < imgChop.getRightPlus1(); idx ++) {
                    System.arraycopy(imgChop.mbarrayImg[idx], imgChop.mnTop,
                            barrayMergedImg[idx + imgChop.mnX0InOriginalImg - nLeft],
                            imgChop.mnTop + imgChop.mnY0InOriginalImg - nTop,
                            imgChop.mnHeight);
                }
                
            }
        } else {
            for (ImageChop imgChop: listImgChops)   {
                for (int idx = imgChop.mnLeft; idx < imgChop.getRightPlus1(); idx ++)  {
                    for (int idx1 = imgChop.mnTop; idx1 < imgChop.getBottomPlus1(); idx1 ++)    {
                        if (imgChop.mbarrayImg[idx][idx1] == 1) {
                            int nNewX = idx + imgChop.mnX0InOriginalImg - nLeft;
                            int nNewY = idx1 + imgChop.mnY0InOriginalImg - nTop;
                            barrayMergedImg[nNewX][nNewY] = 1;
                        }
                    }
                }
            }
        }
        imgChopReturn.setImageChop(barrayMergedImg, 0, 0, nRight + 1 - nLeft, nBottom + 1 - nTop,
                barrayOriginalImg, nLeft, nTop, ImageChop.TYPE_UNKNOWN);
        return imgChopReturn.convert2MinContainer();
    }
    
    // All the image chops need not to have same mbarrayImg. The returned image chop is minimum contained.
    public static ImageChop mergeImgChops(LinkedList<ImageChop> listImgChops)    {
        ImageChop imgChopReturn = new ImageChop();
        if (listImgChops.size() == 0)   {
            return imgChopReturn;
        }
        int nLeft = Integer.MAX_VALUE, nRight = -1, nTop = Integer.MAX_VALUE, nBottom = -1;
        for (ImageChop imgChop: listImgChops)   {
            if (imgChop.mnLeft < nLeft)   {
                nLeft = imgChop.mnLeft;
            }
            if (imgChop.mnLeft + imgChop.mnWidth - 1 > nRight)    {
                nRight = imgChop.mnLeft + imgChop.mnWidth - 1;
            }
            if (imgChop.mnTop < nTop)   {
                nTop = imgChop.mnTop;
            }
            if (imgChop.mnTop + imgChop.mnHeight - 1 > nBottom)    {
                nBottom = imgChop.mnTop + imgChop.mnHeight - 1;
            }
        }
        byte[][] barrayMergedImg  = new byte[nRight + 1][nBottom + 1];
        int nMaxX = -1, nMinX = nRight + 1, nMaxY = -1, nMinY = nBottom + 1;
        for (ImageChop imgChop: listImgChops)   {
            for (int idx = imgChop.mnLeft; idx < imgChop.mnLeft + imgChop.mnWidth; idx ++)  {
                for (int idx1 = imgChop.mnTop; idx1 < imgChop.mnTop + imgChop.mnHeight; idx1 ++)    {
                    if (imgChop.mbarrayImg[idx][idx1] == 1) {
                        barrayMergedImg[idx][idx1] = 1;
                        if (nMaxX > idx)   {
                            nMaxX = idx;
                        }
                        if (nMinX < idx)  {
                            nMinX = idx;
                        }
                        if (nMaxY > idx1)   {
                            nMaxY = idx1;
                        }
                        if (nMinY < idx1)  {
                            nMinY = idx1;
                        }
                    }
                }
            }
        }
        if (nMaxX == -1 || nMinX == nRight + 1 || nMaxY == -1 || nMinY == nBottom + 1 - nTop)   {
            // no point is on
            nMaxX = nMinX = nMaxY = nMinY = 0;
        }
        imgChopReturn.setImageChop(barrayMergedImg, nMinX, nMinY, nMaxX + 1 - nMinX, nMaxY + 1 - nMinY, ImageChop.TYPE_UNKNOWN);
        return imgChopReturn;
    }

    //找主要的一片
    public static int getMajorChopFromSameOriginal(ImageChops imgChops) {
        // here assume all the imgChops have the same barray image original, and imgChops include at least one imgChop
        if (imgChops == null || imgChops.mlistChops.size() == 0)    {
            return -1;
        }
        
        int nMaxWidth = 0, nMaxWidthIdx = -1;  
        int nMaxHeight = 0, nMaxHeightIdx = -1;
        int nMaxArea = 0, nMaxAreaIdx = -1;
        for (int idx = 0; idx < imgChops.mlistChops.size(); idx ++) {
            ImageChop imgChop = imgChops.mlistChops.get(idx);
            if (imgChop.mnWidth > nMaxWidth)    {
                nMaxWidth = imgChop.mnWidth;
                nMaxWidthIdx = idx;
            }
            if (imgChop.mnHeight > nMaxHeight)  {
                nMaxHeight = imgChop.mnHeight;
                nMaxHeightIdx = idx;
            }
            if (imgChop.mnWidth * imgChop.mnHeight > nMaxArea)  {
                nMaxArea = imgChop.mnWidth * imgChop.mnHeight;
                nMaxAreaIdx = idx;
            }
        }
        int nExtractedMajorIdx = nMaxAreaIdx;
        int nOriginalWidth = imgChops.mlistChops.get(0).mbarrayOriginalImg.length;
        int nOriginalHeight = imgChops.mlistChops.get(0).mbarrayOriginalImg[0].length;
        if (nMaxArea > (nOriginalWidth * nOriginalHeight / 2.0))  {
            nExtractedMajorIdx = nMaxAreaIdx;
        } else if (nMaxWidth > (nOriginalWidth / 2.0)) {
            nExtractedMajorIdx = nMaxWidthIdx;
        } else if (nMaxHeight > (nOriginalHeight / 2.0))    {
            nExtractedMajorIdx = nMaxHeightIdx;
        } else  {
            nExtractedMajorIdx = nMaxAreaIdx;
        }
        return nExtractedMajorIdx;
    }

    // this function 
    public static int locateVCutCluster(LinkedList<ImageChop> listChops, int idxThis, ImageChop imgChopMerged) {
        if (idxThis < 0 || idxThis >= listChops.size()) {
            return -1;  // invalid index
        }
        int idx = idxThis;
        LinkedList<ImageChop> listImgChops2Merge = new LinkedList<ImageChop>();
        listImgChops2Merge.add(listChops.get(idx));
        int nMergedLeft = listChops.get(idx).getLeftInOriginalImg(),
                nMergedRightP1 = listChops.get(idx).getRightP1InOriginalImg(),
                nMergedTop = listChops.get(idx).getTopInOriginalImg(),
                nMergedBottomP1 = listChops.get(idx).getBottomP1InOriginalImg();
        for (idx = idxThis + 1; idx < listChops.size(); idx ++)   {
            int nLeftRightGap = listChops.get(idx).getLeftInOriginalImg() - listChops.get(idx - 1).getRightP1InOriginalImg();
            int nLeftHeight = listChops.get(idx - 1).mnHeight;
            int nRightHeight = listChops.get(idx).mnHeight;
            int nLeftRightOverlap = Math.min(listChops.get(idx - 1).getBottomP1InOriginalImg(), listChops.get(idx).getBottomP1InOriginalImg())
                    - Math.max(listChops.get(idx - 1).getTopInOriginalImg(), listChops.get(idx).getTopInOriginalImg());
            if (nLeftRightGap > nLeftHeight * ConstantsMgr.msdClusterChopMaxGap || nLeftRightGap > nRightHeight * ConstantsMgr.msdClusterChopMaxGap)    {
                // the gap between two adjacent v-cuts are too wide, cannot be in the same cluster
                break;
            } else if (nLeftRightOverlap < Math.min(nLeftHeight, nRightHeight) * ConstantsMgr.msdClusterChopMinSmallerOverlap 
                         || nLeftRightOverlap < Math.max(nLeftHeight, nRightHeight) * ConstantsMgr.msdClusterChopMinLargerOverlap)   {
                // there is some overlap, but not large enough
                break;
            }
            listImgChops2Merge.add(listChops.get(idx));
            if (nMergedLeft > listChops.get(idx).getLeftInOriginalImg()) {
                nMergedLeft = listChops.get(idx).getLeftInOriginalImg();
            }
            if (nMergedRightP1 < listChops.get(idx).getRightP1InOriginalImg()) {
                nMergedRightP1 = listChops.get(idx).getRightP1InOriginalImg();
            }
            if (nMergedTop > listChops.get(idx).getTopInOriginalImg()) {
                nMergedTop = listChops.get(idx).getTopInOriginalImg();
            }
            if (nMergedBottomP1 < listChops.get(idx).getBottomP1InOriginalImg()) {
                nMergedBottomP1 = listChops.get(idx).getBottomP1InOriginalImg();
            }
        }
        int nMergedWidth = nMergedRightP1 - nMergedLeft;
        int nMergedHeight = nMergedBottomP1 - nMergedTop;
        if ((double)nMergedWidth/(double)nMergedHeight < ConstantsMgr.msdClusterChopWOverHMin
                || (double)nMergedWidth/(double)nMergedHeight > ConstantsMgr.msdClusterChopWOverHMax) {
            return idxThis + 1; // the shape of merged chop is not right, not a cluster
        }
        if (idx > idxThis + 1)  {
            ImageChop imgChop = mergeImgChopsWithSameOriginal(listImgChops2Merge);
            imgChopMerged.setImageChop(imgChop.mbarrayImg, imgChop.mnLeft, imgChop.mnTop, imgChop.mnWidth, imgChop.mnHeight,
                    imgChop.mbarrayOriginalImg, imgChop.mnX0InOriginalImg, imgChop.mnY0InOriginalImg, imgChop.mnChopType);
        }
        return idx; // returned idx is the first idx that is not in the cluster.
    }
    
    public final static int BLANK_H_DIV_STYLE = 0;
    public final static int DIVIDE_H_DIV_STYLE = 1;
    public final static int UNDER_H_DIV_STYLE = 2;
    public final static int CAP_H_DIV_STYLE = 3;
    public final static int MERGE_H_DIV_STYLE = -1;

    //判断水平切的类型
    public static int identHCutsStyle(LinkedList<ImageChop> listChops, int idxThis, ImageChop chopLastProc, double dNotMergeWidthThresh, double dAvgStrokeWidth, double dMaxEstCharWidth, double dMaxEstCharHeight) {
        ImageChop chopTop = listChops.get(idxThis - 1), chopBottom = listChops.get(idxThis);
        /*if (chopTop != chopLastProc && chopTop.mnHeight <= ConstantsMgr.msdVeryThinOverlappedHeightThresh * dAvgStrokeWidth) {
            chopTop = chopLastProc; // last chop is too thing, cannot be used to identify gap type.
        }*/
        int nGap = chopBottom.getTopInOriginalImg() - chopTop.getBottomP1InOriginalImg();
        int nNewLeft = Math.min(chopTop.getLeftInOriginalImg(), chopBottom.getLeftInOriginalImg());
        int nOverlapLeft = Math.max(chopTop.getLeftInOriginalImg(), chopBottom.getLeftInOriginalImg());
        int nNewRightPlus1 = Math.max(chopTop.getRightP1InOriginalImg(), chopBottom.getRightP1InOriginalImg());
        int nOverlapRightPlus1 = Math.min(chopTop.getRightP1InOriginalImg(), chopBottom.getRightP1InOriginalImg());
        double dAvgCharHeightTop = 0, dAvgCharHeightBottom = 0;
        ImageChops chopsTop = extractConnectedPieces(chopTop);
        ImageChops chopsBottom = extractConnectedPieces(chopBottom);
        double[] darrayTop = new double[2], darrayBottom = new double[2];
        darrayTop = chopsTop.calcAvgWeightedHCharHeight(chopsBottom, chopBottom, dAvgStrokeWidth);
        dAvgCharHeightTop = darrayTop[1];  // normal char height in top
        darrayBottom = chopsBottom.calcAvgWeightedHCharHeight(chopsTop, chopTop, dAvgStrokeWidth);
        dAvgCharHeightBottom = darrayBottom[1];  // normal char height in bottom
        double dBaseAvgCharHeight = Math.max(darrayTop[0], darrayBottom[0]);    // using max all char average height to identify if gap is wide enough
        double dMinNormalCharWInStrokeW = ConstantsMgr.msdMinNormalCharWInStrokeW * dAvgStrokeWidth;
        
        int nTopOnCntsInOverlap = chopsTop.calcProjOnCntOverHRange(nOverlapLeft, nOverlapRightPlus1);
        int nBottomOnCntsInOverlap = chopsBottom.calcProjOnCntOverHRange(nOverlapLeft, nOverlapRightPlus1);
        // nGap is still useful because dAvgTopBtmGap may be very misleading, consider that 1/x_1,
        // we may miss recognize if do not use ngap. Another example is integrate_a^b integrate (cap c) (under d)
        // a and d have over laop, b and c do not have overlap, than c cannot be correctly
        // recognized because its bottom cut includes integrate and under (d), and c will be
        // misrecognized as blank cut.
        
        if (nGap > ConstantsMgr.msdExpressionGap * dBaseAvgCharHeight)   {
            return BLANK_H_DIV_STYLE;
        } else if ((idxThis > 1 && idxThis < listChops.size() - 1)
                && ((listChops.get(idxThis - 2).mnChopType == ImageChop.TYPE_UNKNOWN
                        && chopTop.mnChopType == ImageChop.TYPE_LINE_DIV
                        && chopBottom.mnChopType == ImageChop.TYPE_UNKNOWN
                        && (chopTop.getTopInOriginalImg() - listChops.get(idxThis - 2).getBottomP1InOriginalImg()) <= ConstantsMgr.msdExpressionGap * dBaseAvgCharHeight
                        && (chopBottom.getTopInOriginalImg() - chopTop.getBottomP1InOriginalImg()) <= ConstantsMgr.msdExpressionGap * dBaseAvgCharHeight)
                    || (chopTop.mnChopType == ImageChop.TYPE_UNKNOWN && chopBottom.mnChopType == ImageChop.TYPE_LINE_DIV
                        && listChops.get(idxThis + 1).mnChopType == ImageChop.TYPE_UNKNOWN
                        && (listChops.get(idxThis + 1).getTopInOriginalImg() - chopBottom.getBottomP1InOriginalImg()) <= ConstantsMgr.msdExpressionGap * dBaseAvgCharHeight
                        && (chopBottom.getTopInOriginalImg() - chopTop.getBottomP1InOriginalImg()) <= ConstantsMgr.msdExpressionGap * dBaseAvgCharHeight)))  {
            return DIVIDE_H_DIV_STYLE;
        } else if (idxThis > 1 && listChops.get(idxThis - 2).mnChopType == ImageChop.TYPE_UNKNOWN && chopTop.mnChopType == ImageChop.TYPE_LINE_DIV
                        && chopBottom.mnChopType == ImageChop.TYPE_UNKNOWN
                        && (chopTop.getTopInOriginalImg() - listChops.get(idxThis - 2).getBottomP1InOriginalImg()) <= ConstantsMgr.msdExpressionGap * dBaseAvgCharHeight
                        && (chopBottom.getTopInOriginalImg() - chopTop.getBottomP1InOriginalImg()) <= ConstantsMgr.msdExpressionGap * dBaseAvgCharHeight)  {
            return DIVIDE_H_DIV_STYLE;
        } else if (idxThis < listChops.size() - 1 && chopTop.mnChopType == ImageChop.TYPE_UNKNOWN && chopBottom.mnChopType == ImageChop.TYPE_LINE_DIV
                        && listChops.get(idxThis + 1).mnChopType == ImageChop.TYPE_UNKNOWN
                        && (listChops.get(idxThis + 1).getTopInOriginalImg() - chopBottom.getBottomP1InOriginalImg()) <= ConstantsMgr.msdExpressionGap * dBaseAvgCharHeight
                        && (chopBottom.getTopInOriginalImg() - chopTop.getBottomP1InOriginalImg()) <= ConstantsMgr.msdExpressionGap * dBaseAvgCharHeight)  {
            return DIVIDE_H_DIV_STYLE;
        } else if (dBaseAvgCharHeight <= ConstantsMgr.msdVeryThinOverlappedHeightThresh * dAvgStrokeWidth
                    && nGap <= ConstantsMgr.msdVeryThinGapThresh * dAvgStrokeWidth
                    && ((2 * dAvgCharHeightTop < nOverlapRightPlus1 - nOverlapLeft    // have to ensure that overlap includes more than 1 chars
                            && 2 * dAvgCharHeightBottom < nOverlapRightPlus1 - nOverlapLeft
                            && (nTopOnCntsInOverlap < ConstantsMgr.msdEquationProjHOnCntsRatio * nBottomOnCntsInOverlap
                                || nBottomOnCntsInOverlap < ConstantsMgr.msdEquationProjHOnCntsRatio * nTopOnCntsInOverlap))
                        || Math.abs(chopTop.getLeftInOriginalImg() - chopBottom.getLeftInOriginalImg())
                            >= dMinNormalCharWInStrokeW
                        || Math.abs(chopTop.getRightP1InOriginalImg() - chopBottom.getRightP1InOriginalImg())
                            >= dMinNormalCharWInStrokeW)
                    && chopTop.mnWidth >= dMinNormalCharWInStrokeW
                    && chopBottom.mnWidth >= dMinNormalCharWInStrokeW) {
                // this is for the situation that h-div and equation are mixed in the same horizontal level. So we need to
                // merge. For example, consider 1/2 - 3/4 = 5/6. it is possible that 1 3 5 is a imge chop, ---- is the second image chop
                // - is the third and 2 4 6 is the last. If do not merge 1 3 5 and ----, then when we identify ----- with -,
                // we will misidentify it as black-div instead of merge.
                // we also need to consider divide, it three cuts cannot be merged, so we use
                // chopTop.mnWidth >= dMinNormalCharWInStrokeW && chopBottom.mnWidth >= dMinNormalCharWInStrokeW
                return MERGE_H_DIV_STYLE;
        } else if (nGap < ConstantsMgr.msdBaseCapUnderDistance * dBaseAvgCharHeight)  {
            int nMinGapInRangeExclEdges = calcMinGapOverHRange(chopsTop, chopsBottom, (int)Math.ceil(nOverlapLeft + dAvgStrokeWidth * 3),
                    (int)Math.floor(nOverlapRightPlus1 - dAvgStrokeWidth * 3)); // we believe a normal char that could have top or under must have a width > 3 * avg stroke width.
            if (nMinGapInRangeExclEdges > ConstantsMgr.msdExpressionGap * dBaseAvgCharHeight)   {   // nMinGapInRange == -1 means invalid.
                // min gap in range is too large.
                return BLANK_H_DIV_STYLE;
            }
            // if gap is very narrow compared to overlapped char height, very likely we are gonna merge.
            if (((nNewRightPlus1 - nNewLeft) < dNotMergeWidthThresh)
                && (dAvgCharHeightTop < ConstantsMgr.msdCapUnderHeightRatio2Base * dAvgCharHeightBottom
                        || dAvgCharHeightBottom < ConstantsMgr.msdCapUnderHeightRatio2Base * dAvgCharHeightTop
                        || chopTop.mnHeight < ConstantsMgr.msdCapUnderHeightRatio2Base * chopBottom.mnHeight
                        || chopBottom.mnHeight < ConstantsMgr.msdCapUnderHeightRatio2Base * chopTop.mnHeight))   {
                // this is for a situation like 3/2/(x+y), if not merge, this expression will be divided to 3, /, 2, line_div
                // (x + y), which are five expressions.
                // another possibility is like (a+b)**((x+3)/4), chop 1 is x+3, chop to is (a+b)**(/4), 4 and x+3
                // have similar char height, so use chopTop and chopBottom Height to compare.
                return MERGE_H_DIV_STYLE;
            }

            int nMaxTopChildWidth = 0;
            int nTopOverheadLeft = 0, nTopOverheadRight = 0;
            for (int idx1 = 0; idx1 < chopsTop.mlistChops.size(); idx1 ++)  {
                if (chopsTop.mlistChops.get(idx1).mnWidth > nMaxTopChildWidth)   {
                    nMaxTopChildWidth = chopsTop.mlistChops.get(idx1).mnWidth;
                }
                if (chopsTop.mlistChops.get(idx1).getRightP1InOriginalImg() <= chopBottom.getLeftInOriginalImg()) {
                    nTopOverheadLeft ++;
                } else if (chopsTop.mlistChops.get(idx1).getLeftInOriginalImg() >= chopBottom.getRightP1InOriginalImg()) {
                    nTopOverheadRight ++;
                }
            }
            int nMaxBottomChildWidth = 0;
            int nBottomOverheadLeft = 0, nBottomOverheadRight = 0;
            for (int idx1 = 0; idx1 < chopsBottom.mlistChops.size(); idx1 ++)  {
                if (chopsBottom.mlistChops.get(idx1).mnWidth > nMaxBottomChildWidth)   {
                    nMaxBottomChildWidth = chopsBottom.mlistChops.get(idx1).mnWidth;
                }
                if (chopsBottom.mlistChops.get(idx1).getRightP1InOriginalImg() <= chopTop.getLeftInOriginalImg()) {
                    nBottomOverheadLeft ++;
                } else if (chopsBottom.mlistChops.get(idx1).getLeftInOriginalImg() >= chopTop.getRightP1InOriginalImg()) {
                    nBottomOverheadRight ++;
                }
            }
            double dTopMid = (chopTop.getLeftInOriginalImg() + chopTop.getRightInOriginalImg())/2.0;
            double dBottomMid = (chopBottom.getLeftInOriginalImg() + chopBottom.getRightInOriginalImg())/2.0;
            if ((dTopMid > chopBottom.getRightInOriginalImg() || dTopMid < chopBottom.getLeftInOriginalImg())
                    && (dBottomMid > chopTop.getRightInOriginalImg() || dBottomMid < chopTop.getLeftInOriginalImg()))  {
                // top and bottom do not overlap well, merge
                return MERGE_H_DIV_STYLE;
            } else if (((dTopMid > chopBottom.getRightInOriginalImg() || dTopMid < chopBottom.getLeftInOriginalImg())
                        || (dBottomMid > chopTop.getRightInOriginalImg() || dBottomMid < chopTop.getLeftInOriginalImg()))
                    && Math.abs(chopTop.getLeftInOriginalImg() - chopBottom.getLeftInOriginalImg()) >= (Math.max(chopsTop.mlistChops.getFirst().mnWidth, chopsBottom.mlistChops.getFirst().mnWidth) * 2/3.0)) {
                // top and bottom are not well-aligned, and they are too close, merge (well aligned means either top and bottom are left aligned or they are central aligned)
                return MERGE_H_DIV_STYLE;
            } else if (2 * dAvgCharHeightTop < nOverlapRightPlus1 - nOverlapLeft    // have to ensure that overlap includes more than 1 chars
                        && 2 * dAvgCharHeightBottom < nOverlapRightPlus1 - nOverlapLeft
                        && (nTopOnCntsInOverlap < ConstantsMgr.msdEquationProjHOnCntsRatio * nBottomOnCntsInOverlap
                            || nBottomOnCntsInOverlap < ConstantsMgr.msdEquationProjHOnCntsRatio * nTopOnCntsInOverlap)) {
                // if density of the two chops are significantly different.
                return MERGE_H_DIV_STYLE;
            } else if (chopTop.mnHeight < ConstantsMgr.msdCapUnderHeightRatio2Base * 2 * dBaseAvgCharHeight
                    && dAvgCharHeightTop < ConstantsMgr.msdCapUnderHeightRatio2Base * dAvgCharHeightBottom    
                    && nTopOverheadLeft <= ConstantsMgr.msdMaxCapLeftOverhead
                    && ((nTopOverheadLeft > 0)?((nOverlapLeft - nNewLeft) < ConstantsMgr.msdOverhead2HeightMaxRatio * chopBottom.mnHeight):true)
                    && nTopOverheadRight <= ConstantsMgr.msdMaxCapRightOverhead
                    && ((nTopOverheadRight > 0)?((nNewRightPlus1 - nOverlapRightPlus1) < ConstantsMgr.msdOverhead2HeightMaxRatio * chopBottom.mnHeight):true))    {
                // top one are small chars and bottom one significantly larger and top is not significantly overhead bottom, seems that it is a cap.
                if (nMaxBottomChildWidth == chopBottom.mnWidth)    {
                    // This means bottom is a single char or surrounded by a single char (like sqrt).
                    return CAP_H_DIV_STYLE;                    
                } else if (nMaxTopChildWidth == chopTop.mnWidth)   {
                    // bottom is not a single char, top is a single char (or surrounded by a single char)
                    boolean bCoverEachBotC = true;
                    // make sure cap cover each bottom child.
                    for (int idx1 = 0; idx1 < chopsBottom.mlistChops.size(); idx1 ++)  {
                        ImageChop chopThisBotC = chopsBottom.mlistChops.get(idx1);
                        int nOverlap = Math.min(chopTop.getRightP1InOriginalImg(), chopThisBotC.getRightP1InOriginalImg())
                                - Math.max(chopTop.getLeftInOriginalImg(), chopThisBotC.getLeftInOriginalImg());
                        if (nOverlap <= 0)    {
                            bCoverEachBotC = false;
                            break;
                        }
                    }
                    if (bCoverEachBotC) {
                        return CAP_H_DIV_STYLE;
                    }
                } else if (nTopOnCntsInOverlap >= nBottomOnCntsInOverlap
                        && nTopOnCntsInOverlap < calcWorstCaseLnDivOrOnLen(chopBottom.mnWidth, chopBottom.mnHeight) //this condition means that top on cnts smaller than min or on for a line div so it cannot be a line div.
                        && chopTop.mnHeight > Math.max(2 * dAvgStrokeWidth, nGap)// so it is not like a long bar or several long bars. Use 2 here cause we consider two long bars which are not well aligned. Donot use slope coz it overestimates.
                        && nGap >= ConstantsMgr.msdMinBlankHDivDistance * dBaseAvgCharHeight) {
                    // for the situation where integrate on bottom, top is a line of irrelavant text.
                    return BLANK_H_DIV_STYLE;
                }
                return MERGE_H_DIV_STYLE;
            } else if (chopBottom.mnHeight < ConstantsMgr.msdCapUnderHeightRatio2Base * 2 * dBaseAvgCharHeight
                    && dAvgCharHeightBottom < ConstantsMgr.msdCapUnderHeightRatio2Base * dAvgCharHeightTop    
                    && nBottomOverheadLeft <= ConstantsMgr.msdMaxUnderLeftOverhead
                    && ((nBottomOverheadLeft > 0)?((nOverlapLeft - nNewLeft) < ConstantsMgr.msdOverhead2HeightMaxRatio * chopTop.mnHeight):true)
                    && nBottomOverheadRight <= ConstantsMgr.msdMaxUnderRightOverhead
                    && ((nBottomOverheadRight > 0)?((nNewRightPlus1 - nOverlapRightPlus1) < ConstantsMgr.msdOverhead2HeightMaxRatio * chopTop.mnHeight):true))    {
                // bottom one are small chars and top one significantly larger and bottom is not significantly overhead top, seems that it is a under.
                if (nMaxTopChildWidth == chopTop.mnWidth) {
                    // This means top is a single char or surrounded by a single char (like sqrt)
                    return UNDER_H_DIV_STYLE;                    
                } else if (nMaxBottomChildWidth == chopBottom.mnWidth)   {
                    // top is not a single char, bottom is a single char (or surrounded by a single char)
                    boolean bCoverEachTopC = true;
                    // make sure under cover each top child.
                    for (int idx1 = 0; idx1 < chopsTop.mlistChops.size(); idx1 ++)  {
                        ImageChop chopThisTopC = chopsTop.mlistChops.get(idx1);
                        int nOverlap = Math.min(chopBottom.getRightP1InOriginalImg(), chopThisTopC.getRightP1InOriginalImg())
                                - Math.max(chopBottom.getLeftInOriginalImg(), chopThisTopC.getLeftInOriginalImg());
                        if (nOverlap <= 0)    {
                            bCoverEachTopC = false;
                            break;
                        }
                    }
                    if (bCoverEachTopC) {
                        return UNDER_H_DIV_STYLE;
                    }
                } else if (nTopOnCntsInOverlap <= nBottomOnCntsInOverlap
                        && nBottomOnCntsInOverlap < calcWorstCaseLnDivOrOnLen(chopTop.mnWidth, chopTop.mnHeight) //this condition means that bottom on cnts smaller than min or on for a line div so it cannot be a line div.
                        && chopBottom.mnHeight > Math.max(2 * dAvgStrokeWidth, nGap)// so it is not like a long bar or several long bars. Use 2 here cause we consider two long bars which are not well aligned. Donot use slope coz it overestimates.
                        && nGap >= ConstantsMgr.msdMinBlankHDivDistance * dBaseAvgCharHeight) {
                    // for the situation where integrate on top, bottom is a line of irrelavant text.
                    return BLANK_H_DIV_STYLE;
                } else if (dAvgCharHeightTop * ConstantsMgr.msdLimWToCharHThresh > chopTop.mnWidth
                            && dAvgCharHeightTop * 2 > chopTop.mnHeight)    {
                    // lim's width is shorter than avg height top * msdLimWToCharHThresh
                    //chopTop height should not be too large otherwise it is two lines.
                    // very likely it is lim. Even if it is not lim, it is still a small piece of phase
                    // and should be under h div.
                    return BLANK_H_DIV_STYLE;
                }
                return MERGE_H_DIV_STYLE;   // have to merge because this is unders for several chars or the under cannot cover base. 
            } else  {
                // gap between cap and bottom is very narrow but font size is similar.
                // there are two possibilities:
                // 1. two expressions are very close to each other, like
                // a + b
                // c - d
                // a+ b is very close to c - d, we have to use H-div-cut
                // 2. like x = a/b, a is a cut and x = / is a cut and b is a cut, we have to identify what's below a. If it is a -, then
                // return Merge-DIVCut.
                boolean bIsLnDiv = false;
                ImageChop chopClosest = null;
                // step 1. find out the closest piece
                if (chopTop.getLeftInOriginalImg() >= chopBottom.getLeftInOriginalImg()
                        && chopTop.getRightP1InOriginalImg() <= chopBottom.getRightP1InOriginalImg())   {
                    // bottom is wider than top
                    int idxClosest2Top = 0;
                    int nClosest2TopPieceGap = Integer.MAX_VALUE;
                    for (int idxB = 0; idxB < chopsBottom.mlistChops.size(); idxB ++)    {
                        ImageChop chopBottomChild = chopsBottom.mlistChops.get(idxB);
                        if (chopTop.getLeftInOriginalImg() > chopBottomChild.getRightP1InOriginalImg()
                                || chopTop.getRightP1InOriginalImg() < chopBottomChild.getLeftInOriginalImg())  {
                            // no overlap
                            continue;
                        } else  {
                            int nTopPieceGap = (chopBottomChild.getTopInOriginalImg() - chopTop.getBottomP1InOriginalImg());
                            if (nTopPieceGap < nClosest2TopPieceGap)    {
                                nClosest2TopPieceGap = nTopPieceGap;
                                idxClosest2Top = idxB;
                            }
                        }
                    }
                    if (nClosest2TopPieceGap <= ConstantsMgr.msdExpressionGap * dBaseAvgCharHeight
                            && chopTop.getLeftInOriginalImg() >= chopsBottom.mlistChops.get(idxClosest2Top).getLeftInOriginalImg()
                            && chopTop.getRightP1InOriginalImg() <= chopsBottom.mlistChops.get(idxClosest2Top).getRightP1InOriginalImg())   {
                        chopClosest = chopsBottom.mlistChops.get(idxClosest2Top);
                    }
                } else if (chopTop.getLeftInOriginalImg() <= chopBottom.getLeftInOriginalImg()
                        && chopTop.getRightP1InOriginalImg() >= chopBottom.getRightP1InOriginalImg())    {
                    // top is wider than bottom
                    int idxClosest2Bottom = 0;
                    int nClosest2BottomPieceGap = Integer.MAX_VALUE;
                    for (int idxT = 0; idxT < chopsTop.mlistChops.size(); idxT ++)    {
                        ImageChop chopTopChild = chopsTop.mlistChops.get(idxT);
                        if (chopBottom.getLeftInOriginalImg() > chopTopChild.getRightP1InOriginalImg()
                                || chopBottom.getRightP1InOriginalImg() < chopTopChild.getLeftInOriginalImg())  {
                            // no overlap
                            continue;
                        } else  {
                            int nBottomPieceGap = (chopBottom.getTopInOriginalImg() - chopTopChild.getBottomP1InOriginalImg());
                            if (nBottomPieceGap < nClosest2BottomPieceGap)    {
                                nClosest2BottomPieceGap = nBottomPieceGap;
                                idxClosest2Bottom = idxT;
                            }
                        }
                    }
                    if (nClosest2BottomPieceGap <= ConstantsMgr.msdExpressionGap * dBaseAvgCharHeight
                            && chopBottom.getLeftInOriginalImg() >= chopsTop.mlistChops.get(idxClosest2Bottom).getLeftInOriginalImg()
                            && chopBottom.getRightP1InOriginalImg() <= chopsTop.mlistChops.get(idxClosest2Bottom).getRightP1InOriginalImg())   {
                        chopClosest = chopsTop.mlistChops.get(idxClosest2Bottom);
                    }
                }
                if (chopClosest != null)  {
                    // closest overlap piece
                    int nWorstCaseLineDivOrOnLen = calcWorstCaseLnDivOrOnLen(chopClosest.mnWidth, chopClosest.mnHeight);  // Line div width should be long enough
                    int nWorstCaseLineDiv1RowOnLen = calcWorstCaseLnDiv1RowOnLen(dAvgStrokeWidth, nWorstCaseLineDivOrOnLen);
                    int[] narrayRowOnValues = new int[chopClosest.mnHeight];
                    int[] narrayColOrOnValues = new int[chopClosest.mnWidth];
                    int nStartLnDivRow = -1, nEndLnDivRow = -1;
                    int nTotalOrOnCnts = 0;
                    
                    int nRowStartFrom = chopClosest.getTopInOriginalImg();
                    int nRowEndTo = chopClosest.getBottomP1InOriginalImg();
                    int nRowChangeStep = 1;
                    if (chopTop.mnWidth > chopBottom.mnWidth)  {
                        nRowStartFrom = chopClosest.getBottomInOriginalImg();
                        nRowEndTo = chopClosest.getTopInOriginalImg() - 1;
                        nRowChangeStep = -1;
                    }
                    int nChopClosestRightP1 = chopClosest.getRightPlus1();
                    for (int idxRow = nRowStartFrom; idxRow != nRowEndTo; idxRow += nRowChangeStep)  {
                        int nRowInThis = chopClosest.mapOriginalYIdx2This(idxRow), nRowInConfined = chopClosest.mapOriginalYIdx2Confined(idxRow);
                        for (int idxCol = chopClosest.mnLeft; idxCol < nChopClosestRightP1; idxCol ++)    {
                            if (chopClosest.mbarrayImg[idxCol][nRowInThis] == 1)    {
                                narrayRowOnValues[nRowInConfined] += 1;
                                if (nStartLnDivRow != -1) {
                                    narrayColOrOnValues[idxCol - chopClosest.mnLeft] = 1;
                                }
                            }
                        }
                        if (narrayRowOnValues[nRowInConfined] > nWorstCaseLineDiv1RowOnLen)   {
                            if (nStartLnDivRow == -1)   {
                                // could be a line div.
                                nStartLnDivRow = idxRow;
                                for (int idxCol = chopClosest.mnLeft; idxCol < chopClosest.getRightPlus1(); idxCol ++)    {
                                    // we have started a line div, but this row hasn't been recorded
                                    narrayColOrOnValues[idxCol - chopClosest.mnLeft] |= chopClosest.mbarrayImg[idxCol][nRowInThis];
                                }
                            }
                        } else if (nStartLnDivRow != -1 && nEndLnDivRow == -1)    {
                            // we have started a line div. Now finish it. Actually we should exclude this row from narrayColOrOnValues counting. But without excluding it is still fine.
                            nEndLnDivRow = idxRow;
                            break;
                        }
                    }
                    int nOrOnStartCol = narrayColOrOnValues.length, nOrOnEndCol = -1;
                    for (int idx1 = 0; idx1 < narrayColOrOnValues.length; idx1 ++)  {
                        if (narrayColOrOnValues[idx1] == 1) {
                            if (nOrOnStartCol > idx1)  {
                                nOrOnStartCol = idx1;
                            }
                            if (nOrOnEndCol < idx1) {
                                nOrOnEndCol = idx1;
                            }
                        }
                        nTotalOrOnCnts += narrayColOrOnValues[idx1];
                    }
                    if (nTotalOrOnCnts > nWorstCaseLineDivOrOnLen && (nOrOnEndCol + 1 - nOrOnStartCol) == nTotalOrOnCnts)   {
                        // continous line long enough, it can be a line div.
                        bIsLnDiv = true;
                    }
                }
                if (bIsLnDiv)   {
                    // it is a line div, but only cover part of the expressions, so merge first.
                    return MERGE_H_DIV_STYLE;
                } else  {
                    return BLANK_H_DIV_STYLE;
                }
            }
        } else  {   // ConstantsMgr.msdExpressionGap * dAvgCharHeight >= nGap >= ConstantsMgr.msdBaseCapUnderDistance * dAvgCharHeight
            // gap is not very wide, not very narrow, have to identify depends on the further cut the larger part. This is to handle the
            // situation like a + b/c where b is much above a + so that there is a gap between a + and b.
            if (chopTop.mnWidth < chopBottom.mnWidth && chopTop.mnChopType == ImageChop.TYPE_UNKNOWN)   {
                ImageChops imgChopsBottom = cutVerticallyProj(chopBottom, dAvgStrokeWidth);
                if (imgChopsBottom.mlistChops.size() == 1 
                        && (imgChopsBottom.mlistChops.getFirst().mnWidth / (double)imgChopsBottom.mlistChops.getFirst().mnHeight
                                <= ConstantsMgr.msdExtendableCharWOverHThresh
                            || imgChopsBottom.mlistChops.getFirst().mnHeight > 3 * dAvgStrokeWidth
                            || idxThis == listChops.size() - 1
                            || nGap >= ConstantsMgr.msdLnDiv2TopUnderGapGeneralMax * dBaseAvgCharHeight))  {
                    // cannot further v-cut and it is not a h cut-line
                    return BLANK_H_DIV_STYLE;
                }
                int nTopLeft = chopTop.getLeftInOriginalImg(), nTopRightP1 = chopTop.getRightP1InOriginalImg();
                int idx = 0;
                for (; idx < imgChopsBottom.mlistChops.size(); idx ++)   {
                    int nBottomPartLeft = imgChopsBottom.mlistChops.get(idx).getLeftInOriginalImg();
                    int nBottomPartRightP1 = imgChopsBottom.mlistChops.get(idx).getRightP1InOriginalImg();
                    if (Math.abs(nTopLeft - nBottomPartLeft) <= (1-ConstantsMgr.msdHLnDivMinWidthHandwriting) * chopTop.mnWidth
                            && Math.abs(nTopRightP1 - nBottomPartRightP1) <= (1-ConstantsMgr.msdHLnDivMinWidthHandwriting) * chopTop.mnWidth
                            && (imgChopsBottom.mlistChops.get(idx).getTopInOriginalImg() - chopTop.getBottomP1InOriginalImg())
                                <= chopTop.mnHeight * ConstantsMgr.msdHLnDivMaxDistanceToTopUnder
                            && imgChopsBottom.mlistChops.get(idx).getTopInOriginalImg() > chopBottom.getTopInOriginalImg())   {
                        break;  // could be a hdiv cut.
                    } 
                }
                if (idx >= imgChopsBottom.mlistChops.size())    {
                    if (idxThis < listChops.size() - 1 && nTopOnCntsInOverlap <= nBottomOnCntsInOverlap
                            && chopBottom.mnHeight <= ConstantsMgr.msdVeryThinOverlappedHeightThresh * dAvgStrokeWidth) {
                        // bottom is not the last one (otherwise it could be noise), and it's on count is large (implies it is made up of lines)
                        // and bottom is very thin, very likely it is made up of several disconnected lines. This implies that they belong to above. So we merge
                        return MERGE_H_DIV_STYLE;
                    } else if (nGap < ConstantsMgr.msdBaseCapUnderExtDistance * dBaseAvgCharHeight
                                && dAvgCharHeightTop >= ConstantsMgr.msdLnDivAvgCharHeightTop2BtmMin * dAvgCharHeightBottom
                                && dAvgCharHeightBottom >= ConstantsMgr.msdLnDivAvgCharHeightTop2BtmMin * dAvgCharHeightTop
                                && 2 * dAvgCharHeightTop < nOverlapRightPlus1 - nOverlapLeft    // have to ensure that overlap includes more than 1 chars
                                && 2 * dAvgCharHeightBottom < nOverlapRightPlus1 - nOverlapLeft) {
                        if (nTopOnCntsInOverlap < ConstantsMgr.msdEquationProjHOnCntsRatio * nBottomOnCntsInOverlap
                                && dAvgCharHeightTop <= listChops.getLast().getBottomP1InOriginalImg() - chopBottom.getTopInOriginalImg()) {
                            // if density of the two chops are significantly different. and the gap is still small.
                            // also need to make sure no noise on the edge : dAvgCharHeightTop < listChops.getLast().getBottomP1InOriginalImg() - chopBottom.getTopInOriginalImg()
                            // no noise on the edge means char height on the top less than total height on the bottom or vice versa.
                            return MERGE_H_DIV_STYLE;
                        } else if (nBottomOnCntsInOverlap < ConstantsMgr.msdEquationProjHOnCntsRatio * nTopOnCntsInOverlap 
                                && dAvgCharHeightBottom <= chopTop.getBottomP1InOriginalImg() - listChops.getFirst().getTopInOriginalImg())    {
                            // if density of the two chops are significantly different. and the gap is still small.
                            // also need to make sure no noise on the edge : dAvgCharHeightTop < listChops.getLast().getBottomP1InOriginalImg() - chopBottom.getTopInOriginalImg()
                            // no noise on the edge means char height on the top less than total height on the bottom or vice versa.
                            return MERGE_H_DIV_STYLE;
                        } else {
                            return BLANK_H_DIV_STYLE;
                        }
                    } else {
                        return BLANK_H_DIV_STYLE;
                    }
                }
                
                // now identify if its a h div cut.
                ImageChop imgChopBtmPart = imgChopsBottom.mlistChops.get(idx);
                dMaxEstCharWidth = Math.min(dMaxEstCharWidth, imgChopBtmPart.mnWidth);
                dMaxEstCharHeight = Math.min(dMaxEstCharHeight, imgChopBtmPart.mnHeight);
                ImageChops imgChopsBtmPart = cutHorizontallyProj(imgChopBtmPart, dAvgStrokeWidth, dMaxEstCharWidth, dMaxEstCharHeight);
                if (imgChopsBtmPart.mlistChops.size() == 1
                        && imgChopsBtmPart.mlistChops.get(0).mnHeight
                            <= (dAvgStrokeWidth + imgChopsBtmPart.mlistChops.get(0).mnWidth * ConstantsMgr.msdMaxHorizontalSlope))    {
                    // seems like a h-div cut.
                    return MERGE_H_DIV_STYLE;
                } else if ((imgChopsBtmPart.mlistChops.size() == 2
                            && imgChopsBtmPart.mlistChops.getFirst().mnChopType == ImageChop.TYPE_LINE_DIV
                            && imgChopsBtmPart.mlistChops.getLast().mnChopType == ImageChop.TYPE_UNKNOWN)
                        || (imgChopsBtmPart.mlistChops.size() == 3
                            && imgChopsBtmPart.mlistChops.getFirst().mnChopType == ImageChop.TYPE_LINE_DIV
                            && (imgChopsBtmPart.mlistChops.get(1).mnChopType == ImageChop.TYPE_BLANK_DIV
                                || imgChopsBtmPart.mlistChops.get(1).mnChopType == ImageChop.TYPE_CAP_DIV
                                || imgChopsBtmPart.mlistChops.get(1).mnChopType == ImageChop.TYPE_UNDER_DIV)
                            && imgChopsBtmPart.mlistChops.getLast().mnChopType == ImageChop.TYPE_UNKNOWN)) {
                    // seems like a h-div cut.
                    return MERGE_H_DIV_STYLE;
                }
            } else if (chopTop.mnWidth > chopBottom.mnWidth && chopBottom.mnChopType == ImageChop.TYPE_UNKNOWN)    {
                ImageChops imgChopsTop = cutVerticallyProj(chopTop, dAvgStrokeWidth);
                if (imgChopsTop.mlistChops.size() == 1 
                        && (imgChopsTop.mlistChops.getFirst().mnWidth / (double)imgChopsTop.mlistChops.getFirst().mnHeight
                                <= ConstantsMgr.msdExtendableCharWOverHThresh
                            || imgChopsTop.mlistChops.getFirst().mnHeight > 3 * dAvgStrokeWidth
                            || idxThis == 1
                            || nGap >= ConstantsMgr.msdLnDiv2TopUnderGapGeneralMax * dBaseAvgCharHeight))  {
                    // cannot further v-cut and it is not a h cut-line
                    return BLANK_H_DIV_STYLE;
                }
                int nBottomLeft = chopBottom.getLeftInOriginalImg(), nBottomRightP1 = chopBottom.getRightP1InOriginalImg();
                int idx = 0;
                for (; idx < imgChopsTop.mlistChops.size(); idx ++)   {
                    int nTopPartLeft = imgChopsTop.mlistChops.get(idx).getLeftInOriginalImg();
                    int nTopPartRightP1 = imgChopsTop.mlistChops.get(idx).getRightP1InOriginalImg();
                    if (Math.abs(nBottomLeft - nTopPartLeft) <= (1-ConstantsMgr.msdHLnDivMinWidthHandwriting) * chopBottom.mnWidth
                            && Math.abs(nBottomRightP1 - nTopPartRightP1) <= (1-ConstantsMgr.msdHLnDivMinWidthHandwriting) * chopBottom.mnWidth
                            && (chopBottom.getTopInOriginalImg() - imgChopsTop.mlistChops.get(idx).getBottomP1InOriginalImg())
                                <= chopBottom.mnHeight * ConstantsMgr.msdHLnDivMaxDistanceToTopUnder
                            && imgChopsTop.mlistChops.get(idx).getBottomP1InOriginalImg() < chopTop.getBottomP1InOriginalImg())   {
                        break;  // could be a hdiv cut.
                    } 
                }
                if (idx >= imgChopsTop.mlistChops.size())    {
                    if (nTopOnCntsInOverlap >= nBottomOnCntsInOverlap &&
                            chopTop.mnHeight <= ConstantsMgr.msdVeryThinOverlappedHeightThresh * dAvgStrokeWidth) {
                        // top is very thin, and on count is large (implying it is made up of lines) very likely it is made up of several disconnected lines. This implies that they belong to below. So we merge
                        return MERGE_H_DIV_STYLE;
                    } else if (nGap < ConstantsMgr.msdBaseCapUnderExtDistance * dBaseAvgCharHeight
                                && dAvgCharHeightTop >= ConstantsMgr.msdLnDivAvgCharHeightTop2BtmMin * dAvgCharHeightBottom
                                && dAvgCharHeightBottom >= ConstantsMgr.msdLnDivAvgCharHeightTop2BtmMin * dAvgCharHeightTop
                                && 2 * dAvgCharHeightTop < nOverlapRightPlus1 - nOverlapLeft    // have to ensure that overlap includes more than 1 chars
                                && 2 * dAvgCharHeightBottom < nOverlapRightPlus1 - nOverlapLeft) {
                        if (nTopOnCntsInOverlap < ConstantsMgr.msdEquationProjHOnCntsRatio * nBottomOnCntsInOverlap
                                && dAvgCharHeightTop <= listChops.getLast().getBottomP1InOriginalImg() - chopBottom.getTopInOriginalImg()) {
                            // if density of the two chops are significantly different. and the gap is still small.
                            // also need to make sure no noise on the edge : dAvgCharHeightTop < listChops.getLast().getBottomP1InOriginalImg() - chopBottom.getTopInOriginalImg()
                            // no noise on the edge means char height on the top less than total height on the bottom or vice versa.
                            return MERGE_H_DIV_STYLE;
                        } else if (nBottomOnCntsInOverlap < ConstantsMgr.msdEquationProjHOnCntsRatio * nTopOnCntsInOverlap 
                                && dAvgCharHeightBottom <= chopTop.getBottomP1InOriginalImg() - listChops.getFirst().getTopInOriginalImg())    {
                            // if density of the two chops are significantly different. and the gap is still small.
                            // also need to make sure no noise on the edge : dAvgCharHeightTop < listChops.getLast().getBottomP1InOriginalImg() - chopBottom.getTopInOriginalImg()
                            // no noise on the edge means char height on the top less than total height on the bottom or vice versa.
                            return MERGE_H_DIV_STYLE;
                        } else {
                            return BLANK_H_DIV_STYLE;
                        }
                    } else {
                        return BLANK_H_DIV_STYLE;
                    }
                }
                
                // now identify if its a h div cut.
                ImageChop imgChopTopPart = imgChopsTop.mlistChops.get(idx);
                dMaxEstCharWidth = Math.min(dMaxEstCharWidth, imgChopTopPart.mnWidth);
                dMaxEstCharHeight = Math.min(dMaxEstCharHeight, imgChopTopPart.mnHeight);
                ImageChops imgChopsTopPart = cutHorizontallyProj(imgChopTopPart, dAvgStrokeWidth, dMaxEstCharWidth, dMaxEstCharHeight);
                if (imgChopsTopPart.mlistChops.size() == 1
                        && imgChopsTopPart.mlistChops.get(0).mnHeight
                            <= (dAvgStrokeWidth + imgChopsTopPart.mlistChops.get(0).mnWidth * ConstantsMgr.msdMaxHorizontalSlope))    {
                    // seems like a h-div cut.
                    return MERGE_H_DIV_STYLE;
                } else if ((imgChopsTopPart.mlistChops.size() == 2
                            && imgChopsTopPart.mlistChops.getFirst().mnChopType == ImageChop.TYPE_UNKNOWN
                            && imgChopsTopPart.mlistChops.getLast().mnChopType == ImageChop.TYPE_LINE_DIV)
                        || (imgChopsTopPart.mlistChops.size() == 3
                            && imgChopsTopPart.mlistChops.getFirst().mnChopType == ImageChop.TYPE_UNKNOWN
                            && (imgChopsTopPart.mlistChops.get(1).mnChopType == ImageChop.TYPE_BLANK_DIV
                                    || imgChopsTopPart.mlistChops.get(1).mnChopType == ImageChop.TYPE_CAP_DIV
                                    || imgChopsTopPart.mlistChops.get(1).mnChopType == ImageChop.TYPE_UNDER_DIV)
                            && imgChopsTopPart.mlistChops.getLast().mnChopType == ImageChop.TYPE_LINE_DIV))  {
                    // seems like a h-div cut.
                    return MERGE_H_DIV_STYLE;
                }
            }
        }
        
        return BLANK_H_DIV_STYLE;   // other possibilities are all divided by blank div.
    }


    // This function calculate min gap between top and bottom in the selected range.
    public static int calcMinGapOverHRange(ImageChops chopsTop, ImageChops chopsBtm, int nLeft, int nRightP1) {
        if (nLeft >= nRightP1)  {
            return -1;  // invalid range.
        }
        int nBottomP1OfTop = -1, nTopOfBottom = Integer.MAX_VALUE;
        for (int idx = 0; idx < chopsTop.mlistChops.size(); idx ++) {
            ImageChop chopThis = chopsTop.mlistChops.get(idx);
            if (!(chopThis.getLeftInOriginalImg() >= nRightP1 || chopThis.getRightP1InOriginalImg() <= nLeft)) {
                if (chopThis.getBottomP1InOriginalImg() > nBottomP1OfTop) {
                    nBottomP1OfTop = chopThis.getBottomP1InOriginalImg();
                }
            }
        }
        
        for (int idx = 0; idx < chopsBtm.mlistChops.size(); idx ++) {
            ImageChop chopThis = chopsBtm.mlistChops.get(idx);
            if (!(chopThis.getLeftInOriginalImg() >= nRightP1 || chopThis.getRightP1InOriginalImg() <= nLeft)) {
                if (chopThis.getTopInOriginalImg() < nTopOfBottom) {
                    nTopOfBottom = chopThis.getTopInOriginalImg();
                }
            }
        }
        
        if (nBottomP1OfTop == -1 || nTopOfBottom == Integer.MAX_VALUE) {
            // no chop found in top or bottom.
            return -1;
        } else if (nTopOfBottom < nBottomP1OfTop) {
            return 0;   // no gap.
        } else {
            return nTopOfBottom - nBottomP1OfTop;
        }
    }

}
