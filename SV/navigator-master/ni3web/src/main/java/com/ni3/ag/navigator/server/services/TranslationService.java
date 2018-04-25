/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services;

import com.ni3.ag.navigator.server.domain.Schema;

public interface TranslationService{

	/**
	 * translate schema
	 * 
	 * @param schema
	 *            - schema to be translated
	 * @param languageId
	 *            - id of the language
	 * @return translated schema
	 */
	Schema translateSchema(Schema schema, int languageId);

}
