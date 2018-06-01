/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.server.dao.ObjectAttributeDAO;
import com.ni3.ag.adminconsole.server.dao.ObjectConnectionDAO;
import com.ni3.ag.adminconsole.server.dao.ObjectDefinitionDAO;
import com.ni3.ag.adminconsole.server.dao.PredefinedAttributeDAO;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.PredefinedAttributeEditModel;
import com.ni3.ag.adminconsole.shared.service.def.CalculateFormulaService;
import com.ni3.ag.adminconsole.shared.service.def.PredefinedAttributeService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import com.ni3.ag.adminconsole.validation.ErrorContainerImpl;

public class PredefinedAttributeServiceImpl implements PredefinedAttributeService{

	private ObjectDefinitionDAO objectDefinitionDAO;
	private SchemaDAO schemaDAO;
	private ObjectConnectionDAO objectConnectionDAO;
	private ObjectAttributeDAO objectAttributeDAO;
	private PredefinedAttributeDAO predefinedDAO;
	private ACValidationRule predefinedAttributeDeleteRule;
	private ACValidationRule predefAttributeValidationRule;

	private CalculateFormulaService calculateFormulaService;
	private static final Logger log = Logger.getLogger(PredefinedAttributeServiceImpl.class);

	public void setPredefinedDAO(PredefinedAttributeDAO predefinedDAO){
		this.predefinedDAO = predefinedDAO;
	}

	public void setCalculateFormulaService(CalculateFormulaService calculateFormulaService){
		this.calculateFormulaService = calculateFormulaService;
	}

	public ObjectAttributeDAO getObjectAttributeDAO(){
		return objectAttributeDAO;
	}

	public void setObjectAttributeDAO(ObjectAttributeDAO objectAttributeDAO){
		this.objectAttributeDAO = objectAttributeDAO;
	}

	public SchemaDAO getSchemaDAO(){
		return schemaDAO;
	}

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	public ObjectConnectionDAO getObjectConnectionDAO(){
		return objectConnectionDAO;
	}

	public void setObjectConnectionDAO(ObjectConnectionDAO objectConnectionDAO){
		this.objectConnectionDAO = objectConnectionDAO;
	}

	public void setObjectDefinitionDAO(ObjectDefinitionDAO objectDefinitionDAO){
		this.objectDefinitionDAO = objectDefinitionDAO;
	}

	public ObjectDefinitionDAO getObjectDefinitionDAO(){
		return objectDefinitionDAO;
	}

	public void setPredefinedAttributeDeleteRule(ACValidationRule predefinedAttributeDeleteRule){
		this.predefinedAttributeDeleteRule = predefinedAttributeDeleteRule;
	}

	public void setPredefAttributeValidationRule(ACValidationRule predefAttributeValidationRule){
		this.predefAttributeValidationRule = predefAttributeValidationRule;
	}

	public void updateObjectAttribute(ObjectAttribute oa, Collection<PredefinedAttribute> nestedPredefineds,
			List<Object[]> deletedPredefineds) throws ACException{
		if (nestedPredefineds != null){
			validatePredefinedAttributes(oa);
			predefinedDAO.saveOrUpdateAll(nestedPredefineds);
		}
		objectAttributeDAO.merge(oa);
		updateReferencesFromUserTables(deletedPredefineds);
	}

	protected void validatePredefinedAttributes(ObjectAttribute oa) throws ACException{
		PredefinedAttributeEditModel model = new PredefinedAttributeEditModel();
		model.setCurrentAttribute(oa);
		if (!predefAttributeValidationRule.performCheck(model)){
			throw new ACException(predefAttributeValidationRule.getErrorEntries());
		}
	}

	private void updateReferencesFromUserTables(List<Object[]> deletedPredefineds){
		for (Object[] row : deletedPredefineds){
			PredefinedAttribute pa = (PredefinedAttribute) row[0];
			Integer newValue = (Integer) row[1];
			if (isUsedInUserTable(pa)){
				predefinedDAO.updateValuesInUserTable(pa, newValue);
			}
		}
	}

