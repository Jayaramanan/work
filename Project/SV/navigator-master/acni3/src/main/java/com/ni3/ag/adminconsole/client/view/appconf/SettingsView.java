/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import static com.ni3.ag.adminconsole.client.view.Translation.get;
import static com.ni3.ag.adminconsole.shared.language.TextID.ReadonlyCheckProperty;

import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.controller.appconf.settings.AccessComboListener;
import com.ni3.ag.adminconsole.client.controller.appconf.settings.DeleteAccessType;
import com.ni3.ag.adminconsole.client.controller.appconf.settings.HideGISPanelListener;
import com.ni3.ag.adminconsole.client.controller.appconf.settings.LanguageComboListener;
import com.ni3.ag.adminconsole.client.controller.appconf.settings.SchemaComboListener;
import com.ni3.ag.adminconsole.client.controller.appconf.settings.TabSwitchActionComboListener;
import com.ni3.ag.adminconsole.client.controller.appconf.settings.UpdateAccessType;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.view.AbstractView;
import com.ni3.ag.adminconsole.client.view.ErrorPanel;
import com.ni3.ag.adminconsole.client.view.ErrorRenderer;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACCheckBox;
import com.ni3.ag.adminconsole.client.view.common.ACComboBox;
import com.ni3.ag.adminconsole.client.view.common.ACMandatoryLabel;
import com.ni3.ag.adminconsole.client.view.common.ACTable;
import com.ni3.ag.adminconsole.client.view.common.ACTextField;
import com.ni3.ag.adminconsole.client.view.common.ACToolBar;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.common.ACTreeCellRenderer;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class SettingsView extends JPanel implements AbstractView, ErrorRenderer{

	private static final long serialVersionUID = -2457559592561671076L;
	private ErrorPanel errorPanel;
	private ACTree userTree;
	private ACTable settingsTable;
	private ACButton deleteButton;
	private ACButton updateButton;
	private ACButton refreshButton;
	private ACButton addButton;
	private ACToolBar toolBar;
	private ACComboBox schemaCombo;
	private ACComboBox languageCombo;
	private ACComboBox updateAccessCombo;
	private ACComboBox deleteAccessCombo;
	private ACMandatoryLabel schemaLabel;
	private ACMandatoryLabel languageLabel;
	private JLabel updateAccessLabel;
	private JLabel deleteAccessLabel;
	private JSplitPane rightSplit;
	private JPanel topPanel;
	private ACComboBox tabSwitchActionCombo;
	private JLabel tabSwitchActionLabel;
	private ACCheckBox inheritPropertiesCheckBox;
	private ACCheckBox hideGisPanelCheckBox;
	private ACTextField docURlTextField;
	private ACTextField passwordFormatTextField;
	private JLabel docUrlLabel;
	private JLabel passwordFormatLabel;
	private JTree settingsTree;
	private JComponent[] disableableComponents;
	private ACButton imageCacheRefreshButton;

	private SettingsView(){
	}

	public void initializeComponents(){
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		errorPanel = new ErrorPanel();
		add(errorPanel);

		JSplitPane splitPane = new JSplitPane();
		layout.putConstraint(SpringLayout.NORTH, splitPane, 0, SpringLayout.SOUTH, errorPanel);
		layout.putConstraint(SpringLayout.WEST, splitPane, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, splitPane, 0, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.SOUTH, splitPane, 0, SpringLayout.SOUTH, this);
		add(splitPane);

		JScrollPane treeScroll = new JScrollPane();
		userTree = new ACTree();
		userTree.setExpandsSelectedPaths(true);
		treeScroll.setViewportView(userTree);

		JPanel leftPanel = new JPanel();
		SpringLayout leftPanelLayout = new SpringLayout();
		leftPanel.setLayout(leftPanelLayout);

		leftPanelLayout.putConstraint(SpringLayout.WEST, treeScroll, 10, SpringLayout.WEST, leftPanel);
		leftPanelLayout.putConstraint(SpringLayout.NORTH, treeScroll, 10, SpringLayout.NORTH, leftPanel);
		leftPanelLayout.putConstraint(SpringLayout.SOUTH, treeScroll, -10, SpringLayout.SOUTH, leftPanel);
		leftPanelLayout.putConstraint(SpringLayout.EAST, treeScroll, -10, SpringLayout.EAST, leftPanel);

		splitPane.setLeftComponent(leftPanel);
		leftPanel.add(treeScroll);

		JPanel rightPanel = new JPanel();
		splitPane.setRightComponent(rightPanel);
		SpringLayout rightLayout = new SpringLayout();
		rightPanel.setLayout(rightLayout);

		toolBar = new ACToolBar();
		addButton = toolBar.makeAddButton();
		deleteButton = toolBar.makeDeleteButton();
		updateButton = toolBar.makeUpdateButton();
		refreshButton = toolBar.makeRefreshButton();
		imageCacheRefreshButton = toolBar.makeImageCacheRefreshButton();
		rightPanel.add(toolBar);
		rightLayout.putConstraint(SpringLayout.WEST, toolBar, 10, SpringLayout.WEST, rightPanel);

		topPanel = new JPanel();
		SpringLayout topLayout = new SpringLayout();
		topPanel.setLayout(topLayout);

		schemaLabel = new ACMandatoryLabel(Translation.get(TextID.Schema));
		languageLabel = new ACMandatoryLabel(Translation.get(TextID.Language));

		schemaCombo = new ACComboBox();
		schemaCombo.setRenderer(new SchemaListCellRenderer());
		languageCombo = new ACComboBox();

		tabSwitchActionLabel = new JLabel(Translation.get(TextID.TabSwitchAction));
		tabSwitchActionCombo = new ACComboBox();
		settingsTree = new JTree();
		settingsTree.setExpandsSelectedPaths(true);
		settingsTree.setCellRenderer(new CheckBoxTreeCellRenderer());
		settingsTree.setCellEditor(new CheckBoxTreeCellEditor(settingsTree));
		settingsTree.setEditable(true);
		final JScrollPane settingsTreeScrollPane = new JScrollPane(settingsTree);

		inheritPropertiesCheckBox = new ACCheckBox(Translation.get(TextID.InheritApplicationLevelProperties));
		inheritPropertiesCheckBox.setIconTextGap(10);
		hideGisPanelCheckBox = new ACCheckBox(Translation.get(TextID.HideGisPanel));
		hideGisPanelCheckBox.setHorizontalTextPosition(SwingConstants.LEADING);
		hideGisPanelCheckBox.setIconTextGap(10);

		updateAccessLabel = new JLabel(Translation.get(TextID.UpdateRestriction));
		deleteAccessLabel = new JLabel(Translation.get(TextID.DeleteRestriction));
		updateAccessCombo = new ACComboBox();
		deleteAccessCombo = new ACComboBox();
		docURlTextField = new ACTextField();
		docUrlLabel = new JLabel(Translation.get(TextID.HelpDocumentUrl));
		passwordFormatTextField = new ACTextField();
		passwordFormatLabel = new JLabel(Translation.get(TextID.PasswordComplexity));
		passwordFormatTextField.setToolTipText(Translation.get(TextID.PasswordComplexityTooltip));

		topPanel.add(settingsTreeScrollPane);
		topPanel.add(inheritPropertiesCheckBox);
		topPanel.add(hideGisPanelCheckBox);
		topPanel.add(schemaCombo);
		topPanel.add(languageCombo);
		topPanel.add(schemaLabel);
		topPanel.add(languageLabel);
		topPanel.add(tabSwitchActionLabel);
		topPanel.add(tabSwitchActionCombo);
		topPanel.add(updateAccessLabel);
		topPanel.add(updateAccessCombo);
		topPanel.add(deleteAccessLabel);
		topPanel.add(deleteAccessCombo);
		topPanel.add(docURlTextField);
		topPanel.add(docUrlLabel);
		topPanel.add(passwordFormatTextField);
		topPanel.add(passwordFormatLabel);

		topLayout.putConstraint(SpringLayout.WEST, settingsTreeScrollPane, 10, SpringLayout.EAST, schemaCombo);
		topLayout.putConstraint(SpringLayout.NORTH, settingsTreeScrollPane, 10, SpringLayout.NORTH, topPanel);
		topLayout.putConstraint(SpringLayout.SOUTH, settingsTreeScrollPane, 140, SpringLayout.NORTH, settingsTreeScrollPane);
		topLayout.putConstraint(SpringLayout.EAST, settingsTreeScrollPane, 0, SpringLayout.EAST, topPanel);

		topLayout.putConstraint(SpringLayout.WEST, inheritPropertiesCheckBox, 130, SpringLayout.WEST, topPanel);
		topLayout
		        .putConstraint(SpringLayout.NORTH, inheritPropertiesCheckBox, 0, SpringLayout.NORTH, settingsTreeScrollPane);

		topLayout.putConstraint(SpringLayout.NORTH, schemaCombo, 10, SpringLayout.SOUTH, inheritPropertiesCheckBox);
		topLayout.putConstraint(SpringLayout.WEST, schemaCombo, 135, SpringLayout.WEST, topPanel);
		topLayout.putConstraint(SpringLayout.EAST, schemaCombo, 200, SpringLayout.WEST, schemaCombo);
		topLayout.putConstraint(SpringLayout.EAST, schemaLabel, -10, SpringLayout.WEST, schemaCombo);
		topLayout.putConstraint(SpringLayout.NORTH, schemaLabel, 0, SpringLayout.NORTH, schemaCombo);

		topLayout.putConstraint(SpringLayout.NORTH, languageCombo, 40, SpringLayout.SOUTH, inheritPropertiesCheckBox);
		topLayout.putConstraint(SpringLayout.WEST, languageCombo, 0, SpringLayout.WEST, schemaCombo);
		topLayout.putConstraint(SpringLayout.EAST, languageCombo, 0, SpringLayout.EAST, schemaCombo);
		topLayout.putConstraint(SpringLayout.EAST, languageLabel, 0, SpringLayout.EAST, schemaLabel);
		topLayout.putConstraint(SpringLayout.NORTH, languageLabel, 0, SpringLayout.NORTH, languageCombo);

		topLayout.putConstraint(SpringLayout.NORTH, tabSwitchActionCombo, 100, SpringLayout.NORTH, topPanel);
		topLayout.putConstraint(SpringLayout.WEST, tabSwitchActionCombo, 0, SpringLayout.WEST, schemaCombo);
		topLayout.putConstraint(SpringLayout.EAST, tabSwitchActionCombo, 0, SpringLayout.EAST, schemaCombo);
		topLayout.putConstraint(SpringLayout.EAST, tabSwitchActionLabel, 0, SpringLayout.EAST, schemaLabel);
		topLayout.putConstraint(SpringLayout.NORTH, tabSwitchActionLabel, 2, SpringLayout.NORTH, tabSwitchActionCombo);

		topLayout.putConstraint(SpringLayout.NORTH, hideGisPanelCheckBox, 10, SpringLayout.SOUTH, tabSwitchActionCombo);
		topLayout.putConstraint(SpringLayout.EAST, hideGisPanelCheckBox, 20, SpringLayout.WEST, inheritPropertiesCheckBox);

		topLayout.putConstraint(SpringLayout.NORTH, updateAccessCombo, 10, SpringLayout.SOUTH, hideGisPanelCheckBox);
		topLayout.putConstraint(SpringLayout.WEST, updateAccessCombo, 0, SpringLayout.WEST, schemaCombo);
		topLayout.putConstraint(SpringLayout.EAST, updateAccessCombo, 0, SpringLayout.EAST, schemaCombo);
		topLayout.putConstraint(SpringLayout.EAST, updateAccessLabel, 0, SpringLayout.EAST, schemaLabel);
		topLayout.putConstraint(SpringLayout.NORTH, updateAccessLabel, 2, SpringLayout.NORTH, updateAccessCombo);

		topLayout.putConstraint(SpringLayout.NORTH, deleteAccessCombo, 0, SpringLayout.NORTH, updateAccessCombo);
		topLayout.putConstraint(SpringLayout.WEST, deleteAccessCombo, 135, SpringLayout.EAST, updateAccessCombo);
		topLayout.putConstraint(SpringLayout.EAST, deleteAccessCombo, 200, SpringLayout.WEST, deleteAccessCombo);
		topLayout.putConstraint(SpringLayout.EAST, deleteAccessLabel, -10, SpringLayout.WEST, deleteAccessCombo);
		topLayout.putConstraint(SpringLayout.NORTH, deleteAccessLabel, 2, SpringLayout.NORTH, deleteAccessCombo);

		topLayout.putConstraint(SpringLayout.NORTH, docURlTextField, 10, SpringLayout.SOUTH, deleteAccessCombo);
		topLayout.putConstraint(SpringLayout.WEST, docURlTextField, 0, SpringLayout.WEST, schemaCombo);
		topLayout.putConstraint(SpringLayout.EAST, docURlTextField, 0, SpringLayout.EAST, topPanel);
		topLayout.putConstraint(SpringLayout.EAST, docUrlLabel, 0, SpringLayout.EAST, schemaLabel);
		topLayout.putConstraint(SpringLayout.NORTH, docUrlLabel, 2, SpringLayout.NORTH, docURlTextField);

		topLayout.putConstraint(SpringLayout.NORTH, passwordFormatTextField, 10, SpringLayout.SOUTH, docURlTextField);
		topLayout.putConstraint(SpringLayout.WEST, passwordFormatTextField, 0, SpringLayout.WEST, schemaCombo);
		topLayout.putConstraint(SpringLayout.EAST, passwordFormatTextField, 0, SpringLayout.EAST, topPanel);
		topLayout.putConstraint(SpringLayout.EAST, passwordFormatLabel, 0, SpringLayout.EAST, schemaLabel);
		topLayout.putConstraint(SpringLayout.NORTH, passwordFormatLabel, 2, SpringLayout.NORTH, passwordFormatTextField);

		JScrollPane scrollPane = new JScrollPane();
		settingsTable = new ACTable();
		settingsTable.enableCopyPaste();
		settingsTable.enableToolTips();
		scrollPane.setViewportView(settingsTable);

		rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rightPanel.add(rightSplit);
		rightSplit.setBottomComponent(scrollPane);
		rightSplit.setTopComponent(topPanel);
		rightSplit.setBorder(BorderFactory.createEmptyBorder());

		rightLayout.putConstraint(SpringLayout.WEST, rightSplit, 10, SpringLayout.WEST, rightPanel);
		rightLayout.putConstraint(SpringLayout.NORTH, rightSplit, 0, SpringLayout.SOUTH, toolBar);
		rightLayout.putConstraint(SpringLayout.SOUTH, rightSplit, -10, SpringLayout.SOUTH, rightPanel);
		rightLayout.putConstraint(SpringLayout.EAST, rightSplit, -10, SpringLayout.EAST, rightPanel);

		userTree.setCellRenderer(new ACTreeCellRenderer());

		settingsTable.setDefaultRenderer(String.class, new AdvancedStringCellRenderer());
		splitPane.setDividerLocation((int) (ACMain.getScreenWidth() / 5));
		rightSplit.setDividerLocation(285);

		setUpdateRightsReferenceData();
		setDeleteRightsReferenceData();

		disableableComponents = new JComponent[] { settingsTree, schemaCombo, languageCombo, tabSwitchActionCombo,
		        updateAccessCombo, deleteAccessCombo, hideGisPanelCheckBox, docURlTextField };
		setTopPanelEnabled(false);
	}

	public void setInheritLabel(TextID id){
		inheritPropertiesCheckBox.setText(Translation.get(id));
	}

	public void setInheritPropertiesListener(ItemListener listener){
		ItemListener[] oldListeners = inheritPropertiesCheckBox.getItemListeners();
		for (ItemListener old : oldListeners)
			inheritPropertiesCheckBox.removeItemListener(old);
		inheritPropertiesCheckBox.readdOwnListener();
		inheritPropertiesCheckBox.addItemListener(listener);
	}

	public void setInheritPropertiesSelected(boolean b){
		inheritPropertiesCheckBox.setSelected(b);
	}

	public void setInheritPropertiesEnabled(boolean b){
		inheritPropertiesCheckBox.setVisible(b);
	}

	public void renderErrors(List<ErrorEntry> errors){
		List<String> errorMsgs = new ArrayList<String>();
		for (ErrorEntry error : errors)
			errorMsgs.add(Translation.get(error.getId(), error.getErrors()));
		errorPanel.setErrorMessages(errorMsgs);
	}

	public void setSchemaReferenceData(List<Schema> schemas){
		schemaCombo.removeAllItems();
		if (schemas == null)
			return;
		for (Schema schema : schemas){
			schemaCombo.addItem(schema);
		}
	}

	public void setLanguageReferenceData(List<Language> languages){
		languageCombo.removeAllItems();
		if (languages == null)
			return;
		for (Language language : languages){
			languageCombo.addItem(language);
		}
	}

	public void setUpdateRightsReferenceData(){
		UpdateAccessType[] rights = UpdateAccessType.values();
		updateAccessCombo.removeAllItems();
		if (rights == null)
			return;
		for (UpdateAccessType right : rights){
			updateAccessCombo.addItem(right);
		}
	}

	public void setDeleteRightsReferenceData(){
		DeleteAccessType[] rights = DeleteAccessType.values();
		deleteAccessCombo.removeAllItems();
		if (rights == null)
			return;
		for (DeleteAccessType right : rights){
			deleteAccessCombo.addItem(right);
		}
	}

	public void setTopPanelEnabled(boolean enabled){
		String tooltip = enabled ? null : get(ReadonlyCheckProperty, new String[] { inheritPropertiesCheckBox.getText() });
		for (JComponent c : disableableComponents){
			c.setEnabled(enabled);
			c.setToolTipText(tooltip);
		}
	}

	public void setSelectedLanguage(Language language){
		languageCombo.setInitialSelectedItem(language);
	}

	public void setDocumenatationUrlText(String url){
		docURlTextField.setText(url);
	}

	public String getDocumentationUrlText(){
		return docURlTextField.getText();
	}

	public void setPasswordFormatText(String text){
		passwordFormatTextField.setText(text);
	}

	public String getPasswordFormatText(){
		return passwordFormatTextField.getText();
	}

	public void setSelectedSchema(Schema schema){
		schemaCombo.setInitialSelectedItem(schema);
	}

	public void setUpdateAccess(UpdateAccessType type){
		updateAccessCombo.setInitialSelectedItem(type);
	}

	public void setDeleteAccess(DeleteAccessType type){
		deleteAccessCombo.setInitialSelectedItem(type);
	}

	public void setCurrentAction(TabSwitchAction tabSwitchAction){
		tabSwitchActionCombo.setInitialSelectedItem(tabSwitchAction);
	}

	public JTree getUserTree(){
		return userTree;
	}

	public JTree getSettingsTree(){
		return settingsTree;
	}

	public JTable getSettingsTable(){
		return settingsTable;
	}

	public JButton getAddButton(){
		return addButton;
	}

	public JButton getDeleteButton(){
		return deleteButton;
	}

	public JButton getRefreshButton(){
		return refreshButton;
	}

	public JButton getUpdateButton(){
		return updateButton;
	}

	public void clearErrors(){
		errorPanel.clearErrorMessage();
	}

	public void setActiveTableRow(Setting newSetting){
		int modelIndex = -1;
		SettingsTableModel model = (SettingsTableModel) settingsTable.getModel();
		modelIndex = model.indexOf((Setting) newSetting);

		if (modelIndex >= 0){
			settingsTable.setSelectedCellIndexes(new int[] { modelIndex, modelIndex, 0, 0 });

			int index = settingsTable.convertRowIndexToView(modelIndex);
			if (index >= 0){
				Rectangle r = settingsTable.getCellRect(index, 0, true);
				settingsTable.scrollRectToVisible(r);
			}
		}

		settingsTable.requestFocusInWindow();
	}

	public void stopCellEditing(){
		if (settingsTable.isEditing())
			settingsTable.getCellEditor().stopCellEditing();
	}

	public int getSelectedRowIndex(){
		if (settingsTable.getSelectedRow() >= 0){
			return settingsTable.convertRowIndexToModel(settingsTable.getSelectedRow());
		}
		return -1;
	}

	public Setting getSelectedSetting(){
		Setting setting = null;
		TableModel model = settingsTable.getModel();
		if (model instanceof SettingsTableModel){
			setting = ((SettingsTableModel) model).getSelected(getSelectedRowIndex());
		}
		return setting;
	}

	/**
	 * Use to regain focus when update / refresh button is pressed.
	 */
	public void setSelectedRow(int row){
		if (row != -1){
			int converted = settingsTable.convertRowIndexToView(row);
			settingsTable.getSelectionModel().setSelectionInterval(converted, converted);
		}
	}

	@Override
	public void resetEditedFields(){
		settingsTable.resetChanges();
		schemaCombo.resetChanges();
		languageCombo.resetChanges();
		tabSwitchActionCombo.resetChanges();
		inheritPropertiesCheckBox.resetChanges();
		updateAccessCombo.resetChanges();
		deleteAccessCombo.resetChanges();
		hideGisPanelCheckBox.resetChanges();
		docURlTextField.resetChanges();
		passwordFormatTextField.resetChanges();
	}

	public void refreshCurrentTable(){
		if (settingsTable.getModel() instanceof SettingsTableModel){
			((SettingsTableModel) settingsTable.getModel()).fireTableDataChanged();
		}
	}

	public ACComboBox getSchemaCombo(){
		return schemaCombo;
	}

	public ACComboBox getLanguageCombo(){
		return languageCombo;
	}

	public void addSchemaComboListener(SchemaComboListener lsn){
		schemaCombo.addActionListener(lsn);
	}

	public void addLanguageComboListener(LanguageComboListener lsn){
		languageCombo.addActionListener(lsn);
	}

	public void setTabSwitchActionComboListener(TabSwitchActionComboListener lsn){
		tabSwitchActionCombo.addActionListener(lsn);
	}

	public void addAccessComboListener(AccessComboListener lsn){
		updateAccessCombo.addActionListener(lsn);
		deleteAccessCombo.addActionListener(lsn);
	}

	@Override
	public boolean isChanged(){
		stopCellEditing();
		TreeModel tm = settingsTree.getModel();
		boolean changedTree = false;
		if (tm instanceof SettingsTreeModel){
			SettingsTreeModel stm = (SettingsTreeModel) tm;
			changedTree = stm.isChagned();
		}
		return settingsTable.isChanged() || schemaCombo.isChanged() || languageCombo.isChanged()
		        || tabSwitchActionCombo.isChanged() || inheritPropertiesCheckBox.isChanged()
		        || updateAccessCombo.isChanged() || deleteAccessCombo.isChanged() || changedTree
		        || hideGisPanelCheckBox.isChanged() || docURlTextField.isChanged() || passwordFormatTextField.isChanged();
	}

	public void setTabSwitchActions(TabSwitchAction[] tabSwitchActions){
		tabSwitchActionCombo.removeAllItems();
		for (int i = 0; i < tabSwitchActions.length; i++)
			tabSwitchActionCombo.addItem(tabSwitchActions[i]);
	}

	public ACComboBox getTabSwitchActionComboBox(){
		return tabSwitchActionCombo;
	}

	@Override
	public void restoreSelection(){
		ObjectHolder holder = ObjectHolder.getInstance();
		Object[] currentPath = holder.getMaxPath(new Class<?>[] { Group.class });
		if (currentPath != null){
			TreePath found = new TreeModelSupport().findPathByNodes(currentPath, getUserTree().getModel());
			getUserTree().setSelectionPath(found);
		}
	}

	public List<? extends SortKey> getTableSorting(){
		return settingsTable.getRowSorter().getSortKeys();
	}

	public void setTableSorting(List<? extends SortKey> sortKeys){
		settingsTable.getRowSorter().setSortKeys(sortKeys);
	}

	public void addHideGISPanelListener(HideGISPanelListener hideGISPanelListener){
		hideGisPanelCheckBox.addItemListener(hideGISPanelListener);
	}

	public void setHideGisPanelSelected(boolean b){
		hideGisPanelCheckBox.setSelected(b);
	}

	public void setPasswordFormatEnabled(boolean enabled){
		passwordFormatTextField.setEnabled(enabled);
	}

	public void addRefreshImageCacheButtonListener(ActionListener al){
		imageCacheRefreshButton.addActionListener(al);
	}

	public void setTableModel(SettingsTableModel tm){
		getSettingsTable().setModel(tm);
		getSettingsTable().setRowSorter(new TableRowSorter<SettingsTableModel>(tm));
		TableColumn column = settingsTable.getColumnModel().getColumn(SettingsTableModel.VALUE_COLUMN_INDEX);
		column.setCellEditor(new SettingsValueCellEditor());
		column.setCellRenderer(new SettingsValueCellRenderer());
	}

}
