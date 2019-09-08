/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyzapps.mathrecog;

import com.cyzapps.imgmatrixproc.ImgMatrixOutput;
import com.cyzapps.imgproc.ImageMgr;
import com.cyzapps.mathrecog.UnitPrototypeMgr.UnitProtoType;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author tonyc
 */
public class UnitRecognizer {
    public static final int UNIT_RECOG_SHAPE_COMPARING_METHOD = 0;
    public static final int UNIT_RECOG_JNTPNT_COMPARING_METHOD = 1;
    public static final int UNIT_RECOG_LATTICEDENS_COMPARING_METHOD = 2;
    public static final int UNIT_RECOG_MINDISTANCE_COMPARING_METHOD = 3;
    public static final int NUMBER_OF_COMPARING_METHODS = 4;
    private static int count = 0;
    
    
    public static class UnitCandidate   {
        public static final double BEST_SIMILARITY_VALUE =  0.0;
        public static final double WORST_SIMILARITY_VALUE = 1.0;
        public static double convertRatio2Similarity(double dRatio) {
            return dRatio * (UnitCandidate.WORST_SIMILARITY_VALUE - UnitCandidate.BEST_SIMILARITY_VALUE) + UnitCandidate.BEST_SIMILARITY_VALUE;
        }
        public UnitProtoType mprotoType;
        public double[] mdarraySims = new double[NUMBER_OF_COMPARING_METHODS];
        public double mdOverallSimilarity = WORST_SIMILARITY_VALUE; // from 0 to 1, larger means less similar
        
        public UnitCandidate()  {
            Arrays.fill(mdarraySims, WORST_SIMILARITY_VALUE);
        }
        
        public static double calcOverallSimilarity(CharUnit charUnit, double[] darraySims)   {
            if (charUnit.getWidth() == 0 || charUnit.getHeight() == 0)  {
                return 0.0; // empty char unit
            }
            double dWOverH = (double)charUnit.getWidth() / (double)charUnit.getHeight();
            
            if (dWOverH <= 1.0/ConstantsMgr.msdExtendableCharWOverHThresh
                    || dWOverH >= ConstantsMgr.msdExtendableCharWOverHThresh)  {
                // special case for like 1, - [...
                return darraySims[UNIT_RECOG_SHAPE_COMPARING_METHOD] == BEST_SIMILARITY_VALUE?
                        ((darraySims[UNIT_RECOG_JNTPNT_COMPARING_METHOD] * ConstantsMgr.msdExtCharJntPntSimWeight
                        + darraySims[UNIT_RECOG_LATTICEDENS_COMPARING_METHOD] * ConstantsMgr.msdExtCharLatticeDenSimWeight
                        + darraySims[UNIT_RECOG_MINDISTANCE_COMPARING_METHOD] * ConstantsMgr.msdExtCharMinDisSimWeight))
                        : WORST_SIMILARITY_VALUE;
                
            } else  {
                return darraySims[UNIT_RECOG_SHAPE_COMPARING_METHOD] == BEST_SIMILARITY_VALUE?
                        ((darraySims[UNIT_RECOG_JNTPNT_COMPARING_METHOD] * ConstantsMgr.msdJntPntSimWeight
                        + darraySims[UNIT_RECOG_LATTICEDENS_COMPARING_METHOD] * ConstantsMgr.msdLatticeDenSimWeight
                        + darraySims[UNIT_RECOG_MINDISTANCE_COMPARING_METHOD] * ConstantsMgr.msdMinDisSimWeight))
                        : WORST_SIMILARITY_VALUE;
            }
        }
        
        public int compareTo(UnitCandidate uc2Compare)  {
            if (uc2Compare == null || mdOverallSimilarity > uc2Compare.mdOverallSimilarity)    {
                return 1;
            } else if (mdOverallSimilarity < uc2Compare.mdOverallSimilarity) {
                return -1;
            } else  {
                return 0;
            }
        }
        
        public String toString()    {
            return mprotoType.toString();
        }
    }
    
    public static UnitPrototypeMgr msUPTMgr = new UnitPrototypeMgr();
    public static UnitPrototypeMgr msUPTMgrPrint = new UnitPrototypeMgr();
    public static UnitPrototypeMgr msUPTMgrSPrint = new UnitPrototypeMgr();
    public static UnitPrototypeMgr msUPTMgrSHandwriting = new UnitPrototypeMgr();
    
    public static UnitProtoType getUPTFromSystem(UnitProtoType.Type unitType, int nFromList)   {
        LinkedList<UnitProtoType> listUPTs = msUPTMgr.findUnitPrototype(unitType, nFromList);
        if (listUPTs.size() > 0)    {
            return listUPTs.getFirst();
        } else  {
            return null;
        }
    }
    
