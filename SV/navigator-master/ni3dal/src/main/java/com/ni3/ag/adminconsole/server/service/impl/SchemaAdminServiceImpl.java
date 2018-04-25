/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;
import java.util.Map;
import javax.sql.DataSource;

import com.ni3.ag.adminconsole.domain.*;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.remoting.ThreadLocalStorage;
import com.ni3.ag.adminconsole.server.dao.*;
import com.ni3.ag.adminconsole.server.datasource.ACRoutingDataSource;
import com.ni3.ag.adminconsole.server.datasource.InstanceDescriptor;
import com.ni3.ag.adminconsole.server.dbservice.UserTableStructureService;
import com.ni3.ag.adminconsole.server.exporters.SchemaExporter;
import com.ni3.ag.adminconsole.server.importers.xml.DataImporter;
import com.ni3.ag.adminconsole.server.lifecycle.CacheInvalidationStore;
import com.ni3.ag.adminconsole.server.service.*;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.shared.service.DataGroup;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import com.ni3.ag.adminconsole.validation.ErrorContainerImpl;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

public class SchemaAdminServiceImpl implements SchemaAdminService{
	private static Logger log = Logger.getLogger(SchemaAdminServiceImpl.class);

	private static final String HTTP_PROTOCOL = "http://";

	private ObjectDefinitionDAO objectDefinitionDAO;
	private SchemaDAO schemaDAO;
	private AttributeGroupDAO attributeGroupDAO;
	private ObjectConnectionDAO objectConnectionDAO;
	private GroupDAO groupDAO;
	private NodeDAO nodeDAO;
	private EdgeDAO edgeDAO;
	private DataSourceDAO dataSourceDAO;

	private CopySchemaService copySchemaService;

	private ACValidationRule attributeGroupRule;
	private ACValidationRule attributeFavoriteReferenceRule;
	private ACValidationRule objectFavoriteReferenceRule;
	private ACValidationRule objectChartReferenceRule;

	private String servletAddress;

	private DataSource dataSource;
	private SchemaExporter schemaExporter;
	private UserDAO userDAO;
	private XLSUserDataExporter xlsUserDataExporter;
	private CSVUserDataExporter csvUserDataExporter;
	private CSVUserDataImporter csvUserDataImporter;
	private SalesforceSchemaImporter salesforceSchemaImporter;
	private DataImporter xmlSchemaImporter;
	private com.ni3.ag.adminconsole.server.exporters.xml.SchemaExporter xmlSchemaExporter;
	private ExcelUserDataImporter excelUserDataImporter;
	private ExcelSchemaImporter excelSchemaImporter;
	private ObjectDefinitionService objectDefinitionService;
	private UserTableStructureService userTableStructureService;

	public void setDataSourceDAO(DataSourceDAO dataSourceDAO){
		this.dataSourceDAO = dataSourceDAO;
	}

	public void setObjectDefinitionService(ObjectDefinitionService objectDefinitionService){
		this.objectDefinitionService = objectDefinitionService;
	}

	public void setExcelSchemaImporter(ExcelSchemaImporter excelSchemaImporter){
		this.excelSchemaImporter = excelSchemaImporter;
	}

	public void setExcelUserDataImporter(ExcelUserDataImporter excelUserDataImporter){
		this.excelUserDataImporter = excelUserDataImporter;
	}

	public void setXmlSchemaImporter(DataImporter xmlSchemaImporter){
		this.xmlSchemaImporter = xmlSchemaImporter;
	}

	public void setXlsUserDataExporter(XLSUserDataExporter xlsUserDataExporter){
		this.xlsUserDataExporter = xlsUserDataExporter;
	}

	public void setCsvUserDataExporter(CSVUserDataExporter csvUserDataExporter){
		this.csvUserDataExporter = csvUserDataExporter;
	}

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	public SchemaExporter getSchemaExporter(){
		return schemaExporter;
	}

