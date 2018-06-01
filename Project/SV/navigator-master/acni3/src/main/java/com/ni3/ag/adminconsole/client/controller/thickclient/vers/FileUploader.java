/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.vers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ProgressMonitor;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACOptionPane;
import com.ni3.ag.adminconsole.client.view.thickclient.vers.VersioningView;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class FileUploader implements PropertyChangeListener{
	private static final Logger log = Logger.getLogger(FileUploader.class);
	private VersioningController controller;
	private ProgressMonitor progressMonitor;
	private UploadTask task;

	public FileUploader(AbstractController controller){
		this.controller = (VersioningController) controller;
	}

	public void uploadFile(File file, String servletUrl) throws IOException{

		String message = Translation.get(TextID.UploadingFile, new String[] { file.getName() });
		progressMonitor = new ProgressMonitor(controller.getView(), message, "", 0, 100);

		progressMonitor.setProgress(0);
		task = new UploadTask(servletUrl, file);
		if (task.fileExists()){
			int response = ACOptionPane.showConfirmDialog(null, Translation.get(TextID.QuestionOverwriteExistingFile),
			        Translation.get(TextID.ConfirmOverwrite));
			if (response == ACOptionPane.NO_OPTION)
				return;
		}
		task.addPropertyChangeListener(this);
		task.execute();

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt){
		if ("progress".equals(evt.getPropertyName())){
			int progress = (Integer) evt.getNewValue();
			progressMonitor.setProgress(progress);
			String note = Translation.get(TextID.CompletedPercent, new String[] { String.valueOf(progress) });
			progressMonitor.setNote(note);
			if (progressMonitor.isCanceled()){
				try{
					task.cancelTask(true);
				} catch (IOException e){
					log.error(e.getMessage(), e);
				}
			}
		} else if ("error".equals(evt.getPropertyName())){
			try{
				task.cancelTask(true);
			} catch (IOException e){
				log.error(e.getMessage(), e);
			}
			VersioningView view = (VersioningView) controller.getView();
			String errValue = String.valueOf(evt.getNewValue());
			log.error("can't upload module: " + errValue);
			List<ErrorEntry> errors = new ArrayList<ErrorEntry>();
			errors.add(new ErrorEntry(TextID.MsgCantUploadFile));
			view.renderErrors(errors);
		}
	}
}
