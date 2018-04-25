/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.ni3.ag.adminconsole.domain.Module;
import com.ni3.ag.navigator.server.domain.SyncStatus;
import com.ni3.ag.navigator.server.services.ThickClientModuleService;
import com.ni3.ag.navigator.server.servlets.util.SimpleTLVEncoder;
import com.ni3.ag.navigator.shared.domain.User;

public class ThickClientModuleServiceImpl extends JdbcDaoSupport implements ThickClientModuleService{
	private static final Logger log = Logger.getLogger(ThickClientModuleServiceImpl.class);

	@Override
	public Module getModule(String module, User user, final String modulesPath){
		String sql = "select m.id, m.name, m.path, m.hash, m.version, m.archive_pass, " + "" + "m.params "
		        + "from sys_module_user um, sys_module_list m where um.target_module_id = m.id and " + "um" + ".userid = ?"
		        + " and lower(m.name) = ?";
		Module m = (Module) getJdbcTemplate().queryForObject(sql, new Object[] { user.getId(), module.toLowerCase() },
		        new RowMapper(){
			        @Override
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
				        Module m = new Module();
				        m.setId(rs.getInt(1));
				        m.setName(rs.getString(2));
				        String realPath = modulesPath + rs.getString(3);
				        m.setPath(realPath);
				        m.setHash(rs.getString(4));
				        m.setVersion(rs.getString(5));
				        m.setArchivePassword(rs.getString(6));
				        m.setParams(rs.getString(7));
				        return m;
			        }
		        });

		if (m.getParams() == null){
			m.setParams("");
		}

		if (log.isDebugEnabled()){
			log.debug("got module from database " + m.toString());
		}
		return m;
	}

	@Override
	public void processGetCurrentVersions(HttpServletResponse response, User user) throws IOException{
		log.debug("processGetCurrentVersions");
		String sql = "select m.name, m.version, m.params from sys_module_user mu left join sys_module_list m" + " on mu"
		        + ".target_module_id = m.id where mu.userid = ?";
		log.debug(sql);
		OutputStream out = response.getOutputStream();
		SimpleTLVEncoder encoder = new SimpleTLVEncoder(out);
		final Map<String, Module> versions = new HashMap<String, Module>();
		getJdbcTemplate().query(sql, new Object[] { user.getId() }, new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
				Module m = new Module();
				m.setName(rs.getString(1));
				m.setVersion(rs.getString(2));
				m.setParams(rs.getString(3));
				if (m.getParams() == null){
					m.setParams("");
				}
				versions.put(m.getName(), m);
				return m;
			}
		});

		if (log.isDebugEnabled()){
			log.debug("VERSIONS:\n" + versions);
		}
		encoder.writeInt(versions.size());
		for (String name : versions.keySet()){
			Module m = versions.get(name);
			encoder.writeString(name);
			encoder.writeString(m.getVersion());
			Properties pp = new Properties();
			pp.load(new StringReader(m.getParams()));
			String locked = pp.getProperty(Module.DB_DUMP_PARAM_LOCKED);
			if ("1".equals(locked)){
				encoder.writeInt(1);
			} else{
				encoder.writeInt(0);
			}
		}
		log.debug("sent to user");
	}

	@Override
	public void processCommitModule(User user, String module, String version, HttpServletResponse response)
	        throws IOException{
		log.debug("processCommitModule");

		String sql1 = "select id, params from sys_module_list where lower(name) = ? and version = ?";
		if (log.isDebugEnabled()){
			log.debug("executing: " + sql1);
		}
		final SqlRowSet rs1 = getJdbcTemplate().queryForRowSet(sql1, new Object[] { module.toLowerCase(), version });
		if (!rs1.next()){
			log.error("error get current module");
			return;
		}
		int modid = rs1.getInt("id");
		String params = rs1.getString("params");
		params = params == null ? "" : params.trim();
		if (log.isDebugEnabled()){
			log.debug("module id resolved = " + modid);
			log.debug("module params: " + params);
		}

		String sql2 = "update sys_module_user set current_module_id = target_module_id where userid = " + user.getId() + " "
		        + "and" + " target_module_id = " + modid;
		if (module.equals(Module.DB_DUMP)){
			if (!params.isEmpty()){
				params += "\n";
			}
			params += Module.DB_DUMP_PARAM_LOCKED + "=1";
		} else if (module.equals(Module.DB_SCRIPT)){
			sql2 = "delete from sys_module_user where userid = " + user.getId() + " and target_module_id = " + modid;
		}
		log.debug(sql2);
		getJdbcTemplate().update(sql2);

		String sql3 = "update sys_module_list set params = ? where id = ?";
		log.debug(sql3);
		getJdbcTemplate().update(sql3, new Object[] { params, modid });

		log.debug("marked module as updated for current user");
		if (module.equals(Module.DB_DUMP)){
			markDeltasAsProcessed(user, params);
		}

		log.debug("commit success");
	}

	private void markDeltasAsProcessed(User user, String params) throws IOException{
		if (log.isDebugEnabled()){
			log.debug("marking deltas for user " + user.getId() + " as process");
		}

		if (log.isDebugEnabled()){
			log.debug("Params: " + params);
		}

		if (null == params || params.isEmpty()){
			log.error("error marking deltas - empty params for module");
			return;
		}

		Properties props = new Properties();
		props.load(new StringReader(params));
		String shighid = props.getProperty(Module.DB_DUMP_PARAM_MAX_USER_DELTA);
		Integer highid = new Integer(shighid);

		String sql = "update sys_delta_user set processed = " + SyncStatus.Processed.intValue() + " where "
		        + "target_user_id" + " = " + user.getId() + " and id <= " + highid;
		if (log.isDebugEnabled()){
			log.debug("executing: " + sql);
		}
		getJdbcTemplate().update(sql);

		sql = "update sys_delta_user set processed = " + SyncStatus.New.intValue() + " where target_user_id = "
		        + user.getId() + " and id > " + highid;
		if (log.isDebugEnabled()){
			log.debug("executing: " + sql);
		}
		getJdbcTemplate().update(sql);
		log.debug("done marking");
	}
}
