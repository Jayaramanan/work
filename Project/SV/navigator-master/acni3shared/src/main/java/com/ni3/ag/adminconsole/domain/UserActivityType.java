/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import com.ni3.ag.adminconsole.shared.language.TextID;

public enum UserActivityType{
	SIDLogin(TextID.ActivitySIDLogin), PasswordLogin(TextID.ActivityPasswordLogin), SSOLogin(TextID.ActivitySSOLogin), Synchronization(
			TextID.ActivitySynchronization), Logout(TextID.ActivityLogout), ChangePassword(TextID.ActivityChangePassword), ResetPassword(
			TextID.ActivityResetPassword), CreateNode(TextID.ActivityCreateNode), CreateEdge(TextID.ActivityCreateEdge), UpdateNode(
			TextID.ActivityUpdateNode), UpdateEdge(TextID.ActivityUpdateEdge), MergeNode(TextID.ActivityMergeNode), UpdateNodeMetaphor(
			TextID.ActivityUpdateNodeMetaphor), DeleteNode(TextID.ActivityDeleteNode), DeleteEdge(TextID.ActivityDeleteEdge), CreateFavorite(
			TextID.ActivityCreateFavorite), InvokeFavorite(TextID.ActivityInvokeFavorite), DeleteFavorite(
			TextID.ActivityDeleteFavorite), UpdateFavorite(TextID.ActivityUpdateFavorite), CopyFavorite(
			TextID.ActivityCopyFavorite), CreateFolder(TextID.ActivityCreateFolder), UpdateFolder(
			TextID.ActivityUpdateFolder), DeleteFolder(TextID.ActivityDeleteFolder), SimpleSearch(
			TextID.ActivitySimpleSearch), AdvancedSearch(TextID.ActivityAdvancedSearch), GeoSearch(TextID.ActivityGeoSearch), InvokeChart(
			TextID.ActivityInvokeChart), ExportData(TextID.ActivityExportData), NotALog(TextID.NotALogActivity), OfflineGetModule(
			TextID.ActivityGetModule);

	private TextID type;
	private String str;

	UserActivityType(TextID type){
		this.type = type;
		this.str = type.name();
	}

	public TextID getValue(){
		return type;
	}

	public String getValueText(){
		return type.getKey();
	}

	public static UserActivityType getActivityType(TextID val){
		for (UserActivityType type : values()){
			if (type.getValue().equals(val)){
				return type;
			}
		}
		return null;
	}

	public static UserActivityType getActivityType(String val){
		for (UserActivityType type : values()){
			if (type.getValue().name().equals(val)){
				return type;
			}
		}
		return null;
	}

	public void setString(String str){
		this.str = str;
	}

	public String toString(){
		return str;
	}
}
