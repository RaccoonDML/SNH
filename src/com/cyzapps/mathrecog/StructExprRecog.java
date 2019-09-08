/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyzapps.mathrecog;

import com.cyzapps.VisualMFP.Position3D;
import com.cyzapps.mathrecog.CharLearningMgr.CharCandidate;
import com.cyzapps.mathrecog.MisrecogWordMgr.LetterCandidates;
import com.cyzapps.mathrecog.MisrecogWordMgr.MisrecogWord;
import com.cyzapps.mathrecog.UnitPrototypeMgr.UnitProtoType;
import com.cyzapps.mathrecog.UnitRecognizer.UnitCandidate;
//import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.util.LinkedList;

/**
 * 表达式结构分类列表/单元素/带上下左右标……
 *
 * @author tonyc
 */
public class StructExprRecog {
    public final static int EXPRRECOGTYPE_ENUMTYPE = 0;
    public final static int EXPRRECOGTYPE_LISTCUT = 1;
    public final static int EXPRRECOGTYPE_HBLANKCUT = 2;
    public final static int EXPRRECOGTYPE_HLINECUT = 3;
    public final static int EXPRRECOGTYPE_HCUTCAP = 4;
    public final static int EXPRRECOGTYPE_HCUTUNDER = 5;
    public final static int EXPRRECOGTYPE_HCUTCAPUNDER = 6;
    public final static int EXPRRECOGTYPE_VBLANKCUT = 10;
    public final static int EXPRRECOGTYPE_VCUTLEFTTOPNOTE = 11;    // first element is left top note, second element is base
    // 指数以及上下标的定义在这里
    public final static int EXPRRECOGTYPE_VCUTUPPERNOTE = 12;   // first element is  base, second element is upper note
    public final static int EXPRRECOGTYPE_VCUTLOWERNOTE = 13;   // first element is base, second element is lower note
    public final static int EXPRRECOGTYPE_VCUTLUNOTES = 14;  // first element is base, second element is lower note, third element is upper note.
    public final static int EXPRRECOGTYPE_VCUTMATRIX = 20;  // each element is a column, each element is Hblankcut with same number (> 1) of rows
    public final static int EXPRRECOGTYPE_MULTIEXPRS = 21;  // each element is a row (expression).
    public final static int EXPRRECOGTYPE_GETROOT = 22; // two or three elements, first is root level if root level is not empty, then is is Enum type sqrt or sqrt left etc., last is the rooted expression.

    public final static int AVG_CHAR_WIDTH_IDX = 0;
    public final static int AVG_CHAR_HEIGHT_IDX = 1;
    public final static int CHAR_CNT_IDX = 2;
    public final static int AVG_NORMAL_CHAR_WIDTH_IDX = 3;
    public final static int AVG_NORMAL_CHAR_HEIGHT_IDX = 4;
    public final static int NORMAL_CHAR_CNT_IDX = 5;
    public final static int AVG_VGAP_IDX = 6;
    public final static int VGAP_CNT_IDX = 7;
    public final static int TOTAL_IDX_CNT = 8;

    public int mnExprRecogType = EXPRRECOGTYPE_ENUMTYPE;

    // 0 means UnitProtoType,
    // 1 means a list of StructuredExprRecog children，but unknown cut mode.

    // 这里定义了水平切割的各种情况

    // 2 means horizontally cut by blank, 空白
    // 3 means horizontally cut by line   横线？
    // 4 means horizontally cut with cap, 上划线
    // 5 means horizontally cut with underscore, 下划线
    // 6 means horizontally cut with cap and underscore 上划线和下划线

    //竖直切割的各种情况
    // 10 means vertically cut,
    // 11 means vertically cut with left top note,
    // 12 means vertically cut with upper note,
    // 13 means vertically cut with lower note,
    // 14 means vertically cut with both upper and lower notes,

    // 20 means matrix (each element is a column),
    // 21 means multi expressions,
    // 22 root.

    public UnitProtoType.Type mType = UnitProtoType.Type.TYPE_UNKNOWN;
    public final static String UNKNOWN_FONT_TYPE = "unknown";
    public String mstrFont = UNKNOWN_FONT_TYPE;
    public LinkedList<StructExprRecog> mlistChildren = new LinkedList<StructExprRecog>();

    private byte[][] mbarrayBiValues = new byte[0][0];    // bivalue matrix, not that this is the original matrix.
    // minimum container
    public int mnLeft = 0;
    public int mnTop = 0;
    public int mnWidth = 0;
    public int mnHeight = 0;

    protected ImageChop mimgChop = null;    // image chop (not null only if the ser type is enum type (single char)).

    protected double mdSimilarity = 1.0; // 1.0 means not similar.

    public byte[][] getBiArray() {
        return mbarrayBiValues;
    }

    public int getRight() {
        return mnLeft + mnWidth - 1;
    }

    public int getRightPlus1() {
        return mnLeft + mnWidth;
    }

    public int getBottom() {
        return mnTop + mnHeight - 1;
    }

    public int getBottomPlus1() {
        return mnTop + mnHeight;
    }

    public int getArea() {
        return mnWidth * mnHeight;
    }

    public ImageChop getImageChop(boolean bMerge2Create) {
        if (!bMerge2Create || mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE) {
            // if do not want to merge or expr recog type is an enumtype.
            return mimgChop;
        } else {
            LinkedList<ImageChop> listAllChildren = new LinkedList<ImageChop>();
            for (StructExprRecog ser : mlistChildren) {
                listAllChildren.add(ser.getImageChop(true));
            }
            if (listAllChildren.size() == 0) {
                return mimgChop;
            } else if (listAllChildren.size() == 1) {
                return listAllChildren.getFirst();
            } else {
                ImageChop imgChopMerged = ExprSeperator.mergeImgChopsWithSameOriginal(listAllChildren);
                return imgChopMerged;
            }
        }
    }

    public double getSimilarity() {
        return mdSimilarity;
    }

    public int getExprRecogType() {
        return mnExprRecogType;
    }

    public boolean isChildListType() {
        if (mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isChildListType(int nExprRecogType) {
        if (nExprRecogType == EXPRRECOGTYPE_ENUMTYPE) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isVCutNonMatrixType() {
        if (mnExprRecogType == EXPRRECOGTYPE_VBLANKCUT
                || mnExprRecogType == EXPRRECOGTYPE_VCUTLEFTTOPNOTE
                || mnExprRecogType == EXPRRECOGTYPE_VCUTUPPERNOTE
                || mnExprRecogType == EXPRRECOGTYPE_VCUTLOWERNOTE
                || mnExprRecogType == EXPRRECOGTYPE_VCUTLUNOTES) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isVCutNonMatrixType(int nExprRecogType) {
        if (nExprRecogType == EXPRRECOGTYPE_VBLANKCUT
                || nExprRecogType == EXPRRECOGTYPE_VCUTLEFTTOPNOTE
                || nExprRecogType == EXPRRECOGTYPE_VCUTUPPERNOTE
                || nExprRecogType == EXPRRECOGTYPE_VCUTLOWERNOTE
                || nExprRecogType == EXPRRECOGTYPE_VCUTLUNOTES) {
            return true;
        } else {
            return false;
        }
    }

    public void setSERPlace(int nLeft, int nTop, int nWidth, int nHeight) {
        mnLeft = nLeft;
        mnTop = nTop;
        mnWidth = nWidth;
        mnHeight = nHeight;
    }

    public void setSERPlace(StructExprRecog ser2Cpy) {
        mnLeft = ser2Cpy.mnLeft;
        mnTop = ser2Cpy.mnTop;
        mnWidth = ser2Cpy.mnWidth;
        mnHeight = ser2Cpy.mnHeight;
    }

    public void setSimilarity(double dSimilarity) {
        mdSimilarity = dSimilarity;
    }

    public StructExprRecog(byte[][] barrayImg) {
        mbarrayBiValues = barrayImg;
    }

    public void setStructExprRecog(UnitProtoType.Type unitType, String strFont, ImageChop imgChop) {
        // this function is just used for change unitprototype, this ser should have been an enum type.
        mnExprRecogType = EXPRRECOGTYPE_ENUMTYPE;
        mType = unitType;
        mstrFont = strFont;
        mimgChop = imgChop;
        mlistChildren = new LinkedList<StructExprRecog>();
    }

    public void setStructExprRecog(UnitProtoType.Type unitType, String strFont,
                                   int nLeft, int nTop, int nWidth, int nHeight,
                                   ImageChop imgChop, double dSimilarity) {
        mnExprRecogType = EXPRRECOGTYPE_ENUMTYPE;
        mType = unitType;//可以改
        mstrFont = strFont;
        mlistChildren = new LinkedList<StructExprRecog>();
        mnLeft = nLeft;
        mnTop = nTop;
        mnWidth = nWidth;
        mnHeight = nHeight;
        mimgChop = imgChop;
        mdSimilarity = dSimilarity;
    }

    // this interface should not be used, always assign cut mode.
    public void setStructExprRecog(LinkedList<StructExprRecog> listChildren) {
        mnExprRecogType = EXPRRECOGTYPE_LISTCUT;
        mType = UnitProtoType.Type.TYPE_UNKNOWN;
        mstrFont = UNKNOWN_FONT_TYPE;
        mlistChildren = new LinkedList<StructExprRecog>();
        mlistChildren.addAll(listChildren);
        int nLeft = Integer.MAX_VALUE, nTop = Integer.MAX_VALUE, nRightPlus1 = Integer.MIN_VALUE, nBottomPlus1 = Integer.MIN_VALUE;
        int nTotalArea = 0, nTotalValidChildren = 0;
        double dSumWeightedSim = 0;
        double dSumSimilarity = 0;
        for (StructExprRecog ser : mlistChildren) {
            if (ser.mnLeft == 0 && ser.mnTop == 0 && ser.mnHeight == 0 && ser.mnWidth == 0) {
                continue;   // this can happen in some extreme case like extract2Recog input is null or empty imageChops
                // to avoid jeapodize the ser place, skip.
            }
            if (ser.mnLeft < nLeft) {
                nLeft = ser.mnLeft;
            }
            if (ser.mnLeft + ser.mnWidth > nRightPlus1) {
                nRightPlus1 = ser.mnLeft + ser.mnWidth;
            }
            if (ser.mnTop < nTop) {
                nTop = ser.mnTop;
            }
            if (ser.mnTop + ser.mnHeight > nBottomPlus1) {
                nBottomPlus1 = ser.mnTop + ser.mnHeight;
            }
            nTotalArea += ser.getArea();
            dSumSimilarity += ser.getArea() * ser.mdSimilarity;
            nTotalValidChildren++;
            dSumSimilarity += ser.mdSimilarity;
        }
        mnLeft = nLeft;
        mnTop = nTop;
        mnWidth = nRightPlus1 - nLeft;
        mnHeight = nBottomPlus1 - nTop;

        mimgChop = null;
        if (nTotalArea > 0) {
            mdSimilarity = dSumSimilarity / nTotalArea;
        } else {
            mdSimilarity = dSumSimilarity / nTotalValidChildren;
        }
    }

    public void setStructExprRecog(LinkedList<StructExprRecog> listChildren, int nCutType) {
        if (isChildListType(nCutType)) {
            mnExprRecogType = nCutType;
        } else {
            mnExprRecogType = EXPRRECOGTYPE_LISTCUT;
        }
        mType = UnitProtoType.Type.TYPE_UNKNOWN;
        mstrFont = UNKNOWN_FONT_TYPE;
        mlistChildren = new LinkedList<StructExprRecog>();
        mlistChildren.addAll(listChildren);
        int nLeft = Integer.MAX_VALUE, nTop = Integer.MAX_VALUE, nRightPlus1 = Integer.MIN_VALUE, nBottomPlus1 = Integer.MIN_VALUE;
        int nTotalArea = 0, nTotalValidChildren = 0;
        double dSumWeightedSim = 0;
        double dSumSimilarity = 0;
        for (StructExprRecog ser : mlistChildren) {
            if (ser.mnLeft == 0 && ser.mnTop == 0 && ser.mnHeight == 0 && ser.mnWidth == 0) {
                continue;   // this can happen in some extreme case like extract2Recog input is null or empty imageChops
                // to avoid jeapodize the ser place, skip.
            }
            if (ser.mnLeft < nLeft) {
                nLeft = ser.mnLeft;
            }
            if (ser.mnLeft + ser.mnWidth > nRightPlus1) {
                nRightPlus1 = ser.mnLeft + ser.mnWidth;
            }
            if (ser.mnTop < nTop) {
                nTop = ser.mnTop;
            }
            if (ser.mnTop + ser.mnHeight > nBottomPlus1) {
                nBottomPlus1 = ser.mnTop + ser.mnHeight;
            }
            nTotalArea += ser.getArea();
            dSumWeightedSim += ser.getArea() * ser.mdSimilarity;
            nTotalValidChildren++;
            dSumSimilarity += ser.mdSimilarity;
        }
        mnLeft = nLeft;
        mnTop = nTop;
        mnWidth = nRightPlus1 - nLeft;
        mnHeight = nBottomPlus1 - nTop;

        mimgChop = null;
        if (nTotalArea > 0) {
            mdSimilarity = dSumWeightedSim / nTotalArea;
        } else {
            mdSimilarity = dSumSimilarity / nTotalValidChildren;
        }
    }

    public void setExprRecogType(int nExprRecogType) {
        mnExprRecogType = nExprRecogType;
    }

    public void changeSEREnumType(UnitProtoType.Type unitType, String strFont) {
        // this function is just used for change unitprototype, this ser should have been an enum type.
        mnExprRecogType = EXPRRECOGTYPE_ENUMTYPE;
        mType = unitType;
        mstrFont = strFont;
        mlistChildren = new LinkedList<StructExprRecog>();
    }

    public UnitProtoType.Type getUnitType() {
        return mType;
    }

    public void setUnitType(UnitProtoType.Type unitType) {
        mType = unitType;
    }

    public String getFont() {
        return mstrFont;
    }

    public LinkedList<StructExprRecog> getChildrenList() {
        return mlistChildren;
    }

    // if nAnalyticMode & 1 == 1, extract principle from H divided sers with cap and/or under
    // if nAnalyticMode & 2 == 2, extract principle from sers with left top note
    // if nAnalyticMode & 4 == 4, extract principle from sers with upper and/or lower note(s)
    // if nAnalyticMode & 8 == 8, extract rooted value from root sers.
    public StructExprRecog getPrincipleSER(int nAnalyticMode) {
        if (mnExprRecogType == EXPRRECOGTYPE_HCUTCAP && (nAnalyticMode & 1) == 1) {
            return mlistChildren.getLast();
        } else if (mnExprRecogType == EXPRRECOGTYPE_HCUTUNDER && (nAnalyticMode & 1) == 1) {
            return mlistChildren.getFirst();
        } else if (mnExprRecogType == EXPRRECOGTYPE_HCUTCAPUNDER && (nAnalyticMode & 1) == 1) {
            return mlistChildren.get(1);
        } else if (mnExprRecogType == EXPRRECOGTYPE_VCUTLEFTTOPNOTE && (nAnalyticMode & 2) == 2) {
            return mlistChildren.getLast();
        } else if (mnExprRecogType == EXPRRECOGTYPE_VCUTUPPERNOTE && (nAnalyticMode & 4) == 4) {
            return mlistChildren.getFirst();
        } else if (mnExprRecogType == EXPRRECOGTYPE_VCUTLOWERNOTE && (nAnalyticMode & 4) == 4) {
            return mlistChildren.getFirst();
        } else if (mnExprRecogType == EXPRRECOGTYPE_VCUTLUNOTES && (nAnalyticMode & 4) == 4) {
            return mlistChildren.getFirst();
        } else if (mnExprRecogType == EXPRRECOGTYPE_GETROOT && (nAnalyticMode & 8) == 8) {
            return mlistChildren.getLast();
        } else {
            return this;
        }
    }

    public Position3D calcCentralPnt() {
        return new Position3D(mnLeft + (mnWidth - 1) / 2.0, mnTop + (mnHeight - 1) / 2.0);
    }

    @Override
    public StructExprRecog clone() {
        StructExprRecog structExprRecog = new StructExprRecog(mbarrayBiValues);
        structExprRecog.mnExprRecogType = mnExprRecogType;
        structExprRecog.mType = mType;
        structExprRecog.mstrFont = mstrFont;
        structExprRecog.mlistChildren.addAll(mlistChildren);
        structExprRecog.mnLeft = mnLeft;
        structExprRecog.mnTop = mnTop;
        structExprRecog.mnWidth = mnWidth;
        structExprRecog.mnHeight = mnHeight;
        structExprRecog.mimgChop = mimgChop;
        structExprRecog.mdSimilarity = mdSimilarity;
        return structExprRecog;
    }

    public void copy(StructExprRecog structExprRecog) {
        mbarrayBiValues = structExprRecog.mbarrayBiValues;
        mnExprRecogType = structExprRecog.mnExprRecogType;
        mType = structExprRecog.mType;
        mstrFont = structExprRecog.mstrFont;
        LinkedList<StructExprRecog> listChildren = new LinkedList<StructExprRecog>();
        listChildren.addAll(structExprRecog.mlistChildren);
        mlistChildren = listChildren;
        mnLeft = structExprRecog.mnLeft;
        mnTop = structExprRecog.mnTop;
        mnWidth = structExprRecog.mnWidth;
        mnHeight = structExprRecog.mnHeight;
        mimgChop = structExprRecog.mimgChop;
        mdSimilarity = structExprRecog.mdSimilarity;
    }

    public void addChild(StructExprRecog serChild) {
        if (isChildListType()) {
            mlistChildren.add(serChild);
        } else {
            StructExprRecog serClone = clone();
            LinkedList<StructExprRecog> listChildren = new LinkedList<StructExprRecog>();
            listChildren.add(serClone);
            listChildren.add(serChild);
            setStructExprRecog(listChildren);
        }
        int nThisRight = mnLeft + mnWidth - 1;
        int nThisBottom = mnTop + mnHeight - 1;
        int nChildRight = serChild.mnLeft + serChild.mnWidth - 1;
        int nChildBottom = serChild.mnTop + serChild.mnHeight - 1;
        int nOverallRight = Math.max(nThisRight, nChildRight);
        int nOverallBottom = Math.max(nThisBottom, nChildBottom);
        mnLeft = Math.min(mnLeft, serChild.mnLeft);
        mnTop = Math.min(mnTop, serChild.mnTop);
        mnWidth = nOverallRight + 1 - mnLeft;
        mnHeight = nOverallBottom + 1 - mnTop;
        int nTotalArea = 0;
        double dSumSimilarity = 0;
        for (StructExprRecog ser : mlistChildren) {
            nTotalArea += ser.getArea();
            dSumSimilarity += ser.getArea() * ser.mdSimilarity;
        }
        if (nTotalArea != 0) {
            mdSimilarity = dSumSimilarity / nTotalArea;
        } else {
            mdSimilarity = 0;   // type is not empty, but actually it is empty, so that similarity is 0.
        }
    }

    public static int getSERStylePosition(StructExprRecog ser) {
        if (ser.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE) {
            if (ser.mType == UnitProtoType.Type.TYPE_SMALL_B
                    || ser.mType == UnitProtoType.Type.TYPE_SMALL_D
                    //|| ser.mType == UnitProtoType.Type.TYPE_SMALL_H
                    || ser.mType == UnitProtoType.Type.TYPE_SMALL_K
                    //|| ser.mType == UnitProtoType.Type.TYPE_SMALL_L
                    || ser.mType == UnitProtoType.Type.TYPE_SMALL_DELTA
                    || ser.mType == UnitProtoType.Type.TYPE_SMALL_LAMBDA
                //|| ser.mType == UnitProtoType.Type.TYPE_SMALL_THETA
            ) {
                return 1;   // 1 means the character is an upper character.
            } else if (
                    /*ser.mType == UnitProtoType.Type.TYPE_SMALL_G
                    || */ser.mType == UnitProtoType.Type.TYPE_SMALL_P
                            || ser.mType == UnitProtoType.Type.TYPE_SMALL_Q
                            || ser.mType == UnitProtoType.Type.TYPE_SMALL_Y
                            || ser.mType == UnitProtoType.Type.TYPE_SMALL_G
                            || ser.mType == UnitProtoType.Type.TYPE_SMALL_GAMMA
                            || ser.mType == UnitProtoType.Type.TYPE_SMALL_ETA
                            || ser.mType == UnitProtoType.Type.TYPE_SMALL_RHO
                            || ser.mType == UnitProtoType.Type.TYPE_SMALL_PHI) {
                return -1;  // -1 means the character is a lower character.
            }
        }
        return 0;
    }

    public static boolean is2SERSameHBlankCut(StructExprRecog ser1, StructExprRecog ser2) {
        if (ser1.mnExprRecogType != EXPRRECOGTYPE_HBLANKCUT || ser1.mlistChildren.size() <= 1
                || ser2.mnExprRecogType != EXPRRECOGTYPE_HBLANKCUT || ser2.mlistChildren.size() <= 1
                || ser1.mlistChildren.size() != ser2.mlistChildren.size()) {
            return false;
        }

        for (int idx = 1; idx < ser1.mlistChildren.size(); idx++) {
            StructExprRecog ser1Above = ser1.mlistChildren.get(idx - 1);
            StructExprRecog ser1Below = ser1.mlistChildren.get(idx);
            StructExprRecog ser2Above = ser2.mlistChildren.get(idx - 1);
            StructExprRecog ser2Below = ser2.mlistChildren.get(idx);

            if (ser1Above.mnTop + ser1Above.mnHeight >= ser2Below.mnTop
                    || ser2Above.mnTop + ser2Above.mnHeight >= ser1Below.mnTop) {
                return false;
            }
        }
        return true;
    }

    // CORE 重构函数
    public StructExprRecog restruct() throws InterruptedException {  // needs original image chop as parameter which will be used for matrix and m exprs.
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
        // basic element return this to save computing time and space
        if (mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE) {
            return this;
        } else if (mlistChildren.size() == 0) {
            StructExprRecog ser = new StructExprRecog(mbarrayBiValues);
            ser.setSERPlace(this);
            ser.setSimilarity(0);   // type unknown, similarity is 0.
            return ser;
        }
        // if a matrix, we cannot convert [x] -> x (e.g. 2 * [2 + 3] -> 2 * 2 + 3 is wrong).
        else if (mlistChildren.size() == 1 && mnExprRecogType != EXPRRECOGTYPE_VCUTMATRIX) {
            StructExprRecog ser = mlistChildren.getFirst().restruct();
            return ser;
        }
        // assume horizontal cut is always clear before call restruct, i.e. hblank, hcap, hunder, hcapunder are not confused.
        //  there is a special case for HBlankCut, Under and cap cut (handwriting divide may miss recognized).
        else if (mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT
                || mnExprRecogType == EXPRRECOGTYPE_HCUTCAP
                || mnExprRecogType == EXPRRECOGTYPE_HCUTCAPUNDER
                || mnExprRecogType == EXPRRECOGTYPE_HCUTUNDER
                || mnExprRecogType == EXPRRECOGTYPE_HLINECUT
                || mnExprRecogType == EXPRRECOGTYPE_MULTIEXPRS
                || mnExprRecogType == EXPRRECOGTYPE_GETROOT
                || mnExprRecogType == EXPRRECOGTYPE_VCUTMATRIX) {
            StructExprRecog[] serarrayNoLnDe = new StructExprRecog[3];
            boolean bIsDivide = isActuallyHLnDivSER(this, serarrayNoLnDe);  //TODO need to do it here as well as in vertical restruct

            StructExprRecog serReturn = new StructExprRecog(mbarrayBiValues);
            LinkedList<StructExprRecog> listCuts = new LinkedList<StructExprRecog>();
            if (bIsDivide) {
                listCuts.add(serarrayNoLnDe[0].restruct());
                listCuts.add(serarrayNoLnDe[1].restruct());
                listCuts.add(serarrayNoLnDe[2].restruct());
            } else {
                for (StructExprRecog ser : mlistChildren) {
                    listCuts.add(ser.restruct());
                }
            }
            if (listCuts.getFirst().getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    && listCuts.getFirst().isPossibleVLnChar()
                    && listCuts.getLast().getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    && listCuts.getLast().isPossibleVLnChar()
                    && (bIsDivide || mnExprRecogType == EXPRRECOGTYPE_HLINECUT) // is actually H-line cut.
                    && mnWidth >= ConstantsMgr.msdPlusHeightWidthRatio * mnHeight
                    && mnWidth <= mnHeight / ConstantsMgr.msdPlusHeightWidthRatio
                    && listCuts.getFirst().mnHeight >= ConstantsMgr.msdPlusTopVLnBtmVLnRatio * listCuts.getLast().mnHeight
                    && listCuts.getFirst().mnHeight <= listCuts.getLast().mnHeight / ConstantsMgr.msdPlusTopVLnBtmVLnRatio
                    && listCuts.getFirst().mnLeft < listCuts.getLast().getRight()
                    && listCuts.getFirst().getRight() > listCuts.getLast().mnLeft
                    && listCuts.getFirst().getBottomPlus1() >= listCuts.get(1).mnTop
                    && listCuts.getLast().mnTop <= listCuts.get(1).getBottomPlus1()) {    // the shape of this ser should match +
                // seems like a + instead of 1/1
                double dSimilarity = (listCuts.getFirst().getArea() * listCuts.getFirst().mdSimilarity
                        + listCuts.get(1).getArea() * listCuts.get(1).mdSimilarity
                        + listCuts.getLast().getArea() * listCuts.getLast().mdSimilarity)
                        / (listCuts.getFirst().getArea() + listCuts.get(1).getArea() + listCuts.getLast().getArea());  // total area should not be zero here.
                // now we merge the three parts into +
                LinkedList<ImageChop> listParts = new LinkedList<ImageChop>();
                ImageChop imgChopTop = listCuts.getFirst().getImageChop(false);
                listParts.add(imgChopTop);
                ImageChop imgChopHLn = listCuts.get(1).getImageChop(false);
                listParts.add(imgChopHLn);
                ImageChop imgChopBottom = listCuts.getLast().getImageChop(false);
                listParts.add(imgChopBottom);
                ImageChop imgChop4SER = ExprSeperator.mergeImgChopsWithSameOriginal(listParts);   // need not to shrink imgChop4SER because it has been min container.
                serReturn.setStructExprRecog(UnitProtoType.Type.TYPE_ADD, UNKNOWN_FONT_TYPE, mnLeft, mnTop, mnWidth, mnHeight, imgChop4SER, dSimilarity);
            }
            //todo: This is a fool rule by LH and will be delete later
            /*Here we add our rule to convert topunder{.,/,.} to div*/
            else if (listCuts.getFirst().mType == UnitProtoType.Type.TYPE_DOT
                    && listCuts.getLast().mType == UnitProtoType.Type.TYPE_DOT
                    && listCuts.get(1).mType == UnitProtoType.Type.TYPE_SUBTRACT) {

                double dSimilarity = (listCuts.getFirst().getArea() * listCuts.getFirst().mdSimilarity
                        + listCuts.get(1).getArea() * listCuts.get(1).mdSimilarity
                        + listCuts.getLast().getArea() * listCuts.getLast().mdSimilarity)
                        / (listCuts.getFirst().getArea() + listCuts.get(1).getArea() + listCuts.getLast().getArea());  // total area should not be zero here.
                System.out.println("Now we change it");
                LinkedList<ImageChop> listParts = new LinkedList<ImageChop>();
                ImageChop imgChopTop = listCuts.getFirst().getImageChop(false);
                listParts.add(imgChopTop);
                ImageChop imgChopHLn = listCuts.get(1).getImageChop(false);
                listParts.add(imgChopHLn);
                ImageChop imgChopBottom = listCuts.getLast().getImageChop(false);
                listParts.add(imgChopBottom);
                ImageChop imgChop4SER = ExprSeperator.mergeImgChopsWithSameOriginal(listParts);
                serReturn.setStructExprRecog(UnitProtoType.Type.TYPE_DIVIDE, UNKNOWN_FONT_TYPE, mnLeft, mnTop, mnWidth, mnHeight, imgChop4SER, dSimilarity);
            } else if (bIsDivide) {
                // is actually an h line cut, i.e. div.
                serReturn.setStructExprRecog(listCuts, EXPRRECOGTYPE_HLINECUT);
            } else {
                serReturn.setStructExprRecog(listCuts, mnExprRecogType);
                serReturn = serReturn.identifyHSeperatedChar(); // special treatment for like =, i, ...
                if (serReturn.mnExprRecogType != EXPRRECOGTYPE_ENUMTYPE) {
                    serReturn = serReturn.identifyStrokeBrokenChar();   // convert like 7 underline to 2.
                }
            }

            return serReturn;
        } else {
            // listcut, vblankcut, vcutlefttop, vcutlower, vcutupper, vcutlowerupper
            // the children are vertically cut. if cut mode is LISTCUT, also treated as vertically cut.
            // make the following assumptions for horizontally cut children
            // 1. the h-cut modes have been clear (i.e. normal horizontally cut, cap, undercore etc)
            // 2. the getroot, vcutmatrix and multiexprs have been clear.
            // 3. the v-cut modes are not clear

            // step 1. merge all the vertically cut children into main mlistChildren, and for all h-blank cut children, merge all of its h-blank cut
            // into itself.
            LinkedList<StructExprRecog> listMergeVCutChildren1 = new LinkedList<StructExprRecog>();
            listMergeVCutChildren1.addAll(mlistChildren);
            LinkedList<StructExprRecog> listMergeVCutChildren = new LinkedList<StructExprRecog>();
            boolean bHasVCutChild = true;
            while (bHasVCutChild) {
                bHasVCutChild = false;
                for (int idx = 0; idx < listMergeVCutChildren1.size(); idx++) {
                    if (/*listMergeVCutChildren1.get(idx).mnExprRecogType == EXPRRECOGTYPE_LISTCUT  // treat list cut like vblankcut only when we print its value to string
                            || */listMergeVCutChildren1.get(idx).isVCutNonMatrixType()) {
                        for (int idx1 = 0; idx1 < listMergeVCutChildren1.get(idx).mlistChildren.size(); idx1++) {
                            if (/*listMergeVCutChildren1.get(idx).mlistChildren.get(idx1).mnExprRecogType == EXPRRECOGTYPE_LISTCUT  // treat list cut like vblankcut only when we print its value to string
                                    || */listMergeVCutChildren1.get(idx).mlistChildren.get(idx1).isVCutNonMatrixType()) {
                                bHasVCutChild = true;
                            }
                            int idx2 = listMergeVCutChildren.size() - 1;
                            StructExprRecog serToAdd = listMergeVCutChildren1.get(idx).mlistChildren.get(idx1);
                            for (; idx2 >= 0; idx2--) {
                                double dListChildCentral = listMergeVCutChildren.get(idx2).mnLeft + listMergeVCutChildren.get(idx2).mnWidth / 2.0;
                                double dToAddCentral = serToAdd.mnLeft + serToAdd.mnWidth / 2.0;
                                if (dListChildCentral < dToAddCentral) {
                                    break;
                                }
                            }
                            listMergeVCutChildren.add(idx2 + 1, serToAdd);
                        }
                        // do not consider hblankcut list size == 1 case because it is impossible.
                    } else {
                        int idx2 = listMergeVCutChildren.size() - 1;
                        StructExprRecog serToAdd = listMergeVCutChildren1.get(idx);
                        for (; idx2 >= 0; idx2--) {
                            double dListChildCentral = listMergeVCutChildren.get(idx2).mnLeft + listMergeVCutChildren.get(idx2).mnWidth / 2.0;
                            double dToAddCentral = serToAdd.mnLeft + serToAdd.mnWidth / 2.0;
                            if (dListChildCentral < dToAddCentral) {
                                break;
                            }
                        }
                        listMergeVCutChildren.add(idx2 + 1, serToAdd);
                    }
                }
                if (bHasVCutChild) {
                    listMergeVCutChildren1.clear();
                    listMergeVCutChildren1 = listMergeVCutChildren;
                    listMergeVCutChildren = new LinkedList<StructExprRecog>();
                }
            }


            for (int idx = 0; idx < listMergeVCutChildren.size(); idx++) {
                StructExprRecog serChild = listMergeVCutChildren.get(idx);
                if (serChild.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT) {
                    LinkedList<StructExprRecog> listHBlankCutChildren1 = new LinkedList<StructExprRecog>();
                    listHBlankCutChildren1.addAll(serChild.mlistChildren);
                    LinkedList<StructExprRecog> listHBlankCutChildren = new LinkedList<StructExprRecog>();
                    boolean bHasHBlankCutChild = true;
                    while (bHasHBlankCutChild) {
                        bHasHBlankCutChild = false;
                        for (int idx0 = 0; idx0 < listHBlankCutChildren1.size(); idx0++) {
                            if (listHBlankCutChildren1.get(idx0).mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT) {
                                for (int idx1 = 0; idx1 < listHBlankCutChildren1.get(idx0).mlistChildren.size(); idx1++) {
                                    if (listHBlankCutChildren1.get(idx0).mlistChildren.get(idx1).mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT) {
                                        bHasHBlankCutChild = true;
                                    }
                                    listHBlankCutChildren.add(listHBlankCutChildren1.get(idx0).mlistChildren.get(idx1));
                                }
                            } else {
                                listHBlankCutChildren.add(listHBlankCutChildren1.get(idx0));
                            }
                        }
                        if (bHasHBlankCutChild) {
                            listHBlankCutChildren1.clear();
                            listHBlankCutChildren1 = listHBlankCutChildren;
                            listHBlankCutChildren = new LinkedList<StructExprRecog>();
                        }
                    }
                    StructExprRecog serNewChild = new StructExprRecog(serChild.mbarrayBiValues);
                    serNewChild.setStructExprRecog(listHBlankCutChildren, EXPRRECOGTYPE_HBLANKCUT);
                    listMergeVCutChildren.set(idx, serNewChild);
                }
            }


            // step 2: identify upper notes or lower notes, This is raw upper lower identification procedure. h-cut
            // children are not analyzed.
            LinkedList<Integer> listCharLevel = new LinkedList<Integer>();
            LinkedList<Integer> list1SideAnchorBaseIdx = new LinkedList<Integer>();
            // first of all, find out the highest child which must be base
            int nBiggestChildHeight = 0;
            int nBiggestChildIdx = 0;
            int nHighestNonHDivChildHeight = -1;
            int nHighestNonHDivChildIdx = -1;
            for (int idx = 0; idx < listMergeVCutChildren.size(); idx++) {
                if (listMergeVCutChildren.get(idx).mnHeight > nBiggestChildHeight) {
                    nBiggestChildIdx = idx;
                    nBiggestChildHeight = listMergeVCutChildren.get(idx).mnHeight;
                }
                if (isHDivCannotBeBaseAnchorSER(listMergeVCutChildren.get(idx)) == false
                        && listMergeVCutChildren.get(idx).mnHeight > nHighestNonHDivChildHeight) {
                    // non-hdiv child could be cap or under.
                    nHighestNonHDivChildIdx = idx;
                    nHighestNonHDivChildHeight = listMergeVCutChildren.get(idx).mnHeight;
                }
            }
            boolean bHasNonHDivBaseChild = false;
            // ok, if we have a non h-div child, biggest child is the biggest non h-div child. Otherwise, use biggest child.
            if (nHighestNonHDivChildIdx >= 0) {
                nBiggestChildHeight = nHighestNonHDivChildHeight;
                nBiggestChildIdx = nHighestNonHDivChildIdx;
                bHasNonHDivBaseChild = true;
            }

            listCharLevel.add(0);
            list1SideAnchorBaseIdx.add(nBiggestChildIdx);

            // from highest child to right
            StructExprRecog serBiggestChild = listMergeVCutChildren.get(nBiggestChildIdx);
            BLUCharIdentifier bluCIBiggest = new BLUCharIdentifier(serBiggestChild);
            BLUCharIdentifier bluCI = bluCIBiggest.clone();
            int nLastBaseHeight = listMergeVCutChildren.get(nBiggestChildIdx).mnHeight;
            for (int idx = nBiggestChildIdx + 1; idx < listMergeVCutChildren.size(); idx++) {
                StructExprRecog ser = listMergeVCutChildren.get(idx);
                if (idx > nBiggestChildIdx + 1  // if idx == nBiggestChildIdx + 1, we have already used the bluCIBiggest.
                        && ((bHasNonHDivBaseChild && listCharLevel.size() > 0 && listCharLevel.getLast() == 0
                        && !isHDivCannotBeBaseAnchorSER(listMergeVCutChildren.get(idx - 1)))
                        || (!bHasNonHDivBaseChild && listCharLevel.size() > 0 && listCharLevel.getLast() == 0))) {
                    StructExprRecog serBase = listMergeVCutChildren.get(idx - 1);
                    bluCI.setBLUCharIdentifier(serBase);
                    nLastBaseHeight = listMergeVCutChildren.get(idx - 1).mnHeight;
                    list1SideAnchorBaseIdx.add(idx - 1);
                } else {
                    list1SideAnchorBaseIdx.add(list1SideAnchorBaseIdx.getLast());
                }
                int thisCharLevel = bluCI.calcCharLevel(ser);     // 0 means base char, 1 means upper note, -1 means lower note

                // only from left to right we do the following work.
                if (thisCharLevel == 0 && listCharLevel.size() > 0
                        && ((listCharLevel.getLast() != 0
                        && nLastBaseHeight * ConstantsMgr.msdLUNoteHeightRatio2Base * ConstantsMgr.msdLUNoteHeightRatio2Base >= ser.mnHeight)
                        || (listMergeVCutChildren.get(idx - 1).mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HBLANKCUT
                        && listMergeVCutChildren.get(idx - 1).mlistChildren.size() > 1
                        && nLastBaseHeight * ConstantsMgr.msdLUNoteHeightRatio2Base / listMergeVCutChildren.get(idx - 1).mlistChildren.size() >= ser.mnHeight))
                ) {
                    // to see if it could be upper lower note or lower upper note.
                    if (listCharLevel.getLast() == 1 && ser.getBottom() <= bluCI.mdUpperNoteLBoundThresh) {
                        thisCharLevel = 1;
                    } else if (listCharLevel.getLast() == -1 && ser.mnTop >= bluCI.mdLowerNoteUBoundThresh) {
                        thisCharLevel = -1;
                    } else if (listMergeVCutChildren.get(idx - 1).mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HBLANKCUT
                            && listMergeVCutChildren.get(idx - 1).mlistChildren.size() > 1) {   // hblankcut type.
                        // first of all, find out which h-blank-div child is closest to it.
                        int nMinDistance = Integer.MAX_VALUE;
                        int nMinDistanceIdx = 0;
                        double dAvgChildHeight = 0;
                        for (int idx2 = 0; idx2 < listMergeVCutChildren.get(idx - 1).mlistChildren.size(); idx2++) {
                            int nDistance = Math.abs(ser.mnTop + ser.getBottomPlus1()
                                    - listMergeVCutChildren.get(idx - 1).mlistChildren.get(idx2).mnTop
                                    - listMergeVCutChildren.get(idx - 1).mlistChildren.get(idx2).getBottomPlus1());
                            if (nDistance <= nMinDistance) { // allow a little bit overhead
                                nMinDistance = nDistance;
                                nMinDistanceIdx = idx2;
                            }
                            dAvgChildHeight += listMergeVCutChildren.get(idx - 1).mlistChildren.get(idx2).mnHeight;
                        }
                        dAvgChildHeight /= listMergeVCutChildren.get(idx - 1).mlistChildren.size();
                        if (dAvgChildHeight * ConstantsMgr.msdLUNoteHeightRatio2Base >= ser.mnHeight) {
                            StructExprRecog serHCutChild = listMergeVCutChildren.get(idx - 1).mlistChildren.get(nMinDistanceIdx);
                            int thisHCutChildCharLevel = bluCI.calcCharLevel(serHCutChild);
                            if (thisHCutChildCharLevel == 0) {
                                // this char level is calculated from its closest last ser if it is base.
                                BLUCharIdentifier bluCIThisHCutChild = new BLUCharIdentifier(serHCutChild);
                                thisCharLevel = bluCIThisHCutChild.calcCharLevel(ser);
                            } else {
                                thisCharLevel = thisHCutChildCharLevel; // this char level is same as its closest last ser if it is not base.
                            }
                        }
                    }
                }
                listCharLevel.add(thisCharLevel);
            }

            // from highest char to left
            bluCI = bluCIBiggest.clone();
            for (int idx = nBiggestChildIdx - 1; idx >= 0; idx--) {
                StructExprRecog ser = listMergeVCutChildren.get(idx);
                if ((bHasNonHDivBaseChild && listCharLevel.size() > 0 && listCharLevel.getFirst() == 0
                        && !isHDivCannotBeBaseAnchorSER(listMergeVCutChildren.get(idx + 1)))
                        || (!bHasNonHDivBaseChild && listCharLevel.size() > 0 && listCharLevel.getFirst() == 0)) {
                    StructExprRecog serBase = listMergeVCutChildren.get(idx + 1);
                    bluCI.setBLUCharIdentifier(serBase);
                    list1SideAnchorBaseIdx.addFirst(idx + 1);
                } else {
                    list1SideAnchorBaseIdx.addFirst(list1SideAnchorBaseIdx.getFirst());
                }
                int thisCharLevel = bluCI.calcCharLevel(ser);     // 0 means base char, 1 means upper note, -1 means lower note
                listCharLevel.addFirst(thisCharLevel);
            }

            // from left to highest char
            bluCI = bluCIBiggest.clone();
            nLastBaseHeight = 0;//  to avoid the first char misrecognized to note instead of base,
            // do not use listMergeVCutChildren.get(nBiggestChildIdx).mnHeight;
            // however, from right to left identification can still misrecognize it to note
            // so we need further check later on.
            for (int idx = 1; idx < nBiggestChildIdx; idx++) {
                StructExprRecog ser = listMergeVCutChildren.get(idx);
                if ((bHasNonHDivBaseChild && listCharLevel.get(idx - 1) == 0 && !isHDivCannotBeBaseAnchorSER(listMergeVCutChildren.get(idx - 1)))
                        || (!bHasNonHDivBaseChild && listCharLevel.get(idx - 1) == 0)) {   // base char
                    StructExprRecog serBase = listMergeVCutChildren.get(idx - 1);
                    bluCI.setBLUCharIdentifier(serBase);
                    nLastBaseHeight = listMergeVCutChildren.get(idx - 1).mnHeight;
                }
                int thisCharLevel = bluCI.calcCharLevel(ser);     // 0 means base char, 1 means upper note, -1 means lower note

                // only from left to right we do the following work.
                if (thisCharLevel == 0 && idx > 0
                        && (listCharLevel.get(idx - 1) != 0
                        || (listMergeVCutChildren.get(idx - 1).mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HBLANKCUT
                        && listMergeVCutChildren.get(idx - 1).mlistChildren.size() > 1))
                        && nLastBaseHeight * ConstantsMgr.msdLUNoteHeightRatio2Base * ConstantsMgr.msdLUNoteHeightRatio2Base >= ser.mnHeight) {
                    // to see if it could be upper lower note or lower upper note.
                    if (listCharLevel.get(idx - 1) == 1 && ser.getBottom() <= bluCI.mdUpperNoteLBoundThresh) {
                        thisCharLevel = 1;
                    } else if (listCharLevel.get(idx - 1) == -1 && ser.mnTop >= bluCI.mdLowerNoteUBoundThresh) {
                        thisCharLevel = -1;
                    } else if (listMergeVCutChildren.get(idx - 1).mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HBLANKCUT
                            && listMergeVCutChildren.get(idx - 1).mlistChildren.size() > 1) {   // hblankcut type.
                        // first of all, find out which h-blank-div child is closest to it.
                        int nMinDistance = Integer.MAX_VALUE;
                        int nMinDistanceIdx = 0;
                        for (int idx2 = 0; idx2 < listMergeVCutChildren.get(idx - 1).mlistChildren.size(); idx2++) {
                            int nDistance = Math.abs(ser.mnTop + ser.getBottomPlus1()
                                    - listMergeVCutChildren.get(idx - 1).mlistChildren.get(idx2).mnTop
                                    - listMergeVCutChildren.get(idx - 1).mlistChildren.get(idx2).getBottomPlus1());
                            if (nDistance <= nMinDistance) { // allow a little bit overhead
                                nMinDistance = nDistance;
                                nMinDistanceIdx = idx2;
                            }
                        }
                        StructExprRecog serHCutChild = listMergeVCutChildren.get(idx - 1).mlistChildren.get(nMinDistanceIdx);
                        int thisHCutChildCharLevel = bluCI.calcCharLevel(serHCutChild);
                        if (thisHCutChildCharLevel == 0) {
                            // this char level is calculated from its closest last ser if it is base.
                            BLUCharIdentifier bluCIThisHCutChild = new BLUCharIdentifier(serHCutChild);
                            thisCharLevel = bluCIThisHCutChild.calcCharLevel(ser);
                        } else {
                            thisCharLevel = thisHCutChildCharLevel; // this char level is same as its closest last ser if it is not base.
                        }
                    }
                }
                int nLastSideAnchorBaseType = listMergeVCutChildren.get(list1SideAnchorBaseIdx.get(idx)).mnExprRecogType;
                int nThisSideAnchorBaseType = bluCI.mserBase.mnExprRecogType;
                if (listCharLevel.get(idx) != thisCharLevel) {
                    if ((nLastSideAnchorBaseType == EXPRRECOGTYPE_HCUTCAP
                            || nLastSideAnchorBaseType == EXPRRECOGTYPE_HCUTUNDER
                            || nLastSideAnchorBaseType == EXPRRECOGTYPE_HCUTCAPUNDER)
                            && (nThisSideAnchorBaseType != EXPRRECOGTYPE_HCUTCAP
                            && nThisSideAnchorBaseType != EXPRRECOGTYPE_HCUTUNDER
                            && nThisSideAnchorBaseType != EXPRRECOGTYPE_HCUTCAPUNDER)) {
                        // h-cut is always not accurate
                        listCharLevel.set(idx, thisCharLevel);
                    } else if ((nThisSideAnchorBaseType == EXPRRECOGTYPE_HCUTCAP
                            || nThisSideAnchorBaseType == EXPRRECOGTYPE_HCUTUNDER
                            || nThisSideAnchorBaseType == EXPRRECOGTYPE_HCUTCAPUNDER)
                            && (nLastSideAnchorBaseType != EXPRRECOGTYPE_HCUTCAP
                            && nLastSideAnchorBaseType != EXPRRECOGTYPE_HCUTUNDER
                            && nLastSideAnchorBaseType != EXPRRECOGTYPE_HCUTCAPUNDER)) {
                        // do not change.
                    } else if (listCharLevel.get(idx) == 0) {   // if from one side this char is not base char, then it is not base char
                        listCharLevel.set(idx, thisCharLevel);
                    } else if (thisCharLevel != 0) {   // if one side is 1 while the other side is - 1, then use left side value
                        listCharLevel.set(idx, thisCharLevel);
                    } else if (listCharLevel.get(idx) != 0 && list1SideAnchorBaseIdx.get(idx) > idx + 1) {
                        // if see from right this ser is a note and right anchor is not next to this ser, we use left side value.
                        listCharLevel.set(idx, thisCharLevel);
                    }
                }
            }

            // from right to highest char seems not necessary.

            boolean bNeedReidentifyFirst = false;
            // now the first child may be misrecognized to be -1 or 1, correct it and reidentify char level from 0 to the original first base
            if (listCharLevel.getFirst() == -1
                    && (listMergeVCutChildren.size() < 2    // only have one child, so it has to be base
                    || listMergeVCutChildren.getFirst().mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_ENUMTYPE   // could be base of base**something
                    || ((listMergeVCutChildren.getFirst().isLetterChar() || listMergeVCutChildren.getFirst().isNumberChar())
                    && listMergeVCutChildren.get(1).mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_HCUTCAPUNDER
                    && listMergeVCutChildren.get(1).mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_HCUTUNDER))) {   // could be base of base ** something
                listCharLevel.set(0, 0);
                bNeedReidentifyFirst = true;
            } else if (listCharLevel.getFirst() == 1) {
                boolean bIsRoot = true, bIsTemperature = true;
                int idx = 1;
                for (; idx < listCharLevel.size(); idx++) {
                    if (listCharLevel.get(idx) != 1) {
                        break;
                    }
                }
                if (idx == listCharLevel.size() || listCharLevel.get(idx) != 0
                        || listMergeVCutChildren.get(idx).mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_GETROOT) {
                    bIsRoot = false;
                }
                if (listMergeVCutChildren.size() < 2
                        || listMergeVCutChildren.getFirst().mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                        || (listMergeVCutChildren.getFirst().mType != UnitProtoType.Type.TYPE_SMALL_O
                        && listMergeVCutChildren.getFirst().mType != UnitProtoType.Type.TYPE_BIG_O
                        && listMergeVCutChildren.getFirst().mType != UnitProtoType.Type.TYPE_ZERO)
                        || listMergeVCutChildren.get(1).mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                        || (listMergeVCutChildren.get(1).mType != UnitProtoType.Type.TYPE_SMALL_C
                        && listMergeVCutChildren.get(1).mType != UnitProtoType.Type.TYPE_BIG_C
                        && listMergeVCutChildren.get(1).mType != UnitProtoType.Type.TYPE_BIG_F)) {
                    bIsTemperature = false;
                }
                if (!bIsRoot && !bIsTemperature   // if root or temperature, then it should be upper note
                        && (listMergeVCutChildren.size() < 2       // only have one child, so it has to be base
                        || (listMergeVCutChildren.getFirst().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                        && listMergeVCutChildren.getFirst().isLetterChar()
                        && listMergeVCutChildren.get(1).mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_HCUTCAP
                        && listMergeVCutChildren.get(1).mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_HCUTCAPUNDER)   // could be base_something
                )) {
                    listCharLevel.set(0, 0);
                    bNeedReidentifyFirst = true;
                }
            }
            if (bNeedReidentifyFirst) {

                int idx = 1;
                while (idx < listMergeVCutChildren.size()) {
                    if ((listCharLevel.get(idx - 1) == 0 && !isHDivCannotBeBaseAnchorSER(listMergeVCutChildren.get(idx - 1)))
                            || ((idx - 1) == 0)) {   // base char can be anchor or first ser
                        StructExprRecog serBase = listMergeVCutChildren.get(idx - 1);
                        bluCI.setBLUCharIdentifier(serBase);
                        nLastBaseHeight = listMergeVCutChildren.get(idx - 1).mnHeight;
                    }

                    StructExprRecog ser = listMergeVCutChildren.get(idx);
                    int thisCharLevel = bluCI.calcCharLevel(ser);     // 0 means base char, 1 means upper note, -1 means lower note

                    // only from left to right we do the following work.
                    if (thisCharLevel == 0 && idx > 0
                            && (listCharLevel.get(idx - 1) != 0
                            || (listMergeVCutChildren.get(idx - 1).mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HBLANKCUT
                            && listMergeVCutChildren.get(idx - 1).mlistChildren.size() > 1))
                            && nLastBaseHeight * ConstantsMgr.msdLUNoteHeightRatio2Base * ConstantsMgr.msdLUNoteHeightRatio2Base >= ser.mnHeight) {
                        // to see if it could be upper lower note or lower upper note.
                        if (listCharLevel.get(idx - 1) == 1 && ser.getBottom() <= bluCI.mdUpperNoteLBoundThresh) {
                            thisCharLevel = 1;
                        } else if (listCharLevel.get(idx - 1) == -1 && ser.mnTop >= bluCI.mdLowerNoteUBoundThresh) {
                            thisCharLevel = -1;
                        } else if (listMergeVCutChildren.get(idx - 1).mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HBLANKCUT
                                && listMergeVCutChildren.get(idx - 1).mlistChildren.size() > 1) {   // hblankcut type.
                            // first of all, find out which h-blank-div child is closest to it.
                            int nMinDistance = Integer.MAX_VALUE;
                            int nMinDistanceIdx = 0;
                            for (int idx2 = 0; idx2 < listMergeVCutChildren.get(idx - 1).mlistChildren.size(); idx2++) {
                                int nDistance = Math.abs(ser.mnTop + ser.getBottomPlus1()
                                        - listMergeVCutChildren.get(idx - 1).mlistChildren.get(idx2).mnTop
                                        - listMergeVCutChildren.get(idx - 1).mlistChildren.get(idx2).getBottomPlus1());
                                if (nDistance <= nMinDistance) { // allow a little bit overhead
                                    nMinDistance = nDistance;
                                    nMinDistanceIdx = idx2;
                                }
                            }
                            StructExprRecog serHCutChild = listMergeVCutChildren.get(idx - 1).mlistChildren.get(nMinDistanceIdx);
                            int thisHCutChildCharLevel = bluCI.calcCharLevel(serHCutChild);
                            if (thisHCutChildCharLevel == 0) {
                                // this char level is calculated from its closest last ser if it is base.
                                BLUCharIdentifier bluCIThisHCutChild = new BLUCharIdentifier(serHCutChild);
                                thisCharLevel = bluCIThisHCutChild.calcCharLevel(ser);
                            } else {
                                thisCharLevel = thisHCutChildCharLevel; // this char level is same as its closest last ser if it is not base.
                            }
                        }
                    }
                    if (listCharLevel.get(idx) != thisCharLevel) {
                        // do not consider the impact seen from the other side, even the other side is root or Fahranheit or celcius
                        // this is because if the other side is root or Fahrenheit or celcius, if this level is -1, then it is -1
                        // anyway, if it is 0, because the left most should not be 1 seen from the other side, so doesn't matter
                        // if it is 1, then Fahranheit and celcius and root can handle.
                        listCharLevel.set(idx, thisCharLevel);
                    } else if (thisCharLevel == 0) {
                        break;  // this char level is base and is the same as before adjusting, so no need to go futher.
                    }
                    idx++;
                }
                // need not to go through from right to left again.
            }

            // step 3. idenitify the mode: matrix mode, multi-expr mode or normal mode
            LinkedList<StructExprRecog> listMergeMatrixMExprs = new LinkedList<StructExprRecog>();
            LinkedList<Integer> listMergeMMLevel = new LinkedList<Integer>();
            for (int idx = 0; idx < listMergeVCutChildren.size(); idx++) {
                if (listCharLevel.get(idx) == 0) {
                    //restruct hdivs first, change like hcap(cap, hblank) to hblank(hcap, ...).
                    // otherwise, multi expressions can not be correctly identified.
                    StructExprRecog serPreRestructedHCut = preRestructHDivSer4MatrixMExprs(listMergeVCutChildren.get(idx));
                    if (serPreRestructedHCut != listMergeVCutChildren.get(idx)) {
                        listMergeVCutChildren.set(idx, serPreRestructedHCut);
                    }
                }
            }
            lookForMatrixMExprs(listMergeVCutChildren, listCharLevel, listMergeMatrixMExprs, listMergeMMLevel);

            // step 4: identify upper notes or lower notes.
            LinkedList<StructExprRecog> listBaseULIdentified = new LinkedList<StructExprRecog>();

            listCharLevel.clear();
            int idx0 = 0;
            // step 1: find the first real base ser, real base means a base ser which would be better if it is not hblankcut
            for (; idx0 < listMergeMatrixMExprs.size(); idx0++) {
                if (listMergeMMLevel.get(idx0) == 0 && listMergeMatrixMExprs.get(idx0).mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_HBLANKCUT) {
                    break;
                }
            }
            if (idx0 == listMergeMatrixMExprs.size()) {
                // no non-hblankcut base, so find hblankcut base instead.
                for (idx0 = 0; idx0 < listMergeMatrixMExprs.size(); idx0++) {
                    if (listMergeMMLevel.get(idx0) == 0) {
                        break;
                    }
                }
            }
            // there should be a base, so we need not to worry about idx0 == listMergeMatrixMExprs.size()
            int nIdxFirstBaseChar = idx0;
            for (idx0 = 0; idx0 <= nIdxFirstBaseChar; idx0++) {
                listBaseULIdentified.add(listMergeMatrixMExprs.get(idx0));
                listCharLevel.add(listMergeMMLevel.get(idx0));
            }

            for (; idx0 < listMergeMatrixMExprs.size(); idx0++) {
                StructExprRecog ser = listMergeMatrixMExprs.get(idx0);
                if (idx0 > 0 && listCharLevel.getLast() == 0) {
                    StructExprRecog serBase = listBaseULIdentified.getLast();
                    bluCI.setBLUCharIdentifier(serBase);
                }
                if (ser.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT) {
                    ser = ser.identifyHSeperatedChar();
                    // find out if they are special char or not. have to do it here before child restruct coz if it is =, its position may change later.
                }
                if (ser.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT) {
                    StructExprRecog[] serarrayNoLnDe = new StructExprRecog[3];
                    boolean bIsDivide = isActuallyHLnDivSER(ser, serarrayNoLnDe);
                    //need to do it here as well as in h-cut restruct because h-cut restruct may for independent h-cut ser so never come here.
                    if (bIsDivide) {
                        LinkedList<StructExprRecog> listCuts = new LinkedList<StructExprRecog>();
                        listCuts.add(serarrayNoLnDe[0].restruct());
                        listCuts.add(serarrayNoLnDe[1].restruct());
                        listCuts.add(serarrayNoLnDe[2].restruct());
                        ser.setStructExprRecog(listCuts, EXPRRECOGTYPE_HLINECUT);
                    }
                }
                if (ser.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT) {    // special treatment for hblankcut
                    LinkedList<StructExprRecog> listSerTopParts = new LinkedList<StructExprRecog>();
                    LinkedList<StructExprRecog> listSerBaseParts = new LinkedList<StructExprRecog>();
                    LinkedList<StructExprRecog> listSerBottomParts = new LinkedList<StructExprRecog>();
                    for (int idx1 = 0; idx1 < ser.mlistChildren.size(); idx1++) {
                        StructExprRecog serChild = ser.mlistChildren.get(idx1);
                        if (bluCI.isUpperNote(serChild)) {
                            listSerTopParts.add(serChild);
                        } else if (bluCI.isLowerNote(serChild)) {
                            listSerBottomParts.add(serChild);
                        } else {
                            listSerBaseParts.add(serChild);
                        }
                    }
                    // add the parts into base-upper-lower list following the order bottom->top->base.
                    if (listSerBottomParts.size() > 0) {
                        StructExprRecog serChildBottomPart = new StructExprRecog(ser.getBiArray());
                        if (listSerBottomParts.size() == 1) {
                            serChildBottomPart = listSerBottomParts.getFirst();
                        } else {
                            serChildBottomPart.setStructExprRecog(listSerBottomParts, EXPRRECOGTYPE_HBLANKCUT);
                        }
                        listBaseULIdentified.add(serChildBottomPart);
                        listCharLevel.add(-1);
                    }
                    if (listSerTopParts.size() > 0) {
                        StructExprRecog serChildTopPart = new StructExprRecog(ser.getBiArray());
                        if (listSerTopParts.size() == 1) {
                            serChildTopPart = listSerTopParts.getFirst();
                        } else {
                            serChildTopPart.setStructExprRecog(listSerTopParts, EXPRRECOGTYPE_HBLANKCUT);
                        }
                        listBaseULIdentified.add(serChildTopPart);
                        listCharLevel.add(1);
                    }
                    if (listSerBaseParts.size() > 0) {
                        StructExprRecog serChildBasePart = new StructExprRecog(ser.getBiArray());
                        if (listSerBaseParts.size() == 1) {
                            serChildBasePart = listSerBaseParts.getFirst();
                        } else {
                            serChildBasePart.setStructExprRecog(listSerBaseParts, EXPRRECOGTYPE_HBLANKCUT);
                        }
                        listBaseULIdentified.add(serChildBasePart);
                        listCharLevel.add(0);
                    }
                } else {
                    listBaseULIdentified.add(ser);
                    listCharLevel.add(listMergeMMLevel.get(idx0));
                }
            }

            // step 4.1 special treatment for upper or lower note divided characters like j, i is different coz very unlikely i's dot will be looked like a independent upper note.
            for (int idx = 0; idx < listBaseULIdentified.size(); idx++) {
                if (listCharLevel.get(idx) != 0 || listBaseULIdentified.get(idx).mnExprRecogType != EXPRRECOGTYPE_ENUMTYPE) {
                    continue;   // need to find base and the base has to be a single char.
                }
                StructExprRecog serBase = listBaseULIdentified.get(idx);
                if (idx < listBaseULIdentified.size() - 2 && listCharLevel.get(idx + 1) == -1 && listCharLevel.get(idx + 2) == 0
                        && serBase.isPossibleNumberChar() && listBaseULIdentified.get(idx + 2).isPossibleNumberChar()) {
                    // left and right are both number char, middle is a lower note
                    StructExprRecog serPossibleDot = listBaseULIdentified.get(idx + 1);
                    StructExprRecog serNextBase = listBaseULIdentified.get(idx + 2);
                    double dWOverHThresh = ConstantsMgr.msdExtendableCharWOverHThresh / ConstantsMgr.msdCharWOverHMaxSkewRatio;
                    if (serPossibleDot.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                            && serBase.mnHeight > ConstantsMgr.msdNeighborHeight2PossDotHeight * serPossibleDot.mnHeight
                            && serNextBase.mnHeight > ConstantsMgr.msdNeighborHeight2PossDotHeight * serPossibleDot.mnHeight
                            && serBase.mnHeight > ConstantsMgr.msdNeighborHeight2PossDotWidth * serPossibleDot.mnWidth
                            && serNextBase.mnHeight > ConstantsMgr.msdNeighborHeight2PossDotWidth * serPossibleDot.mnWidth
                            && serPossibleDot.mnWidth < dWOverHThresh * serPossibleDot.mnHeight
                            && serPossibleDot.mnHeight < dWOverHThresh * serPossibleDot.mnWidth) {
                        // serPossibleDot is a dot
                        serPossibleDot.mType = UnitProtoType.Type.TYPE_DOT;
                    }
                }
                if (serBase.mType == UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET
                        || serBase.mType == UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET
                        || serBase.mType == UnitProtoType.Type.TYPE_BIG_J
                        || serBase.mType == UnitProtoType.Type.TYPE_CLOSE_BRACE
                        || serBase.mType == UnitProtoType.Type.TYPE_INTEGRATE
                        || serBase.mType == UnitProtoType.Type.TYPE_SMALL_J_WITHOUT_DOT) {
                    int idx1 = idx + 1;
                    for (; idx1 < listBaseULIdentified.size(); idx1++) {
                        if (listCharLevel.get(idx1) == 1) {
                            break;  // find the first upper note
                        }
                    }
                    if (idx1 < listBaseULIdentified.size()
                            && listBaseULIdentified.get(idx1).mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                            && (listBaseULIdentified.get(idx1).mType == UnitProtoType.Type.TYPE_DOT // at this moment there is no dot multiply.
                            || listBaseULIdentified.get(idx1).mType == UnitProtoType.Type.TYPE_STAR)
                            && (listBaseULIdentified.get(idx1).mnLeft - serBase.getRightPlus1())
                            < serBase.mnWidth * ConstantsMgr.msdSmallJDotVPlaceThresh   // the left-right gap between dot and j main body has to be very small
                            && (serBase.mnTop - listBaseULIdentified.get(idx1).getBottomPlus1())
                            < serBase.mnHeight * ConstantsMgr.msdSmallJDotHPlaceThresh  // the top-bottom gap between dot and j main body has to be very small
                            && serBase.mnTop > listBaseULIdentified.get(idx1).getBottomPlus1()) {  // main body must be low the dot
                        StructExprRecog serSmallj = new StructExprRecog(serBase.mbarrayBiValues);
                        int nLeft = Math.min(serBase.mnLeft, listBaseULIdentified.get(idx1).mnLeft);
                        int nTop = Math.min(serBase.mnTop, listBaseULIdentified.get(idx1).mnTop);
                        int nRightPlus1 = Math.max(serBase.getRightPlus1(), listBaseULIdentified.get(idx1).getRightPlus1());
                        int nBottomPlus1 = Math.max(serBase.getBottomPlus1(), listBaseULIdentified.get(idx1).getBottomPlus1());
                        int nTotalArea = serBase.getArea() + listBaseULIdentified.get(idx1).getArea();
                        double dSimilarity = (serBase.getArea() * serBase.mdSimilarity
                                + listBaseULIdentified.get(idx1).getArea() * listBaseULIdentified.get(idx1).mdSimilarity) / nTotalArea;  // total area should not be zero here.
                        // now we merge the two into small j and replace the seperated two sers with small j
                        LinkedList<ImageChop> listParts = new LinkedList<ImageChop>();
                        ImageChop imgChopTop = listBaseULIdentified.get(idx1).getImageChop(false);
                        listParts.add(imgChopTop);
                        ImageChop imgChopBase = serBase.getImageChop(false);
                        listParts.add(imgChopBase);
                        ImageChop imgChop4SER = ExprSeperator.mergeImgChopsWithSameOriginal(listParts);   // need not to shrink imgChop4SER because it has been min container.
                        serSmallj.setStructExprRecog(UnitProtoType.Type.TYPE_SMALL_J, UNKNOWN_FONT_TYPE, nLeft, nTop, nRightPlus1 - nLeft, nBottomPlus1 - nTop, imgChop4SER, dSimilarity);
                        listBaseULIdentified.remove(idx1);  // remove idx1 first because idx1 is after idx. Otherwise, will remove a wrong child.
                        listBaseULIdentified.remove(idx);
                        listCharLevel.remove(idx1);  // remove idx1 first because idx1 is after idx. Otherwise, will remove a wrong child.
                        listCharLevel.remove(idx);
                        listBaseULIdentified.add(idx, serSmallj);
                        listCharLevel.add(idx, 0);
                    }
                }
            }

            // now we need to handle the cap and/or under notes for \Sigma, \Pi and \Integrate
            // this is for the case that \topunder{infinite, \Sigma, n = 1} is misrecognized to
            // \topunder{infinite, \Sigma, n^=}_1, or \topunder{1+2+3+4, \integrate, 5+6+7+8+9}
            // is misrecognized to 1 + 5 ++26 \topunder(+3, \integrate, +} + 7+8++4+9.
            LinkedList<StructExprRecog> listCUIdentifiedBLU = new LinkedList<StructExprRecog>();
            LinkedList<Integer> listCUIdentifiedCharLvl = new LinkedList<Integer>();
            int nLastCUBaseAreaIdx = -1;
            for (int idx = 0; idx < listBaseULIdentified.size(); idx++) {
                StructExprRecog serBase = listBaseULIdentified.get(idx).getPrincipleSER(1);
                if ((listBaseULIdentified.get(idx).mnExprRecogType == EXPRRECOGTYPE_HCUTCAPUNDER
                        || listBaseULIdentified.get(idx).mnExprRecogType == EXPRRECOGTYPE_HCUTCAP
                        || listBaseULIdentified.get(idx).mnExprRecogType == EXPRRECOGTYPE_HCUTUNDER)
                        && listCharLevel.get(idx) == 0 && serBase.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                        && (serBase.mType == UnitProtoType.Type.TYPE_INTEGRATE
                        || serBase.mType == UnitProtoType.Type.TYPE_BIG_SIGMA
                        || serBase.mType == UnitProtoType.Type.TYPE_BIG_PI)
                        && idx < listBaseULIdentified.size() - 1) {
                    StructExprRecog serCap = (listBaseULIdentified.get(idx).mnExprRecogType == EXPRRECOGTYPE_HCUTUNDER) ?
                            null : listBaseULIdentified.get(idx).mlistChildren.getFirst();
                    int nWholeCapTop = (serCap == null) ? 0 : serCap.mnTop;
                    int nWholeCapBottomP1 = (serCap == null) ? 0 : serCap.getBottomPlus1();
                    StructExprRecog serUnder = (listBaseULIdentified.get(idx).mnExprRecogType == EXPRRECOGTYPE_HCUTCAP) ?
                            null : listBaseULIdentified.get(idx).mlistChildren.getLast();
                    int nWholeUnderTop = (serUnder == null) ? 0 : serUnder.mnTop;
                    int nWholeUnderBottomP1 = (serUnder == null) ? 0 : serUnder.getBottomPlus1();
                    LinkedList<StructExprRecog> listIdentifiedCap = new LinkedList<StructExprRecog>();
                    if (serCap != null) {
                        listIdentifiedCap.add(serCap);
                    }
                    LinkedList<StructExprRecog> listIdentifiedUnder = new LinkedList<StructExprRecog>();
                    if (serUnder != null) {
                        listIdentifiedUnder.add(serUnder);
                    }
                    int idx1 = idx - 1;
                    // now go through its left side
                    for (; idx1 > nLastCUBaseAreaIdx; idx1--) {
                        StructExprRecog serThis = listBaseULIdentified.get(idx1);
                        if (listCharLevel.get(idx1) == 1
                                && listBaseULIdentified.get(idx).mnExprRecogType != EXPRRECOGTYPE_HCUTUNDER  // this character is a top level char and we have cap note
                                && (serCap.mnLeft - serThis.getRightPlus1())
                                <= Math.max(serCap.mnHeight, serThis.mnHeight) * ConstantsMgr.msdMaxCharGapWidthOverHeight //gap is less enough
                                && serBase.mnTop >= serThis.getBottomPlus1()    // Base is below this which means this is a cap.
                                && (Math.max(serThis.getBottomPlus1(), nWholeCapBottomP1) - Math.min(serThis.mnTop, nWholeCapTop))
                                <= serBase.mnHeight * ConstantsMgr.msdCapUnderHeightRatio2Base  // cap under height as a whole should not be too large
                            /*&& serThis.getBottomPlus1() < serCap.mnTop && serThis.mnTop > serCap.getBottomPlus1()*/) {   //there is not necessarily some vertical overlap, consider 2**3 - 4, 3 and - may not have overlap.
                            listIdentifiedCap.addFirst(serThis);
                            serCap = serThis;
                            nWholeCapTop = Math.min(serThis.mnTop, nWholeCapTop);
                            nWholeCapBottomP1 = Math.max(serThis.getBottomPlus1(), nWholeCapBottomP1);
                        } else if (listCharLevel.get(idx1) == -1
                                && listBaseULIdentified.get(idx).mnExprRecogType != EXPRRECOGTYPE_HCUTCAP   // this character is a bottom level char and we have under
                                && (serUnder.mnLeft - serThis.getRightPlus1())
                                <= Math.max(serUnder.mnHeight, serThis.mnHeight) * ConstantsMgr.msdMaxCharGapWidthOverHeight //gap is less enough
                                && serBase.getBottomPlus1() <= serThis.mnTop    // Base is above this which means this is a under
                                && (Math.max(serThis.getBottomPlus1(), nWholeUnderBottomP1) - Math.min(serThis.mnTop, nWholeUnderTop))
                                <= serBase.mnHeight * ConstantsMgr.msdCapUnderHeightRatio2Base  // cap under height as a whole should not be too large
                            /*&& serThis.getBottomPlus1() < serUnder.mnTop && serThis.mnTop > serUnder.getBottomPlus1()*/) {   //there is not necessarily some vertical overlap, consider 2**3 - 4, 3 and - may not have overlap.
                            listIdentifiedUnder.addFirst(serThis);
                            serUnder = serThis;
                            nWholeUnderTop = Math.min(serThis.mnTop, nWholeUnderTop);
                            nWholeUnderBottomP1 = Math.max(serThis.getBottomPlus1(), nWholeUnderBottomP1);
                        } else {
                            break;  // ok, we arrive at the left edge of this CUBase Area, exit.
                        }
                    }

                    for (int idx2 = nLastCUBaseAreaIdx + 1; idx2 <= idx1; idx2++) {
                        // so, now add the chars which are not belong to this cap under area.
                        listCUIdentifiedBLU.add(listBaseULIdentified.get(idx2));
                        listCUIdentifiedCharLvl.add(listCharLevel.get(idx2));
                    }

                    nLastCUBaseAreaIdx = idx;
                    // now go through its right side
                    serCap = (listBaseULIdentified.get(idx).mnExprRecogType == EXPRRECOGTYPE_HCUTUNDER) ?
                            null : listBaseULIdentified.get(idx).mlistChildren.getFirst();
                    serUnder = (listBaseULIdentified.get(idx).mnExprRecogType == EXPRRECOGTYPE_HCUTCAP) ?
                            null : listBaseULIdentified.get(idx).mlistChildren.getLast();
                    idx1 = idx + 1;
                    for (; idx1 < listBaseULIdentified.size(); idx1++) {
                        StructExprRecog serThis = listBaseULIdentified.get(idx1);
                        if (listCharLevel.get(idx1) == 1
                                && listBaseULIdentified.get(idx).mnExprRecogType != EXPRRECOGTYPE_HCUTUNDER   // this character is a top level char and we have cap note
                                && (serThis.mnLeft - serCap.getRightPlus1())
                                <= Math.max(serCap.mnHeight, serThis.mnHeight) * ConstantsMgr.msdMaxCharGapWidthOverHeight //gap is less enough
                                && serBase.mnTop >= serThis.getBottomPlus1()    // Base is below this which means this is a cap.
                                && (Math.max(serThis.getBottomPlus1(), nWholeCapBottomP1) - Math.min(serThis.mnTop, nWholeCapTop))
                                <= serBase.mnHeight * ConstantsMgr.msdCapUnderHeightRatio2Base  // cap under height as a whole should not be too large
                            /*&& serThis.getBottomPlus1() < serCap.mnTop && serThis.mnTop > serCap.getBottomPlus1()*/) {   //there is not necessarily some vertical overlap, consider 2**3 - 4, 3 and - may not have overlap.
                            nLastCUBaseAreaIdx = idx1;
                            listIdentifiedCap.add(serThis);
                            serCap = serThis;
                            nWholeCapTop = Math.min(serThis.mnTop, nWholeCapTop);
                            nWholeCapBottomP1 = Math.max(serThis.getBottomPlus1(), nWholeCapBottomP1);
                        } else if (listCharLevel.get(idx1) == -1
                                && listBaseULIdentified.get(idx).mnExprRecogType != EXPRRECOGTYPE_HCUTCAP   // this character is a bottom level char and we have under note
                                && (serThis.mnLeft - serUnder.getRightPlus1())
                                <= Math.max(serUnder.mnHeight, serThis.mnHeight) * ConstantsMgr.msdMaxCharGapWidthOverHeight //gap is less enough
                                && serBase.getBottomPlus1() <= serThis.mnTop    // Base is above this which means this is a under
                                && (Math.max(serThis.getBottomPlus1(), nWholeUnderBottomP1) - Math.min(serThis.mnTop, nWholeUnderTop))
                                <= serBase.mnHeight * ConstantsMgr.msdCapUnderHeightRatio2Base  // cap under height as a whole should not be too large
                            /*&& serThis.getBottomPlus1() < serUnder.mnTop && serThis.mnTop > serUnder.getBottomPlus1()*/) {   //there is not necessarily some vertical overlap, consider 2**3 - 4, 3 and - may not have overlap.
                            nLastCUBaseAreaIdx = idx1;
                            listIdentifiedUnder.add(serThis);
                            serUnder = serThis;
                            nWholeUnderTop = Math.min(serThis.mnTop, nWholeUnderTop);
                            nWholeUnderBottomP1 = Math.max(serThis.getBottomPlus1(), nWholeUnderBottomP1);
                        } else {
                            break;  // ok, we arrive at the left edge of this CUBase Area, exit.
                        }
                    }
                    // ok, now we have get all the cap parts and all the under parts,
                    // it is time for us to reconstruct this SER and add it to the new BLU list.
                    StructExprRecog serNewCap = null, serNewUnder = null, serNew;
                    if (listIdentifiedCap.size() == 1) {
                        serNewCap = listIdentifiedCap.getFirst();
                    } else if (listIdentifiedCap.size() > 1) {    // size > 1
                        serNewCap = new StructExprRecog(serBase.mbarrayBiValues);
                        serNewCap.setStructExprRecog(listIdentifiedCap, EXPRRECOGTYPE_VBLANKCUT);
                    }
                    if (listIdentifiedUnder.size() == 1) {
                        serNewUnder = listIdentifiedUnder.getFirst();
                    } else if (listIdentifiedUnder.size() > 1) {    // size > 1
                        serNewUnder = new StructExprRecog(serBase.mbarrayBiValues);
                        serNewUnder.setStructExprRecog(listIdentifiedUnder, EXPRRECOGTYPE_VBLANKCUT);
                    }
                    LinkedList<StructExprRecog> listThisAllChildren = new LinkedList<StructExprRecog>();
                    int nExprRecogType = EXPRRECOGTYPE_HCUTCAPUNDER;
                    if (serNewCap != null) {
                        listThisAllChildren.add(serNewCap);
                    } else {
                        nExprRecogType = EXPRRECOGTYPE_HCUTUNDER;
                    }
                    listThisAllChildren.add(serBase);
                    if (serNewUnder != null) {
                        listThisAllChildren.add(serNewUnder);
                    } else {
                        nExprRecogType = EXPRRECOGTYPE_HCUTCAP;
                    }
                    serNew = new StructExprRecog(serBase.mbarrayBiValues);
                    serNew.setStructExprRecog(listThisAllChildren, nExprRecogType);
                    listCUIdentifiedBLU.add(serNew);
                    listCUIdentifiedCharLvl.add(0);
                    // done! Now go to the next SER in listBaseULIdentified.
                    idx = nLastCUBaseAreaIdx;
                }
            }
            // now we have arrive at the end of listBaseULIdentified. We need to go through from nLastCUBaseAreaIdx + 1 to here
            // and add the remaining parts into listCUIdentifiedBLU.
            for (int idx = nLastCUBaseAreaIdx + 1; idx < listBaseULIdentified.size(); idx++) {
                // so, now add the chars which are not belong to this cap under area.
                listCUIdentifiedBLU.add(listBaseULIdentified.get(idx));
                listCUIdentifiedCharLvl.add(listCharLevel.get(idx));
            }
            // reset listBaseULIdentified and listCharLevel because they will be used latter on.
            listBaseULIdentified = listCUIdentifiedBLU;
            listCharLevel = listCUIdentifiedCharLvl;

            // step 4.3 : at last, trying to rectify some miss-identified char levels
            for (int idx = 0; idx < listBaseULIdentified.size(); idx++) {
                //分析中间结果之用
                System.out.print(listBaseULIdentified.get(idx).mnExprRecogType+"\t"+listBaseULIdentified.get(idx).toString()+"\t"+listCharLevel.get(idx));
                if (listBaseULIdentified.get(idx).mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                        && (listBaseULIdentified.get(idx).mType == UnitProtoType.Type.TYPE_EQUAL
                        || listBaseULIdentified.get(idx).mType == UnitProtoType.Type.TYPE_EQUAL_ALWAYS)
                        && (idx > 0 && idx < listBaseULIdentified.size() - 1)
                        && listCharLevel.get(idx) != 0
                        && ((listCharLevel.get(idx - 1) == 0 && listBaseULIdentified.get(idx).mnTop >= listBaseULIdentified.get(idx - 1).mnTop
                        && listBaseULIdentified.get(idx).getBottomPlus1() <= listBaseULIdentified.get(idx - 1).getBottomPlus1())
                        || (listCharLevel.get(idx + 1) == 0 && listBaseULIdentified.get(idx).mnTop >= listBaseULIdentified.get(idx + 1).mnTop
                        && listBaseULIdentified.get(idx).getBottomPlus1() <= listBaseULIdentified.get(idx + 1).getBottomPlus1())))
                {
                    // if the character is = or always=, and its left or right char is a base char but it is not base char and this char is
                    // in right position then change its char level to base char.
                    listCharLevel.set(idx, 0);
                }
                if (listBaseULIdentified.get(idx).mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && listBaseULIdentified.get(idx).mType == UnitProtoType.Type.TYPE_SUBTRACT) {
                    // need not to convert low and small subtract to dot because we have done before.
                    if (listCharLevel.get(idx) != 0 && (idx < listBaseULIdentified.size() - 1) && (listCharLevel.get(idx + 1) == 0)
                            && listBaseULIdentified.get(idx).mnTop >= listBaseULIdentified.get(idx + 1).mnTop
                            + listBaseULIdentified.get(idx + 1).mnHeight * ConstantsMgr.msdSubtractVRangeAgainstNeighbourH
                            && listBaseULIdentified.get(idx).getBottomPlus1() <= listBaseULIdentified.get(idx + 1).getBottomPlus1()
                            - listBaseULIdentified.get(idx + 1).mnHeight * ConstantsMgr.msdSubtractVRangeAgainstNeighbourH
                            && (listBaseULIdentified.get(idx + 1).mnExprRecogType != EXPRRECOGTYPE_ENUMTYPE
                            || listBaseULIdentified.get(idx).mnWidth >= listBaseULIdentified.get(idx + 1).mnWidth * ConstantsMgr.msdSubtractWidthAgainstNeighbourW)) {
                        // if the character is -, and its right char is a base char but it is an upper or lower note char and it's in right position
                        // and its width is long enough, then change its char level to base char. do not use calculate char level because we have got
                        // wrong information from calculate char level.
                        listCharLevel.set(idx, 0);
                    }
                }
                if (listBaseULIdentified.get(idx).mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                        && listBaseULIdentified.get(idx).mType == UnitProtoType.Type.TYPE_DOT) {
                    if (((idx < listBaseULIdentified.size() - 1) ? (listCharLevel.get(idx + 1) == 0) : true)
                            && ((idx > 0) ? (listCharLevel.get(idx - 1) == 0) : true)) {
                        if (listCharLevel.get(idx) == -1) {
                            // if the character is \dot, and its left and right char is a base char but it is an lower note char
                            // then change its char level to base char.
                            listCharLevel.set(idx, 0);
                        } else if (listCharLevel.get(idx) == 0 && idx != 0 && idx != listBaseULIdentified.size() - 1) {
                            // if the character is \dot, and its left and right char is a base char and itself is also a base char
                            // then it actually means multiply.
                            listBaseULIdentified.get(idx).mType = UnitProtoType.Type.TYPE_DOT_MULTIPLY;
                        }
                    } else if (listCharLevel.get(idx) == 0 && idx > 0 && idx < listBaseULIdentified.size() - 1
                            && listCharLevel.get(idx + 1) == 1 && listCharLevel.get(idx - 1) == 1) {
                        // if the character is \dot, and its left and right char is a upper note char and itself is a base char
                        // then change its char level to upper note.
                        listCharLevel.set(idx, 1);
                    }
                }
                if (listBaseULIdentified.get(idx).mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                        && listBaseULIdentified.get(idx).mType == UnitProtoType.Type.TYPE_DOT
                        && listCharLevel.get(idx) == -1
                        && ((idx < listBaseULIdentified.size() - 1) ? (listCharLevel.get(idx + 1) == 0) : true)
                        && ((idx > 0) ? (listCharLevel.get(idx - 1) == 0) : true)) {
                    // if the character is \dot, and its left and right char is a base char but it is an lower note char
                    // then change its char level to base char.
                    listCharLevel.set(idx, 0);
                }
                // need not to consider a case like \topunder{infinite, \Sigma, n = 1} is misrecognized to
                // \topunder{infinite, \Sigma, n^=}_1. This kind of situation has been processed.

                //todo dml_change: 积分号后面的
//                if(listBaseULIdentified.get(idx).mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
//                        &&listBaseULIdentified.get(idx).mType == UnitProtoType.Type.TYPE_INTEGRATE){
//                    int i=idx+1;
//                    for(;i<listBaseULIdentified.size()&&listCharLevel.get(i) == -1;++i);
//                    if(listCharLevel.get(i) ==0){
//                        listCharLevel.set(i, 1);
//                    }
//                }
                if (listBaseULIdentified.get(idx).mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && listCharLevel.get(idx) != 0) {
                    //todo dml_changed7: 先粗暴的认为dx不可能作为指数，后续可加入更高级的纠错逻辑（比如根据切块中心坐标进行纠错）。
                    if (listBaseULIdentified.get(idx).mType == UnitProtoType.Type.TYPE_SMALL_D && (idx < listBaseULIdentified.size() - 1)
                            && (listBaseULIdentified.get(idx + 1).mType == UnitProtoType.Type.TYPE_SMALL_X
//                            ||listBaseULIdentified.get(idx + 1).mType == UnitProtoType.Type.TYPE_MULTIPLY
//                            ||listBaseULIdentified.get(idx + 1).mType == UnitProtoType.Type.TYPE_DOT_MULTIPLY)
                              )) {
                        listCharLevel.set(idx, 0);
                    }
                    //todo 2final_change: x 扩充到 times 和 dottimes |
                    if ((idx > 0)&& (listBaseULIdentified.get(idx - 1).mType == UnitProtoType.Type.TYPE_SMALL_D)
                            &&(listBaseULIdentified.get(idx ).mType == UnitProtoType.Type.TYPE_SMALL_X
//                            ||listBaseULIdentified.get(idx ).mType == UnitProtoType.Type.TYPE_MULTIPLY
//                            ||listBaseULIdentified.get(idx ).mType == UnitProtoType.Type.TYPE_DOT_MULTIPLY
                              )) {
                        listCharLevel.set(idx, 0);
                    }

                    //todo dml_changed8：分数不能直接带指数，除非有括号。
                    if (idx > 0 && listBaseULIdentified.get(idx - 1).mnExprRecogType == EXPRRECOGTYPE_HLINECUT) {
                        listCharLevel.set(idx, 1);
                    }

                }

                //todo dml_changed9: 针对积分内容自动丢弃的问题----实验证明这个逻辑太暴力了，期待更优美的方案
//                if(listBaseULIdentified.get(idx).mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && listCharLevel.get(idx)!=0){
//                    if(idx>=3&&listCharLevel.get(idx-1)!=0&&listCharLevel.get(idx-2)!=0){
//                        listCharLevel.set(idx, 0);
//                    }
//                }
                //todo dml_changed10: 累乘的间接解决---如果是最大的，类型确实small_pi，则改为big_pi
                if (idx == nBiggestChildIdx && listBaseULIdentified.get(idx).mType == UnitProtoType.Type.TYPE_SMALL_PI) {
                    listBaseULIdentified.get(idx).setUnitType(UnitProtoType.Type.TYPE_BIG_PI);
                }

                //todo CYX_changed1: 0位数字后面不能有下标 listCharLevel.get(idx)
                if (isNumberChar(listBaseULIdentified.get(idx).mType)&& listCharLevel.get(idx)==0
                        && idx < listBaseULIdentified.size() - 1
                        && listCharLevel.get(idx+1)==-1
                ) {
                    listCharLevel.set(idx+1, 0);
                }
                //todo CYX_changed2: [后面不能有下标
//                if (listBaseULIdentified.get(idx).mType==UnitProtoType.Type.TYPE_SQUARE_BRACKET && (idx < listBaseULIdentified.size() - 1)
//                        && (isNumberChar(listBaseULIdentified.get(idx + 1).mType))
//                ) {
//                    listCharLevel.set(idx+1, 0);
//
//                }
                //todo CYX_changed3: 小数点降下去
                if ( (idx < listBaseULIdentified.size() - 1)&&(isNumberChar(listBaseULIdentified.get(idx).mType))
                        && listBaseULIdentified.get(idx+1).mType==UnitProtoType.Type.TYPE_DOT
                        && (listCharLevel.get(idx+1)==0)
                ) {
                    listCharLevel.set(idx+1, -1);
                }
                //todo dml change 2.3 加号降下去
                if ( (idx < listBaseULIdentified.size() - 1)&&(isNumberChar(listBaseULIdentified.get(idx).mType))
                        && listBaseULIdentified.get(idx+1).mType==UnitProtoType.Type.TYPE_ADD
                        && (listCharLevel.get(idx+1)==1)
                ) {
                    listCharLevel.set(idx+1, 0);
                }

                //todo CYX_changed2: 上標的z變成2
                if ( (listCharLevel.get(idx)==1)
                        && (listBaseULIdentified.get(idx).mType==UnitProtoType.Type.TYPE_SMALL_Z)
                ) {
                    listBaseULIdentified.get(idx).mType=UnitProtoType.Type.TYPE_TWO;

                }

                System.out.println("\tafter: "+listCharLevel.get(idx));
            }

            // step 5, since different levels have been well-sorted, we merge them.
            LinkedList<StructExprRecog> listBaseTrunk = new LinkedList<StructExprRecog>();
            LinkedList<LinkedList<StructExprRecog>> listLeftTopNote = new LinkedList<LinkedList<StructExprRecog>>();
            LinkedList<LinkedList<StructExprRecog>> listLeftBottomNote = new LinkedList<LinkedList<StructExprRecog>>();
            LinkedList<LinkedList<StructExprRecog>> listUpperNote = new LinkedList<LinkedList<StructExprRecog>>();
            LinkedList<LinkedList<StructExprRecog>> listLowerNote = new LinkedList<LinkedList<StructExprRecog>>();
            int nLastBaseIdx = -1;
            for (int idx = 0; idx < listBaseULIdentified.size(); idx++) {
                if (listCharLevel.get(idx) == 0) {
                    listBaseTrunk.add(listBaseULIdentified.get(idx));
                    listLeftTopNote.add(new LinkedList<StructExprRecog>());
                    listLeftBottomNote.add(new LinkedList<StructExprRecog>());
                    listUpperNote.add(new LinkedList<StructExprRecog>());
                    listLowerNote.add(new LinkedList<StructExprRecog>());
                    int nLastBaseIdxInBaseTrunk = listBaseTrunk.size() - 2;
                    if (nLastBaseIdx < idx - 1) {
                        // nLastBaseIdx == idx - 1 means no notes between nLastBaseIdx and this base.
                        // Note that even if nLastBaseIdx is -1, nLastBaseIdx == idx - 1 is still a valid adjustment.
                        int[] narrayNoteGroup = groupNotesForPrevNextBases(listBaseULIdentified, listCharLevel, nLastBaseIdx, idx);
                        for (int idx1 = nLastBaseIdx + 1; idx1 < idx; idx1++) {
                            int nGroupIdx = idx1 - nLastBaseIdx - 1;
                            if (listCharLevel.get(idx1) == -1) {
                                // lower note
                                if (narrayNoteGroup[nGroupIdx] == 0) {
                                    listLowerNote.get(nLastBaseIdxInBaseTrunk).add(listBaseULIdentified.get(idx1));
                                } else if (narrayNoteGroup[nGroupIdx] == 1) {
                                    listLeftBottomNote.getLast().add(listBaseULIdentified.get(idx1));
                                } // ignore if narrayNoteGroup[nGroupIdx] == other values
                            } else if (listCharLevel.get(idx1) == 1) {
                                // upper note
                                if (narrayNoteGroup[nGroupIdx] == 0) {
                                    listUpperNote.get(nLastBaseIdxInBaseTrunk).add(listBaseULIdentified.get(idx1));
                                } else if (narrayNoteGroup[nGroupIdx] == 1) {
                                    listLeftTopNote.getLast().add(listBaseULIdentified.get(idx1));
                                } // ignore if narrayNoteGroup[nGroupIdx] == other values
                            }   // ignore if listCharLevel.get(idx1) is not -1 or 1.
                        }
                    }
                    if (nLastBaseIdxInBaseTrunk > 0) {
                        convertNotes2HCut(nLastBaseIdxInBaseTrunk, listBaseTrunk, listLeftTopNote, listLeftBottomNote, listUpperNote, listLowerNote);
                    }
                    nLastBaseIdx = idx;
                }
            }
            if (nLastBaseIdx >= 0) {
                int nLastBaseIdxInBaseTrunk = listBaseTrunk.size() - 1;
                for (int idx1 = nLastBaseIdx + 1; idx1 < listBaseULIdentified.size(); idx1++) {
                    if (listCharLevel.get(idx1) == -1) {
                        listLowerNote.get(nLastBaseIdxInBaseTrunk).add(listBaseULIdentified.get(idx1));
                    } else {   // idx1 is not nLastBaseIdx, so that it must be either 1 or -1.
                        listUpperNote.get(nLastBaseIdxInBaseTrunk).add(listBaseULIdentified.get(idx1));
                    }
                }
                convertNotes2HCut(nLastBaseIdxInBaseTrunk, listBaseTrunk, listLeftTopNote, listLeftBottomNote, listUpperNote, listLowerNote);
            }

            // step 6, create a list of merged and reconstructed children.
            LinkedList<StructExprRecog> listProcessed = new LinkedList<StructExprRecog>();
            for (int idx = 0; idx < listBaseTrunk.size(); idx++) {
                StructExprRecog serThis = listBaseTrunk.get(idx);
                serThis = serThis.restruct();
                if (listLeftTopNote.get(idx).size() > 0) {
                    StructExprRecog serLeftTopNote = new StructExprRecog(mbarrayBiValues);
                    if (listLeftTopNote.get(idx).size() > 1) {
                        serLeftTopNote.setStructExprRecog(listLeftTopNote.get(idx), EXPRRECOGTYPE_VBLANKCUT);
                    } else {
                        serLeftTopNote = listLeftTopNote.get(idx).getFirst();
                    }
                    serLeftTopNote = serLeftTopNote.restruct();
                    LinkedList<StructExprRecog> listWithLeftTopNote = new LinkedList<StructExprRecog>();
                    listWithLeftTopNote.add(serLeftTopNote);
                    listWithLeftTopNote.add(serThis);
                    serThis = new StructExprRecog(mbarrayBiValues);
                    serThis.setStructExprRecog(listWithLeftTopNote, EXPRRECOGTYPE_VCUTLEFTTOPNOTE);

                    if (serThis.mlistChildren.getLast().mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                            && serThis.mlistChildren.getFirst().mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                            && (serThis.mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_ZERO
                            || serThis.mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SMALL_O
                            || serThis.mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_BIG_O)
                    ) {
                        if (serThis.mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_SMALL_C
                                || serThis.mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_BIG_C) {
                            // need not to reset left top width and height because they have been set.
                            // similarly, need not to reset similarity.
                            LinkedList<ImageChop> listParts = new LinkedList<ImageChop>();
                            ImageChop imgChopLeftTop = serThis.mlistChildren.getFirst().getImageChop(false);
                            listParts.add(imgChopLeftTop);
                            ImageChop imgChopBase = serThis.mlistChildren.getLast().getImageChop(false);
                            listParts.add(imgChopBase);
                            ImageChop imgChop4SER = ExprSeperator.mergeImgChopsWithSameOriginal(listParts);   // need not to shrink imgChop4SER because it has been min container.
                            serThis.setStructExprRecog(UnitProtoType.Type.TYPE_CELCIUS, UNKNOWN_FONT_TYPE, imgChop4SER);
                        } else if (serThis.mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_BIG_F) {
                            // need not to reset left top width and height because they have been set.
                            // similarly, need not to reset similarity.
                            LinkedList<ImageChop> listParts = new LinkedList<ImageChop>();
                            ImageChop imgChopLeftTop = serThis.mlistChildren.getFirst().getImageChop(false);
                            listParts.add(imgChopLeftTop);
                            ImageChop imgChopBase = serThis.mlistChildren.getLast().getImageChop(false);
                            listParts.add(imgChopBase);
                            ImageChop imgChop4SER = ExprSeperator.mergeImgChopsWithSameOriginal(listParts);   // need not to shrink imgChop4SER because it has been min container.
                            serThis.setStructExprRecog(UnitProtoType.Type.TYPE_FAHRENHEIT, UNKNOWN_FONT_TYPE, imgChop4SER);
                        } else if ((serThis.mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_ONE
                                || serThis.mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_FORWARD_SLASH
                                || serThis.mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_SMALL_L)
                                && listLowerNote.get(idx).size() >= 1
                                && listLowerNote.get(idx).getFirst().mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                                && (listLowerNote.get(idx).getFirst().mType == UnitProtoType.Type.TYPE_ZERO
                                || listLowerNote.get(idx).getFirst().mType == UnitProtoType.Type.TYPE_SMALL_O
                                || listLowerNote.get(idx).getFirst().mType == UnitProtoType.Type.TYPE_BIG_O)
                        ) {
                            LinkedList<ImageChop> listParts = new LinkedList<ImageChop>();
                            ImageChop imgChopLeftTop = serThis.mlistChildren.getFirst().getImageChop(false);
                            listParts.add(imgChopLeftTop);
                            ImageChop imgChopBase = serThis.mlistChildren.getLast().getImageChop(false);
                            listParts.add(imgChopBase);
                            ImageChop imgChopLowerNote = listLowerNote.get(idx).getFirst().getImageChop(false);
                            listParts.add(imgChopLowerNote);
                            ImageChop imgChop4SER = ExprSeperator.mergeImgChopsWithSameOriginal(listParts);   // need not to shrink imgChop4SER because it has been min container.
                            serThis.setStructExprRecog(UnitProtoType.Type.TYPE_PERCENT, UNKNOWN_FONT_TYPE, imgChop4SER);
                            listLowerNote.get(idx).removeFirst();     // first element of lower note list should be removed here because it has been merged into serThis.
                        }
                    } else if (serThis.mlistChildren.getLast().mnExprRecogType == EXPRRECOGTYPE_GETROOT) {
                        StructExprRecog serRootLevel = serThis.mlistChildren.getLast().mlistChildren.getFirst();
                        StructExprRecog serRooted = serThis.mlistChildren.getLast().mlistChildren.getLast();
                        if (serRootLevel.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                                && (serRootLevel.mType == UnitProtoType.Type.TYPE_SQRT_LEFT
                                || serRootLevel.mType == UnitProtoType.Type.TYPE_SQRT_SHORT
                                || serRootLevel.mType == UnitProtoType.Type.TYPE_SQRT_MEDIUM
                                || serRootLevel.mType == UnitProtoType.Type.TYPE_SQRT_LONG
                                || serRootLevel.mType == UnitProtoType.Type.TYPE_SQRT_TALL
                                || serRootLevel.mType == UnitProtoType.Type.TYPE_SQRT_VERY_TALL)) {
                            LinkedList<StructExprRecog> listRoot = new LinkedList<StructExprRecog>();
                            listRoot.add(serLeftTopNote);
                            listRoot.add(serRootLevel);
                            listRoot.add(serRooted);
                            serThis.setStructExprRecog(listRoot, EXPRRECOGTYPE_GETROOT);
                        } else {
                            LinkedList<StructExprRecog> listNewRootLevel = new LinkedList<StructExprRecog>();
                            listNewRootLevel.add(serLeftTopNote);
                            listNewRootLevel.add(serRootLevel);
                            StructExprRecog serNewRootLevel = new StructExprRecog(mbarrayBiValues);
                            serNewRootLevel.setStructExprRecog(listNewRootLevel, EXPRRECOGTYPE_VBLANKCUT);
                            serNewRootLevel = serNewRootLevel.restruct();
                            LinkedList<StructExprRecog> listRoot = new LinkedList<StructExprRecog>();
                            listRoot.add(serNewRootLevel);
                            listRoot.add(serThis.mlistChildren.getLast().mlistChildren.get(1));
                            listRoot.add(serRooted);
                            serThis.setStructExprRecog(listRoot, EXPRRECOGTYPE_GETROOT);
                        }
                    } else {
                        // ignore left top note
                        serThis = serThis.mlistChildren.getLast();
                    }
                }
                if (listLowerNote.get(idx).size() > 0 && listUpperNote.get(idx).size() > 0) {
                    StructExprRecog serLowerNote = new StructExprRecog(mbarrayBiValues);
                    if (listLowerNote.get(idx).size() > 1) {
                        serLowerNote.setStructExprRecog(listLowerNote.get(idx), EXPRRECOGTYPE_VBLANKCUT);
                    } else {
                        serLowerNote = listLowerNote.get(idx).getFirst();
                    }
                    serLowerNote = serLowerNote.restruct(); // restruct lower note
                    StructExprRecog serUpperNote = new StructExprRecog(mbarrayBiValues);
                    if (listUpperNote.get(idx).size() > 1) {
                        serUpperNote.setStructExprRecog(listUpperNote.get(idx), EXPRRECOGTYPE_VBLANKCUT);
                    } else {
                        serUpperNote = listUpperNote.get(idx).getFirst();
                    }
                    serUpperNote = serUpperNote.restruct(); // restruct upper note
                    LinkedList<StructExprRecog> listWithLUNote = new LinkedList<StructExprRecog>();
                    listWithLUNote.add(serThis);
                    listWithLUNote.add(serLowerNote);
                    listWithLUNote.add(serUpperNote);
                    serThis = new StructExprRecog(mbarrayBiValues);
                    serThis.setStructExprRecog(listWithLUNote, EXPRRECOGTYPE_VCUTLUNOTES);
                } else if (listLowerNote.get(idx).size() > 0) {
                    StructExprRecog serLowerNote = new StructExprRecog(mbarrayBiValues);
                    if (listLowerNote.get(idx).size() > 1) {
                        serLowerNote.setStructExprRecog(listLowerNote.get(idx), EXPRRECOGTYPE_VBLANKCUT);
                    } else {
                        serLowerNote = listLowerNote.get(idx).getFirst();
                    }
                    serLowerNote = serLowerNote.restruct(); // restruct lower note
                    LinkedList<StructExprRecog> listWithLowerNote = new LinkedList<StructExprRecog>();
                    listWithLowerNote.add(serThis);
                    listWithLowerNote.add(serLowerNote);
                    serThis = new StructExprRecog(mbarrayBiValues);
                    serThis.setStructExprRecog(listWithLowerNote, EXPRRECOGTYPE_VCUTLOWERNOTE);
                } else if (listUpperNote.get(idx).size() > 0) {
                    StructExprRecog serUpperNote = new StructExprRecog(mbarrayBiValues);
                    if (listUpperNote.get(idx).size() > 1) {
                        serUpperNote.setStructExprRecog(listUpperNote.get(idx), EXPRRECOGTYPE_VBLANKCUT);
                    } else {
                        serUpperNote = listUpperNote.get(idx).getFirst();
                    }
                    serUpperNote = serUpperNote.restruct(); // restruct upper note
                    LinkedList<StructExprRecog> listWithUpperNote = new LinkedList<StructExprRecog>();
                    listWithUpperNote.add(serThis);
                    listWithUpperNote.add(serUpperNote);
                    serThis = new StructExprRecog(mbarrayBiValues);
                    serThis.setStructExprRecog(listWithUpperNote, EXPRRECOGTYPE_VCUTUPPERNOTE);
                    serThis = serThis.identifyHSeperatedChar(); // a VCUTUPPERNOTE could be misrecognized i.
                }
                // base is always not v cut.
                listProcessed.add(serThis);
            }

            StructExprRecog serReturn = new StructExprRecog(mbarrayBiValues);
            if (listProcessed.size() == 1) {
                serReturn = listProcessed.getFirst();
            } else if (listProcessed.size() > 1) {
                serReturn.setStructExprRecog(listProcessed, EXPRRECOGTYPE_VBLANKCUT);
            }
            return serReturn;
        }
    }

    // This function identify upper notes and lower notes if they belong to previous base or next base
    public static int[] groupNotesForPrevNextBases
    (LinkedList<StructExprRecog> listBaseULIdentified, LinkedList<Integer> listCharLevel,
     int nLastBaseIdx, int nNextBaseIdx) {
        // assume nLastBaseIdx < nNextBaseIdx - 1
        if (nLastBaseIdx < 0) {   // nNextBaseIdx is the first base
            int[] narrayGrouped = new int[nNextBaseIdx];
            for (int nElementIdx = 0; nElementIdx <= nNextBaseIdx - 1; nElementIdx++) {
                narrayGrouped[nElementIdx] = 1;
            }
            return narrayGrouped;
        } else if (nNextBaseIdx < 0 || nNextBaseIdx >= listBaseULIdentified.size()) {    // nLastBaseIdx is the last base
            int[] narrayGrouped = new int[listBaseULIdentified.size() - 1 - nLastBaseIdx];
            for (int nElementIdx = nLastBaseIdx + 1; nElementIdx < listBaseULIdentified.size(); nElementIdx++) {
                narrayGrouped[nElementIdx] = 0;
            }
            return narrayGrouped;
        } else if (nLastBaseIdx >= nNextBaseIdx - 1) {
            return new int[0];  // no notes between last and next base.
        } else {
            int[] narrayGrouped = new int[nNextBaseIdx - 1 - nLastBaseIdx];
            double[] darrayUpperNoteGaps = new double[nNextBaseIdx - nLastBaseIdx], darrayLowerNoteGaps = new double[nNextBaseIdx - nLastBaseIdx];
            double dMaxUpperNoteGap = -1, dMaxLowerNoteGap = -1;
            int nMaxUpperNoteGapIdx = -1, nMaxLowerNoteGapIdx = -1;
            int nLastLeftForUpperNote = nLastBaseIdx, nLastLeftForLowerNote = nLastBaseIdx;
            double dUpperNoteFontAvgWidth = 0, dUpperNoteFontAvgHeight = 0;
            double dLowerNoteFontAvgWidth = 0, dLowerNoteFontAvgHeight = 0;
            int nUpperNoteCnt = 0, nLowerNoteCnt = 0;
            int nLastUpperNoteIdx = 0, nLastLowerNoteIdx = 0;
            for (int nElementIdx = nLastBaseIdx + 1; nElementIdx <= nNextBaseIdx - 1; nElementIdx++) {
                int nGroupIdx = nElementIdx - (nLastBaseIdx + 1);
                if (listCharLevel.get(nElementIdx) == -1) {  // lower note
                    StructExprRecog serLast = listBaseULIdentified.get(nLastLeftForLowerNote);
                    StructExprRecog serThis = listBaseULIdentified.get(nElementIdx);
                    dLowerNoteFontAvgWidth += serThis.mnWidth;
                    dLowerNoteFontAvgHeight += serThis.mnHeight;
                    nLowerNoteCnt++;
                    nLastLowerNoteIdx = nElementIdx;
                    int nLastRightP1 = serLast.getRightPlus1();
                    int nThisLeft = serThis.mnLeft;
                    if (nLastLeftForLowerNote > nLastBaseIdx) {
                        double dLastHCentral = (serLast.mnTop + serLast.getBottomPlus1()) / 2.0;
                        double dThisHCentral = (serThis.mnTop + serThis.getBottomPlus1()) / 2.0;
                        double dHCentralGap = Math.abs(dThisHCentral - dLastHCentral);
                        double dHTopGap = Math.abs(serThis.mnTop - serLast.mnTop);
                        double dHBottomP1Gap = Math.abs(serThis.getBottomPlus1() - serLast.getBottomPlus1());
                        //darrayLowerNoteGaps[nGroupIdx] = Math.max(0, nThisLeft - nLastRightP1) + Math.abs(dThisHCentral - dLastHCentral);
                        darrayLowerNoteGaps[nGroupIdx] = Math.hypot(Math.max(0, nThisLeft - nLastRightP1), Math.min(dHCentralGap, Math.min(dHTopGap, dHBottomP1Gap)));
                    } else {
                        // vertical distance measure from base to note is a bit different
                        int nLastBottomP1 = serLast.getBottomPlus1();
                        double dThisHCentral = (serThis.mnTop + serThis.getBottomPlus1()) / 2.0;
                        //darrayLowerNoteGaps[nGroupIdx] = Math.max(0, nThisLeft - nLastRightP1) + Math.max(0, dThisHCentral - nLastBottomP1);
                        darrayLowerNoteGaps[nGroupIdx] = Math.hypot(Math.max(0, nThisLeft - nLastRightP1), Math.max(0, dThisHCentral - nLastBottomP1));
                    }
                    if (darrayLowerNoteGaps[nGroupIdx] >= dMaxLowerNoteGap) { // always try to use the right most gap as max gap
                        dMaxLowerNoteGap = darrayLowerNoteGaps[nGroupIdx];
                        nMaxLowerNoteGapIdx = nGroupIdx;
                    }
                    nLastLeftForLowerNote = nElementIdx;
                    darrayUpperNoteGaps[nGroupIdx] = -1;
                } else if (listCharLevel.get(nElementIdx) == 1) {   // upper note
                    StructExprRecog serLast = listBaseULIdentified.get(nLastLeftForUpperNote);
                    StructExprRecog serThis = listBaseULIdentified.get(nElementIdx);
                    dUpperNoteFontAvgWidth += serThis.mnWidth;
                    dUpperNoteFontAvgHeight += serThis.mnHeight;
                    nUpperNoteCnt++;
                    nLastUpperNoteIdx = nElementIdx;
                    int nLastRightP1 = serLast.getRightPlus1();
                    int nThisLeft = serThis.mnLeft;
                    if (nLastLeftForUpperNote > nLastBaseIdx) {
                        double dLastHCentral = (serLast.mnTop + serLast.getBottomPlus1()) / 2.0;
                        double dThisHCentral = (serThis.mnTop + serThis.getBottomPlus1()) / 2.0;
                        double dHCentralGap = Math.abs(dThisHCentral - dLastHCentral);
                        double dHTopGap = Math.abs(serThis.mnTop - serLast.mnTop);
                        double dHBottomP1Gap = Math.abs(serThis.getBottomPlus1() - serLast.getBottomPlus1());
                        //darrayUpperNoteGaps[nGroupIdx] = Math.max(0, nThisLeft - nLastRightP1) + Math.abs(dThisHCentral - dLastHCentral);
                        darrayUpperNoteGaps[nGroupIdx] = Math.hypot(Math.max(0, nThisLeft - nLastRightP1), Math.min(dHCentralGap, Math.min(dHTopGap, dHBottomP1Gap)));
                    } else {
                        // vertical distance measure from base to note is a bit different
                        int nLastTop = serLast.mnTop;
                        double dThisHCentral = (serThis.mnTop + serThis.getBottomPlus1()) / 2.0;
                        //darrayUpperNoteGaps[nGroupIdx] = Math.max(0, nThisLeft - nLastRightP1) + Math.max(0, nLastTop - dThisHCentral);
                        darrayUpperNoteGaps[nGroupIdx] = Math.hypot(Math.max(0, nThisLeft - nLastRightP1), Math.max(0, nLastTop - dThisHCentral));
                    }
                    if (darrayUpperNoteGaps[nGroupIdx] >= dMaxUpperNoteGap) { // always try to use the right most gap as max gap
                        dMaxUpperNoteGap = darrayUpperNoteGaps[nGroupIdx];
                        nMaxUpperNoteGapIdx = nGroupIdx;
                    }
                    nLastLeftForUpperNote = nElementIdx;
                    darrayLowerNoteGaps[nGroupIdx] = -1;
                } else {    // base note? seems wrong.
                    narrayGrouped[nGroupIdx] = -1;    // invalid group
                    darrayUpperNoteGaps[nGroupIdx] = -1;
                    darrayLowerNoteGaps[nGroupIdx] = -1;
                }
            }
            if (nLastLeftForUpperNote != nLastBaseIdx) {
                StructExprRecog serLast = listBaseULIdentified.get(nLastLeftForUpperNote);
                StructExprRecog serThis = listBaseULIdentified.get(nNextBaseIdx);
                int nLastRightP1 = serLast.getRightPlus1();
                int nThisLeft = serThis.mnLeft;
                double dLastHCentral = (serLast.mnTop + serLast.getBottomPlus1()) / 2.0;
                int nThisTop = serThis.mnTop;
                int nGroupIdx = nNextBaseIdx - 1 - nLastBaseIdx;
                //darrayUpperNoteGaps[nGroupIdx] = Math.max(0, nThisLeft - nLastRightP1) + Math.max(0, nThisTop - dLastHCentral);
                if (serThis.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_GETROOT
                        && (serThis.mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SQRT_LEFT
                        || serThis.mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SQRT_LONG
                        || serThis.mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SQRT_MEDIUM
                        || serThis.mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SQRT_SHORT)) {
                    // if next base is sqrt, the distance from last upper note to sqrt should be calculated in a different way
                    // from other characters because sqrt's upper left part is empty so real distance from sqrt to its upper left
                    // is actually longer than other base characters' distance to the upper left note. In general, the left tick
                    // part of sqrt_left, sqrt_long, sqrt_medium and sqrt_short's w:h is from 1/3 to 4/5, so horizontal difference
                    // is nThisLeft + serThis.mnHeight /2.0 - nLastRightP1 and vertical difference is dThisHCentral - dLastHCentral.
                    double dThisHCentral = (serThis.mnTop + serThis.getBottomPlus1()) / 2.0;
                    darrayUpperNoteGaps[nGroupIdx] = Math.hypot(Math.max(0, nThisLeft + serThis.mnHeight / 2.0 - nLastRightP1), Math.max(0, dThisHCentral - dLastHCentral));
                } else if (serThis.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_GETROOT
                        && (serThis.mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SQRT_TALL
                        || serThis.mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SQRT_VERY_TALL)) {
                    // In general, the left tick part of sqrt_tall and sqrt_very_tall's w:h is from 1/6 to 1/3, so horizontal difference
                    // is nThisLeft + serThis.mnHeight /2.0 - nLastRightP1. And vertical difference is measured from central of upper note
                    // to top + height/4 of sqrt.
                    double dThisHTopCentral = serThis.mnTop + serThis.mnHeight / 4.0;
                    darrayUpperNoteGaps[nGroupIdx] = Math.hypot(Math.max(0, nThisLeft + serThis.mnHeight / 4.0 - nLastRightP1), Math.max(0, dThisHTopCentral - dLastHCentral));
                } else {
                    darrayUpperNoteGaps[nGroupIdx] = Math.hypot(Math.max(0, nThisLeft - nLastRightP1), Math.max(0, nThisTop - dLastHCentral));
                }
                if (darrayUpperNoteGaps[nGroupIdx] >= dMaxUpperNoteGap) { // always try to use the right most gap as max gap
                    dMaxUpperNoteGap = darrayUpperNoteGaps[nGroupIdx];
                    nMaxUpperNoteGapIdx = nGroupIdx;
                }
            } else {
                darrayUpperNoteGaps[nNextBaseIdx - 1 - nLastBaseIdx] = -1;  // last left for upper note is last base.
            }

            if (nLastLeftForLowerNote != nLastBaseIdx) {
                StructExprRecog serLast = listBaseULIdentified.get(nLastLeftForLowerNote);
                StructExprRecog serThis = listBaseULIdentified.get(nNextBaseIdx);
                int nLastRightP1 = serLast.getRightPlus1();
                int nThisLeft = serThis.mnLeft;
                double dLastHCentral = (serLast.mnTop + serLast.getBottomPlus1()) / 2.0;
                int nThisBottomP1 = serThis.getBottomPlus1();
                int nGroupIdx = nNextBaseIdx - 1 - nLastBaseIdx;
                //darrayLowerNoteGaps[nGroupIdx] = Math.max(0, nThisLeft - nLastRightP1) + Math.max(0, dLastHCentral - nThisBottomP1);
                darrayLowerNoteGaps[nGroupIdx] = Math.hypot(Math.max(0, nThisLeft - nLastRightP1), Math.max(0, dLastHCentral - nThisBottomP1));
                if (darrayLowerNoteGaps[nGroupIdx] >= dMaxLowerNoteGap) { // always try to use the right most gap as max gap
                    dMaxLowerNoteGap = darrayLowerNoteGaps[nGroupIdx];
                    nMaxLowerNoteGapIdx = nGroupIdx;
                }
            } else {
                darrayLowerNoteGaps[nNextBaseIdx - 1 - nLastBaseIdx] = -1;  // last left for Lower note is last base.
            }

            if (nUpperNoteCnt > 0) {
                StructExprRecog serLastBase = listBaseULIdentified.get(nLastBaseIdx);
                StructExprRecog serNextBase = listBaseULIdentified.get(nNextBaseIdx);
                StructExprRecog serLastUpperNote = listBaseULIdentified.get(nLastUpperNoteIdx);
                dUpperNoteFontAvgWidth /= nUpperNoteCnt;
                dUpperNoteFontAvgHeight /= nUpperNoteCnt;
                if (dMaxUpperNoteGap <= ConstantsMgr.msdNoteBaseMaxGap * Math.max(dUpperNoteFontAvgWidth, dUpperNoteFontAvgHeight)) { // times 2 because include vertical and horizontal
                    // the max gap between upper notes is narrow,
                    if (serNextBase.mnExprRecogType == EXPRRECOGTYPE_GETROOT) {
                        // the character before sqrt should be an operator, as such the upper parts must be left note
                        for (int nElementIdx = nLastBaseIdx + 1; nElementIdx <= nNextBaseIdx - 1; nElementIdx++) {
                            if (listCharLevel.get(nElementIdx) == 1) {   // upper note
                                int nGroupIdx = nElementIdx - (nLastBaseIdx + 1);
                                narrayGrouped[nGroupIdx] = 1;
                            }
                        }
                    } else if (((serNextBase.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                            && serNextBase.mType == UnitProtoType.Type.TYPE_SMALL_C)
                            || (serNextBase.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                            && serNextBase.mType == UnitProtoType.Type.TYPE_BIG_C)
                            || (serNextBase.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                            && serNextBase.mType == UnitProtoType.Type.TYPE_BIG_F))
                            && (serLastUpperNote.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                            && (serLastUpperNote.mType == UnitProtoType.Type.TYPE_ZERO
                            || serLastUpperNote.mType == UnitProtoType.Type.TYPE_SMALL_O
                            || serLastUpperNote.mType == UnitProtoType.Type.TYPE_BIG_O)
                            && nLastUpperNoteIdx == nNextBaseIdx - 1
                            && (serNextBase.mnLeft - serLastUpperNote.mnLeft) < (2 * serLastUpperNote.mnWidth))) {
                        // could be celcius or fahrenheit.
                        for (int nElementIdx = nLastBaseIdx + 1; nElementIdx <= nNextBaseIdx - 1; nElementIdx++) {
                            if (listCharLevel.get(nElementIdx) == 1) {   // upper note
                                int nGroupIdx = nElementIdx - (nLastBaseIdx + 1);
                                if (nElementIdx == nLastUpperNoteIdx) {
                                    narrayGrouped[nGroupIdx] = 1;
                                } else {
                                    narrayGrouped[nGroupIdx] = 0;
                                }
                            }
                        }
                    } else if (((serNextBase.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                            && serNextBase.mType == UnitProtoType.Type.TYPE_FORWARD_SLASH)
                            || (serNextBase.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                            && serNextBase.mType == UnitProtoType.Type.TYPE_ONE)
                            || (serNextBase.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                            && serNextBase.mType == UnitProtoType.Type.TYPE_SMALL_L))
                            && (serLastUpperNote.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                            && (serLastUpperNote.mType == UnitProtoType.Type.TYPE_ZERO
                            || serLastUpperNote.mType == UnitProtoType.Type.TYPE_SMALL_O
                            || serLastUpperNote.mType == UnitProtoType.Type.TYPE_BIG_O)
                            && nLastUpperNoteIdx == nNextBaseIdx - 1
                            && (serNextBase.mnLeft - serLastUpperNote.mnLeft) < (2 * serLastUpperNote.mnHeight))
                            && (listBaseULIdentified.size() > nNextBaseIdx + 1
                            && listCharLevel.get(nNextBaseIdx + 1) == -1    // lower note
                            && listBaseULIdentified.get(nNextBaseIdx + 1).mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                            && (listBaseULIdentified.get(nNextBaseIdx + 1).mType == UnitProtoType.Type.TYPE_ZERO
                            || listBaseULIdentified.get(nNextBaseIdx + 1).mType == UnitProtoType.Type.TYPE_SMALL_O
                            || listBaseULIdentified.get(nNextBaseIdx + 1).mType == UnitProtoType.Type.TYPE_BIG_O)   // lower note is o.
                            && (listBaseULIdentified.get(nNextBaseIdx + 1).mnLeft - serNextBase.getRightPlus1())
                            < listBaseULIdentified.get(nNextBaseIdx + 1).mnWidth)) {
                        // could be %.
                        for (int nElementIdx = nLastBaseIdx + 1; nElementIdx <= nNextBaseIdx - 1; nElementIdx++) {
                            if (listCharLevel.get(nElementIdx) == 1) {   // upper note
                                int nGroupIdx = nElementIdx - (nLastBaseIdx + 1);
                                if (nElementIdx == nLastUpperNoteIdx) {
                                    narrayGrouped[nGroupIdx] = 1;
                                } else {
                                    narrayGrouped[nGroupIdx] = 0;
                                }
                            }
                        }
                    } else {
                        // all upper notes belong to last base.
                        for (int nElementIdx = nLastBaseIdx + 1; nElementIdx <= nNextBaseIdx - 1; nElementIdx++) {
                            if (listCharLevel.get(nElementIdx) == 1) {   // upper note
                                int nGroupIdx = nElementIdx - (nLastBaseIdx + 1);
                                narrayGrouped[nGroupIdx] = 0;
                            }
                        }
                    }
                } else {
                    // the max gap between upper notes is wide which means the upper notes may belong to different bases
                    for (int nElementIdx = nLastBaseIdx + 1; nElementIdx <= nNextBaseIdx - 1; nElementIdx++) {
                        if (listCharLevel.get(nElementIdx) == 1) {   // upper note
                            int nGroupIdx = nElementIdx - (nLastBaseIdx + 1);
                            if (nGroupIdx >= nMaxUpperNoteGapIdx) {
                                narrayGrouped[nGroupIdx] = 1;   // max gap right should belong to next base
                            } else {
                                narrayGrouped[nGroupIdx] = 0;   // max gap left should belong to last base
                            }
                        }
                    }
                }
            }

            if (nLowerNoteCnt > 0) {
                dLowerNoteFontAvgWidth /= nLowerNoteCnt;
                dLowerNoteFontAvgHeight /= nLowerNoteCnt;
                if (dMaxLowerNoteGap <= ConstantsMgr.msdNoteBaseMaxGap * Math.max(dLowerNoteFontAvgWidth, dLowerNoteFontAvgHeight)) { // times 2 because include vertical and horizontal
                    // the max gap between lower notes is narrow, all lower notes belong to last base.
                    for (int nElementIdx = nLastBaseIdx + 1; nElementIdx <= nNextBaseIdx - 1; nElementIdx++) {
                        if (listCharLevel.get(nElementIdx) == -1) {   // lower note
                            int nGroupIdx = nElementIdx - (nLastBaseIdx + 1);
                            narrayGrouped[nGroupIdx] = 0;
                        }
                    }
                } else {
                    // the max gap between lower notes is wide which means the lower notes may belong to different bases
                    // here we do not worry about the second o of % because if the second o is too far away from / then
                    // even human being cannot recognize it.
                    for (int nElementIdx = nLastBaseIdx + 1; nElementIdx <= nNextBaseIdx - 1; nElementIdx++) {
                        if (listCharLevel.get(nElementIdx) == -1) {   // lower note
                            int nGroupIdx = nElementIdx - (nLastBaseIdx + 1);
                            if (nGroupIdx >= nMaxLowerNoteGapIdx) {
                                narrayGrouped[nGroupIdx] = 1;   // max gap right should belong to next base
                            } else {
                                narrayGrouped[nGroupIdx] = 0;   // max gap left should belong to last base
                            }
                        }
                    }
                }
            }

            return narrayGrouped;
        }
    }

    public static void convertNotes2HCut(int nIdxInBaseTrunk, LinkedList<StructExprRecog> listBaseTrunk,
                                         LinkedList<LinkedList<StructExprRecog>> listLeftTopNote,
                                         LinkedList<LinkedList<StructExprRecog>> listLeftBottomNote,
                                         LinkedList<LinkedList<StructExprRecog>> listUpperNote,
                                         LinkedList<LinkedList<StructExprRecog>> listLowerNote) {
        if (nIdxInBaseTrunk >= 0
                && ((listLeftTopNote.get(nIdxInBaseTrunk).size() > 0 && listUpperNote.get(nIdxInBaseTrunk).size() > 0)
                || (listLeftBottomNote.get(nIdxInBaseTrunk).size() > 0 && listLowerNote.get(nIdxInBaseTrunk).size() > 0))) {
            // this base has both leftTop notes and upper notes, maybe it is actually an h-div ser
            StructExprRecog serBase = listBaseTrunk.get(nIdxInBaseTrunk);
            StructExprRecog serBaseBase = serBase.getPrincipleSER(1);
            StructExprRecog serBaseCap = null;
            StructExprRecog serBaseUnder = null;
            if (serBase.mnExprRecogType == EXPRRECOGTYPE_HCUTCAP || serBase.mnExprRecogType == EXPRRECOGTYPE_HCUTCAPUNDER) {
                serBaseCap = serBase.mlistChildren.getFirst();
            }
            if (serBase.mnExprRecogType == EXPRRECOGTYPE_HCUTUNDER || serBase.mnExprRecogType == EXPRRECOGTYPE_HCUTCAPUNDER) {
                serBaseUnder = serBase.mlistChildren.getLast();
            }
            StructExprRecog serNewCap = serBaseCap, serNewUnder = serBaseUnder;
            if (listLeftTopNote.get(nIdxInBaseTrunk).size() > 0 && listUpperNote.get(nIdxInBaseTrunk).size() > 0) {
                boolean bIsCapMerge = true;
                LinkedList<StructExprRecog> listLeftTopNotes4Base = listLeftTopNote.get(nIdxInBaseTrunk);
                LinkedList<StructExprRecog> listUpperNotes4Base = listUpperNote.get(nIdxInBaseTrunk);
                for (int nCapIdx = 0; nCapIdx < (listLeftTopNotes4Base.size() + listUpperNotes4Base.size()); nCapIdx++) {
                    if (nCapIdx < listLeftTopNotes4Base.size() && listLeftTopNotes4Base.get(nCapIdx).getBottomPlus1() > serBaseBase.mnTop) {
                        bIsCapMerge = false;
                        break;
                    } else if (nCapIdx >= listLeftTopNotes4Base.size()
                            && listUpperNotes4Base.get(nCapIdx - listLeftTopNotes4Base.size()).getBottomPlus1() > serBaseBase.mnTop) {
                        bIsCapMerge = false;
                        break;
                    }
                }
                if (bIsCapMerge) {
                    LinkedList<StructExprRecog> listNewCap = new LinkedList<StructExprRecog>();
                    listNewCap.addAll(listLeftTopNotes4Base);
                    listLeftTopNotes4Base.clear();
                    if (serBaseCap != null) {
                        listNewCap.add(serBaseCap);
                    }
                    listNewCap.addAll(listUpperNotes4Base);
                    listUpperNotes4Base.clear();
                    serNewCap = new StructExprRecog(serBase.mbarrayBiValues);
                    serNewCap.setStructExprRecog(listNewCap, EXPRRECOGTYPE_VBLANKCUT);
                }
            }
            if (listLeftBottomNote.get(nIdxInBaseTrunk).size() > 0 && listLowerNote.get(nIdxInBaseTrunk).size() > 0) {
                boolean bIsUnderMerge = true;
                LinkedList<StructExprRecog> listLeftBottomNotes4Base = listLeftBottomNote.get(nIdxInBaseTrunk);
                LinkedList<StructExprRecog> listLowerNotes4Base = listLowerNote.get(nIdxInBaseTrunk);
                for (int nUnderIdx = 0; nUnderIdx < (listLeftBottomNotes4Base.size() + listLowerNotes4Base.size()); nUnderIdx++) {
                    if (nUnderIdx < listLeftBottomNotes4Base.size() && listLeftBottomNotes4Base.get(nUnderIdx).mnTop < serBaseBase.getBottomPlus1()) {
                        bIsUnderMerge = false;
                        break;
                    } else if (nUnderIdx >= listLeftBottomNotes4Base.size()
                            && listLowerNotes4Base.get(nUnderIdx - listLeftBottomNotes4Base.size()).mnTop < serBaseBase.getBottomPlus1()) {
                        bIsUnderMerge = false;
                        break;
                    }
                }
                if (bIsUnderMerge) {
                    LinkedList<StructExprRecog> listNewUnder = new LinkedList<StructExprRecog>();
                    listNewUnder.addAll(listLeftBottomNotes4Base);
                    listLeftBottomNotes4Base.clear();
                    if (serBaseUnder != null) {
                        listNewUnder.add(serBaseUnder);
                    }
                    listNewUnder.addAll(listLowerNotes4Base);
                    listLowerNotes4Base.clear();
                    serNewUnder = new StructExprRecog(serBase.mbarrayBiValues);
                    serNewUnder.setStructExprRecog(listNewUnder, EXPRRECOGTYPE_VBLANKCUT);
                }
            }
            StructExprRecog serNewBase = serBase;
            LinkedList<StructExprRecog> listNewChildren = new LinkedList<StructExprRecog>();
            if (serNewCap != null && serNewUnder != null) {
                serNewBase = new StructExprRecog(serBase.mbarrayBiValues);
                listNewChildren.add(serNewCap);
                listNewChildren.add(serBaseBase);
                listNewChildren.add(serNewUnder);
                serNewBase.setStructExprRecog(listNewChildren, EXPRRECOGTYPE_HCUTCAPUNDER);
            } else if (serNewCap != null) {
                serNewBase = new StructExprRecog(serBase.mbarrayBiValues);
                listNewChildren.add(serNewCap);
                listNewChildren.add(serBaseBase);
                serNewBase.setStructExprRecog(listNewChildren, EXPRRECOGTYPE_HCUTCAP);
            } else if (serNewUnder != null) {
                serNewBase = new StructExprRecog(serBase.mbarrayBiValues);
                listNewChildren.add(serBaseBase);
                listNewChildren.add(serNewUnder);
                serNewBase.setStructExprRecog(listNewChildren, EXPRRECOGTYPE_HCUTUNDER);
            }
            listBaseTrunk.set(nIdxInBaseTrunk, serNewBase);
        }
    }

    // this function analyse input merged v-cut children and children's level and pick out all the matrixs and multi-expressions
    // the output params are listMergeMatrixMExprs and listMergeMMLevel. They must be empty list when input.
    public static void lookForMatrixMExprs
    (LinkedList<StructExprRecog> listMergeVCutChildren, LinkedList<Integer> listCharLevel,
     LinkedList<StructExprRecog> listMergeMatrixMExprs, LinkedList<Integer> listMergeMMLevel) {
        // 0 is normal mode
        // 1 is round bracket mode
        // 2 is square bracket mode
        // 3 is brace mode
        // 4 is vertical line mode
        // 5 is double vertical line mode (not supported)
        int nExprGoThroughMode = 0;
        int nGoThroughModeStartIdx = -1;
        int nMMLeft = Integer.MAX_VALUE, nMMRightP1 = Integer.MIN_VALUE;
        int nMMTop = Integer.MAX_VALUE, nMMBottomP1 = Integer.MIN_VALUE;
        boolean bGetMM = false;
        for (int idx = 0; idx < listMergeVCutChildren.size(); idx++) {
            StructExprRecog ser = listMergeVCutChildren.get(idx);
            if (nExprGoThroughMode == 0) {   // we are in normal mode
                if (idx != (listMergeVCutChildren.size() - 1) && !ser.isChildListType() && listCharLevel.get(idx) == 0) {
                    if (ser.mType == UnitProtoType.Type.TYPE_ROUND_BRACKET) {
                        nExprGoThroughMode = 1;
                        nGoThroughModeStartIdx = idx;
                    } else if (ser.mType == UnitProtoType.Type.TYPE_SQUARE_BRACKET) {
                        nExprGoThroughMode = 2;
                        nGoThroughModeStartIdx = idx;
                    } else if (ser.mType == UnitProtoType.Type.TYPE_BRACE) {
                        nExprGoThroughMode = 3;
                        nGoThroughModeStartIdx = idx;
                    } else if (ser.mType == UnitProtoType.Type.TYPE_VERTICAL_LINE) {
                        nExprGoThroughMode = 4;
                        nGoThroughModeStartIdx = idx;
                    } else if (ser.mnHeight / ser.mnWidth > ConstantsMgr.msdExtendableCharWOverHThresh * ConstantsMgr.msdCharWOverHGuaranteedExtRatio) {
                        // any other character that can be start of a matrix
                        nExprGoThroughMode = 5;
                        nGoThroughModeStartIdx = idx;
                    }
                }
                if (nExprGoThroughMode == 0) {   //we are in normal mode
                    listMergeMatrixMExprs.add(ser);
                    listMergeMMLevel.add(listCharLevel.get(idx));   // listCharLevel.get(idx) should be 0
                } else {   // we start Matrix or M-expr mode
                    nMMLeft = Integer.MAX_VALUE;
                    nMMRightP1 = Integer.MIN_VALUE;
                    nMMTop = Integer.MAX_VALUE;
                    nMMBottomP1 = Integer.MIN_VALUE;
                }
            } else if ((!ser.isChildListType()) && listCharLevel.get(idx) == 0    // must be a base character
                    && idx != (listMergeVCutChildren.size() - 1)    // should not be in the last character
                    && (ser.mType == UnitProtoType.Type.TYPE_ROUND_BRACKET
                    || ser.mType == UnitProtoType.Type.TYPE_SQUARE_BRACKET
                    || ser.mType == UnitProtoType.Type.TYPE_BRACE
                    || ser.mType == UnitProtoType.Type.TYPE_VERTICAL_LINE
                    || (ser.mType != UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET
                    && ser.mType != UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET
                    && ser.mType != UnitProtoType.Type.TYPE_CLOSE_BRACE
                    && ser.mType != UnitProtoType.Type.TYPE_VERTICAL_LINE
                    && ser.mnHeight / ser.mnWidth > ConstantsMgr.msdExtendableCharWOverHThresh * ConstantsMgr.msdCharWOverHGuaranteedExtRatio))  // seems to be the beginning of a matrix
                    && ser.mnTop <= listMergeVCutChildren.get(nGoThroughModeStartIdx).mnTop
                    && ser.getBottomPlus1() >= listMergeVCutChildren.get(nGoThroughModeStartIdx).getBottomPlus1()) {
                // we are not in normal mode, but old beginning of the matrix is not a real beginning of a matrix. Beginning of a matrix should start from here.
                for (int idx1 = nGoThroughModeStartIdx; idx1 < idx; idx1++) {
                    //serFromGTS cannot be vertically cut. it must either be horizontally cut or a character.
                    StructExprRecog serFromGTS = listMergeVCutChildren.get(idx1);
                    listMergeMatrixMExprs.add(serFromGTS);
                    listMergeMMLevel.add(listCharLevel.get(idx1));
                }
                if (ser.mType == UnitProtoType.Type.TYPE_ROUND_BRACKET) {
                    nExprGoThroughMode = 1;
                    nGoThroughModeStartIdx = idx;
                } else if (ser.mType == UnitProtoType.Type.TYPE_SQUARE_BRACKET) {
                    nExprGoThroughMode = 2;
                    nGoThroughModeStartIdx = idx;
                } else if (ser.mType == UnitProtoType.Type.TYPE_BRACE) {
                    nExprGoThroughMode = 3;
                    nGoThroughModeStartIdx = idx;
                } else if (ser.mType == UnitProtoType.Type.TYPE_VERTICAL_LINE) {
                    nExprGoThroughMode = 4;
                    nGoThroughModeStartIdx = idx;
                } else if (ser.mnHeight / ser.mnWidth > ConstantsMgr.msdExtendableCharWOverHThresh * ConstantsMgr.msdCharWOverHGuaranteedExtRatio) {
                    nExprGoThroughMode = 5;
                    nGoThroughModeStartIdx = idx;
                }
                nMMLeft = Integer.MAX_VALUE;
                nMMRightP1 = Integer.MIN_VALUE;
                nMMTop = Integer.MAX_VALUE;
                nMMBottomP1 = Integer.MIN_VALUE;
            } else if (ser.isChildListType() == false && listCharLevel.get(idx) == 0    // must be a base character
                    && (ser.getBottomPlus1() - listMergeVCutChildren.get(nGoThroughModeStartIdx).mnTop) > ConstantsMgr.msdOpenCloseBracketHeightRatio * ser.mnHeight
                    && (listMergeVCutChildren.get(nGoThroughModeStartIdx).getBottomPlus1() - ser.mnTop) > ConstantsMgr.msdOpenCloseBracketHeightRatio * ser.mnHeight
                    && ser.mnHeight > ConstantsMgr.msdOpenCloseBracketHeightRatio * listMergeVCutChildren.get(nGoThroughModeStartIdx).mnHeight  // must have similar height as the start character
                    && ser.mnHeight < 1 / ConstantsMgr.msdOpenCloseBracketHeightRatio * listMergeVCutChildren.get(nGoThroughModeStartIdx).mnHeight
                    && (ser.mType == UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET
                    || ser.mType == UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET
                    || ser.mType == UnitProtoType.Type.TYPE_CLOSE_BRACE
                    || ser.mType == UnitProtoType.Type.TYPE_VERTICAL_LINE
                    || (ser.mType != UnitProtoType.Type.TYPE_ROUND_BRACKET
                    && ser.mType != UnitProtoType.Type.TYPE_SQUARE_BRACKET
                    && ser.mType != UnitProtoType.Type.TYPE_BRACE
                    && ser.mType != UnitProtoType.Type.TYPE_VERTICAL_LINE
                    && ser.mnHeight / ser.mnWidth > ConstantsMgr.msdExtendableCharWOverHThresh * ConstantsMgr.msdCharWOverHGuaranteedExtRatio))) {
                bGetMM = false;
                boolean bHasBaseBlankDiv = false;
                for (int idx1 = nGoThroughModeStartIdx + 1; idx1 < idx; idx1++) {
                    // should have at least one base level ser with type HBlankCut, otherwise, it cannot be a matrix.
                    if (listCharLevel.get(idx1) == 0 && listMergeVCutChildren.get(idx1).mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT) {
                        bHasBaseBlankDiv = true;
                        break;
                    }
                }

                if (bHasBaseBlankDiv && (ser.getBottomPlus1() - nMMTop) > ConstantsMgr.msdMatrixBracketHeightRatio * ser.mnHeight
                        && (nMMBottomP1 - ser.mnTop) > ConstantsMgr.msdMatrixBracketHeightRatio * ser.mnHeight
                        && ser.mnHeight > ConstantsMgr.msdMatrixBracketHeightRatio * (nMMBottomP1 - nMMTop)  // must have similar height as the start character
                        && ser.mnHeight < 1 / ConstantsMgr.msdMatrixBracketHeightRatio * (nMMBottomP1 - nMMTop)) {   // we do have at least one H-blank cut between two brackets/vlines
                    // ok, this might be a matrix
                    // step a. first calculate average char width and average char height.
                    double dAvgCharWidth = 0, dAvgCharHeight = 0, dSumWeight = 0;
                    for (int idx2 = nGoThroughModeStartIdx + 1; idx2 < idx; idx2++) {
                        double[] darrayMetrics = listMergeVCutChildren.get(idx2).calcAvgCharMetrics();
                        dAvgCharWidth += darrayMetrics[AVG_CHAR_WIDTH_IDX] * darrayMetrics[CHAR_CNT_IDX];
                        dAvgCharHeight += darrayMetrics[AVG_CHAR_HEIGHT_IDX] * darrayMetrics[CHAR_CNT_IDX];
                        dSumWeight += darrayMetrics[CHAR_CNT_IDX];
                    }
                    if (dSumWeight > 0) {
                        dAvgCharWidth /= dSumWeight;
                        dAvgCharHeight /= dSumWeight;
                    } else {
                        dAvgCharWidth = ConstantsMgr.msnMinCharWidthInUnit;
                        dAvgCharHeight = ConstantsMgr.msnMinCharHeightInUnit;
                    }

                    // step b. find h divs.
                    LinkedList<Integer[]> listMMHDivs = new LinkedList<Integer[]>();
                    boolean bIsLastHDivLine = false, bIsHDivLine = true;
                    int nStartMMHDivIdx = -1, nEndMMHDivIdx = -1;
                    for (int idx1 = nMMTop; idx1 < nMMBottomP1; idx1++) {
                        bIsHDivLine = true;
                        for (int idx2 = nGoThroughModeStartIdx + 1; idx2 < idx; idx2++) {
                            StructExprRecog serMMChild = listMergeVCutChildren.get(idx2);
                            if (serMMChild.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT) {
                                for (int idx3 = 0; idx3 < serMMChild.mlistChildren.size(); idx3++) {
                                    if (serMMChild.mlistChildren.get(idx3).mnTop <= idx1
                                            && idx1 < serMMChild.mlistChildren.get(idx3).getBottomPlus1()) {
                                        // cut through one element.
                                        bIsHDivLine = false;
                                        break;
                                    }
                                }
                                if (!bIsHDivLine) {
                                    break;
                                }
                            } else {   // serMMChild is a single element.
                                if (serMMChild.mnTop <= idx1
                                        && idx1 < serMMChild.getBottomPlus1()) {
                                    // cut through one element.
                                    bIsHDivLine = false;
                                    break;
                                }
                            }
                        }
                        if (!bIsLastHDivLine && bIsHDivLine) {   //start of hdiv
                            nStartMMHDivIdx = idx1;
                        } else if (bIsLastHDivLine && !bIsHDivLine) {    // end of hdiv.
                            nEndMMHDivIdx = idx1 - 1;
                            if (nEndMMHDivIdx + 1 - nStartMMHDivIdx >= ConstantsMgr.msdMatrixMExprsHDivRelaxRatio * dAvgCharHeight) {
                                Integer[] narrayHDivTopBottom = new Integer[2];
                                narrayHDivTopBottom[0] = nStartMMHDivIdx;
                                narrayHDivTopBottom[1] = nEndMMHDivIdx;
                                listMMHDivs.add(narrayHDivTopBottom);
                            }
                        }
                        bIsLastHDivLine = bIsHDivLine;
                    }

                    for (int idx1 = 0; idx1 < listMMHDivs.size(); idx1++) {
                        int nStartIdx = listMMHDivs.get(idx1)[0];
                        int nEndIdx = listMMHDivs.get(idx1)[1];
                        if (nEndIdx + 1 - nStartIdx < ConstantsMgr.msdMatrixMExprsHDivRatio * dAvgCharHeight) {
                            // the h-div gap is not wide enough, need to double-check
                            int nLastHCutTop = (idx1 == 0) ? nMMTop : (listMMHDivs.get(idx1 - 1)[1] + 1);
                            int nLastHCutBtmP1 = nStartIdx;
                            int nNextHCutTop = nEndIdx + 1;
                            int nNextHCutBtmP1 = (idx1 == listMMHDivs.size() - 1) ? nMMBottomP1 : listMMHDivs.get(idx1 + 1)[0];
                            int nSumGapTimesWidth = 0, nSumWidth = 0;
                            for (int idx2 = nGoThroughModeStartIdx + 1; idx2 < idx; idx2++) {
                                StructExprRecog serMMChild = listMergeVCutChildren.get(idx2);
                                if (serMMChild.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT) {
                                    int nLastChildInLastHCut = -1, nFirstChildInNextHCut = -1;
                                    for (int idx3 = 0; idx3 < serMMChild.mlistChildren.size(); idx3++) {
                                        if (serMMChild.mlistChildren.get(idx3).mnTop > nLastHCutTop) {
                                            continue;   // haven't been in last hcut, continue;
                                        }
                                        if (serMMChild.mlistChildren.get(idx3).mnTop >= nLastHCutTop
                                                && nLastHCutBtmP1 >= serMMChild.mlistChildren.get(idx3).getBottomPlus1()) {
                                            // cut through one element.
                                            nLastChildInLastHCut = idx3;
                                        } else if (serMMChild.mlistChildren.get(idx3).mnTop >= nNextHCutTop
                                                && nNextHCutBtmP1 >= serMMChild.mlistChildren.get(idx3).getBottomPlus1()
                                            /*&& nFirstChildInNextHCut == -1*/) { // no need to check if nFirstChildInNextHCut == -1 coz it will break anyway.
                                            nFirstChildInNextHCut = idx3;
                                            break;
                                        }
                                        if (serMMChild.mlistChildren.get(idx3).mnTop >= nNextHCutBtmP1) {   // have been beyond next hcut, so exit.
                                            break;
                                        }
                                    }
                                    if (nLastChildInLastHCut != -1 && nFirstChildInNextHCut != -1) {
                                        int nGap = serMMChild.mlistChildren.get(nFirstChildInNextHCut).mnTop
                                                - serMMChild.mlistChildren.get(nLastChildInLastHCut).getBottomPlus1();
                                        int nWidth = serMMChild.mnWidth;
                                        nSumGapTimesWidth += nGap * nWidth;
                                        nSumWidth += nWidth;
                                    }
                                }
                            }
                            double dExtHDivHeight = nEndIdx + 1 - nStartIdx;
                            if (nSumWidth > 0) {
                                dExtHDivHeight = (double) nSumGapTimesWidth / (double) nSumWidth;
                            }
                            if (dExtHDivHeight < ConstantsMgr.msdMatrixMExprsExtHDivRatio * dAvgCharHeight) {
                                // even consider ext h-div, it is still too narrow, it cannot be a h-div.
                                listMMHDivs.remove(idx1);
                                idx1--;
                            }
                        }
                    }

                    // step c. find v divs.
                    LinkedList<Integer[]> listMMVDivs = new LinkedList<Integer[]>();
                    boolean bIsLastVDivLine = false, bIsVDivLine = true;
                    int nStartMMVDivIdx = -1, nEndMMVDivIdx = -1;
                    for (int idx1 = nMMLeft; idx1 < nMMRightP1; idx1++) {
                        bIsVDivLine = true;
                        for (int idx2 = nGoThroughModeStartIdx + 1; idx2 < idx; idx2++) {
                            StructExprRecog serMMChild = listMergeVCutChildren.get(idx2);
                            // serMMChild must be a single element or Hcut list. All vcut children has been merged in listMergeVCutChildren.
                            if (serMMChild.mnLeft <= idx1
                                    && idx1 < serMMChild.getRightPlus1()) {
                                // cut through one element.
                                bIsVDivLine = false;
                                break;
                            }
                        }
                        if (!bIsLastVDivLine && bIsVDivLine) {   //start of vdiv
                            nStartMMVDivIdx = idx1;
                        } else if (bIsLastVDivLine && !bIsVDivLine) {    // end of vdiv.
                            nEndMMVDivIdx = idx1 - 1;
                            if (nEndMMVDivIdx + 1 - nStartMMVDivIdx >= ConstantsMgr.msdMatrixMExprsVDivRatio * dAvgCharWidth) {
                                boolean bIsaVCut = true;
                                double dAvgChildGapWidth = 0;
                                for (int idxHCutChild = 0; idxHCutChild <= listMMHDivs.size(); idxHCutChild++) {
                                    int nChildTop, nChildBottom, nVChildGapLeft = Integer.MIN_VALUE, nVChildGapRight = Integer.MAX_VALUE;
                                    if (idxHCutChild == 0) {
                                        nChildTop = nMMTop;
                                        nChildBottom = (listMMHDivs.size() > 0) ? (listMMHDivs.getFirst()[0] - 1) : (nMMBottomP1 - 1);
                                    } else if (idxHCutChild == listMMHDivs.size()) {
                                        // need not to worry about listMMHDivs.size() == 0 coz if listMMHDivs.size() == 0 then idxHCutChild == 0, this branch is not executed.
                                        nChildTop = listMMHDivs.getLast()[1] + 1;
                                        ;
                                        nChildBottom = nMMBottomP1 - 1;
                                    } else {
                                        nChildTop = listMMHDivs.get(idxHCutChild - 1)[1] + 1;
                                        ;
                                        nChildBottom = listMMHDivs.get(idxHCutChild)[0] - 1;
                                        ;
                                    }
                                    for (int idxMMChild = nGoThroughModeStartIdx + 1; idxMMChild < idx; idxMMChild++) {
                                        StructExprRecog serMMChild = listMergeVCutChildren.get(idxMMChild);
                                        if (serMMChild.getRightPlus1() <= nStartMMVDivIdx) {    // MMChild left of gap
                                            if (serMMChild.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT) {
                                                for (int idxMMGrandChild = 0; idxMMGrandChild < serMMChild.mlistChildren.size(); idxMMGrandChild++) {
                                                    StructExprRecog serMMGrandChild = serMMChild.mlistChildren.get(idxMMGrandChild);
                                                    if (serMMGrandChild.mnTop >= nChildTop && serMMGrandChild.getBottom() <= nChildBottom
                                                            && serMMGrandChild.getRightPlus1() > nVChildGapLeft) {
                                                        nVChildGapLeft = serMMGrandChild.getRightPlus1();
                                                    }
                                                }
                                            } else {   // treated as a single element.
                                                if (serMMChild.mnTop >= nChildTop && serMMChild.getBottom() <= nChildBottom
                                                        && serMMChild.getRightPlus1() > nVChildGapLeft) {
                                                    nVChildGapLeft = serMMChild.getRightPlus1();
                                                }
                                            }
                                        } else {   // MMChild right of gap.
                                            if (serMMChild.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT) {
                                                for (int idxMMGrandChild = 0; idxMMGrandChild < serMMChild.mlistChildren.size(); idxMMGrandChild++) {
                                                    StructExprRecog serMMGrandChild = serMMChild.mlistChildren.get(idxMMGrandChild);
                                                    if (serMMGrandChild.mnTop >= nChildTop && serMMGrandChild.getBottom() <= nChildBottom
                                                            && serMMGrandChild.mnLeft <= nVChildGapRight) {
                                                        nVChildGapRight = serMMGrandChild.mnLeft - 1;
                                                    }
                                                }
                                            } else {   // treated as a single element.
                                                if (serMMChild.mnTop >= nChildTop && serMMChild.getBottom() <= nChildBottom
                                                        && serMMChild.mnLeft <= nVChildGapRight) {
                                                    nVChildGapRight = serMMChild.mnLeft - 1;
                                                }
                                            }
                                        }
                                    }
                                    int nChildGap = nVChildGapRight - nVChildGapLeft + 1;
                                    if (nChildGap < ConstantsMgr.msdMatrixMExprsChildVDivRatio * dAvgCharWidth) {
                                        bIsaVCut = false;
                                        break;
                                    }
                                    dAvgChildGapWidth += nChildGap;
                                }
                                if (bIsaVCut) {
                                    dAvgChildGapWidth /= (listMMHDivs.size() + 1);
                                    if (dAvgChildGapWidth >= ConstantsMgr.msdMatrixMExprsAvgChildVDivRatio * dAvgCharWidth) {
                                        Integer[] narrayVDivLeftRight = new Integer[2];
                                        narrayVDivLeftRight[0] = nStartMMVDivIdx;
                                        narrayVDivLeftRight[1] = nEndMMVDivIdx;
                                        listMMVDivs.add(narrayVDivLeftRight);
                                    }
                                }
                            }
                        }
                        bIsLastVDivLine = bIsVDivLine;
                    }

                    // step d. get matrix.
                    if (listMMHDivs.size() == 0) {
                        // this is not a matrix,
                        bGetMM = false;
                    } else {
                        bGetMM = true;
                        // this is a matrix.
                        LinkedList<StructExprRecog> listMatrixCols = new LinkedList<StructExprRecog>();
                        int nVChildStartIdx = nGoThroughModeStartIdx + 1;
                        for (int idx1 = 0; idx1 <= listMMVDivs.size(); idx1++) {
                            int nVChildEndIdx = idx - 1;
                            for (int idx3 = nVChildStartIdx; idx3 < idx; idx3++) {
                                if (idx1 != listMMVDivs.size()
                                        && listMergeVCutChildren.get(idx3).getRightPlus1() > listMMVDivs.get(idx1)[0]) {
                                    nVChildEndIdx = idx3 - 1;
                                    break;
                                }
                            }

                            StructExprRecog serMatrixCol = new StructExprRecog(listMergeVCutChildren.get(idx).mbarrayBiValues);
                            LinkedList<StructExprRecog> listMatrixColChildren = new LinkedList<StructExprRecog>();
                            int[] narrayRowStartIdices = new int[nVChildEndIdx - nVChildStartIdx + 1];
                            for (int idx2 = 0; idx2 <= listMMHDivs.size(); idx2++) {
                                LinkedList<StructExprRecog> listMatrixCellChildren = new LinkedList<StructExprRecog>();
                                for (int idx3 = nVChildStartIdx; idx3 <= nVChildEndIdx; idx3++) {
                                    StructExprRecog serThisVChild = listMergeVCutChildren.get(idx3);
                                    StructExprRecog serVChildInCell = new StructExprRecog(listMergeVCutChildren.get(idx).mbarrayBiValues);
                                    if (serThisVChild.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT) {
                                        int nRowEndIdx = serThisVChild.mlistChildren.size() - 1;
                                        if (idx2 != listMMHDivs.size()) {
                                            int idx4 = narrayRowStartIdices[idx3 - nVChildStartIdx];
                                            if (idx4 >= serThisVChild.mlistChildren.size()
                                                    || serThisVChild.mlistChildren.get(idx4).mnTop >= listMMHDivs.get(idx2)[0]) {
                                                //narrayRowStartIdices[idx3 - nVChildStartIdx] has been beyond list children size()
                                                // or child narrayRowStartIdices[idx3 - nVChildStartIdx] is below this H Div, continue;
                                                continue;
                                            } else {
                                                for (; idx4 < serThisVChild.mlistChildren.size(); idx4++) {
                                                    if (serThisVChild.mlistChildren.get(idx4).mnTop >= listMMHDivs.get(idx2)[0]) {
                                                        nRowEndIdx = idx4 - 1;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                        if (narrayRowStartIdices[idx3 - nVChildStartIdx] == nRowEndIdx) {
                                            // single child.
                                            serVChildInCell = serThisVChild.mlistChildren.get(nRowEndIdx);
                                            listMatrixCellChildren.add(serVChildInCell);
                                        } else if (narrayRowStartIdices[idx3 - nVChildStartIdx] < nRowEndIdx) {
                                            // multiple child.
                                            serVChildInCell = restructHDivMatrixMExprChild(serThisVChild.mlistChildren, narrayRowStartIdices[idx3 - nVChildStartIdx], nRowEndIdx);
                                            listMatrixCellChildren.add(serVChildInCell);
                                        }
                                        narrayRowStartIdices[idx3 - nVChildStartIdx] = nRowEndIdx + 1;
                                    } else {   // single element.
                                        int nRowEndIdx = -1;
                                        if (idx2 == 0) {
                                            if (serThisVChild.getBottomPlus1() <= listMMHDivs.get(idx2)[0]) {
                                                nRowEndIdx = 0;
                                            }
                                        } else if (idx2 == listMMHDivs.size()) {
                                            if (serThisVChild.mnTop > listMMHDivs.get(idx2 - 1)[1]) {
                                                nRowEndIdx = 0;
                                            }
                                        } else if (serThisVChild.mnTop > listMMHDivs.get(idx2 - 1)[1] && serThisVChild.getBottomPlus1() <= listMMHDivs.get(idx2)[0]) {
                                            nRowEndIdx = 0;
                                        }
                                        if (nRowEndIdx == 0) {
                                            serVChildInCell = serThisVChild;
                                            listMatrixCellChildren.add(serVChildInCell);
                                        }
                                    }
                                }
                                StructExprRecog serMatrixElem = new StructExprRecog(listMergeVCutChildren.get(idx).mbarrayBiValues);
                                if (listMatrixCellChildren.size() == 0) {
                                    int nCellLeft = (idx1 == 0) ? nMMLeft : (listMMVDivs.get(idx1 - 1)[1] + 1);
                                    int nCellTop = (idx2 == 0) ? nMMTop : (listMMHDivs.get(idx2 - 1)[1] + 1);
                                    int nCellRightP1 = (idx1 == listMMVDivs.size()) ? nMMRightP1 : listMMVDivs.get(idx1)[0];
                                    int nCellBottomP1 = (idx2 == listMMHDivs.size()) ? nMMBottomP1 : listMMHDivs.get(idx2)[0];
                                    ImageChop imgChop = new ImageChop();
                                    byte[][] barrayImg = new byte[nCellRightP1 - nCellLeft][nCellBottomP1 - nCellTop];
                                    imgChop.setImageChop(barrayImg, 0, 0, nCellRightP1 - nCellLeft, nCellBottomP1 - nCellTop,
                                            listMergeVCutChildren.get(idx).mbarrayBiValues, nCellLeft, nCellTop, ImageChop.TYPE_UNKNOWN);
                                    serMatrixElem.setStructExprRecog(UnitProtoType.Type.TYPE_EMPTY, "",
                                            nCellLeft, nCellTop, nCellRightP1 - nCellLeft, nCellBottomP1 - nCellTop, imgChop, 1);
                                } else if (listMatrixCellChildren.size() == 1) {
                                    serMatrixElem = listMatrixCellChildren.getFirst();
                                } else {
                                    serMatrixElem.setStructExprRecog(listMatrixCellChildren, EXPRRECOGTYPE_VBLANKCUT);
                                }
                                listMatrixColChildren.add(serMatrixElem);
                            }
                            serMatrixCol.setStructExprRecog(listMatrixColChildren, EXPRRECOGTYPE_HBLANKCUT);
                            listMatrixCols.add(serMatrixCol);
                            nVChildStartIdx = nVChildEndIdx + 1;
                        }
                        StructExprRecog serMatrix = new StructExprRecog(listMergeVCutChildren.get(idx).mbarrayBiValues);
                        serMatrix.setStructExprRecog(listMatrixCols, EXPRRECOGTYPE_VCUTMATRIX);
                        if (nExprGoThroughMode == 4
                                || ser.mType == UnitProtoType.Type.TYPE_VERTICAL_LINE) {
                            // if the matrix is surrouned by vertical line.
                            StructExprRecog serStart = listMergeVCutChildren.get(nGoThroughModeStartIdx).clone();
                            // need not to reset left top width height coz serStart's left top width height have been cloned.
                            // similarly, need not to reset similarity
                            // here serStart should be an enum type, so need not to worry about image chop.
                            serStart.changeSEREnumType(UnitProtoType.Type.TYPE_VERTICAL_LINE, serStart.mstrFont);
                            StructExprRecog serEnd = ser.clone();
                            // need not to reset left top width height coz serEnd's left top width height have been cloned.
                            // similarly, need not to reset similarity
                            // here serEnd should be an enum type, so need not to worry about image chop.
                            serEnd.changeSEREnumType(UnitProtoType.Type.TYPE_VERTICAL_LINE, serEnd.mstrFont);
                            listMergeMatrixMExprs.add(serStart);
                            listMergeMMLevel.add(listCharLevel.get(nGoThroughModeStartIdx));    //listCharLevel.get(nGoThroughModeStartIdx) should be 0
                            listMergeMatrixMExprs.add(serMatrix);
                            listMergeMMLevel.add(listCharLevel.get(idx));   // listCharLevel.get(idx) should be 0
                            listMergeMatrixMExprs.add(serEnd);
                            listMergeMMLevel.add(listCharLevel.get(idx));   // listCharLevel.get(idx) should be 0
                        } else {
                            // the {([ and ])} should not be shown.
                            listMergeMatrixMExprs.add(serMatrix);
                            listMergeMMLevel.add(listCharLevel.get(idx));   // listCharLevel.get(idx) should be 0
                        }
                        nExprGoThroughMode = 0;
                        nGoThroughModeStartIdx = -1;
                    }

                }
                if (bGetMM == false) {   // is not Matrix or M-exprs.
                    // exit to normal mode, but rewind idx to idx - 1 because if it is v-line, it could be start of a matrix.
                    for (int idx1 = nGoThroughModeStartIdx; idx1 < idx; idx1++) {
                        //serFromGTS cannot be vertically cut. it must either be horizontally cut or a character.
                        StructExprRecog serFromGTS = listMergeVCutChildren.get(idx1);
                        listMergeMatrixMExprs.add(serFromGTS);
                        listMergeMMLevel.add(listCharLevel.get(idx1));
                    }
                    nExprGoThroughMode = 0;
                    nGoThroughModeStartIdx = -1;
                    idx--;
                }
            } else {
                // in the middle of a matrix.
                nMMLeft = (nMMLeft > ser.mnLeft) ? ser.mnLeft : nMMLeft;
                nMMRightP1 = (nMMRightP1 < ser.getRightPlus1()) ? ser.getRightPlus1() : nMMRightP1;
                nMMTop = (nMMTop > ser.mnTop) ? ser.mnTop : nMMTop;
                nMMBottomP1 = (nMMBottomP1 < ser.getBottomPlus1()) ? ser.getBottomPlus1() : nMMBottomP1;
            }
        }

        bGetMM = false;
        if (nGoThroughModeStartIdx >= 0 && nGoThroughModeStartIdx < listMergeVCutChildren.size() - 1 && nExprGoThroughMode == 3) {
            // ok we are following brace and brace is not the last char.
            StructExprRecog serStartBrace = listMergeVCutChildren.get(nGoThroughModeStartIdx);
            if ((serStartBrace.getBottomPlus1() - nMMTop) > ConstantsMgr.msdMatrixBracketHeightRatio * serStartBrace.mnHeight
                    && (nMMBottomP1 - serStartBrace.mnTop) > ConstantsMgr.msdMatrixBracketHeightRatio * serStartBrace.mnHeight
                    && serStartBrace.mnHeight > ConstantsMgr.msdMatrixBracketHeightRatio * (nMMBottomP1 - nMMTop)  // must have similar height as the start character
                    && serStartBrace.mnHeight < 1 / ConstantsMgr.msdMatrixBracketHeightRatio * (nMMBottomP1 - nMMTop)) {
                // step a. first calculate average char height and initialize variables.
                double dAvgCharHeight = 0, dSumWeight = 0;
                boolean bIsLastHDivLine = false, bIsHDivLine = true;
                int nStartMMHDivIdx = -1, nEndMMHDivIdx = -1;
                LinkedList<StructExprRecog> listMExprElems = new LinkedList<StructExprRecog>();
                LinkedList<Integer[]> listLastCutThru = new LinkedList<Integer[]>();
                LinkedList<Integer> listGapSizes = new LinkedList<Integer>();
                Integer[] narrayB4LastCutThruIdx = new Integer[listMergeVCutChildren.size() - nGoThroughModeStartIdx - 1];
                Integer[] narrayLastCutThroughIdx = new Integer[listMergeVCutChildren.size() - nGoThroughModeStartIdx - 1];
                for (int idx2 = nGoThroughModeStartIdx + 1; idx2 < listMergeVCutChildren.size(); idx2++) {
                    narrayB4LastCutThruIdx[idx2 - nGoThroughModeStartIdx - 1] = -1;
                    narrayLastCutThroughIdx[idx2 - nGoThroughModeStartIdx - 1] = -1;
                    double[] darrayMetrics = listMergeVCutChildren.get(idx2).calcAvgCharMetrics();
                    dAvgCharHeight += darrayMetrics[AVG_CHAR_HEIGHT_IDX] * darrayMetrics[CHAR_CNT_IDX];
                    dSumWeight += darrayMetrics[CHAR_CNT_IDX];
                }
                if (dSumWeight > 0) {
                    dAvgCharHeight /= dSumWeight;
                } else {
                    dAvgCharHeight = ConstantsMgr.msnMinCharHeightInUnit;
                }

                // step b. find h div
                for (int idx1 = nMMTop; idx1 < nMMBottomP1; idx1++) {
                    bIsHDivLine = true;
                    for (int idx2 = nGoThroughModeStartIdx + 1; idx2 < listMergeVCutChildren.size(); idx2++) {
                        narrayB4LastCutThruIdx[idx2 - nGoThroughModeStartIdx - 1] = narrayLastCutThroughIdx[idx2 - nGoThroughModeStartIdx - 1];
                        StructExprRecog serMMChild = listMergeVCutChildren.get(idx2);
                        if (serMMChild.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT) {
                            for (int idx3 = 0; idx3 < serMMChild.mlistChildren.size(); idx3++) {
                                if (serMMChild.mlistChildren.get(idx3).mnTop <= idx1
                                        && idx1 < serMMChild.mlistChildren.get(idx3).getBottomPlus1()) {
                                    // cut through one element.
                                    bIsHDivLine = false;
                                    narrayLastCutThroughIdx[idx2 - nGoThroughModeStartIdx - 1] = idx3;
                                    break;
                                }
                            }
                        } else {   // serMMChild is a single element.
                            if (serMMChild.mnTop <= idx1
                                    && idx1 < serMMChild.getBottomPlus1()) {
                                // cut through one element.
                                bIsHDivLine = false;
                                narrayLastCutThroughIdx[idx2 - nGoThroughModeStartIdx - 1] = 0;
                            }
                        }
                    }
                    if (!bIsLastHDivLine && bIsHDivLine) {   //start of hdiv
                        nStartMMHDivIdx = idx1;
                    } else if (bIsLastHDivLine && !bIsHDivLine) {    // end of hdiv.
                        nEndMMHDivIdx = idx1 - 1;
                        if (nEndMMHDivIdx + 1 - nStartMMHDivIdx >= ConstantsMgr.msdMatrixMExprsHDivRelaxRatio * dAvgCharHeight) {
                            Integer[] narrayCutThru2Store = new Integer[listMergeVCutChildren.size() - nGoThroughModeStartIdx - 1];
                            for (int idx2 = nGoThroughModeStartIdx + 1; idx2 < listMergeVCutChildren.size(); idx2++) {
                                int nEndChildIdx = narrayB4LastCutThruIdx[idx2 - nGoThroughModeStartIdx - 1];
                                narrayCutThru2Store[idx2 - nGoThroughModeStartIdx - 1] = nEndChildIdx;
                            }
                            listLastCutThru.add(narrayCutThru2Store);
                            listGapSizes.add(nEndMMHDivIdx + 1 - nStartMMHDivIdx);
                        }
                    }
                    bIsLastHDivLine = bIsHDivLine;
                }

                // ok, now double check if some gaps are too narrow.
                for (int idx1 = 0; idx1 < listGapSizes.size(); idx1++) {
                    if (listGapSizes.get(idx1) < ConstantsMgr.msdMatrixMExprsHDivRatio * dAvgCharHeight) {
                        // gap size is not wide enough, we need to do double check.
                        int nSumGapTimesWidth = 0, nSumWidth = 0;
                        int nNumOfVCuts = listMergeVCutChildren.size() - nGoThroughModeStartIdx - 1;
                        for (int idx2 = 0; idx2 < nNumOfVCuts; idx2++) {
                            int nLastHCutIdx = listLastCutThru.get(idx1)[idx2];
                            int nNextHCutIdx;
                            StructExprRecog serThisVCutChild = listMergeVCutChildren.get(idx2 + nGoThroughModeStartIdx + 1);
                            if (idx1 == listGapSizes.size() - 1) {  // this is the last gap
                                if (serThisVCutChild.mnExprRecogType != EXPRRECOGTYPE_HBLANKCUT) {
                                    nNextHCutIdx = 0;
                                } else {
                                    nNextHCutIdx = serThisVCutChild.mlistChildren.size() - 1;   // last child idx.
                                }
                            } else {    // this is not the last gap.
                                nNextHCutIdx = listLastCutThru.get(idx1 + 1)[idx2];
                            }
                            if (nNextHCutIdx > nLastHCutIdx) {  // at this moment nNextHCutIdx is actually the last child in next h-cut, not the first, so adjust it to the first child.
                                nNextHCutIdx = nLastHCutIdx + 1;
                                if (nLastHCutIdx >= 0) {    // it is not -1, and serThisVCutChild must be h-blank cut.
                                    int nWidth = serThisVCutChild.mnWidth;
                                    int nGap = serThisVCutChild.mlistChildren.get(nNextHCutIdx).mnTop - serThisVCutChild.mlistChildren.get(nLastHCutIdx).getBottomPlus1();
                                    nSumGapTimesWidth += nGap * nWidth;
                                    nSumWidth += nWidth;
                                }
                            }
                        }
                        double dExtGapSize = (nSumWidth == 0) ? listGapSizes.get(idx1) : (double) nSumGapTimesWidth / (double) nSumWidth;
                        if (dExtGapSize < ConstantsMgr.msdMatrixMExprsExtHDivRatio * dAvgCharHeight) {
                            // even consider ext-gap, the gap is still too narrow.
                            listGapSizes.remove(idx1);
                            listLastCutThru.remove(idx1);
                            idx1--;
                        }
                    }
                }

                // now after remove all too narrow gaps, we can construct child expressions.
                for (int idx1 = 0; idx1 <= listLastCutThru.size(); idx1++) {
                    StructExprRecog serMExprsElem = new StructExprRecog(listMergeVCutChildren.get(nGoThroughModeStartIdx).mbarrayBiValues);
                    LinkedList<StructExprRecog> listMExprsElemChildren = new LinkedList<StructExprRecog>();
                    for (int idx2 = nGoThroughModeStartIdx + 1; idx2 < listMergeVCutChildren.size(); idx2++) {
                        int nStartChildIdx = (idx1 == 0) ? 0
                                : (listLastCutThru.get(idx1 - 1)[idx2 - nGoThroughModeStartIdx - 1] + 1);
                        int nEndChildIdx;
                        if (idx1 < listLastCutThru.size()) {
                            nEndChildIdx = listLastCutThru.get(idx1)[idx2 - nGoThroughModeStartIdx - 1];
                        } else {
                            nEndChildIdx = (listMergeVCutChildren.get(idx2).mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT)
                                    ? (listMergeVCutChildren.get(idx2).mlistChildren.size() - 1) : 0;
                        }
                        if (nEndChildIdx == nStartChildIdx) {
                            if (listMergeVCutChildren.get(idx2).mnExprRecogType != EXPRRECOGTYPE_HBLANKCUT) {
                                // in this case nStartChildIdx and nEndChildIdx must be 0.
                                listMExprsElemChildren.add(listMergeVCutChildren.get(idx2));
                            } else {
                                listMExprsElemChildren.add(listMergeVCutChildren.get(idx2).mlistChildren.get(nEndChildIdx));
                            }
                        } else if (nEndChildIdx > nStartChildIdx) {
                            StructExprRecog serMEChild = restructHDivMatrixMExprChild(listMergeVCutChildren.get(idx2).mlistChildren, nStartChildIdx, nEndChildIdx);
                            listMExprsElemChildren.add(serMEChild);
                        }
                    }
                    if (listMExprsElemChildren.size() == 0) {
                        // actually this will not happen.
                    } else if (listMExprsElemChildren.size() == 1) {
                        serMExprsElem = listMExprsElemChildren.getFirst();
                    } else {
                        serMExprsElem.setStructExprRecog(listMExprsElemChildren, EXPRRECOGTYPE_VBLANKCUT);
                    }
                    listMExprElems.add(serMExprsElem);
                }

                // now all the children are ready, construct serMExprs.
                StructExprRecog serMExprs = new StructExprRecog(listMergeVCutChildren.get(nGoThroughModeStartIdx).mbarrayBiValues);
                if (listMExprElems.size() == 1) {
                    serMExprs = listMExprElems.getFirst();
                } else if (listMExprElems.size() > 1) {
                    serMExprs.setStructExprRecog(listMExprElems, EXPRRECOGTYPE_MULTIEXPRS);
                } else {
                    //actually this will not happen, if happens, means a bug.
                }
                // add multiexpres into return param
                listMergeMatrixMExprs.add(serStartBrace);
                listMergeMMLevel.add(listCharLevel.get(nGoThroughModeStartIdx));
                listMergeMatrixMExprs.add(serMExprs);
                listMergeMMLevel.add(listCharLevel.get(nGoThroughModeStartIdx));
                bGetMM = true;
            }
        }
        if (bGetMM == false && nExprGoThroughMode != 0) {    // we are still in abnormal mode.
            //add unmerged to return param
            for (int idx1 = nGoThroughModeStartIdx; idx1 < listMergeVCutChildren.size(); idx1++) {
                //serFromGTS cannot be vertically cut. it must either be horizontally cut or a character.
                StructExprRecog serFromGTS = listMergeVCutChildren.get(idx1);
                listMergeMatrixMExprs.add(serFromGTS);
                listMergeMMLevel.add(listCharLevel.get(idx1));
            }
        }
    }

    // assume listHDivChildren size is not 0 or 1.
    public static StructExprRecog restructHDivMatrixMExprChild(LinkedList<StructExprRecog> listHDivChildren,
                                                               int nStart, int nEnd) {
        if (listHDivChildren.size() == 0 || nStart > nEnd) {
            return null;    // this should not happen.
        } else if (nStart == nEnd) {
            return listHDivChildren.get(nStart);
        }
        int mnLeft = Integer.MAX_VALUE, mnTop = Integer.MAX_VALUE, mnRightP1 = Integer.MIN_VALUE, mnBottomP1 = Integer.MIN_VALUE;
        for (int idx = nStart; idx <= nEnd; idx++) {
            if (listHDivChildren.get(idx).mnLeft < mnLeft) {
                mnLeft = listHDivChildren.get(idx).mnLeft;
            }
            if (listHDivChildren.get(idx).mnTop < mnTop) {
                mnTop = listHDivChildren.get(idx).mnTop;
            }
            if (listHDivChildren.get(idx).getRightPlus1() > mnRightP1) {
                mnRightP1 = listHDivChildren.get(idx).getRightPlus1();
            }
            if (listHDivChildren.get(idx).getBottomPlus1() > mnBottomP1) {
                mnBottomP1 = listHDivChildren.get(idx).getBottomPlus1();
            }
        }
        int mnWidth = mnRightP1 - mnLeft, mnHeight = mnBottomP1 - mnTop;
        int nLongestHDivIdx = -1;
        int mnMinLnDivLen = (int) Math.max(mnWidth * ConstantsMgr.msdWorstCaseLineDivOnLenRatio,
                (mnWidth - 2.0 * ConstantsMgr.msnMinNormalCharWidthInUnit));
        for (int idx = nStart; idx <= nEnd; idx++) {
            if (listHDivChildren.get(idx).mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                    && listHDivChildren.get(idx).mType == UnitProtoType.Type.TYPE_SUBTRACT
                    && listHDivChildren.get(idx).mnWidth >= mnMinLnDivLen
                    && (nLongestHDivIdx == -1
                    || listHDivChildren.get(idx).mnWidth > listHDivChildren.get(nLongestHDivIdx).mnWidth)) {
                nLongestHDivIdx = idx;
            }
        }
        int nDivType = -1;   // 0 means blank cut, 1 means top bar, 2 means bottom bar, 3 means line div.
        // Currently assume top and bottom divs have been clearly identified in exprseperator
        // so we only need to identify if it is line div.
        if (nLongestHDivIdx > nStart && nLongestHDivIdx < nEnd) {
            StructExprRecog serLnDiv = listHDivChildren.get(nLongestHDivIdx);
            StructExprRecog serTop = listHDivChildren.get(nLongestHDivIdx - 1);
            StructExprRecog serBtm = listHDivChildren.get(nLongestHDivIdx + 1);
            int nGapTop = serLnDiv.mnTop - serTop.getBottomPlus1();
            int nGapBtm = serBtm.mnTop - serLnDiv.getBottomPlus1();
            double dLnDivGapRefHeight = ConstantsMgr.msdExpressionGap * Math.min(serTop.mnHeight, serBtm.mnHeight);
            if ((serTop.mnExprRecogType != EXPRRECOGTYPE_ENUMTYPE || serTop.mType != UnitProtoType.Type.TYPE_SUBTRACT)
                    && nGapTop <= dLnDivGapRefHeight
                    && (serBtm.mnExprRecogType != EXPRRECOGTYPE_ENUMTYPE || serBtm.mType != UnitProtoType.Type.TYPE_SUBTRACT)
                    && nGapBtm <= dLnDivGapRefHeight) {
                nDivType = 3;   // line div
            } else {
                nDivType = 0;   // if gap is too narrow, it either have been merged in exprseperator or has been identified as top bottom there.
            }
        }

        StructExprRecog serReturn = new StructExprRecog(listHDivChildren.getFirst().mbarrayBiValues);
        if (nDivType == 3) {
            LinkedList<StructExprRecog> listDivChildren = new LinkedList<StructExprRecog>();
            StructExprRecog serNewFirst = restructHDivMatrixMExprChild(listHDivChildren, nStart, nLongestHDivIdx - 1);
            StructExprRecog serNewLast = restructHDivMatrixMExprChild(listHDivChildren, nLongestHDivIdx + 1, nEnd);

            listDivChildren.add(serNewFirst);
            listDivChildren.add(listHDivChildren.get(nLongestHDivIdx));
            listDivChildren.add(serNewLast);
            serReturn.setStructExprRecog(listDivChildren, StructExprRecog.EXPRRECOGTYPE_HLINECUT);
        } else {
            LinkedList<StructExprRecog> listHCutChildren = new LinkedList<StructExprRecog>();
            for (int idx = nStart; idx <= nEnd; idx++) {
                listHCutChildren.add(listHDivChildren.get(idx));
            }
            serReturn.setStructExprRecog(listHCutChildren, EXPRRECOGTYPE_HBLANKCUT);
        }
        return serReturn;
    }

    public StructExprRecog preRestructHDivSer4MatrixMExprs(StructExprRecog ser) {
        // it is possible that ser is a type like hcap, but principle is hblank cut, so this is wrong struct, the bottom hblank cuts in
        // principle should be the top level children, the first hblank cut is the real principle for hcap.
        switch (ser.mnExprRecogType) {
            case StructExprRecog.EXPRRECOGTYPE_HBLANKCUT: {
                LinkedList<StructExprRecog> listNewSers = new LinkedList<StructExprRecog>();
                for (int idx = 0; idx < ser.mlistChildren.size(); idx++) {
                    StructExprRecog serNewChild = preRestructHDivSer4MatrixMExprs(ser.mlistChildren.get(idx));
                    if (serNewChild.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HBLANKCUT) {
                        listNewSers.addAll(serNewChild.mlistChildren);
                    } else {
                        listNewSers.add(serNewChild);
                    }
                }
                StructExprRecog serReturn = new StructExprRecog(ser.mbarrayBiValues);
                serReturn.setStructExprRecog(listNewSers, StructExprRecog.EXPRRECOGTYPE_HBLANKCUT); // listNewSers size should be > 1
                return serReturn;
            }
            case StructExprRecog.EXPRRECOGTYPE_HLINECUT: {
                StructExprRecog serNewNumerator = preRestructHDivSer4MatrixMExprs(ser.mlistChildren.getFirst());
                StructExprRecog serNewDenominator = preRestructHDivSer4MatrixMExprs(ser.mlistChildren.getLast());
                if (serNewNumerator.mnExprRecogType != EXPRRECOGTYPE_HBLANKCUT
                        && serNewDenominator.mnExprRecogType != EXPRRECOGTYPE_HBLANKCUT) {
                    return ser;
                }
                LinkedList<StructExprRecog> listSersAbove = new LinkedList<StructExprRecog>();
                if (serNewNumerator.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT) {
                    listSersAbove.addAll(serNewNumerator.mlistChildren);
                    serNewNumerator = listSersAbove.removeLast();
                }
                LinkedList<StructExprRecog> listSersBelow = new LinkedList<StructExprRecog>();
                if (serNewDenominator.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT) {
                    listSersBelow.addAll(serNewDenominator.mlistChildren);
                    serNewDenominator = listSersBelow.removeFirst();
                }
                StructExprRecog serNewHLnCut = new StructExprRecog(ser.mbarrayBiValues);
                LinkedList<StructExprRecog> listNewHLnCut = new LinkedList<StructExprRecog>();
                listNewHLnCut.add(serNewNumerator);
                listNewHLnCut.add(ser.mlistChildren.get(1));
                listNewHLnCut.add(serNewDenominator);
                serNewHLnCut.setStructExprRecog(listNewHLnCut, EXPRRECOGTYPE_HLINECUT);
                LinkedList<StructExprRecog> listNewChildren = listSersAbove;
                listNewChildren.add(serNewHLnCut);
                listNewChildren.addAll(listSersBelow);
                StructExprRecog serReturn = new StructExprRecog(ser.mbarrayBiValues);
                serReturn.setStructExprRecog(listNewChildren, EXPRRECOGTYPE_HBLANKCUT);
                return serReturn;
            }
            case StructExprRecog.EXPRRECOGTYPE_HCUTCAP:
            case StructExprRecog.EXPRRECOGTYPE_HCUTCAPUNDER:
            case StructExprRecog.EXPRRECOGTYPE_HCUTUNDER: {
                StructExprRecog serNewPrinciple = preRestructHDivSer4MatrixMExprs(ser.getPrincipleSER(1));
                if (serNewPrinciple.mnExprRecogType != EXPRRECOGTYPE_HBLANKCUT) {
                    return ser;
                }
                LinkedList<StructExprRecog> listNewBlankCuts = new LinkedList<StructExprRecog>();
                listNewBlankCuts.addAll(serNewPrinciple.mlistChildren); // have to add all instead of using = because otherwise we change serNewPrinciple.
                if (ser.mnExprRecogType != EXPRRECOGTYPE_HCUTUNDER) {
                    StructExprRecog serTop = ser.mlistChildren.getFirst();
                    StructExprRecog serMajor = listNewBlankCuts.removeFirst();
                    StructExprRecog serNew1st = new StructExprRecog(ser.mbarrayBiValues);
                    LinkedList<StructExprRecog> listNewChildren = new LinkedList<StructExprRecog>();
                    listNewChildren.add(serTop);
                    listNewChildren.add(serMajor);
                    serNew1st.setStructExprRecog(listNewChildren, EXPRRECOGTYPE_HCUTCAP);
                    listNewBlankCuts.addFirst(serNew1st);
                }
                if (ser.mnExprRecogType != EXPRRECOGTYPE_HCUTCAP) {
                    StructExprRecog serMajor = listNewBlankCuts.removeLast();
                    StructExprRecog serBottom = ser.mlistChildren.getLast();
                    StructExprRecog serNewLast = new StructExprRecog(ser.mbarrayBiValues);
                    LinkedList<StructExprRecog> listNewChildren = new LinkedList<StructExprRecog>();
                    listNewChildren.add(serMajor);
                    listNewChildren.add(serBottom);
                    serNewLast.setStructExprRecog(listNewChildren, EXPRRECOGTYPE_HCUTUNDER);
                    listNewBlankCuts.addLast(serNewLast);
                }
                StructExprRecog serReturn = new StructExprRecog(ser.mbarrayBiValues);
                serReturn.setStructExprRecog(listNewBlankCuts, EXPRRECOGTYPE_HBLANKCUT);
                return serReturn;
            }
            default:
                return ser;
        }
    }

    //除法不能作分母的
    public static boolean isHDivCannotBeBaseAnchorSER(StructExprRecog ser) {
        StructExprRecog[] serarrayNoLnDe = new StructExprRecog[3];
        if (ser.mnExprRecogType == EXPRRECOGTYPE_HCUTCAP || ser.mnExprRecogType == EXPRRECOGTYPE_HCUTCAPUNDER || ser.mnExprRecogType == EXPRRECOGTYPE_HCUTUNDER) {
            if (ser.getPrincipleSER(1).mnHeight > ConstantsMgr.msdSignificantPrincipleHThresh * ser.mnHeight) {
                return false;
            } else {
                return true;
            }
        } else if (ser.mnExprRecogType != EXPRRECOGTYPE_HLINECUT && !isActuallyHLnDivSER(ser, serarrayNoLnDe)   // cannot be converted from like under{3, top{-, 4}} to a hdiv (i.e. 3/4 in this case).
                && (ser.mnExprRecogType != EXPRRECOGTYPE_HBLANKCUT || (ser.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT && ser.mlistChildren.size() == 1))) {
            if (ser.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                    && (ser.mType == UnitProtoType.Type.TYPE_SUBTRACT || ser.mType == UnitProtoType.Type.TYPE_EQUAL
                    || ser.mType == UnitProtoType.Type.TYPE_DOT || ser.mType == UnitProtoType.Type.TYPE_DOT_MULTIPLY)) {
                // these kind of chars are too thin to be a base anchor ser
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean isActuallyHLnDivSER(StructExprRecog ser, StructExprRecog[] serarrayNoLnDe) {
        boolean bIsDivide = false;
        StructExprRecog serAboveHLn = null, serHLn = null, serBelowHLn = null;
        if ((ser.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT && ser.mlistChildren.size() == 2)
                || ser.mnExprRecogType == EXPRRECOGTYPE_HCUTCAP || ser.mnExprRecogType == EXPRRECOGTYPE_HCUTUNDER
                || ser.mnExprRecogType == EXPRRECOGTYPE_HCUTCAPUNDER) {
            // ser can match 2nd and 3rd situations in following 3 situations at the same time. So have to test them one by one.
            if (ser.mnExprRecogType == EXPRRECOGTYPE_HCUTCAPUNDER && ser.mlistChildren.get(1).mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                    && ser.mlistChildren.get(1).mType == UnitProtoType.Type.TYPE_SUBTRACT) {
                serAboveHLn = ser.mlistChildren.getFirst();
                serHLn = ser.mlistChildren.get(1);
                serBelowHLn = ser.mlistChildren.getLast();
                if ((serAboveHLn.isChildListType() || serAboveHLn.isNumberChar() || serAboveHLn.isLetterChar())
                        && (serBelowHLn.isChildListType() || serBelowHLn.isNumberChar() || serBelowHLn.isLetterChar())) {
                    double dLeftRightEdgeMaxGap = (1 - ConstantsMgr.msdHLnDivMinWidthHandwriting) * ser.mnWidth;
                    if ((ser.mnWidth - serHLn.mnWidth) <= dLeftRightEdgeMaxGap) {
                        int nDistanceLn2Above = serHLn.mnTop - serAboveHLn.mnTop - serAboveHLn.mnHeight;
                        int nDistanceLn2Below = serBelowHLn.mnTop - serHLn.mnTop - serHLn.mnHeight;
                        double dMaxGapBetweenABLn = Math.min(serAboveHLn.mnHeight, serBelowHLn.mnHeight) * ConstantsMgr.msdHLnDivMaxDistanceToTopUnder;
                        if (nDistanceLn2Above <= dMaxGapBetweenABLn && nDistanceLn2Below <= dMaxGapBetweenABLn) {
                            bIsDivide = true;
                        }
                    }
                }
            }
            if (!bIsDivide
                    && ((ser.mlistChildren.getFirst().mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT && ser.mlistChildren.getFirst().mlistChildren.size() == 2)
                    || (ser.mlistChildren.getFirst().mnExprRecogType == EXPRRECOGTYPE_HCUTUNDER))
                    && ser.mlistChildren.getFirst().mlistChildren.getLast().mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                    && ser.mlistChildren.getFirst().mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_SUBTRACT) {
                serAboveHLn = ser.mlistChildren.getFirst().mlistChildren.getFirst();
                serHLn = ser.mlistChildren.getFirst().mlistChildren.getLast();
                serBelowHLn = ser.mlistChildren.getLast();
                if ((serAboveHLn.isChildListType() || serAboveHLn.isNumberChar() || serAboveHLn.isLetterChar())
                        && (serBelowHLn.isChildListType() || serBelowHLn.isNumberChar() || serBelowHLn.isLetterChar())) {
                    double dLeftRightEdgeMaxGap = (1 - ConstantsMgr.msdHLnDivMinWidthHandwriting) * ser.mnWidth;
                    if ((ser.mnWidth - serHLn.mnWidth) <= dLeftRightEdgeMaxGap) {
                        int nDistanceLn2Above = serHLn.mnTop - serAboveHLn.mnTop - serAboveHLn.mnHeight;
                        int nDistanceLn2Below = serBelowHLn.mnTop - serHLn.mnTop - serHLn.mnHeight;
                        double dMaxGapBetweenABLn = Math.min(serAboveHLn.mnHeight, serBelowHLn.mnHeight) * ConstantsMgr.msdHLnDivMaxDistanceToTopUnder;
                        if (nDistanceLn2Above <= dMaxGapBetweenABLn && nDistanceLn2Below <= dMaxGapBetweenABLn) {
                            bIsDivide = true;
                        }
                    }
                }
            }
            if (!bIsDivide
                    && ((ser.mlistChildren.getLast().mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT && ser.mlistChildren.getLast().mlistChildren.size() == 2)
                    || (ser.mlistChildren.getLast().mnExprRecogType == EXPRRECOGTYPE_HCUTCAP))
                    && ser.mlistChildren.getLast().mlistChildren.getFirst().mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                    && ser.mlistChildren.getLast().mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SUBTRACT) {
                serAboveHLn = ser.mlistChildren.getFirst();
                serHLn = ser.mlistChildren.getLast().mlistChildren.getFirst();
                serBelowHLn = ser.mlistChildren.getLast().mlistChildren.getLast();
                if ((serAboveHLn.isChildListType() || serAboveHLn.isNumberChar() || serAboveHLn.isLetterChar())
                        && (serBelowHLn.isChildListType() || serBelowHLn.isNumberChar() || serBelowHLn.isLetterChar())) {
                    double dLeftRightEdgeMaxGap = (1 - ConstantsMgr.msdHLnDivMinWidthHandwriting) * ser.mnWidth;
                    if ((ser.mnWidth - serHLn.mnWidth) <= dLeftRightEdgeMaxGap) {
                        int nDistanceLn2Above = serHLn.mnTop - serAboveHLn.mnTop - serAboveHLn.mnHeight;
                        int nDistanceLn2Below = serBelowHLn.mnTop - serHLn.mnTop - serHLn.mnHeight;
                        double dMaxGapBetweenABLn = Math.min(serAboveHLn.mnHeight, serBelowHLn.mnHeight) * ConstantsMgr.msdHLnDivMaxDistanceToTopUnder;
                        if (nDistanceLn2Above <= dMaxGapBetweenABLn && nDistanceLn2Below <= dMaxGapBetweenABLn) {
                            bIsDivide = true;
                        }
                    }
                }
            }
        }

        if (bIsDivide) {
            serarrayNoLnDe[0] = serAboveHLn;
            serarrayNoLnDe[1] = serHLn;
            serarrayNoLnDe[2] = serBelowHLn;
        }

        return bIsDivide;
    }

    public StructExprRecog identifyHSeperatedChar() {
        StructExprRecog serReturn = this;   // ensure that serReturn != this if HSeperatedChar is identified.
        // now identify i and j
        // first identify i and j and other characters based on child sers
        // then identify i based on the position of child sers.
        if ((mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTCAP
                || mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTCAPUNDER)
                && mlistChildren.get(0).mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                && (mlistChildren.get(0).mType == UnitProtoType.Type.TYPE_DOT   // no need to worry about dot_multiply at this stage.
                || mlistChildren.get(0).mType == UnitProtoType.Type.TYPE_STAR)
                && mlistChildren.get(1).mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE) {
            if (mlistChildren.get(1).mType == UnitProtoType.Type.TYPE_VERTICAL_LINE
                    || mlistChildren.get(1).mType == UnitProtoType.Type.TYPE_ROUND_BRACKET
                    || mlistChildren.get(1).mType == UnitProtoType.Type.TYPE_SQUARE_BRACKET
                    || mlistChildren.get(1).mType == UnitProtoType.Type.TYPE_BIG_I
                    || mlistChildren.get(1).mType == UnitProtoType.Type.TYPE_ONE
                    || mlistChildren.get(1).mType == UnitProtoType.Type.TYPE_BRACE
                    || mlistChildren.get(1).mType == UnitProtoType.Type.TYPE_SMALL_I_WITHOUT_DOT
                    || mlistChildren.get(1).mType == UnitProtoType.Type.TYPE_SMALL_L) {
                StructExprRecog serSmalli = new StructExprRecog(getBiArray());
                StructExprRecog serChild0 = mlistChildren.get(0);
                StructExprRecog serChild1 = mlistChildren.get(1);
                int nLeft = Math.min(serChild0.mnLeft, serChild1.mnLeft);
                int nTop = Math.min(serChild0.mnTop, serChild1.mnTop);
                int nRightPlus1 = Math.max(serChild0.getRightPlus1(), serChild1.getRightPlus1());
                int nBottomPlus1 = Math.max(serChild0.getBottomPlus1(), serChild1.getBottomPlus1());
                int nTotalArea = serChild0.getArea() + serChild1.getArea();
                LinkedList<ImageChop> listParts = new LinkedList<ImageChop>();
                ImageChop imgChopTop = serChild0.getImageChop(false);
                listParts.add(imgChopTop);
                ImageChop imgChopBase = serChild1.getImageChop(false);
                listParts.add(imgChopBase);
                ImageChop imgChop4SER = ExprSeperator.mergeImgChopsWithSameOriginal(listParts);   // need not to shrink imgChop4SER because it has been min container.
                double dSimilarity = (serChild0.getArea() * serChild0.mdSimilarity
                        + serChild1.getArea() * serChild1.mdSimilarity) / nTotalArea;  // total area should not be zero here.
                serSmalli.setStructExprRecog(UnitProtoType.Type.TYPE_SMALL_I, UNKNOWN_FONT_TYPE, nLeft, nTop, nRightPlus1 - nLeft, nBottomPlus1 - nTop, imgChop4SER, dSimilarity);
                if (mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTCAP) {
                    serReturn = serSmalli;
                } else {
                    LinkedList<StructExprRecog> listSmalliUnder = new LinkedList<StructExprRecog>();
                    listSmalliUnder.add(serSmalli);
                    listSmalliUnder.add(mlistChildren.getLast());
                    serReturn = new StructExprRecog(getBiArray());
                    serReturn.setStructExprRecog(listSmalliUnder, StructExprRecog.EXPRRECOGTYPE_HCUTUNDER);
                }
            } else if (mlistChildren.get(1).mType == UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET
                    || mlistChildren.get(1).mType == UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET
                    || mlistChildren.get(1).mType == UnitProtoType.Type.TYPE_BIG_J
                    || mlistChildren.get(1).mType == UnitProtoType.Type.TYPE_CLOSE_BRACE
                    || mlistChildren.get(1).mType == UnitProtoType.Type.TYPE_INTEGRATE
                    || mlistChildren.get(1).mType == UnitProtoType.Type.TYPE_SMALL_J_WITHOUT_DOT) {
                StructExprRecog serSmallj = new StructExprRecog(getBiArray());
                StructExprRecog serChild0 = mlistChildren.get(0);
                StructExprRecog serChild1 = mlistChildren.get(1);
                int nLeft = Math.min(serChild0.mnLeft, serChild1.mnLeft);
                int nTop = Math.min(serChild0.mnTop, serChild1.mnTop);
                int nRightPlus1 = Math.max(serChild0.getRightPlus1(), serChild1.getRightPlus1());
                int nBottomPlus1 = Math.max(serChild0.getBottomPlus1(), serChild1.getBottomPlus1());
                int nTotalArea = serChild0.getArea() + serChild1.getArea();
                LinkedList<ImageChop> listParts = new LinkedList<ImageChop>();
                ImageChop imgChopTop = serChild0.getImageChop(false);
                listParts.add(imgChopTop);
                ImageChop imgChopBase = serChild1.getImageChop(false);
                listParts.add(imgChopBase);
                ImageChop imgChop4SER = ExprSeperator.mergeImgChopsWithSameOriginal(listParts);   // need not to shrink imgChop4SER because it has been min container.
                double dSimilarity = (serChild0.getArea() * serChild0.mdSimilarity
                        + serChild1.getArea() * serChild1.mdSimilarity) / nTotalArea;  // total area should not be zero here.
                serSmallj.setStructExprRecog(UnitProtoType.Type.TYPE_SMALL_J, UNKNOWN_FONT_TYPE, nLeft, nTop, nRightPlus1 - nLeft, nBottomPlus1 - nTop, imgChop4SER, dSimilarity);
                if (mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTCAP) {
                    serReturn = serSmallj;
                } else {
                    LinkedList<StructExprRecog> listSmalljUnder = new LinkedList<StructExprRecog>();
                    listSmalljUnder.add(serSmallj);
                    listSmalljUnder.add(mlistChildren.getLast());
                    serReturn = new StructExprRecog(getBiArray());
                    serReturn.setStructExprRecog(listSmalljUnder, StructExprRecog.EXPRRECOGTYPE_HCUTUNDER);
                }
            }
        } else if (mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTUNDER   // need not to worry about cap under coz ! should not have a cap.
                && mlistChildren.getLast().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                && (mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_DOT    // need not to worry about dot multiply at this stage.
                || mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_STAR)
                && mlistChildren.getFirst().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE) {
            if (mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_VERTICAL_LINE
                    || mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_ROUND_BRACKET
                    || mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SQUARE_BRACKET
                    || mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET
                    || mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET
                    || mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_BIG_I
                    || mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_BIG_J
                    || mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_ONE
                    || mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_BRACE
                    || mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_CLOSE_BRACE
                    || mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SMALL_I_WITHOUT_DOT
                    || mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SMALL_J_WITHOUT_DOT
                    || mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_INTEGRATE
                    || mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SMALL_L) {
                serReturn = new StructExprRecog(getBiArray());
                StructExprRecog serChild0 = mlistChildren.get(0);
                StructExprRecog serChild1 = mlistChildren.get(1);
                int nLeft = Math.min(serChild0.mnLeft, serChild1.mnLeft);
                int nTop = Math.min(serChild0.mnTop, serChild1.mnTop);
                int nRightPlus1 = Math.max(serChild0.getRightPlus1(), serChild1.getRightPlus1());
                int nBottomPlus1 = Math.max(serChild0.getBottomPlus1(), serChild1.getBottomPlus1());
                int nTotalArea = serChild0.getArea() + serChild1.getArea();
                LinkedList<ImageChop> listParts = new LinkedList<ImageChop>();
                ImageChop imgChopTop = serChild0.getImageChop(false);
                listParts.add(imgChopTop);
                ImageChop imgChopBase = serChild1.getImageChop(false);
                listParts.add(imgChopBase);
                ImageChop imgChop4SER = ExprSeperator.mergeImgChopsWithSameOriginal(listParts);   // need not to shrink imgChop4SER because it has been min container.
                double dSimilarity = (serChild0.getArea() * serChild0.mdSimilarity
                        + serChild1.getArea() * serChild1.mdSimilarity) / nTotalArea;  // total area should not be zero here.
                serReturn.setStructExprRecog(UnitProtoType.Type.TYPE_EXCLAIMATION, UNKNOWN_FONT_TYPE, nLeft, nTop, nRightPlus1 - nLeft, nBottomPlus1 - nTop, imgChop4SER, dSimilarity);
            }
        } else if (((mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HBLANKCUT && mlistChildren.size() == 2)
                || mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTCAP
                || mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTUNDER)
                && mlistChildren.getFirst().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                && mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SUBTRACT
                && mlistChildren.getLast().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                && mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_SUBTRACT) {
            StructExprRecog serFirstChild = mlistChildren.getFirst();
            StructExprRecog serLastChild = mlistChildren.getLast();
            int nOverLapLeft = Math.max(serFirstChild.mnLeft, serLastChild.mnLeft);
            int nOverLapRightP1 = Math.min(serFirstChild.mnLeft + serFirstChild.mnWidth, serLastChild.mnLeft + serLastChild.mnWidth);
            int nOverLapWidth = nOverLapRightP1 - nOverLapLeft;
            if (nOverLapWidth >= ConstantsMgr.msdEqAEqOverlappedHLnWidthRatio * serFirstChild.mnWidth
                    && nOverLapWidth >= ConstantsMgr.msdEqAEqOverlappedHLnWidthRatio * serLastChild.mnWidth
                    && ConstantsMgr.msdEqMinWidthOverHeight * (serLastChild.mnTop - serFirstChild.mnTop - serFirstChild.mnHeight) < nOverLapWidth) {
                serReturn = new StructExprRecog(getBiArray());
                LinkedList<ImageChop> listParts = new LinkedList<ImageChop>();
                ImageChop imgChopPart1 = mlistChildren.getFirst().getImageChop(false);
                listParts.add(imgChopPart1);
                ImageChop imgChopPart2 = mlistChildren.getLast().getImageChop(false);
                listParts.add(imgChopPart2);
                ImageChop imgChop4SER = ExprSeperator.mergeImgChopsWithSameOriginal(listParts);   // need not to shrink imgChop4SER because it has been min container.
                serReturn.setStructExprRecog(UnitProtoType.Type.TYPE_EQUAL, UNKNOWN_FONT_TYPE, imgChop4SER);
                serReturn.setSERPlace(this);
                serReturn.setSimilarity(mdSimilarity);
            }
        } else if (((mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HBLANKCUT && mlistChildren.size() == 3)
                || mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTCAPUNDER)
                && mlistChildren.getFirst().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                && mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SUBTRACT
                && mlistChildren.get(1).mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                && mlistChildren.get(1).mType == UnitProtoType.Type.TYPE_SUBTRACT
                && mlistChildren.getLast().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                && mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_SUBTRACT) {
            StructExprRecog serFirstChild = mlistChildren.getFirst();
            StructExprRecog serSecondChild = mlistChildren.get(1);
            StructExprRecog serLastChild = mlistChildren.getLast();
            int nOverLapLeft = Math.max(Math.max(serFirstChild.mnLeft, serSecondChild.mnLeft), serLastChild.mnLeft);
            int nOverLapRightP1 = Math.min(Math.min(serFirstChild.mnLeft + serFirstChild.mnWidth,
                    serSecondChild.mnLeft + serSecondChild.mnWidth),
                    serLastChild.mnLeft + serLastChild.mnWidth);
            int nOverLapWidth = nOverLapRightP1 - nOverLapLeft;
            if (nOverLapWidth >= ConstantsMgr.msdEqAEqOverlappedHLnWidthRatio * serFirstChild.mnWidth
                    && nOverLapWidth >= ConstantsMgr.msdEqAEqOverlappedHLnWidthRatio * serSecondChild.mnWidth
                    && nOverLapWidth >= ConstantsMgr.msdEqAEqOverlappedHLnWidthRatio * serLastChild.mnWidth
                    && ConstantsMgr.msdAEqMinWidthOverHeight * (serLastChild.mnTop - serFirstChild.mnTop - serFirstChild.mnHeight) < nOverLapWidth) {
                serReturn = new StructExprRecog(getBiArray());
                LinkedList<ImageChop> listParts = new LinkedList<ImageChop>();
                ImageChop imgChopPart1 = mlistChildren.getFirst().getImageChop(false);
                listParts.add(imgChopPart1);
                ImageChop imgChopPart2 = mlistChildren.get(1).getImageChop(false);
                listParts.add(imgChopPart2);
                ImageChop imgChopPart3 = mlistChildren.getLast().getImageChop(false);
                listParts.add(imgChopPart3);
                ImageChop imgChop4SER = ExprSeperator.mergeImgChopsWithSameOriginal(listParts);   // need not to shrink imgChop4SER because it has been min container.
                serReturn.setStructExprRecog(UnitProtoType.Type.TYPE_EQUAL_ALWAYS, UNKNOWN_FONT_TYPE, imgChop4SER);
                serReturn.setSERPlace(this);
                serReturn.setSimilarity(mdSimilarity);
            }
        } else if (mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HLINECUT
                && mlistChildren.getFirst().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                && mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SUBTRACT
                && mlistChildren.getLast().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                && mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_SUBTRACT) {
            StructExprRecog serFirstChild = mlistChildren.getFirst();
            StructExprRecog serLastChild = mlistChildren.getLast();
            int nOverLapLeft = Math.max(serFirstChild.mnLeft, serLastChild.mnLeft);
            int nOverLapRightP1 = Math.min(serFirstChild.mnLeft + serFirstChild.mnWidth, serLastChild.mnLeft + serLastChild.mnWidth);
            int nOverLapWidth = nOverLapRightP1 - nOverLapLeft;
            if (nOverLapWidth >= ConstantsMgr.msdEqAEqOverlappedHLnWidthRatio * serLastChild.mnWidth
                    && nOverLapWidth >= ConstantsMgr.msdEqAEqOverlappedHLnWidthRatio * serFirstChild.mnWidth
                    && ConstantsMgr.msdAEqMinWidthOverHeight * (serLastChild.mnTop - serFirstChild.mnTop - serFirstChild.mnHeight) < nOverLapWidth) {
                serReturn = new StructExprRecog(getBiArray());
                byte[][] barrayImage = new byte[mnWidth][mnHeight];
                System.arraycopy(getBiArray(), mnLeft, barrayImage, 0, mnHeight);
                ImageChop imgChop4SER = new ImageChop();
                // a compromise here. We don't know the imagechop of h-line-div, so we just copy the area from original biarray. This may
                // includes some points from other sers. But should be ok.
                imgChop4SER.setImageChop(barrayImage, 0, 0, mnWidth, mnHeight, getBiArray(), mnLeft, mnTop, ImageChop.TYPE_UNKNOWN);
                serReturn.setStructExprRecog(UnitProtoType.Type.TYPE_EQUAL_ALWAYS, UNKNOWN_FONT_TYPE, imgChop4SER);
                serReturn.setSERPlace(this);
                serReturn.setSimilarity(mdSimilarity);
            }
            // do not consider the case that first cutCap (=) cutUnder (_) or first cutUnder (=) then cutCap(-) because very unlikely to occur
        } else if (((mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HBLANKCUT && mlistChildren.size() == 3)
                || mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTCAPUNDER)
                && mlistChildren.getFirst().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                && mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_DOT
                && mlistChildren.get(1).mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                && mlistChildren.get(1).mType == UnitProtoType.Type.TYPE_SUBTRACT
                && mlistChildren.getLast().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                && mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_DOT
        ) {
            StructExprRecog serFirstChild = mlistChildren.getFirst();
            StructExprRecog serSecondChild = mlistChildren.get(1);
            StructExprRecog serLastChild = mlistChildren.getLast();
            if ((serFirstChild.mnLeft - serSecondChild.mnLeft) >= (ConstantsMgr.msdDivDotHPlaceThresh * serSecondChild.mnWidth)
                    && (serLastChild.mnLeft - serSecondChild.mnLeft) >= (ConstantsMgr.msdDivDotHPlaceThresh * serSecondChild.mnWidth)
                    && (serSecondChild.mnLeft + serSecondChild.mnWidth - serFirstChild.mnLeft - serFirstChild.mnWidth)
                    >= (ConstantsMgr.msdDivDotHPlaceThresh * serSecondChild.mnWidth)
                    && (serSecondChild.mnLeft + serSecondChild.mnWidth - serLastChild.mnLeft - serLastChild.mnWidth)
                    >= (ConstantsMgr.msdDivDotHPlaceThresh * serSecondChild.mnWidth)
                    && (serSecondChild.mnTop - serFirstChild.mnTop - serFirstChild.mnHeight)
                    >= ConstantsMgr.msdDivDotVPlaceThresh * mnHeight
                    && (serLastChild.mnTop - serSecondChild.mnTop - serSecondChild.mnHeight)
                    >= ConstantsMgr.msdDivDotVPlaceThresh * mnHeight) {
                serReturn = new StructExprRecog(getBiArray());
                LinkedList<ImageChop> listParts = new LinkedList<ImageChop>();
                ImageChop imgChopPart1 = mlistChildren.getFirst().getImageChop(false);
                listParts.add(imgChopPart1);
                ImageChop imgChopPart2 = mlistChildren.get(1).getImageChop(false);
                listParts.add(imgChopPart2);
                ImageChop imgChopPart3 = mlistChildren.getLast().getImageChop(false);
                listParts.add(imgChopPart3);
                ImageChop imgChop4SER = ExprSeperator.mergeImgChopsWithSameOriginal(listParts);   // need not to shrink imgChop4SER because it has been min container.
                serReturn.setStructExprRecog(UnitProtoType.Type.TYPE_DIVIDE, UNKNOWN_FONT_TYPE, imgChop4SER);
                serReturn.setSERPlace(this);
                serReturn.setSimilarity(mdSimilarity);
            }
        } else if (mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HLINECUT
                && mlistChildren.getFirst().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                && mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_DOT
                && mlistChildren.getLast().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                && mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_DOT) {
            StructExprRecog serFirstChild = mlistChildren.getFirst();
            StructExprRecog serLastChild = mlistChildren.getLast();
            if ((serFirstChild.mnLeft - mnLeft) >= (ConstantsMgr.msdDivDotHPlaceThresh * mnWidth)
                    && (serLastChild.mnLeft - mnLeft) >= (ConstantsMgr.msdDivDotHPlaceThresh * mnWidth)
                    && (mnLeft + mnWidth - serFirstChild.mnLeft - serFirstChild.mnWidth)
                    >= (ConstantsMgr.msdDivDotHPlaceThresh * mnWidth)
                    && (mnLeft + mnWidth - serLastChild.mnLeft - serLastChild.mnWidth)
                    >= (ConstantsMgr.msdDivDotHPlaceThresh * mnWidth)
                    && (serLastChild.mnTop - serFirstChild.mnTop - serFirstChild.mnHeight)
                    >= ConstantsMgr.msdDivDotVPlaceThresh * 2 * mnHeight) {
                serReturn = new StructExprRecog(getBiArray());
                byte[][] barrayImage = new byte[mnWidth][mnHeight];
                System.arraycopy(getBiArray(), mnLeft, barrayImage, 0, mnHeight);
                ImageChop imgChop4SER = new ImageChop();
                // a compromise here. We don't know the imagechop of h-line-div, so we just copy the area from original biarray. This may
                // includes some points from other sers. But should be ok.
                imgChop4SER.setImageChop(barrayImage, 0, 0, mnWidth, mnHeight, getBiArray(), mnLeft, mnTop, ImageChop.TYPE_UNKNOWN);
                serReturn.setStructExprRecog(UnitProtoType.Type.TYPE_DIVIDE, UNKNOWN_FONT_TYPE, imgChop4SER);
                serReturn.setSERPlace(this);
                serReturn.setSimilarity(mdSimilarity);
            }
        }   // do not consider the case that first cutCap (. over -) cutUnder (.) or first cutUnder (- over .) then cutCap(.) because very unlikely to occur

        if (serReturn.equals(this)) {
            // now identify i and !based on child position, this is for some under notes small chars
            if ((mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTCAP
                    && mlistChildren.getFirst().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    && (mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_DOT   // only ., | and - are full filled dot possible chars
                    || (mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SUBTRACT && mlistChildren.getFirst().mnWidth < 2 * mlistChildren.getFirst().mnHeight)
                    || (mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_VERTICAL_LINE && 2 * mlistChildren.getFirst().mnWidth > mlistChildren.getFirst().mnHeight)))
                    || (mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE
                    && mlistChildren.getLast().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    && (mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_DOT   // only ., | and - are full filled dot possible chars
                    || (mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_SUBTRACT && mlistChildren.getLast().mnWidth < 2 * mlistChildren.getLast().mnHeight)
                    || (mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_VERTICAL_LINE && 2 * mlistChildren.getLast().mnWidth > mlistChildren.getLast().mnHeight)))
                    || (mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HBLANKCUT && mlistChildren.size() == 2
                    && mlistChildren.getFirst().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    && (mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_DOT   // only ., | and - are full filled dot possible chars
                    || (mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SUBTRACT && mlistChildren.getFirst().mnWidth < 2 * mlistChildren.getFirst().mnHeight)
                    || (mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_VERTICAL_LINE && 2 * mlistChildren.getFirst().mnWidth > mlistChildren.getFirst().mnHeight)))) {
                StructExprRecog serDot = (mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE) ?
                        mlistChildren.getFirst() : mlistChildren.getLast();
                StructExprRecog serUnder = (mnExprRecogType != StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE) ?
                        mlistChildren.getLast() : mlistChildren.getFirst();
                if (serUnder.mnWidth <= (serDot.mnWidth * 2) && serUnder.mnHeight >= (serDot.mnHeight * 2.5) && serUnder.mnHeight <= (serDot.mnHeight * 6)
                        && serUnder.mnTop - serDot.getBottomPlus1() <= serDot.mnHeight
                        && serDot.getRightPlus1() - serUnder.getRightPlus1() < serDot.mnWidth
                        && serUnder.mnLeft - serDot.mnLeft < (serDot.mnWidth / 2.0)) {
                    // this is a i
                    serReturn = new StructExprRecog(getBiArray());
                    LinkedList<ImageChop> listParts = new LinkedList<ImageChop>();
                    ImageChop imgChop1 = mlistChildren.getFirst().getImageChop(false);
                    listParts.add(imgChop1);
                    ImageChop imgChop2 = mlistChildren.getLast().getImageChop(false);
                    listParts.add(imgChop2);
                    ImageChop imgChop4SER = ExprSeperator.mergeImgChopsWithSameOriginal(listParts);   // need not to shrink imgChop4SER because it has been min container.
                    int nTotalArea = serDot.getArea() + serUnder.getArea();
                    double dSimilarity = (serDot.getArea() * serDot.mdSimilarity
                            + serUnder.getArea() * serUnder.mdSimilarity) / nTotalArea;  // total area should not be zero here.
                    serReturn.setStructExprRecog(UnitProtoType.Type.TYPE_SMALL_I, UNKNOWN_FONT_TYPE, mnLeft, mnTop, mnWidth, mnHeight, imgChop4SER, dSimilarity);
                }
                // do not identify j in a similar way because the under part of j is very big, it is possible that they are
                // some other characters. and under part of j is easy to recognize.
            } else if ((mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTUNDER
                    && mlistChildren.getLast().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    && mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_DOT)
                    || (mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HBLANKCUT && mlistChildren.size() == 2
                    && mlistChildren.getLast().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    && mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_DOT)) {
                StructExprRecog serTop = mlistChildren.getFirst();
                StructExprRecog serDot = mlistChildren.getLast();
                if (serTop.mnWidth <= (serDot.mnWidth * 2) && serTop.mnHeight <= (serDot.mnHeight * 4)
                        && serDot.mnTop - serTop.getBottomPlus1() <= serDot.mnHeight
                        && serDot.getRightPlus1() - serTop.getRightPlus1() < serDot.mnWidth / 2.0
                        && serTop.mnLeft - serDot.mnLeft < serDot.mnWidth) {
                    // this is a i
                    serReturn = new StructExprRecog(getBiArray());
                    LinkedList<ImageChop> listParts = new LinkedList<ImageChop>();
                    ImageChop imgChop1 = mlistChildren.getFirst().getImageChop(false);
                    listParts.add(imgChop1);
                    ImageChop imgChop2 = mlistChildren.getLast().getImageChop(false);
                    listParts.add(imgChop2);
                    ImageChop imgChop4SER = ExprSeperator.mergeImgChopsWithSameOriginal(listParts);   // need not to shrink imgChop4SER because it has been min container.
                    int nTotalArea = serDot.getArea() + serTop.getArea();
                    double dSimilarity = (serTop.getArea() * serTop.mdSimilarity
                            + serDot.getArea() * serDot.mdSimilarity) / nTotalArea;  // total area should not be zero here.
                    serReturn.setStructExprRecog(UnitProtoType.Type.TYPE_EXCLAIMATION, UNKNOWN_FONT_TYPE, mnLeft, mnTop, mnWidth, mnHeight, imgChop4SER, dSimilarity);
                }
            }
        }

        return serReturn;
    }

    public StructExprRecog identifyStrokeBrokenChar() {
        StructExprRecog serReturn = this;   // ensure that serReturn != this if HSeperatedChar is identified.
        if (mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTUNDER && mlistChildren.size() == 2) {
            if (mlistChildren.getLast().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    && mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_SUBTRACT
                    && ((mlistChildren.getFirst().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    && (mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SEVEN
                    || mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_ONE
                    || mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_NINE)
                    && mlistChildren.getFirst().mnWidth >= ConstantsMgr.msdDisconnected2BaseUnderWidthRatio * mlistChildren.getLast().mnWidth
                    && ConstantsMgr.msdDisconnected2BaseUnderWidthRatio * mlistChildren.getFirst().mnWidth <= mlistChildren.getLast().mnWidth)
                    || (mlistChildren.getFirst().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HLINECUT
                    && (mlistChildren.getFirst().mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_SEVEN
                    || mlistChildren.getFirst().mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_ONE
                    || mlistChildren.getFirst().mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_NINE)
                    && mlistChildren.getFirst().mlistChildren.getLast().mnWidth >= ConstantsMgr.msdDisconnected2BaseUnderWidthRatio * mlistChildren.getLast().mnWidth
                    && ConstantsMgr.msdDisconnected2BaseUnderWidthRatio * mlistChildren.getFirst().mlistChildren.getLast().mnWidth <= mlistChildren.getLast().mnWidth))) {
                // this is actually 2, but because the stroke is broken, it is misrecognized as 7(or 9 or 1) underline
                // another possibility is , if it is like */7 with a under _, it could be */2.
                serReturn = new StructExprRecog(getBiArray());
                serReturn.setStructExprRecog(UnitProtoType.Type.TYPE_TWO, UNKNOWN_FONT_TYPE, mnLeft, mnTop, mnWidth, mnHeight, getImageChop(true), mdSimilarity);
            }
        }
        return serReturn;
    }

    // calculate average char width and height.
    public double[] calcAvgCharMetrics() {
        double[] darrayMetrics = new double[TOTAL_IDX_CNT];
        if (!isChildListType()) {
            darrayMetrics[AVG_CHAR_WIDTH_IDX] = mnWidth;
            darrayMetrics[AVG_CHAR_HEIGHT_IDX] = mnHeight;
            darrayMetrics[CHAR_CNT_IDX] = 1.0; // weight.
            if (mType != UnitProtoType.Type.TYPE_UNKNOWN
                    && mType != UnitProtoType.Type.TYPE_DOT
                    && mType != UnitProtoType.Type.TYPE_ROUND_BRACKET
                    && mType != UnitProtoType.Type.TYPE_SQUARE_BRACKET
                    && mType != UnitProtoType.Type.TYPE_BRACE
                    && mType != UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET
                    && mType != UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET
                    && mType != UnitProtoType.Type.TYPE_CLOSE_BRACE
                    && mType != UnitProtoType.Type.TYPE_VERTICAL_LINE
                    && mType != UnitProtoType.Type.TYPE_SUBTRACT
                    && mType != UnitProtoType.Type.TYPE_SQRT_LEFT
                    && mType != UnitProtoType.Type.TYPE_SQRT_SHORT
                    && mType != UnitProtoType.Type.TYPE_SQRT_MEDIUM
                    && mType != UnitProtoType.Type.TYPE_SQRT_LONG
                    && mType != UnitProtoType.Type.TYPE_SQRT_TALL
                    && mType != UnitProtoType.Type.TYPE_SQRT_VERY_TALL) {
                darrayMetrics[AVG_NORMAL_CHAR_WIDTH_IDX] = mnWidth;
                darrayMetrics[AVG_NORMAL_CHAR_HEIGHT_IDX] = mnHeight;
                darrayMetrics[NORMAL_CHAR_CNT_IDX] = 1.0; // weight.
            } else {
                darrayMetrics[AVG_NORMAL_CHAR_WIDTH_IDX] = mnWidth;
                darrayMetrics[AVG_NORMAL_CHAR_HEIGHT_IDX] = mnHeight;
                darrayMetrics[NORMAL_CHAR_CNT_IDX] = 0.0; // weight.
            }
            darrayMetrics[AVG_VGAP_IDX] = mnWidth;
            darrayMetrics[VGAP_CNT_IDX] = 0.0;  // weight
        } else {
            double dSumWidth = 0, dSumNormalCharWidth = 0;
            double dSumHeight = 0, dSumNormalCharHeight = 0;
            double dTotalWeight = 0, dTotalNormalCharWeight = 0;
            double dVGap = 0, dTotalVGapWeight = 0;
            for (int idx = 0; idx < mlistChildren.size(); idx++) {
                double[] darrayThisMetrics = mlistChildren.get(idx).calcAvgCharMetrics();
                dSumWidth += darrayThisMetrics[AVG_CHAR_WIDTH_IDX] * darrayThisMetrics[CHAR_CNT_IDX];
                dSumNormalCharWidth += darrayThisMetrics[AVG_NORMAL_CHAR_WIDTH_IDX] * darrayThisMetrics[NORMAL_CHAR_CNT_IDX];
                dSumHeight += darrayThisMetrics[AVG_CHAR_HEIGHT_IDX] * darrayThisMetrics[CHAR_CNT_IDX];
                dSumNormalCharHeight += darrayThisMetrics[AVG_NORMAL_CHAR_HEIGHT_IDX] * darrayThisMetrics[NORMAL_CHAR_CNT_IDX];
                dTotalWeight += darrayThisMetrics[CHAR_CNT_IDX];
                dTotalNormalCharWeight += darrayThisMetrics[NORMAL_CHAR_CNT_IDX];
                dVGap += darrayThisMetrics[AVG_VGAP_IDX] * darrayThisMetrics[VGAP_CNT_IDX];
                dTotalVGapWeight += darrayThisMetrics[VGAP_CNT_IDX];
                if (idx > 0 && (mnExprRecogType == EXPRRECOGTYPE_VBLANKCUT || mnExprRecogType == EXPRRECOGTYPE_VCUTMATRIX)) {
                    double dThisVGap = mlistChildren.get(idx).mnLeft - mlistChildren.get(idx - 1).getRightPlus1();
                    dVGap += (dThisVGap > 0) ? dThisVGap : 0;
                    dTotalVGapWeight += (dThisVGap > 0) ? 1.0 : 0;
                }
            }
            darrayMetrics[AVG_CHAR_WIDTH_IDX] = Math.max(ConstantsMgr.msnMinCharWidthInUnit, dSumWidth / dTotalWeight);
            darrayMetrics[AVG_CHAR_HEIGHT_IDX] = Math.max(ConstantsMgr.msnMinCharHeightInUnit, dSumHeight / dTotalWeight);
            darrayMetrics[CHAR_CNT_IDX] = dTotalWeight;
            if (dTotalNormalCharWeight == 0) {
                darrayMetrics[AVG_NORMAL_CHAR_WIDTH_IDX] = darrayMetrics[AVG_CHAR_WIDTH_IDX];
                darrayMetrics[AVG_NORMAL_CHAR_HEIGHT_IDX] = darrayMetrics[AVG_CHAR_HEIGHT_IDX];
            } else {
                darrayMetrics[AVG_NORMAL_CHAR_WIDTH_IDX] = Math.max(ConstantsMgr.msnMinCharWidthInUnit, dSumNormalCharWidth / dTotalNormalCharWeight);
                darrayMetrics[AVG_NORMAL_CHAR_HEIGHT_IDX] = Math.max(ConstantsMgr.msnMinCharHeightInUnit, dSumNormalCharHeight / dTotalNormalCharWeight);
            }
            darrayMetrics[NORMAL_CHAR_CNT_IDX] = dTotalNormalCharWeight;
            if (dTotalVGapWeight == 0) {
                darrayMetrics[AVG_VGAP_IDX] = darrayMetrics[AVG_CHAR_WIDTH_IDX];
            } else {
                darrayMetrics[AVG_VGAP_IDX] = Math.max(ConstantsMgr.msnMinVGapWidthInUnit, dVGap / dTotalVGapWeight);
            }
            darrayMetrics[VGAP_CNT_IDX] = dTotalVGapWeight;
        }
        return darrayMetrics;
    }

    //开括号 绝对值
    public static boolean isBoundChar(UnitProtoType.Type unitType) {
        if (unitType == UnitProtoType.Type.TYPE_ROUND_BRACKET || unitType == UnitProtoType.Type.TYPE_SQUARE_BRACKET
                || unitType == UnitProtoType.Type.TYPE_BRACE || unitType == UnitProtoType.Type.TYPE_VERTICAL_LINE) {
            return true;
        }
        return false;
    }

    public boolean isBoundChar() {
        if (mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && isBoundChar(mType)) {
            return true;
        }
        return false;
    }

    //闭括号 绝对值
    public static boolean isCloseBoundChar(UnitProtoType.Type unitType) {
        if (unitType == UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET || unitType == UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET
                || unitType == UnitProtoType.Type.TYPE_CLOSE_BRACE || unitType == UnitProtoType.Type.TYPE_VERTICAL_LINE) {
            return true;
        }
        return false;
    }

    public boolean isCloseBoundChar() {
        if (mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && isCloseBoundChar(mType)) {
            return true;
        }
        return false;
    }

    //纯数字
    public static boolean isNumberChar(UnitProtoType.Type unitType) {
        if (unitType == UnitProtoType.Type.TYPE_ONE || unitType == UnitProtoType.Type.TYPE_TWO
                || unitType == UnitProtoType.Type.TYPE_THREE || unitType == UnitProtoType.Type.TYPE_FOUR
                || unitType == UnitProtoType.Type.TYPE_FIVE || unitType == UnitProtoType.Type.TYPE_SIX
                || unitType == UnitProtoType.Type.TYPE_SEVEN || unitType == UnitProtoType.Type.TYPE_EIGHT
                || unitType == UnitProtoType.Type.TYPE_NINE || unitType == UnitProtoType.Type.TYPE_ZERO) {
            return true;
        }
        return false;
    }

    public boolean isNumberChar() {
        if (mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && isNumberChar(mType)) {
            return true;
        }
        return false;
    }

    //可能是竖线的
    public static boolean isPossibleVLnChar(UnitProtoType.Type unitType) {
        if (unitType == UnitProtoType.Type.TYPE_ONE
                || unitType == UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET || unitType == UnitProtoType.Type.TYPE_SQUARE_BRACKET
                || unitType == UnitProtoType.Type.TYPE_SMALL_I_WITHOUT_DOT || unitType == UnitProtoType.Type.TYPE_SMALL_L
                || unitType == UnitProtoType.Type.TYPE_VERTICAL_LINE || unitType == UnitProtoType.Type.TYPE_BIG_I) {    // open round bracket is very unlikely to be 1.
            return true;
        }
        return false;
    }

    public boolean isPossibleVLnChar() {
        if (mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && isPossibleVLnChar(mType)) {
            return true;
        }
        return false;
    }

    //可能是数字的
    public static boolean isPossibleNumberChar(UnitProtoType.Type unitType) {
        if (unitType == UnitProtoType.Type.TYPE_ONE || unitType == UnitProtoType.Type.TYPE_TWO
                || unitType == UnitProtoType.Type.TYPE_THREE || unitType == UnitProtoType.Type.TYPE_FOUR
                || unitType == UnitProtoType.Type.TYPE_FIVE || unitType == UnitProtoType.Type.TYPE_SIX
                || unitType == UnitProtoType.Type.TYPE_SEVEN || unitType == UnitProtoType.Type.TYPE_EIGHT
                || unitType == UnitProtoType.Type.TYPE_NINE || unitType == UnitProtoType.Type.TYPE_ZERO
                || unitType == UnitProtoType.Type.TYPE_SMALL_O || unitType == UnitProtoType.Type.TYPE_BIG_O
                || unitType == UnitProtoType.Type.TYPE_SMALL_I_WITHOUT_DOT || unitType == UnitProtoType.Type.TYPE_SMALL_L
                || unitType == UnitProtoType.Type.TYPE_VERTICAL_LINE || unitType == UnitProtoType.Type.TYPE_SMALL_Z
                || unitType == UnitProtoType.Type.TYPE_BIG_Z || unitType == UnitProtoType.Type.TYPE_BIG_I
                || unitType == UnitProtoType.Type.TYPE_SQUARE_BRACKET || unitType == UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET
                || unitType == UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET||unitType==UnitProtoType.Type.TYPE_BACKWARD_SLASH) {
            // open round bracket is very unlikely to be 1.
            return true;
        }
        return false;
    }

    public boolean isPossibleNumberChar() {
        if (mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && isPossibleNumberChar(mType)) {
            return true;
        }
        return false;
    }

    //数字和小数点
    public static boolean isNumericChar(UnitProtoType.Type unitType) {
        if (unitType == UnitProtoType.Type.TYPE_ONE || unitType == UnitProtoType.Type.TYPE_TWO
                || unitType == UnitProtoType.Type.TYPE_THREE || unitType == UnitProtoType.Type.TYPE_FOUR
                || unitType == UnitProtoType.Type.TYPE_FIVE || unitType == UnitProtoType.Type.TYPE_SIX
                || unitType == UnitProtoType.Type.TYPE_SEVEN || unitType == UnitProtoType.Type.TYPE_EIGHT
                || unitType == UnitProtoType.Type.TYPE_NINE || unitType == UnitProtoType.Type.TYPE_ZERO
                || unitType == UnitProtoType.Type.TYPE_DOT) {
            return true;
        }
        return false;
    }

    public boolean isNumericChar() {
        if (mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && isNumericChar(mType)) {
            return true;
        }
        return false;
    }

    //字母
    public static boolean isLetterChar(UnitProtoType.Type unitType) {
        if (unitType == UnitProtoType.Type.TYPE_SMALL_A || unitType == UnitProtoType.Type.TYPE_SMALL_B
                || unitType == UnitProtoType.Type.TYPE_SMALL_C || unitType == UnitProtoType.Type.TYPE_SMALL_D
                || unitType == UnitProtoType.Type.TYPE_SMALL_E || unitType == UnitProtoType.Type.TYPE_SMALL_F
                || unitType == UnitProtoType.Type.TYPE_SMALL_G || unitType == UnitProtoType.Type.TYPE_SMALL_H
                || unitType == UnitProtoType.Type.TYPE_SMALL_I || unitType == UnitProtoType.Type.TYPE_SMALL_J
                || unitType == UnitProtoType.Type.TYPE_SMALL_I_WITHOUT_DOT || unitType == UnitProtoType.Type.TYPE_SMALL_J_WITHOUT_DOT
                || unitType == UnitProtoType.Type.TYPE_SMALL_K || unitType == UnitProtoType.Type.TYPE_SMALL_L
                || unitType == UnitProtoType.Type.TYPE_SMALL_M || unitType == UnitProtoType.Type.TYPE_SMALL_N
                || unitType == UnitProtoType.Type.TYPE_SMALL_O || unitType == UnitProtoType.Type.TYPE_SMALL_P
                || unitType == UnitProtoType.Type.TYPE_SMALL_Q || unitType == UnitProtoType.Type.TYPE_SMALL_R
                || unitType == UnitProtoType.Type.TYPE_SMALL_S || unitType == UnitProtoType.Type.TYPE_SMALL_T
                || unitType == UnitProtoType.Type.TYPE_SMALL_U || unitType == UnitProtoType.Type.TYPE_SMALL_V
                || unitType == UnitProtoType.Type.TYPE_SMALL_W || unitType == UnitProtoType.Type.TYPE_SMALL_X
                || unitType == UnitProtoType.Type.TYPE_SMALL_Y || unitType == UnitProtoType.Type.TYPE_SMALL_Z
                || unitType == UnitProtoType.Type.TYPE_BIG_A || unitType == UnitProtoType.Type.TYPE_BIG_B
                || unitType == UnitProtoType.Type.TYPE_BIG_C || unitType == UnitProtoType.Type.TYPE_BIG_D
                || unitType == UnitProtoType.Type.TYPE_BIG_E || unitType == UnitProtoType.Type.TYPE_BIG_F
                || unitType == UnitProtoType.Type.TYPE_BIG_G || unitType == UnitProtoType.Type.TYPE_BIG_H
                || unitType == UnitProtoType.Type.TYPE_BIG_I || unitType == UnitProtoType.Type.TYPE_BIG_J
                || unitType == UnitProtoType.Type.TYPE_BIG_K || unitType == UnitProtoType.Type.TYPE_BIG_L
                || unitType == UnitProtoType.Type.TYPE_BIG_M || unitType == UnitProtoType.Type.TYPE_BIG_N
                || unitType == UnitProtoType.Type.TYPE_BIG_O || unitType == UnitProtoType.Type.TYPE_BIG_P
                || unitType == UnitProtoType.Type.TYPE_BIG_Q || unitType == UnitProtoType.Type.TYPE_BIG_R
                || unitType == UnitProtoType.Type.TYPE_BIG_S || unitType == UnitProtoType.Type.TYPE_BIG_T
                || unitType == UnitProtoType.Type.TYPE_BIG_U || unitType == UnitProtoType.Type.TYPE_BIG_V
                || unitType == UnitProtoType.Type.TYPE_BIG_W || unitType == UnitProtoType.Type.TYPE_BIG_X
                || unitType == UnitProtoType.Type.TYPE_BIG_Y || unitType == UnitProtoType.Type.TYPE_BIG_Z
                || unitType == UnitProtoType.Type.TYPE_SMALL_ALPHA || unitType == UnitProtoType.Type.TYPE_SMALL_BETA
                || unitType == UnitProtoType.Type.TYPE_SMALL_GAMMA || unitType == UnitProtoType.Type.TYPE_SMALL_DELTA
                || unitType == UnitProtoType.Type.TYPE_SMALL_EPSILON || unitType == UnitProtoType.Type.TYPE_SMALL_ZETA
                || unitType == UnitProtoType.Type.TYPE_SMALL_ETA || unitType == UnitProtoType.Type.TYPE_SMALL_THETA
                || unitType == UnitProtoType.Type.TYPE_SMALL_LAMBDA || unitType == UnitProtoType.Type.TYPE_SMALL_MU
                || unitType == UnitProtoType.Type.TYPE_SMALL_XI || unitType == UnitProtoType.Type.TYPE_SMALL_PI
                || unitType == UnitProtoType.Type.TYPE_SMALL_RHO || unitType == UnitProtoType.Type.TYPE_SMALL_SIGMA
                || unitType == UnitProtoType.Type.TYPE_SMALL_TAU || unitType == UnitProtoType.Type.TYPE_SMALL_PHI
                || unitType == UnitProtoType.Type.TYPE_SMALL_PSI || unitType == UnitProtoType.Type.TYPE_SMALL_OMEGA
                || unitType == UnitProtoType.Type.TYPE_BIG_DELTA || unitType == UnitProtoType.Type.TYPE_BIG_THETA
                //|| unitType == UnitProtoType.Type.TYPE_BIG_PI || unitType == UnitProtoType.Type.TYPE_BIG_SIGMA  // big PI and big SIGMA have speical meanning in math.
                || unitType == UnitProtoType.Type.TYPE_BIG_PHI || unitType == UnitProtoType.Type.TYPE_BIG_OMEGA
        ) {
            return true;
        }
        return false;
    }

    public boolean isLetterChar() {
        if (mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && isLetterChar(mType)) {
            return true;
        }
        return false;
    }

    //积分号
    public static boolean isIntegTypeChar(UnitProtoType.Type unitType) {
        if (unitType == UnitProtoType.Type.TYPE_INTEGRATE || unitType == UnitProtoType.Type.TYPE_INTEGRATE_CIRCLE) {
            return true;
        }
        return false;
    }

    public boolean isIntegTypeChar() {
        if (mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && isIntegTypeChar(mType)) {
            return true;
        }
        return false;
    }

    //各种根号
    public static boolean isSqrtTypeChar(UnitProtoType.Type unitType) {
        if (unitType == UnitProtoType.Type.TYPE_SQRT_LEFT || unitType == UnitProtoType.Type.TYPE_SQRT_SHORT
                || unitType == UnitProtoType.Type.TYPE_SQRT_MEDIUM || unitType == UnitProtoType.Type.TYPE_SQRT_LONG
                || unitType == UnitProtoType.Type.TYPE_SQRT_TALL || unitType == UnitProtoType.Type.TYPE_SQRT_VERY_TALL) {
            return true;
        }
        return false;
    }

    public boolean isSqrtTypeChar() {
        if (mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && isSqrtTypeChar(mType)) {
            return true;
        }
        return false;
    }

    //求和符号
    public static boolean isSIGMAPITypeChar(UnitProtoType.Type unitType) {
        if (unitType == UnitProtoType.Type.TYPE_BIG_SIGMA || unitType == UnitProtoType.Type.TYPE_BIG_PI) {
            return true;
        }
        return false;
    }

    public boolean isSIGMAPITypeChar() {
        if (mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && isSIGMAPITypeChar(mType)) {
            return true;
        }
        return false;
    }

    //判断是不是运算符
    public static boolean isBiOptChar(UnitProtoType.Type unitType) {
        if (unitType == UnitProtoType.Type.TYPE_ADD || unitType == UnitProtoType.Type.TYPE_SUBTRACT
                || unitType == UnitProtoType.Type.TYPE_PLUS_MINUS || unitType == UnitProtoType.Type.TYPE_MULTIPLY || unitType == UnitProtoType.Type.TYPE_DOT_MULTIPLY
                || unitType == UnitProtoType.Type.TYPE_DIVIDE || unitType == UnitProtoType.Type.TYPE_FORWARD_SLASH
                || unitType == UnitProtoType.Type.TYPE_BACKWARD_SLASH || unitType == UnitProtoType.Type.TYPE_EQUAL
                || unitType == UnitProtoType.Type.TYPE_EQUAL_ALWAYS || unitType == UnitProtoType.Type.TYPE_EQUAL_ROUGHLY
                || unitType == UnitProtoType.Type.TYPE_LARGER || unitType == UnitProtoType.Type.TYPE_SMALLER
                || unitType == UnitProtoType.Type.TYPE_NO_LARGER || unitType == UnitProtoType.Type.TYPE_NO_SMALLER
                || unitType == UnitProtoType.Type.TYPE_DOT || unitType == UnitProtoType.Type.TYPE_STAR
                || unitType == UnitProtoType.Type.TYPE_LEFT_ARROW || unitType == UnitProtoType.Type.TYPE_RIGHT_ARROW
                || unitType == UnitProtoType.Type.TYPE_VERTICAL_LINE
        ) {
            return true;
        }
        return false;
    }

    public boolean isBiOptChar() {
        if (mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && isBiOptChar(mType)) {
            return true;
        }
        return false;
    }

    //字符前修饰符 + - ~
    public static boolean isPreUnOptChar(UnitProtoType.Type unitType) {
        if (unitType == UnitProtoType.Type.TYPE_ADD || unitType == UnitProtoType.Type.TYPE_SUBTRACT
                || unitType == UnitProtoType.Type.TYPE_PLUS_MINUS || unitType == UnitProtoType.Type.TYPE_WAVE
        ) {
            return true;
        }
        return false;
    }

    public boolean isPreUnOptChar() {
        if (mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && isPreUnOptChar(mType)) {
            return true;
        }
        return false;
    }

    //字符后修饰符 %
    public static boolean isPostUnOptChar(UnitProtoType.Type unitType) {
        if (unitType == UnitProtoType.Type.TYPE_PERCENT || unitType == UnitProtoType.Type.TYPE_EXCLAIMATION) {
            return true;
        }
        return false;
    }

    public boolean isPostUnOptChar() {
        if (mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && isPostUnOptChar(mType)) {
            return true;
        }
        return false;
    }

    //比较运算符 > <
    public static boolean isCompareOptChar(UnitProtoType.Type unitType) {
        if (unitType == UnitProtoType.Type.TYPE_EQUAL
                || unitType == UnitProtoType.Type.TYPE_EQUAL_ALWAYS
                || unitType == UnitProtoType.Type.TYPE_EQUAL_ROUGHLY
                || unitType == UnitProtoType.Type.TYPE_LARGER
                || unitType == UnitProtoType.Type.TYPE_SMALLER
                || unitType == UnitProtoType.Type.TYPE_NO_LARGER
                || unitType == UnitProtoType.Type.TYPE_NO_SMALLER) {
            return true;
        }
        return false;
    }

    public boolean isCompareOptChar() {
        if (mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && isCompareOptChar(mType)) {
            return true;
        }
        return false;
    }

    //前钱符 ￥ $
    public static boolean isPreUnitChar(UnitProtoType.Type unitType) {
        if (unitType == UnitProtoType.Type.TYPE_DOLLAR || unitType == UnitProtoType.Type.TYPE_EURO
                || unitType == UnitProtoType.Type.TYPE_YUAN || unitType == UnitProtoType.Type.TYPE_POUND
        ) {
            return true;
        }
        return false;
    }

    public boolean isPreUnitChar() {
        if (mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && isPreUnitChar(mType)) {
            return true;
        }
        return false;
    }

    //摄氏度 华摄度
    public static boolean isPostUnitChar(UnitProtoType.Type unitType) {
        if (unitType == UnitProtoType.Type.TYPE_CELCIUS || unitType == UnitProtoType.Type.TYPE_FAHRENHEIT) {
            return true;
        }
        return false;
    }

    public boolean isPostUnitChar() {
        if (mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && isPostUnitChar(mType)) {
            return true;
        }
        return false;
    }

    //错误识别的数字和字母纠正
    public static void rectifyMisRecogNumLetter(CharLearningMgr clm, StructExprRecog ser) {
        if (ser.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && !ser.isLetterChar() && !ser.isNumberChar()) {
            // this letter might be miss recognized, look for another candidate.
            LinkedList<CharCandidate> listCands = clm.findCharCandidates(ser.mType, ser.mstrFont);
            for (int idx1 = 0; idx1 < listCands.size(); idx1++) {
                if (isLetterChar(listCands.get(idx1).mType) || isNumberChar(listCands.get(idx1).mType)) {
                    // ok, change it to the new char
                    ser.changeSEREnumType(listCands.get(idx1).mType,
                            (listCands.get(idx1).mstrFont.length() == 0) ? ser.mstrFont : listCands.get(idx1).mstrFont);
                    break;
                }
            }
        }
    }

    public static void rectifyMisRecogCUBaseChar(CharLearningMgr clm, StructExprRecog ser) {
        if (ser.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && !ser.isLetterChar() && !ser.isNumberChar() && !ser.isSIGMAPITypeChar()
                && !ser.isIntegTypeChar()) {
            // this letter might be miss recognized, look for another candidate.
            LinkedList<CharCandidate> listCands = clm.findCharCandidates(ser.mType, ser.mstrFont);
            for (int idx1 = 0; idx1 < listCands.size(); idx1++) {
                if (isLetterChar(listCands.get(idx1).mType) || isNumberChar(listCands.get(idx1).mType)
                        || isSIGMAPITypeChar(listCands.get(idx1).mType) || isIntegTypeChar(listCands.get(idx1).mType)) {
                    // ok, change it to the new char
                    ser.changeSEREnumType(listCands.get(idx1).mType,
                            (listCands.get(idx1).mstrFont.length() == 0) ? ser.mstrFont : listCands.get(idx1).mstrFont);
                    break;
                }
            }
        }
    }

    public static void rectifyMisRecogLUNotesBaseChar(CharLearningMgr clm, StructExprRecog ser) {
        if (ser.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && !ser.isLetterChar() && !ser.isNumberChar() && !ser.isSIGMAPITypeChar()
                && !ser.isIntegTypeChar()) {
            // this letter might be miss recognized, look for another candidate.
            LinkedList<CharCandidate> listCands = clm.findCharCandidates(ser.mType, ser.mstrFont);
            for (int idx1 = 0; idx1 < listCands.size(); idx1++) {
                if (isLetterChar(listCands.get(idx1).mType) || isNumberChar(listCands.get(idx1).mType)
                        || isSIGMAPITypeChar(listCands.get(idx1).mType) || isIntegTypeChar(listCands.get(idx1).mType)) {
                    // ok, change it to the new char
                    ser.changeSEREnumType(listCands.get(idx1).mType,
                            (listCands.get(idx1).mstrFont.length() == 0) ? ser.mstrFont : listCands.get(idx1).mstrFont);
                    break;
                }
            }
        }
    }

    public static void rectifyMisRecogCapUnderNotesChar(CharLearningMgr clm, StructExprRecog ser) {
        if (ser.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                && !ser.isLetterChar() && !ser.isNumberChar() && ser.mType != UnitProtoType.Type.TYPE_ADD
                && ser.mType != UnitProtoType.Type.TYPE_SUBTRACT && ser.mType != UnitProtoType.Type.TYPE_WAVE
                && ser.mType != UnitProtoType.Type.TYPE_STAR && ser.mType != UnitProtoType.Type.TYPE_DOT) {  // because it is cap under note, it cannot be dot multiply
            // this letter might be miss recognized, look for another candidate.
            LinkedList<CharCandidate> listCands = clm.findCharCandidates(ser.mType, ser.mstrFont);
            for (int idx1 = 0; idx1 < listCands.size(); idx1++) {
                if (isLetterChar(listCands.get(idx1).mType) || isNumberChar(listCands.get(idx1).mType)
                        || ser.mType == UnitProtoType.Type.TYPE_ADD || ser.mType == UnitProtoType.Type.TYPE_SUBTRACT
                        || ser.mType == UnitProtoType.Type.TYPE_WAVE || ser.mType == UnitProtoType.Type.TYPE_STAR
                        || ser.mType == UnitProtoType.Type.TYPE_DOT) {
                    // ok, change it to the new char
                    ser.changeSEREnumType(listCands.get(idx1).mType,
                            (listCands.get(idx1).mstrFont.length() == 0) ? ser.mstrFont : listCands.get(idx1).mstrFont);
                    break;
                }
            }
        }
    }

    //错误字符一轮修正----主要是纠正数字和字母
    public void rectifyMisRecogChars1stRnd(CharLearningMgr clm) {
        // some characters might be misrecognized, so rectify them
        switch (mnExprRecogType) {
            case EXPRRECOGTYPE_ENUMTYPE: {
                break;  // single char, do nothing.
            }
            case EXPRRECOGTYPE_HLINECUT: {
                StructExprRecog serChildNo = mlistChildren.getFirst();
                StructExprRecog serChildDe = mlistChildren.getLast();
                rectifyMisRecogNumLetter(clm, serChildNo);
                rectifyMisRecogNumLetter(clm, serChildDe);
                break;
            }
            case EXPRRECOGTYPE_HBLANKCUT:
            case EXPRRECOGTYPE_MULTIEXPRS:
            case EXPRRECOGTYPE_VCUTMATRIX: {
                for (int idx = 0; idx < mlistChildren.size(); idx++) {
                    StructExprRecog serThisChild = mlistChildren.get(idx);
//                    System.out.println(idx+"\t"+serThisChild.getExprRecogType()+"\t"+serThisChild.mType+"\t"+serThisChild.toString());
                    rectifyMisRecogNumLetter(clm, serThisChild);
                }
                break;
            }
            case EXPRRECOGTYPE_HCUTCAP:
            case EXPRRECOGTYPE_HCUTUNDER:
            case EXPRRECOGTYPE_HCUTCAPUNDER: {
                StructExprRecog serCap = null, serBase = null, serUnder = null;
                if (mnExprRecogType == EXPRRECOGTYPE_HCUTCAP) {
                    serCap = mlistChildren.getFirst();
                    serBase = mlistChildren.getLast();
                } else if (mnExprRecogType == EXPRRECOGTYPE_HCUTUNDER) {
                    serBase = mlistChildren.getFirst();
                    serUnder = mlistChildren.getLast();
                } else {
                    serCap = mlistChildren.getFirst();
                    serBase = mlistChildren.get(1);
                    serUnder = mlistChildren.getLast();
                }

                if (serCap != null) {
                    rectifyMisRecogCapUnderNotesChar(clm, serCap);
                }
                rectifyMisRecogCUBaseChar(clm, serBase);
                if (serUnder != null) {
                    rectifyMisRecogCapUnderNotesChar(clm, serUnder);
                }
                break;
            }
            //大部分在这里！！！
            case EXPRRECOGTYPE_VBLANKCUT: {
                //对字符序列中的每一个字符：
                for (int idx = 0; idx < mlistChildren.size(); idx++) {
                    StructExprRecog serThisChild = mlistChildren.get(idx);

                    /*If we find a 't' and the second letter after it is n, then we think the letter after 't' should be a*/
                    if (serThisChild.mType == UnitProtoType.Type.TYPE_SMALL_T) {
                        if ((idx + 2) < mlistChildren.size() && mlistChildren.get(idx + 2).mType == UnitProtoType.Type.TYPE_SMALL_N) {
                            mlistChildren.get(idx + 1).mType = UnitProtoType.Type.TYPE_SMALL_A;
                            mlistChildren.get(idx + 1).mstrFont = "";
                            mlistChildren.get(idx + 1).mdSimilarity = 0.0;

                            mlistChildren.get(idx + 1).mnExprRecogType = StructExprRecog.EXPRRECOGTYPE_ENUMTYPE;
                        }
                    }

                    /*change some '/' to '1'*/
                    //todo by LH, maybe this rule will make some trouble.
                    if(serThisChild.mType == UnitProtoType.Type.TYPE_FORWARD_SLASH&&idx >=1){
                        StructExprRecog preSon = mlistChildren.get(idx - 1);
                        if(!isNumberChar(preSon.mType)){
                            serThisChild.changeSEREnumType(UnitProtoType.Type.TYPE_ONE, "");
                        }
                    }

                    StructExprRecog serThisChildPrinciple = serThisChild.getPrincipleSER(4); // get principle from upper or lower notes.
                    //System.out.print(serThisChild.mType+"\t");
                    if (serThisChild.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && serThisChild.mType == UnitProtoType.Type.TYPE_BRACE
                            && idx < (mlistChildren.size() - 1) && mlistChildren.get(idx + 1).mnExprRecogType != EXPRRECOGTYPE_HBLANKCUT
                            && mlistChildren.get(idx + 1).mnExprRecogType != EXPRRECOGTYPE_MULTIEXPRS
                            && mlistChildren.get(idx + 1).mnExprRecogType != EXPRRECOGTYPE_VCUTMATRIX) {
                        // should change { to ( if the following ser is not a mult exprs nor a matrix
                        serThisChild.changeSEREnumType(UnitProtoType.Type.TYPE_ROUND_BRACKET, serThisChild.mstrFont);
                    } else if (serThisChildPrinciple.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && serThisChildPrinciple.mType == UnitProtoType.Type.TYPE_CLOSE_BRACE
                            && idx > 0 && mlistChildren.get(idx - 1).mnExprRecogType != EXPRRECOGTYPE_HBLANKCUT
                            && mlistChildren.get(idx - 1).mnExprRecogType != EXPRRECOGTYPE_MULTIEXPRS
                            && mlistChildren.get(idx - 1).mnExprRecogType != EXPRRECOGTYPE_VCUTMATRIX) {
                        // should change } to ) if the previous ser is not a mult exprs nor a matrix
                        serThisChildPrinciple.changeSEREnumType(UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET, serThisChildPrinciple.mstrFont);
                    } else if (idx < mlistChildren.size() - 1) {
                        StructExprRecog serThisChildPrincipleCapUnderUL = serThisChild.getPrincipleSER(5);
                        if (serThisChildPrincipleCapUnderUL.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                                && serThisChildPrincipleCapUnderUL.mType == UnitProtoType.Type.TYPE_SMALL_F
                                && serThisChildPrincipleCapUnderUL.mstrFont.equalsIgnoreCase("cambria_italian_48_thinned") // only this font of small f can be misrecognized integrate.
                                && serThisChild.mlistChildren.size() == 3) {
                            // implies that there are upper note and lower note. So it should be integrate.
                            serThisChildPrincipleCapUnderUL.changeSEREnumType(UnitProtoType.Type.TYPE_INTEGRATE, serThisChildPrinciple.mstrFont);
                        }
                    }

                    //第一个字符不是数字、不是字母等……那么可能识别错了
                    if (idx == 0) {
                        if (serThisChild.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                                && !serThisChild.isLetterChar() && !serThisChild.isNumericChar() && !serThisChild.isBoundChar()
                                && !serThisChild.isIntegTypeChar() && !serThisChild.isPreUnOptChar() && !serThisChild.isPreUnitChar()
                                && !serThisChild.isSIGMAPITypeChar()) {
                            // this letter might be miss recognized, look for another candidate.
                            LinkedList<CharCandidate> listCands = clm.findCharCandidates(serThisChild.mType, serThisChild.mstrFont);
                            for (int idx1 = 0; idx1 < listCands.size(); idx1++) {
                                if (isLetterChar(listCands.get(idx1).mType) || isNumericChar(listCands.get(idx1).mType)
                                        || isBoundChar(listCands.get(idx1).mType) || isIntegTypeChar(listCands.get(idx1).mType)
                                        || isPreUnOptChar(listCands.get(idx1).mType) || isPreUnitChar(listCands.get(idx1).mType)) {
                                    // ok, change it to the new char
                                    serThisChild.changeSEREnumType(listCands.get(idx1).mType,
                                            (listCands.get(idx1).mstrFont.length() == 0) ? serThisChild.mstrFont : listCands.get(idx1).mstrFont);
                                    break;
                                }
                            }
                        }
                        //todo dml changge 3.2 at 7.18 01:04
                        if (serThisChild.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && (serThisChild.isCloseBoundChar() || serThisChild.isBoundChar())) {
                            if (serThisChild.isCloseBoundChar()) {
                                serThisChild.changeSEREnumType(UnitProtoType.Type.TYPE_ONE, "");
                            } else if (serThisChild.mType == UnitProtoType.Type.TYPE_ROUND_BRACKET) {
                                boolean dmlMatch = false;
                                StructExprRecog serDmlChild = null;
                                for (int i = 1; i < mlistChildren.size() ; ++i) {
                                    serDmlChild = mlistChildren.get(i).getPrincipleSER(4);
                                    if (serDmlChild.mType== UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET) {
                                        dmlMatch = true;
                                        break;
                                    }
                                }
                                if (!dmlMatch) {
                                    serThisChild.changeSEREnumType(UnitProtoType.Type.TYPE_ONE, "");
                                }
                            }
                            else if (serThisChild.mType == UnitProtoType.Type.TYPE_SQUARE_BRACKET) {
                                boolean dmlMatch = false;
                                StructExprRecog serDmlChild = null;
                                for (int i = 1; i < mlistChildren.size() ; ++i) {
                                    serDmlChild = mlistChildren.get(i).getPrincipleSER(4);
                                    if (serDmlChild.mType== UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET) {
                                        dmlMatch = true;
                                        break;
                                    }
                                }
                                if (!dmlMatch) {
                                    serThisChild.changeSEREnumType(UnitProtoType.Type.TYPE_ONE, "");
                                }
                            }
                        }
                    }
                    //最后一个字符
                    else if (idx == mlistChildren.size() - 1) {
                        if (serThisChildPrinciple.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                                && !serThisChildPrinciple.isLetterChar() && !serThisChildPrinciple.isNumberChar() && !serThisChildPrinciple.isCloseBoundChar()
                                && !serThisChildPrinciple.isPostUnOptChar() && !serThisChildPrinciple.isPostUnitChar()) {
                            // this letter might be miss recognized, look for another candidate.
                            LinkedList<CharCandidate> listCands = clm.findCharCandidates(serThisChildPrinciple.mType, serThisChildPrinciple.mstrFont);
                            for (int idx1 = 0; idx1 < listCands.size(); idx1++) {
                                if (isLetterChar(listCands.get(idx1).mType) || isNumberChar(listCands.get(idx1).mType)
                                        || isCloseBoundChar(listCands.get(idx1).mType) || isPostUnOptChar(listCands.get(idx1).mType)
                                        || isPostUnitChar(listCands.get(idx1).mType)) {
                                    // ok, change it to the new char
                                    serThisChildPrinciple.changeSEREnumType(listCands.get(idx1).mType,
                                            (listCands.get(idx1).mstrFont.length() == 0) ? serThisChildPrinciple.mstrFont : listCands.get(idx1).mstrFont);
                                    break;
                                }
                            }
                        }
                        if (serThisChild.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && (serThisChild.isCloseBoundChar() || serThisChild.isBoundChar())) {
                            if (serThisChild.isBoundChar()) {
                                serThisChild.changeSEREnumType(UnitProtoType.Type.TYPE_ONE, "");
                            } else if (serThisChild.mType == UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET) {
                                boolean dmlMatch = false;
                                StructExprRecog serDmlChild = null;
                                for (int i = mlistChildren.size()-1; i >=0 ; --i) {
                                    serDmlChild = mlistChildren.get(i).getPrincipleSER(4);
                                    if (serDmlChild.mType== UnitProtoType.Type.TYPE_ROUND_BRACKET) {
                                        dmlMatch = true;
                                        break;
                                    }
                                }
                                if (!dmlMatch) {
                                    serThisChild.changeSEREnumType(UnitProtoType.Type.TYPE_ONE, "");
                                }
                            }
                            else if (serThisChild.mType == UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET) {
                                boolean dmlMatch = false;
                                StructExprRecog serDmlChild = null;
                                for (int i = mlistChildren.size()-1; i >=0 ; --i) {
                                    serDmlChild = mlistChildren.get(i).getPrincipleSER(4);
                                    if (serDmlChild.mType== UnitProtoType.Type.TYPE_SQUARE_BRACKET) {
                                        dmlMatch = true;
                                        break;
                                    }
                                }
                                if (!dmlMatch) {
                                    serThisChild.changeSEREnumType(UnitProtoType.Type.TYPE_ONE, "");
                                }
                            }
                        }
                    }
                    //剩下的都在这
                    else {
                        //前一个后一个
                        StructExprRecog serPrevChild = mlistChildren.get(idx - 1);
                        StructExprRecog serNextChild = mlistChildren.get(idx + 1);
                        //123都是ENUMTYPE
                        if (serPrevChild.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && serThisChild.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                                && serNextChild.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE) {
                            //这个有待细细研究
                            if (!serPrevChild.isLetterChar() && !serPrevChild.isNumericChar()
                                    && !serPrevChild.isCloseBoundChar() && !serPrevChild.isPostUnitChar()
                                    && !serNextChild.isLetterChar() && !serNextChild.isNumericChar()
                                    && !serNextChild.isBoundChar() && !serNextChild.isPreUnitChar()
                                    && !serNextChild.isIntegTypeChar() && !serNextChild.isSIGMAPITypeChar()
                                    && (!serPrevChild.isPostUnOptChar() || !serNextChild.isPreUnOptChar())
                                    && !serThisChildPrinciple.isLetterChar() && !serThisChildPrinciple.isNumericChar()) {
                                // this letter might be miss recognized, look for another candidate.
                                LinkedList<CharCandidate> listCands = clm.findCharCandidates(serThisChildPrinciple.mType, serThisChildPrinciple.mstrFont);
                                for (int idx1 = 0; idx1 < listCands.size(); idx1++) {
                                    if (isLetterChar(listCands.get(idx1).mType) || isNumericChar(listCands.get(idx1).mType)) {
                                        // ok, change it to the new char
                                        serThisChildPrinciple.changeSEREnumType(listCands.get(idx1).mType,
                                                (listCands.get(idx1).mstrFont.length() == 0) ? serThisChildPrinciple.mstrFont : listCands.get(idx1).mstrFont);
                                        break;
                                    }
                                }
                            }
                            //乘号根据字符大小纠错
                            else if ((serThisChild.mType == UnitProtoType.Type.TYPE_SMALL_X || serThisChild.mType == UnitProtoType.Type.TYPE_BIG_X)
                                    && serPrevChild.isPossibleNumberChar() && serNextChild.getPrincipleSER(4).isPossibleNumberChar()
                                    && (serNextChild.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                                    || serNextChild.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE)
                                    && serPrevChild.getBottomPlus1() - serThisChild.getBottomPlus1() >= 0
                                    && serNextChild.getPrincipleSER(4).getBottomPlus1() - serThisChild.getBottomPlus1() >= 0
                                    && (serThisChild.mnTop - serPrevChild.mnTop) >= serThisChild.mnHeight * ConstantsMgr.msdCrosMultiplyLowerThanNeighbor
                                    && (serThisChild.mnTop - serNextChild.getPrincipleSER(4).mnTop) >= serThisChild.mnHeight * ConstantsMgr.msdCrosMultiplyLowerThanNeighbor) {
                                // cross multiply may be misrecognized as x or X. But corss multiply generally is shorter and lower than its left and right neighbours.
                                serThisChild.changeSEREnumType(UnitProtoType.Type.TYPE_MULTIPLY, serThisChild.mstrFont);
                            }
                            //数字字母数字不可
                            else if (serPrevChild.isNumericChar() && serThisChild.isLetterChar() && serNextChild.isNumericChar()) {
                                // this letter might be miss recognized, look for another candidate. this is for the case like 3S4
                                LinkedList<CharCandidate> listCands = clm.findCharCandidates(serThisChild.mType, serThisChild.mstrFont);
                                for (int idx1 = 0; idx1 < listCands.size(); idx1++) {
                                    if (isNumberChar(listCands.get(idx1).mType)) {
                                        // ok, change it to the new char
                                        serThisChild.changeSEREnumType(listCands.get(idx1).mType,
                                                (listCands.get(idx1).mstrFont.length() == 0) ? serThisChild.mstrFont : listCands.get(idx1).mstrFont);
                                        break;
                                    }
                                }
                            }
                            //todo dml_changed6: 字母数字数字不可
                            else if (serPrevChild.isLetterChar() && serThisChild.isNumericChar() && serNextChild.isNumericChar()) {
                                // this letter might be miss recognized, look for another candidate. this is for the case like 3S4
                                LinkedList<CharCandidate> listCands = clm.findCharCandidates(serPrevChild.mType, serPrevChild.mstrFont);
                                for (int idx1 = 0; idx1 < listCands.size(); idx1++) {
                                    if (isNumberChar(listCands.get(idx1).mType)) {
                                        // ok, change it to the new char
                                        serPrevChild.changeSEREnumType(listCands.get(idx1).mType,
                                                (listCands.get(idx1).mstrFont.length() == 0) ? serPrevChild.mstrFont : listCands.get(idx1).mstrFont);
                                        break;
                                    }
                                }
                            }
                            //todo dml_changed5.2 括号不匹配不可---初级纠错，可进一步完善括号匹配机制
                            //todo 3final_change: getprincipleser(4)
                            else if (serThisChild.isCloseBoundChar() || serThisChild.isBoundChar()) {
                                boolean dmlMatch = false;
                                StructExprRecog serDmlChild = null;
                                //这里是闭括号，去前面找开括号
                                if (serThisChild.isCloseBoundChar()) {
                                    for (int i = 0; i < idx; ++i) {
                                        serDmlChild = mlistChildren.get(i).getPrincipleSER(4);
                                        if (serDmlChild.isBoundChar()) {
                                            dmlMatch = true;
                                            break;
                                        }
                                    }
                                    if (!dmlMatch) {//不纠错了，直接改成1吧，因为别的地反可能也有用到clm进行修改的
                                        serThisChild.changeSEREnumType(UnitProtoType.Type.TYPE_ONE, "");
                                    }
                                }
                                //这里是开括号，去后面找闭括号
                                else {
                                    dmlMatch = false;
                                    for (int i = idx + 1; i < mlistChildren.size(); ++i) {
                                        serDmlChild = mlistChildren.get(i).getPrincipleSER(4);
                                        if (serDmlChild.isCloseBoundChar()) {
                                            dmlMatch = true;
                                            break;
                                        }
                                    }
                                    if (!dmlMatch) {//不纠错了，直接改成1吧，因为别的地反可能也有用到clm进行修改的
                                        serThisChild.changeSEREnumType(UnitProtoType.Type.TYPE_ONE, "");
                                    }
                                }
                            }
                            //括号前不能直接接运算符
                            else if (serPrevChild.isBiOptChar() && !serPrevChild.isPossibleNumberChar() && !serPrevChild.isPostUnOptChar() && serNextChild.isNumericChar()
                                    && !serThisChild.isNumberChar() && !serThisChild.isLetterChar() && !serThisChild.isBoundChar()) {
                                // this is for the case like +]9
                                LinkedList<CharCandidate> listCands = clm.findCharCandidates(serThisChild.mType, serThisChild.mstrFont);
                                for (int idx1 = 0; idx1 < listCands.size(); idx1++) {
                                    if (isNumberChar(listCands.get(idx1).mType) || isLetterChar(listCands.get(idx1).mType)) {
                                        // ok, change it to the new char
                                        serThisChild.changeSEREnumType(listCands.get(idx1).mType,
                                                (listCands.get(idx1).mstrFont.length() == 0) ? serThisChild.mstrFont : listCands.get(idx1).mstrFont);
                                        break;
                                    }
                                }
                            }
                            //等号前的乘号 变成x
                            else if (serThisChild.mType == UnitProtoType.Type.TYPE_MULTIPLY
                                    && ((serPrevChild.isBiOptChar() && !serPrevChild.isPossibleNumberChar() /* && !serPrevChild.isLetterChar()*/)   // | can be misrecognized number 1.
                                    || (serNextChild.isBiOptChar() && !serNextChild.isPossibleNumberChar() /* && !serNextChild.isLetterChar()*/))) {
                                // convert like ...\multiply=... to ...x=....
                                // we specify multiply because this is very special case. No other situation when this child is operator would be like this.
                                // moreover, generally if we see two continous biopts, we don't know which one is misrecognized. But here we know.
                                serThisChild.changeSEREnumType(UnitProtoType.Type.TYPE_SMALL_X, serThisChild.mstrFont);
                            }
                        }

                    }
                }
                break;
            }
            case EXPRRECOGTYPE_VCUTLEFTTOPNOTE:
            case EXPRRECOGTYPE_VCUTUPPERNOTE:
            case EXPRRECOGTYPE_VCUTLOWERNOTE:
            case EXPRRECOGTYPE_VCUTLUNOTES: {
                StructExprRecog serLeftTopNote = null, serUpperNote = null, serLowerNote = null, serBase = null;
                if (mnExprRecogType == EXPRRECOGTYPE_VCUTLEFTTOPNOTE) {
                    serLeftTopNote = mlistChildren.getFirst();
                    serBase = mlistChildren.getLast();
                } else if (mnExprRecogType == EXPRRECOGTYPE_VCUTUPPERNOTE) {
                    serBase = mlistChildren.getFirst();
                    serUpperNote = mlistChildren.getLast();
                } else if (mnExprRecogType == EXPRRECOGTYPE_VCUTLOWERNOTE) {
                    serBase = mlistChildren.getFirst();
                    serLowerNote = mlistChildren.getLast();
                } else {
                    serBase = mlistChildren.getFirst();
                    serLowerNote = mlistChildren.get(1);
                    serUpperNote = mlistChildren.getLast();
                }
                if (mnExprRecogType == EXPRRECOGTYPE_VCUTLOWERNOTE && serBase.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                        && serLowerNote.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && serLowerNote.mType == UnitProtoType.Type.TYPE_DOT
                        && serBase.isLetterChar()) {
                    LinkedList<CharCandidate> listCands = clm.findCharCandidates(serBase.mType, serBase.mstrFont);
                    for (int idx1 = 0; idx1 < listCands.size(); idx1++) {
                        if (isNumberChar(listCands.get(idx1).mType)) {
                            // ok, change it to a number.
                            serBase.changeSEREnumType(listCands.get(idx1).mType,
                                    (listCands.get(idx1).mstrFont.length() == 0) ? serBase.mstrFont : listCands.get(idx1).mstrFont);
                            break;
                        }
                    }
                } else if ((mnExprRecogType == EXPRRECOGTYPE_VCUTLOWERNOTE || mnExprRecogType == EXPRRECOGTYPE_VCUTUPPERNOTE) // if it is upper lower note, then it is an integrate
                        && serBase.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && serBase.isIntegTypeChar()) {   // this seems to be a function.
                    LinkedList<CharCandidate> listCands = clm.findCharCandidates(serBase.mType, serBase.mstrFont);
                    for (int idx1 = 0; idx1 < listCands.size(); idx1++) {
                        if (isLetterChar(listCands.get(idx1).mType)) {
                            // ok, change it to a letter.
                            serBase.changeSEREnumType(listCands.get(idx1).mType,
                                    (listCands.get(idx1).mstrFont.length() == 0) ? serBase.mstrFont : listCands.get(idx1).mstrFont);
                            break;
                        }
                    }
                } else {
                    if (serLeftTopNote != null) {
                        rectifyMisRecogCapUnderNotesChar(clm, serLeftTopNote);
                    }
                    if (serUpperNote != null) {
                        //todo by LH, avoid '/' do not be rectify.
                        if(serUpperNote.mType != UnitProtoType.Type.TYPE_FORWARD_SLASH)
                            rectifyMisRecogCapUnderNotesChar(clm, serUpperNote);
                    }
                    if (serLowerNote != null) {
                        rectifyMisRecogCapUnderNotesChar(clm, serLowerNote);
                    }
                    rectifyMisRecogLUNotesBaseChar(clm, serBase);
                }
                break;
            }
            case EXPRRECOGTYPE_GETROOT: {
                StructExprRecog serRootLevel = mlistChildren.getFirst();
                StructExprRecog serRootedExpr = mlistChildren.getLast();
                if (serRootLevel.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && !serRootLevel.isLetterChar() && !serRootLevel.isNumberChar()
                        && !serRootLevel.isSqrtTypeChar()) {
                    // this letter might be miss recognized, look for another candidate.
                    LinkedList<CharCandidate> listCands = clm.findCharCandidates(serRootLevel.mType, serRootLevel.mstrFont);
                    for (int idx1 = 0; idx1 < listCands.size(); idx1++) {
                        if (isLetterChar(listCands.get(idx1).mType) || isNumberChar(listCands.get(idx1).mType)) {
                            // ok, change it to the new char
                            serRootLevel.changeSEREnumType(listCands.get(idx1).mType,
                                    (listCands.get(idx1).mstrFont.length() == 0) ? serRootLevel.mstrFont : listCands.get(idx1).mstrFont);
                            break;
                        }
                    }
                }
                rectifyMisRecogNumLetter(clm, serRootedExpr);
                break;
            }
            default: {
                // EXPRRECOGTYPE_LISTCUT do nothing.
            }
        }

        // rectify its children.
        if (mnExprRecogType != EXPRRECOGTYPE_ENUMTYPE) {
            for (int idx = 0; idx < mlistChildren.size(); idx++) {
                mlistChildren.get(idx).rectifyMisRecogChars1stRnd(clm);
            }
        }
    }

    //错误字符二轮修正----rectify miss-recognized chars in the begnning or end, process the brackets and braces.
    public void rectifyMisRecogChars2ndRnd() {
        if (mnExprRecogType == EXPRRECOGTYPE_VBLANKCUT) {
            LinkedList<StructExprRecog> listBoundingChars = new LinkedList<StructExprRecog>();
            LinkedList<Integer> listBoundingCharIndices = new LinkedList<Integer>();
            LinkedList<StructExprRecog> listVLnChars = new LinkedList<StructExprRecog>();
            LinkedList<Integer> listVLnCharIndices = new LinkedList<Integer>();
            for (int idx = 0; idx < mlistChildren.size(); idx++) {
                StructExprRecog serThisChild = mlistChildren.get(idx).getPrincipleSER(4);
                //todo by LH  change some D to 0
                if(idx>=1) {
                    StructExprRecog curSer = mlistChildren.get(idx);
                    StructExprRecog preSer = mlistChildren.get(idx-1);
                    if (curSer.mType == UnitProtoType.Type.TYPE_BIG_D && (isNumberChar(preSer.mType))) {
                        curSer.changeSEREnumType(UnitProtoType.Type.TYPE_ZERO, "");
                    }
                    //todo YX 模仿将后面有数字的x改成乘号
                    if (preSer.mType == UnitProtoType.Type.TYPE_SMALL_X && (isNumberChar(curSer.mType))) {
                        preSer.changeSEREnumType(UnitProtoType.Type.TYPE_MULTIPLY, "");
                    }
                    //todo YX 模仿将)前的乘号改成x
                    if (preSer.mType == UnitProtoType.Type.TYPE_MULTIPLY && curSer.mType ==UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET) {
                        preSer.changeSEREnumType(UnitProtoType.Type.TYPE_SMALL_X, "");
                    }
                    //todo YX 模仿将（后的乘号改成x
                    if (preSer.mType == UnitProtoType.Type.TYPE_ROUND_BRACKET && curSer.mType ==UnitProtoType.Type.TYPE_MULTIPLY) {
                        curSer.changeSEREnumType(UnitProtoType.Type.TYPE_SMALL_X, "");
                    }

                }
                // now deal with the brackets, square brackets and braces.
                if (serThisChild.isBoundChar()) {
                    if (serThisChild.mType == UnitProtoType.Type.TYPE_VERTICAL_LINE) {
                        if (idx > 0 && idx < mlistChildren.size() - 1
                                && (mlistChildren.get(idx - 1).isNumericChar() || mlistChildren.get(idx - 1).isLetterChar())    // dot is allowed here because it must be decimal point not times (times has been converted to *)
                                && (mlistChildren.get(idx + 1).isNumericChar() || mlistChildren.get(idx + 1).isLetterChar() // dot is allowed here.
                                || mlistChildren.get(idx + 1).mnExprRecogType == EXPRRECOGTYPE_VCUTUPPERNOTE
                                || mlistChildren.get(idx + 1).mnExprRecogType == EXPRRECOGTYPE_VCUTLOWERNOTE
                                || mlistChildren.get(idx + 1).mnExprRecogType == EXPRRECOGTYPE_VCUTLUNOTES)) {
                            // this must be a 1 if the left and right are both letter or numeric char
                            serThisChild.mType = UnitProtoType.Type.TYPE_ONE;
                        } else if (idx > 0 && !mlistChildren.get(idx - 1).isChildListType() && !mlistChildren.get(idx - 1).isNumberChar() && !mlistChildren.get(idx - 1).isLetterChar()
                                && !mlistChildren.get(idx - 1).isPostUnOptChar() && !mlistChildren.get(idx - 1).isBiOptChar() && !mlistChildren.get(idx - 1).isCloseBoundChar()) {
                            serThisChild.mType = UnitProtoType.Type.TYPE_ONE;   // left char is not left char of v-line
                        } else if (idx < mlistChildren.size() - 1 && !mlistChildren.get(idx + 1).isChildListType() && !mlistChildren.get(idx + 1).isNumberChar()
                                && !mlistChildren.get(idx + 1).isLetterChar() && !mlistChildren.get(idx + 1).isPreUnOptChar() && !mlistChildren.get(idx + 1).isBiOptChar()
                                && !mlistChildren.get(idx + 1).isBoundChar()) {
                            serThisChild.mType = UnitProtoType.Type.TYPE_ONE;   // left char is not left char of v-line
                        } else {
                            listVLnChars.add(serThisChild);
                            listVLnCharIndices.add(idx);
                        }
                    } else if (serThisChild.mType != UnitProtoType.Type.TYPE_BRACE
                            || (serThisChild.mType == UnitProtoType.Type.TYPE_BRACE && idx == mlistChildren.size() - 1)
                            || (serThisChild.mType == UnitProtoType.Type.TYPE_BRACE && idx < mlistChildren.size() - 1
                            && mlistChildren.getLast().mnExprRecogType != EXPRRECOGTYPE_MULTIEXPRS)) {
                        listBoundingChars.add(serThisChild);
                        listBoundingCharIndices.add(idx);
                    }
                } else if (serThisChild.isCloseBoundChar()) {
                    if (serThisChild.mType != UnitProtoType.Type.TYPE_VERTICAL_LINE) {
                        boolean bFoundOpenBounding = false;
                        for (int idx1 = listBoundingChars.size() - 1; idx1 >= 0; idx1--) {
                            if ((serThisChild.getBottomPlus1() - listBoundingChars.get(idx1).mnTop) > ConstantsMgr.msdOpenCloseBracketHeightRatio * serThisChild.mnHeight
                                    && (listBoundingChars.get(idx1).getBottomPlus1() - serThisChild.mnTop) > ConstantsMgr.msdOpenCloseBracketHeightRatio * serThisChild.mnHeight
                                    && serThisChild.mnHeight > ConstantsMgr.msdOpenCloseBracketHeightRatio * listBoundingChars.get(idx1).mnHeight  // must have similar height as the start character
                                    && serThisChild.mnHeight < 1 / ConstantsMgr.msdOpenCloseBracketHeightRatio * listBoundingChars.get(idx1).mnHeight) {
                                for (int idx2 = listBoundingChars.size() - 1; idx2 > idx1; idx2--) {
                                    // allow to change all the ( or [ between () or [] pairs coz here ( and [ must not have pair and must be misrecognized.
                                    //StructExprRecog serB4BndChar = listBoundingCharIndices.get(idx2) > 0?mlistChildren.get(listBoundingCharIndices.get(idx2) - 1):null;
                                    //StructExprRecog serAfterBndChar = listBoundingCharIndices.get(idx2) < mlistChildren.size() - 1?mlistChildren.get(listBoundingCharIndices.get(idx2) + 1):null;
                                    if (listBoundingChars.get(idx2).mType == UnitProtoType.Type.TYPE_SQUARE_BRACKET
                                            && (double) listBoundingChars.get(idx2).mnWidth / (double) listBoundingChars.get(idx2).mnHeight <= ConstantsMgr.msdSquareBracketTo1WOverHThresh) {
                                        listBoundingChars.get(idx2).mType = UnitProtoType.Type.TYPE_ONE;    // change to 1, do not use b4 and after char to adjust because not accurate.
                                    } else if (listBoundingChars.get(idx2).mType == UnitProtoType.Type.TYPE_ROUND_BRACKET
                                            && (double) listBoundingChars.get(idx2).mnWidth / (double) listBoundingChars.get(idx2).mnHeight <= ConstantsMgr.msdRoundBracketTo1WOverHThresh) {
                                        listBoundingChars.get(idx2).mType = UnitProtoType.Type.TYPE_ONE;    // change to 1, do not use b4 and after char to adjust because not accurate.
                                    } else {
                                        listBoundingChars.get(idx2).mType = UnitProtoType.Type.TYPE_SMALL_T;    // all the no-close-bounding chars are changed to small t.
                                    }
                                    listBoundingChars.removeLast();
                                    listBoundingCharIndices.removeLast();
                                }
                                listBoundingChars.get(idx1).mType = UnitProtoType.Type.TYPE_ROUND_BRACKET;
                                serThisChild.mType = UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET;
                                listBoundingChars.remove(idx1);
                                listBoundingCharIndices.remove(idx1);
                                bFoundOpenBounding = true;
                                break;
                            }
                        }
                        if (!bFoundOpenBounding) {
                            // cannot find open bounding character, change the close bounding character to 1.
                            //StructExprRecog serB4BndChar = idx > 0?mlistChildren.get(idx - 1):null;
                            //StructExprRecog serAfterBndChar = idx < mlistChildren.size() - 1?mlistChildren.get(idx + 1):null;
                            if (serThisChild.mType == UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET
                                    && (double) serThisChild.mnWidth / (double) serThisChild.mnHeight <= ConstantsMgr.msdSquareBracketTo1WOverHThresh) {
                                serThisChild.mType = UnitProtoType.Type.TYPE_ONE;    // change to 1. Do not use b4 after char to adjust because not accurate (considering - or [1/2]...)
                            } else if (serThisChild.mType == UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET
                                    && (double) serThisChild.mnWidth / (double) serThisChild.mnHeight <= ConstantsMgr.msdRoundBracketTo1WOverHThresh) {
                                serThisChild.mType = UnitProtoType.Type.TYPE_ONE;
                            }
                        }
                    }
                } else if (serThisChild.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                        && (serThisChild.mType == UnitProtoType.Type.TYPE_SMALL_O || serThisChild.mType == UnitProtoType.Type.TYPE_BIG_O)) {
                    // now deal with all o or Os. do not put this in the first round because the condition to change o or O to 0 is more relax.
                    if (mlistChildren.get(idx).mnExprRecogType == EXPRRECOGTYPE_VCUTUPPERNOTE
                            || mlistChildren.get(idx).mnExprRecogType == EXPRRECOGTYPE_VCUTLOWERNOTE
                            || mlistChildren.get(idx).mnExprRecogType == EXPRRECOGTYPE_VCUTLUNOTES) {
                        // if it has upper or lower note.
                        serThisChild.mType = UnitProtoType.Type.TYPE_ZERO;
                    } else if (idx > 0 && (!mlistChildren.get(idx - 1).isLetterChar() || (mlistChildren.get(idx - 1).mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                            && (mlistChildren.get(idx - 1).mType == UnitProtoType.Type.TYPE_SMALL_O || mlistChildren.get(idx - 1).mType == UnitProtoType.Type.TYPE_BIG_O)))) {
                        // if left character is not a letter char or is o or O
                        serThisChild.mType = UnitProtoType.Type.TYPE_ZERO;
                    } else if (idx < (mlistChildren.size() - 1) && (!mlistChildren.get(idx + 1).isLetterChar() || (mlistChildren.get(idx + 1).mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                            && (mlistChildren.get(idx + 1).mType == UnitProtoType.Type.TYPE_SMALL_O || mlistChildren.get(idx + 1).mType == UnitProtoType.Type.TYPE_BIG_O)))) {
                        // if right character is not a letter char or is o or O
                        serThisChild.mType = UnitProtoType.Type.TYPE_ZERO;
                    }
                }
            }

            if (listVLnChars.size() == 1) {
                listVLnChars.getFirst().mType = UnitProtoType.Type.TYPE_ONE;    // all the no-paired vline chars are changed to 1.
            }
            else {
                while (listVLnChars.size() > 0) {
                    int nIdx1st = listVLnCharIndices.getFirst();
                    if (mlistChildren.get(nIdx1st).mnExprRecogType == EXPRRECOGTYPE_VCUTUPPERNOTE
                            || mlistChildren.get(nIdx1st).mnExprRecogType == EXPRRECOGTYPE_VCUTLOWERNOTE
                            || mlistChildren.get(nIdx1st).mnExprRecogType == EXPRRECOGTYPE_VCUTLUNOTES) {
                        listVLnChars.getFirst().mType = UnitProtoType.Type.TYPE_ONE;    // 1st | cannot have upper note or low note .
                        listVLnCharIndices.removeFirst();
                        listVLnChars.removeFirst();
                    } else {
                        break;
                    }
                }
                for (int idx = listVLnChars.size() - 1; idx >= 0; idx--) {
                    StructExprRecog serOneChild = listVLnChars.get(idx);
                    int idx1 = listVLnChars.size() - 1;
                    for (; idx1 >= 0; idx1--) {
                        if (idx1 == idx) {
                            continue;
                        }
                        StructExprRecog serTheOtherChild = listVLnChars.get(idx1);
                        if ((serOneChild.getBottomPlus1() - serTheOtherChild.mnTop) > ConstantsMgr.msdOpenCloseBracketHeightRatio * serOneChild.mnHeight
                                && (serTheOtherChild.getBottomPlus1() - serOneChild.mnTop) > ConstantsMgr.msdOpenCloseBracketHeightRatio * serOneChild.mnHeight
                                && serOneChild.mnHeight > ConstantsMgr.msdOpenCloseBracketHeightRatio * serTheOtherChild.mnHeight  // must have similar height as the start character
                                && serOneChild.mnHeight < 1 / ConstantsMgr.msdOpenCloseBracketHeightRatio * serTheOtherChild.mnHeight) {
                            // has corresponding v-line.
                            break;
                        }
                    }
                    if (idx1 == -1) {
                        // doesn't have corresponding v-line..
                        serOneChild.mType = UnitProtoType.Type.TYPE_ONE;    // all the no-paired vline chars are changed to 1.
                    }
                }
                // recheck the new first VLnChars.

                for (int idx = 0; idx < listVLnChars.size(); idx++) {
                    int nIdxInList = listVLnCharIndices.get(idx);
                    if (listVLnChars.get(idx).mType == UnitProtoType.Type.TYPE_ONE) {
                        continue;
                    } else if (mlistChildren.get(nIdxInList).mnExprRecogType == EXPRRECOGTYPE_VCUTUPPERNOTE
                            || mlistChildren.get(nIdxInList).mnExprRecogType == EXPRRECOGTYPE_VCUTLOWERNOTE
                            || mlistChildren.get(nIdxInList).mnExprRecogType == EXPRRECOGTYPE_VCUTLUNOTES) {
                        listVLnChars.get(idx).mType = UnitProtoType.Type.TYPE_ONE;    // 1st | cannot have upper note or low note .
                    } else {
                        break;
                    }
                }
            }

            if (listBoundingChars.size() > 0 && listBoundingChars.getLast() == mlistChildren.getLast()) {
                // change the last unpaired ( or [ to t or 1 if necessary.
                //StructExprRecog serB4BndChar = mlistChildren.size() > 1?mlistChildren.get(mlistChildren.size() - 2):null;
                if (listBoundingChars.getLast().mType == UnitProtoType.Type.TYPE_SQUARE_BRACKET
                        && (double) listBoundingChars.getLast().mnWidth / (double) listBoundingChars.getLast().mnHeight <= ConstantsMgr.msdSquareBracketTo1WOverHThresh) {
                    listBoundingChars.getLast().mType = UnitProtoType.Type.TYPE_ONE;    // change to 1. Do not use b4 after chars to adjust [ coz not accurate.
                } else if (listBoundingChars.getLast().mType == UnitProtoType.Type.TYPE_ROUND_BRACKET
                        && (double) listBoundingChars.getLast().mnWidth / (double) listBoundingChars.getLast().mnHeight <= ConstantsMgr.msdRoundBracketTo1WOverHThresh) {
                    listBoundingChars.getLast().mType = UnitProtoType.Type.TYPE_ONE;    // change to 1. Do not use b4 after chars to adjust [ coz not accurate.
                } else {
                    //todo dml? [->t  : t-> 1
                    listBoundingChars.getLast().mType = UnitProtoType.Type.TYPE_ONE; // if the last unpaired ( or [ is the last char, very likely it is a t.
                }
            }
            for (int idx = 0; idx < listBoundingChars.size(); idx++) {
                //StructExprRecog serB4BndChar = listBoundingCharIndices.get(idx) > 0?mlistChildren.get(listBoundingCharIndices.get(idx) - 1):null;
                //StructExprRecog serAfterBndChar = listBoundingCharIndices.get(idx) < mlistChildren.size() - 1?mlistChildren.get(listBoundingCharIndices.get(idx) + 1):null;
                if (listBoundingChars.get(idx).mType == UnitProtoType.Type.TYPE_SQUARE_BRACKET
                        && (double) listBoundingChars.get(idx).mnWidth / (double) listBoundingChars.get(idx).mnHeight <= ConstantsMgr.msdSquareBracketTo1WOverHThresh) {
                    listBoundingChars.get(idx).mType = UnitProtoType.Type.TYPE_ONE;    // change to 1. do not use pre or next char height to adjust coz not accurate.
                } else if (listBoundingChars.get(idx).mType == UnitProtoType.Type.TYPE_ROUND_BRACKET
                        && (double) listBoundingChars.get(idx).mnWidth / (double) listBoundingChars.get(idx).mnHeight <= ConstantsMgr.msdRoundBracketTo1WOverHThresh) {
                    listBoundingChars.get(idx).mType = UnitProtoType.Type.TYPE_ONE;    // change to 1. do not use pre or next char height to adjust coz not accurate.
                }
                // do not change other unmatched ( or [ to t or 1 because this makes things worse.
            }
            for (int idx = 0; idx < mlistChildren.size(); idx++) {
                StructExprRecog serThisChild = mlistChildren.get(idx);
                serThisChild.rectifyMisRecogChars2ndRnd();
            }
        }
        else if (isChildListType()) {
            for (int idx = 0; idx < mlistChildren.size(); idx++) {
                StructExprRecog serThisChild = mlistChildren.get(idx);
                if (serThisChild.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE) {
                    if (serThisChild.isBoundChar()) {
                        if (serThisChild.mType == UnitProtoType.Type.TYPE_VERTICAL_LINE
                                && (idx != 0 || mnExprRecogType != EXPRRECOGTYPE_VCUTUPPERNOTE)) {
                            serThisChild.mType = UnitProtoType.Type.TYPE_ONE;   // if it is |**(something), it could still be valid for |...|**(something). But shouldn't have foot notes.
                        } else if (serThisChild.mType == UnitProtoType.Type.TYPE_SQUARE_BRACKET
                                && serThisChild.mnWidth / serThisChild.mnHeight <= ConstantsMgr.msdSquareBracketTo1WOverHThresh) {
                            // if it is [ and it is very thing, very likely it is a 1.
                            serThisChild.mType = UnitProtoType.Type.TYPE_ONE;
                        } else if (serThisChild.mType == UnitProtoType.Type.TYPE_ROUND_BRACKET
                                && serThisChild.mnWidth / serThisChild.mnHeight <= ConstantsMgr.msdRoundBracketTo1WOverHThresh) {
                            // if it is ( and it is very thing, very likely it is a 1.
                            serThisChild.mType = UnitProtoType.Type.TYPE_ONE;
                        } else if (serThisChild.mType != UnitProtoType.Type.TYPE_VERTICAL_LINE) {   // if child is single char, very likely it is not a ( or [ but a t.
                            serThisChild.mType = UnitProtoType.Type.TYPE_SMALL_T;
                        }
                    }
                    else if (serThisChild.isCloseBoundChar() && (idx != 0
                            || (mnExprRecogType != EXPRRECOGTYPE_VCUTUPPERNOTE
                            && mnExprRecogType != EXPRRECOGTYPE_VCUTLOWERNOTE
                            && mnExprRecogType != EXPRRECOGTYPE_VCUTLUNOTES))) {
                        // a close bound char can have upper and/or lower notes. but if the upper note or lower note is close bound char, still need to convert it to 1.
                        // here still convert to 1 because upper lower note can be mis-recognized.
                        serThisChild.mType = UnitProtoType.Type.TYPE_ONE;
                    }
                    else if (serThisChild.mType == UnitProtoType.Type.TYPE_SMALL_O || serThisChild.mType == UnitProtoType.Type.TYPE_BIG_O) {
                        // still convert to 0 even if it has lower or upper notes.
                        serThisChild.mType = UnitProtoType.Type.TYPE_ZERO;
                    }
                }
                else {
                    serThisChild.rectifyMisRecogChars2ndRnd();
                }
            }
        }
    }


    public void rectifyMisRecogWords(MisrecogWordMgr mwm) throws InterruptedException {
        if (mnExprRecogType == EXPRRECOGTYPE_VBLANKCUT) {
            int idx = 0;
            while (idx < mlistChildren.size()) {
                boolean bFindWord = false;
                for (int idx1 = 0; idx1 < mwm.mslistMisrecogWordSet.size(); idx1++) {
                    // first try to find character based pattern
                    LinkedList<StructExprRecog> listSerReplaced = new LinkedList<StructExprRecog>();
                    int nIdxNext = getSimilarCharPatternEndP1(mwm.mslistMisrecogWordSet.get(idx1), this, idx, listSerReplaced);
                    if (nIdxNext > idx) {
                        for (int idx2 = nIdxNext - 1; idx2 >= idx; idx2--) {
                            mlistChildren.remove(idx2);
                        }
                        mlistChildren.addAll(idx, listSerReplaced);
                        idx += listSerReplaced.size();
                        bFindWord = true;
                        break;
                    } else {
                        // then try to find word based pattern.
                        StructExprRecog serPatterned = new StructExprRecog(mbarrayBiValues);
                        nIdxNext = getSimilarWordPatternEndP1(mwm.mslistMisrecogWordSet.get(idx1), this, idx, serPatterned);
                        if (nIdxNext > idx) {
                            for (int idx2 = nIdxNext - 1; idx2 >= idx; idx2--) {
                                mlistChildren.remove(idx2);
                            }
                            mlistChildren.add(idx, serPatterned);
                            idx++;
                            bFindWord = true;
                            break;
                        }
                    }
                }
                if (!bFindWord) {
                    if (mlistChildren.get(idx).isChildListType()) {
                        mlistChildren.get(idx).rectifyMisRecogWords(mwm);
                    }
                    idx++;
                }
            }
        } else if (isChildListType()) {
            for (int idx = 0; idx < mlistChildren.size(); idx++) {
                mlistChildren.get(idx).rectifyMisRecogWords(mwm);
            }
        }
    }

    public static int getSimilarWordPatternEndP1(MisrecogWord mwPattern, StructExprRecog ser,
                                                 int nStart, StructExprRecog serReplaced) throws InterruptedException {
        if (ser.mnExprRecogType != EXPRRECOGTYPE_VBLANKCUT) {
            // if it is not a VBlankcut, the whole ser should match the pattern.
            if (nStart != 0) {
                return nStart;  // should always start from 0 if not a VBlankCut
            } else {
                String strSERString = ser.toString();
                if (mwPattern.getWordSimilarityEndP1(strSERString, nStart) == strSERString.length()) {
                    // ok, get it. but we still need to recognize
                    ImageChop imgChop = ser.getImageChop(true);
                    // calculate similarity here:
                    LinkedList<UnitCandidate> listUCs = UnitRecognizer.recogCharWord(imgChop, mwPattern.mType);
                    if (listUCs.size() == 0 || listUCs.getFirst().mdOverallSimilarity == UnitCandidate.WORST_SIMILARITY_VALUE) {
                        // this does nto match the pattern.
                        return nStart;
                    }
                    StructExprRecog serNew = new StructExprRecog(ser.mbarrayBiValues);
                    serNew.setStructExprRecog(listUCs.getFirst().mprotoType.mnUnitType, listUCs.getFirst().mprotoType.mstrFont,
                            imgChop.getLeftInOriginalImg(), imgChop.getTopInOriginalImg(),
                            imgChop.mnWidth, imgChop.mnHeight, imgChop, listUCs.getFirst().mdOverallSimilarity);
                    StructExprRecog serSelected = ExprRecognizer.selectSERFromCands(ser, serNew);
                    if (serSelected == serNew) {
                        serReplaced.copy(serSelected);  // ok, find similar pattern.
                        return nStart + 1;
                    }
                }
                return nStart;
            }
        } else {
            for (int idx1 = 0; idx1 < mwPattern.mlistWordCands.size(); idx1++) {
                String strSERString = "";
                LinkedList<StructExprRecog> listWordChildren = new LinkedList<StructExprRecog>();
                for (int idx = nStart; idx < ser.mlistChildren.size(); idx++) {
                    strSERString += ser.mlistChildren.get(idx).toString();
                    listWordChildren.add(ser.mlistChildren.get(idx));
                    if (strSERString.length() > mwPattern.mlistWordCands.get(idx1).mstrCandidate.length()) {
                        break;   // this string cannot match because it is too long.
                    } else if (strSERString.equals(mwPattern.mlistWordCands.get(idx1).mstrCandidate)) {
                        // ok find it.
                        StructExprRecog serMatched = new StructExprRecog(ser.getBiArray());
                        if (listWordChildren.size() == 1) {
                            serMatched = listWordChildren.getFirst();
                        } else {
                            serMatched.setStructExprRecog(listWordChildren, StructExprRecog.EXPRRECOGTYPE_VBLANKCUT);
                        }
                        ImageChop imgChop = serMatched.getImageChop(true);
                        // calculate similarity here:
                        LinkedList<UnitCandidate> listUCs = UnitRecognizer.recogCharWord(imgChop, mwPattern.mType);
                        if (listUCs.size() == 0 || listUCs.getFirst().mdOverallSimilarity == UnitCandidate.WORST_SIMILARITY_VALUE) {
                            // this does nto match the pattern.
                            return nStart;
                        }
                        StructExprRecog serNew = new StructExprRecog(ser.mbarrayBiValues);
                        serNew.setStructExprRecog(listUCs.getFirst().mprotoType.mnUnitType, listUCs.getFirst().mprotoType.mstrFont,
                                imgChop.getLeftInOriginalImg(), imgChop.getTopInOriginalImg(),
                                imgChop.mnWidth, imgChop.mnHeight, imgChop, listUCs.getFirst().mdOverallSimilarity);
                        StructExprRecog serSelected = ExprRecognizer.selectSERFromCands(serMatched, serNew);
                        if (serSelected == serNew) {
                            serReplaced.copy(serSelected);  // ok, find similar pattern.
                            return idx + 1;
                        } else {
                            return nStart;  // this is not the right pattern or the string match the candidate.
                        }
                    }
                }
            }
            // unfortunately we cannot find
            return nStart;
        }
    }

    public static int getSimilarCharPatternEndP1(MisrecogWord mwPattern, StructExprRecog ser, int nStart, LinkedList<
            StructExprRecog> listSerReplaced) {
        if (ser.mnExprRecogType != EXPRRECOGTYPE_VBLANKCUT) {
            return 0;   // we can only find similar word pattern from vblankcut.
        }
        int nFoundSimilarChars = 0;
        int nMisRecogChars = 0;
        int idxSer = nStart;
        int nWordLeft = Integer.MAX_VALUE, nWordRightP1 = Integer.MIN_VALUE, nWordTop = Integer.MAX_VALUE, nWordBottomP1 = Integer.MIN_VALUE;
        for (int idx = 0; idx < mwPattern.mstrShouldBe.length(); idx++) {
            LetterCandidates lcs = null;
            // now try to find defined letter candidates list.
            for (LetterCandidates lcsItr : mwPattern.mlistLetterCandSets) {
                if (lcsItr.mnIndex == idx) {
                    lcs = lcsItr;
                    break;
                }
            }
            if (lcs == null) {
                // letter candidates are not defined for this char
                break;
            }
            int idx1 = 0;
            for (; idx1 < lcs.mlistLetterCands.size(); idx1++) {
                String strCand = lcs.mlistLetterCands.get(idx1).mstrCandidate;
                String strSERChildren = "";
                int nLeft = Integer.MAX_VALUE, nRightP1 = Integer.MIN_VALUE, nTop = Integer.MAX_VALUE, nBottomP1 = Integer.MIN_VALUE;
                double dSimilarity = 0;
                int nArea = 0;
                LinkedList<ImageChop> listAllChildrenAsCand = new LinkedList<ImageChop>();
                int idx2 = idxSer;
                for (; idx2 < ser.mlistChildren.size(); idx2++) {
                    /* should still work even if it is not EXPRRECOGTYPE_ENUMTYPE
                    if (ser.mlistChildren.get(idx2).mnExprRecogType != EXPRRECOGTYPE_ENUMTYPE)  {
                        strSERChildren = null;
                        break;
                    }*/
                    strSERChildren += ser.mlistChildren.get(idx2).toString();
                    if (nLeft > ser.mlistChildren.get(idx2).mnLeft) {
                        nLeft = ser.mlistChildren.get(idx2).mnLeft;
                    }
                    if (nRightP1 < ser.mlistChildren.get(idx2).getRightPlus1()) {
                        nRightP1 = ser.mlistChildren.get(idx2).getRightPlus1();
                    }
                    if (nTop > ser.mlistChildren.get(idx2).mnTop) {
                        nTop = ser.mlistChildren.get(idx2).mnTop;
                    }
                    if (nBottomP1 < ser.mlistChildren.get(idx2).getBottomPlus1()) {
                        nBottomP1 = ser.mlistChildren.get(idx2).getBottomPlus1();
                    }
                    if (nWordLeft > nLeft) {
                        nWordLeft = nLeft;
                    }
                    if (nWordRightP1 < nRightP1) {
                        nWordRightP1 = nRightP1;
                    }
                    if (nWordTop > nTop) {
                        nWordTop = nTop;
                    }
                    if (nWordBottomP1 < nBottomP1) {
                        nWordBottomP1 = nBottomP1;
                    }
                    listAllChildrenAsCand.add(ser.mlistChildren.get(idx2).getImageChop(true));
                    dSimilarity += ser.mlistChildren.get(idx2).mdSimilarity * ser.mlistChildren.get(idx2).getArea();
                    nArea += ser.mlistChildren.get(idx2).getArea();
                    if (strSERChildren.length() >= strCand.length()) {
                        break;
                    }
                }
                dSimilarity /= nArea;
                /* should still work even if it is not EXPRRECOGTYPE_ENUMTYPE
                if (strSERChildren == null) {
                    continue;
                }*/
                if (strSERChildren.equals(strCand)) {
                    nFoundSimilarChars++;
                    if (lcs.mlistLetterCands.get(idx1).mbMisrecog) {
                        nMisRecogChars++;
                    }
                    idxSer = idx2 + 1;

                    StructExprRecog serReplacedChild = new StructExprRecog(ser.mbarrayBiValues);
                    ImageChop imgChopChildrenMerged = new ImageChop();
                    if (listAllChildrenAsCand.size() == 1) {    // listAllChildrenAsCand size actually should be > 1.
                        imgChopChildrenMerged = listAllChildrenAsCand.getFirst();
                    } else {
                        imgChopChildrenMerged = ExprSeperator.mergeImgChopsWithSameOriginal(listAllChildrenAsCand);
                    }
                    serReplacedChild.setStructExprRecog(lcs.mType, UNKNOWN_FONT_TYPE, nLeft, nTop, nRightP1 - nLeft, nBottomP1 - nTop, imgChopChildrenMerged, dSimilarity);
                    listSerReplaced.add(serReplacedChild);
                    break;
                }
            }
            if (nFoundSimilarChars < idx + 1) {
                // this char is not found.
                break;
            }

        }

        if (nFoundSimilarChars < mwPattern.mstrShouldBe.length()) {
            return nStart;  // means nothing found.
        } else if ((mwPattern.mstrShouldBe.length() > 2 && nMisRecogChars >= 0.5 * mwPattern.mstrShouldBe.length())
                || (mwPattern.mstrShouldBe.length() == 2 && nMisRecogChars == 2)) {
            return nStart;  // too many miss recog chars.
        } else {
            // we need to check the gap to ensure that they are in a word.
            for (int idx = nStart + 1; idx < idxSer; idx++) {
                if (ser.mlistChildren.get(idx).mnLeft - ser.mlistChildren.get(idx - 1).getRightPlus1()
                        > ConstantsMgr.msdWordCharMaxGap * (nWordBottomP1 - nWordTop)) {
                    // the gap is toooo wide, should not be in a word
                    return nStart;
                }
            }
            return idxSer;  // end + 1 of the similarity string.
        }
    }

    @Override
    public String toString() {
        if (mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE) {
            UnitProtoType unitProtoType = new UnitProtoType();
            unitProtoType.mnUnitType = mType;
            return unitProtoType.toString();

        } else if (mlistChildren.size() == 0) {
            return "";
        } else if (mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT || mnExprRecogType == EXPRRECOGTYPE_MULTIEXPRS) {
            // horizontally cut by blank div, every child is equal. similar to multiexprs
            String strReturn = "";
            for (int idx = 0; idx < mlistChildren.size(); idx++) {
                strReturn += mlistChildren.get(idx).toString();
                if (idx < mlistChildren.size() - 1) {
                    strReturn += "\n";
                }
            }
            return strReturn;
        }

        else if (mnExprRecogType == EXPRRECOGTYPE_HLINECUT) {
            // horizontally cut by line div, and assume there are three children, the middle one is divide.
            String strNumerator = mlistChildren.getFirst().toString();
            String strDenominator = mlistChildren.getLast().toString();
            return "{" + strNumerator + "}/{" + strDenominator + "}";
        } else if (mnExprRecogType == EXPRRECOGTYPE_HCUTCAP) {
            String strCap = mlistChildren.getFirst().toString();
            String strBase = mlistChildren.getLast().toString();
            return "\\top{" + strCap + ", " + strBase + "}";
        } else if (mnExprRecogType == EXPRRECOGTYPE_HCUTUNDER) {
            String strBase = mlistChildren.getFirst().toString();
            String strUnder = mlistChildren.getLast().toString();
            return "\\under{" + strBase + ", " + strUnder + "}";
        } else if (mnExprRecogType == EXPRRECOGTYPE_HCUTCAPUNDER) {
            String strCap = mlistChildren.getFirst().toString();
            String strBase = mlistChildren.get(1).toString();
            String strUnder = mlistChildren.getLast().toString();
            return "\\topunder{" + strCap + ", " + strBase + ", " + strUnder + "}";
        } else if (mnExprRecogType == EXPRRECOGTYPE_VBLANKCUT || mnExprRecogType == EXPRRECOGTYPE_LISTCUT) {
            // treat list cut like VBlankcut if and only if when we print its value to string.
            String strReturn = "";
            for (int idx = 0; idx < mlistChildren.size(); idx++) {
                strReturn += mlistChildren.get(idx).toString();
            }
            return strReturn;
        } else if (mnExprRecogType == EXPRRECOGTYPE_VCUTLEFTTOPNOTE) {
            return "\\lefttop{" + mlistChildren.getFirst().toString() + ", " + mlistChildren.getLast().toString() + "}";
        } else if (mnExprRecogType == EXPRRECOGTYPE_VCUTLOWERNOTE) {
            return "{" + mlistChildren.getFirst().toString() + "}_{" + mlistChildren.getLast().toString() + "}";
        } else if (mnExprRecogType == EXPRRECOGTYPE_VCUTUPPERNOTE) {
            return "{" + mlistChildren.getFirst().toString() + "}^{" + mlistChildren.getLast().toString() + "}";
        } else if (mnExprRecogType == EXPRRECOGTYPE_VCUTLUNOTES) {
            return "{" + mlistChildren.getFirst().toString() + "}_{"
                    + mlistChildren.get(1).toString() + "}^{" + mlistChildren.getLast().toString() + "}";
        } else if (mnExprRecogType == EXPRRECOGTYPE_VCUTMATRIX) {
            // horizontally cut by blank div, every child is equal.
            String strReturn = "[";
            for (int idx = 0; idx < mlistChildren.getFirst().mlistChildren.size(); idx++) {
                strReturn += "[";
                for (int idx1 = 0; idx1 < mlistChildren.size(); idx1++) {
                    strReturn += mlistChildren.get(idx1).mlistChildren.get(idx).toString();
                    if (idx1 < mlistChildren.size() - 1) {
                        strReturn += ",";
                    }
                }
                strReturn += "]";
                if (idx < mlistChildren.getFirst().mlistChildren.size() - 1) {
                    strReturn += ",";
                }
            }
            strReturn += "]";
            return strReturn;
        } else if (mnExprRecogType == EXPRRECOGTYPE_GETROOT) {
            if (mlistChildren.getFirst().mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE
                    && (mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SQRT_LEFT
                    || mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SQRT_SHORT
                    || mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SQRT_MEDIUM
                    || mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SQRT_LONG
                    || mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SQRT_TALL
                    || mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SQRT_VERY_TALL)) {
                return "\\sqrt{" + mlistChildren.getLast().toString() + "}";
            } else {
                return "\\powrt{" + mlistChildren.getLast().toString() + ", " + mlistChildren.getFirst().toString() + "}";
            }
        } else {
            return "\\unknown";
        }
    }


    public void printMatrix() {
        if (mbarrayBiValues == null || mbarrayBiValues.length == 0) {
            System.out.println("[]");
        } else {
            System.out.println("[");
            for (int idx = 0; idx < mbarrayBiValues[0].length; idx++) {
                for (int idx1 = 0; idx1 < mbarrayBiValues.length; idx1++) {
                    if (idx1 >= mnLeft && idx1 < mnLeft + mnWidth && idx >= mnTop && idx < mnTop + mnHeight) {
                        System.out.print(mbarrayBiValues[idx1][idx] + "\t");
                    } else {
                        System.out.print("0\t");
                    }
                }
                System.out.print("\n");
            }
            System.out.println("]");
        }
    }

    public void printMinContainerMatrix() {
        if (mbarrayBiValues == null || mnWidth == 0) {
            System.out.println("[]");
        } else {
            System.out.println("[");
            for (int idx1 = mnTop; idx1 < mnTop + mnHeight; idx1++) {
                for (int idx = mnLeft; idx < mnLeft + mnWidth; idx++) {
                    System.out.print(mbarrayBiValues[idx][idx1] + "\t");
                }
                System.out.print("\n");
            }
            System.out.println("]");
        }
    }

    //todo LH's change:

    public boolean isPossibleZero(UnitProtoType.Type type) {
        if (type == UnitProtoType.Type.TYPE_BIG_O || type == UnitProtoType.Type.TYPE_SMALL_O
                || type == UnitProtoType.Type.TYPE_BIG_D || type == UnitProtoType.Type.TYPE_SMALL_A
                || type == UnitProtoType.Type.TYPE_SMALL_ALPHA)
            return true;

        return false;
    }

    public boolean isPossibleDot(UnitProtoType.Type type) {
        if (type == UnitProtoType.Type.TYPE_SUBTRACT || type == UnitProtoType.Type.TYPE_BACKWARD_SLASH
                || type == UnitProtoType.Type.TYPE_DOT || type == UnitProtoType.Type.TYPE_DOT_MULTIPLY)
            return true;

        return false;
    }

    public boolean isPossibleTheStartOfMatrixOrMutiExpr(UnitProtoType.Type type) {
        /*Or is possible one*/

        /*
        the code of isPossibleVLnChar.
        if (type == UnitProtoType.Type.TYPE_ONE
                || type == UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET || type == UnitProtoType.Type.TYPE_SQUARE_BRACKET
                || type == UnitProtoType.Type.TYPE_SMALL_I_WITHOUT_DOT || type == UnitProtoType.Type.TYPE_SMALL_L
                || type == UnitProtoType.Type.TYPE_VERTICAL_LINE || type == UnitProtoType.Type.TYPE_BIG_I)    {    // open round bracket is very unlikely to be 1.
            return true;
        }
        return false;
        */
        /*To indentify the type which is likely be the start of Matrix or MutiExpr*/
        if (type == UnitProtoType.Type.TYPE_ONE || type == UnitProtoType.Type.TYPE_LEFT_ARROW
                || type == UnitProtoType.Type.TYPE_SMALL_L || type == UnitProtoType.Type.TYPE_SMALL_F
                || type == UnitProtoType.Type.TYPE_SMALL_I_WITHOUT_DOT || type == UnitProtoType.Type.TYPE_BIG_I
                || type == UnitProtoType.Type.TYPE_VERTICAL_LINE || type == UnitProtoType.Type.TYPE_SMALL_L
                || type == UnitProtoType.Type.TYPE_SQUARE_BRACKET || type == UnitProtoType.Type.TYPE_ROUND_BRACKET
                || type == UnitProtoType.Type.TYPE_BIG_L)
            return true;

        return false;
    }

    public boolean isPossibleTheEndOfMatrix(UnitProtoType.Type type) {
        /*To indentify the type which is likely be the end of Matrix*/
        if (type == UnitProtoType.Type.TYPE_ONE || type == UnitProtoType.Type.TYPE_LEFT_ARROW
                || type == UnitProtoType.Type.TYPE_SMALL_I_WITHOUT_DOT || type == UnitProtoType.Type.TYPE_BIG_I
                || type == UnitProtoType.Type.TYPE_VERTICAL_LINE || type == UnitProtoType.Type.TYPE_SMALL_L
                || type == UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET || type == UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET)
            return true;
        return false;
    }

    public boolean ispossibleEqual(StructExprRecog sers) {
        if (sers.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT) {
            for (StructExprRecog son : sers.mlistChildren) {
                if (son.mType != UnitProtoType.Type.TYPE_SUBTRACT && son.mType != UnitProtoType.Type.TYPE_EQUAL)
                    return false;
            }
        } else
            return false;
        System.out.println("Is possible equal!!!");
        return true;
    }

    public enum status {NORMAL, FUNCTIONSET, MATRIX}

    ; //never use since now.

    /*EXPRRECOGTYPE_HBLANKCUT = 2;
    public final static int EXPRRECOGTYPE_HLINECUT = 3;
    public final static int EXPRRECOGTYPE_HCUTCAP = 4;
    public final static int EXPRRECOGTYPE_HCUTUNDER = 5;
    public final static int EXPRRECOGTYPE_HCUTCAPUNDER = 6;
    public final static int EXPRRECOGTYPE_VBLANKCUT = 10;
    public final static int EXPRRECOGTYPE_VCUTLEFTTOPNOTE = 11;    // first element is left top note, second element is base
    public final static int EXPRRECOGTYPE_VCUTUPPERNOTE = 12;   // first element is  base, second element is upper note
    public final static int EXPRRECOGTYPE_VCUTLOWERNOTE = 13;   // first element is base, second element is lower note
    public final static int EXPRRECOGTYPE_VCUTLUNOTES = 14;  // first element is base, second element is lower note, third element is upper note.
    public final static int EXPRRECOGTYPE_VCUTMATRIX = 20; */

    boolean isPossibleDiff(){
        /*To judge if the unitType is diff, I will add more type into here*/
        if(this.mType == UnitProtoType.Type.TYPE_FORWARD_SLASH){
            return true;
        }
        return false;
    }

    public StructExprRecog recifysigleVertialLine(StructExprRecog sers) {
        StructExprRecog serReturn = new StructExprRecog(sers.mbarrayBiValues);

        if (sers.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT) {

            LinkedList<StructExprRecog> newSon = new LinkedList<>();
            for (StructExprRecog son : sers.mlistChildren) {
                if (son.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE) {
                    newSon.add(son);
                } else if (son.mnExprRecogType == EXPRRECOGTYPE_HCUTCAP || son.mnExprRecogType == EXPRRECOGTYPE_HCUTUNDER) {
                    newSon.add(son.mlistChildren.getFirst());
                    newSon.add(son.mlistChildren.getLast());
                } else if (son.mnExprRecogType == EXPRRECOGTYPE_HCUTCAPUNDER) {
                    newSon.add(son.mlistChildren.getFirst());
                    newSon.add(son.mlistChildren.get(1));
                    newSon.add(son.mlistChildren.getLast());
                } /*else if (son.mnExprRecogType == EXPRRECOGTYPE_VCUTUPPERNOTE) {
                    newSon.add(son.mlistChildren.getLast());
                    newSon.add(son.mlistChildren.getFirst());
                } else if (son.mnExprRecogType == EXPRRECOGTYPE_VCUTLOWERNOTE) {
                    newSon.add(son.mlistChildren.getFirst());
                    newSon.add(son.mlistChildren.getLast());
                    System.out.println("change!!!!!!!!!!");
                } else if (son.mnExprRecogType == EXPRRECOGTYPE_VBLANKCUT) {
                    newSon.add(son.mlistChildren.getFirst());
                    newSon.add(son.mlistChildren.getLast());
                    System.out.println("change!!!!!!!!!!");
                } */ else {
                    //hhhhhh
                    newSon.add(son);
                }
            }
            if (newSon.size() != 0) {
                serReturn.setStructExprRecog(newSon, EXPRRECOGTYPE_HBLANKCUT);
            }
            for (StructExprRecog son : sers.mlistChildren) {
                if (son.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE) {
                    if (isPossibleTheStartOfMatrixOrMutiExpr(son.mType)) {
                        son.changeSEREnumType(UnitProtoType.Type.TYPE_ONE, "");
                    } else if (isPossibleZero(son.mType)) {
                        son.changeSEREnumType(UnitProtoType.Type.TYPE_ZERO, "");
                    } else if (son.mType == UnitProtoType.Type.TYPE_LARGER) {
                        son.changeSEREnumType(UnitProtoType.Type.TYPE_SEVEN, "");
                    }
                }
            }
        }
        return serReturn;
    }

    public boolean ispossibletheconsofHblank(StructExprRecog H, StructExprRecog S) {
        if (H.mnExprRecogType != EXPRRECOGTYPE_HBLANKCUT) {
            return false;
        } else {
            for (StructExprRecog son : H.mlistChildren) {
                if (((son.mnTop - son.mnHeight * 0.3) < S.mnTop) && (son.mnTop + son.mnHeight * 1.3 > (S.mnTop + S.mnHeight))) {
                    System.out.println("is belong to the H");
                    return true;
                }
            }
        }
        return false;
    }
    /*
     else if(listCuts.getFirst().mType == UnitProtoType.Type.TYPE_DOT
                    &&listCuts.getLast().mType ==  UnitProtoType.Type.TYPE_DOT
                    &&listCuts.get(1).mType == UnitProtoType.Type.TYPE_SUBTRACT){

        double dSimilarity = (listCuts.getFirst().getArea() * listCuts.getFirst().mdSimilarity
                + listCuts.get(1).getArea() * listCuts.get(1).mdSimilarity
                + listCuts.getLast().getArea() * listCuts.getLast().mdSimilarity)
                / (listCuts.getFirst().getArea() + listCuts.get(1).getArea() + listCuts.getLast().getArea());  // total area should not be zero here.
        System.out.println("Now we change it");
        LinkedList<ImageChop> listParts = new LinkedList<ImageChop>();
        ImageChop imgChopTop = listCuts.getFirst().getImageChop(false);
        listParts.add(imgChopTop);
        ImageChop imgChopHLn = listCuts.get(1).getImageChop(false);
        listParts.add(imgChopHLn);
        ImageChop imgChopBottom = listCuts.getLast().getImageChop(false);
        listParts.add(imgChopBottom);
        ImageChop imgChop4SER = ExprSeperator.mergeImgChopsWithSameOriginal(listParts);
        serReturn.setStructExprRecog(UnitProtoType.Type.TYPE_DIVIDE, UNKNOWN_FONT_TYPE, mnLeft, mnTop, mnWidth, mnHeight, imgChop4SER, dSimilarity);
    }*/

    public void bigger(){
        mnTop -= mnHeight*0.15;
        if(mnTop < 0){
            mnTop = 1;
        }
        mnHeight = (int)(mnHeight*1.3);
    }
//    public StructExprRecog yx_recifyF() {
//        StructExprRecog curSer, preSer, afterSer, startofM = null, firstBhblank = null, lastBhblank = null;
//        //int startidx = -1;
//        //status S = status.NORMAL; /*0 means normal expr, 1 means possibly function set, 2 means possibly Matrix*/
//        if(mlistChildren.size()==1&&mlistChildren.get(0).mType==UnitProtoType.Type.TYPE_DOT)
//            mlistChildren.remove(0);
////        for (int idx = 1; idx < mlistChildren.size() - 1; idx++) {
////            curSer = mlistChildren.get(idx);
////            preSer = mlistChildren.get(idx - 1);
////            afterSer = mlistChildren.get(idx + 1);
////            //todo yuxi
////            System.out.println("YX:" + preSer.toString()+"\tEXPR_TYPE: "+preSer.mType);
////            //if(curSer.mType==UnitProtoType.Type.TYPE_DOT && )
////        }
//
//        return this;
//    }

    boolean ispossibleDiv(StructExprRecog sers){
        if(sers.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT && sers.mlistChildren.size() == 3){
            if(sers.mlistChildren.get(1).mType == UnitProtoType.Type.TYPE_SUBTRACT
                    && (isPossibleDot(sers.mlistChildren.getFirst().mType)
                    || isPossibleDot(sers.mlistChildren.getLast().mType)))
                return true;
        }
        return false;
    }

    public StructExprRecog recifyF() {
        /*1. optimizing the recognising of function set and matrix*/

        if (mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE) {
            /* but after some test, i do like to remove it.*/
            return this;

        } else if (mlistChildren.size() == 0) {

            return null;

        } else if (mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT) {
            // horizontally cut by blank div, every child is equal. similar to multiexprs
            int sta = 0;
            StructExprRecog preSon = null;
            if (ispossibleEqual(this)) {
                LinkedList S = new LinkedList<>();
                for(StructExprRecog son : mlistChildren){
                    if(son.mType == UnitProtoType.Type.TYPE_SUBTRACT && sta == 0){
                        sta = 1;
                        S.add(son);
                        preSon = son;
                    }
                    else if(son.mType == UnitProtoType.Type.TYPE_SUBTRACT && sta == 1){
                        System.out.println(son.mnWidth + "====" + preSon.mnWidth);
                        System.out.println(Math.abs((Math.abs(preSon.mnWidth-son.mnWidth))/(preSon.mnWidth+0.01) - 0));
                        //todo LH003, Here is a bug because I didn't consider the height of equation.
                        if(Math.abs((son.mnWidth)/(preSon.mnWidth+0.01) - 1) <= 0.4 && (son.mnTop-preSon.mnTop) < son.mnWidth*1.2) {
                            //Here we change to substract to euqal
                            double similarity = son.getSimilarity() > preSon.getSimilarity() ? son.getSimilarity() : preSon.getSimilarity();
                            LinkedList<ImageChop> listParts = new LinkedList<ImageChop>();
                            ImageChop imgChop1 = preSon.getImageChop(false);
                            listParts.add(imgChop1);
                            ImageChop imgChop2 = son.getImageChop(false);
                            listParts.add(imgChop2);
                            ImageChop imgChop4SER = ExprSeperator.mergeImgChopsWithSameOriginal(listParts);
                            int newTop = preSon.mnTop;
                            int newheight = son.mnTop - preSon.mnTop + son.mnHeight;
                            son.setStructExprRecog(UnitProtoType.Type.TYPE_EQUAL, UNKNOWN_FONT_TYPE, mnLeft, newTop, mnWidth, newheight, imgChop4SER, similarity);
                            //mlistChildren.remove(preSon);
                            S.add(son);
                            S.remove(preSon);
                            if (mlistChildren.size() == 1) {
                                this.setStructExprRecog(UnitProtoType.Type.TYPE_EQUAL, UNKNOWN_FONT_TYPE, mnLeft, mnTop, mnWidth, mnHeight, imgChop4SER, similarity);
                            }
                            sta = 0;
                        }
                    } else {
                        S.add(son);
                        sta = 0;

                    }
                }
                mlistChildren.clear();
                mlistChildren.addAll(S);
                if (mlistChildren.size() == 1) {
                    this.setStructExprRecog(UnitProtoType.Type.TYPE_EQUAL, UNKNOWN_FONT_TYPE, mnLeft, mnTop, mnWidth, mnHeight, mlistChildren.getFirst().mimgChop, 0);
                }

            }else if (ispossibleDiv(recifysigleVertialLine(this))){
                StructExprRecog newS = recifysigleVertialLine(this);
                StructExprRecog fir = newS.mlistChildren.getFirst();
                StructExprRecog las = newS.mlistChildren.getLast();
                StructExprRecog mid = newS.mlistChildren.get(1);
                System.out.println(fir.mnWidth + " " + las.mnWidth + " " + mid.mnWidth);
                if ((mid.mnWidth / fir.mnWidth > 1.3) && (mid.mnWidth / las.mnWidth > 1.3)) {
                    /*it should be div*/
                    double similarity = 0.0;
                    this.setStructExprRecog(UnitProtoType.Type.TYPE_DIVIDE, UNKNOWN_FONT_TYPE, mnLeft, mnTop, mnWidth, mnHeight, this.getImageChop(false), similarity);
                }
            }

        } else if (mnExprRecogType == EXPRRECOGTYPE_MULTIEXPRS) {


        } else if (mnExprRecogType == EXPRRECOGTYPE_HLINECUT) {
            // horizontally cut by line div, and assume there are three children, the middle one is divide.
            /*If the upper or under is possible be dot, the whole expr maybe div, we should consider it width*/
            StructExprRecog strNumerator = mlistChildren.getFirst();
            StructExprRecog strDenominator = mlistChildren.getLast();
            if (isPossibleDot(strDenominator.mType) && isPossibleDot(strNumerator.mType)) {
                if ((this.mnWidth / strDenominator.mnWidth > 1.3) && (this.mnWidth / strNumerator.mnWidth > 1.3)) {
                    /*it should be div*/
                    double similarity = 0.0;
                    this.setStructExprRecog(UnitProtoType.Type.TYPE_DIVIDE, UNKNOWN_FONT_TYPE, mnLeft, mnTop, mnWidth, mnHeight, this.getImageChop(false), similarity);
                }
            }

        } else if (mnExprRecogType == EXPRRECOGTYPE_HCUTCAP) {


        } else if (mnExprRecogType == EXPRRECOGTYPE_HCUTUNDER) {


        } else if (mnExprRecogType == EXPRRECOGTYPE_HCUTCAPUNDER) {


        } else if (mnExprRecogType == EXPRRECOGTYPE_VBLANKCUT) {
            /*If we find a 't' and the second letter after it is n, then we think the letter after 't' should be a*/

            StructExprRecog curSer, preSer, afterSer, startofM = null, firstBhblank = null, lastBhblank = null;
            int startidx = -1;
            status S = status.NORMAL; /*0 means normal expr, 1 means possibly function set, 2 means possibly Matrix*/
            for (int idx = 1; idx < mlistChildren.size() - 1; idx++) {
                curSer = mlistChildren.get(idx);
                preSer = mlistChildren.get(idx - 1);
                afterSer = mlistChildren.get(idx + 1);
                /*
                if (preSer.mType == UnitProtoType.Type.TYPE_SQUARE_BRACKET || preSer.mType == UnitProtoType.Type.TYPE_ROUND_BRACKET) {
                    startofM = preSer;
                    startidx = idx - 1;
                } else if (preSer.mType == UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET || preSer.mType == UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET) {
                    startofM = null;
                    startidx = -1;
                }*/

                if (preSer.mType == UnitProtoType.Type.TYPE_INTEGRATE && idx == 1) {
                    for (int idx2 = idx; idx2 < mlistChildren.size(); idx2++) {
                        if (ispossibleEqual(mlistChildren.get(idx2))) {
                            //todo YX 积分回来吧
                            //preSer.changeSEREnumType(UnitProtoType.Type.TYPE_BRACE, "");
                            startofM = preSer;
                            startidx = idx - 1;
                            break;
                        }
                    }
                    if(curSer.mnExprRecogType == 2){
                        for(int idx2 = 0; idx2<curSer.mlistChildren.size(); ++idx2){
                            StructExprRecog temp = curSer.mlistChildren.get(idx2);
                            if(temp.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && isPossibleZero(temp.mType)){
                                temp.changeSEREnumType(UnitProtoType.Type.TYPE_ZERO, "");
                            }else if(temp.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && isPossibleTheStartOfMatrixOrMutiExpr(temp.mType)){
                                temp.changeSEREnumType(UnitProtoType.Type.TYPE_ONE, "");
                            }
                        }
                    }
                }

                if (curSer.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT && !ispossibleEqual(curSer)) {
                    lastBhblank = curSer;
                } else if (lastBhblank != null) {
                    if (ispossibletheconsofHblank(lastBhblank, curSer)) {

                    } else {
                        lastBhblank = null;
                    }
                }

                /*a special rule for the t*n*/
                if (preSer.mType == UnitProtoType.Type.TYPE_SMALL_T) {
                    if (afterSer.mType == UnitProtoType.Type.TYPE_SMALL_N) {
                        curSer.mType = UnitProtoType.Type.TYPE_SMALL_A;
                        curSer.mstrFont = "";
                        curSer.mdSimilarity = 0.0;
                        curSer.mnExprRecogType = StructExprRecog.EXPRRECOGTYPE_ENUMTYPE;
                        System.out.println("RECIFY------change one t*n to tan!!!");
                    }
                }
                if (curSer.mType == UnitProtoType.Type.TYPE_SMALL_I) {
                    if (afterSer.mType == UnitProtoType.Type.TYPE_SMALL_N) {
                        preSer.mType = UnitProtoType.Type.TYPE_SMALL_S;
                        preSer.mstrFont = "";
                        preSer.mdSimilarity = 0.0;
                        preSer.mnExprRecogType = StructExprRecog.EXPRRECOGTYPE_ENUMTYPE;
                        System.out.println("RECIFY------change one *in to sin!!!");
                    }
                }
                if (preSer.mType == UnitProtoType.Type.TYPE_SMALL_C || preSer.mType == UnitProtoType.Type.TYPE_BIG_C) {
                    if (afterSer.mType == UnitProtoType.Type.TYPE_SMALL_S) {
                        curSer.mType = UnitProtoType.Type.TYPE_SMALL_O;
                        curSer.mstrFont = "";
                        curSer.mdSimilarity = 0.0;
                        curSer.mnExprRecogType = StructExprRecog.EXPRRECOGTYPE_ENUMTYPE;
                        System.out.println("RECIFY------change one c*s to cos!!!");
                    }
                }

                if (isPossibleTheStartOfMatrixOrMutiExpr(preSer.mType) ) {
                    if (afterSer.mType == UnitProtoType.Type.TYPE_SMALL_G) {
                        curSer.mType = UnitProtoType.Type.TYPE_SMALL_O;
                        curSer.mstrFont = "";
                        curSer.mdSimilarity = 0.0;
                        curSer.mnExprRecogType = StructExprRecog.EXPRRECOGTYPE_ENUMTYPE;
                        System.out.println("RECIFY------change one l*g to log!!!");
                    }
                }

                if (preSer.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && isPossibleTheStartOfMatrixOrMutiExpr(preSer.mType)) {
                    int idxofFirstHblank = -1;
                    firstBhblank = null;
                    for (int idx2 = idx; idx2 < mlistChildren.size(); idx2++) {
                        StructExprRecog temp = mlistChildren.get(idx2);
                        if (temp.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT) {
                            idxofFirstHblank = idx2;
                            break;
                        }
                    }
                    if (idxofFirstHblank != -1) {
                        firstBhblank = mlistChildren.get(idxofFirstHblank);
                        for (int idx2 = idx; idx2 < idxofFirstHblank; idx2++) {
                            if (!ispossibletheconsofHblank(firstBhblank, mlistChildren.get(idx2)) || mlistChildren.get(idx2).mType == UnitProtoType.Type.TYPE_EQUAL) {
                                firstBhblank = null;
                                break;
                            }
                        }
                    }
                    if (firstBhblank != null) {
                        LinkedList<StructExprRecog> grandson = new LinkedList<>();
                        grandson.addAll(firstBhblank.mlistChildren);
                        int totalHeight = 0;
                        for (int idx2 = 0; idx2 < grandson.size(); ++idx2) {
                            StructExprRecog curGrandson = grandson.get(idx2);
                            totalHeight += curGrandson.mnHeight;
                            System.out.println(curGrandson.mnHeight);
                        }
                        System.out.println(preSer.mnHeight);
                        if (totalHeight <= preSer.mnHeight) {
                            preSer.changeSEREnumType(UnitProtoType.Type.TYPE_BRACE, "");

                            System.out.println("RECIFY------find one brace!!!");
                            //preSer.bigger();
                            startofM = preSer;
                            startidx = idx - 1;

                            lastBhblank = null;
                            firstBhblank = null;
                        }
                    }
                }
                if (lastBhblank != null && afterSer.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && isPossibleTheEndOfMatrix(afterSer.mType)) {
                    LinkedList<StructExprRecog> grandson = new LinkedList<>();
                    grandson.addAll(curSer.mlistChildren);
                    int totalHeight = 0;
                    for (int idx2 = 0; idx2 < grandson.size(); ++idx2) {
                        StructExprRecog curGrandson = grandson.get(idx2);
                        totalHeight += curGrandson.mnHeight;
                    }
                    if (totalHeight <= afterSer.mnHeight) {
                        afterSer.changeSEREnumType(UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET, "");
                        //afterSer.bigger();
                        lastBhblank = null;
                        firstBhblank = null;
                        /*but how should we do when starofM == null*/
                        if (startofM != null) {
                            startofM.changeSEREnumType(UnitProtoType.Type.TYPE_SQUARE_BRACKET, "");

                            System.out.println("RECIFY------find one close brace!!!");

                            /*Then I want to add some rule to recify the item of the Matrix. each item of a
                             * matrix should must be a unit type*/
                            int countOfRow = 0;
                            LinkedList<StructExprRecog> rows[] = new LinkedList[10];
                            LinkedList<ImageChop> listParts = new LinkedList<ImageChop>();
                            //StructExprRecog serofMatrix = new StructExprRecog(); //这里需要合并
                            //todo LH005: here to get the structure of matrix.
                            for (int cidx = startidx + 1; cidx <= idx; ++cidx) {
                                //recifysigleVertialLine(mlistChildren.get(cidx));
                                StructExprRecog newColumn = recifysigleVertialLine(mlistChildren.get(cidx));
                                ImageChop imgChop1 = mlistChildren.get(cidx).getImageChop(true);
                                listParts.add(imgChop1);
                                if(cidx == startidx + 1){
                                    countOfRow = newColumn.mlistChildren.size();
                                    for(int jdx = 0; jdx < countOfRow; ++jdx){
                                        rows[jdx] = new LinkedList<>();
                                        rows[jdx].add(newColumn.mlistChildren.get(jdx));
                                    }
                                }else{
                                    for(int jdx = 0; jdx < countOfRow && jdx<newColumn.mlistChildren.size(); ++jdx){
                                        rows[jdx].add(newColumn.mlistChildren.get(jdx));
                                    }
                                }
                            }

                            ImageChop imgChop4SER = ExprSeperator.mergeImgChopsWithSameOriginal(listParts);
                            StructExprRecog serofMatrix = new StructExprRecog(imgChop4SER.mbarrayImg);
                            //son.setStructExprRecog(UnitProtoType.Type.TYPE_EQUAL, UNKNOWN_FONT_TYPE, mnLeft, newTop, mnWidth, newheight, imgChop4SER, similarity);

                            LinkedList<StructExprRecog> ma = new LinkedList<>();

                            for(int idxs = 0; idxs < countOfRow; ++idxs){
                                StructExprRecog serOfCurRow = new StructExprRecog(imgChop4SER.mbarrayImg);
                                LinkedList<StructExprRecog> curRow = new LinkedList<>();
                                curRow.addAll(rows[idxs]);
                                serOfCurRow.setStructExprRecog(curRow, EXPRRECOGTYPE_VBLANKCUT);
                            }

                            serofMatrix.setStructExprRecog(ma, EXPRRECOGTYPE_HBLANKCUT);

                            startofM = null;
                            startidx = -1;
                        }
                    }
                }

            }

        } else if (mnExprRecogType == EXPRRECOGTYPE_LISTCUT) {
            /*I don't know what is list cut means.*/

        } else if (mnExprRecogType == EXPRRECOGTYPE_VCUTLEFTTOPNOTE) {

        } else if (mnExprRecogType == EXPRRECOGTYPE_VCUTLOWERNOTE) {

        } else if (mnExprRecogType == EXPRRECOGTYPE_VCUTUPPERNOTE) {

        } else if (mnExprRecogType == EXPRRECOGTYPE_VCUTLUNOTES) {

        } else if (mnExprRecogType == EXPRRECOGTYPE_VCUTMATRIX) {
            // horizontally cut by blank div, every child is equal.


        } else if (mnExprRecogType == EXPRRECOGTYPE_GETROOT) {

        } else {

        }
        if (mnExprRecogType != EXPRRECOGTYPE_ENUMTYPE) {
            for (StructExprRecog son : mlistChildren) {
                son = son.recifyF();
            }
        }

        return this;
    }

    public void recifyG(){
        if(this == null){
            return;
        }
        if (mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE) {


        } else if (mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT || mnExprRecogType == EXPRRECOGTYPE_MULTIEXPRS) {

        } else if (mnExprRecogType == EXPRRECOGTYPE_HLINECUT) {

        } else if (mnExprRecogType == EXPRRECOGTYPE_HCUTCAP) {

        } else if (mnExprRecogType == EXPRRECOGTYPE_HCUTUNDER) {

        } else if (mnExprRecogType == EXPRRECOGTYPE_HCUTCAPUNDER) {

        } else if (mnExprRecogType == EXPRRECOGTYPE_VBLANKCUT ) {
            //todo: LH004, try to restruct the matrix which is neglected.

            StructExprRecog startofM=null, endofM=null;

            for(int idx = 0; idx < mlistChildren.size(); ++idx){
                StructExprRecog son = mlistChildren.get(idx);

                //find the start of the matrix.
                if(son.mnExprRecogType == EXPRRECOGTYPE_VCUTLUNOTES && son.mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SQUARE_BRACKET){
                    startofM = son;
                    //find the end of the matrix.
                    for(int idx2 = idx + 1; idx2 < mlistChildren.size(); ++idx2){
                        StructExprRecog endSon = mlistChildren.get(idx2);
                        if(endSon.mnExprRecogType == EXPRRECOGTYPE_ENUMTYPE && endSon.mType == UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET){
                            endofM = endSon;
                            break;
                        }
                    }
                    if(endofM != null){
                        LinkedList<StructExprRecog> row1 = new LinkedList<>();
                        LinkedList<StructExprRecog> row2 = new LinkedList<>();
                        LinkedList<StructExprRecog> row3 = new LinkedList<>(); // now can only deal with the matrix which has two or three rows.

                        row3.add(startofM.mlistChildren.get(1));
                        row1.add(startofM.mlistChildren.getLast());
                        for(int idx2 = idx + 1; idx2 < mlistChildren.indexOf(endofM); ++idx2){

                        }

                    }
                }
            }
        } else if (mnExprRecogType == EXPRRECOGTYPE_LISTCUT) {

        } else if (mnExprRecogType == EXPRRECOGTYPE_VCUTLEFTTOPNOTE) {

        } else if (mnExprRecogType == EXPRRECOGTYPE_VCUTLOWERNOTE) {

        } else if (mnExprRecogType == EXPRRECOGTYPE_VCUTUPPERNOTE) {

        } else if (mnExprRecogType == EXPRRECOGTYPE_VCUTLUNOTES) {

        } else if (mnExprRecogType == EXPRRECOGTYPE_VCUTMATRIX) {

        } else if (mnExprRecogType == EXPRRECOGTYPE_GETROOT) {

        }
    }
}


