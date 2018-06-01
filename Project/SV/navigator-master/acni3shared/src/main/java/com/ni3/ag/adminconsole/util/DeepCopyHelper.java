/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

/**
 * Utility for making deep copies (vs. clone()'s shallow copies) of objects. Objects are first serialized and then
 * deserialized. If an object is encountered that cannot be serialized (or that references an object that cannot be
 * serialized) an error is printed to System.err and null is returned. Depending on your specific application, it might
 * make more sense to have copy(...) re-throw the exception.
 */
public class DeepCopyHelper{

	private final static Logger log = Logger.getLogger(DeepCopyHelper.class);

	/**
	 * Returns a copy of the object, or null if the object cannot be serialized.
	 */
	public static Object copy(Object orig){
		Object obj = null;
		try{
			// Write the object out to a byte array
			FastByteArrayOutputStream fbos = new FastByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(fbos);
			out.writeObject(orig);
			out.flush();
			out.close();

			ObjectInputStream in = new ObjectInputStream(fbos.getInputStream());
			obj = in.readObject();
		} catch (IOException e){
			log.error("cant copy object " + orig.toString(), e);
		} catch (ClassNotFoundException cnfe){
			log.error("cant copy object " + orig.toString(), cnfe);
		}
		return obj;
	}

}
