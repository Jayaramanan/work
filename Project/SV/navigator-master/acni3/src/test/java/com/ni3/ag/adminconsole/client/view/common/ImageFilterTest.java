/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.io.File;

import com.ni3.ag.adminconsole.client.test.ACTestCase;

public class ImageFilterTest extends ACTestCase{
	private ImageFilter filter;
	private File file;

	public void setUp(){
		filter = new ImageFilter();
		file = new File("test.pNg");
	}

	public void testGetExtension(){
		String ext = ImageFilter.getExtension(file);
		assertEquals(ImageFilter.PNG, ext);
	}

	public void testAccept(){
		boolean accept = filter.accept(file);
		assertTrue(accept);

		File f = new File("test.ppng");
		accept = filter.accept(f);
		assertFalse(accept);
	}

}
