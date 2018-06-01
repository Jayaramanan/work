package com.ni3.ag.navigator.server.dao;

import java.util.List;

import com.ni3.ag.navigator.shared.domain.Prefilter;

public interface PrefilterDAO{
	List<Prefilter> getPrefilter(int groupId, int schemaId);
}
