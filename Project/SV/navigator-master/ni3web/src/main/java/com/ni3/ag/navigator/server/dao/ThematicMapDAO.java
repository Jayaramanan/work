package com.ni3.ag.navigator.server.dao;

import java.util.List;

import com.ni3.ag.navigator.shared.domain.ThematicMap;

public interface ThematicMapDAO{

	int createThematicMap(ThematicMap thematicMap);

	void updateThematicMap(ThematicMap thematicMap);

	ThematicMap getThematicMap(int id);

	List<ThematicMap> getThematicMapsByFolderId(int folderId, int groupId);

	ThematicMap getThematicMapByName(String name, int folderId, int groupId);

    void deleteThematicMap(int thematicMapId);

	int createThematicMapWithId(ThematicMap tm);

	List<ThematicMap> getThematicMaps();
}
