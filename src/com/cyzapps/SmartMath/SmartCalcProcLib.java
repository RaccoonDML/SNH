package com.cyzapps.SmartMath;

import java.util.LinkedList;
import java.util.Locale;

//import com.cyzapps.SmartMath.ActivitySmartCalc.PlotGraphPlotter;
//import com.cyzapps.SmartMath.ActivitySmartCalc.SmartCalcAbstractExprInterrupter;
//import com.cyzapps.SmartMath.ActivitySmartCalc.SmartCalcFunctionInterrupter;
//import com.cyzapps.SmartMath.ActivitySmartCalc.SmartCalcScriptInterrupter;
//import com.cyzapps.GraphDemon.ActivityChartDemon;
import com.cyzapps.Jfcalc.ExprEvaluator;
import com.cyzapps.Jfcalc.FuncEvaluator;
import com.cyzapps.Jfcalc.MFPNumeric;
import com.cyzapps.Jfcalc.BaseData.CalculateOperator;
import com.cyzapps.Jfcalc.BaseData.CurPos;
import com.cyzapps.Jfcalc.BaseData.DATATYPES;
import com.cyzapps.Jfcalc.BaseData.DataClass;
import com.cyzapps.Jfcalc.BaseData.OPERATORTYPES;
import com.cyzapps.Jmfp.ScriptAnalyzer;
import com.cyzapps.Jmfp.SolveAnalyzer;
import com.cyzapps.Jmfp.VariableOperator;
import com.cyzapps.Jmfp.VariableOperator.Variable;
//import com.cyzapps.Jsma.AEAssign;
import com.cyzapps.Jsma.AECompare;
import com.cyzapps.Jsma.AEConst;
import com.cyzapps.Jsma.AEFunction;
import com.cyzapps.Jsma.AEInvalid;
import com.cyzapps.Jsma.AEPosNegOpt;
import com.cyzapps.Jsma.AEVar;
import com.cyzapps.Jsma.AbstractExpr;
import com.cyzapps.Jsma.ExprAnalyzer;
import com.cyzapps.Jsma.PatternManager;
import com.cyzapps.Jsma.PtnSlvMultiVarsIdentifier;
import com.cyzapps.Jsma.SMErrProcessor;
import com.cyzapps.Jsma.UnknownVarOperator;
import com.cyzapps.Jsma.AbstractExpr.ABSTRACTEXPRTYPES;
import com.cyzapps.Jsma.AbstractExpr.SimplifyParams;
import com.cyzapps.Jsma.SMErrProcessor.JSmartMathErrException;
import com.cyzapps.Jsma.UnknownVarOperator.UnknownVariable;
//import com.cyzapps.MFPFileManager.MFPFileManagerActivity;
import com.cyzapps.adapter.AbstractExprConverter;
import com.cyzapps.adapter.MFPAdapter;
//import com.cyzapps.adapter.AbstractExprConverter;
//import com.cyzapps.adapter.MFPAdapter;

public class SmartCalcProcLib {

    public static String getLocalLanguage()
    {
	    Locale l = Locale.getDefault();  
	    String strLanguage = l.getLanguage();
	    if (strLanguage.equals("en"))	{
	    	return "English";
	    } else if (strLanguage.equals("fr"))	{
	    	return "French";
	    } else if (strLanguage.equals("de"))	{
	    	return "German";
	    } else if (strLanguage.equals("it"))	{
	    	return "Itanian";
	    } else if (strLanguage.equals("ja"))	{
	    	return "Japanese";
	    } else if (strLanguage.equals("ko"))	{
	    	return "Korean";
	    } else if (strLanguage.equals("zh"))	{
	    	if (l.getCountry().equals("TW") || l.getCountry().equals("HK"))	{
	    		return "Traditional_Chinese";
	    	} else	{
	    		return "Simplified_Chinese";
	    	}
	    } else {
	    	return "";	// unknown language
	    }
    }
	
