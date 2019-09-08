/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyzapps.mathexprgen;

import com.cyzapps.mathrecog.ConstantsMgr;
import com.cyzapps.mathrecog.MisrecogWordMgr;
import com.cyzapps.mathrecog.StructExprRecog;
import com.cyzapps.mathrecog.UnitPrototypeMgr.UnitProtoType;
import java.util.LinkedList;

/**
 *转化为可计算的数学表达式
 * @author tonyc
 */
public class SerMFPTranslator   {
    public static class CurPos  {
        public int mnPos = 0;
        public CurPos(int nPos) {
            mnPos = nPos;
        }
    }
    
    public static class SerMFPTransFlags {
        public boolean mbConvertAssign2Eq = false;
    }
    
    public static String cvtUnitPrototype2MFPStr(UnitProtoType.Type typeUpt, SerMFPTransFlags smtFlag)   {
        String str = "";
        switch (typeUpt)  {
        case TYPE_EMPTY:
            str = " ";
            break;
        case TYPE_ZERO:
            str = "0";
            break;
        case TYPE_ONE:
            str = "1";
            break;
        case TYPE_TWO:
            str = "2";
            break;
        case TYPE_THREE:
            str = "3";
            break;
        case TYPE_FOUR:
            str = "4";
            break;
        case TYPE_FIVE:
            str = "5";
            break;
        case TYPE_SIX:
            str = "6";
            break;
        case TYPE_SEVEN:
            str = "7";
            break;
        case TYPE_EIGHT:
            str = "8";
            break;
        case TYPE_NINE:
            str = "9";
            break;
        case TYPE_INFINITE:
            str = "inf";
            break;
        case TYPE_SMALL_A:
            str = "a";
            break;
        case TYPE_SMALL_B:
            str = "b";
            break;
        case TYPE_SMALL_C:
            str = "c";
            break;
        case TYPE_SMALL_D:
            str = "d";
            break;
        case TYPE_SMALL_E:
            str = "e";
            break;
        case TYPE_SMALL_F:
            str = "f";
            break;
        case TYPE_SMALL_G:
            str = "g";
            break;
        case TYPE_SMALL_H:
            str = "h";
            break;
        case TYPE_SMALL_I:
            str = "i";
            break;
        case TYPE_SMALL_I_WITHOUT_DOT:
            str = "l";  // small i without dot looks like a l
            break;
        case TYPE_SMALL_J:
            str = "j";
            break;
        case TYPE_SMALL_J_WITHOUT_DOT:  // small j without dot looks like a big J
            str = "J";
            break;
        case TYPE_SMALL_K:
            str = "k";
            break;
        case TYPE_SMALL_L:
            str = "l";
            break;
        case TYPE_SMALL_M:
            str = "m";
            break;
        case TYPE_SMALL_N:
            str = "n";
            break;
        case TYPE_SMALL_O:
            str = "o";
            break;
        case TYPE_SMALL_P:
            str = "p";
            break;
        case TYPE_SMALL_Q:
            str = "q";
            break;
        case TYPE_SMALL_R:
            str = "r";
            break;
        case TYPE_SMALL_S:
            str = "s";
            break;
        case TYPE_SMALL_T:
            str = "t";
            break;
        case TYPE_SMALL_U:
            str = "u";
            break;
        case TYPE_SMALL_V:
            str = "v";
            break;
        case TYPE_SMALL_W:
            str = "w";
            break;
        case TYPE_SMALL_X:
            str = "x";
            break;
        case TYPE_SMALL_Y:
            str = "y";
            break;
        case TYPE_SMALL_Z:
            str = "z";
            break;
        case TYPE_BIG_A:
            str = "A";
            break;
        case TYPE_BIG_B:
            str = "B";
            break;
        case TYPE_BIG_C:
            str = "C";
            break;
        case TYPE_BIG_D:
            str = "D";
            break;
        case TYPE_BIG_E:
            str = "E";
            break;
        case TYPE_BIG_F:
            str = "F";
            break;
        case TYPE_BIG_G:
            str = "G";
            break;
        case TYPE_BIG_H:
            str = "H";
            break;
        case TYPE_BIG_I:
            str = "I";
            break;
        case TYPE_BIG_J:
            str = "J";
            break;
        case TYPE_BIG_K:
            str = "K";
            break;
        case TYPE_BIG_L:
            str = "L";
            break;
        case TYPE_BIG_M:
            str = "M";
            break;
        case TYPE_BIG_N:
            str = "N";
            break;
        case TYPE_BIG_O:
            str = "O";
            break;
        case TYPE_BIG_P:
            str = "P";
            break;
        case TYPE_BIG_Q:
            str = "Q";
            break;
        case TYPE_BIG_R:
            str = "R";
            break;
        case TYPE_BIG_S:
            str = "S";
            break;
        case TYPE_BIG_T:
            str = "T";
            break;
        case TYPE_BIG_U:
            str = "U";
            break;
        case TYPE_BIG_V:
            str = "V";
            break;
        case TYPE_BIG_W:
            str = "W";
            break;
        case TYPE_BIG_X:
            str = "X";
            break;
        case TYPE_BIG_Y:
            str = "Y";
            break;
        case TYPE_BIG_Z:
            str = "Z";
            break;
        case TYPE_SMALL_ALPHA:
            str = "\u03b1";
            break;
        case TYPE_SMALL_BETA:
            str = "\u03b2";
            break;
        case TYPE_SMALL_GAMMA:
            str = "\u03b3";
            break;
        case TYPE_SMALL_DELTA:
            str = "\u03b4";
            break;
        case TYPE_SMALL_EPSILON:
            str = "\u03b5";
            break;
        case TYPE_SMALL_ZETA:
            str = "\u03b6";
            break;
        case TYPE_SMALL_ETA:
            str = "\u03b7";
            break;
        case TYPE_SMALL_THETA:
            str = "\u03b8";
            break;
        case TYPE_SMALL_LAMBDA:
            str = "\u03bb";
            break;
        case TYPE_SMALL_MU:
            str = "\u03bc";
            break;
        case TYPE_SMALL_XI:
            str = "\u03be";
            break;
        case TYPE_SMALL_PI:
            str = "pi";
            break;
        case TYPE_SMALL_RHO:
            str = "\u03c1";
            break;
        case TYPE_SMALL_SIGMA:
            str = "\u03c3";
            break;
        case TYPE_SMALL_TAU:
            str = "\u03c4";
            break;
        case TYPE_SMALL_PHI:
            str = "\u03c6";
            break;
        case TYPE_SMALL_PSI:
            str = "\u03c8";
            break;
        case TYPE_SMALL_OMEGA:
            str = "\u03c9";
            break;
        case TYPE_BIG_DELTA:
            str = "\u0394";
            break;
        case TYPE_BIG_THETA:
            str = "\u0398";
            break;
        case TYPE_BIG_PI:
            str = "\u03a0";
            break;
        case TYPE_BIG_SIGMA:
            str = "\u03a3";
            break;
        case TYPE_BIG_PHI:
            str = "\u03a6";
            break;
        case TYPE_BIG_OMEGA:
            str = "\u03a9";
            break;
        case TYPE_INTEGRATE:
            str = "integrate";
            break;
        case TYPE_INTEGRATE_CIRCLE:
            str = "integrate";
            break;
        case TYPE_SQRT_LEFT:
            str = "sqrt";
            break;
        case TYPE_SQRT_SHORT:
            str = "sqrt";
            break;
        case TYPE_SQRT_MEDIUM:
            str = "sqrt";
            break;
        case TYPE_SQRT_LONG:
            str = "sqrt";
            break;
        case TYPE_SQRT_TALL:
            str = "sqrt";
            break;
        case TYPE_SQRT_VERY_TALL:
            str = "sqrt";
            break;
        case TYPE_ADD:
            str = "+";
            break;
        case TYPE_SUBTRACT:
            str = "-";
            break;
        case TYPE_PLUS_MINUS:
            str = "+/-";
            break;
        case TYPE_DOT_MULTIPLY:
            str = "*";
            break;
        case TYPE_MULTIPLY:
            str = "*";
            break;
        case TYPE_DIVIDE:
            str = "/";
            break;
        case TYPE_FORWARD_SLASH:
            str = "/";
            break;
        case TYPE_BACKWARD_SLASH:
            str = "\\";
            break;
        case TYPE_EQUAL:
            if (smtFlag.mbConvertAssign2Eq) {
                str = "==";
            } else  {
                str = "=";
            }
            break;
        case TYPE_EQUAL_ALWAYS:
            str = "==";
            break;
        case TYPE_EQUAL_ROUGHLY:
            str = "=~";
            break;
        case TYPE_LARGER:
            str = ">";
            break;
        case TYPE_SMALLER:
            str = "<";
            break;
        case TYPE_NO_LARGER:
            str = "<=";
            break;
        case TYPE_NO_SMALLER:
            str = ">=";
            break;
        case TYPE_PERCENT:
            str = "%";
            break;
        case TYPE_EXCLAIMATION:
            str = "!";
            break;
        case TYPE_DOT:
            str = ".";   //"\\bullet";
            break;
        case TYPE_STAR:
            str = "*";
            break;
        case TYPE_ROUND_BRACKET:
            str = "(";
            break;
        case TYPE_CLOSE_ROUND_BRACKET:
            str = ")";
            break;
        case TYPE_SQUARE_BRACKET:
            str = "[";
            break;
        case TYPE_CLOSE_SQUARE_BRACKET:
            str = "]";
            break;
        case TYPE_BRACE:
            str = "{";
            break;
        case TYPE_CLOSE_BRACE:
            str = "}";
            break;
        case TYPE_VERTICAL_LINE:
            str = "|";
            break;
        case TYPE_WAVE:
            str = "~";
            break;
        case TYPE_LEFT_ARROW:
            str = "<-";
            break;
        case TYPE_RIGHT_ARROW:
            str = "->";
            break;
        case TYPE_DOLLAR:
            str = "$";
            break;
        case TYPE_EURO:
            str = "\\euro";
            break;
        case TYPE_YUAN:
            str = "\\yuan";
            break;
        case TYPE_POUND:
            str = "\\pound";
            break;
        case TYPE_CELCIUS:
            str = "\\celcius";
            break;
        case TYPE_FAHRENHEIT:
            str = "\\fahrenheit";
            break;
        case TYPE_WORD_SIN:
            str = "sin";
            break;
        case TYPE_WORD_COS:
            str = "cos";
            break;
        case TYPE_WORD_TAN:
            str = "tan";
            break;
        case TYPE_WORD_LIM:
            str = "lim";
            break;
        case TYPE_WORD_LOG:
            str = "log";
            break;
        default:
            str = "?";
        }
        return str;
    }
    
