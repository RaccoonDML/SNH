/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyzapps.imgmatrixproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static com.cyzapps.imgmatrixproc.Thin.Xihua;

/**
 *
 * @author tonyc
 */
public class ImgMatrixOutput {
    
    static public void printMatrix(double[][] darray)  {
        if (darray == null || darray.length == 0)   {
            System.out.println("[]");
        } else {
            System.out.println("[");
            for (int idx = 0; idx < darray[0].length; idx ++)   {
                for (int idx1 = 0; idx1 < darray.length; idx1 ++)   {
                    System.out.print(darray[idx1][idx] + "\t");
                }
                System.out.print("\n");
            }
            System.out.println("]");
        }
    }

    public static void createMatrixImage_ful(byte[][] matrix, String filedir) throws IOException {
        int cx = matrix.length;
        int cy = matrix[0].length;

        //填充矩形高宽
        //int cz = 10;
        //生成图的宽度
        int width = cx;// * cz;
        //生成图的高度
        int height = cy;// * cz;

        int czh=0,czw=0;
        if(width>height)
        {
            czh=(width-height)/2;
            height=width;
        }
        else
        {
            czw=(height-width)/2;
            width=height;
        }

        OutputStream output = new FileOutputStream(new File(filedir));
        BufferedImage bufImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D gs = bufImg.createGraphics();
        gs.setBackground(Color.WHITE);
        gs.clearRect(0, 0, width, height);


        gs.setColor(Color.BLACK);
        for (int i = 0; i < cx; i++) {
            for (int j = 0; j < cy; j++) {
                //1绘制填充黑矩形
                if(matrix[i][j]==1){

                    gs.drawRect(i+czw, j+czh, 1, 1);
                    gs.fillRect(i+czw, j+czh, 1, 1);
                }
            }
        }
        gs.dispose();
        bufImg.flush();
        //输出文件
        ImageIO.write(bufImg, "jpeg", output);

    }

    //生成32bmp
    public static int createMatrixImage(byte[][] matrix, String filedir) throws IOException, InterruptedException {

        //BufferedImage image_smoothed1 = ImageMgr.convertBiMatrix2Img(matrix);
        //ImageMgr.saveImg(image_smoothed1, filedir);

        int heise=0;
        int cx = matrix.length;
        int cy = matrix[0].length;
       // System.out.println("the size of pic: "+cx+ " " + cy );

        //填充矩形高宽
        int cz = 0;
        //生成图的宽度
        int width = cx;// * cz;
        //生成图的高度
        int height = cy;// * cz;

        int czh=0,czw=0;
        if(width>height)
        {
            czh=(width-height)/2;
            height=width;
        }
        else
        {
            czw=(height-width)/2;
            width=height;
        }

        if(width<32)
        {
            cz=(32-width)/2;
            width=32;
            height=32;
        }

        OutputStream output = new FileOutputStream(new File(filedir));
        BufferedImage bufImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D gs = bufImg.createGraphics();
        gs.setBackground(Color.WHITE);
        gs.clearRect(0, 0, width, height);


        gs.setColor(Color.BLACK);
        for (int i = 0; i < cx; i++) {
            for (int j = 0; j < cy; j++) {
                //1绘制填充黑矩形
                if(matrix[i][j]==1){

                    heise++;
                    gs.drawRect(i+czw+cz, j+czh+cz, 2, 2);
                    gs.fillRect(i+czw+cz, j+czh+cz, 2, 2);
                }
            }
        }
        //gs.drawImage(bufImg,32,32,null);
        gs.dispose();
        bufImg.flush();
        BufferedImage bufferedImage=new BufferedImage(32,32,BufferedImage.TYPE_INT_RGB);
        //将原始位图缩小后绘制到bufferedImage对象中
        Graphics graphics=bufferedImage.getGraphics();
        graphics.drawImage(bufImg,0,0,32,32,null);

        //ImageIO.write(bufferedImage, "bmp", output);

        //filedir=filedir+".bmp";
        //ImageMgr.saveImg(bufferedImage, filedir);

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

                BufferedImage iThin = Xihua(bufferedImage,array);

                //filedir=filedir+".bmp";
                //ImageMgr.saveImg(iThin, filedir);
                ImageIO.write(iThin, "bmp", output);
                return heise;

//系统细化
//        byte[][] biMatrix = ImageMgr.convertImg2BiMatrix(bufferedImage);
//        ImageChop imgChop = new ImageChop();
//        imgChop.setImageChop(biMatrix, 0, 0, biMatrix.length, biMatrix[0].length, ImageChop.TYPE_UNKNOWN);
//        imgChop = StrokeFinder.thinImageChop(imgChop, true);
//        BufferedImage image_thinned = ImageMgr.convertBiMatrix2Img(imgChop.mbarrayImg);
//        ImageIO.write(image_thinned, "bmp", output);


    }

