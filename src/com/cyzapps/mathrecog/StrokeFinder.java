/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyzapps.mathrecog;

import com.cyzapps.VisualMFP.Position3D;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import com.cyzapps.imgmatrixproc.ImgMatrixOutput;
import com.cyzapps.imgproc.ImageMgr;

import static com.cyzapps.imgmatrixproc.Thin.Xihua;

/**
 *
 * @author tonyc
 */
public class StrokeFinder {
    
    public static class ConnectPntSet   {
        // a group of points which connect strokes.
        public LinkedList<Position3D> mlistPnts = new LinkedList<Position3D>();
        protected double mdAvgX = -1;
        protected double mdAvgY = -1;
        public boolean isPntInSet(Position3D p3D)   {
            for (Position3D pnt : mlistPnts)    {
                if (Math.abs(pnt.getX() - p3D.getX()) <= 1 && Math.abs(pnt.getY() - p3D.getY()) <= 1) {
                    return true;
                }
            }
            return false;
        }
        
        public double getAvgX()  {
            return mdAvgX;
        }
        
        public double getAvgY()  {
            return mdAvgY;
        }
        
        public Position3D getAvgPnt()   {
            if (mlistPnts == null || mlistPnts.size() == 0) {
                mdAvgX = mdAvgY = -1;
                return new Position3D(mdAvgX, mdAvgY);
            }
            double dAvgX = 0, dAvgY = 0;
            for (Position3D pnt : mlistPnts)    {
                dAvgX += pnt.getX();
                dAvgY += pnt.getY();
            }
            mdAvgX = dAvgX / mlistPnts.size();
            mdAvgY = dAvgY / mlistPnts.size();
            return new Position3D(mdAvgX, mdAvgY);
        }
    }
    