    public static String cvtTopBaseBottom2MFPExpr(StructExprRecog serBase, StructExprRecog serLowerNote, StructExprRecog serUpperNote, StructExprRecog serThis,
            StructExprRecog serParentOfThis, CurPos curPos, MisrecogWordMgr mwm)  {    // for integrate, sum over and product over only.
        String strReturn = "";
        int nCurrentPos = curPos.mnPos;

        boolean bIsLUNote = (serThis.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTLOWERNOTE
                || serThis.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE
                || serThis.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTLUNOTES);
        int nBaseIdx = 0;
        if (serThis.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_HCUTCAPUNDER
                || serThis.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_HCUTCAP)    {
            nBaseIdx = 1;
        }
        int nFromIdx = 1;
        if (serThis.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_HCUTCAPUNDER)  {
            nFromIdx = 2;
        }
        String strFrom = (serLowerNote == null)?"":cvtSer2MFPExpr(serLowerNote, serThis, new CurPos(nFromIdx), mwm, new SerMFPTransFlags());
        int nToIdx = 0;
        if (serThis.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE)  {
            nToIdx = 1;
        } else if (serThis.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTLUNOTES)    {
            nToIdx = 2;
        }
        String strTo = (serUpperNote == null)?"":cvtSer2MFPExpr(serUpperNote, serThis, new CurPos(nToIdx), mwm, new SerMFPTransFlags());
        
        boolean bIsFunctionFExpr = false;
        boolean bIsFunctionFLowerNote = false;
        boolean bIsLim = false;
        if (serBase.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE && serBase.getUnitType() == UnitProtoType.Type.TYPE_SMALL_F
                && (serThis.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTLOWERNOTE
                    || serThis.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE
                    || serThis.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTLUNOTES)) {
            StructExprRecog serNext = ((serParentOfThis == null) || (nCurrentPos == serParentOfThis.getChildrenList().size() - 1))
                    ?null:serParentOfThis.getChildrenList().get(nCurrentPos + 1);
            if (serNext != null && serNext.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    && serNext.getUnitType() == UnitProtoType.Type.TYPE_ROUND_BRACKET) {
                bIsFunctionFExpr = true;
            } else  if (serLowerNote != null && serLowerNote.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VBLANKCUT
                    && serLowerNote.getChildrenList().getFirst().getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    && serLowerNote.getChildrenList().getFirst().getUnitType() == UnitProtoType.Type.TYPE_ROUND_BRACKET
                    && serLowerNote.getChildrenList().getLast().getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    && serLowerNote.getChildrenList().getLast().getUnitType() == UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET)   {
                bIsFunctionFLowerNote = true;
            }
        } else if (((serThis.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_HBLANKCUT && serThis.getChildrenList().size() == 2)
                    || serThis.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_HCUTUNDER)
                && serUpperNote == null && serLowerNote != null && serLowerNote.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VBLANKCUT
                && serParentOfThis != null && serParentOfThis.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VBLANKCUT
                && serParentOfThis.getChildrenList().size() > nCurrentPos + 1 // lim is not the last char.
                && serBase.toString().equals("lim")) {
            bIsLim = true;
        }
        
        if (bIsFunctionFExpr)   {
            // this is like f(x)
            String strWord = "f" + ((serLowerNote == null)?"":("_" + strFrom));
            int nBracketLvl = 0;
            LinkedList<StructExprRecog> listserFunExprChildren = new LinkedList<StructExprRecog>();
            int idx3 = nCurrentPos + 1;
            for (; idx3 < serParentOfThis.getChildrenList().size(); idx3 ++)    {
                StructExprRecog serThisChild = serParentOfThis.getChildrenList().get(idx3);
                listserFunExprChildren.add(serThisChild);
                StructExprRecog serPrinciple = serThisChild.getPrincipleSER(4);
                if (serThisChild.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                        && serThisChild.getUnitType() == UnitProtoType.Type.TYPE_ROUND_BRACKET)   {
                    nBracketLvl ++;
                }else if (serPrinciple.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                        && serPrinciple.getUnitType() == UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET)   {
                    nBracketLvl --;
                }
                if (nBracketLvl == 0)   {
                    break;
                }
            }
            if (idx3 == serThis.getChildrenList().size())    {
                idx3 = serThis.getChildrenList().size() - 1;
            }
            
            if (listserFunExprChildren.size() == 1
                    || (listserFunExprChildren.size() == 2
                        && listserFunExprChildren.getLast().getPrincipleSER(4).getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                        && listserFunExprChildren.getLast().getPrincipleSER(4).getUnitType() == UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET)) {   // no parameter
                if (strTo.length() > 0)  {
                    strReturn += strWord + "(" + cvtSer2MFPExpr(listserFunExprChildren.getLast(), null, new CurPos(0), mwm, new SerMFPTransFlags()) + "**(" + strTo + ")";
                } else  {                            
                    strReturn += strWord + "(" + cvtSer2MFPExpr(listserFunExprChildren.getLast(), null, new CurPos(0), mwm, new SerMFPTransFlags());
                }
            } else  {
                StructExprRecog serFuncParam = new StructExprRecog(serThis.getBiArray());
                serFuncParam.setStructExprRecog(listserFunExprChildren, StructExprRecog.EXPRRECOGTYPE_VBLANKCUT);
                String strFuncParam = cvtSer2MFPExpr(serFuncParam, null, new CurPos(0), mwm, new SerMFPTransFlags());
                if (strTo.length() > 0)  {
                    strReturn += strWord + strFuncParam + "**(" + strTo + ")";
                } else  {
                    strReturn += strWord + strFuncParam;
                }
            }
            curPos.mnPos = idx3 + 1;            
        } else if (bIsFunctionFLowerNote)  {
            LinkedList<StructExprRecog> listserFunExprChildren = new LinkedList<StructExprRecog>();
            for (int idx = 1; idx < serLowerNote.getChildrenList().size() - 1; idx ++)  {
                listserFunExprChildren.add(serLowerNote.getChildrenList().get(idx));
            }
            if (listserFunExprChildren.size() == 0) {
                if (strTo.length() > 0) {
                    strReturn += "f()" + "**(" + strTo + ")";
                } else  {
                    strReturn += "f()";
                }
            } else if (listserFunExprChildren.size() == 1)  {
                if (strTo.length() > 0)  {
                    strReturn += "f(" + cvtSer2MFPExpr(listserFunExprChildren.getFirst(), null, new CurPos(0), mwm, new SerMFPTransFlags()) + "**(" + strTo + ")";
                } else  {                            
                    strReturn += "f(" + cvtSer2MFPExpr(listserFunExprChildren.getFirst(), null, new CurPos(0), mwm, new SerMFPTransFlags());
                }
            } else  {
                StructExprRecog serFuncParam = new StructExprRecog(serThis.getBiArray());
                serFuncParam.setStructExprRecog(listserFunExprChildren, StructExprRecog.EXPRRECOGTYPE_VBLANKCUT);
                String strFuncParam = cvtSer2MFPExpr(serFuncParam, null, new CurPos(0), mwm, new SerMFPTransFlags());
                if (strTo.length() > 0)  {
                    strReturn += "f(" + strFuncParam + ")**(" + strTo + ")";
                } else  {
                    strReturn += "f(" + strFuncParam + ")";
                }
            }
        } else if (bIsLim)  {
            // first, find ->
            int idx = 0;
            for (; idx < serLowerNote.getChildrenList().size(); idx ++)  {
                if (serLowerNote.getChildrenList().get(idx).getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                        && serLowerNote.getChildrenList().get(idx).getUnitType() == UnitProtoType.Type.TYPE_RIGHT_ARROW)    {
                    // ok, get it
                    break;
                }
            }
            // if we cannot find right arrow, doesn't matter, the following code assumes the first letter is variable name
            // and the following letter is variable destination.
            StructExprRecog serVariableName = serLowerNote.getChildrenList().getFirst();
            StructExprRecog serVariableDest = serLowerNote.getChildrenList().getLast();
            LinkedList<StructExprRecog> listLeft = new LinkedList<StructExprRecog>();
            for (int idx1 = 0; idx1 < idx; idx1 ++) {
                listLeft.add(serLowerNote.getChildrenList().get(idx1));
            }
            if (listLeft.size() > 1)    {
                serVariableName = new StructExprRecog(serVariableName.getBiArray());    // this line cannot be ommitted because listLeft includes serVariableName
                                                                                        // so if we setStructExprRecog use serVariableName itself, we will corrupt it.
                serVariableName.setStructExprRecog(listLeft, StructExprRecog.EXPRRECOGTYPE_VBLANKCUT);
            }
            String strVarName = cvtSer2MFPExpr(serVariableName, null, new CurPos(0), mwm, new SerMFPTransFlags());  //need not to worry about its parent.
            strVarName = addEscapes(strVarName);
            LinkedList<StructExprRecog> listRight = new LinkedList<StructExprRecog>();
            for (int idx1 = idx + 1; idx1 < serLowerNote.getChildrenList().size(); idx1 ++) {
                listRight.add(serLowerNote.getChildrenList().get(idx1));
            }
            if (listRight.size() > 1)   {
                serVariableDest = new StructExprRecog(serVariableDest.getBiArray());    // this line cannot be ommitted because listLeft includes serVariableDest
                                                                                        // so if we setStructExprRecog use serVariableDest itself, we will corrupt it.
                serVariableDest.setStructExprRecog(listRight, StructExprRecog.EXPRRECOGTYPE_VBLANKCUT);
            }
            String strVariableDest = cvtSer2MFPExpr(serVariableDest, null, new CurPos(0), mwm, new SerMFPTransFlags());  //need not to worry about its parent.
            strVariableDest = addEscapes(strVariableDest);
            
            // in the above when we get we are handling lim function, nCurrentPos has been validated.
            int nOptExprEndP1 = findEndP1OfOpratedExpr(serParentOfThis.getChildrenList(), nCurrentPos);
            StructExprRecog serOperated = new StructExprRecog(serParentOfThis.getBiArray());
            LinkedList<StructExprRecog> listOperateds = new LinkedList<StructExprRecog>();
            for (idx = nCurrentPos + 1; idx < nOptExprEndP1; idx ++)  {
                listOperateds.add(serParentOfThis.getChildrenList().get(idx));
            }
            if (listOperateds.size() > 1)   {
                serOperated.setStructExprRecog(listOperateds, StructExprRecog.EXPRRECOGTYPE_VBLANKCUT);
            } else if (listOperateds.size() == 1) {
                serOperated = listOperateds.getFirst();
            } else {
                serOperated = null;
            }
            String strOperated = (serOperated == null)?"":cvtSer2MFPExpr(serOperated, null, new CurPos(0), mwm, new SerMFPTransFlags());
            strOperated = addEscapes(strOperated);
            strReturn = "lim(\"" + strOperated + "\",\"" + strVarName + "\",\"" + strVariableDest + "\")";
            curPos.mnPos = nOptExprEndP1;
        } else if (serParentOfThis == null || serParentOfThis.getExprRecogType() != StructExprRecog.EXPRRECOGTYPE_VBLANKCUT
                || nCurrentPos == serParentOfThis.getChildrenList().size() - 1
                || (!serBase.isIntegTypeChar() && !serBase.isSIGMAPITypeChar()))    {
            // it is processed as a normal lu notes char  or cap under char, note that if a cap under char, do not show cap and under.
            if ((serUpperNote == null && serLowerNote == null) || !bIsLUNote)  {
                strReturn = cvtSer2MFPExpr(serBase, serThis, new CurPos(nBaseIdx), mwm, new SerMFPTransFlags());
            } else if (serLowerNote != null && serUpperNote == null)    {
                strReturn = cvtSer2MFPExpr(serBase, serThis, new CurPos(nBaseIdx), mwm, new SerMFPTransFlags()) + "_" + strFrom;
            } else if (serUpperNote != null && serLowerNote == null)    {
                strReturn = cvtSer2MFPExpr(serBase, serThis, new CurPos(nBaseIdx), mwm, new SerMFPTransFlags()) + "**" + "(" + strTo + ")";
            } else   {
                strReturn = cvtSer2MFPExpr(serBase, serThis, new CurPos(nBaseIdx), mwm, new SerMFPTransFlags()) + "_" + strFrom + "**" + "(" + strTo +")";
            }
            curPos.mnPos ++;
        } else  {   // it is integrate, or sum over or product over.
            int nOptExprEndP1 = findEndP1OfOpratedExpr(serParentOfThis.getChildrenList(), nCurrentPos);
            String strOptExpr = "";
            if (nOptExprEndP1 == nCurrentPos + 2) {
                // use null for serParent here because the integrated expr is not related to any other sers.
                strOptExpr = cvtSer2MFPExpr(serParentOfThis.getChildrenList().get(nCurrentPos + 1), null, new CurPos(0), mwm, new SerMFPTransFlags());
                strOptExpr = addEscapes(strOptExpr);                        
            } else  {
                StructExprRecog serOperated = new StructExprRecog(serParentOfThis.getBiArray());
                LinkedList<StructExprRecog> listOperateds = new LinkedList<StructExprRecog>();
                for (int idx = nCurrentPos + 1; idx < nOptExprEndP1; idx ++)  {
                    listOperateds.add(serParentOfThis.getChildrenList().get(idx));
                }
                if (listOperateds.size() > 1)   {
                    serOperated.setStructExprRecog(listOperateds, StructExprRecog.EXPRRECOGTYPE_VBLANKCUT);
                } else if (listOperateds.size() == 1) {
                    serOperated = listOperateds.getFirst();
                }
                if (listOperateds.size() > 0) {
                    strOptExpr = cvtSer2MFPExpr(serOperated, null, new CurPos(0), mwm, new SerMFPTransFlags());
                    strOptExpr = addEscapes(strOptExpr);  
                }   // listOperateds.size() could be 0 because of some misrecogs.
            }
            strFrom = addEscapes(strFrom);
            strTo = addEscapes(strTo);

            String strVariable = "";
            if (serBase.isIntegTypeChar())  {
                if (nOptExprEndP1 < serParentOfThis.getChildrenList().size()
                        && !serParentOfThis.getChildrenList().get(nOptExprEndP1).isCompareOptChar() )   {
                    int idx = nOptExprEndP1 + 1;
                    for (; idx < serParentOfThis.getChildrenList().size(); idx ++)   {
                        StructExprRecog serThisChar = serParentOfThis.getChildrenList().get(idx);
                        StructExprRecog serNextChar = (idx == serParentOfThis.getChildrenList().size() - 1)?null
                                :serParentOfThis.getChildrenList().get(idx + 1);
                        if (!serThisChar.getPrincipleSER(5).isLetterChar())    {
                            break;
                        } else if (serThisChar.getUnitType() == UnitProtoType.Type.TYPE_SMALL_D && serNextChar != null
                                && serNextChar.isLetterChar())  {
                            break;
                        } else  {
                            strVariable += cvtSer2MFPExpr(serThisChar, null, new CurPos(0), mwm, new SerMFPTransFlags());
                        }
                    }
                    if (strFrom.length() == 0 && strTo.length() == 0) {	// indefinite integrate
                    	strReturn = "integrate(\"" + strOptExpr + "\",\"" + strVariable + "\")";
                    } else {
                    	strReturn = "integrate(\"" + strOptExpr + "\",\"" + strVariable + "\",\"" + strFrom + "\",\"" + strTo + "\")";
                    }
                    curPos.mnPos = idx;
                } else {    //nOptExprEndP1 >= serParentOfThis.getChildrenList().size()
                    StructExprRecog serOptExpr1 = (nCurrentPos <= serParentOfThis.getChildrenList().size() - 2)?serParentOfThis.getChildrenList().get(nCurrentPos + 1):null;
                    StructExprRecog serOptExpr2 = (nCurrentPos <= serParentOfThis.getChildrenList().size() - 3)?serParentOfThis.getChildrenList().get(nCurrentPos + 2):null;
                    StructExprRecog serOptExpr = null;
                    boolean bIsIntegrate = true;
                    boolean bHasSubtract = false;
                    int nNextPos = nCurrentPos +1;
                    if (nCurrentPos <= serParentOfThis.getChildrenList().size() - 2
                            && serOptExpr1.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_HLINECUT
                            && serOptExpr1.getChildrenList().getFirst().getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VBLANKCUT
                            && serOptExpr1.getChildrenList().getFirst().getChildrenList().size() == 2
                            && serOptExpr1.getChildrenList().getFirst().getChildrenList().getFirst().getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                            && serOptExpr1.getChildrenList().getFirst().getChildrenList().getFirst().getUnitType() == UnitProtoType.Type.TYPE_SMALL_D) {
                        // special case integrate (dx/...)
                        StructExprRecog serThisChar = serOptExpr1.getChildrenList().getFirst().getChildrenList().getLast();
                        if (!serThisChar.getPrincipleSER(5).isLetterChar())    {
                            bIsIntegrate = false;
                        } else  {
                            serOptExpr = serOptExpr1;
                            strVariable += cvtSer2MFPExpr(serThisChar, null, new CurPos(0), mwm, new SerMFPTransFlags());
                            nNextPos ++;
                        }
                    } else if (nCurrentPos <= serParentOfThis.getChildrenList().size() - 3
                            && serOptExpr1.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                            && (serOptExpr1.getUnitType() == UnitProtoType.Type.TYPE_SUBTRACT
                                || serOptExpr1.getUnitType() == UnitProtoType.Type.TYPE_ADD)
                            && serOptExpr2.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_HLINECUT
                            && serOptExpr2.getChildrenList().getFirst().getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VBLANKCUT
                            && serOptExpr2.getChildrenList().getFirst().getChildrenList().size() == 2
                            && serOptExpr2.getChildrenList().getFirst().getChildrenList().getFirst().getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                            && serOptExpr2.getChildrenList().getFirst().getChildrenList().getFirst().getUnitType() == UnitProtoType.Type.TYPE_SMALL_D) {
                        // special case integrate -(dx/...)
                        StructExprRecog serThisChar = serOptExpr2.getChildrenList().getFirst().getChildrenList().getLast();
                        if (!serThisChar.getPrincipleSER(5).isLetterChar())    {
                            bIsIntegrate = false;
                        } else  {
                            if (serOptExpr1.getUnitType() == UnitProtoType.Type.TYPE_SUBTRACT) {
                                bHasSubtract = true;
                            }
                            serOptExpr = serOptExpr2;
                            strVariable += cvtSer2MFPExpr(serThisChar, null, new CurPos(0), mwm, new SerMFPTransFlags());
                            nNextPos += 2;
                        }
                    } else {
                        bIsIntegrate = false;
                    }
                    
                    if (bIsIntegrate) {
                        strOptExpr = cvtSer2MFPExpr(serOptExpr.getChildrenList().getLast(), null, new CurPos(0), mwm, new SerMFPTransFlags());
                        strOptExpr = "1/(" + addEscapes(strOptExpr) + ")";  
                        if (bHasSubtract) {
                            strOptExpr = "-" + strOptExpr;
                        }
                        if (strFrom.length() == 0 && strTo.length() == 0) {	// indefinite integrate
                            strReturn = "integrate(\"" + strOptExpr + "\",\"" + strVariable + "\")";
                        } else {
                            strReturn = "integrate(\"" + strOptExpr + "\",\"" + strVariable + "\",\"" + strFrom + "\",\"" + strTo + "\")";
                        }
                    } else {
                        // this seems not a valid integrate.
                        if ((serUpperNote == null && serLowerNote == null) || !bIsLUNote)  {
                            strReturn = cvtUnitPrototype2MFPStr(serBase.getUnitType(), new SerMFPTransFlags());
                        } else if (serLowerNote != null && serUpperNote == null)    {
                            strReturn = cvtUnitPrototype2MFPStr(serBase.getUnitType(), new SerMFPTransFlags()) + "_" + strFrom;
                        } else if (serUpperNote != null && serLowerNote == null)    {
                            strReturn = cvtUnitPrototype2MFPStr(serBase.getUnitType(), new SerMFPTransFlags()) + "**" + strTo;
                        } else   {
                            strReturn = cvtUnitPrototype2MFPStr(serBase.getUnitType(), new SerMFPTransFlags()) + "_" + strFrom + "**" + strTo;
                        }
                    }
                    curPos.mnPos = nNextPos;
                }
            } else {    // if (serBase.isSIGMAPITypeChar())
                String strFunctionName = "sum_over";
                if (serBase.getUnitType() == UnitProtoType.Type.TYPE_BIG_PI)    {
                    strFunctionName = "product_over";
                }
                strReturn = strFunctionName + "(\"" + strOptExpr + "\",\"" + strFrom + "\",\"" + strTo + "\")";
                curPos.mnPos = nOptExprEndP1;
            }
        }
        return strReturn;
    }

