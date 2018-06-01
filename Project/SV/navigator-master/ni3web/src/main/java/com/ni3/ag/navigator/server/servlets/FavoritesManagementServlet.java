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

import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.FavoriteDAO;
import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.server.domain.DeltaParam;
import com.ni3.ag.navigator.server.domain.DeltaParamIdentifier;
import com.ni3.ag.navigator.server.domain.Favorite;
import com.ni3.ag.navigator.server.services.FavoritesService;
import com.ni3.ag.navigator.server.session.ThreadLocalStorage;
import com.ni3.ag.navigator.shared.domain.DeltaType;
import com.ni3.ag.navigator.shared.domain.FavoriteMode;
import com.ni3.ag.navigator.shared.domain.User;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;
import com.ni3.ag.navigator.shared.proto.NRequest.FavoriteManagement;
import com.ni3.ag.navigator.shared.proto.NResponse.Envelope.Builder;

public class FavoritesManagementServlet extends Ni3Servlet{

	private static final long serialVersionUID = 9101888531980210844L;
	private NRequest.FavoriteManagement protoRequest = null;
	private Favorite newFavorite;

	@Override
	protected void doInternalPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException{
		InputStream is = request.getInputStream();
		protoRequest = NRequest.FavoriteManagement.parseFrom(is);
		NResponse.Envelope.Builder responseBuilder = NResponse.Envelope.newBuilder();
		switch (protoRequest.getAction()){
			case GET_ALL_FOR_SCHEMA:
				handleGetAllFavoritesBySchema(protoRequest, responseBuilder);
				break;
			case GET_FAVORITE_DATA:
				handleGetFavoriteData(protoRequest, responseBuilder);
				break;
			case CREATE:
				handleCreateFavorite(protoRequest, responseBuilder);
				break;
			case UPDATE:
				handleUpdateFavorite(protoRequest, responseBuilder);
				break;
			case DELETE:
				handleDeleteFavorite(protoRequest, responseBuilder);
				break;
			case COPY:
				handleCopyFavorite(protoRequest, responseBuilder);
				break;
		}
		responseBuilder.setStatus(NResponse.Envelope.Status.SUCCESS);
		NResponse.Envelope env = responseBuilder.build();
		env.writeTo(response.getOutputStream());
	}

	private void handleCopyFavorite(FavoriteManagement protoRequest, Builder responseBuilder){
		final FavoritesService service = NSpringFactory.getInstance().getFavoritesService();
		ThreadLocalStorage storage = NSpringFactory.getInstance().getThreadLocalStorage();
		int userId = storage.getCurrentUser().getId();

		final com.ni3.ag.navigator.shared.proto.NRequest.Favorite protoFavorite = protoRequest.getFavorite();

		newFavorite = service.copyFavorite(protoRequest.getId(), null, userId, protoFavorite.getFolderId(), protoFavorite
				.getName(), protoFavorite.getGroupFavorite());
		NResponse.Favorite.Builder respFavorite = NResponse.Favorite.newBuilder();
		respFavorite.setId(newFavorite.getId());
		responseBuilder.setPayload(respFavorite.build().toByteString());
	}

	private void handleCreateFavorite(FavoriteManagement protoRequest, Builder responseBuilder){
		final FavoritesService service = NSpringFactory.getInstance().getFavoritesService();
		ThreadLocalStorage storage = NSpringFactory.getInstance().getThreadLocalStorage();
		int userId = storage.getCurrentUser().getId();

		Favorite favorite = createFavoriteFromProtoFavorite(protoRequest.getFavorite());
		favorite.setCreatorId(userId);
		newFavorite = service.createFavorite(favorite);
		NResponse.Favorite.Builder protoFavorite = NResponse.Favorite.newBuilder();
		protoFavorite.setId(newFavorite.getId());
		responseBuilder.setPayload(protoFavorite.build().toByteString());
	}

