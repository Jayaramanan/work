/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;

import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.client.domain.*;
import com.ni3.ag.navigator.client.domain.cache.IconCache;
import com.ni3.ag.navigator.client.domain.metaphor.NumericMetaphor;
import com.ni3.ag.navigator.client.gateway.ReportGateway;
import com.ni3.ag.navigator.client.gateway.impl.ReportGatewayImpl;
import com.ni3.ag.navigator.client.gui.datalist.DBObjectList;
import com.ni3.ag.navigator.client.gui.datalist.DataSetTableModel;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NRequest.ReportAttribute;
import com.ni3.ag.navigator.shared.proto.NRequest.ReportData;
import com.ni3.ag.navigator.shared.proto.NRequest.ReportRow;
import org.apache.log4j.Logger;

public class Ni3ReportGenerator{

	private final ReportGateway reportGateway = new ReportGatewayImpl();

	private static Logger log = Logger.getLogger(Ni3ReportGenerator.class);
	public static final int METAPHOR_ATTRIBUTE_ID = -1;
	private static final String JPG = "jpg";
	private static final String PNG = "png";
	private static final String GIF = "gif";

	private ReportTemplate template;
	private List<DBObjectList> matrix;
	private Image mapImage;
	private Image graphImage;
	private boolean showNumericMetaphors;

	public Ni3ReportGenerator(ReportTemplate template, Image graphImage, Image mapImage, List<DBObjectList> matrix,
			boolean showNumericMetaphors){
		this.graphImage = graphImage;
		this.mapImage = mapImage;
		this.matrix = matrix;
		this.template = template;
		this.showNumericMetaphors = showNumericMetaphors;
	}

	public byte[] getReport(ReportFormat format) {
		byte[] report = null;
		if (template == null) {
			log.error("No template found or template is invalid for report " + template.getName());
			return null;
		}

		List<NRequest.ReportData> data = null;
		if (ReportType.MERGED.equals(template.getType())) {
			data = getReportDataForMerged();
		} else {
			data = getReportData();
		}

		final byte[] graphImg = getImageBytes(graphImage, JPG);
		final byte[] mapImg = mapImage != null ? getImageBytes(mapImage, JPG) : null;
		final Image logoImage = IconCache.getImage(IconCache.REPORT_NI3_LOGO);
		final byte[] logoImg = getImageBytes(logoImage, PNG);

		report = reportGateway.getReport(template.getId(), format, graphImg, mapImg, logoImg, data);

		return report;
	}

	public List<ReportAttribute> getAttributes(DBObjectList object, List<Integer> selectedColumns, boolean hasMetaphor){
		List<ReportAttribute> attributes = new ArrayList<ReportAttribute>();
		DataSetTableModel model = object.listDescription.getModel();
		DataSetTableModel scrollableModel = object.listDescription.getScrollableModel();
		if (model == null || model.getColumnCount() <= 0){
			return null;
		}

		for (int c = 2; c < model.getColumnCount(); c++){
			Integer attrID = model.getAttribute(c).ID;
			if (selectedColumns.contains(attrID)){
				Attribute attr = model.getAttribute(c);
				ReportAttribute.Builder rAttr = createReportAttribute(attr);
				attributes.add(rAttr.build());
			}
		}
		for (int c = 0; c < scrollableModel.getColumnCount(); c++){
			Integer attrID = scrollableModel.getAttribute(c).ID;
			if (selectedColumns.contains(attrID)){
				Attribute attr = scrollableModel.getAttribute(c);
				ReportAttribute.Builder rAttr = createReportAttribute(attr);
				attributes.add(rAttr.build());
			}
		}
		return attributes;
	}

	private ReportAttribute.Builder createReportAttribute(Attribute attr){
		ReportAttribute.Builder rAttr = ReportAttribute.newBuilder();
		rAttr.setId(attr.ID);
		rAttr.setName(attr.name);
		rAttr.setLabel(attr.label);
		rAttr.setIsDynamic(attr.isDynamic());
		return rAttr;
	}

