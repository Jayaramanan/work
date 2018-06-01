/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useractivity;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACOptionPane;
import com.ni3.ag.adminconsole.client.view.useractivity.UserActivityView;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.UserActivityModel;
import com.ni3.ag.adminconsole.shared.service.def.UserActivityService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.BasicErrorContainer;

public class MonitoringReportButtonListener extends ProgressActionListener{
	private static JFileChooser fileChooser = new JFileChooser();
	static{
		fileChooser.setDialogTitle(Translation.get(TextID.SelectFolderForExportFile));
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
	}
	private UserActivityController controller;
	private SimpleDateFormat fileDateFormat;

	public MonitoringReportButtonListener(UserActivityController controller){
		super(controller);
		this.controller = controller;
		fileDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
	}

	@Override
	public void performAction(ActionEvent e){
		UserActivityController controller = (UserActivityController) getController();
		UserActivityModel model = controller.getModel();
		UserActivityView view = controller.getView();
		view.clearErrors();
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}

		Date from = view.getDateFrom();
		Date to = view.getDateTo();
		Object mode = view.getCurrentFilterMode();
		Object filter = view.getCurrentFilter();
		Language language = SessionData.getInstance().getUserLanguage();

		String command = e.getActionCommand();
		getReport(command, from, to, mode, filter, language);

	}

	private boolean getReport(String type, Date from, Date to, Object mode, Object filter, Language language){
		try{
			File file = requestFolderToSave(type, mode);
			if (file == null)
				return true;
			UserActivityService service = ACSpringFactory.getInstance().getUserActivityService();

			byte[] exportData = null;

			if (TextID.XLSExport.toString().equals(type)){
				exportData = service.getXLSReport(from, to, mode, filter, language);
			} else if (TextID.PDFExport.toString().equals(type)){
				exportData = service.getPDFReport(from, to, mode, filter, language);
			} else if (TextID.HTMLExport.toString().equals(type)){
				exportData = service.getHTMLReport(from, to, mode, filter, language);
			} else{
				return true;
			}

			FileOutputStream fs = new FileOutputStream(file);
			fs.write(exportData);
			fs.close();
		} catch (ACException ex){
			controller.getView().renderErrors(ex.getErrors());
			return false;
		} catch (IOException ex){
			ErrorContainer ec = new BasicErrorContainer(new ErrorEntry(TextID.MsgFailedToCreateOrSaveFile));
			controller.getView().renderErrors(ec.getErrors());
			return false;
		}
		return true;
	}

	private File requestFolderToSave(final String type, Object mode) throws IOException{
		final String fileType = getFileType(type);
		final String resolution = getResolution(type);
		final String modeStr = TextID.UserBased.equals(mode) ? "u" : "a";
		String now = fileDateFormat.format(new Date());
		File sFile = new File("Monitoring-" + now + "-" + modeStr + resolution);
		fileChooser.setSelectedFile(sFile);
		fileChooser.resetChoosableFileFilters();
		fileChooser.setFileFilter(new FileFilter(){
			@Override
			public boolean accept(File f){
				return f.isDirectory() || f.getName().toLowerCase().endsWith(resolution);
			}

			@Override
			public String getDescription(){
				return fileType;
			}
		});

		int result = fileChooser.showDialog(controller.getView(), Translation.get(TextID.Save));
		File f = null;
		if (result == JFileChooser.APPROVE_OPTION){
			f = fileChooser.getSelectedFile();
			if (f.exists()){
				int response = ACOptionPane.showConfirmDialog(null, Translation.get(TextID.QuestionOverwriteExistingFile),
				        Translation.get(TextID.ConfirmOverwrite));
				if (response == ACOptionPane.NO_OPTION)
					return null;
			}
			if (!f.getName().toLowerCase().endsWith(resolution))
				f = new File(f.getAbsolutePath() + resolution);
			f.createNewFile();
		}

		return f;
	}

	private String getResolution(String type){
		String resolution = "";
		if (TextID.XLSExport.toString().equals(type)){
			resolution = ".xls";
		} else if (TextID.PDFExport.toString().equals(type)){
			resolution = ".pdf";
		} else if (TextID.HTMLExport.toString().equals(type)){
			resolution = ".html";
		}
		return resolution;
	}

	private String getFileType(String type){
		String ft = "";
		if (TextID.XLSExport.toString().equals(type)){
			ft = Translation.get(TextID.MicrosoftExcelFiles);
		} else if (TextID.PDFExport.toString().equals(type)){
			ft = Translation.get(TextID.PDFFiles);
		} else if (TextID.HTMLExport.toString().equals(type)){
			ft = Translation.get(TextID.HTMLFiles);
		}
		return ft;
	}
}
