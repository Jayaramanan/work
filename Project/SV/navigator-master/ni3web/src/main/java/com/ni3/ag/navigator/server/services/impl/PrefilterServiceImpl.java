package com.ni3.ag.navigator.server.services.impl;

import java.util.List;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.PrefilterDAO;
import com.ni3.ag.navigator.server.services.PrefilterService;
import com.ni3.ag.navigator.shared.domain.Prefilter;

public class PrefilterServiceImpl implements PrefilterService{

	@Override
	public List<Prefilter> getPrefilter(int groupId, int schemaId){
		PrefilterDAO prefilterDAO = NSpringFactory.getInstance().getPrefilterDAO();
		return prefilterDAO.getPrefilter(groupId, schemaId);
	}
}
