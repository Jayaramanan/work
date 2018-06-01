package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.*;
import java.util.*;

import com.ni3.ag.navigator.server.dao.DeltaHeaderDAO;
import com.ni3.ag.navigator.server.dao.DeltaParamDAO;
import com.ni3.ag.navigator.server.dao.UserDAO;
import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.server.domain.DeltaParam;
import com.ni3.ag.navigator.server.domain.DeltaParamIdentifier;
import com.ni3.ag.navigator.server.domain.SyncStatus;
import com.ni3.ag.navigator.shared.domain.DeltaType;
import com.ni3.ag.navigator.shared.domain.User;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class DeltaHeaderDAOImpl extends JdbcDaoSupport implements DeltaHeaderDAO{
	private static final Logger log = Logger.getLogger(DeltaHeaderDAOImpl.class);

	private UserDAO userDAO;
	private DeltaParamDAO deltaParamDAO;

	public void setDeltaParamDAO(DeltaParamDAO deltaParamDAO){
		this.deltaParamDAO = deltaParamDAO;
	}

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<DeltaHeader> getUnprocessedDeltas(int limit){
		final String query = "SELECT id, deltatype, timestamp, creatorid, issync FROM sys_delta_header "
		        + "WHERE status = ? ORDER BY timestamp";
		List<User> userList = userDAO.getUsers();
		final Map<Integer, User> userMap = new HashMap<Integer, User>(userList.size());
		for (User u : userList)
			userMap.put(u.getId(), u);
		return getJdbcTemplate().query(query, new Object[] { SyncStatus.New.intValue() }, new RowMapper(){
			@Override
			public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException{
				int deltaTypeId = resultSet.getInt("deltatype");
				DeltaType deltaType = DeltaType.getById(deltaTypeId);
				final DeltaHeader deltaHeader = new DeltaHeader(deltaType, userMap.get(resultSet.getInt("creatorid")), null);

				deltaHeader.setId(resultSet.getInt("id"));
				deltaHeader.setSyncStatus(SyncStatus.New);
				deltaHeader.setTimestamp(resultSet.getTimestamp("timestamp"));
				deltaHeader.setSync(resultSet.getInt("issync") != 0);

				Map<DeltaParamIdentifier, DeltaParam> deltaParams = deltaParamDAO.getByDeltaHeader(deltaHeader);
				deltaHeader.setDeltaParameters(deltaParams);
				return deltaHeader;
			}
		});
	}

	@Override
	public void markProcessed(List<DeltaHeader> deltas){
		while (deltas.size() > 0){
			int currentCount = Math.min(deltas.size(), 100);
			for (int i = 0; i < currentCount; i++){
				String sql = "update sys_delta_header set status = " + deltas.get(i).getSyncStatus().intValue()
				        + " where id = " + deltas.get(i).getId();
				getJdbcTemplate().update(sql);
			}
			deltas.subList(0, currentCount).clear();
			log.debug("Execute batch of: " + currentCount);
			log.debug("Deltas are commited");
		}
	}

	@Override
	public DeltaHeader save(final DeltaHeader delta){
		if (DeltaHeader.DO_NOTHING.equals(delta)){
			log.trace("Received DO_NOTHING delta, returning.");
			return delta;
		}

		final String query = "INSERT INTO sys_delta_header(deltatype, status, creatorid, issync) VALUES (?, ?, ?, ?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator(){
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException{
				PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, delta.getDeltaType().intValue());
				ps.setInt(2, delta.getSyncStatus().intValue());
				ps.setInt(3, delta.getCreator().getId());
				ps.setInt(4, delta.isSync() ? 1 : 0);
				return ps;
			}
		}, keyHolder);
		Number generatedId = (Number) keyHolder.getKeys().get("id");
		int newId = generatedId.intValue();
		saveDeltaParameters(delta, newId);
		return get(newId);
	}

	private void saveDeltaParameters(DeltaHeader delta, int generatedKey){
		Map<DeltaParamIdentifier, DeltaParam> deltaParameters = delta.getDeltaParameters();
		Map<DeltaParamIdentifier, DeltaParam> savedParameters = deltaParamDAO.save(deltaParameters, generatedKey);
		delta.setDeltaParameters(savedParameters);
	}

	public DeltaHeader get(int id){
		final String query = "SELECT id, deltatype, \"timestamp\", status, creatorid, issync FROM sys_delta_header where id = ?";
		return (DeltaHeader) getJdbcTemplate().queryForObject(query, new Object[] { id }, new RowMapper(){
			@Override
			public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException{
				int returnedId = resultSet.getInt("id");

				int deltaTypeId = resultSet.getInt("deltatype");
				DeltaType deltaType = DeltaType.getById(deltaTypeId);

				int creatorId = resultSet.getInt("creatorid");
				User user = userDAO.get(creatorId);

				DeltaHeader deltaHeader = new DeltaHeader(deltaType, user, null);

				deltaHeader.setSyncStatus(SyncStatus.fromInt(resultSet.getInt("status")));

				Timestamp timestamp = resultSet.getTimestamp("timestamp");
				deltaHeader.setTimestamp(timestamp);

				resultSet.getInt("issync");

				deltaHeader.setId(returnedId);

				Map<DeltaParamIdentifier, DeltaParam> deltaParams = deltaParamDAO.getByDeltaHeader(deltaHeader);
				deltaHeader.setDeltaParameters(deltaParams);

				return deltaHeader;
			}
		});
	}

	@Override
	public void delete(DeltaHeader delta){
		deltaParamDAO.delete(delta.getDeltaParameters());
		String sql = "DELETE FROM sys_delta_header WHERE id = ?";
		getJdbcTemplate().update(sql, new Object[] { delta.getId() });
	}

	@Override
	public void markProcessed(DeltaHeader delta){
		List<DeltaHeader> result = new ArrayList<DeltaHeader>();
		result.add(delta);
		markProcessed(result);
	}

	@Override
	public int getUnprocessedCount(){
		int count;

		String sql = "SELECT COUNT(*) FROM sys_delta_header WHERE status = ?";
		count = getJdbcTemplate().queryForInt(sql, new Object[] { SyncStatus.New.intValue() });
		return count;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<DeltaHeader> getLastDeltas(int limit, int offset, long lastId){
		String query = "SELECT id, deltatype, timestamp, creatorid, issync FROM sys_delta_header dh"
		        + " WHERE deltatype in (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		if(lastId != -1)
			query += " and id < ?";
		query += " ORDER BY timestamp desc limit ? offset ?";
		List<Object> params = new ArrayList<Object>();
		params.addAll(Arrays.asList((Object)DeltaType.NODE_CREATE.intValue(), DeltaType.NODE_UPDATE.intValue(),
				        DeltaType.EDGE_CREATE.intValue(), DeltaType.EDGE_UPDATE.intValue(), DeltaType.NODE_MERGE.intValue(),
				        DeltaType.OBJECT_DELETE.intValue(), DeltaType.FAVORITE_DELETE.intValue(),
				        DeltaType.FAVORITE_CREATE.intValue(), DeltaType.FAVORITE_UPDATE.intValue(),
				        DeltaType.FAVORITE_COPY.intValue(), DeltaType.FAVORITE_FOLDER_DELETE.intValue(),
				        DeltaType.FAVORITE_FOLDER_CREATE.intValue(), DeltaType.FAVORITE_FOLDER_UPDATE.intValue()));
		if(lastId != -1)
			params.add(lastId);
		params.addAll(Arrays.asList(limit, offset));
		return getJdbcTemplate().query(query, params.toArray(), new RowMapper(){
			@Override
			public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException{
				final int deltaTypeId = resultSet.getInt("deltatype");
				final DeltaType deltaType = DeltaType.getById(deltaTypeId);
				final User user = userDAO.get(resultSet.getInt("creatorid"));

				final DeltaHeader deltaHeader = new DeltaHeader(deltaType, user, null);

				deltaHeader.setId(resultSet.getInt("id"));
				deltaHeader.setTimestamp(resultSet.getTimestamp("timestamp"));
				deltaHeader.setSync(resultSet.getInt("issync") != 0);
				return deltaHeader;
			}
		});
	}
}
