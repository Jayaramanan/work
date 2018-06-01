/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.server.importers.salesforce;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.EditingOption;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectGroup;
import com.ni3.ag.adminconsole.domain.ObjectType;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.SchemaGroup;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.SalesforceConnectionProvider;
import com.ni3.ag.adminconsole.server.dao.GroupDAO;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.server.dao.UserDAO;
import com.ni3.ag.adminconsole.server.dbservice.UserTableStructureService;
import com.ni3.ag.adminconsole.server.service.SalesforceSchemaImporter;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.DescribeTab;
import com.sforce.soap.partner.DescribeTabSetResult;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.FieldType;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.PicklistEntry;
import com.sforce.ws.ConnectionException;

public class SalesforceSchemaImporterImpl implements SalesforceSchemaImporter{
	private static final Logger log = Logger.getLogger(SalesforceSchemaImporterImpl.class);
	private static final String NAME_PATTERN = "[^a-zA-Z0-9 -]*";

	private SalesforceConnectionProvider salesforceConnectionProvider;
	private ACValidationRule generateMandatoryAttributeRule;
	private ACValidationRule schemaNameValidationRule;
	private ACValidationRule userTableNameRule;
	private UserTableStructureService userTableStructureService;
	private SchemaDAO schemaDAO;
	private GroupDAO groupDAO;
	private UserDAO userDAO;

	public void setSalesforceConnectionProvider(SalesforceConnectionProvider salesforceConnectionProvider){
		this.salesforceConnectionProvider = salesforceConnectionProvider;
	}

	public void setGenerateMandatoryAttributeRule(ACValidationRule generateMandatoryAttributeRule){
		this.generateMandatoryAttributeRule = generateMandatoryAttributeRule;
	}

	public void setSchemaNameValidationRule(ACValidationRule schemaNameValidationRule){
		this.schemaNameValidationRule = schemaNameValidationRule;
	}

	public void setUserTableNameRule(ACValidationRule userTableNameRule){
		this.userTableNameRule = userTableNameRule;
	}

	public void setUserTableStructureService(UserTableStructureService userTableStructureService){
		this.userTableStructureService = userTableStructureService;
	}

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	public void setGroupDAO(GroupDAO groupDAO){
		this.groupDAO = groupDAO;
	}

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	@Override
	public Schema importSchema(String schemaName, List<String> objectNames, int userId, String sfUrl, String sfUsername,
			String sfPassword) throws ACException{
		checkSchemaName(schemaName);

		User user = userDAO.getById(userId);
		Schema schema = addSchema(schemaName, user);
		schemaDAO.saveOrUpdate(schema);
		final List<Group> groups = groupDAO.getGroups();
		addSchemaPrivileges(schema, groups);
		PartnerConnection connection = null;
		try{
			connection = salesforceConnectionProvider.getConnection(sfUrl, sfUsername, sfPassword);
		} catch (ConnectionException e){
			log.error(e);
		}
		if (connection != null && importSchema(schema, schemaName, groups, user, objectNames, connection)){
			updateTableNames(schema);
			schemaDAO.saveOrUpdate(schema);
			userTableStructureService.updateUserTables(schema);
		} else{
			throw new ACException(TextID.MsgCannotConnectToSalesforce);
		}
		return schema;
	}

	@Override
	public Map<String, List<String>> getAvailableSalesforceTabs(String sfUrl, String sfUsername, String sfPassword)
			throws ACException{
		final Map<String, List<String>> tabMap = new LinkedHashMap<String, List<String>>();
		try{
			PartnerConnection connection = salesforceConnectionProvider.getConnection(sfUrl, sfUsername, sfPassword);
			final DescribeTabSetResult[] dtsrs = connection.describeTabs();
			for (DescribeTabSetResult dtsr : dtsrs){
				DescribeTab[] tabs = dtsr.getTabs();
				for (DescribeTab tab : tabs){
					logTab(tab);
					if (tab.getSobjectName() != null && !tab.getSobjectName().isEmpty()){
						if (!tabMap.containsKey(dtsr.getLabel())){
							tabMap.put(dtsr.getLabel(), new ArrayList<String>());
						}
						tabMap.get(dtsr.getLabel()).add(tab.getSobjectName());
					}
				}
			}
			return tabMap;
		} catch (ConnectionException ce){
			log.error(ce);
			throw new ACException(TextID.MsgCannotConnectToSalesforce);
		}
	}

