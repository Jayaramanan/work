package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.server.domain.DeltaParam;
import com.ni3.ag.navigator.server.domain.DeltaParamIdentifier;
import com.ni3.ag.navigator.server.domain.FavoritesFolder;
import com.ni3.ag.navigator.server.services.FavoritesFolderService;
import com.ni3.ag.navigator.server.session.ThreadLocalStorage;
import com.ni3.ag.navigator.shared.domain.DeltaType;
import com.ni3.ag.navigator.shared.domain.User;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;
import com.ni3.ag.navigator.shared.proto.NRequest.FavoritesFolderManagement;
import com.ni3.ag.navigator.shared.proto.NResponse.Envelope.Builder;

public class FavoritesFolderManagementServlet extends Ni3Servlet{
	private static final long serialVersionUID = -6907610470096443519L;
	private static final Logger log = Logger.getLogger(FavoritesFolderManagementServlet.class);

	private NRequest.FavoritesFolderManagement.Action action;
	private FavoritesFolder folder;

	@Override
	protected void doInternalPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException{
		InputStream io = request.getInputStream();
		NRequest.FavoritesFolderManagement protoRequest = NRequest.FavoritesFolderManagement.parseFrom(io);
		NResponse.Envelope.Builder responseBuilder = NResponse.Envelope.newBuilder();
		action = protoRequest.getAction();
		switch (action){
			case GET_ALL_FOLDERS:
				handleGetAllFolders(protoRequest, responseBuilder);
				break;
			case CREATE:
				handleCreateFolder(protoRequest, responseBuilder);
				break;
			case UPDATE:
				handleUpdateFolder(protoRequest, responseBuilder);
				break;
			case DELETE:
				handleDeleteFolder(protoRequest, responseBuilder);
				break;
		}
		responseBuilder.setStatus(NResponse.Envelope.Status.SUCCESS);
		responseBuilder.build().writeTo(response.getOutputStream());
	}

	private void handleCreateFolder(FavoritesFolderManagement protoRequest, Builder responseBuilder){
		FavoritesFolderService folderService = NSpringFactory.getInstance().getFavoritesFolderService();
		com.ni3.ag.navigator.shared.proto.NRequest.FavoritesFolder protoFolder = protoRequest.getFolder();
		log.debug("Creating folder: " + protoFolder.getName());

		folder = createFolderFromProtoFolder(protoFolder);

		folder = folderService.createFolder(folder);

		final com.ni3.ag.navigator.shared.proto.NResponse.Folder.Builder folderBuilder = createProtoFolderFromFolder(folder);
		responseBuilder.setPayload(folderBuilder.build().toByteString());
	}

	private void handleUpdateFolder(FavoritesFolderManagement protoRequest, Builder responseBuilder){
		FavoritesFolderService folderService = NSpringFactory.getInstance().getFavoritesFolderService();
		com.ni3.ag.navigator.shared.proto.NRequest.FavoritesFolder protoFolder = protoRequest.getFolder();
		log.debug("Updating folder: " + protoFolder.getName());

		folder = createFolderFromProtoFolder(protoFolder);

		folderService.updateFolder(folder);
	}

	private void handleDeleteFolder(FavoritesFolderManagement protoRequest, Builder responseBuilder){
		FavoritesFolderService folderService = NSpringFactory.getInstance().getFavoritesFolderService();
		com.ni3.ag.navigator.shared.proto.NRequest.FavoritesFolder protoFolder = protoRequest.getFolder();
		log.debug("Deleting folder: " + protoFolder.getName());

		folder = createFolderFromProtoFolder(protoFolder);

		folderService.deleteFolder(protoFolder.getId());
	}

	private void handleGetAllFolders(NRequest.FavoritesFolderManagement protoRequest,
			NResponse.Envelope.Builder responseBuilder){
		FavoritesFolderService folderService = NSpringFactory.getInstance().getFavoritesFolderService();
		List<FavoritesFolder> folders = folderService.getAllFolders(protoRequest.getSchemaId());
		NResponse.Folders.Builder foldersBuilder = NResponse.Folders.newBuilder();
		for (FavoritesFolder ff : folders){
			foldersBuilder.addFolders(createProtoFolderFromFolder(ff));
		}
		responseBuilder.setPayload(foldersBuilder.build().toByteString());
	}

