/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.search;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;

import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;
import com.ni3.ag.navigator.client.model.Ni3Document;

@SuppressWarnings("serial")
public class DlgLinkedNodes extends Ni3Dialog{
	JPanel panel;

	public static final int RET_CANCEL = 0;
	public static final int RET_OK = 1;
	private int returnStatus;

	private JPanel mainPane;
	private JComboBox cb1;
	private JComboBox cb2;
	private JComboBox cb3;
	public Entity entity1;
	public Entity entity2;
	public Entity entity3;
	int mode;
	private Ni3Document doc;

	public DlgLinkedNodes(Ni3Document doc, int Mode){
		super();
		this.doc = doc;
		this.mode = Mode;

		setTitle(UserSettings.getWord("LinkedNodes"));

		returnStatus = RET_CANCEL;

		initComponents();
	}

	protected void initComponents(){
		panel = new JPanel();
		panel.setLayout(new BorderLayout());

		getContentPane().add(panel);

		mainPane = new JPanel();

		mainPane.setLayout(new FlowLayout());

		cb1 = cb2 = cb3 = null;
		int count1, count2;

		Object objNodes[], objConnections[];

		count1 = count2 = 0;
		for (Entity ent : doc.DB.schema.definitions){
			if (ent.CanRead){
				if (ent.isEdge())
					count2++;
				else
					count1++;
			}
		}

		objNodes = new Object[count1];
		objConnections = new Object[count2];

		count1 = count2 = 0;
		for (Entity ent : doc.DB.schema.definitions){
			if (ent.CanRead){
				if (ent.isEdge()){
					objConnections[count2] = ent;
					count2++;
				} else{
					objNodes[count1] = ent;
					count1++;
				}
			}
		}

		cb1 = new JComboBox(objNodes);
		cb1.setName("FromComboBox");
		cb1.setRenderer(new EntityListCellRenderer());
		mainPane.add(cb1);

		if (mode > 1){
			cb2 = new JComboBox(objConnections);
			cb2.setName("ConnectionComboBox");
			cb2.setRenderer(new EntityListCellRenderer());
			mainPane.add(cb2);
		}

		if (mode == 2){
			cb3 = new JComboBox(objNodes);
			cb3.setName("ToComboBox");
			cb3.setRenderer(new EntityListCellRenderer());
			mainPane.add(cb3);
		}

		panel.add(mainPane, BorderLayout.CENTER);

		JPanel okcancel = new JPanel();

		JButton btn = new JButton(UserSettings.getWord("OK"));
		okcancel.add(btn);

		btn.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				okButtonActionPerformed(evt);
			}
		});

		btn = new JButton(UserSettings.getWord("Cancel"));
		okcancel.add(btn);
		btn.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				cancelButtonActionPerformed(evt);
			}
		});

		panel.add(okcancel, BorderLayout.SOUTH);

		setSize(panel.getPreferredSize().width + 40, panel.getPreferredSize().height + 40);
		validate();
	}

	protected void onEnterAction(){
		if (cb1 != null)
			entity1 = (Entity) (cb1.getModel().getElementAt(cb1.getSelectedIndex()));

		if (cb2 != null)
			entity2 = (Entity) (cb2.getModel().getElementAt(cb2.getSelectedIndex()));

		if (cb3 != null)
			entity3 = (Entity) (cb3.getModel().getElementAt(cb3.getSelectedIndex()));

		doClose(RET_OK);
	}

	private void okButtonActionPerformed(java.awt.event.ActionEvent evt){
		onEnterAction();
	}

	/** @return the return status of this dialog - one of RET_OK or RET_CANCEL */
	public int getReturnStatus(){
		return returnStatus;
	}

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt){
		doClose(RET_CANCEL);
	}

	private void doClose(int retStatus){
		returnStatus = retStatus;
		setVisible(false);
		dispose();
	}

	@Override
	public void setVisible(boolean b){
		if (b)
			returnStatus = RET_CANCEL;
		super.setVisible(b);
	}

	private class EntityListCellRenderer extends DefaultListCellRenderer{

		private static final long serialVersionUID = -6377274426593581323L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus){
			Object display = value;
			if (value instanceof Entity){
				display = ((Entity) value).Name;
			}
			return super.getListCellRendererComponent(list, display, index, isSelected, cellHasFocus);
		}
	}
}