    public static String cvtIntFraction2MFPExpr(StructExprRecog serParent, CurPos curPos)    {
        // assume ser is a integer number != 0 and it follows some numbers and finally is a fraction
        if (serParent.getExprRecogType() != StructExprRecog.EXPRRECOGTYPE_VBLANKCUT || curPos.mnPos < 0 || curPos.mnPos > serParent.getChildrenList().size() - 2) {
            // this cannot be integer followed by a fraction
            return null;
        } else if (!serParent.getChildrenList().get(curPos.mnPos).isNumberChar()
                || serParent.getChildrenList().get(curPos.mnPos).getUnitType() == UnitProtoType.Type.TYPE_ZERO) {
            // integer followed by a fraction cannot starts from 0 or any other non-number char
            return null;
        }
        int nOriginalPos = curPos.mnPos;
        String strReturn = "(";
        while (curPos.mnPos < serParent.getChildrenList().size()) {
            if (!serParent.getChildrenList().get(curPos.mnPos).isNumberChar()) {
                break;
            } else {
                strReturn += serParent.getChildrenList().get(curPos.mnPos).toString();
            }
            curPos.mnPos ++;
        }
        if (curPos.mnPos == serParent.getChildrenList().size()) {
            // unfortunately, we arrive in the end but cannot find a fraction.
            curPos.mnPos = nOriginalPos;
            return null;
        } else if (serParent.getChildrenList().get(curPos.mnPos).getExprRecogType() != StructExprRecog.EXPRRECOGTYPE_HLINECUT) {
            // it is not a divisor
            curPos.mnPos = nOriginalPos;
            return null;
        } else {
            StructExprRecog serNumerator = serParent.getChildrenList().get(curPos.mnPos).getChildrenList().getFirst();
            StructExprRecog serDenominator = serParent.getChildrenList().get(curPos.mnPos).getChildrenList().getLast();
            if ((serNumerator.getExprRecogType() != StructExprRecog.EXPRRECOGTYPE_VBLANKCUT && !serNumerator.isNumberChar())
                    || (serDenominator.getExprRecogType() != StructExprRecog.EXPRRECOGTYPE_VBLANKCUT && !serDenominator.isNumberChar())) {
                curPos.mnPos = nOriginalPos;
                return null;
            } else {
                if (serNumerator.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VBLANKCUT) {
                    for (int idx = 0; idx < serNumerator.getChildrenList().size(); idx ++) {
                        if (!serNumerator.getChildrenList().get(idx).isNumberChar()) {
                            curPos.mnPos = nOriginalPos;
                            return null;
                        }
                    }
                }
                if (serDenominator.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VBLANKCUT) {
                    for (int idx = 0; idx < serDenominator.getChildrenList().size(); idx ++) {
                        if (!serDenominator.getChildrenList().get(idx).isNumberChar()) {
                            curPos.mnPos = nOriginalPos;
                            return null;
                        }
                    }
                }
                // ok, now everything is fine.
                strReturn += "+" + serNumerator.toString() + "/" + serDenominator.toString() + ")";
                curPos.mnPos ++;
                return strReturn;
            }
        }
    }
    
