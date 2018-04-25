/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.datalist;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.Schema;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXMLAttribute;

public class MatrixSortOrder{
	private List<SortColumn> sorts;

	public MatrixSortOrder(){
		sorts = new ArrayList<SortColumn>();
	}

	public void addSort(int column, Attribute attr, int entityId, boolean asc){
		sorts.add(new SortColumn(column, attr, entityId, asc));
	}

	public void setSort(int column, Attribute attr, int entityId, boolean clear){
		SortColumn sort = getSortColumn(attr, column);
		if (clear){
			sorts.clear();
		}
		if (sort != null){
			sort.setAsc(!sort.isAsc());
			if (clear){
				sorts.add(sort);
			}
		} else{
			sort = new SortColumn(column, attr, entityId, true);
			sorts.add(sort);
		}
	}

	public SortColumn getSortColumn(Attribute attr, int column){
		SortColumn sc = null;
		for (SortColumn sort : sorts){
			if (attr == null){
				if (sort.getColumn() == column){
					sc = sort;
					break;
				}
			} else if (sort.getAttr() != null && sort.getAttr().ID == attr.ID){
				sc = sort;
				break;
			}
		}
		return sc;
	}

	public List<SortColumn> getSorts(){
		return sorts;
	}

	public void addSorts(List<SortColumn> sorts){
		for (SortColumn sort : sorts){
			addSort(sort.column, sort.attr, sort.entityId, sort.asc);
		}
	}

	public void clear(){
		sorts.clear();
	}

	public class SortColumn{
		private int column;
		private int entityId;
		private Attribute attr;
		private boolean asc;

		public SortColumn(int column, Attribute attr, int entityId, boolean asc){
			this.column = column;
			this.attr = attr;
			this.entityId = entityId;
			this.asc = asc;
		}

		public int getColumn(){
			return column;
		}

		public Attribute getAttr(){
			return attr;
		}

		public boolean isAsc(){
			return asc;
		}

		public void setAsc(boolean asc){
			this.asc = asc;
		}

		public int getEntityId(){
			return entityId;
		}

	}

	public String toXML(){
		final StringBuilder xml = new StringBuilder("<MatrixSort>");
		for (SortColumn sort : sorts){
			final Attribute attr = sort.getAttr();
			xml.append("<Sort EntityID='").append(sort.getEntityId()).append("'");
			xml.append(" AttributeID='").append(attr != null ? attr.ID : 0).append("'");
			xml.append(" Column='").append(sort.getColumn()).append("'");
			xml.append(" Asc='").append(sort.asc).append("' />");
		}
		xml.append("</MatrixSort>");
		return xml.toString();
	}

	public void fromXML(final NanoXML xml, Schema schema){
		NanoXML nextX;
		while ((nextX = xml.getNextElement()) != null){
			NanoXMLAttribute attr;
			Entity entity = null;
			Attribute attribute = null;
			int column = 0;
			boolean asc = true;
			while ((attr = nextX.Tag.getNextAttribute()) != null){
				if ("EntityID".equals(attr.Name)){
					entity = schema.getEntity(attr.getIntegerValue());
				} else if ("AttributeID".equals(attr.Name) && entity != null){
					attribute = entity.getAttribute(attr.getIntegerValue());
				} else if ("Column".equals(attr.Name)){
					column = attr.getIntegerValue();
				} else if ("Asc".equals(attr.Name)){
					asc = attr.getBooleanValue();
				}
			}
			if (entity != null){
				addSort(column, attribute, entity.ID, asc);
			}
		}
	}

}
