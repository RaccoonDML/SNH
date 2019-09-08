package jimageprocessor;     //注意！

import com.cyzapps.Jfcalc.ErrProcessor;
import com.cyzapps.Jsma.SMErrProcessor;
import com.cyzapps.SmartMath.SmartCalcProcLib;
import com.cyzapps.imgmatrixproc.ImgNoiseFilter;
import com.cyzapps.imgmatrixproc.ImgThreshBiMgr;
import com.cyzapps.imgproc.ImageMgr;
import com.cyzapps.mathrecog.CharLearningMgr;
import com.cyzapps.mathrecog.ExprRecognizer;
import com.cyzapps.mathrecog.ImageChop;
import com.cyzapps.mathrecog.MisrecogWordMgr;
import com.cyzapps.uptloadermgr.UPTJavaLoaderMgr;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.cyzapps.SmartCalc.Cut;
import static jimageprocessor.JImageProcessor.recognizeMathExpr;

public class Controller implements Initializable {
    @FXML
    TextField FileAddressField;
    @FXML
    TextFlow CommandFlow;
    @FXML
    AnchorPane AP;
    @FXML
    Label fileNameLabel;
    @FXML
    Button prevPicButton, nextPicButton;

    private String SelectedImagePath = null;
    private CharLearningMgr clm;
    private MisrecogWordMgr mwm;
    private int currentPicIndex = 0;
    private List<File> selectedFiles;
    private ArrayList<String> results;
    //todo yx 17 19.46
    //todo yx 17 19.46
    public String x_Parent="";
    String x_Name="";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int nTestMode0PrintRecogMode = ExprRecognizer.RECOG_SPRINT_MODE;
        int nTestMode0HandwritingRecogMode = ExprRecognizer.RECOG_SHANDWRITING_MODE;
        int nTestMode3and4RecogMode = ExprRecognizer.RECOG_SPRINT_MODE;
        boolean bLoadPrintChars = false;
        boolean bLoadSPrintChars = true;
        boolean bLoadSHandwritingChars = true;
        results = new ArrayList<>();

        clm = new CharLearningMgr();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("res" + File.separator + "clm.xml");
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

