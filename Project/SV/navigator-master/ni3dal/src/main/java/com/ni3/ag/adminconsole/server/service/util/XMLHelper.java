/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.server.service.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XMLHelper{
	private final static Logger log = Logger.getLogger(XMLHelper.class);

	public static Document loadDocument(String xml){
		Document doc = null;
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes());

			doc = db.parse(bis);
		} catch (ParserConfigurationException ex){
			log.error("Error parsing xml " + xml, ex);
		} catch (SAXException ex){
			log.error("Error parsing xml " + xml, ex);
		} catch (IOException ex){
			log.error("Error parsing xml " + xml, ex);
		}
		return doc;
	}
}
