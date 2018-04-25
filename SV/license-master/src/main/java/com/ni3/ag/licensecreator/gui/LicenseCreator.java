package com.ni3.ag.licensecreator.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JSpinner.DateEditor;
import javax.swing.text.AbstractDocument;
import javax.swing.JTextArea;
import javax.swing.SpinnerDateModel;
import javax.swing.SpringLayout;

import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.util.TimeUtil;
import com.ni3.ag.licensecreator.actions.AdminCountFieldDocFilter;
import com.ni3.ag.licensecreator.actions.AdmincountFieldFocusListener;
import com.ni3.ag.licensecreator.actions.CreateActionListener;
import com.ni3.ag.licensecreator.actions.ProductRadioActionListener;
import com.ni3.ag.licensecreator.model.PropertyTableModel;

public class LicenseCreator extends JFrame{
	private static final long serialVersionUID = 1L;
	private JTextArea area;
	private JScrollPane areaScroll;
	private JButton createButton;
	private JSpinner expiryDateTimeSpinner;
	private JSpinner startDateTimeSpinner;
	private JRadioButton acButton;
	private JRadioButton apiButton;
	private JRadioButton navigatorButton;
	private JRadioButton userModuleButton;
	private JCheckBox makeQuery;
	private JTextField systemIdField;
	private JTextField adminCountField;
	private JTable propTable;
	private JTextField clientField;
	private JTextField userIdField;
	private JLabel userIdLabel;
	private JLabel systemIdInfo;
	private JLabel systemIdLabel;
	private JLabel adminCountLabel;
	private JLabel dtLabel;
	private JLabel dtLabel2;
	private JLabel clientInfo;
	private JLabel clientNameLabel;

	private String[] products = new String[] { LicenseData.ACNi3WEB_PRODUCT, LicenseData.API_PRODUCT,
	        LicenseData.NAVIGATOR_PRODUCT };

	public LicenseCreator(){
		createComponents();
		setSize(new Dimension(800, 700));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("License generator");
	}

	private void createComponents(){
		Container c = getContentPane();
		area = new JTextArea();
		SpringLayout layout = new SpringLayout();
		c.setLayout(layout);
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEtchedBorder());
		layout.putConstraint(SpringLayout.NORTH, panel, 10, SpringLayout.NORTH, c);
		layout.putConstraint(SpringLayout.WEST, panel, 10, SpringLayout.WEST, c);
		layout.putConstraint(SpringLayout.EAST, panel, -10, SpringLayout.EAST, c);
		layout.putConstraint(SpringLayout.SOUTH, panel, 195, SpringLayout.NORTH, c);
		c.add(panel);

		SpringLayout panelLayout = new SpringLayout();
		panel.setLayout(panelLayout);

		acButton = new JRadioButton("Admin Console");
		panelLayout.putConstraint(SpringLayout.NORTH, acButton, 10, SpringLayout.NORTH, panel);
		panelLayout.putConstraint(SpringLayout.WEST, acButton, 10, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.EAST, acButton, 150, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.SOUTH, acButton, 25, SpringLayout.NORTH, panel);
		panel.add(acButton);

		apiButton = new JRadioButton("API");
		panelLayout.putConstraint(SpringLayout.NORTH, apiButton, 35, SpringLayout.NORTH, panel);
		panelLayout.putConstraint(SpringLayout.WEST, apiButton, 10, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.EAST, apiButton, 120, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.SOUTH, apiButton, 55, SpringLayout.NORTH, panel);
		panel.add(apiButton);

		navigatorButton = new JRadioButton("Navigator");
		panelLayout.putConstraint(SpringLayout.NORTH, navigatorButton, 60, SpringLayout.NORTH, panel);
		panelLayout.putConstraint(SpringLayout.WEST, navigatorButton, 10, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.EAST, navigatorButton, 120, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.SOUTH, navigatorButton, 85, SpringLayout.NORTH, panel);
		panel.add(navigatorButton);

