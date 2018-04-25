/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gateway.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.client.domain.Favorite;
import com.ni3.ag.navigator.client.gateway.FavoritesGateway;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.domain.FavoriteMode;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;

public class HttpFavoritesGatewayImpl extends AbstractGatewayImpl implements FavoritesGateway{

	@Override
	public void deleteFavorite(Favorite favorite){
		NRequest.FavoriteManagement.Builder request = NRequest.FavoriteManagement.newBuilder();
		request.setAction(NRequest.FavoriteManagement.Action.DELETE);
		NRequest.Favorite.Builder protoFavorite = createProtoFavoriteFromFavorite(favorite);
		request.setFavorite(protoFavorite);
		try{
			sendRequest(ServletName.FavoritesManagementServlet, request.build());
		} catch (IOException ex){
			showErrorAndThrow("Error get favorite folder list: " + ex.getMessage(), ex);
		}
	}

	public int copyFavorite(int favoriteFromId, Favorite newFavorite){
		NRequest.FavoriteManagement.Builder request = NRequest.FavoriteManagement.newBuilder();
		request.setAction(NRequest.FavoriteManagement.Action.COPY);
		NRequest.Favorite.Builder protoFavorite = createProtoFavoriteFromFavorite(newFavorite);
		request.setFavorite(protoFavorite);
		request.setId(favoriteFromId);
		int result = -1;
		try{
			ByteString payload = sendRequest(ServletName.FavoritesManagementServlet, request.build());
			NResponse.Favorite resultFavorite = NResponse.Favorite.parseFrom(payload);
			result = resultFavorite.getId();
		} catch (IOException ex){
			showErrorAndThrow("Error get favorite folder list: " + ex.getMessage(), ex);
		}
		return result;
	}

	@Override
	public int validateFavoriteVersion(int favoriteID){
		NRequest.FavoriteManagement.Builder request = NRequest.FavoriteManagement.newBuilder();
		request.setAction(NRequest.FavoriteManagement.Action.VALIDATE_VERSION);
		request.setId(favoriteID);
		int result = -1;
		try{

			final ByteString payload = sendRequest(ServletName.FavoritesManagementServlet, request.build());
			NResponse.Favorites protoFavorites = NResponse.Favorites.parseFrom(payload);
			result = protoFavorites.getResult().getNumber();

		} catch (IOException ex){
			showErrorAndThrow("Error get favorite folder list: " + ex.getMessage(), ex);
		}

		return result;
	}

	@Override
	public int createFavorite(Favorite favorite){
		NRequest.FavoriteManagement.Builder request = NRequest.FavoriteManagement.newBuilder();
		request.setAction(NRequest.FavoriteManagement.Action.CREATE);
		NRequest.Favorite.Builder protoFavorite = createProtoFavoriteFromFavorite(favorite);
		request.setFavorite(protoFavorite);
		int result = -1;
		try{
			ByteString payload = sendRequest(ServletName.FavoritesManagementServlet, request.build());
			NResponse.Favorite resultFavorite = NResponse.Favorite.parseFrom(payload);
			result = resultFavorite.getId();
		} catch (IOException ex){
			showErrorAndThrow("Error get favorite folder list: " + ex.getMessage(), ex);
		}
		return result;
	}

	@Override
	public void updateFavorite(Favorite favorite){
		NRequest.FavoriteManagement.Builder request = NRequest.FavoriteManagement.newBuilder();
		request.setAction(NRequest.FavoriteManagement.Action.UPDATE);
		NRequest.Favorite.Builder protoFavorite = createProtoFavoriteFromFavorite(favorite);
		request.setFavorite(protoFavorite);
		try{
			sendRequest(ServletName.FavoritesManagementServlet, request.build());
		} catch (IOException ex){
			showErrorAndThrow("Error get favorite folder list: " + ex.getMessage(), ex);
		}
	}

	@Override
	public List<Favorite> getAllFavorites(int schemaID){
		NRequest.FavoriteManagement request = NRequest.FavoriteManagement.newBuilder().setAction(
				NRequest.FavoriteManagement.Action.GET_ALL_FOR_SCHEMA).setSchemaId(schemaID).build();
		try{
			ByteString payload = sendRequest(ServletName.FavoritesManagementServlet, request);
			NResponse.Favorites protoFavorites = NResponse.Favorites.parseFrom(payload);
			List<NResponse.Favorite> protoFavoriteList = protoFavorites.getFavoritesList();
			List<Favorite> favorites = new ArrayList<Favorite>();
			for (NResponse.Favorite protoFavorite : protoFavoriteList){
				Favorite f = new Favorite();
				f.setSchemaId(protoFavorite.getSchemaId());
				f.setId(protoFavorite.getId());
				f.setName(protoFavorite.getName());
				f.setDescription(protoFavorite.getDescription());
				f.setGroupFavorite(protoFavorite.getGroupFavorite());
				f.setFolderId(protoFavorite.getFolderId());
				f.setCreatorId(protoFavorite.getCreatorId());
				f.setMode(FavoriteMode.getByValue(protoFavorite.getMode()));
				f.setLayout(protoFavorite.getLayout());
				favorites.add(f);
			}
			return favorites;
		} catch (IOException e){
			showErrorAndThrow("Error get group and user favorites by schema", e);
			return null;
		}
	}

	@Override
	public Favorite loadFavoriteData(int id){
		NRequest.FavoriteManagement request = NRequest.FavoriteManagement.newBuilder().setAction(
				NRequest.FavoriteManagement.Action.GET_FAVORITE_DATA).setId(id).build();
		try{
			ByteString payload = sendRequest(ServletName.FavoritesManagementServlet, request);
			NResponse.Favorite protoFavorite = NResponse.Favorite.parseFrom(payload);
			Favorite f = new Favorite();
			f.setData(protoFavorite.getData());
			f.setLayout(protoFavorite.getLayout());
			return f;
		} catch (IOException e){
			showErrorAndThrow("Error get group and user favorites by schema", e);
			return null;
		}
	}

	private NRequest.Favorite.Builder createProtoFavoriteFromFavorite(Favorite favorite){
		NRequest.Favorite.Builder protoFavorite = NRequest.Favorite.newBuilder();
		protoFavorite.setId(favorite.getId());
		protoFavorite.setSchemaId(favorite.getSchemaId());
		protoFavorite.setName(favorite.getName());
		protoFavorite.setFolderId(favorite.getFolderId() > 0 ? favorite.getFolderId() : 0);
		protoFavorite.setGroupFavorite(favorite.isGroupFavorite());
		if (favorite.getMode() != null){
			protoFavorite.setMode(favorite.getMode().getValue());
		}
		if (favorite.getDescription() != null){
			protoFavorite.setDescription(favorite.getDescription());
		}
		if (favorite.getLayout() != null){
			protoFavorite.setLayout(favorite.getLayout());
		}
		if (favorite.getData() != null){
			protoFavorite.setData(favorite.getData());
		}

		return protoFavorite;
	}
}