	boolean importSchema(Schema schema, String tabSet, List<Group> groups, User user, List<String> objectNames,
			PartnerConnection connection){
		try{
			final DescribeTabSetResult[] dtsrs = connection.describeTabs();

			for (DescribeTabSetResult dtsr : dtsrs){
				if (!tabSet.equals(dtsr.getLabel())){
					continue;
				}
				logTabSet(dtsr);

				DescribeTab[] tabs = dtsr.getTabs();
				for (DescribeTab tab : tabs){
					logTab(tab);
					if (tab.getSobjectName() != null && objectNames.contains(tab.getSobjectName())){
						final ObjectDefinition od = addObjectDefinition(schema, tab, user);
						fillMandatoryAttributes(groups, od);
						addObjectDefinitionPrivileges(od, groups);
						importAttributes(od, groups, connection);
					}
				}
			}
			return true;
		} catch (ConnectionException ce){
			log.error(ce);
			return false;
		}
	}

	void importAttributes(ObjectDefinition od, List<Group> groups, PartnerConnection connection) throws ConnectionException{
		DescribeSObjectResult describeSObjectResult = connection.describeSObject(od.getName());
		if (describeSObjectResult != null && describeSObjectResult.getFields() != null){
			Field[] fields = describeSObjectResult.getFields();
			for (Field field : fields){
				if (!isFieldImportable(field)){
					log.debug("Skipping field: " + field.getName() + ", label: " + field.getLabel() + ", type: "
							+ field.getType());
					continue;
				}
				logField(field);

				ObjectAttribute attr = addObjectAttribute(od, field);
				addAttributePrivileges(attr, field, groups);

				if (field.getType() == FieldType.picklist){
					PicklistEntry[] picklistValues = field.getPicklistValues();
					if (picklistValues != null && picklistValues.length > 0){
						attr.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
						for (PicklistEntry entry : picklistValues){
							addPredefinedAttribute(attr, entry);
						}
					}
				}
			}
		}
	}

	Schema addSchema(String schemaName, User user){
		Schema schema = new Schema();
		schema.setName(schemaName.replaceAll(NAME_PATTERN, ""));
		schema.setObjectDefinitions(new ArrayList<ObjectDefinition>());
		schema.setCreatedBy(user);
		schema.setCreationDate(new Date());
		return schema;
	}

	ObjectDefinition addObjectDefinition(Schema schema, DescribeTab tab, User user){
		ObjectDefinition od = new ObjectDefinition();
		od.setName(tab.getSobjectName().replaceAll(NAME_PATTERN, ""));
		od.setDescription(tab.getLabel());
		od.setSchema(schema);
		od.setObjectAttributes(new ArrayList<ObjectAttribute>());
		od.setObjectType(ObjectType.NODE);
		od.setCreatedBy(user);
		od.setCreationDate(new Date());
		od.setSort(schema.getObjectDefinitions().size() + 1);

		schema.getObjectDefinitions().add(od);

		return od;
	}

	ObjectAttribute addObjectAttribute(ObjectDefinition od, Field field){
		ObjectAttribute oa = new ObjectAttribute();
		oa.setObjectDefinition(od);
		oa.setName(field.getName());
		oa.setLabel(field.getLabel());
		final FieldType type = field.getType();
		oa.setDataType(getDataType(type));
		oa.setPredefined(type == FieldType.picklist || type == FieldType.multipicklist);
		oa.setIsMultivalue(type == FieldType.multipicklist);
		oa.setAggregable(type == FieldType._int || type == FieldType._double);

		oa.setInTable(od.getTableName());
		int sort = od.getObjectAttributes().size() + 1;
		oa.setSort(sort);
		oa.setLabelSort(sort);
		oa.setFilterSort(sort);
		oa.setSearchSort(sort);
		oa.setMatrixSort(sort);

		od.getObjectAttributes().add(oa);
		return oa;
	}

	void addPredefinedAttribute(ObjectAttribute oa, PicklistEntry entry){
		PredefinedAttribute pa = new PredefinedAttribute();
		pa.setObjectAttribute(oa);
		pa.setValue(entry.getValue());
		pa.setLabel(entry.getLabel());
		pa.setToUse(entry.getActive());
		pa.setSort(oa.getPredefinedAttributes().size() + 1);

		oa.getPredefinedAttributes().add(pa);
	}

	void fillMandatoryAttributes(List<Group> groups, ObjectDefinition od){
		addMandatoryAttributes(od);
		for (ObjectAttribute attr : od.getObjectAttributes()){
			addAttributePrivileges(attr, null, groups);
		}
	}

	boolean isFieldImportable(Field field){
		boolean importable = field != null && field.getType() != null && field.getType() != FieldType.reference
				&& field.getType() != FieldType.id;
		return importable;
	}

