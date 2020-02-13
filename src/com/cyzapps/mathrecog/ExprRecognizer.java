/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyzapps.mathrecog;

import com.cyzapps.imgmatrixproc.ImgMatrixOutput;
import com.cyzapps.mathrecog.UnitPrototypeMgr.UnitProtoType;
import com.cyzapps.mathrecog.UnitRecognizer.UnitCandidate;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import static com.cyzapps.mathrecog.StructExprRecog.isNumberChar;

//IronMan_test 测试新的分支
/**
 * @author tonyc
 * recognization steps:
 * 1. cut horizontally;
 * 2. cut vertically;
 * 3. recognize characters that can be recognized;
 * 4. extract to recognize
 * 5. disconnect to recognize.
 */
public class ExprRecognizer {
    public static final int MAX_RECOGNIZING_STACK_COUNT = 20;
    public static final String TOO_DEEP_CALL_STACK = "Too deep call stack";

    public static final int RECOG_PRINT_MODE = 0;
    public static final int RECOG_SPRINT_MODE = 1;
    public static final int RECOG_SHANDWRITING_MODE = 2;

    // RECOG_PRINT_MODE means recognize printed expression,
    // RECOG_SPRINT_MODE means recognize simple printed expression,
    // RECOG_SHANDWRITING_MODE means recognize simple handingwriting.
    protected static int msnRecognitionMode = RECOG_PRINT_MODE;

    //设置识别模型（UnitRecognizer——————鹿哥内容）
    public static void setRecognitionMode(int nRecognitionMode) {
        msnRecognitionMode = nRecognitionMode;
        if (msnRecognitionMode == RECOG_PRINT_MODE) {
            UnitRecognizer.msUPTMgr = UnitRecognizer.msUPTMgrPrint;
        } else if (msnRecognitionMode == RECOG_SPRINT_MODE) {
            UnitRecognizer.msUPTMgr = UnitRecognizer.msUPTMgrSPrint;
        } else if (msnRecognitionMode == RECOG_SHANDWRITING_MODE) {
            UnitRecognizer.msUPTMgr = UnitRecognizer.msUPTMgrSHandwriting;
        }
    }

    public static int getRecognitionMode() {
        return msnRecognitionMode;
    }


    public static class ExprRecognizeException extends Exception {
        public ExprRecognizeException() {
            super();
        }

        public ExprRecognizeException(String strReason) {
            super(strReason);
        }
    }

    //分析水平切块，水平切块会分成上下，或上中下结构，所以需要确定DIV(分割线) 的位置,从而判断类型。进一步决定ＤＩＶ是作为分数线保留，还是当成上划线或下划线丢弃
    public static StructExprRecog analyzeHCuts(ImageChops imgChops, int nOriginalStartIdx, int nOriginalEndIdx, double dAvgStrokeWidth, int nStackLvl) throws ExprRecognizeException, InterruptedException, IOException {
        if (nStackLvl >= MAX_RECOGNIZING_STACK_COUNT) {
            throw new ExprRecognizeException(TOO_DEEP_CALL_STACK);
        }
        // all the horizontal divs here are line divs
        //这里预先定义了分数线的索引IDX,长度，作用域nStartIdx-nEndIdx
        int nMajorLnDivIdx = -1, nMajorLnDivLen = -1;
        int nStartIdx = -1, nEndIdx = -1;
        for (int idx = nOriginalStartIdx; idx <= nOriginalEndIdx; idx++) { // top or bottom line div should not be divide char.
            ImageChop imgChop = imgChops.mlistChops.get(idx);
            if (imgChop.mnChopType != ImageChop.TYPE_BLANK_DIV &&
                    imgChop.mnChopType != ImageChop.TYPE_CAP_DIV &&
                    imgChop.mnChopType != ImageChop.TYPE_UNDER_DIV) {
                nStartIdx = idx;
                break;
            }
        }

        for (int idx = nOriginalEndIdx; idx >= nOriginalStartIdx; idx--) {
            ImageChop imgChop = imgChops.mlistChops.get(idx);
            if (imgChop.mnChopType != ImageChop.TYPE_BLANK_DIV &&
                    imgChop.mnChopType != ImageChop.TYPE_CAP_DIV &&
                    imgChop.mnChopType != ImageChop.TYPE_UNDER_DIV) {
                nEndIdx = idx;
                break;
            }
        }

        StructExprRecog serReturn = new StructExprRecog(imgChops.mlistChops.get(nStartIdx).mbarrayOriginalImg);

        if (nStartIdx == nEndIdx) {
            //就一个，直接递归下去
            serReturn = recognize(imgChops.mlistChops.get(nStartIdx), imgChops, 0, dAvgStrokeWidth, nStackLvl + 1);
            return serReturn;
        }
        else {
            //获取当前切块序列（nStartIdx -> nEndIdx) 的最大边界
            int nLeft = Integer.MAX_VALUE, nTop = Integer.MAX_VALUE, nRightP1 = Integer.MIN_VALUE, nBottomP1 = Integer.MIN_VALUE;
            for (int idx = nStartIdx; idx <= nEndIdx; idx++) {
                ImageChop chopThis = imgChops.mlistChops.get(idx);
                if (chopThis.getLeftInOriginalImg() < nLeft) {
                    nLeft = chopThis.getLeftInOriginalImg();
                }
                if (chopThis.getTopInOriginalImg() < nTop) {
                    nTop = chopThis.getTopInOriginalImg();
                }
                if (chopThis.getRightP1InOriginalImg() > nRightP1) {
                    nRightP1 = chopThis.getRightP1InOriginalImg();
                }
                if (chopThis.getBottomP1InOriginalImg() > nBottomP1) {
                    nBottomP1 = chopThis.getBottomP1InOriginalImg();
                }
            }
            if (nLeft != Integer.MAX_VALUE && nTop != Integer.MAX_VALUE && nRightP1 != Integer.MIN_VALUE && nBottomP1 != Integer.MIN_VALUE) {
                serReturn.setSERPlace(nLeft, nTop, nRightP1 - nLeft, nBottomP1 - nTop);
            }
        }

        // 关键还是要在这里识别出 TYPE_LINE_DIV 类型
        //先在这里找到分数线的长度，默认是最长的那个imgchop的宽度
        for (int idx = nStartIdx; idx <= nEndIdx; idx++) {
            ImageChop imgChop = imgChops.mlistChops.get(idx);
            if (imgChop.mnChopType == ImageChop.TYPE_LINE_DIV && nMajorLnDivLen < imgChop.mnWidth) {
                nMajorLnDivLen = imgChop.mnWidth;
                nMajorLnDivIdx = idx;
            }
        }
        //#1 这里就已经不是分数线了
        //没有识别类型是LINE_DIV的
        if (nMajorLnDivIdx == -1) {
            // this is a unit with cap or under, no line divs. If it is a cap/under, always pass average stroke width
            // because unlikely cap under will be small and thing characters (except star and dot, but the width of 
            // star and dot should be similar to base char's width).
            if (imgChops.mlistChops.get(nEndIdx - 1).mnChopType == ImageChop.TYPE_UNDER_DIV) {
                StructExprRecog serBase = analyzeHCuts(imgChops, nStartIdx, nEndIdx - 1, dAvgStrokeWidth, nStackLvl + 1);
                StructExprRecog serUnder = recognize(imgChops.mlistChops.get(nEndIdx), imgChops, 0, dAvgStrokeWidth, nStackLvl + 1);
                LinkedList<StructExprRecog> listSers = new LinkedList<StructExprRecog>();
                if (serBase.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTCAP) {
                    listSers.addAll(serBase.mlistChildren);
                    listSers.add(serUnder);
                    serReturn.setStructExprRecog(listSers, StructExprRecog.EXPRRECOGTYPE_HCUTCAPUNDER);
                } else {
                    listSers.add(serBase);
                    listSers.add(serUnder);
                    serReturn.setStructExprRecog(listSers, StructExprRecog.EXPRRECOGTYPE_HCUTUNDER);
                }
            } else if (imgChops.mlistChops.get(nStartIdx + 1).mnChopType == ImageChop.TYPE_CAP_DIV) {
                StructExprRecog serCap = recognize(imgChops.mlistChops.get(nStartIdx), imgChops, 0, dAvgStrokeWidth, nStackLvl + 1);
                StructExprRecog serBase = analyzeHCuts(imgChops, nStartIdx + 1, nEndIdx, dAvgStrokeWidth, nStackLvl + 1);
                LinkedList<StructExprRecog> listSers = new LinkedList<StructExprRecog>();
                if (serBase.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTUNDER) {
                    listSers.add(serCap);
                    listSers.addAll(serBase.mlistChildren);
                    serReturn.setStructExprRecog(listSers, StructExprRecog.EXPRRECOGTYPE_HCUTCAPUNDER);
                } else {
                    listSers.add(serCap);
                    listSers.add(serBase);
                    serReturn.setStructExprRecog(listSers, StructExprRecog.EXPRRECOGTYPE_HCUTCAP);
                }
            } else {
                LinkedList<StructExprRecog> listSers = new LinkedList<StructExprRecog>();
                for (int idx = nStartIdx; idx <= nEndIdx; idx++) {
                    StructExprRecog ser = recognize(imgChops.mlistChops.get(idx), imgChops, 0, dAvgStrokeWidth, nStackLvl + 1);
                    listSers.add(ser);
                }
                serReturn.setStructExprRecog(listSers, StructExprRecog.EXPRRECOGTYPE_HBLANKCUT);
            }
        }

        //#2 这里一定是分数线分割的分数类型了
        else if (nMajorLnDivIdx != nStartIdx && nMajorLnDivIdx != nEndIdx) {
            //分子here
            StructExprRecog serNom = analyzeHCuts(imgChops, nStartIdx, nMajorLnDivIdx - 1, dAvgStrokeWidth, nStackLvl + 1);
            ImageChop imgChopLn = imgChops.mlistChops.get(nMajorLnDivIdx);
            ImageChop imgChopShinkedLn = imgChopLn.shrinkImgArray();
            //分数线here
            StructExprRecog serLn = new StructExprRecog(imgChopLn.mbarrayOriginalImg);
            serLn.setStructExprRecog(UnitProtoType.Type.TYPE_SUBTRACT, serNom.mstrFont,
                    imgChopLn.getLeftInOriginalImg(), imgChopLn.getTopInOriginalImg(),
                    imgChopLn.mnWidth, imgChopLn.mnHeight, imgChopShinkedLn,
                    UnitCandidate.BEST_SIMILARITY_VALUE);   // because it is must be a divide, so set similarity value to be best.
            //分母here
            StructExprRecog serDen = analyzeHCuts(imgChops, nMajorLnDivIdx + 1, nEndIdx, dAvgStrokeWidth, nStackLvl + 1);

            //组成{分子，-，分母}结构
            LinkedList<StructExprRecog> listSers = new LinkedList<StructExprRecog>();
            listSers.add(serNom);
            listSers.add(serLn);
            listSers.add(serDen);
            serReturn.setStructExprRecog(listSers, StructExprRecog.EXPRRECOGTYPE_HLINECUT);

        }
        //#3 分数线在最后的类型---下划线类型
        else if (nMajorLnDivIdx == nEndIdx) {
            StructExprRecog serBase = analyzeHCuts(imgChops, nStartIdx, nMajorLnDivIdx - 1, dAvgStrokeWidth, nStackLvl + 1);
            StructExprRecog serUnder = new StructExprRecog(imgChops.mlistChops.get(nMajorLnDivIdx).mbarrayOriginalImg);
            int nLeft = imgChops.mlistChops.get(nEndIdx).getLeftInOriginalImg();
            int nTop = imgChops.mlistChops.get(nEndIdx).getTopInOriginalImg();
            ImageChop imgChopShrinked = imgChops.mlistChops.get(nMajorLnDivIdx).shrinkImgArray();
            serUnder.setStructExprRecog(UnitProtoType.Type.TYPE_SUBTRACT, StructExprRecog.UNKNOWN_FONT_TYPE,
                    nLeft, nTop, imgChops.mlistChops.get(nEndIdx).mnWidth, imgChops.mlistChops.get(nEndIdx).mnHeight,
                    imgChopShrinked, 1);
            LinkedList<StructExprRecog> listSers = new LinkedList<StructExprRecog>();
            if (serBase.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTCAP) {
                listSers.addAll(serBase.mlistChildren);
                listSers.add(serUnder);
                serReturn.setStructExprRecog(listSers, StructExprRecog.EXPRRECOGTYPE_HCUTCAPUNDER);
            } else {
                listSers.add(serBase);
                listSers.add(serUnder);
                serReturn.setStructExprRecog(listSers, StructExprRecog.EXPRRECOGTYPE_HCUTUNDER);
            }
        }
        //#4 分数线在第一个的类型---上划线类型
        else {    //if (nMajorLnDivIdx == nStartIdx) {
            StructExprRecog serCap = new StructExprRecog(imgChops.mlistChops.get(nMajorLnDivIdx).mbarrayOriginalImg);
            int nLeft = imgChops.mlistChops.get(nStartIdx).getLeftInOriginalImg();
            int nTop = imgChops.mlistChops.get(nStartIdx).getTopInOriginalImg();
            ImageChop imgChopShrinked = imgChops.mlistChops.get(nMajorLnDivIdx).shrinkImgArray();
            serCap.setStructExprRecog(UnitProtoType.Type.TYPE_SUBTRACT, StructExprRecog.UNKNOWN_FONT_TYPE,
                    nLeft, nTop, imgChops.mlistChops.get(nStartIdx).mnWidth, imgChops.mlistChops.get(nStartIdx).mnHeight,
                    imgChopShrinked, 1);
            StructExprRecog serBase = analyzeHCuts(imgChops, nMajorLnDivIdx + 1, nEndIdx, dAvgStrokeWidth, nStackLvl + 1);
            LinkedList<StructExprRecog> listSers = new LinkedList<StructExprRecog>();
            if (serBase.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_HCUTUNDER) {
                listSers.add(serCap);
                listSers.addAll(serBase.mlistChildren);
                serReturn.setStructExprRecog(listSers, StructExprRecog.EXPRRECOGTYPE_HCUTCAPUNDER);
            } else {
                listSers.add(serCap);
                listSers.add(serBase);
                serReturn.setStructExprRecog(listSers, StructExprRecog.EXPRRECOGTYPE_HCUTCAP);
            }
        }

        // now identify i, j and other h divided cuts.
        serReturn = serReturn.identifyHSeperatedChar();

        return serReturn;
    }

