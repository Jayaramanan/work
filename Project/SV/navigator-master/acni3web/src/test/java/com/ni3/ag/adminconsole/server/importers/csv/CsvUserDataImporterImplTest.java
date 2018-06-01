/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.server.importers.csv;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class CsvUserDataImporterImplTest extends TestCase{

	@Override
	protected void setUp() throws Exception{
	}

	public void testNextPart(){
		CSVUserDataImporterImpl importer = new CSVUserDataImporterImpl();
		List<String> csv = new ArrayList<String>();
		csv.add("col1\tcol2\tcol3\tcol4");
		csv.add("aaa\t200\t300\t400");
		csv.add("bbb\t2\t3\t4");
		csv.add("ccccc\t222222\t333333\t444");
		csv.add("ddddddd\t555555\t666666666\t7777777");
		csv.add("d\t5\t6\t7");
		List<String> result = importer.nextPart(csv, 1, 10);
		assertEquals(5, result.size());
		for (int i=0; i< 5; i++){
			assertEquals(csv.get(i+1), result.get(i));
		}
		result = importer.nextPart(csv, 1, 3);
		assertEquals(3, result.size());
		for (int i=0; i< 3; i++){
			assertEquals(csv.get(i+1), result.get(i));
		}
		
		result = importer.nextPart(csv, 5, 10);
		assertEquals(1, result.size());
		
		result = importer.nextPart(csv, 6, 10);
		assertEquals(0, result.size());
	}
}
