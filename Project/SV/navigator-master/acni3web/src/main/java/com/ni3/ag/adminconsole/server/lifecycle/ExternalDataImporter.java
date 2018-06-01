/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.lifecycle;

import javax.sql.DataSource;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ExternalDataImporter{
	private static Logger log = Logger.getLogger(ExternalDataImporter.class);
	private DataSource dataSource;
	private static final String CURRENT_MAJOR_RELEASE = "1";

	public DataSource getDataSource(){
		return dataSource;
	}

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}

	private void executeScript(String scriptName, Statement st) throws Exception{
		String script = null;
		InputStream is = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream baos = null;
		try{
			is = getClass().getClassLoader().getResourceAsStream(scriptName);
			bis = new BufferedInputStream(is);
			baos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			boolean firstRead = true;
			int resourseLength = bis.available();
			while (bis.available() > 0){
				int count = bis.read(buf);
				// skip UTF-8 signature if presented
				boolean hasSignature = false;
				if (firstRead && (buf[0] & 0xFF) == 0xEF && (buf[1] & 0xFF) == 0xBB && (buf[2] & 0xFF) == 0xBF)
					hasSignature = true;
				baos.write(buf, hasSignature ? 3 : 0, hasSignature ? count - 3 : count);
				firstRead = false;
			}
			script = new String(baos.toByteArray(), "UTF-8");
			int scriptLength = script.length();
			log.info("File: " + resourseLength + "  script: " + scriptLength + " (" + (resourseLength - scriptLength) + ")");
		} catch (IOException ex){
			log.error("Error import external db script: " + ex, ex);
			throw ex;
		} finally{
			if (baos != null)
				baos.close();
			if (bis != null)
				bis.close();
			if (is != null)
				is.close();
		}
		if (script == null || script.length() <= 0){
			log.warn("Script has 0 length - cannot proceed");
			return;
		}

		int result = st.executeUpdate(script.toString());
		log.debug("RESULT: " + result);
	}

	public void importExternalData(String type) throws Exception{
		String dirName = "database/" + type + "/" + CURRENT_MAJOR_RELEASE + "/";
		List<String> scripts = getAllScripts(dirName);
//		scripts.addAll(getPostgisScripts("database/postgis/"));
		Connection connection = null;
		Statement st = null;
		String scriptName = null;
		try{
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			st = connection.createStatement();
			for (String s : scripts){
				scriptName = s;
				log.info("PROCESSING: " + s);
				executeScript(s, st);
			}
			log.debug("Processed scripts count:" + scripts.size());
			log.debug("Commit transaction");
			connection.commit();
		} catch (Exception ex){
			log.error("Error execute script: " + scriptName, ex);
			try{
				log.info("Rollback transaction");
				if (connection != null)
					connection.rollback();
			} catch (SQLException e){
				log.error(ex);
			}
			throw ex;
		} finally{
			try{
				log.debug("Close connection");
				if (st != null)
					st.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e){
				log.error(e);
			}
		}
	}

	private List<String> getPostgisScripts(String s){
		List<String> result = new ArrayList<String>();
		result.add(s + "postgis.sql");
		result.add(s + "spatial_ref_sys.sql");
		return result;
	}

	private List<String> getAllScripts(String dirName){
		List<String> scripts = new ArrayList<String>();
		int a = 1, b = 0, c = 0; // version parts

		while (true){
			int majorCount = scripts.size();
			while (true){
				int count = scripts.size();
				while (true){
					String current = "alter-" + a + "." + b + "." + c + ".sql";
					try{
						InputStream is = getClass().getClassLoader().getResourceAsStream(dirName + current);
						if (is == null && c != 0)
							break;
						else{
							// a little hack for irregular numbering
							// if script versioning starts with 1 not with 0
							if (is != null){
								is.close();
								scripts.add(current);
							}
						}
					} catch (Exception ex){
						break;
					}
					c++;
				}
				if (count == scripts.size())
					break;
				else{
					b++;
					c = 0;
				}
			}
			if (majorCount == scripts.size())
				break;
			else{
				a++;
				b = 0;
				c = 0;
			}
		}

		for (int i = 0; i < scripts.size(); i++)
			scripts.set(i, dirName + scripts.get(i));
		return scripts;
	}

}
