package com.ni3.ag.navigator.client.gui.graph.layoutManager.layoutManagerSettings;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;

@SuppressWarnings("serial")
public class DlgHierarchyManagerSettings extends Ni3Dialog{

	public HierarchyManagerSettings settings;

	private JCheckBox hideEdges;

	public static final int RET_CANCEL = 0;
	public static final int RET_OK = 1;
	private int returnStatus;

	public DlgHierarchyManagerSettings(HierarchyManagerSettings settings){
		super();

		this.settings = settings;

		initialize();

		setSize(200, 150);
	}

	void initialize(){
		setTitle(UserSettings.getWord("Settings"));
		getContentPane().setLayout(new BorderLayout());

		hideEdges = new JCheckBox(UserSettings.getWord("Hide hierarchical edges"), settings.hideHierarchyEdges());

		getContentPane().add(hideEdges, BorderLayout.NORTH);
		/*
		 * JLabel lbl = new JLabel(UserSettings.getWord("Node labels")); getContentPane().add(lbl);
		 * 
		 * final ButtonGroup labelShowGrp = new ButtonGroup();
		 * 
		 * JPanel p1 = new JPanel(new BorderLayout());
		 * 
		 * p1.add(lbl,BorderLayout.NORTH);
		 * 
		 * JPanel panel = new JPanel(); panel.setLayout(new GridLayout(2, 2));
		 * 
		 * btn1 = new JRadioButton(UserSettings.getWord("In line"), settings.labels == EShowLabel.None);
		 * labelShowGrp.add(btn1); panel.add(btn1);
		 * 
		 * btn2 = new JRadioButton(UserSettings.getWord("Trim"), settings.labels == EShowLabel.Trim);
		 * labelShowGrp.add(btn2); panel.add(btn2);
		 * 
		 * btn3 = new JRadioButton(UserSettings.getWord("Wrap"), settings.labels == EShowLabel.Wrap);
		 * labelShowGrp.add(btn3); panel.add(btn3);
		 * 
		 * btn4 = new JRadioButton(UserSettings.getWord("Zig-Zag"), settings.labels == EShowLabel.ZigZag);
		 * labelShowGrp.add(btn4); panel.add(btn4);
		 * 
		 * p1.add(panel,BorderLayout.CENTER); getContentPane().add(p1, BorderLayout.CENTER);
		 */
		JPanel okcancel = new JPanel();

		JButton btn = new JButton(UserSettings.getWord("OK"));
		okcancel.add(btn);

		btn.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				okButtonActionPerformed();
			}
		});

		btn = new JButton(UserSettings.getWord("Cancel"));
		okcancel.add(btn);
		btn.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				cancelButtonActionPerformed();
			}
		});

		getContentPane().add(okcancel, BorderLayout.SOUTH);

	}

	private void okButtonActionPerformed(){
		onEnterAction();
	}

	protected void onEnterAction(){
		settings.setHideHierarchyEdges(hideEdges.isSelected());
		doClose(RET_OK);
	}

	/** @return the return status of this dialog - one of RET_OK or RET_CANCEL */
	public int getReturnStatus(){
		return returnStatus;
	}

	private void cancelButtonActionPerformed(){
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

}
