/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.controller;

import java.util.*;

import com.ni3.ag.navigator.client.controller.charts.ChartController;
import com.ni3.ag.navigator.client.controller.graph.GraphController;
import com.ni3.ag.navigator.client.domain.*;
import com.ni3.ag.navigator.client.domain.Attribute.EDynamicAttributeScope;
import com.ni3.ag.navigator.client.domain.query.Query;
import com.ni3.ag.navigator.client.gateway.ObjectManagementGateway;
import com.ni3.ag.navigator.client.gateway.impl.HttpGraphGatewayImpl;
import com.ni3.ag.navigator.client.gateway.impl.HttpObjectManagementGatewayImpl;
import com.ni3.ag.navigator.client.gui.MainPanel;
import com.ni3.ag.navigator.client.gui.datalist.MatrixSortOrder;
import com.ni3.ag.navigator.client.gui.graph.*;
import com.ni3.ag.navigator.client.gui.map.MapSettings;
import com.ni3.ag.navigator.client.model.GraphCollection;
import com.ni3.ag.navigator.client.model.Ni3Document;
import com.ni3.ag.navigator.client.model.PolygonModel;
import com.ni3.ag.navigator.client.util.Utility;
import com.ni3.ag.navigator.shared.constants.DynamicAttributeOperation;
import com.ni3.ag.navigator.shared.domain.ChartType;
import com.ni3.ag.navigator.shared.domain.FavoriteMode;
import com.ni3.ag.navigator.shared.util.StringTokenizerEx;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXMLAttribute;
import org.apache.log4j.Logger;

public class HistoryManager{
	public class HistoryItem{
		private int schemaID;
		private int chartID;
		private int mapID;
		private int thematicMapID;
		private boolean showNumericMetaphors;
		private List<Integer> overlayIds;
		private GraphCollection graph;
		private List<DBObject> DS;
		private GraphPanelSettings gpset;
		private MapSettings mapSettings;
		private DataFilter filter, prefilter;
		private String layout;
		private Map<Integer, ChartParams> chartParams;
		private List<Query> queries;
		private String metaphorSet;
		private FavoriteMode mode;
		private MatrixSortOrder matrixSort;
		private Set<Integer> inPathEdges;
		private PolygonModel polyModel;
		private CommandPanelSettings commandPanelSettings;

		public HistoryItem(final int SchemaID, final int ChartID, final int MapID, final int ThematicMapID,
				final GraphCollection graph, final List<DBObject> DS, final GraphPanelSettings gpset,
				final MapSettings mapSettings, final DataFilter filter, final DataFilter prefilter,
				final List<Query> queries, final FavoriteMode mode, final String metaphorSet, boolean showNumericMetaphors,
				List<Integer> overlayIds){
			this.schemaID = SchemaID;
			this.chartID = ChartID;
			this.mapID = MapID;
			this.thematicMapID = ThematicMapID;
			this.graph = graph;
			this.DS = DS;
			this.gpset = gpset.clone();
			this.mapSettings = mapSettings.copy();
			this.filter = filter;
			this.prefilter = prefilter;
			this.queries = queries;
			this.metaphorSet = metaphorSet;
			this.mode = mode;
			this.chartParams = null;
			this.showNumericMetaphors = showNumericMetaphors;
			this.overlayIds = overlayIds;
			this.chartParams = new HashMap<Integer, ChartParams>();
			this.inPathEdges = new HashSet<Integer>();
			this.polyModel = new PolygonModel();
			layout = mainFrame.getMainPanelLayout();
			commandPanelSettings = new CommandPanelSettings();
		}

		public boolean isShowNumericMetaphors(){
			return showNumericMetaphors;
		}

		public List<Integer> getOverlayIds(){
			return overlayIds;
		}

		public Map<Integer, ChartParams> getChartParams(){
			return chartParams;
		}

		public void setChartParams(Map<Integer, ChartParams> chartParams){
			this.chartParams = chartParams;
		}

		public MatrixSortOrder getMatrixSort(){
			return matrixSort;
		}

