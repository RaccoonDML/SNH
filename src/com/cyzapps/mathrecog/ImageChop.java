/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyzapps.mathrecog;

/**
 *
 * @author tonyc
 */
public class ImageChop {
    public final static int TYPE_UNKNOWN = 0;
    public final static int TYPE_BLANK_DIV = 1;
    public final static int TYPE_LINE_DIV = 2;
    public final static int TYPE_CAP_DIV = 3;
    public final static int TYPE_UNDER_DIV = 4;

    public byte[][] mbarrayImg = null;	// a reference to the image.
    public int mnLeft;
    public int mnTop;
    public int mnWidth;
    public int mnHeight;
    public int getRight()	{ return mnLeft + mnWidth - 1; }
    public int getBottom()	{ return mnTop + mnHeight - 1; }
    public int getRightPlus1()  { return mnLeft + mnWidth; }
    public int getBottomPlus1() { return mnTop + mnHeight; }
    public int getLeftInOriginalImg()   { return mnX0InOriginalImg + mnLeft; }
    public int getTopInOriginalImg()    { return mnY0InOriginalImg + mnTop; }
    public int getRightInOriginalImg()  { return mnX0InOriginalImg + mnLeft + mnWidth - 1; }
    public int getBottomInOriginalImg() { return mnY0InOriginalImg + mnTop + mnHeight - 1; }
    public int getRightP1InOriginalImg()  { return mnX0InOriginalImg + mnLeft + mnWidth; }
    public int getBottomP1InOriginalImg() { return mnY0InOriginalImg + mnTop + mnHeight; }

    public int mapOriginalXIdx2This(int nXIdxInOriginalImg)  { return nXIdxInOriginalImg - mnX0InOriginalImg; }
    public int mapOriginalXIdx2Confined(int nXIdxInOriginalImg)  { return nXIdxInOriginalImg - mnX0InOriginalImg - mnLeft; }

    public int mapThisXIdx2Original(int nXIdx)  { return nXIdx + mnX0InOriginalImg; }
    public int mapThisXIdx2Confined(int nXIdx)  { return nXIdx - mnLeft; }

    public int mapConfinedXIdx2Original(int nXIdxInConfined)  { return nXIdxInConfined + mnLeft + mnX0InOriginalImg; }
    public int mapConfinedXIdx2This(int nXIdxInConfined)  { return nXIdxInConfined + mnLeft; }

    public int mapOriginalYIdx2This(int nYIdxInOriginalImg)  { return nYIdxInOriginalImg - mnY0InOriginalImg; }
    public int mapOriginalYIdx2Confined(int nYIdxInOriginalImg)  { return nYIdxInOriginalImg - mnY0InOriginalImg - mnTop; }

    public int mapThisYIdx2Original(int nYIdx)  { return nYIdx + mnY0InOriginalImg; }
    public int mapThisYIdx2Confined(int nYIdx)  { return nYIdx - mnTop; }

    public int mapConfinedYIdx2Original(int nYIdxInConfined)  { return nYIdxInConfined + mnTop + mnY0InOriginalImg; }
    public int mapConfinedYIdx2This(int nYIdxInConfined)  { return nYIdxInConfined + mnTop; }

    public byte[][] mbarrayOriginalImg = null;  // a reference to the very original image.
    public int mnX0InOriginalImg = 0; // index of left most of mbarrayImg (i.e. 0 idx of mbarrayImg) in original image.
    public int mnY0InOriginalImg = 0;  // index of top most of mbarrayImg (i.e. 0 idx of mbarrayImg) in original image.
    public int mnChopType = 0;	// chop types, 0 for top or bottom,
                                // 1 for blank area divisor
                                // 2 for line divisor
                                // 0 for unknown
    public boolean isEmptyImage()   {
        if (mbarrayImg == null || mbarrayImg.length == 0 || mbarrayImg[0].length == 0 || mnWidth == 0 || mnHeight == 0) {
            return true;
        }
        return false;
    }

    public void setImageChop(byte[][] barrayImg, int nLeft, int nTop, int nWidth, int nHeight, int nType)	{
        mbarrayOriginalImg = mbarrayImg = barrayImg;
        mnLeft = nLeft;
        mnTop = nTop;
        mnWidth = nWidth;
        mnHeight = nHeight;
        mnChopType = nType;
    }

