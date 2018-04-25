package com.ni3.ag.navigator.server.cache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.server.datasource.AttributeDataSource;
import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class SrcIdToIdCacheImpl extends JdbcDaoSupport implements SrcIdToIdCache {
	private static final Logger log = Logger.getLogger(SrcIdToIdCacheImpl.class);
	private Map<String, AttributeDataSource> attributeDataSources;
	private Map<String, Integer> srcId2Id;
	private Map<Integer, String> id2SrcId;

	public void setAttributeDataSources(Map<String, AttributeDataSource> attributeDataSources){
		this.attributeDataSources = attributeDataSources;
	}

	@SuppressWarnings("unchecked")
	public void init(){
		srcId2Id = new HashMap<String, Integer>();
		id2SrcId = new HashMap<Integer, String>();
		String sql = "select tablename from pg_tables where tablename ilike 'usr_schema_%'";
		log.debug(sql);
		List<String> tables = getJdbcTemplate().query(sql, new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
				return rs.getString(1);
			}
		});
		for (String tableName : tables){
			sql = "select u.id, u.srcid from " + tableName + " u left join cis_objects o on o.id=u.id where o.status in (0, 1)";
			getJdbcTemplate().query(sql, new RowMapper(){
				@Override
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
					int id = rs.getInt("id");
					String src = rs.getString("srcid");
					srcId2Id.put(src, id);
					id2SrcId.put(id, src);
					return null;
				}
			});
		}
	}

	@Override
	public int getId(String srcId, ObjectDefinition od){
		if (!srcId2Id.containsKey(srcId))
			return -1;
		return srcId2Id.get(srcId);
	}

	@Override
	public String getSrcId(Integer id){
		return id2SrcId.get(id);
	}

	@Override
	public void add(ObjectDefinition entity, int id, String newSrcId){
		srcId2Id.put(newSrcId, id);
		id2SrcId.put(id, newSrcId);
		Attribute attribute = entity.getAttribute("srcid");
		if(attribute == null){
			log.error("Error save mapping " + id  + "->" + newSrcId);
			return;
		}
		AttributeDataSource attributeDataSource = attributeDataSources.get(attribute.getDataSource());
		Map<Attribute, String> values = new HashMap<Attribute, String>();
		values.put(attribute, newSrcId);
		attributeDataSource.saveOrUpdate(id, values);
	}
}
