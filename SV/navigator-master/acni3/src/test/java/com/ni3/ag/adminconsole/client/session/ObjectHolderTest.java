/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.session;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;

public class ObjectHolderTest extends ACTestCase{
	public void testMaxPath(){
		ObjectHolder holder = ObjectHolder.getInstance();

		Object[] path = new Object[] { "", "", new ObjectDefinition() };
		holder.setCurrentPath(path);

		Object[] result = holder.getMaxPath(new Class<?>[] { ObjectDefinition.class });
		assertEquals(3, result.length);
		assertSame(path[2], result[2]);

		result = holder.getMaxPath(new Class<?>[] { ObjectDefinition.class, ObjectDefinition.class });
		assertEquals(3, result.length);
		assertSame(path[2], result[2]);

		result = holder.getMaxPath(new Class<?>[] { ObjectAttribute.class });
		assertEquals(2, result.length);

		path = new Object[] { "", "", new ObjectDefinition(), new ObjectAttribute() };
		holder.setCurrentPath(path);

		result = holder.getMaxPath(new Class<?>[] { ObjectDefinition.class, ObjectAttribute.class, ObjectAttribute.class });
		assertEquals(4, result.length);
		assertSame(path[2], result[2]);
		assertSame(path[3], result[3]);

		result = holder.getMaxPath(new Class<?>[] { ObjectDefinition.class });
		assertEquals(3, result.length);
		assertSame(path[2], result[2]);

		result = holder.getMaxPath(new Class<?>[] { ObjectAttribute.class, ObjectDefinition.class });
		assertEquals(2, result.length);

		result = holder.getMaxPath(new Class<?>[] { ObjectAttribute.class });
		assertEquals(2, result.length);

	}
}