		public void setMatrixSort(MatrixSortOrder matrixSort){
			this.matrixSort = matrixSort;
		}

		public int getChartID(){
			return chartID;
		}

		public PolygonModel getPolyModel(){
			return polyModel;
		}

		public void setPolyModel(PolygonModel polyModel){
			this.polyModel = polyModel;
		}

		public int getMapID(){
			return mapID;
		}

		public int getThematicMapID(){
			return thematicMapID;
		}

		public GraphCollection getGraph(){
			return graph;
		}

		public List<DBObject> getDS(){
			return DS;
		}

		public GraphPanelSettings getGpset(){
			return gpset;
		}

		public MapSettings getMapSettings(){
			return mapSettings;
		}

		public DataFilter getFilter(){
			return filter;
		}

		public DataFilter getPrefilter(){
			return prefilter;
		}

		public String getLayout(){
			return layout;
		}

		public void setLayout(String layout){
			this.layout = layout;
		}

		public List<Query> getQueries(){
			return queries;
		}

		public String getMetaphorSet(){
			return metaphorSet;
		}

		public FavoriteMode getMode(){
			return mode;
		}

		public void setInPathEdges(Set<Edge> inPathEdges){
			this.inPathEdges.clear();
			for (Edge edge : inPathEdges){
				this.inPathEdges.add(edge.ID);
			}
		}

		public CommandPanelSettings getCommandPanelSettings(){
			return commandPanelSettings;
		}

		public void setCommandPanelSettings(CommandPanelSettings commandPanelSettings){
			this.commandPanelSettings = commandPanelSettings;
		}

		public void fromXML(final String XML, final Schema schema){
			NanoXML xml = new NanoXML(XML, 0, XML.length());

			if (queries == null){
				queries = new ArrayList<Query>();
			} else{
				queries.clear();
			}
			String chartTypes = null;
			NanoXMLAttribute attr;
			while ((attr = xml.Tag.getNextAttribute()) != null){
				if ("SchemaID".equals(attr.Name)){
					schemaID = attr.getIntegerValue();
				} else if ("ChartID".equals(attr.Name)){
					chartID = attr.getIntegerValue();
				} else if ("MapID".equals(attr.Name)){
					mapID = attr.getIntegerValue();
				} else if ("Mode".equals(attr.Name)){
					mode = FavoriteMode.getByValue(attr.getIntegerValue());
				} else if ("ThematicDataSetID".equals(attr.Name)){
					thematicMapID = attr.getIntegerValue();
				} else if ("OverlayIDs".equals(attr.Name)){
					String s = attr.getValue();
					if (s != null){
						overlayIds = Utility.stringToIntegerList(s, ",");
					}
				} else if ("MetaphorSet".equals(attr.Name)){
					metaphorSet = attr.getValue();
				} else if ("ChartType".equals(attr.Name)){
					chartTypes = attr.getValue();
				} else if ("NumericMetaphors".equals(attr.Name)){
					showNumericMetaphors = attr.getBooleanValue();
				} else{
					Utility.debugToConsole(attr.Name + " = " + attr.Value);
				}
			}

			chartParams = new HashMap<Integer, ChartParams>();
			if (chartID > 0){
				chartParams = ChartController.getInstance().fillChartParams(schema, chartID);
			}
			mapSettings = new MapSettings();
			matrixSort = new MatrixSortOrder();
			polyModel = new PolygonModel();

			NanoXML nextX;
			while ((nextX = xml.getNextElement()) != null){
				if ("ChartParams".equals(nextX.getName())){

				} else if ("DynamicAttributes".equals(nextX.getName())){
					loadDynamicAttributesFromXML(nextX, schema);
				} else if ("DynamicChart".equals(nextX.getName())){
					final Map<Integer, List<DynamicChartAttribute>> attrMap = ChartController.getInstance().fromXML(nextX,
							schema);
					chartParams = ChartController.getInstance().fillDynamicChartParams(schema, attrMap);
				} else if ("GraphPanelSettings".equals(nextX.getName())){
					gpset.fromXML(nextX);
				} else if ("Query".equals(nextX.getName())){
					final Query q = new Query("", schema);
					q.fromXML(nextX);
					queries.add(q);
				} else if ("MapSettings".equals(nextX.getName())){
					mapSettings.fromXML(nextX);
				} else if ("MatrixSort".equals(nextX.getName())){
					matrixSort.fromXML(nextX, schema);
				} else if ("Filter".equals(nextX.getName())){
					filter.removeChartFilters();
					filter.fromXML(nextX, schema);
				} else if ("Prefilter".equals(nextX.getName())){
					prefilter.fromXML(nextX, schema);
					prefilter.addFilter(Doc.SYSGroupPrefilter);
				} else if ("CommandPanel".equals(nextX.getName())){
					commandPanelSettings.fromXML(nextX);
				} else if ("HaloColors".equals(nextX.getName())){
					for (final Object o : Schema.PredefinedAttributesValue.values()){
						Value v = (Value) o;
						v.setHaloColorSelected(false);
					}

					final NanoXMLAttribute attr2 = nextX.Tag.getAttribute("SelectedColorList");
					final StringTokenizerEx tok = new StringTokenizerEx(attr2.Value, ",", false);
					while (tok.hasMoreTokens()){
						final int ID = tok.nextIntegerToken();
						Value v = (Value) Schema.PredefinedAttributesValue.get(ID);
						v.setHaloColorSelected(true);
					}
				} else if ("Path".equals(nextX.getName())){
					NanoXML edgeElement;
					while ((edgeElement = nextX.getNextElement()) != null){
						String edgeId = edgeElement.Tag.getAttribute("ID").Value;
						inPathEdges.add(Integer.parseInt(edgeId));
					}
				} else if ("Polygons".equals(nextX.getName())){
					polyModel.fromXML(nextX);
				} else{
					Utility.debugToConsole(nextX.getName());
				}
			}

			if (!chartParams.isEmpty() && chartTypes != null && !chartTypes.isEmpty()){
				setChartTypes(chartParams, chartTypes);
			}

			xml = new NanoXML(XML, 0, XML.length());
			while ((nextX = xml.getNextElement()) != null){
				if ("Graph".equals(nextX.getName())){
					graph.fromXML(nextX);
				}
			}
			graph.saveLayout();
		}

