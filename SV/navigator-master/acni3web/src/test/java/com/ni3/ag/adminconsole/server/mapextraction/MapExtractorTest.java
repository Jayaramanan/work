/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.mapextraction;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.server.mapextraction.MapExtractor;

import junit.framework.TestCase;

public class MapExtractorTest extends TestCase{

	public void testProcessLine(){
		MapExtractor extractor = new MapExtractor();
		String baseDir = "test";
		Map<Integer, List<String>> fileMap = new HashMap<Integer, List<String>>();
		String line = null;
		extractor.processLine(line, fileMap, baseDir);
		assertTrue(fileMap.isEmpty());

		line = "123\t2345abc";
		extractor.processLine(line, fileMap, baseDir);
		assertTrue(fileMap.isEmpty());

		line = "2345abc";
		extractor.processLine(line, fileMap, baseDir);
		assertTrue(fileMap.isEmpty());

		line = "2\t2\t." + File.separator + "PuzzleUS-Part-5000000";
		extractor.processLine(line, fileMap, baseDir);
		assertTrue(fileMap.containsKey(5000000));
		assertEquals("test" + File.separator + "PuzzleUS-Part", fileMap.get(5000000).get(0));

		line = "2\t2\t." + File.separator + "folder" + File.separator + "PuzzleUS2-Part-5000000";
		extractor.processLine(line, fileMap, baseDir);
		assertTrue(fileMap.containsKey(5000000));
		List<String> files = fileMap.get(5000000);
		assertEquals(2, files.size());
		assertEquals("test" + File.separator + "folder" + File.separator + "PuzzleUS2-Part", files.get(1));
	}

	public void testHasScale(){
		MapExtractor extractor = new MapExtractor();
		String[] scales = new String[] { "10000", "25000", "50000", "200000" };
		assertTrue(extractor.hasScale(10000, scales));
		assertTrue(extractor.hasScale(new Integer(25000), scales));
		assertTrue(extractor.hasScale(50000, scales));
		assertTrue(extractor.hasScale(200000, scales));

		assertFalse(extractor.hasScale(null, scales));
		assertFalse(extractor.hasScale(1000, scales));
		assertFalse(extractor.hasScale(20000, scales));
	}
}
