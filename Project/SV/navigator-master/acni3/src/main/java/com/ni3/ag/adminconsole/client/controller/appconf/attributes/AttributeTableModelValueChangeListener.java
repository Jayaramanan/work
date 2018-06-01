/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.attributes;

import com.ni3.ag.adminconsole.client.view.appconf.AttributeEditTableModel;
import com.ni3.ag.adminconsole.domain.InMatrixType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ErrorContainerImpl;

public class AttributeTableModelValueChangeListener{
	private static final int MAX_FIXED_COUNT = 5;

	private AttributeEditController controller;

	public AttributeTableModelValueChangeListener(AttributeEditController attributeEditController){
		controller = attributeEditController;
	}

	public boolean canChangeValue(Object aValue, ObjectAttribute oa, int columnIndex){
		if (aValue == null)
			return true;
		controller.getView().clearErrors();
		if (columnIndex != AttributeEditTableModel.INMATRIX_COLUMN_INDEX)
			return true;
		InMatrixType inMatrixType = (InMatrixType) aValue;
		if (getFixedCount(oa) >= MAX_FIXED_COUNT && InMatrixType.Fixed.equals(inMatrixType)
		        && !inMatrixType.getValue().equals(oa.getInMatrix())){
			ErrorContainer ec = new ErrorContainerImpl();
			ec.getErrors().add(new ErrorEntry(TextID.MsgTooManyFixedAttributes));
			controller.getView().renderErrors(ec);
			return false;
		}
		return true;
	}

	private int getFixedCount(ObjectAttribute oa){
		int count = 0;
		for (ObjectAttribute a : oa.getObjectDefinition().getObjectAttributes()){
			if (InMatrixType.Fixed.getValue().equals(a.getInMatrix()))
				count++;
		}
		return count;
	}

}
