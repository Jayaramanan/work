package com.ni3.ag.navigator.client.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;
import com.ni3.ag.navigator.client.domain.Attribute.EDynamicAttributeScope;
import com.ni3.ag.navigator.client.gui.MainPanel;
import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.gui.customlayouts.GridLayout2;
import com.ni3.ag.navigator.shared.constants.DynamicAttributeOperation;

@SuppressWarnings("serial")
public class DlgDynamicAggregation extends Ni3Dialog implements ActionListener{

	private MainPanel parentMP;

	private JComboBox scope;

	private JComboBox fromEntity;
	private JComboBox fromAttribute;
	private JComboBox toEntity;
	private JComboBox function;

	private JButton btnok, btncancel;

	public static final int RET_CANCEL = 0;
	public static final int RET_OK = 1;
	private int returnStatus = RET_CANCEL;

	public Attribute newAttr;

	public DlgDynamicAggregation(MainPanel parentMP){
		super();
		this.parentMP = parentMP;

		setTitle(UserSettings.getWord("Dynamic aggregation"));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		newAttr = null;

		initControls();
	}

	private void initControls(){
		JPanel panel = new JPanel();

		JPanel okcancel = new JPanel();

		btnok = new JButton(UserSettings.getWord("OK"));
		okcancel.add(btnok);

		btnok.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				onEnterAction();
			}
		});

		getRootPane().setDefaultButton(btnok);

		btncancel = new JButton(UserSettings.getWord("Cancel"));
		okcancel.add(btncancel);

		ActionListener cancelAction = new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				cancelButtonActionPerformed(evt);
			}
		};

		btncancel.addActionListener(cancelAction);

		btncancel.registerKeyboardAction(cancelAction, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false),
				JComponent.WHEN_IN_FOCUSED_WINDOW);

		fromEntity = new JComboBox();
		fromEntity.setPreferredSize(new Dimension(150, 25));
		fromEntity.setRenderer(new EntityListCellRenderer());
		fillEntities(fromEntity);

		fromAttribute = new JComboBox();
		fromAttribute.setPreferredSize(new Dimension(150, 25));

		toEntity = new JComboBox();
		toEntity.setPreferredSize(new Dimension(150, 25));
		toEntity.setRenderer(new EntityListCellRenderer());
		fillNodes(toEntity);

		fillAttributes(parentMP.Doc.DB.schema.definitions.get(0), (Entity) (toEntity.getSelectedItem()), fromAttribute,
				EDynamicAttributeScope.Graph);

		String functions[] = { UserSettings.getWord(DynamicAttributeOperation.Sum.toString()),
				UserSettings.getWord(DynamicAttributeOperation.Avg.toString()),
				UserSettings.getWord(DynamicAttributeOperation.Min.toString()),
				UserSettings.getWord(DynamicAttributeOperation.Max.toString()) };
		function = new JComboBox(functions);
		function.setPreferredSize(new Dimension(150, 25));

		String scopes[] = { UserSettings.getWord("Graph"),/* UserSettings.getWord("Matrix"), */
		UserSettings.getWord("Database") };
		scope = new JComboBox(scopes);
		scope.setPreferredSize(new Dimension(150, 25));

		JLabel labelFrom = new JLabel(UserSettings.getWord("From"));
		JLabel labelTo = new JLabel(UserSettings.getWord("To"));
		JLabel labelFunction = new JLabel(UserSettings.getWord("Function"));

		this.setMinimumSize(new Dimension(350, 180));
		setBounds(100, 100, 350, 180);

		getContentPane().add(panel, BorderLayout.CENTER);

		panel.setLayout(new GridLayout2(4, 3, 10, 8));
		panel.setMinimumSize(new Dimension(10, 10));

		panel.add(new JLabel(UserSettings.getWord("Scope")));
		panel.add(scope);
		panel.add(new JLabel());

		panel.add(labelFrom);
		panel.add(fromEntity);
		panel.add(fromAttribute);

		panel.add(labelTo);
		panel.add(toEntity);
		panel.add(new JLabel());

		panel.add(labelFunction);
		panel.add(function);
		panel.add(new JLabel());

		add(new JLabel(" "), BorderLayout.NORTH);
		add(new JLabel(" "), BorderLayout.WEST);
		add(new JLabel(" "), BorderLayout.EAST);

		add(okcancel, BorderLayout.SOUTH);

		fromEntity.addActionListener(this);
		toEntity.addActionListener(this);
		scope.addActionListener(this);
	}

	private void fillEntities(JComboBox cb){
		for (Entity e : parentMP.Doc.DB.schema.definitions)
			if (e.CanRead)
				cb.addItem(e);
	}

	private void fillNodes(JComboBox cb){
		for (Entity e : parentMP.Doc.DB.schema.definitions)
			if (e.isNode() && e.CanRead)
				cb.addItem(e);
	}

	private void fillAttributes(Entity e, Entity target, JComboBox cb, EDynamicAttributeScope scope){
		cb.removeAllItems();

		for (Attribute a : e.getReadableAttributes())
			if (!a.isDynamic() && !a.multivalue && !a.predefined && a.isNumericAttribute() && !a.isSystemAttribute()
					&& a.isAggregable() && !a.inContext){
				cb.addItem(a);
			}

		for (Attribute a : target.getReadableAttributes())
			if (a.isDynamic() && a.getDynamicScope() == scope){
				cb.removeItem(a.getDynamicFromAttribute());
			}

		btnok.setEnabled(cb.getItemCount() > 0);
	}

	@Override
	protected void onEnterAction(){
		if (!btnok.isEnabled() || fromAttribute.getSelectedItem() == null){
			return;
		}

		EDynamicAttributeScope _scope = EDynamicAttributeScope.valueOf((String) scope.getSelectedItem());
		Attribute src = (Attribute) fromAttribute.getSelectedItem();

		boolean attrExists = false;

		for (Attribute a : ((Entity) toEntity.getSelectedItem()).getReadableAttributes())
			if (a.isDynamic() && a.getDynamicScope() != _scope && a.getDynamicFromAttribute() == src){
				attrExists = true;
				a.setDynamicScope(_scope);
				a.createLabel();
				parentMP.Doc.DB.refreshDynamicAttributes(parentMP.Doc.Subgraph);
				parentMP.Doc.dispatchEvent(Ni3ItemListener.MSG_SubgraphChanged, Ni3ItemListener.SRC_Unknown, parentMP, null);
			}

		if (!attrExists){
			newAttr = new Attribute((Entity) toEntity.getSelectedItem(), (Entity) fromEntity.getSelectedItem(), src,
					DynamicAttributeOperation.valueOf((String) function.getSelectedItem()), _scope);

			newAttr.ent.addAttribute(newAttr);

			parentMP.Doc.dispatchEvent(Ni3ItemListener.MSG_DynamicAttributeAdded, Ni3ItemListener.SRC_Unknown, parentMP,
					newAttr);
		}

		doClose(RET_OK);
	}

	/** @return the return status of this dialog - one of RET_OK or RET_CANCEL */
	public int getReturnStatus(){
		return returnStatus;
	}

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt){
		doClose(RET_CANCEL);
	}

	/** Closes the dialog */
	@SuppressWarnings("unused")
	private void closeDialog(java.awt.event.WindowEvent evt){
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

	@Override
	public void actionPerformed(ActionEvent e){
		if (e.getSource() == fromEntity || e.getSource() == toEntity || e.getSource() == scope)
			fillAttributes((Entity) (fromEntity.getSelectedItem()), (Entity) (toEntity.getSelectedItem()), fromAttribute,
					EDynamicAttributeScope.valueOf((String) scope.getSelectedItem()));
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
