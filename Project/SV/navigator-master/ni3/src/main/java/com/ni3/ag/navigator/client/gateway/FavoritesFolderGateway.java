/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gateway;

import java.util.List;

import com.ni3.ag.navigator.client.domain.Folder;

public interface FavoritesFolderGateway{

	List<Folder> getFolders(int schemaID);

	int createFolder(Folder folder);

	void updateFolder(Folder folder);

	void deleteFolder(Folder folder);

}
