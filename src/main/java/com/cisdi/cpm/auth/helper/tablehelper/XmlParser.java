package com.cisdi.cpm.auth.helper.tablehelper;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XmlParser {

//	private static XmlParser xmlParser = null;
//
//	private XmlParser() {}
//
//	public static XmlParser newInstance() {
//		if (xmlParser == null) {
//			xmlParser = new XmlParser();
//		}
//
//		return xmlParser;
//	}

    public Document getDocument(String xmlPath) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbParser = dbf.newDocumentBuilder();

        return dbParser.parse(new File(xmlPath));
    }
}