    public void setImageChop(byte[][] barrayImg, int nLeft, int nTop, int nWidth, int nHeight, byte[][] barrayOriginalImg, int nX0InOriginalImg, int nY0InOriginalImg, int nType)	{
        mbarrayImg = barrayImg;
        mnLeft = nLeft;
        mnTop = nTop;
        mnWidth = nWidth;
        mnHeight = nHeight;
        mbarrayOriginalImg = barrayOriginalImg;
        mnX0InOriginalImg = nX0InOriginalImg;
        mnY0InOriginalImg = nY0InOriginalImg;
        mnChopType = nType;
    }

    public ImageChop shrinkImgArray()   {
        // this function is different from function convert2MinContainer. It does not necessarily return min container. It 
        // simply returns a image chop whose left and top = 0, and rightP1 and bottomP1 are width and height respectively.
        // And in order to save memory and accelerate the copying procedure, if this imagechop already has a shrinked image
        // array, this function returns this image itself.
        if (mnLeft == 0 && mnTop == 0 && getRightPlus1() == mnWidth && getBottomPlus1() == mnHeight)    {
            return this;
        } else  {
            byte[][] barrayImg = new byte[mnWidth][mnHeight];
            // copy matrix.
            for (int idx = 0; idx < mnWidth; idx ++)    {
                System.arraycopy(mbarrayImg[idx + mnLeft], mnTop, barrayImg[idx], 0, mnHeight);
            }
            int nX0InOriginalImg = mnX0InOriginalImg + mnLeft;
            int nY0InOriginalImg = mnY0InOriginalImg + mnTop;
            ImageChop imgChopReturn = new ImageChop();
            imgChopReturn.setImageChop(barrayImg, 0, 0, mnWidth, mnHeight, mbarrayOriginalImg, nX0InOriginalImg, nY0InOriginalImg, mnChopType);
            return imgChopReturn;
        }
    }
    
    public ImageChop convert2MinContainer()  {
        ImageChop imgChop = new ImageChop();
        int nNewTop = getBottomPlus1();
        int nNewBottom  = mnTop - 1;
        int nNewLeft = getRightPlus1();
        int nNewRight = mnLeft - 1;
        int nNewHeight, nNewWidth;

        for (int idx = mnTop; idx < getBottomPlus1(); idx ++)   {
            boolean bFound = false;
            for (int idx1 = mnLeft; idx1 < getRightPlus1(); idx1 ++)   {
                if (mbarrayImg[idx1][idx] == 1) {
                    nNewTop = idx;
                    nNewLeft = nNewRight = idx1;
                    bFound = true;
                    break;
                }
            }
            if (bFound)   {
                break;
            }
        }
        // stop here if no point found.
        if (nNewTop == getBottomPlus1())    {
            nNewHeight = nNewWidth = 0;
            nNewTop = mnTop;
            nNewLeft = mnLeft;
            imgChop.setImageChop(mbarrayImg, nNewLeft, nNewTop, nNewWidth, nNewHeight, mbarrayOriginalImg, mnX0InOriginalImg, mnY0InOriginalImg, mnChopType);
        } else  {
            for (int idx = getBottom(); idx >= mnTop; idx --)   {
                boolean bFound = false;
                for (int idx1 = getRight(); idx1 >= mnLeft; idx1 --)   {
                    if (mbarrayImg[idx1][idx] == 1) {
                        nNewBottom = idx;
                        if (idx1 < nNewLeft)    {
                            nNewLeft = idx1;
                        } else if (idx1 > nNewRight)    {
                            nNewRight = idx1;
                        }
                        bFound = true;
                        break;
                    }
                }
                if (bFound)   {
                    break;
                }
            }

            for (int idx1 = mnLeft; idx1 < nNewLeft; idx1 ++)   {
                boolean bFound = false;
                for (int idx = nNewTop + 1; idx <= nNewBottom; idx ++)   {
                    if (mbarrayImg[idx1][idx] == 1) {
                        nNewLeft = idx1;
                        bFound = true;
                        break;
                    }
                }
                if (bFound) {
                    break;
                }
            }

            for (int idx1 = getRight(); idx1 > nNewRight; idx1 --)    {
                boolean bFound = false;
                for (int idx = nNewBottom - 1; idx >= nNewTop; idx --)   {
                    if (mbarrayImg[idx1][idx] == 1) {
                        nNewRight = idx1;
                        bFound = true;
                        break;
                    }
                }
                if (bFound) {
                    break;
                }
            }

            nNewHeight = nNewBottom + 1 - nNewTop;
            nNewWidth = nNewRight + 1 - nNewLeft;

            imgChop.setImageChop(mbarrayImg, nNewLeft, nNewTop, nNewWidth, nNewHeight, mbarrayOriginalImg, mnX0InOriginalImg, mnY0InOriginalImg, mnChopType);
        }
        return imgChop;
    }