	private ReportAttribute.Builder createReportAttribute(int id, String name, String label){
		ReportAttribute.Builder rAttr = ReportAttribute.newBuilder();
		rAttr.setId(id);
		rAttr.setName(name);
		rAttr.setLabel(label);
		rAttr.setIsDynamic(false);
		return rAttr;
	}

	private List<ReportData> getReportDataForMerged() {
		List<ReportData> dataList = new ArrayList<ReportData>();

		Collection<Node> nodes = getPersonNodesThatAreNotFilteredOut();
		Map<Integer, List<Integer>> selectedColumnMap = template.getSelectedColumns();
		List<Integer> selectedColumnsForPersons = selectedColumnMap.get(35);
		ReportData.Builder data = ReportData.newBuilder();
		List<ReportAttribute> attributes = null;
		for (Node node : nodes) {
//			int entityId = node.Obj.getEntity().ID;
//			if (entityId != 35) { //Persons
//				continue;
//			}

			if (selectedColumnsForPersons == null || selectedColumnsForPersons.size() == 0) {
				continue;
			}
			boolean hasMetaphor = selectedColumnsForPersons.contains(METAPHOR_ATTRIBUTE_ID);


			//only need to do this once
			if (attributes == null){
				attributes = getAttributes(nodes, selectedColumnsForPersons, selectedColumnMap.get(36), hasMetaphor);
				data.addAllAttributes(attributes);
				data.setHasMetaphor(hasMetaphor);
				data.setIsNumericMetaphor(showNumericMetaphors);
				data.setName(node.Obj.getEntity().Name);
				data.setEntityId(node.Obj.getEntity().ID);
			}

			List<ReportRow> rows = getReportRowsForMerged(node, selectedColumnsForPersons, selectedColumnMap.get(36), selectedColumnsForPersons.contains(METAPHOR_ATTRIBUTE_ID));
			if (!rows.isEmpty()){
				data.addAllRows(rows);
			}


//			List<ReportAttribute> attributes = getAttributes(node.Obj, selectedColumns, hasMetaphor);
//			data.addAllAttributes(attributes);
		}
		dataList.add(data.build());



		return dataList;
	}

	private Collection<Node> getPersonNodesThatAreNotFilteredOut() {
		Collection<Node> res = new ArrayList<Node>();
		Collection<Node> nodes = matrix.get(0).Doc.Subgraph.getNodes();
		for (Node n : nodes){
			if (n.Obj.getEntity().ID == 35 && !n.isFilteredOut()){
				res.add(n);
			}
		}
		return res;
	}

