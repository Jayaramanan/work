package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.ni3.ag.navigator.server.dao.FavoritesFolderDAO;
import com.ni3.ag.navigator.server.domain.FavoritesFolder;

public class FavoritesFolderDAOImpl extends JdbcDaoSupport implements FavoritesFolderDAO{

	private RowMapper folderRowMapper = new RowMapper(){
		@Override
		public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException{
			FavoritesFolder ret = new FavoritesFolder();
			ret.setId(resultSet.getInt("id"));
			ret.setFolderName(resultSet.getString("foldername"));
			ret.setSchemaId(resultSet.getInt("schemaid"));
			ret.setCreatorId(resultSet.getInt("creatorid"));
			ret.setParentId(resultSet.getInt("parentid"));
			ret.setGroupFolder(resultSet.getInt("groupfolder") != 0);
			ret.setSortOrder(resultSet.getInt("sort"));
			return ret;
		}
	};

	@Override
	public long getCount(){
		final String sql = "select count(id) from cis_favorites_folder";
		return getJdbcTemplate().queryForLong(sql);
	}

	// @formatter:off
	private static final String SELECT_RECURSIVE_CHILDS = "WITH RECURSIVE subfolders AS" + "("
	        + "    SELECT id,foldername,schemaid,creatorid,parentid,groupfolder,sort FROM cis_favorites_folder WHERE id = ?"
	        + "    UNION ALL" + "    SELECT f.id,f.foldername,f.schemaid,f.creatorid,f.parentid,f.groupfolder,f.sort"
	        + "    FROM" + "        cis_favorites_folder AS f" + "    JOIN" + "        subfolders AS sf"
	        + "        ON (f.parentid = sf.id)" + ")" + "SELECT *" + "FROM subfolders";

	// @formatter:on

