package com.ni3.ag.navigator.server.gateway;

import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.shared.proto.NResponse.Envelope;
import com.ni3.ag.navigator.shared.proto.NResponse.SyncResult;

import java.io.IOException;
import java.util.List;

public interface SyncGateway{

	boolean checkConnectivity(String url, String session) throws IOException;

	Envelope sendDeltas(List<DeltaHeader> deltas, String url, String session) throws IOException;

	Integer getMasterDeltaCount(String url, String sessionID) throws IOException;

	SyncResult getDeltasFromMaster(String url, String session) throws IOException;

	void commitProcessedDeltasToMaster(List<DeltaHeader> processedDeltas, String url, String session) throws IOException;

	SyncResult getUsersAndGroups(String string, String sessionID) throws IOException;

	SyncResult getMasterIcons(String url, String session) throws IOException;

	void sendPrepareDataRequest(int userId, String url, String sessionId) throws IOException;
}
