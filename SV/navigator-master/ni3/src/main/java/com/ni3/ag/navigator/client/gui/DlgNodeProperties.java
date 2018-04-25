/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.awt.*;
import javax.swing.*;

import com.ni3.ag.navigator.client.domain.*;
import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;
import com.ni3.ag.navigator.client.domain.Context;
import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.util.SpringUtilities;

@SuppressWarnings("serial")
public class DlgNodeProperties extends Ni3Dialog{
	private JComboBox Type;

	private JTabbedPane tab;

	private DBObjectPanel DBPane[];
	private Entity Entities[];

	private DBObject obj;

	public static final int RET_CANCEL = 0;
	public static final int RET_OK = 1;
	private int returnStatus = RET_CANCEL;

	private int increment = 1;

	public int Operation;
	private final boolean locked;
	private final MainPanel parentMP;
	private final DlgNodePropertiesAction action;

	/**
	 * readOnly flag - this will display all editable attributes as read-only if true
	 * Needed for "Details" menu
	 **/
	public DlgNodeProperties(final MainPanel xparentMP, final DBObject inObj, final boolean locked, DlgNodePropertiesAction action){
		super();
		parentMP = xparentMP;
		obj = inObj;
		this.locked = locked;
		this.action = action;

		setSize(500, 300);

		initComponents(action);
		setLocationRelativeTo(xparentMP);
	}

	public DlgNodeProperties(final MainPanel xparentMP, final DBObject inObj, final boolean locked){
		this(xparentMP, inObj, locked, DlgNodePropertiesAction.EDIT);
	}

	private void cancelButtonActionPerformed(final java.awt.event.ActionEvent evt){
		doClose(RET_CANCEL);
	}

	/** Closes the dialog */
	@SuppressWarnings("unused")
	private void closeDialog(final java.awt.event.WindowEvent evt){
		doClose(RET_CANCEL);
	}

	private void doClose(final int retStatus){
		returnStatus = retStatus;
		setVisible(false);
		dispose();
	}

	/** @return the return status of this dialog - one of RET_OK or RET_CANCEL */
	public int getReturnStatus(){
		return returnStatus;
	}

	protected void initComponents(){
		initComponents(DlgNodePropertiesAction.DETAILS.EDIT);
	}

