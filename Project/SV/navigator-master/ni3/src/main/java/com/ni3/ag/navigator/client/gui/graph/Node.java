/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.graph;

import java.util.*;
import java.util.List;
import java.awt.*;

import com.ni3.ag.navigator.client.controller.charts.SNA;
import com.ni3.ag.navigator.client.controller.charts.SNA.SNAAttribute;
import com.ni3.ag.navigator.client.domain.ChartFilter;
import com.ni3.ag.navigator.client.domain.DataFilter;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.Value;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.shared.domain.ChartType;
import com.ni3.ag.navigator.shared.util.StringTokenizerEx;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXMLAttribute;

public class Node extends GraphObject{
	public class ChartSliceValue{
		public double value;
		public Color color;

		public ChartSliceValue(double value, Color color){
			this.value = value;
			this.color = color;
		}
	}

	public static final double DEFAULT_SCALE_FACTOR = 0.66; // Default value for scale slider

	public static Map<Integer, Double> maxGraphHaloRadiusPerType = new HashMap<Integer, Double>();
	public static double maxGraphHaloR; // Radius of node with biggest halo

	// TODO consider removing
	public static double MaxMetaphorZoom; // Maximum possible size of metaphor

	public static double MaxMetaphorBlink, MinMetaphorBlink; // Start/end scales for metaphor blink

	public static int BlinkPhase = 0;

	public static double nodeScaleGraph = 0.66;

	public static Image anchor; // Anchor icon definition - shows if node is focus node
	public static int anchorW, anchorH;
	public static boolean markFocusNodes; // Show anchor on focus nodes

	private double x; // Position of node on graph
	private double y;

	public int contractedRelativesCount; // Number of relatives that are contracted and still in graph
	private int childrenCount; // Number of relatives in global graph

	public boolean ZigZag;

	public int fathersCount;

	public double fx; // Spring layout - change of coordinates
	public double fy;

	public int degree; // Degree of node
	private int level; // level when node has been expanded
	private int metaphorWidth, metaphorHeight; // Size of metaphor on graph (without scaling)
	private double metaphorRadius; // Radius of metaphor on graph (without scaling)
	private int chartNegativePartHeight;

	public boolean selected; // node selected,
	public boolean selectedFrom; // node selected as From node for edge creation
	public boolean selectedTo; // node selected as To node for edge creation

	public double scaleFactor; // Scale factor for rendering this node

	public boolean fixed; // Node is focus node and can not be moved during layout
	private boolean expandedManualy; // Node is expanded manually

	public List<Edge> inEdges; // List of edges related to fathers
	public List<Edge> outEdges; // List of edges related to children

	private Set<Integer> roots;

	public boolean filteredOutByChartAF; // True if this node is filtered out by chart legend rules

	private List<ChartSliceValue> chartValues;

	private double chartTotal; // Sum of chart values
	private double chartR; // Radius of chart
	public double chartTotalWithoutFiltering; // Sum of non-filtered chart values

	private boolean marked;
	private boolean leading;
	private boolean layouted;
	public int layoutHierarchyLevel;
	private int layoutParent;
	public int layoutChildrenCount;
	public int ilayout1, ilayout2; // layout manager variables

	public double betweenness, closeness, eigenvector, clustering, delta; // SNA variables
	public int degreeCentrality, sigma, d;

	private DataFilter selectiveExpandDataFilter; // Filter used if this node was selectively expanded
	public int labelWidth;

	public Node(int CountChildren, int CountFathers){
		super();

		scaleFactor = 0.66;
		MinMetaphorBlink = 0.1;
		MaxMetaphorBlink = 0.99;
		inEdges = new ArrayList<Edge>(CountFathers);
		outEdges = new ArrayList<Edge>(CountChildren);

		roots = new HashSet<Integer>();
		degree = -1;
		level = -1000;
		childrenCount = CountChildren;
		fathersCount = CountFathers;

		metaphorRadius = 10.0;
		metaphorWidth = metaphorHeight = 1;

		chartValues = new ArrayList<ChartSliceValue>();

		resetChart();
	}

