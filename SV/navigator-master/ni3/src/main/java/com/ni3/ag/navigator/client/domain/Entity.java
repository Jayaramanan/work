/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ni3.ag.navigator.client.domain.Attribute.EDynamicAttributeScope;
import com.ni3.ag.navigator.client.domain.AttributeComparator.SortMode;
import com.ni3.ag.navigator.client.util.Utility;
import com.ni3.ag.navigator.shared.domain.UrlOperation;
import com.ni3.ag.navigator.shared.proto.NResponse;

public class Entity{
	public static final int COMMON_ENTITY_ID = -2;
	public int ID;
	public String Name;
	private int objectTypeID;
	private int sort;
	private String description;

	private Schema schema;

	private List<Attribute> attributesAll;
	private List<Attribute> attributesReadable;
	private List<Attribute> attributesSortSearch;
	private List<Attribute> attributesSortFilter;
	private List<Attribute> attributesSortMatrix;

	private UrlOperation operations[];

	public boolean CanRead, CanUpdate, CanCreate, CanDelete;

	public List<Context> context;

	private boolean hasContextAttributes;

	public Entity(int id){
		ID = id;
	}

	public Entity(NResponse.Entity entity, List<NResponse.UrlOperation> urlOperations, Schema parent){
		this.schema = parent;

		ArrayList<Attribute> attributesAllX;
		attributesAllX = new ArrayList<Attribute>();

		ID = entity.getId();
		Name = UserSettings.getWord(entity.getName());
		objectTypeID = entity.getObjectTypeId();
		sort = entity.getSort();
		description = UserSettings.getWord(entity.getDescription());
		CanRead = entity.getCanRead();
		CanCreate = entity.getCanCreate();
		CanUpdate = entity.getCanUpdate();
		CanDelete = entity.getCanDelete();
		operations = new UrlOperation[urlOperations.size()];
		for (int i = 0; i < urlOperations.size(); i++){
			final NResponse.UrlOperation url = urlOperations.get(i);
			operations[i] = new UrlOperation(url.getId(), url.getLabel(), url.getUrl(), url.getSort());
		}

		int sortCounter = 0;
		final List<com.ni3.ag.navigator.shared.proto.NResponse.Attribute> attributesList = entity.getAttributesList();
		for (com.ni3.ag.navigator.shared.proto.NResponse.Attribute wireAttribute : attributesList){
			Utility.PaletteInUse.nextSequence();
			final Attribute attribute = new Attribute(this, wireAttribute);
			attribute.loadValues(wireAttribute);
			attribute.setSort(sortCounter);

			boolean ToAdd = true;
			for (Attribute a1 : attributesAllX){
				if (a1.ID == attribute.ID){
					a1.setCanRead(attribute.isCanRead());

					ToAdd = false;
				}
			}

			if (ToAdd){
				attributesAllX.add(attribute);
				sortCounter++;
			}
		}

		attributesAll = new ArrayList<Attribute>();
		for (Attribute attr : attributesAllX){
			attributesAll.add(attr);
			if (attr.inContext){
				hasContextAttributes = true;
			}
		}

		attributesReadable = new ArrayList<Attribute>();
		int srt = 0;
		for (Attribute a : attributesAll){
			if (a.isCanRead()){
				attributesReadable.add(a);
			}
			a.setSort(srt);
			srt++;
		}

		context = new ArrayList<Context>();

		final List<com.ni3.ag.navigator.shared.proto.NResponse.Context> contextsList = entity.getContextsList();
		for (com.ni3.ag.navigator.shared.proto.NResponse.Context wireContext : contextsList){
			context.add(new Context(this, wireContext));
		}

		attributesSortSearch = new ArrayList<Attribute>();
		attributesSortFilter = new ArrayList<Attribute>();
		attributesSortMatrix = new ArrayList<Attribute>();

		for (Attribute a : attributesReadable){
			if (!a.inContext && a.isInAdvancedSearch()){
				attributesSortSearch.add(a);
			}
			attributesSortFilter.add(a);
			if (a.getInMatrix() > 0){
				attributesSortMatrix.add(a);
			}
		}

		Collections.sort(attributesSortSearch, new AttributeComparator(SortMode.SORT_SEARCH));

		Collections.sort(attributesSortFilter, new AttributeComparator(SortMode.SORT_FILTER));

		Collections.sort(attributesSortMatrix, new AttributeComparator(SortMode.SORT_MATRIX));

		for (int i = 0; i < attributesSortMatrix.size(); i++){
			Attribute attr = attributesSortMatrix.get(i);
			attr.setSortMatrix(i);
		}

	}

	public Schema getSchema(){
		return schema;
	}

	public void setSchema(Schema schema){
		this.schema = schema;
	}

	public String getDescription(){
		return description;
	}

	public boolean isEdge(){
		return (objectTypeID == 4 || objectTypeID == 6);
	}

