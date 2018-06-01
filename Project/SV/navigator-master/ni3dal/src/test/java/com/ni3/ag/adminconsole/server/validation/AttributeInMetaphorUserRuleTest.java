package com.ni3.ag.adminconsole.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.*;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.server.dao.ObjectDefinitionDAO;
import com.ni3.ag.adminconsole.server.util.PrivateAccessor;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import junit.framework.TestCase;

public class AttributeInMetaphorUserRuleTest extends TestCase{

	public void testGetDeletedAttributes(){
		AttributeInMetaphorUserRule rule = new AttributeInMetaphorUserRule();
		List<ObjectAttribute> expectedAttributes = new ArrayList<ObjectAttribute>();
		ObjectDefinition newOD = new ObjectDefinition();
		ObjectDefinition oldOD = new ObjectDefinition();
		List<ObjectAttribute> result = (List<ObjectAttribute>) PrivateAccessor.invokePrivateMethod(rule, "getDeletedAttributes", newOD, oldOD);
		assertEquals(expectedAttributes, result);

		oldOD.setObjectAttributes(new ArrayList<ObjectAttribute>());
		newOD.setObjectAttributes(new ArrayList<ObjectAttribute>());
		ObjectAttribute oa = new ObjectAttribute();
		oa.setId(1);
		oldOD.getObjectAttributes().add(oa);
		expectedAttributes.add(oa);

		result = (List<ObjectAttribute>) PrivateAccessor.invokePrivateMethod(rule, "getDeletedAttributes", newOD, oldOD);
		assertEquals(expectedAttributes, result);

		newOD.getObjectAttributes().add(oa);
		expectedAttributes.remove(oa);
		result = (List<ObjectAttribute>) PrivateAccessor.invokePrivateMethod(rule, "getDeletedAttributes", newOD, oldOD);
		assertEquals(expectedAttributes, result);
	}

	public void testCheckPredefinedUsedInMetaphor() throws Exception{
		AttributeInMetaphorUserRule rule = new AttributeInMetaphorUserRule();
		PredefinedAttribute pa = new PredefinedAttribute();
		pa.setObjectAttribute(new ObjectAttribute());
		pa.getObjectAttribute().setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
		pa.getObjectAttribute().getPredefinedAttributes().add(pa);
		pa.getObjectAttribute().setObjectDefinition(new ObjectDefinition());
		pa.getObjectAttribute().getObjectDefinition().setMetaphors(new ArrayList<Metaphor>());
		pa.getObjectAttribute().setLabel("attr");
		pa.setLabel("palabel");
		pa.setId(1);

		Metaphor m = new Metaphor();
		m.setMetaphorSet("mset");
		m.setMetaphorData(new ArrayList<MetaphorData>());
		pa.getObjectAttribute().getObjectDefinition().getMetaphors().add(m);
		PrivateAccessor.invokePrivateMethod(rule, "checkPredefinedUsedInMetaphor", pa);
		List<ErrorEntry> result = (List<ErrorEntry>) PrivateAccessor.getPrivateField(rule, "errors");
		List<ErrorEntry> expectedErrors = new ArrayList<ErrorEntry>();
		assertEquals(expectedErrors, result);

		m.getMetaphorData().add(new MetaphorData(m, pa.getObjectAttribute(), pa));
		expectedErrors.add(new ErrorEntry(TextID.MsgPredefinedValueUsedInMetaphor, new String[]{"attr", "palabel", "mset"}));
		PrivateAccessor.invokePrivateMethod(rule, "checkPredefinedUsedInMetaphor", pa);
		result = (List<ErrorEntry>) PrivateAccessor.getPrivateField(rule, "errors");
		assertEquals(expectedErrors, result);

		m.getMetaphorData().add(new MetaphorData(m, pa.getObjectAttribute(), pa));
		rule = new AttributeInMetaphorUserRule();
		PrivateAccessor.invokePrivateMethod(rule, "checkPredefinedUsedInMetaphor", pa);
		result = (List<ErrorEntry>) PrivateAccessor.getPrivateField(rule, "errors");
		assertEquals(expectedErrors, result);
	}

