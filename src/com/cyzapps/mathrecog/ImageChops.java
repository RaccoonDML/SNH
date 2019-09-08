/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyzapps.mathrecog;

import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 * @author tonyc
 */
public class ImageChops {
    public LinkedList<ImageChop> mlistChops = new LinkedList<ImageChop>();	// cops
    
    // Do not extractConnectedPieces again for the chops, assume they have been extracted. We can either use a single imgchop
    // or a list of imgchops, or both to compare
    public double[] calcAvgWeightedHCharHeight(ImageChops imgChopsOverlap, ImageChop imgChopOverlap, double dAvgStrokeWidth) {
        double dAvgHeight = 0, dAvgWeight = 0;
        int nLeftThresh = 0, nRightP1Thresh = 0;
        if (imgChopOverlap != null) {
            nLeftThresh = imgChopOverlap.getLeftInOriginalImg();
            nRightP1Thresh = imgChopOverlap.getRightP1InOriginalImg();
        } else if (imgChopsOverlap != null) {
            for (int idx3 = 0; idx3 < imgChopsOverlap.mlistChops.size(); idx3 ++)  {
                if (nLeftThresh > imgChopsOverlap.mlistChops.get(idx3).getLeftInOriginalImg()) {
                    nLeftThresh = imgChopsOverlap.mlistChops.get(idx3).getLeftInOriginalImg();
                }
                if (nRightP1Thresh < imgChopsOverlap.mlistChops.get(idx3).getRightP1InOriginalImg()) {
                    nRightP1Thresh = imgChopsOverlap.mlistChops.get(idx3).getRightP1InOriginalImg();
                }
            }
        }
        int[] narrayHeights = new int[mlistChops.size()];
        double[] darrayWeights = new double[mlistChops.size()];
        /*double dAvgThisWidth = 0;
        for (int idx = 0; idx < mlistChops.size(); idx ++) {
            dAvgThisWidth += mlistChops.get(idx).mnWidth;
        }
        if (imgChopsOverlap != null && imgChopsOverlap.mlistChops.size() > 0) {
            dAvgThisWidth =  imgChopOverlap.mnWidth/(double)imgChopsOverlap.mlistChops.size();
        }*/
        for (int idx = 0; idx < mlistChops.size(); idx ++) {
            int nThisWidth = mlistChops.get(idx).mnWidth;
            int nThisHeight = mlistChops.get(idx).mnHeight;
            if (nThisHeight >= ConstantsMgr.msnMinCharHeightInUnit && nThisHeight >= dAvgStrokeWidth && nThisWidth > 0) {
                // a seperated point or a disconnected stroke may significantly drag down dAvgHeight value.
                double dWeight = 0;
                int nThisLeft = mlistChops.get(idx).getLeftInOriginalImg(), nThisRightP1 = mlistChops.get(idx).getRightP1InOriginalImg();
                double dThisMid = (nThisLeft + nThisRightP1)/2.0;
                if ((dThisMid <= nRightP1Thresh && dThisMid >= nLeftThresh) // if the mid of this child is between left right range
                        || (nThisLeft <= nLeftThresh && nThisRightP1 >= nRightP1Thresh)) {  // or this child covers left right range.
                    dWeight = 1;
                } else {
                    double dThisMidToEdge = Math.max(dThisMid - nRightP1Thresh, nLeftThresh - dThisMid);
                    if (dThisMidToEdge > nThisWidth * 1.5) {    // edge of this to edge of range should be less than 1 char width
                        continue;   // too far from the overlapping range, so no effect taken into account.
                    }
                    dWeight = Math.pow(ConstantsMgr.msdHeightEffectFadingConst, dThisMidToEdge/dAvgStrokeWidth);
                }

                dAvgHeight += nThisHeight * dWeight;
                dAvgWeight += dWeight;
                narrayHeights[idx] = nThisHeight;
                darrayWeights[idx] = dWeight;
            }
        }
        if (dAvgWeight != 0) {
            dAvgHeight /= dAvgWeight;
        }
        
        // now remove too tall chars like [, (, ), ] and too small chars, eg dot or line.
        double dThreshU = 2 * dAvgHeight; // to make it faster, do not use standard deviation.
        double dThreshL = 2 * dAvgStrokeWidth;  // a dot's height in general should be no bigger than 2 average stroke width
        double dAvgNormalCharHeight = 0, dAvgNormalCharWeight = 0;
        for (int idx = 0; idx < mlistChops.size(); idx ++) {
            if (narrayHeights[idx] <= dThreshU && narrayHeights[idx] >= dThreshL) {
                dAvgNormalCharHeight += narrayHeights[idx] * darrayWeights[idx];
                dAvgNormalCharWeight += darrayWeights[idx];
            }
        }
        if (dAvgNormalCharWeight != 0) {
            dAvgNormalCharHeight /= dAvgNormalCharWeight;
        }
        
        double[] darrayReturn = new double[2];
        darrayReturn[0] = Math.max(dAvgHeight, Math.max(ConstantsMgr.msnMinCharHeightInUnit, dAvgStrokeWidth));
        darrayReturn[1] = Math.max(dAvgNormalCharHeight, Math.max(ConstantsMgr.msnMinCharHeightInUnit, dAvgStrokeWidth));
        return darrayReturn;
    }

