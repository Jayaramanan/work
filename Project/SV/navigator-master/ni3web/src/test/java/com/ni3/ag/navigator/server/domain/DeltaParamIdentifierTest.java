package com.ni3.ag.navigator.server.domain;

import java.lang.reflect.Field;
import junit.framework.TestCase;

public class DeltaParamIdentifierTest extends TestCase{

	public void testDeltaParamIdentifier(){
		Class<?> c = DeltaParamIdentifier.class;
		Field[] fs = c.getDeclaredFields();
		for (Field f : fs){
			try{
				DeltaParamIdentifier o = (DeltaParamIdentifier) f.get(null);
				assertEquals(f.getName(), o.getIdentifier());
				assertTrue(o.isFixedParam());
				assertEquals(DeltaParamIdentifier.getById(o.getIdentifier()), o);
				assertTrue(DeltaParamIdentifier.getById(o.getIdentifier()) == o);
			} catch (IllegalAccessException e){

			}
		}
	}
}