	private void handleUpdateFavorite(FavoriteManagement protoRequest, Builder responseBuilder){
		final FavoritesService service = NSpringFactory.getInstance().getFavoritesService();

		final com.ni3.ag.navigator.shared.proto.NRequest.Favorite protoFavorite = protoRequest.getFavorite();

		service.updateFavorite(protoFavorite.getId(), protoFavorite.getFolderId(), protoFavorite.getName(), protoFavorite
				.getData(), protoFavorite.getDescription(), protoFavorite.getLayout(), FavoriteMode.getByValue(protoFavorite
				.getMode()), protoFavorite.getGroupFavorite());
	}

	private void handleDeleteFavorite(FavoriteManagement protoRequest, Builder responseBuilder){
		final FavoritesService service = NSpringFactory.getInstance().getFavoritesService();

		final com.ni3.ag.navigator.shared.proto.NRequest.Favorite protoFavorite = protoRequest.getFavorite();

		service.deleteFavorite(protoFavorite.getId());
	}

	private void handleGetFavoriteData(NRequest.FavoriteManagement protoRequest, NResponse.Envelope.Builder responseBuilder){
		FavoriteDAO favoriteDAO = NSpringFactory.getInstance().getFavoritesDao();
		Favorite f = favoriteDAO.get(protoRequest.getId());
		NResponse.Favorite.Builder protoFavorite = NResponse.Favorite.newBuilder();
		protoFavorite.setId(f.getId()).setData(f.getData().replace("\n", ""));
		if (f.getLayout() != null && !f.getLayout().isEmpty())
			protoFavorite.setLayout(f.getLayout()).build();
		responseBuilder.setPayload(protoFavorite.build().toByteString());
	}

	private void handleGetAllFavoritesBySchema(NRequest.FavoriteManagement protoRequest,
			NResponse.Envelope.Builder responseBuilder){
		NResponse.Favorites.Builder builder = NResponse.Favorites.newBuilder();
		FavoriteDAO favoriteDAO = NSpringFactory.getInstance().getFavoritesDao();
		ThreadLocalStorage storage = NSpringFactory.getInstance().getThreadLocalStorage();
		List<Favorite> favorites = favoriteDAO.getBySchema(protoRequest.getSchemaId(), storage.getCurrentUser().getId());
		for (Favorite f : favorites){
			String desc = f.getDescription();
			if (desc != null)
				desc = desc.replace("\\n", "\n");
			builder.addFavorites(NResponse.Favorite.newBuilder().setId(f.getId()).setDescription(desc).setSchemaId(
					f.getSchemaId()).setName(f.getName()).setCreatorId(f.getCreatorId()).setFolderId(f.getFolderId())
					.setGroupFavorite(f.getGroupFavorite()).setMode(f.getMode().getValue()));
		}
		responseBuilder.setPayload(builder.build().toByteString());
	}

	private Favorite createFavoriteFromProtoFavorite(com.ni3.ag.navigator.shared.proto.NRequest.Favorite protoFavorite){
		Favorite favorite = new Favorite();
		favorite.setSchemaId(protoFavorite.getSchemaId());
		favorite.setId(protoFavorite.getId());
		favorite.setName(protoFavorite.getName());
		favorite.setDescription(protoFavorite.getDescription());
		favorite.setGroupFavorite(protoFavorite.getGroupFavorite());
		favorite.setFolderId(protoFavorite.getFolderId());
		favorite.setMode(FavoriteMode.getByValue(protoFavorite.getMode()));
		favorite.setLayout(protoFavorite.getLayout());
		favorite.setData(protoFavorite.getData());
		return favorite;
	}

	@Override
	protected DeltaHeader getTransactionDeltaForRequest(){
		ThreadLocalStorage localStorage = NSpringFactory.getInstance().getThreadLocalStorage();
		User currentUser = localStorage.getCurrentUser();
		DeltaHeader res = DeltaHeader.DO_NOTHING;
		if (protoRequest == null)
			return res;

		switch (protoRequest.getAction()){
			case CREATE:
				res = getDeltaHeaderForCreate(currentUser);
				break;
			case DELETE:
				res = getDeltaHeaderForDelete(currentUser);
				break;
			case UPDATE:
				res = getDeltaHeaderForUpdate(currentUser);
				break;
			case COPY:
				res = getDeltaHeaderForCopy(currentUser);
				break;
		}

		return res;
	}

