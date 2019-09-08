/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyzapps.mathrecog;



import java.util.LinkedList;
import java.util.Locale;

/**
 *
 * @author tonyc
 */
public class UnitPrototypeMgr {
    public static class UnitProtoType   {
        public static enum Type {
            TYPE_EMPTY,
            // use bookman old style black font 72
            TYPE_ZERO,
            TYPE_ONE,
            TYPE_TWO,
            TYPE_THREE,
            TYPE_FOUR,
            TYPE_FIVE,
            TYPE_SIX,
            TYPE_SEVEN,
            TYPE_EIGHT,
            TYPE_NINE,
            TYPE_INFINITE,
            TYPE_SMALL_A,
            TYPE_SMALL_B,
            TYPE_SMALL_C,
            TYPE_SMALL_D,
            TYPE_SMALL_E,
            TYPE_SMALL_F,
            TYPE_SMALL_G,
            TYPE_SMALL_H,
            TYPE_SMALL_I,
            TYPE_SMALL_I_WITHOUT_DOT,
            TYPE_SMALL_J,
            TYPE_SMALL_J_WITHOUT_DOT,
            TYPE_SMALL_K,
            TYPE_SMALL_L,
            TYPE_SMALL_M,
            TYPE_SMALL_N,
            TYPE_SMALL_O,
            TYPE_SMALL_P,
            TYPE_SMALL_Q,
            TYPE_SMALL_R,
            TYPE_SMALL_S,
            TYPE_SMALL_T,
            TYPE_SMALL_U,
            TYPE_SMALL_V,
            TYPE_SMALL_W,
            TYPE_SMALL_X,
            TYPE_SMALL_Y,
            TYPE_SMALL_Z,
            TYPE_BIG_A,
            TYPE_BIG_B,
            TYPE_BIG_C,
            TYPE_BIG_D,
            TYPE_BIG_E,
            TYPE_BIG_F,
            TYPE_BIG_G,
            TYPE_BIG_H,
            TYPE_BIG_I,
            TYPE_BIG_J,
            TYPE_BIG_K,
            TYPE_BIG_L,
            TYPE_BIG_M,
            TYPE_BIG_N,
            TYPE_BIG_O,
            TYPE_BIG_P,
            TYPE_BIG_Q,
            TYPE_BIG_R,
            TYPE_BIG_S,
            TYPE_BIG_T,
            TYPE_BIG_U,
            TYPE_BIG_V,
            TYPE_BIG_W,
            TYPE_BIG_X,
            TYPE_BIG_Y,
            TYPE_BIG_Z,
            // use Arial black font 72
            TYPE_SMALL_ALPHA,
            TYPE_SMALL_BETA,
            TYPE_SMALL_GAMMA,
            TYPE_SMALL_DELTA,
            TYPE_SMALL_EPSILON,
            TYPE_SMALL_ZETA,
            TYPE_SMALL_ETA,
            TYPE_SMALL_THETA,
            TYPE_SMALL_LAMBDA,
            TYPE_SMALL_MU,
            TYPE_SMALL_XI,
            TYPE_SMALL_PI,
            TYPE_SMALL_RHO,
            TYPE_SMALL_SIGMA,
            TYPE_SMALL_TAU,
            TYPE_SMALL_PHI,
            TYPE_SMALL_PSI,
            TYPE_SMALL_OMEGA,
            TYPE_BIG_DELTA,
            TYPE_BIG_THETA,
            TYPE_BIG_PI,
            TYPE_BIG_SIGMA,
            TYPE_BIG_PHI,
            TYPE_BIG_OMEGA,
            TYPE_INTEGRATE,
            TYPE_INTEGRATE_CIRCLE,
            TYPE_SQRT_LEFT,
            TYPE_SQRT_SHORT,
            TYPE_SQRT_MEDIUM,
            TYPE_SQRT_LONG,
            TYPE_SQRT_TALL,
            TYPE_SQRT_VERY_TALL,
            // use bookman old style black font 72
            TYPE_ADD,
            TYPE_SUBTRACT,
            TYPE_PLUS_MINUS,
            TYPE_MULTIPLY,
            TYPE_DOT_MULTIPLY,
            TYPE_DIVIDE,
            TYPE_FORWARD_SLASH,
            TYPE_BACKWARD_SLASH,
            TYPE_EQUAL,
            TYPE_EQUAL_ALWAYS,
            TYPE_EQUAL_ROUGHLY,
            TYPE_LARGER,
            TYPE_SMALLER,
            TYPE_NO_LARGER,
            TYPE_NO_SMALLER,
            TYPE_PERCENT,
            TYPE_EXCLAIMATION,
            TYPE_DOT,
            TYPE_STAR,
            //小括号
            TYPE_ROUND_BRACKET,
            TYPE_CLOSE_ROUND_BRACKET,
            //中括号
            TYPE_SQUARE_BRACKET,
            TYPE_CLOSE_SQUARE_BRACKET,
            //大括号
            TYPE_BRACE,
            TYPE_CLOSE_BRACE,

            TYPE_VERTICAL_LINE,
            TYPE_WAVE,
            TYPE_LEFT_ARROW,
            TYPE_RIGHT_ARROW,
            TYPE_DOLLAR,
            TYPE_EURO,
            TYPE_YUAN,
            TYPE_POUND,
            TYPE_CELCIUS,
            TYPE_FAHRENHEIT,
            TYPE_WORD_SIN,
            TYPE_WORD_COS,
            TYPE_WORD_TAN,
            TYPE_WORD_LIM,
            TYPE_WORD_LOG,
            TYPE_UNKNOWN;
        }
                
        public String mstrFont = "";
        public Type mnUnitType = Type.TYPE_UNKNOWN;
        public CharUnit mcharUnit = new CharUnit();
        public double mdWMinNumStrokes = 0; // width is equal to at least how many strokes, 0 mean any value should be ok
        public double mdHMinNumStrokes = 0; // height is equal to at least how many strokes, 0 mean any value should be ok
        
