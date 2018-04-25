/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.util;

public class SimplePasswordGenerator implements PasswordGenerator{

	private int length;

	public int getLength(){
		return length;
	}

	public void setLength(int length){
		this.length = length;
	}

	@Override
	public String generatePassword(){
		char[] pw = new char[length];
		int c = 'A';
		int r1 = 0;
		for (int i = 0; i < length; i++){
			r1 = (int) (Math.random() * 3);
			switch (r1){
				case 0:
					c = '0' + (int) (Math.random() * 10);
					break;
				case 1:
					c = 'a' + (int) (Math.random() * 26);
					break;
				case 2:
					c = 'A' + (int) (Math.random() * 26);
					break;
			}
			pw[i] = (char) c;
		}
		return new String(pw);
	}
}
