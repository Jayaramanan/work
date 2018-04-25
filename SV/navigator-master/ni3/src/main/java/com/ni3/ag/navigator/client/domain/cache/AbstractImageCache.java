package com.ni3.ag.navigator.client.domain.cache;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import com.ni3.ag.navigator.client.gateway.IconGateway;

public abstract class AbstractImageCache extends AbstractCache{

	private static final Logger log = Logger.getLogger(AbstractImageCache.class);

	private final Map<String, Image> inMemoryCache = new HashMap<String, Image>();

	@Override
	public boolean cleanup(){
		inMemoryCache.clear();
		return super.cleanup();
	}

	public Image getImage(final String name){
		Image image = null;
		if (inMemoryCache.containsKey(name)){
			image = inMemoryCache.get(name);
		} else{
			image = load(name);

			// TODO: HACK
			if (image == null){
				image = loadRemoteImage(name);
			}
			// TODO: END HACK

			if (image != null){
				inMemoryCache.put(name, image);
			}
		}
		return image;
	}

	@Deprecated
	protected abstract IconGateway getImageLoader();

	@Deprecated
	private Image loadRemoteImage(String name){
		final Image image = getImageLoader().loadImage(name);
		if (image != null){
			saveImage(name, image);
		}
		return image;
	}

	public ImageIcon getImageIcon(final String name){
		Image image = getImage(name);
		if (image == null){
			image = getImage("all.png");
		}
		return image != null ? new ImageIcon(image) : null;
	}

	private Image load(final String name){
		final File file = new File(getRootDirectory(), name);
		BufferedImage image = null;
		if (file.canRead()){
			try{
				image = ImageIO.read(file);
			} catch (final IOException e){
				log.error("Can't load cached image from disk: " + file.getName(), e);
				try{
					file.delete();
				} catch (Exception e1){
					// ignore
				}
			}
		}
		return image;
	}

	public boolean saveImage(final String name, final Image image){
		inMemoryCache.put(name, image);

		boolean ret = false;
		final File file = new File(getRootDirectory(), name);
		try{
			ret = ImageIO.write((RenderedImage) image, "png", file);
		} catch (final IOException e){
			log.error("Can't save image to disk: " + file.getName(), e);
		}
		return ret;
	}
}
