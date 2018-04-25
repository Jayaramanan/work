/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.license;

import junit.framework.TestCase;

public class KeyStoreTest extends TestCase{
	public void testPublicKey(){
		assertNotNull(KeyStore.publicKey);
	}
}
