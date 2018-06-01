package com.ni3.ag.adminconsole.server.service;

import java.util.List;

import com.ni3.ag.adminconsole.domain.DeltaHeader;
import com.ni3.ag.adminconsole.domain.DeltaType;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.server.importers.UserDataTable;

public interface DeltaService{

	void saveAll(List<DeltaHeader> deltaHeaders);

	DeltaHeader getDeltaHeader(DeltaType type, Integer userId, Integer objectId, ObjectDefinition entity,
			UserDataTable data, int row);

}