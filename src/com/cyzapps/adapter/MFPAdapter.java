package com.cyzapps.adapter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Handler;


//import com.cyzapps.SmartMath.ActivitySettings;
import com.cyzapps.Jfcalc.BaseData.DATATYPES;
import com.cyzapps.Jfcalc.BaseData.DataClass;
import com.cyzapps.Jfcalc.ErrProcessor.ERRORTYPES;
import com.cyzapps.Jfcalc.ErrProcessor.JFCALCExpErrException;
import com.cyzapps.Jfcalc.MFPNumeric;
import com.cyzapps.Jmfp.Statement;
import com.cyzapps.Jmfp.ErrorProcessor.JMFPCompErrException;
import com.cyzapps.Jmfp.ScriptAnalyzer.ScriptStatementException;
import com.cyzapps.Jmfp.Statement.Statement_function;
import com.cyzapps.Jsma.SMErrProcessor.JSmartMathErrException;
import com.cyzapps.SmartMath.ActivitySettings;

import javax.naming.Context;
//import com.cyzapps.MFPFileManager.MFPFileManagerActivity;

public class MFPAdapter {
	public static LinkedList<MFPKeyWordInfo> m_slMFPKeyWordInfo = new LinkedList<MFPKeyWordInfo>();
	public static LinkedList<InternalFuncInfo> m_slInternalFuncInfo = new LinkedList<InternalFuncInfo>();
	public static LinkedList<FunctionEntry> m_slFunctionSpace = new LinkedList<FunctionEntry>();
	public static LinkedList<String> m_slFailedFilePaths = new LinkedList<String>();
	public static LinkedList<Statement_function> m_slRedefinedFunction = new LinkedList<Statement_function>();
	
	public static final String STRING_ASSET_SCRIPT_LIB_FOLDER_EXTENSION = "_lib";
	public static final String STRING_ASSET_SCRIPT_LIB_FOLDER = "predef_lib";
	public static final String STRING_ASSET_SCRIPT_MATH_LIB_FILE = "math.mfps";
	public static final String STRING_ASSET_SCRIPT_MISC_LIB_FILE = "misc.mfps";
	public static final String STRING_ASSET_SCRIPT_SIG_PROC_LIB_FILE = "sig_proc.mfps";
	public static final String STRING_ASSET_CHARTS_FOLDER_EXTENSION = "_lib";
	public static final String STRING_ASSET_CHARTS_FOLDER = "charts_lib";
	public static final String STRING_ASSET_CHART_EXAMPLE1_FILE = "chart_example1.mfpc";
	public static final String STRING_ASSET_CHART_EXAMPLE2_FILE = "chart_example2.mfpc";
	public static final String STRING_ASSET_ZIP_FILE = "assets.zip";
	public static final String STRING_ASSET_JMATHCMD_JAR_FILE = "JMathCmd.jar";
	public static final String STRING_ASSET_INTERNAL_FUNC_INFO_FILE = "InternalFuncInfo.txt";
	public static final String STRING_ASSET_ENGLISH_LANG_FOLDER = "en"; 
	public static final String STRING_ASSET_SCHINESE_LANG_FOLDER = "zh-CN"; 
	public static final String STRING_ASSET_TCHINESE_LANG_FOLDER = "zh-TW";
	public static final String STRING_ASSET_LANGUAGEINFO_FOLDER = "LanguageInfo";
	public static final String STRING_ASSET_MFP_KEY_WORDS_INFO_FILE = "MFPKeyWordsInfo.txt";

	public static final int INT_ASSET_PATH_MAX_CHILD_LEVEL = 32;	// assume asset path cannot be as deep as 32 level.
	
    public static final String STRING_PATH_DIVISOR = System.getProperty("file.separator");
	public static final int MAX_NUMBER_OF_OPEN_FILES = 2048;
	public static final long FD_EXPIRY_TIME = 3600000;

    public static MFPNumeric mmfpNumBigThresh = new MFPNumeric(100000000);
    public static MFPNumeric mmfpNumSmallThresh = new MFPNumeric("0.00000001"); //0.00000001 has to be accurately represented. So use string

	public static byte[] m_sbyteBuffer = new byte[32768];	//make it static so that save realloc time.
	
	public static void clear()
	{
		m_slMFPKeyWordInfo.clear();
		m_slInternalFuncInfo.clear();
		m_slFunctionSpace.clear();
		m_slFailedFilePaths.clear();
		m_slRedefinedFunction.clear();
	}
	
