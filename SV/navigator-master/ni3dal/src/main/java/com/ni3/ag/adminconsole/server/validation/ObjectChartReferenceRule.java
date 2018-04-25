/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.ObjectChart;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class ObjectChartReferenceRule implements ACValidationRule{

	private static Logger log = Logger.getLogger(ObjectChartReferenceRule.class);

	private List<ErrorEntry> errors;

	public boolean performCheck(AbstractModel m){
		log.debug("perform check for object definition reference from sys_object_chart");
		errors = new ArrayList<ErrorEntry>();
		SchemaAdminModel model = (SchemaAdminModel) m;
		ObjectDefinition od = model.getCurrentObjectDefinition();
		if (od == null || od.getSchema() == null)
			return true;
		Schema schema = od.getSchema();

		Set<Chart> refCharts = new HashSet<Chart>();
		for (Chart chart : schema.getCharts()){
			for (ObjectChart oc : chart.getObjectCharts()){
				if (od.equals(oc.getObject())){
					refCharts.add(chart);
					break;
				}
			}
		}
		if (!refCharts.isEmpty()){
			addErrorMessage(od, refCharts);
		}

		return errors.isEmpty();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

	private void addErrorMessage(ObjectDefinition od, Set<Chart> refCharts){
		log.debug("form error message");
		StringBuffer sb = new StringBuffer();
		for (Chart ch : refCharts){
			sb.append(", ");
			sb.append(ch.getName());
		}
		String[] params = new String[2];
		params[0] = od.getName();
		params[1] = sb.toString().substring(2);
		log.warn("Object definition `" + od.getName() + "` is referenced from charts: " + params[1]);
		errors.add(new ErrorEntry(TextID.MsgObjectReferencedFromCharts, params));
	}
}