	DataType getDataType(final FieldType type){
		DataType dt = DataType.TEXT;
		switch (type){
			case _int:
				dt = DataType.INT;
				break;
			case _double:
			case currency:
			case percent:
				dt = DataType.DECIMAL;
				break;
			case date:
				dt = DataType.DATE;
				break;
			case url:
				dt = DataType.URL;
				break;
			case _boolean:
				dt = DataType.INT; // TODO DataType.BOOL should be set
				break;
			default:
				dt = DataType.TEXT;
		}
		return dt;
	}

	void addSchemaPrivileges(Schema schema, List<Group> groups){
		schema.setSchemaGroups(new ArrayList<SchemaGroup>());
		for (Group group : groups){
			final SchemaGroup schemaGroup = new SchemaGroup(schema, group);
			schemaGroup.setCanRead(true);
			schema.getSchemaGroups().add(schemaGroup);
		}
	}

	void addObjectDefinitionPrivileges(ObjectDefinition od, List<Group> groups){
		od.setObjectGroups(new ArrayList<ObjectGroup>());
		for (Group group : groups){
			final ObjectGroup objectGroup = new ObjectGroup(od, group);
			objectGroup.setCanRead(true);
			objectGroup.setCanCreate(true);
			objectGroup.setCanUpdate(true);
			objectGroup.setCanDelete(true);
			od.getObjectGroups().add(objectGroup);
		}
	}

	void addAttributePrivileges(ObjectAttribute attr, Field field, List<Group> groups){
		attr.setAttributeGroups(new ArrayList<AttributeGroup>());
		for (Group group : groups){
			final AttributeGroup attributeGroup = new AttributeGroup(attr, group);
			if (field == null){
				attributeGroup.setCanRead(false);
				attributeGroup.setEditingOption(EditingOption.NotVisible);
			} else{
				attributeGroup.setCanRead(true);
				if (!field.isNillable() && !field.isCalculated() && field.isCreateable() && !field.isDefaultedOnCreate()){
					attributeGroup.setEditingOption(EditingOption.Mandatory);
				} else if (field.isCreateable() || field.isUpdateable()){
					attributeGroup.setEditingOption(EditingOption.Editable);
				} else{
					attributeGroup.setEditingOption(EditingOption.NotVisible);
				}
			}
			attr.getAttributeGroups().add(attributeGroup);
		}
	}

	private void addMandatoryAttributes(ObjectDefinition od){
		SchemaAdminModel model = new SchemaAdminModel();
		model.setCurrentObjectDefinition(od);
		generateMandatoryAttributeRule.performCheck(model);
	}

	private void checkSchemaName(String schemaName) throws ACException{
		SchemaAdminModel model = new SchemaAdminModel();
		Schema schema = new Schema();
		schema.setName(schemaName);
		model.setCurrentSchema(schema);
		if (!schemaNameValidationRule.performCheck(model)){
			log.error("schema with such name already exists - cannot proceed import");
			throw new ACException(schemaNameValidationRule.getErrorEntries());
		}
	}

	private void updateTableNames(Schema schema){
		SchemaAdminModel model = new SchemaAdminModel();
		for (ObjectDefinition od : schema.getObjectDefinitions()){
			model.setCurrentObjectDefinition(od);
			userTableNameRule.performCheck(model);
		}
	}

	private void logTabSet(DescribeTabSetResult dtsr){
		if (log.isDebugEnabled()){
			log.debug("Label: " + dtsr.getLabel());
			log.debug("\tLogo URL: " + dtsr.getLogoUrl());
			log.debug("\tTab selected: " + dtsr.isSelected());
			DescribeTab[] tabs = dtsr.getTabs();
			log.debug("\tTabs defined: " + (tabs != null ? tabs.length : 0));
		}
	}

	private void logTab(DescribeTab tab){
		if (log.isDebugEnabled()){
			log.debug("\t\tName: " + tab.getSobjectName());
			log.debug("\t\tLabel: " + tab.getLabel());
		}
	}

	private void logField(Field field){
		if (log.isDebugEnabled()){
			log.debug("\t\t\tField name: " + field.getName() + ", label: " + field.getLabel() + ", data type: "
					+ field.getType());
			StringBuilder sb = new StringBuilder();
			PicklistEntry[] picklistValues = field.getPicklistValues();
			if (picklistValues != null && picklistValues.length > 0){
				for (int j = 0; j < picklistValues.length; j++){
					if (sb.length() > 0){
						sb.append("; ");
					}
					sb.append(picklistValues[j].getLabel());
				}
				log.debug("\t\t\t\tValues: " + sb);
			}
		}
	}
}
