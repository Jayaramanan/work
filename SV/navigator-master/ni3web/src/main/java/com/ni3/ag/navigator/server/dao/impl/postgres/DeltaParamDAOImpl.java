package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.ni3.ag.navigator.server.dao.DeltaParamDAO;
import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.server.domain.DeltaParam;
import com.ni3.ag.navigator.server.domain.DeltaParamIdentifier;

public class DeltaParamDAOImpl extends JdbcDaoSupport implements DeltaParamDAO{
	private RowMapper deltaParamRowMapper = new RowMapper(){
		@Override
		public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException{
			long returnedId = resultSet.getLong("id");
			String name = resultSet.getString("name");
			String value = resultSet.getString("value");

			DeltaParam deltaParam = new DeltaParam(DeltaParamIdentifier.getById(name), value);
			// in case of simple attribute id
			if (deltaParam.getName() == null)
				deltaParam.setName(new DeltaParamIdentifier(name));
			deltaParam.setId(returnedId);
			return deltaParam;
		}
	};

	@Override
	public DeltaParam save(final DeltaParam param, final int parentId){
		final String sql = "INSERT INTO sys_delta_params( deltaid, \"name\", \"value\") VALUES (?, ?, ?)";
		PreparedStatementCreator psc = new PreparedStatementCreator(){
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException{
				PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
				ps.setInt(1, parentId);
				ps.setString(2, param.getName().getIdentifier());
				ps.setString(3, param.getValue());

				return ps;
			}
		};

		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(psc, keyHolder);
		Number generatedId = (Number) keyHolder.getKeys().get("id");
		int generatedKey = generatedId.intValue();

		return get(generatedKey);
	}

	public DeltaParam get(long id){
		final String sql = "SELECT id, \"name\", \"value\" FROM sys_delta_params where id = ?";
		return (DeltaParam) getJdbcTemplate().queryForObject(sql, new Object[] { id }, deltaParamRowMapper);
	}

	@Override
	public Map<DeltaParamIdentifier, DeltaParam> save(Map<DeltaParamIdentifier, DeltaParam> params, int parentId){
		Map<DeltaParamIdentifier, DeltaParam> res = new HashMap<DeltaParamIdentifier, DeltaParam>();

		for (DeltaParam param : params.values()){
			DeltaParam savedDeltaParam = save(param, parentId);
			res.put(savedDeltaParam.getName(), savedDeltaParam);
		}
		return res;
	}

	@Override
	public void delete(Map<DeltaParamIdentifier, DeltaParam> deltaParameters){
		if (deltaParameters != null){
			for (DeltaParam param : deltaParameters.values()){
				delete(param);
			}
		}
	}

	@Override
	public void delete(DeltaParam parameter){
		final String sql = "DELETE FROM sys_delta_params WHERE id = ?";
		getJdbcTemplate().update(sql, new Object[] { parameter.getId() });
	}

	@Override
	public Map<DeltaParamIdentifier, DeltaParam> getByDeltaHeader(DeltaHeader deltaHeader){
		Map<DeltaParamIdentifier, DeltaParam> res = new HashMap<DeltaParamIdentifier, DeltaParam>();
		final String sql = "SELECT id, \"name\", \"value\" FROM sys_delta_params where deltaid = ?";
		List<DeltaParam> list = getJdbcTemplate().query(sql, new Object[] { deltaHeader.getId() }, deltaParamRowMapper);
		for (DeltaParam dp : list){
			res.put(dp.getName(), dp);
		}

		return res;
	}
}