	public void setSchemaExporter(SchemaExporter schemaExporter){
		this.schemaExporter = schemaExporter;
	}

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}

	public String getServletAddress(){
		return servletAddress;
	}

	public void setServletAddress(String servletAddress){
		this.servletAddress = servletAddress;
	}

	public void setAttributeGroupRule(ACValidationRule attributeGroupRule){
		this.attributeGroupRule = attributeGroupRule;
	}

	public void setAttributeFavoriteReferenceRule(ACValidationRule attributeFavoriteReferenceRule){
		this.attributeFavoriteReferenceRule = attributeFavoriteReferenceRule;
	}

	public void setObjectFavoriteReferenceRule(ACValidationRule objectFavoriteReferenceRule){
		this.objectFavoriteReferenceRule = objectFavoriteReferenceRule;
	}

	public void setObjectChartReferenceRule(ACValidationRule objectChartReferenceRule){
		this.objectChartReferenceRule = objectChartReferenceRule;
	}

	public void setAttributeGroupDAO(AttributeGroupDAO attributeGroupDAO){
		this.attributeGroupDAO = attributeGroupDAO;
	}

	public void setObjectDefinitionDAO(ObjectDefinitionDAO objectDefinitionDAO){
		this.objectDefinitionDAO = objectDefinitionDAO;
	}

	public SchemaDAO getSchemaDAO(){
		return schemaDAO;
	}

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	public void setObjectConnectionDAO(ObjectConnectionDAO objectConnectionDAO){
		this.objectConnectionDAO = objectConnectionDAO;
	}

	public void setGroupDAO(GroupDAO groupDAO){
		this.groupDAO = groupDAO;
	}

	public void setCopySchemaService(CopySchemaService copySchemaService){
		this.copySchemaService = copySchemaService;
	}

	public void setCsvUserDataImporter(CSVUserDataImporter csvUserDataImporter){
		this.csvUserDataImporter = csvUserDataImporter;
	}

	public void setXmlSchemaExporter(com.ni3.ag.adminconsole.server.exporters.xml.SchemaExporter xmlSchemaExporter){
		this.xmlSchemaExporter = xmlSchemaExporter;
	}

	public void setSalesforceSchemaImporter(SalesforceSchemaImporter salesforceSchemaImporter){
		this.salesforceSchemaImporter = salesforceSchemaImporter;
	}

	public void setNodeDAO(NodeDAO nodeDAO){
		this.nodeDAO = nodeDAO;
	}

	public void setEdgeDAO(EdgeDAO edgeDAO){
		this.edgeDAO = edgeDAO;
	}

	public void setUserTableStructureService(UserTableStructureService userTableStructureService){
		this.userTableStructureService = userTableStructureService;
	}

	@Override
	public List<Schema> getSchemas(){
		List<Schema> schemas = schemaDAO.getSchemas();
		for (Schema schema : schemas){
			Hibernate.initialize(schema.getObjectDefinitions());

			List<ObjectDefinition> objectDefinitions = schema.getObjectDefinitions();
			for (ObjectDefinition objectDefinition : objectDefinitions){
				Hibernate.initialize(objectDefinition.getObjectAttributes());
				for (ObjectAttribute oa : objectDefinition.getObjectAttributes())
					Hibernate.initialize(oa.getPredefinedAttributes());
				Hibernate.initialize(objectDefinition.getMetaphors());
				for (Metaphor m : objectDefinition.getMetaphors())
					Hibernate.initialize(m.getMetaphorData());
			}

		}
		return schemas;
	}

	@Override
	public Schema copySchema(Integer id, String newName, User user) throws ACException{
		Schema newSchema = null;
		try{
			newSchema = copySchemaService.copySchema(id, newName, user);
			generateSchema(newSchema.getId(), 0);
		} catch (CloneNotSupportedException e){
			log.error("could not copy schema", e);
			throw new ACException(TextID.MsgCantCopySchema, new String[] { e.getMessage() });
		}
		return newSchema;
	}

	@Override
	public ObjectDefinition addObjectDefinition(Schema parent, String name, User user) throws ACException{
		return objectDefinitionService.addObjectDefinition(parent, name, user);
	}

	@Override
	public Schema addSchema(String name, User user) throws ACException{
		log.info("AddSchema");
		user = userDAO.getById(user.getId());

		Schema schema = new Schema();
		schema.setCreationDate(new Date());
		schema.setName(name);
		schema.setDescription(name);
		schema.setCreatedBy(user);

		List<Group> groups = groupDAO.getGroups();
		schema.setSchemaGroups(new ArrayList<SchemaGroup>());
		for (Group g : groups){
			SchemaGroup sg = new SchemaGroup(schema, g);
			sg.setCanRead(false);
			schema.getSchemaGroups().add(sg);
		}
		schema = schemaDAO.saveOrUpdate(schema);

		return schema;
	}

	protected ObjectAttribute getNewObjectAttribute(ObjectAttribute origAttr, ObjectDefinition cloneObject){
		List<ObjectAttribute> cloneAttrs = cloneObject.getObjectAttributes();
		if (cloneAttrs != null){
			for (ObjectAttribute cloneAttr : cloneAttrs){
				if (cloneAttr.getName().equals(origAttr.getName())){
					return cloneAttr;
				}
			}
		}
		return null;
	}

	@Override
	public void deleteSchema(Integer id) throws ACException{
		log.info("DeleteSchemaObjectDefinition");
		ACRoutingDataSource dSource = (ACRoutingDataSource) dataSource;
		if (dSource.isCluster()){
			throw new ACException(TextID.MsgCantDeleteOnClusteredInstance);
		}
		Schema schema = schemaDAO.getSchema(id);
		schema.setCreatedBy(null);
		deleteSchemaDependences(schema);
		schemaDAO.deleteSchema(schema);
		List<String> ss = new ArrayList<String>();
		for (ObjectDefinition child : schema.getObjectDefinitions()){
			ss.add(child.getTableName());
			if (child.hasContextAttributes())
				ss.add(child.getTableName() + ObjectAttribute.CONTEXT_TABLE_SUFFIX);
		}
		if (!ss.isEmpty())
			userTableStructureService.dropUserTables(ss);
		log.debug("deleted");
	}

	@Override
	public void deleteObject(Integer id, boolean force) throws ACException{
		log.info("DeleteObjectDefinition");
		ACRoutingDataSource dSource = (ACRoutingDataSource) dataSource;
		if (dSource.isCluster()){
			throw new ACException(TextID.MsgCantDeleteOnClusteredInstance);
		}

		ObjectDefinition od = objectDefinitionDAO.getObjectDefinition(id);

		if (!force){
			SchemaAdminModel model = new SchemaAdminModel();
			model.setCurrentObjectDefinition(od);

			if (!objectFavoriteReferenceRule.performCheck(model)){
				log.error("Cannot delete object, it is referenced from favorites");
				throw new ACException(objectFavoriteReferenceRule.getErrorEntries());
			}
			if (od.isNode() && !objectChartReferenceRule.performCheck(model)){
				log.error("Cannot delete object, it is referenced from sys_object_chart");
				throw new ACException(objectChartReferenceRule.getErrorEntries());
			}
		}

		deleteObjectDependences(od, force);

		od.getSchema().getObjectDefinitions().remove(od);
		objectDefinitionDAO.deleteObject(od);
		List<String> ss = new ArrayList<String>();
		ss.add(od.getTableName());
		if (od.hasContextAttributes())
			ss.add(od.getTableName() + ObjectAttribute.CONTEXT_TABLE_SUFFIX);
		userTableStructureService.dropUserTables(ss);
		log.debug("deleted");
	}

	@Override
	public ErrorContainer validateAttributesDelete(List<ObjectAttribute> attributes){
		log.debug("attribute collection: " + attributes);
		SchemaAdminModel model = new SchemaAdminModel();
		ObjectDefinition od = new ObjectDefinition();
		od.setObjectAttributes(attributes);
		model.setCurrentObjectDefinition(od);

		attributeGroupRule.performCheck(model);
		ErrorContainer ec = new ErrorContainerImpl();
		List<ErrorEntry> errors = ec.getErrors();
		errors.addAll(attributeGroupRule.getErrorEntries());

		model.setAttributesToDelete(attributes);
		attributeFavoriteReferenceRule.performCheck(model);
		errors.addAll(attributeFavoriteReferenceRule.getErrorEntries());

		return ec;
	}

	@Override
	public ObjectDefinition updateObjectDefinition(ObjectDefinition od, boolean ignoreUserData) throws ACException{
		return objectDefinitionService.updateObjectDefinition(od, ignoreUserData);
	}

	@Override
	public ErrorContainer generateSchema(Integer schemaId, Integer objectId){
		ErrorContainer result = null;
		log.debug("Generate Schema schema(" + schemaId + ") object(" + objectId + ")");
		ACRoutingDataSource dSource = (ACRoutingDataSource) dataSource;
		if (dSource.isCluster()){
			InstanceDescriptor id = dSource.getCurrentInstanceDescriptor();
			String currentDsId = id.getDsIdentifier();
			List<String> dsids = id.getDsIdentifiers();
			for (int i = 0; i < dsids.size(); i++){
				id.setCurrentDsIdentifier(dsids.get(i));
				result = updateUserTables(schemaId, objectId);
			}
			id.setCurrentDsIdentifier(currentDsId);
		} else{
			result = updateUserTables(schemaId, objectId);
		}
		return result;
	}

	@Override
	public ErrorContainer updateUserTables(Integer schemaId, Integer objectId){
		if (objectId == null || objectId <= 0){
			Schema schema = schemaDAO.getSchema(schemaId);
			return userTableStructureService.updateUserTables(schema);
		} else{
			ObjectDefinition object = objectDefinitionDAO.getObjectDefinition(objectId);
			return userTableStructureService.updateUserTable(object);
		}
	}

	@Override
	public ObjectDefinition loadSingleObjectDefinition(Integer id) throws ACException{
		log.debug("User requested load ObjectDefinition with id: " + id);
		ObjectDefinition od = objectDefinitionDAO.getObjectDefinition(id);
		if (od == null)
			throw new ACException(TextID.MsgObjectWithIdNotFound, new String[] { id != null ? id.toString() : "null" });
		Hibernate.initialize(od.getObjectAttributes());
		Hibernate.initialize(od.getMetaphors());
		for (ObjectAttribute oa : od.getObjectAttributes())
			Hibernate.initialize(oa.getPredefinedAttributes());
		for (Metaphor m : od.getMetaphors())
			Hibernate.initialize(m.getMetaphorData());
		log.debug("Object loaded: " + od);
		return od;
	}

	@Override
	public Schema loadSingleSchema(Integer id){
		log.debug("User requested load ObjectDefinition with id: " + id);
		Schema schema = schemaDAO.getSchema(id);
		Hibernate.initialize(schema.getObjectDefinitions());
		for (ObjectDefinition object : schema.getObjectDefinitions()){
			Hibernate.initialize(object.getObjectAttributes());
			Hibernate.initialize(object.getMetaphors());
			for (ObjectAttribute oa : object.getObjectAttributes())
				Hibernate.initialize(oa.getPredefinedAttributes());
			for (Metaphor m : object.getMetaphors())
				Hibernate.initialize(m.getMetaphorData());
		}
		return schema;
	}

	@Override
	public void updateSchema(Schema schema){
		getSchemaDAO().saveOrUpdate(schema);
	}

	@Override
	public void dropUserTables(List<String> tables) throws ACException{
		ACRoutingDataSource dSource = (ACRoutingDataSource) dataSource;
		if (dSource.isCluster()){
			throw new ACException(TextID.MsgCantDeleteOnClusteredInstance);
		}
		userTableStructureService.dropUserTables(tables);
	}

	private void deleteObjectDependences(ObjectDefinition object, boolean force) throws ACException{
		log.debug("Deleting dependences for object " + object.getName());

		if (object.isNode()){
			nodeDAO.deleteReferencedCisNodes(object);
			if (force)
				objectDefinitionDAO.deleteObjectChartsByObject(object);
		} else
			edgeDAO.deleteReferencedCisEdges(object);
		List<ObjectConnection> connections = objectConnectionDAO.getConnectionsByObject(object);
		objectConnectionDAO.deleteAll(connections);
	}

	private void deleteSchemaDependences(Schema schema) throws ACException{
		log.debug("Deleting dependences for schema " + schema.getName());

		List<ObjectDefinition> objects = schema.getObjectDefinitions();
		if (objects != null && objects.size() > 0){
			for (ObjectDefinition object : objects){
				deleteObjectDependences(object, false);
			}
		}
	}

	@Override
	public List<AttributeGroup> getAttributeGroups(Integer id){
		return attributeGroupDAO.getAttributeGroups(id);
	}

	@Override
	public ExportData getSchemaExport(String name) throws ACException{
		Schema sch = schemaDAO.getSchemaByName(name);
		if (sch == null)
			throw new ACException(TextID.MsgSchemaDoesNotExists, new String[] { name });
		ByteArrayOutputStream os = schemaExporter.exportSchema(sch);
		return new ExportData(name, os.toByteArray());
	}

	@Override
	public void importSchemaFromXML(String xml) throws ACException{
		xmlSchemaImporter.makeImport(xml);
	}

	@Override
	public byte[] exportSchemaToXML(Integer schemaId) throws ACException{
		return xmlSchemaExporter.exportSchema(schemaId);
	}

	@Override
	public void importUserDataFromXLS(byte[] data, Integer schemaId, Integer userId, boolean recalculateFormulas)
			throws ACException{
		excelUserDataImporter.importDataFromExcel(data, schemaId, userId, recalculateFormulas);
	}

	@Override
	public void importUserDataFromCSV(List<String> lines, Integer schemaId, Integer userId, String fileName,
			String columnSeparator, boolean recalculateFormulas) throws ACException{
		csvUserDataImporter.importDataFromCSV(lines, schemaId, userId, fileName, columnSeparator, recalculateFormulas);
	}

	@Override
	public void importSchemaFromXLS(byte[] data, String schemaName, User user) throws ACException{
		excelSchemaImporter.importExcelSchema(data, schemaName, user);
	}

	@Override
	public ExportData exportUserDataToXLS(String schemaName, User user) throws ACException{
		user = userDAO.getById(user.getId());
		Hibernate.initialize(user.getGroups());
		if (user.getGroups() == null || user.getGroups().isEmpty())
			throw new ACException(TextID.MsgUserNotFound);
		Schema sch = schemaDAO.getSchemaByName(schemaName);
		if (sch == null)
			throw new ACException(TextID.MsgSchemaDoesNotExists, new String[] { schemaName });
		return new ExportData(schemaName, xlsUserDataExporter.performAction(sch, user).toByteArray());
	}

	@Override
	public byte[] exportUserDataToCSV(ObjectDefinition od, User user, String columnSeparator, String lineSeparator)
			throws ACException{
		user = userDAO.getById(user.getId());
		Hibernate.initialize(user.getGroups());
		if (user.getGroups() == null || user.getGroups().isEmpty())
			throw new ACException(TextID.MsgUserNotFound);
		return csvUserDataExporter.performAction(od, user, columnSeparator, lineSeparator);
	}

	@Override
	public void updateCache(String navHost, User user, Integer schemaId) throws ACException{
		log.info("Update cache request: " + navHost);
		String dbid = ThreadLocalStorage.getInstance().getCurrentDatabaseInstanceId();
		if (!navHost.startsWith("http")){
			navHost = HTTP_PROTOCOL + navHost;
		}
		String param = "ReloadSchema=1" + (schemaId != null ? "&Schema=" + schemaId : "");
		updateCache(navHost, dbid, user, servletAddress, param);
		param = "ReloadGraph=1" + (schemaId != null ? "&Schema=" + schemaId : "");
		updateCache(navHost, dbid, user, servletAddress, param);
	}

	private void updateCache(String navHost, String dbid, User user, String sAddress, String param) throws ACException{
		try{
			String requestString = navHost + sAddress;
			requestString += "?";
			requestString += addURLParam("User", user.getUserName()) + "&";
			requestString += param;
			URL url = new URL(requestString);
			URLConnection uConn = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(uConn.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null){
				if (inputLine.equals(TextID.MsgDBIDNotConfigured.toString())){
					log.error("Cannot reload cache, dbid with name " + dbid + " is not configured in Navigator's server");
					throw new ACException(TextID.MsgDBIDNotConfigured, new String[] { dbid });
				}
				log.info(inputLine);
			}
			in.close();
		} catch (MalformedURLException e){
			log.error("", e);
			throw new ACException(TextID.MsgInvalidNavigatorURL);
		} catch (UnsupportedEncodingException e){
			log.error("", e);
			throw new ACException(TextID.MsgInvalidNavigatorURL);
		} catch (IOException e){
			log.error("", e);
			throw new ACException(TextID.MsgErrorRequestNavigator);
		}
	}

	private String addURLParam(String name, String val) throws UnsupportedEncodingException{
		return name + "=" + (val != null ? URLEncoder.encode(val, "UTF-8") : "");
	}

	public List<ObjectAttribute> getNewAttributes(List<ObjectAttribute> object){
		return objectDefinitionService.getNewAttributes(object);
	}

	@Override
	public void setInvalidationRequired(DataGroup gr, boolean required){
		CacheInvalidationStore.getInstance().setInvalidationRequired(gr, required);
	}

	@Override
	public boolean isInvalidationRequired(DataGroup gr){
		return CacheInvalidationStore.getInstance().isInvalidationRequired(gr);
	}

	@Override
	public boolean isAnyInvalidationRequired(){
		return CacheInvalidationStore.getInstance().isAnyInvalidationRequired();
	}

	@Override
	public void resetAnyInvalidationRequired(){
		CacheInvalidationStore.getInstance().resetAnyInvalidationRequired();
	}

	@Override
	public void setAllInvalidationRequired(boolean b, DataGroup... dataGroups){
		for (DataGroup gr : dataGroups){
			CacheInvalidationStore.getInstance().setInvalidationRequired(gr, b);
		}
	}

	@Override
	public Set<DataGroup> getInvalidationRequiredGroups(){
		return CacheInvalidationStore.getInstance().getAllInvalidationRequered();
	}

	@Override
	public Schema reloadFullSchema(Schema currentSchema){
		Schema schema = schemaDAO.getSchema(currentSchema.getId());

		Hibernate.initialize(schema.getObjectDefinitions());
		List<ObjectDefinition> objectDefinitions = schema.getObjectDefinitions();
		for (ObjectDefinition objectDefinition : objectDefinitions){
			Hibernate.initialize(objectDefinition.getObjectAttributes());
			List<ObjectAttribute> attributes = objectDefinition.getObjectAttributes();
			for (ObjectAttribute attribute : attributes){
				Hibernate.initialize(attribute.getPredefinedAttributes());
			}
			Hibernate.initialize(objectDefinition.getMetaphors());
			for (Metaphor m : objectDefinition.getMetaphors())
				Hibernate.initialize(m.getMetaphorData());
		}
		return schema;
	}

	@Override
	public Schema importSchemaFromSalesforce(String schemaName, List<String> objectNames, int userId, String url,
			String username, String password) throws ACException{
		return salesforceSchemaImporter.importSchema(schemaName, objectNames, userId, url, username, password);
	}

	@Override
	public List<com.ni3.ag.adminconsole.domain.DataSource> getDataSources(){
		return dataSourceDAO.getDataSources();
	}

	@Override
	public Map<String, List<String>> getAvailableSalesforceTabs(String url, String username, String password)
			throws ACException{
		return salesforceSchemaImporter.getAvailableSalesforceTabs(url, username, password);
	}
}