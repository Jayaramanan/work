package com.ni3.ag.navigator.server.services.impl;

import java.util.ArrayList;

import com.ni3.ag.navigator.server.domain.*;
import com.ni3.ag.navigator.server.search.AdvancedCriteria;
import com.ni3.ag.navigator.server.util.PrivateAccessor;
import com.ni3.ag.navigator.shared.domain.DataType;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SearchServiceImplTest extends TestCase{
	private SearchServiceImpl searchService;

	@Override
	public void setUp() throws Exception{
		searchService = new SearchServiceImpl();
	}

//
//	private Entity createObjectDef(int id){
//		Entity e = new Entity();
//		e.setObjectPermissions(new ArrayList<ObjectDefinitionGroup>());
//		ObjectDefinitionGroup odg = new ObjectDefinitionGroup();
//		odg.setCanRead(true);
//		odg.setGroupId(500);
//		odg.setObjectId(id);
//		e.getObjectPermissions().add(odg);
//		e.setId(id);
//		e.setAttributes(new ArrayList<Attribute>());
//		for(int i = 1; i <= 5; i++){
//			Attribute a = new Attribute();
//			a.setDatatype(DataType.TEXT);
//			a.setId((id * 10) + i);
//			a.setInSimpleSearch(i % 2 == 1);
//			a.setName("attr" + id + "." + i);
//			e.getAttributes().add(a);
//		}
//		return e;
//	}
//
//	public void testContainsTermModificator(){
//		Object[][] data = new Object[][]{
//				{new String[]{}, false},
//				{new String[]{""}, false},
//				{new String[]{"a"}, false},
//				{new String[]{"a", "b", "c"}, false},
//				{new String[]{"+a", "b"}, true},
//				{new String[]{"a", "-c"}, true},
//				{new String[]{"a", "+a"}, true},
//				{new String[]{"a", "a-a", "a+a"}, false}
//		};
//		for(Object[] d : data){
//			assertEquals(d[1], PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "containsTermModificator", d[0]));
//		}
//	}
//
//
//	public void testGenerateContextSelectSQL() throws Exception{
//		Entity e = new Entity();
//		int contextId = 1;
//		String key = "6";
//		Object simpleSearch = PrivateAccessor.invokePrivateMethod(searchService, "generateContextSelectSQL", e, contextId, key);
//		assertNull(simpleSearch);
//
//		Context c = new Context();
//		c.setId(1);
//		c.setAttributes(new ArrayList<Attribute>());
//		c.setTablename("zzz");
//
//		Attribute pk = new Attribute(1);
//		pk.setDatatype(DataType.INT);
//		pk.setName("conPK");
//		c.getAttributes().add(pk);
//		c.setPkAttribute(pk);
//
//		Attribute a = new Attribute(2);
//		a.setName("attr2");
//		c.getAttributes().add(a);
//
//		e.setContexts(new ArrayList<Context>());
//		e.getContexts().add(c);
//		contextId = 2;
//		simpleSearch = PrivateAccessor.invokePrivateMethod(searchService, "generateContextSelectSQL", e, contextId, key);
//		assertNull(simpleSearch);
//
//		contextId = 1;
//		simpleSearch = PrivateAccessor.invokePrivateMethod(searchService, "generateContextSelectSQL", e, contextId, key);
//
//		String expectedSql = "SELECT a.ID,a.conPK,a.attr2 FROM zzz a WHERE a.conPK=6";
//		String sql = (String) PrivateAccessor.getPrivateField(simpleSearch, "sql");
//		assertEquals(sql, expectedSql);
//
//		pk.setDatatype(DataType.TEXT);
//		simpleSearch = PrivateAccessor.invokePrivateMethod(searchService, "generateContextSelectSQL", e, contextId, key);
//		expectedSql = "SELECT a.ID,a.conPK,a.attr2 FROM zzz a WHERE a.conPK ILIKE ?";
//		sql = (String) PrivateAccessor.getPrivateField(simpleSearch, "sql");
//		assertEquals(sql, expectedSql);
//
//		List<String> params = (List<String>) PrivateAccessor.getPrivateField(simpleSearch, "params");
//		assertEquals(1, params.size());
//		assertEquals("6", params.get(0));
//	}
//
//	public void testGenerateSelectSQL() throws Exception{
//		List<String> params = new ArrayList<String>();
//		List<Attribute> includedAttributes = new ArrayList<Attribute>();
//
//		Entity entity = new Entity();
//		entity.setId(1);
//		entity.setAttributes(new ArrayList<Attribute>());
//
//		Object[][] data = new Object[][]{
//				{1, "attr1", false, "cis_objects"},
//				{2, "attr2", false, "cis_nodes"},
//				{4, "attr4", true, "cis_nodes"},
//				{3, "attr3", false, "usr_schema_entity1"}
//		};
//
//		int ID_INDEX = 0;
//		int NAME_INDEX = 1;
//		int IN_CONTEXT = 2;
//		int TABLE_NAME_INDEX = 3;
//		for(Object[] aData : data){
//			Attribute a = new Attribute();
//			a.setId((Integer) aData[ID_INDEX]);
//			a.setName((String) aData[NAME_INDEX]);
//			if(!(Boolean) aData[IN_CONTEXT])
//				a.setInContext((Boolean) aData[IN_CONTEXT]);
//			//a.setTableName((String) aData[TABLE_NAME_INDEX]);
//			entity.attributes.add(a);
//			includedAttributes.add(a);
//		}
//		entity.initTableNames();
//
//		SimpleSearch ss = (SimpleSearch) PrivateAccessor.invokePrivateMethod(searchService, "generateSelectSQL", entity);
//
//		assertEquals("SELECT o.ID,a0.attr1,a1.attr2,a1.attr4,a2.attr3 FROM cis_objects a0,cis_nodes a1,usr_schema_entity1 a2, " +
//				"CIS_OBJECTS o WHERE a0.ID=o.ID AND a1.ID=o.ID AND a2.ID=o.ID AND o.ObjectType=1 AND o.status IN (0,1)", ss.sql);
//		assertEquals(params, ss.params);
//		assertEquals(includedAttributes, ss.includedAttributes);
//		assertEquals(entity.getId(), ss.entityId);
//		assertEquals(1, ss.entityId);
//	}

	public void testMakeConditionForPredefined(){
		Attribute attribute = new Attribute();
		attribute.setId(1);
		AdvancedCriteria.Section section = new AdvancedCriteria.Section();
		AdvancedCriteria.Section expected = new AdvancedCriteria.Section();
		String[] term = new String[]{};
		PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "makeConditionForPredefined", attribute, section, term);
		assertEquals(expected, section);

		attribute.setValues(new ArrayList<PredefinedAttribute>());
		PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "makeConditionForPredefined", attribute, section, term);
		assertEquals(expected, section);

		Object[][] predefData = new Object[][]{
				{1, "1", "val1"},
				{2, "2", "val2"},
				{3, "3", "val3"},
				{4, "4", "val4"},
				{5, "5", "val5"}
		};
		for(Object[] data : predefData){
			PredefinedAttribute pa = new PredefinedAttribute();
			pa.setId((Integer) data[0]);
			pa.setValue((String) data[1]);
			pa.setLabel((String) data[2]);
			attribute.getValues().add(pa);
		}

		expected.createConditionGroup(1).addCondition(new AdvancedCriteria.Section.Condition(1, "=", "1", false));

		PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "makeConditionForPredefined", attribute, section, new String[]{"val1"});
		assertEquals(expected, section);

		expected.getConditionGroups().get(0).addCondition(new AdvancedCriteria.Section.Condition(1, "=", "2", false));

		section = new AdvancedCriteria.Section();
		PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "makeConditionForPredefined", attribute, section, new String[]{"val1", "val2"});
		assertEquals(expected, section);

		expected.getConditionGroups().clear();
		expected.createConditionGroup(1).addCondition(new AdvancedCriteria.Section.Condition(1, "=", "1", false));
		expected.getConditionGroups().get(0).addCondition(new AdvancedCriteria.Section.Condition(1, "=", "3", false));
		section = new AdvancedCriteria.Section();
		PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "makeConditionForPredefined", attribute, section, new String[]{"val1", "val3"});
		assertEquals(expected, section);

		attribute.setMultivalue(true);
		section = new AdvancedCriteria.Section();
		expected.getConditionGroups().clear();
		expected.createConditionGroup(1).addCondition(new AdvancedCriteria.Section.Condition(1, "AtLeastOne", "1,3", false));
		PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "makeConditionForPredefined", attribute, section, new String[]{"val1", "val3"});
		assertEquals(expected, section);
	}

	public void testMakeCriteriaFromSimple(){
//		Schema schema = new Schema();
//		schema.setId(100);
//		schema.setDefinitions(new ArrayList<ObjectDefinition>());
//
//		SimpleCriteria simpleCriteria = new SimpleCriteria();
//		simpleCriteria.setTerm("asdf");
//
//		Group group = new Group(500);
//
//		AdvancedCriteria expected = new AdvancedCriteria();
//		expected.setQueryType(QueryType.NODE.getValue());
//		expected.setSections(new ArrayList<AdvancedCriteria.Section>());
//
//		AdvancedCriteria result = (AdvancedCriteria) PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(),
//				"makeCriteriaFromSimple", schema, simpleCriteria, group);
//		assertEquals(expected, result);
//
//		for (int i = 1; i < 3; i++){
//			schema.getDefinitions().add(createObjectDef(i, group));
//		}
//
//		AdvancedCriteria.Section s = new AdvancedCriteria.Section();
//		expected.getSections().add(s);
//		s.setEntity(1);
//		s.addConditionGroup(11).addCondition(new AdvancedCriteria.Section.Condition(11, "~", "asdf", false));
//		s.addConditionGroup(13).addCondition(new AdvancedCriteria.Section.Condition(13, "~", "asdf", false));
//		s.addConditionGroup(15).addCondition(new AdvancedCriteria.Section.Condition(15, "~", "asdf", false));
//
//		s = new AdvancedCriteria.Section();
//		expected.getSections().add(s);
//		s.setEntity(2);
//		s.addConditionGroup(21).addCondition(new AdvancedCriteria.Section.Condition(21, "~", "asdf", false));
//		s.addConditionGroup(23).addCondition(new AdvancedCriteria.Section.Condition(23, "~", "asdf", false));
//		s.addConditionGroup(25).addCondition(new AdvancedCriteria.Section.Condition(25, "~", "asdf", false));
//
//		result = (AdvancedCriteria) PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "makeCriteriaFromSimple",
//				schema, simpleCriteria, group);
//		assertEquals(expected, result);
//
//		simpleCriteria.setTerm("aaa bbb \"ccc\"");
//
//		s = expected.getSections().get(0);
//		s.getConditionGroups().clear();
//		s.addConditionGroup(11).addCondition(new AdvancedCriteria.Section.Condition(11, "~", "aaa", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(11, "~", "bbb", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(11, "=", "ccc", false));
//		s.addConditionGroup(13).addCondition(new AdvancedCriteria.Section.Condition(13, "~", "aaa", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(13, "~", "bbb", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(13, "=", "ccc", false));
//		s.addConditionGroup(15).addCondition(new AdvancedCriteria.Section.Condition(15, "~", "aaa", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(15, "~", "bbb", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(15, "=", "ccc", false));
//
//		s = expected.getSections().get(1);
//		s.getConditionGroups().clear();
//		s.addConditionGroup(21).addCondition(new AdvancedCriteria.Section.Condition(21, "~", "aaa", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(21, "~", "bbb", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(21, "=", "ccc", false));
//		s.addConditionGroup(23).addCondition(new AdvancedCriteria.Section.Condition(23, "~", "aaa", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(23, "~", "bbb", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(23, "=", "ccc", false));
//		s.addConditionGroup(25).addCondition(new AdvancedCriteria.Section.Condition(25, "~", "aaa", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(25, "~", "bbb", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(25, "=", "ccc", false));
//
//		result = (AdvancedCriteria) PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "makeCriteriaFromSimple",
//				schema, simpleCriteria, group);
//		assertEquals(expected, result);
//
//		simpleCriteria.setTerm("-bbb +\"ccc\" -\"zzz\"");
//
//		s = expected.getSections().get(0);
//		s.getConditionGroups().clear();
//		s.addConditionGroup(11).addCondition(new AdvancedCriteria.Section.Condition(11, "!~", "bbb", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(11, "=", "ccc", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(11, "<>", "zzz", false)).setConditionConnectionType(true);
//		s.addConditionGroup(13).addCondition(new AdvancedCriteria.Section.Condition(13, "!~", "bbb", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(13, "=", "ccc", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(13, "<>", "zzz", false)).setConditionConnectionType(true);
//		s.addConditionGroup(15).addCondition(new AdvancedCriteria.Section.Condition(15, "!~", "bbb", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(15, "=", "ccc", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(15, "<>", "zzz", false)).setConditionConnectionType(true);
//
//		s = expected.getSections().get(1);
//		s.getConditionGroups().clear();
//		s.addConditionGroup(21).addCondition(new AdvancedCriteria.Section.Condition(21, "!~", "bbb", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(21, "=", "ccc", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(21, "<>", "zzz", false)).setConditionConnectionType(true);
//		s.addConditionGroup(23).addCondition(new AdvancedCriteria.Section.Condition(23, "!~", "bbb", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(23, "=", "ccc", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(23, "<>", "zzz", false)).setConditionConnectionType(true);
//		s.addConditionGroup(25).addCondition(new AdvancedCriteria.Section.Condition(25, "!~", "bbb", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(25, "=", "ccc", false)).addCondition(
//				new AdvancedCriteria.Section.Condition(25, "<>", "zzz", false)).setConditionConnectionType(true);
//
//		result = (AdvancedCriteria) PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "makeCriteriaFromSimple",
//				schema, simpleCriteria, group);
//		assertEquals(expected, result);
	}

	public void testvalueMatchingTerms(){
		PredefinedAttribute pa = new PredefinedAttribute();
		pa.setLabel("asdfghjkl");
		String[] terms = new String[] { "as", "df", "gh" };
		assertTrue((Boolean) PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "valueMatchingTerms", pa, terms));
		terms = new String[] { "+as", "+df", "+gh" };
		assertTrue((Boolean) PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "valueMatchingTerms", pa, terms));
		terms = new String[] { "as", "df", "-zz" };
		assertTrue((Boolean) PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "valueMatchingTerms", pa, terms));
		terms = new String[] { "As", "dF", "-zz" };
		assertTrue((Boolean) PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "valueMatchingTerms", pa, terms));
		terms = new String[] { "asdfghjkl" };
		assertTrue((Boolean) PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "valueMatchingTerms", pa, terms));
		terms = new String[] { "+asdfghjkl" };
		assertTrue((Boolean) PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "valueMatchingTerms", pa, terms));
		terms = new String[] { "+asdfghjk" };
		assertTrue((Boolean) PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "valueMatchingTerms", pa, terms));
		terms = new String[] { "+fghjkl" };
		assertTrue((Boolean) PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "valueMatchingTerms", pa, terms));
		terms = new String[] { "hjkl" };
		assertTrue((Boolean) PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "valueMatchingTerms", pa, terms));
		terms = new String[] { "df", "+ghjkl" };
		assertTrue((Boolean) PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "valueMatchingTerms", pa, terms));

		terms = new String[] { "df", "+zghjkl" };
		assertFalse((Boolean) PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "valueMatchingTerms", pa, terms));
		terms = new String[] { "zdf", "+zghjkl" };
		assertFalse((Boolean) PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "valueMatchingTerms", pa, terms));
		terms = new String[] { "z" };
		assertFalse((Boolean) PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "valueMatchingTerms", pa, terms));
	}

	private ObjectDefinition createObjectDef(int id, Group group){
		ObjectDefinition e = new ObjectDefinition();
		e.setObjectPermissions(new ArrayList<ObjectDefinitionGroup>());
		ObjectDefinitionGroup odg = new ObjectDefinitionGroup();
		odg.setCanRead(true);
		odg.setGroupId(500);
		odg.setObject(new ObjectDefinition(id));
		e.getObjectPermissions().add(odg);
		e.setId(id);
		e.setAttributes(new ArrayList<Attribute>());
		for (int i = 1; i <= 5; i++){
			Attribute a = new Attribute();
			a.setDatatype(DataType.TEXT);
			a.setId((id * 10) + i);
			a.setInSimpleSearch(i % 2 == 1);
			a.setName("attr" + id + "." + i);
			e.getAttributes().add(a);
		}
		return e;
	}

	public void testContainsTermModificator(){
		Object[][] data = new Object[][] { { new String[] {}, false }, { new String[] { "" }, false },
				{ new String[] { "a" }, false }, { new String[] { "a", "b", "c" }, false },
				{ new String[] { "+a", "b" }, true }, { new String[] { "a", "-c" }, true },
				{ new String[] { "a", "+a" }, true }, { new String[] { "a", "a-a", "a+a" }, false } };
		for (Object[] d : data){
			assertEquals(d[1], PrivateAccessor.invokePrivateMethod(new SearchServiceImpl(), "containsTermModificator", d[0]));
		}
	}

	public void testGetSQL() throws Exception{
		// Object[] setup = generateGetNodesSQLSetup();
		// SimpleSearch ss = (SimpleSearch) PrivateAccessor.invokePrivateMethod(searchService, "getSQL", setup);
		// SimpleSearch expected = (SimpleSearch) PrivateAccessor.invokePrivateMethod(searchService, "getNodesSQL",
		// setup);
		// assertEquals(expected, ss);
		//
		// setup = generateGetLinkedNodesSQLSetup();
		// ss = (SimpleSearch) PrivateAccessor.invokePrivateMethod(searchService, "getSQL", setup);
		// expected = (SimpleSearch) PrivateAccessor.invokePrivateMethod(searchService, "getNodesSQL", setup);
		// assertEquals(expected, ss);
		//
		// setup = generateNodeWithConnectionsSQLSetup();
		// ss = (SimpleSearch) PrivateAccessor.invokePrivateMethod(searchService, "getSQL", setup);
		// expected = (SimpleSearch) PrivateAccessor.invokePrivateMethod(searchService, "getNodesSQL", setup);
		// assertEquals(expected, ss);
	}

