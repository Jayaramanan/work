package com.ni3.ag.navigator.server.cache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.server.datasource.postgres.DefaultPostgreSQLDataSource;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.domain.Schema;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class SrcIdToFakeIdCacheImpl extends JdbcDaoSupport implements SrcIdToIdCache{
	private static final Logger log = Logger.getLogger(SrcIdToFakeIdCacheImpl.class);
	private static int uniqIdBase = 1;
	private Map<Integer, String> idToSrcIdMap = new HashMap<Integer, String>();
	private Map<String, Integer> srcIdToIdMap = new HashMap<String, Integer>();
	private Map<Integer, Integer> idToEntityIdMap = new HashMap<Integer, Integer>();

	@Override
	public int getId(String srcId, ObjectDefinition entity){
		if (!srcIdToIdMap.containsKey(srcId)){
			log.debug("Requested srcId is not mapped");
			int nextUniqId = nextId();
			log.debug("Generating new id: " + srcId + " -> " + nextUniqId + "(" + entity.getName() + ")");
			srcIdToIdMap.put(srcId, nextUniqId);
			idToSrcIdMap.put(nextUniqId, srcId);
			idToEntityIdMap.put(nextUniqId, entity.getId());
			serializeToDB(entity, nextUniqId, srcId);
		}
		return srcIdToIdMap.get(srcId);
	}

	private void serializeToDB(ObjectDefinition entity, int nextUniqId, String srcId){
		final String tableName = DefaultPostgreSQLDataSource.getTableNameForEntity(entity);
		final String sql = "select (select count(*) from " + tableName + "  where id = ?), " +
				"(select count(*) from " + tableName + "  where srcid = ?), " +
				"(select count(*) from " + tableName + "  where id = ? and srcid = ?)";
		int[] counts = (int[]) getJdbcTemplate().queryForObject(sql, new Object[]{nextUniqId, srcId, nextUniqId, srcId}, new RowMapper(){
			@Override
			public Object mapRow(ResultSet resultSet, int i) throws SQLException{
				return new int[]{resultSet.getInt(1), resultSet.getInt(2), resultSet.getInt(3)};
			}
		});
		if(counts[0] == 0 && counts[1] == 0 && counts[2] == 0)
			createMappingInDB(tableName, nextUniqId, srcId);
		else if(counts[1] == 1 && counts[1] == 1 && counts[2] == 1)
			log.debug("Already mapped: " + srcId + " -> " + nextUniqId);
		else{
			//010 - srcId id is mapped to another id
			//100 - id mapped to another srcid
			//110 - both have mapping bu they does not match
			if(counts[1] == 1)
				throw new RuntimeException("Src id " + srcId + "is mapped to another id");
			else if(counts[0] == 1)
				throw new RuntimeException("Id " + nextUniqId + " is mapped to another srcId");
			else
				throw new RuntimeException("Mapping error: " + srcId + " -> " + nextUniqId);
		}
	}

	private void createMappingInDB(String tableName, int nextUniqId, String srcId){
		final String sql = "insert into " + tableName + "(id, srcid) values (?, ?)";
		log.debug(sql);
		getJdbcTemplate().update(sql, new Object[]{nextUniqId, srcId});
	}

	private static int nextId(){
		return ++uniqIdBase;
	}

	@Override
	public String getSrcId(Integer id){
		if (!idToSrcIdMap.containsKey(id)){
			log.error("Requested id is not in map: " + id);
			return null;
		}
		return idToSrcIdMap.get(id);
	}

	@Override
	public void add(ObjectDefinition entity, int id, String newSrcId){
		log.debug("Adding new object for entity " + entity.getName());
		log.debug("mapping: " + newSrcId + " -> " + id);
		idToSrcIdMap.put(id, newSrcId);
		srcIdToIdMap.put(newSrcId, id);
		idToEntityIdMap.put(id, entity.getId());
		serializeToDB(entity, id, newSrcId);
	}

	public int getEntityIdById(int id){
		if (!idToEntityIdMap.containsKey(id)){
			log.error("Id cache does not know anything about id: " + id);
			throw new RuntimeException("Unknown type for id: " + id);
		}
		return idToEntityIdMap.get(id);
	}

	public void init(List<Schema> schemas){
		log.debug("Initializing src to id map");
		for (Schema schema : schemas){
			log.debug("Initialising mapping for schema: " + schema.getName());
			for (final ObjectDefinition od : schema.getDefinitions()){
				log.debug("\tInitializing mapping for object: " + od.getName());
				String tableName = DefaultPostgreSQLDataSource.getTableNameForEntity(od);
				log.debug("\tTableName: " + tableName);
				final String sql = "select id, srcid from " + tableName;
				log.debug("\t\t" + sql);
				getJdbcTemplate().query(sql, new RowMapper(){
					@Override
					public Object mapRow(ResultSet resultSet, int i) throws SQLException{
						int id = resultSet.getInt(1);
						String srcId = resultSet.getString(2);
						idToSrcIdMap.put(id, srcId);
						srcIdToIdMap.put(srcId, id);
						idToEntityIdMap.put(id, od.getId());
						if(uniqIdBase < id)
							uniqIdBase = id + 1;
						return null;
					}
				});
			}
		}
	}
}
