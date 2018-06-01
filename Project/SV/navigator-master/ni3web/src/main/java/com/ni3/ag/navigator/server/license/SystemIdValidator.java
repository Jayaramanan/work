/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.license;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.LicenseDAO;
import com.ni3.ag.navigator.server.util.SystemIdentification;
import com.smardec.license4j.LicenseManager;

public class SystemIdValidator extends HttpServlet{
	private static final long serialVersionUID = -3113757255563257210L;
	private static final Logger log = Logger.getLogger(SystemIdValidator.class);
	private static final String PRODUCT_NAME = "Navigator";
	public static final String SYSTEM_ID_PROPERTY = "com.ni3.ag.license.systemId";

	@Override
	public void init() throws ServletException{
		super.init();
		String currentSystemId = SystemIdentification.getSystemId();
		if (currentSystemId == null){
			log.warn("Error while generating system Id");
			return;
		}

		String systemId = getLicenseSystemId();
		if (systemId == null){
			log.warn("`" + SYSTEM_ID_PROPERTY + "` property not found in license");
			return;
		}
		if (!systemId.equals(currentSystemId))
			throw new ServletException("System ID ERROR: calculated=" + currentSystemId + " license=" + systemId);
		log.info("System ID validated");
	}

	private String getLicenseSystemId() throws ServletException{
		final LicenseDAO licenseDAO = NSpringFactory.getInstance().getLicenseDAO();
		List<String> licenses = licenseDAO.getNavigatorLicenses();
		if (licenses == null || licenses.isEmpty()){
			log.warn("Cannot find license for product: " + PRODUCT_NAME + " in database");
			return null;
		}

		LicenseManager.setPublicKey(com.ni3.ag.navigator.server.util.KeyStore.publicKey);
		// TODO check all licenses, not only the first one
		ByteArrayInputStream bis = new ByteArrayInputStream(licenses.get(0).getBytes());
		try{
			com.smardec.license4j.License smartLicense = LicenseManager.loadLicense(bis);
			if (!LicenseManager.isValid(smartLicense))
				throw new ServletException("Validation of license failed!");

			String currentSystemID = (String) smartLicense.getFeature(SYSTEM_ID_PROPERTY);
			return currentSystemID;
		} catch (Exception e){
			throw new ServletException(e);
		}

	}
}
