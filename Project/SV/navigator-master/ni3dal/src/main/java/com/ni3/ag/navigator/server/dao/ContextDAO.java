package com.ni3.ag.navigator.server.dao;

import java.util.List;

import com.ni3.ag.navigator.server.domain.Context;

public interface ContextDAO{

	List<Context> findByName(String name);
}
