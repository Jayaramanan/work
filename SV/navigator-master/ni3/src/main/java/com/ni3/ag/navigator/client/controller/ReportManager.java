/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.controller;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import com.ni3.ag.navigator.client.domain.*;
import com.ni3.ag.navigator.client.gui.reports.ReportColumnSelectionTreeModel;
import com.ni3.ag.navigator.client.gui.reports.ReportPreviewDialog;
import com.ni3.ag.navigator.client.gui.reports.TreeAttribute;
import com.ni3.ag.navigator.client.gui.reports.TreeEntity;
import com.ni3.ag.navigator.client.model.Ni3Document;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import org.apache.log4j.Logger;

import com.ni3.ag.navigator.client.gateway.ReportGateway;
import com.ni3.ag.navigator.client.gateway.impl.ReportGatewayImpl;
import com.ni3.ag.navigator.client.gui.datalist.DBObjectList;
import com.ni3.ag.navigator.client.gui.datalist.DataSetTableModel;
import com.ni3.ag.navigator.client.gui.graph.GraphPanel;
import com.ni3.ag.navigator.client.gui.map.MapView;
import com.ni3.ag.navigator.shared.proto.NResponse.Report;

public class ReportManager{
	private static final Logger log = Logger.getLogger(ReportManager.class);
	private final ReportGateway reportGateway = new ReportGatewayImpl();
	private ReportPreviewDialog dlg;
	private Map<TreeEntity, List<TreeAttribute>> attributeMap;
	private List<TreeEntity> entities;
	private List<DBObjectList> matrix;

	private GraphPanel graphPanel;
	private MapView mapView;
	private Ni3Document doc;

