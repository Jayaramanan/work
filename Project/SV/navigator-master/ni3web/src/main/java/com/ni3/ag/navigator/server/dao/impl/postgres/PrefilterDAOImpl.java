package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.dao.PrefilterDAO;
import com.ni3.ag.navigator.shared.domain.Prefilter;

public class PrefilterDAOImpl extends JdbcDaoSupport implements PrefilterDAO {
    @Override
    public List<Prefilter> getPrefilter(int groupId, int schemaId) {
        final String sql = "SELECT p.id as id, p.groupid as groupid, " +
                "o.schemaid as schemaid, a.ObjectDefinitionID as objectid, " +
                "a.ID as attributeid, p.PredefID as predefid " +
                "FROM SYS_GROUP_PREFILTER p,CHT_PredefinedAttributes prd, " +
                "SYS_OBJECT_ATTRIBUTES a, sys_object o " +
                "WHERE p.PredefID=prd.ID AND prd.AttributeID=a.ID " +
                "and o.id=a.ObjectDefinitionID and p.GroupID = ? and o.schemaid = ?";
        return getJdbcTemplate().query(sql, new Object[]{groupId, schemaId}, new RowMapper() {
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                Prefilter pf = new Prefilter();
                pf.setId(rs.getInt("id"));
                pf.setGroupId(rs.getInt("groupid"));
                pf.setSchemaId(rs.getInt("schemaid"));
                pf.setObjectDefinitionId(rs.getInt("objectid"));
                pf.setAttributeId(rs.getInt("attributeid"));
                pf.setPredefinedId(rs.getInt("predefid"));
                return pf;
            }
        });
    }
}
