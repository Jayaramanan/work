package com.ni3.ag.navigator.server.dao;

import com.ni3.ag.navigator.server.domain.Group;
import java.util.List;

public interface GroupDAO{

	Group get(int id);

	void save(Group g);

	List<Group> getGroups();

	long getCount();

	public Group getByUser(Integer id);
}