	public Node(Node n){
		ID = n.ID;
		Type = n.Type;
		Obj = n.Obj;
		x = n.x;
		y = n.y;

		leading = n.leading;

		contractedRelativesCount = n.contractedRelativesCount;
		childrenCount = n.childrenCount;
		fathersCount = n.fathersCount;

		fx = n.fx;
		fy = n.fy;
		degree = n.degree;
		metaphorWidth = n.metaphorWidth;
		metaphorHeight = n.metaphorHeight;
		metaphorRadius = n.metaphorRadius;

		status = n.status;

		selected = n.selected;
		selectedFrom = n.selectedFrom;
		selectedTo = n.selectedTo;

		scaleFactor = n.scaleFactor;

		fixed = n.fixed;
		contracted = n.contracted;
		setFilteredOut(n.isFilteredOut());
		expandedManualy = n.expandedManualy;
		filteredOutByChartAF = n.filteredOutByChartAF;

		if (n.lbl != null)
			lbl = n.lbl;

		if (n.fathersCount > 0)
			inEdges = new ArrayList<Edge>(n.fathersCount);
		else
			inEdges = new ArrayList<Edge>(1);

		if (n.childrenCount > 0)
			outEdges = new ArrayList<Edge>(n.childrenCount);
		else
			outEdges = new ArrayList<Edge>(1);

		roots = new HashSet<Integer>(n.getRoots());

		chartTotal = n.getChartTotal();
		chartTotalWithoutFiltering = n.chartTotalWithoutFiltering;
		chartR = n.chartR;

		chartValues = new ArrayList<ChartSliceValue>();
		chartValues.addAll(n.chartValues);

		marked = n.marked;
		level = n.level;

		ilayout1 = n.ilayout1;
		ilayout2 = n.ilayout2;

		selectiveExpandDataFilter = n.selectiveExpandDataFilter;
	}

	public int getChildrenCount(){
		return childrenCount;
	}

	public int getFathersCount(){
		return fathersCount;
	}

	public void setChildrenCount(int childrenCount){
		this.childrenCount = childrenCount;
	}

	public void setFathersCount(int fathersCount){
		this.fathersCount = fathersCount;
	}

	public double getX(){
		return x;
	}

	public void setX(double x){
		this.x = x;
	}

	public double getY(){
		return y;
	}

	public void setY(double y){
		this.y = y;
	}

	public double getChartTotal(){
		return chartTotal;
	}

	public void setChartTotal(double chartTotal){
		this.chartTotal = chartTotal;
	}

	public double getLon(){
		return Obj != null ? Obj.getLon() : 0;
	}

	public void setLon(double lon){
		if (Obj != null){
			Obj.setLon(lon);
		}
	}

	public double getLat(){
		return Obj != null ? Obj.getLat() : 0;
	}

	public void setLat(double lat){
		if (Obj != null){
			Obj.setLat(lat);
		}
	}

	public boolean hasChart(){
		return !chartValues.isEmpty();
	}

	public boolean isExpandedManualy(){
		return expandedManualy;
	}

	public void setExpandedManualy(boolean expandedManualy){
		this.expandedManualy = expandedManualy;
	}

	public int getLevel(){
		return level;
	}

	public void setLevel(int level){
		this.level = level;
	}

	public DataFilter getSelectiveExpandDataFilter(){
		return selectiveExpandDataFilter;
	}

	public void setSelectiveExpandDataFilter(DataFilter selectiveExpandDataFilter){
		this.selectiveExpandDataFilter = selectiveExpandDataFilter;
	}

	public boolean isMarked(){
		return marked;
	}

	public void setMarked(boolean marked){
		this.marked = marked;
	}

	public double getChartR(){
		return chartR;
	}

	public void setChartR(double chartR){
		this.chartR = chartR;
	}

	public int getMetaphorWidth(){
		return metaphorWidth;
	}

	public void setMetaphorWidth(int metaphorWidth){
		this.metaphorWidth = metaphorWidth;
	}

