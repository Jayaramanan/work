package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.dao.UserGroupDAO;

public class UserGroupDAOImpl extends JdbcDaoSupport implements UserGroupDAO{

	@Override
	public void save(int id, int grId){
        final String sql = 	"insert into sys_user_group(userid, groupid) values (?, ?)";
        getJdbcTemplate().update(sql, new Object[]{id, grId});
	}

	@Override
	public Map<Integer, Integer> getUserGroups(){
        final String sql = "select userid, groupid from sys_user_group";
        List<Integer[]> list = getJdbcTemplate().query(sql, new RowMapper() {
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Integer[]{rs.getInt(1), rs.getInt(2)};
            }
        });
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
        for(Integer[] item : list)
                result.put(item[0], item[1]);
        return result;
	}

}
