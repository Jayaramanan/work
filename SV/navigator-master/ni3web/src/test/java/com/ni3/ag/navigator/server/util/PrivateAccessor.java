package com.ni3.ag.navigator.server.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.Assert;

/**
 * Provides access to private members in classes.
 */
public class PrivateAccessor{

	public static Object getPrivateField(Object o, String fieldName){
		// Check we have valid arguments...
		Assert.assertNotNull(o);
		Assert.assertNotNull(fieldName);

		// Go and find the private field...
		final Field fields[] = o.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; ++i){
			if (fieldName.equals(fields[i].getName())){
				try{
					fields[i].setAccessible(true);
					return fields[i].get(o);
				} catch (IllegalAccessException ex){
					Assert.fail("IllegalAccessException accessing " + fieldName);
				}
			}
		}
		Assert.fail("Field '" + fieldName + "' not found");
		return null;
	}

	public static Object invokePrivateMethod(Object o, String methodName, Object... params){
		// Check we have valid arguments...
		Assert.assertNotNull(o);
		Assert.assertNotNull(methodName);
		Assert.assertNotNull(params);

		// Go and find the private method...
		final Method methods[] = o.getClass().getDeclaredMethods();
		for (int i = 0; i < methods.length; ++i){
			if (methodName.equals(methods[i].getName())){
				try{
					methods[i].setAccessible(true);
					return methods[i].invoke(o, params);
				} catch (IllegalAccessException ex){
					ex.printStackTrace();
					Assert.fail("IllegalAccessException accessing " + methodName);
				} catch (InvocationTargetException ite){
					ite.printStackTrace();
					Assert.fail("InvocationTargetException accessing " + methodName);
				}
			}
		}
		Assert.fail("Method '" + methodName + "' not found");
		return null;
	}

	public static void setPrivateField(Object o, String fieldName, Object value){
		// Check we have valid arguments...
		Assert.assertNotNull(o);
		Assert.assertNotNull(fieldName);

		if (!searchAndSet(o, fieldName, value, o.getClass())){
			Assert.fail("Field '" + fieldName + "' not found");
		}
	}

	private static boolean searchAndSet(Object o, String fieldName, Object value, Class<?> clazz){
		for (Field field : clazz.getDeclaredFields()){
			if (fieldName.equals(field.getName())){
				try{
					field.setAccessible(true);
					field.set(o, value);
					return true;
				} catch (IllegalAccessException ex){
					Assert.fail("IllegalAccessException accessing " + fieldName);
				}
			}
		}
		return o.getClass().getSuperclass() != Object.class && searchAndSet(o, fieldName, value, clazz.getSuperclass());
	}
}