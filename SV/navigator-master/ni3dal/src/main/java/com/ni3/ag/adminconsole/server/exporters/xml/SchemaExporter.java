/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters.xml;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.GroupDAO;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;

public class SchemaExporter{

	private static final Logger log = Logger.getLogger(SchemaExporter.class);
	private GroupDAO groupDAO;
	private SchemaDAO schemaDAO;

	private ObjectDefinitionExporter objectExporter;
	private GroupExporter groupExporter;

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	public byte[] exportSchema(Integer schemaId) throws ACException{
		byte[] xml;
		List<Group> groups = groupDAO.getGroups();

		Schema schema = schemaDAO.getSchema(schemaId);

		Hibernate.initialize(schema.getObjectDefinitions());
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try{
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();

			Element schemasElem = doc.createElement("schemaDefinitions");
			doc.appendChild(schemasElem);
			Element ownerElem = doc.createElement("owner");
			User owner = schema.getCreatedBy();
			if (owner == null)
				throw new ACException(TextID.MsgNoPropertySet, new String[] { schema.getName() + " => createdBy" });
			ownerElem.setAttribute("name", owner.getUserName());
			schemasElem.appendChild(ownerElem);
			Element schemaElem = doc.createElement("schemaDefinition");
			log.debug("exporting schema `" + schema.getName() + "`");
			schemaElem.setAttribute("name", schema.getName());
			schemaElem.setAttribute("description", schema.getDescription());
			schemasElem.appendChild(schemaElem);
			Element odListElem = doc.createElement("objectDefinitions");
			schemaElem.appendChild(odListElem);

			for (ObjectDefinition od : schema.getObjectDefinitions())
				objectExporter.export(doc, odListElem, od);

			Element groupElem = doc.createElement("groups");
			schemaElem.appendChild(groupElem);

			groupExporter.setSchema(schema);
			for (Group group : groups){
				groupExporter.export(doc, groupElem, group);
			}

			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(stream);
			transformer.transform(source, result);
			xml = stream.toByteArray();
		} catch (ParserConfigurationException pce){
			log.error(pce.getMessage(), pce);
			throw new ACException(TextID.MsgFailedToMakeSchemaExport);
		} catch (TransformerException te){
			log.error(te.getMessage(), te);
			throw new ACException(TextID.MsgFailedToMakeSchemaExport);
		}
		return xml;

	}

	public GroupDAO getGroupDAO(){
		return groupDAO;
	}

	public void setGroupDAO(GroupDAO groupDAO){
		this.groupDAO = groupDAO;
	}

	public GroupExporter getGroupExporter(){
		return groupExporter;
	}

	public void setGroupExporter(GroupExporter groupExporter){
		this.groupExporter = groupExporter;
	}

	public ObjectDefinitionExporter getObjectExporter(){
		return objectExporter;
	}

	public void setObjectExporter(ObjectDefinitionExporter objectExporter){
		this.objectExporter = objectExporter;
	}

}
