/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyzapps.mathrecog;

/**
 *
 * @author tonyc
 */
public class BLUCharIdentifier {

    public double mdUpperNoteUBoundThresh;
    public double mdUpperNoteLBoundThresh;
    public double mdLowerNoteUBoundThresh;
    public double mdLowerNoteLBoundThresh;
    public double mdUpperNoteUExtBoundThresh;
    public double mdUpperNoteLExtBoundThresh;
    public double mdLowerNoteUExtBoundThresh;
    public double mdLowerNoteLExtBoundThresh;

    public double mdAdjUpperNoteUBoundThresh;
    public double mdAdjUpperNoteLBoundThresh;
    public double mdAdjLowerNoteUBoundThresh;
    public double mdAdjLowerNoteLBoundThresh;
    public double mdAdjUpperNoteUExtBoundThresh;
    public double mdAdjUpperNoteLExtBoundThresh;
    public double mdAdjLowerNoteUExtBoundThresh;
    public double mdAdjLowerNoteLExtBoundThresh;

    public StructExprRecog mserBase;
    public int mnBaseStylePosition = 0;
    public BLUCharIdentifier(StructExprRecog serBase)   {
        setBLUCharIdentifier(serBase);
    }

    public BLUCharIdentifier(StructExprRecog serBase, int nBaseStylePosition)   {
        setBLUCharIdentifier(serBase, nBaseStylePosition);
    }

    public final void setBLUCharIdentifier(StructExprRecog serBase)   {
        int nBaseStylePosition = StructExprRecog.getSERStylePosition(serBase);
        setBLUCharIdentifier(serBase, nBaseStylePosition);            
    }

    public final void setBLUCharIdentifier(StructExprRecog serBase, int nBaseStylePosition)   {
        // Using getPrincipleSER is for a situation like \SIGMA_{something},
        // base level char following it may be misrecognized to upper notes.
        StructExprRecog serBasePrinciple = serBase.getPrincipleSER(1);
        double[] darray = calcUpperNoteUpperBoundThresh(serBasePrinciple, nBaseStylePosition);
        mdUpperNoteUBoundThresh = darray[0];
        mdAdjUpperNoteUBoundThresh = darray[1];
        darray = calcUpperNoteLowerBoundThresh(serBasePrinciple, nBaseStylePosition);
        mdUpperNoteLBoundThresh = darray[0];
        mdAdjUpperNoteLBoundThresh = darray[1];
        darray = calcLowerNoteUpperBoundThresh(serBasePrinciple, nBaseStylePosition);
        mdLowerNoteUBoundThresh = darray[0];
        mdAdjLowerNoteUBoundThresh = darray[1];
        darray = calcLowerNoteLowerBoundThresh(serBasePrinciple, nBaseStylePosition);
        mdLowerNoteLBoundThresh = darray[0];
        mdAdjLowerNoteLBoundThresh = darray[1];
        darray = calcUpperNoteUpperExtBoundThresh(serBasePrinciple, nBaseStylePosition);
        mdUpperNoteUExtBoundThresh = darray[0];
        mdAdjUpperNoteUExtBoundThresh = darray[1];
        darray = calcUpperNoteLowerExtBoundThresh(serBasePrinciple, nBaseStylePosition);
        mdUpperNoteLExtBoundThresh = darray[0];
        mdAdjUpperNoteLExtBoundThresh = darray[1];
        darray = calcLowerNoteUpperExtBoundThresh(serBasePrinciple, nBaseStylePosition);
        mdLowerNoteUExtBoundThresh = darray[0];
        mdAdjLowerNoteUExtBoundThresh = darray[1];
        darray = calcLowerNoteLowerExtBoundThresh(serBasePrinciple, nBaseStylePosition);
        mdLowerNoteLExtBoundThresh = darray[0];
        mdAdjLowerNoteLExtBoundThresh = darray[1];
        mserBase = serBase;
        mnBaseStylePosition = nBaseStylePosition;            
    }