        public static String getTypeValueString(UnitProtoType.Type unitType) {
            String str = "";
            switch (unitType)  {
            case TYPE_EMPTY:
                str="TYPE_EMPTY";
                break;
            case TYPE_ZERO:
                str="TYPE_ZERO";
                break;
            case TYPE_ONE:
                str="TYPE_ONE";
                break;
            case TYPE_TWO:
                str="TYPE_TWO";
                break;
            case TYPE_THREE:
                str="TYPE_THREE";
                break;
            case TYPE_FOUR:
                str="TYPE_FOUR";
                break;
            case TYPE_FIVE:
                str="TYPE_FIVE";
                break;
            case TYPE_SIX:
                str="TYPE_SIX";
                break;
            case TYPE_SEVEN:
                str="TYPE_SEVEN";
                break;
            case TYPE_EIGHT:
                str="TYPE_EIGHT";
                break;
            case TYPE_NINE:
                str="TYPE_NINE";
                break;
            case TYPE_INFINITE:
                str="TYPE_INFINITE";
                break;
            case TYPE_SMALL_A:
                str="TYPE_SMALL_A";
                break;
            case TYPE_SMALL_B:
                str="TYPE_SMALL_B";
                break;
            case TYPE_SMALL_C:
                str="TYPE_SMALL_C";
                break;
            case TYPE_SMALL_D:
                str="TYPE_SMALL_D";
                break;
            case TYPE_SMALL_E:
                str="TYPE_SMALL_E";
                break;
            case TYPE_SMALL_F:
                str="TYPE_SMALL_F";
                break;
            case TYPE_SMALL_G:
                str="TYPE_SMALL_G";
                break;
            case TYPE_SMALL_H:
                str="TYPE_SMALL_H";
                break;
            case TYPE_SMALL_I:
                str="TYPE_SMALL_I";
                break;
            case TYPE_SMALL_I_WITHOUT_DOT:
                str="TYPE_SMALL_I_WITHOUT_DOT";
                break;
            case TYPE_SMALL_J:
                str="TYPE_SMALL_J";
                break;
            case TYPE_SMALL_J_WITHOUT_DOT:
                str="TYPE_SMALL_J_WITHOUT_DOT";
                break;
            case TYPE_SMALL_K:
                str="TYPE_SMALL_K";
                break;
            case TYPE_SMALL_L:
                str="TYPE_SMALL_L";
                break;
            case TYPE_SMALL_M:
                str="TYPE_SMALL_M";
                break;
            case TYPE_SMALL_N:
                str="TYPE_SMALL_N";
                break;
            case TYPE_SMALL_O:
                str="TYPE_SMALL_O";
                break;
            case TYPE_SMALL_P:
                str="TYPE_SMALL_P";
                break;
            case TYPE_SMALL_Q:
                str="TYPE_SMALL_Q";
                break;
            case TYPE_SMALL_R:
                str="TYPE_SMALL_R";
                break;
            case TYPE_SMALL_S:
                str="TYPE_SMALL_S";
                break;
            case TYPE_SMALL_T:
                str="TYPE_SMALL_T";
                break;
            case TYPE_SMALL_U:
                str="TYPE_SMALL_U";
                break;
            case TYPE_SMALL_V:
                str="TYPE_SMALL_V";
                break;
            case TYPE_SMALL_W:
                str="TYPE_SMALL_W";
                break;
            case TYPE_SMALL_X:
                str="TYPE_SMALL_X";
                break;
            case TYPE_SMALL_Y:
                str="TYPE_SMALL_Y";
                break;
            case TYPE_SMALL_Z:
                str="TYPE_SMALL_Z";
                break;
            case TYPE_BIG_A:
                str="TYPE_BIG_A";
                break;
            case TYPE_BIG_B:
                str="TYPE_BIG_B";
                break;
            case TYPE_BIG_C:
                str="TYPE_BIG_C";
                break;
            case TYPE_BIG_D:
                str="TYPE_BIG_D";
                break;
            case TYPE_BIG_E:
                str="TYPE_BIG_E";
                break;
            case TYPE_BIG_F:
                str="TYPE_BIG_F";
                break;
            case TYPE_BIG_G:
                str="TYPE_BIG_G";
                break;
            case TYPE_BIG_H:
                str="TYPE_BIG_H";
                break;
            case TYPE_BIG_I:
                str="TYPE_BIG_I";
                break;
            case TYPE_BIG_J:
                str="TYPE_BIG_J";
                break;
            case TYPE_BIG_K:
                str="TYPE_BIG_K";
                break;
            case TYPE_BIG_L:
                str="TYPE_BIG_L";
                break;
            case TYPE_BIG_M:
                str="TYPE_BIG_M";
                break;
            case TYPE_BIG_N:
                str="TYPE_BIG_N";
                break;
            case TYPE_BIG_O:
                str="TYPE_BIG_O";
                break;
            case TYPE_BIG_P:
                str="TYPE_BIG_P";
                break;
            case TYPE_BIG_Q:
                str="TYPE_BIG_Q";
                break;
            case TYPE_BIG_R:
                str="TYPE_BIG_R";
                break;
            case TYPE_BIG_S:
                str="TYPE_BIG_S";
                break;
            case TYPE_BIG_T:
                str="TYPE_BIG_T";
                break;
            case TYPE_BIG_U:
                str="TYPE_BIG_U";
                break;
            case TYPE_BIG_V:
                str="TYPE_BIG_V";
                break;
            case TYPE_BIG_W:
                str="TYPE_BIG_W";
                break;
            case TYPE_BIG_X:
                str="TYPE_BIG_X";
                break;
            case TYPE_BIG_Y:
                str="TYPE_BIG_Y";
                break;
            case TYPE_BIG_Z:
                str="TYPE_BIG_Z";
                break;
            case TYPE_SMALL_ALPHA:
                str="TYPE_SMALL_ALPHA";
                break;
            case TYPE_SMALL_BETA:
                str="TYPE_SMALL_BETA";
                break;
            case TYPE_SMALL_GAMMA:
                str="TYPE_SMALL_GAMMA";   // use bookman old style black font 72
                break;
            case TYPE_SMALL_DELTA:
                str="TYPE_SMALL_DELTA";
                break;
            case TYPE_SMALL_EPSILON:
                str="TYPE_SMALL_EPSILON";
                break;
            case TYPE_SMALL_ZETA:
                str="TYPE_SMALL_ZETA";
                break;
            case TYPE_SMALL_ETA:
                str="TYPE_SMALL_ETA";
                break;
            case TYPE_SMALL_THETA:
                str="TYPE_SMALL_THETA";
                break;
            case TYPE_SMALL_LAMBDA:
                str="TYPE_SMALL_LAMBDA";
                break;
            case TYPE_SMALL_MU:
                str="TYPE_SMALL_MU";
                break;
            case TYPE_SMALL_XI:
                str="TYPE_SMALL_XI";
                break;
            case TYPE_SMALL_PI:
                str="TYPE_SMALL_PI";
                break;
            case TYPE_SMALL_RHO:
                str="TYPE_SMALL_RHO";
                break;
            case TYPE_SMALL_SIGMA:
                str="TYPE_SMALL_SIGMA";
                break;
            case TYPE_SMALL_TAU:
                str="TYPE_SMALL_TAU"; // use bookman old style black font 72
                break;
            case TYPE_SMALL_PHI:
                str="TYPE_SMALL_PHI";
                break;
            case TYPE_SMALL_PSI:
                str="TYPE_SMALL_PSI";
                break;
            case TYPE_SMALL_OMEGA:
                str="TYPE_SMALL_OMEGA";
                break;
            case TYPE_BIG_DELTA:
                str="TYPE_BIG_DELTA";
                break;
            case TYPE_BIG_THETA:
                str="TYPE_BIG_THETA";
                break;
            case TYPE_BIG_PI:
                str="TYPE_BIG_PI";
                break;
            case TYPE_BIG_SIGMA:
                str="TYPE_BIG_SIGMA";
                break;
            case TYPE_BIG_PHI:
                str="TYPE_BIG_PHI";   // use bookman old style black font 72
                break;
            case TYPE_BIG_OMEGA:
                str="TYPE_BIG_OMEGA";
                break;
            case TYPE_INTEGRATE:
                str="TYPE_INTEGRATE";
                break;
            case TYPE_INTEGRATE_CIRCLE:
                str="TYPE_INTEGRATE_CIRCLE";
                break;
            case TYPE_SQRT_LEFT:
                str="TYPE_SQRT_LEFT";
                break;
            case TYPE_SQRT_SHORT:
                str="TYPE_SQRT_SHORT";
                break;
            case TYPE_SQRT_MEDIUM:
                str="TYPE_SQRT_MEDIUM";
                break;
            case TYPE_SQRT_LONG:
                str="TYPE_SQRT_LONG";
                break;
            case TYPE_SQRT_TALL:
                str="TYPE_SQRT_TALL";
                break;
            case TYPE_SQRT_VERY_TALL:
                str="TYPE_SQRT_VERY_TALL";
                break;
            case TYPE_ADD:
                str="TYPE_ADD";
                break;
            case TYPE_SUBTRACT:
                str="TYPE_SUBTRACT";
                break;
            case TYPE_PLUS_MINUS:
                str="TYPE_PLUS_MINUS";
                break;
            case TYPE_DOT_MULTIPLY:
                str="TYPE_DOT_MULTIPLY";
                break;
            case TYPE_MULTIPLY:
                str="TYPE_MULTIPLY";
                break;
            case TYPE_DIVIDE:
                str="TYPE_DIVIDE";
                break;
            case TYPE_FORWARD_SLASH:
                str="TYPE_FORWARD_SLASH";
                break;
            case TYPE_BACKWARD_SLASH:
                str="TYPE_BACKWARD_SLASH";
                break;
            case TYPE_EQUAL:
                str="TYPE_EQUAL";
                break;
            case TYPE_EQUAL_ALWAYS:
                str="TYPE_EQUAL_ALWAYS";
                break;
            case TYPE_EQUAL_ROUGHLY:
                str="TYPE_EQUAL_ROUGHLY";
                break;
            case TYPE_LARGER:
                str="TYPE_LARGER";
                break;
            case TYPE_SMALLER:
                str="TYPE_SMALLER";
                break;
            case TYPE_NO_LARGER:
                str="TYPE_NO_LARGER";
                break;
            case TYPE_NO_SMALLER:
                str="TYPE_NO_SMALLER";
                break;
            case TYPE_PERCENT:
                str="TYPE_PERCENT";
                break;
            case TYPE_EXCLAIMATION:
                str="TYPE_EXCLAIMATION";
                break;
            case TYPE_DOT:
                str="TYPE_DOT";
                break;
            case TYPE_STAR:
                str="TYPE_STAR";
                break;
            case TYPE_ROUND_BRACKET:
                str="TYPE_ROUND_BRACKET";
                break;
            case TYPE_CLOSE_ROUND_BRACKET:
                str="TYPE_CLOSE_ROUND_BRACKET";
                break;
            case TYPE_SQUARE_BRACKET:
                str="TYPE_SQUARE_BRACKET";
                break;
            case TYPE_CLOSE_SQUARE_BRACKET:
                str="TYPE_CLOSE_SQUARE_BRACKET";
                break;
            case TYPE_BRACE:
                str="TYPE_BRACE";
                break;
            case TYPE_CLOSE_BRACE:
                str="TYPE_CLOSE_BRACE";
                break;
            case TYPE_VERTICAL_LINE:
                str="TYPE_VERTICAL_LINE";
                break;
            case TYPE_WAVE:
                str="TYPE_WAVE";
                break;
            case TYPE_LEFT_ARROW:
                str="TYPE_LEFT_ARROW";
                break;
            case TYPE_RIGHT_ARROW:
                str="TYPE_RIGHT_ARROW";
                break;
            case TYPE_DOLLAR:
                str="TYPE_DOLLAR";
                break;
            case TYPE_EURO:
                str="TYPE_EURO";
                break;
            case TYPE_YUAN:
                str="TYPE_YUAN";
                break;
            case TYPE_POUND:
                str="TYPE_POUND";
                break;
            case TYPE_CELCIUS:
                str="TYPE_CELCIUS";
                break;
            case TYPE_FAHRENHEIT:
                str="TYPE_FAHRENHEIT";
                break;
            case TYPE_WORD_SIN:
                str="TYPE_WORD_SIN";
                break;
            case TYPE_WORD_COS:
                str="TYPE_WORD_COS";
                break;
            case TYPE_WORD_TAN:
                str="TYPE_WORD_TAN";
                break;
            case TYPE_WORD_LIM:
                str="TYPE_WORD_LIM";
                break;
            case TYPE_WORD_LOG:
                str = "TYPE_WORD_LOG";
                break;
            case TYPE_UNKNOWN:
                str="TYPE_UNKNOWN";
                break;
            default:
                str="TYPE_UNKNOWN";
            }
            return str;
        }

