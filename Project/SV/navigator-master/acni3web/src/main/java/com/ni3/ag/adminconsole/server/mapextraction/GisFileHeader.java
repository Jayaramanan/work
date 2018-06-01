/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.mapextraction;

import java.nio.ByteBuffer;

public class GisFileHeader{
	public static final int HEADER_SIZE = 52;
	private int TileCount;
	private int MapID;
	private int TilesCountX, TilesCountY;
	private int X, Y, W, H;
	private int StepX, StepY, Scale;

	public void read(byte[] buf){
		ByteBuffer bb = ByteBuffer.wrap(buf);
		MapID = bb.getInt();
		TilesCountX = bb.getInt();
		TilesCountY = bb.getInt();
		X = bb.getInt();
		Y = bb.getInt();
		W = bb.getInt();
		H = bb.getInt();
		StepX = bb.getInt();
		bb.getInt();
		StepY = bb.getInt();
		bb.getInt();
		Scale = bb.getInt();
		bb.getInt();
		TileCount = TilesCountX * TilesCountY;
	}

	public String dumpString(){
		StringBuffer sb = new StringBuffer();
		sb.append("GIS INDEX FILE HEADER\n");
		sb.append("MapID: " + MapID).append("\n");
		sb.append("TilesCount X | Y: ").append(TilesCountX).append(" | ").append(TilesCountY).append("\n");
		sb.append("Sizes X | Y | W | H: ").append(X).append(" | ").append(Y).append(" | ").append(W).append(" | ").append(H)
		        .append("\n");
		sb.append("Steps X | Y").append(StepX).append(" | ").append(StepY).append("\n");
		sb.append("Scale: " + Scale);
		return sb.toString();
	}

	public byte[] cloneModify(int startTileX, int startTileY, int TilesCountX, int TilesCountY){
		ByteBuffer bb = ByteBuffer.allocate(HEADER_SIZE);
		bb.putInt(this.MapID);
		bb.putInt(TilesCountX);
		bb.putInt(TilesCountY);
		bb.putInt(this.X + startTileX * this.StepX);
		bb.putInt(this.Y + startTileY * this.StepY);
		bb.putInt(TilesCountX * this.StepX);
		bb.putInt(TilesCountY * this.StepY);
		bb.putInt(this.StepX);
		bb.putInt(this.StepX);
		bb.putInt(this.StepY);
		bb.putInt(this.StepY);
		bb.putInt(this.Scale);
		bb.putInt(this.Scale);
		return bb.array();
	}

	public int getTilesCountX(){
	    return TilesCountX;
    }

	public int getTilesCountY(){
	    return TilesCountY;
    }

	public int getStepX(){
	    return StepX;
    }

	public int getStepY(){
	    return StepY;
    }

	public int getX(){
	    return X;
    }

	public int getY(){
	    return Y;
    }

	public int getTileCount(){
	    return TileCount;
    }

	public int getScale(){
	    return Scale;
    }
}
