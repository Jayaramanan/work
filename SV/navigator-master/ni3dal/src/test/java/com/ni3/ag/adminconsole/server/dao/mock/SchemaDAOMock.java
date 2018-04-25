/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.mock;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;

public class SchemaDAOMock implements SchemaDAO{

	ArrayList<Schema> schemas;

	public SchemaDAOMock(){
		schemas = new ArrayList<Schema>();
	}

	@Override
	public void deleteSchema(Schema schema){
		// TODO Auto-generated method stub

	}

	@Override
	public Schema getSchema(Integer schemaID){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Schema getSchemaByName(String value){
		for (Schema s : schemas){
			if (s.getName().equals(value))
				return s;
		}
		return null;
	}

	@Override
	public List<Schema> getSchemas(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Schema> getSchemasWithEdges(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Schema> getSchemasWithNodesAndInMetaphor(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Schema> getSchemasWithPredefinedAttributes(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Schema merge(Schema schema){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Schema saveOrUpdate(Schema schema){
		// TODO Auto-generated method stub
		return null;
	}

	public Schema save(Schema schema){
		boolean ok = schemas.add(schema);
		if (ok)
			return schema;
		return null;
	}

	public void createSchemaDefinitionsFromXML(){

		Schema s0 = new Schema();
		s0.setName("Test Schema");
		s0.setId(new Integer(0));
		save(s0);

		Schema s3 = new Schema();
		s3.setName("Imported Schema");
		s3.setId(new Integer(3));
		save(s3);
	}

	@Override
	public List<Schema> getSchemasByUser(User u){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveOrUpdateAll(List<Schema> schemas){
		// TODO Auto-generated method stub

	}

	@Override
	public void evictAll(List<Schema> schemas){
		// TODO Auto-generated method stub

	}

	@Override
	public List<Schema> getSchemasWithNodes(){
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ni3.ag.adminconsole.server.dao.SchemaDAO#getSchemasWithAggregableAttributes()
	 */
	@Override
	public List<Schema> getSchemasWithAggregableAttributes(){
		// TODO Auto-generated method stub
		return null;
	}

}