    public int getTotalOnCount()    {
        int nTotalOnCount = 0;
        for (int idx = 0; idx < mnHeight; idx ++)    {
            for (int idx1 = 0; idx1 < mnWidth; idx1 ++)  {
                if (mbarrayImg[idx1 + mnLeft][idx + mnTop] == 1)  {
                    nTotalOnCount ++;
                }
            }
        }
        return nTotalOnCount;
    }
    
    public int getTotalOnCountInSkeleton(boolean bConsiderStrokeEnd) throws InterruptedException  {
        // use findSkeletonIndexMethodOld instead of findSkeletonIndexMethod because Old includes less blurs.
        byte[][] biOutput = StrokeFinder.findSkeletonIndexMethod(mbarrayImg, mnLeft, mnTop, mnWidth, mnHeight);
        // do not smooth stroke here. Otherwise may over-estimate stroke width because many short strokes are removed.
        //byte[][] biOutput = StrokeFinder.smoothStroke(biOutput, 0, 0, mnWidth, mnHeight, 1);
        int nTotalOnCountInSkeleton = 0;
        int nTotalStrokeEnds = 0;
        for (int idx = 0; idx < mnHeight; idx ++)    {
            for (int idx1 = 0; idx1 < mnWidth; idx1 ++)  {
                if (biOutput[idx1][idx] == 1)  {
                    nTotalOnCountInSkeleton ++;
                    if (bConsiderStrokeEnd) {
                        /*int nNeighbourSum = ((idx1 == 0)?0:biOutput[idx1 - 1][idx])
                                + ((idx1 == 0 || idx == 0)?0:biOutput[idx1 - 1][idx - 1])
                                + ((idx1 == 0 || idx == (mnHeight - 1))?0:biOutput[idx1 - 1][idx + 1])
                                + ((idx1 == (mnWidth - 1))?0:biOutput[idx1 + 1][idx])
                                + ((idx1 == (mnWidth - 1) || idx == 0)?0:biOutput[idx1 + 1][idx - 1])
                                + ((idx1 == (mnWidth - 1) || idx == (mnHeight - 1))?0:biOutput[idx1 + 1][idx + 1])
                                + ((idx == 0)?0:biOutput[idx1][idx - 1])
                                + ((idx == (mnHeight - 1))?0:biOutput[idx1][idx + 1]);*/
                        if (idx1 > 0 && idx1 < (mnWidth - 1) && idx > 0 && idx < (mnHeight - 1))    {
                            // only if this point is not an edge point, we consider neighbour sum.
                            int nNeighbourSum = biOutput[idx1 - 1][idx]
                                                + biOutput[idx1 - 1][idx - 1]
                                                + biOutput[idx1 - 1][idx + 1]
                                                + biOutput[idx1 + 1][idx]
                                                + biOutput[idx1 + 1][idx - 1]
                                                + biOutput[idx1 + 1][idx + 1]
                                                + biOutput[idx1][idx - 1]
                                                + biOutput[idx1][idx + 1];
                            if (nNeighbourSum <= 1) {
                                nTotalStrokeEnds ++;
                            }
                        }
                    }
                }
            }
        }
        if (nTotalOnCountInSkeleton <= 2 || !bConsiderStrokeEnd) {
            return nTotalOnCountInSkeleton;
        } else {
            // end of strokes are removed during thinning procedure so have to add them back.
            return nTotalOnCountInSkeleton + nTotalStrokeEnds;
        }
    }
    
