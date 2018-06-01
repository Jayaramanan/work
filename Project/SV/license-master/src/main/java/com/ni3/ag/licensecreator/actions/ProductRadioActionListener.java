package com.ni3.ag.licensecreator.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import com.ni3.ag.licensecreator.gui.LicenseCreator;
import com.ni3.ag.licensecreator.model.PropertyTableModel;

@SuppressWarnings("serial")
public class ProductRadioActionListener extends AbstractAction{
	private LicenseCreator licenseCreator;

	public ProductRadioActionListener(LicenseCreator licenseCreator){
		this.licenseCreator = licenseCreator;
	}

	@Override
	public void actionPerformed(ActionEvent e){
		licenseCreator.getPropertyTable().setModel(new PropertyTableModel(licenseCreator.getProduct()));
		licenseCreator.updateComponentAvailability();
	}

}