        public String toString()    {
            String str = "";
            switch (mnUnitType)  {
            case TYPE_EMPTY:
                str = "";
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
                str = "\\infinite";
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
                str = "\\alpha";
                break;
            case TYPE_SMALL_BETA:
                str = "\\beta";
                break;
            case TYPE_SMALL_GAMMA:
                str = "\\gamma";
                break;
            case TYPE_SMALL_DELTA:
                str = "\\delta";
                break;
            case TYPE_SMALL_EPSILON:
                str = "\\epsilon";
                break;
            case TYPE_SMALL_ZETA:
                str = "\\zeta";
                break;
            case TYPE_SMALL_ETA:
                str = "\\eta";
                break;
            case TYPE_SMALL_THETA:
                str = "\\theta";
                break;
            case TYPE_SMALL_LAMBDA:
                str = "\\lambda";
                break;
            case TYPE_SMALL_MU:
                str = "\\mu";
                break;
            case TYPE_SMALL_XI:
                str = "\\xi";
                break;
            case TYPE_SMALL_PI:
                str = "\\pi";
                break;
            case TYPE_SMALL_RHO:
                str = "\\rho";
                break;
            case TYPE_SMALL_SIGMA:
                str = "\\sigma";
                break;
            case TYPE_SMALL_TAU:
                str = "\\tau";
                break;
            case TYPE_SMALL_PHI:
                str = "\\phi";
                break;
            case TYPE_SMALL_PSI:
                str = "\\psi";
                break;
            case TYPE_SMALL_OMEGA:
                str = "\\omega";
                break;
            case TYPE_BIG_DELTA:
                str = "\\Delta";
                break;
            case TYPE_BIG_THETA:
                str = "\\Theta";
                break;
            case TYPE_BIG_PI:
                str = "\\Pi";
                break;
            case TYPE_BIG_SIGMA:
                str = "\\Sigma";
                break;
            case TYPE_BIG_PHI:
                str = "\\Phi";
                break;
            case TYPE_BIG_OMEGA:
                str = "\\Omega";
                break;
            case TYPE_INTEGRATE:
                str = "\\integrate";
                break;
            case TYPE_INTEGRATE_CIRCLE:
                str = "\\integcir";
                break;
            case TYPE_SQRT_LEFT:
                str = "\\sqrt";
                break;
            case TYPE_SQRT_SHORT:
                str = "\\sqrt";
                break;
            case TYPE_SQRT_MEDIUM:
                str = "\\sqrt";
                break;
            case TYPE_SQRT_LONG:
                str = "\\sqrt";
                break;
            case TYPE_SQRT_TALL:
                str = "\\sqrt";
                break;
            case TYPE_SQRT_VERY_TALL:
                str = "\\sqrt";
                break;
            case TYPE_ADD:
                str = "+";
                break;
            case TYPE_SUBTRACT:
                str = "-";
                break;
            case TYPE_PLUS_MINUS:
                str = "\\plusminus";
                break;
            case TYPE_DOT_MULTIPLY:
                str = "\\dottimes";
                break;
            case TYPE_MULTIPLY:
                str = "\\times";
                break;
            case TYPE_DIVIDE:
                str = "\\div";
                break;
            case TYPE_FORWARD_SLASH:
                str = "/";
                break;
            case TYPE_BACKWARD_SLASH:
                str = "\\setminus";
                break;
            case TYPE_EQUAL:
                str = "=";
                break;
            case TYPE_EQUAL_ALWAYS:
                str = "\\equalalways";
                break;
            case TYPE_EQUAL_ROUGHLY:
                str = "\\equalrough";
                break;
            case TYPE_LARGER:
                str = "\\larger";
                break;
            case TYPE_SMALLER:
                str = "\\smaller";
                break;
            case TYPE_NO_LARGER:
                str = "\\nolarger";
                break;
            case TYPE_NO_SMALLER:
                str = "\\nosmaller";
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
                str = "\\brace";
                break;
            case TYPE_CLOSE_BRACE:
                str = "\\closebrace";
                break;
            case TYPE_VERTICAL_LINE:
                str = "\\arrowvert";
                break;
            case TYPE_WAVE:
                str = "\\wave";
                break;
            case TYPE_LEFT_ARROW:
                str = "\\leftarrow";
                break;
            case TYPE_RIGHT_ARROW:
                str = "\\rightarrow";
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
                str = "\\unknown";
            }
            return str;
        }

        public static String cvtFontStr2Font(String strInput)  {
            return strInput.trim().toLowerCase(Locale.US);
        }