    public BLUCharIdentifier(BLUCharIdentifier bluCharIdentifier)   {
        mdUpperNoteUBoundThresh = bluCharIdentifier.mdUpperNoteUBoundThresh;
        mdUpperNoteLBoundThresh = bluCharIdentifier.mdUpperNoteLBoundThresh;
        mdLowerNoteUBoundThresh = bluCharIdentifier.mdLowerNoteUBoundThresh;
        mdLowerNoteLBoundThresh = bluCharIdentifier.mdLowerNoteLBoundThresh;
        mdUpperNoteUExtBoundThresh = bluCharIdentifier.mdUpperNoteUExtBoundThresh;
        mdUpperNoteLExtBoundThresh = bluCharIdentifier.mdUpperNoteLExtBoundThresh;
        mdLowerNoteUExtBoundThresh = bluCharIdentifier.mdLowerNoteUExtBoundThresh;
        mdLowerNoteLExtBoundThresh = bluCharIdentifier.mdLowerNoteLExtBoundThresh;
        mdAdjUpperNoteUBoundThresh = bluCharIdentifier.mdAdjUpperNoteUBoundThresh;
        mdAdjUpperNoteLBoundThresh = bluCharIdentifier.mdAdjUpperNoteLBoundThresh;
        mdAdjLowerNoteUBoundThresh = bluCharIdentifier.mdAdjLowerNoteUBoundThresh;
        mdAdjLowerNoteLBoundThresh = bluCharIdentifier.mdAdjLowerNoteLBoundThresh;
        mdAdjUpperNoteUExtBoundThresh = bluCharIdentifier.mdAdjUpperNoteUExtBoundThresh;
        mdAdjUpperNoteLExtBoundThresh = bluCharIdentifier.mdAdjUpperNoteLExtBoundThresh;
        mdAdjLowerNoteUExtBoundThresh = bluCharIdentifier.mdAdjLowerNoteUExtBoundThresh;
        mdAdjLowerNoteLExtBoundThresh = bluCharIdentifier.mdAdjLowerNoteLExtBoundThresh;
        mserBase = bluCharIdentifier.mserBase;
        mnBaseStylePosition = bluCharIdentifier.mnBaseStylePosition;            
    }

    @Override
    public BLUCharIdentifier clone()    {
        return new BLUCharIdentifier(this);
    }

