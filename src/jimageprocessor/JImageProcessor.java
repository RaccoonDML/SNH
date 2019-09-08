package jimageprocessor;

import com.cyzapps.imgmatrixproc.ImgNoiseFilter;
import com.cyzapps.imgmatrixproc.ImgThreshBiMgr;
import com.cyzapps.imgproc.ImageMgr;
import com.cyzapps.mathexprgen.SerMFPTranslator;
import com.cyzapps.mathexprgen.SerMFPTranslator.CurPos;
import com.cyzapps.mathexprgen.SerMFPTranslator.SerMFPTransFlags;
import com.cyzapps.mathrecog.*;
import com.cyzapps.mathrecog.ExprRecognizer.ExprRecognizeException;
import com.cyzapps.uptloadermgr.UPTJavaLoaderMgr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author tonyc
 */
public class JImageProcessor {

    /**
     * @param args the command line arguments
     */

    public static String[] resu = new String[500];
    public static double[] similarty = new double[500];

    public static void main(String[] args) throws InterruptedException, IOException {

        int nTestMode = 0;
        try {
            nTestMode = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            nTestMode = 0;
        }

        int nTestMode0PrintRecogMode = ExprRecognizer.RECOG_SPRINT_MODE;
        int nTestMode0HandwritingRecogMode = ExprRecognizer.RECOG_SHANDWRITING_MODE;
        int nTestMode3and4RecogMode = ExprRecognizer.RECOG_SPRINT_MODE;
        boolean bLoadPrintChars = false;
        boolean bLoadSPrintChars = true;
        boolean bLoadSHandwritingChars = true;
        if (nTestMode == -1) {
            // generate UPT loader java files
            generateUPTsJAVALoader();
        } else if (nTestMode == 0) {
            CharLearningMgr clm = new CharLearningMgr();
            FileInputStream fis = null;
            try {
                fis = new FileInputStream("clm.xml");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(JImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
            InputStream is = fis;
            if (is != null) {
                clm.readFromXML(is);
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(JImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            MisrecogWordMgr mwm = new MisrecogWordMgr();
            fis = null;
            try {
                fis = new FileInputStream("mwm.xml");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(JImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
            is = fis;
            if (is != null) {
                mwm.readFromXML(is);
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(JImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            // load prototypes, this is much quicker as it is coded in source.
            UPTJavaLoaderMgr.load(bLoadPrintChars, bLoadSPrintChars, bLoadSHandwritingChars);
            //String[] strFolders = getPrototypeFolders();
            //ImageMgr.loadUnitProtoTypesBMPs2UPTs(UnitRecognizer.msUPTMgr, strFolders, "prototypes");

            ExprRecognizer.setRecognitionMode(nTestMode0HandwritingRecogMode); //hand mode on

            int max_num = 15;
            String Img_folder = "teat_data3";
//            for(int i=1;i<=max_num;++i){
//                preprocessImage(String.valueOf(i)+".jpg", Img_folder, "prepresult", 100, true);
//            }
//            for(int i=1;i<=max_num;++i){
//                testThinImage("prepresult", "thinimgresult");
//            }

//            recognizeMathExpr("test_data3/" + "3" + ".jpg.bmp", clm, mwm, true);
            System.out.println();

        } else if (nTestMode == 1) {
            int nPixelDiv = 100;

        } else if (nTestMode == 2) {
            testThinImage("thinimgtest", "thinimgresult");
        } else {
            if (nTestMode == 3 || nTestMode == 4) {
                int nPixelDiv = 100;
                byte[][] biMatrix = preprocessImage("mr_initial.bmp", ".", ".", nPixelDiv, nTestMode == 3);
                ImageChop imgChop = new ImageChop();
                /* From test it is found that after rectify there are more noises, effect is worse, so comment this part.
                imgChop.setImageChop(biMatrix, 0, 0, biMatrix.length, biMatrix[0].length, ImageChop.TYPE_UNKNOWN);
                ImageChop imgChopThinned = StrokeFinder.thinImageChop(imgChop, true);
                double dAngle = ImgRectifier.calcRectifyAngleHough(imgChopThinned.mbarrayImg);
                biMatrix = ImgRectifier.adjustMatrixByAngle(biMatrix, -dAngle, new double[2]);
                biMatrix = StrokeFinder.smoothStroke(biMatrix, 0, 0, biMatrix.length, biMatrix[0].length, 19);*/
                BufferedImage image_rectified = ImageMgr.convertBiMatrix2Img(biMatrix);
                ImageMgr.saveImg(image_rectified, "mr_rectified.bmp");

                if (biMatrix.length > 0 && biMatrix[0].length > 0) {
                    imgChop.setImageChop(biMatrix, 0, 0, biMatrix.length, biMatrix[0].length, ImageChop.TYPE_UNKNOWN);
                    imgChop = StrokeFinder.thinImageChop(imgChop, true);
                    BufferedImage image_finalized = ImageMgr.convertBiMatrix2Img(imgChop.mbarrayImg);
                    ImageMgr.saveImg(image_finalized, "mr_finalized.bmp");
                }
            }
            CharLearningMgr clm = new CharLearningMgr();
            FileInputStream fis = null;
            try {
                fis = new FileInputStream("clm.xml");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(JImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
            InputStream is = fis;
            if (is != null) {
                clm.readFromXML(is);
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(JImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            MisrecogWordMgr mwm = new MisrecogWordMgr();
            fis = null;
            try {
                fis = new FileInputStream("mwm.xml");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(JImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
            is = fis;
            if (is != null) {
                mwm.readFromXML(is);
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(JImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            UPTJavaLoaderMgr.load(bLoadPrintChars, bLoadSPrintChars, bLoadSHandwritingChars);
            ExprRecognizer.setRecognitionMode(nTestMode3and4RecogMode); // change to 1 for handwriting mode.
            //findSkeleton("recog_test2.bmp", "testrecog_thin.bmp");
            //recognizeMathExpr("mathrecog1_1.bmp", clm, mwm);
            //recognizeMathExpr("seperate.bmp", clm, mwm);
//            recognizeMathExpr("mr_finalized.bmp", clm, mwm, true);
//            //recognizeMathExpr("mr_smoothed.bmp", clm, mwm);
//            //recognizeMathExpr("regtest/recog_regtest24.bmp", clm, mwm, true);
//
//            recognizeMathExpr("regtest/recog_regtest204.bmp", clm, mwm, true);
//            recognizeMathExpr("todo/filter1/mr_finallyproc(1).bmp", clm, mwm, true);
//            recognizeMathExpr("todo/filter1/mr_finallyproc(2).bmp", clm, mwm, true);
//            recognizeMathExpr("todo/filter1/mr_finallyproc(3).bmp", clm, mwm, true);
//            recognizeMathExpr("todo/filter1/mr_finallyproc(4).bmp", clm, mwm, true);
//            recognizeMathExpr("todo/filter1/mr_finallyproc(5).bmp", clm, mwm, true);
//            recognizeMathExpr("todo/filter1/mr_finallyproc(7).bmp", clm, mwm, true);
//            recognizeMathExpr("todo/filter1/mr_finallyproc(8).bmp", clm, mwm, true);
            //recognizeMathExpr("mr_finalized.bmp", clm, mwm);
        }
    }

    public static String recognizeMathExpr(String x_Parent,String x_Name,String strImageFile, CharLearningMgr clm, MisrecogWordMgr mwm, boolean bFilter) throws InterruptedException, IOException {
        BufferedImage image = ImageMgr.readImg(strImageFile);
        byte[][] biMatrix = ImageMgr.convertImg2BiMatrix(image);
        ImageChop imgChop = new ImageChop();
        imgChop.setImageChop(biMatrix, 0, 0, biMatrix.length, biMatrix[0].length, ImageChop.TYPE_UNKNOWN);
        imgChop = imgChop.convert2MinContainer();
        System.out.println("Now reading image file " + strImageFile + " :");
        long startTime = System.nanoTime();
        String strOutput = "\\Exception";

        //ImgMatrixOutput.createMatrixImage_ful(imgChop.mbarrayImg, "dml_data/101.jpg");

        try {

            //重点1 初步解构加识别
            ExprRecognizer.dml_cnt = 0;
            ExprRecognizer.YX_size = 100;
            StructExprRecog ser = ExprRecognizer.recognize(imgChop, null, -1, 0, 0);
            StructExprRecog serOld = ser; //保存上一步的结果，便于与本步对比





            System.out.println("\n1-RAW: image " + strImageFile + " includes expression :\n" + ser.toString()+"\tEXPR_TYPE:\t"+ser.getExprRecogType());

            // Here is YX's recify
           // ser = ser.yx_recifyF();
            //System.out.println("\n1.45-YXRECIFY：\n" + ser.toString()+"\tEXPR_TYPE: "+ser.mnExprRecogType);

            // Here is LH's recify
            ser = ser.recifyF();
            System.out.println("\n1.5-XZRECIFY：\n" + ser.toString()+"\tEXPR_TYPE: "+ser.mnExprRecogType);




            //todo yx 17_18:08 去除等号

            if(ser.mlistChildren.getLast().mType== UnitPrototypeMgr.UnitProtoType.Type.TYPE_EQUAL)
            {
                System.out.println("yx 更改等号:"+ser.mlistChildren.getLast().mType);
                String newStr;
                String oldImageFile=x_Parent+ File.separator + x_Name;
                BufferedImage img= ImageIO.read(new File(oldImageFile));
                int chunkWidth = image.getWidth() ;
                int chunkHeight = image.getHeight() ;

                BufferedImage newImage = new BufferedImage(ser.mlistChildren.getLast().mnLeft, chunkHeight, img.getType());

                newImage=img.getSubimage(0,0,ser.mlistChildren.getLast().mnLeft,chunkHeight);
                ImageIO.write(newImage, "jpg", new File("res" + File.separator + "prepresult"+File.separator + "1"+x_Name));
                preprocessImage("1"+x_Name, "res" + File.separator + "prepresult",
                        "res" + File.separator + "prepresult", 100, true);
                System.out.println("输出路径:"+"res" + File.separator + "prepresult"+File.separator + "1"+x_Name);
                newStr=recognizeMathExpr("res" + File.separator + "prepresult","1"+x_Name,"res" + File.separator + "prepresult"+File.separator + "1"+ x_Name + ".bmp",clm, mwm, true);
                return newStr;
            }
            //todo dml change 7.18 10:12
            if(ser.mlistChildren.getFirst().mType== UnitPrototypeMgr.UnitProtoType.Type.TYPE_BRACE)
            {
                System.out.println("yx 更改方程组:"+ ser.mlistChildren.getFirst().mType);
                String newStr;
                String oldImageFile=x_Parent+ File.separator + x_Name;
                BufferedImage img= ImageIO.read(new File(oldImageFile));
                int chunkWidth = image.getWidth() ;
                int chunkHeight = image.getHeight() ;

                BufferedImage newImage = new BufferedImage(chunkWidth-ser.mlistChildren.getFirst().mnLeft-ser.mlistChildren.getFirst().mnWidth, chunkHeight, img.getType());

                newImage=img.getSubimage(ser.mlistChildren.getFirst().mnLeft+ser.mlistChildren.getFirst().mnWidth,0,chunkWidth-ser.mlistChildren.getFirst().mnLeft-ser.mlistChildren.getFirst().mnWidth,chunkHeight);
                ImageIO.write(newImage, "jpg", new File("res" + File.separator + "prepresult"+File.separator + "1"+x_Name));
                preprocessImage("1"+x_Name, "res" + File.separator + "prepresult",
                        "res" + File.separator + "prepresult", 100, true);
                System.out.println("输出路径:"+"res" + File.separator + "prepresult"+File.separator + "1"+x_Name);
                newStr=recognizeMathExpr("res" + File.separator + "prepresult","1"+x_Name,"res" + File.separator + "prepresult"+File.separator + "1"+ x_Name + ".bmp",clm, mwm, true);
                return newStr;
            }


            if (bFilter) {
                ser = ExprFilter.filterRawSER(ser, null);
                System.out.println("\n1.6-FILTERED:\n" + ser.toString()+"\tEXPR_TYPE:\t"+ser.getExprRecogType()+"\n");
                serOld = ser;
            }


//            for (int idx = 0; idx < ser.mlistChildren.size(); idx ++)   {
//                StructExprRecog serThisChild = ser.mlistChildren.get(idx);
//                System.out.print("EXPR_TYPE: "+serThisChild.mnExprRecogType + "\tM_TYPE: ");
//                System.out.printf("%28s",serThisChild.mType);
//                System.out.println("\t\t"+serThisChild.toString());
//            }

            if (ser != null) {
                //重点2 字符串重构

                ser = ser.restruct();
                for (int idx = 0; idx < ser.mlistChildren.size(); idx ++)   {
                    StructExprRecog serThisChild = ser.mlistChildren.get(idx);
                    System.out.print("EXPR_TYPE: "+serThisChild.mnExprRecogType + "\tM_TYPE: ");
                    System.out.printf("%28s",serThisChild.mType);
                    System.out.println("\t\t"+serThisChild.toString());
                }
                //ser.recifyG();

                System.out.println("\n2-RESTRUCT:\n" + ser.toString());
                serOld = ser;
                //重点2.5 过滤时加入表达式尾去掉dottimes逻辑，换做X比较好
                if (bFilter) {
                    ser = ExprFilter.filterRestructedSER(ser, null, null);
                    System.out.println("\n2.5-FILTERED:\n" + ser.toString());
                    serOld = ser;
                }

                if (ser != null) {
                    //重点3 错误识别一轮二轮修正，错误识别函数名修正
                    ser.rectifyMisRecogChars1stRnd(clm);
                    System.out.println("\n3-RECTIFY_1:\n" + ser.toString());
                    ser.rectifyMisRecogChars2ndRnd();
                    System.out.println("3-RECTIFY_2:\n" + ser.toString());
                    ser.rectifyMisRecogWords(mwm);
                    System.out.println("3-RECTIFY_3:\n" + ser.toString());

                    //重点4 最终结果（结构化表达式） 翻译成可计算的数学表达式
                    SerMFPTransFlags smtFlags = new SerMFPTransFlags();
                    smtFlags.mbConvertAssign2Eq = true;
                    strOutput = SerMFPTranslator.cvtSer2MFPExpr(ser, null, new CurPos(0), mwm, smtFlags);

                } else {
                    strOutput = "2NO VALID EXPRESSION FOUND.";
                }
            } else {
                strOutput = "1NO VALID EXPRESSION FOUND.";
            }
        } catch (ExprRecognizeException e) {
            //递归爆栈 20
            if (e.getMessage().compareTo(ExprRecognizer.TOO_DEEP_CALL_STACK) == 0) {
                strOutput = "Expression is too complicated to recognize.";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.nanoTime();
        System.out.println("\n4-FINAL_RESULT:\n" + strOutput);
        System.out.println(String.format("\nTOTAL_TIME: Recognize %s takes %s\n", strImageFile, toString(endTime - startTime)));
        return strOutput;
    }


    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
            }
        }
        return flag;
    }

    public static byte[][] preprocessImage(String strImageFile, String strSrcFolder, String strDestFolder, int nPixelDiv, boolean bFilterSmooth) throws InterruptedException {
        System.out.println("Now processing image file " + strImageFile);
        if (bFilterSmooth) {
            BufferedImage image = ImageMgr.readImg(strSrcFolder + File.separator + strImageFile);
            int[][] grayMatrix = ImageMgr.convertImg2GrayMatrix(image);
            BufferedImage image_grayed = ImageMgr.convertGrayMatrix2Img(grayMatrix);
            ImageMgr.saveImg(image_grayed, "mr_grayed.bmp");

            grayMatrix = ImgNoiseFilter.filterNoiseNbAvg4Gray(grayMatrix, 1);
            BufferedImage image_filtered = ImageMgr.convertGrayMatrix2Img(grayMatrix);
            ImageMgr.saveImg(image_filtered, "mr_filtered.bmp");

            int nWHMax = Math.max(grayMatrix.length, grayMatrix[0].length);
            int nEstimatedStrokeWidth = (int) Math.ceil((double) nWHMax / (double) nPixelDiv);
            byte[][] biMatrix = ImgThreshBiMgr.convertGray2Bi2ndD(grayMatrix, (int) Math.max(3.0, nEstimatedStrokeWidth / 2.0));  // selected value was 6.
            BufferedImage image_bilized1 = ImageMgr.convertBiMatrix2Img(biMatrix);
            ImageMgr.saveImg(image_bilized1, "mr_bilized1.bmp");
            ImageChop imgChop = new ImageChop();
            imgChop.setImageChop(biMatrix, 0, 0, biMatrix.length, biMatrix[0].length, ImageChop.TYPE_UNKNOWN);
            double dAvgStrokeWidth = imgChop.calcAvgStrokeWidth();

            int nFilterR = (int) Math.ceil((dAvgStrokeWidth / 2.0 - 1) / 2.0);
            biMatrix = ImgNoiseFilter.filterNoiseNbAvg4Bi(biMatrix, nFilterR, 1);
            biMatrix = ImgNoiseFilter.filterNoiseNbAvg4Bi(biMatrix, nFilterR, 2);
            imgChop.setImageChop(biMatrix, 0, 0, biMatrix.length, biMatrix[0].length, ImageChop.TYPE_UNKNOWN);
            BufferedImage image_smoothed1 = ImageMgr.convertBiMatrix2Img(imgChop.mbarrayImg);
            ImageMgr.saveImg(image_smoothed1, "mr_smoothed1.bmp");

            biMatrix = ImgNoiseFilter.filterNoisePoints4Bi(biMatrix, (int) dAvgStrokeWidth);
            imgChop.setImageChop(biMatrix, 0, 0, biMatrix.length, biMatrix[0].length, ImageChop.TYPE_UNKNOWN);
            BufferedImage image_smoothed2 = ImageMgr.convertBiMatrix2Img(imgChop.mbarrayImg);
            ImageMgr.saveImg(image_smoothed2, "mr_smoothed2.bmp");
            ImageMgr.saveImg(image_smoothed2, strDestFolder + File.separator + strImageFile + ".bmp");
            return biMatrix;
        } else {
            BufferedImage image = ImageMgr.readImg(strSrcFolder + File.separator + strImageFile);
            byte[][] biMatrix = ImageMgr.convertImg2BiMatrix(image);
            return biMatrix;
        }
    }

    public static void generateUPTsJAVALoader() {
        String[] strFolders = getPrototypeFolders();
        ImageMgr.loadUnitProtoTypesBmps2JAVA(UnitRecognizer.msUPTMgrPrint, strFolders, "prototypes_print", "uptloadersprint", UPTJavaLoaderMgr.LOAD_UPTS_JAVA_CNT_PRINT);
        ImageMgr.loadUnitProtoTypesBmps2JAVA(UnitRecognizer.msUPTMgrSPrint, strFolders, "prototypes_sprint", "uptloaderssprint", UPTJavaLoaderMgr.LOAD_UPTS_JAVA_CNT_SPRINT);
        ImageMgr.loadUnitProtoTypesBmps2JAVA(UnitRecognizer.msUPTMgrSHandwriting, strFolders, "prototypes_shandwriting", "uptloadersshandwriting", UPTJavaLoaderMgr.LOAD_UPTS_JAVA_CNT_SHANDWRITING);
    }

    public static String[] getPrototypeFolders() {
        String[] strFolders = new String[118];
        int idx = 0;
        strFolders[idx++] = "add";
        strFolders[idx++] = "backward_slash";
        strFolders[idx++] = "big_A";
        strFolders[idx++] = "big_B";
        strFolders[idx++] = "big_C";
        strFolders[idx++] = "big_D";
        strFolders[idx++] = "big_DELTA";
        strFolders[idx++] = "big_E";
        strFolders[idx++] = "big_F";
        strFolders[idx++] = "big_G";
        strFolders[idx++] = "big_H";
        strFolders[idx++] = "big_I";
        strFolders[idx++] = "big_J";
        strFolders[idx++] = "big_K";
        strFolders[idx++] = "big_L";
        strFolders[idx++] = "big_M";
        strFolders[idx++] = "big_N";
        strFolders[idx++] = "big_O";
        strFolders[idx++] = "big_OMEGA";
        strFolders[idx++] = "big_P";
        strFolders[idx++] = "big_PHI";
        strFolders[idx++] = "big_PI";
        strFolders[idx++] = "big_Q";
        strFolders[idx++] = "big_R";
        strFolders[idx++] = "big_S";
        strFolders[idx++] = "big_SIGMA";
        strFolders[idx++] = "big_T";
        strFolders[idx++] = "big_THETA";
        strFolders[idx++] = "big_U";
        strFolders[idx++] = "big_V";
        strFolders[idx++] = "big_W";
        strFolders[idx++] = "big_X";
        strFolders[idx++] = "big_Y";
        strFolders[idx++] = "big_Z";
        strFolders[idx++] = "brace";
        strFolders[idx++] = "close_brace";
        strFolders[idx++] = "close_round_bracket";
        strFolders[idx++] = "close_square_bracket";
        strFolders[idx++] = "dollar";
        strFolders[idx++] = "dot";
        strFolders[idx++] = "eight";
        strFolders[idx++] = "euro";
        strFolders[idx++] = "five";
        strFolders[idx++] = "forward_slash";
        strFolders[idx++] = "four";
        strFolders[idx++] = "infinite";
        strFolders[idx++] = "integrate";
        strFolders[idx++] = "integrate_circle";
        strFolders[idx++] = "larger";
        strFolders[idx++] = "left_arrow";
        strFolders[idx++] = "multiply";
        strFolders[idx++] = "nine";
        strFolders[idx++] = "one";
        strFolders[idx++] = "pound";
        strFolders[idx++] = "right_arrow";
        strFolders[idx++] = "round_bracket";
        strFolders[idx++] = "seven";
        strFolders[idx++] = "six";
        strFolders[idx++] = "smaller";
        strFolders[idx++] = "small_a";
        strFolders[idx++] = "small_alpha";
        strFolders[idx++] = "small_b";
        strFolders[idx++] = "small_beta";
        strFolders[idx++] = "small_c";
        strFolders[idx++] = "small_d";
        strFolders[idx++] = "small_delta";
        strFolders[idx++] = "small_e";
        strFolders[idx++] = "small_epsilon";
        strFolders[idx++] = "small_eta";
        strFolders[idx++] = "small_f";
        strFolders[idx++] = "small_g";
        strFolders[idx++] = "small_gamma";
        strFolders[idx++] = "small_h";
        strFolders[idx++] = "small_i_without_dot";
        strFolders[idx++] = "small_k";
        strFolders[idx++] = "small_lambda";
        strFolders[idx++] = "small_m";
        strFolders[idx++] = "small_mu";
        strFolders[idx++] = "small_n";
        strFolders[idx++] = "small_o";
        strFolders[idx++] = "small_omega";
        strFolders[idx++] = "small_p";
        strFolders[idx++] = "small_phi";
        strFolders[idx++] = "small_pi";
        strFolders[idx++] = "small_psi";
        strFolders[idx++] = "small_q";
        strFolders[idx++] = "small_r";
        strFolders[idx++] = "small_rho";
        strFolders[idx++] = "small_s";
        strFolders[idx++] = "small_sigma";
        strFolders[idx++] = "small_t";
        strFolders[idx++] = "small_tau";
        strFolders[idx++] = "small_theta";
        strFolders[idx++] = "small_u";
        strFolders[idx++] = "small_v";
        strFolders[idx++] = "small_w";
        strFolders[idx++] = "small_x";
        strFolders[idx++] = "small_xi";
        strFolders[idx++] = "small_y";
        strFolders[idx++] = "small_z";
        strFolders[idx++] = "small_zeta";
        strFolders[idx++] = "sqrt_long";
        strFolders[idx++] = "sqrt_medium";
        strFolders[idx++] = "sqrt_short";
        strFolders[idx++] = "sqrt_tall";
        strFolders[idx++] = "sqrt_very_tall";
        strFolders[idx++] = "square_bracket";
        strFolders[idx++] = "star";
        strFolders[idx++] = "subtract";
        strFolders[idx++] = "three";
        strFolders[idx++] = "two";
        strFolders[idx++] = "vertical_line";
        strFolders[idx++] = "word_sin";
        strFolders[idx++] = "word_cos";
        strFolders[idx++] = "word_tan";
        strFolders[idx++] = "word_lim";
        strFolders[idx++] = "yuan";
        strFolders[idx++] = "zero";
        return strFolders;
    }

    public static String[] getPrototypeTestFolders() {
        String[] strFolders = new String[1];
        strFolders[0] = "right_arrow";
        return strFolders;
    }

    public static String toString(long nanoSecs) {
        int minutes = (int) (nanoSecs / 60000000000.0);
        int seconds = (int) (nanoSecs / 1000000000.0) - (minutes * 60);
        int millisecs = (int) (((nanoSecs / 1000000000.0) - (seconds + minutes * 60)) * 1000);


        if (minutes == 0 && seconds == 0) {
            return millisecs + "ms";
        } else if (minutes == 0 && millisecs == 0) {
            return seconds + "s";
        } else if (seconds == 0 && millisecs == 0) {
            return minutes + "min";
        } else if (minutes == 0) {
            return seconds + "s " + millisecs + "ms";
        } else if (seconds == 0) {
            return minutes + "min " + millisecs + "ms";
        } else if (millisecs == 0) {
            return minutes + "min " + seconds + "s";
        }
        return minutes + "min " + seconds + "s " + millisecs + "ms";
    }

    public static void testThinImage(String strSrcFolder, String strDestFolder) throws InterruptedException {
        System.out.println("Now test thinning image algorithm, source image folder is " + strSrcFolder + ", destination folder is " + strDestFolder);
        File folder = new File(strSrcFolder);
        for (File fProtoType : folder.listFiles()) {
            if (fProtoType.isFile()) {
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
}