    // last bit of n01SwitchMode enabled means convex 1 -> 0 allowed, second last bit is 1 means single point 1 -> 0 allowed,
    // third last bit enabled means end of stroke 1-> 0 allowed, 
    // fifith last bit enabled means concave 0->1 allowed.
    // returned barray is the same size of input barrayImg.
    public static byte[][] smoothStroke(byte[][] barrayImg, int nLeft, int nTop, int nWidth, int nHeight, int n01SwitchMode)	{
		/* To save time, do not validate parameters.
         * 
        if (barrayImg == null || barrayImg.length == 0 || barrayImg[0].length == 0
				|| nLeft < 0 || nTop < 0 || nWidth == 0 || nHeight == 0
				|| nLeft >= barrayImg.length || nTop >= barrayImg[0].length
				|| (nLeft + nWidth) > barrayImg.length || (nTop + nHeight) > barrayImg[0].length
				)	{
			// parameters are invalid.
			return new byte[0][0];
		} */
        // p3 p2 p1
        // p4    p0
        // p5 p6 p7
        byte[][] barrayReturn = new byte[nWidth][nHeight];  // initialize a zero array.
        for (int j = 0; j < nWidth; j ++)  {
            barrayReturn[j] = new byte[nHeight];
            System.arraycopy(barrayImg[nLeft + j], nTop, barrayReturn[j], 0, nHeight);
        }
        
        for (int i = 0; i < nHeight; i ++)  {
            for (int j = 0; j < nWidth; j ++)   {
                byte bP0 = (j == nWidth - 1)?0:barrayImg[nLeft + j + 1][nTop + i];
                byte bP1 = (i == 0)?0:((j == nWidth - 1)?0:barrayImg[nLeft + j + 1][nTop + i - 1]);
                byte bP2 = (i == 0)?0:barrayImg[nLeft + j][nTop + i - 1];
                byte bP3 = (i == 0)?0:((j == 0)?0:barrayImg[nLeft + j - 1][nTop + i - 1]);
                byte bP4 = (j == 0)?0:barrayImg[nLeft + j - 1][nTop + i];
                byte bP5 = (i == nHeight - 1)?0:((j == 0)?0:barrayImg[nLeft + j - 1][nTop + i + 1]);
                byte bP6 = (i == nHeight - 1)?0:barrayImg[nLeft + j][nTop + i + 1];
                byte bP7 = (i == nHeight - 1)?0:((j == nWidth - 1)?0:barrayImg[nLeft + j + 1][nTop + i + 1]);
                // allow 1 to 0
                if (barrayImg[nLeft + j][nTop + i] == 1)    {
                    int nOr012 = bP0 | bP1 | bP2;
                    int nOr456 = bP4 | bP5 | bP6;
                    int nOr234 = bP2 | bP3 | bP4;
                    int nOr670 = bP6 | bP7 | bP0;
                    int nAnd01 = bP0 & bP1;
                    int nAnd12 = bP1 & bP2;
                    int nAnd23 = bP2 & bP3;
                    int nAnd34 = bP3 & bP4;
                    int nAnd45 = bP4 & bP5;
                    int nAnd56 = bP5 & bP6;
                    int nAnd67 = bP6 & bP7;
                    int nAnd70 = bP7 & bP0;
                    int nSum1357 = bP1 + bP3 + bP5 + bP7;
                    int nSum0246 = bP0 + bP2 + bP4 + bP6;

                    if ((nOr012 & nOr456) == 1 || (nOr234 & nOr670) == 1 || (nSum1357 > 1 && nSum0246 == 0))   {
                        continue;
                    } if ((n01SwitchMode & 1) == 1 && (nSum1357 + nSum0246) == 2 && (nAnd01 | nAnd12 | nAnd23 | nAnd34 | nAnd45 | nAnd56 | nAnd67 | nAnd70) == 1)   {
                        // convex 1-> 0
                        barrayReturn[j][i] = 0;
                    } else if ((n01SwitchMode & 2) == 2 && (bP0 + bP1 + bP2 + bP3 + bP4 + bP5 + bP6 + bP7) == 0) {
                        // single point 1->0
                        barrayReturn[j][i] = 0;
                    } else if ((n01SwitchMode & 4) == 4 && (bP0 + bP1 + bP2 + bP3 + bP4 + bP5 + bP6 + bP7) == 1)    {
                        // end of stroke 1->0
                        barrayReturn[j][i] = 0;
                    }
                }
                if (barrayImg[nLeft + j][nTop + i] == 0)    {
                    if ((n01SwitchMode & 16) == 16)   {
                        int nOnCountIn0246 = bP0 + bP2 + bP4 + bP6;
                        if (nOnCountIn0246 >= 3)    {
                            barrayReturn[j][i] = 1;
                        }
                    }
                }
            }
        }
        return barrayReturn;
    }