    // returned candidate list should not be empty.
    public static LinkedList<UnitCandidate> recogChar(ImageChop imgChopOriginal, ImageChop imgChopThinned, double dAvgStrokeWidth, int nTotalOnCnt) throws IOException, InterruptedException {
        // step 1: quickly identify - and . from chop type
        // set overall similarity of the quick identifies to be ConstantsMgr.msdGoodRecogCharThresh
        // so that 1. need no further cut to recognize, 2. when comparing with cut, the comparison is fair.
        // TODO: need to calculate similarity, not a constant.
        LinkedList<UnitCandidate> listCandidates = new LinkedList<UnitCandidate>();
        double dWOverH = (double)imgChopOriginal.mnWidth/(double)imgChopOriginal.mnHeight;
        if (imgChopOriginal.mnChopType == ImageChop.TYPE_BLANK_DIV
                || (imgChopOriginal.mnWidth <= dAvgStrokeWidth * 0.5 && imgChopOriginal.mnHeight <= dAvgStrokeWidth * 2.0)  // these 2 lines seems to be useless
                || (imgChopOriginal.mnWidth <= dAvgStrokeWidth * 2.0 && imgChopOriginal.mnHeight <= dAvgStrokeWidth * 0.5)/*
                || (nTotalOnCnt > 0 && imgChopOriginal.mnWidth < dAvgStrokeWidth * 2.5 && imgChopOriginal.mnHeight < dAvgStrokeWidth * 2.5  // this condition leads to a lot of errors.
                    && (double)nTotalOnCnt / (double)(imgChopOriginal.mnWidth + imgChopOriginal.mnHeight) <= 0.5 * dAvgStrokeWidth)*/) {
            UnitCandidate unitCandidate = new UnitCandidate();
            unitCandidate.mprotoType = getUPTFromSystem(UnitProtoType.Type.TYPE_EMPTY, UnitPrototypeMgr.NORMAL_UPT_LIST);

            if (unitCandidate.mprotoType != null) {
                Arrays.fill(unitCandidate.mdarraySims, UnitCandidate.BEST_SIMILARITY_VALUE);
                unitCandidate.mdOverallSimilarity = ConstantsMgr.msdGoodRecogCharThresh;
                listCandidates.add(unitCandidate);

            }

            return listCandidates;  // only 1 candidate.
        } else if ((imgChopOriginal.mnHeight < 5.0 * dAvgStrokeWidth // was 1.5 * dAvgStrokeWidth, but this leads to a lot of misrecog (- to long sqrt).
                    && dWOverH >= ConstantsMgr.msdExtendableCharWOverHThresh)   // do not consider ~ (Wave) here.
                || (imgChopOriginal.mnHeight < 3.0 * dAvgStrokeWidth && imgChopOriginal.mnWidth > 2.0 * dAvgStrokeWidth   // do not consider ~ (Wave) here. Also note use mnHeight < 3.0 not mnWidth < 3.0 Avgstroke
                    && dWOverH >= ConstantsMgr.msdExtendableCharWOverHThresh / ConstantsMgr.msdCharWOverHMaxSkewRatio)
                || (imgChopOriginal.mnHeight <= 1.5 * dAvgStrokeWidth && imgChopOriginal.mnWidth >= 1.5 * imgChopOriginal.mnHeight
                    && imgChopOriginal.mnWidth / dAvgStrokeWidth > ConstantsMgr.msdExtendableCharWOverHThresh / ConstantsMgr.msdCharWOverHMaxSkewRatio))    {  // special treatment for very small characters.
            // there is another case, i.e. long sqrt, so do not add another condition :
            // || dWOverH >= ConstantsMgr.msdExtendableCharWOverHThresh * ConstantsMgr.msdCharWOverHMaxSkewRatio
            UnitCandidate unitCandidate = new UnitCandidate();
            unitCandidate.mprotoType = getUPTFromSystem(UnitProtoType.Type.TYPE_SUBTRACT, UnitPrototypeMgr.NORMAL_UPT_LIST);
            Arrays.fill(unitCandidate.mdarraySims, UnitCandidate.BEST_SIMILARITY_VALUE);
            unitCandidate.mdOverallSimilarity = ConstantsMgr.msdGoodRecogCharThresh;
            listCandidates.add(unitCandidate);

            return listCandidates;  // only 1 candidate.
        } else if ((imgChopOriginal.mnWidth < 2 * dAvgStrokeWidth
                    && (imgChopOriginal.mnWidth - dAvgStrokeWidth) / dWOverH < 0.5*ConstantsMgr.msdExtendableCharWOverHThresh * dAvgStrokeWidth
                    && dWOverH <= 1/ConstantsMgr.msdExtendableCharWOverHThresh)// consider the ratio, if very very long and thing and angle is very close to 90, very unlikely it is ( or [ or integrate.
                || (imgChopOriginal.mnHeight < 3.0 * dAvgStrokeWidth && imgChopOriginal.mnHeight > 2.0 * dAvgStrokeWidth    // if height <= 2.0 dAvgStrokeWidth then it is a point.
                    && dWOverH <= ConstantsMgr.msdCharWOverHMaxSkewRatio / ConstantsMgr.msdExtendableCharWOverHThresh)   // special treatment for small characters
                || (imgChopOriginal.mnWidth <= 1.5 * dAvgStrokeWidth && imgChopOriginal.mnWidth < imgChopOriginal.mnHeight
                    && dAvgStrokeWidth / imgChopOriginal.mnHeight < ConstantsMgr.msdCharWOverHMaxSkewRatio / ConstantsMgr.msdExtendableCharWOverHThresh))    {   // special treatment for very small characters
            UnitCandidate unitCandidate = new UnitCandidate();
            //todo : LH001
            unitCandidate.mprotoType = getUPTFromSystem(UnitProtoType.Type.TYPE_ONE, UnitPrototypeMgr.NORMAL_UPT_LIST);
            Arrays.fill(unitCandidate.mdarraySims, UnitCandidate.BEST_SIMILARITY_VALUE);
            unitCandidate.mdOverallSimilarity = ConstantsMgr.msdGoodRecogCharThresh;
            listCandidates.add(unitCandidate);

            return listCandidates;  // only 1 candidate.
        } else if (((imgChopOriginal.mnWidth + imgChopOriginal.mnHeight) <= 4 * dAvgStrokeWidth
                    && dWOverH < ConstantsMgr.msdExtendableCharWOverHThresh / ConstantsMgr.msdCharWOverHMaxSkewRatio // ensure W/H is close to 1 but not 1.
                    && dWOverH > ConstantsMgr.msdCharWOverHMaxSkewRatio / ConstantsMgr.msdExtendableCharWOverHThresh)
                || (imgChopOriginal.mnWidth <= 2 * dAvgStrokeWidth && imgChopOriginal.mnHeight <= 2 * dAvgStrokeWidth)
                || (nTotalOnCnt > ConstantsMgr.msdDotOnCnt2AreaMin * imgChopOriginal.mnWidth * imgChopOriginal.mnHeight
                    && imgChopOriginal.mnWidth < 2 * imgChopOriginal.mnHeight
                    && imgChopOriginal.mnWidth > 0.5 * imgChopOriginal.mnHeight)){
            UnitCandidate unitCandidate = new UnitCandidate();
            unitCandidate.mprotoType = getUPTFromSystem(UnitProtoType.Type.TYPE_DOT, UnitPrototypeMgr.NORMAL_UPT_LIST);
            Arrays.fill(unitCandidate.mdarraySims, UnitCandidate.BEST_SIMILARITY_VALUE);
            // dot cannot be further divided, even if further divided, still two dots
            // and assign it a larger overall similarity (worse than normal) so that
            // a char will not be futher cut to dots.
            unitCandidate.mdOverallSimilarity = UnitCandidate.WORST_SIMILARITY_VALUE;    //ConstantsMgr.msdGoodRecogCharThresh;
            listCandidates.add(unitCandidate);

            //int CaoLuLu = 1;
            //ImgMatrixOutput.createMatrixImage(imgChopOriginal.mbarrayImg, "\\test"+String.valueOf(CaoLuLu++)+".jpg");


            return listCandidates;  // only 1 candidate.
        }
        
        // step 1.5: identify 1 from 6 points.
        if (is1NotBoundChars(imgChopOriginal, dAvgStrokeWidth)) {
            UnitCandidate unitCandidate = new UnitCandidate();
            unitCandidate.mprotoType = getUPTFromSystem(UnitProtoType.Type.TYPE_ONE, UnitPrototypeMgr.NORMAL_UPT_LIST);
            Arrays.fill(unitCandidate.mdarraySims, UnitCandidate.BEST_SIMILARITY_VALUE);
            unitCandidate.mdOverallSimilarity = ConstantsMgr.msdGoodRecogCharThresh;
            listCandidates.add(unitCandidate);

            return listCandidates;  // only 1 candidate.
        }


        // step 2: identify special characters.
        CharUnit charUnitThinned = new CharUnit(imgChopThinned);
        if (charUnitThinned.getPntCount() == 0) {
            UnitCandidate unitCandidate = new UnitCandidate();
            unitCandidate.mprotoType = getUPTFromSystem(UnitProtoType.Type.TYPE_EMPTY, UnitPrototypeMgr.NORMAL_UPT_LIST);
            if (unitCandidate.mprotoType != null) {
                Arrays.fill(unitCandidate.mdarraySims, UnitCandidate.BEST_SIMILARITY_VALUE);
                unitCandidate.mdOverallSimilarity = ConstantsMgr.msdGoodRecogCharThresh;
                listCandidates.add(unitCandidate);
            }

            return listCandidates;  // no candidate.
        }
        else if (charUnitThinned.getPntCount() == 1)    {
            // this must be a dot
            UnitCandidate unitCandidate = new UnitCandidate();
            unitCandidate.mprotoType = getUPTFromSystem(UnitProtoType.Type.TYPE_DOT, UnitPrototypeMgr.NORMAL_UPT_LIST);
            Arrays.fill(unitCandidate.mdarraySims, UnitCandidate.BEST_SIMILARITY_VALUE);
            // dot cannot be further divided, even if further divided, still two dots
            // and assign it a larger overall similarity (worse than normal) so that
            // a char will not be futher cut to dots.
            unitCandidate.mdOverallSimilarity = UnitCandidate.WORST_SIMILARITY_VALUE;    //ConstantsMgr.msdGoodRecogCharThresh;
            listCandidates.add(unitCandidate);

            return listCandidates;
        }
        else if (charUnitThinned.getMaxX() == charUnitThinned.getMinX())    {
            // this must be a vertical line
            UnitCandidate unitCandidate = new UnitCandidate();
            unitCandidate.mprotoType = getUPTFromSystem(UnitProtoType.Type.TYPE_VERTICAL_LINE, UnitPrototypeMgr.NORMAL_UPT_LIST);
            Arrays.fill(unitCandidate.mdarraySims, UnitCandidate.BEST_SIMILARITY_VALUE);
            unitCandidate.mdOverallSimilarity = ConstantsMgr.msdGoodRecogCharThresh;
            listCandidates.add(unitCandidate);

            return listCandidates;
        }
        else if (charUnitThinned.getMaxY() == charUnitThinned.getMinY())   {
            UnitCandidate unitCandidate = new UnitCandidate();
            unitCandidate.mprotoType = getUPTFromSystem(UnitProtoType.Type.TYPE_SUBTRACT, UnitPrototypeMgr.NORMAL_UPT_LIST);
            Arrays.fill(unitCandidate.mdarraySims, UnitCandidate.BEST_SIMILARITY_VALUE);
            unitCandidate.mdOverallSimilarity = ConstantsMgr.msdGoodRecogCharThresh;
            listCandidates.add(unitCandidate);

            return listCandidates;
        }


        // step 3: recognize char units.
        double dExtendableCharWHThresh = ConstantsMgr.msdExtendableCharWOverHThresh * ConstantsMgr.msdCharWOverHGuaranteedExtRatio;
        LinkedList<UnitProtoType> listFromUPTs = msUPTMgr.mlistUnitPrototypes;
        LinkedList<UnitCandidate> listCands = new LinkedList<UnitCandidate>();
        
        double dJntPntMaxSimLmt = UnitCandidate.convertRatio2Similarity(0.5);
        if (dWOverH >= dExtendableCharWHThresh)    {
            listFromUPTs = msUPTMgr.mlistHExtendableUPTs;
            dJntPntMaxSimLmt = UnitCandidate.convertRatio2Similarity(0.8);
        } else if (dWOverH <= 1.0/dExtendableCharWHThresh)    {
            // note that because we thrink the candidate list set, w/h may not satify , so do not use recogCharMethod(charUnitThinned, UNIT_RECOG_JNTPNT_COMPARING_METHOD ... here
            listFromUPTs = msUPTMgr.mlistVExtendableUPTs;
            dJntPntMaxSimLmt = UnitCandidate.convertRatio2Similarity(0.8);
        } 
        
        double dWNumStrokes = imgChopOriginal.mnWidth/dAvgStrokeWidth;
        double dHNumStrokes = imgChopOriginal.mnHeight/dAvgStrokeWidth;


        //UnitCandidate candidate = myRecog(imgChopThinned.mbarrayImg);

        //listCands.add(candidate);



        CharUnit charUnitOriginal = new CharUnit(imgChopOriginal);


        listCands = recogCharMethod(charUnitOriginal, dWNumStrokes, dHNumStrokes,
                                    UNIT_RECOG_SHAPE_COMPARING_METHOD,
                                    null, listFromUPTs, UnitCandidate.convertRatio2Similarity(0.5), 100000);   //listFromUPTs should not be empty
//        System.out.println("1**"+listCands.get(0).mprotoType.toString());
        if (listCands.size() > 0)   {
//            System.out.println("2**"+listCands.get(0).mprotoType.toString());
            listCands = recogCharMethod(charUnitThinned, dWNumStrokes, dHNumStrokes,
                                        UNIT_RECOG_JNTPNT_COMPARING_METHOD,
                                        listCands, null, dJntPntMaxSimLmt, 100000); // the similarity limit might be different for extendable chars
            if (listCands.size() > 0)   {
//                System.out.println("3**"+listCands.get(0).mprotoType.toString());
                listCands = recogCharMethod(charUnitThinned, dWNumStrokes, dHNumStrokes,
                                            UNIT_RECOG_LATTICEDENS_COMPARING_METHOD,
                                            listCands, null, UnitCandidate.convertRatio2Similarity(0.5), 8);
                if (listCands.size() > 0)   {
//                    System.out.println("4**"+listCands.get(0).mprotoType.toString());
                    listCands = recogCharMethod(charUnitThinned, dWNumStrokes, dHNumStrokes,
                                                UNIT_RECOG_MINDISTANCE_COMPARING_METHOD,
                                                listCands, null, UnitCandidate.convertRatio2Similarity(0.9), 8);
                }
            }
        }


        /*
         * 
        if (listCands.size() > 0)   {
            UnitCandidate uc = listCands.get(0);
            System.out.println(uc.mprotoType.toString() + "\t" + uc.mprotoType.mstrFont + "\t" + uc.mdOverallSimilarity + "\t"
                    + uc.mdarraySims[UNIT_RECOG_SHAPE_COMPARING_METHOD] + "\t"
                    + uc.mdarraySims[] + "\t"UNIT_RECOG_JNTPNT_COMPARING_METHOD
                    + uc.mdarraySims[UNIT_RECOG_LATTICEDENS_COMPARING_METHOD] + "\t"
                    + uc.mdarraySims[UNIT_RECOG_MINDISTANCE_COMPARING_METHOD]);
        }
         * 
         */

        // now reorder listCands based on the recognition mode
        /*if (ExprRecognizer.msnRecognitionMode == 1) {
            // simple handwriting
            if (listCands.size() > 1 && listCands.getFirst().mprotoType.mnUnitType.compareTo(UnitProtoType.Type.TYPE_ZERO) < 0
                    && listCands.getFirst().mprotoType.mnUnitType.compareTo(UnitProtoType.Type.TYPE_NINE) > 0) {
                for (int idx = 1; idx < listCands.size(); idx ++) {
                    if (listCands.get(idx).mprotoType.mnUnitType.compareTo(UnitProtoType.Type.TYPE_ZERO) >= 0
                            && listCands.get(idx).mprotoType.mnUnitType.compareTo(UnitProtoType.Type.TYPE_NINE) <= 0
                            && listCands.get(idx).mdOverallSimilarity - ConstantsMgr.msdHandWritingLetterDisadv < listCands.getFirst().mdOverallSimilarity) {
                        // first candidate is not a number, while this candidate is a number, and they have close similarities.
                        // move the number to the beginning.
                        UnitCandidate uc = listCands.remove(idx);
                        listCands.addFirst(uc);
                        break;
                    }
                }
            }
        }*/
//        System.out.println("结束**"+listCands.get(0).mprotoType.toString());
        return listCands;
    }

