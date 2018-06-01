/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ni3.ag.adminconsole.domain.*;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.server.dao.ObjectDefinitionDAO;
import com.ni3.ag.adminconsole.shared.service.DataGroup;
import com.ni3.ag.adminconsole.validation.ACException;

public class SchemaAdminServiceMock implements com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService{
	private ObjectDefinitionDAO objectDefinitionDAO;

	public void setObjectDefinitionDAO(ObjectDefinitionDAO objectDefinitionDAO){
		this.objectDefinitionDAO = objectDefinitionDAO;
	}

	public ObjectDefinitionDAO getObjectDefinitionDAO(){
		return objectDefinitionDAO;
	}

	@Override
	public List<Schema> getSchemas(){
		int id = 1;
		ArrayList<Schema> res = new ArrayList<Schema>();

		Schema schema1 = new Schema();
		schema1.setId(id++);
		schema1.setName("Schema1");

		List<ObjectDefinition> objectDefinitions1 = new ArrayList<ObjectDefinition>();
		ObjectDefinition child1 = new ObjectDefinition();
		child1.setId(id++);
		child1.setName("ObjectDefinition1");
		child1.setSchema(schema1);
		child1.setObjectAttributes(generateObjectAttributes(child1, 1, 10));
		child1.setObjectType(ObjectType.NODE);
		objectDefinitions1.add(child1);

		ObjectDefinition child2 = new ObjectDefinition();
		child2.setId(id++);
		child2.setName("ObjectDefinition2");
		child2.setSchema(schema1);
		child2.setObjectAttributes(generateObjectAttributes(child2, 50, 10));
		child2.setObjectType(ObjectType.NODE);
		objectDefinitions1.add(child2);

		ObjectDefinition child3 = new ObjectDefinition();
		child3.setId(id++);
		child3.setName("ObjectDefinition3");
		child3.setSchema(schema1);
		child3.setObjectType(ObjectType.NODE);
		objectDefinitions1.add(child3);

		schema1.setObjectDefinitions(objectDefinitions1);
		res.add(schema1);

		Schema schema2 = new Schema();
		schema2.setId(id++);
		schema2.setName("Schema2");
		List<ObjectDefinition> objectDefinitions2 = new ArrayList<ObjectDefinition>();

		ObjectDefinition child4 = new ObjectDefinition();
		child4.setId(id++);
		child4.setName("ObjectDefinition1");
		child4.setSchema(schema2);
		child4.setObjectAttributes(generateObjectAttributes(child4, 100, 10));
		child4.setObjectType(ObjectType.NODE);
		objectDefinitions2.add(child4);

		ObjectDefinition child5 = new ObjectDefinition();
		child5.setId(id++);
		child5.setName("ObjectDefinition2");
		child5.setSchema(schema2);
		child5.setObjectType(ObjectType.NODE);
		objectDefinitions2.add(child5);

		ObjectDefinition child6 = new ObjectDefinition();
		child6.setId(id++);
		child6.setName("ObjectDefinition3");
		child6.setSchema(schema2);
		child6.setObjectType(ObjectType.NODE);
		objectDefinitions2.add(child6);

		schema2.setObjectDefinitions(objectDefinitions2);
		res.add(schema2);

		return res;
	}

	private List<ObjectAttribute> generateObjectAttributes(ObjectDefinition parent, int baseId, int count){
		ArrayList<ObjectAttribute> attrs = new ArrayList<ObjectAttribute>();
		for (int i = 0; i < count; i++){
			ObjectAttribute oa = new ObjectAttribute(parent);
			oa.setId(baseId + i);
			oa.setSort(1);
			oa.setName("attr" + oa.getId());
			oa.setLabel("label" + oa.getId());
			oa.setPredefined(Boolean.TRUE);
			oa.setDescription(oa.getName());
			oa.setDataType(DataType.TEXT);
			oa.setInFilter(true);
			oa.setInLabel(true);
			oa.setInToolTip(true);
			oa.setInSimpleSearch(true);
			oa.setInAdvancedSearch(true);
			oa.setInMetaphor(true);
			attrs.add(oa);
		}
		return attrs;
	}

	@Override
	public ObjectDefinition addObjectDefinition(Schema arg1, String arg2, User user){
		return null;
	}

	@Override
	public Schema copySchema(Integer arg0, String arg1, User user) throws ACException{
		return null;
	}

	@Override
	public void deleteObject(Integer id, boolean force) throws ACException{
		// TODO Auto-generated method stub
	}

	@Override
	public void deleteSchema(Integer id) throws ACException{
		// TODO Auto-generated method stub
	}

	@Override
	public ErrorContainer validateAttributesDelete(List<ObjectAttribute> attrIds){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectDefinition updateObjectDefinition(ObjectDefinition od, boolean b) throws ACException{
		return null;
		// TODO Auto-generated method stub
	}

	@Override
	public ObjectDefinition loadSingleObjectDefinition(Integer id) throws ACException{
		// TODO Auto-generated method stub
		return null;
	}

	public void updateSchemaName(Integer id, String schemaName){
	}

	@Override
	public void updateSchema(Schema schema){
		// TODO Auto-generated method stub

	}

	@Override
	public void importSchemaFromXML(String xml) throws ACException{
	}

	@Override
	public void dropUserTables(List<String> tables) throws ACException{
		// TODO Auto-generated method stub

	}

	@Override
	public Schema loadSingleSchema(Integer id){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Schema addSchema(String name, User user) throws ACException{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AttributeGroup> getAttributeGroups(Integer id){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExportData getSchemaExport(String name){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExportData exportUserDataToXLS(String schemaName, User user) throws ACException{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateCache(String navHost, User user, Integer id) throws ACException{
		// TODO Auto-generated method stub

	}

	@Override
	public void importSchemaFromXLS(byte[] data, String schemaName, User user) throws ACException{
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] exportUserDataToCSV(ObjectDefinition od, User user, String cs, String ls) throws ACException{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] exportSchemaToXML(Integer schemaId) throws ACException{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInvalidationRequired(DataGroup gr, boolean required){
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isInvalidationRequired(DataGroup gr){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAnyInvalidationRequired(){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetAnyInvalidationRequired(){
		// TODO Auto-generated method stub

	}

	@Override
	public void setAllInvalidationRequired(boolean b, DataGroup... dataGroups){
		// TODO Auto-generated method stub

	}

	@Override
	public Set<DataGroup> getInvalidationRequiredGroups(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorContainer generateSchema(Integer schemaDefinitionId, Integer objectId){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorContainer updateUserTables(Integer schemaId, Integer objectId){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Schema reloadFullSchema(Schema currentSchema){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, List<String>> getAvailableSalesforceTabs(String url, String username, String password)
			throws ACException{
		// TODO Auto-generated method stub
		return null;
	}

	public Schema importSchemaFromSalesforce(String schemaName, List<String> objectNames, int userId, String url,
			String username, String password) throws ACException{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DataSource> getDataSources(){
		return null; // To change body of implemented methods use File | Settings | File Templates.
	}

	/* (non-Javadoc)
	 * @see com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService#importUserDataFromCSV(java.util.List, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public void importUserDataFromCSV(List<String> lines, Integer schemaId, Integer userId, String fileName,
			String columnSeparator, boolean recalculateFormulas) throws ACException{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService#importUserDataFromXLS(byte[], java.lang.Integer, java.lang.Integer, boolean)
	 */
	@Override
	public void importUserDataFromXLS(byte[] data, Integer schemaId, Integer userId, boolean recalculateFormulas)
			throws ACException{
		// TODO Auto-generated method stub
		
	}

}