    //CORE::分块好的图片——>StructExprRecog
    // here assume barrayImg has been the minimum containing rectangle of the image.
    // imgChopsFrom is the image chops that includes imgChopOriginal, it can be null,
    // nReadingOrder is 0 means horizontally cut imgchops, is 1 means vertically cut imgchops.
    public static int dml_cnt=0;
    public static double YX_size=100;
    public static StructExprRecog recognize(ImageChop imgChopOriginal, ImageChops imgChopsFrom, int nCutMode, double dAvgStrokeWidth, int nStackLvl) throws ExprRecognizeException, InterruptedException, IOException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
        if (nStackLvl >= MAX_RECOGNIZING_STACK_COUNT) {
            throw new ExprRecognizeException(TOO_DEEP_CALL_STACK);
        }

        StructExprRecog serReturn = new StructExprRecog(imgChopOriginal.mbarrayOriginalImg);

        //  重点！！！ 判断切块类型
        if (imgChopOriginal.isEmptyImage() ||
                imgChopOriginal.mnChopType == ImageChop.TYPE_BLANK_DIV ||
                imgChopOriginal.mnChopType == ImageChop.TYPE_UNDER_DIV ||
                imgChopOriginal.mnChopType == ImageChop.TYPE_CAP_DIV) {
            ImageChop imgChopShrinked = new ImageChop();
            byte[][] barrayImage = new byte[imgChopOriginal.mnWidth][imgChopOriginal.mnHeight];

            // do not use imgChopOriginal directly because imgChopOriginal may included some points which are cut into it.
            imgChopShrinked.setImageChop(barrayImage, 0, 0, imgChopOriginal.mnWidth, imgChopOriginal.mnHeight,
                    imgChopOriginal.mbarrayOriginalImg, imgChopOriginal.getLeftInOriginalImg(), imgChopOriginal.getTopInOriginalImg(), imgChopOriginal.mnChopType);

            //不知道这里为什么就返回空类型了，TYPE_UNDER_DIV 为啥就等同于空类型了呢-----!!!因为还没有加入显示这些上划线，下划线的功能，至于TYPE_BLANK_DIV 因为本来就是个空白分割器
            serReturn.setStructExprRecog(UnitProtoType.Type.TYPE_EMPTY, StructExprRecog.UNKNOWN_FONT_TYPE,
                    imgChopOriginal.getLeftInOriginalImg(), imgChopOriginal.getTopInOriginalImg(),
                    imgChopOriginal.mnWidth, imgChopOriginal.mnHeight, imgChopShrinked, 1);  // empty char similarity is always 1.

            return serReturn;
        }
        //  TYPE_LINE_DIV——>TYPE_SUBTRACT
        else if (imgChopOriginal.mnChopType == ImageChop.TYPE_LINE_DIV) {
            ImageChop imgChopShrinked = imgChopOriginal.shrinkImgArray();
            serReturn.setStructExprRecog(UnitProtoType.Type.TYPE_SUBTRACT, StructExprRecog.UNKNOWN_FONT_TYPE,
                    imgChopOriginal.getLeftInOriginalImg(), imgChopOriginal.getTopInOriginalImg(),
                    imgChopOriginal.mnWidth, imgChopOriginal.mnHeight, imgChopShrinked, 1);  // empty char similarity is always 1.

            return serReturn;
        }

        //计算切块后的总点数
        int nTotalOnCount = imgChopOriginal.getTotalOnCount();
        int nTotalOnCountInSkeleton = imgChopOriginal.getTotalOnCountInSkeleton(true);
        double dThisStrokeWidth = 1;  // minum stroke width is 1.
        if (nTotalOnCountInSkeleton != 0 && nTotalOnCount != 0) {
            dThisStrokeWidth = (double) nTotalOnCount / (double) nTotalOnCountInSkeleton;
        } else if (nTotalOnCount != 0) {
            dThisStrokeWidth = Math.sqrt((double) nTotalOnCount);
        }
        if (dAvgStrokeWidth <= 0
                || (imgChopOriginal.mnWidth > ConstantsMgr.msdNoEnoughInfo4AvgStrokeWidthThresh * dThisStrokeWidth
                || imgChopOriginal.mnHeight > ConstantsMgr.msdNoEnoughInfo4AvgStrokeWidthThresh * dThisStrokeWidth)
                || dAvgStrokeWidth < dThisStrokeWidth) {
            // if average stroke width is not passed in, use local stroke width
            // or
            // if average stroke width is passed in but this piece of image chop is clear
            // which means we can identify it correctly using its own local stroke width.
            // or
            // if average stroke width is passed and image chop is not clear, but average stroke
            // width is much smaller than local stroke width, use its own local stroke width. Otherwise
            // may miss identify the character.
            // we still use local stroke width
            dAvgStrokeWidth = dThisStrokeWidth;
        }   // else use dAvgStrokeWidth (parent stroke width).

        //评估字符的最大高度和宽度
        // now estimate maximum char width and height regardless of connection
        // 1 / msdAvgCharWidthOverHeight * width / x == height / y
        // height * x + width * y >= total_on_count_inskeleton / 2 
        // assuming for single char, skeleton length cannot be longer than 2 * char width + 2 * char height
        // solve the above function we get
        // x == width * y/ (msdAvgCharWidthOverHeight * height)
        // width * y * (msdAvgCharWidthOverHeight + 1)/msdAvgCharWidthOverHeight >= total_on_count_inskeleton / 2
        // y >= total_on_count_inskeleton * msdAvgCharWidthOverHeight/ 2 / width / (msdAvgCharWidthOverHeight + 1)
        // x >= total_on_count_inskeleton/2/height/(msdAvgCharWidthOverHeight + 1)

        double dImgArea = imgChopOriginal.mnHeight * imgChopOriginal.mnWidth;
        double dMaxEstCharHeight = 1.0, dMaxEstCharWidth = 1.0;
        if (nTotalOnCountInSkeleton != 0) {
            dMaxEstCharHeight = Math.max(ConstantsMgr.msnMinCharHeightInUnit,
                    2 * dImgArea * (ConstantsMgr.msdAvgCharWidthOverHeight + 1) / (nTotalOnCountInSkeleton * ConstantsMgr.msdAvgCharWidthOverHeight));
            if (dMaxEstCharHeight > imgChopOriginal.mnHeight) {
                dMaxEstCharHeight = imgChopOriginal.mnHeight;
            }
            dMaxEstCharWidth = Math.max(ConstantsMgr.msnMinCharWidthInUnit,
                    2 * dImgArea * (ConstantsMgr.msdAvgCharWidthOverHeight + 1) / nTotalOnCountInSkeleton);
            if (dMaxEstCharWidth > imgChopOriginal.mnWidth) {
                dMaxEstCharWidth = imgChopOriginal.mnWidth;
            }
        }

        // the output imgchops are also minimum container adjusted
        //原始图片进来后首先进行水平方向的切分，传入的参数……见下面
        ImageChops imgChops = ExprSeperator.cutHorizontallyProj(imgChopOriginal, dAvgStrokeWidth, dMaxEstCharWidth, dMaxEstCharHeight);

        //分成了很多块
        if (imgChops.mlistChops.size() > 1) {
            int nThisCutStartIdx = 0, nThisCutEndIdx = -1;  // cut by blank div.
            LinkedList<StructExprRecog> listHBlankCuts = new LinkedList<StructExprRecog>();
            int idx = 0;
            for (idx = 0; idx < imgChops.mlistChops.size(); idx++) {
                ImageChop imgChop = imgChops.mlistChops.get(idx);
                if (imgChop.mnChopType == ImageChop.TYPE_BLANK_DIV) {
                    nThisCutEndIdx = idx - 1;
                    StructExprRecog serThisCut = analyzeHCuts(imgChops, nThisCutStartIdx, nThisCutEndIdx, dAvgStrokeWidth, nStackLvl + 1);
                    listHBlankCuts.add(serThisCut);
                    nThisCutStartIdx = idx + 1;
                }
            }
            if (imgChops.mlistChops.getLast().mnChopType != ImageChop.TYPE_BLANK_DIV) {
                StructExprRecog serThisCut = analyzeHCuts(imgChops, nThisCutStartIdx, imgChops.mlistChops.size() - 1, dAvgStrokeWidth, nStackLvl + 1);
                listHBlankCuts.add(serThisCut);
            }
            if (listHBlankCuts.size() == 1) {
                serReturn = listHBlankCuts.getFirst();

            } else {   // size must be > 1

                serReturn.setStructExprRecog(listHBlankCuts, StructExprRecog.EXPRRECOGTYPE_HBLANKCUT);

            }
        }

