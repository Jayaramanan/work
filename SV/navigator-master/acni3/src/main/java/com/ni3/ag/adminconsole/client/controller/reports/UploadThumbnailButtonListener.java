/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.reports;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACImageFileChooser;
import com.ni3.ag.adminconsole.client.view.reports.ReportsView;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.ReportsModel;

public class UploadThumbnailButtonListener extends ProgressActionListener{

	private final static Logger log = Logger.getLogger(UploadThumbnailButtonListener.class);

	public UploadThumbnailButtonListener(ReportsController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		ReportsController controller = (ReportsController) getController();
		ReportsView view = controller.getView();
		view.clearErrors();
		ReportsModel model = controller.getModel();
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();

		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}
		if (model.getCurrentReport() == null)
			return;

		ACImageFileChooser chooser = new ACImageFileChooser();
		chooser.setMultiSelectionEnabled(false);
		int returnVal = chooser.showDialog(view, Translation.get(TextID.Open));

		if (returnVal == ACImageFileChooser.APPROVE_OPTION){

			File file = chooser.getSelectedFile();
			byte[] icon = null;
			List<ErrorEntry> errors = new ArrayList<ErrorEntry>();

			if (!file.exists() || (icon = getBytesFromFile(file)) == null){
				errors.add(new ErrorEntry(TextID.MsgCannotAddNewIcon, new String[] { file.getName() }));
			}

			if (!errors.isEmpty()){
				view.renderErrors(errors);
			}

			view.setIcon(icon);
		}
	}

	public byte[] getBytesFromFile(File file){
		byte[] bytes = null;
		try{
			InputStream is = new FileInputStream(file);

			bytes = new byte[(int) file.length()];

			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0){
				offset += numRead;
			}

			is.close();
		} catch (IOException e){
			log.debug(e.getMessage(), e);
			return null;
		}
		return bytes;
	}

}
