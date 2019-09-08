/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyzapps.uptloadermgr;

import com.cyzapps.mathrecog.ExprRecognizer;
import com.cyzapps.mathrecog.UnitPrototypeMgr;
import com.cyzapps.mathrecog.UnitPrototypeMgr.UnitProtoType;
import com.cyzapps.mathrecog.UnitRecognizer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tonyc
 */
public abstract class UPTJavaLoaderMgr {
    public static final String LOAD_UPTS_JAVA = "UPTJavaLoader";
    public static final int LOAD_UPTS_JAVA_CNT_PRINT = 100;
    public static final int LOAD_UPTS_JAVA_CNT_SPRINT = 50;
    public static final int LOAD_UPTS_JAVA_CNT_SHANDWRITING = 100;

    public static String createLoadUPTJAVAHead(String strClassName, String strUPTLoadersFolder)    {
        String strReturn = "package com.cyzapps." + strUPTLoadersFolder + ";" + System.getProperty("line.separator");
        strReturn += "import com.cyzapps.mathrecog.UnitPrototypeMgr;" + System.getProperty("line.separator");
        strReturn += "import com.cyzapps.mathrecog.UnitPrototypeMgr.UnitProtoType;" + System.getProperty("line.separator");
        strReturn += System.getProperty("line.separator");
        strReturn += "public class " + strClassName + "   {" + System.getProperty("line.separator");
        strReturn += System.getProperty("line.separator");
        strReturn += "\tpublic static void load(UnitPrototypeMgr uptMgr) {" + System.getProperty("line.separator");
        return strReturn;
    }
    
    public static String outputUPTInfo2JAVA(UnitProtoType.Type unitType, String strFont, double dWMinNumStrokes, double dHMinNumStrokes, int nSave2Which, byte[][] biMatrix)  {
        // do not use UnitProtoType because UnitProtoType needs to create charUnit which is expensive.
        String strBiMatrixJAVA = "\t\t{" + System.getProperty("line.separator");
        strBiMatrixJAVA += "\t\t\tbyte[][] biMatrix = new byte[" + biMatrix.length + "][" + biMatrix[0].length + "];" + System.getProperty("line.separator");
        for (int idx = 0; idx < biMatrix.length; idx ++)    {
            for (int idx1 = 0; idx1 < biMatrix[0].length; idx1 ++)  {
                if (biMatrix[idx][idx1] != 0)   {
                    strBiMatrixJAVA += "\t\t\tbiMatrix[" + idx + "][" + idx1 + "] = " + biMatrix[idx][idx1] + ";" + System.getProperty("line.separator");
                }
            }
        }
        
        String strOutput = strBiMatrixJAVA;
        if ((nSave2Which & 1) == 1)    {
            strOutput += "\t\t\tuptMgr.addUnitPrototype(UnitProtoType.Type." + UnitProtoType.getTypeValueString(unitType) + ", \""
                    + strFont + "\", " + dWMinNumStrokes + ", "+ dHMinNumStrokes + ", biMatrix, UnitPrototypeMgr.NORMAL_UPT_LIST);" + System.getProperty("line.separator");
        }
        if ((nSave2Which & 2) == 2)    {
            strOutput += "\t\t\tuptMgr.addUnitPrototype(UnitProtoType.Type." + UnitProtoType.getTypeValueString(unitType) + ", \""
                    + strFont + "\", " + dWMinNumStrokes + ", "+ dHMinNumStrokes + ", biMatrix, UnitPrototypeMgr.HEXTENDABLE_UPT_LIST);" + System.getProperty("line.separator");
        }
        if ((nSave2Which & 4) == 4)    {
            strOutput += "\t\t\tuptMgr.addUnitPrototype(UnitProtoType.Type." + UnitProtoType.getTypeValueString(unitType) + ", \""
                    + strFont + "\", " + dWMinNumStrokes + ", "+ dHMinNumStrokes + ", biMatrix, UnitPrototypeMgr.VEXTENDABLE_UPT_LIST);" + System.getProperty("line.separator");
        }
        if ((nSave2Which & 8) == 8)    {
            strOutput += "\t\t\tuptMgr.addUnitPrototype(UnitProtoType.Type." + UnitProtoType.getTypeValueString(unitType) + ", \""
                    + strFont + "\", " + dWMinNumStrokes + ", "+ dHMinNumStrokes + ", biMatrix, UnitPrototypeMgr.WORD_UPT_LIST);" + System.getProperty("line.separator");
        }
        strOutput += "\t\t}" + System.getProperty("line.separator");
        return strOutput;
    }
    
    public static String createLoadUPTJAVATail()    {
        String strReturn = "\t}" + System.getProperty("line.separator") + "}" + System.getProperty("line.separator");
        return strReturn;
    }
    
