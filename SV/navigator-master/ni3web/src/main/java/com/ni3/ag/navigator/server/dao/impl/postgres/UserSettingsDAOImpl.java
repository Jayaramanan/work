package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.dao.UserSettingsDAO;
import com.ni3.ag.navigator.shared.domain.UserSetting;

public class UserSettingsDAOImpl extends JdbcDaoSupport implements UserSettingsDAO{
	private static final Logger log = Logger.getLogger(UserSettingsDAOImpl.class);
	private RowMapper settingRowMapper = new RowMapper(){
		@Override
		public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException{
			UserSetting ret = new UserSetting();
			ret.setId(resultSet.getInt("id"));
			ret.setSection(resultSet.getString("section"));
			ret.setProperty(resultSet.getString("prop"));
			ret.setValue(resultSet.getString("value"));
			return ret;
		}
	};

	@Override
	public UserSetting get(Integer userId, String property){
		final String sql = "SELECT id,section,prop,value FROM sys_settings_user WHERE id=? AND prop=?";
		List<?> settings = getJdbcTemplate().query(sql, new Object[] { userId, property }, settingRowMapper);
		return settings.isEmpty() ? null : (UserSetting) settings.get(0);
	}

	@Override
	public void save(UserSetting settings){
		String sql = "DELETE FROM sys_settings_user WHERE id=? AND prop=?";
		getJdbcTemplate().update(sql, new Object[] { settings.getId(), settings.getProperty() });
		sql = "INSERT INTO sys_settings_user (id,section,prop,value) VALUES (?, ?, ?, ?)";
		getJdbcTemplate().update(sql,
				new Object[] { settings.getId(), settings.getSection(), settings.getProperty(), settings.getValue() });
	}

	@Override
	public void delete(UserSetting settings){
		final String sql = "DELETE FROM sys_settings_user WHERE id=? AND prop=?";
		getJdbcTemplate().update(sql, new Object[] { settings.getId(), settings.getProperty() });
	}

	@Override
	public List<UserSetting> getSettingsForUser(int userId){
		final String sql = "select " + userId + " as id, section, prop, value from sys_user_settings where userid = " + "?";
		return getJdbcTemplate().query(sql, new Object[] { userId }, settingRowMapper);
	}

	@Override
	public UserSetting getSettingForUser(int userId, String section, String property){
		final String sql = "select " + userId + " as id, section, prop, value from sys_user_settings"
				+ " where userid = ? and section = ? and prop = ?";
		List<?> settings = getJdbcTemplate().query(sql, new Object[] { userId, section, property }, settingRowMapper);
		return settings.isEmpty() ? null : (UserSetting) settings.get(0);
	}
}
