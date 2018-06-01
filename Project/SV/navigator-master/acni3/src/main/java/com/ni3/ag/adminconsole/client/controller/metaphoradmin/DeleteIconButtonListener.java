/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.metaphoradmin;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.LinkedList;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.metaphoradmin.DeleteIconDialog;
import com.ni3.ag.adminconsole.client.view.metaphoradmin.NodeMetaphorView;
import com.ni3.ag.adminconsole.domain.Icon;
import com.ni3.ag.adminconsole.domain.Metaphor;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.NodeMetaphorModel;
import com.ni3.ag.adminconsole.shared.service.def.NodeMetaphorService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import com.ni3.ag.adminconsole.validation.rules.ServerErrorContainerWrapper;

public class DeleteIconButtonListener extends ProgressActionListener{

	private ACValidationRule metaphorDataChangeRule;

	public DeleteIconButtonListener(NodeMetaphorController controller){
		super(controller);
		metaphorDataChangeRule = (ACValidationRule) ACSpringFactory.getInstance()
		        .getBean("metaphorDataChangeValidationRule");
	}

	@Override
	public void performAction(ActionEvent e){
		NodeMetaphorController controller = (NodeMetaphorController) getController();
		NodeMetaphorView view = controller.getView();
		NodeMetaphorModel model = controller.getModel();
		view.getRightPanel().stopCellEditing();
		view.clearErrors();

		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}

		if (metaphorDataChangeRule.performCheck(model)){
			view.renderErrors(metaphorDataChangeRule.getErrorEntries());
			return;
		}

		List<Icon> icons = model.getIcons();
		DeleteIconDialog dlg = new DeleteIconDialog(icons);
		dlg.setVisible(true);
		if (dlg.isOkPressed()){
            final String docrootPath = dbInstance.getDocrootPath();
            if (docrootPath == null || docrootPath.isEmpty()){
                view.renderErrors(new ACException(TextID.MsgDocrootUndefined));
                return;
            }

			List<Icon> iconsToDelete = dlg.getIconsToDelete();

            final List<Metaphor> metaphorsWithDeletedIcons = getMetaphorsThatUseIconsFromModel(model, iconsToDelete);
            if (metaphorsWithDeletedIcons.size() > 0){
                renderIconsInUseError(view, metaphorsWithDeletedIcons);
                return;
            }
			if (iconsToDelete != null && iconsToDelete.size() > 0){
				NodeMetaphorService service = ACSpringFactory.getInstance().getNodeMetaphorService();
				try{
					service.deleteIcons(iconsToDelete);
				} catch (ACException e1){
					view.renderErrors(new ServerErrorContainerWrapper(e1));
					return;
				}
				icons.removeAll(iconsToDelete);
				view.getRightPanel().setIconReferenceData(icons);
			}
		}
	}

    private static void renderIconsInUseError(NodeMetaphorView view, List<Metaphor> metaphorsInUse) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < metaphorsInUse.size(); i++){
            if (i > 0){
                sb.append(", ");
            }
            sb.append(metaphorsInUse.get(i).getIcon().getIconName());
        }
        view.renderErrors(new ACException(TextID.MsgMetaphorIconsAreInUse, new String[]{sb.toString()}));
    }

    protected static List<Metaphor> getMetaphorsThatUseIconsFromModel(NodeMetaphorModel nmModel, List<Icon> icons) {
        final LinkedList<Metaphor> metaphors = new LinkedList<Metaphor>();
        final List<Metaphor> currentMetaphors = nmModel.getCurrentMetaphors();
        for (Metaphor metaphor : currentMetaphors) {
            for (Icon icon : icons) {
                if (metaphor.getIconName().equals(icon.getIconName())) {
                    metaphors.add(metaphor);
                    break;
                }
            }
        }
        return metaphors;
	}

	public ACValidationRule getMetaphorDataChangeRule(){
		return metaphorDataChangeRule;
	}

	public void setMetaphorDataChangeRule(ACValidationRule metaphorDataChangeRule){
		this.metaphorDataChangeRule = metaphorDataChangeRule;
	}

}