	public static boolean isEmpty()
	{
		if (m_slMFPKeyWordInfo.size() == 0 && m_slInternalFuncInfo.size() == 0 && m_slFunctionSpace.size() == 0
				&& m_slFailedFilePaths.size() == 0 && m_slRedefinedFunction.size() == 0)
			return true;
		return false;
	}
	
	public static boolean isFuncSpaceEmpty()
	{
		if (m_slFunctionSpace.size() == 0)
			return true;
		return false;
	}
	
	public static class FunctionEntry	{
		public String[] m_strarrayNameSpace = new String[0];
		public Statement_function m_sf = null;
		public int m_nStartStatementPos = -1;
		public int[] m_nlistHelpBlock = null;
		public String[] m_strLines = new String[0];
		public Statement[] m_sLines = new Statement[0];

		public boolean isSameFunction(Statement_function sf)	{
			if (m_sf.m_strFunctionName.equals(sf.m_strFunctionName))	{
				if (m_sf.m_strParams.length == sf.m_strParams.length)	{
					if (sf.m_strParams.length == 0)	{
						return true;
					} else if (sf.m_strParams.length > 0
							&& m_sf.m_strParams[m_sf.m_strParams.length - 1].equals("opt_argv")
							&& sf.m_strParams[sf.m_strParams.length - 1].equals("opt_argv"))	{
						return true;
					} else if (sf.m_strParams.length > 0
							&& !m_sf.m_strParams[m_sf.m_strParams.length - 1].equals("opt_argv")
							&& !sf.m_strParams[sf.m_strParams.length - 1].equals("opt_argv"))	{
						return true;
					}
				}
			}
			return false;
		}
		public boolean matchFunction(String strFunctionName, int nNumofParams)	{
			if (m_sf.m_strFunctionName.equals(strFunctionName))	{
				if (m_sf.m_strParams.length == nNumofParams && !(m_sf.m_bIncludeOptParam))	{
					// make sure function name and the number of parameters match.
					return true;
				} else if (m_sf.m_strParams.length - 1 <= nNumofParams && m_sf.m_bIncludeOptParam)	{
					// if there are optional parameters.
					return true;
				}
			}
			return false;
		}
		
	}
	


    public static FunctionEntry loadSession(String[] strlistSession) throws JMFPCompErrException    {
		LinkedList<Statement> lAllStatements = new LinkedList<Statement>();
        Statement sSessionStart = new Statement("function session_function()", 0);
        sSessionStart.analyze();
        lAllStatements.add(sSessionStart);
        boolean bInHelpBlock = false;
		Statement sCurrent = null;
        for (int idx = 0; idx < strlistSession.length; idx ++)  {
            Statement sLine = new Statement(strlistSession[idx], idx + 1);
            if (bInHelpBlock == false && sCurrent != null)	{
                // this statement needs to be appended to last one (only if not in a help block)
                sCurrent.concatenate(sLine);
            } else	{
                sCurrent = sLine;
            }
            if (bInHelpBlock == false && sCurrent.isFinishedStatement() == false)	{
                continue;
            }
            try	{
                sCurrent.analyze();
            } catch(JMFPCompErrException e)	{
                // analyzing might trigger some exceptions which should be ignored.
                sCurrent.m_eAnalyze = e;
            } catch (Exception e)   {
                // analyzing might trigger some exceptions which should be ignored.
                sCurrent.m_eAnalyze = e;
            }
            if (bInHelpBlock == false)	{
                lAllStatements.add(sCurrent);
                if (sCurrent.m_statementType != null
                        && sCurrent.m_statementType.getType().equals("help"))	{
                    bInHelpBlock = true;
                }
            } else if (sCurrent.m_statementType != null
                        && sCurrent.m_statementType.getType().equals("endh"))	{
                lAllStatements.add(sCurrent);
                bInHelpBlock = false;
            }
            sCurrent = null;
        }
        Statement sSessionEnd = new Statement("endf", strlistSession.length + 1);
        sSessionEnd.analyze();
        lAllStatements.add(sSessionEnd);

        FunctionEntry functionEntry = new FunctionEntry();
        functionEntry.m_strarrayNameSpace = new String[0];
        functionEntry.m_sf = (Statement_function)(sSessionStart.m_statementType);;
        functionEntry.m_nStartStatementPos = 0;
        functionEntry.m_strLines = strlistSession;
        functionEntry.m_sLines = lAllStatements.toArray(new Statement[0]);;
        functionEntry.m_nlistHelpBlock = new int[0];
        return functionEntry;
    }
    
