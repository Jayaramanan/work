package com.ni3.ag.navigator.client.domain.cache;

import java.io.File;

import com.ni3.ag.navigator.client.model.SystemGlobals;
import org.apache.log4j.Logger;

public abstract class AbstractCache{

	private static final Logger log = Logger.getLogger(AbstractCache.class);

	protected abstract String getName();

	protected File getRootDirectory(){
		StringBuilder builder = new StringBuilder();
		builder.append(SystemGlobals.getUserHomeDir());
		builder.append(File.separatorChar);
		builder.append(".ni3");
		builder.append(File.separatorChar);
		builder.append(SystemGlobals.Instance);
		builder.append(File.separatorChar);
		builder.append(getName());
		builder.append(File.separatorChar);
		File cacheDirectory = new File(builder.toString());
		if (!cacheDirectory.exists()){
			cacheDirectory.mkdirs();
		}

		return cacheDirectory;
	}

	public boolean cleanup(){
		final File cacheDirectory = new File(getRootDirectory(), getName());
		return cacheDirectory.exists() ? removeDirectory(cacheDirectory) : true;
	}

	private boolean removeDirectory(final File directory){
		if (directory.isDirectory()){
			final String[] list = directory.list();
			if (list != null){
				for (final String name : list){
					final File entry = new File(name);
					if (entry.isDirectory()){
						if (!removeDirectory(entry)){
							return false;
						}
					} else{
						if (!entry.delete()){
							return false;
						}
					}
				}
			}
		}

		return directory.delete();
	}
}