	public ObjectAttribute reloadAttribute(Integer id){
		ObjectAttribute attribute = objectAttributeDAO.getObjectAttribute(id);
		Hibernate.initialize(attribute.getPredefinedAttributes());
		for (PredefinedAttribute pa : attribute.getPredefinedAttributes())
			Hibernate.initialize(pa.getChildren());
		return attribute;
	}

	@Override
	public ErrorContainer checkReferencedConnectionTypes(List<PredefinedAttribute> deletedPredefinedAttributes){
		ErrorContainerImpl sec = new ErrorContainerImpl();
		for (PredefinedAttribute pa : deletedPredefinedAttributes){
			if (ObjectAttribute.CONNECTION_TYPE_ATTRIBUTE_NAME.equals(pa.getObjectAttribute().getName())){
				log.debug("FOUND CONNECTION TYPE FOR PREDEFINED ATTRIBUTE: " + pa.getId() + " " + pa.getLabel());
				List<ObjectConnection> conns = objectConnectionDAO.getConnectionsByConnectionType(pa);
				log.debug("Found connections for related connection type: " + conns);
				if (!conns.isEmpty())
					sec.addError(TextID.MsgConnectionTypeIsInUse, new String[] { pa.getLabel() });
			}
		}

		return sec;
	}

	@Override
	public void calculateFormulaValue(Integer attributeId) throws ACException{
		calculateFormulaService.calculateFormulaValue(attributeId);
	}

	@Override
	public List<Schema> getFullSchemas(){
		List<Schema> schemas = schemaDAO.getSchemas();
		for (Schema schema : schemas){
			Hibernate.initialize(schema.getObjectDefinitions());

			List<ObjectDefinition> objectDefinitions = schema.getObjectDefinitions();
			for (ObjectDefinition objectDefinition : objectDefinitions){
				List<ObjectAttribute> objectAttributes = objectDefinition.getObjectAttributes();
				Hibernate.initialize(objectAttributes);
				for (ObjectAttribute oa : objectAttributes){
					Hibernate.initialize(oa.getPredefinedAttributes());
					for (PredefinedAttribute pa : oa.getPredefinedAttributes())
						Hibernate.initialize(pa.getChildren());
				}
			}
		}
		return schemas;
	}

	@Override
	public List<PredefinedAttribute> getAllPredefinedAttributes(ObjectDefinition od){
		List<PredefinedAttribute> predefineds = new ArrayList<PredefinedAttribute>();
		ObjectDefinition objectDefinition = objectDefinitionDAO.getObjectDefinition(od.getId());

		List<ObjectAttribute> objectAttributes = objectDefinition.getObjectAttributes();
		Hibernate.initialize(objectAttributes);
		for (ObjectAttribute oa : objectAttributes){
			Hibernate.initialize(oa.getPredefinedAttributes());
			for (PredefinedAttribute pa : oa.getPredefinedAttributes())
				Hibernate.initialize(pa.getChildren());
			predefineds.addAll(oa.getPredefinedAttributes());
		}

		return predefineds;
	}

	@Override
	public ErrorContainer checkReferencesFromMetaphors(PredefinedAttribute pa){
		ErrorContainerImpl ec = new ErrorContainerImpl();
		PredefinedAttributeEditModel model = new PredefinedAttributeEditModel();
		model.setCurrentPredefined(pa);
		if (!predefinedAttributeDeleteRule.performCheck(model)){
			ec.addAllErrors(predefinedAttributeDeleteRule.getErrorEntries());
		}
		return ec;
	}

	@Override
	public boolean isUsedInUserTable(PredefinedAttribute pa){
		boolean used = predefinedDAO.isUsedInUserTable(pa);
		return used;
	}
}
