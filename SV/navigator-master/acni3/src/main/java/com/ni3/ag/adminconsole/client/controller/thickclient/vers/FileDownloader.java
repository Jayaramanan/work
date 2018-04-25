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
import com.ni3.ag.adminconsole.client.view.thickclient.vers.VersioningView;
import com.ni3.ag.adminconsole.domain.Module;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class FileDownloader implements PropertyChangeListener{
	private static final Logger log = Logger.getLogger(FileDownloader.class);
	private VersioningController controller;
	private ProgressMonitor progressMonitor;
	private DownloadTask task;

	public FileDownloader(AbstractController controller){
		this.controller = (VersioningController) controller;
	}

	public void saveModuleToFile(Module m, String servletUrl, File localFile) throws IOException{

		String message = Translation.get(TextID.DownloadingFile, new String[] { localFile.getName() });
		progressMonitor = new ProgressMonitor(controller.getView(), message, "", 0, 100);
		progressMonitor.setProgress(0);
		progressMonitor.setMillisToPopup(0);
		progressMonitor.setMillisToDecideToPopup(0);
		task = new DownloadTask(m, servletUrl, localFile);
		if (task.ping()){
			task.addPropertyChangeListener(this);
			task.execute();
		} else
			throw new IOException("destination unreachable");
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt){
		String prop = evt.getPropertyName();
		if ("progress".equals(prop)){
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
		} else if (prop != null && prop.startsWith("error")){
			try{
				task.cancelTask(true);
			} catch (IOException e){
				log.error(e.getMessage(), e);
			}
			VersioningView view = (VersioningView) controller.getView();
			Module m = view.getSelectedModule();
			String errValue = String.valueOf(evt.getNewValue());
			log.error("can't download/save module: " + errValue);
			List<ErrorEntry> errors = new ArrayList<ErrorEntry>();
			if ("error_download".equals(prop))
				errors.add(new ErrorEntry(TextID.MsgCantDownloadFile, new String[] { m.getPath() }));
			else
				errors.add(new ErrorEntry(TextID.MsgCantSaveFile, new String[] { m.getPath() }));
			view.renderErrors(errors);
		}
	}
}