	public int getMetaphorHeight(){
		return metaphorHeight;
	}

	public void setMetaphorHeight(int metaphorHeight){
		this.metaphorHeight = metaphorHeight;
	}

	public void setMetaphorRadius(double metaphorRadius){
		this.metaphorRadius = metaphorRadius;
	}

	public int getScaledMetaphorHeight(boolean defaultScale){
		return (int) (metaphorHeight * (defaultScale ? DEFAULT_SCALE_FACTOR : scaleFactor));
	}

	public int getScaledMetaphorWidth(boolean defaultScale){
		return (int) (metaphorWidth * (defaultScale ? DEFAULT_SCALE_FACTOR : scaleFactor));
	}

	public int getScaledMetaphorRadius(boolean defaultScale){
		return (int) (metaphorRadius * (defaultScale ? DEFAULT_SCALE_FACTOR : scaleFactor));
	}

	public Set<Integer> getRoots(){
		return roots;
	}

	public int getChartNegativePartHeight(){
		return chartNegativePartHeight;
	}

	public void setChartNegativePartHeight(int chartNegativePartHeight){
		this.chartNegativePartHeight = chartNegativePartHeight;
	}

	public boolean isLayouted(){
		return layouted;
	}

	public void setLayouted(boolean layouted){
		this.layouted = layouted;
	}

	public int getLayoutParent(){
		return layoutParent;
	}

	public void setLayoutParent(int layoutParent){
		this.layoutParent = layoutParent;
	}

	public boolean isLeading(){
		return leading;
	}

	public void setLeading(boolean leading){
		this.leading = leading;
	}

	public String toString(){
		return toXML();
	}

	public String toXML(){
		StringBuilder ret = new StringBuilder(2048);

		ret.append("<Node ID='").append(ID).append("' Leading='");
		ret.append(leading).append("' Position='");
		ret.append(x).append(",");
		ret.append(y).append("' F='");
		ret.append(fx).append(",");
		ret.append(fy).append("' Degree='");
		ret.append(degree).append("' Level='");
		ret.append(level).append("' Selected='");
		ret.append(selected).append("' SelectedFrom='");
		ret.append(selectedFrom).append("' SelectedTo='");
		ret.append(selectedTo).append("'");
		ret.append(" ScaleFactor='");
		ret.append(scaleFactor).append("' Fixed='");
		ret.append(fixed).append("' Contracted='");
		ret.append(contracted).append("' ExpandedManualy='");
		ret.append(expandedManualy).append("'");
		ret.append(" Roots='");
		boolean first = true;
		for (Integer root : roots){
			if (!first)
				ret.append(",");
			ret.append(root);
			first = false;
		}
		ret.append("' ");

		if (selectiveExpandDataFilter != null && selectiveExpandDataFilter.filter != null){
			first = true;
			ret.append("SelectiveExpand='");

			for (Value v : selectiveExpandDataFilter.filter.values()){
				if (!first)
					ret.append(",");
				ret.append(v.getId());
				first = false;
			}
			ret.append("' ");
		}

		ret.append(" Marked='").append(marked).append("' />\n");

		return ret.toString();
	}