    public void copy(BLUCharIdentifier bluCharIdentifier)   {
        mdUpperNoteUBoundThresh = bluCharIdentifier.mdUpperNoteUBoundThresh;
        mdUpperNoteLBoundThresh = bluCharIdentifier.mdUpperNoteLBoundThresh;
        mdLowerNoteUBoundThresh = bluCharIdentifier.mdLowerNoteUBoundThresh;
        mdLowerNoteLBoundThresh = bluCharIdentifier.mdLowerNoteLBoundThresh;
        mdUpperNoteUExtBoundThresh = bluCharIdentifier.mdUpperNoteUExtBoundThresh;
        mdUpperNoteLExtBoundThresh = bluCharIdentifier.mdUpperNoteLExtBoundThresh;
        mdLowerNoteUExtBoundThresh = bluCharIdentifier.mdLowerNoteUExtBoundThresh;
        mdLowerNoteLExtBoundThresh = bluCharIdentifier.mdLowerNoteLExtBoundThresh;
        mdAdjUpperNoteUBoundThresh = bluCharIdentifier.mdAdjUpperNoteUBoundThresh;
        mdAdjUpperNoteLBoundThresh = bluCharIdentifier.mdAdjUpperNoteLBoundThresh;
        mdAdjLowerNoteUBoundThresh = bluCharIdentifier.mdAdjLowerNoteUBoundThresh;
        mdAdjLowerNoteLBoundThresh = bluCharIdentifier.mdAdjLowerNoteLBoundThresh;
        mdAdjUpperNoteUExtBoundThresh = bluCharIdentifier.mdAdjUpperNoteUExtBoundThresh;
        mdAdjUpperNoteLExtBoundThresh = bluCharIdentifier.mdAdjUpperNoteLExtBoundThresh;
        mdAdjLowerNoteUExtBoundThresh = bluCharIdentifier.mdAdjLowerNoteUExtBoundThresh;
        mdAdjLowerNoteLExtBoundThresh = bluCharIdentifier.mdAdjLowerNoteLExtBoundThresh;
        mserBase = bluCharIdentifier.mserBase;
        mnBaseStylePosition = bluCharIdentifier.mnBaseStylePosition;            
    }
// 计算这个ser的level
    public int calcCharLevel(StructExprRecog serThis)   {
        int nThisCharLevel = 0;
        int nThisTop = serThis.mnTop, nThisBottom = serThis.getBottom();
        int nThisSERStylePos = StructExprRecog.getSERStylePosition(serThis);
        if (nThisSERStylePos != mnBaseStylePosition) {
            if ((nThisTop >= mdAdjLowerNoteUBoundThresh && nThisBottom >= mdAdjLowerNoteLBoundThresh)
                    || (nThisTop >= mdAdjLowerNoteUExtBoundThresh && nThisBottom >= mdAdjLowerNoteLExtBoundThresh)) {
                nThisCharLevel = -1;
            } else if ((nThisTop <= mdAdjUpperNoteUBoundThresh && nThisBottom <= mdAdjUpperNoteLBoundThresh)
                    || (nThisTop <= mdAdjUpperNoteUExtBoundThresh && nThisBottom <= mdAdjUpperNoteLExtBoundThresh))  {
                nThisCharLevel = 1;
            }
        } else {
            if ((nThisTop >= mdLowerNoteUBoundThresh && nThisBottom >= mdLowerNoteLBoundThresh)
                    || (nThisTop >= mdLowerNoteUExtBoundThresh && nThisBottom >= mdLowerNoteLExtBoundThresh)) {
                nThisCharLevel = -1;
            } else if ((nThisTop <= mdUpperNoteUBoundThresh && nThisBottom <= mdUpperNoteLBoundThresh)
                    || (nThisTop <= mdUpperNoteUExtBoundThresh && nThisBottom <= mdUpperNoteLExtBoundThresh))  {
                nThisCharLevel = 1;
            }
        }
        return nThisCharLevel;
    }

    public boolean isLowerNote(StructExprRecog serThis) {
        int nThisTop = serThis.mnTop, nThisBottom = serThis.getBottom();
        int nThisSERStylePos = StructExprRecog.getSERStylePosition(serThis);
        if (nThisSERStylePos != mnBaseStylePosition) {
            if ((nThisTop >= mdAdjLowerNoteUBoundThresh && nThisBottom >= mdAdjLowerNoteLBoundThresh)
                    || (nThisTop >= mdAdjLowerNoteUExtBoundThresh && nThisBottom >= mdAdjLowerNoteLExtBoundThresh)) {
                return true;
            }
            return false;
        } else {
            if ((nThisTop >= mdLowerNoteUBoundThresh && nThisBottom >= mdLowerNoteLBoundThresh)
                    || (nThisTop >= mdLowerNoteUExtBoundThresh && nThisBottom >= mdLowerNoteLExtBoundThresh)) {
                return true;
            }
            return false;
        }
    }

    public boolean isUpperNote(StructExprRecog serThis) {
        int nThisTop = serThis.mnTop, nThisBottom = serThis.getBottom();
        int nThisSERStylePos = StructExprRecog.getSERStylePosition(serThis);
        if (nThisSERStylePos != mnBaseStylePosition) {
            if ((nThisTop <= mdAdjUpperNoteUBoundThresh && nThisBottom <= mdAdjUpperNoteLBoundThresh)
                    || (nThisTop <= mdAdjUpperNoteUExtBoundThresh && nThisBottom <= mdAdjUpperNoteLExtBoundThresh))  {
                return true;
            }
            return false;
        } else {
            if ((nThisTop <= mdUpperNoteUBoundThresh && nThisBottom <= mdUpperNoteLBoundThresh)
                    || (nThisTop <= mdUpperNoteUExtBoundThresh && nThisBottom <= mdUpperNoteLExtBoundThresh))  {
                return true;
            }
            return false;
        }
    }

