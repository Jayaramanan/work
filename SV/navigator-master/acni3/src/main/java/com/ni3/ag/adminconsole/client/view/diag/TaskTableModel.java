/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.diag;

import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;

public class TaskTableModel extends ACTableModel{
	private static final long serialVersionUID = 1L;

	public static final int DESCRIPTION_COLUMN_INDEX = 0;
	public static final int BUTTON_COLUMN_INDEX = 2;
	public static final int ACTION_DESCRIPTION_COLUMN_INDEX = 3;

	private List<DiagnoseTaskResult> data;
	private Map<DiagnoseTaskResult, TaskButton> buttons;
	private ActionListener fixButtonListener;

	public TaskTableModel(List<DiagnoseTaskResult> results, ActionListener fixButtonListener){
		addColumn(Translation.get(TextID.Name), false, String.class, false);
		addColumn(Translation.get(TextID.Status), false, DiagnoseTaskResult.class, false);
		addColumn(Translation.get(TextID.Action), true, TaskButton.class, false);
		addColumn(Translation.get(TextID.ActionDescription), false, String.class, false);
		buttons = new HashMap<DiagnoseTaskResult, TaskButton>();
		this.fixButtonListener = fixButtonListener;
		data = results;
	}

	@Override
	public int getRowCount(){
		return data.size();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex){
		if (columnIndex == BUTTON_COLUMN_INDEX){
			DiagnoseTaskResult dtr = data.get(rowIndex);
			return dtr.isFixable();
		}
		return super.isCellEditable(rowIndex, columnIndex);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex){
		DiagnoseTaskResult res = data.get(rowIndex);
		switch (columnIndex){
			case 0:
				return res.getDescription();
			case 1:
				return res;
			case BUTTON_COLUMN_INDEX: {
				if (!res.isFixable())
					return null;
				TaskButton tb = buttons.get(res);
				if (tb == null){
					Logger.getLogger(getClass()).fatal("TaskButton for fixable task == null");
					return tb;
				}
				tb.setStatus(res.getStatus());
				return tb;
			}
			case ACTION_DESCRIPTION_COLUMN_INDEX: {
				return res.getActionDescription();
			}
		}
		return null;
	}

	public void replaceResult(DiagnoseTaskResult result){
		for (int i = 0; i < data.size(); i++){
			DiagnoseTaskResult dtr = data.get(i);
			if (dtr.getTaskClass().equals(result.getTaskClass())){
				data.set(i, result);
				TaskButton tb = buttons.get(dtr);
				buttons.remove(dtr);
				if (result.isFixable()){
					if (tb == null){
						tb = new TaskButton(i, result.getStatus());
						tb.addActionListener(fixButtonListener);
					} else{
						tb.setStatus(result.getStatus());
					}
					buttons.put(result, tb);
				}
				break;
			}
		}
	}

}