	public ReportManager(Ni3Document doc, List<Report> reports){
		List<ReportTemplate> templates = new ArrayList<ReportTemplate>();
		this.doc = doc;
		for (final Report report : reports){
			final ReportTemplate template = new ReportTemplate(report.getName(), report.getId());
			if (report.getType() == Report.ReportType.DYNAMIC){
				template.setType(ReportType.DYNAMIC_REPORT);
			} else{
				template.setType(ReportType.STATIC_REPORT);
			}

			//hack for Sharon
			if (report.getId() == 20 ){
				template.setType(ReportType.MERGED);
				HashMap<Integer, List<Integer>> initialValues = new HashMap<Integer, List<Integer>>();
				initialValues.put(35, getPersonsInitialSelection());
				initialValues.put(36, getOrgInitialSelection());
				initialValues.put(37, new ArrayList<Integer>());
				template.setSelectedColumns(initialValues);
			}

			if (report.getPreview() != null && !report.getPreview().isEmpty()){
				final ImageIcon icon = new ImageIcon(report.getPreview().toByteArray());
				template.setPreviewIcon(icon);
			}
			templates.add(template);
		}
		dlg = new ReportPreviewDialog();
		dlg.initListModel(templates);
		dlg.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e){
				if (e.getValueIsAdjusting())
					return;
				dlg.checkTreeVisibility();
				resetTreeChanges();
			}
		});
		dlg.addXlsButtonListener(new LaunchReportListener(ReportFormat.XLS));
		dlg.addPdfButtonListener(new LaunchReportListener(ReportFormat.PDF));
		dlg.addResetButtonListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				resetTreeChanges();
			}
		});
		dlg.addCancelButtonListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				resetTreeChanges();
				dlg.setVisible(false);
			}
		});
	}

	private ArrayList<Integer> getPersonsInitialSelection() {
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		arrayList.add(324);
		arrayList.add(323);
		arrayList.add(325);
		arrayList.add(329);
		return arrayList;
	}

	private ArrayList<Integer> getOrgInitialSelection() {
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		arrayList.add(351);
		arrayList.add(1029);
		arrayList.add(360);
		arrayList.add(353);
		arrayList.add(361);
		arrayList.add(1037);
		return arrayList;
	}

	public void showDialog(List<DBObjectList> matrix, GraphPanel graphPanel, MapView mapView){
		this.graphPanel = graphPanel;
		this.mapView = mapView;
		this.matrix = matrix;
		initTreeModel(matrix);
		dlg.showDialog();
	}

	private void onLaunchReport(ReportFormat format){
		final ReportTemplate selectedTemplate = dlg.getSelectedReport();
		if (selectedTemplate == null){
			return;
		}

		applyChanges();
		dlg.setVisible(false);

		SystemGlobals.MainFrame.startAnimation();

		byte[] report = null;
		if (selectedTemplate.isDynamicReport()){
			try{
				final BufferedImage graphImage = new BufferedImage(graphPanel.getWidth(), graphPanel.getHeight(),
				        BufferedImage.TYPE_3BYTE_BGR);
				final Graphics graphGraphics = graphImage.getGraphics();
				graphPanel.print(graphGraphics, null, 0, graphPanel.getSize());

				Image mapImage = null;
				if (mapView != null && SystemGlobals.MainFrame.mapsShown() && mapView.getHeight() > 0
				        && mapView.getWidth() > 0){
					mapImage = mapView.getMapImage();
				}

				final Ni3ReportGenerator gen = new Ni3ReportGenerator(selectedTemplate, graphImage, mapImage, matrix, doc
				        .isShowNumericMetaphors());

				report = gen.getReport(format);
				if (report == null){
					JOptionPane.showMessageDialog(dlg, UserSettings.getWord("MsgCannotShowReport"), UserSettings
					        .getWord("Error"), JOptionPane.ERROR_MESSAGE);
				}

			} catch (final PrinterException e1){
				log.error("Cannot launch report, error: " + e1.getMessage());
			}
		} else if (selectedTemplate.isStaticReport()){
			report = reportGateway.getReport(selectedTemplate.getId(), format, null, null, null, null);
		}

		SystemGlobals.MainFrame.stopAnimation(123);

		if (report != null){
			saveReport(selectedTemplate, report, format);
		}
	}

	private boolean saveReport(ReportTemplate template, byte[] report, ReportFormat format){
		try{
			File file = requestFolderToSave(template, format);
			if (file == null)
				return true;

			FileOutputStream fs = new FileOutputStream(file);
			fs.write(report);
			fs.close();
		} catch (IOException ex){
			log.error(ex);
			return false;
		}
		return true;
	}

	private File requestFolderToSave(ReportTemplate template, ReportFormat format) throws IOException{
		final String fileType = getFileType(format);
		final String resolution = getResolution(format);
		File sFile = new File(template.getName() + resolution);
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle(UserSettings.getWord("Save"));
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setSelectedFile(sFile);
		fileChooser.resetChoosableFileFilters();
		fileChooser.setFileFilter(new FileFilter(){
			@Override
			public boolean accept(File f){
				return f.isDirectory() || f.getName().toLowerCase().endsWith(resolution);
			}

			@Override
			public String getDescription(){
				return fileType;
			}
		});

		int result = fileChooser.showDialog(dlg, UserSettings.getWord("Save"));
		File f = null;
		if (result == JFileChooser.APPROVE_OPTION){
			f = fileChooser.getSelectedFile();
			if (f.exists()){
				int response = JOptionPane.showConfirmDialog(dlg, UserSettings.getWord("QuestionOverwriteExistingFile"),
				        UserSettings.getWord("ConfirmOverwrite"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.NO_OPTION)
					return null;
			}
			if (!f.getName().toLowerCase().endsWith(resolution))
				f = new File(f.getAbsolutePath() + resolution);
			f.createNewFile();
		}

		return f;
	}

	private String getResolution(ReportFormat format){
		String resolution = "";
		switch (format){
			case XLS:
				resolution = ".xls";
				break;
			case PDF:
				resolution = ".pdf";
				break;
			case HTML:
				resolution = ".html";
				break;
		}
		return resolution;
	}

	private String getFileType(ReportFormat format){
		String ft = "";
		switch (format){
			case XLS:
				ft = UserSettings.getWord("MicrosoftExcelFiles");
				break;
			case PDF:
				ft = UserSettings.getWord("PDFFiles");
				break;
			case HTML:
				ft = UserSettings.getWord("HTMLFiles");
				break;
		}
		return ft;
	}

	private void initTreeModel(List<DBObjectList> matrix){
		attributeMap = new HashMap<TreeEntity, List<TreeAttribute>>();
		entities = new ArrayList<TreeEntity>();
		for (DBObjectList o : matrix){
			TreeEntity entity = new TreeEntity(o.listDescription.getEntity());
			List<TreeAttribute> attributes = getAttributes(o);
			entities.add(entity);
			attributeMap.put(entity, attributes);
		}
		dlg.setTreeModel(new ReportColumnSelectionTreeModel(attributeMap, entities));
	}

	private List<TreeAttribute> getAttributes(DBObjectList object){
		List<TreeAttribute> attributes = new ArrayList<TreeAttribute>();
		DataSetTableModel model = object.listDescription.getModel();
		DataSetTableModel scrollableModel = object.listDescription.getScrollableModel();
		if (model == null || model.getColumnCount() <= 0){
			return null;
		}

		// attribute for metaphors
		Attribute metaphorAttr = createMetaphorAttribute();
		attributes.add(new TreeAttribute(metaphorAttr));

		// skip columns: 0-selection, 1-metaphor
		for (int c = 2; c < model.getColumnCount(); c++){
			Attribute attr = model.getAttribute(c);
			attributes.add(new TreeAttribute(attr));
		}
		for (int c = 0; c < scrollableModel.getColumnCount(); c++){
			Attribute attr = scrollableModel.getAttribute(c);
			attributes.add(new TreeAttribute(attr));
		}
		return attributes;
	}

	private Attribute createMetaphorAttribute(){
		Attribute metaphorAttr = new Attribute();
		metaphorAttr.ID = -1;
		metaphorAttr.label = UserSettings.getWord("Metaphor");
		return metaphorAttr;
	}

	protected void resetTreeChanges(){
		ReportTemplate report = dlg.getSelectedReport();
		if (report == null || !report.isDynamicReport()){
			return;
		}
		Map<Integer, List<Integer>> scMap = report.getSelectedColumns();
		for (TreeEntity entity : entities){
			Integer entityID = entity.getEntity().ID;
			boolean contains = scMap.containsKey(entityID);
			List<Integer> columns = scMap.get(entityID);

			List<TreeAttribute> attributes = attributeMap.get(entity);
			boolean noneSelected = true;
			for (TreeAttribute attr : attributes){
				Integer attrID = attr.getAttribute().ID;
				boolean selected = !contains || columns.contains(attrID);
				attr.setSelected(selected);
				if (selected)
					noneSelected = false;
			}
			entity.setSelected(!noneSelected);
		}
		dlg.refreshColumnTree();
	}

	private void applyChanges(){
		ReportTemplate report = dlg.getSelectedReport();
		if (report == null || !report.isDynamicReport()){
			return;
		}
		for (TreeEntity entity : entities){
			Map<Integer, List<Integer>> selectedColumns = report.getSelectedColumns();
			Integer id = entity.getEntity().ID;
			if (!selectedColumns.containsKey(id)){
				selectedColumns.put(id, new ArrayList<Integer>());
			}
			List<Integer> attrIDs = selectedColumns.get(id);
			attrIDs.clear();
			List<TreeAttribute> attributes = attributeMap.get(entity);
			for (TreeAttribute attr : attributes){
				if (attr.isSelected()){
					Integer attrID = attr.getAttribute().ID;
					attrIDs.add(attrID);
				}
			}
		}
	}

	private class LaunchReportListener implements ActionListener{
		private ReportFormat format;

		public LaunchReportListener(ReportFormat format){
			this.format = format;
		}

		@Override
		public void actionPerformed(ActionEvent e){
			onLaunchReport(format);
		}

	}
}
