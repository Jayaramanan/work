/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Icon;
import com.ni3.ag.adminconsole.domain.Metaphor;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.server.dao.NodeMetaphorDAO;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.NodeMetaphorModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class MetaphorIconDeleteValidationRule implements ACValidationRule{
	private NodeMetaphorDAO nodeMetaphorDAO;
	private List<ErrorEntry> errors;

	public void setNodeMetaphorDAO(NodeMetaphorDAO nodeMetaphorDAO){
		this.nodeMetaphorDAO = nodeMetaphorDAO;
	}

	@Override
	public boolean performCheck(AbstractModel model){
		NodeMetaphorModel nmModel = (NodeMetaphorModel) model;
		List<Icon> iconsToDelete = nmModel.getIcons();
		List<Metaphor> metaphors = nodeMetaphorDAO.getMetaphorsByIcons(iconsToDelete);
		errors = new ArrayList<ErrorEntry>();

		if (metaphors != null && metaphors.size() > 0){
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < metaphors.size(); i++){
				if (i > 0){
					sb.append(", ");
				}
				sb.append(metaphors.get(i).getIcon().getIconName());
			}
			errors.add(new ErrorEntry(TextID.MsgMetaphorIconsAreInUse, new String[] { sb.toString() }));
		}

		return errors.isEmpty();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