		private Map<Integer, ChartType> setChartTypes(Map<Integer, ChartParams> chartParams, String value){
			Map<Integer, ChartType> result = new HashMap<Integer, ChartType>();
			if (value != null && !value.isEmpty()){
				String[] pairs = value.split(",");
				for (String pair : pairs){
					String[] values = pair.split("-");
					if (values.length == 2){
						try{
							final Integer entityId = Integer.valueOf(values[0]);
							final Integer type = Integer.valueOf(values[1]);
							final ChartType chartType = ChartType.fromInt(type);
							if (chartType != null && chartParams.containsKey(entityId)){
								chartParams.get(entityId).setChartType(chartType);
							}
						} catch (NumberFormatException ex){
							// ignore
						}
					}
				}
			}
			return result;
		}

		@Override
		public String toString(){
			return toXML(FavoriteMode.FAVORITE);
		}

		public String toXML(final FavoriteMode mode){
			final StringBuilder ret = new StringBuilder(100 * 1024);

			ret.append("<NI3 version='3.00' SchemaID='").append(schemaID).append("' ChartID='").append(chartID).append(
					"' MapID='").append(mapID).append("' ThematicDataSetID='").append(thematicMapID).append("' " + "Mode='")
					.append(mode.getValue()).append("' MetaphorSet='").append(metaphorSet).append("' ");

			if (chartID != 0 && !chartParams.isEmpty()){
				ret.append("ChartType='");
				boolean first = true;
				for (Integer entityId : chartParams.keySet()){
					if (first){
						first = false;
					} else{
						ret.append(",");
					}
					final int ct = chartParams.get(entityId).getChartType().toInt();
					ret.append(entityId).append("-").append(ct);
				}
				ret.append("' ");
			}

			ret.append("NumericMetaphors='").append(showNumericMetaphors);
			if (overlayIds != null && !overlayIds.isEmpty()){
				ret.append("' OverlayIDs='").append(Utility.listToString(overlayIds));
			}
			ret.append("' >");

			if (mode == FavoriteMode.QUERY){
				for (final Query q : queries){
					ret.append(q.toXML()).append("\n");
				}
			}

			ret.append(getDynamicAttributesXML(Doc.DB.schema));

			if (chartID == DynamicChart.DYNAMIC_CHART_ID){
				ret.append(ChartController.getInstance().toXML(chartParams));
			}

			ret.append(graph.toXML(mode));
			if (matrixSort != null){
				ret.append(matrixSort.toXML());
			}

			ret.append(gpset.toXML());
			ret.append(mapSettings.toXML());

			ret.append(filter.toXML("Filter"));
			ret.append(prefilter.toXML("Prefilter"));
			ret.append(commandPanelSettings.toXML());
			ret.append(getPathXML());
			ret.append(polyModel.toXml());

			ret.append("</NI3>\n");

			Utility.debugToConsole(ret.toString());
			return ret.toString();
		}

