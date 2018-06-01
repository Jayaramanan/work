package com.ni3.ag.navigator.server.services.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.dao.DaoException;
import com.ni3.ag.navigator.server.dao.FavoriteDAO;
import com.ni3.ag.navigator.server.dao.IAmDAO;
import com.ni3.ag.navigator.server.domain.Favorite;
import com.ni3.ag.navigator.server.services.FavoritesService;
import com.ni3.ag.navigator.server.services.ObjectManagementService;
import com.ni3.ag.navigator.shared.domain.FavoriteMode;

public class FavoritesServiceImpl extends JdbcDaoSupport implements FavoritesService{

	private static final Logger log = Logger.getLogger(FavoritesServiceImpl.class);
	private FavoriteDAO favoriteDAO;
	private IAmDAO iAmDAO;
	private ObjectManagementService objectManagementService;

	public void setFavoriteDAO(FavoriteDAO favoriteDAO){
		this.favoriteDAO = favoriteDAO;
	}

	public void setiAmDAO(IAmDAO iAmDAO){
		this.iAmDAO = iAmDAO;
	}

	public void setObjectManagementService(ObjectManagementService objectManagementService){
		this.objectManagementService = objectManagementService;
	}

	@Override
	public String checkVersion(final int favoriteId){
		String ret = RESULT_ERROR;

		// TODO: should be moved to lower layer
		String sql = "SELECT f.dbversion AS dbversion, s.version AS version, " + ""
				+ "f.name AS name FROM cis_favorites f, sys_iam s WHERE f.id=?";
		String result = (String) getJdbcTemplate().queryForObject(sql, new Object[] { favoriteId }, new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
				final String dbVersion = rs.getString("dbversion");
				final String version = rs.getString("version");
				final String name = rs.getString("name");

				String result = checkVersion(RESULT_ERROR, dbVersion, version);
				final String logMessage = "Favorite " + favoriteId + " (" + name + ") is out of date. Favorite "
						+ "version=" + version + ", DB version=" + dbVersion;
				if (OLD_FAVORITE_ERROR.equals(result)){
					log.error(logMessage);
				} else if (OLD_FAVORITE_WARNING.equals(result)){
					log.warn(logMessage);
				}
				return result;
			}
		});
		if (result != null){
			ret = result;
		}

		return ret;
	}

	private String checkVersion(String ret, final String dbVersion, final String version){
		final String[] dbTokens = dbVersion.split("\\.");
		final String[] tokens = version.split("\\.");
		if (dbTokens.length == 3 && tokens.length == 3){
			if (!dbTokens[1].equals(tokens[1])){
				ret = OLD_FAVORITE_ERROR;
			} else if (!dbTokens[2].equals(tokens[2])){
				ret = OLD_FAVORITE_WARNING;
			} else{
				ret = "0";
			}
		}
		return ret;
	}

	@Override
	public Favorite copyFavorite(final Integer originalId, final Integer favoriteId, final Integer creatorId,
			final Integer destinationFolderId, final String newName, final Boolean isGroupFavorite){
		final Favorite favorite = favoriteDAO.get(originalId);
		if (favorite == null)
			throw new DaoException("Cannot find original favorite to copy: " + originalId);

		return createFavorite(favoriteId, creatorId, favorite.getSchemaId(), newName, favorite.getData(), favorite
				.getDescription(), destinationFolderId, isGroupFavorite, favorite.getLayout(), favorite.getMode());
	}

	@Override
	public Favorite createFavorite(final Integer favoriteId, final Integer userId, final Integer schemaId,
			final String name, final String data, final String description, final Integer folderId,
			final Boolean isGroupFavorite, final String layout, final FavoriteMode mode){
		Favorite favorite = new Favorite();
		favorite.setSchemaId(schemaId);
		favorite.setName(name);
		favorite.setCreatorId(userId);
		favorite.setData(data);
		favorite.setDescription(description);
		favorite.setFolderId(folderId);
		favorite.setGroupFavorite(isGroupFavorite);
		favorite.setLayout(layout);
		favorite.setMode(mode);

		final String version = iAmDAO.getVersion();
		favorite.setDbVersion(version);

		favorite.setId(favoriteId != null ? favoriteId : 0);
		favorite = favoriteDAO.create(favorite);
		return favorite;
	}

	@Override
	public Favorite createFavorite(Favorite favorite){
		final String version = iAmDAO.getVersion();
		favorite.setDbVersion(version);
		return favoriteDAO.create(favorite);
	}

	@Override
	public void deleteFavorite(final Integer favoriteId){
		final Favorite favorite = favoriteDAO.get(favoriteId);
		if (favorite == null){
			log.warn("Cannot delete favorite with ID: " + favoriteId + " does not exist");
			return;
		}
		favoriteDAO.delete(favoriteId);
		if (favorite.getMode() == FavoriteMode.TOPIC){
			objectManagementService.clearContext(favoriteId, favorite.getSchemaId());
		}
	}

	@Override
	public boolean updateFavorite(final Integer favoriteId, final Integer folderId, final String name, final String data,
			final String description, final String layout, final FavoriteMode mode, final Boolean group){
		final Favorite favorite = favoriteDAO.get(favoriteId);

		if (favorite == null)
			return false;
		final String version = iAmDAO.getVersion();
		favorite.setDbVersion(version);

		favorite.setFolderId(folderId == null ? 0 : folderId);

		if (name != null && !name.isEmpty()){
			favorite.setName(name);
		}
		if (data != null && !data.isEmpty()){
			favorite.setData(data);
		}
		if (description != null && !description.isEmpty()){
			favorite.setDescription(description);
		}
		if (layout != null && !layout.isEmpty()){
			favorite.setLayout(layout);
		}
		if (mode != null){
			favorite.setMode(mode);
		}
		if (group != null){
			favorite.setGroupFavorite(group);
		}
		favoriteDAO.save(favorite);
		return true;
	}

	@Override
	public void convertFavoriteToGroup(Integer favoriteId){
		final Favorite favorite = favoriteDAO.get(favoriteId);
		if (favorite != null){
			favorite.setGroupFavorite(true);
			favorite.setFolderId(0);
			favoriteDAO.save(favorite);
		} else{
			// handle? how?
		}
	}
}
