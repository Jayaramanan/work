package com.ni3.ag.navigator.client.gui;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.common.Ni3Frame;

public class ActivityStreamFrame extends Ni3Frame{
	private static final long serialVersionUID = 6491514892587562437L;
	private JButton refreshButton;
	private JButton showMoreButton;
	private JCheckBox showOnStartupCb;

	private JEditorPane htmlPane;

	public ActivityStreamFrame(){
		super(UserSettings.getWord("ActivityStream"));
		initComponents();
	}

	protected void initComponents(){
		setSize(new Dimension(650, 420));

		JPanel mainPanel = new JPanel();
		SpringLayout layout = new SpringLayout();
		mainPanel.setLayout(layout);
		getContentPane().add(mainPanel);

		htmlPane = new JEditorPane();
		htmlPane.setEditable(false);
		htmlPane.setContentType("text/html");

		JScrollPane sp = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setViewportView(htmlPane);

		mainPanel.add(sp);
		layout.putConstraint(SpringLayout.NORTH, sp, 0, SpringLayout.NORTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, sp, 0, SpringLayout.EAST, mainPanel);
		layout.putConstraint(SpringLayout.WEST, sp, 0, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.SOUTH, sp, -40, SpringLayout.SOUTH, mainPanel);

		refreshButton = new JButton(UserSettings.getWord("Refresh"));

		layout.putConstraint(SpringLayout.NORTH, refreshButton, -33, SpringLayout.SOUTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, refreshButton, -10, SpringLayout.EAST, mainPanel);
		mainPanel.add(refreshButton);

		showMoreButton = new JButton(UserSettings.getWord("ShowMore"));

		layout.putConstraint(SpringLayout.NORTH, showMoreButton, -33, SpringLayout.SOUTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, showMoreButton, -10, SpringLayout.WEST, refreshButton);
		mainPanel.add(showMoreButton);

		showOnStartupCb = new JCheckBox(UserSettings.getWord("ShowOnStartup"));

		layout.putConstraint(SpringLayout.NORTH, showOnStartupCb, -33, SpringLayout.SOUTH, mainPanel);
		layout.putConstraint(SpringLayout.WEST, showOnStartupCb, 10, SpringLayout.WEST, mainPanel);
		mainPanel.add(showOnStartupCb);

		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setAlwaysOnTop(true);
	}

	public void addRefreshButtonListener(ActionListener l){
		refreshButton.addActionListener(l);
	}

	public void addShowMoreButtonListener(ActionListener l){
		showMoreButton.addActionListener(l);
	}

	public void addShowOnStartupCheckboxListener(ItemListener l){
		showOnStartupCb.addItemListener(l);
	}

	public void setShowOnStartup(boolean value){
		showOnStartupCb.setSelected(value);
	}

	public JEditorPane getHtmlPane(){
		return htmlPane;
	}

	public void showIt(){
		double screenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		double screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		setLocation((int) (screenWidth / 2) - getWidth() / 2, (int) (screenHeight / 2) - getHeight() / 2);
		setVisible(true);
	}

}