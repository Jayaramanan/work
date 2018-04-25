/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ACToolBar extends JToolBar{

	private static final long serialVersionUID = 1L;

	private final static String ADD_ICON = "/images/Add.png";
	private final static String DELETE_ICON = "/images/Delete.png";
	private final static String SAVE_ICON = "/images/Save.png";
	private final static String REFRESH_ICON = "/images/Sync.png";
	private final static String COPY_ICON = "/images/Copy.png";
	private final static String ADD_LANGUAGE_ICON = "/images/Add_language.png";
	private final static String DELETE_LANGUAGE_ICON = "/images/Delete_language.png";
	private final static String ADD_GROUP_ICON = "/images/Add_group.png";
	private final static String DELETE_GROUP_ICON = "/images/Delete_group.png";
	private final static String ADD_METAPHOR_SET_ICON = "/images/Add_metaphor_set.png";
	private final static String DELETE_METAPHOR_SET_ICON = "/images/Delete_metaphor_set.png";
	private final static String COPY_METAPHOR_SET_ICON = "/images/Copy_metaphor_set.png";
	private final static String ADD_ICON_ICON = "/images/Add_icon.png";
	private final static String ADD_SCHEMA_ICON = "/images/Add_schema.png";
	private final static String ADD_OBJECT_ICON = "/images/Add_object.png";
	private final static String COPY_SCHEMA_ICON = "/images/Copy_schema.png";
	private final static String DELETE_SCHEMA_OBJECT_ICON = "/images/Delete_schema.png";
	private final static String DELETE_CASCADE_ICON = "/images/Delete_cascade.png";
	private final static String UPDATE_CASCADE_ICON = "/images/Update_cascade.png";
	private final static String DELETE_ICON_ICON = "/images/Delete_icon.png";
	private final static String ADD_CHART_ICON = "/images/Add_chart.png";
	private final static String DELETE_CHART_ICON = "/images/Delete_chart.png";
	private final static String CALCULATE_CHART_ICON = "/images/Calculate_chart.png";
	private final static String CONNECT_ICON = "/images/Connect.png";
	private final static String DISCONNECT_ICON = "/images/Disconnect.png";
	private final static String EXPORT_ICON = "/images/Export.png";
	private final static String XLS_SCHEMA_EXPORT_ICON = "/images/XLS_schema_export.png";
	private final static String XLS_DATA_EXPORT_ICON = "/images/XLS_data_export.png";
	private final static String CSV_DATA_EXPORT_ICON = "/images/CSV_data_export.png";
	private final static String IMPORT_ICON = "/images/Import.png";
	private final static String XML_SCHEMA_EXPORT_ICON = "/images/XML_schema_export.png";
	private final static String XML_IMPORT_ICON = "/images/XML_import.png";
	private final static String XLS_SCHEMA_IMPORT_ICON = "/images/XLS_schema_import.png";
	private final static String SALESFORCE_SCHEMA_IMPORT_ICON = "/images/Salesforce_schema_import.png";
	private static final String XLS_DATA_IMPORT_ICON = "/images/XLS_data_import.png";
	private static final String CSV_DATA_IMPORT_ICON = "/images/CSV_data_import.png";
	private final static String DIAGNOSE_ICON = "/images/Diagnose.png";
	private final static String UPDATE_CACHE_ICON = "/images/Update_cache.png";
	private final static String ADD_DATASOURCE_ICON = "/images/Add_datasource.png";
	private final static String GRANT_PRIVILEGES_ICON = "/images/Grant_privileges.png";
	private final static String REMOVE_PRIVILEGES_ICON = "/images/Remove_privileges.png";
	private final static String LAUNCH_NOW_ICON = "/images/Play.png";
	private final static String CALCULATE_CHART_NOW_ICON = "/images/Calculate_chart_now.png";
	private final static String DELETE_DATASOURCE_ICON = "/images/Delete_datasource.png";
	private static final String RESET_PASSWORD_ICON = "/images/ResetPassword.png";
	private static final String UPLOAD_ICON = "/images/upload.png";
	private static final String DOWNLOAD_ICON = "/images/download.png";
	private final static String EXPAND_TREE_ICON = "/images/Expand_tree.png";
	private final static String SEARCH_ICON = "/images/Search.png";
	private final static String XLS_REPORT_ICON = "/images/XLS_report.png";
	private final static String PDF_REPORT_ICON = "/images/PDF_report.png";
	private final static String HTML_REPORT_ICON = "/images/HTML_report.png";
	private final static String GRADIENT_ICON = "/images/gradient.png";
	private final static String SEND_ICON = "/images/SendStarterSSO.png";
	private static final String GENERATE_CONNECTION_TYPES_ICON = "/images/generate_edges.png";
	private static final String IMAGE_REFRESH_ICON = "/images/refresh_images.png";

	private static Logger log = Logger.getLogger(ACToolBar.class);

	public ACToolBar(){
		setOrientation(HORIZONTAL);
		setFloatable(false);
		setRollover(true);
	}

	public ACButton makeAddButton(){
		String text = Translation.get(TextID.Add);
		return makeButton(ADD_ICON, text, Mnemonic.AltA);
	}

	public ACButton makeAddButton2(){
		String text = Translation.get(TextID.Add);
		return makeButton(ADD_ICON, text, Mnemonic.AltN);
	}

	public ACButton makeAddDatasourceButton(){
		String text = Translation.get(TextID.AddDatasourceToTree);
		return makeButton(ADD_DATASOURCE_ICON, text, Mnemonic.AltQ);
	}

	public ACButton makeDeleteDatasourceButton(){
		String text = Translation.get(TextID.RemoveDatasourceFromTree);
		return makeButton(DELETE_DATASOURCE_ICON, text, Mnemonic.AltF);
	}

	public ACButton makeDeleteButton(){
		String text = Translation.get(TextID.Delete);
		return makeButton(DELETE_ICON, text, Mnemonic.AltD);
	}

	public ACButton makeDeleteButton2(){
		String text = Translation.get(TextID.Delete);
		return makeButton(DELETE_ICON, text, Mnemonic.AltF);
	}

	public ACButton makeUpdateButton(){
		String text = Translation.get(TextID.Update);
		return makeButton(SAVE_ICON, text, Mnemonic.AltU);
	}

	public ACButton makeUpdateButton2(){
		String text = Translation.get(TextID.Update);
		return makeButton(SAVE_ICON, text, Mnemonic.AltS);
	}

	public ACButton makeRefreshButton(){
		String text = Translation.get(TextID.Refresh);
		return makeButton(REFRESH_ICON, text, Mnemonic.AltR);
	}

	public ACButton makeRefreshButton2(){
		String text = Translation.get(TextID.Refresh);
		return makeButton(REFRESH_ICON, text, Mnemonic.AltC);
	}

	public ACButton makeAddMapButton(){
		String text = Translation.get(TextID.Add);
		return makeButton(ADD_ICON, text, Mnemonic.AltM);
	}

	public ACButton makeDeleteMapButton(){
		String text = Translation.get(TextID.Delete);
		return makeButton(DELETE_ICON, text, Mnemonic.AltE);
	}

	public ACButton makeCopyButton(){
		String text = Translation.get(TextID.Copy);
		return makeButton(COPY_ICON, text, Mnemonic.AltC);
	}

	public ACButton makeDeleteLanguageButton(){
		String text = Translation.get(TextID.DeleteLanguage);
		return makeButton(DELETE_LANGUAGE_ICON, text, Mnemonic.AltE);
	}

	public ACButton makeAddLanguageButton(){
		String text = Translation.get(TextID.AddLanguage);
		return makeButton(ADD_LANGUAGE_ICON, text, Mnemonic.AltN);
	}

	public ACButton makeDeleteGroupButton(){
		String text = Translation.get(TextID.DeleteGroup);
		return makeButton(DELETE_GROUP_ICON, text, Mnemonic.AltE);
	}

	public ACButton makeAddGroupButton(){
		String text = Translation.get(TextID.AddGroup);
		return makeButton(ADD_GROUP_ICON, text, Mnemonic.AltG);
	}

	public ACButton makeCopyGroupButton(){
		String text = Translation.get(TextID.CopyGroup);
		return makeButton(COPY_ICON, text, Mnemonic.AltC);
	}

	public ACButton makeDeleteChartButton(){
		String text = Translation.get(TextID.DeleteChart);
		return makeButton(DELETE_CHART_ICON, text, Mnemonic.AltE);
	}

	public ACButton makeCalculateChartButton(){
		String text = Translation.get(TextID.ScheduleChartCalculation);
		return makeButton(CALCULATE_CHART_ICON, text, Mnemonic.AltC);
	}

	public ACButton makeCalculateChartNowButton(){
		String text = Translation.get(TextID.CalculateChartNow);
		return makeButton(CALCULATE_CHART_NOW_ICON, text, Mnemonic.AltH);
	}

	public ACButton makeAddChartButton(){
		String text = Translation.get(TextID.AddChart);
		return makeButton(ADD_CHART_ICON, text, Mnemonic.AltG);
	}

	public ACButton makeAddMetaphorSetButton(){
		String text = Translation.get(TextID.AddMetaphorSet);
		return makeButton(ADD_METAPHOR_SET_ICON, text, Mnemonic.AltS);
	}

	public ACButton makeDeleteMetaphorSetButton(){
		String text = Translation.get(TextID.DeleteMetaphorSet);
		return makeButton(DELETE_METAPHOR_SET_ICON, text, Mnemonic.AltE);
	}

	public ACButton makeCopyMetaphorSetButton(){
		String text = Translation.get(TextID.CopyMetaphorSet);
		return makeButton(COPY_METAPHOR_SET_ICON, text, Mnemonic.AltC);
	}

	public ACButton makeAddIconButton(){
		String text = Translation.get(TextID.AddIcon);
		return makeButton(ADD_ICON_ICON, text, Mnemonic.AltI);
	}

	public ACButton makeDeleteIconButton(){
		String text = Translation.get(TextID.DeleteIcon);
		return makeButton(DELETE_ICON_ICON, text, Mnemonic.AltL);
	}

	public ACButton makeAddSchemaButton(){
		String text = Translation.get(TextID.AddSchema);
		return makeButton(ADD_SCHEMA_ICON, text, Mnemonic.AltS);
	}

	public ACButton makeAddObjectButton(){
		String text = Translation.get(TextID.AddObject);
		return makeButton(ADD_OBJECT_ICON, text, Mnemonic.AltO);
	}

	public ACButton makeCopySchemaButton(){
		String text = Translation.get(TextID.Copy);
		return makeButton(COPY_SCHEMA_ICON, text, Mnemonic.AltC);
	}

	public ACButton makeDeleteSchemaObjectButton(){
		String text = Translation.get(TextID.Delete);
		return makeButton(DELETE_SCHEMA_OBJECT_ICON, text, null);
	}

	public ACButton makeXMLImportButton(){
		String text = Translation.get(TextID.XMLImport);
		return makeButton(XML_IMPORT_ICON, text, Mnemonic.AltX);
	}

	public ACButton makeConnectButton(){
		String text = Translation.get(TextID.Connect);
		return makeButton(CONNECT_ICON, text, Mnemonic.AltN);
	}

	public ACButton makeDisconnectButton(){
		String text = Translation.get(TextID.Disconnect);
		return makeButton(DISCONNECT_ICON, text, Mnemonic.AltT);
	}

	public ACButton makeGrantPrivilegesButton(){
		String text = Translation.get(TextID.GrantAllPrivileges);
		ACButton button = makeButton(GRANT_PRIVILEGES_ICON, text, Mnemonic.AltA);
		button.setActionCommand(TextID.GrantAllPrivileges.toString());
		return button;
	}

	public ACButton makeExpandTreeButton(){
		String text = Translation.get(TextID.ExpandTree);
		ACButton button = makeButton(EXPAND_TREE_ICON, text, Mnemonic.AltX);
		button.setActionCommand(TextID.ExpandTree.toString());
		return button;
	}

	public ACButton makeRemovePrivilegesButton(){
		String text = Translation.get(TextID.RemoveAllPrivileges);
		ACButton button = makeButton(REMOVE_PRIVILEGES_ICON, text, Mnemonic.AltD);
		button.setActionCommand(TextID.RemoveAllPrivileges.toString());
		return button;
	}

	public ACSplitButton makeDeleteSplitButton(){
		ACSplitButton button = makeSplitButton(DELETE_ICON, TextID.Delete, Mnemonic.AltD, true);

		String cascadeText = Translation.get(TextID.CascadeDelete);
		URL imageURL = ACMain.class.getResource(DELETE_CASCADE_ICON);
		ImageIcon cascadeDeleteIcon = new ImageIcon(imageURL);
		button.add(cascadeDeleteIcon, cascadeText, TextID.CascadeDelete.toString());

		return button;
	}

	public ACButton makeSchemaExportButton(){
		String text = Translation.get(TextID.ExportSchema);
		return makeButton(EXPORT_ICON, text, Mnemonic.AltE);
	}

	public ACButton makeDiagnosticButton(){
		String text = Translation.get(TextID.Diagnose);
		return makeButton(DIAGNOSE_ICON, text, Mnemonic.AltD);
	}

	public ACButton makeUpdateCacheButton(){
		String text = Translation.get(TextID.InvalidateCache);
		return makeButton(UPDATE_CACHE_ICON, text, Mnemonic.AltP);
	}

	public ACButton makePreviewButton(){
		String text = Translation.get(TextID.PreviewExport);
		return makeButton(DIAGNOSE_ICON, text, Mnemonic.AltP);
	}

	public ACButton makeMapDirViewButton(){
		String text = Translation.get(TextID.ViewMapDir);
		return makeButton(DIAGNOSE_ICON, text, Mnemonic.AltP);
	}

	public ACButton makeLaunchNowButton(){
		String text = Translation.get(TextID.LaunchNow);
		return makeButton(LAUNCH_NOW_ICON, text, Mnemonic.AltL);
	}
	
	public ACButton makeRecalculateButton(){
		String text = Translation.get(TextID.RecalculateValues);
		return makeButton(LAUNCH_NOW_ICON, text, Mnemonic.AltL);
	}

	public ACButton makeSearchActivitiesButton(){
		String text = Translation.get(TextID.SearchActivities);
		return makeButton(SEARCH_ICON, text, Mnemonic.AltF);
	}

	public ACSplitButton makeUpdateSplitButton(){
		ACSplitButton button = makeSplitButton(SAVE_ICON, TextID.Update, Mnemonic.AltU, true);
		button.setName("ACToolBar_" + Mnemonic.AltU.name());

		String cascadeText = Translation.get(TextID.UpdateLiveData);
		URL imageURL = ACMain.class.getResource(UPDATE_CASCADE_ICON);
		ImageIcon cascadeDeleteIcon = new ImageIcon(imageURL);
		button.add(cascadeDeleteIcon, cascadeText, TextID.UpdateLiveData.toString());

		return button;
	}

	public ACSplitButton makeAttributeUpdateSplitButton(){
		ACSplitButton button = makeSplitButton(SAVE_ICON, TextID.Update, Mnemonic.AltU, true);
		button.setName("ACToolBar_" + Mnemonic.AltU.name());

		String cascadeText = Translation.get(TextID.UpdateLiveData);
		URL imageURL = ACMain.class.getResource(UPDATE_CASCADE_ICON);
		ImageIcon cascadeDeleteIcon = new ImageIcon(imageURL);
		button.add(cascadeDeleteIcon, cascadeText, TextID.UpdateLiveData.toString());

		return button;
	}

	public ACSplitButton makeExportSplitButton(){
		ACSplitButton button = makeSplitButton(EXPORT_ICON, TextID.Export, Mnemonic.AltE, false);

		String xmlSchemaExportText = Translation.get(TextID.XMLSchemaExport);
		URL xmlExportIconUrl = ACMain.class.getResource(XML_SCHEMA_EXPORT_ICON);
		ImageIcon xmlExportIcon = new ImageIcon(xmlExportIconUrl);
		button.add(xmlExportIcon, xmlSchemaExportText, TextID.XMLSchemaExport.toString());

		String text = Translation.get(TextID.XLSDataExport);
		URL url = ACMain.class.getResource(XLS_DATA_EXPORT_ICON);
		ImageIcon icon = new ImageIcon(url);
		button.add(icon, text, TextID.XLSDataExport.toString());

		String exportCSVText = Translation.get(TextID.CSVDataExport);
		URL imageCSV = ACMain.class.getResource(CSV_DATA_EXPORT_ICON);
		ImageIcon exportCSVIcon = new ImageIcon(imageCSV);
		button.add(exportCSVIcon, exportCSVText, TextID.CSVDataExport.toString());

		String exportXLSText = Translation.get(TextID.XLSSchemaExport);
		URL imageURL = ACMain.class.getResource(XLS_SCHEMA_EXPORT_ICON);
		ImageIcon exportXLSIcon = new ImageIcon(imageURL);
		button.add(exportXLSIcon, exportXLSText, TextID.XLSSchemaExport.toString());

		return button;
	}

	public ACSplitButton makeImportSplitButton(){
		ACSplitButton button = makeSplitButton(IMPORT_ICON, TextID.Import, Mnemonic.AltI, false);

		String importXMLText = Translation.get(TextID.XMLImport);
		URL xmlURL = ACMain.class.getResource(XML_IMPORT_ICON);
		ImageIcon importXMLIcon = new ImageIcon(xmlURL);
		button.add(importXMLIcon, importXMLText, TextID.XMLImport.toString());

		String importXLSText = Translation.get(TextID.XLSDataImport);
		URL xlsURL = ACMain.class.getResource(XLS_DATA_IMPORT_ICON);
		ImageIcon importXLSIcon = new ImageIcon(xlsURL);
		button.add(importXLSIcon, importXLSText, TextID.XLSDataImport.toString());

		String importCSVText = Translation.get(TextID.CSVDataImport);
		URL csvURL = ACMain.class.getResource(CSV_DATA_IMPORT_ICON);
		ImageIcon importCSVIcon = new ImageIcon(csvURL);
		button.add(importCSVIcon, importCSVText, TextID.CSVDataImport.toString());

		String importXLSSchemaText = Translation.get(TextID.XLSSchemaImport);
		URL xlsSchemaURL = ACMain.class.getResource(XLS_SCHEMA_IMPORT_ICON);
		ImageIcon importXLSSchemaIcon = new ImageIcon(xlsSchemaURL);
		button.add(importXLSSchemaIcon, importXLSSchemaText, TextID.XLSSchemaImport.toString());

		String importSfSchemaText = Translation.get(TextID.SalesforceSchemaImport);
		URL sfSchemaURL = ACMain.class.getResource(SALESFORCE_SCHEMA_IMPORT_ICON);
		ImageIcon importSfSchemaIcon = new ImageIcon(sfSchemaURL);
		button.add(importSfSchemaIcon, importSfSchemaText, TextID.SalesforceSchemaImport.toString());

		return button;
	}

	public ACButton makeResetPasswordButton(){
		String text = Translation.get(TextID.ResetPassword);
		return makeButton(RESET_PASSWORD_ICON, text, Mnemonic.AltS);
	}

	private ACButton makeButton(String iconName, String text, Mnemonic mnemonic){
		URL imageURL = ACMain.class.getResource(iconName);

		ACButton button = new ACButton();
		if (mnemonic != null){
			button.setHotKey(mnemonic);
		}
		button.setToolTipText(text);
		button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		if (imageURL != null){
			button.setIcon(new ImageIcon(imageURL, null));
		} else{
			button.setText(text);
			log.warn("Icon not found: " + iconName);
		}
		if (mnemonic != null)
			button.setName("ACToolBar_" + mnemonic.name());

		add(button);

		return button;
	}

	private ACSplitButton makeSplitButton(String iconName, TextID textID, Mnemonic mnemonic, boolean withDefaultAction){
		String text = Translation.get(textID);

		URL imageURL = ACMain.class.getResource(iconName);
		Icon icon = null;
		if (imageURL != null){
			icon = new ImageIcon(imageURL, text);
		} else{
			log.warn("Icon not found: " + iconName);
		}
		if (mnemonic != null)
			text += " ( " + mnemonic.toString() + " ) ";
		ACSplitButton button = new ACSplitButton(mnemonic, withDefaultAction);
		button.setToolTipText(text);
		button.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		button.setIcon(icon);

		add(button);

		if (withDefaultAction){
			button.add(icon, text, textID.toString());
		}

		return button;
	}

	public ACButton makeUploadButton(){
		String text = Translation.get(TextID.Upload);
		return makeButton(UPLOAD_ICON, text, Mnemonic.AltP);
	}

	public ACButton makeDownloadButton(){
		String text = Translation.get(TextID.Download);
		return makeButton(DOWNLOAD_ICON, text, Mnemonic.AltL);
	}

	public ACButton makeGenerateDBPropertiesButton(){
		String text = Translation.get(TextID.GenerateDatabaseProperties);
		return makeButton(DIAGNOSE_ICON, text, Mnemonic.AltG);
	}

	public ACButton makeSendButton(){
		String text = Translation.get(TextID.Send);
		return makeButton(RESET_PASSWORD_ICON, text, Mnemonic.AltM);
	}

	public ACButton makeXLSReportButton(){
		String text = Translation.get(TextID.XLSExport);
		ACButton button = makeButton(XLS_REPORT_ICON, text, Mnemonic.AltX);
		button.setActionCommand(TextID.XLSExport.toString());
		return button;
	}

	public ACButton makePDFReportButton(){
		String text = Translation.get(TextID.PDFExport);
		ACButton button = makeButton(PDF_REPORT_ICON, text, Mnemonic.AltP);;
		button.setActionCommand(TextID.PDFExport.toString());
		return button;
	}

	public ACButton makeHTMLReportButton(){
		String text = Translation.get(TextID.HTMLExport);
		ACButton button = makeButton(HTML_REPORT_ICON, text, Mnemonic.AltH);
		button.setActionCommand(TextID.HTMLExport.toString());
		return button;
	}

	public void switchButtons(ACButton remove, ACButton add){
		int index = this.getComponentIndex(remove);
		if (index != -1){
			this.remove(remove);
			this.add(add, index);
		}
	}

	public ACButton makeGradientButton(){
		String text = Translation.get(TextID.Gradient);
		return makeButton(GRADIENT_ICON, text, Mnemonic.AltG);
	}

	public ACButton makeSendSSOButton(){
		String text = Translation.get(TextID.Send);
		return makeButton(SEND_ICON, text, Mnemonic.AltO);
	}

	public ACButton makeGenerateConnectionTypesButton(){
		String text = Translation.get(TextID.GenerateConnectionTypes);
		return makeButton(GENERATE_CONNECTION_TYPES_ICON, text, Mnemonic.AltG);
	}

	public ACButton makeImageCacheRefreshButton(){
		String text = Translation.get(TextID.ImageCacheRefresh);
		return makeButton(IMAGE_REFRESH_ICON, text, Mnemonic.AltR);
	}
}
