/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyzapps.mathrecog;

import com.cyzapps.mathrecog.UnitPrototypeMgr.UnitProtoType;
import java.util.LinkedList;

/**
 *
 * @author tonyc
 *
 */

/**
 * 表达式过滤，水平切割，竖直切割
 */
public class ExprFilter {
    
    // this function should not be used because it does not improve correctness but make recognition slower.

    // this function filter H-cut image chops before recognition. Note that this function can only be used in the very first recognize function
    public static ImageChops filterHCutImageChops(ImageChop imgChop, ImageChops imgChops, double dAvgStrokeWidth) {
        if (imgChops.mlistChops.size() <= 1) {
            return imgChops;
        }
        double dAvgChopHeight =0, dMaxChopHeight = 0;
        int nLeftExclFirst = Integer.MAX_VALUE, nRightP1ExclFirst = Integer.MIN_VALUE,
                nLeftExclLast = Integer.MAX_VALUE, nRightP1ExclLast = Integer.MIN_VALUE;
        for (int idx = 0; idx < imgChops.mlistChops.size(); idx ++) {
            ImageChop imgChopThis = imgChops.mlistChops.get(idx);
            dAvgChopHeight += imgChopThis.mnHeight;
            if (imgChopThis.mnHeight > dMaxChopHeight) {
                dMaxChopHeight = imgChopThis.mnHeight;
            }
            int nThisLeft = imgChopThis.getLeftInOriginalImg(), nThisRightP1 = imgChopThis.getRightP1InOriginalImg();
            if (idx > 0) {
                nLeftExclFirst = (nLeftExclFirst > nThisLeft)?nThisLeft:nLeftExclFirst;
                nRightP1ExclFirst = (nRightP1ExclFirst < nThisRightP1)?nThisRightP1:nRightP1ExclFirst;
            }
            if (idx < imgChops.mlistChops.size() - 1) {
                nLeftExclLast = (nLeftExclLast > nThisLeft)?nThisLeft:nLeftExclLast;
                nRightP1ExclLast = (nRightP1ExclLast < nThisRightP1)?nThisRightP1:nRightP1ExclLast;
            }
        }
        dAvgChopHeight /= imgChops.mlistChops.size();
        // consider the following situations that first or last image chop can be filtered off:
        // 1. first or last image chop is very thing and is significantly far away from other chops
        // 2. first or last image chop is very thing and long and does not cover other image chops well.
        ImageChops imgChopsReturn = new ImageChops();
        for (int idx = 0; idx < imgChops.mlistChops.size(); idx ++) {
            ImageChop imgChopThis = imgChops.mlistChops.get(idx);
            boolean bAddThisChop = true;
            if (idx == 0) {
                if (imgChopThis.mnHeight < dAvgStrokeWidth * 3
                        || imgChopThis.mnHeight < dMaxChopHeight * ConstantsMgr.msdBadChopHeight2MaxRatio
                        || imgChopThis.mnHeight < dAvgChopHeight * ConstantsMgr.msdBadChopHeight2AvgRatio) {
                    double dUpperNoteMaxDistanceToBase = Math.max(imgChopThis.mnHeight, (int)dAvgStrokeWidth * 3);
                    if ((imgChopThis.getLeftInOriginalImg() > nRightP1ExclFirst + dUpperNoteMaxDistanceToBase)
                            || (imgChopThis.getRightP1InOriginalImg() < nLeftExclFirst - dUpperNoteMaxDistanceToBase)) {
                        // this image chop is too far away from the major part, use dUpperNoteMaxDistanceToBase to ensure that it is not an upper note
                        bAddThisChop = false;
                    } else if ((double)imgChopThis.mnWidth / (double)imgChopThis.mnHeight > ConstantsMgr.msdExtendableCharWOverHThresh
                            && (((nLeftExclFirst - imgChopThis.getLeftInOriginalImg()) > imgChopThis.mnWidth * ConstantsMgr.msdTopUnderNotCoverWellThresh)
                                || ((imgChopThis.getRightP1InOriginalImg() - nRightP1ExclFirst) > imgChopThis.mnWidth * ConstantsMgr.msdTopUnderNotCoverWellThresh))) {
                        bAddThisChop = false;
                    }
                }
            } else if (idx == imgChops.mlistChops.size() - 1) {
                if (imgChopThis.mnHeight < dAvgStrokeWidth * 3
                        || imgChopThis.mnHeight < dMaxChopHeight * ConstantsMgr.msdBadChopHeight2MaxRatio
                        || imgChopThis.mnHeight < dAvgChopHeight * ConstantsMgr.msdBadChopHeight2AvgRatio) {
                    double dLowerNoteMaxDistanceToBase = Math.max(imgChopThis.mnHeight, (int)dAvgStrokeWidth * 3);
                    if ((imgChopThis.getLeftInOriginalImg() > nRightP1ExclLast + dLowerNoteMaxDistanceToBase)
                            || (imgChopThis.getRightP1InOriginalImg() < nLeftExclLast - dLowerNoteMaxDistanceToBase)) {
                        // this image chop is too far away from the major part, use dUpperNoteMaxDistanceToBase to ensure that it is not an upper note
                        bAddThisChop = false;
                    } else if ((double)imgChopThis.mnWidth / (double)imgChopThis.mnHeight > ConstantsMgr.msdExtendableCharWOverHThresh
                            && (((nLeftExclLast - imgChopThis.getLeftInOriginalImg()) > imgChopThis.mnWidth * ConstantsMgr.msdTopUnderNotCoverWellThresh)
                                || ((imgChopThis.getRightP1InOriginalImg() - nRightP1ExclLast) > imgChopThis.mnWidth * ConstantsMgr.msdTopUnderNotCoverWellThresh))) {
                        bAddThisChop = false;
                    }
                }
            }
            if (bAddThisChop) {
                imgChopsReturn.mlistChops.add(imgChopThis);
            }
        }
        return imgChopsReturn;
    }
    
    // this function filter V-cut image chops before recognition
    public static ImageChops filterVCutImageChops(ImageChop imgChop, ImageChops imgChops, double dAvgStrokeWidth) {
        return imgChops;
    }