		userModuleButton = new JRadioButton("Navigator module access");
		panelLayout.putConstraint(SpringLayout.NORTH, userModuleButton, 10, SpringLayout.NORTH, panel);
		panelLayout.putConstraint(SpringLayout.WEST, userModuleButton, 150, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.EAST, userModuleButton, 330, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.SOUTH, userModuleButton, 25, SpringLayout.NORTH, panel);
		panel.add(userModuleButton);

		userIdLabel = new JLabel("User ID");
		panelLayout.putConstraint(SpringLayout.NORTH, userIdLabel, 7, SpringLayout.NORTH, panel);
		panelLayout.putConstraint(SpringLayout.WEST, userIdLabel, 350, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.EAST, userIdLabel, 50, SpringLayout.WEST, userIdLabel);
		panelLayout.putConstraint(SpringLayout.SOUTH, userIdLabel, 20, SpringLayout.NORTH, userIdLabel);
		panel.add(userIdLabel);

		userIdField = new JTextField("1");
		panelLayout.putConstraint(SpringLayout.NORTH, userIdField, 1, SpringLayout.NORTH, userIdLabel);
		panelLayout.putConstraint(SpringLayout.WEST, userIdField, 5, SpringLayout.EAST, userIdLabel);
		panelLayout.putConstraint(SpringLayout.EAST, userIdField, 50, SpringLayout.WEST, userIdField);
		panelLayout.putConstraint(SpringLayout.SOUTH, userIdField, 1, SpringLayout.SOUTH, userIdLabel);
		panel.add(userIdField);

		expiryDateTimeSpinner = new JSpinner();
		panelLayout.putConstraint(SpringLayout.NORTH, expiryDateTimeSpinner, 40, SpringLayout.NORTH, panel);
		panelLayout.putConstraint(SpringLayout.WEST, expiryDateTimeSpinner, -100, SpringLayout.EAST, panel);
		panelLayout.putConstraint(SpringLayout.EAST, expiryDateTimeSpinner, -10, SpringLayout.EAST, panel);
		panelLayout.putConstraint(SpringLayout.SOUTH, expiryDateTimeSpinner, 62, SpringLayout.NORTH, panel);
		panel.add(expiryDateTimeSpinner);

		startDateTimeSpinner = new JSpinner();
		panelLayout.putConstraint(SpringLayout.NORTH, startDateTimeSpinner, 10, SpringLayout.NORTH, panel);
		panelLayout.putConstraint(SpringLayout.WEST, startDateTimeSpinner, -100, SpringLayout.EAST, panel);
		panelLayout.putConstraint(SpringLayout.EAST, startDateTimeSpinner, -10, SpringLayout.EAST, panel);
		panelLayout.putConstraint(SpringLayout.SOUTH, startDateTimeSpinner, 32, SpringLayout.NORTH, panel);
		panel.add(startDateTimeSpinner);

		dtLabel = new JLabel();
		dtLabel.setText("Expiry date");
		panelLayout.putConstraint(SpringLayout.NORTH, dtLabel, 0, SpringLayout.NORTH, expiryDateTimeSpinner);
		panelLayout.putConstraint(SpringLayout.EAST, dtLabel, -5, SpringLayout.WEST, expiryDateTimeSpinner);
		panelLayout.putConstraint(SpringLayout.WEST, dtLabel, -80, SpringLayout.WEST, expiryDateTimeSpinner);
		panelLayout.putConstraint(SpringLayout.SOUTH, dtLabel, 0, SpringLayout.SOUTH, expiryDateTimeSpinner);
		panel.add(dtLabel);

		dtLabel2 = new JLabel();
		dtLabel2.setText("Start date");
		panelLayout.putConstraint(SpringLayout.NORTH, dtLabel2, 0, SpringLayout.NORTH, startDateTimeSpinner);
		panelLayout.putConstraint(SpringLayout.EAST, dtLabel2, -5, SpringLayout.WEST, startDateTimeSpinner);
		panelLayout.putConstraint(SpringLayout.WEST, dtLabel2, -80, SpringLayout.WEST, startDateTimeSpinner);
		panelLayout.putConstraint(SpringLayout.SOUTH, dtLabel2, 0, SpringLayout.SOUTH, startDateTimeSpinner);
		panel.add(dtLabel2);

