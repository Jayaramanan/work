/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.util;

import java.io.IOException;

public interface BinaryDataConverter{
	public String encodeData(byte[] data);

	public byte[] decodeData(String data) throws IOException;
}
