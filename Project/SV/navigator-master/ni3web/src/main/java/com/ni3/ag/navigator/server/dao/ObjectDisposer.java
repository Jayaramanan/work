package com.ni3.ag.navigator.server.dao;

import com.ni3.ag.navigator.server.domain.CisObject;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;

/**
 * Contract for object disposing dispose can be either delete from DB or move to some archive table...
 */
public interface ObjectDisposer{
	void dispose(CisObject co, ObjectDefinition od);
}