	private DeltaHeader getDeltaHeaderForCopy(User currentUser){
		Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();

		params.put(DeltaParamIdentifier.CopyFavoriteNewId, new DeltaParam(DeltaParamIdentifier.CopyFavoriteNewId, ""
				+ newFavorite.getId()));
		params.put(DeltaParamIdentifier.CopyFavoriteSchemaId, new DeltaParam(DeltaParamIdentifier.CopyFavoriteSchemaId, ""
				+ newFavorite.getSchemaId()));

		return new DeltaHeader(DeltaType.FAVORITE_COPY, currentUser, params);
	}

	private DeltaHeader getDeltaHeaderForUpdate(User currentUser){
		Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
		params.put(DeltaParamIdentifier.UpdateFavoriteSchemaId, new DeltaParam(DeltaParamIdentifier.UpdateFavoriteSchemaId,
				"" + protoRequest.getFavorite().getSchemaId()));
		params.put(DeltaParamIdentifier.UpdateFavoriteId, new DeltaParam(DeltaParamIdentifier.UpdateFavoriteId, ""
				+ protoRequest.getFavorite().getId()));
		return new DeltaHeader(DeltaType.FAVORITE_UPDATE, currentUser, params);
	}

	private DeltaHeader getDeltaHeaderForDelete(User currentUser){
		Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();

		final String favoriteId = protoRequest.getFavorite().getId() + "";
		params.put(DeltaParamIdentifier.DeleteFavoriteId, new DeltaParam(DeltaParamIdentifier.DeleteFavoriteId, favoriteId));
		params.put(DeltaParamIdentifier.DeleteFavoriteSchemaId, new DeltaParam(DeltaParamIdentifier.DeleteFavoriteSchemaId,
				"" + protoRequest.getFavorite().getSchemaId()));

		return new DeltaHeader(DeltaType.FAVORITE_DELETE, currentUser, params);
	}

	private DeltaHeader getDeltaHeaderForCreate(User currentUser){
		Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();

		params.put(DeltaParamIdentifier.CreateFavoriteNewId, new DeltaParam(DeltaParamIdentifier.CreateFavoriteNewId, ""
				+ newFavorite.getId()));
		params.put(DeltaParamIdentifier.CreateFavoriteSchemaId, new DeltaParam(DeltaParamIdentifier.CreateFavoriteSchemaId,
				"" + newFavorite.getSchemaId()));
		return new DeltaHeader(DeltaType.FAVORITE_CREATE, currentUser, params);
	}

	@Override
	protected UserActivityType getActivityType(){
		UserActivityType activity = null;
		final NRequest.FavoriteManagement.Action action = protoRequest != null ? protoRequest.getAction() : null;
		if (action == null)
			return activity;
		switch (action){
			case GET_FAVORITE_DATA:
				activity = UserActivityType.InvokeFavorite;
				break;
			case CREATE:
				activity = UserActivityType.CreateFavorite;
				break;
			case UPDATE:
				activity = UserActivityType.UpdateFavorite;
				break;
			case COPY:
				activity = UserActivityType.CopyFavorite;
				break;
			case DELETE:
				activity = UserActivityType.DeleteFavorite;
				break;
		}
		return activity;
	}

	@Override
	protected List<LogParam> getActivityParams(){
		List<LogParam> params = new ArrayList<LogParam>();
		final NRequest.FavoriteManagement.Action action = protoRequest != null ? protoRequest.getAction() : null;
		if (action != null){
			switch (protoRequest.getAction()){
				case GET_FAVORITE_DATA:
					params.add(new LogParam(ID_LOG_PARAM, protoRequest.getId()));
					break;
				case UPDATE:
				case DELETE:
					final com.ni3.ag.navigator.shared.proto.NRequest.Favorite protoFavorite = protoRequest.getFavorite();
					if (protoFavorite != null){
						params.add(new LogParam(ID_LOG_PARAM, protoFavorite.getId()));
					}
					break;
				case CREATE:
					params.add(new LogParam(ID_LOG_PARAM, newFavorite.getId()));
					break;
				case COPY:
					params.add(new LogParam(ID_LOG_PARAM, newFavorite.getId()));
					params.add(new LogParam(FROMID_LOG_PARAM, protoRequest.getId()));
					break;
			}
		}
		return params;
	}
}
