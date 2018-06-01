/** Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain;

import java.util.*;

import com.ni3.ag.navigator.client.util.Utility;
import com.ni3.ag.navigator.shared.util.StringTokenizerEx;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXMLAttribute;

public class DataFilter{
	public Map<Integer, Value> filter;
	public List<Integer> haloOn;
	public List<Integer> expanded;
	private boolean noOrphans;
	private boolean connectedOnly;
	public boolean dontFilterFocusNodes;
	public boolean FilterEmptyCharts;
	public boolean FilterFrom, FilterTo, NoSingles, currentFavoritesonly;

	private Map<Integer, ChartFilter> chartFilterMap;

	public int topicMode;

	public DataFilter(){
		filter = new HashMap<Integer, Value>();
		expanded = new ArrayList<Integer>();
		haloOn = new ArrayList<Integer>();
		chartFilterMap = new HashMap<Integer, ChartFilter>();

		noOrphans = true;
		connectedOnly = false;
		dontFilterFocusNodes = true;
		FilterFrom = true;
		FilterTo = true;
		NoSingles = false;
		currentFavoritesonly = false;

		FilterEmptyCharts = false;
		topicMode = 2;

		resetChartFilters();
	}

	public DataFilter(DataFilter f){
		filter = new HashMap<Integer, Value>();
		expanded = new ArrayList<Integer>();
		haloOn = new ArrayList<Integer>();
		chartFilterMap = new HashMap<Integer, ChartFilter>();

		copyFilter(f);
	}

	public boolean isNoOrphans(){
		return noOrphans;
	}

	public void setNoOrphans(boolean noOrphans){
		this.noOrphans = noOrphans;
		if (!noOrphans){
			this.connectedOnly = false;
		}
	}

	public boolean isConnectedOnly(){
		return connectedOnly;
	}

	public void setConnectedOnly(boolean connectedOnly){
		this.connectedOnly = connectedOnly;
	}

	// TODO move to controller
	public void initFilterProperties(){
		noOrphans = UserSettings.getBooleanAppletProperty("NoOrphans_True", true);
		connectedOnly = UserSettings.getBooleanAppletProperty("NoUnrelated_True", false);
		dontFilterFocusNodes = UserSettings.getBooleanAppletProperty("NoFocus_True", false);
		NoSingles = UserSettings.getBooleanAppletProperty("NoSingles_True", false);
		FilterFrom = UserSettings.getBooleanAppletProperty("FilterFrom_True", true);
		FilterTo = UserSettings.getBooleanAppletProperty("FilterTo_True", true);
		currentFavoritesonly = UserSettings.getBooleanAppletProperty("CurrentFavoritesOnly_True", true);
	}

	public ChartFilter getChartFilter(int entityId){
		ChartFilter cf = chartFilterMap.get(entityId);
		if (cf == null){
			cf = initChartFilter(entityId);
		}
		return cf;
	}

	public ChartFilter initChartFilter(int entityId){
		final ChartFilter cf = new ChartFilter();
		chartFilterMap.put(entityId, cf);
		return cf;
	}

	public Set<Integer> getChartFilterEntities(){
		return chartFilterMap.keySet();
	}

	public void removeChartFilters(){
		if (chartFilterMap != null){
			chartFilterMap.clear();
		}
	}

	public void copyFilter(DataFilter f){
		filter = new HashMap<Integer, Value>();
		if (f != null){
			noOrphans = f.noOrphans;
			connectedOnly = f.connectedOnly;
			FilterEmptyCharts = f.FilterEmptyCharts;
			dontFilterFocusNodes = f.dontFilterFocusNodes;
			NoSingles = f.NoSingles;
			FilterFrom = f.FilterFrom;
			FilterTo = f.FilterTo;
			currentFavoritesonly = f.currentFavoritesonly;
			topicMode = f.topicMode;

			if (f.chartFilterMap != null){
				for (Integer entityId : f.getChartFilterEntities()){
					final ChartFilter cFilter = f.getChartFilter(entityId);
					if (cFilter != null){
						final ChartFilter newCFilter = new ChartFilter(cFilter.getMinChartVal(), cFilter.getMaxChartVal(),
								cFilter.copyChartFilterAttributes());
						chartFilterMap.put(entityId, newCFilter);
					}
				}
			}

			filter.putAll(f.filter);

			haloOn.addAll(f.haloOn);
			expanded.addAll(f.expanded);
		} else{
			noOrphans = true;
			connectedOnly = false;
			dontFilterFocusNodes = true;
			FilterEmptyCharts = false;
			FilterFrom = true;
			FilterTo = true;
			NoSingles = false;
			currentFavoritesonly = false;
			topicMode = 2;

		}
	}

	public void addFilter(DataFilter f){
		if (filter == null)
			filter = new HashMap<Integer, Value>();
		if (f == null)
			return;
		for (Value v : f.filter.values())
			addExclusion(v);
	}

	public void addExclusion(int ID){
		Value v = Schema.getValue(ID);

		if (v != null)
			addExclusion(v);
	}

	public void addExclusion(Value v){
		if (filter.containsKey(v.getId()))
			return;

		filter.put(v.getId(), v);
	}

	public void addExpansion(int row){
		expanded.add(row);
	}

	public void addHalo(int ID){
		haloOn.add(ID);
	}

	public void reset(){
		filter = new HashMap<Integer, Value>();
	}

	public DataFilter resetChartFilters(){
		for (Integer entityId : chartFilterMap.keySet()){
			resetChartFilter(entityId);
		}

		return this;
	}

	public void resetChartFilter(Integer entityId){
		ChartFilter cf = chartFilterMap.get(entityId);
		if (cf != null){
			cf.setMinChartVal(Double.NEGATIVE_INFINITY);
			cf.setMaxChartVal(Double.POSITIVE_INFINITY);
			for (ChartFilterAttribute attr : cf.getAttributes()){
				attr.setMinChartAttrVal(Double.NEGATIVE_INFINITY);
				attr.setMaxChartAttrVal(Double.POSITIVE_INFINITY);
				attr.setExcluded(false);
			}
		}
	}

	public void copyChartFilters(DataFilter filter){
		if (filter != null){
			for (Integer entityId : filter.getChartFilterEntities()){
				ChartFilter cf = getChartFilter(entityId);
				ChartFilter fromCf = filter.getChartFilter(entityId);
				if (fromCf != null){
					cf.setMinChartVal(fromCf.getMinChartVal());
					cf.setMaxChartVal(fromCf.getMaxChartVal());
					cf.setAttributes(fromCf.copyChartFilterAttributes());
				}
			}
		}
	}

	public boolean checkExclusion(int ID){
		return filter.containsKey(ID);
	}

	@Override
	public String toString(){
		return toXML("filter");
	}

	public String toXML(String SectionName){
		StringBuilder ret = new StringBuilder();

		ret.append("<").append(SectionName).append(" PropagateFilter='").append(noOrphans)
				.append("' CurrentFavoritesOnly='").append(currentFavoritesonly).append("' ConnectedOnly='").append(
						connectedOnly).append("' NoEmptyCharts='").append(FilterEmptyCharts).append("' NoSingles='").append(
						NoSingles).append("' FilterFrom='").append(FilterFrom).append("' FilterTo='").append(FilterTo)
				.append("' NoFocus='").append(dontFilterFocusNodes).append("'>\n");

		int count = 0;
		StringBuilder ret2 = new StringBuilder();
		if (filter != null)
			for (Value v : filter.values()){
				ret2.append(v.toXML());
				count++;
			}

		ret.append(ret2);

		count = 0;
		ret2 = new StringBuilder();

		if (expanded != null){
			ret2.append("<Expanded List='");
			for (Integer i : expanded){
				if (count != 0)
					ret2.append(",");
				ret2.append(i);
				count++;
			}

			ret2.append("'/>\n");
		}

		if (haloOn != null){
			ret2.append("<Halo List='");
			for (Integer i : haloOn){
				if (count != 0)
					ret2.append(",");
				ret2.append(i);
				count++;
			}

			ret2.append("'/>\n");
		}

		ret2.append(getChartFilterXml());

		ret.append(ret2);

		ret.append("</").append(SectionName).append(">\n");

		return ret.toString();
	}

	private String getChartFilterXml(){
		StringBuilder sb = new StringBuilder();
		for (Integer entityId : chartFilterMap.keySet()){
			ChartFilter cf = chartFilterMap.get(entityId);
			if (cf.getAttributes() == null || cf.getAttributes().isEmpty()){
				continue;
			}
			sb.append("<ChartFilter EntityID='").append(entityId).append("' Check='");
			boolean first = true;
			for (int i = 0; cf.getAttributes() != null && i < cf.getAttributes().size(); i++){
				if (cf.isExcluded(i)){
					if (first)
						first = false;
					else
						sb.append(",");
					sb.append(i);
				}
			}
			sb.append("' Min='").append(cf.getMinChartVal());
			sb.append("' Max='").append(cf.getMaxChartVal());
			sb.append("'>\n");
			for (int i = 0; cf.getAttributes() != null && i < cf.getAttributes().size(); i++){
				final ChartFilterAttribute attr = cf.getAttributes().get(i);
				if (!attr.isExcluded()
						&& (attr.getMinChartAttrVal() != Double.NEGATIVE_INFINITY || attr.getMaxChartAttrVal() != Double.POSITIVE_INFINITY)){
					sb.append("<Attribute Index='").append(i);
					sb.append("' Min='").append(attr.getMinChartAttrVal());
					sb.append("' Max='").append(attr.getMaxChartAttrVal());
					sb.append("' />\n");
				}
			}

			sb.append("</ChartFilter>\n");
		}
		return sb.toString();
	}

	public void fromXML(NanoXML xml, Schema schema){
		NanoXMLAttribute attr;
		while ((attr = xml.Tag.getNextAttribute()) != null){
			if ("PropagateFilter".equals(attr.Name))
				noOrphans = attr.getBooleanValue();
			else if ("ConnectedOnly".equals(attr.Name))
				connectedOnly = attr.getBooleanValue();
			else if ("CurrentFavoritesOnly".equals(attr.Name))
				currentFavoritesonly = attr.getBooleanValue();
			else if ("NoEmptyCharts".equals(attr.Name))
				FilterEmptyCharts = attr.getBooleanValue();
			else if ("NoFocus".equals(attr.Name))
				dontFilterFocusNodes = attr.getBooleanValue();
			else if ("NoSingles".equals(attr.Name))
				NoSingles = attr.getBooleanValue();
			else if ("FilterFrom".equals(attr.Name))
				FilterFrom = attr.getBooleanValue();
			else if ("FilterTo".equals(attr.Name))
				FilterTo = attr.getBooleanValue();
			else
				Utility.debugToConsole(attr.Name + " = " + attr.Value);
		}

		NanoXML nextX;
		Value v;
		while ((nextX = xml.getNextElement()) != null){
			if ("Value".equals(nextX.getName())){
				attr = nextX.Tag.getAttribute("EntityID");
				if (attr != null){
					int EID = attr.getIntegerValue();
					attr = nextX.Tag.getAttribute("AttrID");
					if (attr != null){
						int AID = attr.getIntegerValue();

						Entity ent = schema.getEntity(EID);
						if (ent == null)
							v = new Value(nextX, null);
						else{
							v = new Value(nextX, ent.getAttribute(AID));
						}

						addExclusion(v);
					}
				}
			} else if ("Expanded".equals(nextX.getName())){
				attr = nextX.Tag.getAttribute("List");
				StringTokenizerEx tok = new StringTokenizerEx(attr.Value, ",", false);
				while (tok.hasMoreTokens()){
					expanded.add(tok.nextIntegerToken());
				}
			} else if ("Halo".equals(nextX.getName())){
				attr = nextX.Tag.getAttribute("List");
				StringTokenizerEx tok = new StringTokenizerEx(attr.Value, ",", false);
				while (tok.hasMoreTokens()){
					haloOn.add(tok.nextIntegerToken());
				}
			} else if ("ChartFilter".equals(nextX.getName())){
				parseChartFilter(nextX);
			}
		}
	}

	private void parseChartFilter(NanoXML nextX){
		NanoXMLAttribute attr;
		attr = nextX.Tag.getAttribute("EntityID");
		if (attr != null){
			int entityId = attr.getIntegerValue();
			final ChartFilter chartFilter = getChartFilter(entityId);
			attr = nextX.Tag.getAttribute("Check");
			StringTokenizerEx tok = new StringTokenizerEx(attr.Value, ",", false);
			while (tok.hasMoreTokens()){
				chartFilter.setExcluded(tok.nextIntegerToken(), true);
			}

			attr = nextX.Tag.getAttribute("Min");
			if (attr != null){
				final double min = attr.getDoubleValue();
				chartFilter.setMinChartVal(min);
			}

			attr = nextX.Tag.getAttribute("Max");
			if (attr != null){
				final double max = attr.getDoubleValue();
				chartFilter.setMaxChartVal(max);
			}
			NanoXML next;
			while ((next = nextX.getNextElement()) != null){
				if (!"Attribute".equals(next.getName()))
					continue;
				attr = next.Tag.getAttribute("Index");
				if (attr != null){
					int index = attr.getIntegerValue();
					attr = next.Tag.getAttribute("Min");
					if (attr != null){
						final double min = attr.getDoubleValue();
						chartFilter.setMinChartAttrVal(index, min);
					}

					attr = next.Tag.getAttribute("Max");
					if (attr != null){
						final double max = attr.getDoubleValue();
						chartFilter.setMaxChartAttrVal(index, max);
					}
				}
			}
		}
	}

	public boolean isObjectFilteredOut(DBObject obj){
		if (obj == null || obj.getEntity() == null || filter.isEmpty())
			return false;

		boolean filteredOut = false;
		final List<Attribute> attributes = obj.getEntity().getReadableAttributes();
		for (Attribute attribute : attributes){
			if (!attribute.predefined){
				continue;
			}
			if (attribute.multivalue){
				Value[] values = (Value[]) obj.getValue(attribute.ID);
				if (values != null){
					boolean allFiltered = true;
					for (Value value : values){
						if (value != null && !checkExclusion(value.getId())){
							allFiltered = false;
							break;
						}
					}
					if (allFiltered){
						filteredOut = true;
						break;
					}
				}
			} else{
				Value value = (Value) obj.getValue(attribute.ID);
				if (value != null && checkExclusion(value.getId())){
					filteredOut = true;
					break;
				}
			}
		}

		return filteredOut;
	}
}
