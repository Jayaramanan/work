package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.dao.UserDAO;
import com.ni3.ag.navigator.shared.domain.User;

public class UserDAOImpl extends JdbcDaoSupport implements UserDAO{

	private RowMapper userRowMapper = new RowMapper(){
		@Override
		public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException{
			User user = new User();
			user.setId(resultSet.getInt("id"));
			user.setFirstName(resultSet.getString("firstname"));
			user.setLastName(resultSet.getString("lastname"));
			user.setUserName(resultSet.getString("username"));
			user.setPassword(resultSet.getString("password"));
			user.setSID(resultSet.getString("sid"));
			user.setActive(resultSet.getBoolean("isactive"));
			user.seteMail(resultSet.getString("email"));
			user.setHasOfflineClient(resultSet.getBoolean("hasofflineclient"));
			user.setEtlUser(resultSet.getString("etluser"));
			user.setEtlPassword(resultSet.getString("etlpassword"));

			return user;

		}
	};

	@Override
	public List<User> findByEmail(final String email){
		final String sql = "SELECT id, firstname, lastname, username, password, sid, isactive, email, "
		        + "hasofflineclient, etluser, etlpassword FROM sys_user WHERE lower(email) = lower(?)";
		return getJdbcTemplate().query(sql, new Object[] { email }, userRowMapper);
	}

	@Override
	public User get(final Integer userId){
		final String sql = "SELECT id, firstname, lastname, username, password, sid, isactive, email, hasofflineclient, "
		        + "etluser, etlpassword FROM sys_user WHERE id=?";
		List<?> users = getJdbcTemplate().query(sql, new Object[] { userId }, userRowMapper);
		return users.isEmpty() ? null : (User) users.get(0);
	}

	@Override
	public User getBySID(final String sid){
		final String sql = "SELECT id, firstname, lastname, username, password, sid, isactive, email, hasofflineclient, "
		        + "etluser, etlpassword FROM sys_user WHERE sid=?";
		List<?> users = getJdbcTemplate().query(sql, new Object[] { sid }, userRowMapper);
		return users.isEmpty() ? null : (User) users.get(0);
	}

	@Override
	public User getByUsername(final String username){
		if (username.charAt(0) == '*'){
			return null;
		}

		final String sql = "SELECT id, firstname, lastname, username, password, sid, isactive, email, hasofflineclient, "
		        + "etluser, etlpassword FROM sys_user WHERE lower(username)=?";
		List<?> users = getJdbcTemplate().query(sql, new Object[] { username.toLowerCase() }, userRowMapper);
		return users.isEmpty() ? null : (User) users.get(0);
	}

	@Override
	public User getByUsernamePassword(final String username, final String password){
		final String sql = "SELECT id, firstname, lastname, username, password, sid, isactive, email, hasofflineclient, "
		        + "etluser, etlpassword FROM sys_user WHERE lower(username)=? AND password=?";
		List<?> users = getJdbcTemplate().query(sql, new Object[] { username.toLowerCase(), password }, userRowMapper);
		return users.isEmpty() ? null : (User) users.get(0);
	}

	@Override
	public void update(final User user){
		final String sql = "UPDATE sys_user SET firstname=?, lastname=?, username=?, password=?, sid=?, isactive=?, "
		        + "email=?, hasofflineclient=?, etluser=?, etlpassword=? WHERE id=?";
		getJdbcTemplate().update(
		        sql,
		        new Object[] { user.getFirstName(), user.getLastName(), user.getUserName(), user.getPassword(),
		                user.getSID(), user.isActive() ? 1 : 0, user.geteMail(), user.getHasOfflineClient() ? 1 : 0,
		                user.getEtlUser(), user.getEtlPassword(), user.getId() });
	}

	@Override
	public List<User> getUsers(){
		final String sql = "SELECT id, firstname, lastname, username, password, sid, isactive, email, hasofflineclient, "
		        + "etluser, etlpassword FROM sys_user";
		return getJdbcTemplate().query(sql, userRowMapper);
	}

	@Override
	public List<User> getOfflineUsers(){
		final String sql = "SELECT id, firstname, lastname, username, password, sid, isactive, email, hasofflineclient, "
		        + "etluser, etlpassword FROM sys_user where hasofflineclient = 1";
		return getJdbcTemplate().query(sql, userRowMapper);
	}

	@Override
	public void save(final User u){
		final String INSERT = "insert into sys_user(id, firstname, lastname, username, password, sid, isactive, email, "
		        + "hasofflineclient, etluser, etlpassword) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		getJdbcTemplate().update(
		        INSERT,
		        new Object[] { u.getId(), u.getFirstName(), u.getLastName(), u.getUserName(), u.getPassword(), u.getSID(),
		                u.isActive() ? 1 : 0, u.geteMail(), u.getHasOfflineClient() ? 1 : 0, u.getEtlUser(),
		                u.getEtlPassword() });
	}
}
