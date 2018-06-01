package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.dao.UncommittedDeltasDAO;
import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.server.domain.SyncStatus;

public class UncommittedDeltasDAOImpl extends JdbcDaoSupport implements UncommittedDeltasDAO{
	@Override
	public List<DeltaHeader> getUncommittedDeltas(){
        final String sql = "select id from sys_uncommitted_deltas";
        return getJdbcTemplate().query(sql, new RowMapper() {
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new DeltaHeader(rs.getLong(1), SyncStatus.Processed.intValue());
            }
        });
	}

	@Override
	public void clearUncommitted(){
        final String sql = "delete from sys_uncommitted_deltas";
        getJdbcTemplate().update(sql);
	}

	@Override
	public void save(DeltaHeader deltaHeader){
        final String sql = "insert into sys_uncommitted_deltas(id) values (?)";
        getJdbcTemplate().update(sql, new Object[]{deltaHeader.getId()});
	}
}
