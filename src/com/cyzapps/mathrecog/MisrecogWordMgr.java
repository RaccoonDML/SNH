/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyzapps.mathrecog;

import com.cyzapps.mathrecog.UnitPrototypeMgr.UnitProtoType;
import java.io.InputStream;
import java.util.LinkedList;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 *
 * @author tonyc
 */
public class MisrecogWordMgr {
    public static final int WORD_TYPE_FUNCTION = 0;
    public static final int WORD_TYPE_VARIABLE = 1;
    
    public static class WordCandidate {
        public String mstrCandidate = "";
    }
    
    public static class LetterCandidate {
        public boolean mbMisrecog = false;
        public String mstrCandidate = "";
    }
    
    public static class LetterCandidates    {
        public int mnIndex = 0;
        public UnitProtoType.Type mType = UnitProtoType.Type.TYPE_UNKNOWN;
        public LinkedList<LetterCandidate> mlistLetterCands = new LinkedList<LetterCandidate>();
    }
    
    public static class MisrecogWord {
        public String mstrShouldBe = "";
        public int mnWordType = WORD_TYPE_FUNCTION;  // default is function word type
        public UnitProtoType.Type mType = UnitProtoType.Type.TYPE_UNKNOWN;
        public LinkedList<WordCandidate> mlistWordCands = new LinkedList<WordCandidate>();
        public LinkedList<LetterCandidates> mlistLetterCandSets = new LinkedList<LetterCandidates>();

        public int getWordSimilarityEndP1(String str, int nStart)   {
            for (WordCandidate wc : mlistWordCands)    {
                if (str.length() >= nStart + wc.mstrCandidate.length()
                        && str.substring(nStart, nStart + wc.mstrCandidate.length()).equals(wc.mstrCandidate)) {
                    return nStart + wc.mstrCandidate.length();
                }
            }
            return nStart;
        }
        
        public int getLettersSimilarityEndP1(String str, int nStart) {
            int nFoundSimilarChars = 0;
            int nMisRecogChars = 0;
            int idxStr = nStart;
            for (int idx = 0; idx < mstrShouldBe.length(); idx ++)   {
                for (LetterCandidates lcs : mlistLetterCandSets)    {
                    if (lcs.mnIndex == idx) {
                        break;
                    }
                    int idx1 = 0;
                    for (; idx1 < lcs.mlistLetterCands.size(); idx1 ++) {
                        String strCand = lcs.mlistLetterCands.get(idx1).mstrCandidate;
                        if ((nStart + idxStr + strCand.length()) <= str.length() && str.substring(idxStr, strCand.length()).equals(strCand))    {
                            nFoundSimilarChars ++;
                            if (lcs.mlistLetterCands.get(idx1).mbMisrecog)  {
                                nMisRecogChars ++;
                            }
                            idxStr += strCand.length();
                            break;
                        }
                    }
                }
                if (nFoundSimilarChars < idx + 1)
				{
					// this char is not found.
					break;
                }
            }
            
            if (nFoundSimilarChars < mstrShouldBe.length()) {
                return nStart;  // means nothing found.
            } else if ((mstrShouldBe.length() > 2 && nMisRecogChars >= 0.5 * mstrShouldBe.length())
                    || (mstrShouldBe.length() == 2 && nMisRecogChars == 2)){
                return nStart;  // too many miss recog chars.
            } else  {
                return idxStr;  // end + 1 of the similarity string.
            }
        }
    }
    
    public LinkedList<MisrecogWord> mslistMisrecogWordSet = new LinkedList<MisrecogWord>();
    
