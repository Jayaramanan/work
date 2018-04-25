package com.ni3.ag.navigator.server.dao.impl;

import com.ni3.ag.navigator.server.dao.DeltaHeaderUserDAO;
import com.ni3.ag.navigator.server.dao.DeltaParamDAO;
import com.ni3.ag.navigator.server.dao.UserDAO;
import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.server.domain.SyncStatus;
import com.ni3.ag.navigator.shared.domain.DeltaType;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DeltaHeaderUserDAOImpl extends JdbcDaoSupport implements DeltaHeaderUserDAO{
	private static final Logger log = Logger.getLogger(DeltaHeaderUserDAOImpl.class);
	private UserDAO userDAO;
	private DeltaParamDAO deltaParamDAO;

	public void setDeltaParamDAO(DeltaParamDAO deltaParamDAO){
		this.deltaParamDAO = deltaParamDAO;
	}

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	@Override
	public Long getUnprocessedCountForUser(int userId){
		final String sql = "select count(*) from sys_delta_user where target_user_id = ? and processed = ?";
		return getJdbcTemplate().queryForLong(sql, new Object[] { userId, SyncStatus.New.intValue() });
	}

	@Override
	public List<DeltaHeader> getUnprocessedForUser(int userId, int limit){
		String sql = "select du.id, dh.deltatype, dh.timestamp, du.processed, dh.creatorid, dh.issync, dh.id "
		        + "from sys_delta_header dh left join sys_delta_user du on dh.id = du.delta_header_id "
		        + "where du.processed = ? and du.target_user_id = ? order by dh.id";
		if (limit > 0)
			sql += " limit " + limit;

		return getJdbcTemplate().query(sql, new Object[] { SyncStatus.New.intValue(), userId }, new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
				long userDeltaId = rs.getLong(1);
				DeltaHeader dh = new DeltaHeader(0L);
				dh.setDeltaType(DeltaType.getById(rs.getInt(2)));
				dh.setTimestamp(rs.getTimestamp(3));
				dh.setSyncStatus(SyncStatus.fromInt(rs.getInt(4)));
				dh.setCreator(userDAO.get(rs.getInt(5)));
				dh.setSync(rs.getInt(6) == 1);
				long deltaHeaderId = rs.getLong(7);
				dh.setId(deltaHeaderId);
				dh.setDeltaParameters(deltaParamDAO.getByDeltaHeader(dh));
				dh.setId(userDeltaId);
				log.debug("GOT: " + dh.toString());
				return dh;

			}
		});
	}

	@Override
	public void markUserDeltasAsProcessed(List<DeltaHeader> deltas){
		while (deltas.size() > 0){
			int currentCount = Math.min(deltas.size(), 100);
			for (int i = 0; i < currentCount; i++){
				final String sql = "update sys_delta_user set processed = " + deltas.get(i).getSyncStatus().intValue()
				        + " where id = " + deltas.get(i).getId();
				getJdbcTemplate().update(sql);
			}
			deltas.subList(0, currentCount).clear();
			log.debug("Detlas are commited");
		}
	}

	@Override
	public void create(DeltaHeader delta, Integer userId){
		final String query = "INSERT INTO sys_delta_user (delta_header_id, target_user_id, processed) VALUES (?, ?, ?)";
		getJdbcTemplate().update(query, new Object[] { delta.getId(), userId, SyncStatus.New.intValue() });
	}
}