        mwm = new MisrecogWordMgr();
        fis = null;
        try {
            fis = new FileInputStream("res" + File.separator + "mwm.xml");
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

        ExprRecognizer.setRecognitionMode(nTestMode0HandwritingRecogMode); //hand mode on

        //*****************************
        //      Buttons handlers
        //*****************************
        prevPicButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (currentPicIndex > 0 && !selectedFiles.isEmpty())
                    showImage(selectedFiles.get(--currentPicIndex));
            }
        });
        nextPicButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (currentPicIndex < selectedFiles.size() - 1 && !selectedFiles.isEmpty())
                    showImage(selectedFiles.get(++currentPicIndex));
            }
        });
    }

    @FXML
    private void OpenFiles() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "image files: bmp, png, jpg",
                "*.bmp", "*.png", "*.jpg"); // more file extensions can be added
        // clear collection of files

        fileChooser.getExtensionFilters().addAll(extFilter);

        List<File> tempListOfFiles = fileChooser.showOpenMultipleDialog(null);

        if (!tempListOfFiles.isEmpty()) {
            tempListOfFiles.forEach(x -> PutText(x.getPath(), false, Color.BLACK, "Arial", 16));
            currentPicIndex = 0;

            // show in program
            showImage(tempListOfFiles.get(currentPicIndex));

            SelectedImagePath = tempListOfFiles.get(currentPicIndex).getAbsolutePath();
            FileAddressField.setText(SelectedImagePath);
            fileNameLabel.setText(tempListOfFiles.get(currentPicIndex).getName());


            selectedFiles = tempListOfFiles;
            results.clear();
        }
    }

    @FXML
    void PreProcess() throws InterruptedException {
        if (selectedFiles.isEmpty()) {
            PutText("Please choose a picture(s)\n", false, Color.BLACK, "Arial", 16);
            return;
        }

        String path = getSelectedImagePath();
        System.out.println(path + " ");//+ pic);

        for (File x : selectedFiles) {
            preprocessImage(x.getName(), x.getParent(),
                    "res" + File.separator + "prepresult", 100, true);
            PutText("Image " + x.getName() + " has been processed", false, Color.BLACK, "Arial", 16);
        }
    }

    @FXML
    void RunProcess() throws InterruptedException, IOException {
        if (getSelectedImagePath() == null) {
            PutText("Please choose a picture(s)\n", false, Color.BLACK, "Arial", 16);
            return;
        }

        int i = 0;
        for (File x : selectedFiles) {
            x_Name=x.getName();
            x_Parent=x.getParent();
            results.add(recognizeMathExpr(x_Parent,x_Name,"res" + File.separator + "prepresult" + File.separator + x.getName() + ".bmp",
                    clm, mwm, true));
            PutText(selectedFiles.get(i).getName() + " recognition result:" + "\n" + results.get(i++) + "\n", false, Color.RED, "Arial", 16);
        }

    }

    @FXML
    void calculate() throws InterruptedException, SMErrProcessor.JSmartMathErrException, ErrProcessor.JFCALCExpErrException {
        if (results.isEmpty()) {
            PutText("There is no result yet\n", false, Color.BLACK, "Arial", 16);
            return;
        }
        String calcA = null;
        int i = 0;

        for (String strExpressions : results) {
            if (strExpressions.indexOf("\n") != -1)//方程组
            {
                strExpressions = Cut(strExpressions);
            } else if (strExpressions.indexOf("integrate") != -1 && strExpressions.indexOf("==") != -1)//积分方程
            {
                String calcA1;
                String[] strarraycup = strExpressions.split("==");
                calcA1 = SmartCalcProcLib.calculate(strarraycup[0], false);
                String temp = calcA1.replace(File.separator + "text", "");//改了\\
                temp = temp.replace("\"", "");
                temp = temp.replace("{", "(");
                temp = temp.replace("}", ")");
                System.out.println(temp);
                temp = temp.replace("×", "*");
                temp = temp.replace("^", "**");
                strExpressions = temp + "==" + strarraycup[1];
            }
            if (strExpressions.indexOf("derivative") != -1) {//求导
                Function df = new Function(strExpressions);
                String arg = Function.x + Function.ccount + Function.str;//这里的df.ccount，为求导阶数，目前一阶导数测试通过
                try {
                    Socket socket = new Socket("127.0.0.1", 9999);
                    System.out.println("Client start!");
                    PrintWriter out = new PrintWriter(socket.getOutputStream()); // 输出，to 服务器 socket
                    out.println("derivative:" + arg);
                    out.flush(); // 刷缓冲输出，to 服务器

                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            socket.getInputStream())); // 输入， from 服务器 socket
                    calcA = in.readLine();
                    System.out.println("Client end!");
                    socket.close();
                    //boolean success = (new File(dir)).delete();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                calcA = SmartCalcProcLib.calculate(strExpressions, false);
            }

            String temp = calcA.replace(File.separator + "text", "");
            temp = temp.replace("\"", "");
            temp = temp.replace("{", "(");
            temp = temp.replace("}", ")");
            if (temp.length() == 0)
                temp = "I can't calculate it yet";
            PutText(selectedFiles.get(i).getName() + " ANSWER:\n" + temp + "\n", false, Color.BLACK, "Arial", 16);
            System.out.println(temp);
        }
    }

    @FXML
    void RunAll() throws InterruptedException, IOException, SMErrProcessor.JSmartMathErrException, ErrProcessor.JFCALCExpErrException {

        if (selectedFiles.isEmpty()) {
            PutText("Please choose a picture(s)\n", false, Color.BLACK, "Arial", 16);
            return;
        }
        int i = 0;
        String calcA = null;
        for (File x : selectedFiles) {

            PutText(i++ + ")", false, Color.BLACK, "Menlo", 18);
            //************************
            //      PreProcess
            //************************

            preprocessImage(x.getName(), x.getParent(),
                    "res" + File.separator + "prepresult", 100, true);

            PutText("Image " + x.getName() + " has been processed",
                    false, Color.BLACK, "Arial", 16);

            //todo yx 17 19.48


            //************************
            //      RunProcess
            //************************
            String strExpressions = recognizeMathExpr(x.getParent(),x.getName(),"res" + File.separator + "prepresult" + File.separator + x.getName() + ".bmp",
                    clm, mwm, true);
            PutText("The recognition result:" + "\n" + strExpressions, false, Color.RED, "Arial", 16);


            //************************
            //      calculate
            //************************
            if (strExpressions.contains("\n"))//方程组
            {
                try {
                    strExpressions = Cut(strExpressions);
                }
                catch (ErrProcessor.JFCALCExpErrException e) {
                    System.out.println("not FCZ but have \\n ");
                }
            } else if (strExpressions.contains("integrate") && strExpressions.contains("=="))//积分方程
            {
                String calcA1;
                String[] strarraycup = strExpressions.split("==");
                calcA1 = SmartCalcProcLib.calculate(strarraycup[0], false);
                String temp = calcA1.replace(File.separator + "text", "");//改了\\
                temp = temp.replace("\"", "");
                temp = temp.replace("{", "(");
                temp = temp.replace("}", ")");
                System.out.println(temp);
                temp = temp.replace("×", "*");
                temp = temp.replace("^", "**");
                strExpressions = temp + "==" + strarraycup[1];
            }
            if (strExpressions.contains("derivative")) {//求导
                Function df = new Function(strExpressions);
                String arg = Function.x + Function.ccount + Function.str;//这里的df.ccount，为求导阶数，目前一阶导数测试通过

                Socket socket = new Socket("127.0.0.1", 9998);
                System.out.println("Client start!");
                PrintWriter out = new PrintWriter(socket.getOutputStream()); // 输出，to 服务器 socket
                out.println("derivative:" + arg);
                out.flush(); // 刷缓冲输出，to 服务器

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream())); // 输入， from 服务器 socket
                calcA = in.readLine();
                System.out.println("Client end!");
                socket.close();
            } else {
                calcA = SmartCalcProcLib.calculate(strExpressions, false);
            }

            String temp = calcA.replace(File.separator + "text", "");
            temp = temp.replace("\"", "");
            temp = temp.replace("{", "(");
            temp = temp.replace("}", ")");
            if (temp.length() == 0)
                temp = "I can't calculate it yet";
            PutText("ANSWER:\n" + temp + "\n", false, Color.BLACK, "Arial", 16);
            System.out.println(temp);
        }
    }

