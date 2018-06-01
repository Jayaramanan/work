package com.ni3.ag.navigator.client.gui.common;

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

public class Ni3FileChooser extends JFileChooser{
	private static final long serialVersionUID = -3157707374684008581L;
	private String title;
	private JDialog dialog;
	private int returnValue;

	public Ni3FileChooser(String title){
		this.title = title;
	}

	@Override
	protected JDialog createDialog(final Component parent) throws HeadlessException{
		dialog = super.createDialog(parent);

		return dialog;
	}

	@Override
	public int showDialog(Component parent, String approveButtonText) throws HeadlessException{
		if (approveButtonText != null){
			setApproveButtonText(approveButtonText);
			setDialogType(CUSTOM_DIALOG);
		}
		dialog = createDialog(parent);
		dialog.setTitle(title);
		dialog.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				returnValue = CANCEL_OPTION;
			}
		});
		returnValue = ERROR_OPTION;
		rescanCurrentDirectory();

		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
		dialog.setAlwaysOnTop(false);

		firePropertyChange("JFileChooserDialogIsClosingProperty", dialog, null);
		dialog.removeAll();
		dialog.dispose();
		dialog = null;
		return returnValue;
	}

	@Override
	public void approveSelection(){
		returnValue = APPROVE_OPTION;
		if (dialog != null){
			dialog.setVisible(false);
		}
		fireActionPerformed(APPROVE_SELECTION);
	}

	@Override
	public void cancelSelection(){
		returnValue = CANCEL_OPTION;
		if (dialog != null){
			dialog.setVisible(false);
		}
		fireActionPerformed(CANCEL_SELECTION);
	}
}