        public static String cvtTypeEnum2Str(UnitProtoType.Type unitType)   {
            String strType = "UNKNOWN";
            if (unitType == UnitProtoType.Type.TYPE_EMPTY)   {
                strType = "EMPTY";
            } else if (unitType == UnitProtoType.Type.TYPE_ZERO)    {
                strType = "ZERO";
            } else if (unitType == UnitProtoType.Type.TYPE_ONE)    {
                strType = "ONE";
            } else if (unitType == UnitProtoType.Type.TYPE_TWO)    {
                strType = "TWO";
            } else if (unitType == UnitProtoType.Type.TYPE_THREE)    {
                strType = "THREE";
            } else if (unitType == UnitProtoType.Type.TYPE_FOUR)    {
                strType = "FOUR";
            } else if (unitType == UnitProtoType.Type.TYPE_FIVE)    {
                strType = "FIVE";
            } else if (unitType == UnitProtoType.Type.TYPE_SIX)    {
                strType = "SIX";
            } else if (unitType == UnitProtoType.Type.TYPE_SEVEN)    {
                strType = "SEVEN";
            } else if (unitType == UnitProtoType.Type.TYPE_EIGHT)    {
                strType = "EIGHT";
            } else if (unitType == UnitProtoType.Type.TYPE_NINE)    {
                strType = "NINE";
            } else if (unitType == UnitProtoType.Type.TYPE_INFINITE)    {
                strType = "INFINITE";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_A)    {
                strType = "SMALL_A";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_B)    {
                strType = "SMALL_B";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_C)    {
                strType = "SMALL_C";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_D)    {
                strType = "SMALL_D";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_E)    {
                strType = "SMALL_E";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_F)    {
                strType = "SMALL_F";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_G)    {
                strType = "SMALL_G";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_H)    {
                strType = "SMALL_H";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_I)    {
                strType = "SMALL_I";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_I_WITHOUT_DOT)    {
                strType = "SMALL_I_WITHOUT_DOT";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_J)    {
                strType = "SMALL_J";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_J_WITHOUT_DOT)    {
                strType = "SMALL_J_WITHOUT_DOT";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_K)    {
                strType = "SMALL_K";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_L)    {
                strType = "SMALL_L";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_M)    {
                strType = "SMALL_M";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_N)    {
                strType = "SMALL_N";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_O)    {
                strType = "SMALL_O";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_P)    {
                strType = "SMALL_P";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_Q)    {
                strType = "SMALL_Q";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_R)    {
                strType = "SMALL_R";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_S)    {
                strType = "SMALL_S";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_T)    {
                strType = "SMALL_T";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_U)    {
                strType = "SMALL_U";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_V)    {
                strType = "SMALL_V";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_W)    {
                strType = "SMALL_W";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_X)    {
                strType = "SMALL_X";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_Y)    {
                strType = "SMALL_Y";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_Z)    {
                strType = "SMALL_Z";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_A)    {
                strType = "BIG_A";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_B)    {
                strType = "BIG_B";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_C)    {
                strType = "BIG_C";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_D)    {
                strType = "BIG_D";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_E)    {
                strType = "BIG_E";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_F)    {
                strType = "BIG_F";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_G)    {
                strType = "BIG_G";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_H)    {
                strType = "BIG_H";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_I)    {
                strType = "BIG_I";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_J)    {
                strType = "BIG_J";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_K)    {
                strType = "BIG_K";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_L)    {
                strType = "BIG_L";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_M)    {
                strType = "BIG_M";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_N)    {
                strType = "BIG_N";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_O)    {
                strType = "BIG_O";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_P)    {
                strType = "BIG_P";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_Q)    {
                strType = "BIG_Q";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_R)    {
                strType = "BIG_R";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_S)    {
                strType = "BIG_S";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_T)    {
                strType = "BIG_T";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_U)    {
                strType = "BIG_U";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_V)    {
                strType = "BIG_V";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_W)    {
                strType = "BIG_W";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_X)    {
                strType = "BIG_X";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_Y)    {
                strType = "BIG_Y";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_Z)    {
                strType = "BIG_Z";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_ALPHA)    {
                strType = "SMALL_ALPHA";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_BETA)    {
                strType = "SMALL_BETA";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_GAMMA)    {
                strType = "SMALL_GAMMA";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_DELTA)    {
                strType = "SMALL_DELTA";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_EPSILON)    {
                strType = "SMALL_EPSILON";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_ZETA)    {
                strType = "SMALL_ZETA";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_ETA)    {
                strType = "SMALL_ETA";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_THETA)    {
                strType = "SMALL_THETA";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_LAMBDA)    {
                strType = "SMALL_LAMBDA";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_MU)    {
                strType = "SMALL_MU";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_XI)    {
                strType = "SMALL_XI";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_PI)    {
                strType = "SMALL_PI";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_RHO)    {
                strType = "SMALL_RHO";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_SIGMA)    {
                strType = "SMALL_SIGMA";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_TAU)    {
                strType = "SMALL_TAU";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_PHI)    {
                strType = "SMALL_PHI";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_PSI)    {
                strType = "SMALL_PSI";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALL_OMEGA)    {
                strType = "SMALL_OMEGA";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_DELTA)    {
                strType = "BIG_DELTA";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_THETA)    {
                strType = "BIG_THETA";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_PI)    {
                strType = "BIG_PI";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_SIGMA)    {
                strType = "BIG_SIGMA";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_PHI)    {
                strType = "BIG_PHI";
            } else if (unitType == UnitProtoType.Type.TYPE_BIG_OMEGA)    {
                strType = "BIG_OMEGA";
            } else if (unitType == UnitProtoType.Type.TYPE_INTEGRATE)    {
                strType = "INTEGRATE";
            } else if (unitType == UnitProtoType.Type.TYPE_INTEGRATE_CIRCLE)    {
                strType = "INTEGRATE_CIRCLE";
            } else if (unitType == UnitProtoType.Type.TYPE_SQRT_LEFT)    {
                strType = "SQRT_LEFT";
            } else if (unitType == UnitProtoType.Type.TYPE_SQRT_SHORT)    {
                strType = "SQRT_SHORT";
            } else if (unitType == UnitProtoType.Type.TYPE_SQRT_MEDIUM)    {
                strType = "SQRT_MEDIUM";
            } else if (unitType == UnitProtoType.Type.TYPE_SQRT_LONG)    {
                strType = "SQRT_LONG";
            } else if (unitType == UnitProtoType.Type.TYPE_SQRT_TALL)    {
                strType = "SQRT_TALL";
            } else if (unitType == UnitProtoType.Type.TYPE_SQRT_VERY_TALL)    {
                strType = "SQRT_VERY_TALL";
            } else if (unitType == UnitProtoType.Type.TYPE_ADD)    {
                strType = "ADD";
            } else if (unitType == UnitProtoType.Type.TYPE_SUBTRACT)    {
                strType = "SUBTRACT";
            } else if (unitType == UnitProtoType.Type.TYPE_PLUS_MINUS)    {
                strType = "PLUS_MINUS";
            } else if (unitType == UnitProtoType.Type.TYPE_DOT_MULTIPLY)    {
                strType = "DOT_MULTIPLY";
            } else if (unitType == UnitProtoType.Type.TYPE_MULTIPLY)    {
                strType = "MULTIPLY";
            } else if (unitType == UnitProtoType.Type.TYPE_DIVIDE)    {
                strType = "DIVIDE";
            } else if (unitType == UnitProtoType.Type.TYPE_FORWARD_SLASH)    {
                strType = "FORWARD_SLASH";
            } else if (unitType == UnitProtoType.Type.TYPE_BACKWARD_SLASH)    {
                strType = "BACKWARD_SLASH";
            } else if (unitType == UnitProtoType.Type.TYPE_EQUAL)    {
                strType = "EQUAL";
            } else if (unitType == UnitProtoType.Type.TYPE_EQUAL_ALWAYS)    {
                strType = "EQUAL_ALWAYS";
            } else if (unitType == UnitProtoType.Type.TYPE_EQUAL_ROUGHLY)    {
                strType = "EQUAL_ROUGHLY";
            } else if (unitType == UnitProtoType.Type.TYPE_LARGER)    {
                strType = "LARGER";
            } else if (unitType == UnitProtoType.Type.TYPE_SMALLER)    {
                strType = "SMALLER";
            } else if (unitType == UnitProtoType.Type.TYPE_NO_LARGER)    {
                strType = "NO_LARGER";
            } else if (unitType == UnitProtoType.Type.TYPE_NO_SMALLER)    {
                strType = "NO_SMALLER";
            } else if (unitType == UnitProtoType.Type.TYPE_PERCENT)    {
                strType = "PERCENT";
            } else if (unitType == UnitProtoType.Type.TYPE_EXCLAIMATION)    {
                strType = "EXCLAIMATION";
            } else if (unitType == UnitProtoType.Type.TYPE_DOT)    {
                strType = "DOT";
            } else if (unitType == UnitProtoType.Type.TYPE_STAR)    {
                strType = "STAR";
            } else if (unitType == UnitProtoType.Type.TYPE_ROUND_BRACKET)    {
                strType = "ROUND_BRACKET";
            } else if (unitType == UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET)    {
                strType = "CLOSE_ROUND_BRACKET";
            } else if (unitType == UnitProtoType.Type.TYPE_SQUARE_BRACKET)    {
                strType = "SQUARE_BRACKET";
            } else if (unitType == UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET)    {
                strType = "CLOSE_SQUARE_BRACKET";
            } else if (unitType == UnitProtoType.Type.TYPE_BRACE)    {
                strType = "BRACE";
            } else if (unitType == UnitProtoType.Type.TYPE_CLOSE_BRACE)    {
                strType = "CLOSE_BRACE";
            } else if (unitType == UnitProtoType.Type.TYPE_VERTICAL_LINE)    {
                strType = "VERTICAL_LINE";
            } else if (unitType == UnitProtoType.Type.TYPE_WAVE)    {
                strType = "WAVE";
            } else if (unitType == UnitProtoType.Type.TYPE_LEFT_ARROW)    {
                strType = "LEFT_ARROW";
            } else if (unitType == UnitProtoType.Type.TYPE_RIGHT_ARROW)    {
                strType = "RIGHT_ARROW";
            } else if (unitType == UnitProtoType.Type.TYPE_DOLLAR)    {
                strType = "DOLLAR";
            } else if (unitType == UnitProtoType.Type.TYPE_EURO)    {
                strType = "EURO";
            } else if (unitType == UnitProtoType.Type.TYPE_YUAN)    {
                strType = "YUAN";
            } else if (unitType == UnitProtoType.Type.TYPE_POUND)    {
                strType = "POUND";
            } else if (unitType == UnitProtoType.Type.TYPE_CELCIUS)    {
                strType = "CELCIUS";
            } else if (unitType == UnitProtoType.Type.TYPE_FAHRENHEIT)    {
                strType = "FAHRENHEIT";
            } else if (unitType == UnitProtoType.Type.TYPE_WORD_SIN)    {
                strType = "WORD_SIN";
            } else if (unitType == UnitProtoType.Type.TYPE_WORD_COS)    {
                strType = "WORD_COS";
            } else if (unitType == UnitProtoType.Type.TYPE_WORD_TAN)    {
                strType = "WORD_TAN";
            } else if (unitType == UnitProtoType.Type.TYPE_WORD_LIM)    {
                strType = "WORD_LIM";
            } else if (unitType == UnitProtoType.Type.TYPE_WORD_LOG){
                strType = "WORD_LOG";
            }
            return strType;
        }