    // following functions always consider adjustment of ser style positon.
    public int calcCharLevel(ImageChop imgChopThis)   {
        int nThisCharLevel = 0;
        int nThisTop = imgChopThis.getTopInOriginalImg(), nThisBottom = imgChopThis.getBottomInOriginalImg();
        if ((nThisTop >= mdAdjLowerNoteUBoundThresh && nThisBottom >= mdAdjLowerNoteLBoundThresh)
                || (nThisTop >= mdAdjLowerNoteUExtBoundThresh && nThisBottom >= mdAdjLowerNoteLExtBoundThresh)) {
            nThisCharLevel = -1;
        } else if ((nThisTop <= mdAdjUpperNoteUBoundThresh && nThisBottom <= mdAdjUpperNoteLBoundThresh)
                || (nThisTop <= mdAdjUpperNoteUExtBoundThresh && nThisBottom <= mdAdjUpperNoteLExtBoundThresh))  {
            nThisCharLevel = 1;
        }            
        return nThisCharLevel;
    }

    public boolean isLowerNote(ImageChop imgChopThis) {
        int nThisTop = imgChopThis.getTopInOriginalImg(), nThisBottom = imgChopThis.getBottomInOriginalImg();
        if ((nThisTop >= mdAdjLowerNoteUBoundThresh && nThisBottom >= mdAdjLowerNoteLBoundThresh)
                || (nThisTop >= mdAdjLowerNoteUExtBoundThresh && nThisBottom >= mdAdjLowerNoteLExtBoundThresh)) {
            return true;
        }
        return false;
    }

    public boolean isUpperNote(ImageChop imgChopThis) {
        int nThisTop = imgChopThis.getTopInOriginalImg(), nThisBottom = imgChopThis.getBottomInOriginalImg();
        if ((nThisTop <= mdAdjUpperNoteUBoundThresh && nThisBottom <= mdAdjUpperNoteLBoundThresh)
                || (nThisTop <= mdAdjUpperNoteUExtBoundThresh && nThisBottom <= mdAdjUpperNoteLExtBoundThresh))  {
            return true;
        }
        return false;
    }

    public int calcCharLevel(int nThisTop, int nThisBottom)   {
        int nThisCharLevel = 0;
        if ((nThisTop >= mdAdjLowerNoteUBoundThresh && nThisBottom >= mdAdjLowerNoteLBoundThresh)
                || (nThisTop >= mdAdjLowerNoteUExtBoundThresh && nThisBottom >= mdAdjLowerNoteLExtBoundThresh)) {
            nThisCharLevel = -1;
        } else if ((nThisTop <= mdAdjUpperNoteUBoundThresh && nThisBottom <= mdAdjUpperNoteLBoundThresh)
                || (nThisTop <= mdAdjUpperNoteUExtBoundThresh && nThisBottom <= mdAdjUpperNoteLExtBoundThresh))  {
            nThisCharLevel = 1;
        }            
        return nThisCharLevel;
    }

    public boolean isLowerNote(int nThisTop, int nThisBottom) {
        if ((nThisTop >= mdAdjLowerNoteUBoundThresh && nThisBottom >= mdAdjLowerNoteLBoundThresh)
                || (nThisTop >= mdAdjLowerNoteUExtBoundThresh && nThisBottom >= mdAdjLowerNoteLExtBoundThresh)) {
            return true;
        }
        return false;
    }

    public boolean isUpperNote(int nThisTop, int nThisBottom) {
        if ((nThisTop <= mdAdjUpperNoteUBoundThresh && nThisBottom <= mdAdjUpperNoteLBoundThresh)
                || (nThisTop <= mdAdjUpperNoteUExtBoundThresh && nThisBottom <= mdAdjUpperNoteLExtBoundThresh))  {
            return true;
        }
        return false;
    }