    /**
     * //使用空格竖直切割原始（结构化表达式）！！！core!!!
     * @param serInput
     * @param serParent
     * @return
     * step1:去掉边缘噪声
     * step2:找到特殊的巨大缺口或空格，按空格竖直分块
     * step3:去掉每个子块的噪声
     */
    public static StructExprRecog filterVBlankCutRawSER(StructExprRecog serInput, StructExprRecog serParent) {
        StructExprRecog serOutput = null;
        // step 1: remove all the edge bad characters which may be obtained from photo edge
        double[] darrayMetrics = serInput.calcAvgCharMetrics();  // average height, width (including and excluding the boundary characters)
        double dAvgWidthOfGap = darrayMetrics[StructExprRecog.AVG_VGAP_IDX];
        double dAvgWidth = darrayMetrics[StructExprRecog.AVG_NORMAL_CHAR_WIDTH_IDX], dAvgHeight = darrayMetrics[StructExprRecog.AVG_NORMAL_CHAR_HEIGHT_IDX];
        LinkedList<StructExprRecog> listChildren = new LinkedList<StructExprRecog>();
        for (int idx = 0; idx < serInput.mlistChildren.size(); idx ++) {
            StructExprRecog serThisChild = serInput.mlistChildren.get(idx);
            if (serThisChild.mType != UnitProtoType.Type.TYPE_DOT && serThisChild.mnHeight < ConstantsMgr.msdMinSerHeightAgainstAvg * dAvgHeight
                    && serThisChild.mnWidth < ConstantsMgr.msdMinSerWidthAgainstAvg * dAvgWidth) {
                continue;   // seems like a noise point.
            }
            listChildren.add(serThisChild);
        }

        int idxFrom = 0, idxTo = listChildren.size() - 1;
        for (; idxFrom <= idxTo; idxFrom ++) {
            StructExprRecog serThisChild = listChildren.get(idxFrom);
            if ((serThisChild.mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                        && serThisChild.mnWidth >= ConstantsMgr.msdMinSerWidthAgainstAvg * dAvgWidth)   //this group of chars cannot be too thin.
                    || serThisChild.isBoundChar()
                    || serThisChild.isIntegTypeChar()
                    || serThisChild.isSIGMAPITypeChar()
                    || (serThisChild.mnWidth <= ConstantsMgr.msdAbnormalCharWidthThresh * dAvgWidth
                        && serThisChild.mnHeight <= ConstantsMgr.msdAbnormalCharWidthThresh * dAvgHeight)) {
                break;
            }
        }
        for (; idxTo >= idxFrom; idxTo --) {
            StructExprRecog serThisChild = listChildren.get(idxTo);
            if ((serThisChild.mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                        && serThisChild.mnWidth >= ConstantsMgr.msdMinSerWidthAgainstAvg * dAvgWidth)   //this group of chars cannot be too thin.
                    || serThisChild.isCloseBoundChar()
                    || (serThisChild.mnWidth <= ConstantsMgr.msdAbnormalCharWidthThresh * dAvgWidth
                        && serThisChild.mnHeight <= ConstantsMgr.msdAbnormalCharWidthThresh * dAvgHeight)) {
                break;
            }
        }

        // step 2: find out abnormally large gaps and abnormal characters and divide the v-blank cut into pieces
        LinkedList<int[]> listValidExprRanges = new LinkedList<int[]>();
        if (idxTo > idxFrom) {
            int nLastStart = idxFrom;
            int nPartLeft = listChildren.get(nLastStart).mnLeft;
            int nPartRightP1 = listChildren.get(nLastStart).getRightPlus1();
            int nLastMaxHCutsStart = idxFrom;
            int nLastMaxHCuts = 1;
            boolean bCompulsoryOneHCut = false;
            LinkedList<int[]> listStandardHeights = new LinkedList<int[]>();
            for (int idx = idxFrom + 1; idx <= idxTo; idx ++) {
                StructExprRecog serChild = listChildren.get(idx);
                if (serChild.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                        && (serChild.mType == UnitProtoType.Type.TYPE_BRACE
                            || serChild.mType == UnitProtoType.Type.TYPE_INTEGRATE  // integrate, big sigma and big pi may have upper lower notes that looks like two H-cut children.
                            || serChild.mType == UnitProtoType.Type.TYPE_INTEGRATE_CIRCLE
                            || serChild.mType == UnitProtoType.Type.TYPE_BIG_SIGMA
                            || serChild.mType == UnitProtoType.Type.TYPE_BIG_PI)) {
                    if (idx > idxFrom + 1) {
                        int[] narray = new int[3];
                        narray[0] = nLastMaxHCutsStart;
                        narray[1] = idx;
                        narray[2] = nLastMaxHCuts;
                        listStandardHeights.add(narray);
                    }
                    bCompulsoryOneHCut = true;
                    nLastMaxHCutsStart = idx;
                    nLastMaxHCuts = 1;
                } else if (serChild.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    &&(serChild.mType == UnitProtoType.Type.TYPE_ROUND_BRACKET
                        || serChild.mType == UnitProtoType.Type.TYPE_SQUARE_BRACKET
                        || serChild.mType == UnitProtoType.Type.TYPE_VERTICAL_LINE
                        || serChild.mType == UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET
                        || serChild.mType == UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET)) {
                    if (idx > idxFrom + 1) {
                        int[] narray = new int[3];
                        narray[0] = nLastMaxHCutsStart;
                        narray[1] = idx;
                        narray[2] = nLastMaxHCuts;
                        listStandardHeights.add(narray);
                    }
                    bCompulsoryOneHCut = false;
                    nLastMaxHCutsStart = idx;
                    nLastMaxHCuts = 1;
                } else if (!bCompulsoryOneHCut
                        && (serChild.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HBLANKCUT
                            || serChild.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_MULTIEXPRS)) {
                    // do not consider cap under assuming they have been correctly identified.
                    if (nLastMaxHCuts < listChildren.size()) {
                        nLastMaxHCuts = listChildren.size();
                    }
                }
            }
            int[] narray = new int[3];
            narray[0] = nLastMaxHCutsStart;
            narray[1] = idxTo + 1;
            narray[2] = nLastMaxHCuts;
            listStandardHeights.add(narray);
            int nLastStandardHeightsIdx = 0;
            for (int idx = idxFrom + 1; idx <= idxTo; idx ++) {
                // abnormally large char in the very beginning has been filtered off.
                StructExprRecog serThisChild = listChildren.get(idx);
                StructExprRecog serLastChild = listChildren.get(idx - 1);                
                int nMaxHCuts = 1;
                while (nLastStandardHeightsIdx < listStandardHeights.size()
                        && listStandardHeights.get(nLastStandardHeightsIdx)[1] <= idx - 1) {
                    nLastStandardHeightsIdx ++;
                }
                if (nLastStandardHeightsIdx < listStandardHeights.size()) {
                    nMaxHCuts = listStandardHeights.get(nLastStandardHeightsIdx)[2];
                }

                double dCharWidth2MeasureGap = dAvgWidth;   // for very long chars, gap should be wider.
                if (serThisChild.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE && serThisChild.mnWidth > dCharWidth2MeasureGap
                        && serThisChild.mnWidth < ConstantsMgr.msdAbnormalCharWidthThresh * dAvgWidth) {
                    dCharWidth2MeasureGap = serThisChild.mnWidth;
                }
                if (serLastChild.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE && serLastChild.mnWidth > dCharWidth2MeasureGap
                        && serLastChild.mnWidth < ConstantsMgr.msdAbnormalCharWidthThresh * dAvgWidth) {
                    dCharWidth2MeasureGap = serLastChild.mnWidth;
                }

                if (serThisChild.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                            && !serThisChild.isBoundChar() && !serThisChild.isCloseBoundChar() && !serThisChild.isIntegTypeChar() && !serThisChild.isSIGMAPITypeChar()
                            && (serThisChild.mnWidth > ConstantsMgr.msdAbnormalCharWidthThresh * dAvgWidth
                                || serThisChild.mnHeight > ConstantsMgr.msdAbnormalCharHeightThresh * dAvgHeight)){
                    // an abnormal character or a group of abnormal characters (they are too thin to be multi-exprs).
                    if (idx > nLastStart) {
                        int[] narrayIdices = new int[4];
                        narrayIdices[0] = nLastStart;
                        narrayIdices[1] = idx - 1;
                        narrayIdices[2] = nPartLeft;
                        narrayIdices[3] = nPartRightP1;
                        listValidExprRanges.add(narrayIdices);
                    }
                    nLastStart = idx + 1;
                } else if (serThisChild.mnLeft - serLastChild.getRightPlus1()
                        > (ConstantsMgr.msdAbnormalVCutGapThresh * dCharWidth2MeasureGap + dAvgWidthOfGap) * Math.sqrt((double)nMaxHCuts)
                    ) {   // considering some matrix. Because called b4 restruct, so that matrix cut is not identified yet (all are v cut)
                    // a too large gap
                    if (idx > nLastStart) {
                        int[] narrayIdices = new int[4];
                        narrayIdices[0] = nLastStart;
                        narrayIdices[1] = idx - 1;
                        narrayIdices[2] = nPartLeft;
                        narrayIdices[3] = nPartRightP1;
                        listValidExprRanges.add(narrayIdices);
                    }
                    nLastStart = idx;
                    nPartLeft = listChildren.get(nLastStart).mnLeft;
                    nPartRightP1 = listChildren.get(nLastStart).getRightPlus1();
                    if (idx == idxTo) { // end of expression
                        int[] narrayIdices = new int[4];
                        narrayIdices[0] = nLastStart;
                        narrayIdices[1] = idx;
                        narrayIdices[2] = nPartLeft;
                        narrayIdices[3] = nPartRightP1;
                        listValidExprRanges.add(narrayIdices);
                    }
                } else {
                    if (idx == nLastStart) {
                        nPartLeft = listChildren.get(nLastStart).mnLeft;
                        nPartRightP1 = listChildren.get(nLastStart).getRightPlus1();
                    } else {
                        if (nPartLeft > listChildren.get(idx).mnLeft) {
                            nPartLeft = listChildren.get(idx).mnLeft;
                        }
                        if (nPartRightP1 < listChildren.get(idx).getRightPlus1()) {
                            nPartRightP1 = listChildren.get(idx).getRightPlus1();
                        }
                    }
                    if (idx == idxTo) {
                        int[] narrayIdices = new int[4];
                        narrayIdices[0] = nLastStart;
                        narrayIdices[1] = idx;
                        narrayIdices[2] = nPartLeft;
                        narrayIdices[3] = nPartRightP1;
                        listValidExprRanges.add(narrayIdices);
                    }
                }
            }
        }
        if (listValidExprRanges.size() == 0) {
            serOutput = null;
        }
        else if (listValidExprRanges.size() == 1) {
            if (listValidExprRanges.getFirst()[0] == 0 && listValidExprRanges.getFirst()[1] == serInput.mlistChildren.size() - 1) {
                serOutput = serInput;   // means no noise point is removed.
            } else if (listValidExprRanges.getFirst()[0] == listValidExprRanges.getFirst()[1]) {
                serOutput = listChildren.get(listValidExprRanges.getFirst()[0]);
            } else {
                LinkedList<StructExprRecog> listNewChildren = new LinkedList<StructExprRecog>();
                for (int idx = listValidExprRanges.getFirst()[0]; idx <= listValidExprRanges.getFirst()[1]; idx ++) {
                    listNewChildren.add(listChildren.get(idx));
                }
                serOutput = new StructExprRecog(serInput.getBiArray());
                serOutput.setStructExprRecog(listNewChildren, StructExprRecog.EXPRRECOGTYPE_VBLANKCUT);
            }
        }
        else {
            int nAllLeft = serInput.mnLeft;
            int nAllRightP1 = serInput.getRightPlus1();
            int nLeftPRight = nAllLeft + nAllRightP1;
            int nMinDiff = Math.abs(listValidExprRanges.getFirst()[2] + listValidExprRanges.getFirst()[3] - nLeftPRight);
            int nMinDiffIdx = 0;
            for (int idx = 1; idx < listValidExprRanges.size(); idx ++) {
                int nThisDiff = Math.abs(listValidExprRanges.get(idx)[2] + listValidExprRanges.get(idx)[3] - nLeftPRight);
                if (nThisDiff <= nMinDiff) {
                    nMinDiffIdx = idx;
                    nMinDiff = nThisDiff;
                } else {
                    break;
                }
            }

            int nFromChild = listValidExprRanges.get(nMinDiffIdx)[0], nToChild = listValidExprRanges.get(nMinDiffIdx)[1];
            if (nFromChild == nToChild) {
                serOutput = listChildren.get(nFromChild);
            } else {
                LinkedList<StructExprRecog> listNewChildren = new LinkedList<StructExprRecog>();
                for (int idx1 = nFromChild; idx1 <= nToChild; idx1 ++) {
                    listNewChildren.add(listChildren.get(idx1));
                }
                serOutput = new StructExprRecog(serInput.getBiArray());
                serOutput.setStructExprRecog(listNewChildren, StructExprRecog.EXPRRECOGTYPE_VBLANKCUT);
            }
        }

        // step 3. remove noise points on top and bottom of each child.
        if (serOutput != null && serOutput.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VBLANKCUT) {
            // do not call children's filterRawSER, which may significantly deteriorate the expression.
            LinkedList<StructExprRecog> listNewNewChildren = new LinkedList<StructExprRecog>();
            listNewNewChildren.addAll(serOutput.mlistChildren);
            boolean bHasHBlankChildren = false;
            for (int idx = 0; idx < listNewNewChildren.size(); idx ++) {
                StructExprRecog serLast = null, serThis = listNewNewChildren.get(idx), serNext = null;
                if (idx > 0) {
                    serLast = listNewNewChildren.get(idx - 1);
                }
                if (idx < listNewNewChildren.size() - 1) {
                    serNext = listNewNewChildren.get(idx + 1);
                }
                if (serThis.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HBLANKCUT
                        || serThis.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTCAP
                        || serThis.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTUNDER
                        || serThis.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTCAPUNDER) { // implies that there are at least 2 children.
                    bHasHBlankChildren = true;
                    boolean bFirstChildIsNoise = false, bLastChildIsNoise = false;
                    StructExprRecog serThisFirstChild = serThis.mlistChildren.getFirst();
                    if (serThisFirstChild.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                            && serThisFirstChild.mType == UnitProtoType.Type.TYPE_DOT
                            && (serLast == null || serLast.mnTop >= serThisFirstChild.mnTop)
                            && (serNext == null || serNext.mnTop >= serThisFirstChild.mnTop)
                            && (serThis.mlistChildren.get(1).mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                                    || (!serThis.mlistChildren.get(1).isLetterChar() && ! serThis.mlistChildren.get(1).isPossibleNumberChar()))) {
                        // ok, first child is noise.
                        bFirstChildIsNoise = true;
                    }
                    StructExprRecog serThisLastChild = serThis.mlistChildren.getLast();
                    StructExprRecog serThisLastChildB4 = serThis.mlistChildren.get(serThis.mlistChildren.size() - 2);
                    if (serThisLastChild.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                            && serThisLastChild.mType == UnitProtoType.Type.TYPE_DOT
                            && (serLast == null || serLast.getBottomPlus1() <= serThisLastChild.getBottomPlus1())
                            && (serNext == null || serNext.getBottomPlus1() <= serThisFirstChild.getBottomPlus1())
                            && !serThisLastChildB4.isPossibleVLnChar()) {
                        // ok, last child is noise. No meed to worry about serThisLastChildB4 if it is not like |.
                        bLastChildIsNoise = true;
                    }
                    LinkedList<StructExprRecog> listNewThisChildren = new LinkedList<StructExprRecog>();
                    listNewThisChildren.addAll(serThis.mlistChildren);
                    if (bFirstChildIsNoise) {
                        listNewThisChildren.removeFirst();
                    }
                    if (bLastChildIsNoise) {
                        listNewThisChildren.removeLast();
                    }
                    if (listNewThisChildren.size() == 0) {
                        // ok, we should remove this child
                        listNewNewChildren.remove(idx);
                        idx --;
                    } else if (listNewThisChildren.size() == 1) {
                        listNewNewChildren.set(idx, listNewThisChildren.getFirst());
                    } else if (bFirstChildIsNoise || bLastChildIsNoise) {   // listNewThisChildren.size() >= 2
                        if (serThis.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HBLANKCUT) {
                            StructExprRecog serNewThis = new StructExprRecog(serInput.getBiArray());
                            serNewThis.setStructExprRecog(listNewThisChildren, StructExprRecog.EXPRRECOGTYPE_HBLANKCUT);
                            listNewNewChildren.set(idx, serNewThis);
                        } else if (serThis.mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_HCUTCAP
                                && serThis.mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_HCUTUNDER) {
                            // hcutcap and hcutunder still have 2 children implies that cap or under note is not removed
                            StructExprRecog serNewThis = new StructExprRecog(serInput.getBiArray());
                            if (bFirstChildIsNoise) {
                                serNewThis.setStructExprRecog(listNewThisChildren, StructExprRecog.EXPRRECOGTYPE_HCUTUNDER);
                            } else {// if (bLastChildIsNoise) {
                                serNewThis.setStructExprRecog(listNewThisChildren, StructExprRecog.EXPRRECOGTYPE_HCUTCAP);
                            }
                            listNewNewChildren.set(idx, serNewThis);
                        }
                    }
                }
            }
            if (bHasHBlankChildren) {
                serOutput = new StructExprRecog(serInput.getBiArray());
                serOutput.setStructExprRecog(listNewNewChildren, StructExprRecog.EXPRRECOGTYPE_VBLANKCUT);
            }
        }
        
        // do not try to filter off more noise points from front or end here because effect is not good
        return serOutput;
    }   