    // Do not extractConnectedPieces again for the chops, assume they have been extracted.
    // project the image chop to a horizontal line and calculate how many on s are over the range from nleft to nRightP1
    // the left and nRightP1 are in original image.
    public int calcProjOnCntOverHRange(int nLeft, int nRightP1) {
        if (nLeft >= nRightP1)  {
            return -1;  // invalid range.
        }
        byte[] blistProjs = new byte[nRightP1 - nLeft]; // initial value is 0.
        for (int idx = 0; idx < mlistChops.size(); idx ++) {
            ImageChop chopThis = mlistChops.get(idx);
            for (int idx1 = Math.max(nLeft, chopThis.getLeftInOriginalImg());
                    idx1 < Math.min(nRightP1, chopThis.getRightP1InOriginalImg()); idx1 ++) {
                blistProjs[idx1 - nLeft] = 1;
            }
        }

        int nSumOn = 0;
        for (int idx = 0; idx < nRightP1 - nLeft; idx ++)   {
            nSumOn += blistProjs[idx];
        }
        return nSumOn;
    }
    
    // Do not extractConnectedPieces again for the chops, assume they have been extracted.
    // project the image chop to a horizontal line and calculate how many on s are over the range from nleft to nRightP1
    // the left and nRightP1 are in original image.
    public int calcProjOnCntAvgTopBP1OverHRange(int nLeft, int nRightP1, double[] darrayATopBP1) {
        if (nLeft >= nRightP1)  {
            return -1;  // invalid range.
        }
        byte[] blistProjs = new byte[nRightP1 - nLeft]; // initial value is 0.
        int[] narrayTops = new int[nRightP1 - nLeft];
        Arrays.fill(narrayTops, Integer.MAX_VALUE);
        int[] narrayBottomP1s = new int[nRightP1 - nLeft];
        for (int idx = 0; idx < mlistChops.size(); idx ++) {
            ImageChop chopThis = mlistChops.get(idx);
            for (int idx1 = Math.max(nLeft, chopThis.getLeftInOriginalImg());
                    idx1 < Math.min(nRightP1, chopThis.getRightP1InOriginalImg()); idx1 ++) {
                blistProjs[idx1 - nLeft] = 1;
                int nTop = chopThis.getTopInOriginalImg();
                if (narrayTops[idx1 - nLeft] > nTop) {
                    narrayTops[idx1 - nLeft] = nTop;
                }
                int nBottomP1 = chopThis.getBottomP1InOriginalImg();
                if (narrayBottomP1s[idx1 - nLeft] < nBottomP1) {
                    narrayBottomP1s[idx1 - nLeft] = nBottomP1;
                }
            }
        }

        int nSumOn = 0;
        double dSumTop = 0, dSumBottomP1 = 0;
        for (int idx = 0; idx < nRightP1 - nLeft; idx ++)   {
            if (blistProjs[idx] == 1) {
                nSumOn += blistProjs[idx];
                dSumTop += narrayTops[idx];
                dSumBottomP1 += narrayBottomP1s[idx];
            }
        }
        if (nSumOn > 0) {
            darrayATopBP1[0] = dSumTop/nSumOn;
            darrayATopBP1[1] = dSumBottomP1/nSumOn;
        }
        return nSumOn;
    }

