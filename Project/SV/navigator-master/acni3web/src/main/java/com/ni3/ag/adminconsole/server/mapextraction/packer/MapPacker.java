package com.ni3.ag.adminconsole.server.mapextraction.packer;

import java.io.*;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.log4j.Logger;

public class MapPacker{
	private static final Logger log = Logger.getLogger(MapPacker.class);

	public void packResultMaps(String tempDirectory, String zipName, String dirName){
		log.debug("Packing result to " + zipName);
		log.debug("Source dir: " + dirName);
		File tempDir = new File(tempDirectory);
		if (!tempDir.exists())
			if (!tempDir.mkdir())
				log.error("Error create dir: " + tempDir);
		String archiveName = tempDirectory + zipName;
		FileOutputStream outfs = null;
		boolean success = false;
		try{
			outfs = new FileOutputStream(archiveName);
			CompressorOutputStream bzipOut = new CompressorStreamFactory().createCompressorOutputStream(
			        CompressorStreamFactory.BZIP2, outfs);
			TarArchiveOutputStream tarOut = new TarArchiveOutputStream(bzipOut);
			File dir = new File(dirName);
			File[] files = dir.listFiles();
			for (File file : files){
				if (file.isDirectory())
					continue;
				putFileToArchive(file, tarOut);
			}
			tarOut.close();
			bzipOut.close();
			outfs.close();
			success = true;
		} catch (IOException ex){
			log.error("Error create map extraction result archive", ex);
		} catch (CompressorException ex){
			log.error("Error create map extraction result archive", ex);
		} finally{
			if (outfs != null)
				try{
					outfs.close();
				} catch (IOException e){
					log.error("Error closing file", e);
				}
			if (!success)
				if (!new File(archiveName).delete())
					log.error("Error deleting invalid result archive");
		}
	}

	private void putFileToArchive(File file, TarArchiveOutputStream tarOut) throws IOException{
		log.debug("\tArchiving " + file.getName());
		FileInputStream infs = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(infs);
		TarArchiveEntry entry = new TarArchiveEntry(file);
		tarOut.putArchiveEntry(entry);
		byte[] buf = new byte[100 * 1024];
		while (bis.available() > 0){
			int count = bis.read(buf);
			tarOut.write(buf, 0, count);
		}
		tarOut.closeArchiveEntry();
		infs.close();
		bis.close();
	}
}