	@Override
	public Integer create(final FavoritesFolder folder){
		PreparedStatementCreator psc = new PreparedStatementCreator(){
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException{
				PreparedStatement statement = con.prepareStatement("INSERT INTO cis_favorites_folder "
				        + "(id,foldername,schemaid,creatorid,parentid,groupfolder,sort) VALUES (?,?,?,?,?,?,?)",
				        Statement.RETURN_GENERATED_KEYS);
				statement.setInt(1, folder.getId());
				statement.setString(2, folder.getFolderName());
				statement.setInt(3, folder.getSchemaId());
				statement.setInt(4, folder.getCreatorId());
				final Integer parentId = folder.getParentId();
				if (parentId != null && parentId != 0){
					statement.setInt(5, parentId);
				} else{
					statement.setNull(5, Types.INTEGER);
				}
				statement.setInt(6, folder.getGroupFolder() ? 1 : 0);
				statement.setInt(7, folder.getSortOrder());
				return statement;
			}
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(psc, keyHolder);
		Number generatedId = (Number) keyHolder.getKeys().get("id");

		return generatedId.intValue();
	}

	@Override
	public void delete(final FavoritesFolder folder){
		delete(folder.getId());
	}

	@Override
	public void delete(final Integer folderId){
		if (folderId == null){
			return;
		}
		final String sql = "DELETE FROM cis_favorites_folder WHERE ID=?";
		getJdbcTemplate().update(sql, new Object[] { folderId });
	}

	@Override
	public List<Integer> getTraverseListParentFirst(List<FavoritesFolder> branch, Integer topFolderId){
		Map<Integer, Integer> idToParentIdMap = new HashMap<Integer, Integer>();
		List<Integer> result = new ArrayList<Integer>();
		for (FavoritesFolder folder : branch){
			Integer parentId = folder.getParentId();
			Integer id = folder.getId();
			idToParentIdMap.put(id, parentId);
		}
		getChildren(topFolderId, idToParentIdMap, result);
		return result;
	}

	@Override
	public List<FavoritesFolder> getFolders(){
		final String sql = "SELECT id,foldername,schemaid,creatorid,parentid,groupfolder,sort FROM cis_favorites_folder";
		return getJdbcTemplate().query(sql, folderRowMapper);
	}

	private void getChildren(Integer topid, Map<Integer, Integer> idToParentMap, List<Integer> result){
		result.add(topid);

		for (Integer id : idToParentMap.keySet()){
			Integer parent = idToParentMap.get(id);
			if (parent != null && topid.equals(parent))
				getChildren(id, idToParentMap, result);
		}
	}

	@Override
	public FavoritesFolder get(final Integer id){
		final String sql = "SELECT id,foldername,schemaid,creatorid,parentid,groupfolder,"
		        + "sort FROM cis_favorites_folder WHERE ID=?";
		List<?> folders = getJdbcTemplate().query(sql, new Object[] { id }, folderRowMapper);
		return folders.isEmpty() ? null : (FavoritesFolder) folders.get(0);
	}

	@Override
	public List<FavoritesFolder> getSubfolders(final int id){
		return getJdbcTemplate().query(SELECT_RECURSIVE_CHILDS, new Object[] { id }, folderRowMapper);
	}

	@Override
	public FavoritesFolder save(final FavoritesFolder folder){
		final int existingId = folder.getId();
		PreparedStatementCreator psc = new PreparedStatementCreator(){
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException{
				PreparedStatement statement;
				final String folderName = folder.getFolderName();
				Integer parentId = folder.getParentId();
				if (existingId != 0){
					statement = con
					        .prepareStatement("UPDATE cis_favorites_folder SET foldername=?, parentid=?, sort=? WHERE ID=?");
					if (folderName == null){
						statement.setNull(1, java.sql.Types.VARCHAR);
					} else{
						statement.setString(1, folderName);
					}
					if (parentId == null || parentId == 0){
						statement.setNull(2, java.sql.Types.INTEGER);
					} else{
						statement.setInt(2, parentId);
					}
					statement.setInt(3, folder.getSortOrder());
					statement.setInt(4, existingId);
				} else{
					statement = con
					        .prepareStatement(
					                "INSERT INTO cis_favorites_folder (foldername,schemaid,creatorid,parentid,groupfolder,sort) VALUES (?,?,?,?,?,?)",
					                Statement.RETURN_GENERATED_KEYS);
					statement.setString(1, folderName);
					statement.setInt(2, folder.getSchemaId());
					statement.setInt(3, folder.getCreatorId());

					if (parentId == null || parentId == 0){
						statement.setNull(4, java.sql.Types.INTEGER);
					} else{
						statement.setInt(4, parentId);
					}
					statement.setInt(5, folder.getGroupFolder() ? 1 : 0);
					statement.setInt(6, folder.getSortOrder());
				}
				return statement;
			}
		};
		if (existingId == 0){
			KeyHolder keyHolder = new GeneratedKeyHolder();
			getJdbcTemplate().update(psc, keyHolder);
			Number generatedId = (Number) keyHolder.getKeys().get("id");

			folder.setId(generatedId.intValue());
		} else
			getJdbcTemplate().update(psc);
		return folder;
	}

	@Override
	public List<FavoritesFolder> findByName(final String name){
		final String query = "SELECT id, foldername, schemaid, creatorid, parentid, groupfolder, sort FROM "
		        + "cis_favorites_folder WHERE foldername = ? ORDER BY id";
		return getJdbcTemplate().query(query, new Object[] { name }, folderRowMapper);
	}

	@Override
	public List<FavoritesFolder> getFolders(int schemaId, int userId){
		final String sql = "SELECT id, foldername, schemaid, creatorid, parentid, groupfolder, "
		        + "sort FROM CIS_FAVORITES_FOLDER " + "WHERE SchemaID=? and " + "	( " + "		(groupfolder=0 AND CreatorID=?) "
		        + "		or " + "		(groupfolder=1 AND CreatorID IN (SELECT userid from SYS_USER_GROUP "
		        + "						WHERE groupid in " + "						(select groupid from sys_user_group where userid = ?))) " + "	) "
		        + "order by ParentID";
		return getJdbcTemplate().query(sql, new Object[] { schemaId, userId, userId }, folderRowMapper);
	}
}
