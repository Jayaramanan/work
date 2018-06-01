/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.ChartAttribute;
import com.ni3.ag.adminconsole.domain.ChartGroup;
import com.ni3.ag.adminconsole.domain.Formula;
import com.ni3.ag.adminconsole.domain.GroupPrefilter;
import com.ni3.ag.adminconsole.domain.Metaphor;
import com.ni3.ag.adminconsole.domain.MetaphorData;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectChart;
import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectGroup;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.SchemaGroup;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.ChartDAO;
import com.ni3.ag.adminconsole.server.dao.ObjectConnectionDAO;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.server.dao.UserDAO;
import com.ni3.ag.adminconsole.server.service.CopySchemaService;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class CopySchemaServiceImpl implements CopySchemaService{

	private static final Logger log = Logger.getLogger(CopySchemaServiceImpl.class);

	private SchemaDAO schemaDAO;
	private ObjectConnectionDAO objectConnectionDAO;
	private ChartDAO chartDAO;
	private ACValidationRule tableNameRule;
	private UserDAO userDAO;

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	public void setObjectConnectionDAO(ObjectConnectionDAO objectConnectionDAO){
		this.objectConnectionDAO = objectConnectionDAO;
	}

	public void setChartDAO(ChartDAO chartDAO){
		this.chartDAO = chartDAO;
	}

	public void setTableNameRule(ACValidationRule tableNameRule){
		this.tableNameRule = tableNameRule;
	}

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	@Override
	public Schema copySchema(Integer id, String newName, User user) throws ACException, CloneNotSupportedException{
		log.debug("Try copy schema " + id + " with new name " + newName);
		Schema origSchema = schemaDAO.getSchema(id);
		// this line is needed for fix AC-1360
		user = userDAO.getById(user.getId());
		dumpSchemaIds(origSchema, "just selected");
		Schema clone = origSchema.clone(null, new ArrayList<ObjectDefinition>(), null, null, null, null, user);
		clone.setCreationDate(new Date());
		clone.setName(newName);
		clone = schemaDAO.save(clone);
		dumpSchemaIds(clone, "clone saved");
		log.debug("schema object saved");
		for (ObjectDefinition od : origSchema.getObjectDefinitions()){
			ObjectDefinition childClone = od.clone(null, clone, new ArrayList<ObjectAttribute>(), null, null, null, od
			        .getContext(), user);
			childClone.setCreationDate(new Date());
			clone.getObjectDefinitions().add(childClone);
		}
		clone = schemaDAO.saveOrUpdate(clone);
		clone = schemaDAO.merge(clone);
		dumpSchemaIds(clone, "after child saving");
		log.debug("schema object saved");
		log.debug("adding attributes");
		for (int i = 0; i < origSchema.getObjectDefinitions().size(); i++){
			ObjectDefinition original = origSchema.getObjectDefinitions().get(i);
			ObjectDefinition childClone = clone.getObjectDefinitions().get(i);
			for (ObjectAttribute oa : original.getObjectAttributes()){
				ObjectAttribute attrClone = oa.clone(null, childClone, null, new ArrayList<PredefinedAttribute>(), null,
				        null);
				if (oa.getFormula() != null){
					Formula cloneFormula = oa.getFormula().clone(null, attrClone);
					attrClone.setFormula(cloneFormula);
				}

				attrClone.setCreated(new Date());
				childClone.getObjectAttributes().add(attrClone);
			}
		}

		for (ObjectDefinition cloneOD : clone.getObjectDefinitions()){
			SchemaAdminModel model = new SchemaAdminModel();
			model.setCurrentObjectDefinition(cloneOD);
			tableNameRule.performCheck(model);
		}
		clone = schemaDAO.saveOrUpdate(clone);
		clone = schemaDAO.merge(clone);
		dumpSchemaIds(clone, "save with attributes");
		for (int i = 0; i < origSchema.getObjectDefinitions().size(); i++){
			ObjectDefinition originalOD = origSchema.getObjectDefinitions().get(i);
			ObjectDefinition cloneOD = clone.getObjectDefinitions().get(i);
			for (int j = 0; j < originalOD.getObjectAttributes().size(); j++){
				ObjectAttribute originalOA = originalOD.getObjectAttributes().get(j);
				ObjectAttribute cloneOA = cloneOD.getObjectAttributes().get(j);
				if (originalOA.getPredefinedAttributes() != null){
					for (PredefinedAttribute pa : originalOA.getPredefinedAttributes()){
						PredefinedAttribute clonePA = pa.clone(null, cloneOA, new ArrayList<GroupPrefilter>());
						cloneOA.getPredefinedAttributes().add(clonePA);
					}
				}
			}
		}
		clone = schemaDAO.saveOrUpdate(clone);
		clone = schemaDAO.merge(clone);
		dumpSchemaIds(clone, "save with predefined");

		copyPrivileges(origSchema, clone);
		schemaDAO.saveOrUpdate(clone);

		List<ObjectConnection> newConnections = copyObjectConnections(origSchema, clone);
		objectConnectionDAO.saveOrUpdateAll(newConnections);

		copyNodeMetaphors(origSchema, clone);

		List<Chart> newCharts = copyCharts(origSchema, clone);
		chartDAO.saveOrUpdateAll(newCharts);
		clone = schemaDAO.saveOrUpdate(clone);

		return clone;
	}

	private void copyPrivileges(Schema origSchema, Schema cloneSchema) throws CloneNotSupportedException{
		copySchemaPrivileges(origSchema, cloneSchema);
		for (int i = 0; i < origSchema.getObjectDefinitions().size(); i++){
			ObjectDefinition originalOD = origSchema.getObjectDefinitions().get(i);
			ObjectDefinition cloneOD = cloneSchema.getObjectDefinitions().get(i);
			copyObjectPrivileges(originalOD, cloneOD);
			for (int j = 0; j < originalOD.getObjectAttributes().size(); j++){
				ObjectAttribute originalOA = originalOD.getObjectAttributes().get(j);
				ObjectAttribute cloneOA = cloneOD.getObjectAttributes().get(j);
				copyAttributePrivileges(originalOA, cloneOA);
				if (originalOA.getPredefinedAttributes() != null){
					for (int k = 0; k < originalOA.getPredefinedAttributes().size(); k++){
						copyPredefinedAttributePrivileges(originalOA.getPredefinedAttributes().get(k), cloneOA
						        .getPredefinedAttributes().get(k));
					}
				}
			}
		}
	}

	void copySchemaPrivileges(Schema origSchema, Schema cloneSchema) throws CloneNotSupportedException{
		if (origSchema.getSchemaGroups() == null){
			return;
		}
		List<SchemaGroup> newSchemaGroups = new ArrayList<SchemaGroup>();
		for (SchemaGroup sg : origSchema.getSchemaGroups()){
			SchemaGroup newSG = sg.clone(cloneSchema, sg.getGroup());
			newSchemaGroups.add(newSG);
		}
		cloneSchema.setSchemaGroups(newSchemaGroups);
	}

	void copyObjectPrivileges(ObjectDefinition origObject, ObjectDefinition cloneObject) throws CloneNotSupportedException{
		if (origObject.getObjectGroups() == null){
			return;
		}
		List<ObjectGroup> newOugs = new ArrayList<ObjectGroup>();
		for (ObjectGroup oug : origObject.getObjectGroups()){
			ObjectGroup newOug = oug.clone(cloneObject, oug.getGroup());
			newOugs.add(newOug);
		}
		cloneObject.setObjectGroups(newOugs);
	}

	void copyAttributePrivileges(ObjectAttribute origAttribute, ObjectAttribute cloneAttribute)
	        throws CloneNotSupportedException{
		if (origAttribute.getAttributeGroups() == null){
			return;
		}
		List<AttributeGroup> newAgs = new ArrayList<AttributeGroup>();
		for (AttributeGroup ag : origAttribute.getAttributeGroups()){
			AttributeGroup newAG = ag.clone(cloneAttribute, ag.getGroup());
			newAgs.add(newAG);
		}
		cloneAttribute.setAttributeGroups(newAgs);
	}

	void copyPredefinedAttributePrivileges(PredefinedAttribute origAttribute, PredefinedAttribute cloneAttribute){
		if (origAttribute.getPredefAttributeGroups() == null){
			return;
		}
		List<GroupPrefilter> newAgs = new ArrayList<GroupPrefilter>();
		for (GroupPrefilter ag : origAttribute.getPredefAttributeGroups()){
			GroupPrefilter newAG = new GroupPrefilter(ag.getGroup(), cloneAttribute);
			newAgs.add(newAG);
		}
		cloneAttribute.setPredefAttributeGroups(newAgs);
	}

	private List<ObjectConnection> copyObjectConnections(Schema origSchema, Schema cloneSchema)
	        throws CloneNotSupportedException{
		List<ObjectConnection> newConnections = new ArrayList<ObjectConnection>();
		for (int i = 0; i < origSchema.getObjectDefinitions().size(); i++){
			ObjectDefinition originalOD = origSchema.getObjectDefinitions().get(i);
			ObjectDefinition cloneOD = cloneSchema.getObjectDefinitions().get(i);
			if (!cloneOD.isEdge()){
				continue;
			}

			List<ObjectConnection> origConnections = objectConnectionDAO.getConnectionsByObject(originalOD);
			if (origConnections != null && origConnections.size() > 0){
				for (ObjectConnection origConnection : origConnections){
					ObjectDefinition from = getNewObject(origConnection.getFromObject(), cloneSchema);
					ObjectDefinition to = getNewObject(origConnection.getToObject(), cloneSchema);
					PredefinedAttribute connType = getNewConnectionType(origConnection.getConnectionType(), cloneOD);
					ObjectConnection newConnection = origConnection.clone(null, connType, from, to, cloneOD);
					newConnections.add(newConnection);
				}
			}
		}
		log.debug("Created new connections: " + newConnections.size());
		return newConnections;
	}

	private void copyNodeMetaphors(Schema origSchema, Schema newSchema) throws CloneNotSupportedException{

		for (int i = 0; i < origSchema.getObjectDefinitions().size(); i++){
			ObjectDefinition originalOD = origSchema.getObjectDefinitions().get(i);
			ObjectDefinition newOD = newSchema.getObjectDefinitions().get(i);
			if (newOD.isEdge()){
				continue;
			}

			List<ObjectAttribute> inMetaphorOrigAttrs = new ArrayList<ObjectAttribute>();
			List<ObjectAttribute> inMetaphorCloneAttrs = new ArrayList<ObjectAttribute>();
			for (int k = 0; k < originalOD.getObjectAttributes().size(); k++){
				ObjectAttribute attribute = originalOD.getObjectAttributes().get(k);
				if (attribute.isInMetaphor()){
					inMetaphorOrigAttrs.add(attribute);
					inMetaphorCloneAttrs.add(newOD.getObjectAttributes().get(k));
				}
			}

			newOD.setMetaphors(new ArrayList<Metaphor>());
			List<Metaphor> nodeMetaphors = originalOD.getMetaphors();
			for (Metaphor origNm : nodeMetaphors){
				Metaphor newNm = origNm.clone(null, newSchema, newOD, null);
				List<MetaphorData> origMDList = origNm.getMetaphorData();
				List<MetaphorData> newMDList = new ArrayList<MetaphorData>();
				for (int m = 0; m < origMDList.size(); m++){
					MetaphorData origMetaphorData = origMDList.get(m);
					PredefinedAttribute origPa = origMetaphorData.getData();
					log.debug("Copying metaphor data, orig. attribute=" + origMetaphorData.getAttribute().getName()
					        + ", orig. predefined attribute=" + origPa.getValue());
					ObjectAttribute cloneAttr = getCloneAttr(origMetaphorData.getAttribute(), inMetaphorCloneAttrs);
					if (origPa == null || cloneAttr == null)
						continue;

					PredefinedAttribute newPa = getNewPredefinedAttribute(origPa, cloneAttr);
					log.debug("Found new predefined attribute: " + newPa.getValue());
					if (newPa != null){
						MetaphorData newMetaphorData = new MetaphorData(newNm, cloneAttr, newPa);
						newMDList.add(newMetaphorData);
					}
				}
				newNm.setMetaphorData(newMDList);
				newOD.getMetaphors().add(newNm);

				log.debug(newOD.getMetaphors().size() + " Node metaphors cloned for object " + newOD.getName());
			}
		}
	}

	private ObjectAttribute getCloneAttr(ObjectAttribute attribute, List<ObjectAttribute> inMetaphorCloneAttrs){
		for (ObjectAttribute oa : inMetaphorCloneAttrs){
			if (attribute.getName().equals(oa.getName())){
				return oa;
			}
		}
		return null;
	}

	private List<Chart> copyCharts(Schema origSchema, Schema cloneSchema) throws CloneNotSupportedException{
		List<Chart> origCharts = origSchema.getCharts();
		List<Chart> newCharts = new ArrayList<Chart>();
		if (origCharts != null){
			for (Chart origChart : origCharts){
				Chart newChart = origChart.clone(null, cloneSchema, null, null, null);
				copyObjectCharts(origChart, newChart);
				newCharts.add(newChart);

				if (origChart.getChartGroups() != null){
					newChart.setChartGroups(new ArrayList<ChartGroup>());
					for (ChartGroup cg : origChart.getChartGroups()){
						ChartGroup newCG = new ChartGroup(cg.getGroup(), newChart);
						newChart.getChartGroups().add(newCG);
					}
				}
			}
		}
		return newCharts;
	}

	protected void copyObjectCharts(Chart origChart, Chart newChart) throws CloneNotSupportedException{
		List<ObjectChart> objectCharts = origChart.getObjectCharts();
		if (objectCharts != null && objectCharts.size() > 0){
			newChart.setObjectCharts(new ArrayList<ObjectChart>());
			for (ObjectChart origObjChart : objectCharts){
				ObjectDefinition od = getNewObject(origObjChart.getObject(), newChart.getSchema());
				ObjectChart newObjChart = origObjChart.clone(null, od, newChart);
				newChart.getObjectCharts().add(newObjChart);
				copyChartAttributes(origObjChart, newObjChart);
			}
		}
	}

	protected void copyChartAttributes(ObjectChart origObjectChart, ObjectChart newObjectChart)
	        throws CloneNotSupportedException{
		List<ChartAttribute> chartAttributes = origObjectChart.getChartAttributes();
		if (chartAttributes != null && chartAttributes.size() > 0){
			newObjectChart.setChartAttributes(new ArrayList<ChartAttribute>());
			for (ChartAttribute origChartAttribute : chartAttributes){
				ObjectAttribute newAttr = getNewObjectAttribute(origChartAttribute.getAttribute(), newObjectChart
				        .getObject());
				ChartAttribute newChartAttribute = origChartAttribute.clone(null, newObjectChart, newAttr);
				newObjectChart.getChartAttributes().add(newChartAttribute);
			}
		}
	}

	protected PredefinedAttribute getNewPredefinedAttribute(PredefinedAttribute origPa, ObjectAttribute cloneAttribute){
		List<PredefinedAttribute> clonePAs = cloneAttribute.getPredefinedAttributes();
		if (clonePAs != null){
			for (PredefinedAttribute pa : clonePAs){
				if (pa.getValue().equals(origPa.getValue())){
					return pa;
				}
			}
		}
		return null;
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

	ObjectDefinition getNewObject(ObjectDefinition origObject, Schema cloneSchema){
		for (ObjectDefinition cloneObject : cloneSchema.getObjectDefinitions()){
			if (cloneObject.getName().equals(origObject.getName())){
				return cloneObject;
			}
		}
		return null;
	}

	private void dumpSchemaIds(Schema schemaObject, String string){
		log.debug("----------------dumpSchemaIds----------------------------------------");
		log.debug(string);
		log.debug("id: " + schemaObject.getId());
		for (ObjectDefinition od : schemaObject.getObjectDefinitions()){
			log.debug("\tchild id: " + od.getId());
			for (ObjectAttribute oa : od.getObjectAttributes()){
				log.debug("\t\tOA id: " + oa.getId());
				for (PredefinedAttribute pa : oa.getPredefinedAttributes()){
					log.debug("\t\t\tPA id: " + pa.getId());
				}
			}
		}
		log.debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}

	protected PredefinedAttribute getNewConnectionType(PredefinedAttribute origPa, ObjectDefinition cloneOD){
		for (ObjectAttribute cloneAttribute : cloneOD.getObjectAttributes()){
			if (cloneAttribute.getName().equals(ObjectAttribute.CONNECTION_TYPE_ATTRIBUTE_NAME)){
				List<PredefinedAttribute> clonePAs = cloneAttribute.getPredefinedAttributes();
				if (clonePAs != null){
					for (PredefinedAttribute pa : clonePAs){
						if (pa.getValue().equals(origPa.getValue())){
							log.debug("Found new connection type " + pa.getValue());
							return pa;
						}
					}
				}
			}
		}
		return null;
	}
}
