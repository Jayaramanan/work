/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.vers;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ArchiveFilter;
import com.ni3.ag.adminconsole.client.view.thickclient.vers.VersioningView;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.VersioningModel;

public class UploadModuleButtonListener extends ProgressActionListener{

	private final static Logger log = Logger.getLogger(UploadModuleButtonListener.class);
	private static String previousFolder;

	public UploadModuleButtonListener(VersioningController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		VersioningController controller = (VersioningController) getController();
		VersioningView view = controller.getView();
		view.stopEditing();
		view.clearErrors();

		VersioningModel model = controller.getModel();
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected() || !controller.checkModulesPath()){
			return;
		}

		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.addChoosableFileFilter(new ArchiveFilter());
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setMultiSelectionEnabled(true);

		if (previousFolder != null)
			chooser.setCurrentDirectory(new File(previousFolder));
		int returnVal = chooser.showDialog(view, Translation.get(TextID.Open));

		if (returnVal == JFileChooser.APPROVE_OPTION){
			File[] files = chooser.getSelectedFiles();
			approvePressed(files);
			controller.reloadPaths();
			controller.updateViewPaths();

			try{
				previousFolder = files[0].getParentFile().getCanonicalPath();
			} catch (IOException ex){
				log.error("", ex);
				previousFolder = null;
			}
		}
	}

	private void approvePressed(File[] files){
		String servletUrl = ((VersioningController) getController()).getModulesTransferServletUrl();

		List<ErrorEntry> errors = new ArrayList<ErrorEntry>();

		for (File file : files){
			if (!file.exists())
				errors.add(new ErrorEntry(TextID.MsgFileNotFound, new String[] { file.getName() }));
			try{
				FileUploader uploader = new FileUploader(getController());
				uploader.uploadFile(file, servletUrl);
			} catch (IOException e){
				log.error("can't read/upload module", e);
				errors.add(new ErrorEntry(TextID.MsgCantUploadFile, new String[] { e.getMessage() }));
			}
		}

		((VersioningView) getController().getView()).renderErrors(errors);

	}

}
