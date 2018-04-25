/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.mock;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.License;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.server.dao.LicenseDAO;

public class LicenseDAOMock implements LicenseDAO{

	@Override
	public void delete(License license){
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll(List<License> licenses){
		// TODO Auto-generated method stub

	}

	@Override
	public License getLicense(License license){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<License> getLicenseByProduct(String product){
		List<License> list = new ArrayList<License>();
		License lic = new License();
		lic.setProduct(product);
		if (LicenseData.NAVIGATOR_PRODUCT.equals(product)){
			lic.setLicense("com.ni3.ag.license.productName=Navigator\n"
			        + "com.ni3.ag.license.startDate=rO0ABXNyABZqYXZhLmxhbmcuU3RyaW5nQnVmZmVyLwcH2erI6tMDAANJAAVjb3VudFoABnNoYXJlZFsABXZhbHVldAACW0N4cAAAAAoAdXIAAltDsCZmsOJdhKwCAAB4cAAAABoAMAA1AC4AMQAxAC4AMgAwADEAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAeA==\n"
			        + "com.ni3.ag.license.expiryDate=rO0ABXNyABZqYXZhLmxhbmcuU3RyaW5nQnVmZmVyLwcH2erI6tMDAANJAAVjb3VudFoABnNoYXJlZFsABXZhbHVldAACW0N4cAAAAAoAdXIAAltDsCZmsOJdhKwCAAB4cAAAABoAMAA1AC4AMQAxAC4AMgAwADIAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAeA==\n"
			        + "com.ni3.ag.license.userCount=rO0ABXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAF\n"
			        + "com.ni3.ag.navigator.base=rO0ABXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAB\n"
			        + "com.ni3.ag.navigator.datacapture=rO0ABXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAB\n"
			        + "com.ni3.ag.navigator.charts=rO0ABXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAB\n"
			        + "com.ni3.ag.navigator.maps=rO0ABXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAB\n"
			        + "com.ni3.ag.navigator.geoanalytics=rO0ABXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAB\n"
			        + "com.ni3.ag.navigator.remoteclient=rO0ABXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAB\n"
			        + "com.ni3.ag.navigator.reports=rO0ABXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAB\n"
			        + "SignCode=FF07\n"
			        + "Signature=302D02150086F181E39937347C9B0E2DB0DFDBEAC1C2F0F02D0214518B483A3CA10169D8F043040FCE6A8F98020F57");
		} else if (LicenseData.ACNi3WEB_PRODUCT.equals(product)){
			lic.setLicense("com.ni3.ag.license.productName=Admin Console\n"
			        + "com.ni3.ag.license.startDate=rO0ABXNyABZqYXZhLmxhbmcuU3RyaW5nQnVmZmVyLwcH2erI6tMDAANJAAVjb3VudFoABnNoYXJlZFsABXZhbHVldAACW0N4cAAAAAoAdXIAAltDsCZmsOJdhKwCAAB4cAAAABoAMAA1AC4AMQAxAC4AMgAwADEAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAeA==\n"
			        + "com.ni3.ag.license.expiryDate=rO0ABXNyABZqYXZhLmxhbmcuU3RyaW5nQnVmZmVyLwcH2erI6tMDAANJAAVjb3VudFoABnNoYXJlZFsABXZhbHVldAACW0N4cAAAAAoAdXIAAltDsCZmsOJdhKwCAAB4cAAAABoAMAA1AC4AMQAxAC4AMgAwADIAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAeA==\n"
			        + "com.ni3.ag.license.userCount=rO0ABXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAF\n"
			        + "com.ni3.ag.adminconsole.users=rO0ABXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAB\n"
			        + "com.ni3.ag.adminconsole.schema=rO0ABXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAB\n"
			        + "com.ni3.ag.adminconsole.metaphor=rO0ABXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAB\n"
			        + "com.ni3.ag.adminconsole.language=rO0ABXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAB\n"
			        + "com.ni3.ag.adminconsole.charts=rO0ABXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAB\n"
			        + "com.ni3.ag.adminconsole.geo=rO0ABXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAB\n"
			        + "com.ni3.ag.adminconsole.diagnostics=rO0ABXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAB\n"
			        + "com.ni3.ag.adminconsole.offline=rO0ABXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAB\n"
			        + "com.ni3.ag.adminconsole.reports=rO0ABXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAB\n"
			        + "com.ni3.ag.adminconsole.etl=rO0ABXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAB\n"
			        + "SignCode=FF3F\n"
			        + "Signature=302C0214770BC3ABDB65BAF9DDA56D806BFCE82EA38DDDA70214681449B57FE7FACF9ACABC9F9F05E9930EEE1523");
		}
		list.add(lic);
		return list;
	}

	@Override
	public List<License> getLicenses(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public License merge(License l){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public License saveOrUpdate(License license){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveOrUpdateAll(List<License> licenses){
		// TODO Auto-generated method stub

	}

}
