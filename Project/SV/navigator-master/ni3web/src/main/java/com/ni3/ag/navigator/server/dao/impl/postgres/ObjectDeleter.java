package com.ni3.ag.navigator.server.dao.impl.postgres;

import com.ni3.ag.navigator.server.dao.ObjectDefinitionDAO;
import com.ni3.ag.navigator.server.dao.ObjectDisposer;
import com.ni3.ag.navigator.server.domain.CisObject;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class ObjectDeleter extends JdbcDaoSupport implements ObjectDisposer {
	private ObjectDefinitionDAO objectDefinitionDAO;
    private static final Logger log = Logger.getLogger(ObjectDeleter.class);

	public void setObjectDefinitionDAO(ObjectDefinitionDAO objectDefinitionDAO){
		this.objectDefinitionDAO = objectDefinitionDAO;
	}

	//TODO reimplement physical deletion objects from database
	@Override
	public void dispose(CisObject co, ObjectDefinition od) {
//     String sql = "delete from " + od.getTableName() + " where id = ?";
//     getJdbcTemplate().update(sql, new Object[]{co.getId()});
//
//     if (od.isNode())
//         sql = "delete from cis_nodes  where id = ?";
//     else if (od.isEdge())
//         sql = "delete from cis_edges  where id = ?";
//     else
//         log.error("Unknown object type (not node/not edge");
//     getJdbcTemplate().update(sql, new Object[]{co.getId()});
//
//     sql = "delete from cis_objects where id = ?";
//     getJdbcTemplate().update(sql, new Object[]{co.getId()});
 }
}