		private String getPathXML(){
			if (inPathEdges.isEmpty())
				return "";
			StringBuilder sb = new StringBuilder();
			sb.append("<Path>");
			for (Integer e : inPathEdges)
				sb.append("<edge ID='").append(e).append("'/>");
			sb.append("</Path>\n");
			return sb.toString();
		}

		private String getDynamicAttributesXML(Schema schema){
			final StringBuilder xml = new StringBuilder();
			for (Entity entity : schema.definitions){
				if (entity.hasDynamicAttributes()){
					for (Attribute attr : entity.getAllAttributes()){
						if (attr.isDynamic()){
							xml.append("<Entity EntityID='").append(entity.ID).append("'");
							xml.append(" EntityFromID='").append(attr.getDynamicFromEntity().ID).append("'");
							xml.append(" AttributeID='").append(attr.getDynamicFromAttribute().ID).append("'");
							xml.append(" Operation='").append(attr.getDynamicOperation()).append("'");
							xml.append(" Scope='").append(attr.getDynamicScope()).append("' />");
						}
					}
				}
			}
			if (xml.length() > 0){
				xml.insert(0, "<DynamicAttributes>").append("</DynamicAttributes>");
			}
			return xml.toString();
		}

		private void loadDynamicAttributesFromXML(NanoXML xml, Schema schema){
			NanoXML nextX;
			while ((nextX = xml.getNextElement()) != null){
				NanoXMLAttribute attr;
				Entity entity = null;
				Attribute attribute = null;
				Entity entityFrom = null;
				DynamicAttributeOperation operation = null;
				EDynamicAttributeScope scope = null;
				while ((attr = nextX.Tag.getNextAttribute()) != null){
					if ("EntityID".equals(attr.Name)){
						entity = schema.getEntity(attr.getIntegerValue());
					} else if ("EntityFromID".equals(attr.Name)){
						entityFrom = schema.getEntity(attr.getIntegerValue());
					} else if ("AttributeID".equals(attr.Name) && entityFrom != null){
						attribute = entityFrom.getAttribute(attr.getIntegerValue());
					} else if ("Operation".equals(attr.Name)){
						operation = DynamicAttributeOperation.valueOf(attr.getValue());
					} else if ("Scope".equals(attr.Name)){
						scope = EDynamicAttributeScope.valueOf(attr.getValue());
					}
				}
				if (entity != null && attribute != null && entityFrom != null && operation != null && scope != null){
					Attribute dynAttr = new Attribute(entity, entityFrom, attribute, operation, scope);
					entity.addAttribute(dynAttr);

					Doc.dispatchEvent(Ni3ItemListener.MSG_DynamicAttributeAdded, Ni3ItemListener.SRC_Unknown, mainFrame,
							dynAttr);

					if (log.isDebugEnabled()){
						log.debug("Loaded dynamic attribute, entity = " + entity + ", entity from = " + entityFrom
								+ ", attribute = " + attribute + ", operation = " + operation + ", scope = " + scope);
					}
				}
			}

		}
	}