		createButton = new JButton("Create");
		panelLayout.putConstraint(SpringLayout.NORTH, createButton, -30, SpringLayout.SOUTH, panel);
		panelLayout.putConstraint(SpringLayout.WEST, createButton, -90, SpringLayout.EAST, panel);
		panelLayout.putConstraint(SpringLayout.EAST, createButton, -10, SpringLayout.EAST, panel);
		panelLayout.putConstraint(SpringLayout.SOUTH, createButton, -10, SpringLayout.SOUTH, panel);
		panel.add(createButton);

		makeQuery = new JCheckBox("Generate SQL query");
		panelLayout.putConstraint(SpringLayout.NORTH, makeQuery, -30, SpringLayout.NORTH, createButton);
		panelLayout.putConstraint(SpringLayout.WEST, makeQuery, -150, SpringLayout.EAST, panel);
		panelLayout.putConstraint(SpringLayout.EAST, makeQuery, -10, SpringLayout.EAST, panel);
		panelLayout.putConstraint(SpringLayout.SOUTH, makeQuery, -10, SpringLayout.NORTH, createButton);
		panel.add(makeQuery);

		systemIdLabel = new JLabel("System ID");
		panelLayout.putConstraint(SpringLayout.NORTH, systemIdLabel, 10, SpringLayout.SOUTH, navigatorButton);
		panelLayout.putConstraint(SpringLayout.WEST, systemIdLabel, 15, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.EAST, systemIdLabel, 90, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.SOUTH, systemIdLabel, 30, SpringLayout.SOUTH, navigatorButton);
		panel.add(systemIdLabel);

		systemIdField = new JTextField();
		panelLayout.putConstraint(SpringLayout.NORTH, systemIdField, 1, SpringLayout.NORTH, systemIdLabel);
		panelLayout.putConstraint(SpringLayout.WEST, systemIdField, 5, SpringLayout.EAST, systemIdLabel);
		panelLayout.putConstraint(SpringLayout.EAST, systemIdField, 250, SpringLayout.EAST, systemIdLabel);
		panelLayout.putConstraint(SpringLayout.SOUTH, systemIdField, 1, SpringLayout.SOUTH, systemIdLabel);
		panel.add(systemIdField);

		systemIdInfo = new JLabel("if empty - not included");
		panelLayout.putConstraint(SpringLayout.NORTH, systemIdInfo, 0, SpringLayout.NORTH, systemIdLabel);
		panelLayout.putConstraint(SpringLayout.WEST, systemIdInfo, 5, SpringLayout.EAST, systemIdField);
		panelLayout.putConstraint(SpringLayout.EAST, systemIdInfo, 150, SpringLayout.EAST, systemIdField);
		panelLayout.putConstraint(SpringLayout.SOUTH, systemIdInfo, 0, SpringLayout.SOUTH, systemIdLabel);
		panel.add(systemIdInfo);

		adminCountLabel = new JLabel("Admin count");
		panelLayout.putConstraint(SpringLayout.NORTH, adminCountLabel, 10, SpringLayout.SOUTH, systemIdLabel);
		panelLayout.putConstraint(SpringLayout.WEST, adminCountLabel, 15, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.EAST, adminCountLabel, 90, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.SOUTH, adminCountLabel, 30, SpringLayout.SOUTH, systemIdLabel);
		panel.add(adminCountLabel);