	private void initComponents(DlgNodePropertiesAction action){
		if (obj == null){
			setTitle(UserSettings.getWord("Create node"));
			Operation = 1;
		} else{
			getFontMetrics(getFont());
			setTitle(UserSettings.getWord("Edit node"));
			Operation = 2;
		}
		if (DlgNodePropertiesAction.DETAILS == action){
			setTitle("Details");
		} else if (DlgNodePropertiesAction.REPLICATE == action){
			setTitle("Replicate");
		}

		final JPanel split = new JPanel();
		split.setLayout(new BorderLayout());
		split.setPreferredSize(new Dimension(200, 850));

		getContentPane().add(split);
		if (obj == null){
			final JPanel panel1 = new JPanel();
			panel1.setPreferredSize(new Dimension(200, 40));
			panel1.setMinimumSize(new Dimension(200, 40));
			panel1.setLayout(new SpringLayout());
			split.add(panel1, BorderLayout.NORTH);

			final DefaultComboBoxModel CBModel = new DefaultComboBoxModel();

			Entities = new Entity[parentMP.Doc.DB.schema.definitions.size()];

			int n = 0;
			for (final Entity e : parentMP.Doc.DB.schema.definitions){
				if ((e.isNode()) && ((Operation == 1 && e.CanCreate) || (Operation == 2 && e.CanUpdate))){
					CBModel.addElement(e.Name);
					Entities[n] = e;
					n++;
				}
			}

			Type = new javax.swing.JComboBox();
            Type.setName("NodeType");
			Type.setModel(CBModel);
			if (n > 0){
				Type.setSelectedIndex(0);
			}
			Type.setPreferredSize(new Dimension(100, 25));
			Type.setMinimumSize(new Dimension(100, 25));
			panel1.add(new JLabel(UserSettings.getWord("Node type")));
			panel1.add(Type);

			Type.addActionListener(new java.awt.event.ActionListener(){
				@Override
				public void actionPerformed(final java.awt.event.ActionEvent evt){
					final int index = Type.getSelectedIndex();

					if (index > -1){
						DBPane[0].MakeAttributesPanel(Entities[index], Entities[index].getReadableAttributes(false), null, false,
						        Operation, DBPane[0].locked, getFontMetrics(getFont()), getGraphics());

						if (parentMP.Doc.getTopicID() != 0 && !Entities[index].context.isEmpty()){
							final Context c = Entities[index].context.get(0);
							DBPane[1].MakeAttributesPanel(Entities[index], c.getAttributes(), null, false, Operation, false,
							        getFontMetrics(getFont()), getGraphics());
						}

						final Dimension d = getSize();
						d.width += increment;
						increment *= -1;
						setSize(d);
						doLayout();
						repaint();
					}
				}
			});

			SpringUtilities.makeCompactGrid(panel1, 1, 2, // rows, cols
			        6, 6, // initX, initY
			        6, 6); // xPad, yPad
		}

		tab = new JTabbedPane();
		DBPane = new DBObjectPanel[2];

		DBPane[0] = new DBObjectPanel(obj, parentMP.Doc, false);
		DBPane[1] = new DBObjectPanel(obj, parentMP.Doc, false);
		tab.insertTab("General", null, DBPane[0], "General data", 0);

		split.add(tab, BorderLayout.CENTER);
		DBPane[0].setPreferredSize(new Dimension(50, 50));
		DBPane[1].setPreferredSize(new Dimension(50, 50));

		if (obj == null){
			if (Entities[0] != null){
				DBPane[0].MakeAttributesPanel(Entities[0], Entities[0].getReadableAttributes(false),
				        null, false, Operation, false, getFontMetrics(getFont()), getGraphics());

				if (parentMP.Doc.getTopicID() != 0 && !Entities[0].context.isEmpty()){
					final Context c = Entities[0].context.get(0);
					tab.insertTab(UserSettings.getWord(c.name), null, DBPane[1], UserSettings.getWord(c.name), 1);
					DBPane[1].MakeAttributesPanel(Entities[0], c.getAttributes(), null, false, Operation, false,
					        getFontMetrics(getFont()), getGraphics());
				}
			}
		} else{
			DBPane[0].MakeAttributesPanel(obj.getEntity(), obj.getEntity().getReadableAttributes(false), null,
			        false, Operation, locked, getFontMetrics(getFont()), getGraphics());

			if (parentMP.Doc.getTopicID() != 0 && !obj.getEntity().context.isEmpty()){
				final Context c = obj.getEntity().context.get(0);
				tab.insertTab(UserSettings.getWord(c.name), null, DBPane[1], UserSettings.getWord(c.name), 1);
				DBPane[1].MakeAttributesPanel(obj.getEntity(), c.getAttributes(), null, false, Operation, false,
				        getFontMetrics(getFont()), getGraphics());
			}
		}

		final JPanel okcancel = new JPanel();

		if (!(DlgNodePropertiesAction.DETAILS == action)) {
			JButton btn = new JButton(UserSettings.getWord("OK"));
			okcancel.add(btn);

			btn.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(final java.awt.event.ActionEvent evt) {
					okButtonActionPerformed();
				}
			});
		}

		JButton btn = new JButton(UserSettings.getWord("Cancel"));
		okcancel.add(btn);
		btn.addActionListener(new java.awt.event.ActionListener(){
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt){
				cancelButtonActionPerformed(evt);
			}
		});

		if (!(DlgNodePropertiesAction.DETAILS == action)) {
			btn = new JButton(UserSettings.getWord("Clear"));
			okcancel.add(btn);
			btn.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(final java.awt.event.ActionEvent evt) {
					DBPane[0].clearPanel();
					DBPane[1].clearPanel();
				}
			});
		}

		split.add(okcancel, BorderLayout.SOUTH);
	}

	private void okButtonActionPerformed(){
		onEnterAction();
	}

	protected void onEnterAction(){
		if (!DBPane[0].validateForm(action)){
			return;
		}

		if (DlgNodePropertiesAction.REPLICATE == action){
			obj = DBPane[0].fillObj(true);
		} else {
			obj = DBPane[0].fillObj(false);
		}

		parentMP.commandPanel.filtersPanel.repaint();

		if (parentMP.Doc.getTopicID() != 0 && !obj.getEntity().context.isEmpty()){
			DBPane[1].obj = obj;
			obj = DBPane[1].fillObj(false);
		}
		doClose(RET_OK);
	}

	@Override
	public void setVisible(final boolean b){
		if (b){
			returnStatus = RET_CANCEL;
		}
		super.setVisible(b);
	}

	public DBObject getObject(){
		return obj;
	}
}
