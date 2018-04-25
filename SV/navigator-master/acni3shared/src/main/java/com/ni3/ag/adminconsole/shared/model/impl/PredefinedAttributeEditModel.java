/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;

public class PredefinedAttributeEditModel extends AbstractModel{

	private Map<DatabaseInstance, List<Schema>> fullSchemaMap = new HashMap<DatabaseInstance, List<Schema>>();
	private List<Object[]> deletedPredefinedAttributes = new ArrayList<Object[]>();
	private ObjectDefinition currentObject;
	private ObjectAttribute currentAttribute;
	/** all predefineds this screen operates with */
	private List<PredefinedAttribute> currentPredefineds;
	private PredefinedAttribute firstSelectedPredefined;
	private PredefinedAttribute lastSelectedPredefined;
	private PredefinedAttribute currentPredefined;
	private String dateFormat;

	public void setCurrentAttribute(ObjectAttribute currentAttribute){
		this.currentAttribute = currentAttribute;
	}

	public ObjectAttribute getCurrentAttribute(){
		return currentAttribute;
	}

	public void addPredefinedAttribute(PredefinedAttribute pa){
		if (currentAttribute == null)
			return;
		if (currentAttribute.getPredefinedAttributes() == null)
			currentAttribute.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
		currentAttribute.getPredefinedAttributes().add(pa);
		if (currentPredefineds == null)
			currentPredefineds = new ArrayList<PredefinedAttribute>();
		currentPredefineds.add(pa);
	}

	public void deletePredefinedAttribute(PredefinedAttribute deleted){
		if (currentAttribute.getPredefinedAttributes() != null){
			currentAttribute.getPredefinedAttributes().remove(deleted);
		}
		if (currentPredefineds != null)
			currentPredefineds.remove(deleted);
	}

	public void addDeletedPredefinedAttribute(PredefinedAttribute predefinedAttribute, Integer newId){
		deletedPredefinedAttributes.add(new Object[] { predefinedAttribute, newId });
	}

	public void addDeletedPredefinedAttributes(List<PredefinedAttribute> paList){
		for (PredefinedAttribute pa : paList)
			deletedPredefinedAttributes.add(new Object[] { pa, null });
	}

	public List<PredefinedAttribute> getDeletedPredefinedAttributes(){
		List<PredefinedAttribute> result = new ArrayList<PredefinedAttribute>();
		for (Object[] row : deletedPredefinedAttributes){
			result.add((PredefinedAttribute) row[0]);
		}

		return result;
	}

	public List<Object[]> getDeletedPredefinedAttributesWithOptions(){
		return deletedPredefinedAttributes;
	}

	public void clearDeletedPredefinedAttributes(){
		deletedPredefinedAttributes.clear();
	}

	public ObjectDefinition getCurrentObject(){
		return currentObject;
	}

	public void setCurrentObject(ObjectDefinition currentObject){
		this.currentObject = currentObject;
	}

	public boolean isInstanceLoaded(DatabaseInstance instance){
		return fullSchemaMap.containsKey(instance);
	}

	public List<Schema> getFullSchemas(){
		return fullSchemaMap.get(currentDatabaseInstance);
	}

	public void setFullSchemas(List<Schema> schemas){
		fullSchemaMap.put(currentDatabaseInstance, schemas);
	}

	public void setFirstSelectedPredefinedAttribute(PredefinedAttribute predefinedAttribute){
		firstSelectedPredefined = predefinedAttribute;
	}

	public void setLastSelectedPredefinedAttribute(PredefinedAttribute predefinedAttribute){
		lastSelectedPredefined = predefinedAttribute;
	}

	public PredefinedAttribute getFirstSelectedPredefined(){
		return firstSelectedPredefined;
	}

	public PredefinedAttribute getLastSelectedPredefined(){
		return lastSelectedPredefined;
	}

	public void setCurrentPredefinedAttributes(List<PredefinedAttribute> paList){
		this.currentPredefineds = paList;
	}

	public List<PredefinedAttribute> getCurrentPredefinedAttributes(){
		return currentPredefineds;
	}

	public Map<DatabaseInstance, List<Schema>> getFullSchemaMap(){
		return fullSchemaMap;
	}

	public void removeNestedPredefineds(){
		if (currentPredefineds == null)
			return;
		for (PredefinedAttribute pa : currentPredefineds)
			pa.setNested(false);
	}

	public PredefinedAttribute getCurrentPredefinedAttribute(){
		return currentPredefined;
	}

	public void setCurrentPredefined(PredefinedAttribute currentPredefined){
		this.currentPredefined = currentPredefined;
	}

	public String getDateFormat(){
		return dateFormat;
	}

	public void setDateFormat(String dateFormat){
		this.dateFormat = dateFormat;
	}

}
