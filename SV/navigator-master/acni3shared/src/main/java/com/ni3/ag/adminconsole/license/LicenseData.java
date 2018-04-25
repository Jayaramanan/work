/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.license;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.License;

public class LicenseData extends Hashtable<String, Object> implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(LicenseData.class);

	public static final String ACNi3WEB_PRODUCT = "Admin Console";
	public static final String NAVIGATOR_PRODUCT = "Navigator";
	public static final String API_PRODUCT = "API";

	public static final String USER_COUNT_PROPERTY = "com.ni3.ag.license.userCount";
	public static final String EXPIRY_DATE_PROPERTY = "com.ni3.ag.license.expiryDate";
	public static final String START_DATE_PROPERTY = "com.ni3.ag.license.startDate";
	public static final String PRODUCT_NAME_PROPERTY = "com.ni3.ag.license.productName";
	public static final String SYSTEM_ID_PROPERTY = "com.ni3.ag.license.systemId";
	public static final String CLIENT_PROPERTY = "com.ni3.ag.license.client";

	public static final String BASE_MODULE = "com.ni3.ag.navigator.base";
	public static final String DATA_CAPTURE_MODULE = "com.ni3.ag.navigator.datacapture";
	public static final String CHARTS_MODULE = "com.ni3.ag.navigator.charts";
	public static final String MAPS_MODULE = "com.ni3.ag.navigator.maps";
	public static final String GEO_ANALYTICS_MODULE = "com.ni3.ag.navigator.geoanalytics";
	public static final String REMOTE_CLIENT_MODULE = "com.ni3.ag.navigator.remoteclient";
	public static final String REPORTS_MODULE = "com.ni3.ag.navigator.reports";

	public static final String ACUSERS_MODULE = "com.ni3.ag.adminconsole.users";
	public static final String ACSCHEMA_MODULE = "com.ni3.ag.adminconsole.schema";
	public static final String ACMETAPHOR_MODULE = "com.ni3.ag.adminconsole.metaphor";
	public static final String ACLANGUAGE_MODULE = "com.ni3.ag.adminconsole.language";
	public static final String ACCHART_MODULE = "com.ni3.ag.adminconsole.charts";
	public static final String ACGEO_MODULE = "com.ni3.ag.adminconsole.geo";
	public static final String ACDIAGNOSTICS_MODULE = "com.ni3.ag.adminconsole.diagnostics";
	public static final String ACOFFLINE_MODULE = "com.ni3.ag.adminconsole.offline";
	public static final String ACREPORTS_MODULE = "com.ni3.ag.adminconsole.reports";
	public static final String ACETL_MODULE = "com.ni3.ag.adminconsole.etl";

	public static final String[] properties = { PRODUCT_NAME_PROPERTY, USER_COUNT_PROPERTY, EXPIRY_DATE_PROPERTY,
	        START_DATE_PROPERTY, SYSTEM_ID_PROPERTY, CLIENT_PROPERTY, BASE_MODULE, DATA_CAPTURE_MODULE, CHARTS_MODULE,
	        MAPS_MODULE, GEO_ANALYTICS_MODULE, REMOTE_CLIENT_MODULE, REPORTS_MODULE, ACUSERS_MODULE, ACSCHEMA_MODULE,
	        ACMETAPHOR_MODULE, ACLANGUAGE_MODULE, ACCHART_MODULE, ACGEO_MODULE, ACDIAGNOSTICS_MODULE, ACOFFLINE_MODULE,
	        ACREPORTS_MODULE, ACETL_MODULE };

	public static DateFormat LICENSE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

	public enum LicenseStatus{
		Active, NotStarted, Expired, Invalid;
	}

	private License license;
	private boolean valid = true;
	private LicenseStatus status;

	public void setLicense(License license){
		this.license = license;
	}

	public License getLicense(){
		return license;
	}

	@Override
	public synchronized Object put(String key, Object value){
		if (value == null)
			return value;
		return super.put(key, value);
	}

	public void setValid(boolean v){
		this.valid = v;
		if (!valid){
			setStatus(LicenseStatus.Invalid);
		}
	}

	public boolean isValid(){
		return valid;
	}

	public LicenseStatus getStatus(){
		return status;
	}

	public void setStatus(LicenseStatus status){
		this.status = status;
	}

	public static Date getDate(Object value){
		Date date = null;
		if (value instanceof StringBuffer || value instanceof String){
			try{
				date = LICENSE_DATE_FORMAT.parse(value.toString());
			} catch (ParseException e){
				log.error("Cannot parse date: " + value);
			}
		} else if (value instanceof Date){
			date = (Date) value;
		}
		return date;
	}

	public static StringBuffer getDateStr(Object value){
		StringBuffer date = null;
		if (value instanceof String){
			date = new StringBuffer((String) value);
		} else if (value instanceof StringBuffer){
			date = (StringBuffer) value;
		} else if (value instanceof Date){
			Date dt = (Date) value;
			date = new StringBuffer(LICENSE_DATE_FORMAT.format(dt));
		}
		return date;
	}

	public LicenseData clone(){
		LicenseData cloneLData = new LicenseData();
		cloneLData.setValid(this.isValid());

		License orig = this.getLicense();
		License clone = new License();
		clone.setId(orig.getId());
		clone.setLicense(orig.getLicense());
		clone.setProduct(orig.getProduct());
		cloneLData.setLicense(clone);

		return cloneLData;
	}

	public boolean equals(Object o){
		if (o instanceof LicenseData){
			Integer id1 = getLicense().getId();
			Integer id2 = ((LicenseData) o).getLicense().getId();
			if (id1 != null && id2 != null)
				return id1.equals(id2);
		}
		return false;
	}

	@Override
	public synchronized String toString(){
		if (license != null){
			return license.getProduct() + "(" + license.getId() + ")";
		}
		return super.toString();
	}
}
