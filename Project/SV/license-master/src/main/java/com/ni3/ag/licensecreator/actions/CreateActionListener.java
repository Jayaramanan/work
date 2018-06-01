package com.ni3.ag.licensecreator.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.license.KeyStore;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.shared.service.def.ChecksumEncoder;
import com.ni3.ag.adminconsole.shared.service.impl.CustomChecksumEncoder;
import com.ni3.ag.licensecreator.gui.LicenseCreator;
import com.ni3.ag.licensecreator.model.LicenseCreatorModel;
import com.ni3.ag.licensecreator.model.PropertyTableModel;
import com.smardec.license4j.License;
import com.smardec.license4j.LicenseManager;

public class CreateActionListener implements ActionListener{
	private static final Logger log = Logger.getLogger(CreateActionListener.class);
	private LicenseCreator licenseCreator;
	private ChecksumEncoder encoder = new CustomChecksumEncoder();
	private LicenseCreatorModel model;

	public CreateActionListener(){
		this(null);

	}

	public CreateActionListener(LicenseCreator licenseCreator){
		this.licenseCreator = licenseCreator;
		model = new LicenseCreatorModel();
	}

	private void populateDataFromView(){
		model.setAdminCount(licenseCreator.getAdminCount());
		model.setExpiryDate(licenseCreator.getExpiryDate());
		model.setStartDate(licenseCreator.getStartDate());
		model.setSystemId(licenseCreator.getSystemId());
		model.setClient(licenseCreator.getClient());
		PropertyTableModel tModel = (PropertyTableModel) licenseCreator.getPropertyTable().getModel();
		model.setPropertyTableModel(tModel);
	}

	public void setModel(LicenseCreatorModel model){
		this.model = model;
	}

	public void actionPerformed(ActionEvent e){

		populateDataFromView();

		if (licenseCreator.isUserModuleSelected()){
			Integer userId = licenseCreator.getUserId();
			String sql = generateUserModuleSql(userId);
			licenseCreator.setLicenseText(sql);
			return;
		}

		try{
			Date start = model.getStartDate();
			Date end = model.getExpiryDate();
			if (end.before(start)){
				JOptionPane.showMessageDialog(licenseCreator, "Please check dates: Expiry date < start date");
				return;
			}
			String product = licenseCreator.getProduct();
			String original = makeLicensebySelectedProduct(product);
			String slicense = makeQuery(original, product);

			licenseCreator.setLicenseText(slicense);

			LicenseManager.setPublicKey(KeyStore.publicKey);
			ByteArrayInputStream bis = new ByteArrayInputStream(original.getBytes());
			com.smardec.license4j.License smartLicense = LicenseManager.loadLicense(bis);
			licenseCreator.setLicenseValid(LicenseManager.isValid(smartLicense));
		} catch (Exception ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(licenseCreator, ex.getClass() + "\n" + ex.getMessage());
		}
	}

	public String makeLicensebySelectedProduct(String product) throws IllegalArgumentException, GeneralSecurityException,
	        IOException{
		LicenseManager
		        .setPrivateKey("3082014B0201003082012C06072A8648CE3804013082011F02818100FD7F53811D75122952DF4A9C2EECE4E7F611B7523CEF4400C31E3F80B6512669455D402251FB593D8D58FABFC5F5BA30F6CB9B556CD7813B801D346FF26660B76B9950A5A49F9FE8047B1022C24FBBA9D7FEB7C61BF83B57E7C6A8A6150F04FB83F6D3C51EC3023554135A169132F675F3AE2B61D72AEFF22203199DD14801C70215009760508F15230BCCB292B982A2EB840BF0581CF502818100F7E1A085D69B3DDECBBCAB5C36B857B97994AFBBFA3AEA82F9574C0B3D0782675159578EBAD4594FE67107108180B449167123E84C281613B7CF09328CC8A6E13C167A8B547C8D28E0A3AE1E2BB3A675916EA37F0BFA213562F1FB627A01243BCCA4F1BEA8519089A883DFE15AE59F06928B665E807B552564014C3BFECF492A041602147B99B646B45ADC69F2CC85EC2984802C9BB97440");

		Date start = model.getStartDate();
		String startStr = LicenseData.LICENSE_DATE_FORMAT.format(start);
		log.debug("Start date = " + startStr);
		Date end = model.getExpiryDate();
		String endStr = LicenseData.LICENSE_DATE_FORMAT.format(end);
		log.debug("End date = " + endStr);
		Integer adminCount = model.getAdminCount();
		String systemId = model.getSystemId();
		String clientName = model.getClient();
		if (systemId != null)
			systemId = systemId.trim();
		if (clientName != null)
			clientName = clientName.trim();
		License license = new License();
		license.addFeature(LicenseData.PRODUCT_NAME_PROPERTY, product);
		license.addFeature(LicenseData.START_DATE_PROPERTY, new StringBuffer(startStr));
		license.addFeature(LicenseData.EXPIRY_DATE_PROPERTY, new StringBuffer(endStr));
		license.addFeature(LicenseData.USER_COUNT_PROPERTY, adminCount);

		if (systemId != null && !systemId.isEmpty())
			license.addFeature(LicenseData.SYSTEM_ID_PROPERTY, systemId);
		if (clientName != null && !clientName.isEmpty())
			license.addFeature(LicenseData.CLIENT_PROPERTY, clientName);

		List<Object[]> data = model.getPropertyTableModel().getData();
		for (Object[] o : data){
			license.addFeature((String) o[0], o[1]);
		}

		StringWriter sw = new StringWriter();
		LicenseManager.saveLicense(license, sw);
		String original = sw.getBuffer().toString();
		return original;
	}

	private String makeQuery(String slicense, String product){
		if (licenseCreator.makeQuery())
			return "insert into sys_licenses(product, license) values ('" + product + "', '" + slicense.replace("\n", "\\n")
			        + "');";
		else
			return slicense;
	}

	public String generateUserModuleSql(Integer userId){
		String sql = "";
		sql += generateInsertSql(userId, LicenseData.BASE_MODULE);
		sql += generateInsertSql(userId, LicenseData.DATA_CAPTURE_MODULE);
		sql += generateInsertSql(userId, LicenseData.CHARTS_MODULE);
		sql += generateInsertSql(userId, LicenseData.MAPS_MODULE);
		sql += generateInsertSql(userId, LicenseData.GEO_ANALYTICS_MODULE);
		sql += generateInsertSql(userId, LicenseData.REMOTE_CLIENT_MODULE);
		sql += generateInsertSql(userId, LicenseData.REPORTS_MODULE);
		return sql;
	}

	private String generateInsertSql(Integer userId, String module){
		String checksum = encoder.encode(userId, module);
		String sql = "insert into sys_user_edition (userId, editionId, checksum) values(" + userId + ", '" + module + "', '"
		        + checksum + "'); \n";
		return sql;
	}
}
