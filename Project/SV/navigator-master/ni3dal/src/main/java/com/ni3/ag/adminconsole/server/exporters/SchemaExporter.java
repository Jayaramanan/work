/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.ObjectAttributeDAO;
import com.ni3.ag.adminconsole.server.dao.UserDAO;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;
import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

public class SchemaExporter{
	private static final Logger log = Logger.getLogger(SchemaExporter.class);
	private UserDAO userDAO;
	private ObjectAttributeDAO objectAttributeDAO;
	private ObjectDefinitionExporter objectExporter;
	private PredefinedAttributeExporter predefinedExporter;
	private UserSimpleExporter userSimpleExporter;
	private FormulaExporter formulaExporter;
	private ConnectionTypeExporter connectionTypeExporter;

	public ByteArrayOutputStream exportSchema(Schema schema) throws ACException{
		List<User> users = userDAO.getUsers();
		List<ObjectAttribute> formulaAttrs = new ArrayList<ObjectAttribute>();
		Hibernate.initialize(schema.getObjectDefinitions());
		try{
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			WritableWorkbook book = Workbook.createWorkbook(stream);
			int i;
			for (i = 0; i < schema.getObjectDefinitions().size(); i++){
				ObjectDefinition od = schema.getObjectDefinitions().get(i);
				formulaAttrs.addAll(objectAttributeDAO.getObjectAttributesWithFormulas(od));
				WritableSheet sheet = book.createSheet(od.getName(), i + 1);
				objectExporter.export(sheet, od);
			}
			WritableSheet sheet = book.createSheet(UserSimpleExporter.SEET_NAME, i + 1);
			userSimpleExporter.export(sheet, users);

			predefinedExporter.export(book, schema);

			sheet = book.createSheet(FormulaExporter.SHEET_NAME, i + 1);
			formulaExporter.export(sheet, formulaAttrs);

			sheet = book.createSheet(ConnectionTypeExporter.SHEET_NAME, book.getSheets().length + 1);
			connectionTypeExporter.export(sheet, schema);

			book.write();
			book.close();
			return stream;
		} catch (Exception e){
			log.error(e.getMessage(), e);
			throw new ACException(TextID.MsgFailedToMakeSchemaExport);
		}
	}

	public void setObjectAttributeDAO(ObjectAttributeDAO objectAttributeDAO){
		this.objectAttributeDAO = objectAttributeDAO;
	}

	public UserDAO getUserDAO(){
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	public void setFormulaExporter(FormulaExporter formulaExporter){
		this.formulaExporter = formulaExporter;
	}

	public void setUserSimpleExporter(UserSimpleExporter userSimpleExporter){
		this.userSimpleExporter = userSimpleExporter;
	}

	public void setObjectExporter(ObjectDefinitionExporter objectExporter){
		this.objectExporter = objectExporter;
	}

	public void setPredefinedExporter(PredefinedAttributeExporter predefinedExporter){
		this.predefinedExporter = predefinedExporter;
	}

	public void setConnectionTypeExporter(ConnectionTypeExporter connectionTypeExporter){
		this.connectionTypeExporter = connectionTypeExporter;
	}
}
