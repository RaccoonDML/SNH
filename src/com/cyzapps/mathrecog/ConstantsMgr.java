/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyzapps.mathrecog;

import com.cyzapps.mathrecog.UnitRecognizer.UnitCandidate;

/**
 *
 * @author tonyc
 */
public class ConstantsMgr {
    
    // based on common sense.
    public static double msdEquationProjHOnCntsRatio = 0.7;  // project the equation to a horizontal line, what is the min ratio of on counts to total.
                                                                // assuming the gap between characters should be half of character average width.
    public static double msdEqAEqOverlappedHLnWidthRatio = 0.75;	// the ratio of h-line overlapped line / total width in equal or always equal.
    public static double msdEqMinWidthOverHeight = 2;
    public static double msdAEqMinWidthOverHeight = 1;
    public static double msdSmallJDotHPlaceThresh = 0.3; // the gap between the main body and the dot of small j should be at most 0.3 of main body height
    public static double msdSmallJDotVPlaceThresh = 0.1; // the gap between the main body and the dot of small j should be at most 0.1 of main body width
    public static double msdDivDotHPlaceThresh = 0.2;    // dot should be at least 0.4 of width from Left (and from right, top and bottom).
    public static double msdDivDotVPlaceThresh = 0.1;    // dot should be at least 0.4 of width from Left (and from right, top and bottom).
    public static double msdDivDotMaxSize = 1.5;    // 1.5 times divide line hight is the maximum divide dot size
    public static double msdItalianIntegrateLowerNoteMostLeft = 0.3;    // if integrate is italian, left of lower note can be at most 0.3 width of integrate from left
    public static double msdItalianIntegrateLowerNoteMostRight = 0.6;   // if integrate is italian, right of lower note can be at most 0.6 width of integrate from left
    public static double msdMaxHorizontalSlope = 0.03;	// user should ensure that slope is less than 0.03.
    public static double msdDisconnectPoss2Width = 0.005;  // clearly, if very long, we may see some small disconnects which should be ignored.

    public static double msdHeightSkewRatio = 0.06;	// larger height implies that h-cut's width can be shorter compared to chop width.
    public static double msdMaxHLnThickness = 0.14;   // max line div thinkness is calculated by avg stroke width + 0.14 * width. 0.14 = sqrt(3)/2 - 1
    public static double msdCapUnderHeightRatio2Base = 0.7;  // height of cap or under cannot be more than 0.7 height of its base.
    public static double msdLnDivAvgCharHeightTop2BtmMin = 0.4;    // avg char height of top to bottom (or bottom to top) should be no less than 0.4.
    public static double msdHLnDivMinWidthHandwriting = 0.8;  // in handwriting, h-div width should be at least 0.8 * total width.
    public static double msdHLnDivMaxDistanceToTopUnder = 1.0;   // max distance between above and Ln div and between below and Ln Div should be 0.6 * above / below height
    public static double msdPlusHeightWidthRatio = 0.7; // +'s height : +'s width should be between 1/0.7 and 0.7.
    public static double msdPlusTopVLnBtmVLnRatio = 0.85;   // +'s top vline : +'s btm vline should be between 1/0.85 and 0.85.
    
    public static double msdClusterChopMaxGap = 0.15;   // in a cluster, gap between characters should be at most 0.15 of character height.
    public static double msdWordCharMaxGap = 0.30;  // in a word, character gap should not be too wide.
    public static double msdNoteBaseMaxGap = 1.0;   // upper/lower note's gap to base should be at most 1.0 * note average height.
    public static double msdClusterChopMinSmallerOverlap = 0.9;    // in a cluster, if there is a height overlap, should be at least 0.9 * smaller char height
    public static double msdClusterChopMinLargerOverlap = 0.25;    // in a cluster, if there is a height overlap, should be at least 0.25 * larger char height
    public static double msdClusterHCutsMinOverlap = 0.7;   // if a cluster is hblankcut, the overlap between top and bottom should be at least 0.7 * max width
    public static double msdClusterChopWOverHMin = 0.75;    // w/h of a cluster chop (e.g. lim) should be from 0.75 to 1.5
    public static double msdClusterChopWOverHMax = 1.5;    // w/h of a cluster chop (e.g. lim) should be from 0.75 to 1.5
    