    public static byte[][] findSkeletonIndex1Direct(byte[][] barrayImg, int nLeft, int nTop, int nWidth, int nHeight,
                                                    boolean bColThenRow, boolean bColStepUp, boolean bRowStepUp) throws InterruptedException	{
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
        

        byte[] barrayDeleteMarks = {
                                    0,0,0,1,0,0,1,1,0,1,1,1,0,0,1,1,
                                    0,0,1,1,1,0,1,1,0,0,1,1,0,0,1,1,
                                    0,0,0,0,0,0,0,0,1,1,1,1,0,0,1,1,
                                    0,0,0,0,0,0,0,0,0,0,1,1,0,0,1,1,
                                    0,0,0,0,0,0,0,0,1,1,1,1,0,0,1,1,
                                    1,0,1,1,1,0,1,1,1,1,1,1,1,1,1,1,
                                    1,0,0,0,0,0,0,0,1,1,1,1,0,0,1,1,
                                    1,0,1,1,1,0,1,1,1,1,1,1,1,1,1,1,
                                    0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                                    1,0,1,1,1,0,1,1,0,0,1,1,0,0,1,1,
                                    0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                                    0,0,0,0,0,0,0,0,0,0,1,1,0,0,1,1,
                                    1,0,0,0,0,0,0,0,1,1,1,1,0,0,1,1,
                                    1,0,1,1,1,0,1,1,1,1,1,1,1,1,1,1,
                                    1,0,0,0,0,0,0,0,1,1,1,1,0,0,1,1,
                                    1,0,1,1,1,0,1,1,1,1,1,1,1,1,1,1
                                   };
        byte[][] barrayReturn = new byte[nWidth][nHeight];  // initialize a zero array.
        for (int j = 0; j < nWidth; j ++)  {
            System.arraycopy(barrayImg[nLeft + j], nTop, barrayReturn[j], 0, nHeight);
        }

        boolean bStart = true;
        while (bStart)  {
            bStart = false;
            // first get the edge points
            byte[][] barrayTmp = new byte[nWidth][nHeight]; // initialize a zero array to store edge information.
            for (int i = 0; i < nHeight; i ++)  {
                for (int j = 0; j < nWidth; j ++)   {
                    if (barrayReturn[j][i] == 0)   {
                        continue;
                    }
                    // P0 P1 P2
                    // P7    P3
                    // P6 P5 P4
                    // it is fine if bP0 ~ bP7 out of order here.
                    byte bP3 = (j == nWidth - 1)?0:barrayReturn[j + 1][i];
                    byte bP2 = (i == 0)?0:((j == nWidth - 1)?0:barrayReturn[j + 1][i - 1]);
                    byte bP1 = (i == 0)?0:barrayReturn[j][i - 1];
                    byte bP0 = (i == 0)?0:((j == 0)?0:barrayReturn[j - 1][i - 1]);
                    byte bP7 = (j == 0)?0:barrayReturn[j - 1][i];
                    byte bP6 = (i == nHeight - 1)?0:((j == 0)?0:barrayReturn[j - 1][i + 1]);
                    byte bP5 = (i == nHeight - 1)?0:barrayReturn[j][i + 1];
                    byte bP4 = (i == nHeight - 1)?0:((j == nWidth - 1)?0:barrayReturn[j + 1][i + 1]);
                    
                    int nAnd = bP1 & bP3 & bP5 & bP7;   //bP0 & bP1 & bP2 & bP3 & bP4 & bP5 & bP6 & bP7;
                    if (nAnd == 0)  {
                        barrayTmp[j][i] = 1;
                    }
                }
            }
            // now start to delete
            int iFrom = bRowStepUp?0:nHeight - 1;
            int iTo = bRowStepUp?nHeight:-1;
            int iStep = bRowStepUp?1:-1;
            int jFrom = bColStepUp?0:nWidth - 1;
            int jTo = bColStepUp?nWidth:-1;
            int jStep = bColStepUp?1:-1;
            if (bColThenRow)    {
                for (int j = jFrom; j != jTo; j += jStep)   {
                    for (int i = iFrom; i != iTo; i += iStep)  {
                        if (barrayTmp[j][i] == 0)   {
                            continue;
                        }
                        if (barrayReturn[j][i] == 1)    {
                            // P5 P6 P7
                            // P3    P4
                            // P0 P1 P2
                            int nP0 = (i == nHeight - 1)?0:((j == 0)?0:barrayReturn[j - 1][i + 1]);
                            int nP1 = (i == nHeight - 1)?0:barrayReturn[j][i + 1];
                            int nP2 = (i == nHeight - 1)?0:((j == nWidth - 1)?0:barrayReturn[j + 1][i + 1]);
                            int nP3 = (j == 0)?0:barrayReturn[j - 1][i];
                            int nP4 = (j == nWidth - 1)?0:barrayReturn[j + 1][i];
                            int nP5 = (i == 0)?0:((j == 0)?0:barrayReturn[j - 1][i - 1]);
                            int nP6 = (i == 0)?0:barrayReturn[j][i - 1];
                            int nP7 = (i == 0)?0:((j == nWidth - 1)?0:barrayReturn[j + 1][i - 1]);

                            nP1 *= 2;
                            nP2 *= 4;
                            nP3 *= 8;
                            nP4 *= 16;
                            nP5 *= 32;
                            nP6 *= 64;
                            nP7 *= 128;
                            int nOr = nP0 | nP1 | nP2 | nP3 | nP4 | nP5 | nP6 | nP7;
                            if (barrayDeleteMarks[nOr] == 1)  {
                                barrayReturn[j][i] = 0;
                                bStart = true;
                            }
                        }
                    }
                }
            } else {
                for (int i = iFrom; i != iTo; i += iStep)  {
                    for (int j = jFrom; j != jTo; j += jStep)   {
                        if (barrayTmp[j][i] == 0)   {
                            continue;
                        }
                        if (barrayReturn[j][i] == 1)    {
                            // P5 P6 P7
                            // P3    P4
                            // P0 P1 P2
                            int nP0 = (i == nHeight - 1)?0:((j == 0)?0:barrayReturn[j - 1][i + 1]);
                            int nP1 = (i == nHeight - 1)?0:barrayReturn[j][i + 1];
                            int nP2 = (i == nHeight - 1)?0:((j == nWidth - 1)?0:barrayReturn[j + 1][i + 1]);
                            int nP3 = (j == 0)?0:barrayReturn[j - 1][i];
                            int nP4 = (j == nWidth - 1)?0:barrayReturn[j + 1][i];
                            int nP5 = (i == 0)?0:((j == 0)?0:barrayReturn[j - 1][i - 1]);
                            int nP6 = (i == 0)?0:barrayReturn[j][i - 1];
                            int nP7 = (i == 0)?0:((j == nWidth - 1)?0:barrayReturn[j + 1][i - 1]);

                            nP1 *= 2;
                            nP2 *= 4;
                            nP3 *= 8;
                            nP4 *= 16;
                            nP5 *= 32;
                            nP6 *= 64;
                            nP7 *= 128;
                            int nOr = nP0 | nP1 | nP2 | nP3 | nP4 | nP5 | nP6 | nP7;
                            if (barrayDeleteMarks[nOr] == 1)  {
                                barrayReturn[j][i] = 0;
                                bStart = true;
                            }
                        }
                    }
                }
            }
        }

        return barrayReturn;
    }
    
