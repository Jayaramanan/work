package com.ni3.ag.adminconsole.server.jobs.data;

public interface ExtractStorage extends Iterable<String>{

	void add(String o);

	void clean();

	int size();

}