    public static double msdMaxCapLeftOverhead = 6; // Cap left Overhead comparing to principle should be 3 maximum.
    public static double msdMaxCapRightOverhead = 8; // Cap right Overhead comparing to principle should be 5 maximum (consider top notes can be misrecoged as cap).
    public static double msdMaxUnderLeftOverhead = 6; // Under left Overhead comparing to principle should be 3 maximum.
    public static double msdMaxUnderRightOverhead = 6; // Under right Overhead comparing to principle should be 3 maximum.
    public static double msdOverhead2HeightMaxRatio = 0.8;    // Overhead comparing base height should be no larger than 0.75
    public static double msdBaseCapUnderDistance = 0.41; // the distance between base and cap/under should not be more than 0.4 height of base.
    public static double msdBaseCapUnderExtDistance = 0.6; // the distance between base and cap/under is less than 0.4 height of base, they can still be a fraction.
    public static double msdSignificantPrincipleHThresh = 0.54;  // principle height is > 55%* height of whole. This is to avoid some incomplete chars.
    public static double msdMinBlankHDivDistance = 0.25; // the distance between two h-divs should be at least 0.25 height of heighest div.
    public static double msdExpressionGap = 1.0;    // if gap between expressions is larger than 1.0 * average char height, then definitely two expressions
    public static double msdLnDiv2TopUnderGapGeneralMax = 0.85;    // gap between a line div and it top & under generally should be max at 0.85 avg char height
    public static double msdVerticalCutThresh = 0.5;    // if this col avg on count is less than 0.5 of max col avg on count and it is extreme pnt, cut.
	
    public static int msnMinCharWidthInUnit = 3;  // min char width in unit, not pixel
    public static int msnMinNormalCharWidthInUnit = 5;  // min normal char (not sub not super) width in unit
    public static double msdAvgCharWidthOverHeight = 0.5;  // on average, average char width over height is 0.5 (this includes | h-div, sqrt, ], etc.)
    public static double msdMaxCharGapWidthOverHeight = 0.75;  // in the same expression, char gap over height should be no greater than 0.75
    public static int msnMinVGapWidthInUnit = 1;  // min V gap width in unit, not pixel
	
    public static int msnMinCharHeightInUnit = 5;  // min char height in unit, not pixel
    public static int msnMinNormalCharHeightInUnit = 10;  // min normal char (not sub not super) height in unit
    public static double msdHeightEffectFadingConst = 0.5; // the fading factor of the effect on average char height
    
    public static double msdAvgCharWOverH = 0.6;    // average char width over height
	
    public static double msdBracketMatrixHeightRatio = 0.67;    // from 0 to 1
    public static double msdOpenCloseBracketHeightRatio = 0.85;  // from 0 to 1
    public static double msdBndCharHeightRatio = 1.2;    // bound char GENERALLY is at least 1.2 * bounded char's height. If not, and if it does not have its matched closing bound, it may be 1 or t.
    public static double msdMatrixBracketHeightRatio = 0.6; // height ratio between matrix and bracket.
    public static double msdMatrixMExprsHDivRatio = 0.35; // h-div should be at least 0.45 * avg_char_height.
    public static double msdMatrixMExprsHDivRelaxRatio = 0.2; // h-div's height is 0.2 * avg_char_height, it can still be a h-div if ext-h-div is wide enough
    public static double msdMatrixMExprsExtHDivRatio = 0.5; // ext-h-div should be at least 0.5 * avg_char_height.
    public static double msdMatrixMExprsVDivRatio = 0.45; // v-div should be at least 0.45 * avg_char_width.
    public static double msdMatrixMExprsChildVDivRatio = 0.8; // child v-div should be at least 0.8 * avg_char_height.
    public static double msdMatrixMExprsAvgChildVDivRatio = 1.2; // on average child v-div should be at least 1.2 * avg_char_height.
    public static double msdLUNoteHeightRatio2Base = 0.6;  // base char should be at least note char/0.6 height.
    
