package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.dao.ObjectChartDAO;
import com.ni3.ag.navigator.shared.domain.ChartType;
import com.ni3.ag.navigator.shared.domain.DisplayOperation;
import com.ni3.ag.navigator.shared.domain.ObjectChart;

public class ObjectChartDAOImpl extends JdbcDaoSupport implements ObjectChartDAO {
    private RowMapper objectChartMapper = new RowMapper() {
        @Override
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            ObjectChart oc = new ObjectChart();
            oc.setId(rs.getInt(1));
            oc.setObjectId(rs.getInt(2));
            oc.setChartId(rs.getInt(3));
            oc.setMinValue(rs.getInt(4));
            oc.setMaxValue(rs.getInt(5));
            oc.setMinScale(rs.getDouble(6));
            oc.setMaxScale(rs.getDouble(7));
            oc.setLabelInUse(rs.getInt(8) != 0);
            oc.setLabelFont(rs.getString(9));
            oc.setNumberFormat(rs.getString(10));
            oc.setDisplayOperation(DisplayOperation.fromInt(rs.getInt(11)));
            oc.setChartType(ChartType.fromInt(rs.getInt(12)));
            oc.setValueDisplayed(rs.getInt(13) != 0);
            oc.setFontColor(rs.getString(14));
            return oc;
        }
    };

    @Override
    public List<ObjectChart> getObjectCharts(int chartId) {
        final String sql = "SELECT id, ObjectID, chartid, minvalue, maxvalue, minscale, maxscale, " +
                "labelinuse, labelfontsize, numberformat, DisplayOperation, " +
                "ChartType, isValueDisplayed, fontcolor " +
                "FROM SYS_OBJECT_CHART " +
                "WHERE ChartID=?";
        return getJdbcTemplate().query(sql, new Object[]{chartId}, objectChartMapper);
    }
}
