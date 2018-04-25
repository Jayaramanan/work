package com.ni3.ag.navigator.client.gateway;

import java.io.IOException;

import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.shared.proto.NResponse.SyncResult;

public interface SyncGateway{

	ByteString checkConnectivity() throws IOException;

	SyncResult sendChangesToMaster();

	Integer getMyChangesCount();

	SyncResult logoutMaster();

	Integer getMasterChangesCount();

	SyncResult getChangesFromMaster();

	SyncResult callImageSync(String path);
}