	public static String calculate(String strExpressions, boolean bShowInValidAExpr) {
		/*
		 * make sure that we do not output any log or show any chart in calculator screen
		 * or be interrupted.
		 * but calculator is still able to save files in disk.
		 */
		try {
			FuncEvaluator.msstreamConsoleInput = null;
			FuncEvaluator.msstreamLogOutput = null;
			//FuncEvaluator.msfunctionInterrupter = new SmartCalcFunctionInterrupter();
			//FuncEvaluator.msfileOperator = new MFPFileManagerActivity.MFPFileOperator();
			FuncEvaluator.msgraphPlotter = null;
			FuncEvaluator.msgraphPlotter3D = null;
			if (FuncEvaluator.mspm == null) {
				FuncEvaluator.mspm = new PatternManager();
				try {
					FuncEvaluator.mspm.loadPatterns(2);    // load pattern is a very time consuming work. So only do this if needed.
				} catch (Exception e) {
					// load all integration patterns. Assume load patterns will not throw any exceptions.
				}
			}
			//ScriptAnalyzer.msscriptInterrupter = new SmartCalcScriptInterrupter();
			//AbstractExpr.msaexprInterrupter = new SmartCalcAbstractExprInterrupter();

			ExprEvaluator exprEvaluator = new ExprEvaluator();
			// clear variable namespaces
			exprEvaluator.m_lVarNameSpaces = new LinkedList<LinkedList<Variable>>();

			String[] strarrayExprs = strExpressions.split("\n");//多个表达式
			String strOutput = "";
			//String strOriginalExprColor = "color:#008000;", strExprColor = "color:#0000ff;", strResultColor = "color:#FFA500;",
			//		strVarValueColor = "color:#ff0000;", strVarNameColor = "color:#800080;";
			LinkedList<AbstractExpr> listaeInputExprs = new LinkedList<AbstractExpr>();
			LinkedList<UnknownVariable> listVarUnknown = new LinkedList<UnknownVariable>();    // The unknown variable list未知变量列表
			LinkedList<LinkedList<Variable>> lVarNameSpaces = new LinkedList<LinkedList<Variable>>();
			for (int idx = 0; idx < strarrayExprs.length; idx++) {
				if (strarrayExprs[idx].trim().length() == 0) {
					continue;    // empty string
				} else {
					/* evaluate the expression */
					CurPos curpos = new CurPos();
					curpos.m_nPos = 0;
					String strarrayAnswer[] = new String[2];
					AbstractExpr aexpr = new AEInvalid();
					try {
						/* evaluate the expression */
						aexpr = ExprAnalyzer.analyseExpression(strarrayExprs[idx], curpos);
						LinkedList<AbstractExpr> listAEVars = new LinkedList<AbstractExpr>();
						LinkedList<AbstractExpr> listAERootVars = new LinkedList<AbstractExpr>();
						AbstractExpr[] arrayAEs = new AbstractExpr[1];
						arrayAEs[0] = aexpr;
						PtnSlvMultiVarsIdentifier.lookupToSolveVarsInExprs(arrayAEs, listAEVars, listAERootVars);
						LinkedList<Variable> listVarThisSpace = new LinkedList<Variable>();
						lVarNameSpaces.add(listVarThisSpace);
						for (int idx1 = 0; idx1 < listAEVars.size(); idx1++) {
							if (listAEVars.get(idx1) instanceof AEVar) {
								String strName = ((AEVar) listAEVars.get(idx1)).mstrVariableName;
								if (VariableOperator.lookUpPreDefined(strName) == null) {    // this variable is not a predefined var nor does it exist
									//这个变量不是一个预定义的变量，也不存在
									UnknownVariable varUnknown = new UnknownVariable(strName);
									// if this variable hasn't been added, add it.
									if (UnknownVarOperator.lookUpList(strName, listVarUnknown) == null) {
										listVarUnknown.add(varUnknown);//总变量
										//System.out.println("`测试0："+AbstractExprConverter.convtPlainStr2QuotedUrl(strName));
									}
									if (VariableOperator.lookUpList(strName, listVarThisSpace) == null) {
										listVarThisSpace.add(varUnknown);//这个式子里的变量
										//System.out.println("`测试1："+AbstractExprConverter.convtPlainStr2QuotedUrl(strName));
									}
								}
							}
						}
						AbstractExpr aeSimplified = new AEInvalid();
						// shouldn't use exprEvaluator.evaluateExpression coz this function cannot set unknown var assigned.
						//不应该用exprEvaluator。因为这个函数不能设置分配给它的未知变量。
						aeSimplified = aexpr.simplifyAExprMost(listVarUnknown, lVarNameSpaces, new SimplifyParams(false, false, false));
						if (aeSimplified instanceof AEConst) { // ok, we get the value!
							String strResultOutput = MFPAdapter.outputDatum(((AEConst) aeSimplified).getDataClassRef())[1];
							//System.out.println("测试1、strResultOutput："+ strResultOutput);


							//strOutput +=//strResultOutput;//AbstractExprConverter.convtPlainStr2JQMathNoException(strarrayExprs[idx])
							//AbstractExprConverter.convtPlainStr2QuotedUrl(strResultOutput);//重要
							//AbstractExprConverter.convtPlainStr2QuotedUrl(strarrayExprs[idx])
							// + AbstractExprConverter.convtAExpr2JQMath(aexpr) + "$</a>&nbsp;<big>&rArr;</big>&nbsp;<a href=\""	// do not use convtAExprJQMath here because it cannot properly show hex or binary values.

							//System.out.println(AbstractExprConverter.convtPlainStr2QuotedUrl(strarrayExprs[idx])+"    "+AbstractExprConverter.convtPlainStr2JQMathNoException(strarrayExprs[idx]));
							// +AbstractExprConverter.convtAExpr2JQMath(aeSimplified) + "$</a></p>\n";
							boolean bConvertOutput2Expr = false;
							if (aexpr instanceof AEFunction) {
								String strFuncName = ((AEFunction) aexpr).mstrFuncName.trim();
								if (strFuncName.equalsIgnoreCase("integrate") && ((AEFunction) aexpr).mlistChildren.size() == 2
										&& ((AEConst) aeSimplified).getDataClassRef().getDataType() == DATATYPES.DATUM_STRING) {
									bConvertOutput2Expr = true;    // indefinite integration result is a string based expression. 不定积分结果是一个基于字符串的表达式。
								}
							}
							if (bConvertOutput2Expr) {
								try {
									// convert to expression and then output.转换为表达式，然后输出。
									AbstractExpr aeIndefIntegResult = ExprAnalyzer.analyseExpression(((AEConst) aeSimplified).getDataClassRef().getStringValue(), new CurPos());
									// System.out.println("测试："+"2："+ AbstractExprConverter.convtAExpr2JQMath(aeIndefIntegResult));
									strOutput += '\n' + AbstractExprConverter.convtAExpr2JQMath(aeIndefIntegResult);
									// System.out.println("测试："+"2："+ strOutput);
								} catch (Exception e) {
									// cannot convert anyway.无论如何不能转换。

									strOutput += '\n' + AbstractExprConverter.convtAExpr2JQMath(aeSimplified);
									// System.out.println("测试："+"2、："+ strOutput);
								}
							} else {

								strOutput += '\n' + AbstractExprConverter.convtAExpr2JQMath(aeSimplified);
								// System.out.println("测试："+"2、："+ strOutput);
							}
						} else {    // we just simplify it, still need solver
							//System.out.println("测试化简");
							strOutput +=//AbstractExprConverter.convtPlainStr2QuotedUrl(strarrayExprs[idx])+
									//+ "\" style=\"text-decoration: none;" + "\">$"
									// + AbstractExprConverter.convtAExpr2JQMath(aexpr) + "$</a></p>";	// do not use convtAExprJQMath here because it cannot properly show hex or binary values.
									"原式：" + AbstractExprConverter.convtPlainStr2JQMathNoException(strarrayExprs[idx]) + "\n";
							//System.out.println("测试3："+AbstractExprConverter.convtPlainStr2JQMathNoException(strarrayExprs[idx]));
							listaeInputExprs.add(aeSimplified);
						}
					} catch (Exception e) {
						if (bShowInValidAExpr) {    // camera calculation preview will not show any invalid expression.
							strOutput += AbstractExprConverter.convtPlainStr2QuotedUrl(strarrayExprs[idx]) + '\n';

							strarrayAnswer[0] = "Error";
							strarrayAnswer[1] = "<p>";// + context.getString(R.string.invalid_expr_to_solve) + " : ";
							if (e instanceof JSmartMathErrException
									&& ((JSmartMathErrException) e).m_se.m_enumErrorType
									== SMErrProcessor.ERRORTYPES.ERROR_ONLY_VARIABLE_CAN_BE_ASSIGNED_A_VALUE) {
								//strarrayAnswer[1] += context.getString(R.string.did_you_use_assign) + "</p>";
							} else {
								strarrayAnswer[1] += MFPAdapter.outputException(e) + ".</p>";
							}
							strOutput += strarrayAnswer[1];
						}
					}
				}
			}

			boolean bNeedArrow = true;
			for (int idx1 = 0; idx1 < listVarUnknown.size(); idx1++) {
				if (listVarUnknown.get(idx1).isValueAssigned()) {
					// this means value is assigned in the expression.这意味着在表达式中赋值。
					//System.out.println("测试赋值");
					String strVarName = listVarUnknown.get(idx1).getName();
					String strVarValue;
					try {
						if (bNeedArrow) {//没用
							//strOutput += "<p> <big>&rarr;</big> </p>";
							bNeedArrow = false;
						}
						strVarValue = MFPAdapter.outputDatum(listVarUnknown.get(idx1).getSolvedValue())[1];
						strOutput += AbstractExprConverter.convtPlainStr2QuotedUrl(strVarName) + '\n'
								+ AbstractExprConverter.convtPlainStr2JQMath(strVarName, false)
								+ AbstractExprConverter.convtPlainStr2QuotedUrl(strVarValue) + '\n'
								//+ "\" style=\"text-decoration: none;" + "\">$"
								+ AbstractExprConverter.convtAExpr2JQMath(new AEConst(listVarUnknown.get(idx1).getSolvedValue()));
						//+ "$</a></p>\n";
					} catch (Exception e) {
						// do not print variable and its value if there is any exception.如果有任何异常，不要打印变量及其值。
					}

				}
			}

			boolean bContinue2Solve = true;
			if (listaeInputExprs.size() != 0) {
				// now construct a solver.现在构造一个求解器
				//System.out.println("测试构造求解器");
				if (bNeedArrow) {
					//strOutput += "<p> <big>&rarr;</big> </p>";
					bNeedArrow = false;
				}
				LinkedList<AbstractExpr> listaeOriginals = new LinkedList<AbstractExpr>();
				String strExprs4Solver = new String();
				for (int idx = 0; idx < listaeInputExprs.size(); idx++) {
					try {
						AbstractExpr aeReturn = listaeInputExprs.get(idx);
						//SimplifyParams SimplifyParams1=new SimplifyParams(true,true,true);
						SimplifyParams SimplifyParams1 = new SimplifyParams(false, false, false);
						aeReturn = aeReturn.simplifyAExprMost(listVarUnknown, lVarNameSpaces, SimplifyParams1);    // do not simplify most here.这里不要过于简化。
						if (aeReturn instanceof AECompare && ((AECompare) aeReturn).moptType == OPERATORTYPES.OPERATOR_EQ) {
							// move the part right to == to left and constract a pos-neg opt aexpr.将该部分右移到==左，并构造一个后neg opt aexpr。
							LinkedList<AbstractExpr> listChildren = new LinkedList<AbstractExpr>();
							listChildren.add(((AECompare) aeReturn).maeLeft.cloneSelf());
							listChildren.add(((AECompare) aeReturn).maeRight.cloneSelf());
							LinkedList<CalculateOperator> listOpts = new LinkedList<CalculateOperator>();
							listOpts.add(new CalculateOperator(OPERATORTYPES.OPERATOR_POSSIGN, 1, true));
							listOpts.add(new CalculateOperator(OPERATORTYPES.OPERATOR_SUBTRACT, 2));
							listaeOriginals.add(new AEPosNegOpt(listChildren, listOpts));

							strExprs4Solver += //AbstractExprConverter.convtAExpr2QuotedUrl(listaeInputExprs.get(idx))+
									//+ "\" style=\"text-decoration: none;" + "\">$"
									AbstractExprConverter.convtAExpr2JQMath(listaeInputExprs.get(idx));
							//测试4\text"5"+\text"x"=\text"2"
							//把原式去括号
							//System.out.println("测试4"+strExprs4Solver);
						} else {
							strOutput +=  //AbstractExprConverter.convtAExpr2QuotedUrl(listaeInputExprs.get(idx))+
									//+ "\" style=\"text-decoration: none;" + "\">$"
									'\n' + "答案：\n" +
											AbstractExprConverter.convtAExpr2JQMath(listaeInputExprs.get(idx));
							//System.out.println("测试4`"+strOutput);
							//+ "$</a> <big>&rarr;</big> " + "!</p>";
							bContinue2Solve = false;
							break;
						}
					} catch (Exception e) {
						try {
							strOutput += //AbstractExprConverter.convtAExpr2QuotedUrl(listaeInputExprs.get(idx))+
									//+ "\" style=\"text-decoration: none;" + "\">$"
									AbstractExprConverter.convtAExpr2JQMath(listaeInputExprs.get(idx));
							//+ "$</a> <big>&rarr;</big> " +  "!\n";
							//System.out.println("测试5"+strOutput);
						} catch (Exception e1) {
							strOutput += AbstractExprConverter.convtPlainStr2QuotedUrl(listaeInputExprs.get(idx).toString()) + '\n';
							//+ "\" style=\"text-decoration: none;" + "\">"
							//+ "!\n";
							//System.out.println("测试5"+strOutput);
						}
						bContinue2Solve = false;
						break;
					}
				}

				if (bContinue2Solve) {
					try {
						AbstractExpr[] aeOriginalExprs = new AbstractExpr[listaeOriginals.size()];
						for (int index = 0; index < listaeOriginals.size(); index++) {
							aeOriginalExprs[index] = listaeOriginals.get(index);
						}

						// aeOriginalExprs.length must be non-zero.长度必须非零。
						// first of all, output the expressions.首先，输出表达式。
						//strOutput += strExprs4Solver ; // not want too many middle level output.不想要太多的中层产出。
						//System.out.println("测试5"+strOutput);

						LinkedList<UnknownVariable> listAlreadyPrinted = UnknownVarOperator.cloneUnknownVarList(listVarUnknown);
						AbstractExpr[] aeOriginalExprsOld = aeOriginalExprs;
						// load patterns only if we need it.只有在需要时才加载模式。
						if (SolveAnalyzer.mspm == null) {
							SolveAnalyzer.mspm = new PatternManager();
							try {
								SolveAnalyzer.mspm.loadPatterns(13);
							} catch (Exception e) {
								// TODO Do something if load pattern failed;如果加载模式失败，TODO会做些什么;
							}
						}
						aeOriginalExprs = SolveAnalyzer.mspm.simplifyByPtnSlvVarIdentifier(aeOriginalExprsOld, listVarUnknown, lVarNameSpaces);
						for (int idx = 0; idx < listVarUnknown.size(); idx++) {
							if (listVarUnknown.get(idx).isValueAssigned()
									&& (!listAlreadyPrinted.get(idx).isValueAssigned()
									|| !listAlreadyPrinted.get(idx).getSolvedValue().isEqual(listVarUnknown.get(idx).getSolvedValue()))) {
								// value is changed during Pattern identifying值在模式标识期间更改
								String strVarName = listVarUnknown.get(idx).getName();
								Variable var = VariableOperator.lookUpSpaces(strVarName, lVarNameSpaces);
								if (var != null) {
									var.setValue(listVarUnknown.get(idx).getSolvedValue());
								}
								// need to simplify MFPAdapter.outputDatum(listVarUnknown.get(idx).getSolvedValue())[1] because
								// the outputDatum might be 3 * i, and cannot be initialized as AEConst, so converted to 3 * 1 * i
								String strValueOutput = MFPAdapter.outputDatum(listVarUnknown.get(idx).getSolvedValue())[1];
								strOutput += "\r\n"
										+ "答案：\n"
										+ AbstractExprConverter.convtPlainStr2QuotedUrl(strVarName) + '\n'
										//+ "\" style=\"text-decoration: none;" +"\">$"
										//+"\n"
										+ AbstractExprConverter.convtPlainStr2JQMath(strVarName, false)
										+ " = "
										//+ AbstractExprConverter.convtPlainStr2QuotedUrl(strValueOutput)
										//+ "\" style=\"text-decoration: none;" +  "\">$"
										+ AbstractExprConverter.convtAExpr2JQMath(new AEConst(listVarUnknown.get(idx).getSolvedValue()));
								//+ "$</a></p>\n";
								//System.out.println("测试6"+AbstractExprConverter.convtPlainStr2QuotedUrl(strVarName)+"   "+AbstractExprConverter.convtPlainStr2JQMath(strVarName, false));
								//System.out.println("测试6"+strOutput);
							}
						}

						if (aeOriginalExprs.length != 0) {
							// then try to use pattern analyzer然后尝试使用模式分析器
							// first, print simplified expression(s)//首先，打印简化表达式
							String strExprs4PatternAnalyser = "";
							for (int idx = 0; idx < aeOriginalExprs.length; idx++) {
								AECompare aeEqualZero = new AECompare(aeOriginalExprs[idx],
										OPERATORTYPES.OPERATOR_EQ,
										new AEConst(new DataClass(DATATYPES.DATUM_DOUBLE, MFPNumeric.ZERO)));

								strExprs4PatternAnalyser += //AbstractExprConverter.convtAExpr2QuotedUrl(aeEqualZero)+
										//+ "\" style=\"text-decoration: none;" + "\">$"
										"化简：" + AbstractExprConverter.convtAExpr2JQMath(aeEqualZero) + '\n';
								//System.out.println("测试7"+strExprs4PatternAnalyser);
							}
							if (AbstractExpr.isExprsEqual(aeOriginalExprs, aeOriginalExprsOld) == false) {
								strOutput += "\r\n" + "答案：\n" + strExprs4PatternAnalyser;
								//System.out.println("测试7`"+strOutput);
							}
							listAlreadyPrinted = UnknownVarOperator.cloneUnknownVarList(listVarUnknown);
							// only run SolveAnalyzer.solveExprVars once to save time.只有SolveAnalyzer运行。为了节省时间，请一次性清洗。
							LinkedList<LinkedList<UnknownVariable>> listAllResultSets = SolveAnalyzer.solveExprVars(SolveAnalyzer.mspm, aeOriginalExprs, listVarUnknown, lVarNameSpaces);
							if (listAllResultSets.size() > 0) {
								//strOutput += "<pre>";
								//  at this moment only store all the roots in an array. In the future should have more choices.此时，只将所有根存储在一个数组中。未来应该有更多的选择。
								for (int idx = 0; idx < listAllResultSets.get(0).size(); idx++) {
									String strVarName = listAllResultSets.get(0).get(idx).getName();
									UnknownVariable varSolved = UnknownVarOperator.lookUpList(strVarName, listAlreadyPrinted);
									if (varSolved != null && varSolved.isValueAssigned()) {
										continue;    // this value must be printed before.此值必须在之前打印。
									}
									String strOneVarOutput = new String();
									int nLineStartPos = 0;
									strOneVarOutput += //AbstractExprConverter.convtPlainStr2QuotedUrl(strVarName)+
											//+ "\" style=\"text-decoration: none;" + "\">$"
											AbstractExprConverter.convtPlainStr2JQMath(strVarName, false) + " = ";
									//System.out.println("测试8"+strOneVarOutput);

									if (listAllResultSets.size() > 1) {
										strOneVarOutput += "{ ";
									}
									DataClass[] datumList = new DataClass[listAllResultSets.size()];
									int nAssignedValue = 0;
									for (int idx1 = 0; idx1 < listAllResultSets.size(); idx1++) {
										datumList[idx1] = new DataClass();
										if (listAllResultSets.get(idx1).get(idx).isValueAssigned()) {
											datumList[idx1] = listAllResultSets.get(idx1).get(idx).getSolvedValue();
											nAssignedValue++;
										}
										String strOneRoot = MFPAdapter.outputDatum(datumList[idx1])[1];
										// need to simplify most b4 convert plain string to JQMath because like 3*i cannot be initialized to an AEConst
										//需要简化大多数b4转换普通字符串到JQMath，因为像3*i不能初始化为AEConst
										strOneVarOutput += //"\n"+//AbstractExprConverter.convtPlainStr2QuotedUrl(strOneRoot)+
												//+ "\" style=\"text-decoration: none;" + "\">$"
												AbstractExprConverter.convtAExpr2JQMath(new AEConst(datumList[idx1]));
										//System.out.println("测试9"+strOneVarOutput);
										if (idx1 != listAllResultSets.size() - 1) {
											strOneVarOutput += ", ";
											if (strOneVarOutput.length() - nLineStartPos >= 20) {
												// the result is very long so that it takes whole line. next result should be placed in next line.
												//结果很长，用了整整一行。下一个结果应该放在下一行。
												//strOneVarOutput += "\n";
												nLineStartPos = strOneVarOutput.length();
											}
										}
									}
									if (listAllResultSets.size() > 1) {
										strOneVarOutput += " }";
									}
									if (nAssignedValue != 0) {
										strOutput += strOneVarOutput + "\n";
										//System.out.println("测试10"+strOutput);
										DataClass datumValue = new DataClass();
										datumValue.setDataList(datumList);
										Variable var = VariableOperator.lookUpSpaces(listAllResultSets.get(0).get(idx).getName(), lVarNameSpaces);
										// var should exist in lVarNameSpaces  var应该存在于lVarNameSpaces中
										var.setValue(datumValue);
									}
								}
								//strOutput += "</pre>";
								//System.out.println("测试10`"+strOutput);
							}
						}
					} catch (Exception e) {
						if (e instanceof JSmartMathErrException
								&& ((JSmartMathErrException) e).m_se.m_enumErrorType
								== SMErrProcessor.ERRORTYPES.ERROR_UNRECOGNIZED_PATTERN) {
							//strOutput += "<p>" + "!</p>";

						} else {
							//strOutput += "<p>" + "!</p>";	// some other exception may not be invalid.其他一些异常可能不无效。
						}
					}
				}
			}
			return strOutput;
		}
		catch (Exception e){
			return "error";
		}
	}

}