    // index thinner method
    // returned barray is the same size of input barrayImg.
    public static byte[][] findSkeletonIndexMethodOld(byte[][] barrayImg, int nLeft, int nTop, int nWidth, int nHeight) throws InterruptedException	{
		/* To save time, do not validate parameters.*/
        // i : 0 -> nHeight - 1;
        // j : 0 -> nWidth - 1;
        byte[][] barrayReturn1 = findSkeletonIndex1Direct(barrayImg, nLeft, nTop, nWidth, nHeight, false, true, true);
        // j : nWidth - 1 -> 0;
        // i : 0 -> nHeight - 1;
        byte[][] barrayReturn2 = findSkeletonIndex1Direct(barrayImg, nLeft, nTop, nWidth, nHeight, true, false, true);
        byte[][] barrayReturn = new byte[nWidth][nHeight];  // initialize a zero array.
        for (int j = 0; j < nWidth; j ++)  {
            for (int i = 0; i < nHeight; i ++)  {
                barrayReturn[j][i] = (byte) (barrayReturn1[j][i] | barrayReturn2[j][i]);
            }
        }
        barrayReturn = findSkeletonIndex1Direct(barrayReturn, 0, 0, nWidth, nHeight, false, true, true);
       
        return barrayReturn;
    }
    
    // index thinner method
    // returned barray is the same size of input barrayImg.
    public static byte[][] findSkeletonIndexMethod(byte[][] barrayImg, int nLeft, int nTop, int nWidth, int nHeight) throws InterruptedException	{
		/* To save time, do not validate parameters.*/
        byte[][] barrayReturn1 = findSkeletonIndex1Direct(barrayImg, nLeft, nTop, nWidth, nHeight, true, true, true);
        byte[][] barrayReturn2 = findSkeletonIndex1Direct(barrayImg, nLeft, nTop, nWidth, nHeight, true, true, false);
        byte[][] barrayReturn3 = findSkeletonIndex1Direct(barrayImg, nLeft, nTop, nWidth, nHeight, false, true, true);
        byte[][] barrayReturn4 = findSkeletonIndex1Direct(barrayImg, nLeft, nTop, nWidth, nHeight, false, false, true);
        byte[][] barrayReturn = new byte[nWidth][nHeight];  // initialize a zero array.
        for (int j = 0; j < nWidth; j ++)  {
            for (int i = 0; i < nHeight; i ++)  {
                barrayReturn[j][i] = (byte) (barrayReturn1[j][i] | barrayReturn4[j][i] | barrayReturn2[j][i] | barrayReturn3[j][i]);
            }
        }
        barrayReturn = findSkeletonIndex1Direct(barrayReturn, 0, 0, nWidth, nHeight, false, true, true);
        return barrayReturn;
    }
    