	private com.ni3.ag.navigator.shared.proto.NResponse.Folder.Builder createProtoFolderFromFolder(FavoritesFolder ff){
		final com.ni3.ag.navigator.shared.proto.NResponse.Folder.Builder protoFolder = NResponse.Folder.newBuilder();
		protoFolder.setId(ff.getId());
		protoFolder.setParentId(ff.getParentId());
		protoFolder.setFolderName(ff.getFolderName());
		protoFolder.setSchemaId(ff.getSchemaId());
		protoFolder.setCreatorId(ff.getCreatorId());
		protoFolder.setGroupFolder(ff.getGroupFolder()).setSortOrder(ff.getSortOrder());
		return protoFolder;
	}

	private FavoritesFolder createFolderFromProtoFolder(
			com.ni3.ag.navigator.shared.proto.NRequest.FavoritesFolder protoFolder){
		FavoritesFolder folder = new FavoritesFolder();
		if (protoFolder.getId() > 0){
			folder.setId(protoFolder.getId());
		}
		folder.setFolderName(protoFolder.getName());
		folder.setSchemaId(protoFolder.getSchemaId());
		if (protoFolder.getParentFolderId() > 0){
			folder.setParentId(protoFolder.getParentFolderId());
		}
		folder.setGroupFolder(protoFolder.getGroupFolder());
		folder.setSortOrder(protoFolder.getSort());
		return folder;
	}

	@Override
	protected DeltaHeader getTransactionDeltaForRequest(){
		DeltaHeader result = DeltaHeader.DO_NOTHING;

		ThreadLocalStorage localStorage = NSpringFactory.getInstance().getThreadLocalStorage();
		User currentUser = localStorage.getCurrentUser();

		switch (action){
			case CREATE: {
				Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
				params.put(DeltaParamIdentifier.CreateFavoriteFolderNewId, new DeltaParam(
						DeltaParamIdentifier.CreateFavoriteFolderNewId, "" + folder.getId()));
				params.put(DeltaParamIdentifier.CreateFavoriteFolderSchemaId, new DeltaParam(
						DeltaParamIdentifier.CreateFavoriteFolderSchemaId, "" + folder.getSchemaId()));

				result = new DeltaHeader(DeltaType.FAVORITE_FOLDER_CREATE, currentUser, params);
				break;
			}
			case DELETE: {
				Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
				params.put(DeltaParamIdentifier.DeleteFavoriteFolderObjId, new DeltaParam(
						DeltaParamIdentifier.DeleteFavoriteFolderObjId, "" + folder.getId()));
				params.put(DeltaParamIdentifier.DeleteFavoriteFolderSchemaId, new DeltaParam(
						DeltaParamIdentifier.DeleteFavoriteFolderSchemaId, "" + folder.getSchemaId()));

				result = new DeltaHeader(DeltaType.FAVORITE_FOLDER_DELETE, currentUser, params);
				break;
			}
			case UPDATE: {
				Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
				params.put(DeltaParamIdentifier.UpdateFavoriteFolderFolderId, new DeltaParam(
						DeltaParamIdentifier.UpdateFavoriteFolderFolderId, "" + folder.getId()));
				params.put(DeltaParamIdentifier.UpdateFavoriteFolderSchemaId, new DeltaParam(
						DeltaParamIdentifier.UpdateFavoriteFolderSchemaId, "" + folder.getSchemaId()));

				result = new DeltaHeader(DeltaType.FAVORITE_FOLDER_UPDATE, currentUser, params);
				break;
			}
		}

		return result;
	}

	@Override
	protected UserActivityType getActivityType(){
		UserActivityType activity = null;
		if (action == null){
			return null;
		}
		switch (action){
			case UPDATE:
				activity = UserActivityType.UpdateFolder;
				break;
			case DELETE:
				activity = UserActivityType.DeleteFolder;
				break;
			case CREATE:
				activity = UserActivityType.CreateFolder;
				break;
		}
		return activity;
	}

	@Override
	protected List<LogParam> getActivityParams(){
		List<LogParam> params = new ArrayList<LogParam>();
		if (action != null){
			switch (action){
				case UPDATE:
				case DELETE:
				case CREATE:
					params.add(new LogParam(ID_LOG_PARAM, folder.getId()));
					break;
			}
		}
		return params;
	}
}
