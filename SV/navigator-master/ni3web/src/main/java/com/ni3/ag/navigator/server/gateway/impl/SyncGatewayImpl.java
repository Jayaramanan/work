package com.ni3.ag.navigator.server.gateway.impl;

import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.server.domain.DeltaParam;
import java.io.IOException;
import java.util.List;

import com.ni3.ag.navigator.server.gateway.SyncGateway;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.gateway.AbstractGateway;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NRequest.Deltas;
import com.ni3.ag.navigator.shared.proto.NRequest.Deltas.Builder;
import com.ni3.ag.navigator.shared.proto.NRequest.Synchronize;
import com.ni3.ag.navigator.shared.proto.NResponse.Envelope;
import com.ni3.ag.navigator.shared.proto.NResponse.SyncResult;

public class SyncGatewayImpl extends AbstractGateway implements SyncGateway{

	@Override
	public Envelope sendDeltas(List<DeltaHeader> deltas, String url, String session) throws IOException{
		Builder builder = Deltas.newBuilder();
		for (DeltaHeader dh : deltas)
			builder.addDeltas(makeSendableDelta(dh));
		Synchronize message = Synchronize.newBuilder().setAction(Synchronize.Action.PROCESS_OFFLINE_DELTAS)
		        .setDeltas(builder.build()).build();
		return sendRequest(url, ServletName.SynchronizationServlet, message, session);
	}

	private com.ni3.ag.navigator.shared.proto.NRequest.DeltaHeader makeSendableDelta(DeltaHeader dh){
		com.ni3.ag.navigator.shared.proto.NRequest.DeltaHeader.Builder builder = com.ni3.ag.navigator.shared.proto.NRequest.DeltaHeader
		        .newBuilder();
		builder.setId(dh.getId());
		builder.setDeltaType(dh.getDeltaType().intValue());
		builder.setTimestamp(dh.getTimestamp().getTime());
		builder.setSyncStatus(dh.getSyncStatus().intValue());
		builder.setCreator(dh.getCreator().getId());
		builder.setIsSync(true);
		for (DeltaParam dp : dh.getDeltaParameters().values()){
			if(dp.getValue() == null)
				continue;
			builder.addParams(makeDeltaParam(dp));
		}
		return builder.build();
	}

	private com.ni3.ag.navigator.shared.proto.NRequest.DeltaParam makeDeltaParam(DeltaParam dp){
		com.ni3.ag.navigator.shared.proto.NRequest.DeltaParam.Builder builder = com.ni3.ag.navigator.shared.proto.NRequest.DeltaParam
		        .newBuilder();
		builder.setId(dp.getId());
		builder.setName(dp.getName().getIdentifier());
		builder.setValue(dp.getValue());
		return builder.build();
	}

	@Override
	public boolean checkConnectivity(String url, String session) throws IOException{
		Synchronize req = Synchronize.newBuilder().setAction(Synchronize.Action.CHECK_TABLE_LOCK).build();
		Envelope response = sendRequest(url, ServletName.SynchronizationServlet, req, session);
		if (!Envelope.Status.SUCCESS.equals(response.getStatus()))
			return false;
		SyncResult result = SyncResult.parseFrom(response.getPayload());
		return SyncResult.Status.OK.equals(result.getStatus());
	}

	@Override
	public Integer getMasterDeltaCount(String url, String sessionID) throws IOException{
		Synchronize req = Synchronize.newBuilder().setAction(Synchronize.Action.GET_MASTER_DELTAS_COUNT).build();
		Envelope response = sendRequest(url, ServletName.SynchronizationServlet, req, sessionID);
		if (!Envelope.Status.SUCCESS.equals(response.getStatus()))
			return null;
		SyncResult result = SyncResult.parseFrom(response.getPayload());
		if (!SyncResult.Status.OK.equals(result.getStatus()))
			return null;
		return result.getTotalCount();
	}

	@Override
	public SyncResult getDeltasFromMaster(String url, String session) throws IOException{
		Synchronize message = Synchronize.newBuilder().setAction(Synchronize.Action.GET_MASTER_DELTAS).build();
		Envelope env = sendRequest(url, ServletName.SynchronizationServlet, message, session);
		if (env == null || !Envelope.Status.SUCCESS.equals(env.getStatus()))
			return null;
		SyncResult sr = SyncResult.parseFrom(env.getPayload());
		if (sr == null || !SyncResult.Status.OK.equals(sr.getStatus()))
			return null;
		return sr;
	}

	@Override
	public void commitProcessedDeltasToMaster(List<DeltaHeader> processedDeltas, String url, String session)
	        throws IOException{
		NRequest.ProcessedDeltas.Builder builder = NRequest.ProcessedDeltas.newBuilder();
		for (DeltaHeader dh : processedDeltas){
			builder.addProcessed(NRequest.ProcessedDelta.newBuilder().setId(dh.getId())
			        .setStatus(dh.getSyncStatus().intValue()).build());
		}
		Synchronize message = Synchronize.newBuilder().setAction(Synchronize.Action.COMMIT_PROCESSED_DELTAS)
		        .setProcessedIds(builder.build()).build();
		sendRequest(url, ServletName.SynchronizationServlet, message, session);
	}

	@Override
	public SyncResult getUsersAndGroups(String url, String session) throws IOException{
		Synchronize message = Synchronize.newBuilder().setAction(Synchronize.Action.GET_MASTER_USERS).build();
		Envelope env = sendRequest(url, ServletName.SynchronizationServlet, message, session);
		if (env == null || !Envelope.Status.SUCCESS.equals(env.getStatus()))
			return null;
		SyncResult sr = SyncResult.parseFrom(env.getPayload());
		if (sr == null || !SyncResult.Status.OK.equals(sr.getStatus()))
			return null;
		return sr;
	}

	@Override
	public SyncResult getMasterIcons(String url, String session) throws IOException{
		Synchronize message = Synchronize.newBuilder().setAction(Synchronize.Action.GET_MASTER_ICONS).build();
		Envelope env = sendRequest(url, ServletName.SynchronizationServlet, message, session);
		SyncResult sr = SyncResult.parseFrom(env.getPayload());
		if (!SyncResult.Status.OK.equals(sr.getStatus()))
			return null;
		return sr;
	}

	@Override
	public void sendPrepareDataRequest(int userId, String url, String session) throws IOException{
		Synchronize message = Synchronize.newBuilder().setAction(Synchronize.Action.PREPARE_DATA).build();
		Envelope env = sendRequest(url, ServletName.SynchronizationServlet, message, session);
		SyncResult sr = SyncResult.parseFrom(env.getPayload());
		if (!SyncResult.Status.OK.equals(sr.getStatus()))
			throw new IOException("Error send prepare data reuqest to master");
	}

}
