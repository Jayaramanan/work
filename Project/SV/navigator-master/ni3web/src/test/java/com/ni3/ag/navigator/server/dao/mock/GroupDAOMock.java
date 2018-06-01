package com.ni3.ag.navigator.server.dao.mock;

import java.util.List;

import com.ni3.ag.navigator.server.dao.GroupDAO;
import com.ni3.ag.navigator.server.domain.Group;

public class GroupDAOMock implements GroupDAO{
	@Override
	public Group get(int id){
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void save(Group g){
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public List<Group> getGroups(){
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public long getCount(){
		return 0;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Group getByUser(Integer id){
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
