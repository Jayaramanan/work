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
import com.ni3.ag.adminconsole.client.view.common.ACOptionPane;
import com.ni3.ag.adminconsole.client.view.common.ArchiveFilter;
import com.ni3.ag.adminconsole.client.view.thickclient.vers.VersioningView;
import com.ni3.ag.adminconsole.domain.Module;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class DownloadModuleButtonListener extends ProgressActionListener{

	private final static Logger log = Logger.getLogger(DownloadModuleButtonListener.class);

	public DownloadModuleButtonListener(VersioningController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		VersioningController controller = (VersioningController) getController();
		VersioningView view = (VersioningView) controller.getView();
		view.stopEditing();
		view.clearErrors();
		String servletUrl = controller.getModulesTransferServletUrl();
		Module m = view.getSelectedModule();
		if (m == null || !controller.checkModulesPath())
			return;

		if (m.getPath() == null)
			return;

		JFileChooser jfc = new JFileChooser();
		jfc.setFileFilter(new ArchiveFilter());
		jfc.setSelectedFile(new File(m.getPath()));
		int option = jfc.showDialog(view, Translation.get(TextID.Save));
		File fileToSave = jfc.getSelectedFile();
		if (option == JFileChooser.APPROVE_OPTION && fileToSave != null){
			approvePressed(m, fileToSave, servletUrl);
		}
	}

	private void approvePressed(Module m, File fileToSave, String servletUrl){
		VersioningView view = (VersioningView) getController().getView();
		if (fileToSave.exists()){
			int option = ACOptionPane.showConfirmDialog(view, Translation.get(TextID.QuestionOverwriteExistingFile),
			        Translation.get(TextID.ConfirmOverwrite));
			if (option != ACOptionPane.YES_OPTION)
				return;
		}
		try{
			if (!fileToSave.exists()){
				if (!fileToSave.getName().endsWith(ArchiveFilter.ZIP))
					fileToSave = new File(fileToSave.getAbsolutePath() + ArchiveFilter.ZIP);
				fileToSave.createNewFile();
			}
			FileDownloader downloader = new FileDownloader(getController());
			downloader.saveModuleToFile(m, servletUrl, fileToSave);
		} catch (IOException ex){
			log.error("can't download/save module", ex);
			List<ErrorEntry> errors = new ArrayList<ErrorEntry>();
			errors.add(new ErrorEntry(TextID.MsgCantSaveFile, new String[] { m.getPath() }));
			view.renderErrors(errors);
		}
	}

}