    //原始字符序列（raw ser) 初步过滤（filter) 变为更加合理的表达式
    // this function filters raw ser(structexpress) input and returns another raw ser which is most likely to be
    // a valid expression. Here raw ser means the ser hasn't been restructed. If the raw ser input
    // cannot be a valid expression, it returns null. note that returned ser could be a part of or
    // a copy of serInput.
    public static StructExprRecog filterRawSER(StructExprRecog serInput, StructExprRecog serParent) {
        StructExprRecog serOutput = null;
        switch (serInput.mnExprRecogType) {
        case StructExprRecog.EXPRRECOGTYPE_ENUMTYPE: {
            if (serParent == null && !isValidMathExpr(serInput)) {
                // this is a top level expression and its similarity is invalid.
                serOutput = null;
            } else if (serInput.mType == UnitProtoType.Type.TYPE_BIG_E || serInput.mType == UnitProtoType.Type.TYPE_SMALL_E
                    || serInput.mType == UnitProtoType.Type.TYPE_BIG_I || serInput.mType == UnitProtoType.Type.TYPE_SMALL_I
                    || serInput.isPossibleNumberChar()) {
                serOutput = serInput;
            } else {
                // it is not a valid value
                serOutput = null;
            }
            break;
        }
        case StructExprRecog.EXPRRECOGTYPE_LISTCUT: { // this type actually is not used.
            if (serParent == null && !isValidMathExpr(serInput)) {
                serOutput = null;
            } else {
                serOutput = serInput;
            }
            break;
        }
        case StructExprRecog.EXPRRECOGTYPE_HBLANKCUT:
          case StructExprRecog.EXPRRECOGTYPE_MULTIEXPRS: {
            LinkedList<StructExprRecog> listChildren = new LinkedList<StructExprRecog>();
            LinkedList<Double> listMathPossibility = new LinkedList<Double>();
            double dMaxMathPossibility = 0.0;
            for (int idx = 0; idx < serInput.mlistChildren.size(); idx ++) {
                StructExprRecog serNewChild = filterRawSER(serInput.mlistChildren.get(idx), serInput);
                if (serParent == null) {
                    if (!isValidMathExpr(serNewChild)) {
                        serNewChild = null;
                    }
                }
                if (serNewChild != null) {
                    listChildren.add(serNewChild);
                    double dMathPossibility = calcMathPossibility(serNewChild);
                    listMathPossibility.add(dMathPossibility);
                    if (dMathPossibility > dMaxMathPossibility) {
                        dMaxMathPossibility = dMathPossibility;
                    }
                }
            }
            LinkedList<StructExprRecog> listNewChildren = new LinkedList<StructExprRecog>();
            for (int idx = 0; idx < listChildren.size(); idx ++) {
                if (listChildren.get(idx) != null
                        && (listMathPossibility.get(idx) >= dMaxMathPossibility - ConstantsMgr.msdMathPossibilityCheckThresh
                            || listMathPossibility.get(idx) >= ConstantsMgr.msdMathPossiblityGoodThresh)) {
                    listNewChildren.add(listChildren.get(idx));
                }
            }
            if (listNewChildren.size() == 0) {
                serOutput = null;
            } else if (listNewChildren.size() == 1) {
                serOutput = listNewChildren.getFirst();
            } else {
                serOutput = new StructExprRecog(serInput.getBiArray());
                serOutput.setStructExprRecog(listNewChildren, StructExprRecog.EXPRRECOGTYPE_HBLANKCUT);
            }
            break;
        }
        case StructExprRecog.EXPRRECOGTYPE_HLINECUT: {
            StructExprRecog serNewFirst = filterRawSER(serInput.mlistChildren.getFirst(), serInput);
            StructExprRecog serNewLast = filterRawSER(serInput.mlistChildren.getLast(), serInput);
            if ((serParent == null && !isValidMathExpr(serInput.mlistChildren.getFirst()))
                    || serNewFirst == null
                    || serNewFirst.mnLeft != serInput.mlistChildren.getFirst().mnLeft
                    || serNewFirst.mnWidth != serInput.mlistChildren.getFirst().mnWidth) {
                // do not remove noises only remove big chunks in this function so that left width should not change
                if (serParent == null && !isValidMathExpr(serInput.mlistChildren.getLast())) {
                    serOutput = null;
                } else {
                    serOutput = serNewLast;
                }
            } else if ((serParent == null && !isValidMathExpr(serInput.mlistChildren.getLast()))
                    || serNewLast == null 
                    || serNewLast.mnLeft != serInput.mlistChildren.getLast().mnLeft
                    || serNewLast.mnWidth != serInput.mlistChildren.getLast().mnWidth) {
                // do not remove noises only remove big chunks in this function so that left width should not change
                serOutput = serNewFirst;
            } else {
                serOutput = serInput;
            }
            break;
        }
        case StructExprRecog.EXPRRECOGTYPE_HCUTCAP:
          case StructExprRecog.EXPRRECOGTYPE_HCUTUNDER: 
          case StructExprRecog.EXPRRECOGTYPE_HCUTCAPUNDER: {
            StructExprRecog serPrinciple = serInput.getPrincipleSER(1);
            if (serParent == null && (!isValidMathExpr(serInput) || !isValidMathExpr(serPrinciple)) /* || calcMathPossibility(serPrinciple) == 0.0*/) {
                serOutput = null;
            } else {    // do not filter principle or cap or under because too complicated before restruction is carried out.
                serOutput = serInput;
            }
            break;
        }
        case StructExprRecog.EXPRRECOGTYPE_VBLANKCUT: {
            serOutput = filterVBlankCutRawSER(serInput, serParent);
            break;
        }
        case StructExprRecog.EXPRRECOGTYPE_VCUTLEFTTOPNOTE: {
            // actually should not be here because serInput is a raw ser
            StructExprRecog serPrinciple = serInput.getPrincipleSER(2);
            if ((serParent == null && (!isValidMathExpr(serInput) || !isValidMathExpr(serPrinciple)))
                    || calcMathPossibility(serPrinciple) == 0.0) {
                serOutput = null;
            } else if (serPrinciple.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VBLANKCUT) {
                StructExprRecog serNewPrinciple = filterRawSER(serPrinciple, serInput);
                if (serNewPrinciple == null || serPrinciple.mnLeft < serNewPrinciple.mnLeft) {
                    serOutput = serNewPrinciple;   // valid principle part is not related to the notes.
                } else if (serNewPrinciple.mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_GETROOT) {
                    serOutput = serNewPrinciple; // not a square root,left top note could be a noise point.
                } else {
                    LinkedList<StructExprRecog> listChildren = new LinkedList<StructExprRecog>();
                    listChildren.add(serInput.mlistChildren.getFirst());
                    listChildren.add(serNewPrinciple);
                    serOutput = new StructExprRecog(serInput.getBiArray());
                    serOutput.setStructExprRecog(listChildren, StructExprRecog.EXPRRECOGTYPE_VCUTLEFTTOPNOTE);                   
                }                
            } else if (serPrinciple.mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_GETROOT) {
                serOutput = filterRawSER(serPrinciple, serInput); // not a square root,left top note could be a noise point.
            } else {
                serOutput = serInput;
            }
            break;
        }
        case StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE:
          case StructExprRecog.EXPRRECOGTYPE_VCUTLOWERNOTE: 
          case StructExprRecog.EXPRRECOGTYPE_VCUTLUNOTES: {
            // actually should not be here because serInput is a raw ser
            StructExprRecog serPrinciple = serInput.getPrincipleSER(4);
            if (serParent == null && (!isValidMathExpr(serInput) || !isValidMathExpr(serPrinciple))/* || calcMathPossibility(serPrinciple) == 0.0*/) {
                serOutput = null;
            } else if (serPrinciple.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VBLANKCUT) {
                StructExprRecog serNewPrinciple = filterRawSER(serPrinciple, serInput);
                if (serNewPrinciple == null || serPrinciple.getRightPlus1() > serNewPrinciple.getRightPlus1()) {
                    serOutput = serNewPrinciple;   // valid principle part is not related to the notes.
                } else if (serNewPrinciple != serPrinciple) {
                    LinkedList<StructExprRecog> listChildren = new LinkedList<StructExprRecog>();
                    listChildren.add(serNewPrinciple);
                    StructExprRecog serLowerNote = null;
                    if (serInput.mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE) {
                        serLowerNote = serInput.mlistChildren.get(1);
                        listChildren.add(serLowerNote);
                    }
                    StructExprRecog serUpperNote = null;
                    if (serInput.mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_VCUTLOWERNOTE) {
                        serUpperNote = serInput.mlistChildren.getLast();
                        listChildren.add(serUpperNote);
                    }
                    serOutput = new StructExprRecog(serInput.getBiArray());
                    serOutput.setStructExprRecog(listChildren, serInput.mnExprRecogType);
                } else {
                    serOutput = serInput;
                }
            } else {
                serOutput = serInput;
            }
            break;
        }
        case StructExprRecog.EXPRRECOGTYPE_VCUTMATRIX: {  // each element is a column, each element is Hblankcut with same number (> 1) of rows
            serOutput = serInput;   // actually should not be here coz serInput is a raw ser.
            for (int idx  = 0; idx < serInput.mlistChildren.size(); idx ++) {
                StructExprRecog serChild = serInput.mlistChildren.get(idx);
                StructExprRecog serNewChild = filterRawSER(serChild, serInput);
                if (serNewChild == null || serNewChild.mnLeft != serChild.mnLeft || serNewChild.mnTop != serChild.mnTop
                        || serNewChild.mnWidth != serChild.mnWidth || serNewChild.mnHeight != serChild.mnHeight) {
                    // here if serNewChild is different from serChild, it must be big chunk cut off so compare the size
                    serOutput = null;
                    break;
                }
            }
            break;
        }
        case StructExprRecog.EXPRRECOGTYPE_GETROOT: { // two element, first is root level (default is Enum type sqrt or sqrt left etc.), second is the rooted expression.
            if (serParent == null && (!isValidMathExpr(serInput.mlistChildren.getLast()) || !isValidMathExpr(serInput))) {
                serOutput = null;   // similarity seems not valid.
            }else {
                StructExprRecog serLast = serInput.mlistChildren.getLast();
                StructExprRecog serNewLast = filterRawSER(serInput.mlistChildren.getLast(), serInput);
                if ((serInput.mlistChildren.getLast().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                        || serInput.mlistChildren.getLast().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_LISTCUT
                        || serInput.mlistChildren.getLast().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HLINECUT
                        || serInput.mlistChildren.getLast().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTCAP
                        || serInput.mlistChildren.getLast().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTUNDER
                        || serInput.mlistChildren.getLast().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTCAPUNDER
                        || serInput.mlistChildren.getLast().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VBLANKCUT
                        || serInput.mlistChildren.getLast().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE
                        || serInput.mlistChildren.getLast().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTLOWERNOTE  // consider a case x_0 = 1, sqrt(x_0)
                        || serInput.mlistChildren.getLast().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTLUNOTES
                        || serInput.mlistChildren.getLast().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_GETROOT)
                    && serNewLast != null
                    && serNewLast.mnLeft == serLast.mnLeft && serNewLast.mnTop == serLast.mnTop
                    && serNewLast.mnWidth == serLast.mnWidth && serNewLast.mnHeight == serLast.mnHeight) {
                    // only if the rooted part is the above types the root formula can be calculated. consider cap under because we need to
                    // think about integration and sum. Moreover, any part of the rooted ser should not be filtered off. Otherwise, it is not
                    // a valid rooting expression.
                    serOutput = serInput;
                } else {
                    serOutput = null;    // root an invalid expression, return null.
                }
            }
            break;
        }
        default: {
            serOutput = null;
        }
        }
        return serOutput;
    }

