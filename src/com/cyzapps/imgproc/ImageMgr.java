package com.cyzapps.imgproc;

import com.cyzapps.uptloadermgr.UPTJavaLoaderMgr;
import com.cyzapps.imgmatrixproc.ImgMatrixConverter;
import com.cyzapps.mathrecog.ImageChop;
import com.cyzapps.mathrecog.UnitPrototypeMgr;
import com.cyzapps.mathrecog.UnitPrototypeMgr.UnitProtoType;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;


public class ImageMgr {

	public static int[][] convertImg2ColorMatrix(BufferedImage image) {
        //Raster raster=image.getData();
        //int w=raster.getWidth(),h=raster.getHeight();
        int w = image.getWidth(), h = image.getHeight();
        int pixels[][]=new int[w][h];
        for (int x=0;x<w;x++)
        {
            for(int y=0;y<h;y++)
            {
                // do not use image.getRGB(x, y) directly because it includes alpha.
                int[] rgb = ImgMatrixConverter.convertInt2RGB(image.getRGB(x, y));
                pixels[x][y] = ImgMatrixConverter.convertRGB2Int(rgb[0], rgb[1], rgb[2]);
            }
        }
	
		return pixels;
	}
    
	public static int[][] convertImg2GrayMatrix(BufferedImage image) {
        //Raster raster=image.getData();
        //int w=raster.getWidth(),h=raster.getHeight();
        int w = image.getWidth(), h = image.getHeight();
        int pixels[][]=new int[w][h];
        for (int x=0;x<w;x++)
        {
            for(int y=0;y<h;y++)
            {
                // do not use image.getRGB(x, y) directly because it includes alpha.
                int[] rgb = ImgMatrixConverter.convertInt2RGB(image.getRGB(x, y));
                pixels[x][y] = (rgb[0] * 19595 + rgb[1] * 38469 + rgb[2] * 7472) >> 16;
            }
        }
	
		return pixels;
	}
    

	public static byte[][] convertImg2BiMatrix(BufferedImage image)	{
		int[][] narrayColorMatrix = convertImg2ColorMatrix(image);
		return ImgMatrixConverter.convertColor2Bi(narrayColorMatrix);
	}
	
	public static BufferedImage convertBiMatrix2Img(byte[][] biMatrix)	{
		if (biMatrix == null || biMatrix.length == 0 || biMatrix[0].length == 0)	{
			return null;	// we cannot create a zero width zero height bitmap.
		}
		return convertBiMatrix2Img(biMatrix, 0, 0, biMatrix.length, biMatrix[0].length);
	}
	
