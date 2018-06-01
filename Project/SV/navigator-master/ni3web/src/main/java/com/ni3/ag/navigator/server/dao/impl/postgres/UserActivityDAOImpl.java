package com.ni3.ag.navigator.server.dao.impl.postgres;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.dao.UserActivityDAO;

public class UserActivityDAOImpl extends JdbcDaoSupport implements UserActivityDAO{

	@Override
	public void save(Integer userId, String activityType, String message, String remoteAddr, String httpHeader){
        final String sql = "INSERT INTO SYS_USER_ACTIVITY (IPAddress,Request,UserID,ActivityType,HTTPHeader) " +
                "VALUES (?,?,?,?,?)";
        getJdbcTemplate().update(sql, new Object[]{remoteAddr, message, userId, activityType, httpHeader});
	}
}