	public static class InternalFuncInfo {

		public String mstrFuncName = "";
		public int mnLeastNumofParams = 0;
		public boolean mbOptParam = false;
		public String[] mstrlistHelpInfo = new String[0];
	}

	public static class MFPKeyWordInfo	{
		public String mstrKeyWordName = "";
		public Map<String, String> mmapHelpInfo = new HashMap<String, String>();
		
		public String extractHelpFromMFPKeyWordInfo(String strLang)	{
			String strLangLowerCase = strLang.trim().toLowerCase(Locale.US);
			String strHelp = mmapHelpInfo.get(strLangLowerCase);
			if (strHelp == null)	{
				strHelp = mmapHelpInfo.get("default");
				if (strHelp == null)	{
					strHelp = "";
				}
			}
			return strHelp;
		}
	}


	
	// get help information for all the functions having the same name.
	public static String getMFPKeyWordHelp(String strMFPKeyWord, String strLang)	{
		for (MFPKeyWordInfo itr : m_slMFPKeyWordInfo)	{
			if (itr.mstrKeyWordName.equalsIgnoreCase(strMFPKeyWord))	{
				return itr.extractHelpFromMFPKeyWordInfo(strLang);
			}
		}
		return null;	// this is not a keyword with help info.
	}
	
	public static boolean msbIsReloadingAll = false;

	// the following functions is reloading libs or at activity reloading


	// get help information from a specific help block.
	public static String extractHelpFromBlock(String[] strLines, int nStartLine, int nEndLine, String strLang)	{
		String strReturn = "";
		String strDefault = "";
		boolean bLanguageFound = false;
		if (nStartLine > 0 && nStartLine < strLines.length
				&& nEndLine > 0 && nEndLine <= strLines.length
				&& nStartLine < nEndLine)	{
			boolean bInSubBlock = false;
			boolean bInProperSubBlock = false;
			boolean bInDefaultSubBlock = false;
			for (int index = nStartLine; index < nEndLine - 1; index++)	{
				// means this part of block is for all the language
				String strLine = strLines[index];
				String strTrimLine = strLine.trim();
				if (strTrimLine.length() > 0 && strTrimLine.charAt(0) == '@')	{
					if (strTrimLine.compareToIgnoreCase("@end") == 0)	{
						if (bInDefaultSubBlock == true)	{
							bInDefaultSubBlock = false;
						}
						if (bInProperSubBlock == true)	{
							bInProperSubBlock = false;
						}
						if (bInSubBlock == true)	{
							bInSubBlock = false;
						}
					} else if (strTrimLine.length() >= "@language:".length())	{
						if (strTrimLine.compareToIgnoreCase("@language:") == 0)	{
							bInDefaultSubBlock = true;
						}
						if (strTrimLine.compareToIgnoreCase("@language:" + strLang) == 0)	{
							bLanguageFound = true;
							bInProperSubBlock = true;
						}
						bInSubBlock = true;
					}
				} else {
					if (bInSubBlock == false)	{
						strReturn += strLines[index] + "\n";
						strDefault += strLines[index] + "\n";
					}
					if (bInProperSubBlock)	{
						strReturn += strLines[index] + "\n";
					}
					if (bInDefaultSubBlock)	{
						strDefault += strLines[index] + "\n";
					}
				}
			}
		}
		
		if (bLanguageFound == false)	{
			return strDefault;
		} else	{
			return strReturn;
		}
	}
	