//	private Object[] generateNodeWithConnectionsSQLSetup(){
//		return new Object[0];  //To change body of created methods use File | Settings | File Templates.
//	}
//
//	private Object[] generateGetLinkedNodesSQLSetup(){
//		return new Object[0];  //To change body of created methods use File | Settings | File Templates.
//	}
//
//	private Object[] generateGetNodesSQLSetup(){
//		int groupId = 1;
//
//		Schema schema = new Schema(1);
//		schema.setDefinitions(new ArrayList<Entity>());
//		Entity entity = new Entity();
//		entity.setId(1);
//		entity.setAttributes(new ArrayList<Attribute>());
//		Attribute attribute = new Attribute();
//		attribute.setId(1);
//		attribute.setDatatype(DataType.TEXT);
//		entity.getAttributes().add(attribute);
//
//		attribute = new Attribute();
//		attribute.setId(2);
//		attribute.setDatatype(DataType.INT);
//		entity.getAttributes().add(attribute);
//		schema.getDefinitions().add(entity);
//
//		AdvancedCriteria criteria = new AdvancedCriteria();
//		criteria.setGeoCondition("");
//
//		List<AdvancedCriteria.Section> sections = new ArrayList<AdvancedCriteria.Section>();
//		AdvancedCriteria.Section section = new AdvancedCriteria.Section();
//		section.setEntity(1);
//		section.addCondition(new AdvancedCriteria.Section.Condition(1, "=", "aa"));
//		section.addCondition(new AdvancedCriteria.Section.Condition(1, "=", "2"));
//		sections.add(section);
//
//		Query query = new Query(QueryType.NODE.getValue(), sections, schema);
//		return new Object[]{query, groupId, criteria};
//	}
//
//	private SimpleSearch generateGetNodesSQLExpected(){
//		return null;
//	}
//
//	public void testGetConnectedNodeSQL() throws Exception{
////TODO: Test goes here...
///*
//try {
//   Method method = SearchServiceImpl.getClass().getMethod("getConnectedNodeSQL", final.class, final.class, AdvancedCriteria.class);
//   method.setAccessible(true);
//   method.invoke(<Object>, <Parameters>);
//} catch(NoSuchMethodException e) {
//} catch(IllegalAccessException e) {
//} catch(InvocationTargetException e) {
//}
//*/
//	}
//
//	/**
//	 * Method: getLinkedNodesSQL(QueryX query, final int GroupsID, AdvancedCriteria criteria)
//	 */
//	public void testGetLinkedNodesSQL() throws Exception{
////TODO: Test goes here...
///*
//try {
//   Method method = SearchServiceImpl.getClass().getMethod("getLinkedNodesSQL", QueryX.class, final.class, AdvancedCriteria.class);
//   method.setAccessible(true);
//   method.invoke(<Object>, <Parameters>);
//} catch(NoSuchMethodException e) {
//} catch(IllegalAccessException e) {
//} catch(InvocationTargetException e) {
//}
//*/
//	}
//
//	/**
//	 * Method: getNodesSQL(QueryX query, final int GroupsID, AdvancedCriteria criteria)
//	 */
//	public void testGetNodesSQL() throws Exception{
////		Object[] setup = generateGetNodesSQLSetup();
////		SimpleSearch expected = generateGetNodesSQLExpected();
////		SimpleSearch ss = (SimpleSearch) PrivateAccessor.invokePrivateMethod(searchService, "getNodesSQL", setup);
////		assertEquals(expected, ss);
//	}
//
//	/**
//	 * Method: makeIdList(List<Integer> ids)
//	 */
//	public void testMakeIdList() throws Exception{
////TODO: Test goes here...
///*
//try {
//   Method method = SearchServiceImpl.getClass().getMethod("makeIdList", List<Integer>.class);
//   method.setAccessible(true);
//   method.invoke(<Object>, <Parameters>);
//} catch(NoSuchMethodException e) {
//} catch(IllegalAccessException e) {
//} catch(InvocationTargetException e) {
//}
//*/
//	}
//
//	/**
//	 * Method: getSectionSQL(SectionX section, String tableIdentifier, String GeoSearchCondition, String objectsTableAlias)
//	 */
//	public void testGetSectionSQL() throws Exception{
////TODO: Test goes here...
///*
//try {
//   Method method = SearchServiceImpl.getClass().getMethod("getSectionSQL", SectionX.class, String.class, String.class, String.class);
//   method.setAccessible(true);
//   method.invoke(<Object>, <Parameters>);
//} catch(NoSuchMethodException e) {
//} catch(IllegalAccessException e) {
//} catch(InvocationTargetException e) {
//}
//*/
//	}
//
//	/**
//	 * Method: getExistingNodeSQLWhere()
//	 */
//	public void testGetExistingNodeSQLWhere() throws Exception{
////TODO: Test goes here...
///*
//try {
//   Method method = SearchServiceImpl.getClass().getMethod("getExistingNodeSQLWhere");
//   method.setAccessible(true);
//   method.invoke(<Object>, <Parameters>);
//} catch(NoSuchMethodException e) {
//} catch(IllegalAccessException e) {
//} catch(InvocationTargetException e) {
//}
//*/
//	}
//
//	/**
//	 * Method: getSectionWhereSQL(SectionX section, String tableIdentifier, String GeoSearchCondition, String objectsTableAlias)
//	 */
//	public void testGetSectionWhereSQL() throws Exception{
////TODO: Test goes here...
///*
//try {
//   Method method = SearchServiceImpl.getClass().getMethod("getSectionWhereSQL", SectionX.class, String.class, String.class, String.class);
//   method.setAccessible(true);
//   method.invoke(<Object>, <Parameters>);
//} catch(NoSuchMethodException e) {
//} catch(IllegalAccessException e) {
//} catch(InvocationTargetException e) {
//}
//*/
//	}
//
//	/**
//	 * Method: makeGeoSearchCriteria(String geoCriteria)
//	 */
//	public void testMakeGeoSearchCriteria() throws Exception{
////TODO: Test goes here...
///*
//try {
//   Method method = SearchServiceImpl.getClass().getMethod("makeGeoSearchCriteria", String.class);
//   method.setAccessible(true);
//   method.invoke(<Object>, <Parameters>);
//} catch(NoSuchMethodException e) {
//} catch(IllegalAccessException e) {
//} catch(InvocationTargetException e) {
//}
//*/
//	}
//
//	/**
//	 * Method: isVisibleForUser(Entity entity, Group group)
//	 */
//	public void testIsVisibleForUser() throws Exception{
////TODO: Test goes here...
///*
//try {
//   Method method = SearchServiceImpl.getClass().getMethod("isVisibleForUser", Entity.class, Group.class);
//   method.setAccessible(true);
//   method.invoke(<Object>, <Parameters>);
//} catch(NoSuchMethodException e) {
//} catch(IllegalAccessException e) {
//} catch(InvocationTargetException e) {
//}
//*/
//	}
//
//	/**
//	 * Method: extractFromResultset(List<DBObject> results, SimpleSearch ss, SqlRowSet rowSet)
//	 */
//	public void testExtractFromResultset() throws Exception{
////TODO: Test goes here...
///*
//try {
//   Method method = SearchServiceImpl.getClass().getMethod("extractFromResultset", List<DBObject>.class, SimpleSearch.class, SqlRowSet.class);
//   method.setAccessible(true);
//   method.invoke(<Object>, <Parameters>);
//} catch(NoSuchMethodException e) {
//} catch(IllegalAccessException e) {
//} catch(InvocationTargetException e) {
//}
//*/
//	}
//
//	/**
//	 * Method: generateSearchSQL(final Entity target, final String SearchFor, final boolean getOnlyCount, final boolean Exact, final int groupID, final String preFilter)
//	 */
//	public void testGenerateSearchSQL() throws Exception{
////TODO: Test goes here...
///*
//try {
//   Method method = SearchServiceImpl.getClass().getMethod("generateSearchSQL", final.class, final.class, final.class, final.class, final.class, final.class);
//   method.setAccessible(true);
//   method.invoke(<Object>, <Parameters>);
//} catch(NoSuchMethodException e) {
//} catch(IllegalAccessException e) {
//} catch(InvocationTargetException e) {
//}
//*/
//	}
//
//	/**
//	 * Method: createSimpleCriteria(SimpleSearch simpleSearch, boolean exact, String searchFor, Attribute a)
//	 */
//	public void testCreateSimpleCriteria() throws Exception{
////TODO: Test goes here...
///*
//try {
//   Method method = SearchServiceImpl.getClass().getMethod("createSimpleCriteria", SimpleSearch.class, boolean.class, String.class, Attribute.class);
//   method.setAccessible(true);
//   method.invoke(<Object>, <Parameters>);
//} catch(NoSuchMethodException e) {
//} catch(IllegalAccessException e) {
//} catch(InvocationTargetException e) {
//}
//*/
//	}
//
//	/**
//	 * Method: createPredefinedCriteria(boolean exact, String uSearchFor, Attribute a)
//	 */
//	public void testCreatePredefinedCriteria() throws Exception{
////TODO: Test goes here...
///*
//try {
//   Method method = SearchServiceImpl.getClass().getMethod("createPredefinedCriteria", boolean.class, String.class, Attribute.class);
//   method.setAccessible(true);
//   method.invoke(<Object>, <Parameters>);
//} catch(NoSuchMethodException e) {
//} catch(IllegalAccessException e) {
//} catch(InvocationTargetException e) {
//}
//*/
//	}
//
//	/**
//	 * Method: getTableNameSql(final Entity target, final String additionalTables[])
//	 */
//	public void testGetTableNameSql() throws Exception{
////TODO: Test goes here...
///*
//try {
//   Method method = SearchServiceImpl.getClass().getMethod("getTableNameSql", final.class, final.class);
//   method.setAccessible(true);
//   method.invoke(<Object>, <Parameters>);
//} catch(NoSuchMethodException e) {
//} catch(IllegalAccessException e) {
//} catch(InvocationTargetException e) {
//}
//*/
//	}
//
//	/**
//	 * Method: getLinkTablesSql(final Entity target, final String MasterTable, final String additionalTables[])
//	 */
//	public void testGetLinkTablesSql() throws Exception{
////TODO: Test goes here...
///*
//try {
//   Method method = SearchServiceImpl.getClass().getMethod("getLinkTablesSql", final.class, final.class, final.class);
//   method.setAccessible(true);
//   method.invoke(<Object>, <Parameters>);
//} catch(NoSuchMethodException e) {
//} catch(IllegalAccessException e) {
//} catch(InvocationTargetException e) {
//}
//*/
//	}
//
//	/**
//	 * Method: getAttributesListSql(SimpleSearch simpleSearch, final Entity target, final String prefix)
//	 */
//	public void testGetAttributesListSql() throws Exception{
////TODO: Test goes here...
///*
//try {
//   Method method = SearchServiceImpl.getClass().getMethod("getAttributesListSql", SimpleSearch.class, final.class, final.class);
//   method.setAccessible(true);
//   method.invoke(<Object>, <Parameters>);
//} catch(NoSuchMethodException e) {
//} catch(IllegalAccessException e) {
//} catch(InvocationTargetException e) {
//}
//*/
//	}
//
//
//	public static Test suite(){
//		return new TestSuite(SearchServiceImplTest.class);
//	}

	private Object[] generateNodeWithConnectionsSQLSetup(){
		return new Object[0]; // To change body of created methods use File | Settings | File Templates.
	}

	private Object[] generateGetLinkedNodesSQLSetup(){
		return new Object[0]; // To change body of created methods use File | Settings | File Templates.
	}

	public void testGetConnectedNodeSQL() throws Exception{
		// TODO: Test goes here...
		/*
		 * try { Method method = SearchServiceImpl.getClass().getMethod("getConnectedNodeSQL", final.class, final.class,
		 * AdvancedCriteria.class); method.setAccessible(true); method.invoke(<Object>, <Parameters>); }
		 * catch(NoSuchMethodException e) { } catch(IllegalAccessException e) { } catch(InvocationTargetException e) { }
		 */
	}

	/**
	 * Method: getLinkedNodesSQL(QueryX query, final int GroupsID, AdvancedCriteria criteria)
	 */
	public void testGetLinkedNodesSQL() throws Exception{
		// TODO: Test goes here...
		/*
		 * try { Method method = SearchServiceImpl.getClass().getMethod("getLinkedNodesSQL", QueryX.class, final.class,
		 * AdvancedCriteria.class); method.setAccessible(true); method.invoke(<Object>, <Parameters>); }
		 * catch(NoSuchMethodException e) { } catch(IllegalAccessException e) { } catch(InvocationTargetException e) { }
		 */
	}

	/**
	 * Method: getNodesSQL(QueryX query, final int GroupsID, AdvancedCriteria criteria)
	 */
	public void testGetNodesSQL() throws Exception{
		// Object[] setup = generateGetNodesSQLSetup();
		// SimpleSearch expected = generateGetNodesSQLExpected();
		// SimpleSearch ss = (SimpleSearch) PrivateAccessor.invokePrivateMethod(searchService, "getNodesSQL", setup);
		// assertEquals(expected, ss);
	}

	/**
	 * Method: makeIdList(List<Integer> ids)
	 */
	public void testMakeIdList() throws Exception{
		// TODO: Test goes here...
		/*
		 * try { Method method = SearchServiceImpl.getClass().getMethod("makeIdList", List<Integer>.class);
		 * method.setAccessible(true); method.invoke(<Object>, <Parameters>); } catch(NoSuchMethodException e) { }
		 * catch(IllegalAccessException e) { } catch(InvocationTargetException e) { }
		 */
	}

	/**
	 * Method: getSectionSQL(SectionX section, String tableIdentifier, String GeoSearchCondition, String
	 * objectsTableAlias)
	 */
	public void testGetSectionSQL() throws Exception{
		// TODO: Test goes here...
		/*
		 * try { Method method = SearchServiceImpl.getClass().getMethod("getSectionSQL", SectionX.class, String.class,
		 * String.class, String.class); method.setAccessible(true); method.invoke(<Object>, <Parameters>); }
		 * catch(NoSuchMethodException e) { } catch(IllegalAccessException e) { } catch(InvocationTargetException e) { }
		 */
	}

	/**
	 * Method: getExistingNodeSQLWhere()
	 */
	public void testGetExistingNodeSQLWhere() throws Exception{
		// TODO: Test goes here...
		/*
		 * try { Method method = SearchServiceImpl.getClass().getMethod("getExistingNodeSQLWhere");
		 * method.setAccessible(true); method.invoke(<Object>, <Parameters>); } catch(NoSuchMethodException e) { }
		 * catch(IllegalAccessException e) { } catch(InvocationTargetException e) { }
		 */
	}

	/**
	 * Method: getSectionWhereSQL(SectionX section, String tableIdentifier, String GeoSearchCondition, String
	 * objectsTableAlias)
	 */
	public void testGetSectionWhereSQL() throws Exception{
		// TODO: Test goes here...
		/*
		 * try { Method method = SearchServiceImpl.getClass().getMethod("getSectionWhereSQL", SectionX.class,
		 * String.class, String.class, String.class); method.setAccessible(true); method.invoke(<Object>, <Parameters>);
		 * } catch(NoSuchMethodException e) { } catch(IllegalAccessException e) { } catch(InvocationTargetException e) {
		 * }
		 */
	}

	/**
	 * Method: makeGeoSearchCriteria(String geoCriteria)
	 */
	public void testMakeGeoSearchCriteria() throws Exception{
		// TODO: Test goes here...
		/*
		 * try { Method method = SearchServiceImpl.getClass().getMethod("makeGeoSearchCriteria", String.class);
		 * method.setAccessible(true); method.invoke(<Object>, <Parameters>); } catch(NoSuchMethodException e) { }
		 * catch(IllegalAccessException e) { } catch(InvocationTargetException e) { }
		 */
	}

	/**
	 * Method: isVisibleForUser(Entity entity, Group group)
	 */
	public void testIsVisibleForUser() throws Exception{
		// TODO: Test goes here...
		/*
		 * try { Method method = SearchServiceImpl.getClass().getMethod("isVisibleForUser", Entity.class, Group.class);
		 * method.setAccessible(true); method.invoke(<Object>, <Parameters>); } catch(NoSuchMethodException e) { }
		 * catch(IllegalAccessException e) { } catch(InvocationTargetException e) { }
		 */
	}

	/**
	 * Method: extractFromResultset(List<DBObject> results, SimpleSearch ss, SqlRowSet rowSet)
	 */
	public void testExtractFromResultset() throws Exception{
		// TODO: Test goes here...
		/*
		 * try { Method method = SearchServiceImpl.getClass().getMethod("extractFromResultset", List<DBObject>.class,
		 * SimpleSearch.class, SqlRowSet.class); method.setAccessible(true); method.invoke(<Object>, <Parameters>); }
		 * catch(NoSuchMethodException e) { } catch(IllegalAccessException e) { } catch(InvocationTargetException e) { }
		 */
	}

	/**
	 * Method: generateSearchSQL(final Entity target, final String SearchFor, final boolean getOnlyCount, final boolean
	 * Exact, final int groupID, final String preFilter)
	 */
	public void testGenerateSearchSQL() throws Exception{
		// TODO: Test goes here...
		/*
		 * try { Method method = SearchServiceImpl.getClass().getMethod("generateSearchSQL", final.class, final.class,
		 * final.class, final.class, final.class, final.class); method.setAccessible(true); method.invoke(<Object>,
		 * <Parameters>); } catch(NoSuchMethodException e) { } catch(IllegalAccessException e) { }
		 * catch(InvocationTargetException e) { }
		 */
	}

	/**
	 * Method: createSimpleCriteria(SimpleSearch simpleSearch, boolean exact, String searchFor, Attribute a)
	 */
	public void testCreateSimpleCriteria() throws Exception{
		// TODO: Test goes here...
		/*
		 * try { Method method = SearchServiceImpl.getClass().getMethod("createSimpleCriteria", SimpleSearch.class,
		 * boolean.class, String.class, Attribute.class); method.setAccessible(true); method.invoke(<Object>,
		 * <Parameters>); } catch(NoSuchMethodException e) { } catch(IllegalAccessException e) { }
		 * catch(InvocationTargetException e) { }
		 */
	}

	/**
	 * Method: createPredefinedCriteria(boolean exact, String uSearchFor, Attribute a)
	 */
	public void testCreatePredefinedCriteria() throws Exception{
		// TODO: Test goes here...
		/*
		 * try { Method method = SearchServiceImpl.getClass().getMethod("createPredefinedCriteria", boolean.class,
		 * String.class, Attribute.class); method.setAccessible(true); method.invoke(<Object>, <Parameters>); }
		 * catch(NoSuchMethodException e) { } catch(IllegalAccessException e) { } catch(InvocationTargetException e) { }
		 */
	}

	/**
	 * Method: getTableNameSql(final Entity target, final String additionalTables[])
	 */
	public void testGetTableNameSql() throws Exception{
		// TODO: Test goes here...
		/*
		 * try { Method method = SearchServiceImpl.getClass().getMethod("getTableNameSql", final.class, final.class);
		 * method.setAccessible(true); method.invoke(<Object>, <Parameters>); } catch(NoSuchMethodException e) { }
		 * catch(IllegalAccessException e) { } catch(InvocationTargetException e) { }
		 */
	}

	/**
	 * Method: getLinkTablesSql(final Entity target, final String MasterTable, final String additionalTables[])
	 */
	public void testGetLinkTablesSql() throws Exception{
		// TODO: Test goes here...
		/*
		 * try { Method method = SearchServiceImpl.getClass().getMethod("getLinkTablesSql", final.class, final.class,
		 * final.class); method.setAccessible(true); method.invoke(<Object>, <Parameters>); }
		 * catch(NoSuchMethodException e) { } catch(IllegalAccessException e) { } catch(InvocationTargetException e) { }
		 */
	}

	/**
	 * Method: getAttributesListSql(SimpleSearch simpleSearch, final Entity target, final String prefix)
	 */
	public void testGetAttributesListSql() throws Exception{
		// TODO: Test goes here...
		/*
		 * try { Method method = SearchServiceImpl.getClass().getMethod("getAttributesListSql", SimpleSearch.class,
		 * final.class, final.class); method.setAccessible(true); method.invoke(<Object>, <Parameters>); }
		 * catch(NoSuchMethodException e) { } catch(IllegalAccessException e) { } catch(InvocationTargetException e) { }
		 */
	}

	public static Test suite(){
		return new TestSuite(SearchServiceImplTest.class);
	}
}