	public void fromXML(NanoXML xml){

		NanoXMLAttribute attr;

		while ((attr = xml.Tag.getNextAttribute()) != null){
			if ("ID".equals(attr.Name)){
				ID = attr.getIntegerValue();
			} else if ("Leading".equals(attr.Name)){
				leading = attr.getBooleanValue();
			} else if ("ExpandedManualy".equals(attr.Name)){
				expandedManualy = attr.getBooleanValue();
			} else if ("Selected".equals(attr.Name)){
				selected = attr.getBooleanValue();
			} else if ("SelectedFrom".equals(attr.Name)){
				selectedFrom = attr.getBooleanValue();
			} else if ("SelectedTo".equals(attr.Name)){
				selectedTo = attr.getBooleanValue();
			} else if ("Fixed".equals(attr.Name)){
				fixed = attr.getBooleanValue();
			} else if ("Marked".equals(attr.Name)){
				marked = attr.getBooleanValue();
			} else if ("Degree".equals(attr.Name)){
				degree = attr.getIntegerValue();
			} else if ("Level".equals(attr.Name)){
				level = attr.getIntegerValue();
			} else if ("ScaleFactor".equals(attr.Name)){
				scaleFactor = attr.getDoubleValue();
			} else if ("Position".equals(attr.Name)){
				int pos = attr.Value.indexOf(",");
				if (pos != -1){
					try{
						x = Double.parseDouble(attr.Value.substring(0, pos));
						y = Double.parseDouble(attr.Value.substring(pos + 1));
					} catch (NumberFormatException e){
						// ignore
					}
				}
			} else if ("F".equals(attr.Name)){
				int pos = attr.Value.indexOf(",");
				if (pos != -1){
					try{
						fx = Double.parseDouble(attr.Value.substring(0, pos));
						fy = Double.parseDouble(attr.Value.substring(pos + 1));
					} catch (NumberFormatException e){
						// ignore
					}
				}
			} else if ("Roots".equals(attr.Name)){
				StringTokenizerEx tok = new StringTokenizerEx(attr.Value, ",", false);
				while (tok.hasMoreTokens()){
					roots.add(tok.nextIntegerToken());
				}
			} else if ("SelectiveExpand".equals(attr.Name)){
				selectiveExpandDataFilter = new DataFilter();
				StringTokenizerEx tok = new StringTokenizerEx(attr.Value, ",", false);
				while (tok.hasMoreTokens()){
					selectiveExpandDataFilter.addExclusion(tok.nextIntegerToken());
				}
			}
		}
	}

	public void copyGeometry(Node n){
		x = n.x;
		y = n.y;

		leading = n.leading;
		expandedManualy = n.expandedManualy;

		fx = n.fx;
		fy = n.fy;
		degree = n.degree;
		level = n.level;

		selected = n.selected;
		selectedFrom = n.selectedFrom;
		selectedTo = n.selectedTo;

		scaleFactor = n.scaleFactor;
		fixed = n.fixed;

		roots = new HashSet<Integer>(n.getRoots());

		marked = n.marked;

		selectiveExpandDataFilter = n.selectiveExpandDataFilter;
	}

	public void resetChart(){
		chartValues.clear();
		chartTotal = 0.0;
		chartTotalWithoutFiltering = 0.0;
	}

	public boolean removeRoot(int rootId){
		boolean result = false;
		if (roots.contains(rootId)){
			roots.remove(rootId);
			result = !leading;
		}

		return result;
	}

	void expand(){
		if (contracted){
			contracted = false;
		}

		for (Edge e : inEdges){
			if (e.from.degree > degree)
				e.Expand(this);
		}

		for (Edge e : outEdges){
			if (e.to.degree > degree)
				e.Expand(this);
		}
	}

	private boolean canContract(){
		for (Edge e : inEdges){
			if (e.isActive()){
				if (e.from.degree <= degree){
					if ((!e.from.isMarked() || !e.from.contracted) && e.from.isActive())
						return false;
				}
			}
		}

		for (Edge e : outEdges){
			if (e.isActive()){
				if (e.to.degree <= degree){
					if ((!e.to.isMarked() || !e.to.contracted) && e.to.isActive())
						return false;
				}
			}
		}

		return true;
	}

	public void contract(boolean first){
		if (first || !contracted){
			if (!first){
				if (!canContract())
					return;
			}

			contracted = true;

			for (Edge e : inEdges){
				if (e.from.degree > degree && e.from.isMarked()){
					e.Contract(this);
				}
			}

			for (Edge e : outEdges){
				if (e.to.degree > degree && e.to.isMarked()){
					e.Contract(this);
				}
			}
		}

		if (first)
			contracted = false;
	}