	private static final Logger log = Logger.getLogger(HistoryManager.class);

	private final MainPanel mainFrame;
	private final Ni3Document Doc;

	private GraphController graphController;

	private int currentSelection;
	private List<HistoryItem> historyItems = new ArrayList<HistoryItem>();

	public HistoryManager(final MainPanel parent){
		mainFrame = parent;
		Doc = parent.Doc;
		graphController = new GraphController(mainFrame);
	}

	public void addHistoryData(final int SchemaID, final int ChartID, Map<Integer, ChartParams> chartParams,
			final int MapID, final int ThematicMapID, final GraphCollection graph, final List<DBObject> DS,
			final GraphPanelSettings gpset, final MapSettings mapSettings, final DataFilter filter,
			final DataFilter prefilter, final List<Query> queries, final FavoriteMode mode, final String metaphorSet,
			final boolean showNumericMetaphors, List<Integer> overlayIds, MatrixSortOrder matrixSort,
			PolygonModel polyModel, CommandPanelSettings commandPanelSettings, Set<Edge> inPathEdges){
		saveCurrentState(SchemaID, ChartID, chartParams, MapID, ThematicMapID, graph, DS, gpset, mapSettings, filter,
				prefilter, queries, mode, metaphorSet, showNumericMetaphors, overlayIds, matrixSort, polyModel,
				commandPanelSettings, inPathEdges);
		currentSelection = historyItems.size();
	}

	public void saveCurrentState(final int SchemaID, final int ChartID, Map<Integer, ChartParams> chartParams,
			final int MapID, final int ThematicMapID, final GraphCollection graph, final List<DBObject> DS,
			final GraphPanelSettings gpset, final MapSettings mapSettings, final DataFilter filter,
			final DataFilter prefilter, final List<Query> queries, final FavoriteMode mode, final String metaphorSet,
			final boolean showNumericMetaphors, List<Integer> overlayIds, MatrixSortOrder matrixSort,
			PolygonModel polyModel, CommandPanelSettings commandPanelSettings, Set<Edge> inPathEdges){

		if (historyItems.size() > 95){
			historyItems.subList(0, 10).clear();
			currentSelection -= 10;
		}
		final HistoryItem hi = new HistoryItem(SchemaID, ChartID, MapID, ThematicMapID, graph, DS, gpset, mapSettings,
				filter, prefilter, queries, mode, metaphorSet, showNumericMetaphors, overlayIds);
		hi.setChartParams(chartParams);
		hi.setMatrixSort(matrixSort);
		hi.setPolyModel(polyModel);
		hi.setCommandPanelSettings(commandPanelSettings);
		hi.setInPathEdges(inPathEdges);
		if (!isLastUndo()){
			historyItems.set(currentSelection, hi);
			historyItems.subList(currentSelection + 1, historyItems.size()).clear();
		} else
			historyItems.add(hi);
	}

	public void back(){
		Utility.debugToConsole("Back", 2);
		if (currentSelection > 0){
			currentSelection--;
			restoreState(historyItems.get(currentSelection), false, true);
		}
	}

	public void forward(){
		Utility.debugToConsole("Forward", 2);

		if (currentSelection < historyItems.size() - 1){
			currentSelection++;
			restoreState(historyItems.get(currentSelection), false, true);
		}
	}

	public void clear(){
		currentSelection = 0;
		historyItems.clear();
	}

	public void cloneContextEdges(final int oldTopicID, final int newTopicID){
		if (oldTopicID == 0){
			return;
		}

		final GraphCollection bunch = new GraphCollection(false);
		List<Edge> edges = new HttpGraphGatewayImpl().getEdgesByFavorite(newTopicID, Doc.SchemaID, Doc.DB.getDataFilter());
		if (edges != null){
			bunch.addResultToGraph(edges);
		}

		Doc.DB.getSubgraphData(bunch);

		ObjectManagementGateway objGateway = new HttpObjectManagementGatewayImpl();

		for (final Edge e : bunch.getEdges()){
			if (e.Obj.getEntity().isContextEdge()){
				objGateway.insertEdge(e.Obj, newTopicID, e.from.ID, e.to.ID);
				e.ID = e.Obj.getId();
			}
		}
	}