    //对restructed 后的字符序列进行过滤
    // this function will try to filter any non math expression part and keep the expression that can be calculated.
    // here we do not consider situation like a^*, although it is could be right in some complicated expressions.
    public static StructExprRecog filterRestructedSER(StructExprRecog serInput, StructExprRecog serParent, StructExprRecog serGrandParent) {
        StructExprRecog serOutput = null;
        switch (serInput.mnExprRecogType) {
            //ENUMTYPE
            case StructExprRecog.EXPRRECOGTYPE_ENUMTYPE: {
            if (!serInput.isPossibleNumberChar() && !serInput.isLetterChar() && serInput.mType != UnitProtoType.Type.TYPE_INFINITE
                    && serInput.mType != UnitProtoType.Type.TYPE_BIG_PI) {  // big Pi is not recognized as a letter, but it can be misrecognized n.
                if (serParent == null) {
                    serOutput = null;   // a single char has to be letter or number (not possible number)
                } else if ((serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTCAP
                            || serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTUNDER
                            || serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTCAPUNDER
                            || serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTLEFTTOPNOTE
                            || serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTLOWERNOTE
                            || serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE
                            || serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTLUNOTES)
                        && serInput != serParent.getPrincipleSER(7)) {
                    serOutput = null;   // serInput is a single char and is note but not a possible number or letter. Here we allow possible number coz notes are small and easy to misrecog.
                } else if (serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTLEFTTOPNOTE
                        && serInput == serParent.getPrincipleSER(2)) {
                    serOutput = null;   //left top note principle must be a letter coz left top type is only for temperature after restrunction.
                } else if (serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_GETROOT
                        && serInput == serParent.getPrincipleSER(8)) {
                    serOutput = null;   // rooted value is invalid.
                } else if (serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_GETROOT
                        && serParent.mlistChildren.size() == 3
                        && serInput == serParent.mlistChildren.getFirst()) {
                    serOutput = null;   // root level should be valid.
                } else if (serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_MULTIEXPRS
                        || serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HBLANKCUT
                        || serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTMATRIX) {
                    serOutput = null;   // means an invalid line or invalid column in matrix.
                } else if (serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HLINECUT
                        && (serInput == serParent.mlistChildren.getFirst() || serInput == serParent.mlistChildren.getLast())) {
                    serOutput = null;   // invalid nominator or denominator.
                } else if (serInput.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE && serInput.mType == UnitProtoType.Type.TYPE_DOT
                        && (serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTLOWERNOTE
                            || serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE
                            || serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTLUNOTES                        
                            || serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_GETROOT)
                        && serInput == serParent.getPrincipleSER(12)) {
                    serOutput = null;   // principle cannot be a dot for upper lower notes. Do not consider other chars coz notes can be dot noise.
                } else if (serInput.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE && serInput.mType == UnitProtoType.Type.TYPE_DOT
                        && (serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTCAP
                            || serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTUNDER
                            || serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTCAPUNDER
                            || serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTLEFTTOPNOTE)
                        && serInput == serParent.getPrincipleSER(3)) {
                    serOutput = null;   // principle cannot be a dot. very likely dot is a noise.
                } else {
                    serOutput = serInput;
                }
            } else if ((serParent == null || ((serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_MULTIEXPRS || serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HBLANKCUT)
                                && (serGrandParent == null || serGrandParent.mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_VCUTMATRIX)))
                    && !serInput.isNumberChar() && !serInput.isLetterChar() && serInput.mType != UnitProtoType.Type.TYPE_INFINITE
                    && serInput.mType != UnitProtoType.Type.TYPE_BIG_PI) {
                serOutput = null;   // single expression or pure multi expressions (not matrix) should have higher standard. So must be confirmed number char.
            } else {
                serOutput = serInput;
            }
            break;
        }
        //？？？
        case StructExprRecog.EXPRRECOGTYPE_LISTCUT: { // this type actually is not used.
            serOutput = serInput;
            break;
        }
        //multiexprs 多表达式 方程组？
        case StructExprRecog.EXPRRECOGTYPE_HBLANKCUT:
            case StructExprRecog.EXPRRECOGTYPE_MULTIEXPRS: {
            LinkedList<StructExprRecog> listChildren = new LinkedList<StructExprRecog>();
            for (int idx = 0; idx < serInput.mlistChildren.size(); idx ++) {
                StructExprRecog serNewChild = filterRestructedSER(serInput.mlistChildren.get(idx), serInput, serParent);
                listChildren.add(serNewChild);
            }
            LinkedList<StructExprRecog> listNewChildren = new LinkedList<StructExprRecog>();
            for (int idx = 0; idx < listChildren.size(); idx ++) {
                if (listChildren.get(idx) != null) {
                    listNewChildren.add(listChildren.get(idx));
                }
            }
            if (listNewChildren.size() == 0) {
                serOutput = null;
            } else if (listNewChildren.size() == 1) {
                serOutput = listNewChildren.getFirst();
            } else {
                serOutput = new StructExprRecog(serInput.getBiArray());
                serOutput.setStructExprRecog(listNewChildren, serInput.mnExprRecogType);
            }
            break;
        }
        //分数
        case StructExprRecog.EXPRRECOGTYPE_HLINECUT: {
            StructExprRecog serNewFirst = filterRestructedSER(serInput.mlistChildren.getFirst(), serInput, serParent);
            StructExprRecog serNewLast = filterRestructedSER(serInput.mlistChildren.getLast(), serInput, serParent);
            if (serNewFirst == null && serNewLast == null) {
                serOutput = null;
            } else if (serNewFirst == null && serNewLast != null) {
                serOutput = serNewLast;
            } else if (serNewFirst != null && serNewLast == null) {
                serOutput = serNewFirst;
            } else {
                serOutput = new StructExprRecog(serInput.getBiArray());   // here could only remove noise so should still be a hline cut
                LinkedList<StructExprRecog> listChildren = new LinkedList<StructExprRecog>();
                listChildren.add(serNewFirst);
                listChildren.add(serInput.mlistChildren.get(1));
                listChildren.add(serNewLast);
                serOutput.setStructExprRecog(listChildren, StructExprRecog.EXPRRECOGTYPE_HLINECUT);
            }
            break;
        }
        //上划线 下划线 上下划线
        case StructExprRecog.EXPRRECOGTYPE_HCUTCAP:
          case StructExprRecog.EXPRRECOGTYPE_HCUTUNDER: 
          case StructExprRecog.EXPRRECOGTYPE_HCUTCAPUNDER: {
            StructExprRecog serPrinciple = serInput.getPrincipleSER(1);
            StructExprRecog serCap = (serInput.mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_HCUTUNDER)?
                    serInput.mlistChildren.getFirst():null;
            StructExprRecog serUnder = (serInput.mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_HCUTCAP)?
                    serInput.mlistChildren.getLast():null;
            StructExprRecog serPrincipleNew = filterRestructedSER(serPrinciple, serInput, serParent);
            StructExprRecog serCapNew = (serCap == null)?null:filterRestructedSER(serCap, serInput, serParent);
            StructExprRecog serUnderNew = (serUnder == null)?null:filterRestructedSER(serUnder, serInput, serParent);
            if (serPrincipleNew == null) {
                serOutput = null;
            } else if (serCapNew == null && serUnderNew == null) {
                serOutput = serPrincipleNew;
            } else if (serCapNew == null) {
                serOutput = new StructExprRecog(serInput.getBiArray());
                LinkedList<StructExprRecog> listChildren = new LinkedList<StructExprRecog>();
                listChildren.add(serPrincipleNew);
                listChildren.add(serUnderNew);
                serOutput.setStructExprRecog(listChildren, StructExprRecog.EXPRRECOGTYPE_HCUTUNDER);
            } else if (serUnderNew == null) {
                serOutput = new StructExprRecog(serInput.getBiArray());
                LinkedList<StructExprRecog> listChildren = new LinkedList<StructExprRecog>();
                listChildren.add(serCapNew);
                listChildren.add(serPrincipleNew);
                serOutput.setStructExprRecog(listChildren, StructExprRecog.EXPRRECOGTYPE_HCUTCAP);
            } else {
                serOutput = new StructExprRecog(serInput.getBiArray());
                LinkedList<StructExprRecog> listChildren = new LinkedList<StructExprRecog>();
                listChildren.add(serCapNew);
                listChildren.add(serPrincipleNew);
                listChildren.add(serUnderNew);
                serOutput.setStructExprRecog(listChildren, StructExprRecog.EXPRRECOGTYPE_HCUTCAPUNDER);
            }
            break;
        }

        //竖直方向空白切割 大多数表达式是这种情况
        case StructExprRecog.EXPRRECOGTYPE_VBLANKCUT: {
            LinkedList<StructExprRecog> listNewChildren = new LinkedList<StructExprRecog>();
            int idxLast = -1;
            for (int idx = 0; idx < serInput.mlistChildren.size(); idx ++) {
                StructExprRecog serNewChild = filterRestructedSER(serInput.mlistChildren.get(idx), serInput, serParent);

                if (serNewChild != null) {
                    if (listNewChildren.size() > 0 && listNewChildren.getLast().isPossibleNumberChar()
                            && (serInput.mlistChildren.get(idxLast).mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTLOWERNOTE
                                || serInput.mlistChildren.get(idxLast).mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTLUNOTES)
                            && serInput.mlistChildren.get(idxLast).mlistChildren.get(1).mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                            && serInput.mlistChildren.get(idxLast).mlistChildren.get(1).mType == UnitProtoType.Type.TYPE_DOT
                            && ((serNewChild.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE
                                    && serNewChild.getPrincipleSER(4).isPossibleNumberChar())
                                || serNewChild.isPossibleNumberChar())) {
                        listNewChildren.add(serInput.mlistChildren.get(idxLast).mlistChildren.get(1));
                        // the lower note dot was removed by the filter but it is actually a decimal point.
                        //下标点移除
                    }
                    listNewChildren.add(serNewChild);
                    idxLast = idx;
                }
            }
            int idxFrom = 0;
            int idxTo = listNewChildren.size() - 1;
            if (serParent == null || serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HBLANKCUT
                    || serParent.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_MULTIEXPRS) {
                // multiexprs may have dot points in the end of each expression.
                // step 1. look for close boundary from beginning and open boundary from end. Do not consider [ ] because they can be misrecognized 1.
                //todo dml_changed5 : 不要直接过滤掉不匹配的括号，因为他们可能是被错误识别的
                int idxFirstCloseBndNoMatch = idxFrom - 1, idxLastCloseBndNoMatch = idxFrom - 1;
                int idxFirstOpenBndNoMatch = idxTo + 1, idxLastOpenBndNoMatch = idxTo + 1;
                for (int idx = idxFrom; idx <= idxTo; idx ++) {
                    StructExprRecog ser = listNewChildren.get(idx).getPrincipleSER(4);
                    //这里只考虑了{}作为过滤条件
                    if (ser.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                            && (//ser.mType == UnitProtoType.Type.TYPE_ROUND_BRACKET||
                                /*|| ser.mType == UnitProtoType.Type.TYPE_SQUARE_BRACKET*/
                                 ser.mType == UnitProtoType.Type.TYPE_BRACE)) {
                        break;
                    }
                    //
                    else if (ser.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                            && (//ser.mType == UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET||
                                /*|| ser.mType == UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET*/
                                ser.mType == UnitProtoType.Type.TYPE_CLOSE_BRACE)){
                        if (idxFirstCloseBndNoMatch == idxFrom - 1) {
                            idxFirstCloseBndNoMatch = idx;
                        }
                        idxLastCloseBndNoMatch = idx;
                    }
                }
                //这里去掉所有以开括号为依据的过滤逻辑，因为([可能是1,{可能是方程组或积分
//                for (int idx = idxTo; idx >= idxFrom; idx --) {
//                    StructExprRecog ser = listNewChildren.get(idx).getPrincipleSER(4);
//                    if (ser.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
//                            && (ser.mType == UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET
//                                /*|| ser.mType == UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET*/
//                                || ser.mType == UnitProtoType.Type.TYPE_CLOSE_BRACE)) {
//                        break;
//                    } else if (ser.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
//                           &&(ser.mType == UnitProtoType.Type.TYPE_ROUND_BRACKET
//                                /*|| ser.mType == UnitProtoType.Type.TYPE_SQUARE_BRACKET*/
//                                /*|| ser.mType == UnitProtoType.Type.TYPE_BRACE*/)) {   // brace can be starting of a group of expressions.
//                        if (idxFirstOpenBndNoMatch == idxTo + 1) {
//                            idxFirstOpenBndNoMatch = idx;
//                        }
//                        idxLastOpenBndNoMatch = idx;
//                    }
//                }

                // now find out the major part
                int nWidthB4UnMatchCloseBnd = 0, nWidthInBnd = 0, nWidthAfterMatchOpenBnd = 0;
                if (idxFirstCloseBndNoMatch > idxFrom) {
                    nWidthB4UnMatchCloseBnd = listNewChildren.get(idxFirstCloseBndNoMatch - 1).getRightPlus1()
                            - listNewChildren.get(idxFrom).mnLeft;
                }
                if (idxLastCloseBndNoMatch < idxLastOpenBndNoMatch - 1) {
                    nWidthInBnd = listNewChildren.get(idxLastOpenBndNoMatch - 1).getRightPlus1()
                            - listNewChildren.get(idxLastCloseBndNoMatch + 1).mnLeft;
                }
                if (idxFirstOpenBndNoMatch < idxTo) {
                    nWidthAfterMatchOpenBnd = listNewChildren.get(idxTo).getRightPlus1()
                            - listNewChildren.get(idxFirstOpenBndNoMatch + 1).mnLeft;
                }

                if (nWidthB4UnMatchCloseBnd >= ConstantsMgr.msdVBlankCutMajorWidthRatio * nWidthInBnd
                        && nWidthB4UnMatchCloseBnd > nWidthAfterMatchOpenBnd) {
                    idxTo = idxFirstCloseBndNoMatch - 1;
                } else if (nWidthAfterMatchOpenBnd >= ConstantsMgr.msdVBlankCutMajorWidthRatio * nWidthInBnd
                        && nWidthB4UnMatchCloseBnd < nWidthAfterMatchOpenBnd) {
                    idxFrom = idxFirstOpenBndNoMatch + 1;
                } else {
                    idxFrom = idxLastCloseBndNoMatch + 1;
                    idxTo = idxLastOpenBndNoMatch - 1;
                }

                // step 2. remove all the bi-opts from start and end
                //todo dml_changed3: 句尾的dottimes认为是x，而不是直接删掉。
                for (; idxFrom <= idxTo; idxFrom ++) {
                    StructExprRecog ser = listNewChildren.get(idxFrom).getPrincipleSER(15);
                    if (ser.isBiOptChar() && !ser.isPreUnOptChar()    // some opt chars can be both bi opt and preunopt.
                            && ser.mType != UnitProtoType.Type.TYPE_VERTICAL_LINE
                            && ser.mType != UnitProtoType.Type.TYPE_FORWARD_SLASH
                            && ser.mType != UnitProtoType.Type.TYPE_BACKWARD_SLASH
                            && ser.mType != UnitProtoType.Type.TYPE_MULTIPLY
                            &&ser.mType!=UnitProtoType.Type.TYPE_DOT_MULTIPLY) {
                        // vertical line, forward slash and backward slash can be actually misrecognized 1, multiply can be x.
                        continue;
                    } else {
                        //2final_change: times也加上。
                        if(ser.mType==UnitProtoType.Type.TYPE_DOT_MULTIPLY){
                            ser.mType=UnitProtoType.Type.TYPE_SMALL_X;
                        }
                        break;
                    }
                }

                for (; idxTo >= idxFrom; idxTo --) {
                    StructExprRecog ser = listNewChildren.get(idxTo).getPrincipleSER(15);
                    //这些是不要的
                    if (ser.isBiOptChar()
                            && ser.mType != UnitProtoType.Type.TYPE_VERTICAL_LINE
                            && ser.mType != UnitProtoType.Type.TYPE_FORWARD_SLASH
                            && ser.mType != UnitProtoType.Type.TYPE_BACKWARD_SLASH
                            && ser.mType != UnitProtoType.Type.TYPE_MULTIPLY
                            && ser.mType != UnitProtoType.Type.TYPE_DOT_MULTIPLY) {
                        // vertical line, forward slash and backward slash can be actually misrecognized 1, multiply can be x.
                        continue;
                    } else {
                        if(ser.mType==UnitProtoType.Type.TYPE_DOT_MULTIPLY){
                            ser.mType=UnitProtoType.Type.TYPE_SMALL_X;;
                        }
                        break;
                    }
                }
            }
            
            // step 3., remove noise points
            LinkedList<StructExprRecog> listNewNewChildren  = new LinkedList<StructExprRecog>();
            for (int idx = idxFrom; idx <= idxTo; idx ++) {
                if (idx < idxTo) {
                    StructExprRecog serThis = listNewChildren.get(idx), serNext = listNewChildren.get(idx + 1); //opt char with notes should have been filtered to opt char without notes.
                    if (serThis.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE && serThis.mType == UnitProtoType.Type.TYPE_DOT) {
                        if (serNext.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE && !serNext.isPossibleNumberChar() && !serNext.isLetterChar()){
                            // remove the dot one which should be a noise point.
                            continue;
                        } else if (idx > 0 && (!listNewChildren.get(idx - 1).isPossibleNumberChar() || !serNext.getPrincipleSER(4).isPossibleNumberChar())) {
                            // this should not be a decimal point. Note that we don't consider class member, i.e. xxx.xxx yet. Also need to consider a situation like 0.4**2
                            continue;
                        } else {
                            listNewNewChildren.add(listNewChildren.get(idx));
                        }
                    } else {
                        listNewNewChildren.add(listNewChildren.get(idx));
                    }
                } else {
                    if (listNewChildren.get(idx).mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                            || listNewChildren.get(idx).mType != UnitProtoType.Type.TYPE_DOT) {
                        // dot should not be the last. The last dot should be a noise point.
                        listNewNewChildren.add(listNewChildren.get(idx));
                    }
                }
            }
            
            if (listNewNewChildren.size() == 0) {
                serOutput = null;
            } else if (listNewNewChildren.size() == 1) {
                serOutput = listNewNewChildren.getFirst();
            } else {
                serOutput = new StructExprRecog(serInput.getBiArray());
                serOutput.setStructExprRecog(listNewNewChildren, StructExprRecog.EXPRRECOGTYPE_VBLANKCUT);
            }
            break;
        }
        //左上角标
        case StructExprRecog.EXPRRECOGTYPE_VCUTLEFTTOPNOTE: {
            StructExprRecog serPrinciple = serInput.getPrincipleSER(2);
            // here we assume after restruction all left top notes have been converted to 0C, 0F or roots. If any left top note left, simply ignore 
            serOutput = filterRestructedSER(serPrinciple, serInput, serParent);
            break;
        }
        //上标，下标，上下标
        case StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE:
          case StructExprRecog.EXPRRECOGTYPE_VCUTLOWERNOTE: 
          case StructExprRecog.EXPRRECOGTYPE_VCUTLUNOTES: {
            // actually should not be here because serInput is a raw ser
            StructExprRecog serPrinciple = serInput.getPrincipleSER(4);
            StructExprRecog serNewPrinciple = filterRestructedSER(serPrinciple, serInput, serParent);
            LinkedList<StructExprRecog> listChildren = new LinkedList<StructExprRecog>();
            StructExprRecog serNewUpperNote = null;
            if (serInput.mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_VCUTLOWERNOTE) {
                StructExprRecog upper = serInput.mlistChildren.getLast();
                //todo here is to avoid ' be filtered.
                if(upper.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE && upper.isPossibleDiff())
                    serNewUpperNote = upper;
                else
                    serNewUpperNote = filterRestructedSER(serInput.mlistChildren.getLast(), serInput, serParent);
            }
            StructExprRecog serNewLowerNote = null;
            if (serInput.mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE) {
                if (serPrinciple.mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                        || (!serPrinciple.isLetterChar() && !serPrinciple.isSIGMAPITypeChar() && !serPrinciple.isIntegTypeChar()
                            && !serPrinciple.isCloseBoundChar() && serPrinciple.mType != UnitProtoType.Type.TYPE_MULTIPLY))   {
                    // multiply can be x. | can have lower notes as deriviative var value.
                    serNewLowerNote = null; // only letters can have lower note
                } else {
                    serNewLowerNote = filterRestructedSER(serInput.mlistChildren.get(1), serInput, serParent);
                }
            }
            if (serNewPrinciple == null) {
                serOutput = null;
            } else if ((serNewUpperNote == null && serNewLowerNote == null)
                    || serNewPrinciple.getRightPlus1() < serPrinciple.getRightPlus1()) {
                serOutput = serNewPrinciple;
            } else {
                serOutput = new StructExprRecog(serInput.getBiArray());
                int nSERType;
                if (serNewUpperNote != null && serNewLowerNote == null) {
                    listChildren.add(serNewPrinciple);
                    listChildren.add(serNewUpperNote);
                    nSERType = StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE;
                } else if (serNewUpperNote == null && serNewLowerNote != null) {
                    listChildren.add(serNewPrinciple);
                    listChildren.add(serNewLowerNote);                
                    nSERType = StructExprRecog.EXPRRECOGTYPE_VCUTLOWERNOTE;
                } else {
                    listChildren.add(serNewPrinciple);
                    listChildren.add(serNewLowerNote);                
                    listChildren.add(serNewUpperNote);
                    nSERType = StructExprRecog.EXPRRECOGTYPE_VCUTLUNOTES;
                }
                serOutput.setStructExprRecog(listChildren, nSERType);
            }
            break;
        }
        //矩阵类型
        case StructExprRecog.EXPRRECOGTYPE_VCUTMATRIX: {  // each element is a column, each element is Hblankcut with same number (> 1) of rows
            LinkedList<LinkedList<StructExprRecog>> listNewChildren = new LinkedList<LinkedList<StructExprRecog>>();
            for (int idx = 0; idx < serInput.mlistChildren.size(); idx ++) {
                int nNumOfElemInvalid = 0;
                LinkedList<StructExprRecog> listAColumn = new LinkedList<StructExprRecog>();
                for (int idx1 = 0; idx1 < serInput.mlistChildren.get(idx).mlistChildren.size(); idx1 ++) {
                    StructExprRecog serElem = filterRestructedSER(serInput.mlistChildren.get(idx).mlistChildren.get(idx1),
                                                serInput.mlistChildren.get(idx), serInput);
                    if (serElem == null) {
                        nNumOfElemInvalid ++;
                    }
                    listAColumn.add(serElem);
                }
                if (nNumOfElemInvalid != serInput.mlistChildren.get(idx).mlistChildren.size()) {
                    // if the whole column is noise,  remove it. Otherwise, add the column even if it includes invalid points.
                    listNewChildren.add(listAColumn);
                }
            }
            boolean bInvalidMatrix = false;
            if (listNewChildren.size() > 0) {
                LinkedList<Integer> listRemovedRows = new LinkedList<Integer>();
                for (int idx1 = 0; idx1 < listNewChildren.getFirst().size(); idx1 ++) {
                    int nNumOfElemInvalid = 0;
                    for (int idx = 0; idx < listNewChildren.size(); idx ++) {
                        if (listNewChildren.get(idx).get(idx1) == null) {
                            nNumOfElemInvalid ++;
                        }
                    }
                    if (nNumOfElemInvalid == listNewChildren.size()) {
                        listRemovedRows.add(idx1);
                    } else if (nNumOfElemInvalid > 0) {
                        serOutput = null;
                        bInvalidMatrix = true;
                        break;  // include a row which has invalid elements, but not all elements are invalid
                    }
                }
                
                if (!bInvalidMatrix) {
                    if (listRemovedRows.size() == listNewChildren.getFirst().size()) {
                        // all the rows are invalid
                        serOutput = null;
                        bInvalidMatrix = true;
                    } else {
                        LinkedList<StructExprRecog> listNewSerChildren = new LinkedList<StructExprRecog>();
                        for (int idx = 0; idx < listNewChildren.size(); idx ++) {
                            LinkedList<StructExprRecog> listThisColumn = new LinkedList<StructExprRecog>();
                            for (int idx1 = 0; idx1 < listNewChildren.get(idx).size(); idx1 ++) {
                                if (listRemovedRows.indexOf(idx1) == -1) {
                                    listThisColumn.add(listNewChildren.get(idx).get(idx1));
                                }
                            }
                            StructExprRecog serThisColumn = new StructExprRecog(serInput.getBiArray());
                            serThisColumn.setStructExprRecog(listThisColumn, StructExprRecog.EXPRRECOGTYPE_HBLANKCUT);
                            listNewSerChildren.add(serThisColumn);
                        }
                        serOutput = new StructExprRecog(serInput.getBiArray());
                        serOutput.setStructExprRecog(listNewSerChildren, StructExprRecog.EXPRRECOGTYPE_VCUTMATRIX);
                    }
                }
            } else {
                serOutput = null;
                bInvalidMatrix = true;
            }
            break;
        }
        //根号类型
        case StructExprRecog.EXPRRECOGTYPE_GETROOT: { // two element, first is root level (default is Enum type sqrt or sqrt left etc.), second is the rooted expression.
            StructExprRecog serLeftTopNote = serInput.mlistChildren.getFirst();
            StructExprRecog serRootChar =  serInput.mlistChildren.getFirst();
            StructExprRecog serNewLeftTopNote = null;
            StructExprRecog serNewPrinciple = filterRestructedSER(serInput.getPrincipleSER(8), serInput, serParent);
            if (serLeftTopNote.mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    || (serLeftTopNote.mType != UnitProtoType.Type.TYPE_SQRT_LEFT
                        && serLeftTopNote.mType != UnitProtoType.Type.TYPE_SQRT_LONG
                        && serLeftTopNote.mType != UnitProtoType.Type.TYPE_SQRT_MEDIUM
                        && serLeftTopNote.mType != UnitProtoType.Type.TYPE_SQRT_SHORT
                        && serLeftTopNote.mType != UnitProtoType.Type.TYPE_SQRT_TALL
                        && serLeftTopNote.mType != UnitProtoType.Type.TYPE_SQRT_VERY_TALL)) {
                serNewLeftTopNote =filterRestructedSER(serLeftTopNote, serInput, serParent);
                serRootChar = serInput.mlistChildren.get(1);
            }
            
            if (serNewPrinciple == null) {
                serOutput = null;
            } else {
                LinkedList<StructExprRecog> listChildren = new LinkedList<StructExprRecog>();
                if (serNewLeftTopNote != null) {
                    listChildren.add(serNewLeftTopNote);
                }
                listChildren.add(serRootChar);
                listChildren.add(serNewPrinciple);
                serOutput = new StructExprRecog(serInput.getBiArray());
                serOutput.setStructExprRecog(listChildren, StructExprRecog.EXPRRECOGTYPE_GETROOT);
            }
            break;
        }
        //空
        default: {
            serOutput = null;
        }
        }
        return serOutput;
    }