	public boolean IsPointInNode(ChartType chartType, double _x, double _y){
		boolean result = false;
		if (isActive()){
			Rectangle rect;
			final int width = getScaledMetaphorWidth(true);
			final int height = getScaledMetaphorHeight(true);
			if (hasChart() && ChartType.Bar == chartType){
				int addHeight = (int) (getChartNegativePartHeight() * DEFAULT_SCALE_FACTOR);
				rect = new Rectangle((int) x, (int) y - height, width, height + addHeight);
			} else{
				rect = new Rectangle((int) x - width / 2, (int) y - height / 2, width, height);
			}
			result = rect.contains(_x, _y);
		}
		return result;
	}

	public boolean IsNodeInRectangle(double _x, double _y, double _w, double _h){
		if (isActive()){
			final int width = getScaledMetaphorWidth(false);
			final int height = getScaledMetaphorHeight(false);
			return (_x <= x - width / 2 && _x + _w >= x + width / 2 && _y <= y - height / 2 && _y + _h >= y + height / 2);
		} else
			return false;
	}

	public int getExternalRelatives(){
		return childrenCount + fathersCount - outEdges.size() - inEdges.size();
	}

	public boolean haveMoreThanOneRelative(){
		int ID = -1;
		for (Edge e : outEdges){
			if (ID == -1)
				ID = e.to.ID;

			if (ID != e.to.ID)
				return false;
		}

		for (Edge e : inEdges){
			if (ID == -1)
				ID = e.from.ID;

			if (ID != e.from.ID)
				return false;
		}

		return ID != -1;

	}

	synchronized public void recalculateGraphValues(DataFilter filter){
		chartTotal = 0.0;
		chartTotalWithoutFiltering = 0.0;

		int chartId = SystemGlobals.MainFrame.Doc.getCurrentChartId();
		ChartFilter cFilter = filter.getChartFilter(chartId != SNA.SNA_CHART_ID ? Obj.getEntity().ID
				: Entity.COMMON_ENTITY_ID);
		for (int n = 0; n < chartValues.size(); n++){
			ChartSliceValue csv = chartValues.get(n);
			if (csv.value != 0.0 && !cFilter.isExcluded(n)){
				if (csv.value >= cFilter.getMinChartAttrVal(n) - 0.0005
						&& csv.value <= cFilter.getMaxChartAttrVal(n) + 0.0005){
					chartTotal += csv.value;
				}

				chartTotalWithoutFiltering += csv.value;
			}
		}
	}

	public static void nextBlinkPhase(){
		BlinkPhase++;
		if (BlinkPhase > 10)
			BlinkPhase = 0;
	}

	public boolean toBlink(){
		return (BlinkPhase < 5);
	}

	public double getMaxDimension(){
		if (Obj != null && Obj.getIcon() != null){
			metaphorWidth = (int) (Obj.getIcon().getWidth(null) * DEFAULT_SCALE_FACTOR * nodeScaleGraph);
			metaphorHeight = (int) (Obj.getIcon().getHeight(null) * DEFAULT_SCALE_FACTOR * nodeScaleGraph);
		} else{
			metaphorWidth = metaphorHeight = 10;
		}

		return Math.max(metaphorWidth, metaphorHeight) * 1.42;
	}

//	@Override
//	public boolean equals(Object o){
//		return o != null && o instanceof Node && ((Node) o).ID == ID;
//	}

	public double getChartValue(int n){
		return chartValues.get(n).value;
	}

	public int getChartCount(){
		return chartValues.size();
	}

	public Color getChartColor(int i){
		return chartValues.get(i).color;
	}

	public void addChartValue(double doubleVal, Color color){
		chartValues.add(new ChartSliceValue(doubleVal, color));
	}

	public void clearChartValues(){
		chartValues.clear();
	}

	public boolean isGeoCoded(){
		return getLon() != 0 || getLat() != 0;
	}

	public Number getSnaValue(SNAAttribute attribute){
		Number value = null;
		switch (attribute){
			case DEGREE:
				value = degreeCentrality;
				break;
			case CLOSENESS:
				value = closeness;
				break;
			case BETWEENNESS:
				value = betweenness;
				break;
			case CLUSTERING:
				value = clustering;
				break;
			case EIGENVECTOR:
				value = eigenvector;
				break;
		}
		return value;
	}
}
