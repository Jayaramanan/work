package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.dao.PaletteDAO;
import com.ni3.ag.navigator.server.domain.Palette;

public class PaletteDAOImpl extends JdbcDaoSupport implements PaletteDAO{
	private static final Logger log = Logger.getLogger(PaletteDAOImpl.class);
    private RowMapper paletteRowMapper = new RowMapper() {
        @Override
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            Palette p = new Palette();
          		p.setId(rs.getInt(1));
          		p.setSequence(rs.getInt(2));
          		p.setColorOrder(rs.getInt(3));
          		p.setColor(rs.getString(4));
          		return p;
        }
    };

    @Override
	public List<Palette> getPalette(int id){
        final String sql = "SELECT paletteid, sequence, colororder, color FROM SYS_Palette WHERE PaletteID=? " +
                "ORDER BY 1,2";
        return getJdbcTemplate().query(sql, new Object[]{id}, paletteRowMapper);
	}
}
