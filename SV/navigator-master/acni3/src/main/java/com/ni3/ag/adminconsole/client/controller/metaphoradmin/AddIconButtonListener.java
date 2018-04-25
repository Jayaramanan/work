/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.metaphoradmin;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACImageFileChooser;
import com.ni3.ag.adminconsole.client.view.metaphoradmin.MetaphorImageFileChooser;
import com.ni3.ag.adminconsole.client.view.metaphoradmin.NodeMetaphorView;
import com.ni3.ag.adminconsole.domain.Icon;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.NodeMetaphorModel;
import com.ni3.ag.adminconsole.shared.service.def.NodeMetaphorService;
import com.ni3.ag.adminconsole.util.ValidationUtils;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ErrorContainerImpl;

public class AddIconButtonListener extends ProgressActionListener{

	private final static Logger log = Logger.getLogger(AddIconButtonListener.class);

	public AddIconButtonListener(NodeMetaphorController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		NodeMetaphorController controller = (NodeMetaphorController) getController();
		NodeMetaphorView view = controller.getView();
		view.getRightPanel().stopCellEditing();
		view.clearErrors();

		NodeMetaphorModel model = controller.getModel();
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}

		MetaphorImageFileChooser chooser = new MetaphorImageFileChooser();
		int returnVal = chooser.showDialog(view, Translation.get(TextID.Open));

		if (returnVal == ACImageFileChooser.APPROVE_OPTION){

			File[] files = chooser.getSelectedFiles();
			ErrorContainerImpl errors = new ErrorContainerImpl();
			for (File file : files){
				addIconFromFileToModel(file, errors, chooser.isUploadToDocroot());
			}

			if (!errors.getErrors().isEmpty()){
				view.renderErrors(errors);
			}

			view.getRightPanel().setIconReferenceData(model.getIcons());
		}
	}

	private void addIconFromFileToModel(File file, ErrorContainerImpl errors, boolean addIconToDocroot){
		if (!validateIconFile(file, errors)){
			return;
		}
		NodeMetaphorModel model = ((NodeMetaphorController) getController()).getModel();
		try{
			Icon icon = addNewIcon(file, addIconToDocroot);
			model.getIcons().add(icon);
		} catch (ACException ex){
			log.error("", ex);
			errors.addAllErrors(ex.getErrors());
		}
	}

	private boolean validateIconFile(File file, ErrorContainerImpl errors){
		boolean valid = true;
		if (!ValidationUtils.isIconNameValid(file.getName())){
			errors.addError(TextID.MsgProhibitedSymbolsInIconName);
			valid = false;
		}
		if (!file.exists()){
			errors.addError(TextID.MsgCannotAddNewIcon, new String[] { file.getName() });
			valid = false;
		}
		return valid;
	}

	public Icon addNewIcon(File file, boolean uploadToDocroot) throws ACException{
		DatabaseInstance di = SessionData.getInstance().getCurrentDatabaseInstance();
		String path = di.getDocrootPath();
		if (path == null || path.isEmpty()){
			throw new ACException(TextID.MsgDocrootUndefined);
		}

		Icon icon = new Icon();
		icon.setIconName(file.getName());
		try{
			icon.setIcon(getBytesFromFile(file));
		} catch (IOException e){
			throw new ACException(TextID.MsgCannotAddNewIcon, new String[] { file.getName() });
		}
		NodeMetaphorService service = ACSpringFactory.getInstance().getNodeMetaphorService();
		Integer id = service.addNewIcon(icon, uploadToDocroot);
		icon.setId(id);
		return icon;
	}

	public byte[] getBytesFromFile(File file) throws IOException{
		InputStream is = new FileInputStream(file);

		byte[] bytes = new byte[(int) file.length()];

		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0){
			offset += numRead;
		}

		is.close();
		return bytes;
	}
}
