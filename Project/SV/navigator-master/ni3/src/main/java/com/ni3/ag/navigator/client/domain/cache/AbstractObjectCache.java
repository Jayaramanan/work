package com.ni3.ag.navigator.client.domain.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

public abstract class AbstractObjectCache<ObjectType> extends AbstractCache{

	private static final Logger log = Logger.getLogger(AbstractObjectCache.class);

	protected boolean save(final String name, final ObjectType object){
		boolean saved = false;
		final File file = new File(getRootDirectory(), name);
		ObjectOutputStream objectStream = null;
		try{
			final FileOutputStream fileStream = new FileOutputStream(file);
			objectStream = new ObjectOutputStream(fileStream);
			objectStream.writeObject(object);
			saved = true;
		} catch (Exception e){
			log.error("Can't save cached object to disk", e);
			try{
				file.delete();
			} catch (Exception e1){
				// ignore
			}
		} finally{
			if (objectStream != null){
				try{
					objectStream.close();
				} catch (IOException e){
					// ignore
				}
			}
		}
		return saved;
	}

	@SuppressWarnings("unchecked")
	protected ObjectType load(final String name){
		ObjectType castedObject = null;
		final File file = new File(getRootDirectory(), name);
		ObjectInputStream objectStream = null;
		if (file.canRead()){
			try{
				final FileInputStream fileStream = new FileInputStream(file);
				objectStream = new ObjectInputStream(fileStream);
				final Object object = objectStream.readObject();
				castedObject = (ObjectType) object;
			} catch (Exception e){
				log.error("Can't load cached object from disk", e);
				try{
					file.delete();
				} catch (Exception e1){
					// ignore
				}
			} finally{
				if (objectStream != null){
					try{
						objectStream.close();
					} catch (IOException e){
						// ignore
					}
				}
			}
		}
		return castedObject;
	}

}
