package com.ni3.ag.licensecreator.actions;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import com.ni3.ag.licensecreator.gui.LicenseCreator;

public class AdmincountFieldFocusListener implements FocusListener{	
	private LicenseCreator licenseCreator;

	public AdmincountFieldFocusListener(LicenseCreator licenseCreator){
		this.licenseCreator = licenseCreator;
    }

	@Override
	public void focusGained(FocusEvent e){
	}

	@Override
	public void focusLost(FocusEvent e){
		String s = licenseCreator.getAdminCountText();
		if(s == null)
			s = "";
		s = s.trim();
		licenseCreator.setAdminCountText(s.isEmpty() ? "0" : s);
	}

}