    // EXPRRECOGTYPE_HBLANKCUT and EXPRRECOGTYPE_MULTIEXPRS can not be treated the
    // same way as EXPRRECOGTYPE_HLINECUT, considering that \sum_(a+1)^b, where ba
    // is an EXPRRECOGTYPE_HBLANKCUT ser
    // EXPRRECOGTYPE_HLINECUT has been treated specially considering x = 3/(4/(x+5)/6)
    // x should not be treated as upper note.
    public static double[] calcUpperNoteUpperExtBoundThresh(StructExprRecog serBase, int nBaseStylePositon) {
        StructExprRecog serCalc = serBase;
        if (/*serBase.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT
                || serBase.mnExprRecogType == EXPRRECOGTYPE_MULTIEXPRS
                || */serBase.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HLINECUT)   {
            serCalc = serBase.mlistChildren.getFirst();
        }
        double dThresh = -0.05, dLift = 0.025;
        double[] darrayReturn = new double[2];
        darrayReturn[0] = serCalc.mnTop + serCalc.mnHeight * dThresh;
        if (nBaseStylePositon == 1) {
            darrayReturn[1] = serCalc.mnTop + serCalc.mnHeight * (dThresh + dLift);
        } else if (nBaseStylePositon == -1) {
            darrayReturn[1] = serCalc.mnTop + serCalc.mnHeight * (dThresh - dLift);
        } else  {
            darrayReturn[1] = darrayReturn[0]; 
        }
        return darrayReturn;
    }
    
    public static double[] calcUpperNoteUpperBoundThresh(StructExprRecog serBase, int nBaseStylePositon) {
        StructExprRecog serCalc = serBase;
        if (/*serBase.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT
                || serBase.mnExprRecogType == EXPRRECOGTYPE_MULTIEXPRS
                || */serBase.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HLINECUT)   {
            serCalc = serBase.mlistChildren.getFirst();
        }
        double dThresh = 0.25, dLift = 0.025;
        double[] darrayReturn = new double[2];
        darrayReturn[0] = serCalc.mnTop + serCalc.mnHeight * dThresh;
        if (nBaseStylePositon == 1) {
            darrayReturn[1] = serCalc.mnTop + serCalc.mnHeight * (dThresh + dLift);
        } else if (nBaseStylePositon == -1) {
            darrayReturn[1] = serCalc.mnTop + serCalc.mnHeight * (dThresh - dLift);
        } else  {
            darrayReturn[1] = darrayReturn[0]; 
        }
        return darrayReturn;
    }
    
    public static double[] calcUpperNoteLowerBoundThresh(StructExprRecog serBase, int nBaseStylePositon) {
        StructExprRecog serCalc = serBase;
        if (/*serBase.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT
                || serBase.mnExprRecogType == EXPRRECOGTYPE_MULTIEXPRS
                || */serBase.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HLINECUT)   {
            serCalc = serBase.mlistChildren.getFirst();
        }
        double dThresh = 0.55, dLift = 0.05;
        double[] darrayReturn = new double[2];
        darrayReturn[0] = serCalc.mnTop + serCalc.mnHeight * dThresh;
        if (nBaseStylePositon == 1) {
            darrayReturn[1] = serCalc.mnTop + serCalc.mnHeight * (dThresh + dLift);
        } else if (nBaseStylePositon == -1) {
            darrayReturn[1] = serCalc.mnTop + serCalc.mnHeight * (dThresh - dLift);
        } else  {
            darrayReturn[1] = darrayReturn[0]; 
        }
        return darrayReturn;
    }
    
    public static double[] calcUpperNoteLowerExtBoundThresh(StructExprRecog serBase, int nBaseStylePositon) {
        StructExprRecog serCalc = serBase;
        if (/*serBase.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT
                || serBase.mnExprRecogType == EXPRRECOGTYPE_MULTIEXPRS
                || */serBase.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HLINECUT)   {
            serCalc = serBase.mlistChildren.getFirst();
        }
        double dThresh = 0.62, dLift = 0.05;
        double[] darrayReturn = new double[2];
        darrayReturn[0] = serCalc.mnTop + serCalc.mnHeight * dThresh;
        if (nBaseStylePositon == 1) {
            darrayReturn[1] = serCalc.mnTop + serCalc.mnHeight * (dThresh + dLift);
        } else if (nBaseStylePositon == -1) {
            darrayReturn[1] = serCalc.mnTop + serCalc.mnHeight * (dThresh - dLift);
        } else  {
            darrayReturn[1] = darrayReturn[0]; 
        }
        return darrayReturn;
    }

