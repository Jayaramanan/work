/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.controller;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.*;

import com.ni3.ag.navigator.client.domain.SyncModule;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.ImageSynchronizationDialog;
import com.ni3.ag.navigator.client.gui.SynchronizationProgressDialog;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import org.apache.log4j.Logger;

import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.client.gateway.SyncGateway;
import com.ni3.ag.navigator.client.gateway.impl.HttpSyncGatewayImpl;
import com.ni3.ag.navigator.shared.proto.NResponse.SyncResult;

public class OfflineSynchronizer{
	private static final Logger log = Logger.getLogger(OfflineSynchronizer.class);
	public static final String SYNCHRONIZE_IMAGES = "SyncImages";
	public static final String SYNCHRONIZE_OUT = "SyncOut";
	public static final String SYNCHRONIZE_IN = "SyncIn";
	public static final String SYNCHRONIZE_BOTH = "SyncBoth";

	private SynchronizationProgressDialog dlg;
	private SyncModule currentModule;
	private volatile boolean stopSync;

	public OfflineSynchronizer(){
		dlg = new SynchronizationProgressDialog(new AbstractAction(UserSettings.getWord("Stop")){
			@Override
			public void actionPerformed(ActionEvent e){
				dlg.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				JButton jb = (JButton) e.getSource();
				jb.setText(jb.getText() + "...");
				jb.setEnabled(false);
				stopSync = true;
			}
		});
	}

	public void callSync(final boolean toMaster, final boolean fromMaster){
		stopSync = false;
		new Thread(new Runnable(){
			@Override
			public void run(){

				log.debug("Start sync");
				currentModule = createModule(UserSettings.getWord("Checking connectivity"),
				        SynchronizationProgressDialog.IN_PROGRESS_STATUS);
				dlg.addModuleProgress(currentModule);
				if (!checkConnectivity()){
					updateModule(SynchronizationProgressDialog.ERROR_STATUS);
					return;
				}
				updateModule(SynchronizationProgressDialog.OK_STATUS);
				dlg.repaintList();
				log.debug("Connectivity checked");

				dlg.setStopButtonEnabled(true);
				log.debug("Stop enabled");
				if (toMaster)
					callSync(true);

				log.debug("To master called");

				if (fromMaster)
					callSync(false);

				log.debug("From master called");

				dlg.setStopButtonEnabled(false);

				log.debug("Stop diabled");

				logout();

				dlg.setOkButtonEnabled(true);
				dlg.resetStopButtonLabel();
				dlg.setCursor(null);

				SystemGlobals.MainFrame.favoritesMenu.refreshFavorites();
				log.debug("End sync");
			}
		}).start();
		dlg.showDialog();
	}

	private void callSync(boolean toMaster){
		if (toMaster)
			dlg.addString(UserSettings.getWord("Sending changes"));
		else
			dlg.addString(UserSettings.getWord("Receiving changes"));
		SyncGateway syncGateway = new HttpSyncGatewayImpl();
		Integer count;

		if (toMaster)
			count = syncGateway.getMyChangesCount();
		else
			count = syncGateway.getMasterChangesCount();

		if (count == null)
			currentModule = createModule(UserSettings.getWord("Objects"), 0, SynchronizationProgressDialog.ERROR_STATUS);
		else
			currentModule = createModule(UserSettings.getWord("Objects"), count,
			        SynchronizationProgressDialog.IN_PROGRESS_STATUS);
		dlg.addModuleProgress(currentModule);
		int ok = 0, warn = 0, er = 0;
		while (count != null){
			if (count == 0){
				updateModule(SynchronizationProgressDialog.OK_STATUS);
				dlg.repaintList();
				break;
			}
			SyncResult sr;

			if (toMaster)
				sr = syncGateway.sendChangesToMaster();
			else
				sr = syncGateway.getChangesFromMaster();

			if (sr == null || !SyncResult.Status.OK.equals(sr.getStatus())){
				if (sr != null){
					ok += sr.getOkCount();
					warn += sr.getWarnCount();
					er += sr.getErrorCount();
					currentModule.setProcessedRecords(ok, warn, er);
					updateModule(SynchronizationProgressDialog.ERROR_STATUS);
				} else
					updateModule(SynchronizationProgressDialog.ERROR_STATUS);
				dlg.repaintList();
				break;
			}
			ok += sr.getOkCount();
			warn += sr.getWarnCount();
			er += sr.getErrorCount();
			currentModule.setProcessedRecords(ok, warn, er);

			updateModule(SynchronizationProgressDialog.IN_PROGRESS_STATUS);
			dlg.repaintList();
			if (!sr.getHasMoreToProcess() || stopSync){
				updateModule(SynchronizationProgressDialog.OK_STATUS);
				dlg.repaintList();
				break;
			}
		}
	}

	public boolean checkConnectivity(){
		SyncGateway syncGateway = new HttpSyncGatewayImpl();
		boolean connectionError = false;
		boolean lockError = false;
		try{
            ByteString payload = syncGateway.checkConnectivity();
            SyncResult sResult = SyncResult.parseFrom(payload);
            if (SyncResult.Status.CONNECTION_ERROR.equals(sResult.getStatus()))
                connectionError = true;
            else if (SyncResult.Status.LOCK_ERROR.equals(sResult.getStatus()))
                lockError = true;
        } catch (IOException ex){
			log.error("Error check connectivity to master", ex);
			connectionError = true;
		}

		if (connectionError){
			dlg.addErrorString(UserSettings.getWord("MsgErrorConnectingToMasterServer"));
			log.error("Error connecting to Master server");
		} else if (lockError){

			dlg.addErrorString(UserSettings.getWord("MsgMasterDatabaseLocked"));
			log.error("Master database is locked");
		}

		dlg.repaintList();
		return !connectionError && !lockError;
	}

	private void logout(){
		SyncGateway syncGateway = new HttpSyncGatewayImpl();
		log.debug("Logout");
		syncGateway.logoutMaster();
	}

	private SyncModule createModule(String moduleName, int totalRecords, int status){
		return new SyncModule(moduleName, totalRecords, status);
	}

	private SyncModule createModule(String moduleName, int status){
		return new SyncModule(moduleName, -1, status);
	}

	private void updateModule(int status){
		if (currentModule != null){
			currentModule.setStatus(status);
		}
	}

	public void callSyncImages(){
		final ImageSynchronizationDialog dlg = new ImageSynchronizationDialog();
		dlg.showDialog();
		log.info("start sync images");
		SyncGateway syncGateway = new HttpSyncGatewayImpl();
		int err;
		try{
			URL metaphors = new URL(SystemGlobals.MetaphorURL);
			SyncResult sr = syncGateway.callImageSync(metaphors.getPath());
			if (sr != null && SyncResult.Status.OK.equals(sr.getStatus())){
				err = sr.getErrorCount();
				dlg.updateState(ImageSynchronizationDialog.STATUS_DONE, err);
			} else
				dlg.setErrorState();
		} catch (MalformedURLException e){
			log.error("Error sync images", e);
			dlg.setErrorState();
		}
	}
}
