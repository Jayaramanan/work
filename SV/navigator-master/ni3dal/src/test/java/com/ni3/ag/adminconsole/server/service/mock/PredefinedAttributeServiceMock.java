/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.shared.service.def.PredefinedAttributeService;
import com.ni3.ag.adminconsole.validation.ACException;

public class PredefinedAttributeServiceMock implements PredefinedAttributeService{

	public List<Schema> getShemas(){
		Schema schema1 = new Schema();
		schema1.setId(1000);
		schema1.setName("schema1");
		Schema schema2 = new Schema();
		schema2.setId(2000);
		schema2.setName("schema2");
		Schema schema3 = new Schema();
		schema3.setId(3000);
		schema3.setName("schema3");
		schema1.setObjectDefinitions(generateObjects(1, schema1));
		schema2.setObjectDefinitions(generateObjects(10, schema2));
		schema3.setObjectDefinitions(generateObjects(20, schema3));
		ArrayList<Schema> ar = new ArrayList<Schema>();
		ar.add(schema1);
		ar.add(schema2);
		ar.add(schema3);
		return ar;
	}

	private List<ObjectDefinition> generateObjects(int i, Schema parent){
		ArrayList<ObjectDefinition> ar = new ArrayList<ObjectDefinition>();
		for (int id = i; id < i + 5; id++){
			ObjectDefinition od = new ObjectDefinition();
			od.setSchema(parent);
			od.setId(id);
			od.setName("obj def" + id);
			od.setObjectAttributes(generateObjectAttributes(id * 10, od));
			ar.add(od);
		}
		return ar;
	}

	private List<ObjectAttribute> generateObjectAttributes(int i, ObjectDefinition od){
		ArrayList<ObjectAttribute> ar = new ArrayList<ObjectAttribute>();
		for (int id = i; id < i + 20; id++){
			ObjectAttribute oa = new ObjectAttribute(od);
			oa.setId(id);
			oa.setName("Attr" + id);
			ar.add(oa);
		}
		return ar;
	}

	public void updateObjectAttribute(ObjectAttribute oa) throws ACException{
		// TODO Auto-generated method stub

	}

	public ObjectAttribute reloadAttribute(Integer id){
		// TODO Auto-generated method stub
		return null;
	}

	public List<Language> getLanguages(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorContainer checkReferencedConnectionTypes(List<PredefinedAttribute> deletedPredefinedAttributes){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void calculateFormulaValue(Integer attributeId) throws ACException{
		// TODO Auto-generated method stub

	}

	@Override
	public List<Schema> getFullSchemas(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PredefinedAttribute> getAllPredefinedAttributes(ObjectDefinition od){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorContainer checkReferencesFromMetaphors(PredefinedAttribute pa){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsedInUserTable(PredefinedAttribute pa){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateObjectAttribute(ObjectAttribute oa, Collection<PredefinedAttribute> nestedPredefineds,
	        List<Object[]> deletedPredefineds) throws ACException{
		// TODO Auto-generated method stub

	}

}
