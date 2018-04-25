/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import java.awt.Component;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTabbedPane;
import com.ni3.ag.adminconsole.client.view.connection.ObjectConnectionView;
import com.ni3.ag.adminconsole.shared.language.TextID;

/**
 * 
 * @author user
 */
public class ApplicationConfigurationTabbedPane extends ACTabbedPane{

	private static final long serialVersionUID = 1L;

	public ApplicationConfigurationTabbedPane(){
	}

	public void setAttributeEditView(AttributeEditView view){
		addTab(Translation.get(TextID.Attributes), view);
	}

	public void setObjectConnectionView(ObjectConnectionView objConnView){
		addTab(Translation.get(TextID.Connections), objConnView);
	}

	public void setPredefinedAttributeEditView(Component view){
		addTab(Translation.get(TextID.AttributeValues), view);
	}

	public void setFormatAttributesView(FormatAttributesView view){
		addTab(Translation.get(TextID.Format), view);
	}

}
