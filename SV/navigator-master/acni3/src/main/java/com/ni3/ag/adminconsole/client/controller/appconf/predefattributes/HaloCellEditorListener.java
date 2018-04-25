/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.client.controller.appconf.predefattributes;

import java.util.ArrayList;

import java.util.List;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.view.appconf.PredefinedAttributeEditView;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.PredefinedAttributeEditModel;

public class HaloCellEditorListener implements CellEditorListener{
	private PredefinedAttributeEditController controller;
	private final static Logger log = Logger.getLogger(HaloCellEditorListener.class);

	public HaloCellEditorListener(PredefinedAttributeEditController controller){
		this.controller = controller;
	}

	/**
	 * Checks all predefineds that are connected with current and shows an error if they have halo set
	 * 
	 * @param current
	 */
	void checkNestedPredefinedsForHalosSet(PredefinedAttribute current){
		PredefinedAttributeEditModel model = controller.getModel();
		for (PredefinedAttribute pa : model.getCurrentPredefinedAttributes())
			if (!pa.equals(current) && current.equals(pa.getNestedTo()) && pa.getHaloColor() != null
			        && !"".equals(pa.getHaloColor())){
				List<ErrorEntry> errors = new ArrayList<ErrorEntry>();
				errors.add(new ErrorEntry(TextID.WarnThisDeletesHaloForNestedPredefineds,
				        new String[] { current.getLabel() }));
				controller.getView().renderErrors(errors);
				break;
			}
	}

	@Override
	public void editingStopped(ChangeEvent e){
		PredefinedAttributeEditView view = controller.getView();
		view.clearErrors();

		int index = view.getSelectedAttributeModelIndex();
		if (index == -1)
			return;

		checkNestedPredefinedHalos(view.getSelectedPredefinedAttribute());
	}

	void checkNestedPredefinedHalos(PredefinedAttribute pa){

		if (pa.getHaloColor() != null && !"".equals(pa.getHaloColor())){
			log.debug("------------------------");
			log.debug("predefined `" + pa.getLabel() + "` halo color not empty - checking for nested predefineds");
			List<PredefinedAttribute> nestedPredefineds = getNestedPredefinedAttributes(pa);

			for (PredefinedAttribute p : nestedPredefineds){
				p.setNested(true);
				p.setNestedTo(pa);
			}
		}

		// show warning message
		checkNestedPredefinedsForHalosSet(pa);
	}

	List<PredefinedAttribute> getNestedPredefinedAttributes(PredefinedAttribute pa){
		PredefinedAttributeEditModel model = controller.getModel();

		List<PredefinedAttribute> allPredefineds = model.getCurrentPredefinedAttributes();
		log.debug("total predefineds: " + allPredefineds.size());

		// unmark predefineds which were nested to this earlier
		for (PredefinedAttribute p : allPredefineds){
			if (pa.equals(p.getNestedTo())){
				p.setNested(false);
				p.setNestedTo(null);
			}
		}

		// get top parents - "roots" for current object definition
		List<PredefinedAttribute> roots = new ArrayList<PredefinedAttribute>();
		int depth = getPredefinedRootsAndDepthLevel(pa, roots, allPredefineds);
		log.debug(roots.size() + " roots found, depth in tree: " + depth);

		// search for nested predefineds
		List<PredefinedAttribute> nestedPredefineds = new ArrayList<PredefinedAttribute>();
		for (PredefinedAttribute root : roots){
			searchNestedPredefineds(root, nestedPredefineds, depth, 0);
			log.debug("nested predefineds so far (root " + root.getId() + "):");
			for (PredefinedAttribute nestedPa : nestedPredefineds)
				log.debug("   " + nestedPa.getId() + " (`" + nestedPa.getLabel() + "`); " + "halo " + nestedPa.getHaloColor());
		}

		return nestedPredefineds;
	}

	int getPredefinedRootsAndDepthLevel(PredefinedAttribute currentPa, List<PredefinedAttribute> roots,
	        List<PredefinedAttribute> allPredefineds){
		int depth = 0;
		for (PredefinedAttribute pa : allPredefineds){
			pa.setLevel(0);
			PredefinedAttribute rootParent = findRootParent(pa);
			if (pa.equals(currentPa))
				depth = pa.getLevel();
			if (!roots.contains(rootParent))
				roots.add(rootParent);
		}
		return depth;
	}

	PredefinedAttribute findRootParent(PredefinedAttribute pa){
		PredefinedAttribute root = pa;
		while (root.getParent() != null){
			root = root.getParent();
			pa.setLevel(pa.getLevel() + 1);
		}
		return root;
	}

	/**
	 * Finds all nested predefined attributes recursively and adds them to result. This method suggests that parent is
	 * the top-most root of the tree, so it never moves up in the tree
	 */
	void searchNestedPredefineds(PredefinedAttribute parent, List<PredefinedAttribute> result, int level, int currentLevel){
		if (currentLevel != level)
			result.add(parent);
		List<PredefinedAttribute> currentChildren = parent.getChildren();
		if (currentChildren == null)
			return;

		// go down the tree, never up
		for (PredefinedAttribute child : currentChildren)
			searchNestedPredefineds(child, result, level, currentLevel + 1);
	}

	@Override
	public void editingCanceled(ChangeEvent e){

	}

}
