/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.shared.domain;

import java.io.Serializable;

public class License implements Serializable{
	private static final long serialVersionUID = -2264291915723620167L;

	private boolean isValid;

	private boolean baseModule;
	private boolean dataCaptureModule;
	private boolean chartsModule;
	private boolean mapsModule;
	private boolean geoAnalyticsModule;
	private boolean remoteClientModule;
	private boolean reportsModule;

	public boolean isValid(){
		return isValid;
	}

	public void setValid(boolean isValid){
		this.isValid = isValid;
	}

	public boolean hasBaseModule(){
		return baseModule;
	}

	public void setBaseModule(boolean baseModule){
		this.baseModule = baseModule;
	}

	public boolean hasDataCaptureModule(){
		return dataCaptureModule;
	}

	public void setDataCaptureModule(boolean dataCaptureModule){
		this.dataCaptureModule = dataCaptureModule;
	}

	public boolean hasChartsModule(){
		return chartsModule;
	}

	public void setChartsModule(boolean chartsModule){
		this.chartsModule = chartsModule;
	}

	public boolean hasMapsModule(){
		return mapsModule;
	}

	public void setMapsModule(boolean mapsModule){
		this.mapsModule = mapsModule;
	}

	public boolean hasGeoAnalyticsModule(){
		return geoAnalyticsModule;
	}

	public void setGeoAnalyticsModule(boolean geoAnalyticsModule){
		this.geoAnalyticsModule = geoAnalyticsModule;
	}

	public boolean hasRemoteClientModule(){
		return remoteClientModule;
	}

	public void setRemoteClientModule(boolean remoteClientModule){
		this.remoteClientModule = remoteClientModule;
	}

	public boolean hasReportsModule(){
		return reportsModule;
	}

	public void setReportsModule(boolean reportsModule){
		this.reportsModule = reportsModule;
	}

}
