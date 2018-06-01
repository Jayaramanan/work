/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.xml;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.server.datasource.ACRoutingDataSource;
import com.ni3.ag.adminconsole.server.datasource.InstanceDescriptor;
import com.ni3.ag.adminconsole.server.dbservice.UserTableStructureService;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class SchemaObjectImporter extends AbstractImporter{
	private final static Logger log = Logger.getLogger(SchemaObjectImporter.class);
	private SchemaDAO schemaDAO;
	private UserTableStructureService userTableStructureService;

	// =============--------------
	// embedded importers
	// ===========------------
	private AbstractImporter objectDefinitionImporter;
	private AbstractImporter groupImporter;
	private AbstractImporter connectionImporter;

	private ACValidationRule schemaNameValidationRule;

	private User owner;

	private DataSource dataSource;

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}

	public void setOwner(User owner){
		this.owner = owner;
	}

	public void setConnectionImporter(AbstractImporter connectionImporter){
		this.connectionImporter = connectionImporter;
	}

	@Override
	protected Map<String, AbstractImporter> getTagToEmbeddedImporterMapping(){
		Map<String, AbstractImporter> map = new LinkedHashMap<String, AbstractImporter>();
		map.put("objectDefinition", objectDefinitionImporter);
		map.put("connection", connectionImporter);
		map.put("group", groupImporter);
		return map;
	}

	public ErrorContainer importData(NodeList nodeList){
		ErrorContainer errorContainer = super.importData(nodeList, true);
		processEmbeddedImporters();

		if (!objects.isEmpty()){
			Schema schema = (Schema) objects.get(0);

			ACRoutingDataSource dSource = (ACRoutingDataSource) dataSource;
			if (dSource.isCluster()){
				InstanceDescriptor id = dSource.getCurrentInstanceDescriptor();
				String currentDsId = id.getDsIdentifier();
				List<String> dsids = id.getDsIdentifiers();
				for (int i = 0; i < dsids.size(); i++){
					id.setCurrentDsIdentifier(dsids.get(i));
					userTableStructureService.updateUserTables(schema);
				}
				id.setCurrentDsIdentifier(currentDsId);
			} else{
				userTableStructureService.updateUserTables(schema);
			}
		}
		return errorContainer;
	}

	public void setUserTableStructureService(UserTableStructureService userTableStructureService){
		this.userTableStructureService = userTableStructureService;
	}

	public ACValidationRule getSchemaNameValidationRule(){
		return schemaNameValidationRule;
	}

	public void setSchemaNameValidationRule(ACValidationRule schemaNameValidationRule){
		this.schemaNameValidationRule = schemaNameValidationRule;
	}

	public SchemaDAO getSchemaDAO(){
		return schemaDAO;
	}

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	public AbstractImporter getObjectDefinitionImporter(){
		return objectDefinitionImporter;
	}

	public void setObjectDefinitionImporter(AbstractImporter objectDefinitionImporter){
		this.objectDefinitionImporter = objectDefinitionImporter;
	}

	public AbstractImporter getGroupImporter(){
		return groupImporter;
	}

	public void setGroupImporter(AbstractImporter groupImporter){
		this.groupImporter = groupImporter;
	}

	@Override
	public Object getObjectFromXML(Node node){
		String schemaName = node.getAttributes().getNamedItem("name").getNodeValue();
		log.debug("importing schema `" + schemaName + "`");
		// update
		Schema update = schemaDAO.getSchemaByName(schemaName);
		if (update == null){
			update = new Schema();
			update.setName(schemaName);
			update.setCreatedBy(owner);
			update.setCreationDate(new Date());
		}
		Node descrNode = node.getAttributes().getNamedItem("description");
		if (descrNode != null)
			update.setDescription(descrNode.getTextContent());

		return update;
	}

	@Override
	protected void persist(Object o){
		Schema s = schemaDAO.saveOrUpdate((Schema) o);
		setCurrentSchema(s);
	}

	@Override
	protected boolean validateObject(Object o, Node node){
		SchemaAdminModel model = new SchemaAdminModel();
		model.setCurrentSchema((Schema) o);
		schemaNameValidationRule.performCheck(model);
		errorContainer.addAllErrors(schemaNameValidationRule.getErrorEntries());
		return errorContainer.getErrors().isEmpty();
	}

	@Override
	public String[] getMandatoryXMLAttributes(){
		return new String[] { "name" };
	}
}