    public double calcAvgStrokeWidth() throws InterruptedException {
        byte[][] biIntermediate = StrokeFinder.findSkeletonIndexMethod(mbarrayImg, mnLeft, mnTop, mnWidth, mnHeight);
        byte[][] biOutput = StrokeFinder.smoothStroke(biIntermediate, 0, 0, mnWidth, mnHeight, 1);
        int nTotalOnCount = 0, nTotalOnCountInSkeleton = 0;
        for (int idx = 0; idx < mnHeight; idx ++)    {
            for (int idx1 = 0; idx1 < mnWidth; idx1 ++)  {
                if (mbarrayImg[idx1 + mnLeft][idx + mnTop] == 1)  {
                    nTotalOnCount ++;
                }
                if (biOutput[idx1][idx] == 1)  {
                    nTotalOnCountInSkeleton ++;
                }
            }
        }
        if (nTotalOnCountInSkeleton != 0 && nTotalOnCount != 0)   {
            return (double)nTotalOnCount/(double)nTotalOnCountInSkeleton;
        } else if (nTotalOnCount != 0)  {
            return Math.sqrt((double)nTotalOnCount);
        } else  {
            return 1;  // minum stroke width is 1.
        }
    }

    // All the image chops need not to have same mbarrayImg. The returned image chop is minimum contained.
    public double calcAvgSeperatedCharHeight(double dAvgStrokeWidth)    {
        return calcAvgSeperatedCharHeight(getLeftInOriginalImg(), getTopInOriginalImg(), getRightP1InOriginalImg(), getBottomP1InOriginalImg(), dAvgStrokeWidth);
    }

    // even if there is a single point in the threshs, it is counted in to calculate average char height.
    public double calcAvgSeperatedCharHeight(int nLeftThresh, int nTopThresh, int nRightP1Thresh, int nBottomP1Thresh, double dAvgStrokeWidth) {
        ImageChops imgChops = ExprSeperator.extractConnectedPieces(this);
        double dAvgHeight = 0;
        int nNumOfCntedChars = 0;
        for (int idx = 0; idx < imgChops.mlistChops.size(); idx ++) {
            byte[][] barrayThis = imgChops.mlistChops.get(idx).mbarrayImg;
            int nThisLeft = imgChops.mlistChops.get(idx).mnLeft;
            int nThisTop = imgChops.mlistChops.get(idx).mnTop;
            int nThisWidth = imgChops.mlistChops.get(idx).mnWidth;
            int nThisHeight = imgChops.mlistChops.get(idx).mnHeight;
            double dThisCharHeight = nThisHeight;
            if (nThisWidth != 0 && nThisHeight != 0
                    && (imgChops.mlistChops.get(idx).mnLeft < mapOriginalXIdx2This(nRightP1Thresh)
                        || imgChops.mlistChops.get(idx).getRightPlus1() > mapOriginalXIdx2This(nLeftThresh))
                    && (imgChops.mlistChops.get(idx).mnTop < mapOriginalXIdx2This(nBottomP1Thresh)
                        || imgChops.mlistChops.get(idx).getBottomPlus1() > mapOriginalXIdx2This(nTopThresh)))    {
                double dMaxCutHeight = 0;
                int nLastAllThroughDivIdx = nThisTop - 1;
                int idx2 = nThisTop;
                for (; idx2 < imgChops.mlistChops.get(idx).getBottomPlus1(); idx2 ++)    {
                    boolean bIsAllThroughLn = true;
                    for (int idx1 = nThisLeft; idx1 < imgChops.mlistChops.get(idx).getRightPlus1(); idx1 ++)    {
                        if (barrayThis[idx1][idx2] == 0)    {
                            bIsAllThroughLn = false;
                            break;
                        }
                    }
                    if (bIsAllThroughLn && (idx2 - nLastAllThroughDivIdx - 1) > dMaxCutHeight)    {
                        dMaxCutHeight = idx2 - nLastAllThroughDivIdx - 1;
                    }
                }
                if ((idx2 - nLastAllThroughDivIdx - 1) > dMaxCutHeight)    {
                        dMaxCutHeight = idx2 - nLastAllThroughDivIdx - 1;
                }
                dThisCharHeight = dMaxCutHeight;
            }
            if (dThisCharHeight >= ConstantsMgr.msnMinCharHeightInUnit && dThisCharHeight > dAvgStrokeWidth)  {
                // a seperated point or a disconnected stroke may significantly drag down dAvgHeight value.
                dAvgHeight += dThisCharHeight;
                nNumOfCntedChars ++;
            }
        }
        if (nNumOfCntedChars != 0) {
            dAvgHeight /= nNumOfCntedChars;

        }
        return Math.max(dAvgHeight, Math.max(ConstantsMgr.msnMinCharHeightInUnit, dAvgStrokeWidth));
    }