        public static UnitProtoType.Type getmningTypeValue(String str) {
            UnitProtoType.Type  mn ;
            switch (str)  {
                case "":
                    mn=UnitProtoType.Type.TYPE_EMPTY;
                    break;
                case "0":
                    mn=UnitProtoType.Type.TYPE_ZERO;
                    break;
                case "1":
                    mn=UnitProtoType.Type.TYPE_ONE;
                    break;
                case "2":
                    mn=UnitProtoType.Type.TYPE_TWO;
                    break;
                case "3":
                    mn=UnitProtoType.Type.TYPE_THREE;
                    break;
                case "4":
                    mn=UnitProtoType.Type.TYPE_FOUR;
                    break;
                case "5":
                    mn=UnitProtoType.Type.TYPE_FIVE;
                    break;
                case "6":
                    mn=UnitProtoType.Type.TYPE_SIX;
                    break;
                case "7":
                    mn=UnitProtoType.Type.TYPE_SEVEN;
                    break;
                case "8":
                    mn=UnitProtoType.Type.TYPE_EIGHT;
                    break;
                case "9":
                    mn=UnitProtoType.Type.TYPE_NINE;
                    break;
                case "\\infinite":
                    mn=UnitProtoType.Type.TYPE_INFINITE;
                    break;
                case "a":
                    mn=UnitProtoType.Type.TYPE_SMALL_A;
                    break;
                case "b":
                    mn=UnitProtoType.Type.TYPE_SMALL_B;
                    break;
                case "c":
                    mn=UnitProtoType.Type.TYPE_SMALL_C;
                    break;
                case "d":
                    mn=UnitProtoType.Type.TYPE_SMALL_D;
                    break;
                case "e":
                    mn=UnitProtoType.Type.TYPE_SMALL_E;
                    break;
                case "f":
                    mn=UnitProtoType.Type.TYPE_SMALL_F;
                    break;
                case "g":
                    mn=UnitProtoType.Type.TYPE_SMALL_G;
                    break;
                case "h":
                    mn=UnitProtoType.Type.TYPE_SMALL_H;
                    break;
                case "i":
                    mn=UnitProtoType.Type.TYPE_SMALL_I;
                    break;
//                case "l":
//                    mn=UnitProtoType.Type.TYPE_SMALL_I_WITHOUT_DOT;
//                    break;
                case "j":
                    mn=UnitProtoType.Type.TYPE_SMALL_J;
                    break;
//                case "J":
//                    mn=UnitProtoType.Type.TYPE_SMALL_J_WITHOUT_DOT;
//                    break;
                case "k":
                    mn=UnitProtoType.Type.TYPE_SMALL_K;
                    break;
                case "l":
                    mn=UnitProtoType.Type.TYPE_SMALL_L;
                    break;
                case "m":
                    mn=UnitProtoType.Type.TYPE_SMALL_M;
                    break;
                case "n":
                    mn=UnitProtoType.Type.TYPE_SMALL_N;
                    break;
                case "o":
                    mn=UnitProtoType.Type.TYPE_SMALL_O;
                    break;
                case "p":
                    mn=UnitProtoType.Type.TYPE_SMALL_P;
                    break;
                case "q":
                    mn=UnitProtoType.Type.TYPE_SMALL_Q;
                    break;
                case "r":
                    mn=UnitProtoType.Type.TYPE_SMALL_R;
                    break;
                case "s":
                    mn=UnitProtoType.Type.TYPE_SMALL_S;
                    break;
                case "t":
                    mn=UnitProtoType.Type.TYPE_SMALL_T;
                    break;
                case "u":
                    mn=UnitProtoType.Type.TYPE_SMALL_U;
                    break;
                case "v":
                    mn=UnitProtoType.Type.TYPE_SMALL_V;
                    break;
                case "w":
                    mn=UnitProtoType.Type.TYPE_SMALL_W;
                    break;
                case "x":
                    mn=UnitProtoType.Type.TYPE_SMALL_X;
                    break;
                case "y":
                    mn=UnitProtoType.Type.TYPE_SMALL_Y;
                    break;
                case "z":
                    mn=UnitProtoType.Type.TYPE_SMALL_Z;
                    break;
                case "A":
                    mn=UnitProtoType.Type.TYPE_BIG_A;
                    break;
                case "B":
                    mn=UnitProtoType.Type.TYPE_BIG_B;
                    break;
                case "C":
                    mn=UnitProtoType.Type.TYPE_BIG_C;
                    break;
                case "D":
                    mn=UnitProtoType.Type.TYPE_BIG_D;
                    break;
                case "E":
                    mn=UnitProtoType.Type.TYPE_BIG_E;
                    break;
                case "F":
                    mn=UnitProtoType.Type.TYPE_BIG_F;
                    break;
                case "G":
                    mn=UnitProtoType.Type.TYPE_BIG_G;
                    break;
                case "H":
                    mn=UnitProtoType.Type.TYPE_BIG_H;
                    break;
                case "I":
                    mn=UnitProtoType.Type.TYPE_BIG_I;
                    break;
                case "J":
                    mn=UnitProtoType.Type.TYPE_BIG_J;
                    break;
                case "K":
                    mn=UnitProtoType.Type.TYPE_BIG_K;
                    break;
                case "L":
                    mn=UnitProtoType.Type.TYPE_BIG_L;
                    break;
                case "M":
                    mn=UnitProtoType.Type.TYPE_BIG_M;
                    break;
                case "N":
                    mn=UnitProtoType.Type.TYPE_BIG_N;
                    break;
                case "O":
                    mn=UnitProtoType.Type.TYPE_BIG_O;
                    break;
                case "P":
                    mn=UnitProtoType.Type.TYPE_BIG_P;
                    break;
                case "Q":
                    mn=UnitProtoType.Type.TYPE_BIG_Q;
                    break;
                case "R":
                    mn=UnitProtoType.Type.TYPE_BIG_R;
                    break;
                case "S":
                    mn=UnitProtoType.Type.TYPE_BIG_S;
                    break;
                case "T":
                    mn=UnitProtoType.Type.TYPE_BIG_T;
                    break;
                case "U":
                    mn=UnitProtoType.Type.TYPE_BIG_U;
                    break;
                case "V":
                    mn=UnitProtoType.Type.TYPE_BIG_V;
                    break;
                case "W":
                    mn=UnitProtoType.Type.TYPE_BIG_W;
                    break;
                case "X":
                    mn=UnitProtoType.Type.TYPE_DOT_MULTIPLY;
                    break;
                case "Y":
                    mn=UnitProtoType.Type.TYPE_BIG_Y;
                    break;
                case "Z":
                    mn=UnitProtoType.Type.TYPE_BIG_Z;
                    break;
                case "\\alpha":
                    mn=UnitProtoType.Type.TYPE_SMALL_ALPHA;
                    break;
                case "\\beta":
                    mn=UnitProtoType.Type.TYPE_SMALL_BETA;
                    break;
                case "\\gamma":
                    mn=UnitProtoType.Type.TYPE_SMALL_GAMMA;   // use bookman old style black font 72
                    break;
                case "\\delta":
                    mn=UnitProtoType.Type.TYPE_SMALL_DELTA;
                    break;
                case "\\epsilon":
                    mn=UnitProtoType.Type.TYPE_SMALL_EPSILON;
                    break;
                case "\\zeta":
                    mn=UnitProtoType.Type.TYPE_SMALL_ZETA;
                    break;
                case "\\eta":
                    mn=UnitProtoType.Type.TYPE_SMALL_ETA;
                    break;
                case "\\theta":
                    mn=UnitProtoType.Type.TYPE_SMALL_THETA;
                    break;
                case "\\lambda":
                    mn=UnitProtoType.Type.TYPE_SMALL_LAMBDA;
                    break;
                case "\\mu":
                    mn=UnitProtoType.Type.TYPE_SMALL_MU;
                    break;
                case "\\xi":
                    mn=UnitProtoType.Type.TYPE_SMALL_XI;
                    break;
                case "\\pi":
                    mn=UnitProtoType.Type.TYPE_SMALL_PI;
                    break;
                case "\\rho":
                    mn=UnitProtoType.Type.TYPE_SMALL_RHO;
                    break;
                case "\\sigma":
                    mn=UnitProtoType.Type.TYPE_SMALL_SIGMA;
                    break;
                case "\\tau":
                    mn=UnitProtoType.Type.TYPE_SMALL_TAU; // use bookman old style black font 72
                    break;
                case "\\phi":
                    mn=UnitProtoType.Type.TYPE_SMALL_PHI;
                    break;
                case "\\psi":
                    mn=UnitProtoType.Type.TYPE_SMALL_PSI;
                    break;
                case "\\omega":
                    mn=UnitProtoType.Type.TYPE_SMALL_OMEGA;
                    break;
                case "\\Delta":
                    mn=UnitProtoType.Type.TYPE_BIG_DELTA;
                    break;
                case "\\Theta":
                    mn=UnitProtoType.Type.TYPE_BIG_THETA;
                    break;
                case "\\Pi":
                    mn=UnitProtoType.Type.TYPE_BIG_PI;
                    break;
                case "\\Sigma":
                    mn=UnitProtoType.Type.TYPE_BIG_SIGMA;
                    break;
                case "\\Phi":
                    mn=UnitProtoType.Type.TYPE_BIG_PHI;   // use bookman old style black font 72
                    break;
                case "\\Omega":
                    mn=UnitProtoType.Type.TYPE_BIG_OMEGA;
                    break;
                case "\\integrate":
                    mn=UnitProtoType.Type.TYPE_INTEGRATE;
                    break;
                case "\\integcir":
                    mn=UnitProtoType.Type.TYPE_INTEGRATE_CIRCLE;
                    break;
//                case TYPE_SQRT_LEFT:
//                    mn=UnitProtoType.Type.TYPE_SQRT_LEFT;
//                    break;
//                case TYPE_SQRT_SHORT:
//                    mn=UnitProtoType.Type.TYPE_SQRT_SHORT;
//                    break;
//                case TYPE_SQRT_MEDIUM:
//                    mn=UnitProtoType.Type.TYPE_SQRT_MEDIUM;
//                    break;
//                case TYPE_SQRT_LONG:
//                    mn=UnitProtoType.Type.TYPE_SQRT_LONG;
//                    break;
//                case TYPE_SQRT_TALL:
//                    mn=UnitProtoType.Type.TYPE_SQRT_TALL;
//                    break;
                case "\\sqrt":
                    mn=UnitProtoType.Type.TYPE_SQRT_VERY_TALL;
                    break;
                case "+":
                    mn=UnitProtoType.Type.TYPE_ADD;
                    break;
                case "-":
                    mn=UnitProtoType.Type.TYPE_SUBTRACT;
                    break;
                case "\\plusminus":
                    mn=UnitProtoType.Type.TYPE_PLUS_MINUS;
                    break;
                case "\\dottimes":
                    mn=UnitProtoType.Type.TYPE_DOT_MULTIPLY;
                    break;
                    //todo 1_final_change by dml TYPE_DOT_MULTIPLY -> TYPE_MULTIPLY
                case "\\times":
                    mn=UnitProtoType.Type.TYPE_MULTIPLY;
                    break;
                case "\\div":
                    mn=UnitProtoType.Type.TYPE_DIVIDE;
                    break;
                case "/":
                    mn=UnitProtoType.Type.TYPE_FORWARD_SLASH;
                    break;
                case "\\setminus":
                    mn=UnitProtoType.Type.TYPE_BACKWARD_SLASH;
                    break;
                case "=":
                    mn=UnitProtoType.Type.TYPE_EQUAL;
                    break;
                case "\\equalalways":
                    mn=UnitProtoType.Type.TYPE_EQUAL_ALWAYS;
                    break;
                case "\\equalrough":
                    mn=UnitProtoType.Type.TYPE_EQUAL_ROUGHLY;
                    break;
                case "\\larger":
                    mn=UnitProtoType.Type.TYPE_LARGER;
                    break;
                case "\\smaller":
                    mn=UnitProtoType.Type.TYPE_SMALLER;
                    break;
                case "\\nolarger":
                    mn=UnitProtoType.Type.TYPE_NO_LARGER;
                    break;
                case "\\nosmaller":
                    mn=UnitProtoType.Type.TYPE_NO_SMALLER;
                    break;
                case "%":
                    mn=UnitProtoType.Type.TYPE_PERCENT;
                    break;
                case "!":
                    mn=UnitProtoType.Type.TYPE_EXCLAIMATION;
                    break;
                case ".":
                    mn=UnitProtoType.Type.TYPE_DOT;
                    break;
                case "*":
                    mn=UnitProtoType.Type.TYPE_STAR;
                    break;
                case "(":
                    mn=UnitProtoType.Type.TYPE_ROUND_BRACKET;
                    break;
                case ")":
                    mn=UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET;
                    break;
                case "[":
                    mn=UnitProtoType.Type.TYPE_SQUARE_BRACKET;
                    break;
                case "]":
                    mn=UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET;
                    break;
                case "\\brace":
                    mn=UnitProtoType.Type.TYPE_BRACE;
                    break;
                case "\\closebrace":
                    mn=UnitProtoType.Type.TYPE_CLOSE_BRACE;
                    break;
                case "\\arrowvert":
                    mn=UnitProtoType.Type.TYPE_VERTICAL_LINE;
                    break;
                case "\\wave":
                    mn=UnitProtoType.Type.TYPE_WAVE;
                    break;
                case "\\leftarrow":
                    mn=UnitProtoType.Type.TYPE_LEFT_ARROW;
                    break;
                case "\\rightarrow":
                    mn=UnitProtoType.Type.TYPE_RIGHT_ARROW;
                    break;
                case "$":
                    mn=UnitProtoType.Type.TYPE_DOLLAR;
                    break;
                case "\\euro":
                    mn=UnitProtoType.Type.TYPE_EURO;
                    break;
                case "\\yuan":
                    mn=UnitProtoType.Type.TYPE_YUAN;
                    break;
                case "\\pound":
                    mn=UnitProtoType.Type.TYPE_POUND;
                    break;
                case "\\celcius":
                    mn=UnitProtoType.Type.TYPE_CELCIUS;
                    break;
                case "\\fahrenheit":
                    mn=UnitProtoType.Type.TYPE_FAHRENHEIT;
                    break;
                case "sin":
                    mn=UnitProtoType.Type.TYPE_WORD_SIN;
                    break;
                case "cos":
                    mn=UnitProtoType.Type.TYPE_WORD_COS;
                    break;
                case "tan":
                    mn=UnitProtoType.Type.TYPE_WORD_TAN;
                    break;
                case "lim":
                    mn=UnitProtoType.Type.TYPE_WORD_LIM;
                    break;
                case "log":
                    mn= UnitProtoType.Type.TYPE_WORD_LOG;
                    break;
                case "\\unknown":
                    mn=UnitProtoType.Type.TYPE_UNKNOWN;
                    break;
                default:
                    mn=UnitProtoType.Type.TYPE_UNKNOWN;
            }
            return mn;
        }