	public List<ReportAttribute> getAttributes(Collection<Node> nodes, List<Integer> selectedColumnsForPersons, List<Integer> selectedColumnsForOrgs, boolean hasMetaphor){
		Node object = null;
		boolean found = false;
		for (Node node : nodes){
			if (found){
				break;
			}
			object = node;
			if (!node.inEdges.isEmpty()){
				for (Edge e : node.inEdges){
					if (e.from.Obj.getEntity().ID == 36  && !e.isFilteredOut() && !e.from.isFilteredOut()){
						found = true;
						break;
					}
				}
			}
			if (!node.outEdges.isEmpty()){
				for (Edge e : node.outEdges){
					if (e.to.Obj.getEntity().ID == 36 && !e.isFilteredOut() && !e.to.isFilteredOut()){
						found = true;
						break;
					}
				}
			}
		}
		List<ReportAttribute> attributes = new ArrayList<ReportAttribute>();
		for (Integer attributeId : selectedColumnsForPersons){
			if (attributeId == -1){
//				ReportAttribute.Builder rAttr = createReportAttribute(-1, "", "");
//				attributes.add(rAttr.build());
			} else {
				Attribute attribute = object.Obj.getEntity().getAttribute(attributeId);
				ReportAttribute.Builder rAttr = createReportAttribute(attributeId, attribute.name, attribute.label);
				attributes.add(rAttr.build());
			}

		}

		List<Edge> inEdges = object.inEdges;
		List<Edge> outEdges = object.outEdges;
		List<Node> orgs = new ArrayList<Node>();

		if (!inEdges.isEmpty()){
			for (Edge e : inEdges){
				if (e.from.Obj.getEntity().ID == 36  && !e.isFilteredOut() && !e.from.isFilteredOut()){
					orgs.add(e.from);
				}
			}
		}

		if (!outEdges.isEmpty()){
			for (Edge e : outEdges){
				if (e.to.Obj.getEntity().ID == 36 && !e.isFilteredOut() && !e.to.isFilteredOut()){
					orgs.add(e.to);
				}
			}
		}

		if (!orgs.isEmpty()){
			for (Integer attributeId : selectedColumnsForOrgs){
				if (attributeId == -1 ){
//							ReportAttribute.Builder rAttr = createReportAttribute(-1, "", "");
//							attributes.add(rAttr.build());
				} else {
					String name = orgs.get(0).Obj.getEntity().getAttribute(attributeId).name;
					String label = orgs.get(0).Obj.getEntity().getAttribute(attributeId).label;
					ReportAttribute.Builder rAttr = createReportAttribute(attributeId, name, label);
					attributes.add(rAttr.build());
				}
			}
		}

		//DataSetTableModel model = object.listDescription.getModel();
		//DataSetTableModel scrollableModel = object.listDescription.getScrollableModel();
		//if (model == null || model.getColumnCount() <= 0){
//			return null;
//		}
//
//		 (int c = 2; c < model.getColumnCount(); c++){
//			Integer attrID = model.getAttribute(c).ID;
//			if (selectedColumns.contains(attrID)){
//				Attribute attr = model.getAttribute(c);
//				ReportAttribute.Builder rAttr = createReportAttribute(attr);
//				attributes.add(rAttr.build());
//			}
//		}
//		for (int c = 0; c < scrollableModel.getColumnCount(); c++){
//			Integer attrID = scrollableModel.getAttribute(c).ID;
//			if (selectedColumns.contains(attrID)){
//				Attribute attr = scrollableModel.getAttribute(c);
//				ReportAttribute.Builder rAttr = createReportAttribute(attr);
//				attributes.add(rAttr.build());
//			}
//		}

		return attributes;
	}



	private List<ReportData> getReportData(){
		List<ReportData> dataList = new ArrayList<ReportData>();
		for (DBObjectList object : matrix){
			Integer entityID = object.listDescription.getEntity().ID;
			Map<Integer, List<Integer>> selectedColumnMap = template.getSelectedColumns();
			List<Integer> selectedColumns = selectedColumnMap.get(entityID);
			if (selectedColumns == null || selectedColumns.size() == 0){
				continue;
			}
			boolean hasMetaphor = selectedColumns.contains(METAPHOR_ATTRIBUTE_ID);

			ReportData.Builder data = ReportData.newBuilder();
			List<ReportAttribute> attributes = getAttributes(object, selectedColumns, hasMetaphor);
			data.addAllAttributes(attributes);
			data.setHasMetaphor(hasMetaphor);
			data.setIsNumericMetaphor(showNumericMetaphors);
			data.setName(object.Name);
			data.setEntityId(entityID);

			List<ReportRow> rows = getReportRows(object, selectedColumns, hasMetaphor);
			if (!rows.isEmpty()){
				data.addAllRows(rows);
			}
			dataList.add(data.build());
		}
		return dataList;
	}