    //是否为合法数学表达式判定
    // can handle ser == null case.
    public static boolean isValidMathExpr(StructExprRecog ser) {
        if (ser == null) {
            return false;
        } else if (ser.mdSimilarity > ConstantsMgr.msdGoodRecogExprThresh ) {
            return true;    // this may not be a standard coz dot, -, | = always have 1.0 similarity.
        } else {
            return true;
        }
    }

    //是否为单数学符号
    public static int isMathSignChar(StructExprRecog ser) {
        if (ser.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE) {
            if (ser.mType == UnitProtoType.Type.TYPE_ONE
                    || ser.mType == UnitProtoType.Type.TYPE_TWO
                    || ser.mType == UnitProtoType.Type.TYPE_THREE
                    || ser.mType == UnitProtoType.Type.TYPE_FOUR
                    || ser.mType == UnitProtoType.Type.TYPE_FIVE
                    || ser.mType == UnitProtoType.Type.TYPE_SIX
                    || ser.mType == UnitProtoType.Type.TYPE_SEVEN
                    || ser.mType == UnitProtoType.Type.TYPE_EIGHT
                    || ser.mType == UnitProtoType.Type.TYPE_NINE
                    || ser.mType == UnitProtoType.Type.TYPE_INFINITE
                    || ser.mType == UnitProtoType.Type.TYPE_SMALL_PI
                    || ser.mType == UnitProtoType.Type.TYPE_BIG_PI
                    || ser.mType == UnitProtoType.Type.TYPE_BIG_SIGMA
                    || ser.mType == UnitProtoType.Type.TYPE_INTEGRATE
                    || ser.mType == UnitProtoType.Type.TYPE_INTEGRATE_CIRCLE
                    || ser.mType == UnitProtoType.Type.TYPE_SQRT_LEFT
                    || ser.mType == UnitProtoType.Type.TYPE_SQRT_SHORT
                    || ser.mType == UnitProtoType.Type.TYPE_SQRT_MEDIUM
                    || ser.mType == UnitProtoType.Type.TYPE_SQRT_LONG
                    || ser.mType == UnitProtoType.Type.TYPE_SQRT_TALL
                    || ser.mType == UnitProtoType.Type.TYPE_SQRT_VERY_TALL
                    || ser.mType == UnitProtoType.Type.TYPE_ADD
                    || ser.mType == UnitProtoType.Type.TYPE_PLUS_MINUS
                    || ser.mType == UnitProtoType.Type.TYPE_DOT_MULTIPLY
                    || ser.mType == UnitProtoType.Type.TYPE_MULTIPLY
                    || ser.mType == UnitProtoType.Type.TYPE_STAR
                    || ser.mType == UnitProtoType.Type.TYPE_DIVIDE
                    || ser.mType == UnitProtoType.Type.TYPE_EQUAL
                    || ser.mType == UnitProtoType.Type.TYPE_EQUAL_ALWAYS
                    || ser.mType == UnitProtoType.Type.TYPE_EQUAL_ROUGHLY
                    || ser.mType == UnitProtoType.Type.TYPE_LARGER
                    || ser.mType == UnitProtoType.Type.TYPE_SMALLER
                    || ser.mType == UnitProtoType.Type.TYPE_NO_LARGER
                    || ser.mType == UnitProtoType.Type.TYPE_NO_SMALLER
                    || ser.mType == UnitProtoType.Type.TYPE_PERCENT
                    || ser.mType == UnitProtoType.Type.TYPE_SQUARE_BRACKET
                    || ser.mType == UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET
                    || ser.mType == UnitProtoType.Type.TYPE_BRACE
                    || ser.mType == UnitProtoType.Type.TYPE_CLOSE_BRACE)    {
                // only consider the characters thay very clearly to be math formula sign. otherwise,like zero can be misrecognized o, so not considered.
                return 1;
            } else if (ser.mType == UnitProtoType.Type.TYPE_SUBTRACT    // - is needed considering a long line div can occupy a large area.
                    || ser.mType == UnitProtoType.Type.TYPE_ZERO
                    || ser.mType == UnitProtoType.Type.TYPE_SMALL_O
                    || ser.mType == UnitProtoType.Type.TYPE_BIG_O
                    || ser.mType == UnitProtoType.Type.TYPE_SMALL_S
                    || ser.mType == UnitProtoType.Type.TYPE_BIG_S
                    || ser.mType == UnitProtoType.Type.TYPE_ONE
                    || ser.mType == UnitProtoType.Type.TYPE_VERTICAL_LINE
                    //todo dml_changed4 : 将小括号放入易混数字系列，配合clm，mwm进行纠错
                    || ser.mType == UnitProtoType.Type.TYPE_ROUND_BRACKET
                    || ser.mType == UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET
                    /*|| ser.mType == UnitProtoType.Type.TYPE_DOT
                    || ser.mType == UnitProtoType.Type.TYPE_DOT_MULTIPLY*/) {
                // these could be misrecognized numbers. Dot is not included because noise chop in general includes a lot of dots so that it can be a punishment.
                return 0;
            } else {
                return -1;
            }
        }
        return -1;
    }