		adminCountField = new JTextField("5");
		panelLayout.putConstraint(SpringLayout.NORTH, adminCountField, 1, SpringLayout.NORTH, adminCountLabel);
		panelLayout.putConstraint(SpringLayout.WEST, adminCountField, 5, SpringLayout.EAST, adminCountLabel);
		panelLayout.putConstraint(SpringLayout.EAST, adminCountField, 30, SpringLayout.EAST, adminCountLabel);
		panelLayout.putConstraint(SpringLayout.SOUTH, adminCountField, 1, SpringLayout.SOUTH, adminCountLabel);
		panel.add(adminCountField);

		clientNameLabel = new JLabel("Client name");
		panelLayout.putConstraint(SpringLayout.NORTH, clientNameLabel, 10, SpringLayout.SOUTH, adminCountLabel);
		panelLayout.putConstraint(SpringLayout.WEST, clientNameLabel, 15, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.EAST, clientNameLabel, 90, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.SOUTH, clientNameLabel, 30, SpringLayout.SOUTH, adminCountLabel);
		panel.add(clientNameLabel);

		clientField = new JTextField();
		panelLayout.putConstraint(SpringLayout.NORTH, clientField, 1, SpringLayout.NORTH, clientNameLabel);
		panelLayout.putConstraint(SpringLayout.WEST, clientField, 5, SpringLayout.EAST, clientNameLabel);
		panelLayout.putConstraint(SpringLayout.EAST, clientField, 250, SpringLayout.EAST, clientNameLabel);
		panelLayout.putConstraint(SpringLayout.SOUTH, clientField, 1, SpringLayout.SOUTH, clientNameLabel);
		panel.add(clientField);

		clientInfo = new JLabel("if empty - not included");
		panelLayout.putConstraint(SpringLayout.NORTH, clientInfo, 0, SpringLayout.NORTH, clientNameLabel);
		panelLayout.putConstraint(SpringLayout.WEST, clientInfo, 5, SpringLayout.EAST, clientField);
		panelLayout.putConstraint(SpringLayout.EAST, clientInfo, 150, SpringLayout.EAST, clientField);
		panelLayout.putConstraint(SpringLayout.SOUTH, clientInfo, 0, SpringLayout.SOUTH, clientNameLabel);
		panel.add(clientInfo);

		SpinnerDateModel spinnerModel = new SpinnerDateModel(TimeUtil.getToday(), null, null,
		        java.util.Calendar.DAY_OF_MONTH);
		expiryDateTimeSpinner.setModel(spinnerModel);
		DateEditor editor = new javax.swing.JSpinner.DateEditor(expiryDateTimeSpinner, "dd.MM.yyyy");
		expiryDateTimeSpinner.setEditor(editor);

		spinnerModel = new SpinnerDateModel(TimeUtil.getToday(), null, null, java.util.Calendar.DAY_OF_MONTH);
		startDateTimeSpinner.setModel(spinnerModel);
		editor = new javax.swing.JSpinner.DateEditor(startDateTimeSpinner, "dd.MM.yyyy");
		startDateTimeSpinner.setEditor(editor);

		ButtonGroup bg = new ButtonGroup();
		bg.add(acButton);
		bg.add(apiButton);
		bg.add(navigatorButton);
		bg.add(userModuleButton);
		acButton.setSelected(true);

		propTable = new JTable();
		JScrollPane propTableScroll = new JScrollPane();
		propTableScroll.setViewportView(propTable);
		layout.putConstraint(SpringLayout.NORTH, propTableScroll, 10, SpringLayout.SOUTH, panel);
		layout.putConstraint(SpringLayout.WEST, propTableScroll, 10, SpringLayout.WEST, c);
		layout.putConstraint(SpringLayout.EAST, propTableScroll, -10, SpringLayout.EAST, c);
		layout.putConstraint(SpringLayout.SOUTH, propTableScroll, 190, SpringLayout.SOUTH, panel);
		c.add(propTableScroll);

