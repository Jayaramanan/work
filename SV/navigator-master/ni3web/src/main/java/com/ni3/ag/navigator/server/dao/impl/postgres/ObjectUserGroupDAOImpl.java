package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.server.dao.ObjectUserGroupDAO;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class ObjectUserGroupDAOImpl extends JdbcDaoSupport implements ObjectUserGroupDAO {
    private final static Logger log = Logger.getLogger(ObjectUserGroupDAOImpl.class);

    @Override
    public Map<Integer, List<Integer>> getReadACL() {
        final Map<Integer, List<Integer>> ret = new HashMap<Integer, List<Integer>>();
        final String query = "SELECT objectid,groupid FROM sys_object_user_group WHERE "
                + "canread=1 ORDER BY groupid";
        getJdbcTemplate().query(query, new RowMapper() {
            @Override
            public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                final int objectId = resultSet.getInt("objectid");
                final int groupId = resultSet.getInt("groupid");
                if (ret.containsKey(groupId)) {
                    ret.get(groupId).add(objectId);
                } else {
                    final ArrayList<Integer> arrayList = new ArrayList<Integer>(1);
                    arrayList.add(objectId);
                    ret.put(groupId, arrayList);
                }
                return null;
            }
        });
        return ret;
    }
}