    public static double[] calcLowerNoteUpperExtBoundThresh(StructExprRecog serBase, int nBaseStylePositon)   {
        StructExprRecog serCalc = serBase;
        if (/*serBase.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT
                || serBase.mnExprRecogType == EXPRRECOGTYPE_MULTIEXPRS
                || */serBase.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HLINECUT)   {
            serCalc = serBase.mlistChildren.getLast();
        }        
        double dThresh = 0.38, dLift = 0.05;
        double[] darrayReturn = new double[2];
        darrayReturn[0] = serCalc.mnTop + serCalc.mnHeight * dThresh;
        if (nBaseStylePositon == 1) {
            darrayReturn[1] = serCalc.mnTop + serCalc.mnHeight * (dThresh + dLift);
        } else if (nBaseStylePositon == -1) {
            darrayReturn[1] = serCalc.mnTop + serCalc.mnHeight * (dThresh - dLift);
        } else  {
            darrayReturn[1] = darrayReturn[0]; 
        }
        return darrayReturn;
    }
    
    public static double[] calcLowerNoteUpperBoundThresh(StructExprRecog serBase, int nBaseStylePositon)   {
        StructExprRecog serCalc = serBase;
        if (/*serBase.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT
                || serBase.mnExprRecogType == EXPRRECOGTYPE_MULTIEXPRS
                || */serBase.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HLINECUT)   {
            serCalc = serBase.mlistChildren.getLast();
        }        
        double dThresh = 0.45, dLift = 0.05;
        double[] darrayReturn = new double[2];
        darrayReturn[0] = serCalc.mnTop + serCalc.mnHeight * dThresh;
        if (nBaseStylePositon == 1) {
            darrayReturn[1] = serCalc.mnTop + serCalc.mnHeight * (dThresh + dLift);
        } else if (nBaseStylePositon == -1) {
            darrayReturn[1] = serCalc.mnTop + serCalc.mnHeight * (dThresh - dLift);
        } else  {
            darrayReturn[1] = darrayReturn[0]; 
        }
        return darrayReturn;
    }
    
    public static double[] calcLowerNoteLowerBoundThresh(StructExprRecog serBase, int nBaseStylePositon)   {
        StructExprRecog serCalc = serBase;
        if (/*serBase.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT
                || serBase.mnExprRecogType == EXPRRECOGTYPE_MULTIEXPRS
                || */serBase.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HLINECUT)   {
            serCalc = serBase.mlistChildren.getLast();
        }
        double dThresh = 0.75, dLift = 0.025;
        double[] darrayReturn = new double[2];
        darrayReturn[0] = serCalc.mnTop + serCalc.mnHeight * dThresh;
        if (nBaseStylePositon == 1) {
            darrayReturn[1] = serCalc.mnTop + serCalc.mnHeight * (dThresh + dLift);
        } else if (nBaseStylePositon == -1) {
            darrayReturn[1] = serCalc.mnTop + serCalc.mnHeight * (dThresh - dLift);
        } else  {
            darrayReturn[1] = darrayReturn[0]; 
        }
        return darrayReturn;
    }
    
    public static double[] calcLowerNoteLowerExtBoundThresh(StructExprRecog serBase, int nBaseStylePositon)   {
        StructExprRecog serCalc = serBase;
        if (/*serBase.mnExprRecogType == EXPRRECOGTYPE_HBLANKCUT
                || serBase.mnExprRecogType == EXPRRECOGTYPE_MULTIEXPRS
                || */serBase.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HLINECUT)   {
            serCalc = serBase.mlistChildren.getLast();
        }
        double dThresh = 1.05, dLift = 0.025;
        double[] darrayReturn = new double[2];
        darrayReturn[0] = serCalc.mnTop + serCalc.mnHeight * dThresh;
        if (nBaseStylePositon == 1) {
            darrayReturn[1] = serCalc.mnTop + serCalc.mnHeight * (dThresh + dLift);
        } else if (nBaseStylePositon == -1) {
            darrayReturn[1] = serCalc.mnTop + serCalc.mnHeight * (dThresh - dLift);
        } else  {
            darrayReturn[1] = darrayReturn[0]; 
        }
        return darrayReturn;
    }
}
