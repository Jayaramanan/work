/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.predefattributes;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.appconf.PredefinedAttributeEditView;
import com.ni3.ag.adminconsole.client.view.appconf.PredefinedAttributeTableModel;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.shared.model.impl.PredefinedAttributeEditModel;
import com.ni3.ag.adminconsole.validation.rules.PredefinedAttributeSelectionRule;

public class GradientButtonListener extends ProgressActionListener{

	private static final Logger log = Logger.getLogger(GradientButtonListener.class);
	private PredefinedAttributeEditController controller;

	public GradientButtonListener(PredefinedAttributeEditController pController){
		super(pController);
		this.controller = pController;
	}

	@Override
	public void performAction(ActionEvent e){
		PredefinedAttributeEditView view = controller.getView();
		view.stopCellEditing();
		view.clearErrors();
		PredefinedAttributeTableModel pmodel = view.getPredefinedAttributeTableModel();
		int first = 0;
		int last = pmodel.getRowCount() - 1;
		if (last <= 0)
			return;
		first = view.getPredefinedAttributeModelIndex(first);
		last = view.getPredefinedAttributeModelIndex(last);
		PredefinedAttributeEditModel model = controller.getModel();
		PredefinedAttribute pFirst = pmodel.getPredefinedAttribute(first);
		if (pFirst.getHaloColor() == null)
			pFirst.setHaloColor(formatColor(Color.WHITE));
		PredefinedAttribute pLast = pmodel.getPredefinedAttribute(last);
		if (pLast.getHaloColor() == null)
			pLast.setHaloColor(formatColor(Color.WHITE));
		model.setFirstSelectedPredefinedAttribute(pFirst);
		model.setLastSelectedPredefinedAttribute(pLast);
		PredefinedAttributeSelectionRule rule = (PredefinedAttributeSelectionRule) ACSpringFactory.getInstance().getBean(
		        "PredefinedAttributeSelectionRule");
		if (!rule.performCheck(model)){
			view.renderErrors(rule.getErrorEntries());
			return;
		}
		String[] halos = makeGradient(pFirst.getHaloColor(), pLast.getHaloColor(), pmodel.getRowCount());
		if (halos == null)
			return;
		model.removeNestedPredefineds();
		for (int i = 0; i < pmodel.getRowCount(); i++){
			int idx = view.getPredefinedAttributeModelIndex(i);
			PredefinedAttribute pa = pmodel.getPredefinedAttribute(idx);
			pa.setHaloColor(halos[i]);
			controller.checkHalos(pa);
		}
		pmodel.fireTableDataChanged();
	}

	public static String[] makeGradient(String haloColor, String haloColor2, int rowCount){
		List<String> halos = new ArrayList<String>();
		try{
			Color color1 = Color.decode(haloColor);
			Color color2 = Color.decode(haloColor2);
			halos.add(haloColor);
			for (int j = 1; j < rowCount - 1; j++){
				float num = (float) j / (float) (rowCount - 1);
				int red = (int) (color2.getRed() * num + color1.getRed() * (1 - num));
				int green = (int) (color2.getGreen() * num + color1.getGreen() * (1 - num));
				int blue = (int) (color2.getBlue() * num + color1.getBlue() * (1 - num));
				Color co = new Color(red, green, blue);
				halos.add(formatColor(co));
			}
			halos.add(haloColor2);
			return halos.toArray(new String[] {});
		} catch (IllegalArgumentException e){
			log.error("Error making halo gradient from colors (" + haloColor + ", " + haloColor2 + ")", e);
		}
		return null;
	}

	private static String formatColor(Color co){
		String rgb = Integer.toHexString(co.getRGB());
		rgb = "#" + rgb.substring(2, rgb.length());
		return rgb;
	}
}