    public void readFromXML(InputStream is) {
        mslistMisrecogWordSet.clear();
        Document doc;
		try {
	        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(is);
	        // normalize text representation
	        doc.getDocumentElement().normalize();
            NodeList listOfMisrecogWordSets = doc.getElementsByTagName("MisrecogWordSets").item(0).getChildNodes();
            for (int idxMWSets = 0; idxMWSets < listOfMisrecogWordSets.getLength(); idxMWSets ++)   {
                Node misrecogWordNode = listOfMisrecogWordSets.item(idxMWSets);
                if (misrecogWordNode.getNodeType() == Node.ELEMENT_NODE)    {
                    MisrecogWord misrecogWord = new MisrecogWord();
                    NamedNodeMap nnmMW = misrecogWordNode.getAttributes();
	            	Node nodeAttrShouldBe = nnmMW.getNamedItem("ShouldBe");
                    misrecogWord.mstrShouldBe = "";
                    if (nodeAttrShouldBe != null)   {
                        misrecogWord.mstrShouldBe = nodeAttrShouldBe.getNodeValue();
                    }
                    Node nodeAttrWordType = nnmMW.getNamedItem("WordType");
                    misrecogWord.mnWordType = WORD_TYPE_FUNCTION;
                    if (nodeAttrWordType != null)   {
                        if (nodeAttrWordType.getNodeValue().equals("VARIABLE"))    {
                            misrecogWord.mnWordType = WORD_TYPE_VARIABLE;
                        }
                    }
                    Node nodeAttrUnitType = nnmMW.getNamedItem("UnitType");                    
                    if (nodeAttrUnitType == null) {
                        misrecogWord.mType = UnitProtoType.Type.TYPE_UNKNOWN;
                    } else {
                        String strUnitType = nodeAttrUnitType.getNodeValue();
                        misrecogWord.mType = UnitProtoType.cvtTypeStr2Enum(strUnitType);
                    }
                    
                    misrecogWord.mlistLetterCandSets = new LinkedList<LetterCandidates>();
                    NodeList listOfLetterCandSets = misrecogWordNode.getChildNodes();
	            	for (int idxLetterCandSets = 0; idxLetterCandSets < listOfLetterCandSets.getLength(); idxLetterCandSets ++)	{
                        Node nodeChild = listOfLetterCandSets.item(idxLetterCandSets);
                        if (nodeChild.getNodeType() != Node.ELEMENT_NODE)   {
                            continue;
                        } else if (nodeChild.getNodeName().equals("WordCandidate"))	{
                            WordCandidate wordCandidate = new WordCandidate();
                            wordCandidate.mstrCandidate = nodeChild.getFirstChild().getNodeValue();
                            misrecogWord.mlistWordCands.add(wordCandidate);
                        } else if (nodeChild.getNodeName().equals("LetterCandidates"))	{
                            NamedNodeMap nnmLetterCandSet = nodeChild.getAttributes();
                            Node nodeAttrIndexLetterCands = nnmLetterCandSet.getNamedItem("Index");
                            Node nodeAttrUnitTypeLetterCands = nnmLetterCandSet.getNamedItem("UnitType");
                            LetterCandidates letterCandidates = new LetterCandidates();
                            String strIndexLetterCands = "0";
                            String strUnitTypeLetterCands = "UNKNOWN";
                            if (nodeAttrIndexLetterCands != null)   {
                                strIndexLetterCands = nodeAttrIndexLetterCands.getNodeValue();
                            }
                            if (nodeAttrUnitTypeLetterCands != null)   {
                                strUnitTypeLetterCands = nodeAttrUnitTypeLetterCands.getNodeValue();
                            }
                            try {
                                letterCandidates.mnIndex = Integer.parseInt(strIndexLetterCands);
                            } catch (NumberFormatException e)   {
                                letterCandidates.mnIndex = 0;
                            }
                            letterCandidates.mType = UnitProtoType.cvtTypeStr2Enum(strUnitTypeLetterCands);
                            letterCandidates.mlistLetterCands = new LinkedList<LetterCandidate>();
                            NodeList listOfLetterCands = nodeChild.getChildNodes();
                            for (int idxLetterCands = 0; idxLetterCands < listOfLetterCands.getLength(); idxLetterCands ++) {
                                Node nodeLetterCand = listOfLetterCands.item(idxLetterCands);
                                if (nodeLetterCand.getNodeType() == Node.ELEMENT_NODE && nodeLetterCand.getNodeName().equals("LetterCandidate"))	{
                                    NamedNodeMap nnmLetterCand = nodeLetterCand.getAttributes();
                                    Node nodeAttrMisrecog = nnmLetterCand.getNamedItem("Misrecog");
                                    LetterCandidate letterCandidate = new LetterCandidate();
                                    String strMisrecog = "false";
                                    if (nodeAttrMisrecog != null)   {
                                        strMisrecog = nodeAttrMisrecog.getNodeValue();
                                    }
                                    if (strMisrecog.trim().compareToIgnoreCase("true") == 0)    {
                                        letterCandidate.mbMisrecog = true;
                                    } else  {
                                        letterCandidate.mbMisrecog = false;
                                    }
                                    String strCand = nodeLetterCand.getFirstChild().getNodeValue();
                                    letterCandidate.mstrCandidate = strCand;
                                    letterCandidates.mlistLetterCands.add(letterCandidate);
                                }
                            }
                            if (letterCandidates.mlistLetterCands.size() > 0 && letterCandidates.mnIndex >= 0
                                    && letterCandidates.mnIndex < misrecogWord.mstrShouldBe.length())    {
                                misrecogWord.mlistLetterCandSets.add(letterCandidates);
                            }
                        }
                    }
                    if (misrecogWord.mstrShouldBe.length() > 0
                            && (misrecogWord.mlistLetterCandSets.size() > 0 || misrecogWord.mlistWordCands.size() > 0))  {
                        mslistMisrecogWordSet.add(misrecogWord);
                    }
                }
            }
		} catch (Exception e)	{
		}
    }
    
