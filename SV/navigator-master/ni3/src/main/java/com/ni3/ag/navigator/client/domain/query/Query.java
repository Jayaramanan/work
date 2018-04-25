/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain.query;

import java.util.ArrayList;

import com.ni3.ag.navigator.client.domain.Schema;
import com.ni3.ag.navigator.shared.constants.QueryType;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXMLAttribute;

public class Query{
	private String name;
	private QueryType type;
	private int maxResults;
	private ArrayList<Section> sections;
	private Schema schema;
	private int copyNToGraph;
	private String textQuery;
	private String geoSearchCondition;

	public Query(final String name, final Schema schema){
		this.schema = schema;
		this.name = name;
		copyNToGraph = -1;
		maxResults = -1;

		sections = new ArrayList<Section>();
	}

	public QueryType getType(){
		return type;
	}

	public void add(final Section s){
		sections.add(s);
	}

	public int getCopyNToGraph(){
		return copyNToGraph;
	}

	public void setCopyNToGraph(int copyNToGraph){
		this.copyNToGraph = copyNToGraph;
	}

	public int getMaxResults(){
		return maxResults;
	}

	public void setMaxResults(int maxResults){
		this.maxResults = maxResults;
	}

	public ArrayList<Section> getSections(){
		return sections;
	}

	public void setTextQuery(String textQuery){
		this.textQuery = textQuery;
	}

	public String getGeoSearchCondition(){
		return geoSearchCondition;
	}

	public void setGeoSearchCondition(String geoSearchCondition){
		this.geoSearchCondition = geoSearchCondition;
	}

	public void fromXML(final NanoXML xml){
		NanoXMLAttribute attrXML;

		while ((attrXML = xml.Tag.getNextAttribute()) != null){
			if ("Name".equals(attrXML.Name)){
				name = attrXML.getValue();
			} else if ("TextQuery".equals(attrXML.Name)){
				textQuery = attrXML.getValue();
			} else if ("GeoSearch".equals(attrXML.Name)){
				geoSearchCondition = attrXML.getValue();
				if (geoSearchCondition.equals("null")){
					geoSearchCondition = null;
				}
			} else if ("CopyNToGraph".equals(attrXML.Name)){
				copyNToGraph = attrXML.getIntegerValue();
			} else if ("MaxResults".equals(attrXML.Name)){
				maxResults = attrXML.getIntegerValue();
			} else if ("Type".equals(attrXML.Name)){
				type = QueryType.getByValue(attrXML.getIntegerValue());
			}
		}

		Section s;
		NanoXML nextX;
		while ((nextX = xml.getNextElement()) != null){
			if ("Section".equals(nextX.getName())){
				s = new Section();
				s.fromXML(schema, nextX);
				sections.add(s);
			}
		}
	}

	public void setType(final QueryType type){
		this.type = type;
	}

	@Override
	public String toString(){
		String ret = schema.ID + "\t" + name + "\t" + (type != null ? type.getValue() : "") + "\t" + copyNToGraph + "\t"
				+ maxResults + "\t" + textQuery + "\t";

		for (final Section s : sections){
			ret += s.toString();
		}

		ret += "End\t";
		return ret;
	}

	public String toXML(){
		String ret = "<Query SchemaID='" + schema.ID + "' Name='" + name + "' Type='"
				+ (type != null ? type.getValue() : "") + "' CopyNToGraph='" + copyNToGraph + "' MaxResults='" + maxResults
				+ "' TextQuery='" + textQuery + "' GeoSearch='" + geoSearchCondition + "'>";

		for (final Section s : sections){
			ret += s.toXML();
		}

		ret += "</Query>";
		return ret;
	}
}
