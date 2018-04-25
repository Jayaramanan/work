/** Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class Str{

	public static String escape(String in){
		String ret;
		try{
			ret = URLEncoder.encode(in, "utf-8");
		} catch (UnsupportedEncodingException e){
			ret = in;
		}

		return ret;
	}

	public static String unescape(String in){
		String ret;
		try{
			ret = URLDecoder.decode(in, "utf-8");
		} catch (UnsupportedEncodingException e){
			ret = in;
		}

		return ret;
	}
}