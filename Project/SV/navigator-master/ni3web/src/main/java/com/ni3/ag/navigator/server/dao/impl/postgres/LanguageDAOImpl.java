package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.dao.LanguageDAO;
import com.ni3.ag.navigator.shared.domain.LanguageItem;

public class LanguageDAOImpl extends JdbcDaoSupport implements LanguageDAO{
    private RowMapper languageRowMapper = new RowMapper() {
        @Override
        public Object mapRow(ResultSet resultset, int rowNum) throws SQLException {
            LanguageItem li = new LanguageItem();
            li.setId(resultset.getInt(1));
            li.setProperty(resultset.getString(2));
            li.setValue(resultset.getString(3));
            return li;
        }
    };

    @Override
	public List<LanguageItem> getTranslations(int id){
			final String sql = "select languageid, prop, value from sys_user_language where languageid = ?";
        return getJdbcTemplate().query(sql, new Object[]{id}, languageRowMapper); 
	}
}
