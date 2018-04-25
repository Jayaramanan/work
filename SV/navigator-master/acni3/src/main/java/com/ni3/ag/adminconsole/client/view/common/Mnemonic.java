/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.event.KeyEvent;

public enum Mnemonic{
	AltC(KeyEvent.VK_C), AltL(KeyEvent.VK_L), AltO(KeyEvent.VK_O), AltS(KeyEvent.VK_S), AltA(KeyEvent.VK_A), AltD(
	        KeyEvent.VK_D), AltR(KeyEvent.VK_R), AltU(KeyEvent.VK_U), AltE(KeyEvent.VK_E), AltN(KeyEvent.VK_N), AltG(
	        KeyEvent.VK_G), AltI(KeyEvent.VK_I), AltX(KeyEvent.VK_X), AltT(KeyEvent.VK_T), AltF(KeyEvent.VK_F), AltM(
	        KeyEvent.VK_M), AltP(KeyEvent.VK_P), AltH(KeyEvent.VK_H), AltQ(KeyEvent.VK_Q);

	private int key;

	Mnemonic(int key){
		this.key = key;
	}

	public static Mnemonic fromKey(int key){
		for (Mnemonic m : values())
			if (m.getKey() == key)
				return m;
		return null;
	}

	@Override
	public String toString(){
		return KeyEvent.getKeyModifiersText(KeyEvent.ALT_MASK) + " + " + KeyEvent.getKeyText(key);
	}

	public int getKey(){
		return key;
	}

}
