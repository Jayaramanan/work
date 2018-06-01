/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.servlets.util;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class UserDataIntegrityValidator extends JdbcDaoSupport{
	private static final Logger log = Logger.getLogger(UserDataIntegrityValidator.class);

	//TODO make integrity validation
	public boolean checkUserData(){
		boolean result = true;
//		SchemaLoaderService schemaLoaderService = NSpringFactory.getInstance().getSchemaLoaderService();
//		List<Schema> schemas = schemaLoaderService.getAllSchemas();
//		for(Schema schema : schemas){
//			for(Entity entity : schema.getDefinitions()){
//				ObjectDataSource objectDataSource = (ObjectDataSource) NSpringFactory.getInstance().getBean(entity.getDataSource());
//				final int count = objectDataSource.validateCisUsr(entity.getId());
//				if (count > 0){
//					log.error("Missing " + count + " records in user table `" + entity.getName() + "`\n"
//					        + "Please launch diagnostics in Admin Console to fix it");
//					result = false;
//				}
//			}
//		}
		return result;
	}
}