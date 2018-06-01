/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ni3.ag.adminconsole.domain.*;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.shared.service.DataGroup;
import com.ni3.ag.adminconsole.validation.ACException;

public interface SchemaAdminService{
	List<Schema> getSchemas();

	ObjectDefinition addObjectDefinition(Schema parent, String name, User user) throws ACException;

	Schema addSchema(String name, User user) throws ACException;

	Schema copySchema(Integer id, String newName, User user) throws ACException;

	void deleteSchema(Integer id) throws ACException;

	void deleteObject(Integer id, boolean forceDelete) throws ACException;

	ErrorContainer validateAttributesDelete(List<ObjectAttribute> attrIds);

	ObjectDefinition updateObjectDefinition(ObjectDefinition od, boolean ignoreUserData) throws ACException;

	ErrorContainer generateSchema(Integer schemaDefinitionId, Integer objectId);

	ObjectDefinition loadSingleObjectDefinition(Integer id) throws ACException;

	void updateSchema(Schema schema);

	void dropUserTables(List<String> tables) throws ACException;

	Schema loadSingleSchema(Integer id);

	List<AttributeGroup> getAttributeGroups(Integer id);

	ExportData getSchemaExport(String name) throws ACException;

	void importSchemaFromXML(String xml) throws ACException;

	void importUserDataFromXLS(byte[] data, Integer schemaId, Integer userId, boolean recalculateFormulas) throws ACException;

	void importSchemaFromXLS(byte[] data, String schemaName, User user) throws ACException;

	ExportData exportUserDataToXLS(String schemaName, User user) throws ACException;

	void updateCache(String navHost, User user, Integer schemaId) throws ACException;

	void importUserDataFromCSV(List<String> lines, Integer schemaId, Integer userId, String fileName, String columnSeparator,
			boolean recalculateFormulas) throws ACException;

	void setInvalidationRequired(DataGroup gr, boolean required);

	boolean isInvalidationRequired(DataGroup gr);

	byte[] exportUserDataToCSV(ObjectDefinition od, User user, String columnSeparator, String lineSeparator)
			throws ACException;

	byte[] exportSchemaToXML(Integer schemaId) throws ACException;

	boolean isAnyInvalidationRequired();

	void resetAnyInvalidationRequired();

	void setAllInvalidationRequired(boolean b, DataGroup... dataGroups);

	Set<DataGroup> getInvalidationRequiredGroups();

	ErrorContainer updateUserTables(Integer schemaId, Integer objectId);

	Schema reloadFullSchema(Schema currentSchema);

	Map<String, List<String>> getAvailableSalesforceTabs(String url, String username, String password) throws ACException;

	Schema importSchemaFromSalesforce(String schemaName, List<String> objectNames, int userId, String url, String username,
			String password) throws ACException;

	List<DataSource> getDataSources();

}