	public boolean isContextEdge(){
		return objectTypeID == 6;
	}

	public boolean isNode(){
		return (objectTypeID == 2 || objectTypeID == 3);
	}

	public UrlOperation[] getUrlOperations(){
		return operations;
	}

	public String toString(){
		return description;
	}

	public Attribute getAttribute(int ID){
		for (Attribute a : attributesAll)
			if (a.ID == ID)
				return a;

		return null;
	}

	public Attribute getAttribute(String Name){
		for (Attribute a : attributesAll)
			if (a.name.equalsIgnoreCase(Name))
				return a;

		return null;
	}

	public String getCSV(String separator){
		StringBuilder ret = new StringBuilder();

		if (isEdge()){
			ret.append(UserSettings.getWord("From")).append(separator).append(UserSettings.getWord("To"));
		}

		for (Attribute a : attributesAll){
			if (a.inExport){
				if (ret.length() > 0)
					ret.append(separator);

				ret.append(a.label);
			}
		}

		return ret.toString();
	}

	public Context getContext(String name){
		for (Context c : context)
			if (name.equalsIgnoreCase(c.name))
				return c;

		return null;
	}

	public List<Attribute> getInLabelAttributes(){
		final List<Attribute> attributes = new ArrayList<Attribute>();
		for (Attribute attr : attributesReadable){
			if (attr.inLabel){
				attributes.add(attr);
			}
		}
		Collections.sort(attributes, new AttributeComparator(SortMode.SORT_LABEL));
		return attributes;
	}

	public List<Attribute> getInAdvancedSearchAttributes(){
		return attributesSortSearch;
	}

	public List<Attribute> getAttributesSortedForFilter(){
		return attributesSortFilter;
	}

	public List<Attribute> getAttributesSortedForMatrix(){
		return getAttributesSortedForMatrix(true);
	}

	public List<Attribute> getAttributesSortedForMatrix(boolean withInContext){
		final List<Attribute> attrs;
		if (withInContext || !hasContextAttributes){
			attrs = attributesSortMatrix;
		} else{
			attrs = new ArrayList<Attribute>();
			for (Attribute attr : attributesSortMatrix){
				if (!attr.inContext){
					attrs.add(attr);
				}
			}
		}
		return attrs;
	}

	public List<Attribute> getAllAttributes(){
		return attributesAll;
	}

	public List<Attribute> getReadableAttributes(){
		return getReadableAttributes(true);
	}

	public List<Attribute> getReadableAttributes(boolean withInContext){
		final List<Attribute> attrs;
		if (withInContext || !hasContextAttributes){
			attrs = attributesReadable;
		} else{
			attrs = new ArrayList<Attribute>();
			for (Attribute attr : attributesReadable){
				if (!attr.inContext){
					attrs.add(attr);
				}
			}
		}
		return attrs;
	}

	public int getSort(){
		return sort;
	}

	public boolean hasContextAttributes(){
		return hasContextAttributes;
	}

	public void addAttribute(Attribute a){
		attributesAll.add(a);
		attributesReadable.add(a);
		attributesSortMatrix.add(a);
	}

	public void removeDynamicAttributes(){
		final List<Attribute> toRemove = new ArrayList<Attribute>();
		for (Attribute a : attributesAll){
			if (a.isDynamic()){
				toRemove.add(a);
			}
		}

		attributesAll.removeAll(toRemove);
		attributesReadable.removeAll(toRemove);
		attributesSortMatrix.removeAll(toRemove);
	}

	public void removeSnaAttributes(){
		final List<Attribute> toRemove = new ArrayList<Attribute>();
		for (Attribute a : attributesAll){
			if (a.isSnaAttribute()){
				toRemove.add(a);
			}
		}

		attributesAll.removeAll(toRemove);
		attributesReadable.removeAll(toRemove);
		attributesSortMatrix.removeAll(toRemove);
	}

	public boolean hasDynamicAttributes(){
		boolean result = false;
		for (Attribute attr : attributesAll){
			if (attr.isDynamic()){
				result = true;
				break;
			}
		}
		return result;
	}

	public boolean hasSnaAttribute(){
		boolean result = false;
		for (Attribute attr : attributesAll){
			if (attr.isSnaAttribute()){
				result = true;
				break;
			}
		}
		return result;
	}

	public List<Attribute> getGraphDynamicAttributes(){
		List<Attribute> attributes = new ArrayList<Attribute>();
		for (Attribute attr : attributesAll){
			if (attr.isDynamic() && attr.getDynamicScope() != null && attr.getDynamicScope() == EDynamicAttributeScope.Graph){
				attributes.add(attr);
			}
		}
		return attributes;
	}

	public List<Attribute> getDynamicAttributes(){
		List<Attribute> attributes = new ArrayList<Attribute>();
		for (Attribute attr : attributesAll){
			if (attr.isDynamic()){
				attributes.add(attr);
			}
		}
		return attributes;
	}

}