    public String writeToXMLString()    {
        String strOutput = "<MisrecogWordSets>\n";
        for (int idx = 0; idx < mslistMisrecogWordSet.size(); idx ++)  {
            MisrecogWord misrecogWord = mslistMisrecogWordSet.get(idx);
            String strWordType = "FUNCTION";
            if (misrecogWord.mnWordType == WORD_TYPE_VARIABLE)  {
                strWordType = "VARIABLE";
            }
            strOutput += "<MisrecogWord ShouldBe=\"" + misrecogWord.mstrShouldBe
                    + "\" WordType=\"" + strWordType + "\""
                    + ((misrecogWord.mType == UnitProtoType.Type.TYPE_UNKNOWN)?""
                        :("UnitType=\"" + UnitProtoType.cvtTypeEnum2Str(misrecogWord.mType) + "\""))
                    + " >\n";
            for (int idx1 = 0; idx1 < misrecogWord.mlistWordCands.size(); idx1 ++)  {
                strOutput += "<WordCandidate>" + misrecogWord.mlistWordCands.get(idx1).mstrCandidate + "</WordCandidate>\n";
            }
            for (int idx1 = 0; idx1 < misrecogWord.mlistLetterCandSets.size(); idx1 ++)   {
                LetterCandidates letterCandSet = misrecogWord.mlistLetterCandSets.get(idx1);
                strOutput += "<LetterCandidates Index=\"" + letterCandSet.mnIndex
                        + "\" UnitType=\"" + UnitProtoType.cvtTypeEnum2Str(letterCandSet.mType) + "\" >\n";
                for (int idx2 = 0; idx2 < letterCandSet.mlistLetterCands.size(); idx2 ++)   {
                    LetterCandidate letterCand = letterCandSet.mlistLetterCands.get(idx2);
                    strOutput += "<LetterCandidate ";
                    if (letterCand.mbMisrecog) {
                        strOutput += "Misrecog=\"true\" ";
                    }
                    strOutput += ">" + letterCand.mstrCandidate + "</LetterCandidate>\n";
                }
                strOutput += "</LetterCandidates>\n";                
            }
            strOutput += "</MisrecogWord>\n";
        }
        strOutput += "</MisrecogWordSets>\n";
        return strOutput;
    }
    
    public boolean isEmpty() {
    	return mslistMisrecogWordSet.size() == 0;
    }
}