    public static double msdExtendableCharWOverHThresh = 4.0;   // the threshold of width/height for some extendable chars like |, 1, - , sqrt etc.
    public static double msdCharWOverHMaxSkewRatio = 2.0;   // was 2.5, Width/height is at most 2.5 times at least 0.4 times of to recognize char's width / height
    public static double msdCharWOverHGuaranteedExtRatio = 1.35; // if h/w >= msdExtendableCharWOverHThresh * msdCharWOverHGuaranteedExtRatio, we guarantee it is an extendable char.
    public static double msdGoodRecogCharThresh = UnitCandidate.convertRatio2Similarity(0.055);    // was 0.055
    public static double msdGoodRecogExprThresh = UnitCandidate.convertRatio2Similarity(0.1);    // similarity > 0.1 means invalid expression
    public static double msdDisconnectSERDisadv = 0.005 * (UnitCandidate.WORST_SIMILARITY_VALUE - UnitCandidate.BEST_SIMILARITY_VALUE);    // disadvantage of disconnected SER comparing to non-dis SER, was 0.005
    public static double msdHandWritingLetterDisadv = 0.002 * (UnitCandidate.WORST_SIMILARITY_VALUE - UnitCandidate.BEST_SIMILARITY_VALUE); // disadvantage letter against numbers.
    /* suggested weighting **************
    public static double msdExtCharJntPntSimWeight = 0.1; // extendable char similarity joint point weight
    public static double msdJntPntSimWeight = 0.02;    
    public static double msdExtCharLatticeDenSimWeight = 0.5; // extendable char similarity lattice density weight
    public static double msdLatticeDenSimWeight = 0.7;    
    public static double msdExtCharMinDisSimWeight = 0.4; // extendable char similarity min distance weight
    public static double msdMinDisSimWeight = 0.28;    
    */
    public static double msdExtCharJntPntSimWeight = 0.1; // extendable char similarity joint point weight
    public static double msdJntPntSimWeight = 0.02;    
    public static double msdExtCharLatticeDenSimWeight = 0.5; // extendable char similarity lattice density weight
    public static double msdLatticeDenSimWeight = 0.7;    
    public static double msdExtCharMinDisSimWeight = 0.4; // extendable char similarity min distance weight
    public static double msdMinDisSimWeight = 0.28;    
    
    public static double msdNoEnoughInfo4AvgStrokeWidthThresh = 3;    // if a imageChop's height and width < 4.0* avgStrokeWidth, we believe we cannot get accurate avg stroke width from it.

    public static double msdAbnormalCharWidthThresh = 2.5;  // if char width is larger than 3.0 * average char width, filter it off.
    public static double msdAbnormalCharHeightThresh = 2.5;  // if char height is larger than 3.0 * average char width, filter it off.
    //todo dml_change 0.8 -> 2.0
    public static double msdAbnormalVCutGapThresh = 1.5;  // if v-cut gap is larger than 0.5 * average char width + gap, means it is end of expression
    public static double msdAbnormalHCutGapThresh = 0.8;  // if h-cut gap larger than 0.5 * average char height + gap, means it is end of expression
    public static double msdMathPossibilityCheckThresh = 0.5;   // if max math possib - this math possib > 0.3, very likely this is not a good expression.
    public static double msdMathPossiblityGoodThresh = 0.0;  // if math possiblity >= 0.0, it is always looked on as good expression.
    public static double msdMinSerHeightAgainstAvg = 0.15;   // height and width of a char is too small means a noise point if ser type is not dot.
    public static double msdMinSerWidthAgainstAvg = 0.15;   // height and width of a char is too small means a noise point if ser type is not dot.
    