//    public String getRes() {
//        return res;
//    }

    //  This method can be used outside (in other files)
    //  to add text to the CommandFlow (right)
    // clearField == true, CommandFlow we'll be cleared
    public void PutText(String text, boolean clearField, Color color, String fontName, int size) {
        if (clearField)
            CommandFlow.getChildren().clear();

        Text caption = new Text(text);
        caption.setFont(Font.font(fontName, size));
        caption.setFill(color);
        CommandFlow.getChildren().addAll(caption, new Text("\n"));
    }

    // This method can be used outside (in other files)
    // Returns path to the opened image
    //如果在别的文档不能用这个函数，就可以删掉
    // 注意函数自动增加 '\n'
    private String getSelectedImagePath() {
        return SelectedImagePath;
    }

    public static class Function {
        public static String str;
        public static String x;
        public static int ccount;

        public Function(String Ostr) {
            int[] count = new int[6];
            count[0] = Ostr.indexOf("\"");
            for (int i = 1; i < 6; i++) {
                count[i] = Ostr.indexOf("\"", count[i - 1] + 1);
            }
            str = Ostr.substring(count[0] + 1, count[1]);
            x = Ostr.substring(count[2] + 1, count[3]);
            ccount = 1;
        }

        //  计算求导阶数，并赋值给ccount
        public static void StringCount(String str) {
            int index = 0;
            String key = "derivative";
            while ((index = str.indexOf(key)) != -1) {
                str = str.substring(index + key.length());
                ccount++;
            }

        }
    }

    public static byte[][] preprocessImage(String strImageFile, String strSrcFolder, String strDestFolder, int nPixelDiv, boolean bFilterSmooth) throws InterruptedException {
        System.out.println("===========================================================================");
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

    private void showImage(File pic) {
        if (pic == null) {
            // clears image field
            AP.getChildren().clear();
            return;
        }

        ImageView iv = new ImageView(new Image(pic.toURI().toString()));

        iv.setFitWidth(AP.getWidth());
        iv.setFitHeight(AP.getHeight());
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.setCache(false);

        SelectedImagePath = pic.getAbsolutePath();

        AP.getChildren().clear();
        AP.getChildren().add(iv);

        FileAddressField.setText(SelectedImagePath);
        fileNameLabel.setText(pic.getName());
    }
}