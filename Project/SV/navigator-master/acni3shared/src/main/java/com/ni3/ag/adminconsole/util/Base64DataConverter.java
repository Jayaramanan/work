/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.util;

import java.io.IOException;

public class Base64DataConverter implements BinaryDataConverter{

	@Override
	public String encodeData(byte[] data){
		return Base64.encodeBytes(data);
	}

	@Override
	public byte[] decodeData(String data) throws IOException{
		return Base64.decode(data);
	}

}
