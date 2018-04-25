/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.reports.impl;

import ar.com.fdvs.dj.core.DJJRDesignHelper;
import ar.com.fdvs.dj.domain.DynamicJasperDesign;
import ar.com.fdvs.dj.domain.DynamicReport;

public class Ni3DJJRDesignHelper extends DJJRDesignHelper{

	public static void populateReportOptionsFromDesign(DynamicJasperDesign jd, DynamicReport dr){
		DJJRDesignHelper.populateReportOptionsFromDesign(jd, dr);
	}
}
