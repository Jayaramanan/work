/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin.importers;


import java.io.*;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.schemaadmin.SchemaAdminController;
import com.ni3.ag.adminconsole.client.gui.MainPanel2;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminView;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.shared.service.DataGroup;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.rules.ServerErrorContainerWrapper;

public class XLSSchemaImporter implements Importer{
	private final static Logger log = Logger.getLogger(XLSSchemaImporter.class);
	private static JFileChooser chooser = null;
	static{
		chooser = new JFileChooser();
		chooser.setFileFilter(new FileFilter(){

			@Override
			public boolean accept(File f){
				return f.isDirectory() || f.getName().endsWith(".xls");
			}

			@Override
			public String getDescription(){
				return "XLS files";
			}
		});
	}

	private SchemaAdminController controller = null;

	public XLSSchemaImporter(SchemaAdminController controller){
		this.controller = controller;
	}

	@Override
	public boolean doImport(){
		SchemaAdminView view = controller.getView();
		SchemaAdminModel model = controller.getModel();

		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return false;
		}

		int result = chooser.showDialog(controller.getView(), Translation.get(TextID.Open));
		File file = null;
		if (result == JFileChooser.APPROVE_OPTION){
			file = chooser.getSelectedFile();
		}
		if (file == null){
			return false;
		}

		SchemaAdminService schemaAdminService = ACSpringFactory.getInstance().getSchemaAdminService();
		boolean ok = true;
		try{
			InputStream is = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int available = bis.available();
			while (available > 0){
				byte[] buf = new byte[available];
				int count = bis.read(buf);
				baos.write(buf, 0, count);
				available = bis.available();
			}
            bis.close();
			baos.flush();
			baos.close();
			is.close();

			schemaAdminService.importSchemaFromXLS(baos.toByteArray(), getSchemaName(file), SessionData.getInstance()
			        .getUser());
		} catch (IOException ex){
			log.debug(ex.getMessage(), ex);
			ok = false;
		} catch (ACException ex){
			log.debug(ex.getMessage(), ex);
			view.renderErrors(new ServerErrorContainerWrapper(ex));
			ok = false;
		}

		if (ok){
			schemaAdminService.setAllInvalidationRequired(true, DataGroup.Attributes, DataGroup.Users, DataGroup.Schema);
			MainPanel2.setAllInvalidationNeeded(DataGroup.Attributes, DataGroup.Users, DataGroup.Schema);
		}
		return ok;
	}

	private String getSchemaName(File file){
		String fName = file.getName();
		fName = fName.substring(0, fName.length() - 4);
		return fName.replaceAll("[^a-zA-Z ]", "");
	}

}
