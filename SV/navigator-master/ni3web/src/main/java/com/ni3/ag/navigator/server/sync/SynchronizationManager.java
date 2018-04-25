/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.sync;

import java.io.IOException;
import java.util.List;

import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.server.domain.Icon;
import com.ni3.ag.navigator.shared.proto.NResponse.SyncResult.Status;

public interface SynchronizationManager{

	List<Icon> getMyIcons();

	int syncImages(int userId, String path, String paramPath) throws IOException;

	Status checkConnectionToMasterServer(int userId) throws IOException;

	Object[] getUsersAndGroups();

	void commitDeltas(List<DeltaHeader> deltas);

	Object[] getDeltasToPull(Integer userId);

	boolean syncUsers(int userId) throws IOException;

	Object[] processMasterDeltas(int userId) throws IOException;

	Long getDeltaCountForUser(Integer userId);

	void sendPrepareDatRequestToMaster(int userId) throws IOException;

	boolean commitUncommittedDeltas(int userId);

	Integer getDeltaCountToPull(int userId) throws IOException;

	boolean makeLogout(Integer userId);

	Integer getDeltaCountToPush();

	void processOfflineDeltas(List<DeltaHeader> deltas);

	Object[] pushAllChangedToMaster(Integer userId) throws IOException;

	boolean checkTableLock();

	void prepareDataForUser(Integer id);

}