    public static String cvtVBlank2MFPExpr(StructExprRecog ser, StructExprRecog serParent, CurPos curPos, MisrecogWordMgr mwm, SerMFPTransFlags smtFlag) {
        String strReturn = "";

        CurPos curPosThis = new CurPos(0);
        LinkedList<StructExprRecog> listserShownChildren = new LinkedList<StructExprRecog>();
        LinkedList<String> liststrChildren = new LinkedList<String>();
        while (curPosThis.mnPos < ser.getChildrenList().size())   {
            if (curPosThis.mnPos == 0 && ser.getChildrenList().size() == 2
                    && ser.getChildrenList().get(0).getPrincipleSER(5).getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    && ser.getChildrenList().get(0).getPrincipleSER(5).getUnitType() == UnitProtoType.Type.TYPE_BRACE
                    && ser.getChildrenList().get(1).getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_MULTIEXPRS) {
                // it is like { followed by multi expr. use getPrincipleSER(5) to prevent some noise
                curPosThis.mnPos ++;
                continue;   // the first { is ignored.
            } else    {
                StructExprRecog serThisChild = ser.getChildrenList().get(curPosThis.mnPos);
                listserShownChildren.add(serThisChild);
                String strThisChild = "";
                if (serThisChild.isIntegTypeChar()) {
                    strThisChild = cvtTopBaseBottom2MFPExpr(serThisChild, null, null, serThisChild, ser, curPosThis, mwm);  // integrate treatment.
                } else  {
                    StructExprRecog serLastChild = null;
                    if (curPosThis.mnPos > 0) {
                        serLastChild = ser.getChildrenList().get(curPosThis.mnPos - 1);
                    }
                    boolean bIsIntFraction = false;
                    if (serLastChild == null || !serLastChild.isNumericChar()) {
                        String strIntFraction = cvtIntFraction2MFPExpr(ser, curPosThis);
                        if (strIntFraction != null) {
                            bIsIntFraction = true;
                            strThisChild = strIntFraction;
                        }
                    }
                    if (!bIsIntFraction) {
                        SerMFPTransFlags smtFlagThis = new SerMFPTransFlags();
                        smtFlagThis.mbConvertAssign2Eq = smtFlag.mbConvertAssign2Eq;
                        strThisChild = cvtSer2MFPExpr(serThisChild, ser, curPosThis, mwm, smtFlagThis); // ignore cap and under for single chars
                    }
                }
                liststrChildren.add(strThisChild);
            }
        }
        // first, check if there is words or functions.
        int idx = 0;
        while (idx < liststrChildren.size()) {
            int nBeginningIdx = idx;
            // identify if it is a word. go through all the words.
            int idx1 = 0;
            for (; idx1 < mwm.mslistMisrecogWordSet.size(); idx1 ++)    {
                String strWord = mwm.mslistMisrecogWordSet.get(idx1).mstrShouldBe;
                boolean bIsShouldBeWord = true;
                boolean bIsUpperNotedWord = false;
                String strUpperNote = "";
                int nWordInSERLength = strWord.length();
                if (liststrChildren.get(idx).equals(strWord))  {
                    // ok, seems that we find the word
                    bIsShouldBeWord = true;
                    bIsUpperNotedWord = false;
                    nWordInSERLength = 1;
                } else if (listserShownChildren.get(idx).getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE)    {
                    StructExprRecog serBase = listserShownChildren.get(idx).getChildrenList().getFirst();
                    StructExprRecog serUpperNote = listserShownChildren.get(idx).getChildrenList().getLast();
                    if  (serBase.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE && serBase.toString().equals(strWord)) {
                        // although it is a word, SER type is still an enumtype.
                        bIsShouldBeWord = true;
                        bIsUpperNotedWord = true;
                        strUpperNote = cvtSer2MFPExpr(serUpperNote, listserShownChildren.get(idx), new CurPos(1), mwm, new SerMFPTransFlags());
                        nWordInSERLength = 1;
                    } else  {
                        bIsShouldBeWord = false;
                        bIsUpperNotedWord = false;
                    }
                } else  {
                    if (idx > liststrChildren.size() - strWord.length()) {
                        continue;
                    }
                    // go through each character of a word.
                    for (int idx2 = 0; idx2 < strWord.length(); idx2 ++)    {
                        int idxChild = idx + idx2;
                        if (idx2 < strWord.length() - 1 && listserShownChildren.get(idxChild).getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                                && liststrChildren.get(idxChild).equals(strWord.substring(idx2, idx2 + 1)))  {
                            continue;
                        } else if (idx2 == strWord.length() - 1)    {
                            StructExprRecog serLastPart = listserShownChildren.get(idxChild);
                            if (serLastPart.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE
                                    && serLastPart.getChildrenList().getFirst().getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                                    && cvtUnitPrototype2MFPStr(serLastPart.getChildrenList().getFirst().getUnitType(), new SerMFPTransFlags()).equals(strWord.substring(idx2, idx2 + 1)))  {
                                strUpperNote = cvtSer2MFPExpr(serLastPart.getChildrenList().get(1), serLastPart, new CurPos(1), mwm, new SerMFPTransFlags());
                                bIsUpperNotedWord = true;
                                continue;
                            } else if (serLastPart.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                                    && liststrChildren.get(idxChild).equals(strWord.substring(idx2, idx2 + 1))) {
                                continue;
                            } else  {
                                bIsShouldBeWord = false;
                                break;
                            }
                        } else  {
                            bIsShouldBeWord = false;
                            break;
                        }
                    }
                }
                if (!bIsShouldBeWord)   {
                    continue;
                }
                // ok, now find a word.
                if ((idx + nWordInSERLength) == liststrChildren.size() && mwm.mslistMisrecogWordSet.get(idx1).mnWordType == MisrecogWordMgr.WORD_TYPE_FUNCTION)  {
                    // a function should be followed by something
                    continue;
                }
                StructExprRecog serAfterWord = ((idx + nWordInSERLength) < liststrChildren.size())?listserShownChildren.get(idx + nWordInSERLength):null;
                StructExprRecog serAfterAfterWord = ((idx + nWordInSERLength) < (liststrChildren.size() - 1))?
                        listserShownChildren.get(idx + nWordInSERLength + 1):null;
                if (mwm.mslistMisrecogWordSet.get(idx1).mnWordType == MisrecogWordMgr.WORD_TYPE_FUNCTION)   {
                    if (serAfterWord == null)   {
                        continue;   // a function must be followed by something.
                    } else if (serAfterWord.isNumberChar()
                            || (serAfterWord.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE
                                && serAfterWord.getChildrenList().getFirst().isNumberChar())
                            || (serAfterWord.isPreUnOptChar() && serAfterAfterWord != null
                                && (serAfterAfterWord.isNumberChar()
                                    || (serAfterAfterWord.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE
                                        && serAfterAfterWord.getChildrenList().getFirst().isNumberChar()))))    {
                        if (bIsUpperNotedWord)  {
                            strReturn += "(";
                        }
                        strReturn += strWord + "(";
                        int nNumberOfDot = 0;
                        int idx3 = idx + nWordInSERLength;
                        if (serAfterWord.isPreUnOptChar())  {
                            strReturn += liststrChildren.get(idx3);
                            idx3 ++;
                        }
                        for (; idx3 < liststrChildren.size(); idx3 ++)  {
                            if (listserShownChildren.get(idx3).isNumberChar())  {
                                strReturn += liststrChildren.get(idx3);
                                continue;
                            } else if (listserShownChildren.get(idx3).getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                                    && listserShownChildren.get(idx3).getUnitType() == UnitProtoType.Type.TYPE_DOT)   {
                                if (nNumberOfDot == 1)  {
                                    break;
                                } else  {
                                    strReturn += liststrChildren.get(idx3);
                                    nNumberOfDot ++;
                                    continue;
                                }
                            } else if (listserShownChildren.get(idx3).getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE
                                    && listserShownChildren.get(idx3).getChildrenList().getFirst().isNumberChar())  {
                                // it is something like 3**2
                                strReturn += liststrChildren.get(idx3);
                                idx3 ++;
                                break;
                            } else  {
                                break;
                            }
                        }
                        // ok, get the operated exprs.
                        strReturn += ")";
                        if (bIsUpperNotedWord)  {
                            strReturn += ")**(" + strUpperNote + ")";
                        }
                        // idx3 is the next char to process.
                        idx = idx3;
                        break;
                    } else if (serAfterWord.isLetterChar()
                            || (serAfterWord.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE
                                && serAfterWord.getChildrenList().getFirst().isLetterChar())
                            || (serAfterWord.isPreUnOptChar() && serAfterAfterWord != null
                                && (serAfterAfterWord.isLetterChar()
                                    || (serAfterAfterWord.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE
                                        && serAfterAfterWord.getChildrenList().getFirst().isLetterChar())))) {
                        if (bIsUpperNotedWord)  {
                            strReturn += "(";
                        }
                        strReturn += strWord + "(";
                        int idx3 = idx + nWordInSERLength;
                        if (serAfterWord.isPreUnOptChar())  {
                            strReturn += liststrChildren.get(idx3);
                            idx3 ++;
                        }
                        for (; idx3 < liststrChildren.size(); idx3 ++)  {
                            if (listserShownChildren.get(idx3).isNumberChar() || listserShownChildren.get(idx3).isLetterChar())  {
                                strReturn += liststrChildren.get(idx3);
                                continue;
                            } else if (listserShownChildren.get(idx3).getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE
                                    && listserShownChildren.get(idx3).getChildrenList().getFirst().isNumberChar())  {
                                // it is something like 3**2
                                strReturn += liststrChildren.get(idx3);
                                idx3 ++;
                                break;
                            } else  {
                                break;
                            }
                        }
                        // ok, get the operated exprs.
                        strReturn += ")";
                        if (bIsUpperNotedWord)  {
                            strReturn += ")**(" + strUpperNote + ")";
                        }
                        // idx3 is the next char to process.
                        idx = idx3;
                        break;
                    } else if (serAfterWord.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                            && serAfterWord.getUnitType() == UnitProtoType.Type.TYPE_ROUND_BRACKET) {
                        int nBracketLvl = 0;
                        LinkedList<StructExprRecog> listserFunChildren = new LinkedList<StructExprRecog>();
                        int idx3 = idx + nWordInSERLength;
                        for (; idx3 < listserShownChildren.size(); idx3 ++)    {
                            StructExprRecog serThisChild = listserShownChildren.get(idx3);
                            listserFunChildren.add(serThisChild);
                            StructExprRecog serPrinciple = listserShownChildren.get(idx3).getPrincipleSER(4);
                            if (serThisChild.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                                    && serThisChild.getUnitType() == UnitProtoType.Type.TYPE_ROUND_BRACKET)   {
                                nBracketLvl ++;
                            }else if (serPrinciple.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                                    && serPrinciple.getUnitType() == UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET)   {
                                nBracketLvl --;
                            }
                            if (nBracketLvl == 0)   {
                                break;
                            }
                        }
                        if (idx3 == listserShownChildren.size())    {
                            idx3 = listserShownChildren.size() - 1;
                        }
                        StructExprRecog serFuncParam = new StructExprRecog(ser.getBiArray());
                        if (listserFunChildren.size() == 1
                                || (listserFunChildren.size() == 2
                                    && listserFunChildren.getLast().getPrincipleSER(4).getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                                    && listserFunChildren.getLast().getPrincipleSER(4).getUnitType() == UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET)) {   // no parameter
                            if (bIsUpperNotedWord)  {
                                strReturn += strWord + "(" + liststrChildren.get(idx3) + "**(" + strUpperNote + ")";
                            } else  {
                                strReturn += strWord + "(" + liststrChildren.get(idx3);
                            }
                        } else  {
                            serFuncParam.setStructExprRecog(listserFunChildren, StructExprRecog.EXPRRECOGTYPE_VBLANKCUT);
                            String strFuncParam = cvtSer2MFPExpr(serFuncParam, null, new CurPos(0), mwm, new SerMFPTransFlags());
                            if (bIsUpperNotedWord)  {
                                strReturn += "(" + strWord + strFuncParam + ")**(" + strUpperNote + ")";
                            } else  {
                                strReturn += strWord + strFuncParam;
                            }
                        }
                        idx = idx3 + 1;
                        break;
                    } else  {
                        if (bIsUpperNotedWord)  {
                            strReturn += "(";
                        }
                        strReturn += strWord + "(" + liststrChildren.get(idx + nWordInSERLength) + ")";
                        if (bIsUpperNotedWord)  {
                            strReturn += ")**(" + strUpperNote + ")";
                        }
                        idx += nWordInSERLength + 1;
                        break;
                    }
                } else if (bIsUpperNotedWord) {   // variable, but with upper note
                    for (int idx3 = idx; idx3 < idx + nWordInSERLength; idx3 ++)    {
                        strReturn += liststrChildren.get(idx3);
                    }
                    idx += nWordInSERLength;
                    break;
                } else  {   // variable, but without upper note
                    strReturn += strWord;
                    int idx3 = idx + nWordInSERLength;
                    for (; idx3 < liststrChildren.size(); idx3 ++)  {
                        if (listserShownChildren.get(idx3).isNumberChar())  {
                            strReturn += liststrChildren.get(idx3);
                            continue;
                        } else if (listserShownChildren.get(idx3).getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE
                                && listserShownChildren.get(idx3).getChildrenList().getFirst().isNumberChar())  {
                            // it is something like 3**2
                            strReturn += liststrChildren.get(idx3);
                            idx3 ++;
                            break;
                        } else  {
                            break;
                        }
                    }
                    idx = idx3;
                    break;
                }
            }
            // finish word finding.
            boolean bFindWord = !(idx1 == mwm.mslistMisrecogWordSet.size());
            StructExprRecog serThisUnit = listserShownChildren.get(nBeginningIdx);
            if (!bFindWord)   {
                // no, it is not a word, now let's consider all other possibilities like |.                   
                if (listserShownChildren.get(idx).getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                        && listserShownChildren.get(idx).getUnitType() == UnitProtoType.Type.TYPE_VERTICAL_LINE)    {
                    // is it a |?
                    StructExprRecog serPossibleOpenVLn = listserShownChildren.get(idx);
                    int idx4 = idx + 1;
                    for (; idx4 < listserShownChildren.size(); idx4 ++)   {
                        StructExprRecog serPossibleCloseVLn = listserShownChildren.get(idx4).getPrincipleSER(4);
                        if (serPossibleCloseVLn.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                                && serPossibleCloseVLn.getUnitType() == UnitProtoType.Type.TYPE_VERTICAL_LINE)  {
                            if ((serPossibleOpenVLn.getBottomPlus1() - serPossibleCloseVLn.mnTop) > ConstantsMgr.msdOpenCloseBracketHeightRatio * serPossibleOpenVLn.mnHeight
                                    && (serPossibleCloseVLn.getBottomPlus1() - serPossibleOpenVLn.mnTop) > ConstantsMgr.msdOpenCloseBracketHeightRatio * serPossibleOpenVLn.mnHeight
                                    && serPossibleOpenVLn.mnHeight > ConstantsMgr.msdOpenCloseBracketHeightRatio * serPossibleCloseVLn.mnHeight  // must have similar height as the start character
                                    && serPossibleOpenVLn.mnHeight < 1/ConstantsMgr.msdOpenCloseBracketHeightRatio * serPossibleCloseVLn.mnHeight)   {
                                // find corresponding close vertical line.
                                boolean bIsAbs = false;
                                if (idx4 > idx + 1) {
                                    StructExprRecog serAfterFirstVLn = listserShownChildren.get(idx + 1);
                                    if (serAfterFirstVLn.isBoundChar() || serAfterFirstVLn.getPrincipleSER(4).isPossibleNumberChar()
                                            || serAfterFirstVLn.getPrincipleSER(4).isLetterChar()
                                            || serAfterFirstVLn.getPrincipleSER(4).isPossibleNumberChar()
                                            || serAfterFirstVLn.getPrincipleSER(1).isIntegTypeChar()
                                            || serAfterFirstVLn.getPrincipleSER(1).isSIGMAPITypeChar()
                                            || serAfterFirstVLn.isPreUnOptChar()
                                            || serAfterFirstVLn.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_GETROOT
                                            || serAfterFirstVLn.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_HLINECUT
                                            || serAfterFirstVLn.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTMATRIX) {
                                        StructExprRecog serBeforeLastVLn = listserShownChildren.get(idx4 - 1);
                                        if (serBeforeLastVLn.getPrincipleSER(4).isCloseBoundChar() || serBeforeLastVLn.getPrincipleSER(4).isPossibleNumberChar()
                                                || serBeforeLastVLn.getPrincipleSER(4).isLetterChar()
                                                || serBeforeLastVLn.getPrincipleSER(4).isPossibleNumberChar()
                                                || serBeforeLastVLn.isPostUnOptChar()
                                                || serBeforeLastVLn.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_GETROOT
                                                || serBeforeLastVLn.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_HLINECUT
                                                || serBeforeLastVLn.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTMATRIX) {
                                            bIsAbs = true;
                                        }
                                    }

                                }

                                if (bIsAbs) {   // this is abs function
                                    serPossibleCloseVLn.setUnitType(UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET);
                                    liststrChildren.set(idx4, cvtSer2MFPExpr(listserShownChildren.get(idx4), ser, new CurPos(idx4), mwm, new SerMFPTransFlags()));
                                    strReturn += "abs(";
                                } else { // this is 1.
                                    serPossibleCloseVLn.setUnitType(UnitProtoType.Type.TYPE_ONE);
                                    liststrChildren.set(idx4, cvtSer2MFPExpr(listserShownChildren.get(idx4), ser, new CurPos(idx4), mwm, new SerMFPTransFlags()));
                                    strReturn += "1";
                                }
                                break;
                            }
                        }
                    }
                    if (idx4 == listserShownChildren.size())    {
                        // cannot find close v-line. don't consider that | may be or.
                        strReturn += "1";   //liststrChildren.get(idx);
                    }
                } else  {
                    // this is not a vline.
                    strReturn += liststrChildren.get(idx);
                }
                idx ++;
            }
            if (serParent != null
                    && (serParent.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTLUNOTES
                        || serParent.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTLOWERNOTE)
                    && curPos.mnPos == 1)   {
                continue;   // * should not be inserted into footnote.
            } else if (idx < listserShownChildren.size())  {   // identify if there is an extra multiply.
                StructExprRecog serNextUnit = listserShownChildren.get(idx).getPrincipleSER(4);
                if (!strReturn.substring(strReturn.length() - 1).equals("(")) { // no need to worry about ( in upper note or lower note because has been fixed before.
                    if (serNextUnit.isCloseBoundChar())  {
                        continue;   //) or )**2
                    } else if (serThisUnit.isBoundChar())   {
                        continue;
                    } else if (serThisUnit.isNumericChar() && serNextUnit.isNumericChar())    {
                        continue;   // like this is 3 and next is 2.
                    } else if (serThisUnit.isLetterChar() && serNextUnit.isNumericChar())   {
                        continue;   // like this is a and next is 1
                    } else if ((serThisUnit.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                                    || serThisUnit.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE
                                    || serThisUnit.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTLOWERNOTE
                                    || serThisUnit.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTLUNOTES)
                            && serThisUnit.getPrincipleSER(4).getUnitType() == UnitProtoType.Type.TYPE_SMALL_F
                            && serNextUnit.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                            && serNextUnit.getUnitType() == UnitProtoType.Type.TYPE_ROUND_BRACKET)   {
                        if ((serThisUnit.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTLOWERNOTE
                                    || serThisUnit.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTLUNOTES)
                                && serThisUnit.getChildrenList().get(1).getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VBLANKCUT
                                && serThisUnit.getChildrenList().get(1).getChildrenList().getFirst().getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                                && serThisUnit.getChildrenList().get(1).getChildrenList().getFirst().getUnitType() == UnitProtoType.Type.TYPE_ROUND_BRACKET
                                && serThisUnit.getChildrenList().get(1).getChildrenList().getLast().getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                                && serThisUnit.getChildrenList().get(1).getChildrenList().getLast().getUnitType() == UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET)   {
                            // this is like f_(x)
                            strReturn += "*";
                        } else  {
                            continue;   // like this is f(x), f**2(x), f_min(x)
                        }
                    } else if (!serThisUnit.isBiOptChar() && !serThisUnit.isPreUnOptChar()
                            && !serNextUnit.isBiOptChar() && !serNextUnit.isPostUnOptChar()) {
                        strReturn += "*";
                    } else  {
                        continue;
                    }
                }
            }
        }
        curPos.mnPos ++;
        return strReturn;
    }
    
