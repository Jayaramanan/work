/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;

public class DataImporter{
	private static final Logger log = Logger.getLogger(DataImporter.class);
	private SchemaObjectImporter schemaObjectImporter;
	private OwnerImporter ownerImporter;

	public void setSchemaObjectImporter(SchemaObjectImporter schemaObjectImporter){
		this.schemaObjectImporter = schemaObjectImporter;
	}

	public void setOwnerImporter(OwnerImporter ownerImporter){
		this.ownerImporter = ownerImporter;
	}

	public void makeImport(String xml) throws ACException{
		Document doc = loadDocument(xml);
		NodeList nodeList = doc.getElementsByTagName("owner");
		ErrorContainer ec = ownerImporter.importData(nodeList, true);
		schemaObjectImporter.setOwner(ownerImporter.getOwner());
		List<ErrorEntry> errors = ec.getErrors();
		if (errors.isEmpty()){
			nodeList = doc.getElementsByTagName("schemaDefinition");
			ec = schemaObjectImporter.importData(nodeList);
			errors = ec.getErrors();
		}
		if (!errors.isEmpty()){
			throw new ACException(errors);
		}
	}

	private Document loadDocument(String xml) throws ACException{
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes("UTF-8"));

			return db.parse(bis);
		} catch (ParserConfigurationException ex){
			log.error("Error parsing xml " + xml, ex);
			throw new ACException(TextID.MsgImportError, new String[] { ex.getMessage() });
		} catch (SAXException ex){
			log.error("Error parsing xml " + xml, ex);
			throw new ACException(TextID.MsgImportError, new String[] { ex.getMessage() });
		} catch (IOException ex){
			log.error("Error parsing xml " + xml, ex);
			throw new ACException(TextID.MsgImportError, new String[] { ex.getMessage() });
		}

	}
}
