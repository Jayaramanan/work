package com.ni3.ag.navigator.server.dao.impl.postgres;

import com.ni3.ag.navigator.server.dao.GroupDAO;
import com.ni3.ag.navigator.server.domain.Group;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class GroupDAOImpl extends JdbcDaoSupport implements GroupDAO{
    private static final String SELECT_ALL = "select id, nodescope, edgescope from sys_group order by id";
    private static final String SELECT_BY_ID = "select id, nodescope, edgescope from sys_group where id = ?";
    private static final String CREATE_GROUP = "insert into sys_group (id) values (?)";
    private static final String SELECT_BY_USER = "select id, nodescope, edgescope from sys_group g, " +
            "sys_user_group ug where ug.groupid = g.id and ug.userid = ?";
    private static RowMapper groupRowMapper = new RowMapper(){
        @Override
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
            final Group g = new Group(rs.getInt(1));
            g.setNodeScope(rs.getString(2));
            g.setEdgeScope(rs.getString(3));
            return g;
        }
    };

    @Override
    public Group get(final int id){
        return (Group) getJdbcTemplate().queryForObject(SELECT_BY_ID, new Object[]{id}, groupRowMapper);
    }

    @Override
    public List<Group> getGroups(){
        return getJdbcTemplate().query(SELECT_ALL, groupRowMapper);
    }

    @Override
    public Group getByUser(Integer id){
        return (Group) getJdbcTemplate().queryForObject(SELECT_BY_USER, new Object[]{id}, groupRowMapper);
    }

    @Override
    public void save(final Group g){
        getJdbcTemplate().update(CREATE_GROUP, new Object[]{g.getId()});
    }

    public long getCount(){
        return getJdbcTemplate().queryForLong("select count(*) from sys_group");
    }
}
