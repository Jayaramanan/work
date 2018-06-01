/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.mapextraction;

import java.nio.ByteBuffer;

public class GisFileIndexItem{
	public final static int INDEX_ITEM_SIZE = 12;
	private int FileIndex = -1;
	private int offset = -1;
	private int len = -1;

	public void read(ByteBuffer bb){
		FileIndex = bb.getInt();
		offset = bb.getInt();
		len = bb.getInt();
	}

	public byte[] toBytes(){
		ByteBuffer bb = ByteBuffer.allocate(INDEX_ITEM_SIZE);
		bb.putInt(FileIndex);
		bb.putInt(offset);
		bb.putInt(len);
		return bb.array();
	}

	public int getFileIndex(){
	    return FileIndex;
    }

	public long getOffset(){
	    return offset;
    }

	public int getLen(){
	    return len;
    }

	public void setFileIndex(int index){
		this.FileIndex = index;
    }

	public void setOffset(int offset){
		this.offset = offset;
    }

	public void setLen(int len){
		this.len = len;
    }
}
