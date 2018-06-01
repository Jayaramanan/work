package com.ni3.ag.navigator.server.dao;

import java.util.List;

import com.ni3.ag.navigator.server.domain.Schema;

public interface SchemaDAO{
	Schema getSchema(int id);

	List<Schema> getSchemas();
}