    //生成重复三次
//    public static void createMatrixImage(byte[][] matrix, String filedir) throws IOException {
//
//        //BufferedImage image_smoothed1 = ImageMgr.convertBiMatrix2Img(matrix);
//        //ImageMgr.saveImg(bufferedImage, filedir);
//
//        int cx = matrix.length;
//        int cy = matrix[0].length;
//
//        //填充矩形高宽
//        int cz = 20;
//        //生成图的宽度
//        int width = cx;// * cz;
//        //生成图的高度
//        int height = cy;// * cz;
//
//        int czh=0,czw=0;
//        if(width>height)
//        {
//            czh=(width-height)/2;
//            height=width;
//        }
//        else
//        {
//            czw=(height-width)/2;
//            width=height;
//        }
//
//        OutputStream output = new FileOutputStream(new File(filedir));
//        BufferedImage bufImg = new BufferedImage(width*3+cz*6, height+cz*2, BufferedImage.TYPE_INT_RGB);
//        Graphics2D gs = bufImg.createGraphics();
//        gs.setBackground(Color.WHITE);
//        gs.clearRect(0, 0, width*3+cz*6, height+cz*2);
//
//
//        gs.setColor(Color.BLACK);
//        for (int i = 0; i < cx; i++) {
//            for (int j = 0; j < cy; j++) {
//                //1绘制填充黑矩形
//                if(matrix[i][j]==1){
//                    for(int h=0;h<3;h++)
//                    {
//                        gs.drawRect(i+czw+h*width+h*cz+cz, j+czh+cz, 1, 1);
//                        gs.fillRect(i+czw+h*width+h*cz+cz, j+czh+cz, 1, 1);
//                    }
//
//                }
//            }
//        }
//        //gs.drawImage(bufImg,32,32,null);
//        gs.dispose();
//        bufImg.flush();
////        BufferedImage bufferedImage=new BufferedImage(32,32,BufferedImage.TYPE_INT_RGB);
////        //将原始位图缩小后绘制到bufferedImage对象中
////        Graphics graphics=bufferedImage.getGraphics();
////        graphics.drawImage(bufImg,0,0,32,32,null);
////        //输出文件
//        ImageIO.write(bufImg, "bmp", output);
//
//    }




    static public void printMatrix(double[][] darray, int nLeft, int nTop, int nWidth, int nHeight)  {
        if (darray == null || darray.length == 0)   {
            System.out.println("[]");
        } else if (nLeft < 0 || nTop < 0 || nWidth < 0 || nHeight < 0
                || nLeft + nWidth > darray.length || nTop + nHeight > darray[0].length)    {
            System.out.println("[Invalid range]");
        } else {
            System.out.println("[");
            for (int idx1 = nTop; idx1 < nTop + nHeight; idx1 ++)   {
                for (int idx = nLeft; idx < nLeft + nWidth; idx ++)   {
                    System.out.print(darray[idx][idx1] + "\t");
                }
                System.out.print("\n");
            }
            System.out.println("]");
        }
    }