	// get help information for the function.
	public static String getFunctionHelp(String strFuncName, int nNumofParam, boolean bIncludeOpt, String strLang)	{
		ListIterator<FunctionEntry> itrFE = m_slFunctionSpace.listIterator();
		String strReturn = "";
		boolean bFunctionFound = false;
		while (itrFE.hasNext())	{
			FunctionEntry fe = itrFE.next();
			if (fe.m_sf.m_strFunctionName.equalsIgnoreCase(strFuncName)
					&& fe.m_sf.m_bIncludeOptParam == bIncludeOpt
					&& (bIncludeOpt?((fe.m_sf.m_strParams.length - 1) == nNumofParam)
							:(fe.m_sf.m_strParams.length == nNumofParam)))	{
				bFunctionFound = true;
				if (fe.m_nlistHelpBlock != null)	{
					strReturn = extractHelpFromBlock(fe.m_strLines,
													fe.m_nlistHelpBlock[0],
													fe.m_nlistHelpBlock[1],
													strLang);
					break;
				}
			}
		}
		if (bFunctionFound)	{
			return strReturn;
		}
		// function not found, search predefined function info.
		ListIterator<InternalFuncInfo> itrIFI = m_slInternalFuncInfo.listIterator();
		strReturn = "";
		while (itrIFI.hasNext())	{
			InternalFuncInfo ifi = itrIFI.next();
			if (ifi.mstrFuncName.equalsIgnoreCase(strFuncName)
					&& ifi.mbOptParam == bIncludeOpt
					&& ifi.mnLeastNumofParams == nNumofParam)	{
				bFunctionFound = true;
				strReturn = extractHelpFromBlock(ifi.mstrlistHelpInfo,
												1,
												ifi.mstrlistHelpInfo.length,
												strLang);
				break;
			}
		}
		if (bFunctionFound)	{
			return strReturn;
		} else {
			return null;
		}
	}
	
	// get help information for all the functions having the same name.
	public static String getFunctionHelp(String strFuncName, String strLang)	{
		ListIterator<FunctionEntry> itrFE = m_slFunctionSpace.listIterator();
		String strFuncDeclares = "";
		String strReturn = "";
		while (itrFE.hasNext())	{
			FunctionEntry fe = itrFE.next();
			if (fe.m_sf.m_strFunctionName.compareToIgnoreCase(strFuncName) == 0)	{
				String strFuncDeclare = "";
				if (fe.m_sf.m_bIncludeOptParam)	{
					strFuncDeclare += fe.m_sf.m_strFunctionName + "("
								+ (fe.m_sf.m_strParams.length - 1) + "...)";
				} else	{
					strFuncDeclare += fe.m_sf.m_strFunctionName + "("
							+ fe.m_sf.m_strParams.length + ")";						
				}
				if (strFuncDeclares.indexOf(strFuncDeclare) >= 0)	{
					// this function has been defined before, should not be included.
					continue;
				}
				strFuncDeclares += ":" + strFuncDeclare;
				strReturn += strFuncDeclare + " :\n";
				if (fe.m_nlistHelpBlock != null)	{
					strReturn += extractHelpFromBlock(fe.m_strLines,
												fe.m_nlistHelpBlock[0],
												fe.m_nlistHelpBlock[1],
												strLang);						
				}
			}
		}

		ListIterator<InternalFuncInfo> itrIFI = m_slInternalFuncInfo.listIterator();
		while (itrIFI.hasNext())	{
			InternalFuncInfo ifi = itrIFI.next();
			if (ifi.mstrFuncName.equalsIgnoreCase(strFuncName))	{
				String strFuncDeclare = ifi.mstrFuncName + "(" + ifi.mnLeastNumofParams;
				if (ifi.mbOptParam)	{
					strFuncDeclare += "...)";
				} else	{
					strFuncDeclare += ")";						
				}
				if (strFuncDeclares.indexOf(strFuncDeclare) >= 0)	{
					// this function has been defined before, should not be included.
					continue;
				}
				strFuncDeclares += ":" + strFuncDeclare;
				strReturn += strFuncDeclare + " :\n";
				strReturn += extractHelpFromBlock(ifi.mstrlistHelpInfo,
												1,
												ifi.mstrlistHelpInfo.length,
												strLang);
			}
		}
		return strReturn;
	}
	