    public static void load(boolean bLoadPrintChars, boolean bLoadSPrintChars, boolean bLoadSHandwritingChars)   {
        // load prototypes, this is much quicker as it is coded in source.
        int idxUPTJavaLoader = 0;
        if (bLoadPrintChars) {
            for (idxUPTJavaLoader = 0; idxUPTJavaLoader < UPTJavaLoaderMgr.LOAD_UPTS_JAVA_CNT_PRINT; idxUPTJavaLoader++)    {
                String strUPTJLoaderId = "com.cyzapps.uptloadersprint." + UPTJavaLoaderMgr.LOAD_UPTS_JAVA + idxUPTJavaLoader;
                try {
                    Class<?> cls = Class.forName(strUPTJLoaderId);
                    Method method = cls.getMethod("load", UnitPrototypeMgr.class);
                    method.invoke(null, UnitRecognizer.msUPTMgrPrint);
                } catch(ClassNotFoundException e0)   {
                    Logger.getLogger(UPTJavaLoaderMgr.class.getName()).log(Level.SEVERE, null, e0);
                } catch (IllegalAccessException e1)  {
                    Logger.getLogger(UPTJavaLoaderMgr.class.getName()).log(Level.SEVERE, null, e1);
                } catch (InvocationTargetException e2)   {
                    Logger.getLogger(UPTJavaLoaderMgr.class.getName()).log(Level.SEVERE, null, e2);
                } catch (NoSuchMethodException e3)  {
                    Logger.getLogger(UPTJavaLoaderMgr.class.getName()).log(Level.SEVERE, null, e3);
                }
            }
        }

        if (bLoadSPrintChars) {
            for (idxUPTJavaLoader = 0; idxUPTJavaLoader < UPTJavaLoaderMgr.LOAD_UPTS_JAVA_CNT_SPRINT; idxUPTJavaLoader++)    {
                String strUPTJLoaderId = "com.cyzapps.uptloaderssprint." + UPTJavaLoaderMgr.LOAD_UPTS_JAVA + idxUPTJavaLoader;
                try {
                    Class<?> cls = Class.forName(strUPTJLoaderId);
                    Method method = cls.getMethod("load", UnitPrototypeMgr.class);
                    method.invoke(null, UnitRecognizer.msUPTMgrSPrint);
                } catch(ClassNotFoundException e0)   {
                    Logger.getLogger(UPTJavaLoaderMgr.class.getName()).log(Level.SEVERE, null, e0);
                } catch (IllegalAccessException e1)  {
                    Logger.getLogger(UPTJavaLoaderMgr.class.getName()).log(Level.SEVERE, null, e1);
                } catch (InvocationTargetException e2)   {
                    Logger.getLogger(UPTJavaLoaderMgr.class.getName()).log(Level.SEVERE, null, e2);
                } catch (NoSuchMethodException e3)  {
                    Logger.getLogger(UPTJavaLoaderMgr.class.getName()).log(Level.SEVERE, null, e3);
                }
            }
        }

        if (bLoadSHandwritingChars) {
            for (idxUPTJavaLoader = 0; idxUPTJavaLoader < UPTJavaLoaderMgr.LOAD_UPTS_JAVA_CNT_SHANDWRITING; idxUPTJavaLoader++)    {
                String strUPTJLoaderId = "com.cyzapps.uptloadersshandwriting." + UPTJavaLoaderMgr.LOAD_UPTS_JAVA + idxUPTJavaLoader;
                try {
                    Class<?> cls = Class.forName(strUPTJLoaderId);
                    Method method = cls.getMethod("load", UnitPrototypeMgr.class);
                    method.invoke(null, UnitRecognizer.msUPTMgrSHandwriting);
                } catch(ClassNotFoundException e0)   {
                    Logger.getLogger(UPTJavaLoaderMgr.class.getName()).log(Level.SEVERE, null, e0);
                } catch (IllegalAccessException e1)  {
                    Logger.getLogger(UPTJavaLoaderMgr.class.getName()).log(Level.SEVERE, null, e1);
                } catch (InvocationTargetException e2)   {
                    Logger.getLogger(UPTJavaLoaderMgr.class.getName()).log(Level.SEVERE, null, e2);
                } catch (NoSuchMethodException e3)  {
                    Logger.getLogger(UPTJavaLoaderMgr.class.getName()).log(Level.SEVERE, null, e3);
                }
            }
        }
        
        if (ExprRecognizer.getRecognitionMode() == ExprRecognizer.RECOG_PRINT_MODE) {
            UnitRecognizer.msUPTMgr = UnitRecognizer.msUPTMgrPrint;
        } else if (ExprRecognizer.getRecognitionMode() == ExprRecognizer.RECOG_SPRINT_MODE) {
            UnitRecognizer.msUPTMgr = UnitRecognizer.msUPTMgrSPrint;
        } else if (ExprRecognizer.getRecognitionMode() == ExprRecognizer.RECOG_SHANDWRITING_MODE) {
            UnitRecognizer.msUPTMgr = UnitRecognizer.msUPTMgrSHandwriting;
        }
    }
}
    
