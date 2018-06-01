/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.mapextraction;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;

public class RasterCache{
	private static final Logger log = Logger.getLogger(RasterCache.class);

	private String userName;
	private Rectangle area;

	public RasterCache(String userName, double x1, double x2, double y1, double y2){
		this(userName);
		int mx1 = WGS84Conversion.ConvertLongitudeToM(x1, y1);
		int mx2 = WGS84Conversion.ConvertLongitudeToM(x2, y2);
		int my1 = WGS84Conversion.ConvertLatitudeToM(y1);
		int my2 = WGS84Conversion.ConvertLatitudeToM(y2);
		area = new Rectangle(Math.min(mx1, mx2), Math.min(my1, my2), Math.abs(mx1 - mx2), Math.abs(my1 - my2));
	}

	private RasterCache(String userName){

		this.userName = userName;
	}

	public void createRaster(int Scale, String rootFolder) throws ACException{
		log.debug("Pool start v1.27");
		double RealWorldScaleFactor = 4500.00;

		double zoomf = RealWorldScaleFactor / Scale;

		zoomf = ((int) (zoomf * 100000.00)) / 100000.00;

		int TilesCountX, TilesCountY;

		int FileCounter = 1;

		FileOutputStream w = null;
		FileChannel fch = null;

		RasterMapLoader Loader = new RasterMapLoader(rootFolder, Scale);
		log.debug("Loader: " + Loader);

		int startTileX, startTileY, endTileX, endTileY;

		startTileX = Math.min(Loader.getHeader().getTilesCountX(), Math.max(0, -1+(area.x - Loader.getHeader().getX()) / Loader.getHeader().getStepX()));
		startTileY = Math.min(Loader.getHeader().getTilesCountY(), Math.max(0, -1+(area.y - Loader.getHeader().getY()) / Loader.getHeader().getStepY()));
		log.debug("startTileX: " + startTileX);
		log.debug("startTileY: " + startTileY);

		endTileX = Math.min(startTileX + 2 + (int)Math.ceil((float)area.width / Loader.getHeader().getStepX()), Loader.getHeader().getTilesCountX());
		endTileY = Math.min(startTileY + 2 + (int)Math.ceil((float)area.height / Loader.getHeader().getStepY()), Loader.getHeader().getTilesCountY());
		log.debug("endTileX: " + endTileX);
		log.debug("endTileY: " + endTileY);

		TilesCountX = endTileX - startTileX + 1;
		TilesCountY = endTileY - startTileY + 1;

		GisFileIndexItem[] indexItems = new GisFileIndexItem[TilesCountX * TilesCountY];

		int Tx, Ty, x, y;
		int counter = 0;
		int TotalTiles = TilesCountX * TilesCountY;

		for (Tx = startTileX, x = 0; Tx <= endTileX; Tx++ , x++){
			for (Ty = startTileY, y = 0; Ty <= endTileY; Ty++ , y++){
				counter++;
				if (counter % 1000 == 0)
					log.debug("Tile " + counter + "/" + TotalTiles);

				if (w == null){
					String path = getNewRootFolder(rootFolder, userName) + "-Part-" + (int) Scale + "-" + FileCounter
					        + ".dat";
					log.debug("path: " + path);
					try{
						File f = new File(path);
						w = new FileOutputStream(f);
					} catch (FileNotFoundException e){
						log.error("Cannot create file " + path);
						throw new ACException(TextID.MsgErrorExctractingMap, new String[] { e.getMessage() });
					}

					fch = w.getChannel();

					FileCounter++;
				}

				try{
					indexItems[x + y * TilesCountX] = new GisFileIndexItem();
					indexItems[x + y * TilesCountX].setFileIndex(FileCounter);
					indexItems[x + y * TilesCountX].setOffset((int)fch.position());
					Loader.copyRaster(w, Tx, Ty);
					indexItems[x + y * TilesCountX].setLen((int) (fch.position() - indexItems[x + y * TilesCountX].getOffset()));

					if (indexItems[x + y * TilesCountX].getOffset() > 1500000000){
						try{
							w.close();
							w = null;
							FileCounter++;
						} catch (IOException e1){
							log.error(e1);
							throw new ACException(TextID.MsgErrorExctractingMap, new String[] { e1.getMessage() });
						}
					}
				} catch (IOException e){
					log.error(e);
					throw new ACException(TextID.MsgErrorExctractingMap, new String[] { e.getMessage() });
				}
			}
		}

		try{
			if (w != null){
				w.close();
			}
		} catch (IOException e1){
			log.error(e1);
			throw new ACException(TextID.MsgErrorExctractingMap, new String[] { e1.getMessage() });
		}

		try{
			w = new FileOutputStream(new File(getNewRootFolder(rootFolder, userName) + "-Part-" + (int) Scale + ".idx"));
		} catch (FileNotFoundException e){
			log.error(e);
			throw new ACException(TextID.MsgErrorExctractingMap, new String[] { e.getMessage() });
		}

		ByteBuffer bb = ByteBuffer
		        .allocate(GisFileHeader.HEADER_SIZE + GisFileIndexItem.INDEX_ITEM_SIZE * indexItems.length);
		bb.put(Loader.getHeader().cloneModify(startTileX, startTileY, TilesCountX, TilesCountY));

		for (int n = 0; n < TilesCountX * TilesCountY; n++){
			bb.put(indexItems[n].toBytes());
		}
		bb.flip();

		try{
			w.getChannel().write(bb);
			w.close();
		} catch (IOException e){
			log.error(e);
			throw new ACException(TextID.MsgErrorExctractingMap, new String[] { e.getMessage() });
		}

		log.debug("Pool end");
	}

	private String getNewRootFolder(String oldRoot, String addFolder){
		File f = new File(oldRoot);
		File dir = new File(f.getParent() + File.separator + addFolder);
		if (!dir.exists()){
			dir.mkdir();
		}
		String newRoot = dir.getAbsolutePath() + File.separator + f.getName();
		return newRoot;
	}

}