    public static String cvtSer2MFPExpr(StructExprRecog ser, StructExprRecog serParent, CurPos curPos, MisrecogWordMgr mwm, SerMFPTransFlags smtFlag)    {
        String strReturn = "";
        switch (ser.getExprRecogType())    {
        case StructExprRecog.EXPRRECOGTYPE_ENUMTYPE:
        {
            SerMFPTransFlags smtFlagThis = new SerMFPTransFlags();
            smtFlagThis.mbConvertAssign2Eq = smtFlag.mbConvertAssign2Eq;
            strReturn = cvtUnitPrototype2MFPStr(ser.getUnitType(), smtFlagThis);
            curPos.mnPos ++;
            break;
        }
        case StructExprRecog.EXPRRECOGTYPE_HBLANKCUT:
        {
            // do not consider a situation where HBlankCut is actually a worng cut char.
            if (ser.getChildrenList().size() == 2 && ser.getChildrenList().getLast().getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VBLANKCUT
                    && serParent != null && serParent.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VBLANKCUT
                    && serParent.getChildrenList().size() > curPos.mnPos + 1 // lim is not the last char.
                    && ser.getChildrenList().getFirst().toString().equals("lim")) { // ok, this seems to be lim function.
                strReturn = cvtTopBaseBottom2MFPExpr(ser.getChildrenList().getFirst(), ser.getChildrenList().getLast(), null, ser, serParent, curPos, mwm);
            } else  {
                // not limit, process in a normal way.
                for (int idx = 0; idx < ser.getChildrenList().size(); idx ++)   {
                    CurPos curPosChild = new CurPos(idx);
                    SerMFPTransFlags smtFlagThis = new SerMFPTransFlags();
                    smtFlagThis.mbConvertAssign2Eq = smtFlag.mbConvertAssign2Eq;
                    strReturn += cvtSer2MFPExpr(ser.getChildrenList().get(idx), ser, curPosChild, mwm, smtFlagThis);
                    if (idx < ser.getChildrenList().size() - 1) {
                        strReturn += "\n";
                    }
                }
                curPos.mnPos ++;
            }
            break;
        }
        case StructExprRecog.EXPRRECOGTYPE_HLINECUT:
        {
            // horizontally cut by line div, and assume there are three children, the middle one is divide.
            CurPos curPosN = new CurPos(0), curPosD = new CurPos(1);
            SerMFPTransFlags smtFlagThis = new SerMFPTransFlags();
            smtFlagThis.mbConvertAssign2Eq = false; // h-line cut, assign will not be converted to equation.
            StructExprRecog serNumerator = ser.getChildrenList().getFirst();
            StructExprRecog serDenominator = ser.getChildrenList().getLast();
            boolean bIsDerivative = true;
            if (serNumerator.getExprRecogType() != StructExprRecog.EXPRRECOGTYPE_VBLANKCUT
                    || serNumerator.getChildrenList().size() < 2
                    || serDenominator.getExprRecogType() != StructExprRecog.EXPRRECOGTYPE_VBLANKCUT
                    || serDenominator.getChildrenList().size() < 2) {
                bIsDerivative = false;
            } else {
                StructExprRecog serNInitial = serNumerator.getChildrenList().getFirst();
                StructExprRecog serDInitial = serDenominator.getChildrenList().getFirst();
                StructExprRecog serDLast = serDenominator.getChildrenList().getLast();
                if (serDInitial.getExprRecogType() != StructExprRecog.EXPRRECOGTYPE_ENUMTYPE) {
                    bIsDerivative = false;
                } else if (serNInitial.getExprRecogType() != StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                        && serNInitial.getExprRecogType() != StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE) {
                    bIsDerivative = false;
                } else if (serNInitial.getExprRecogType() != serDLast.getExprRecogType()) {
                    bIsDerivative = false; // d^nf / dx^n or df/dx
                } else if (serNInitial.getPrincipleSER(4).getExprRecogType() != StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                        || serDLast.getPrincipleSER(4).getExprRecogType() != StructExprRecog.EXPRRECOGTYPE_ENUMTYPE) {
                    // base of nominal initial or denominator last is not a letter.
                    bIsDerivative = false;
                } else if (serNInitial.getPrincipleSER(4).getUnitType() != serDInitial.getUnitType()) {
                    // base of nominal initial should match denominator's initial
                    bIsDerivative = false;
                } else if (serDInitial.getUnitType() != UnitProtoType.Type.TYPE_SMALL_D
                        && serDInitial.getUnitType() != UnitProtoType.Type.TYPE_BIG_DELTA
                        && serDInitial.getUnitType() != UnitProtoType.Type.TYPE_SMALL_DELTA) {
                    // not df/dx or \Delta f/\Delta x or \delta f/\delta x?
                    bIsDerivative = false;
                } else if (serNInitial.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE
                        && (serNInitial.getChildrenList().getLast().getExprRecogType() != StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                            || serNInitial.getChildrenList().getLast().getUnitType() != serDLast.getChildrenList().getLast().getUnitType())) {
                    // not d^nf / dx^n
                    bIsDerivative = false;
                }
            }
            if (bIsDerivative) {
                StructExprRecog serToDeri = null;
                LinkedList<StructExprRecog> listToDeri = new LinkedList<StructExprRecog>();
                for (int idx = 1; idx < serNumerator.getChildrenList().size(); idx ++) {
                    listToDeri.add(serNumerator.getChildrenList().get(idx));
                }
                if (listToDeri.size() == 1) {
                    serToDeri = listToDeri.getFirst();
                } else {
                    serToDeri = new StructExprRecog(serNumerator.getBiArray());
                    serToDeri.setStructExprRecog(listToDeri, StructExprRecog.EXPRRECOGTYPE_VBLANKCUT);
                }
                String strToDeri = cvtSer2MFPExpr(serToDeri, null, new CurPos(0), mwm, smtFlagThis);    // to be derivatived expression.
                StructExprRecog serOrder = null;
                StructExprRecog serVarName = null;
                LinkedList<StructExprRecog> listVarName = new LinkedList<StructExprRecog>();
                listVarName.addAll(serDenominator.getChildrenList());
                listVarName.removeFirst();
                if (listVarName.getLast().getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE) {
                    // higher order derivative
                    StructExprRecog serLast = listVarName.pollLast();
                    serOrder = serLast.getChildrenList().getLast();
                    serLast = serLast.getPrincipleSER(4);
                    listVarName.addLast(serLast);
                }
                String strOrder = "1";  // derivative order.
                if (serOrder != null) {
                    strOrder = cvtSer2MFPExpr(serOrder, null, new CurPos(0), mwm, smtFlagThis);
                }
                if (listVarName.size() == 1) {
                    serVarName = listVarName.getFirst();
                } else {
                    serVarName = new StructExprRecog(serDenominator.getBiArray());
                    serVarName.setStructExprRecog(listVarName, StructExprRecog.EXPRRECOGTYPE_VBLANKCUT);
                }
                String strVarName = cvtSer2MFPExpr(serVarName, null, new CurPos(0), mwm, smtFlagThis);  // variable name
                boolean bIsDefDeri = true;  // definite derivative or not.
                String strVarVal = "";  // definite derivative value
                if (serParent == null || serParent.getExprRecogType() != StructExprRecog.EXPRRECOGTYPE_VBLANKCUT
                        || serParent.getChildrenList().size() == curPos.mnPos + 1) {
                    bIsDefDeri = false;
                } else {
                    StructExprRecog serNext = serParent.getChildrenList().get(curPos.mnPos + 1);
                    if (serNext.getExprRecogType() != StructExprRecog.EXPRRECOGTYPE_VCUTLOWERNOTE) {
                        bIsDefDeri = false;
                    } else {
                        StructExprRecog serNextBase = serNext.getPrincipleSER(4);
                        if (serNextBase.getExprRecogType() != StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                                || (serNextBase.getUnitType() != UnitProtoType.Type.TYPE_VERTICAL_LINE
                                    && serNextBase.getUnitType() != UnitProtoType.Type.TYPE_ONE))   {
                            bIsDefDeri = false;
                        } else {
                            StructExprRecog serNextLN = serNext.getChildrenList().getLast();
                            if (serNextLN.getExprRecogType() != StructExprRecog.EXPRRECOGTYPE_VBLANKCUT
                                    || serNextLN.getChildrenList().size() < 3)  {
                                bIsDefDeri = false;
                            } else {
                                int idxEq = 0;
                                for (; idxEq < serNextLN.getChildrenList().size(); idxEq ++) {
                                    if (serNextLN.getChildrenList().get(idxEq).getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                                            && serNextLN.getChildrenList().get(idxEq).getUnitType() == UnitProtoType.Type.TYPE_EQUAL) {
                                        break;  // find =
                                    }
                                }
                                if (idxEq == 0 || idxEq == serNextLN.getChildrenList().size() - 1) {
                                    bIsDefDeri = false;     // = is at the beginning or end.
                                }
                                if (bIsDefDeri) {
                                    LinkedList<StructExprRecog> listVarValInLN = new LinkedList<StructExprRecog>();
                                    if (idxEq < serNextLN.getChildrenList().size()) { // foot note is x = ...
                                        LinkedList<StructExprRecog> listVarNameInLN = new LinkedList<StructExprRecog>();
                                        for (int idx = 0; idx < idxEq; idx ++) {
                                            listVarNameInLN.add(serNextLN.getChildrenList().get(idx));
                                        }
                                        StructExprRecog serVarNameInLN;
                                        if (listVarNameInLN.size() == 1) {
                                            serVarNameInLN = listVarNameInLN.getFirst();
                                        } else {
                                            serVarNameInLN = new StructExprRecog(serNextLN.getBiArray());
                                            serVarNameInLN.setStructExprRecog(listVarNameInLN, StructExprRecog.EXPRRECOGTYPE_VBLANKCUT);
                                        }
                                        
                                        String strVarNameInLN = cvtSer2MFPExpr(serVarNameInLN, null, new CurPos(0), mwm, smtFlagThis);
                                        if (!strVarNameInLN.equalsIgnoreCase(strVarName)) {
                                            bIsDefDeri = false;
                                        } else {
                                            for (int idx = idxEq + 1; idx < serNextLN.getChildrenList().size(); idx ++) {
                                                listVarValInLN.add(serNextLN.getChildrenList().get(idx));
                                            }
                                        }
                                    } else {    // foot note is a value.
                                        listVarValInLN.addAll(serNextLN.getChildrenList());
                                    }
                                    if (bIsDefDeri) {
                                        StructExprRecog serVarValInLN;
                                        if (listVarValInLN.size() == 1) {
                                            serVarValInLN = listVarValInLN.getFirst();
                                        } else {
                                            serVarValInLN = new StructExprRecog(serNextLN.getBiArray());
                                            serVarValInLN.setStructExprRecog(listVarValInLN, StructExprRecog.EXPRRECOGTYPE_VBLANKCUT);
                                        }
                                        strVarVal = cvtSer2MFPExpr(serVarValInLN, null, new CurPos(0), mwm, smtFlagThis);
                                    }
                                }
                            }
                        }
                    }
                }
                // 求导阶数信息原来在这里
                if (bIsDefDeri) {
                    strReturn = "deri_ridders(\"" + addEscapes(strToDeri) + "\", \"" + addEscapes(strVarName) + "\", " + strVarVal + ", " + strOrder + ")";
                    curPos.mnPos += 2;  
                } else if (strOrder.equals("1")) { // 1st order
                    strReturn = "derivative(\"" + addEscapes(strToDeri) + "\", \"" + addEscapes(strVarName) + "\")";
                    curPos.mnPos ++;
                } else if (strOrder.equals("2")) { // second order
                    strReturn = "derivative(derivative(\"" + addEscapes(strToDeri) + "\", \"" + addEscapes(strVarName) + "\"), \"" + addEscapes(strVarName) + "\")";
                    curPos.mnPos ++;
                } else if (strOrder.equals("3")) { // third order
                    strReturn = "derivative(derivative(derivative(\"" + addEscapes(strToDeri) + "\", \"" + addEscapes(strVarName) + "\"), \"" + addEscapes(strVarName) + "\"), \"" + addEscapes(strVarName) + "\")";
                    curPos.mnPos ++;
                } else {
                    // to high order indefinite derivative, do not do any derivative conversion.
                    String strNumerator = cvtSer2MFPExpr(ser.getChildrenList().getFirst(), ser, curPosN, mwm, smtFlagThis);
                    String strDenominator = cvtSer2MFPExpr(ser.getChildrenList().getLast(), ser, curPosD, mwm, smtFlagThis);
                    strReturn = "((" + strNumerator + ")/(" + strDenominator + "))";    // assume strNumerator and strDenominator have included neccessary ().
                    curPos.mnPos ++;
                }
            } else {
                // not derivative.
                String strNumerator = cvtSer2MFPExpr(ser.getChildrenList().getFirst(), ser, curPosN, mwm, smtFlagThis);
                String strDenominator = cvtSer2MFPExpr(ser.getChildrenList().getLast(), ser, curPosD, mwm, smtFlagThis);
                strReturn = "((" + strNumerator + ")/(" + strDenominator + "))";    // assume strNumerator and strDenominator have included neccessary ().
                curPos.mnPos ++;
            }
            break;
        }
        case StructExprRecog.EXPRRECOGTYPE_HCUTCAP:
        case StructExprRecog.EXPRRECOGTYPE_HCUTUNDER:
        case StructExprRecog.EXPRRECOGTYPE_HCUTCAPUNDER:
        {
            StructExprRecog serBase = (ser.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_HCUTUNDER)?
                    ser.getChildrenList().getFirst():ser.getChildrenList().get(1);
            StructExprRecog serCap
                    = (ser.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_HCUTUNDER)?
                        null:ser.getChildrenList().getFirst();
            StructExprRecog serUnder
                    = (ser.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_HCUTCAP)?
                        null:ser.getChildrenList().getLast();
            strReturn = cvtTopBaseBottom2MFPExpr(serBase, serUnder, serCap, ser, serParent, curPos, mwm);
            break;
        }
        case StructExprRecog.EXPRRECOGTYPE_VBLANKCUT:
        {
            strReturn = cvtVBlank2MFPExpr(ser, serParent, curPos, mwm, smtFlag);
            break;
        }
        case StructExprRecog.EXPRRECOGTYPE_VCUTLEFTTOPNOTE:
        {
            // left top note should not appear because it has been converted to oC or oF
            StructExprRecog serNote = ser.getChildrenList().getFirst();
            StructExprRecog serBase = ser.getChildrenList().getLast();
            if (serNote.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    && (serNote.getUnitType() == UnitProtoType.Type.TYPE_SMALL_O
                        || serNote.getUnitType() == UnitProtoType.Type.TYPE_BIG_O
                        || serNote.getUnitType() == UnitProtoType.Type.TYPE_ZERO)
                    && serBase.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE)   {
                if (serBase.getUnitType() == UnitProtoType.Type.TYPE_SMALL_C
                        || serBase.getUnitType() == UnitProtoType.Type.TYPE_BIG_C) {
                    strReturn = "\\celcius";
                } else if (serBase.getUnitType() == UnitProtoType.Type.TYPE_BIG_F)    {
                    strReturn = "\\fahrenheit";
                } else  {
                    CurPos curPosThis = new CurPos(1);
                    SerMFPTransFlags smtFlagThis = new SerMFPTransFlags();
                    smtFlagThis.mbConvertAssign2Eq = smtFlag.mbConvertAssign2Eq;
                    strReturn = cvtSer2MFPExpr(serBase, ser, curPosThis, mwm, smtFlagThis);   // assume the note is just a noise stroke.
                }
            } else  {    
                CurPos curPosThis = new CurPos(1);
                SerMFPTransFlags smtFlagThis = new SerMFPTransFlags();
                smtFlagThis.mbConvertAssign2Eq = smtFlag.mbConvertAssign2Eq;
                strReturn = cvtSer2MFPExpr(serBase, ser, curPosThis, mwm, smtFlagThis);   // assume the note is just a noise stroke.
            }
            curPos.mnPos ++;
            break;
        }
        case StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE:
        case StructExprRecog.EXPRRECOGTYPE_VCUTLOWERNOTE:
        case StructExprRecog.EXPRRECOGTYPE_VCUTLUNOTES:
        {
            //todo by LH, Here to let the expr with upper note '/' be derivative
            //we need to consider the case in which there have two '/',

            StructExprRecog serBase = ser.getChildrenList().getFirst();
            StructExprRecog serLowerNote
                    = (ser.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE)?
                    null:ser.getChildrenList().get(1);
            StructExprRecog serUpperNote
                    = (ser.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_VCUTLOWERNOTE)?
                    null:ser.getChildrenList().getLast();

            if(ser.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VCUTUPPERNOTE && serUpperNote.mType == UnitProtoType.Type.TYPE_FORWARD_SLASH){
                String strVarName = "null";

                SerMFPTransFlags smtFlags = new SerMFPTransFlags();
                smtFlags.mbConvertAssign2Eq = true;
                String strRealExpr = cvtSer2MFPExpr(serBase, null, new CurPos(0), mwm, smtFlags);

                if(serBase.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE){
                    strVarName = strRealExpr;
                }else if(serBase.mnExprRecogType == StructExprRecog.EXPRRECOGTYPE_VBLANKCUT){
                    for(StructExprRecog son : serBase.mlistChildren){
                        if(son.isLetterChar()){
                            strVarName = son.toString();
                            break;
                        }else if(son.getPrincipleSER(4).isLetterChar()){
                            strVarName = son.getPrincipleSER(4).toString();
                            break;
                        }
                    }
                }

                strReturn = "derivative(\"" + addEscapes(strRealExpr) + "\", \"" + strVarName + "\")";
            }
            else
                strReturn = cvtTopBaseBottom2MFPExpr(serBase, serLowerNote, serUpperNote, ser, serParent, curPos, mwm);
            break;
        }
        case StructExprRecog.EXPRRECOGTYPE_VCUTMATRIX:
        {
            // horizontally cut by blank div, every child is equal.
            strReturn = "[";
            for (int idx = 0; idx < ser.getChildrenList().getFirst().getChildrenList().size(); idx ++)  {
                strReturn += "[";
                for (int idx1 = 0; idx1 < ser.getChildrenList().size(); idx1 ++)    {
                    CurPos curPosThis = new CurPos(idx1);
                    strReturn += cvtSer2MFPExpr(ser.getChildrenList().get(idx1).getChildrenList().get(idx), ser, curPosThis, mwm, new SerMFPTransFlags());
                    if (idx1 < ser.getChildrenList().size() - 1)    {
                        strReturn += ",";
                    }
                }
                strReturn += "]";
                if (idx < ser.getChildrenList().getFirst().getChildrenList().size() - 1)    {
                    strReturn += ",";
                }
            }
            strReturn += "]";
            curPos.mnPos ++;
            break;
        }
        case StructExprRecog.EXPRRECOGTYPE_MULTIEXPRS:
        {
            for (int idx = 0; idx < ser.getChildrenList().size(); idx ++)   {
                CurPos curPosThis = new CurPos(idx);
                SerMFPTransFlags smtFlagThis = new SerMFPTransFlags();
                smtFlagThis.mbConvertAssign2Eq = smtFlag.mbConvertAssign2Eq;
                strReturn += cvtSer2MFPExpr(ser.getChildrenList().get(idx), ser, curPosThis, mwm, smtFlagThis);
                if (idx < ser.getChildrenList().size() - 1) {
                    strReturn += "\n";
                }
            }
            curPos.mnPos ++;
            break;
        }
        case StructExprRecog.EXPRRECOGTYPE_GETROOT:
        {
            if (ser.getChildrenList().getFirst().getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    && (ser.getChildrenList().getFirst().getUnitType() == UnitProtoType.Type.TYPE_SQRT_LEFT
                        || ser.getChildrenList().getFirst().getUnitType() == UnitProtoType.Type.TYPE_SQRT_SHORT
                        || ser.getChildrenList().getFirst().getUnitType() == UnitProtoType.Type.TYPE_SQRT_MEDIUM
                        || ser.getChildrenList().getFirst().getUnitType() == UnitProtoType.Type.TYPE_SQRT_LONG
                        || ser.getChildrenList().getFirst().getUnitType() == UnitProtoType.Type.TYPE_SQRT_TALL
                        || ser.getChildrenList().getFirst().getUnitType() == UnitProtoType.Type.TYPE_SQRT_VERY_TALL))   {
                strReturn = "sqrt(" + cvtSer2MFPExpr(ser.getChildrenList().getLast(), ser, new CurPos(1), mwm, new SerMFPTransFlags()) + ")";
            } else  {
                strReturn = "pow(" + cvtSer2MFPExpr(ser.getChildrenList().getLast(), ser, new CurPos(1), mwm, new SerMFPTransFlags())
                        + ", 1/(" + cvtSer2MFPExpr(ser.getChildrenList().getFirst(), ser, new CurPos(0), mwm, new SerMFPTransFlags()) + "))";
            }
            curPos.mnPos ++;
            break;
        }
        default:    // list cut, do nothing
        }
        return strReturn;
    }
    