    public static LinkedList<ConnectPntSet> getConnectPntSets(byte[][] barrayImg, int nLeft, int nTop, int nWidth, int nHeight) {
        LinkedList<ConnectPntSet> listConnectPntSets = new LinkedList<ConnectPntSet>();
        ConnectPntSet lastConnectPntSet = null;
        for (int idx = nTop; idx < nTop + nHeight; idx ++)  {
            for (int idx1 = nLeft; idx1 < nLeft + nWidth; idx1 ++) {
                if (barrayImg[idx1][idx] == 1)  {
                    int nOnNeighbour = 0;
                    if (idx1 > nLeft && idx > nTop && barrayImg[idx1 - 1][idx - 1] == 1)    {
                        nOnNeighbour ++;
                    }
                    if (idx > nTop && barrayImg[idx1][idx - 1] == 1)    {
                        nOnNeighbour ++;
                    }
                    if (idx1 < nLeft + nWidth - 1 && idx > nTop && barrayImg[idx1 + 1][idx - 1] == 1)   {
                        nOnNeighbour ++;
                    }
                    if (idx1 < nLeft + nWidth - 1 && barrayImg[idx1 + 1][idx] == 1) {
                        nOnNeighbour ++;
                    }
                    if (idx1 < nLeft + nWidth - 1 && idx < nTop + nHeight - 1 && barrayImg[idx1 + 1][idx + 1] == 1) {
                        nOnNeighbour ++;
                    }
                    if (idx < nTop + nHeight - 1 && barrayImg[idx1][idx + 1] == 1)  {
                        nOnNeighbour ++;
                    }
                    if (idx1 > nLeft && idx < nTop + nHeight - 1 && barrayImg[idx1 - 1][idx + 1] == 1)  {
                        nOnNeighbour ++;
                    }
                    if (idx1 > nLeft && barrayImg[idx1 - 1][idx] == 1)  {
                        nOnNeighbour ++;
                    }
                    if (nOnNeighbour >= 3)  {
                        // ok, find a connect point.
                        Position3D pnt = new Position3D(idx1, idx);
                        if (lastConnectPntSet != null && lastConnectPntSet.isPntInSet(pnt))    {
                            lastConnectPntSet.mlistPnts.add(pnt);    
                        } else  {
                            ConnectPntSet cps2Add = null;
                            for (ConnectPntSet cps : listConnectPntSets)  {
                                if (cps == lastConnectPntSet)   {
                                    continue;
                                } else if (cps.isPntInSet(pnt)) {
                                    cps2Add = cps;
                                }
                            }
                            if (cps2Add == null) {
                                lastConnectPntSet = new ConnectPntSet();
                                listConnectPntSets.add(lastConnectPntSet);
                            } else  {
                                lastConnectPntSet = cps2Add;
                            }
                            lastConnectPntSet.mlistPnts.add(pnt);
                        }
                    }
                }
            }
        }
        // now sort the listConnectPntSets
        for (ConnectPntSet cps : listConnectPntSets)    {
            cps.getAvgPnt();
        }
        LinkedList<ConnectPntSet> listReturn = new LinkedList<ConnectPntSet>();
        while (listConnectPntSets.size() > 0)   {
            ConnectPntSet cps2Add = listConnectPntSets.removeFirst();
            int idx = 0;
            for (idx = 0; idx < listReturn.size(); idx ++)    {
                ConnectPntSet cps = listReturn.get(idx);
                double d2AddEdge = Math.min(Math.min(cps2Add.mdAvgX - nLeft, nLeft + nWidth - 1 - cps2Add.mdAvgX),
                        Math.min(cps2Add.mdAvgY - nTop, nTop + nHeight - 1 - cps2Add.mdAvgY));
                double dCompEdge = Math.min(Math.min(cps.mdAvgX - nLeft, nLeft + nWidth - 1 - cps.mdAvgX),
                        Math.min(cps.mdAvgY - nTop, nTop + nHeight - 1 - cps.mdAvgY));
                double d2AddSumEdge = Math.min(cps2Add.mdAvgX - nLeft, nLeft + nWidth - 1 - cps2Add.mdAvgX)
                                    + Math.min(cps2Add.mdAvgY - nTop, nTop + nHeight - 1 - cps2Add.mdAvgY); // edge x + edge y
                double dCompSumEdge = Math.min(cps.mdAvgX - nLeft, nLeft + nWidth - 1 - cps.mdAvgX)
                                    + Math.min(cps.mdAvgY - nTop, nTop + nHeight - 1 - cps.mdAvgY); // edge x + edge y
                if (d2AddEdge < dCompEdge)  {
                    listReturn.add(idx, cps2Add);
                    break;
                } else if (d2AddEdge == dCompEdge && d2AddSumEdge < dCompSumEdge)   {
                    listReturn.add(idx, cps2Add);
                    break;
                }
            }
            if (idx == listReturn.size())   {
                // append to tail.
                listReturn.addLast(cps2Add);
            }
        }
        return listReturn;
    }