	private List<ReportRow> getReportRows(DBObjectList object, List<Integer> selectedColumns, boolean hasMetaphor){
		List<ReportRow> rows = new ArrayList<ReportRow>();

		DataSetTableModel fixedModel = object.listDescription.getModel();
		DataSetTableModel scrollableModel = object.listDescription.getScrollableModel();

		for (int r = 0; r < fixedModel.getRowCount(); r++){
			boolean isSelected = (Boolean) fixedModel.getValueAt(r, 0);
			if (!isSelected){
				continue; // row not selected
			}
			ReportRow.Builder row = ReportRow.newBuilder();

			if (hasMetaphor){
				Object value = fixedModel.getValueAt(r, 1);
				if (showNumericMetaphors && value instanceof NumericMetaphor){
					row.setIndex(((NumericMetaphor) value).getIndex());
				} else if (value instanceof Image){
					DBObject obj = fixedModel.getDBObjectAt(r).obj;
					String extension = getIconExtension(obj);
					final byte[] imageBytes = getImageBytes((Image) value, extension);
					ByteString bs = ByteString.copyFrom(imageBytes);
					row.setMetaphor(bs);
				}
			}

			for (int c = 2; c < fixedModel.getColumnCount(); c++){
				Integer attrID = fixedModel.getAttribute(c).ID;
				if (selectedColumns.contains(attrID)){
					Attribute a = fixedModel.getAttribute(c);
					final Object value = fixedModel.getValueAt(r, c);
					row.addValues(prepareValue(value, a));
				}
			}
			for (int c = 0; c < scrollableModel.getColumnCount(); c++){
				Integer attrID = scrollableModel.getAttribute(c).ID;
				if (selectedColumns.contains(attrID)){
					Attribute a = scrollableModel.getAttribute(c);
					final Object value = scrollableModel.getValueAt(r, c);
					row.addValues(prepareValue(value, a));
				}
			}
			rows.add(row.build());
		}

		return rows;
	}

	private List<ReportRow> getReportRowsForMerged(Node node, List<Integer> selectedColumnsForPersons, List<Integer> selectedColumnsForOrgs,boolean hasMetaphor) {
		List<ReportRow> rows = new ArrayList<ReportRow>();

		ReportRow.Builder row = ReportRow.newBuilder();
		for (Integer attributeId : selectedColumnsForPersons) {
			if (attributeId == -1) {
				String extension = getIconExtension(node.Obj);
				final byte[] imageBytes = getImageBytes(node.Obj.getMetaphor().getIcon(), extension);
				ByteString bs = ByteString.copyFrom(imageBytes);
				row.setMetaphor(bs);
			} else {
				Object attributeValue = node.Obj.getData().get(attributeId);
				String s = prepareValue(row, attributeValue);
				row.addValues(s);
			}
		}


		List<Node> orgs = getOrgList(node);
		if (orgs.isEmpty()) {
			for (Integer attributeId : selectedColumnsForOrgs) {
				row.addValues("");
			}
			rows.add(row.build());
		} else {
			Map<Integer, String> attributeIdToString = initializeAttributeIdToStringMap(selectedColumnsForOrgs);
			for (Node org : orgs) {
				for (Integer attributeId : selectedColumnsForOrgs) {
					if (attributeId == -1) {
						continue;
					}
					Object attributeValue = org.Obj.getData().get(attributeId);
					String s = prepareValue(row, attributeValue);
					attributeIdToString.put(attributeId, attributeIdToString.get(attributeId) + s + ";");
//					row.addValues(s);
				}
			}
			for (Integer attributeId : selectedColumnsForOrgs) {
				row.addValues(attributeIdToString.get(attributeId));
			}
			rows.add(row.build());
		}





//			for (Integer attributeId : selectedColumnsForOrgs ){
//				if (attributeId != -1) {
//					Object attributeValue = "";
//					row.addValues((String) attributeValue);
//				}
//			}




//			for (int c = 2; c < fixedModel.getColumnCount(); c++){
//				Integer attrID = fixedModel.getAttribute(c).ID;
//				if (selectedColumns.contains(attrID)){
//					Attribute a = fixedModel.getAttribute(c);
//					final Object value = fixedModel.getValueAt(r, c);
//					row.addValues(prepareValue(value, a));
//				}
//			}
//			for (int c = 0; c < scrollableModel.getColumnCount(); c++){
//				Integer attrID = scrollableModel.getAttribute(c).ID;
//				if (selectedColumns.contains(attrID)){
//					Attribute a = scrollableModel.getAttribute(c);
//					final Object value = scrollableModel.getValueAt(r, c);
//					row.addValues(prepareValue(value, a));
//				}
//			}
//			rows.add(row.build());
//		}

		return rows;
	}