    public static double msdVBlankCutMajorWidthRatio = 2.0; // divide a v-blank cut list into several parts, the selected recog part should at least 2.0 times of width of the middle part if it is not in the middle.
    public static double msdSubtractVRangeAgainstNeighbourH = 0.05; // a subtract char's vertical position should not be the top 15% or bottom 5% of its right neightour's height
    public static double msdDecimalPntVRangeAgainstNeighbourH = 0.15; // a decimal point's vertical position should be the bottom 15% percent of its right or left char.
    public static double msdSubtractWidthAgainstNeighbourW = 0.35;   // a subtract's width should be at least 35% of its right neighbour's width if right neighbour is a char.
    public static double msdInfiniteWOverHThresh = 1.7; // for infinite, w/h should be larger than 1.7
    public static double msdSquareBracketTo1WOverHThresh = 0.35; // for misrecognized [ or ], if w/h <= 0.35, it is likely to be 1.
    //todo dml_change 0.2 -> 0.15
    public static double msdRoundBracketTo1WOverHThresh = 0.15;  // for misrecognized ( or ), if w/h <= 0.35, it is likely to be 1.
    public static double msdDisconnected2BaseUnderWidthRatio = 0.7;  // for a disconnected 2, under _ should be at least 0.8 * width of top one. Same vice versa.
    public static double msdVeryThinOverlappedHeightThresh = 2.3;   // if overlapped chars' avg height <= 4.0 * average stroke width , this implies that overlapped char height can not be used to determine gap is wide or not.
    public static double msdVeryThinGapThresh = 4.0;    // Gap <= 4.0 * average stroke width, which means gap is very narrow and it may imply remerge.
    public static double msdMinNormalCharWInStrokeW = 3.0;    // a normal char, like w, a, not ., |, ..., in minimum, should be at least 3.0 avg stroke width difference, they are not absolute covered.
    public static int msnDisconnectLnCutPerStrokeWidth = 20;    // every 20 average stroke width we see a disconnect.
    public static double msdLimWToCharHThresh = 3;  // for like lim, average char height * 3 > lim's width.   
    public static double msd1WOverHLThresh = 0.43;   // 1's w/h should be no smaller than 0.5
    public static double msdHard2Identify1BndCharWidthThresh = 2.95; // if width is less than 2.8* avg stroke width, it is hard to identify 1 from bound char.
    public static double msdDotOnCnt2AreaMin = 0.8; // if on cnt > 0.6 * area and w/h is from (0.5 to 2), then it is a dot.
    public static double msdNeighborHeight2PossDotHeight = 3.5;   // if a lower note is 1/5.0 height of its left and right number neighbour, it is actually a dot.
    public static double msdNeighborHeight2PossDotWidth = 3.0;   // if a lower note's width is 1/4.0 height of its left and right number neighbour, it is actually a dot.

    public static double msdWorstCaseLineDivOnLenRatio = 0.95;  // line div's on point should be 95% of image chop width at least.
    public static double msdMajorArea2TopBtmThresh = 1.5;   // if cap or btm's area > 2* major area, cap or btm is major, major is actually btm or cap.
    
    public static double msdBadChopHeight2MaxRatio = 0.15;  // if top or bottom chop's height is thinner than 0.15 * max chop height in the first recognize, this could be a noise chop.
    public static double msdBadChopHeight2AvgRatio = 0.35;  // if top or bottom chop's height is thinner than 0.35 * avg chop height in the first recognize, this could be a noise chop.
    public static double msdTopUnderNotCoverWellThresh = 0.25;  // if 0.35 of top or bottom part is not used to cover major chops, this top or bottom seems to be noise.
    public static double msdCrosMultiplyLowerThanNeighbor = 0.25;   // cross multiply is generally lower and shower than its neighbors. So check its height to see if it is x or cross multiply.
    public static double msdNoiseHCutHeightThreshRatio = 5.0;   // if a h-chop's height is less than 1/5.0 of it top and bottom chop heights, and it is not a -, and it is not in the middle of a v_cut list, then it could be a noise.
    public static double msdNoiseHeightToNormalThresh = 0.35;   // if on the vcut list, edge ser's height is less than 0.25 * avg height and it is not a ---, then it is a noise point.
    public static double msd1TopOverheadsRatio = 5; // top left overhead / btm right overhead should be at least 5
    public static double msd1BtmOverheadsRatio = 0.5; // btm left overhead / btm right overhead should be at least 0.5
    public static double msd1TopBtmOverheadsRatio = 0.35; // top overhead / btm overhead should be at least 0.3
}
