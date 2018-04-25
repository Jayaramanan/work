package com.ni3.ag.navigator.server.GISUtil;

import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class GISRasterLayer{
	public int layerID;

	public int mapID;
	public int X, Y, W, H;
	public int scale;

	int tilesX, tilesY;
	int stepX, stepY;

	String fileName;
	Rectangle BR;

	int fileIndex[];
	int offset[];
	int length[];

	public GISRasterLayer(){

	}

	public void load(String file, int Version) throws IOException{
		RandomAccessFile w;

		try{
			w = new RandomAccessFile(file + ".idx", "r");
		} catch (FileNotFoundException e){
			w = null;
			throw e;
		}

		fileName = file;
		mapID = w.readInt();
		tilesX = w.readInt();
		tilesY = w.readInt();
		X = w.readInt();
		Y = w.readInt();
		W = w.readInt();
		H = w.readInt();

		stepX = w.readInt();
		w.readInt();
		stepY = w.readInt();
		w.readInt();
		scale = w.readInt();
		w.readInt();

		BR = new Rectangle();
		BR.x = X;
		BR.y = Y;
		BR.width = W;
		BR.height = H;

		fileIndex = new int[tilesX * tilesY];
		offset = new int[tilesX * tilesY];
		length = new int[tilesX * tilesY];

		for (int n = 0; n < tilesX * tilesY; n++){
			if (Version == 1){
				fileIndex[n] = -1;
			} else if (Version == 2){
				fileIndex[n] = w.readInt() - 1;
			}
			offset[n] = w.readInt();
			length[n] = w.readInt();
		}

		w.close();
	}

	public int getRaster(byte buff[], int tileID) throws IOException{
		if (tileID < tilesX * tilesY){
			RandomAccessFile w;

			String fname = fileName;

			if (fileIndex[tileID] != -1){
				fname += "-" + fileIndex[tileID];
			}

			w = new RandomAccessFile(fname + ".dat", "r");
			w.seek(offset[tileID]);

			w.read(buff, 0, length[tileID]);

			w.close();

			return length[tileID];
		}

		return 0;
	}

	public String getTileList(Rectangle filter[]){
		String ret = "";

		for (Rectangle flt : filter){
			if (BR.intersects(flt)){
				int sx, sy, ex, ey;

				sx = flt.x - BR.x;
				ex = flt.x + flt.width - BR.x;

				sy = flt.y - BR.y;
				ey = flt.y + flt.height - BR.y;

				sx = Math.min(Math.max((int) (sx / (float) stepX) - 1, 0), tilesX);
				ex = Math.min(Math.max((int) (ex / (float) stepX) + 1, 0), tilesX);

				sy = Math.min(Math.max((int) (sy / (float) stepY) - 1, 0), tilesY);
				ey = Math.min(Math.max((int) (ey / (float) stepY) + 1, 0), tilesY);

				for (int i = sx; i < ex; i++){
					for (int j = sy; j < ey; j++){
						if (length[i + j * tilesX] > 0)
							ret += layerID + "\t" + (i + j * tilesX) + "\t" + (X + stepX * i) + "\t" + (Y + stepY * j)
							        + "\t" + stepX + "\t" + stepY + "\t" + (length[i + j * tilesX]) + "\t\n";
					}
				}
			}
		}

		return ret;
	}
}
