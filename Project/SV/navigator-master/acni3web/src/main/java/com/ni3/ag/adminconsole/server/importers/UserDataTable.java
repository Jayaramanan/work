/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;

public class UserDataTable{
	private ObjectDefinition od;
	private List<List<Object>> userData;
	private List<ObjectAttribute> attributes;
	private int srcIdColumn = -1;

	public UserDataTable(ObjectDefinition od, ObjectAttribute[] attrs){
		this.od = od;
		this.attributes = new ArrayList<ObjectAttribute>();
		Collections.addAll(attributes, attrs);
		userData = new ArrayList<List<Object>>();
		ObjectAttribute srcIdAttr = getAttributeByName(ObjectAttribute.SRCID_ATTRIBUTE_NAME);
		srcIdColumn = this.attributes.indexOf(srcIdAttr);
	}

	public int size(){
		return userData != null ? userData.size() : 0;
	}

	public ObjectDefinition getOd(){
		return od;
	}

	public List<ObjectAttribute> getAttributes(){
		return attributes;
	}

	public void addRow(String[] rowStr){
		List<Object> row = new ArrayList<Object>();
		for (int i = 0; i < attributes.size(); i++){
			String s = rowStr.length > i ? (String) rowStr[i] : null;
			if (!attributes.get(i).isPredefined() && s != null && s.contains("'")){
				s = s.replace("'", "\\\'");
			}
			row.add(s);
		}
		this.userData.add(row);
	}

	public Object getValue(int row, int column){
		final List<Object> rowData = userData.get(row);
		return (column >= 0 && rowData.size() > column) ? rowData.get(column) : null;
	}

	public void setValue(int row, int column, Object value){
		final List<Object> rowData = userData.get(row);
		if (rowData.size() > column){
			userData.get(row).set(column, value);
		}
	}

	public String getSrcId(int row){
		// remove starting and ending ' signs (previously added for strings).
		String value = (String) getValue(row, srcIdColumn);
		if (value != null && value.startsWith("'") && value.endsWith("'") && value.length() > 1){
			value = value.substring(1, value.length() - 1);
		}
		return value;
	}

	public boolean isEmpty(){
		return userData == null || userData.isEmpty();
	}

	public void filterOut(Set<Integer> invalidEdgeIndexes){
		if (invalidEdgeIndexes.isEmpty())
			return;
		List<List<Object>> rezUserData = userData;
		userData = new ArrayList<List<Object>>();
		for (int i = 0; i < rezUserData.size(); i++){
			if (!invalidEdgeIndexes.contains(i)){
				userData.add(rezUserData.get(i));
			}
		}
	}

	private void generateMissingSrcIds(){
		long lastTime = System.currentTimeMillis();
		for (List<Object> row : userData){
			if (row.size() <= srcIdColumn || row.get(srcIdColumn) == null || "".equals(row.get(srcIdColumn))){
				lastTime++;
				final String sourceId = "AC_" + lastTime;
				if (row.size() <= srcIdColumn){
					row.add(sourceId);
				} else{
					row.set(srcIdColumn, sourceId);
				}
			}
		}
	}

	private ObjectAttribute getAttributeByName(String name){
		ObjectAttribute attribute = null;
		for (ObjectAttribute attr : attributes){
			if (attr.getName().equalsIgnoreCase(name)){
				attribute = attr;
				break;
			}
		}
		return attribute;
	}

	public void checkSrcIdAttribute(){
		if (srcIdColumn < 0){
			ObjectAttribute srcIdAttribute = null;
			for (ObjectAttribute attr : od.getObjectAttributes()){
				if (ObjectAttribute.SRCID_ATTRIBUTE_NAME.equalsIgnoreCase(attr.getName())){
					srcIdAttribute = attr;
					break;
				}
			}
			if (srcIdAttribute != null){
				attributes.add(srcIdAttribute);
				srcIdColumn = attributes.size() - 1;
			}
		}

		if (srcIdColumn >= 0){
			generateMissingSrcIds();
		}
	}

	public void addAttribute(ObjectAttribute oa){
		attributes.add(oa);
		for (List<Object> row : userData){
			row.add(null);
		}
	}
}