    static public void printMatrix(long[][] lnarray)  {
        if (lnarray == null || lnarray.length == 0)   {
            System.out.println("[]");
        } else {
            System.out.println("[");
            for (int idx = 0; idx < lnarray[0].length; idx ++)   {
                for (int idx1 = 0; idx1 < lnarray.length; idx1 ++)   {
                    System.out.print(lnarray[idx1][idx] + "\t");
                }
                System.out.print("\n");
            }
            System.out.println("]");
        }
    }
    
    static public void printMatrix(long[][] lnarray, int nLeft, int nTop, int nWidth, int nHeight)  {
        if (lnarray == null || lnarray.length == 0)   {
            System.out.println("[]");
        } else if (nLeft < 0 || nTop < 0 || nWidth < 0 || nHeight < 0
                || nLeft + nWidth > lnarray.length || nTop + nHeight > lnarray[0].length)    {
            System.out.println("[Invalid range]");
        } else {
            System.out.println("[");
            for (int idx1 = nTop; idx1 < nTop + nHeight; idx1 ++)   {
                for (int idx = nLeft; idx < nLeft + nWidth; idx ++)   {
                    System.out.print(lnarray[idx][idx1] + "\t");
                }
                System.out.print("\n");
            }
            System.out.println("]");
        }
    }

    static public void printMatrix(int[][] narray)  {
        if (narray == null || narray.length == 0)   {
            System.out.println("[]");
        } else {
            System.out.println("[");
            for (int idx = 0; idx < narray[0].length; idx ++)   {
                for (int idx1 = 0; idx1 < narray.length; idx1 ++)   {
                    System.out.print(narray[idx1][idx] + "\t");
                }
                System.out.print("\n");
            }
            System.out.println("]");
        }
    }
    
    static public void printMatrix(int[][] narray, int nLeft, int nTop, int nWidth, int nHeight)  {
        if (narray == null || narray.length == 0)   {
            System.out.println("[]");
        } else if (nLeft < 0 || nTop < 0 || nWidth < 0 || nHeight < 0
                || nLeft + nWidth > narray.length || nTop + nHeight > narray[0].length)    {
            System.out.println("[Invalid range]");
        } else {
            System.out.println("[");
            for (int idx1 = nTop; idx1 < nTop + nHeight; idx1 ++)   {
                for (int idx = nLeft; idx < nLeft + nWidth; idx ++)   {
                    System.out.print(narray[idx][idx1] + "\t");
                }
                System.out.print("\n");
            }
            System.out.println("]");
        }
    }

    static public void printMatrix(byte[][] barray)  {
        if (barray == null || barray.length == 0)   {
            System.out.println("[]");
        } else {
            System.out.println("[");
            for (int idx = 0; idx < barray[0].length; idx ++)   {
                for (int idx1 = 0; idx1 < barray.length; idx1 ++)   {
                    System.out.print(barray[idx1][idx] + "\t");
                }
                System.out.print("\n");
            }
            System.out.println("]");
        }
    }

    static public void printMatrix(byte[][] barray, int nLeft, int nTop, int nWidth, int nHeight)  {
        if (barray == null || barray.length == 0)   {
            System.out.println("[]");
        } else if (nLeft < 0 || nTop < 0 || nWidth < 0 || nHeight < 0
                || nLeft + nWidth > barray.length || nTop + nHeight > barray[0].length)    {
            System.out.println("[Invalid range]");
        } else {
            System.out.println("[");
            for (int idx1 = nTop; idx1 < nTop + nHeight; idx1 ++)   {
                for (int idx = nLeft; idx < nLeft + nWidth; idx ++)   {
                    System.out.print(barray[idx][idx1] + "\t");
                }
                System.out.print("\n");
            }
            System.out.println("]");
        }
    }        
}
