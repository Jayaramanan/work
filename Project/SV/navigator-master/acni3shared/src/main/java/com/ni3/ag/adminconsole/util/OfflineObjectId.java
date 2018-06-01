/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.util;

public class OfflineObjectId{
	public final static int ID_MASK = Integer.valueOf("000000000000011111111111111111", 2).intValue();
	public final static int USER_MASK = Integer.valueOf("111111111111100000000000000000", 2).intValue();

	public final static int USER_ID_OFFSET = 17;
	public final static int MASTER_BIT_OFFSET = 30;

	private int resultId;

	public static OfflineObjectId OFFLINE_OBJECT_START_ID = new OfflineObjectId(1, 0, true);

	public OfflineObjectId(int userId, int id, boolean offline){
		int res = offline ? 1 : 0;
		resultId = id | userId << USER_ID_OFFSET | res << MASTER_BIT_OFFSET;
	}

	public int getResult(){
		return resultId;
	}
}
