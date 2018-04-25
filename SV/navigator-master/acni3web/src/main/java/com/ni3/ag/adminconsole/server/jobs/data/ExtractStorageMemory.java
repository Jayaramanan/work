package com.ni3.ag.adminconsole.server.jobs.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExtractStorageMemory implements ExtractStorage{
	private List<String> storage = new ArrayList<String>();

	@Override
	public void add(String o){
		storage.add(o);
	}

	@Override
	public Iterator<String> iterator(){
		return storage.iterator();
	}

	@Override
	public void clean(){
		storage.clear();
	}

	@Override
	public int size(){
		return storage.size();
	}
}