		areaScroll = new JScrollPane();
		area = new JTextArea();
		areaScroll.setViewportView(area);
		layout.putConstraint(SpringLayout.NORTH, areaScroll, 10, SpringLayout.SOUTH, propTableScroll);
		layout.putConstraint(SpringLayout.WEST, areaScroll, 10, SpringLayout.WEST, c);
		layout.putConstraint(SpringLayout.EAST, areaScroll, -10, SpringLayout.EAST, c);
		layout.putConstraint(SpringLayout.SOUTH, areaScroll, -10, SpringLayout.SOUTH, c);
		areaScroll.setBorder(BorderFactory.createEtchedBorder());
		c.add(areaScroll);

		propTable.setModel(new PropertyTableModel(getProduct()));

		createButton.addActionListener(new CreateActionListener(this));

		ProductRadioActionListener pal = new ProductRadioActionListener(this);
		navigatorButton.addActionListener(pal);
		apiButton.addActionListener(pal);
		acButton.addActionListener(pal);
		userModuleButton.addActionListener(pal);

		((AbstractDocument) adminCountField.getDocument()).setDocumentFilter(new AdminCountFieldDocFilter());
		adminCountField.addFocusListener(new AdmincountFieldFocusListener(this));
		updateComponentAvailability();
	}

	public String getProduct(){
		assert (acButton.isSelected() || apiButton.isSelected() || navigatorButton.isSelected() || userModuleButton
		        .isSelected());
		if (acButton.isSelected())
			return products[0];
		else if (apiButton.isSelected())
			return products[1];
		else if (navigatorButton.isSelected())
			return products[2];
		return null;
	}

	public boolean isUserModuleSelected(){
		return userModuleButton.isSelected();
	}

	public Integer getUserId(){
		Integer userId = null;
		try{
			userId = Integer.parseInt(userIdField.getText());
		} catch (NumberFormatException ex){
			userId = 1;
			userIdField.setText("1");
		}
		return userId;
	}

	public JTable getPropertyTable(){
		return propTable;
	}

	public void setLicenseText(String slicense){
		area.setText(slicense);
	}

	public void setLicenseValid(boolean valid){
		areaScroll.setBorder(BorderFactory.createLineBorder(valid ? Color.green : Color.red));
	}

	public Date getExpiryDate(){
		return (Date) expiryDateTimeSpinner.getValue();
	}

	public Date getStartDate(){
		return (Date) startDateTimeSpinner.getValue();
	}

	public Integer getAdminCount(){
		return Integer.valueOf(adminCountField.getText());
	}

	public String getSystemId(){
		return systemIdField.getText();
	}

	public String getClient(){
		return clientField.getText();
	}

	public boolean makeQuery(){
		return makeQuery.isSelected();
	}

	public String getAdminCountText(){
		return adminCountField.getText();
	}

	public void setAdminCountText(String string){
		adminCountField.setText(string);
	}

	public void updateComponentAvailability(){
		boolean isAPI = apiButton.isSelected();
		boolean isNavigator = navigatorButton.isSelected();
		boolean isAC = acButton.isSelected();
		boolean isUserModule = userModuleButton.isSelected();

		systemIdField.setEnabled(isAPI || isNavigator || isAC);
		systemIdLabel.setEnabled(isAPI || isNavigator || isAC);
		systemIdInfo.setEnabled(isAPI || isNavigator || isAC);
		adminCountField.setEnabled(isAC);
		adminCountLabel.setEnabled(isAC);
		propTable.setEnabled(isNavigator || isAC);
		clientField.setEnabled(isAPI || isNavigator || isAC);
		clientInfo.setEnabled(isAPI || isNavigator || isAC);
		clientNameLabel.setEnabled(isAPI || isNavigator || isAC);
		makeQuery.setEnabled(isAPI || isNavigator || isAC);
		userIdField.setEnabled(isUserModule);
		userIdLabel.setEnabled(isUserModule);
		expiryDateTimeSpinner.setEnabled(isAPI || isNavigator || isAC);
		startDateTimeSpinner.setEnabled(isAPI || isNavigator || isAC);
		dtLabel.setEnabled(isAPI || isNavigator || isAC);
		dtLabel2.setEnabled(isAPI || isNavigator || isAC);
	}

}