    public static UnitCandidate myRecog(byte[][] matrix) throws IOException, InterruptedException {
        //首先使用ImgMatrixOutPut创建一个文件，然后调用python，
        String dir = "E:\\recomath\\final\\SNH\\Python\\Pretre\\Pretre\\data\\"+String.valueOf(count)+".jpg";
        //count += 1;
        ImgMatrixOutput.createMatrixImage(matrix, dir);
        UnitProtoType.Type rety = UnitProtoType.Type.TYPE_UNKNOWN;
        //String filePath1 = "E:\\code\\python\\Pretre\\Pretre\\reSize.py";
        String filePath = "E:\\recomath\\final\\SNH\\Python\\Pretre\\Pretre\\new_1.py";
        Process proc;
        try {
            //尝试首先将图片缩小再腐蚀
            //String[] Statement1 = new String[]{"C:\\Users\\ASUS\\AppData\\Local\\Programs\\Python\\Python35\\python", filePath1};

            //testThinImage("E:\\recomath\\final\\SNH\\Python\\Pretre\\Pretre\\data", "E:\\recomath\\final\\SNH\\Python\\Pretre\\Pretre\\dataAfterthin");

            //System.path.add("C:\\Users\\ASUS\\AppData\\Local\\Programs\\Python\\Python36\\Lib\\site-packages");
            String[] Statement = new String[]{"C:\\Users\\ASUS\\AppData\\Local\\Programs\\Python\\Python35\\python",filePath};
            //System.out.println(Statement[0] + Statement[1]);
            proc = Runtime.getRuntime().exec(Statement);

            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;

            while ((line = in.readLine()) != null) {
                line = line.toLowerCase();
                System.out.print(line);
                switch(line){
                    case "infty": rety = UnitProtoType.getmningTypeValue("\\infinite");
                    case "alpha": rety = UnitProtoType.getmningTypeValue("\\alpha");
                    case "beta": rety = UnitProtoType.getmningTypeValue("\\beta");
                    case "ascii_124": rety = UnitProtoType.getmningTypeValue("/");
                    case "div": rety = UnitProtoType.getmningTypeValue("\\div");
                    case "delta": rety = UnitProtoType.getmningTypeValue("\\Delta");
                        //case "exists": rety = UnitProtoType.getmningTypeValue("\\infinite");
                        //case "forall": rety = UnitProtoType.getmningTypeValue("\\infinite");
                    case "forward_slash": rety = UnitProtoType.getmningTypeValue("/");
                    case "gamma": rety = UnitProtoType.getmningTypeValue("\\gamma");
                        //case "geq": rety = UnitProtoType.getmningTypeValue("\\infinite");
                        //case "gt": rety = UnitProtoType.getmningTypeValue("\\infinite");
                        //case "in": rety = UnitProtoType.getmningTypeValue("\\infinite");
                    case "int": rety = UnitProtoType.getmningTypeValue("\\integrate");
                    case "lambda": rety = UnitProtoType.getmningTypeValue("\\lambda");
                        //case "leq": rety = UnitProtoType.getmningTypeValue("\\infinite");
                        //case "lt": rety = UnitProtoType.getmningTypeValue("\\infinite");
                    case "mu": rety = UnitProtoType.getmningTypeValue("\\mu");
                        //case "neq": rety = UnitProtoType.getmningTypeValue("\\infinite");
                    case "phi": rety = UnitProtoType.getmningTypeValue("\\phi");
                    case "pi": rety = UnitProtoType.getmningTypeValue("\\pi");
                        //case "pm": rety = UnitProtoType.getmningTypeValue("\\infinite");
                        //case "rightarrow": rety = UnitProtoType.getmningTypeValue("\\infinite");
                    case "sigma": rety = UnitProtoType.getmningTypeValue("\\sigma");
                        //case "sum": rety = UnitProtoType.getmningTypeValue("\\infinite");
                    case "theta": rety = UnitProtoType.getmningTypeValue("\\theta");
                    case "times": rety = UnitProtoType.getmningTypeValue("\\times");
                    case "{": rety = UnitProtoType.getmningTypeValue("{");
                    case "}": rety = UnitProtoType.getmningTypeValue("}");
                    case "!": rety = UnitProtoType.getmningTypeValue("!");
                    case "(": rety = UnitProtoType.getmningTypeValue("(");
                    case ")": rety = UnitProtoType.getmningTypeValue(")");
                    case "+": rety = UnitProtoType.getmningTypeValue("+");
                    case "[": rety = UnitProtoType.getmningTypeValue("[");
                    case "]": rety = UnitProtoType.getmningTypeValue("]");
                    case "=": rety = UnitProtoType.getmningTypeValue("=");
                    case "-": rety = UnitProtoType.getmningTypeValue("-");
                    default:
                        rety = UnitProtoType.getmningTypeValue(line);
                }
            }
            in.close();
            proc.waitFor();
            //boolean success = (new File(dir)).delete();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UnitProtoType retype = new UnitProtoType();
        retype.mnUnitType = rety;
        UnitCandidate candidate = new UnitCandidate();
        candidate.mprotoType = retype;
        candidate.mdOverallSimilarity = 0;//ifUnit(imgChopThinned.mbarrayImg);;
        return candidate;
    }

    public static int ifUnit(byte[][] matrix){
        int height  = matrix.length;
        int width = matrix[1].length;
        int[] counts = new int[height];
        int jutis = 0;
        for(int i=0; i<height; ++i){
            counts[i] = 0;
            for(int j=0; j<width; ++j){
                if(matrix[i][j] != 0){
                    System.out.print("*");
                    counts[i] += 1;
                }
                else
                    System.out.print(" ");
            }
            System.out.println(" " + counts[i]);
            if(counts[i] != 0){
                if(jutis == 0){
                    jutis = 1;
                    continue;
                }
                else if(jutis == 2){
                    return 1;
                }
            }
            else{
                if(jutis == 1){
                    jutis = 2;
                }
            }
        }
        System.out.println("is unit");
        return 0;
    }

    public static void testThinImage(String strSrcFolder, String strDestFolder) throws InterruptedException  {
        System.out.println("Now test thinning image algorithm, source image folder is " + strSrcFolder + ", destination folder is " + strDestFolder);
        File folder = new File(strSrcFolder);
        for (File fProtoType : folder.listFiles())  {
            if (fProtoType.isFile())    {
                String strFileName = fProtoType.getName();
                String strFilePath = fProtoType.getPath();
                BufferedImage image = ImageMgr.readImg(strFilePath);
                byte[][] biMatrix = ImageMgr.convertImg2BiMatrix(image);
                ImageChop imgChop = new ImageChop();
                imgChop.setImageChop(biMatrix, 0, 0, biMatrix.length, biMatrix[0].length, ImageChop.TYPE_UNKNOWN);
                imgChop = StrokeFinder.thinImageChop(imgChop, true);
                BufferedImage image_thinned = ImageMgr.convertBiMatrix2Img(imgChop.mbarrayImg);
                ImageMgr.saveImg(image_thinned, strDestFolder + File.separator + strFileName);
            }
        }
        return;
    }

    public static boolean is1NotBoundChars(ImageChop chop, double dAvgStrokeWidth) {
        if (chop.mnWidth < ConstantsMgr.msdHard2Identify1BndCharWidthThresh * dAvgStrokeWidth   // except bound char and 1, other chars width >= 3 avg stroke width.
                && (double)chop.mnWidth/(double)chop.mnHeight >= 1/ConstantsMgr.msdExtendableCharWOverHThresh
                && (double)chop.mnWidth/(double)chop.mnHeight <= ConstantsMgr.msd1WOverHLThresh) {  // if w/h > msd1WOverHLThresh, it is easy to recog bound char using traditonal method.
            int nTopRangeFrom = chop.mnTop;
            int nRndStrokeWTop = Math.max((int)(1.5*dAvgStrokeWidth),1);    // the knot on top of 1 cannot be wider than 1.5 avg stroke width.
            int nRndStrokeWBtm = Math.max((int)dAvgStrokeWidth,1);
            int nTopRangeTo = chop.mnTop + nRndStrokeWTop;
            int nMidRangeFrom = (int)Math.floor(chop.mnTop + chop.mnHeight/3.0);
            int nMidRangeTo = (int)Math.ceil(chop.mnTop + chop.mnHeight/1.5);
            int nBtmRangeFrom = chop.getBottomPlus1() - nRndStrokeWBtm;
            int nBtmRangeTo = chop.getBottomPlus1();
            int nTopRangeLeftMost = -1, nTopRangeRightMost = -1, nMidRangeLeftMost = -1, nMidRangeRightMost = -1, nBtmRangeLeftMost = -1, nBtmRangeRightMost = -1;
            for (int idx1 = chop.mnLeft; idx1 < chop.getRightPlus1(); idx1 ++) {
                for (int idx = nTopRangeFrom; idx < nTopRangeTo; idx ++) {
                    if (chop.mbarrayImg[idx1][idx] == 1) {
                        nTopRangeLeftMost = idx1;
                        break;
                    }
                }
                if (nTopRangeLeftMost >= 0) {
                    break;
                }
            }
            for (int idx1 = chop.getRight(); idx1 >= chop.mnLeft; idx1 --) {
                for (int idx = nTopRangeFrom; idx < nTopRangeTo; idx ++) {
                    if (chop.mbarrayImg[idx1][idx] == 1) {
                        nTopRangeRightMost = idx1;
                        break;
                    }
                }
                if (nTopRangeRightMost >= 0) {
                    break;
                }
            }
            for (int idx1 = chop.mnLeft; idx1 < chop.getRightPlus1(); idx1 ++) {
                for (int idx = nMidRangeFrom; idx < nMidRangeTo; idx ++) {
                    if (chop.mbarrayImg[idx1][idx] == 1) {
                        nMidRangeLeftMost = idx1;
                        break;
                    }
                }
                if (nMidRangeLeftMost >= 0) {
                    break;
                }
            }
            for (int idx1 = chop.getRight(); idx1 >= chop.mnLeft; idx1 --) {
                for (int idx = nMidRangeFrom; idx < nMidRangeTo; idx ++) {
                    if (chop.mbarrayImg[idx1][idx] == 1) {
                        nMidRangeRightMost = idx1;
                        break;
                    }
                }
                if (nMidRangeRightMost >= 0) {
                    break;
                }
            }
            for (int idx1 = chop.mnLeft; idx1 < chop.getRightPlus1(); idx1 ++) {
                for (int idx = nBtmRangeFrom; idx < nBtmRangeTo; idx ++) {
                    if (chop.mbarrayImg[idx1][idx] == 1) {
                        nBtmRangeLeftMost = idx1;
                        break;
                    }
                }
                if (nBtmRangeLeftMost >= 0) {
                    break;
                }
            }
            for (int idx1 = chop.getRight(); idx1 >= chop.mnLeft; idx1 --) {
                for (int idx = nBtmRangeFrom; idx < nBtmRangeTo; idx ++) {
                    if (chop.mbarrayImg[idx1][idx] == 1) {
                        nBtmRangeRightMost = idx1;
                        break;
                    }
                }
                if (nBtmRangeRightMost >= 0) {
                    break;
                }
            }
            
            int nLeft = chop.mnLeft;
            int nRight = chop.getRight();
            
            if ((nRight == nTopRangeRightMost || nRight == nBtmRangeRightMost)
                    && (nLeft == nBtmRangeLeftMost
                        || (nLeft == nTopRangeLeftMost
                            && nLeft < nBtmRangeLeftMost))
                    && (nBtmRangeRightMost - nBtmRangeLeftMost) >= (nMidRangeRightMost - nMidRangeLeftMost)
                    && (nTopRangeRightMost - nTopRangeLeftMost) >= (nMidRangeRightMost - nMidRangeLeftMost)) {
                // this can be 1.
                /*  some 1 image is not so standard.
                if (nTopLeftOverhead > 0 && nBtmLeftOverhead > 0 && nBtmRightOverhead > 0
                        && nTopLeftOverhead >= nTopRightOverhead * ConstantsMgr.msd1TopOverheadsRatio
                        && nTopLeftOverhead * ConstantsMgr.msd1TopBtmOverheadsRatio <= nBtmLeftOverhead
                        && nTopLeftOverhead >= nBtmLeftOverhead * ConstantsMgr.msd1TopBtmOverheadsRatio
                        && nBtmLeftOverhead >= nBtmRightOverhead * ConstantsMgr.msd1BtmOverheadsRatio
                        && nBtmLeftOverhead * ConstantsMgr.msd1BtmOverheadsRatio <= nBtmRightOverhead) {
                    return true;
                } else if (nTopLeftOverhead > 0
                        && nTopLeftOverhead >= nTopRightOverhead * ConstantsMgr.msd1TopOverheadsRatio
                        && nTopLeftOverhead >= nBtmLeftOverhead * ConstantsMgr.msd1TopOverheadsRatio
                        && nTopLeftOverhead >= nBtmRightOverhead * ConstantsMgr.msd1TopOverheadsRatio) {
                    return true;
                }*/
                if (nBtmRangeRightMost > nTopRangeRightMost && nBtmRangeLeftMost < nMidRangeLeftMost) {
                    int nTopLeftOverhead = nMidRangeLeftMost - nTopRangeLeftMost;
                    int nBtmLeftOverhead = nMidRangeLeftMost - nBtmRangeLeftMost;
                    int nBtmRightOverhead = nBtmRangeRightMost - nMidRangeRightMost;
                    if (nTopLeftOverhead > nBtmLeftOverhead && nBtmLeftOverhead * ConstantsMgr.msd1BtmOverheadsRatio > nBtmRightOverhead) {
                        return false;   // this seems to be a ]
                    } else {
                        return true;
                    }
                } else if (nBtmRangeRightMost == nTopRangeRightMost && nBtmRangeLeftMost == nMidRangeLeftMost && nTopRangeLeftMost < nMidRangeLeftMost) {
                    return true;
                } else if (chop.mnWidth <= 2 * dAvgStrokeWidth) {
                    return true;
                }
            }
        }   
        return false;
    }
    // returned a candidate which includes similarity.
    public static LinkedList<UnitCandidate> recogCharWord(ImageChop imgChop, UnitProtoType.Type unitType) throws InterruptedException  {
        // step 0, find unitType
        LinkedList<UnitProtoType> listUpts = msUPTMgr.findUnitPrototype(unitType, UnitPrototypeMgr.WORD_UPT_LIST);
        LinkedList<UnitCandidate> listUCs = new LinkedList<UnitCandidate>();
        if (listUpts.size() == 0) {
            // cannot find the prototype.
            UnitCandidate candidate = new UnitCandidate();
            candidate.mprotoType = new UnitProtoType();
            Arrays.fill(candidate.mdarraySims, UnitCandidate.WORST_SIMILARITY_VALUE);    // 1.0 means worst similarity
            candidate.mdOverallSimilarity = UnitCandidate.WORST_SIMILARITY_VALUE;
            return listUCs;   // ok, this cannot be the word
        }
        
		// step 1, calculate average stroke width
		int nTotalOnCount = imgChop.getTotalOnCount();
		int nTotalOnCountInSkeleton = imgChop.getTotalOnCountInSkeleton(true);
		double dAvgStrokeWidth = 1;  // minum stroke width is 1.
		if (nTotalOnCountInSkeleton != 0 && nTotalOnCount != 0)   {
			dAvgStrokeWidth = (double)nTotalOnCount/(double)nTotalOnCountInSkeleton;
		} else if (nTotalOnCount != 0)    {
			dAvgStrokeWidth = Math.sqrt((double)nTotalOnCount);
		}
		
		// step 2, see if height and width satisfy upt requirement
		double dWNumStrokes = imgChop.mnWidth/dAvgStrokeWidth;
		double dHNumStrokes = imgChop.mnHeight/dAvgStrokeWidth;

        for (UnitProtoType upt: listUpts)   {
            UnitCandidate candidate = new UnitCandidate();
            candidate.mprotoType = upt;
            CharUnit charUnit = new CharUnit(imgChop);
            if (dWNumStrokes < upt.mdWMinNumStrokes || dHNumStrokes < upt.mdHMinNumStrokes
                    || upt.mcharUnit.compareToShape(charUnit) != UnitCandidate.BEST_SIMILARITY_VALUE)    {
                // need not to check if upt.mdWMinNumStrokes is 0 or not coz upt.mdWMinNumStrokes
                // has been greater than a positive value
                // W/H should not be beyond extendable char wh thresh.
                Arrays.fill(candidate.mdarraySims, UnitCandidate.WORST_SIMILARITY_VALUE);    // 1.0 means worst similarity
                candidate.mdOverallSimilarity = UnitCandidate.WORST_SIMILARITY_VALUE;
            } else {
                ImageChop imgChopThinned = StrokeFinder.thinImageChop(imgChop, true);
                CharUnit charUnitThinned = new CharUnit(imgChopThinned);
                candidate.mdarraySims[UNIT_RECOG_SHAPE_COMPARING_METHOD] = UnitCandidate.BEST_SIMILARITY_VALUE;
                candidate.mdarraySims[UNIT_RECOG_JNTPNT_COMPARING_METHOD] = upt.mcharUnit.compareToCountJntPnts(charUnitThinned);
                candidate.mdarraySims[UNIT_RECOG_LATTICEDENS_COMPARING_METHOD] = upt.mcharUnit.compareToLatticeDensity(charUnitThinned, 6, 3, 1);
                candidate.mdarraySims[UNIT_RECOG_MINDISTANCE_COMPARING_METHOD] = upt.mcharUnit.compareToMinDistance(charUnit);
                candidate.mdOverallSimilarity = UnitCandidate.calcOverallSimilarity(charUnit, candidate.mdarraySims);
            }
            boolean bAdded = false;
            for (UnitCandidate uc: listUCs) {
                if (candidate.mdOverallSimilarity < uc.mdOverallSimilarity) {
                    listUCs.add(listUCs.indexOf(uc), candidate);
                    bAdded = true;
                    break;
                }
            }
            if (!bAdded)    {
                listUCs.add(candidate);
            }
        }
        return listUCs;
    }
    
    // only if listCandsInput is null or empty, use listFromUPTs to select candidates.
    public static LinkedList<UnitCandidate> recogCharMethod(CharUnit charUnit, double dWNumStrokes, double dHNumStrokes, int nMethod, LinkedList<UnitCandidate> listCandsInput,
                                                            LinkedList<UnitProtoType> listFromUPTs, double dMaxSimilarity, int nNumOfCands)  {
        LinkedList<UnitCandidate> listCandidates = new LinkedList<UnitCandidate>();
        if (listCandsInput == null || listCandsInput.size() == 0) {
            for (UnitProtoType upt : listFromUPTs)  {
                if (upt.mnUnitType == UnitProtoType.Type.TYPE_UNKNOWN || upt.mnUnitType == UnitProtoType.Type.TYPE_EMPTY)   {
                    continue;
                }

                double[] darraySims = new double[NUMBER_OF_COMPARING_METHODS];
                Arrays.fill(darraySims, UnitCandidate.WORST_SIMILARITY_VALUE);

                switch(nMethod) {
                    case UNIT_RECOG_SHAPE_COMPARING_METHOD:
                        if (dWNumStrokes >= upt.mdWMinNumStrokes &&  dHNumStrokes >= upt.mdHMinNumStrokes)  {
                            // width and height should be at least upt.mdWMinNumStrokes and upt.mdHMinNumStrokes respectively.
                            darraySims[nMethod] = upt.mcharUnit.compareToShape(charUnit);
                        } else  {
                            darraySims[nMethod] = UnitCandidate.WORST_SIMILARITY_VALUE;
                        }
                        break;
                    case UNIT_RECOG_JNTPNT_COMPARING_METHOD:
                        darraySims[nMethod] = upt.mcharUnit.compareToCountJntPnts(charUnit);
                        break;
                    case UNIT_RECOG_LATTICEDENS_COMPARING_METHOD:
                        darraySims[nMethod] = upt.mcharUnit.compareToLatticeDensity(charUnit, 6, 3, 1);   // have to use charUnit as parameter because all the prototypes can be normlized.
                        break;
                    case UNIT_RECOG_MINDISTANCE_COMPARING_METHOD:
                        darraySims[nMethod] = upt.mcharUnit.compareToMinDistance(charUnit);
                        break;
                }
                double dOverallSimilarity = UnitCandidate.calcOverallSimilarity(charUnit, darraySims);
                int idx = 0;
                for (idx = 0; idx < listCandidates.size(); idx ++) {
                    if (dOverallSimilarity < listCandidates.get(idx).mdOverallSimilarity)    {
                        UnitCandidate candidate = new UnitCandidate();
                        candidate.mprotoType = upt;
                        candidate.mdarraySims = darraySims;
                        candidate.mdOverallSimilarity = dOverallSimilarity;
                        listCandidates.add(idx, candidate);
                        break;
                    }
                }
                if (listCandidates.size() < nNumOfCands && darraySims[nMethod] <= dMaxSimilarity)    {
                    if (idx == listCandidates.size())    {
                        UnitCandidate candidate = new UnitCandidate();
                        candidate.mprotoType = upt;
                        candidate.mdarraySims = darraySims;
                        candidate.mdOverallSimilarity = dOverallSimilarity;
                        listCandidates.add(idx, candidate);
                    }
                } else {
                    while (listCandidates.size() > nNumOfCands
                             || (listCandidates.size() > 0
                                && listCandidates.getLast().mdarraySims[nMethod] > dMaxSimilarity)) {
                        listCandidates.removeLast();
                    }
                }
            }
//            System.out.println("method1："+listCandidates.get(0).mprotoType.toString());
        }
        else  {
            for (UnitCandidate uc : listCandsInput)  {
                if (uc.mprotoType.mnUnitType == UnitProtoType.Type.TYPE_UNKNOWN || uc.mprotoType.mnUnitType == UnitProtoType.Type.TYPE_EMPTY)   {
                    continue;
                }
                double[] darraySims = uc.mdarraySims;
                switch(nMethod) {
                    case UNIT_RECOG_SHAPE_COMPARING_METHOD:
                        if (dWNumStrokes >= uc.mprotoType.mdWMinNumStrokes &&  dHNumStrokes >= uc.mprotoType.mdHMinNumStrokes)  {
                            // width and height should be at least uc.mprotoType.mdWMinNumStrokes and uc.mprotoType.mdHMinNumStrokes respectively.
                            darraySims[nMethod] = uc.mprotoType.mcharUnit.compareToShape(charUnit);
                        } else  {
                            darraySims[nMethod] = UnitCandidate.WORST_SIMILARITY_VALUE;  //shape doesnt match requirements.
                        }
                        break;
                    case UNIT_RECOG_JNTPNT_COMPARING_METHOD:
                        darraySims[nMethod] = uc.mprotoType.mcharUnit.compareToCountJntPnts(charUnit);
                        break;
                    case UNIT_RECOG_LATTICEDENS_COMPARING_METHOD:
                        darraySims[nMethod] = uc.mprotoType.mcharUnit.compareToLatticeDensity(charUnit, 6, 3, 1);   // have to use charUnit as parameter because all the prototypes can be normlized.
                        break;
                    case UNIT_RECOG_MINDISTANCE_COMPARING_METHOD:
                        darraySims[nMethod] = uc.mprotoType.mcharUnit.compareToMinDistance(charUnit);
                        break;
                }
                double dOverallSimilarity = UnitCandidate.calcOverallSimilarity(charUnit, darraySims);
                int idx = 0;
                for (idx = 0; idx < listCandidates.size(); idx ++) {
                    if (dOverallSimilarity < listCandidates.get(idx).mdOverallSimilarity)    {
                        UnitCandidate candidate = new UnitCandidate();
                        candidate.mprotoType = uc.mprotoType;
                        candidate.mdarraySims = darraySims;
                        candidate.mdOverallSimilarity = dOverallSimilarity;
                        listCandidates.add(idx, candidate);
                        break;
                    }
                }
                if (listCandidates.size() < nNumOfCands && darraySims[nMethod] <= dMaxSimilarity)    {
                    if (idx == listCandidates.size())    {
                        UnitCandidate candidate = new UnitCandidate();
                        candidate.mprotoType = uc.mprotoType;
                        candidate.mdarraySims = darraySims;
                        candidate.mdOverallSimilarity = dOverallSimilarity;
                        listCandidates.add(idx, candidate);
                    }
                } else {
                    while (listCandidates.size() > nNumOfCands
                             || (listCandidates.size() > 0
                                && listCandidates.getLast().mdarraySims[nMethod] > dMaxSimilarity)) {
                        listCandidates.removeLast();
                    }
                }
            }
//            System.out.println("method2："+listCandidates.get(0).mprotoType.toString());
        }
        return listCandidates;
    }
}
