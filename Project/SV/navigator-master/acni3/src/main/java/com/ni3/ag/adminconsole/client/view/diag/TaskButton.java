/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.diag;

import javax.swing.JButton;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class TaskButton extends JButton{
	private static final long serialVersionUID = -979750306432533251L;
	private DiagnoseTaskStatus status;
	private int index;

	public TaskButton(int i, DiagnoseTaskStatus status){
		super(Translation.get(TextID.Fix));
		setEnabled(status != DiagnoseTaskStatus.Ok);
		this.status = status;
		index = i;
	}

	public DiagnoseTaskStatus getStatus(){
		return status;
	}

	public int getIndex(){
		return index;
	}

	public void setStatus(DiagnoseTaskStatus status){
		this.status = status;
		setEnabled(status != DiagnoseTaskStatus.Ok);
	}

	@Override
	public boolean isEnabled(){
		return !DiagnoseTaskStatus.Ok.equals(status);
	}
}