	public void DeleteNodeThroughHistory(final Node node){
		for (HistoryItem hi : historyItems){
			hi.graph.simpleRemoveNode(node.ID);
		}
	}

	public void deleteEdgeThroughHistory(Edge e){
		for (HistoryItem hi : historyItems){
			hi.graph.simpleRemoveEdge(e);
		}
	}

	public void getTopicEdges(final int favoriteID, final GraphCollection graph){
		List<Edge> edges = new HttpGraphGatewayImpl().getEdgesByFavorite(favoriteID, Doc.SchemaID, Doc.DB.getDataFilter());
		if (edges != null){
			graph.addResultToGraph(edges);
		}
	}

	public boolean isLastUndo(){
		return (currentSelection == historyItems.size());
	}

	public void loadContextData(final int favoriteID, final GraphCollection graph){
		for (final Entity e : Doc.DB.schema.definitions){
			final Context c = e.getContext("Favorites");
			if (c != null){
				final List<DBObject> set = new ArrayList<DBObject>();
				for (final GraphObject o : graph.getObjects()){
					if (o.Obj != null && o.Obj.getEntity().ID == e.ID){
						set.add(o.Obj);
					}
				}

				if (!set.isEmpty()){
					Doc.DB.getObjectContext(set, c, Integer.toString(favoriteID));
				}
			}
		}
	}

	public void restoreState(final HistoryItem hi, final boolean executeQueries, final boolean fireEvents){
		Utility.debugToConsole("Restore state", 2);

		graphController.setGraph(hi, executeQueries, fireEvents);

		Doc.Subgraph.restoreLayout();

		mainFrame.setMainPanelLayout(hi.layout);

		if (fireEvents){
			Doc.dispatchEvent(Ni3ItemListener.MSG_RecalculateStatistics, Ni3ItemListener.SRC_Doc, null, this);
		}
		restorePath(hi);
		mainFrame.repaint();
	}

	private void restorePath(HistoryItem hi){
		List<Edge> pathEdges = new ArrayList<Edge>();
		for (Integer id : hi.inPathEdges){
			Edge e = Doc.Subgraph.findEdge(id);
			if (e != null)
				pathEdges.add(e);
			else
				log.warn("Cannot find edge with id " + id + " for path");
		}
		if (pathEdges.size() == hi.inPathEdges.size()){
			Doc.resetInPathEdges();
			Doc.getInPathEdges().addAll(pathEdges);
		} else{
			log.warn("Saved path is broken, cannot find one/more then one edge(s)");
		}
	}

	public void saveContextData(final int favoriteID, final GraphCollection graph){
		ObjectManagementGateway objGateway = new HttpObjectManagementGatewayImpl();
		for (final Entity e : Doc.DB.schema.definitions){
			final Context c = e.getContext("Favorites");
			if (c != null){
				for (final GraphObject o : graph.getObjects()){
					if (o.Obj.getEntity().ID == e.ID && o.Obj.hasContextValues(c)){
						final Object value = c.pk.getDataType().getValue(Integer.toString(favoriteID));
						o.Obj.setValue(c.pk.ID, value);
						objGateway.setContext(o.Obj, c, favoriteID, o.status != 0);
					}
				}
			}
		}
	}

	public void saveContextEdges(final GraphCollection bunch, final int oldTopicID, final int newTopicID){
		if (oldTopicID == 0){
			return;
		}

		ObjectManagementGateway objGateway = new HttpObjectManagementGatewayImpl();

		for (final Edge e : bunch.getEdges()){
			if (e.Obj.getEntity().isContextEdge() && e.favoritesID == oldTopicID){
				objGateway.insertEdge(e.Obj, newTopicID, e.from.ID, e.to.ID);
				e.ID = e.Obj.getId();
			}
		}
	}

}