	private HashMap<Integer, String> initializeAttributeIdToStringMap(List<Integer> selectedColumnsForOrgs) {
		HashMap<Integer, String> res = new HashMap<Integer, String>();
		for (Integer attributeId : selectedColumnsForOrgs){
			res.put(attributeId, "");
		}
		return res;
	}

	private List<Node> getOrgList(Node node) {
		List<Node> orgs = new ArrayList<Node>();

		List<Edge> inEdges = node.inEdges;
		if (!inEdges.isEmpty()){
			for (Edge e : inEdges){
				if (e.from.Obj.getEntity().ID == 36  && !e.isFilteredOut() && !e.from.isFilteredOut()){
					orgs.add(e.from);
				}
			}
		}

		List<Edge> outEdges = node.outEdges;
		if (!outEdges.isEmpty()){
			for (Edge e : outEdges){
				if (e.to.Obj.getEntity().ID == 36  && !e.isFilteredOut() && !e.to.isFilteredOut()){
					orgs.add(e.to);
				}
			}
		}
		return orgs;
	}

	private String prepareValue(ReportRow.Builder row, Object attributeValue) {
		String value;
		if (attributeValue instanceof String) {
            value = (String) attributeValue;
        } else if (attributeValue instanceof Value) {
            value = ((Value) attributeValue).getLabel();
        } else if (attributeValue == null){
            value = "";
        } else if (attributeValue instanceof Value[]) {
            String s = "";
            for (Value v : (Value[]) attributeValue) {
                s += v.getLabel() + "; ";
            }
            value = s;
        } else if (attributeValue instanceof Double || attributeValue instanceof Integer){
            value = String.valueOf(attributeValue);
        } else {
            throw new RuntimeException("unexpected value");
        }
		return value;
	}

	private String prepareValue(Object val, Attribute attribute){
		if (val == null || "".equals(val))
			return "";
		if (!attribute.isURLAttribute())
			return val.toString();
		String sval = (String) val;
		sval = sval.replace("<HTML>", "").replace("</HTML>", "");
		String[] ss = sval.split(";");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ss.length; i++){
			Pattern p = Pattern.compile("<A.+>(.+)</A>");
			Matcher m = p.matcher(ss[i]);
			if (m.matches()){
				if (i != 0)
					sb.append(";");
				sb.append(m.group(1));
			}
		}
		return sb.toString();
	}

	private byte[] getImageBytes(Image image, String extension){
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] result = null;
		try{
			RenderedImage rImage;
			if (image instanceof RenderedImage){
				rImage = (RenderedImage) image;
			} else{
				BufferedImage buffImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
						BufferedImage.TYPE_3BYTE_BGR);
				Graphics g = buffImage.getGraphics();
				g.drawImage(image, 0, 0, null);
				rImage = buffImage;
			}
			ImageIO.write(rImage, extension, os);
			result = os.toByteArray();
			os.close();
		} catch (IOException e){
			log.error(e);
		}
		return result;
	}

	private String getIconExtension(DBObject obj){
		String iconName = obj.getIconName();
		iconName = iconName.toLowerCase();

		String extension = JPG;
		if (iconName != null){
			if (iconName.endsWith(JPG) || iconName.endsWith("jpeg")){
				extension = JPG;
			} else if (iconName.endsWith(PNG)){
				extension = PNG;
			} else if (iconName.endsWith(GIF)){
				extension = GIF;
			}
		}
		return extension;
	}
}