    // All the image chops need not to have same mbarrayImg. The returned image chop is minimum contained.
    // the four threshes are in original image.
    public double calcAvgHOverlapCharHeight(ImageChop chopOverlap, double dAvgStrokeWidth) {
        ImageChops imgChops = ExprSeperator.extractConnectedPieces(this);
        ImageChops imgChopsOverlap = ExprSeperator.extractConnectedPieces(chopOverlap);
        
        double dAvgHeight = 0;
        int nNumOfCntedChars = 0;
        for (int idx = 0; idx < imgChops.mlistChops.size(); idx ++) {
            byte[][] barrayThis = imgChops.mlistChops.get(idx).mbarrayImg;
            int nThisLeft = imgChops.mlistChops.get(idx).mnLeft;
            int nThisTop = imgChops.mlistChops.get(idx).mnTop;
            int nThisWidth = imgChops.mlistChops.get(idx).mnWidth;
            int nThisHeight = imgChops.mlistChops.get(idx).mnHeight;
            double dThisCharHeight = nThisHeight;
            if (nThisWidth != 0 && nThisHeight != 0)    {
                boolean bOverlapped = false;
                for (int idx3 = 0; idx3 < imgChopsOverlap.mlistChops.size(); idx3 ++)  {
                    int nLeftThresh = imgChopsOverlap.mlistChops.get(idx3).getLeftInOriginalImg();
                    int nRightP1Thresh = imgChopsOverlap.mlistChops.get(idx3).getRightP1InOriginalImg();
                    if (imgChops.mlistChops.get(idx).mnLeft < mapOriginalXIdx2This(nRightP1Thresh)
                        || imgChops.mlistChops.get(idx).getRightPlus1() > mapOriginalXIdx2This(nLeftThresh))    {
                        bOverlapped = true;
                        break;
                    }
                }
                if (!bOverlapped)   {   // not overlap, go to see the next one.
                    continue;
                }
                
                double dMaxCutHeight = 0;
                int nLastAllThroughDivIdx = nThisTop - 1;
                int idx2 = nThisTop;
                for (; idx2 < imgChops.mlistChops.get(idx).getBottomPlus1(); idx2 ++)    {
                    boolean bIsAllThroughLn = true;
                    for (int idx1 = nThisLeft; idx1 < imgChops.mlistChops.get(idx).getRightPlus1(); idx1 ++)    {
                        if (barrayThis[idx1][idx2] == 0)    {
                            bIsAllThroughLn = false;
                            break;
                        }
                    }
                    if (bIsAllThroughLn && (idx2 - nLastAllThroughDivIdx - 1) > dMaxCutHeight)    {
                        dMaxCutHeight = idx2 - nLastAllThroughDivIdx - 1;
                    }
                }
                if ((idx2 - nLastAllThroughDivIdx - 1) > dMaxCutHeight)    {
                        dMaxCutHeight = idx2 - nLastAllThroughDivIdx - 1;
                }
                dThisCharHeight = dMaxCutHeight;
            }
            if (dThisCharHeight >= ConstantsMgr.msnMinCharHeightInUnit && dThisCharHeight > dAvgStrokeWidth)  {
                // a seperated point or a disconnected stroke may significantly drag down dAvgHeight value.
                dAvgHeight += dThisCharHeight;
                nNumOfCntedChars ++;
            }
        }
        if (nNumOfCntedChars != 0) {
            dAvgHeight /= nNumOfCntedChars;

        }
        return Math.max(dAvgHeight, Math.max(ConstantsMgr.msnMinCharHeightInUnit, dAvgStrokeWidth));
    }