	public void testCheckPerform() throws Exception{
		AttributeInMetaphorUserRule rule = new AttributeInMetaphorUserRule();
		SchemaAdminModel model = new SchemaAdminModel();

		ObjectDefinition od = new ObjectDefinition();
		od.setId(1);
		od.setObjectAttributes(new ArrayList<ObjectAttribute>());
		model.setCurrentObjectDefinition(od);
		rule.setObjectDefinitionDAO(new ObjectDefinitionDAO(){
			@Override
			public List<ObjectDefinition> getObjectDefinitions(){
				return null;  //To change body of implemented methods use File | Settings | File Templates.
			}

			@Override
			public ObjectDefinition getObjectDefinition(int id){
				ObjectDefinition od = new ObjectDefinition();
				od.setId(1);
				od.setObjectAttributes(new ArrayList<ObjectAttribute>());
				ObjectAttribute oa = new ObjectAttribute();
				oa.setObjectDefinition(od);
				oa.setId(1);
				od.getObjectAttributes().add(oa);
				oa.setLabel("attr");
				oa.setPredefined(true);
				oa.setInMetaphor(true);

				oa.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
				PredefinedAttribute pa = new PredefinedAttribute();
				pa.setObjectAttribute(oa);
				pa.setId(1);
				pa.setLabel("palabel");
				oa.getPredefinedAttributes().add(pa);

				Metaphor m = new Metaphor();
				m.setMetaphorSet("mset");
				m.setMetaphorData(new ArrayList<MetaphorData>());
				m.getMetaphorData().add(new MetaphorData(m, oa, pa));
				m.getMetaphorData().add(new MetaphorData(m, oa, pa));
				od.setMetaphors(new ArrayList<Metaphor>());
				od.getMetaphors().add(m);
				return od;
			}

			@Override
			public ObjectDefinition saveOrUpdate(ObjectDefinition objectDefinition){
				return null;  //To change body of implemented methods use File | Settings | File Templates.
			}

			@Override
			public void deleteObject(ObjectDefinition o){
				//To change body of implemented methods use File | Settings | File Templates.
			}

			@Override
			public ObjectDefinition save(ObjectDefinition clone){
				return null;  //To change body of implemented methods use File | Settings | File Templates.
			}

			@Override
			public List<ObjectDefinition> getNodeLikeObjectDefinitions(){
				return null;  //To change body of implemented methods use File | Settings | File Templates.
			}

			@Override
			public ObjectDefinition merge(ObjectDefinition od){
				return null;  //To change body of implemented methods use File | Settings | File Templates.
			}

			@Override
			public ObjectDefinition getObjectDefinitionByName(String objectName, Integer schemaId){
				return null;  //To change body of implemented methods use File | Settings | File Templates.
			}

			@Override
			public List<ObjectDefinition> getObjectDefinitionsByUser(User u){
				return null;  //To change body of implemented methods use File | Settings | File Templates.
			}

			@Override
			public List<ObjectDefinition> getSchemaNodeLikeObjects(Integer schemaId){
				return null;  //To change body of implemented methods use File | Settings | File Templates.
			}

			@Override
			public List<ObjectDefinition> getSchemaEdgeLikeObjects(Integer schemaId){
				return null;  //To change body of implemented methods use File | Settings | File Templates.
			}

			@Override
			public List<ObjectDefinition> getNodeObjectsWithNotFixedAttributes(){
				return null;  //To change body of implemented methods use File | Settings | File Templates.
			}

			@Override
			public ObjectDefinition getObjectDefinitionWithInMetaphor(Integer id){
				return null;  //To change body of implemented methods use File | Settings | File Templates.
			}

			@Override
			public void deleteObjectChartsByObject(ObjectDefinition object){
				//To change body of implemented methods use File | Settings | File Templates.
			}

			@Override
			public void evict(ObjectDefinition od){
				//To change body of implemented methods use File | Settings | File Templates.
			}
		});

		List<ErrorEntry> expectedErrors = new ArrayList<ErrorEntry>();
		expectedErrors.add(new ErrorEntry(TextID.MsgPredefinedValueUsedInMetaphor, new String[]{"attr", "palabel", "mset"}));
		assertFalse(rule.performCheck(model));
		assertEquals(expectedErrors, rule.getErrorEntries());
	}
}
