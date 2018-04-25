package com.ni3.ag.adminconsole.server.importers.util;

public enum ImportDataError{
	InvalidPredefined(1), InvalidInt(2), InvalidDecimal(3), InvalidBool(4), StringTooLong(5), InvalidFromToId(6);
	
	private int val;
	
	ImportDataError(int val){
		this.val = val;
	}
	
	public int intValue(){
		return val;
	}
}
