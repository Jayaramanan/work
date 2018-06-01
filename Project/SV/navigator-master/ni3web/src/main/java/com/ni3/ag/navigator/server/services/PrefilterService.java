package com.ni3.ag.navigator.server.services;

import java.util.List;

import com.ni3.ag.navigator.shared.domain.Prefilter;

public interface PrefilterService{
	List<Prefilter> getPrefilter(int groupId, int schemaId);
}
