/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.client.view.schemaadmin;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.table.TableColumn;

import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.view.ErrorRenderer;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class SalesforceImportDialog extends JDialog implements ErrorRenderer{
	private static final long serialVersionUID = 1L;
	private JComboBox tabSetCombo;
	private JTable tabTable;
	private ACButton okButton;
	private ACButton cancelButton;
	private ACButton loginButton;
	private JTextField urlTxt;
	private JTextField usernameTxt;
	private JPasswordField passwordTxt;
	private JLabel errorLabel;
	private JPanel cardPanel;
	private CardLayout cardLayout;

	private SalesforceTabSelectionTableModel model;

	private java.util.Map<String, List<String>> data;
	private boolean success;

	public SalesforceImportDialog(){
		super();
		setModal(true);
		setTitle(Translation.get(TextID.SalesforceSchemaImport));
		initComponents();
		setSize(380, 400);
		setLocation((int) (ACMain.getScreenWidth() / 2) - getWidth() / 2, (int) (ACMain.getScreenHeight() / 2) - getHeight()
				/ 2);
		setIconImage(new ImageIcon(getClass().getResource("/images/Ni3.png")).getImage());
	}

	public void initComponents(){
		JPanel mainPanel = new JPanel();
		SpringLayout mainLayout = new SpringLayout();
		mainPanel.setLayout(mainLayout);
		getContentPane().add(mainPanel);

		cardPanel = new JPanel();
		cardLayout = new CardLayout();
		cardPanel.setLayout(cardLayout);
		JPanel loginPanel = new JPanel();
		JPanel importPanel = new JPanel();
		SpringLayout loginLayout = new SpringLayout();
		loginPanel.setLayout(loginLayout);
		SpringLayout importLayout = new SpringLayout();
		importPanel.setLayout(importLayout);

		urlTxt = new JTextField();
		JLabel urlLabel = new JLabel(Translation.get(TextID.URL));
		usernameTxt = new JTextField();
		JLabel usernameLabel = new JLabel(Translation.get(TextID.UserName));
		passwordTxt = new JPasswordField();
		passwordTxt.setToolTipText(Translation.get(TextID.SalesforcePasswordTooltip));
		JLabel passwordLabel = new JLabel(Translation.get(TextID.Password));

		tabSetCombo = new JComboBox();
		JLabel tabSetLabel = new JLabel(Translation.get(TextID.TabSet));
		tabTable = new JTable();
		loginButton = new ACButton(TextID.Login);
		okButton = new ACButton(TextID.Ok);
		cancelButton = new ACButton(TextID.Cancel);
		JScrollPane jsp = new JScrollPane(tabTable);

		errorLabel = new JLabel();
		errorLabel.setForeground(Color.RED);
		errorLabel.setFont(new Font(errorLabel.getFont().getName(), Font.BOLD, 12));
		errorLabel.setBounds(10, 140, 350, 25);

		model = new SalesforceTabSelectionTableModel(new ArrayList<String>());
		tabTable.setModel(model);

		mainPanel.add(cardPanel);
		cardPanel.add(loginPanel, "0");
		cardPanel.add(importPanel, "1");

		loginPanel.add(urlLabel);
		loginPanel.add(urlTxt);
		loginPanel.add(usernameLabel);
		loginPanel.add(usernameTxt);
		loginPanel.add(passwordLabel);
		loginPanel.add(passwordTxt);
		importPanel.add(tabSetCombo);
		importPanel.add(tabSetLabel);
		importPanel.add(jsp);
		mainPanel.add(loginButton);
		mainPanel.add(okButton);
		mainPanel.add(cancelButton);
		mainPanel.add(errorLabel);

		loginLayout.putConstraint(SpringLayout.NORTH, urlTxt, 50, SpringLayout.NORTH, loginPanel);
		loginLayout.putConstraint(SpringLayout.WEST, urlTxt, 80, SpringLayout.WEST, loginPanel);
		loginLayout.putConstraint(SpringLayout.EAST, urlTxt, -20, SpringLayout.EAST, loginPanel);
		loginLayout.putConstraint(SpringLayout.EAST, urlLabel, -10, SpringLayout.WEST, urlTxt);
		loginLayout.putConstraint(SpringLayout.NORTH, urlLabel, 0, SpringLayout.NORTH, urlTxt);

		loginLayout.putConstraint(SpringLayout.NORTH, usernameTxt, 10, SpringLayout.SOUTH, urlTxt);
		loginLayout.putConstraint(SpringLayout.WEST, usernameTxt, 0, SpringLayout.WEST, urlTxt);
		loginLayout.putConstraint(SpringLayout.EAST, usernameTxt, -20, SpringLayout.EAST, loginPanel);
		loginLayout.putConstraint(SpringLayout.EAST, usernameLabel, -10, SpringLayout.WEST, usernameTxt);
		loginLayout.putConstraint(SpringLayout.NORTH, usernameLabel, 0, SpringLayout.NORTH, usernameTxt);

		loginLayout.putConstraint(SpringLayout.NORTH, passwordTxt, 10, SpringLayout.SOUTH, usernameTxt);
		loginLayout.putConstraint(SpringLayout.WEST, passwordTxt, 0, SpringLayout.WEST, usernameTxt);
		loginLayout.putConstraint(SpringLayout.EAST, passwordTxt, -20, SpringLayout.EAST, loginPanel);
		loginLayout.putConstraint(SpringLayout.EAST, passwordLabel, -10, SpringLayout.WEST, passwordTxt);
		loginLayout.putConstraint(SpringLayout.NORTH, passwordLabel, 0, SpringLayout.NORTH, passwordTxt);

		importLayout.putConstraint(SpringLayout.WEST, tabSetCombo, 80, SpringLayout.WEST, importPanel);
		importLayout.putConstraint(SpringLayout.EAST, tabSetCombo, -10, SpringLayout.EAST, importPanel);
		importLayout.putConstraint(SpringLayout.NORTH, tabSetCombo, 10, SpringLayout.NORTH, importPanel);

		importLayout.putConstraint(SpringLayout.EAST, tabSetLabel, -10, SpringLayout.WEST, tabSetCombo);
		importLayout.putConstraint(SpringLayout.NORTH, tabSetLabel, 0, SpringLayout.NORTH, tabSetCombo);

		importLayout.putConstraint(SpringLayout.NORTH, jsp, 10, SpringLayout.SOUTH, tabSetCombo);
		importLayout.putConstraint(SpringLayout.WEST, jsp, 10, SpringLayout.WEST, importPanel);
		importLayout.putConstraint(SpringLayout.EAST, jsp, -10, SpringLayout.EAST, importPanel);
		importLayout.putConstraint(SpringLayout.SOUTH, jsp, 0, SpringLayout.SOUTH, importPanel);

		mainLayout.putConstraint(SpringLayout.NORTH, cardPanel, 0, SpringLayout.NORTH, mainPanel);
		mainLayout.putConstraint(SpringLayout.WEST, cardPanel, 0, SpringLayout.WEST, mainPanel);
		mainLayout.putConstraint(SpringLayout.EAST, cardPanel, 0, SpringLayout.EAST, mainPanel);
		mainLayout.putConstraint(SpringLayout.SOUTH, cardPanel, -60, SpringLayout.SOUTH, mainPanel);

		mainLayout.putConstraint(SpringLayout.NORTH, errorLabel, 0, SpringLayout.SOUTH, cardPanel);
		mainLayout.putConstraint(SpringLayout.WEST, errorLabel, 10, SpringLayout.WEST, mainPanel);

		mainLayout.putConstraint(SpringLayout.NORTH, cancelButton, -33, SpringLayout.SOUTH, mainPanel);
		mainLayout.putConstraint(SpringLayout.EAST, cancelButton, -10, SpringLayout.EAST, mainPanel);

		mainLayout.putConstraint(SpringLayout.NORTH, okButton, -33, SpringLayout.SOUTH, mainPanel);
		mainLayout.putConstraint(SpringLayout.EAST, okButton, -10, SpringLayout.WEST, cancelButton);

		mainLayout.putConstraint(SpringLayout.NORTH, loginButton, -33, SpringLayout.SOUTH, mainPanel);
		mainLayout.putConstraint(SpringLayout.EAST, loginButton, -10, SpringLayout.WEST, cancelButton);

		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				setVisible(false);
			}
		});

		tabSetCombo.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				String tabSet = (String) tabSetCombo.getSelectedItem();
				model.setData(data.get(tabSet));
				okButton.setEnabled(getSelectedTabSet() != null);
			}
		});

		TableColumn column = tabTable.getColumnModel().getColumn(0);
		column.setPreferredWidth(30);
		column.setMinWidth(30);
		column.setMaxWidth(30);
	}

	public void showDialog(String url, String username, String password){
		success = false;
		urlTxt.setText(url);
		usernameTxt.setText(username);
		passwordTxt.setText(password);
		usernameTxt.requestFocusInWindow();
		setActiveLoginView(true);
		setVisible(true);
	}

	public void setActiveLoginView(boolean active){
		okButton.setVisible(!active);
		loginButton.setVisible(active);
		if (active){
			cardLayout.first(cardPanel);
		} else{
			cardLayout.last(cardPanel);
		}
	}

	public void setData(Map<String, List<String>> data){
		this.data = data;
		tabSetCombo.removeAllItems();
		for (String key : data.keySet()){
			tabSetCombo.addItem(key);
		}
		if (tabSetCombo.getItemCount() > 0){
			tabSetCombo.setSelectedIndex(0);
		}
		setActiveLoginView(false);
	}

	public List<String> getSelectedTabNames(){
		return model.getSelectedTabNames();
	}

	public String getSelectedTabSet(){
		return (String) tabSetCombo.getSelectedItem();
	}

	public String getUrl(){
		return urlTxt.getText();
	}

	public String getUsername(){
		return usernameTxt.getText();
	}

	public String getPassword(){
		return new String(passwordTxt.getPassword());
	}

	public void addLoginButtonListener(ActionListener l){
		loginButton.addActionListener(l);
	}

	public void addOkButtonListener(ActionListener l){
		okButton.addActionListener(l);
	}

	public void clearErrors(){
		errorLabel.setText("");
	}

	public void renderErrors(ErrorContainer c){
		if (c != null){
			renderErrors(c.getErrors());
		}
	}

	@Override
	public void renderErrors(List<ErrorEntry> errors){
		if (errors != null && !errors.isEmpty()){
			ErrorEntry err = errors.get(0);
			String msg = Translation.get(err.getId(), err.getErrors());
			errorLabel.setText(msg);
		}
	}

	public boolean isSuccess(){
		return success;
	}

	public void setSuccess(boolean b){
		this.success = b;
	}
}
