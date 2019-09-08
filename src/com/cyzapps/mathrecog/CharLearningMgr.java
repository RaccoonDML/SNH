/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyzapps.mathrecog;

import com.cyzapps.mathrecog.UnitPrototypeMgr.UnitProtoType;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Locale;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 *
 * @author tonyc
 */
public class CharLearningMgr {
    public static class CharCandidate   {
        public UnitProtoType.Type mType = UnitProtoType.Type.TYPE_UNKNOWN;
        public String mstrFont = "";
        public int mnMisRecogTimes = 0;
    }
    
    public static class CharCandSet {
        public UnitProtoType.Type mType1stGlance = UnitProtoType.Type.TYPE_UNKNOWN;
        public String mstrFont1stGlance = "";
        public LinkedList<CharCandidate> mlistCharCands = new LinkedList<CharCandidate>();
    }
    
    public LinkedList<CharCandSet> mlistCharCandSets = new LinkedList<CharCandSet>();
    
    public LinkedList<CharCandidate> findCharCandidates(UnitProtoType.Type unitType, String strFont)    {
        for (CharCandSet ccs : mlistCharCandSets)   {
            if (ccs.mType1stGlance == unitType && (ccs.mstrFont1stGlance.length() == 0 || ccs.mstrFont1stGlance.equals(strFont)))   {
                // find it.
                return ccs.mlistCharCands;
            }
        }
        return new LinkedList<CharCandidate>();
    }
    
    public void readFromXML(InputStream is) {
        mlistCharCandSets.clear();
        Document doc;
		try {
	        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(is);
	        // normalize text representation
	        doc.getDocumentElement().normalize();
            NodeList listOfCharCandSets = doc.getElementsByTagName("CCSetsList").item(0).getChildNodes();
            for (int idxCCSets = 0; idxCCSets < listOfCharCandSets.getLength(); idxCCSets ++)   {
                Node charCandSetsNode = listOfCharCandSets.item(idxCCSets);
                if (charCandSetsNode.getNodeType() == Node.ELEMENT_NODE)    {
                    NamedNodeMap nnmCCSets = charCandSetsNode.getAttributes();
	            	Node nodeAttrType1stGlance = nnmCCSets.getNamedItem("Type");
                    Node nodeAttrFont1stGlance = nnmCCSets.getNamedItem("Font");
                    String strType1stGlance = "", strFont1stGlance = "";
                    if (nodeAttrType1stGlance != null)   {
                        strType1stGlance = nodeAttrType1stGlance.getNodeValue();
                    }
                    if (nodeAttrFont1stGlance != null)   {
                        strFont1stGlance = nodeAttrFont1stGlance.getNodeValue();
                    }
                    UnitProtoType.Type type1stGlance = UnitProtoType.cvtTypeStr2Enum(strType1stGlance);
                    strFont1stGlance = UnitProtoType.cvtFontStr2Font(strFont1stGlance);  // validate the string
                    if (type1stGlance == UnitProtoType.Type.TYPE_UNKNOWN)   {
                        continue;   // invalid type or font.
                    }
                    LinkedList<CharCandidate> listCharCands = new LinkedList<CharCandidate>();
                    NodeList listOfCharCands = charCandSetsNode.getChildNodes();
	            	for (int idxCharCand = 0; idxCharCand < listOfCharCands.getLength(); idxCharCand ++)	{
                        if (listOfCharCands.item(idxCharCand).getNodeType() == Node.ELEMENT_NODE &&
                                listOfCharCands.item(idxCharCand).getNodeName().equals("CharCand"))	{
                            NamedNodeMap nnmCharCand = listOfCharCands.item(idxCharCand).getAttributes();
                            Node nodeAttrTypeCharCand = nnmCharCand.getNamedItem("Type");
                            Node nodeAttrFontCharCand = nnmCharCand.getNamedItem("Font");
                            Node nodeAttrMisRecogTimes = nnmCharCand.getNamedItem("MissRecogTimes");
                            String strTypeCharCand = "", strFontCharCand = "";
                            if (nodeAttrTypeCharCand != null)   {
                                strTypeCharCand = nodeAttrTypeCharCand.getNodeValue();
                            }
                            if (nodeAttrFontCharCand != null)   {
                                strFontCharCand = nodeAttrFontCharCand.getNodeValue();
                            }
                            int nMisRecogTimes = 1;
                            if (nodeAttrMisRecogTimes != null)  {
                                try {
                                    nMisRecogTimes = Integer.parseInt(nodeAttrMisRecogTimes.getNodeValue());
                                } catch(NumberFormatException e)    {
                                    
                                }
                                if (nMisRecogTimes < 0) {
                                    nMisRecogTimes = 0;
                                }
                            }
                            UnitProtoType.Type typeCharCand = UnitProtoType.cvtTypeStr2Enum(strTypeCharCand);
                            strFontCharCand = UnitProtoType.cvtFontStr2Font(strFontCharCand);  // validate the string
                            if (typeCharCand == UnitProtoType.Type.TYPE_UNKNOWN)   {
                                continue;   // invalid type or font.
                            }
                            CharCandidate charCand = new CharCandidate();
                            charCand.mType = typeCharCand;
                            charCand.mstrFont = strFontCharCand;
                            charCand.mnMisRecogTimes = nMisRecogTimes;
                            // sort and insert.
                            int idx = 0;
                            for (; idx < listCharCands.size(); idx ++)   {
                                if (nMisRecogTimes > listCharCands.get(idx).mnMisRecogTimes)    {
                                    listCharCands.add(idx, charCand);
                                }
                            }
                            if (idx == listCharCands.size())    {
                                listCharCands.add(charCand);
                            }
                        }
                    }
                    if (listCharCands.size() == 0)  {
                        continue;
                    }
                    CharCandSet charCandSet = new CharCandSet();
                    charCandSet.mType1stGlance = type1stGlance;
                    charCandSet.mstrFont1stGlance = strFont1stGlance;
                    charCandSet.mlistCharCands = listCharCands;
                    mlistCharCandSets.add(charCandSet);
                }
            }
		} catch (Exception e)	{
		}
    }
    
    public String writeToXMLString()    {
        String strOutput = "<CCSetsList>\n";
        for (int idx = 0; idx < mlistCharCandSets.size(); idx ++)  {
            CharCandSet ccs = mlistCharCandSets.get(idx);
            strOutput += "<CharCandSet Type=\"" + UnitProtoType.cvtTypeEnum2Str(ccs.mType1stGlance) + "\" Font=\"" + ccs.mstrFont1stGlance + "\" >\n";
            for (int idx1 = 0; idx1 < ccs.mlistCharCands.size(); idx1 ++)   {
                CharCandidate charCand = ccs.mlistCharCands.get(idx1);
                strOutput += "<CharCandSet Type=\"" + UnitProtoType.cvtTypeEnum2Str(charCand.mType)
                        + "\" Font=\"" + charCand.mstrFont
                        + "\" MissRecogTimes=\"" + charCand.mnMisRecogTimes + "\" / >\n";
            }
            strOutput += "</CharCandSet>\n";
        }
        strOutput += "</CCSetsList>\n";
        return strOutput;
    }
    
    public boolean isEmpty() {
    	return mlistCharCandSets.size() == 0;
    }
}
