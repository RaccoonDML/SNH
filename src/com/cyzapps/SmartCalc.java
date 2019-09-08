package com.cyzapps;

import com.cyzapps.Jfcalc.*;
import com.cyzapps.*;
import com.cyzapps.Jmfp.VariableOperator;
import com.cyzapps.Jsma.*;
import com.cyzapps.SmartMath.SmartCalcProcLib;
import com.cyzapps.adapter.AbstractExprConverter;
import com.cyzapps.adapter.MFPAdapter;
import com.cyzapps.Jsma.UnknownVarOperator;

import java.util.LinkedList;

public class SmartCalc {
    public static void main(String[] args) throws InterruptedException, ErrProcessor.JFCALCExpErrException, SMErrProcessor.JSmartMathErrException {
        String calcA;
        String strExpressions="x+y==3\ny-x+z==3\nz+y==4";

        if(strExpressions.indexOf("\n")!=-1)//方程组
        {
            strExpressions=Cut(strExpressions);
        }
        else if(strExpressions.indexOf("integrate")!=-1&&strExpressions.indexOf("==")!=-1)//积分方程
        {
            String calcA1;
            String[] strarraycup = strExpressions.split("==");
            calcA1= SmartCalcProcLib.calculate(strarraycup[0],false);
            String temp = calcA1.replace("\\text", "");
            temp = temp.replace("\"", "");
            temp = temp.replace("{", "(");
            temp = temp.replace("}", ")");
            System.out.println(temp);
            temp = temp.replace("×", "*");
            temp = temp.replace("^", "**");
            strExpressions=temp+"=="+strarraycup[1];
        }

        calcA= SmartCalcProcLib.calculate(strExpressions,false);
        // System.out.println(calcA);

        //integrate("x**(2)","x","0","1"),integrate("x**(2)","x"),integrate("x**(2)","x")==9
        //sin,cos，asin,16+9*e**(3),log(2),pow(5+7, 1/3)
        //x+5-2==10,x+y+5==10,X+X**(2)+3==-5,a+5**(((X+3)/(4))),sqrt(x)==2,sqrt(x+y)==2,pow(x, 1/3)==2,log(x)==2,f_x==x+1\nx==2
        // "x+y==3\ny-x==1"]]"x+y==3\ny-x+z==3\nz+y==4"]]"x+y==3\ny==x+2"]]x+y==3 y=2，f_x==x+1\nx==2
        //A==[[2,4],[6,9]]      [[1,2,5],[6,7,9],[8,0,3]]*A==[[4],[5],[6]]   [[1,2,5],[6,7,9],[8,0,3]]*[[x1],[x2],[x3]]==[[4],[5],[6]]
        //sum_over("k**(2)+2*k-4","k=0","9")  product_over("k","k=1","3")    lim("1/n","n","inf")
        //"x==1\nx+y==2\nx+y+z==4"

        //x+sqrt(x+2)==2,derivative("x**(6)", "X") ans =6*x^5  ,integrate("x","x")==2
        
        String temp = calcA.replace("\\text", "");
        temp = temp.replace("\"", "");
        temp = temp.replace("{", "(");
        temp = temp.replace("}", ")");

        System.out.println(temp);

    }


    public static String Cut(String strExpressions) throws InterruptedException, SMErrProcessor.JSmartMathErrException, ErrProcessor.JFCALCExpErrException {
        String[] strarrayExprs = strExpressions.split("\n");//多个表达式
        String temp;
        int num=strarrayExprs.length;
        int[] array=new int[num];
        //int[] array={0};
        LinkedList<UnknownVarOperator.UnknownVariable> listVarUnknown = new LinkedList<UnknownVarOperator.UnknownVariable>();    // The unknown variable list未知变量列表
        LinkedList<LinkedList<VariableOperator.Variable>> lVarNameSpaces = new LinkedList<LinkedList<VariableOperator.Variable>>();

        for (int idx = 0; idx < strarrayExprs.length; idx++) {
            if (strarrayExprs[idx].trim().length() == 0) {
                continue;    // empty string
            } else {
                /* evaluate the expression */
                BaseData.CurPos curpos = new BaseData.CurPos();
                curpos.m_nPos = 0;
                String strarrayAnswer[] = new String[2];
                AbstractExpr aexpr = new AEInvalid();

                /* evaluate the expression */
                aexpr = ExprAnalyzer.analyseExpression(strarrayExprs[idx], curpos);
                LinkedList<AbstractExpr> listAEVars = new LinkedList<AbstractExpr>();
                LinkedList<AbstractExpr> listAERootVars = new LinkedList<AbstractExpr>();
                AbstractExpr[] arrayAEs = new AbstractExpr[1];
                arrayAEs[0] = aexpr;
                PtnSlvMultiVarsIdentifier.lookupToSolveVarsInExprs(arrayAEs, listAEVars, listAERootVars);

                LinkedList<VariableOperator.Variable> listVarThisSpace = new LinkedList<VariableOperator.Variable>();
                lVarNameSpaces.add(listVarThisSpace);
                int zong=0,zhe=0;
                for (int idx1 = 0; idx1 < listAEVars.size(); idx1++) {
                    if (listAEVars.get(idx1) instanceof AEVar) {
                        String strName = ((AEVar) listAEVars.get(idx1)).mstrVariableName;
                        if (VariableOperator.lookUpPreDefined(strName) == null) {    // this variable is not a predefined var nor does it exist
                            //这个变量不是一个预定义的变量，也不存在
                            UnknownVarOperator.UnknownVariable varUnknown = new UnknownVarOperator.UnknownVariable(strName);
                            // if this variable hasn't been added, add it.
                            if (UnknownVarOperator.lookUpList(strName, listVarUnknown) == null) {
                                listVarUnknown.add(varUnknown);//总变量
                                //System.out.println("测试1111：" + AbstractExprConverter.convtPlainStr2QuotedUrl(strName));
                                zong+=1;
                                //array[idx]=array[idx]+1;
                            }
                            if (VariableOperator.lookUpList(strName, listVarThisSpace) == null) {
                                listVarThisSpace.add(varUnknown);//这个式子里的变量
                                //System.out.println("`测试1111：" + AbstractExprConverter.convtPlainStr2QuotedUrl(strName));
                                zhe+=1;
                            }
                        }
                    }
                }
                if(zhe==1)
                    array[idx]=-1;
                else
                    array[idx]=zong;
                //System.out.println("`测试1111：" + String.valueOf(idx)+' '+String.valueOf(array[idx]));
            }
        }

        int tint;
        int yidong=1;
        for (int i = 0; i < num-1; i++)
        {
            yidong=0;
            for(int j=0;j<num-i-1;j++)
            {
                if(array[j+1]<array[j])
                {
                    temp=strarrayExprs[j];
                    strarrayExprs[j]=strarrayExprs[j+1];
                    strarrayExprs[j+1]=temp;
                    tint=array[j];
                    array[j]=array[j+1];
                    array[j+1]=tint;
                    yidong=1;
                }
            }
            if(yidong==0)
                break;
        }
        String allStr="";
        for(int i = 0; i < num; i++)
        {
            allStr+=strarrayExprs[i]+'\n';
        }
        //System.out.println("啦啦啦啦：" + allStr);
        return allStr;
    }


}
