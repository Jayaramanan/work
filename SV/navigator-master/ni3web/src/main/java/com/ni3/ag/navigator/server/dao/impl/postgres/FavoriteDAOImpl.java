package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.*;
import java.util.List;

import com.ni3.ag.navigator.server.dao.FavoriteDAO;
import com.ni3.ag.navigator.server.domain.Favorite;
import com.ni3.ag.navigator.shared.domain.FavoriteMode;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class FavoriteDAOImpl extends JdbcDaoSupport implements FavoriteDAO{

	private RowMapper favoriteRowMapper = new RowMapper(){
		@Override
		public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException{
			Favorite ret = new Favorite();
			ret.setId(resultSet.getInt("id"));
			ret.setDescription(resultSet.getString("description"));
			ret.setSchemaId(resultSet.getInt("schemaid"));
			ret.setData(resultSet.getString("data"));
			ret.setLayout(resultSet.getString("layout"));
			ret.setName(resultSet.getString("name"));
			ret.setCreatorId(resultSet.getInt("creatorid"));
			ret.setFolderId(resultSet.getInt("folderid"));
			ret.setGroupFavorite(resultSet.getBoolean("groupfavorites"));
			ret.setMode(FavoriteMode.getByValue(resultSet.getInt("mode")));
			ret.setDbVersion(resultSet.getString("dbversion"));
			return ret;
		}
	};

	@Override
	public long getCount(){
		final String sql = "select count(id) from cis_favorites";
		return getJdbcTemplate().queryForLong(sql);
	}

	@Override
	public void delete(final Favorite favorite){
		if (favorite == null){
			return;
		}
		delete(favorite.getId());
	}

	@Override
	public void delete(final Integer id){
		final String sql = "DELETE FROM cis_favorites WHERE ID=?";
		getJdbcTemplate().update(sql, new Object[]{id});
	}

	@Override
	public void deleteByFolder(final Integer folderId){
		final String sql = "delete FROM cis_favorites WHERE FolderID=?";
		getJdbcTemplate().update(sql, new Object[]{folderId});
	}

	@Override
	public Favorite get(final Integer id){
		final String sql = "SELECT id,description,schemaid,data,layout,name,creatorid,folderid,groupfavorites,mode,dbversion FROM cis_favorites WHERE id=?";
		List<Favorite> favorites = getJdbcTemplate().query(sql, new Object[]{id}, favoriteRowMapper);
		if (favorites.isEmpty())
			return null;
		return favorites.get(0);
	}

	@Override
	public Favorite create(final Favorite favorite){
		if (favorite.getId() == 0)
			return createNoId(favorite);
		else
			return createWithId(favorite);
	}

	private Favorite createWithId(Favorite favorite){
		final String sql = "INSERT INTO cis_favorites (id,description,schemaid,data,layout,name,creatorid,folderid,groupfavorites,mode,dbversion) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
		getJdbcTemplate().update(
				sql,
				new Object[]{favorite.getId(), favorite.getDescription(), favorite.getSchemaId(), favorite.getData(),
						favorite.getLayout(), favorite.getName(), favorite.getCreatorId(),
						favorite.getFolderId() != 0 ? favorite.getFolderId() : null, favorite.getGroupFavorite() ? 1 : 0,
						favorite.getMode().getValue(), favorite.getDbVersion()});
		return favorite;
	}

	private Favorite createNoId(final Favorite favorite){
		PreparedStatementCreator psc = new PreparedStatementCreator(){
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException{
				PreparedStatement statement = con.prepareStatement("INSERT INTO cis_favorites (description,schemaid,"
						+ "data,layout,name,creatorid,folderid,groupfavorites,mode,dbversion) "
						+ "VALUES (?,?,?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
				statement.setString(1, favorite.getDescription());
				statement.setInt(2, favorite.getSchemaId());
				statement.setString(3, favorite.getData());
				statement.setString(4, favorite.getLayout());
				statement.setString(5, favorite.getName());
				statement.setInt(6, favorite.getCreatorId());
				if (favorite.getFolderId() == 0){
					statement.setNull(7, java.sql.Types.INTEGER);
				} else{
					statement.setInt(7, favorite.getFolderId());
				}
				statement.setInt(8, favorite.getGroupFavorite() ? 1 : 0);
				statement.setInt(9, favorite.getMode().getValue());
				statement.setString(10, favorite.getDbVersion());
				return statement;
			}
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(psc, keyHolder);
		Number generatedId = (Number) keyHolder.getKeys().get("id");
		favorite.setId(generatedId.intValue());
		return favorite;
	}

	@Override
	public List<Favorite> getBySchema(int schemaId, int userId){
		final String sql = "SELECT id,description,schemaid,data,layout,name, "
				+ "creatorid,folderid,groupfavorites,mode,dbversion FROM cis_favorites WHERE " + "schemaid=? " + "and "
				+ "		((groupfavorites = 0 and creatorid = ?) " + "or "
				+ "    (groupfavorites = 1 and creatorid in (select userid from sys_user_group "
				+ "       where groupid = (select groupid " + "									  from sys_user_group where userid = ?)))) "
				+ "ORDER BY name";
		return getJdbcTemplate().query(sql, new Object[]{schemaId, userId, userId}, favoriteRowMapper);
	}

	@Override
	public List<Favorite> getBySchema(int schemaId){
		final String sql = "SELECT id,description,schemaid,data,layout,name,creatorid,folderid,groupfavorites,mode,dbversion FROM cis_favorites " +
				"WHERE schemaid=? " +
				"ORDER BY name";
		return getJdbcTemplate().query(sql, new Object[]{schemaId}, favoriteRowMapper);
	}

	@Override
	public Favorite save(final Favorite favorite){
		final Integer passedId = favorite.getId();
		PreparedStatementCreator psc = new PreparedStatementCreator(){
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException{
				PreparedStatement statement;
				if (passedId != null && passedId != 0){
					statement = con
							.prepareStatement("UPDATE cis_favorites SET description=?,schemaid=?,data=?,layout=?,name=?,creatorid=?,folderid=?,groupfavorites=?,mode=?,dbversion=? WHERE id=?");
				} else{
					statement = con
							.prepareStatement(
									"INSERT INTO cis_favorites (description,schemaid,data,layout,name,creatorid,folderid,groupfavorites,mode,dbversion) VALUES (?,?,?,?,?,?,?,?,?,(select version from sys_iam))",
									Statement.RETURN_GENERATED_KEYS);
				}

				statement.setString(1, favorite.getDescription());
				statement.setInt(2, favorite.getSchemaId());
				statement.setString(3, favorite.getData());
				statement.setString(4, favorite.getLayout());
				statement.setString(5, favorite.getName());
				statement.setInt(6, favorite.getCreatorId());
				if (favorite.getFolderId() > 0){
					statement.setInt(7, favorite.getFolderId());
				} else{
					statement.setNull(7, Types.INTEGER);
				}
				statement.setInt(8, favorite.getGroupFavorite() ? 1 : 0);
				statement.setInt(9, favorite.getMode().getValue());

				if (passedId != null){
					statement.setString(10, favorite.getDbVersion());
					if (passedId != 0){
						statement.setInt(11, passedId);
					} else{
						statement.setNull(11, Types.INTEGER);
					}
				}
				return statement;
			}
		};

		if (passedId == null || passedId == 0){
			KeyHolder keyHolder = new GeneratedKeyHolder();
			getJdbcTemplate().update(psc, keyHolder);
			Number generatedId = (Number) keyHolder.getKeys().get("id");
			return get(generatedId.intValue());
		} else
			getJdbcTemplate().update(psc);
		return favorite;
	}

	@Override
	public List<Integer> getFavoriteIdsByFolder(Integer folderId){
		final String sql = "SELECT id FROM cis_favorites WHERE folderid=?";
		return getJdbcTemplate().queryForList(sql, new Object[]{folderId}, Integer.class);
	}

	@Override
	public List<Favorite> getFavorites(){
		final String sql = "SELECT id,description,schemaid,data,layout,name,creatorid,folderid," +
				"groupfavorites,mode,dbversion FROM cis_favorites";
		return getJdbcTemplate().query(sql, favoriteRowMapper);
	}

}