    public static ImageChop thinImageChop(ImageChop imgChopOriginal, boolean bSmoothAfterThin) throws InterruptedException    {
        if (imgChopOriginal.isEmptyImage()) {
            return new ImageChop();
        } else  {
  //          System.out.println("imgChopOriginal");
 //           ImgMatrixOutput.printMatrix(imgChopOriginal.mbarrayImg);
            byte[][] biOutput = StrokeFinder.findSkeletonIndexMethod(imgChopOriginal.mbarrayImg,
                                                            imgChopOriginal.mnLeft,
                                                            imgChopOriginal.mnTop,
                                                            imgChopOriginal.mnWidth,
                                                            imgChopOriginal.mnHeight);
            if (bSmoothAfterThin)   {
                //biOutput = StrokeFinder.smoothStroke(biOutput, 0, 0, biOutput.length, biOutput[0].length, 1);

                //迟改

                Integer[] array = {0,0,1,1,0,0,1,1,1,1,0,1,1,1,0,1,
                        1,1,0,0,1,1,1,1,0,0,0,0,0,0,0,1,
                        0,0,1,1,0,0,1,1,1,1,0,1,1,1,0,1,
                        1,1,0,0,1,1,1,1,0,0,0,0,0,0,0,1,
                        1,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,
                        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                        1,1,0,0,1,1,0,0,1,1,0,1,1,1,0,1,
                        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                        0,0,1,1,0,0,1,1,1,1,0,1,1,1,0,1,
                        1,1,0,0,1,1,1,1,0,0,0,0,0,0,0,1,
                        0,0,1,1,0,0,1,1,1,1,0,1,1,1,0,1,
                        1,1,0,0,1,1,1,1,0,0,0,0,0,0,0,0,
                        1,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,
                        1,1,0,0,1,1,1,1,0,0,0,0,0,0,0,0,
                        1,1,0,0,1,1,0,0,1,1,0,1,1,1,0,0,
                        1,1,0,0,1,1,1,0,1,1,0,0,1,0,0,0};
                BufferedImage image_thinned = ImageMgr.convertBiMatrix2Img(biOutput);
                BufferedImage iThin = Xihua(image_thinned,array);
                byte[][] biMatrix = ImageMgr.convertImg2BiMatrix(iThin);
                biOutput = biMatrix;

            }
  //          System.out.println("imgChopThinned:");
  //          ImgMatrixOutput.printMatrix(biOutput);
            ImageChop imgChop = new ImageChop();
            imgChop.setImageChop(biOutput, 0, 0, biOutput.length, biOutput[0].length,
                    imgChopOriginal.mbarrayOriginalImg, 
                    imgChopOriginal.getLeftInOriginalImg(),
                    imgChopOriginal.getTopInOriginalImg(),
                    imgChopOriginal.mnChopType);
            imgChop = imgChop.convert2MinContainer();
            return imgChop;
        }
    }
}
