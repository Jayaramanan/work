/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import jxl.common.Logger;

import org.hibernate.Hibernate;

import com.ni3.ag.adminconsole.domain.ApplicationSetting;
import com.ni3.ag.adminconsole.domain.LineStyle;
import com.ni3.ag.adminconsole.domain.LineWeight;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.server.dao.ApplicationSettingsDAO;
import com.ni3.ag.adminconsole.server.dao.LineStyleDAO;
import com.ni3.ag.adminconsole.server.dao.LineWeightDAO;
import com.ni3.ag.adminconsole.server.dao.ObjectConnectionDAO;
import com.ni3.ag.adminconsole.server.dao.ObjectDefinitionDAO;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.shared.service.def.ObjectsConnectionsService;

public class ObjectsConnectionsServiceImpl implements ObjectsConnectionsService{
	private static final Logger log = Logger.getLogger(ObjectsConnectionsServiceImpl.class);
	private ObjectDefinitionDAO objectDefinitionDAO;
	private SchemaDAO schemaDAO;
	private LineWeightDAO lineWeightDAO;
	private LineStyleDAO lineStyleDAO;
	private ObjectConnectionDAO objectConnectionDAO;
	private ApplicationSettingsDAO applicationSettingsDAO;

	public List<LineStyle> getLineStyles(){
		return lineStyleDAO.getLineStyles();
	}

	public List<LineWeight> getLineWeights(){
		return lineWeightDAO.getLineWeights();
	}

	public List<ObjectDefinition> getObjectDefinitions(){
		return getObjectDefinitionDAO().getObjectDefinitions();
	}

	public SchemaDAO getSchemaDAO(){
		return schemaDAO;
	}

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	public void setApplicationSettingsDAO(ApplicationSettingsDAO applicationSettingsDAO){
		this.applicationSettingsDAO = applicationSettingsDAO;
	}

	public List<Schema> getSchemas(){
		List<Schema> schemasWithEdges = schemaDAO.getSchemasWithEdges();
		for (Schema schema : schemasWithEdges){
			List<ObjectDefinition> objectDefinitions = schema.getObjectDefinitions();
			Hibernate.initialize(objectDefinitions);
			for (ObjectDefinition od : objectDefinitions){
				List<ObjectAttribute> objectAttributes = od.getObjectAttributes();
				Hibernate.initialize(objectAttributes);
				for (ObjectAttribute oa : objectAttributes){
					Hibernate.initialize(oa.getPredefinedAttributes());
				}
				List<ObjectConnection> objectConnections = od.getObjectConnections();
				Hibernate.initialize(objectConnections);
				for (ObjectConnection c : objectConnections){
					Hibernate.initialize(c.getObject());
					Hibernate.initialize(c.getFromObject());
					Hibernate.initialize(c.getToObject());
				}
			}
		}
		return schemasWithEdges;
	}

	public void setObjectDefinitionDAO(ObjectDefinitionDAO objectDefinitionDAO){
		this.objectDefinitionDAO = objectDefinitionDAO;
	}

	public ObjectDefinitionDAO getObjectDefinitionDAO(){
		return objectDefinitionDAO;
	}

	public void setLineWeightDAO(LineWeightDAO lineWeightDAO){
		this.lineWeightDAO = lineWeightDAO;
	}

	public LineWeightDAO getLineWeightDAO(){
		return lineWeightDAO;
	}

	public void setLineStyleDAO(LineStyleDAO lineStyleDAO){
		this.lineStyleDAO = lineStyleDAO;
	}

	public LineStyleDAO getLineStyleDAO(){
		return lineStyleDAO;
	}

	public void setObjectConnectionDAO(ObjectConnectionDAO objectConnectionDAO){
		this.objectConnectionDAO = objectConnectionDAO;
	}

	public ObjectConnectionDAO getObjectConnectionDAO(){
		return objectConnectionDAO;
	}

	public List<ObjectDefinition> getNodeLikeObjectDefinitions(){
		List<ObjectDefinition> nodeLikeObjectDefinitions = getObjectDefinitionDAO().getNodeLikeObjectDefinitions();
		for (ObjectDefinition od : nodeLikeObjectDefinitions){
			Hibernate.initialize(od.getObjectAttributes());
		}
		return nodeLikeObjectDefinitions;
	}

	@Override
	public LineWeight getDefaultLineWeight(){
		return lineWeightDAO.getDefaultLineWeight();
	}

	public void save(ObjectDefinition object){
		objectDefinitionDAO.saveOrUpdate(object);
	}

	@Override
	public ObjectDefinition reloadObject(Integer id){
		ObjectDefinition object = objectDefinitionDAO.getObjectDefinition(id);

		List<ObjectAttribute> objectAttributes = object.getObjectAttributes();
		Hibernate.initialize(objectAttributes);
		for (ObjectAttribute oa : objectAttributes){
			Hibernate.initialize(oa.getPredefinedAttributes());
		}

		List<ObjectConnection> objectConnections = object.getObjectConnections();
		Hibernate.initialize(objectConnections);
		for (ObjectConnection c : objectConnections){
			Hibernate.initialize(c.getObject());
			Hibernate.initialize(c.getFromObject());
			Hibernate.initialize(c.getToObject());
		}
		return object;
	}

	@Override
	public void updateHierarchiesSetting(ObjectConnection oc){
		ApplicationSetting s = applicationSettingsDAO.getApplicationSetting(Setting.APPLET_SECTION,
				Setting.HIERARCHIES_PROPERTY);
		if (s == null){
			s = new ApplicationSetting(Setting.APPLET_SECTION, Setting.HIERARCHIES_PROPERTY, "");
		}
		String value = s.getValue();
		if (value == null){
			value = "";
		}
		final Integer id = oc.getConnectionType().getId();
		if (!containsId(id, value)){
			if (!value.isEmpty()){
				value += ";";
			}
			value += id;
			s.setValue(value);

			List<ApplicationSetting> settings = new ArrayList<ApplicationSetting>();
			settings.add(s);
			applicationSettingsDAO.saveOrUpdate(settings);
		}
	}

	@Override
	public boolean isHierarchicalConnection(ObjectConnection oc){
		boolean hierarchical = false;
		final ApplicationSetting hierarchies = applicationSettingsDAO.getApplicationSetting(Setting.APPLET_SECTION,
				Setting.HIERARCHIES_PROPERTY);
		final Integer ctId = oc.getConnectionType().getId();
		if (hierarchies != null){
			String value = hierarchies.getValue();
			hierarchical = containsId(ctId, value);
		}
		return hierarchical;
	}

	boolean containsId(final Integer ctId, String value){
		boolean contains = false;
		if (value != null && !value.isEmpty()){
			String[] ids = value.split(";");
			for (String id : ids){
				try{
					int hId = Integer.valueOf(id);
					if (hId == ctId){
						contains = true;
						break;
					}
				} catch (NumberFormatException e){
					log.debug("Cannot parse hierarchical edge id: " + id);
					// ignore
				}
			}
		}
		return contains;
	}

}