	// return data recorded (string[0]) and data shown (string[1])
	public static String[] outputDatum(DataClass datumAnswer) throws JFCALCExpErrException	{
		datumAnswer.validateDataClass(); // prevent refer to itself
		
		String[] strarrayReturn = new String[2];
		String strAnswerRecorded = new String();
		String strAnswerShown = new String();
		
        /* try to convert a double value to integer if we can */
        if (datumAnswer.getDataType() == DATATYPES.DATUM_DOUBLE && datumAnswer.getDataValue().isActuallyInteger()) {
            datumAnswer.setDataValue(datumAnswer.getDataValue().toIntOrNanInfMFPNum(), DATATYPES.DATUM_INTEGER);
        }
		
		if (datumAnswer.getDataType() == DATATYPES.DATUM_NULL)
		{
			strAnswerShown = strAnswerRecorded = "NULL";
		}
		else if (datumAnswer.getDataType() == DATATYPES.DATUM_BOOLEAN)
		{
			/* If the answer is a boolean */
            if (datumAnswer.getDataValue().mType == MFPNumeric.Type.MFP_NAN_VALUE)  {
                throw new JFCALCExpErrException(ERRORTYPES.ERROR_CAN_NOT_CONVERT_NAN_VALUE_TO_BOOLEAN);
            }
            else if (datumAnswer.getDataValue().isActuallyZero())
			{
				strAnswerShown = strAnswerRecorded = "FALSE";
			}
			else
			{
				strAnswerShown = strAnswerRecorded = "TRUE";
			}
		}
		else if (datumAnswer.getDataType() == DATATYPES.DATUM_INTEGER)
		{
			/* If the answer is an integer, assume integer is always in a range of long 如果答案是整数，假设整数总是在长范围内*/
            if (datumAnswer.getDataValue().isNanOrInf()) {
                strAnswerShown = strAnswerRecorded = datumAnswer.getDataValue().toString();
            } else if (isVeryBigorSmallValue(datumAnswer.getDataValue())
                    && !datumAnswer.getDataValue().isActuallyZero())  { // 0 is also a very small value but should not use scientific notation0也是一个很小的值，但不应该使用科学符号
                Format format = null;
                if (ActivitySettings.msnBitsofPrecision != -1)   {
                    String strTmp = "";
                    for (int idx = 0; idx < ActivitySettings.msnBitsofPrecision; idx ++) {
                        strTmp += "#";
                    }
                    if (ActivitySettings.msnBitsofPrecision == 0)    {
                        format = new DecimalFormat("0E0", new DecimalFormatSymbols(Locale.US));	// otherwise, 0.5 may be output as 0,5 in spanish
                    } else  {
                        format = new DecimalFormat("0." + strTmp + "E0", new DecimalFormatSymbols(Locale.US));
                    }
                } else  {
                    format = new DecimalFormat("0.0E0", new DecimalFormatSymbols(Locale.US));
                }
                MFPNumeric mfpNumIntValue = datumAnswer.getDataValue().toIntOrNanInfMFPNum();
				strAnswerRecorded = strAnswerShown = format.format(mfpNumIntValue.toBigInteger());
			} else {
				strAnswerRecorded = strAnswerShown = datumAnswer.getDataValue().toIntOrNanInfMFPNum().toString();				
			}
		}
		else if (datumAnswer.getDataType() == DATATYPES.DATUM_DOUBLE)
		{
            Format format = null;
			if (datumAnswer.getDataValue().isNanOrInf()) {
                strAnswerShown = strAnswerRecorded = datumAnswer.getDataValue().toString();
            } else if (isVeryBigorSmallValue(datumAnswer.getDataValue())) {
                String strTmp = "";
                for (int idx = 0; idx < ActivitySettings.msnBitsofPrecision; idx ++) {
                    strTmp += "#";
                }
                if (ActivitySettings.msnBitsofPrecision != -1)   {
                    if (ActivitySettings.msnBitsofPrecision == 0)    {
                        format = new DecimalFormat("0E0", new DecimalFormatSymbols(Locale.US));
                    } else  {
                        format = new DecimalFormat("0." + strTmp + "E0", new DecimalFormatSymbols(Locale.US));
                    }
                } else  {
                    format = new DecimalFormat("0.0E0", new DecimalFormatSymbols(Locale.US));
                }
				strAnswerRecorded = strAnswerShown = format.format(datumAnswer.getDataValue().toBigDecimal());
            }
            else
            {
                String strTmp = "";
                for (int idx = 0; idx < ActivitySettings.msnBitsofPrecision; idx ++) {
                    strTmp += "#";
                }
                if (ActivitySettings.msnBitsofPrecision != -1)   {
                    if (ActivitySettings.msnBitsofPrecision == 0)    {
                        format = new DecimalFormat("0", new DecimalFormatSymbols(Locale.US));
                    } else  {
                        String strValueString = datumAnswer.getDataValue().toString();
                        int decimalIndex = strValueString.indexOf( '.' );
                        int nNumofZerosB4SigDig = 0;
                        if (decimalIndex != -1)	{	// this means no decimal point, it is an integer
                        	int idx = 0;
                        	for (idx = 0; idx < strValueString.length(); idx ++)	{
                        		if (strValueString.charAt(idx) >= '1' && strValueString.charAt(idx) <= '9')	{
                        			break;	// siginificant digit start from here.
                        		}
                        	}
                        	if (idx > decimalIndex)	{
                        		nNumofZerosB4SigDig = idx - decimalIndex - 1;
                        	}
                        }
                        String strTmpZeroB4SigDig = "";
                        for (int idx = 0; idx < nNumofZerosB4SigDig; idx ++) {
                        	strTmpZeroB4SigDig += "#";
                        }
                        format = new DecimalFormat("0." + strTmpZeroB4SigDig + strTmp, new DecimalFormatSymbols(Locale.US));
                    }
                } else  {
                    format = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.US));
                }
				strAnswerRecorded = strAnswerShown = format.format(datumAnswer.getDataValue().doubleValue());
            }
		}
		else if (datumAnswer.getDataType() == DATATYPES.DATUM_COMPLEX)
		{
			boolean bImageNegative = false;
			if (datumAnswer.getImage().isActuallyNegative())	{	// this means NAN is always positive.
				bImageNegative = true;
			}
			String[] strOutputReal = outputDatum(datumAnswer.getRealDataClass());
			DataClass datumImage = datumAnswer.getImageDataClass();
			String strRealImageConn = "+";
			if (bImageNegative)	{
				datumImage.setDataValue(datumImage.getDataValue().negate(), datumImage.getDataType());
				strRealImageConn = "-";
			}
			String[] strOutputImage = outputDatum(datumImage);
			if (datumAnswer.getReal().isActuallyZero() && datumAnswer.getImage().isActuallyZero())	{
				strAnswerShown = strAnswerRecorded = "0";
			} else if (datumAnswer.getReal().isActuallyZero())	{
				if (strOutputImage[1].equals("1"))	{
					strAnswerRecorded = strAnswerShown = "i";
				} else if (datumAnswer.getImage().isNanOrInf()) {
                    // inf*i is not infi but nan + infi. So have to use infi here. Same as nani.
					strAnswerRecorded = strOutputImage[0] + "i";
					strAnswerShown = strOutputImage[1] + "i";
				} else	{
					strAnswerRecorded = strOutputImage[0] + " * i";
					strAnswerShown = strOutputImage[1] + " * i";
				}
				if (bImageNegative)	{
					strAnswerRecorded = strRealImageConn + strAnswerRecorded;
					strAnswerShown = strRealImageConn + strAnswerShown;
				}
			} else if (datumAnswer.getImage().isActuallyZero())	{
				strAnswerRecorded = strOutputReal[0];
				strAnswerShown = strOutputReal[1];
			} else if (datumAnswer.getImage().isNanOrInf()) {
                // inf*i is not infi but nan + infi. So have to use infi here. Same as nani.
				// for nani, need not to worry about strRealImageConn because it should be positive.
				strAnswerRecorded = strOutputReal[0] + " " + strRealImageConn + " " + strOutputImage[0] + "i";
				strAnswerShown = strOutputReal[1] + " " + strRealImageConn + " " + strOutputImage[1] + "i";
			} else	{
				strAnswerRecorded = strOutputReal[0] + " " + strRealImageConn + " " + strOutputImage[0] + " * i";
				strAnswerShown = strOutputReal[1] + " " + strRealImageConn + " " + strOutputImage[1] + " * i";
			}
		}
		else if (datumAnswer.getDataType() == DATATYPES.DATUM_REF_DATA)
		{
			strAnswerRecorded = strAnswerShown = "[";
			for (int index = 0; index < datumAnswer.getDataListSize(); index ++)
			{
				if (index == (datumAnswer.getDataListSize() - 1))
				{
					strAnswerRecorded += (datumAnswer.getDataList()[index] == null)?
							outputDatum(new DataClass())[0]
							:outputDatum(datumAnswer.getDataList()[index])[0];
					strAnswerShown += (datumAnswer.getDataList()[index] == null)?
							outputDatum(new DataClass())[1]
							:outputDatum(datumAnswer.getDataList()[index])[1];
				}
				else
				{
					strAnswerRecorded += (datumAnswer.getDataList()[index] == null)?
							outputDatum(new DataClass())[0]
							:outputDatum(datumAnswer.getDataList()[index])[0] + ", ";
					strAnswerShown += (datumAnswer.getDataList()[index] == null)?
							outputDatum(new DataClass())[1]
							:outputDatum(datumAnswer.getDataList()[index])[1] + ", ";
				}
			}
			strAnswerRecorded = strAnswerShown += "]";
		}
		else if (datumAnswer.getDataType() == DATATYPES.DATUM_STRING)
		{
			strAnswerShown = strAnswerRecorded = "\"" + datumAnswer.getStringValue() + "\"";
		}
		else if (datumAnswer.getDataType() == DATATYPES.DATUM_REF_FUNC)
		{
			strAnswerRecorded = datumAnswer.getFunctionName() + "()";
			strAnswerShown = "Function name: " + datumAnswer.getFunctionName();
		}
        else if (datumAnswer.getDataType() == DATATYPES.DATUM_ABSTRACT_EXPR)
        {
            strAnswerShown = strAnswerRecorded = datumAnswer.output();  // actually calls getAExpr().output().
        }
		strarrayReturn[0] = strAnswerRecorded;
		strarrayReturn[1] = strAnswerShown;
		return strarrayReturn;
	}
	
	public static String outputException(Exception e)	{
		String strOutput = new String();
		/* If there is an error */
		if (e instanceof JFCALCExpErrException)	{
			JFCALCExpErrException eExp = (JFCALCExpErrException)e;
			String strError = eExp.m_se.getErrorInfo();
			strOutput = String.format("%s\n", strError);
			if (eExp.m_strBlockName != null)	{
				String strTmp1 = new String();
				strTmp1 = String.format("In function %s :\n", eExp.m_strBlockName);
				String strTmp2 = outputException(eExp.m_exceptionLowerLevel);
				strOutput += strTmp1 + strTmp2;
			}
		} else if(e instanceof JMFPCompErrException)	{
			JMFPCompErrException eMFP = (JMFPCompErrException)e;
			String strTmp1 = new String();
			if (eMFP.m_se.m_nStartLineNo == eMFP.m_se.m_nEndLineNo)	{
				strTmp1 = String.format("\tLine %d : ", eMFP.m_se.m_nStartLineNo);
			} else	{
				strTmp1 = String.format("\tLines %d-%d : ", eMFP.m_se.m_nStartLineNo, eMFP.m_se.m_nEndLineNo);
			}
			String strError = eMFP.m_se.getErrorInfo();
			String strTmp2 = new String();
			strTmp2 = String.format("%s\n", strError);
			strOutput = outputException(eMFP.m_exceptionLowerLevel);
			strOutput = strTmp1 + strTmp2 + strOutput;
		} else if (e instanceof ScriptStatementException){
			ScriptStatementException eSSE = (ScriptStatementException)e;
			if (eSSE.m_statement.m_nStartLineNo == eSSE.m_statement.m_nEndLineNo)	{
				strOutput = String.format("\tLine %d : %s\n", eSSE.m_statement.m_nStartLineNo, e.toString());
			} else	{	
				strOutput = String.format("\tLines %d-%d : %s\n", eSSE.m_statement.m_nStartLineNo,
																eSSE.m_statement.m_nEndLineNo,
																e.toString());
			}
		} else if (e instanceof JSmartMathErrException) {
			JSmartMathErrException eSM = (JSmartMathErrException)e;
			String strError = eSM.m_se.getErrorInfo();
			strOutput = String.format("%s\n", strError);
			if (eSM.m_strBlockName != null)	{
				String strTmp1 = new String();
				strTmp1 = String.format("In function %s :\n", eSM.m_strBlockName);
				String strTmp2 = outputException(eSM.m_exceptionLowerLevel);
				strOutput += strTmp1 + strTmp2;
			}
		} else if (e != null)	{
			strOutput = String.format("%s\n", e.toString());
		}
		return strOutput;
	}

	public static boolean isVeryBigorSmallValue(MFPNumeric mfpNumValue)
	{
//		if (ActivitySettings.msnBigSmallThresh < 0)
//			return false;
		if ((mfpNumValue.compareTo(mmfpNumBigThresh) >= 0)
				|| (mfpNumValue.compareTo(mmfpNumBigThresh.negate()) <= 0)
				|| ((mfpNumValue.compareTo(mmfpNumSmallThresh)) <= 0)
	                && (mfpNumValue.compareTo(mmfpNumSmallThresh.negate()) >= 0)) {
			return true;
		} else {
			return false;
		}
	}



}