    // Do not extractConnectedPieces again for the chops, assume they have been extracted.
    // project the image chop to a horizontal line and calculate how many on s are over the range from nTop to nBottomP1
    // the left and nRightP1 are in original image.
    public int calcProjOnCntOverVRange(int nTop, int nBottomP1) {
        if (nTop >= nBottomP1)  {
            return -1;  // invalid range.
        }
        byte[] blistProjs = new byte[nBottomP1 - nTop]; // initial value is 0.
        for (int idx = 0; idx < mlistChops.size(); idx ++) {
            ImageChop chopThis = mlistChops.get(idx);
            for (int idx1 = Math.max(nTop, chopThis.getTopInOriginalImg());
                    idx1 < Math.min(nBottomP1, chopThis.getBottomP1InOriginalImg()); idx1 ++) {
                blistProjs[idx1 - nTop] = 1;
            }
        }

        int nSumOn = 0;
        for (int idx = 0; idx < nBottomP1 - nTop; idx ++)   {
            nSumOn += blistProjs[idx];
        }
        return nSumOn;
    }

    // Do not extractConnectedPieces again for the chops, assume they have been extracted.
    // project the image chop to a horizontal line and calculate how many on s are over the range from nTop to nBottomP1
    // the left and nRightP1 are in original image.
    public int calcProjOnCntAvgLeftRP1OverVRange(int nTop, int nBottomP1, double[] darrayALeftRP1) {
        if (nTop >= nBottomP1)  {
            return -1;  // invalid range.
        }
        byte[] blistProjs = new byte[nBottomP1 - nTop]; // initial value is 0.
        int[] narrayLefts = new int[nBottomP1 - nTop];
        Arrays.fill(narrayLefts, Integer.MAX_VALUE);
        int[] narrayRightP1s = new int[nBottomP1 - nTop];
        for (int idx = 0; idx < mlistChops.size(); idx ++) {
            ImageChop chopThis = mlistChops.get(idx);
            for (int idx1 = Math.max(nTop, chopThis.getTopInOriginalImg());
                    idx1 < Math.min(nBottomP1, chopThis.getBottomP1InOriginalImg()); idx1 ++) {
                blistProjs[idx1 - nTop] = 1;
                int nLeft = chopThis.getLeftInOriginalImg();
                if (narrayLefts[idx1 - nTop] > nLeft) {
                    narrayLefts[idx1 - nTop] = nLeft;
                }
                int nRightP1 = chopThis.getRightP1InOriginalImg();
                if (narrayRightP1s[idx1 - nTop] < nRightP1) {
                    narrayRightP1s[idx1 - nTop] = nRightP1;
                }
            }
        }

        int nSumOn = 0;
        double dSumLeft = 0, dSumRightP1 = 0;
        for (int idx = 0; idx < nBottomP1 - nTop; idx ++)   {
            if (blistProjs[idx] == 1) {
                nSumOn += blistProjs[idx];
                dSumLeft += narrayLefts[idx];
                dSumRightP1 += narrayRightP1s[idx];
            }
        }
        if (nSumOn > 0) {
            darrayALeftRP1[0] = dSumLeft/nSumOn;
            darrayALeftRP1[1] = dSumRightP1/nSumOn;
        }
        return nSumOn;
    }

}
