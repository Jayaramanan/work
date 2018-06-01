/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import junit.framework.TestCase;

public class ACTableModelTest extends TestCase{
	public void testCorrectStringValue(){
		@SuppressWarnings("serial")
		ACTableModel model = new ACTableModel(){

			@Override
			public Object getValueAt(int rowIndex, int columnIndex){
				return null;
			}

			@Override
			public int getRowCount(){
				return 0;
			}
		};
		assertNull(model.validateValue(null));
		assertNull(model.validateValue(""));
		assertNull(model.validateValue("   "));

		assertEquals("test", model.validateValue("test"));
		assertEquals("test", model.validateValue(" test  "));

		Integer one = new Integer(1);
		assertEquals(one, model.validateValue(one));
	}
}
