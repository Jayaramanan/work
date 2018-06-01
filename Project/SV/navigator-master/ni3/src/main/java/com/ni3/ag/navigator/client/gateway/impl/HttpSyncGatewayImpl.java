package com.ni3.ag.navigator.client.gateway.impl;

import java.io.IOException;

import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.client.gateway.SyncGateway;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.proto.NRequest.Synchronize;
import com.ni3.ag.navigator.shared.proto.NResponse.SyncResult;

public class HttpSyncGatewayImpl extends AbstractGatewayImpl implements SyncGateway{

	@Override
	public ByteString checkConnectivity() throws IOException{
		Synchronize message = Synchronize.newBuilder().setAction(Synchronize.Action.CHECK_CONNECTIVITY).build();
		return sendRequest(ServletName.SynchronizationServlet, message);
	}

	@Override
	public Integer getMyChangesCount() {
        Synchronize message = Synchronize.newBuilder().setAction(Synchronize.Action.GET_COUNT_TO_PUSH).build();
        try {
            ByteString payload = sendRequest(ServletName.SynchronizationServlet, message);
            SyncResult sr = SyncResult.parseFrom(payload);
            if (!SyncResult.Status.OK.equals(sr.getStatus())) {
                showErrorAndThrow("Error get changes count, status != OK", null);
            }
            return sr.getTotalCount();
        } catch (IOException e) {
            showErrorAndThrow("Error get changes count", null);
            return 0;
        }
    }

	@Override
	public SyncResult sendChangesToMaster(){
		try{
			Synchronize message = Synchronize.newBuilder().setAction(Synchronize.Action.PUSH_DELTAS).build();
            ByteString payload = sendRequest(ServletName.SynchronizationServlet, message);
            return SyncResult.parseFrom(payload);
		} catch (IOException ex){
            showErrorAndThrow("Error send changes to master", ex);
            return null;
        }
	}

	@Override
	public SyncResult logoutMaster(){
		try{
			Synchronize message = Synchronize.newBuilder().setAction(Synchronize.Action.LOGOUT_MASTER).build();
            ByteString payload = sendRequest(ServletName.SynchronizationServlet, message);
			SyncResult sr = SyncResult.parseFrom(payload);
			if (!SyncResult.Status.OK.equals(sr.getStatus())){
                showErrorAndThrow("Error logging out from master", null);
            }
			return sr;
		} catch (IOException ex){
            showErrorAndThrow("Error logging out from master", ex);
            return null;
        }
	}

	@Override
	public Integer getMasterChangesCount() {
        Synchronize message = Synchronize.newBuilder().setAction(Synchronize.Action.GET_COUNT_TO_ROLLON).build();
        try {
            ByteString payload = sendRequest(ServletName.SynchronizationServlet, message);
            SyncResult sr = SyncResult.parseFrom(payload);
            if (!SyncResult.Status.OK.equals(sr.getStatus())) {
                showErrorAndThrow("Error get changes count from master", null);
            }
            return sr.getTotalCount();
        } catch (IOException e) {
            showErrorAndThrow("Error get changes count from master", e);
            return null;
        }
    }

	@Override
	public SyncResult getChangesFromMaster() {
        Synchronize message = Synchronize.newBuilder().setAction(Synchronize.Action.ROLLON_MASTER_DELTAS).build();
        try {
            ByteString payload = sendRequest(ServletName.SynchronizationServlet, message);
            SyncResult sr = SyncResult.parseFrom(payload);
            if (!SyncResult.Status.OK.equals(sr.getStatus())) {
                showErrorAndThrow("Error get changes from master", null);
            }
            return sr;
        } catch (IOException e) {
            showErrorAndThrow("Error get changes from master", e);
            return null;
        }
    }

	@Override
	public SyncResult callImageSync(String path) {
        try {
            Synchronize message = Synchronize.newBuilder().setAction(Synchronize.Action.CALL_SYNC_IMAGES)
                    .setMetaphorPath(path).build();
            ByteString payload = sendRequest(ServletName.SynchronizationServlet, message);
            SyncResult sr = SyncResult.parseFrom(payload);
            if (!SyncResult.Status.OK.equals(sr.getStatus())) {
                showErrorAndThrow("Error synchronizing images", null);
            }
            return sr;
        } catch (IOException ex) {
            showErrorAndThrow("Error synchronizing images", ex);
            return null;
        }
    }
}
