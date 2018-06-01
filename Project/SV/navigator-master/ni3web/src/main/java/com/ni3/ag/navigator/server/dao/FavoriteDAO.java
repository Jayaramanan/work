package com.ni3.ag.navigator.server.dao;

import java.util.List;

import com.ni3.ag.navigator.server.domain.Favorite;

public interface FavoriteDAO{

	void delete(Favorite favorite);

	void delete(Integer id);

	void deleteByFolder(Integer folderId);

	Favorite get(Integer id);

	Favorite save(Favorite favorite);

	Favorite create(Favorite favorite);
	
	long getCount();

	List<Favorite> getBySchema(int schemaId, int userId);

	List<Favorite> getBySchema(int schemaId);

	List<Integer> getFavoriteIdsByFolder(Integer id);

	List<Favorite> getFavorites();
}