    // returns end p1, for example (...), end plus 1 is the ), { ..., end plus 1 is length (beyond the end of serlist)
    // integrate, end plus 1 is the character d.
    public static int findEndP1OfOpratedExpr(LinkedList<StructExprRecog> listSers, int nStart)  {
        StructExprRecog serInitial = listSers.get(nStart);
        StructExprRecog serInitPrinciple = serInitial.getPrincipleSER(5);
        int nEnd = nStart;
        if (nStart == listSers.size() - 1)  {
            nEnd = nStart;
        } if (serInitPrinciple.isIntegTypeChar())  {
            int nCloseIntegLvl = 0;
            int idx = nStart + 1;
            for (; idx < listSers.size(); idx ++)   {
                StructExprRecog serThis = listSers.get(idx);
                if (serThis.getPrincipleSER(5).isIntegTypeChar())    {   //LUnotes or cap under.
                    nCloseIntegLvl --;
                } else if (serThis.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE)    {
                    if (serThis.getUnitType() == UnitProtoType.Type.TYPE_SMALL_D && idx < (listSers.size() - 1)
                            && listSers.get(idx + 1).getPrincipleSER(5).isLetterChar())    {
                        // something like dx
                        nCloseIntegLvl ++;                    
                        if (nCloseIntegLvl == 1) {
                            break;
                        }
                    } else if (serThis.isCompareOptChar())  {
                        break;
                    }
                }
            }
            if (idx < listSers.size())   {
                nEnd = idx;
            } else  {
                nEnd = listSers.size();
            }
        } else if (serInitial.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                && serInitial.getUnitType() == UnitProtoType.Type.TYPE_BRACE
                && listSers.get(nStart + 1).getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_MULTIEXPRS)    {
            if (listSers.size() > nStart + 2 && listSers.get(nStart + 2).getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    && listSers.get(nStart + 2).getUnitType() == UnitProtoType.Type.TYPE_CLOSE_BRACE) {
                nEnd = nStart + 2;
            } else  {
                nEnd = nStart + 2;  // same as { ... }
            }
        } else  {
            int nCloseRoundBracketLvl = 0;
            int idx = nStart + 1;
            for (; idx < listSers.size(); idx ++)   {
                StructExprRecog serThis = listSers.get(idx);
                if (serThis.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                    && serThis.getUnitType() == UnitProtoType.Type.TYPE_ROUND_BRACKET)    {
                    nCloseRoundBracketLvl --;
                } else if (serThis.getPrincipleSER(4).getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                        && serThis.getPrincipleSER(4).getUnitType() == UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET)  {
                    nCloseRoundBracketLvl ++;
                    if (nCloseRoundBracketLvl == 1) {
                        break;
                    }
                } else if (serThis.getExprRecogType() == StructExprRecog.EXPRRECOGTYPE_ENUMTYPE
                        && (serThis.getUnitType() == UnitProtoType.Type.TYPE_EQUAL
                            || serThis.getUnitType() == UnitProtoType.Type.TYPE_EQUAL_ALWAYS
                            || serThis.getUnitType() == UnitProtoType.Type.TYPE_EQUAL_ROUGHLY
                            || serThis.getUnitType() == UnitProtoType.Type.TYPE_LARGER
                            || serThis.getUnitType() == UnitProtoType.Type.TYPE_SMALLER
                            || serThis.getUnitType() == UnitProtoType.Type.TYPE_NO_LARGER
                            || serThis.getUnitType() == UnitProtoType.Type.TYPE_NO_SMALLER))    {
                    break;
                }
            }
            if (idx < listSers.size())   {
                nEnd = idx;
            } else  {
                nEnd = listSers.size();
            }
        }
        return nEnd;
    }
    
    //add Escapes in ActivityPlotXYGraph works differently from same name function in ChartOperator.
	public static String addEscapes(String strInput)	{
		String strOutput = "";
		if (strInput != null)	{
			for (int i = 0; i < strInput.length(); i++)	{
				char cCurrent = strInput.charAt(i);
				if (cCurrent == '\"')	{
					strOutput += "\\\"";
				} else if (cCurrent == '\\')	{
					strOutput += "\\\\";
				} else	{
					strOutput += cCurrent;
				}
			}
		}
		return strOutput;
	}
}
