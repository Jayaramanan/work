/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.metaphoradmin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.TableColumn;

import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.Mnemonic;
import com.ni3.ag.adminconsole.domain.Icon;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class DeleteIconDialog extends JDialog implements ActionListener{

	private static final long serialVersionUID = 3864844263486827445L;
	private ACButton okButton;
	private ACButton cancelButton;

	private List<Icon> icons;
	private boolean okPressed = false;
	DeleteIconTableModel model;

	public DeleteIconDialog(List<Icon> icons){
		this.icons = icons;
		setTitle(Translation.get(TextID.DeleteIcon));
		initComponents();
		setLocation((int) (ACMain.getScreenWidth() / 2) - getWidth() / 2, (int) (ACMain.getScreenHeight() / 2) - getHeight() / 2);
		setIconImage(new ImageIcon(getClass().getResource("/images/Ni3.png")).getImage());
	}

	private void initComponents(){
		setModal(true);
		setSize(new Dimension(400, 600));

		JPanel mainPanel = new JPanel();
		SpringLayout layout = new SpringLayout();
		mainPanel.setLayout(layout);
		getContentPane().add(mainPanel);

		JTable table = new JTable();
		Collections.sort(icons, new Comparator<Icon>(){
			@Override
			public int compare(Icon o1, Icon o2){
				if (o1.getIconName() == null || o2.getIconName() == null)
					return 0;
				return o2.getIconName().compareTo(o2.getIconName());
			}
		});
		model = new DeleteIconTableModel(icons);
		table.setModel(model);

		JScrollPane sp = new JScrollPane();
		sp.setViewportView(table);

		mainPanel.add(sp);
		layout.putConstraint(SpringLayout.NORTH, sp, 0, SpringLayout.NORTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, sp, 0, SpringLayout.EAST, mainPanel);
		layout.putConstraint(SpringLayout.WEST, sp, 0, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.SOUTH, sp, -40, SpringLayout.SOUTH, mainPanel);

		okButton = new ACButton(Mnemonic.AltO, TextID.Ok);
		okButton.setSize(70, 23);
		okButton.setPreferredSize(new Dimension(70, 23));
		cancelButton = new ACButton(Mnemonic.AltC, TextID.Cancel);
		cancelButton.setSize(70, 23);
		cancelButton.setPreferredSize(new Dimension(70, 23));

		layout.putConstraint(SpringLayout.NORTH, cancelButton, -33, SpringLayout.SOUTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, cancelButton, -10, SpringLayout.EAST, mainPanel);
		mainPanel.add(cancelButton);
		cancelButton.addActionListener(this);

		layout.putConstraint(SpringLayout.NORTH, okButton, -33, SpringLayout.SOUTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, okButton, -10, SpringLayout.WEST, cancelButton);
		mainPanel.add(okButton);
		okButton.addActionListener(this);

		table.setDefaultRenderer(Icon.class, new IconTableCellRenderer());
		TableColumn column = table.getColumnModel().getColumn(0);
		column.setPreferredWidth(30);
		column.setMinWidth(30);
		column.setMaxWidth(30);
	}

	@Override
	public void actionPerformed(ActionEvent e){
		if (e.getSource() == okButton){
			okPressed = true;
		} else{
			okPressed = false;
		}
		setVisible(false);
	}

	public boolean isOkPressed(){
		return okPressed;
	}

	public List<Icon> getIconsToDelete(){
		if (!okPressed){
			return null;
		}
		return model.getSelectedIcons();
	}

}