    // project the image chop to a horizontal line and calculate how many on s are over the range from nleft to nRightP1
    // the left and nRightP1 are in original image.
    public int calcProjOnCntOverHRange(int nLeft, int nRightP1) {
        if (nLeft >= nRightP1)  {
            return 0;  // invalid range.
        }
        byte[] blistProjs = new byte[nRightP1 - nLeft]; // initial value is 0.
        ImageChops imgChops = ExprSeperator.extractConnectedPieces(this);
        for (int idx = 0; idx < imgChops.mlistChops.size(); idx ++) {
            ImageChop chopThis = imgChops.mlistChops.get(idx);
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

    // project the image chop to a horizontal line and calculate how many on s are over the range from nTop to nBottomP1
    // the left and nRightP1 are in original image.
    public int calcProjOnCntOverVRange(int nTop, int nBottomP1) {
        if (nTop <= nBottomP1)  {
            return 0;  // invalid range.
        }
        byte[] blistProjs = new byte[nBottomP1 - nTop]; // initial value is 0.
        ImageChops imgChops = ExprSeperator.extractConnectedPieces(this);
        for (int idx = 0; idx < imgChops.mlistChops.size(); idx ++) {
            ImageChop chopThis = imgChops.mlistChops.get(idx);
            for (int idx1 = Math.max(nTop, chopThis.getTopInOriginalImg());
                    idx1 < Math.min(nBottomP1, chopThis.getBottomP1InOriginalImg()); idx1 ++) {
                blistProjs[idx1 - nTop] = 1;
            }
        }

        int nSumOn = 0;
        for (int idx = 0; idx < nBottomP1 - nTop; idx ++)   {
            nSumOn += blistProjs[idx - nTop];
        }
        return nSumOn;
    }

    public void printMatrix()   {
        if (mbarrayImg == null || mbarrayImg.length == 0)   {
            System.out.println("[]");
        } else {
            System.out.println("[");
            for (int idx = 0; idx < mbarrayImg[0].length; idx ++)   {
                for (int idx1 = 0; idx1 < mbarrayImg.length; idx1 ++)   {
                    if (idx1 >= mnLeft && idx1 < mnLeft + mnWidth && idx >= mnTop && idx < mnTop + mnHeight)    {
                        System.out.print(mbarrayImg[idx1][idx] + "\t");
                    } else  {
                        System.out.print("0\t");
                    }
                }
                System.out.print("\n");
            }
            System.out.println("]");
        }
    }

    public void printMinContainerMatrix()   {
        if (mbarrayImg == null || mnWidth == 0)   {
            System.out.println("[]");
        } else {
            System.out.println("[");
            for (int idx1 = mnTop; idx1 < mnTop + mnHeight; idx1 ++)   {
                for (int idx = mnLeft; idx < mnLeft + mnWidth; idx ++)   {
                    System.out.print(mbarrayImg[idx][idx1] + "\t");
                }
                System.out.print("\n");
            }
            System.out.println("]");
        }
    }

    public void printRefMatrix()   {
        if (mbarrayOriginalImg == null || mbarrayOriginalImg.length == 0)   {
            System.out.println("[]");
        } else {
            System.out.println("[");
            for (int idx = 0; idx < mbarrayOriginalImg[0].length; idx ++)   {
                for (int idx1 = 0; idx1 < mbarrayOriginalImg.length; idx1 ++)   {
                    if (idx1 >= getLeftInOriginalImg() && idx1 < getRightP1InOriginalImg()
                            && idx >= getTopInOriginalImg() && idx < getBottomP1InOriginalImg())    {
                        System.out.print(mbarrayOriginalImg[idx1][idx] + "\t");
                    } else  {
                        System.out.print("0\t");
                    }
                }
                System.out.print("\n");
            }
            System.out.println("]");
        }
    }
}