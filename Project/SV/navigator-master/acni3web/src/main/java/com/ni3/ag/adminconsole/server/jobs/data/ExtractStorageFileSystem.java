package com.ni3.ag.adminconsole.server.jobs.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class ExtractStorageFileSystem implements ExtractStorage{
	private static final Logger log = Logger.getLogger(ExtractStorageFileSystem.class);
	private static int uniqID = 1;
	private int count;
	private File file;
	private FileOutputStream fileOutputStream;
	private FileInputStream fileInputStream;
	private BufferedOutputStream outputStream;
	private BufferedInputStream inputStream;
	private Iterator<String> iterator;

	@Override
	public Iterator<String> iterator(){
		if (iterator == null)
			createIterator();
		return iterator;
	}

	private void createIterator(){
		if (outputStream != null)
			try{
				outputStream.close();
				outputStream = null;
			} catch (IOException e1){
				log.error("Error flushing file output stream", e1);
			}
		if (fileOutputStream != null)
			try{
				fileOutputStream.flush();
				fileOutputStream.close();
				fileOutputStream = null;
			} catch (IOException e1){
				log.error("Error flushing file output stream", e1);
			}
		try{
			if (file != null && file.exists()){
				fileInputStream = new FileInputStream(file);
				inputStream = new BufferedInputStream(fileInputStream);
				iterator = new FileSystemStorageIterator(inputStream);
			} else{
				iterator = createEmptyIterator();
			}
		} catch (IOException e){
			log.error("Error creating `FileSystemStorageIterator` iterator", e);
		}
	}

	private Iterator<String> createEmptyIterator(){
		Iterator<String> it = new Iterator<String>(){

			@Override
			public void remove(){
			}

			@Override
			public String next(){
				return null;
			}

			@Override
			public boolean hasNext(){
				return false;
			}
		};
		return it;
	}

	@Override
	public void add(String o){
		try{
			if (outputStream == null)
				createOutputStream();
			byte[] data = o.getBytes("UTF-8");
			ByteBuffer buf = ByteBuffer.allocate(4);
			buf.putInt(data.length);
			outputStream.write(buf.array());
			outputStream.write(data);
			count++;
		} catch (IOException e){
			log.error("Error add object to storage", e);
		}
	}

	private void createOutputStream() throws FileNotFoundException, IOException{
		file = new File("extract" + (++uniqID) + ".storage");
		fileOutputStream = new FileOutputStream(file);
		outputStream = new BufferedOutputStream(fileOutputStream);
	}

	@Override
	public void clean(){
		if (outputStream != null)
			try{
				outputStream.close();
			} catch (IOException e1){
				log.error("Error closing object output stream", e1);
			}
		outputStream = null;
		if (fileOutputStream != null)
			try{
				fileOutputStream.close();
			} catch (IOException e){
				log.error("Error closing file output stream", e);
			}
		fileOutputStream = null;
		if (inputStream != null)
			try{
				inputStream.close();
			} catch (IOException e){
				log.error("Error closing object input stream", e);
			}
		inputStream = null;
		if (fileInputStream != null)
			try{
				fileInputStream.close();
			} catch (IOException e){
				log.error("Error closing file input stream", e);
			}
		fileInputStream = null;
		if (file != null && !file.delete())
			log.warn("Error deleting temp file (extract storage)");
		file = null;
		iterator = null;
		count = 0;
	}

	@Override
	public int size(){
		return count;
	}

	private class FileSystemStorageIterator implements Iterator<String>{
		private BufferedInputStream ois;

		public FileSystemStorageIterator(BufferedInputStream inputStream){
			ois = inputStream;
		}

		@Override
		public boolean hasNext(){
			try{
				return ois.available() > 0;
			} catch (IOException e){
				log.warn("Iterator cannot process hasNext method - ObjectInputStream.avalible thrown an exception", e);
				return false;
			}
		}

		@Override
		public String next(){
			try{
				byte[] buf = new byte[4];
				if (ois.read(buf) != 4)
					return null;
				ByteBuffer bb = ByteBuffer.wrap(buf);
				int len = bb.getInt();
				byte[] store = new byte[len];
				if (ois.read(store) != len)
					return null;
				return new String(store, "UTF-8");
			} catch (IOException e){
				log.error("Error read object from object input stream", e);
			}
			return null;
		}

		@Override
		public void remove(){
			// do nothing
		}
	}
}