	public static BufferedImage convertBiMatrix2Img(byte[][] biMatrix, int nLeft, int nTop, int nWidth, int nHeight)	{
		if (biMatrix == null || biMatrix.length == 0 || biMatrix[0].length == 0)	{
			return null;	// we cannot create a zero width zero height bitmap.
		}
		int width = Math.min(nWidth, biMatrix.length - nLeft);
		int height = Math.min(nHeight, biMatrix[0].length - nTop);
		int[] colorMatrix = new int[width * height];
		for (int idx = 0; idx < height; idx ++)	{
			for (int idx1 = 0; idx1 < width; idx1 ++)	{
				if (biMatrix[idx1 + nLeft][idx + nTop] == 0)	{
					colorMatrix[idx * width + idx1] = Color.WHITE.getRGB();
				} else	{
					colorMatrix[idx * width + idx1] = Color.BLACK.getRGB();
				}
			}
		}

        // Initialize BufferedImage, assuming Color[][] is already properly populated.
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Set each pixel of the BufferedImage to the color from the Color[][].
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bufferedImage.setRGB(x, y, colorMatrix[y * width + x]);
            }
        }		
		return bufferedImage;
	}
	
	public static BufferedImage convertGrayMatrix2Img(int[][] grayMatrix)	{
		if (grayMatrix == null || grayMatrix.length == 0 || grayMatrix[0].length == 0)	{
			return null;	// we cannot create a zero width zero height bitmap.
		}
		return convertGrayMatrix2Img(grayMatrix, 0, 0, grayMatrix.length, grayMatrix[0].length);
	}
	
	public static BufferedImage convertGrayMatrix2Img(int[][] grayMatrix, int nLeft, int nTop, int nWidth, int nHeight)	{
		if (grayMatrix == null || grayMatrix.length == 0 || grayMatrix[0].length == 0)	{
			return null;	// we cannot create a zero width zero height bitmap.
		}
		int width = Math.min(nWidth, grayMatrix.length - nLeft);
		int height = Math.min(nHeight, grayMatrix[0].length - nTop);
		int[] colorMatrix = new int[width * height];
		for (int idx = 0; idx < height; idx ++)	{
			for (int idx1 = 0; idx1 < width; idx1 ++)	{
				if (grayMatrix[idx1 + nLeft][idx + nTop] < 0)	{
                    grayMatrix[idx1 + nLeft][idx + nTop] = 0;
				} else if (grayMatrix[idx1 + nLeft][idx + nTop] > 255)	{
					grayMatrix[idx1 + nLeft][idx + nTop] = 255;
				}
				colorMatrix[idx * width + idx1] = 255*256*65536 + grayMatrix[idx1 + nLeft][idx + nTop] * 65793; // dont forget alpha
			}
		}

        // Initialize BufferedImage, assuming Color[][] is already properly populated.
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Set each pixel of the BufferedImage to the color from the Color[][].
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bufferedImage.setRGB(x, y, colorMatrix[y * width + x]);
            }
        }		
		return bufferedImage;
	}
	
	
	public static BufferedImage convertColorMatrix2Img(int[][] colorMatrix)	{
		if (colorMatrix == null || colorMatrix.length == 0 || colorMatrix[0].length == 0)	{
			return null;	// we cannot create a zero width zero height bitmap.
		}
		return convertGrayMatrix2Img(colorMatrix, 0, 0, colorMatrix.length, colorMatrix[0].length);
	}
	
	public static BufferedImage convertColorMatrix2Img(int[][] colorMatrix, int nLeft, int nTop, int nWidth, int nHeight)	{
		if (colorMatrix == null || colorMatrix.length == 0 || colorMatrix[0].length == 0)	{
			return null;	// we cannot create a zero width zero height bitmap.
		}
		int width = Math.min(nWidth, colorMatrix.length - nLeft);
		int height = Math.min(nHeight, colorMatrix[0].length - nTop);
		int[] colorList = new int[width * height];
		for (int idx = 0; idx < height; idx ++)	{
			for (int idx1 = 0; idx1 < width; idx1 ++)	{
				colorList[idx * width + idx1] = 255*256*65536 + colorMatrix[idx1 + nLeft][idx + nTop]; // dont forget alpha
			}
		}

        // Initialize BufferedImage, assuming Color[][] is already properly populated.
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Set each pixel of the BufferedImage to the color from the Color[][].
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bufferedImage.setRGB(x, y, colorList[y * width + x]);
            }
        }		
		return bufferedImage;
	}
	        
    public static void saveImageChop(ImageChop chop, String strFileName)   {
        BufferedImage img = ImageMgr.convertBiMatrix2Img(
                chop.mbarrayImg,
                chop.mnLeft,
                chop.mnTop,
                chop.mnWidth,
                chop.mnHeight);
        ImageMgr.saveImg(img, strFileName);

    }
    
	public static void saveImg(BufferedImage image, String strFilePathName)	{
        try {
            ImageIO.write(image, "bmp", new File(strFilePathName));
        } catch (IOException ex) {
            Logger.getLogger(ImageMgr.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
    
    public static BufferedImage readImg(String strFilePathName) {
        try {
            return ImageIO.read(new File(strFilePathName));
        } catch (IOException ex) {
            Logger.getLogger(ImageMgr.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static String getFont(String strProtoTypeFileName) {
        String strFileExtension = ".bmp";            
        String strFontPart = "";
        if (strProtoTypeFileName.length() > 4
                && strProtoTypeFileName
                    .substring(strProtoTypeFileName.length() - strFileExtension.length())
                    .equalsIgnoreCase(strFileExtension))  {
            strFontPart = strProtoTypeFileName.substring(0, strProtoTypeFileName.length() - strFileExtension.length());
        }
        return strFontPart;
    }
    
    public static String cvtProtoTypeFont2Folder(String strProtoTypeFont) {
        return "prototype_" + strProtoTypeFont + "_thinned";
    }    
    
    public static void loadUnitProtoTypesBmps2JAVA(UnitPrototypeMgr uptMgr, String[] strProtoTypeFolders, String strProtoTypeParentFolder, String strUPTLoadersFolder, int nUPTJavaCnt)  {
        uptMgr.clear();
        
        // add unknown prototype
        uptMgr.addUnitPrototype(UnitProtoType.Type.TYPE_UNKNOWN, null, UnitPrototypeMgr.NORMAL_UPT_LIST);
        // add empty prototype
        uptMgr.addUnitPrototype(UnitProtoType.Type.TYPE_EMPTY, new byte[0][0], UnitPrototypeMgr.NORMAL_UPT_LIST);
        
        BufferedWriter[] out = new BufferedWriter[nUPTJavaCnt];
        try {
            String strJAVAHead = "";
            
            for (int idxOut = 0; idxOut < nUPTJavaCnt; idxOut ++)    {
                out[idxOut] = new BufferedWriter(new FileWriter(".." + File.separator + "src" + File.separator
                                                        + "com"  + File.separator + "cyzapps" + File.separator + strUPTLoadersFolder + File.separator
                                                        + UPTJavaLoaderMgr.LOAD_UPTS_JAVA + idxOut + ".java"));
                strJAVAHead = UPTJavaLoaderMgr.createLoadUPTJAVAHead(UPTJavaLoaderMgr.LOAD_UPTS_JAVA + idxOut, strUPTLoadersFolder);
                out[idxOut].write(strJAVAHead);
            }

            int nMinLnLenInUPTJAVAFile = 20;    // the characters in one line should be at least 20 characters, like biMatrix[0][8] = 1;
            int nMaxUPTJAVAFileLnCnt = 1024;    // one java file should have at most 1024 lines.
            int nThisUPTJAVAFileLnCnt = 0;
            int nWhichOut = 0;  // which JAVA file to output.
            int nProtoTypeCnt = 0;
            for (String strProtoTypeFolder : strProtoTypeFolders)   {
                File folder = new File(strProtoTypeParentFolder + File.separator + strProtoTypeFolder);
                if (!folder.exists() || !folder.isDirectory()) {
                    continue;
                }
                // add other prototypes.
                for (File fProtoType : folder.listFiles())  {
                    if (fProtoType.isFile())    {
                        String strFileName = fProtoType.getName();
                        String strFilePath = fProtoType.getPath();
                        BufferedImage bufferedImage = readImg(strFilePath);
                        if (bufferedImage == null)  {
                            continue;
                        }
                        byte[][] biMatrix = convertImg2BiMatrix(bufferedImage);
                        UnitProtoType.Type unitType = UnitProtoType.Type.TYPE_UNKNOWN;
                        int nSave2HExtendableList = 2;
                        int nSave2VExtendableList = 4;
                        int nSave2WordList = 8;
                        int nSave2Which = 1;    // by default, save to normal list.
                        double dWMinNumStrokes = 0, dHMinNumStrokes = 0;
                        if (strProtoTypeFolder.equalsIgnoreCase("ZERO"))    {
                            unitType = UnitProtoType.Type.TYPE_ZERO;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("ONE"))    {
                            unitType = UnitProtoType.Type.TYPE_ONE;
                            dWMinNumStrokes = 1.0;
                            dHMinNumStrokes = 3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("TWO"))    {
                            unitType = UnitProtoType.Type.TYPE_TWO;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("THREE"))    {
                            unitType = UnitProtoType.Type.TYPE_THREE;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("FOUR"))    {
                            unitType = UnitProtoType.Type.TYPE_FOUR;
                            dWMinNumStrokes = 0;    //4.0;
                            dHMinNumStrokes = 0;    //4.5;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("FIVE"))    {
                            unitType = UnitProtoType.Type.TYPE_FIVE;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SIX"))    {
                            unitType = UnitProtoType.Type.TYPE_SIX;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SEVEN"))    {
                            unitType = UnitProtoType.Type.TYPE_SEVEN;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("EIGHT"))    {
                            unitType = UnitProtoType.Type.TYPE_EIGHT;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("NINE"))    {
                            unitType = UnitProtoType.Type.TYPE_NINE;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("INFINITE"))    {
                            unitType = UnitProtoType.Type.TYPE_INFINITE;
                            dWMinNumStrokes = 4.0;
                            dHMinNumStrokes = 3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_A"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_A;
                            dWMinNumStrokes = 0;    //4.0;
                            dHMinNumStrokes = 0;    //3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_B"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_B;
                            dWMinNumStrokes = 0;    //4.0;
                            dHMinNumStrokes = 0;    //3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_C"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_C;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_D"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_D;
                            dWMinNumStrokes = 0;    //4.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_E"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_E;
                            dWMinNumStrokes = 0;    //4.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_F"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_F;
                            dWMinNumStrokes = 0;    //4.0;
                            dHMinNumStrokes = 0;    //5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_G"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_G;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_H"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_H;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_I"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_I;
                            dWMinNumStrokes = 1.5;
                            dHMinNumStrokes = 5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_I_WITHOUT_DOT"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_I_WITHOUT_DOT;
                            dWMinNumStrokes = 1;
                            dHMinNumStrokes = 3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_J"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_J;
                            dWMinNumStrokes = 2.5;
                            dHMinNumStrokes = 6.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_J_WITHOUT_DOT"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_J_WITHOUT_DOT;
                            dWMinNumStrokes = 2;
                            dHMinNumStrokes = 4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_K"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_K;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_L"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_L;
                            dWMinNumStrokes = 1.0;
                            dHMinNumStrokes = 3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_M"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_M;
                            dWMinNumStrokes = 0;    //5.0;
                            dHMinNumStrokes = 0;    //3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_N"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_N;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_O"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_O;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_P"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_P;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_Q"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_Q;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_R"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_R;
                            dWMinNumStrokes = 0;    //2.0;
                            dHMinNumStrokes = 0;    //3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_S"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_S;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_T"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_T;
                            dWMinNumStrokes = 0;    //2.5;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_U"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_U;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_V"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_V;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_W"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_W;
                            dWMinNumStrokes = 0;    //5.0;
                            dHMinNumStrokes = 0;    //3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_X"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_X;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_Y"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_Y;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_Z"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_Z;
                            dWMinNumStrokes = 0;    //5.0;
                            dHMinNumStrokes = 0;    //3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_A"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_A;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_B"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_B;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_C"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_C;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_D"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_D;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_E"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_E;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_F"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_F;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_G"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_G;
                            dWMinNumStrokes = 0;    //4.0;
                            dHMinNumStrokes = 0;    //5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_H"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_H;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_I"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_I;
                            dWMinNumStrokes = 1.0;
                            dHMinNumStrokes = 4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_J"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_J;
                            dWMinNumStrokes = 3.0;
                            dHMinNumStrokes = 4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_K"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_K;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_L"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_L;
                            dWMinNumStrokes = 4.0;
                            dHMinNumStrokes = 4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_M"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_M;
                            dWMinNumStrokes = 0;    //5.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_N"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_N;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_O"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_O;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_P"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_P;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_Q"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_Q;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_R"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_R;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_S"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_S;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_T"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_T;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_U"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_U;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_V"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_V;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_W"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_W;
                            dWMinNumStrokes = 0;    //5.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_X"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_X;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_Y"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_Y;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_Z"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_Z;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_ALPHA"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_ALPHA;
                            dWMinNumStrokes = 0;    //3.5;
                            dHMinNumStrokes = 0;    //3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_BETA"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_BETA;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_GAMMA"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_GAMMA;
                            dWMinNumStrokes = 0;    //2.0;
                            dHMinNumStrokes = 0;    //3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_DELTA"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_DELTA;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_EPSILON"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_EPSILON;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_ZETA"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_ZETA;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_ETA"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_ETA;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_THETA"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_THETA;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_LAMBDA"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_LAMBDA;
                            dWMinNumStrokes = 3.0;
                            dHMinNumStrokes = 3.5;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_MU"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_MU;
                            dWMinNumStrokes = 0;    //3.5;
                            dHMinNumStrokes = 0;    //3.5;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_XI"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_XI;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_PI"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_PI;
                            dWMinNumStrokes = 0;    //4.0;
                            dHMinNumStrokes = 0;    //3.5;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_RHO"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_RHO;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_SIGMA"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_SIGMA;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_TAU"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_TAU;
                            dWMinNumStrokes = 0;    //2.0;
                            dHMinNumStrokes = 0;    //3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_PHI"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_PHI;
                            dWMinNumStrokes = 0;    //4.0;
                            dHMinNumStrokes = 0;    //4.5;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_PSI"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_PSI;
                            dWMinNumStrokes = 0;    //4.0;
                            dHMinNumStrokes = 0;    //4.5;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALL_OMEGA"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALL_OMEGA;
                            dWMinNumStrokes = 0;    //5.0;
                            dHMinNumStrokes = 0;    //3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_DELTA"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_DELTA;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_THETA"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_THETA;
                            dWMinNumStrokes = 0;    //4.0;
                            dHMinNumStrokes = 0;    //5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_PI"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_PI;
                            dWMinNumStrokes = 0;    //5.0;
                            dHMinNumStrokes = 0;    //5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_SIGMA"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_SIGMA;
                            dWMinNumStrokes = 0;    //5.0;
                            dHMinNumStrokes = 0;    //5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_PHI"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_PHI;
                            dWMinNumStrokes = 0;    //5.0;
                            dHMinNumStrokes = 0;    //5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BIG_OMEGA"))    {
                            unitType = UnitProtoType.Type.TYPE_BIG_OMEGA;
                            dWMinNumStrokes = 0;    //5.0;
                            dHMinNumStrokes = 0;    //5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("INTEGRATE"))    {
                            unitType = UnitProtoType.Type.TYPE_INTEGRATE;
                            nSave2Which |= nSave2VExtendableList;
                            dWMinNumStrokes = 4.0;
                            dHMinNumStrokes = 6.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("INTEGRATE_CIRCLE"))    {
                            unitType = UnitProtoType.Type.TYPE_INTEGRATE_CIRCLE;
                            dWMinNumStrokes = 4.0;
                            dHMinNumStrokes = 6.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SQRT_LEFT"))    {
                            unitType = UnitProtoType.Type.TYPE_SQRT_LEFT;
                            dWMinNumStrokes = 4.0;
                            dHMinNumStrokes = 5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SQRT_SHORT"))    {
                            unitType = UnitProtoType.Type.TYPE_SQRT_SHORT;
                            dWMinNumStrokes = 5.0;
                            dHMinNumStrokes = 6.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SQRT_MEDIUM"))    {
                            unitType = UnitProtoType.Type.TYPE_SQRT_MEDIUM;
                            dWMinNumStrokes = 9.0;
                            dHMinNumStrokes = 8.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SQRT_LONG"))    {
                            unitType = UnitProtoType.Type.TYPE_SQRT_LONG;
                            nSave2Which |= nSave2HExtendableList;
                            dWMinNumStrokes = 12.0;
                            dHMinNumStrokes = 8.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SQRT_TALL"))    {
                            unitType = UnitProtoType.Type.TYPE_SQRT_TALL;
                            dWMinNumStrokes = 9.0;
                            dHMinNumStrokes = 12.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SQRT_VERY_TALL"))    {
                            unitType = UnitProtoType.Type.TYPE_SQRT_VERY_TALL;
                            nSave2Which |= nSave2VExtendableList;
                            dWMinNumStrokes = 9.0;
                            dHMinNumStrokes = 18.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("ADD"))    {
                            unitType = UnitProtoType.Type.TYPE_ADD;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SUBTRACT"))    {
                            unitType = UnitProtoType.Type.TYPE_SUBTRACT;
                            nSave2Which |= nSave2HExtendableList;
                            dWMinNumStrokes = 3.0;
                            dHMinNumStrokes = 1.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("PLUS_MINUS"))    {
                            unitType = UnitProtoType.Type.TYPE_PLUS_MINUS;
                            dWMinNumStrokes = 3.0;
                            dHMinNumStrokes = 5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("DOT_MULTIPLY"))    {
                            unitType = UnitProtoType.Type.TYPE_DOT_MULTIPLY;
                            dWMinNumStrokes = 1.0;
                            dHMinNumStrokes = 1.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("MULTIPLY"))    {
                            unitType = UnitProtoType.Type.TYPE_MULTIPLY;
                            dWMinNumStrokes = 0;    //3.0;
                            dHMinNumStrokes = 0;    //3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("DIVIDE"))    {
                            unitType = UnitProtoType.Type.TYPE_DIVIDE;
                            dWMinNumStrokes = 5.0;
                            dHMinNumStrokes = 5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("FORWARD_SLASH"))    {
                            unitType = UnitProtoType.Type.TYPE_FORWARD_SLASH;
                            dWMinNumStrokes = 2.0;
                            dHMinNumStrokes = 3.5;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BACKWARD_SLASH"))    {
                            unitType = UnitProtoType.Type.TYPE_BACKWARD_SLASH;
                            dWMinNumStrokes = 2.0;
                            dHMinNumStrokes = 3.5;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("EQUAL"))    {
                            unitType = UnitProtoType.Type.TYPE_EQUAL;
                            dWMinNumStrokes = 3.0;
                            dHMinNumStrokes = 2.5;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("EQUAL_ALWAYS"))    {
                            unitType = UnitProtoType.Type.TYPE_EQUAL_ALWAYS;
                            dWMinNumStrokes = 3.0;
                            dHMinNumStrokes = 4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("EQUAL_ROUGHLY"))    {
                            unitType = UnitProtoType.Type.TYPE_EQUAL_ROUGHLY;
                            dWMinNumStrokes = 3.0;
                            dHMinNumStrokes = 3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("LARGER"))    {
                            unitType = UnitProtoType.Type.TYPE_LARGER;
                            dWMinNumStrokes = 3.0;
                            dHMinNumStrokes = 3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SMALLER"))    {
                            unitType = UnitProtoType.Type.TYPE_SMALLER;
                            dWMinNumStrokes = 3.0;
                            dHMinNumStrokes = 3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("NO_LARGER"))    {
                            unitType = UnitProtoType.Type.TYPE_NO_LARGER;
                            dWMinNumStrokes = 3.0;
                            dHMinNumStrokes = 4.5;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("NO_SMALLER"))    {
                            unitType = UnitProtoType.Type.TYPE_NO_SMALLER;
                            dWMinNumStrokes = 3.0;
                            dHMinNumStrokes = 4.5;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("PERCENT"))    {
                            unitType = UnitProtoType.Type.TYPE_PERCENT;
                            dWMinNumStrokes = 6.0;
                            dHMinNumStrokes = 6.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("EXCLAIMATION"))    {
                            unitType = UnitProtoType.Type.TYPE_EXCLAIMATION;
                            dWMinNumStrokes = 1.0;
                            dHMinNumStrokes = 5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("DOT"))    {
                            unitType = UnitProtoType.Type.TYPE_DOT;
                            dWMinNumStrokes = 1.0;
                            dHMinNumStrokes = 1.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("STAR"))    {
                            unitType = UnitProtoType.Type.TYPE_STAR;
                            dWMinNumStrokes = 3.0;
                            dHMinNumStrokes = 3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("ROUND_BRACKET"))    {
                            unitType = UnitProtoType.Type.TYPE_ROUND_BRACKET;
                            nSave2Which |= nSave2VExtendableList;
                            dWMinNumStrokes = 1.5;
                            dHMinNumStrokes = 3.5;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("CLOSE_ROUND_BRACKET"))    {
                            unitType = UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET;
                            nSave2Which |= nSave2VExtendableList;
                            dWMinNumStrokes = 1.5;
                            dHMinNumStrokes = 3.5;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("SQUARE_BRACKET"))    {
                            unitType = UnitProtoType.Type.TYPE_SQUARE_BRACKET;
                            nSave2Which |= nSave2VExtendableList;
                            dWMinNumStrokes = 1.5;
                            dHMinNumStrokes = 3.5;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("CLOSE_SQUARE_BRACKET"))    {
                            unitType = UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET;
                            nSave2Which |= nSave2VExtendableList;
                            dWMinNumStrokes = 1.5;
                            dHMinNumStrokes = 3.5;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("BRACE"))    {
                            unitType = UnitProtoType.Type.TYPE_BRACE;
                            nSave2Which |= nSave2VExtendableList;
                            dWMinNumStrokes = 2.0;
                            dHMinNumStrokes = 4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("CLOSE_BRACE"))    {
                            unitType = UnitProtoType.Type.TYPE_CLOSE_BRACE;
                            nSave2Which |= nSave2VExtendableList;
                            dWMinNumStrokes = 2.0;
                            dHMinNumStrokes = 4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("VERTICAL_LINE"))    {
                            unitType = UnitProtoType.Type.TYPE_VERTICAL_LINE;
                            nSave2Which |= nSave2VExtendableList;
                            dWMinNumStrokes = 1.0;
                            dHMinNumStrokes = 3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("WAVE"))    {
                            unitType = UnitProtoType.Type.TYPE_WAVE;
                            dWMinNumStrokes = 3.0;
                            dHMinNumStrokes = 2.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("LEFT_ARROW"))    {
                            unitType = UnitProtoType.Type.TYPE_LEFT_ARROW;
                            dWMinNumStrokes = 4.0;
                            dHMinNumStrokes = 3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("RIGHT_ARROW"))    {
                            unitType = UnitProtoType.Type.TYPE_RIGHT_ARROW;
                            dWMinNumStrokes = 4.0;
                            dHMinNumStrokes = 3.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("DOLLAR"))    {
                            unitType = UnitProtoType.Type.TYPE_DOLLAR;
                            dWMinNumStrokes = 0;    //5.0;
                            dHMinNumStrokes = 0;    //6.0;
                       } else if (strProtoTypeFolder.equalsIgnoreCase("EURO"))    {
                            unitType = UnitProtoType.Type.TYPE_EURO;
                            dWMinNumStrokes = 0;    //5.0;
                            dHMinNumStrokes = 0;    //6.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("YUAN"))    {
                            unitType = UnitProtoType.Type.TYPE_YUAN;
                            dWMinNumStrokes = 0;    //5.0;
                            dHMinNumStrokes = 0;    //6.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("POUND"))    {
                            unitType = UnitProtoType.Type.TYPE_POUND;
                            dWMinNumStrokes = 0;    //6.0;
                            dHMinNumStrokes = 0;    //6.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("CELCIUS"))    {
                            unitType = UnitProtoType.Type.TYPE_CELCIUS;
                            dWMinNumStrokes = 6.0;
                            dHMinNumStrokes = 5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("FAHRENHEIT"))    {
                            unitType = UnitProtoType.Type.TYPE_FAHRENHEIT;
                            dWMinNumStrokes = 6.0;
                            dHMinNumStrokes = 5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("WORD_SIN"))    {
                            unitType = UnitProtoType.Type.TYPE_WORD_SIN;
                            nSave2Which = nSave2WordList;   // do not save it to normal list
                            dWMinNumStrokes = 9.0;
                            dHMinNumStrokes = 5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("WORD_COS"))    {
                            unitType = UnitProtoType.Type.TYPE_WORD_COS;
                            nSave2Which = nSave2WordList;   // do not save it to normal list
                            dWMinNumStrokes = 11.0;
                            dHMinNumStrokes = 5.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("WORD_TAN"))    {
                            unitType = UnitProtoType.Type.TYPE_WORD_TAN;
                            nSave2Which = nSave2WordList;   // do not save it to normal list
                            dWMinNumStrokes = 9.0;
                            dHMinNumStrokes = 4.0;
                        } else if (strProtoTypeFolder.equalsIgnoreCase("WORD_LIM"))    {
                            unitType = UnitProtoType.Type.TYPE_WORD_LIM;
                            nSave2Which = nSave2WordList;   // do not save it to normal list
                            dWMinNumStrokes = 9.0;
                            dHMinNumStrokes = 5.0;
                        }
                        String strThisOutput = UPTJavaLoaderMgr.outputUPTInfo2JAVA(unitType, getFont(strFileName),
                                                                dWMinNumStrokes, dHMinNumStrokes, nSave2Which, biMatrix);
                        int nRoughLnCnt = (int)Math.ceil(strThisOutput.length() / nMinLnLenInUPTJAVAFile);
                        if (nWhichOut >= nUPTJavaCnt - 1) {
                            nWhichOut = nUPTJavaCnt - 1;    // if too many unitTypes, then always output the remaining unittype to last java file.
                            nThisUPTJAVAFileLnCnt += nRoughLnCnt;
                        } else if (nThisUPTJAVAFileLnCnt >= nMaxUPTJAVAFileLnCnt) {
                            // if the JAVA file is not empty, and it size has been too large, move to next JAVA file.
                            nWhichOut++;
                            nThisUPTJAVAFileLnCnt = nRoughLnCnt;
                        } else {
                            // the JAVA file is not large enough, we output here.
                            nThisUPTJAVAFileLnCnt += nRoughLnCnt;
                        }
                        out[nWhichOut].write(strThisOutput);
                        nProtoTypeCnt ++;
                    }
                }
            }
            for (int idxOut = 0; idxOut < nUPTJavaCnt; idxOut ++)    {
                out[idxOut].write(UPTJavaLoaderMgr.createLoadUPTJAVATail());
                out[idxOut].close();
                out[idxOut] = null;
            }
        } catch (IOException e)   {
            try {
                for (int idxOut = 0; idxOut < nUPTJavaCnt; idxOut ++)    {
                    if (out[idxOut] != null)    {
                        out[idxOut].close();
                    }
                }
            } catch (Exception e1)   {
                
            }
        }
    }
    
}