        public static UnitProtoType.Type cvtTypeStr2Enum(String strTypeCharCand)   {
            String strUpperCase = strTypeCharCand.trim().toUpperCase(Locale.US);
            UnitProtoType.Type unitType = UnitProtoType.Type.TYPE_UNKNOWN;
            if (strUpperCase.equals("EMPTY"))   {
                unitType = UnitProtoType.Type.TYPE_EMPTY;
            } else if (strUpperCase.equals("ZERO"))    {
                unitType = UnitProtoType.Type.TYPE_ZERO;
            } else if (strUpperCase.equals("ONE"))    {
                unitType = UnitProtoType.Type.TYPE_ONE;
            } else if (strUpperCase.equals("TWO"))    {
                unitType = UnitProtoType.Type.TYPE_TWO;
            } else if (strUpperCase.equals("THREE"))    {
                unitType = UnitProtoType.Type.TYPE_THREE;
            } else if (strUpperCase.equals("FOUR"))    {
                unitType = UnitProtoType.Type.TYPE_FOUR;
            } else if (strUpperCase.equals("FIVE"))    {
                unitType = UnitProtoType.Type.TYPE_FIVE;
            } else if (strUpperCase.equals("SIX"))    {
                unitType = UnitProtoType.Type.TYPE_SIX;
            } else if (strUpperCase.equals("SEVEN"))    {
                unitType = UnitProtoType.Type.TYPE_SEVEN;
            } else if (strUpperCase.equals("EIGHT"))    {
                unitType = UnitProtoType.Type.TYPE_EIGHT;
            } else if (strUpperCase.equals("NINE"))    {
                unitType = UnitProtoType.Type.TYPE_NINE;
            } else if (strUpperCase.equals("INFINITE"))    {
                unitType = UnitProtoType.Type.TYPE_INFINITE;
            } else if (strUpperCase.equals("SMALL_A"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_A;
            } else if (strUpperCase.equals("SMALL_B"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_B;
            } else if (strUpperCase.equals("SMALL_C"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_C;
            } else if (strUpperCase.equals("SMALL_D"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_D;
            } else if (strUpperCase.equals("SMALL_E"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_E;
            } else if (strUpperCase.equals("SMALL_F"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_F;
            } else if (strUpperCase.equals("SMALL_G"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_G;
            } else if (strUpperCase.equals("SMALL_H"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_H;
            } else if (strUpperCase.equals("SMALL_I"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_I;
            } else if (strUpperCase.equals("SMALL_I_WITHOUT_DOT"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_I_WITHOUT_DOT;
            } else if (strUpperCase.equals("SMALL_J"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_J;
            } else if (strUpperCase.equals("SMALL_J_WITHOUT_DOT"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_J_WITHOUT_DOT;
            } else if (strUpperCase.equals("SMALL_K"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_K;
            } else if (strUpperCase.equals("SMALL_L"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_L;
            } else if (strUpperCase.equals("SMALL_M"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_M;
            } else if (strUpperCase.equals("SMALL_N"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_N;
            } else if (strUpperCase.equals("SMALL_O"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_O;
            } else if (strUpperCase.equals("SMALL_P"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_P;
            } else if (strUpperCase.equals("SMALL_Q"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_Q;
            } else if (strUpperCase.equals("SMALL_R"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_R;
            } else if (strUpperCase.equals("SMALL_S"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_S;
            } else if (strUpperCase.equals("SMALL_T"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_T;
            } else if (strUpperCase.equals("SMALL_U"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_U;
            } else if (strUpperCase.equals("SMALL_V"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_V;
            } else if (strUpperCase.equals("SMALL_W"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_W;
            } else if (strUpperCase.equals("SMALL_X"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_X;
            } else if (strUpperCase.equals("SMALL_Y"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_Y;
            } else if (strUpperCase.equals("SMALL_Z"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_Z;
            } else if (strUpperCase.equals("BIG_A"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_A;
            } else if (strUpperCase.equals("BIG_B"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_B;
            } else if (strUpperCase.equals("BIG_C"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_C;
            } else if (strUpperCase.equals("BIG_D"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_D;
            } else if (strUpperCase.equals("BIG_E"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_E;
            } else if (strUpperCase.equals("BIG_F"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_F;
            } else if (strUpperCase.equals("BIG_G"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_G;
            } else if (strUpperCase.equals("BIG_H"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_H;
            } else if (strUpperCase.equals("BIG_I"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_I;
            } else if (strUpperCase.equals("BIG_J"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_J;
            } else if (strUpperCase.equals("BIG_K"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_K;
            } else if (strUpperCase.equals("BIG_L"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_L;
            } else if (strUpperCase.equals("BIG_M"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_M;
            } else if (strUpperCase.equals("BIG_N"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_N;
            } else if (strUpperCase.equals("BIG_O"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_O;
            } else if (strUpperCase.equals("BIG_P"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_P;
            } else if (strUpperCase.equals("BIG_Q"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_Q;
            } else if (strUpperCase.equals("BIG_R"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_R;
            } else if (strUpperCase.equals("BIG_S"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_S;
            } else if (strUpperCase.equals("BIG_T"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_T;
            } else if (strUpperCase.equals("BIG_U"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_U;
            } else if (strUpperCase.equals("BIG_V"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_V;
            } else if (strUpperCase.equals("BIG_W"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_W;
            } else if (strUpperCase.equals("BIG_X"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_X;
            } else if (strUpperCase.equals("BIG_Y"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_Y;
            } else if (strUpperCase.equals("BIG_Z"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_Z;
            } else if (strUpperCase.equals("SMALL_ALPHA"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_ALPHA;
            } else if (strUpperCase.equals("SMALL_BETA"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_BETA;
            } else if (strUpperCase.equals("SMALL_GAMMA"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_GAMMA;
            } else if (strUpperCase.equals("SMALL_DELTA"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_DELTA;
            } else if (strUpperCase.equals("SMALL_EPSILON"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_EPSILON;
            } else if (strUpperCase.equals("SMALL_ZETA"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_ZETA;
            } else if (strUpperCase.equals("SMALL_ETA"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_ETA;
            } else if (strUpperCase.equals("SMALL_THETA"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_THETA;
            } else if (strUpperCase.equals("SMALL_LAMBDA"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_LAMBDA;
            } else if (strUpperCase.equals("SMALL_MU"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_MU;
            } else if (strUpperCase.equals("SMALL_XI"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_XI;
            } else if (strUpperCase.equals("SMALL_PI"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_PI;
            } else if (strUpperCase.equals("SMALL_RHO"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_RHO;
            } else if (strUpperCase.equals("SMALL_SIGMA"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_SIGMA;
            } else if (strUpperCase.equals("SMALL_TAU"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_TAU;
            } else if (strUpperCase.equals("SMALL_PHI"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_PHI;
            } else if (strUpperCase.equals("SMALL_PSI"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_PSI;
            } else if (strUpperCase.equals("SMALL_OMEGA"))    {
                unitType = UnitProtoType.Type.TYPE_SMALL_OMEGA;
            } else if (strUpperCase.equals("BIG_DELTA"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_DELTA;
            } else if (strUpperCase.equals("BIG_THETA"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_THETA;
            } else if (strUpperCase.equals("BIG_PI"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_PI;
            } else if (strUpperCase.equals("BIG_SIGMA"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_SIGMA;
            } else if (strUpperCase.equals("BIG_PHI"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_PHI;
            } else if (strUpperCase.equals("BIG_OMEGA"))    {
                unitType = UnitProtoType.Type.TYPE_BIG_OMEGA;
            } else if (strUpperCase.equals("INTEGRATE"))    {
                unitType = UnitProtoType.Type.TYPE_INTEGRATE;
            } else if (strUpperCase.equals("INTEGRATE_CIRCLE"))    {
                unitType = UnitProtoType.Type.TYPE_INTEGRATE_CIRCLE;
            } else if (strUpperCase.equals("SQRT_LEFT"))    {
                unitType = UnitProtoType.Type.TYPE_SQRT_LEFT;
            } else if (strUpperCase.equals("SQRT_SHORT"))    {
                unitType = UnitProtoType.Type.TYPE_SQRT_SHORT;
            } else if (strUpperCase.equals("SQRT_MEDIUM"))    {
                unitType = UnitProtoType.Type.TYPE_SQRT_MEDIUM;
            } else if (strUpperCase.equals("SQRT_LONG"))    {
                unitType = UnitProtoType.Type.TYPE_SQRT_LONG;
            } else if (strUpperCase.equals("SQRT_TALL"))    {
                unitType = UnitProtoType.Type.TYPE_SQRT_TALL;
            } else if (strUpperCase.equals("SQRT_VERY_TALL"))    {
                unitType = UnitProtoType.Type.TYPE_SQRT_VERY_TALL;
            } else if (strUpperCase.equals("ADD"))    {
                unitType = UnitProtoType.Type.TYPE_ADD;
            } else if (strUpperCase.equals("SUBTRACT"))    {
                unitType = UnitProtoType.Type.TYPE_SUBTRACT;
            } else if (strUpperCase.equals("PLUS_MINUS"))    {
                unitType = UnitProtoType.Type.TYPE_PLUS_MINUS;
            } else if (strUpperCase.equals("DOT_MULTIPLY"))    {
                unitType = UnitProtoType.Type.TYPE_DOT_MULTIPLY;
            } else if (strUpperCase.equals("MULTIPLY"))    {
                unitType = UnitProtoType.Type.TYPE_MULTIPLY;
            } else if (strUpperCase.equals("DIVIDE"))    {
                unitType = UnitProtoType.Type.TYPE_DIVIDE;
            } else if (strUpperCase.equals("FORWARD_SLASH"))    {
                unitType = UnitProtoType.Type.TYPE_FORWARD_SLASH;
            } else if (strUpperCase.equals("BACKWARD_SLASH"))    {
                unitType = UnitProtoType.Type.TYPE_BACKWARD_SLASH;
            } else if (strUpperCase.equals("EQUAL"))    {
                unitType = UnitProtoType.Type.TYPE_EQUAL;
            } else if (strUpperCase.equals("EQUAL_ALWAYS"))    {
                unitType = UnitProtoType.Type.TYPE_EQUAL_ALWAYS;
            } else if (strUpperCase.equals("EQUAL_ROUGHLY"))    {
                unitType = UnitProtoType.Type.TYPE_EQUAL_ROUGHLY;
            } else if (strUpperCase.equals("LARGER"))    {
                unitType = UnitProtoType.Type.TYPE_LARGER;
            } else if (strUpperCase.equals("SMALLER"))    {
                unitType = UnitProtoType.Type.TYPE_SMALLER;
            } else if (strUpperCase.equals("NO_LARGER"))    {
                unitType = UnitProtoType.Type.TYPE_NO_LARGER;
            } else if (strUpperCase.equals("NO_SMALLER"))    {
                unitType = UnitProtoType.Type.TYPE_NO_SMALLER;
            } else if (strUpperCase.equals("PERCENT"))    {
                unitType = UnitProtoType.Type.TYPE_PERCENT;
            } else if (strUpperCase.equals("EXCLAIMATION"))    {
                unitType = UnitProtoType.Type.TYPE_EXCLAIMATION;
            } else if (strUpperCase.equals("DOT"))    {
                unitType = UnitProtoType.Type.TYPE_DOT;
            } else if (strUpperCase.equals("STAR"))    {
                unitType = UnitProtoType.Type.TYPE_STAR;
            } else if (strUpperCase.equals("ROUND_BRACKET"))    {
                unitType = UnitProtoType.Type.TYPE_ROUND_BRACKET;
            } else if (strUpperCase.equals("CLOSE_ROUND_BRACKET"))    {
                unitType = UnitProtoType.Type.TYPE_CLOSE_ROUND_BRACKET;
            } else if (strUpperCase.equals("SQUARE_BRACKET"))    {
                unitType = UnitProtoType.Type.TYPE_SQUARE_BRACKET;
            } else if (strUpperCase.equals("CLOSE_SQUARE_BRACKET"))    {
                unitType = UnitProtoType.Type.TYPE_CLOSE_SQUARE_BRACKET;
            } else if (strUpperCase.equals("BRACE"))    {
                unitType = UnitProtoType.Type.TYPE_BRACE;
            } else if (strUpperCase.equals("CLOSE_BRACE"))    {
                unitType = UnitProtoType.Type.TYPE_CLOSE_BRACE;
            } else if (strUpperCase.equals("VERTICAL_LINE"))    {
                unitType = UnitProtoType.Type.TYPE_VERTICAL_LINE;
            } else if (strUpperCase.equals("WAVE"))    {
                unitType = UnitProtoType.Type.TYPE_WAVE;
            } else if (strUpperCase.equals("LEFT_ARROW"))    {
                unitType = UnitProtoType.Type.TYPE_LEFT_ARROW;
            } else if (strUpperCase.equals("RIGHT_ARROW"))    {
                unitType = UnitProtoType.Type.TYPE_RIGHT_ARROW;
            } else if (strUpperCase.equals("DOLLAR"))    {
                unitType = UnitProtoType.Type.TYPE_DOLLAR;
            } else if (strUpperCase.equals("EURO"))    {
                unitType = UnitProtoType.Type.TYPE_EURO;
            } else if (strUpperCase.equals("YUAN"))    {
                unitType = UnitProtoType.Type.TYPE_YUAN;
            } else if (strUpperCase.equals("POUND"))    {
                unitType = UnitProtoType.Type.TYPE_POUND;
            } else if (strUpperCase.equals("CELCIUS"))    {
                unitType = UnitProtoType.Type.TYPE_CELCIUS;
            } else if (strUpperCase.equals("FAHRENHEIT"))    {
                unitType = UnitProtoType.Type.TYPE_FAHRENHEIT;
            } else if (strUpperCase.equals("WORD_SIN"))    {
                unitType = UnitProtoType.Type.TYPE_WORD_SIN;
            } else if (strUpperCase.equals("WORD_COS"))    {
                unitType = UnitProtoType.Type.TYPE_WORD_COS;
            } else if (strUpperCase.equals("WORD_TAN"))    {
                unitType = UnitProtoType.Type.TYPE_WORD_TAN;
            } else if (strUpperCase.equals("WORD_LIM"))    {
                unitType = UnitProtoType.Type.TYPE_WORD_LIM;
            } else if (strUpperCase.equals("WORD_LOG")){
                unitType = UnitProtoType.Type.TYPE_WORD_LOG;
            }
            return unitType;
        }
    }
    
    public static final int NORMAL_UPT_LIST = 0;
    public static final int HEXTENDABLE_UPT_LIST = 1;
    public static final int VEXTENDABLE_UPT_LIST = 2;
    public static final int WORD_UPT_LIST = 3;  // words which include very close characters like lim, cos, tan, sin etc.
    
    public LinkedList<UnitProtoType> mlistUnitPrototypes = new LinkedList<UnitProtoType>();
    public LinkedList<UnitProtoType> mlistHExtendableUPTs = new LinkedList<UnitProtoType>();
    public LinkedList<UnitProtoType> mlistVExtendableUPTs = new LinkedList<UnitProtoType>();
    public LinkedList<UnitProtoType> mlistWordUPTs = new LinkedList<UnitProtoType>();
    
    public boolean isEmpty()   {
        return (mlistUnitPrototypes.size() == 0);
    }
    
    public LinkedList<UnitProtoType> findUnitPrototype(UnitProtoType.Type unitPType, int nFromList)    {
        LinkedList<UnitProtoType> listFoundUPTs = new LinkedList<UnitProtoType>();
        LinkedList<UnitProtoType> listFrom = mlistUnitPrototypes;
        if (nFromList == HEXTENDABLE_UPT_LIST) {
            listFrom = mlistHExtendableUPTs;
        } else if (nFromList == VEXTENDABLE_UPT_LIST)  {
            listFrom = mlistVExtendableUPTs;
        } else if (nFromList == WORD_UPT_LIST)  {
            listFrom = mlistWordUPTs;
        }
        for (UnitProtoType upt : listFrom)   {
            if (upt.mnUnitType == unitPType)    {
                listFoundUPTs.add(upt);
            }
        }
        return listFoundUPTs;    // cannot find.
    }
    
    public LinkedList<UnitProtoType> findUnitPrototype(String strFont, int nFromList)    {
        LinkedList<UnitProtoType> listFoundUPTs = new LinkedList<UnitProtoType>();
        LinkedList<UnitProtoType> listFrom = mlistUnitPrototypes;
        if (nFromList == HEXTENDABLE_UPT_LIST) {
            listFrom = mlistHExtendableUPTs;
        } else if (nFromList == VEXTENDABLE_UPT_LIST)  {
            listFrom = mlistVExtendableUPTs;
        } else if (nFromList == WORD_UPT_LIST)  {
            listFrom = mlistWordUPTs;
        }
        for (UnitProtoType upt : listFrom)   {
            if (upt.mstrFont.equalsIgnoreCase(strFont))    {
                listFoundUPTs.add(upt);
            }
        }
        return listFoundUPTs;    // cannot find.
    }
    
    public void addUnitPrototype(UnitProtoType.Type unitPType, String strFont, double dWMinNumStrokes, double dHMinNumStrokes, byte[][] biMatrix, int nFromList)    {
        // do not do any duplication check.
        UnitProtoType upt2Add = new UnitProtoType();
        upt2Add.mnUnitType = unitPType;
        upt2Add.mstrFont = strFont;
        upt2Add.mcharUnit = new CharUnit(biMatrix);
        upt2Add.mdWMinNumStrokes = dWMinNumStrokes;
        upt2Add.mdHMinNumStrokes = dHMinNumStrokes;
        if (nFromList == VEXTENDABLE_UPT_LIST) {
            mlistVExtendableUPTs.add(upt2Add);
        } else if (nFromList == HEXTENDABLE_UPT_LIST)  {
            mlistHExtendableUPTs.add(upt2Add);
        } else if (nFromList == WORD_UPT_LIST)  {
            mlistWordUPTs.add(upt2Add);
        } else  {
            mlistUnitPrototypes.add(upt2Add);
        }
    }
    
    public void addUnitPrototype(UnitProtoType.Type unitPType, byte[][] biMatrix, int nFromList)    {
        // do not do any duplication check.
        UnitProtoType upt2Add = new UnitProtoType();
        upt2Add.mnUnitType = unitPType;
        upt2Add.mstrFont = "default";
        upt2Add.mdWMinNumStrokes = 0;
        upt2Add.mdHMinNumStrokes = 0;

        upt2Add.mcharUnit = new CharUnit(biMatrix);
        if (nFromList == VEXTENDABLE_UPT_LIST) {
            mlistVExtendableUPTs.add(upt2Add);
        } else if (nFromList == HEXTENDABLE_UPT_LIST)  {
            mlistHExtendableUPTs.add(upt2Add);
        } else if (nFromList == WORD_UPT_LIST)  {
            mlistWordUPTs.add(upt2Add);
        } else  {
            mlistUnitPrototypes.add(upt2Add);
        }
    }
    
    public void addUnitPrototype(UnitProtoType upt2Add, int nFromList)    {
        // do not do any duplication check.
        if (nFromList == VEXTENDABLE_UPT_LIST) {
            mlistVExtendableUPTs.add(upt2Add);
        } else if (nFromList == HEXTENDABLE_UPT_LIST)  {
            mlistHExtendableUPTs.add(upt2Add);
        } else if (nFromList == WORD_UPT_LIST)  {
            mlistWordUPTs.add(upt2Add);
        } else  {
            mlistUnitPrototypes.add(upt2Add);
        }
    }
    
    public void clear() {
        mlistUnitPrototypes.clear();
        mlistHExtendableUPTs.clear();
        mlistVExtendableUPTs.clear();
    }




}
