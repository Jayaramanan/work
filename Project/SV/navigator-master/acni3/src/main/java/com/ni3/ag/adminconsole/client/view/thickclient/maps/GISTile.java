/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.maps;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

public class GISTile{
	private static final Logger log = Logger.getLogger(GISTile.class);
	private ImageIcon image;
	private Rectangle bufRect;
	private int rasterID;
	private int tileID;
	private int lastUsed;
	private int length;
	private byte data[];

	public boolean imageInUse;

	public GISTile(String line, String RasterServerURL){
		imageInUse = false;

		StringTokenizerEx tokenizer = new StringTokenizerEx(line, "\t", false);
		rasterID = Integer.parseInt(tokenizer.nextToken());
		tileID = Integer.parseInt(tokenizer.nextToken());
		lastUsed = 0;

		bufRect = new Rectangle();
		bufRect.x = Integer.parseInt(tokenizer.nextToken());
		bufRect.y = Integer.parseInt(tokenizer.nextToken());
		bufRect.width = Integer.parseInt(tokenizer.nextToken());
		bufRect.height = Integer.parseInt(tokenizer.nextToken());
		length = Integer.parseInt(tokenizer.nextToken());

		data = null;

		if (RasterServerURL != null)
			getRasterFromServer(RasterServerURL);
	}

	public void getRasterFromServer(String rasterServerURL){
		if (data != null)
			return;

		log.debug("Get raster " + rasterID + "-" + tileID + " len: " + length);

		if (length > 1000000)
			return;

		data = new byte[length];
		URL imageURL = null;
		URLConnection connection = null;

		try{
			imageURL = new URL(rasterServerURL + "/GetArea?RasterID=" + rasterID + "&TileID=" + tileID);
			connection = imageURL.openConnection();
		} catch (MalformedURLException e){
			log.error("raster server unreachable", e);
		} catch (IOException e){
			log.error("cant connect to raster server", e);
		}

		int offset = 0, len = 0, retlen;

		try{
			InputStream is = connection.getInputStream();
			while (len < length){
				try{
					retlen = is.read(data, offset, length - len);
					if (retlen < 0){
						log.warn("Read raster - unexpected end of stream : " + offset + " - " + (length - len) + "-"
						        + length);

						break;
					}

					offset += retlen;
					len += retlen;
				} catch (IndexOutOfBoundsException e){
					data = null;
					log.error("Read raster - unexpected end of stream : " + offset + " - " + (length - len) + "-" + length,
					        e);
				}
			}
		} catch (IOException e){
			log.error(e);
		}
	}

	public void loadImage(){
		imageInUse = true;
		if (image == null && data != null){
			log.debug("Load image " + rasterID + "-" + tileID);
			try{
				image = new ImageIcon(data);
			} catch (OutOfMemoryError e){
				image = null;
			}
		}
	}

	public void disposeImage(){
		if (image != null){
			log.debug("Dispose image " + rasterID + "-" + tileID);
			imageInUse = false;
			image = null;
		}
	}

	public int getLastUsed(){
	    return lastUsed;
    }

	public void setLastUsed(int lastUsed){
	    this.lastUsed = lastUsed;
    }

	public int getRasterID(){
	    return rasterID;
    }

	public int getTileID(){
	    return tileID;
    }

	public Rectangle getBufRect(){
	    return bufRect;
    }

	public ImageIcon getImage(){
	    return image;
    }
}
