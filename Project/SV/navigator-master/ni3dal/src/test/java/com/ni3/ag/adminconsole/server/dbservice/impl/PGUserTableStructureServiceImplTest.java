/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dbservice.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedList;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.server.dbservice.impl.PGUserTableStructureServiceImpl.DBColumn;

public class PGUserTableStructureServiceImplTest extends TestCase{

	private PGUserTableStructureServiceImpl service;
	private ObjectAttribute attr;
	private DBColumn dbCol;
	private DataType dataType;

	@Override
	protected void setUp() throws Exception{
		service = new PGUserTableStructureServiceImpl();
		attr = new ObjectAttribute();
		dataType = DataType.TEXT;
		attr.setDataType(dataType);
		dbCol = service.new DBColumn("col1", "text", 255);
	}

	public void testIsEqualDBTypesEqual(){
		// varchars
		assertTrue(service.isEqualDBTypes(attr, dbCol));

		// integers
		dbCol.setDataType("integer");
		attr.setDataType(DataType.INT);
		assertTrue(service.isEqualDBTypes(attr, dbCol));

		dbCol.setDataType("int4");
		assertTrue(service.isEqualDBTypes(attr, dbCol));

		// numerics
		dbCol.setDataType("numeric");
		attr.setDataType(DataType.DECIMAL);
		assertTrue(service.isEqualDBTypes(attr, dbCol));

		// texts
		dbCol.setDataType("text");
		attr.setDataType(DataType.TEXT);
		assertTrue(service.isEqualDBTypes(attr, dbCol));
	}

	public void testIsEqualDBTypesNotEqual(){
		// integer vs numeric
		dbCol.setDataType("numeric");
		attr.setDataType(DataType.INT);
		assertFalse(service.isEqualDBTypes(attr, dbCol));
		dbCol.setDataType("integer");
		attr.setDataType(DataType.DECIMAL);
		assertFalse(service.isEqualDBTypes(attr, dbCol));

		// varchar vs numeric
		dbCol.setDataType("varchar");
		attr.setDataType(DataType.DECIMAL);
		assertFalse(service.isEqualDBTypes(attr, dbCol));
		dbCol.setDataType("numeric");
		attr.setDataType(DataType.TEXT);
		assertFalse(service.isEqualDBTypes(attr, dbCol));

		// varchar vs text
		dbCol.setDataType("varchar");
		attr.setDataType(DataType.TEXT);
		assertFalse(service.isEqualDBTypes(attr, dbCol));
	}

	public void testGetDatabaseDataTypeText(){
		assertEquals("text", service.getDatabaseDataType(attr));

		attr.setDataType(DataType.DATE);
		assertEquals("text", service.getDatabaseDataType(attr));

		attr.setDataType(DataType.URL);
		assertEquals("text", service.getDatabaseDataType(attr));

		attr.setDataType(DataType.INT);
		attr.setIsMultivalue(true);
		assertEquals("text", service.getDatabaseDataType(attr));

		attr.setPredefined(true);
		assertEquals("text", service.getDatabaseDataType(attr));
	}

	public void testGetDatabaseDataTypeInteger(){
		attr.setDataType(DataType.INT);
		assertEquals("integer", service.getDatabaseDataType(attr));

		attr.setDataType(DataType.BOOL);
		assertEquals("integer", service.getDatabaseDataType(attr));

		attr.setDataType(DataType.TEXT);
		attr.setPredefined(true);
		assertEquals("integer", service.getDatabaseDataType(attr));
	}

	public void testGetDatabaseDataTypeNumeric(){
		attr.setDataType(DataType.DECIMAL);
		assertEquals("numeric", service.getDatabaseDataType(attr));
	}

	public void testUpdateUserTableDropCxtxTable(){
		final String mockTableName = "mockTable";

		final ObjectDefinition objectDefinition = mock(ObjectDefinition.class);
		when(objectDefinition.getTableName()).thenReturn(mockTableName);

		final PGUserTableStructureServiceImpl service = mock(PGUserTableStructureServiceImpl.class);

		when(service.checkUserTable(mockTableName, objectDefinition, false)).thenReturn(new LinkedList<ErrorEntry>());
		when(service.executeUpdate(anyString())).thenReturn(null);

		when(service.updateUserTable(any(ObjectDefinition.class))).thenCallRealMethod();

		final ErrorContainer result = service.updateUserTable(objectDefinition);

		verify(service).checkUserTable(mockTableName, objectDefinition, false);
		verify(service).executeUpdate("drop table if exists mockTable" + ObjectAttribute.CONTEXT_TABLE_SUFFIX);

		assertTrue(result.getErrors().isEmpty());
	}

	public void testUpdateUserTableCheckCxtxTable(){
		final String mockTableName = "mockTable";
		final LinkedList<ErrorEntry> errorEntries = new LinkedList<ErrorEntry>();
		errorEntries.add(new ErrorEntry());

		final ObjectDefinition objectDefinition = mock(ObjectDefinition.class);
		when(objectDefinition.getTableName()).thenReturn(mockTableName);
		when(objectDefinition.hasContextAttributes()).thenReturn(true);
		final PGUserTableStructureServiceImpl service = mock(PGUserTableStructureServiceImpl.class);

		when(service.checkUserTable(mockTableName, objectDefinition, false)).thenReturn(errorEntries);
		when(service.checkUserTable(mockTableName + ObjectAttribute.CONTEXT_TABLE_SUFFIX, objectDefinition, true))
		        .thenReturn(errorEntries);
		when(service.updateUserTable(any(ObjectDefinition.class))).thenCallRealMethod();

		final ErrorContainer result = service.updateUserTable(objectDefinition);

		verify(service).checkUserTable(mockTableName, objectDefinition, false);
		verify(service).checkUserTable(mockTableName + ObjectAttribute.CONTEXT_TABLE_SUFFIX, objectDefinition, true);

		assertEquals(2, result.getErrors().size());
	}
}