        //返回一块，或者不能分
        else if (imgChops.mlistChops.size() == 1) {
            // cannot be horizontally divided.
            //不能水平分再试试竖直分
            ImageChops chops = ExprSeperator.cutVerticallyProj(imgChopOriginal, dAvgStrokeWidth);
            if (chops.mlistChops.size() > 1) {
                LinkedList<StructExprRecog> listSers = new LinkedList<StructExprRecog>();
                int idx = 0;
                while (idx < chops.mlistChops.size()) {
                    ImageChop imgChop = chops.mlistChops.get(idx);
                    //递归识别！！我调用我自己
                    StructExprRecog ser = recognize(imgChop, chops, 1, 0, nStackLvl + 1); // it could be lower note with thin strokes, so do not pass avg stroke width.

                    listSers.add(ser);

                    idx++;
                }
                if (listSers.size() > 1) {
                    serReturn.setStructExprRecog(listSers, StructExprRecog.EXPRRECOGTYPE_VBLANKCUT);
                } else {
                    serReturn = listSers.getFirst();
                }
                //serReturn = serReturn.restruct(); // do not need to restruct at this moment.
            }
            //横竖切完都只剩一块了
            else if (chops.mlistChops.size() == 1) {  // do not use isRecognizableChar function.

                //提取最小连接块
                ImageChops imgChopsExtracted = ExprSeperator.extractConnectedPieces(imgChopOriginal);
                if (imgChopsExtracted.mlistChops.size() == 0) {   // empty image, actually this will not happen.
                    ImageChop imgChopShinked = new ImageChop();
                    byte[][] barrayImage = new byte[imgChopOriginal.mnWidth][imgChopOriginal.mnHeight];

                    // do not use imgChopOriginal directly because imgChopOriginal may included some points which are cut into it.
                    imgChopShinked.setImageChop(barrayImage, 0, 0, imgChopOriginal.mnWidth, imgChopOriginal.mnHeight,
                            imgChopOriginal.mbarrayOriginalImg, imgChopOriginal.getLeftInOriginalImg(), imgChopOriginal.getTopInOriginalImg(), imgChopOriginal.mnChopType);

                    serReturn.setStructExprRecog(UnitProtoType.Type.TYPE_EMPTY, StructExprRecog.UNKNOWN_FONT_TYPE,
                            imgChopOriginal.getLeftInOriginalImg(), imgChopOriginal.getTopInOriginalImg(),
                            imgChopOriginal.mnWidth, imgChopOriginal.mnHeight, imgChopShinked, 1);  // empty char similarity is always 1.
                }
                //识别单个字符,重点中的重点
                else if (imgChopsExtracted.mlistChops.size() == 1) {

                    StructExprRecog serReturnCand1 = new StructExprRecog(imgChopOriginal.mbarrayOriginalImg),
                            serReturnCand2 = new StructExprRecog(imgChopOriginal.mbarrayOriginalImg);


                    ImageChop imgChopThinned = StrokeFinder.thinImageChop(imgChopOriginal, true);

                    //识别的函数
                    LinkedList<UnitCandidate> listCandidates = UnitRecognizer.recogChar(imgChopOriginal, imgChopThinned, dAvgStrokeWidth, nTotalOnCount);


                    ImageChop imgChopShinked = imgChopOriginal.shrinkImgArray();

//                    if(listCandidates.size() == 1 && listCandidates.get(0).mprotoType == null){
//                        return null;
//                    }

                    if (listCandidates.size() > 0) {
                        serReturnCand1.setStructExprRecog(listCandidates.get(0).mprotoType.mnUnitType, listCandidates.get(0).mprotoType.mstrFont,
                                imgChopOriginal.getLeftInOriginalImg(), imgChopOriginal.getTopInOriginalImg(),
                                imgChopOriginal.mnWidth, imgChopOriginal.mnHeight, imgChopShinked,
                                listCandidates.get(0).mdOverallSimilarity);
                    }
                    else {
                        // we still need to set its place and image chop although it is unknown.
                        serReturnCand1.setStructExprRecog(UnitProtoType.Type.TYPE_UNKNOWN, serReturnCand1.getFont(),
                                imgChopOriginal.getLeftInOriginalImg(), imgChopOriginal.getTopInOriginalImg(),
                                imgChopOriginal.mnWidth, imgChopOriginal.mnHeight, imgChopShinked,
                                serReturnCand1.mdSimilarity);
                    }
                    if (imgChopsFrom == null || nCutMode != 1) {
                        // if imgChopsFrom is null, means we are analyzing a single connected piece
                        // if nCutMode != 1, means it is horizontally cut, however, connected stroke
                        // is only supported to be vertically disconnected, so have to mannually
                        // generate imgChopsFrom and set nCutMode to be 1.
                        imgChopsFrom = new ImageChops();
                        imgChopsFrom.mlistChops.add(imgChopOriginal);
                        nCutMode = 1;   // vertical cut.
                    }


                    int heise;

                    //还是要用Thinned
                    String dirs = "python" + File.separator + "data" + File.separator + String.format("%03d", 1) + ".jpg";
                    heise=ImgMatrixOutput.createMatrixImage(imgChopThinned.mbarrayImg, dirs);
                    //分析图片用的，可注释这两行
//                    String dml_dir = "dml_data" + File.separator + String.format("%03d", ++dml_cnt) + ".jpg";
//                    ImgMatrixOutput.createMatrixImage(imgChopThinned.mbarrayImg, dml_dir);

                    //todo add some rule to not use or trust py's result by LH
                    //if(!shouldnotUsePy(serReturnCand1))
                    usePy();


                    System.out.println(heise);
                    System.out.println(imgChopThinned.mbarrayImg.length*imgChopThinned.mbarrayImg[0].length);
                    System.out.println(YX_size);
                    //todo 面积纠错法 高

                    if(imgChopThinned.mbarrayImg.length<30
                            && (Math.abs(imgChopThinned.mbarrayImg.length-imgChopThinned.mbarrayImg[0].length)<10)
                            && (serReturnCand1.mType!= UnitProtoType.Type.TYPE_ZERO)
                            && (serReturnCand1.mType!= UnitProtoType.Type.TYPE_SMALL_O)
                            && (serReturnCand1.mType!= UnitProtoType.Type.TYPE_TWO)
                            && (serReturnCand1.mType!= UnitProtoType.Type.TYPE_FOUR)
                            && (serReturnCand1.mType!= UnitProtoType.Type.TYPE_SEVEN)
                            && (similarty<0.997)
                            && (imgChopThinned.mbarrayImg.length*imgChopThinned.mbarrayImg[0].length/YX_size < 0.5)
                            &&(YX_size!=100)
                        //||(serReturnCand1.mType== UnitProtoType.Type.TYPE_ADD)
                    )
                    {
                        System.out.println("[PYTHON_原]\t" + getTpye(resu) + " \t" + resu +"\t"+similarty);
                        resu=".";
                        similarty = 0.997;
                    }


                    //test3
                    System.out.println("[JAVA___RESULT]\t" + serReturnCand1.mType + " \t" + serReturnCand1.toString());
                    System.out.println("[PYTHON_RESULT]\t" + getTpye(resu) + " \t" + resu +"\t"+similarty);
                    UnitProtoType.Type cType = getTpye((resu));
                    serReturn = serReturnCand1;
                    if ((similarty >= 0.995||cType==serReturnCand1.mType)&& !shouldnotUsePy(serReturnCand1) && !shouldnotTrustPy(cType)) {
                    //if ((similarty >= 0.995||cType==serReturnCand1.mType)) {
                        serReturn.mType = correctPY_YX(getTpye(resu),serReturnCand1.mType,getTpye(resu));
                        //serReturn.mType=getTpye(resu);
                        serReturn.mdSimilarity = 0.0;
                        serReturn.mnExprRecogType = StructExprRecog.EXPRRECOGTYPE_ENUMTYPE;
                    }
                    //这里进行过度切分！(过度切分并没有用cnn来识别）然后，从java识别结果ser1和过度切分分析结果ser2中选一个
//                    else {
//                        if(serReturnCand1.mType!=correctPY_YX(getTpye(resu),serReturnCand1.mType,serReturnCand1.mType))
//                        {
//                            serReturn.mType = correctPY_YX(getTpye(resu),serReturnCand1.mType,serReturn.mType);
//                        }
//                        else if(serReturnCand1.mdSimilarity>0.097)
//                        {
//                            serReturnCand2 = disconnect2Recog(imgChopsFrom, nCutMode, imgChopsFrom.mlistChops.indexOf(imgChopOriginal), dAvgStrokeWidth, serReturnCand1, new LinkedList<ImageChop>(), nStackLvl + 1);
//                            serReturn = selectSERFromCands(serReturnCand1, serReturnCand2);
//                        }
//                    }
//                    //这里进行过度切分！(过度切分并没有用cnn来识别）然后，从java识别结果ser1和过度切分分析结果ser2中选一个
                    // todo LH002
                    else if(serReturnCand1.mdSimilarity>0.07){
                        serReturnCand1.mType = correctPY_YX(getTpye(resu),serReturnCand1.mType,serReturnCand1.mType);
                        serReturnCand2 = disconnect2Recog(imgChopsFrom, nCutMode, imgChopsFrom.mlistChops.indexOf(imgChopOriginal), dAvgStrokeWidth, serReturnCand1, new LinkedList<ImageChop>(), nStackLvl + 1);
//                        System.out.println("切分："+serReturnCand1.toString()+"\t"+serReturnCand2.toString());
                        String YX_Cand3=serReturnCand2.toString();
                        if(YX_Cand3.length()==2 && isNumeric(YX_Cand3))
                        {
                            serReturn = selectSERFromCands(serReturnCand1, serReturnCand2);
                        }
                        else
                        {
                            if(YX_Cand3.length()==2 && (
                                    (YX_Cand3.charAt(1)=='-')&&(Character.isDigit(YX_Cand3.charAt(0)))
                                   || (YX_Cand3.charAt(1)=='.')&&(Character.isDigit(YX_Cand3.charAt(0)))
                            )
                            )
                            {
                                serReturn = serReturnCand1;
                                serReturn.mType = getTpye(String.valueOf(YX_Cand3.charAt(0)));
                            }
                            else
                                serReturn = serReturnCand1;
                        }


                    }
                    System.out.println("[FINAL__RESULT]\t" + serReturn.mType + " \t" + serReturn.toString()+"\n");

                    if(isNumberChar(serReturn.mType) && YX_size==100)
                    {
                        YX_size=imgChopThinned.mbarrayImg.length*imgChopThinned.mbarrayImg[0].length;
                        if(serReturn.mType==UnitProtoType.Type.TYPE_ONE)
                            YX_size=YX_size*imgChopThinned.mbarrayImg.length;
                    }




                } else {
                    int nExtractedMajorIdx = ExprSeperator.getMajorChopFromSameOriginal(imgChopsExtracted);
                    serReturn = extract2Recog(imgChopsExtracted, nExtractedMajorIdx, dAvgStrokeWidth, nStackLvl + 1);// from test, it seems that cut-recog cannot improve correctness, so do not do.
                }
            }
        }
        //识别出结构化的字符
        serReturn = serReturn.identifyHSeperatedChar();   // = or always equal might be h cuted here.
        return serReturn;
    }

    public static String resu="cll";
    public static double similarty;
    public static int count = 0;

    public static boolean isNumeric(String s){
        if(s!=null && !"".equals(s.trim()))
            return s.matches("^[0-9]*$");
        else
            return false;
    }

    public static boolean shouldnotUsePy(StructExprRecog ser){
        /*Some case wo shoule not use py, Because use py may let to misunderstood and to save time*/
        /*UnitProtoType.Type.TYPE_VERTICAL_LINE
        UnitProtoType.Type.TYPE_SUBTRACT
        UnitProtoType.Type.TYPE_EMPTY
        UnitProtoType.Type.TYPE_DOT
        UnitProtoType.Type.TYPE_ONE
        //those character is pre-Recognised by java
        */
        if(ser.mType == UnitProtoType.Type.TYPE_DOT || ser.mType == UnitProtoType.Type.TYPE_ONE || ser.mType ==  UnitProtoType.Type.TYPE_VERTICAL_LINE
                || ser.mType == UnitProtoType.Type.TYPE_SUBTRACT || ser.mType == UnitProtoType.Type.TYPE_EMPTY||
        ser.mType==UnitProtoType.Type.TYPE_ROUND_BRACKET||ser.mType==UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET)
            return true;
        return false;
    }

    public static boolean shouldnotTrustPy(UnitProtoType.Type cType){
        /*We do not trust the result of python because it should not get a result of 'i' or 'j' ect.*/
        if(cType != UnitProtoType.Type.TYPE_SMALL_I
                && cType != UnitProtoType.Type.TYPE_SMALL_J
                && cType!=UnitProtoType.Type.TYPE_WORD_SIN
                && cType!=UnitProtoType.Type.TYPE_WORD_TAN
                && cType!=UnitProtoType.Type.TYPE_SMALL_N
        )
            return false;
        return true;
    }

    public static UnitProtoType.Type correctPY_YX(UnitProtoType.Type pythonType, UnitProtoType.Type javaType,UnitProtoType.Type returnType)
    {
        //hhhhhh
        UnitProtoType unitProtoTypeP = new UnitProtoType();
        unitProtoTypeP.mnUnitType = pythonType;
        UnitProtoType unitProtoTypeJ = new UnitProtoType();
        unitProtoTypeJ.mnUnitType = javaType;
        if(pythonType==UnitProtoType.Type.TYPE_SMALL_B  && javaType==UnitProtoType.Type.TYPE_SIX)
        {//python:{ java:s 则：int
            return UnitProtoType.Type.TYPE_SIX;
        }
        else if(pythonType==UnitProtoType.Type.TYPE_SMALL_B  && javaType==UnitProtoType.Type.TYPE_EIGHT)
        {//python:{ java:s 则：int
            return UnitProtoType.Type.TYPE_EIGHT;
        }
        else if(pythonType==UnitProtoType.Type.TYPE_SMALL_N && javaType==UnitProtoType.Type.TYPE_ZERO )
        {//python:1 java:(or) 则：1
            return UnitProtoType.Type.TYPE_ZERO ;
        }
        else if(pythonType==UnitProtoType.Type.TYPE_INTEGRATE && javaType==UnitProtoType.Type.TYPE_SUBTRACT)
        {//python:2 java:z 则：2
            return UnitProtoType.Type.TYPE_INTEGRATE;
        }
//        else if(pythonType==UnitProtoType.Type.TYPE_THREE && javaType==UnitProtoType.Type.TYPE_FIVE)
//        {//python:2 java:z 则：2
//            return UnitProtoType.Type.TYPE_FIVE;
//        }
        else if(pythonType==UnitProtoType.Type.TYPE_SMALL_A && javaType==UnitProtoType.Type.TYPE_TWO)
        {//python:2 java:z 则：2
            return UnitProtoType.Type.TYPE_TWO;
        }
        else if(pythonType==UnitProtoType.Type.TYPE_SMALL_F && javaType==UnitProtoType.Type.TYPE_FIVE)
        {//python:2 java:z 则：2
            return UnitProtoType.Type.TYPE_FIVE;
        }
        else if(pythonType==UnitProtoType.Type.TYPE_SUBTRACT && javaType==UnitProtoType.Type.TYPE_SMALL_Y)
        {//python:2 java:z 则：2
            return UnitProtoType.Type.TYPE_DOT;
        }
        else if(pythonType==UnitProtoType.Type.TYPE_SMALL_F && javaType==UnitProtoType.Type.TYPE_SMALL_I_WITHOUT_DOT)
        {//python:2 java:z 则：2
            return UnitProtoType.Type.TYPE_FIVE;
        }
        else if(pythonType==UnitProtoType.Type.TYPE_SMALL_Y && javaType==UnitProtoType.Type.TYPE_SEVEN)
        {//python:2 java:z 则：2
            return UnitProtoType.Type.TYPE_NINE;
        }
        else if(pythonType==UnitProtoType.Type.TYPE_EMPTY)
        {//python:2 java:z 则：2
            return javaType;
        }
        //todo dml change 2.1
        else if(pythonType==UnitProtoType.Type.TYPE_ONE&&similarty>0.9985 && javaType==UnitProtoType.Type.TYPE_ROUND_BRACKET)
        {//python:2 java:z 则：2
            return UnitProtoType.Type.TYPE_ONE;
        }

        //todo dml change 3.1 at 7.17 00:12
        else if(pythonType==UnitProtoType.Type.TYPE_SMALL_B)
        {//python:2 java:z 则：2
            return UnitProtoType.Type.TYPE_SIX;
        }
        else if(pythonType==UnitProtoType.Type.TYPE_SUBTRACT&&(isNumeric(javaType.toString())
                ||javaType==UnitProtoType.Type.TYPE_BRACE
                ||javaType==UnitProtoType.Type.TYPE_INTEGRATE))
        {//python:- java:数字 则：数字
            return javaType;
        }

        //todo this is a rule for the java type is A but the python is a
        else if(pythonType.toString().equals(javaType.toString().toLowerCase())){
            return javaType;
        }
        return returnType;
    }

    public static void usePy() {
        //System.out.println("\nHello,ready to use python");
        String line = null;
        resu = new String();
        similarty = 1;
        try {
            int count = 0;
            Socket socket = new Socket("127.0.0.1", 9998);
            //System.out.println("Client start!");
            PrintWriter out = new PrintWriter(socket.getOutputStream()); // 输出，to 服务器 socket
            out.println("Client request! :-) ");
            out.flush(); // 刷缓冲输出，to 服务器
            int i = 0;
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 输入， from 服务器 socket
            while ((line = in.readLine()) != null) {
                // 这里已经将所有CNN识别结果都转化成小写了
                line = line.toLowerCase();
                //System.out.println(line);
                if (i % 2 == 0)
                    resu = line;
                else {
                    //count++;
                    similarty = Double.valueOf(line);
                }
                i++;
            }
            //System.out.println("Client end!");
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static UnitProtoType.Type getTpye(String line) {
        UnitProtoType.Type rety = UnitProtoType.Type.TYPE_UNKNOWN;
        switch (line) {
            case "infty":
                rety = UnitProtoType.getmningTypeValue("\\infinite");
                break;
            case "alpha":
                rety = UnitProtoType.getmningTypeValue("\\alpha");
                break;
            case "beta":
                rety = UnitProtoType.getmningTypeValue("\\beta");
                break;
            case "ascii_124":
                rety = UnitProtoType.getmningTypeValue("/");
                break;
            case "div":
                rety = UnitProtoType.getmningTypeValue("\\div");
                break;
            case "delta":
                rety = UnitProtoType.getmningTypeValue("\\Delta");
                break;
            //case "exists": rety = UnitProtoType.getmningTypeValue("\\infinite"); break;
            //case "forall": rety = UnitProtoType.getmningTypeValue("\\infinite"); break;
            case "forward_slash":
                rety = UnitProtoType.getmningTypeValue("/");
                break;
            case "gamma":
                rety = UnitProtoType.getmningTypeValue("\\gamma");
                break;
            //case "geq": rety = UnitProtoType.getmningTypeValue("\\infinite"); break;
            //case "gt": rety = UnitProtoType.getmningTypeValue("\\infinite"); break;
            //case "in": rety = UnitProtoType.getmningTypeValue("\\infinite"); break;
            case "int":
                rety = UnitProtoType.getmningTypeValue("\\integrate");
                break;
            case "lambda":
                rety = UnitProtoType.getmningTypeValue("\\lambda");
                break;
            //case "leq": rety = UnitProtoType.getmningTypeValue("\\infinite"); break;
            //case "lt": rety = UnitProtoType.getmningTypeValue("\\infinite"); break;
            case "mu":
                rety = UnitProtoType.getmningTypeValue("\\mu");
                break;
            //case "neq": rety = UnitProtoType.getmningTypeValue("\\infinite"); break;
            case "phi":
                rety = UnitProtoType.getmningTypeValue("\\phi");
                break;
            case "pi":
                rety = UnitProtoType.getmningTypeValue("\\pi");
                break;
            //case "pm": rety = UnitProtoType.getmningTypeValue("\\infinite"); break;
            case "rightarrow"://YX:这里的趋近改了
                rety = UnitProtoType.getmningTypeValue("\\rightarrow");
                break;
                //WTF!!!!!!!!!!!!!!!!!!!!!!!!!!!
            case "sum":
                rety = UnitProtoType.getmningTypeValue("\\Sigma");
                break;
//            case "sigma":
//                rety = UnitProtoType.getmningTypeValue("\\sigma");
//                break;
            case "theta":
                rety = UnitProtoType.getmningTypeValue("\\theta");
                break;
            case "times":
                rety = UnitProtoType.getmningTypeValue("\\times");
                break;
            case "{":
                rety = UnitProtoType.getmningTypeValue("\\brace");
                break;
            case "}":
                rety = UnitProtoType.getmningTypeValue("\\closebrace");
                break;
            case "!":
                rety = UnitProtoType.getmningTypeValue("!");
                break;
            case "(":
                rety = UnitProtoType.getmningTypeValue("(");
                break;
            case ")":
                rety = UnitProtoType.getmningTypeValue(")");
                break;
            case "+":
                rety = UnitProtoType.getmningTypeValue("+");
                break;
            case "[":
                rety = UnitProtoType.getmningTypeValue("[");
                break;
            case "]":
                rety = UnitProtoType.getmningTypeValue("]");
                break;
            case "=":
                rety = UnitProtoType.getmningTypeValue("=");
                break;
            case "-":
                rety = UnitProtoType.getmningTypeValue("-");
                break;
            default:
                rety = UnitProtoType.getmningTypeValue(line);
                break;
        }
        return rety;
    }

    // this function tries to recognize a v-cut cluster and h-cut it
    public static StructExprRecog recogCluster(ImageChop imgChopCluster, double dAvgStrokeWidth, int nStackLvl) throws ExprRecognizeException, InterruptedException, IOException {
        if (nStackLvl >= MAX_RECOGNIZING_STACK_COUNT) {
            throw new ExprRecognizeException(TOO_DEEP_CALL_STACK);
        }
        StructExprRecog serReturn = new StructExprRecog(imgChopCluster.mbarrayOriginalImg);

        // need not to worry about empty imgChopCluster here

        int nTotalOnCount = imgChopCluster.getTotalOnCount();
        int nTotalOnCountInSkeleton = imgChopCluster.getTotalOnCountInSkeleton(true);
        double dThisStrokeWidth = 1;  // minum stroke width is 1.
        if (nTotalOnCountInSkeleton != 0 && nTotalOnCount != 0) {
            dThisStrokeWidth = (double) nTotalOnCount / (double) nTotalOnCountInSkeleton;
        } else if (nTotalOnCount != 0) {
            dThisStrokeWidth = Math.sqrt((double) nTotalOnCount);
        }
        if (dAvgStrokeWidth <= 0
                || (imgChopCluster.mnWidth > ConstantsMgr.msdNoEnoughInfo4AvgStrokeWidthThresh * dThisStrokeWidth
                || imgChopCluster.mnHeight > ConstantsMgr.msdNoEnoughInfo4AvgStrokeWidthThresh * dThisStrokeWidth)
                || dAvgStrokeWidth < dThisStrokeWidth) {
            // if average stroke width is not passed in, use local stroke width
            // or
            // if average stroke width is passed in but this piece of image chop is clear
            // which means we can identify it correctly using its own local stroke width.
            // or
            // if average stroke width is passed and image chop is not clear, but average stroke
            // width is much smaller than local stroke width, use its own local stroke width. Otherwise
            // may miss identify the character.
            // we still use local stroke width
            dAvgStrokeWidth = dThisStrokeWidth;
        }   // else use dAvgStrokeWidth (parent stroke width).

        // now estimate maximum char width and height regardless of connection
        // 1 / msdAvgCharWidthOverHeight * width / x == height / y
        // height * x + width * y >= total_on_count_inskeleton / 2 
        // assuming for single char, skeleton length cannot be longer than 2 * char width + 2 * char height
        // solve the above function we get
        // x == width * y/ (msdAvgCharWidthOverHeight * height)
        // width * y * (msdAvgCharWidthOverHeight + 1)/msdAvgCharWidthOverHeight >= total_on_count_inskeleton / 2
        // y >= total_on_count_inskeleton * msdAvgCharWidthOverHeight/ 2 / width / (msdAvgCharWidthOverHeight + 1)
        // x >= total_on_count_inskeleton/2/height/(msdAvgCharWidthOverHeight + 1)
        double dImgArea = imgChopCluster.mnHeight * imgChopCluster.mnWidth;
        double dMaxEstCharHeight = 1.0, dMaxEstCharWidth = 1.0;
        if (nTotalOnCountInSkeleton != 0) {
            dMaxEstCharHeight = Math.max(ConstantsMgr.msnMinCharHeightInUnit,
                    2 * dImgArea * (ConstantsMgr.msdAvgCharWidthOverHeight + 1) / (nTotalOnCountInSkeleton * ConstantsMgr.msdAvgCharWidthOverHeight));
            if (dMaxEstCharHeight > imgChopCluster.mnHeight) {
                dMaxEstCharHeight = imgChopCluster.mnHeight;
            }
            dMaxEstCharWidth = Math.max(ConstantsMgr.msnMinCharWidthInUnit,
                    2 * dImgArea * (ConstantsMgr.msdAvgCharWidthOverHeight + 1) / nTotalOnCountInSkeleton);
            if (dMaxEstCharWidth > imgChopCluster.mnWidth) {
                dMaxEstCharWidth = imgChopCluster.mnWidth;
            }
        }
        ImageChops imgChops = ExprSeperator.cutHorizontallyProj(imgChopCluster, dAvgStrokeWidth, dMaxEstCharWidth, dMaxEstCharHeight);

        if (imgChops.mlistChops.size() <= 1) {
            // cannot be horizontally cut, this is not a cluster, 
            return serReturn;
        }
        int nNonBlankChopCnt = 0;
        for (int idx = 0; idx < imgChops.mlistChops.size(); idx++) {
            ImageChop imgChop = imgChops.mlistChops.get(idx);
            if (imgChop.mnChopType != ImageChop.TYPE_BLANK_DIV && imgChop.mnChopType != ImageChop.TYPE_CAP_DIV
                    && imgChop.mnChopType != ImageChop.TYPE_UNDER_DIV) {
                nNonBlankChopCnt++;
                if (imgChop.mnChopType == ImageChop.TYPE_LINE_DIV) {
                    // if there is a line div, then cannot be a cluster of v-cuts
                    return serReturn;
                }
            }
        }

        if (nNonBlankChopCnt == 2 || nNonBlankChopCnt == 3) {
            // can be h-blank cut or cap or under or cap-under, in one words, no longer v-cut series
            int nThisCutStartIdx = 0, nThisCutEndIdx = -1;  // cut by blank div.
            LinkedList<StructExprRecog> listHBlankCuts = new LinkedList<StructExprRecog>();
            int idx = 0;
            for (idx = 0; idx < imgChops.mlistChops.size(); idx++) {
                ImageChop imgChop = imgChops.mlistChops.get(idx);
                if (imgChop.mnChopType == ImageChop.TYPE_BLANK_DIV) {
                    nThisCutEndIdx = idx - 1;
                    StructExprRecog serThisCut = analyzeHCuts(imgChops, nThisCutStartIdx, nThisCutEndIdx, dAvgStrokeWidth, nStackLvl + 1);
                    listHBlankCuts.add(serThisCut);
                    nThisCutStartIdx = idx + 1;
                }
            }
            if (imgChops.mlistChops.getLast().mnChopType != ImageChop.TYPE_BLANK_DIV) {
                StructExprRecog serThisCut = analyzeHCuts(imgChops, nThisCutStartIdx, imgChops.mlistChops.size() - 1, dAvgStrokeWidth, nStackLvl + 1);
                listHBlankCuts.add(serThisCut);
            }
            if (listHBlankCuts.size() == 1) {

                serReturn = listHBlankCuts.getFirst();    // because size must be greater than one, this line will not be executed
            } else {   // size must be > 1

                serReturn.setStructExprRecog(listHBlankCuts, StructExprRecog.EXPRRECOGTYPE_HBLANKCUT);
            }
        }


        return serReturn;
    }

    //提取来识别
    public static StructExprRecog extract2Recog(ImageChops imgChops, int nExtractedMajorIdx, double dAvgStrokeWidth, int nStackLvl) throws ExprRecognizeException, InterruptedException, IOException {
        if (nStackLvl >= MAX_RECOGNIZING_STACK_COUNT) {
            throw new ExprRecognizeException(TOO_DEEP_CALL_STACK);
        }
        StructExprRecog serCand = new StructExprRecog(imgChops.mlistChops.get(nExtractedMajorIdx).mbarrayOriginalImg);
        if (imgChops == null || imgChops.mlistChops.size() == 0) {
            //ser.setStructExprRecog(UnitProtoType.Type.TYPE_UNKNOWN, 0, 0, 0, 0);
            return serCand;  // an unknown imgChopOriginal. note that its place is all 0.
        } else {
            int nLeft = Integer.MAX_VALUE, nTop = Integer.MAX_VALUE, nRightP1 = Integer.MIN_VALUE, nBottomP1 = Integer.MIN_VALUE;
            for (int idx = 0; idx < imgChops.mlistChops.size(); idx++) {
                ImageChop chopThis = imgChops.mlistChops.get(idx);
                if (chopThis.getLeftInOriginalImg() < nLeft) {
                    nLeft = chopThis.getLeftInOriginalImg();
                }
                if (chopThis.getTopInOriginalImg() < nTop) {
                    nTop = chopThis.getTopInOriginalImg();
                }
                if (chopThis.getRightP1InOriginalImg() > nRightP1) {
                    nRightP1 = chopThis.getRightP1InOriginalImg();
                }
                if (chopThis.getBottomP1InOriginalImg() > nBottomP1) {
                    nBottomP1 = chopThis.getBottomP1InOriginalImg();
                }
            }
            if (nLeft != Integer.MAX_VALUE && nTop != Integer.MAX_VALUE && nRightP1 != Integer.MIN_VALUE && nBottomP1 != Integer.MIN_VALUE) {
                serCand.setSERPlace(nLeft, nTop, nRightP1 - nLeft, nBottomP1 - nTop);
            }
        }

        // do not use isRecognizableChar function, directly compare two recognization results.
        // recognization method 1, do not cut.
        ImageChop imgChopBase = imgChops.mlistChops.get(nExtractedMajorIdx);
        ImageChop imgChopThinned = StrokeFinder.thinImageChop(imgChopBase, true);
        StructExprRecog serReturnCand1 = new StructExprRecog(imgChopBase.mbarrayOriginalImg),
                serReturnCand2 = new StructExprRecog(imgChopBase.mbarrayOriginalImg);
        LinkedList<UnitCandidate> listCandidates = UnitRecognizer.recogChar(imgChopBase, imgChopThinned, dAvgStrokeWidth, -1);
        ImageChop imgChopShrinked = imgChopBase.shrinkImgArray();
        if (listCandidates.size() > 0) {
            serReturnCand1.setStructExprRecog(listCandidates.get(0).mprotoType.mnUnitType, listCandidates.get(0).mprotoType.mstrFont,
                    imgChopBase.getLeftInOriginalImg(), imgChopBase.getTopInOriginalImg(),
                    imgChopBase.mnWidth, imgChopBase.mnHeight, imgChopShrinked,
                    listCandidates.get(0).mdOverallSimilarity);
        } else {
            // we still need to set its place although it is unknown.
            serReturnCand1.setStructExprRecog(UnitProtoType.Type.TYPE_UNKNOWN, serReturnCand1.mstrFont,
                    imgChopBase.getLeftInOriginalImg(), imgChopBase.getTopInOriginalImg(),
                    imgChopBase.mnWidth, imgChopBase.mnHeight, imgChopShrinked,
                    serReturnCand1.mdSimilarity);
        }
        LinkedList<ImageChop> listCutChops = new LinkedList<ImageChop>();
        StructExprRecog serMajor = serReturnCand1;
        if (serReturnCand1.mType != UnitProtoType.Type.TYPE_SQRT_LONG && serReturnCand1.mType != UnitProtoType.Type.TYPE_SQRT_TALL
                && serReturnCand1.mType != UnitProtoType.Type.TYPE_SQRT_VERY_TALL) {
            // very tall or high chars have disadvantage comparing to cut-recog. We know that only sqrt needs extract to recog and
            // it could be very high or tall. So if see very high or tall char, do not cut to recog.
            serReturnCand2 = disconnect2Recog(imgChops, 1, imgChops.mlistChops.indexOf(imgChopBase), dAvgStrokeWidth, serReturnCand1, listCutChops, nStackLvl + 1);
            // now we compare which one is better.
            serMajor = selectSERFromCands(serReturnCand1, serReturnCand2);
            if (serMajor == serReturnCand1) {
                listCutChops = new LinkedList<ImageChop>(); // this means we do not need cut chops any longer.
            }
        }

        if (serMajor.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                && (serMajor.mType == UnitProtoType.Type.TYPE_SQRT_LEFT
                || serMajor.mType == UnitProtoType.Type.TYPE_SQRT_SHORT
                || serMajor.mType == UnitProtoType.Type.TYPE_SQRT_MEDIUM
                || serMajor.mType == UnitProtoType.Type.TYPE_SQRT_LONG
                || serMajor.mType == UnitProtoType.Type.TYPE_SQRT_TALL
                || serMajor.mType == UnitProtoType.Type.TYPE_SQRT_VERY_TALL)) {
            // sqrt, it has been in minimum container
            LinkedList<ImageChop> listUpperLeft = new LinkedList<ImageChop>();
            LinkedList<ImageChop> listUnder = new LinkedList<ImageChop>();
            for (int idx = 0; idx < imgChops.mlistChops.size(); idx++) {
                if (idx == nExtractedMajorIdx) {
                    continue;
                }
                ImageChop imgChopThis = imgChops.mlistChops.get(idx);
                double dImgChopCentralX = imgChopThis.getLeftInOriginalImg() + imgChopThis.mnWidth / 2.0;
                double dImgChopCentralY = imgChopThis.getTopInOriginalImg() + imgChopThis.mnHeight / 2.0;
                int nCentralXInBaseThis = imgChopBase.mapOriginalXIdx2This((int) dImgChopCentralX);
                int nCentralYInBaseThis = imgChopBase.mapOriginalYIdx2This((int) dImgChopCentralY);
                boolean bIsUnder = false;
                if (nCentralXInBaseThis < imgChopBase.getRightPlus1() && nCentralYInBaseThis < imgChopBase.getBottomPlus1()) {
                    for (int idx1 = imgChopBase.mnTop; idx1 <= nCentralYInBaseThis; idx1++) {
                        if (imgChopBase.mbarrayImg[nCentralXInBaseThis][idx1] == 1) {
                            bIsUnder = true;
                            break;
                        }
                    }
                } else {
                    bIsUnder = true;
                }
                if (bIsUnder == false && nCentralXInBaseThis >= (imgChopBase.mnLeft + imgChopBase.mnWidth / 2.0)) {
                    // in the right side, should be under
                    bIsUnder = true;
                }
                if (!bIsUnder) {
                    listUpperLeft.add(imgChopThis);
                } else {
                    listUnder.add(imgChopThis);
                }
            }

            StructExprRecog serRootLvl = null;
            if (listUpperLeft.size() == 1) {
                ImageChop imgChopRootLvl = listUpperLeft.getFirst();
                serRootLvl = recognize(imgChopRootLvl, null, -1, 0, nStackLvl + 1);
            } else if (listUpperLeft.size() > 1) {
                ImageChop imgChopRootLvl = ExprSeperator.mergeImgChopsWithSameOriginal(listUpperLeft);
                serRootLvl = recognize(imgChopRootLvl, null, -1, 0, nStackLvl + 1); // could be upper left note with thin strokes, do not pass average stroke width here.
            }
            ImageChop imgChopRooted = (listUnder.size() == 1) ? listUnder.getFirst() : ExprSeperator.mergeImgChopsWithSameOriginal(listUnder); // size == 0 can be handled as well.
            StructExprRecog serRooted = recognize(imgChopRooted, null, -1, dAvgStrokeWidth, nStackLvl + 1); // average stroke width can be passed here
            LinkedList<StructExprRecog> listRootChildren = new LinkedList<StructExprRecog>();
            if (serRootLvl != null) {
                listRootChildren.add(serRootLvl);
            }
            listRootChildren.add(serMajor);
            listRootChildren.add(serRooted);
            serCand.setStructExprRecog(listRootChildren, StructExprRecog.EXPRRECOGTYPE_GETROOT);
        } else {
            // seems to be base of foot note or head note.
            // the max image chop has been in minimum container so left top should both be 0.
            int nLeftOfRightList = Integer.MAX_VALUE;
            int nRightOfLeftList = Integer.MIN_VALUE;
            LinkedList<ImageChop> listLeft = new LinkedList<ImageChop>();
            LinkedList<ImageChop> listRight = new LinkedList<ImageChop>();
            LinkedList<ImageChop> listTop = new LinkedList<ImageChop>();
            LinkedList<ImageChop> listBottom = new LinkedList<ImageChop>();
            LinkedList<ImageChop> listUpperLeft = new LinkedList<ImageChop>();
            LinkedList<ImageChop> listLowerLeft = new LinkedList<ImageChop>();
            LinkedList<ImageChop> listUpperRight = new LinkedList<ImageChop>();
            LinkedList<ImageChop> listLowerRight = new LinkedList<ImageChop>();
            LinkedList<ImageChop> listRemergedImgChops = new LinkedList<ImageChop>();
            listRemergedImgChops.add(imgChopBase);
            for (int idx = 0; idx < imgChops.mlistChops.size(); idx++) {
                if (idx == nExtractedMajorIdx) {
                    continue;
                }

                ImageChop imgChopThis = imgChops.mlistChops.get(idx);
                listRemergedImgChops.add(imgChopThis);
                double dImgChopBaseCentralX = imgChopBase.getLeftInOriginalImg() + imgChopBase.mnWidth / 2.0;
                double dImgChopCentralX = imgChopThis.getLeftInOriginalImg() + imgChopThis.mnWidth / 2.0;
                double dImgChopCentralY = imgChopThis.getTopInOriginalImg() + imgChopThis.mnHeight / 2.0;
                double dLeftThresh = imgChopBase.getLeftInOriginalImg();    // base char's left right edge determines cap or left upper note.
                double dRightThresh = imgChopBase.getRightInOriginalImg();
                BLUCharIdentifier bluCIMajor = new BLUCharIdentifier(serMajor);

                if (dImgChopCentralX > dRightThresh && bluCIMajor.isUpperNote(imgChopThis)) {
                    listUpperRight.add(imgChopThis);
                } else if (imgChopThis.getRightInOriginalImg() <= dImgChopBaseCentralX
                        && imgChopThis.getLeftInOriginalImg() < dLeftThresh // do not use dImgChopCentralX < dLeftThresh to avoid that base char is Itanlian like /
                        && bluCIMajor.isUpperNote(imgChopThis)) {
                    listUpperLeft.add(imgChopThis);
                } else if (dImgChopCentralX < dLeftThresh && bluCIMajor.isLowerNote(imgChopThis)) {
                    listLowerLeft.add(imgChopThis);
                } else if (imgChopThis.getLeftInOriginalImg() >= dImgChopBaseCentralX
                        && imgChopThis.getRightInOriginalImg() > dRightThresh // do not use dImgChopCentralX > dRightThresh to avoid that base char is Itanlian like /
                        && bluCIMajor.isLowerNote(imgChopThis)) {
                    listLowerRight.add(imgChopThis);
                } else if ((serMajor.getUnitType() == UnitProtoType.Type.TYPE_INTEGRATE
                        || serMajor.getUnitType() == UnitProtoType.Type.TYPE_INTEGRATE_CIRCLE) // for integrate thing is a bit different coz lower note can be very left.
                        && imgChopThis.getLeftInOriginalImg() >= imgChopBase.getLeftInOriginalImg() + imgChopBase.mnWidth * ConstantsMgr.msdItalianIntegrateLowerNoteMostLeft
                        && imgChopThis.getRightInOriginalImg() > imgChopBase.getLeftInOriginalImg() + imgChopBase.mnWidth * ConstantsMgr.msdItalianIntegrateLowerNoteMostRight // do not use dImgChopCentralX > dRightThresh to avoid that base char is Itanlian like /
                        && bluCIMajor.isLowerNote(imgChopThis) && imgChopThis.getTopInOriginalImg() < imgChopBase.getBottomP1InOriginalImg()) {
                    // for integrate, also need to make sure it is not under note because integrate may have under notes
                    listLowerRight.add(imgChopThis);
                } else if (dImgChopCentralX > dRightThresh) {
                    listRight.add(imgChopThis);
                    if (imgChopThis.getLeftInOriginalImg() < nLeftOfRightList) {
                        nLeftOfRightList = imgChopThis.getLeftInOriginalImg();
                    }
                } else if (dImgChopCentralX < dLeftThresh) {
                    listLeft.add(imgChopThis);
                    if (imgChopThis.getRightInOriginalImg() > nRightOfLeftList) {
                        nRightOfLeftList = imgChopThis.getRightInOriginalImg();
                    }
                } else if (dImgChopCentralY < imgChopBase.getTopInOriginalImg()) {
                    listTop.add(imgChopThis);
                } else if (dImgChopCentralY > imgChopBase.getBottomInOriginalImg()) {
                    listBottom.add(imgChopThis);
                } else if (serMajor.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE) {    // base is a single char
                    // this seems to be a broken stroke, merge into base imgchop
                    LinkedList<ImageChop> listBaseThis = new LinkedList<ImageChop>();
                    listBaseThis.add(listRemergedImgChops.getFirst());
                    listBaseThis.add(imgChopThis);
                    ImageChop imgChopNewBase = ExprSeperator.mergeImgChopsWithSameOriginal(listBaseThis);
                    listRemergedImgChops.removeFirst(); // remove old base
                    listRemergedImgChops.removeLast(); // remove this
                    listRemergedImgChops.addFirst(imgChopNewBase); // add new base.
                } else {    // base is not a single char.
                    double dMajorWeightedCentralX = 0, dMajorWeightSum = 0;
                    for (int idx3 = 0; idx3 < serMajor.mlistChildren.size(); idx3++) {
                        StructExprRecog serMajorPart = serMajor.mlistChildren.get(idx3);
                        dMajorWeightedCentralX += serMajorPart.mnHeight * (serMajorPart.mnLeft + serMajorPart.mnWidth / 2.0);
                        dMajorWeightSum += serMajorPart.mnHeight;
                    }
                    if (dMajorWeightSum != 0) {
                        dMajorWeightedCentralX /= dMajorWeightSum;
                    }
                    if (dImgChopCentralX >= dMajorWeightedCentralX) {
                        listRight.add(imgChopThis);
                        if (imgChopThis.getLeftInOriginalImg() < nLeftOfRightList) {
                            nLeftOfRightList = imgChopThis.getLeftInOriginalImg();
                        }
                    } else {
                        listLeft.add(imgChopThis);
                        if (imgChopThis.getRightInOriginalImg() > nRightOfLeftList) {
                            nRightOfLeftList = imgChopThis.getRightInOriginalImg();
                        }
                    }
                }
            }

            if (listRemergedImgChops.size() < imgChops.mlistChops.size()) {
                // has remerge.
                if (listRemergedImgChops.size() > 1) {
                    ImageChops imgChopsRemerged = new ImageChops();
                    imgChopsRemerged.mlistChops = listRemergedImgChops;
                    serCand = extract2Recog(imgChopsRemerged, 0, dAvgStrokeWidth, nStackLvl + 1);
                } else {
                    // a single char
                    imgChopThinned = StrokeFinder.thinImageChop(listRemergedImgChops.get(0), true);
                    listCandidates = UnitRecognizer.recogChar(listRemergedImgChops.get(0), imgChopThinned, dAvgStrokeWidth, -1);
                    if (listCandidates.size() > 0) {
                        // need not to set serplace because it has been set.
                        serCand.setStructExprRecog(listCandidates.get(0).mprotoType.mnUnitType, listCandidates.get(0).mprotoType.mstrFont, listRemergedImgChops.get(0));
                        serCand.setSimilarity(listCandidates.get(0).mdOverallSimilarity);
                    } else {
                        // we still need to set imgChop here.
                        serCand.setStructExprRecog(UnitProtoType.Type.TYPE_UNKNOWN, serCand.getFont(), listRemergedImgChops.get(0));
                        // the similarity has been initialized to 1, so need not to reset it.
                    }
                }
            } else {
                // no remerge.
                // now need to double check the upper right, upper left, lower right and lower left, put them
                // in to left and right if needed.
                for (int idx = listUpperRight.size() - 1; idx >= 0; idx--) {
                    if (listUpperRight.get(idx).getLeftInOriginalImg() > nLeftOfRightList) {
                        ImageChop imgChopThis = listUpperRight.remove(idx);
                        listRight.add(imgChopThis);
                    }
                }
                for (int idx = listLowerRight.size() - 1; idx >= 0; idx--) {
                    if (listLowerRight.get(idx).getLeftInOriginalImg() > nLeftOfRightList) {
                        ImageChop imgChopThis = listLowerRight.remove(idx);
                        listRight.add(imgChopThis);
                    }
                }
                for (int idx = listUpperLeft.size() - 1; idx >= 0; idx--) {
                    if (listUpperLeft.get(idx).getRightInOriginalImg() < nRightOfLeftList) {
                        ImageChop imgChopThis = listUpperLeft.remove(idx);
                        listLeft.add(imgChopThis);
                    }
                }
                for (int idx = listLowerLeft.size() - 1; idx >= 0; idx--) {
                    if (listLowerLeft.get(idx).getRightInOriginalImg() < nRightOfLeftList) {
                        ImageChop imgChopThis = listLowerLeft.remove(idx);
                        listLeft.add(imgChopThis);
                    }
                }

                ImageChop imgChopUpperLeft, imgChopUpperRight, imgChopLowerLeft, imgChopLowerRight, imgChopLeft, imgChopRight, imgChopTop, imgChopBottom;
                imgChopUpperLeft = imgChopUpperRight = imgChopLowerLeft = imgChopLowerRight = imgChopLeft = imgChopRight = imgChopTop = imgChopBottom = new ImageChop();
                // if upper or lower notes, we always don't pass in the average stroke width coz
                // notes size may larger than 2 or 3 * average stroke width. But its actual stroke
                // width is still small.

                StructExprRecog serLeft = null, serRight = null, serUpperLeft = null, serUpperRight = null, serLowerLeft = null, serLowerRight = null,
                        serTop = null, serBottom = null;
                if (listCutChops.size() == 2 && serMajor.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VBLANKCUT) {
                    //if serMajor is h-blank cut recognized by disconnect2Recog, size of children list should be 2.
                    LinkedList<ImageChop> listAllLeft = new LinkedList<ImageChop>();
                    listAllLeft.addAll(listUpperLeft);
                    listAllLeft.addAll(listLeft);
                    listAllLeft.addAll(listLowerLeft);
                    LinkedList<ImageChop> listAllRight = new LinkedList<ImageChop>();
                    listAllRight.addAll(listUpperRight);
                    listAllRight.addAll(listRight);
                    listAllRight.addAll(listLowerRight);
                    for (int idxTop = 0; idxTop < listTop.size(); idxTop++) {
                        ImageChop imgThis = listTop.get(idxTop);
                        if (imgThis.getLeftInOriginalImg() < (listCutChops.getFirst().getLeftInOriginalImg()
                                + listCutChops.getFirst().getRightP1InOriginalImg()) / 2.0
                                && imgThis.getRightP1InOriginalImg() > (listCutChops.getLast().getLeftInOriginalImg()
                                + listCutChops.getLast().getRightP1InOriginalImg()) / 2.0) {
                            continue;   // top of both.
                        } else if ((listCutChops.getFirst().getRightP1InOriginalImg() - imgThis.getLeftInOriginalImg())
                                >= (imgThis.getRightP1InOriginalImg() - listCutChops.getLast().getLeftInOriginalImg())) {
                            listAllLeft.add(imgThis);
                            listTop.remove(idxTop);
                            idxTop--;
                        } else {
                            listAllRight.add(imgThis);
                            listTop.remove(idxTop);
                            idxTop--;
                        }
                    }
                    for (int idxBottom = 0; idxBottom < listBottom.size(); idxBottom++) {
                        ImageChop imgThis = listBottom.get(idxBottom);
                        if (imgThis.getLeftInOriginalImg() < (listCutChops.getFirst().getLeftInOriginalImg()
                                + listCutChops.getFirst().getRightP1InOriginalImg()) / 2.0
                                && imgThis.getRightP1InOriginalImg() > (listCutChops.getLast().getLeftInOriginalImg()
                                + listCutChops.getLast().getRightP1InOriginalImg()) / 2.0) {
                            continue;   // bottom of both.
                        } else if ((listCutChops.getFirst().getRightP1InOriginalImg() - imgThis.getLeftInOriginalImg())
                                >= (imgThis.getRightP1InOriginalImg() - listCutChops.getLast().getLeftInOriginalImg())) {
                            listAllLeft.add(imgThis);
                            listBottom.remove(idxBottom);
                            idxBottom--;
                        } else {
                            listAllRight.add(imgThis);
                            listBottom.remove(idxBottom);
                            idxBottom--;
                        }
                    }
                    if (listAllLeft.size() > 0) {
                        listAllLeft.add(listCutChops.getFirst());
                        imgChopLeft = ExprSeperator.mergeImgChopsWithSameOriginal(listAllLeft);
                        serLeft = recognize(imgChopLeft, null, -1, dAvgStrokeWidth, nStackLvl + 1);
                    } else {
                        serLeft = serMajor.mlistChildren.getFirst();
                    }
                    if (listAllRight.size() > 0) {
                        listAllRight.add(listCutChops.getLast());
                        imgChopRight = ExprSeperator.mergeImgChopsWithSameOriginal(listAllRight);
                        serRight = recognize(imgChopRight, null, -1, dAvgStrokeWidth, nStackLvl + 1);
                    } else {
                        serRight = serMajor.mlistChildren.getLast();
                    }
                    if (listTop.size() > 0) {
                        imgChopTop = (listTop.size() == 1) ? listTop.getFirst() : ExprSeperator.mergeImgChopsWithSameOriginal(listTop);
                        serTop = recognize(imgChopTop, null, -1, 0, nStackLvl + 1);
                    }
                    if (listBottom.size() > 0) {
                        imgChopBottom = (listBottom.size() == 1) ? listBottom.getFirst() : ExprSeperator.mergeImgChopsWithSameOriginal(listBottom);
                        serBottom = recognize(imgChopBottom, null, -1, 0, nStackLvl + 1);
                    }
                    LinkedList<StructExprRecog> listVBlankCut = new LinkedList<StructExprRecog>();
                    listVBlankCut.add(serLeft);
                    listVBlankCut.add(serRight);
                    serCand.setStructExprRecog(listVBlankCut, StructExprRecog.EXPRRECOGTYPE_VBLANKCUT);
                    if (serTop != null && serBottom != null) {
                        StructExprRecog serNewCand = new StructExprRecog(imgChops.mlistChops.get(nExtractedMajorIdx).mbarrayOriginalImg);
                        LinkedList<StructExprRecog> listChildren = new LinkedList<StructExprRecog>();
                        listChildren.add(serTop);
                        listChildren.add(serCand);
                        listChildren.add(serBottom);
                        serNewCand.setStructExprRecog(listChildren, StructExprRecog.EXPRRECOGTYPE_HCUTCAPUNDER);
                        serCand = serNewCand;
                    } else if (serTop != null) {
                        StructExprRecog serNewCand = new StructExprRecog(imgChops.mlistChops.get(nExtractedMajorIdx).mbarrayOriginalImg);
                        LinkedList<StructExprRecog> listChildren = new LinkedList<StructExprRecog>();
                        listChildren.add(serTop);
                        listChildren.add(serCand);
                        if (serCand.getArea() * ConstantsMgr.msdMajorArea2TopBtmThresh < serTop.getArea()) {
                            // major part is too small, so major seems like cap.
                            serNewCand.setStructExprRecog(listChildren, StructExprRecog.EXPRRECOGTYPE_HCUTUNDER);
                        } else {
                            serNewCand.setStructExprRecog(listChildren, StructExprRecog.EXPRRECOGTYPE_HCUTCAP);
                        }
                        serCand = serNewCand;
                    } else if (serBottom != null) {
                        StructExprRecog serNewCand = new StructExprRecog(imgChops.mlistChops.get(nExtractedMajorIdx).mbarrayOriginalImg);
                        LinkedList<StructExprRecog> listChildren = new LinkedList<StructExprRecog>();
                        listChildren.add(serCand);
                        listChildren.add(serBottom);
                        if (serCand.getArea() * ConstantsMgr.msdMajorArea2TopBtmThresh < serBottom.getArea()) {
                            // major part is too small, so major seems like cap.
                            serNewCand.setStructExprRecog(listChildren, StructExprRecog.EXPRRECOGTYPE_HCUTCAP);
                        } else {
                            serNewCand.setStructExprRecog(listChildren, StructExprRecog.EXPRRECOGTYPE_HCUTUNDER);
                        }
                        serCand = serNewCand;
                    }
                } else {
                    // if serMajor is a single char, or say is not recognized by disconnect2Recog.
                    if (listUpperLeft.size() > 0) {
                        imgChopUpperLeft = (listUpperLeft.size() == 1) ? listUpperLeft.getFirst() : ExprSeperator.mergeImgChopsWithSameOriginal(listUpperLeft);
                        serUpperLeft = recognize(imgChopUpperLeft, null, -1, 0, nStackLvl + 1);
                    }
                    if (listUpperRight.size() > 0) {
                        imgChopUpperRight = (listUpperRight.size() == 1) ? listUpperRight.getFirst() : ExprSeperator.mergeImgChopsWithSameOriginal(listUpperRight);
                        serUpperRight = recognize(imgChopUpperRight, null, -1, 0, nStackLvl + 1);
                    }
                    if (listLowerLeft.size() > 0) {
                        imgChopLowerLeft = (listLowerLeft.size() == 1) ? listLowerLeft.getFirst() : ExprSeperator.mergeImgChopsWithSameOriginal(listLowerLeft);
                        serLowerLeft = recognize(imgChopLowerLeft, null, -1, 0, nStackLvl + 1);
                    }
                    if (listLowerRight.size() > 0) {
                        imgChopLowerRight = (listLowerRight.size() == 1) ? listLowerRight.getFirst() : ExprSeperator.mergeImgChopsWithSameOriginal(listLowerRight);
                        serLowerRight = recognize(imgChopLowerRight, null, -1, 0, nStackLvl + 1);
                    }
                    // pass average stroke width to left and right
                    if (listLeft.size() > 0) {
                        imgChopLeft = (listLeft.size() == 1) ? listLeft.getFirst() : ExprSeperator.mergeImgChopsWithSameOriginal(listLeft);
                        serLeft = recognize(imgChopLeft, null, -1, dAvgStrokeWidth, nStackLvl + 1);
                    }
                    if (listRight.size() > 0) {
                        imgChopRight = (listRight.size() == 1) ? listRight.getFirst() : ExprSeperator.mergeImgChopsWithSameOriginal(listRight);
                        serRight = recognize(imgChopRight, null, -1, dAvgStrokeWidth, nStackLvl + 1);
                    }
                    // do not pass average stroke width to top and bottom because they may be 
                    // part of head or foot notes.
                    if (listTop.size() > 0) {
                        imgChopTop = (listTop.size() == 1) ? listTop.getFirst() : ExprSeperator.mergeImgChopsWithSameOriginal(listTop);
                        serTop = recognize(imgChopTop, null, -1, 0, nStackLvl + 1);
                    }
                    if (listBottom.size() > 0) {
                        imgChopBottom = (listBottom.size() == 1) ? listBottom.getFirst() : ExprSeperator.mergeImgChopsWithSameOriginal(listBottom);
                        serBottom = recognize(imgChopBottom, null, -1, 0, nStackLvl + 1);
                    }

                    if (serTop != null && serBottom != null) {
                        StructExprRecog serNewMajor = new StructExprRecog(imgChops.mlistChops.get(nExtractedMajorIdx).mbarrayOriginalImg);
                        LinkedList<StructExprRecog> listChildren = new LinkedList<StructExprRecog>();
                        listChildren.add(serTop);
                        listChildren.add(serMajor);
                        listChildren.add(serBottom);
                        serNewMajor.setStructExprRecog(listChildren, StructExprRecog.EXPRRECOGTYPE_HCUTCAPUNDER);
                        serMajor = serNewMajor;
                    } else if (serTop != null) {
                        StructExprRecog serNewMajor = new StructExprRecog(imgChops.mlistChops.get(nExtractedMajorIdx).mbarrayOriginalImg);
                        LinkedList<StructExprRecog> listChildren = new LinkedList<StructExprRecog>();
                        listChildren.add(serTop);
                        listChildren.add(serMajor);
                        if (serMajor.getArea() * ConstantsMgr.msdMajorArea2TopBtmThresh < serTop.getArea()) {
                            // major part is too small, so major seems like under.
                            serNewMajor.setStructExprRecog(listChildren, StructExprRecog.EXPRRECOGTYPE_HCUTUNDER);
                        } else {
                            serNewMajor.setStructExprRecog(listChildren, StructExprRecog.EXPRRECOGTYPE_HCUTCAP);
                        }
                        serMajor = serNewMajor;
                    } else if (serBottom != null) {
                        StructExprRecog serNewMajor = new StructExprRecog(imgChops.mlistChops.get(nExtractedMajorIdx).mbarrayOriginalImg);
                        LinkedList<StructExprRecog> listChildren = new LinkedList<StructExprRecog>();
                        listChildren.add(serMajor);
                        listChildren.add(serBottom);
                        if (serMajor.getArea() * ConstantsMgr.msdMajorArea2TopBtmThresh < serBottom.getArea()) {
                            // major part is too small, so major seems like cap.
                            serNewMajor.setStructExprRecog(listChildren, StructExprRecog.EXPRRECOGTYPE_HCUTCAP);
                        } else {
                            serNewMajor.setStructExprRecog(listChildren, StructExprRecog.EXPRRECOGTYPE_HCUTUNDER);
                        }
                        serMajor = serNewMajor;
                    }
                    LinkedList<StructExprRecog> listVBlankCut = new LinkedList<StructExprRecog>();
                    if (serLeft != null) {
                        listVBlankCut.add(serLeft);
                    }
                    if (serLowerLeft != null) {
                        listVBlankCut.add(serLowerLeft);
                    }
                    if (serMajor.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                            && (serMajor.mType == UnitProtoType.Type.TYPE_SMALL_C
                            || serMajor.mType == UnitProtoType.Type.TYPE_BIG_C)
                            && (serUpperLeft != null && serUpperLeft.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                            && (serUpperLeft.mType == UnitProtoType.Type.TYPE_SMALL_O
                            || serUpperLeft.mType == UnitProtoType.Type.TYPE_BIG_O
                            || serUpperLeft.mType == UnitProtoType.Type.TYPE_ZERO))) {
                        StructExprRecog serCelsius = new StructExprRecog(imgChops.mlistChops.get(nExtractedMajorIdx).mbarrayOriginalImg);
                        int nLeft = Math.min(imgChopBase.getLeftInOriginalImg(),
                                imgChopUpperLeft.getLeftInOriginalImg());   // imgChopUpperLeft in this case must not be null or empty.
                        int nTop = Math.min(imgChopBase.getTopInOriginalImg(),
                                imgChopUpperLeft.getTopInOriginalImg());
                        int nRightPlus1 = Math.max(imgChopBase.getRightP1InOriginalImg(),
                                imgChopUpperLeft.getRightP1InOriginalImg());
                        int nBottomPlus1 = Math.max(imgChopBase.getBottomP1InOriginalImg(),
                                imgChopUpperLeft.getBottomP1InOriginalImg());
                        LinkedList<ImageChop> listParts = new LinkedList<ImageChop>();
                        listParts.add(imgChopUpperLeft);
                        listParts.add(imgChopBase);
                        ImageChop imgChop4SER = ExprSeperator.mergeImgChopsWithSameOriginal(listParts);   // need not to shrink imgChop4SER because it has been min container.
                        int nTotalArea = serMajor.getArea() + serUpperLeft.getArea();
                        double dSimilarity = (serMajor.getArea() * serMajor.mdSimilarity + serUpperLeft.getArea() * serUpperLeft.mdSimilarity)
                                / nTotalArea;  // total area should not be zero here.
                        serCelsius.setStructExprRecog(UnitProtoType.Type.TYPE_CELCIUS, StructExprRecog.UNKNOWN_FONT_TYPE,
                                nLeft, nTop, nRightPlus1 - nLeft, nBottomPlus1 - nTop, imgChop4SER, dSimilarity);
                        listVBlankCut.add(serCelsius);
                    } else if (serMajor.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                            && serMajor.mType == UnitProtoType.Type.TYPE_BIG_F
                            && (serUpperLeft != null && serUpperLeft.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                            && (serUpperLeft.mType == UnitProtoType.Type.TYPE_SMALL_O
                            || serUpperLeft.mType == UnitProtoType.Type.TYPE_BIG_O
                            || serUpperLeft.mType == UnitProtoType.Type.TYPE_ZERO))) {
                        StructExprRecog serFahrenheit = new StructExprRecog(imgChops.mlistChops.get(nExtractedMajorIdx).mbarrayOriginalImg);
                        int nLeft = Math.min(imgChopBase.getLeftInOriginalImg(),
                                imgChopUpperLeft.getLeftInOriginalImg());   // imgChopUpperLeft in this case must not be null or empty.
                        int nTop = Math.min(imgChopBase.getTopInOriginalImg(),
                                imgChopUpperLeft.getTopInOriginalImg());
                        int nRightPlus1 = Math.max(imgChopBase.getRightP1InOriginalImg(),
                                imgChopUpperLeft.getRightP1InOriginalImg());
                        int nBottomPlus1 = Math.max(imgChopBase.getBottomP1InOriginalImg(),
                                imgChopUpperLeft.getBottomP1InOriginalImg());
                        LinkedList<ImageChop> listParts = new LinkedList<ImageChop>();
                        listParts.add(imgChopUpperLeft);
                        listParts.add(imgChopBase);
                        ImageChop imgChop4SER = ExprSeperator.mergeImgChopsWithSameOriginal(listParts);   // need not to shrink imgChop4SER because it has been min container.
                        int nTotalArea = serMajor.getArea() + serUpperLeft.getArea();
                        double dSimilarity = (serMajor.getArea() * serMajor.mdSimilarity + serUpperLeft.getArea() * serUpperLeft.mdSimilarity)
                                / nTotalArea;  // total area should not be zero here.
                        serFahrenheit.setStructExprRecog(UnitProtoType.Type.TYPE_FAHRENHEIT, StructExprRecog.UNKNOWN_FONT_TYPE,
                                nLeft, nTop, nRightPlus1 - nLeft, nBottomPlus1 - nTop, imgChop4SER, dSimilarity);
                        listVBlankCut.add(serFahrenheit);
                    } else {
                        if (serUpperLeft != null) {
                            listVBlankCut.add(serUpperLeft);
                        }
                        listVBlankCut.add(serMajor);
                    }
                    if (serLowerRight != null) {
                        listVBlankCut.add(serLowerRight);
                    }
                    if (serUpperRight != null) {
                        listVBlankCut.add(serUpperRight);
                    }
                    if (serRight != null) {
                        listVBlankCut.add(serRight);
                    }
                    if (listVBlankCut.size() > 1) {
                        serCand.setStructExprRecog(listVBlankCut, StructExprRecog.EXPRRECOGTYPE_VBLANKCUT);
                    } else if (listVBlankCut.size() == 1) {
                        serCand = listVBlankCut.getFirst();
                    }
                }
            }
        }

        return serCand;
    }

    //不能被很好的识别吗？
    public static boolean canNotBeGoodRecog(StructExprRecog ser) {
        if (ser.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE) {
            if (ser.mType == UnitProtoType.Type.TYPE_INFINITE && ser.mnWidth / (double) ser.mnHeight < ConstantsMgr.msdInfiniteWOverHThresh) {
                return true;    // width of infinite is too narrow comparing to height.
            }
        }
        return false;
    }

    //找到最大公差
    public static double[] findMaxToleranceCut2Recog(StructExprRecog ser) {
        double[] darrayReturn = new double[2]; // first double means number of strokes that can be cut, second means max number of stroke width that can be cut
        if (ser.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE) {
            if (ser.mType == UnitProtoType.Type.TYPE_INFINITE && ser.mnWidth / (double) ser.mnHeight < ConstantsMgr.msdInfiniteWOverHThresh) {
                darrayReturn[0] = 2;
                darrayReturn[1] = 1.5;  // for example, connected stroke of 60 could be wider than average stroke width.
            } else {
                darrayReturn[0] = 2;
                darrayReturn[1] = 1;
            }
        }
        return darrayReturn;
    }

    //分离来识别
    public static StructExprRecog disconnect2Recog(ImageChops imgChopsFrom, int nCutMode, int nThisIdx, double dAvgStrokeWidth, StructExprRecog serCand1, LinkedList<ImageChop> listCutChops, int nStackLvl) throws ExprRecognizeException, InterruptedException, IOException {
        if (nStackLvl >= MAX_RECOGNIZING_STACK_COUNT) {
            throw new ExprRecognizeException(TOO_DEEP_CALL_STACK);
        }
        if (nCutMode != 1) {
            return serCand1;    // not vertically cut
        } else if (serCand1.mdSimilarity <= ConstantsMgr.msdGoodRecogCharThresh && !canNotBeGoodRecog(serCand1)) {
            // should not be <, must be <= because the quick identified chars have overall simiarities equal to ConstantsMgr.msdGoodRecogCharThresh
            return serCand1;    // no need to disconnect to recognize
        } else if (imgChopsFrom == null || imgChopsFrom.mlistChops.size() == 0) {
            return serCand1;
        }
        ImageChop imgChop2Cut = imgChopsFrom.mlistChops.get(nThisIdx);
        double dMinCharWidthMultiStrokes = 4.0;
        if (serCand1.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE) {
            if (serCand1.mType == UnitProtoType.Type.TYPE_SMALL_M || serCand1.mType == UnitProtoType.Type.TYPE_BIG_M
                    || serCand1.mType == UnitProtoType.Type.TYPE_SMALL_W || serCand1.mType == UnitProtoType.Type.TYPE_BIG_W
                    || serCand1.mType == UnitProtoType.Type.TYPE_SMALL_OMEGA) {
                // very wide character, the threshold should be wider.
                dMinCharWidthMultiStrokes = 5.0;
            } else if (serCand1.mType == UnitProtoType.Type.TYPE_SMALL_PI || serCand1.mType == UnitProtoType.Type.TYPE_BIG_PI
                    || serCand1.mType == UnitProtoType.Type.TYPE_SMALL_U || serCand1.mType == UnitProtoType.Type.TYPE_BIG_U
                    || serCand1.mType == UnitProtoType.Type.TYPE_SMALL_N || serCand1.mType == UnitProtoType.Type.TYPE_BIG_N
                    || serCand1.mType == UnitProtoType.Type.TYPE_SMALL_K || serCand1.mType == UnitProtoType.Type.TYPE_BIG_K
                    || serCand1.mType == UnitProtoType.Type.TYPE_SMALL_H || serCand1.mType == UnitProtoType.Type.TYPE_BIG_H
                    || serCand1.mType == UnitProtoType.Type.TYPE_BIG_OMEGA || serCand1.mType == UnitProtoType.Type.TYPE_BIG_L) {
                // wide character, the threshold should be wider.
                dMinCharWidthMultiStrokes = 4.5;
            }
        }
        if (imgChop2Cut.mnWidth < dMinCharWidthMultiStrokes * dAvgStrokeWidth) {
            // too narrow imgchop, should not cut
            return serCand1;
        }

        double dAvgImgChopWidth = 0, dMinImgChopWidth = Double.MAX_VALUE;
        for (int idx = 0; idx < imgChopsFrom.mlistChops.size(); idx++) {
            dAvgImgChopWidth += imgChopsFrom.mlistChops.get(idx).mnWidth;
            if (dMinImgChopWidth > imgChopsFrom.mlistChops.get(idx).mnWidth) {
                dMinImgChopWidth = imgChopsFrom.mlistChops.get(idx).mnWidth;
            }
        }
        dAvgImgChopWidth /= imgChopsFrom.mlistChops.size();

        int nWindowWidth = (int) Math.min(dAvgImgChopWidth / 4.0, dMinImgChopWidth / 2.0);
        nWindowWidth = (int) Math.max(nWindowWidth, Math.ceil(dAvgStrokeWidth));

        double[] darrayMaxTolerance = findMaxToleranceCut2Recog(serCand1);
        ImageChops imgChops = ExprSeperator.cutVerticallyViaMinPath(imgChop2Cut, nWindowWidth, dAvgStrokeWidth, true,
                (int) Math.ceil(dAvgStrokeWidth * darrayMaxTolerance[1]), (int) darrayMaxTolerance[0]);
        if (imgChops.mlistChops.size() < 2) {
            // cannot chop
            return serCand1;
        }

        // ok, now lets divide and recognize.
        // do not pass average stroke here because they may be notes
        StructExprRecog serLeft = recognize(imgChops.mlistChops.getFirst(), imgChops, nCutMode, 0, nStackLvl + 1);
        StructExprRecog serRight = recognize(imgChops.mlistChops.getLast(), imgChops, nCutMode, 0, nStackLvl + 1);

        LinkedList<StructExprRecog> listSers = new LinkedList<StructExprRecog>();
        listSers.add(serLeft);
        listCutChops.add(imgChops.mlistChops.getFirst());
        listSers.add(serRight);
        listCutChops.add(imgChops.mlistChops.getLast());

        StructExprRecog serCand2 = new StructExprRecog(imgChop2Cut.mbarrayOriginalImg);
        serCand2.setStructExprRecog(listSers, StructExprRecog.EXPRRECOGTYPE_VBLANKCUT);
        return serCand2;
    }

    //从两个cands(候选）中选一个ser(结构化表达式）
    public static StructExprRecog selectSERFromCands(StructExprRecog serCand1, StructExprRecog serCand2) {
        if (!(serCand1.isChildListType() ^ serCand2.isChildListType())) {
            return (serCand1.mdSimilarity >= serCand2.mdSimilarity) ? serCand1 : serCand2;
        } else {
            StructExprRecog serDisconnect, serNoDisconnect;
            if (serCand1.isChildListType()) {
                serDisconnect = serCand1;
                serNoDisconnect = serCand2;
            } else {
                serDisconnect = serCand2;
                serNoDisconnect = serCand1;
            }
            if (serNoDisconnect.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    && serNoDisconnect.mType == UnitProtoType.Type.TYPE_INFINITE
                    && serDisconnect.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VBLANKCUT
                    && serDisconnect.mlistChildren.size() == 2
                    && (double) serNoDisconnect.mnWidth / (double) serNoDisconnect.mnHeight < ConstantsMgr.msdInfiniteWOverHThresh) {
                return serDisconnect;   // misrecognized infinite, should be like 00 or 6 8
            } else if (serNoDisconnect.isSqrtTypeChar()
                    && serDisconnect.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VBLANKCUT
                    && serDisconnect.mlistChildren.size() == 2
                    && serDisconnect.mlistChildren.getLast().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    && serDisconnect.mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_SUBTRACT) {
                return serNoDisconnect;   // sqrt may be incorrectly disconnected to sqrt_left and -.
            } else if (serNoDisconnect.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    && serNoDisconnect.mType == UnitProtoType.Type.TYPE_SMALL_U
                    && serDisconnect.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VBLANKCUT
                    && serDisconnect.mlistChildren.size() == 2
                    && serDisconnect.mlistChildren.getFirst().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    && (serDisconnect.mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_ZERO
                    || serDisconnect.mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_SMALL_O
                    || serDisconnect.mlistChildren.getFirst().mType == UnitProtoType.Type.TYPE_BIG_O)
                    && serDisconnect.mlistChildren.getLast().mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    && serDisconnect.mlistChildren.getLast().mType == UnitProtoType.Type.TYPE_DOT) {
                return serDisconnect;   // 0. may be misrecognized as u.
            } else if (serDisconnect.mdSimilarity + ConstantsMgr.msdDisconnectSERDisadv < serNoDisconnect.mdSimilarity) {
                return serDisconnect;
            } else {
                return serNoDisconnect;
            }
        }
    }
}
