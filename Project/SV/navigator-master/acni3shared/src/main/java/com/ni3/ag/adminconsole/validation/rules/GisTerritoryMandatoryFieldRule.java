/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.GisTerritory;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.GeoAnalyticsModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class GisTerritoryMandatoryFieldRule implements ACValidationRule{
	private List<ErrorEntry> errors;

	@Override
	public boolean performCheck(AbstractModel amodel){
		errors = new ArrayList<ErrorEntry>();
		GeoAnalyticsModel model = (GeoAnalyticsModel) amodel;
		List<GisTerritory> terrs = model.getGisTerritories();
		if (terrs == null)
			return true;
		for (GisTerritory gt : terrs){
			if (gt.getTerritory() == null || gt.getTerritory().isEmpty() || gt.getLabel() == null || gt.getLabel().isEmpty()){
				errors.add(new ErrorEntry(TextID.MsgNotAllRequeredFieldsHasValues));
				return false;
			}
		}
		return true;
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