    //可计算性评估
    // can handle ser == null case.
    public static double calcMathPossibility(StructExprRecog ser) {
        if (ser == null) {
            return 0.0;
        }
        else if (ser.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE) {
            return isMathSignChar(ser);
        }
        else if (ser.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HLINECUT
                || ser.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_GETROOT) {
            // h-line cut and root implie this should be a math formula
            return 1.0;
        }
        //位置判定
        else if (ser.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTCAP
                || ser.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTUNDER
                || ser.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTCAPUNDER
                || ser.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTLOWERNOTE
                || ser.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTLUNOTES
                || ser.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE) {
            StructExprRecog serPrinciple = ser.getPrincipleSER(5);
            if (serPrinciple.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    && (serPrinciple.mType == UnitProtoType.Type.TYPE_INTEGRATE
                        || serPrinciple.mType == UnitProtoType.Type.TYPE_INTEGRATE_CIRCLE
                        || serPrinciple.mType == UnitProtoType.Type.TYPE_BIG_SIGMA
                        || serPrinciple.mType == UnitProtoType.Type.TYPE_BIG_PI)) {
                return 1.0; // like integrate from xxx to xxx.
            } else {
                double dReturn = 0.0;
                double dTotalArea = 0.0;
                for (int idx = 0; idx < ser.mlistChildren.size(); idx ++) {
                    dReturn += calcMathPossibility(ser.mlistChildren.get(idx)) * ser.mlistChildren.get(idx).getArea();
                    dTotalArea += ser.mlistChildren.get(idx).getArea();
                }
                dReturn /= dTotalArea;
                if (ser.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTLOWERNOTE
                    || ser.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTLUNOTES
                    || ser.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE)  {
                    dReturn = (dReturn + 1.0)/2.0;  // extra 1.0 is for the structure weight. Generally lu notes imply a math expression, but there are exceptions.
                }
                return dReturn;
            }
        } else {
            double dReturn = 0.0;
            double dTotalArea = 0.0;
            for (int idx = 0; idx < ser.mlistChildren.size(); idx ++) {
                dReturn += calcMathPossibility(ser.mlistChildren.get(idx)) * ser.mlistChildren.get(idx).getArea();
                dTotalArea += ser.mlistChildren.get(idx).getArea();
            }
            dReturn /= dTotalArea;
            if (ser.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTMATRIX) {
                dReturn = (dReturn + 1.0)/2.0;  // extra 1.0 is for the structure weight if it is a matrix. Generally matrix is a math expression, but there are exceptions.
            }
            return dReturn;
        }
    }
}
