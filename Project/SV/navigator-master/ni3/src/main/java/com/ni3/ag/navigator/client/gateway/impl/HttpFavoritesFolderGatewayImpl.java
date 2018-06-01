/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gateway.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.client.domain.Folder;
import com.ni3.ag.navigator.client.domain.cache.IconCache;
import com.ni3.ag.navigator.client.gateway.FavoritesFolderGateway;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;

public class HttpFavoritesFolderGatewayImpl extends AbstractGatewayImpl implements FavoritesFolderGateway{

	@Override
	public int createFolder(Folder folder){
		NRequest.FavoritesFolderManagement.Builder request = NRequest.FavoritesFolderManagement.newBuilder();
		request.setAction(NRequest.FavoritesFolderManagement.Action.CREATE);
		NRequest.FavoritesFolder.Builder protoFolder = NRequest.FavoritesFolder.newBuilder();
		protoFolder.setSchemaId(folder.getSchemaID());
		protoFolder.setName(folder.getName());
		protoFolder.setSort(folder.getSort());
		protoFolder.setParentFolderId(folder.getParentFolderID() > 0 ? folder.getParentFolderID() : 0);
		protoFolder.setGroupFolder(folder.isGroupFolder());
		request.setFolder(protoFolder);
		int result = -1;
		try{
			ByteString payload = sendRequest(ServletName.FavoritesFolderManagementServlet, request.build());
			NResponse.Folder resultFolder = NResponse.Folder.parseFrom(payload);
			result = resultFolder.getId();
		} catch (IOException ex){
			showErrorAndThrow("Error get favorite folder list: " + ex.getMessage(), ex);
		}
		return result;
	}

	@Override
	public void updateFolder(Folder folder){
		NRequest.FavoritesFolderManagement.Builder request = NRequest.FavoritesFolderManagement.newBuilder();
		request.setAction(NRequest.FavoritesFolderManagement.Action.UPDATE);
		NRequest.FavoritesFolder.Builder protoFolder = NRequest.FavoritesFolder.newBuilder();
		protoFolder.setId(folder.getId());
		protoFolder.setSchemaId(folder.getSchemaID());
		protoFolder.setName(folder.getName());
		protoFolder.setSort(folder.getSort());
		protoFolder.setParentFolderId(folder.getParentFolderID() > 0 ? folder.getParentFolderID() : 0);
		protoFolder.setGroupFolder(folder.isGroupFolder());
		request.setFolder(protoFolder);
		try{
			sendRequest(ServletName.FavoritesFolderManagementServlet, request.build());
		} catch (IOException ex){
			showErrorAndThrow("Error get favorite folder list: " + ex.getMessage(), ex);
		}
	}

	@Override
	public void deleteFolder(Folder folder){
		NRequest.FavoritesFolderManagement.Builder request = NRequest.FavoritesFolderManagement.newBuilder();
		request.setAction(NRequest.FavoritesFolderManagement.Action.DELETE);
		NRequest.FavoritesFolder.Builder protoFolder = NRequest.FavoritesFolder.newBuilder();
		protoFolder.setId(folder.getId());
		protoFolder.setSchemaId(folder.getSchemaID());
		request.setFolder(protoFolder);
		try{
			sendRequest(ServletName.FavoritesFolderManagementServlet, request.build());
		} catch (IOException ex){
			showErrorAndThrow("Error get favorite folder list: " + ex.getMessage(), ex);
		}
	}

	@Override
	public List<Folder> getFolders(int schemaID){
		NRequest.FavoritesFolderManagement request = NRequest.FavoritesFolderManagement.newBuilder().setAction(
				NRequest.FavoritesFolderManagement.Action.GET_ALL_FOLDERS).setSchemaId(schemaID).build();
		try{
			ByteString payload = sendRequest(ServletName.FavoritesFolderManagementServlet, request);
			NResponse.Folders protoFolders = NResponse.Folders.parseFrom(payload);
			List<NResponse.Folder> protoFolderList = protoFolders.getFoldersList();
			List<Folder> folders = new ArrayList<Folder>();
			for (NResponse.Folder protoFolder : protoFolderList){
				Folder ff = new Folder();
				ff.setIcon(IconCache.getImageIcon(IconCache.MENU_FOLDER));
				ff.setId(protoFolder.getId());
				ff.setParentFolderID(protoFolder.getParentId());
				ff.setName(protoFolder.getFolderName());
				ff.setSchemaID(protoFolder.getSchemaId());
				ff.setGroupFolder(protoFolder.getGroupFolder());
				ff.setSort(protoFolder.getSortOrder());
				folders.add(ff);
			}
			return folders;
		} catch (IOException ex){
			showErrorAndThrow("Error get favorite folder list: " + ex.getMessage(), ex);
			return null;
		}
	}

}
