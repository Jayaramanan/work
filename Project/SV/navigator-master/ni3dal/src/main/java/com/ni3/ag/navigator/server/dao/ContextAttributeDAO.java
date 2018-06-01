package com.ni3.ag.navigator.server.dao;

import com.ni3.ag.navigator.server.domain.ContextAttribute;
import java.util.List;

public interface ContextAttributeDAO{

	List<ContextAttribute> findByContextId(Integer id);

}
