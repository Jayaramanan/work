/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Hashtable;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.thickclient.ThickClientView;
import com.ni3.ag.adminconsole.domain.OfflineJob;
import com.ni3.ag.adminconsole.shared.model.impl.ThickClientModel;
import com.ni3.ag.adminconsole.shared.service.def.UserDataExportService;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class PreviewOfflineJobListener extends ProgressActionListener{

	private ThickClientController controller;

	private ACValidationRule updateJobValidationRule;

	public PreviewOfflineJobListener(ThickClientController controller){
		super(controller);
		this.controller = controller;
		this.updateJobValidationRule = (ACValidationRule) ACSpringFactory.getInstance().getBean(
		        "OfflineClientExportJobValidationRule");
	}

	@Override
	public void performAction(ActionEvent e){
		ThickClientView view = controller.getView();
		view.setPreviewText("");
		ThickClientModel model = controller.getModel();
		int row = view.getSelectedRowIndex();
		if (row < 0){
			return;
		}
		OfflineJob job = view.getTableModel().getSelectedJob(row);
		if (job == null)
			return;
		if (!updateJobValidationRule.performCheck(model)){
			view.renderErrors(updateJobValidationRule.getErrorEntries());
			return;
		}

		UserDataExportService service = ACSpringFactory.getInstance().getUserDataExportService();
		Boolean withFirstDegree = job.getWithFirstDegreeObjects();
		boolean b = withFirstDegree != null ? withFirstDegree : false;

		Hashtable<String, Integer> tableSizes = service.getExportPreview(job.getUserIds(), b);

		String text = parseTableSizes(tableSizes);
		view.setPreviewText(text);

	}

	private String parseTableSizes(Hashtable<String, Integer> tableSizes){
		String text = "<html><b>Export preview:</b><br/>";
		Enumeration<String> en = tableSizes.keys();
		while (en.hasMoreElements()){
			String key = en.nextElement();
			text += key + " records: " + tableSizes.get(key) + "<br />";
		}
		text += "</html>";
		return text;
	}

}
