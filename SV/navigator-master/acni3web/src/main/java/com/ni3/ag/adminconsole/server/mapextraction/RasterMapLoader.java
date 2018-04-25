/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.mapextraction;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;

public class RasterMapLoader{
	private static final Logger log = Logger.getLogger(RasterMapLoader.class);
	private static final int BUFFER_SIZE = 1024 * 256;

	private GisFileHeader header;
	private GisFileIndexItem[] indexItems;

	private String RootFolder;

	private byte buf[];

	public RasterMapLoader(String RootFolder, int Scale) throws ACException{
		this.RootFolder = RootFolder;
		String path = RootFolder + "-" + (int) Scale + ".idx";
		FileInputStream w = null;
		try{
			File file = new File(path);
			if (!file.exists()){
				log.error("File not found: " + path);
				throw new ACException(TextID.MsgFileNotFound, new String[] { path });
			}
			w = new FileInputStream(file);
			buf = new byte[4 * 13];
			w.read(buf);
			header = new GisFileHeader();
			header.read(buf);
			log.debug(header.dumpString());

			buf = new byte[header.getTileCount() * 4 * 3];
			w.read(buf);

			ByteBuffer bb = ByteBuffer.wrap(buf);
			indexItems = new GisFileIndexItem[header.getTileCount()];

			for (int n = 0; n < header.getTileCount(); n++){
				indexItems[n] = new GisFileIndexItem();
				indexItems[n].read(bb);
			}

			buf = new byte[BUFFER_SIZE];
		} catch (FileNotFoundException e){
			log.error(e);
			throw new ACException(TextID.MsgErrorExctractingMap, new String[] { e.getMessage() });
		} catch (IOException e){
			log.error(e);
			throw new ACException(TextID.MsgErrorExctractingMap, new String[] { e.getMessage() });
		} finally{
			if (w != null)
				try{
					w.close();
				} catch (IOException e){
					log.error("Cannot close file input stream", e);
				}
		}
	}

	void copyRaster(FileOutputStream w, int TileX, int TileY) throws ACException{
		int TileID = header.getTilesCountX() * TileY + TileX;

		if (TileID < header.getTileCount()){
			String fname = RootFolder;
			if (indexItems[TileID].getFileIndex() != -1){
				fname += "-" + header.getScale();
				fname += "-" + (indexItems[TileID].getFileIndex() - 1);
			}
			fname += ".dat";

			try{
				FileInputStream q = new FileInputStream(new File(fname));
				BufferedInputStream bis = new BufferedInputStream(q);
				bis.skip(indexItems[TileID].getOffset());
				int lenR = bis.read(buf, 0, indexItems[TileID].getLen());
				if (lenR != indexItems[TileID].getLen()){
					String msg = "TileSize readed != tileSize by index: " + lenR + " | " + indexItems[TileID].getLen();
					log.warn(msg);
					throw new ACException(TextID.MsgErrorExctractingMap, new String[] { msg });
				}
				byte[] rez = buf;
				// ////////////////////////////////////
				if (lenR > 0)
					w.write(rez, 0, lenR);
				bis.close();
				q.close();
			} catch (IOException e){
				log.error(e);
				throw new ACException(TextID.MsgErrorExctractingMap, new String[] { e.getMessage() });
			}
		} else
			log.error("Tile index grater then tile count: " + TileID + " > " + header.getTileCount());
	}

	public GisFileHeader getHeader(){
	    return header;
    }

}
