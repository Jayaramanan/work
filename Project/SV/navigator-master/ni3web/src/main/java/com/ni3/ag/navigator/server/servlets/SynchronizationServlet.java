/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.server.domain.DeltaParam;
import com.ni3.ag.navigator.server.domain.DeltaParamIdentifier;
import com.ni3.ag.navigator.server.domain.Group;
import com.ni3.ag.navigator.server.domain.Icon;
import com.ni3.ag.navigator.server.domain.SyncStatus;
import com.ni3.ag.navigator.server.services.DeltaHeaderService;
import com.ni3.ag.navigator.server.session.ThreadLocalStorage;
import com.ni3.ag.navigator.server.sync.SynchronizationManager;
import com.ni3.ag.navigator.shared.domain.DeltaType;
import com.ni3.ag.navigator.shared.domain.User;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;
import com.ni3.ag.navigator.shared.proto.NRequest.Deltas;
import com.ni3.ag.navigator.shared.proto.NRequest.Synchronize;
import com.ni3.ag.navigator.shared.proto.NResponse.Envelope;
import com.ni3.ag.navigator.shared.proto.NResponse.ProcessedDeltas;
import com.ni3.ag.navigator.shared.proto.NResponse.SyncResult;
import com.ni3.ag.navigator.shared.proto.NResponse.UserGroup;
import com.ni3.ag.navigator.shared.proto.NResponse.Delta.Builder;
import com.ni3.ag.navigator.shared.proto.NResponse.SyncResult.Status;

@SuppressWarnings("serial")
public class SynchronizationServlet extends Ni3Servlet{
	private static final Logger log = Logger.getLogger(SynchronizationServlet.class);

	final SynchronizationManager synchronizationManager = NSpringFactory.getInstance().getSynchronizationManager();

	@Override
	public void doInternalPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	        IOException{
		log.debug("Servlet requested");
		Synchronize message = Synchronize.parseFrom(request.getInputStream());

		log.debug("Action called " + message.getAction());
		if (Synchronize.Action.CHECK_CONNECTIVITY.equals(message.getAction())){// jar -> war (check master available)
			handleCheckConnectivity(response);
		} else if (Synchronize.Action.CHECK_TABLE_LOCK.equals(message.getAction())){// war -> master (check tables)
			handleCheckTableLock(response);
		} else if (Synchronize.Action.GET_COUNT_TO_PUSH.equals(message.getAction())){// jar -> war (get my deltas count
			// to push to master)
			handleGetDeltasCountToPush(response);
		} else if (Synchronize.Action.PUSH_DELTAS.equals(message.getAction())){// jar -> war (start push process)
			handlePushDeltasToMaster(response);
		} else if (Synchronize.Action.PROCESS_OFFLINE_DELTAS.equals(message.getAction())){// war -> master (receive and
			// process offline deltas)
			handleOfflineDeltas(response, message.getDeltas());
		} else if (Synchronize.Action.LOGOUT_MASTER.equals(message.getAction())){// jar -> war (logout from master)
			handleLogoutMaster(response);
		} else if (Synchronize.Action.GET_COUNT_TO_ROLLON.equals(message.getAction())){// jar -> war (get deltas count
			// from master)
			handleGetCountToRollon(response);
		} else if (Synchronize.Action.GET_MASTER_DELTAS_COUNT.equals(message.getAction())){// war -> master (get deltas
			// count)
			handleGetMasterDeltasCount(response);
		} else if (Synchronize.Action.ROLLON_MASTER_DELTAS.equals(message.getAction())){// jar -> war (request some
			// deltas from master)
			handleRollonMasterDeltas(response);
		} else if (Synchronize.Action.GET_MASTER_DELTAS.equals(message.getAction())){// war -> master (get some deltas
			// to process)
			handleGetMasterDeltas(response);
		} else if (Synchronize.Action.COMMIT_PROCESSED_DELTAS.equals(message.getAction())){// war -> master (mark deltas
			// with processed statuses)
			handleCommitDeltas(response, message);
		} else if (Synchronize.Action.GET_MASTER_USERS.equals(message.getAction())){// war -> master (get users)
			handleGetMasterUsers(response);
		} else if (Synchronize.Action.CALL_SYNC_IMAGES.equals(message.getAction())){// jar -> war (sync images)
			handleCallImageSync(response, message);
		} else if (Synchronize.Action.GET_MASTER_ICONS.equals(message.getAction())){// war -> master
			handleGetMasterIcons(response);
		} else if (Synchronize.Action.PREPARE_DATA.equals(message.getAction())){
			handlePrepareDataRequest(response);
		}
	}

	private void handleGetMasterIcons(HttpServletResponse response){
		log.debug("handleGetMasterIcons");

		List<Icon> icons = synchronizationManager.getMyIcons();
		log.debug("Got icon count: " + icons.size());
		SyncResult.Builder builder = SyncResult.newBuilder();
		builder.setStatus(Status.OK);
		for (Icon i : icons)
			builder.addIcons(NResponse.Icon.newBuilder().setId(i.getId()).setIconName(i.getIconName()).build());
		Envelope env = Envelope.newBuilder().setStatus(Envelope.Status.SUCCESS).setPayload(builder.build().toByteString())
		        .build();
		try{
			log.debug("Sendcing icons to offline");
			env.writeTo(response.getOutputStream());
			log.debug("Sent");
		} catch (IOException ex){
			log.error("Error send master icons to offline");
			sendFail(response);
		}
	}

	private void handleCallImageSync(HttpServletResponse response, Synchronize message){
		try{
			ThreadLocalStorage localStorage = NSpringFactory.getInstance().getThreadLocalStorage();
			User currentUser = localStorage.getCurrentUser();
			String path = message.getMetaphorPath();
			String rez = getServletContext().getRealPath(path);
			log.debug("Context path " + rez);
			int index = rez.lastIndexOf("work");
			rez = rez.substring(0, index);
			path = rez + "webapps/" + path;
			log.debug("docroot " + path);
			int errors = synchronizationManager.syncImages(currentUser.getId(), path, message.getMetaphorPath());
			Envelope env = Envelope.newBuilder().setStatus(Envelope.Status.SUCCESS).setPayload(
			        SyncResult.newBuilder().setStatus(Status.OK).setErrorCount(errors).build().toByteString()).build();
			env.writeTo(response.getOutputStream());
		} catch (IOException ex){
			log.error("Error call sync images", ex);
			sendFail(response);
		}
	}

	@SuppressWarnings("unchecked")
	private void handleGetMasterUsers(HttpServletResponse response){
		try{
			Object[] result = synchronizationManager.getUsersAndGroups();
			if (result == null || result[0] == null || result[1] == null || result[2] == null){
				log.error("Failed to get user and group collections on master");
				log.error("Result: " + result);
				if (result != null)
					log.error("Result: " + Arrays.toString(result));
				Envelope env = Envelope.newBuilder().setStatus(Envelope.Status.SUCCESS).setPayload(
				        SyncResult.newBuilder().setStatus(SyncResult.Status.FAILED).build().toByteString()).build();
				env.writeTo(response.getOutputStream());
				return;
			}

			List<User> users = (List<User>) result[0];
			List<Group> groups = (List<Group>) result[1];
			Map<Integer, Integer> userGroups = (Map<Integer, Integer>) result[2];
			log.debug("Users: " + users.size());
			log.debug("Groups: " + groups.size());
			log.debug("Mapping: " + userGroups.size());

			NResponse.SyncResult.Builder builder = SyncResult.newBuilder();
			builder.setStatus(SyncResult.Status.OK);
			for (User u : users)
				builder.addUserIds(u.getId());
			for (Group g : groups)
				builder.addGroupIds(g.getId());

			for (int uid : userGroups.keySet())
				builder.addUserGroups(UserGroup.newBuilder().setUserId(uid).setGroupId(userGroups.get(uid)).build());

			Envelope env = Envelope.newBuilder().setStatus(Envelope.Status.SUCCESS).setPayload(
			        builder.build().toByteString()).build();
			env.writeTo(response.getOutputStream());
			log.debug("Result sent");
		} catch (IOException e){
			log.error("Error send master users and groups");
			sendFail(response);
		}
	}

	private void handleCommitDeltas(HttpServletResponse response, Synchronize message){
		log.debug("handleCommitDeltas");
		List<NRequest.ProcessedDelta> rDeltas = message.getProcessedIds().getProcessedList();
		List<DeltaHeader> deltas = new ArrayList<DeltaHeader>();
		for (NRequest.ProcessedDelta pd : rDeltas)
			deltas.add(new DeltaHeader(pd.getId(), pd.getStatus()));
		synchronizationManager.commitDeltas(deltas);
		Envelope env = Envelope.newBuilder().setStatus(Envelope.Status.SUCCESS).setPayload(
		        SyncResult.newBuilder().setStatus(SyncResult.Status.OK).build().toByteString()).build();
		try{
			env.writeTo(response.getOutputStream());
			log.debug("OK sent");
		} catch (IOException e){
			log.error("Error commit deltas");
			sendFail(response);
		}
	}

	@SuppressWarnings("unchecked")
	private void handleGetMasterDeltas(HttpServletResponse response){
		log.debug("handleGetMasterDeltas");
		ThreadLocalStorage localStorage = NSpringFactory.getInstance().getThreadLocalStorage();
		User currentUser = localStorage.getCurrentUser();
		Object[] result = synchronizationManager.getDeltasToPull(currentUser.getId());
		log.debug("result: " + result);
		if (result == null || result[1] == null){
			log.error("result is null - send error to offline");
			sendFail(response);
			return;
		}
		boolean hasMore = (Boolean) result[1];
		log.debug("Has more deltas for user: " + hasMore);
		Envelope env;
		if (result[0] == null){
			env = Envelope.newBuilder().setStatus(Envelope.Status.SUCCESS).setPayload(
			        SyncResult.newBuilder().setStatus(SyncResult.Status.FAILED).build().toByteString()).build();
			log.debug("making fail message for offline");
		} else{
			List<DeltaHeader> deltas = (List<DeltaHeader>) result[0];
			log.debug("extracted deltas count: " + deltas.size());
			NResponse.Deltas.Builder builder = NResponse.Deltas.newBuilder();
			for (DeltaHeader dho : deltas){
				Builder dBuilder = NResponse.Delta.newBuilder();
				log.debug("Processing " + dho.getId());
				dBuilder.setId(dho.getId());
				dBuilder.setDeltaType(dho.getDeltaType().intValue());
				dBuilder.setTimestamp(dho.getTimestamp().getTime());
				dBuilder.setSyncStatus(dho.getSyncStatus().intValue());
				dBuilder.setCreatorId(dho.getCreator().getId());
				dBuilder.setIsSync(dho.isSync());
				for (DeltaParam dp : dho.getDeltaParameters().values()){
					String dpName = dp.getName().getIdentifier();
					String dpValue = dp.getValue();
					if (dpValue == null){
						log.warn("param value for id = " + dpName + " is null, skipping");
						continue;
					}
					com.ni3.ag.navigator.shared.proto.NResponse.DeltaParam.Builder dpBuilder = NResponse.DeltaParam
					        .newBuilder();
					dBuilder.addDeltaParams(dpBuilder.setName(dpName).setValue(dpValue).build());
				}
				builder.addDeltas(dBuilder.build());
			}
			log.debug("message with deltas ready");
			env = Envelope.newBuilder().setStatus(Envelope.Status.SUCCESS).setPayload(
			        SyncResult.newBuilder().setStatus(SyncResult.Status.OK).setDeltas(builder.build()).setHasMoreToProcess(
			                hasMore).build().toByteString()).build();
		}
		try{
			env.writeTo(response.getOutputStream());
			log.debug("deltas sent to user");
		} catch (IOException e){
			log.error("Error send deltas from master to offline");
			sendFail(response);
		}
	}

	private void handleRollonMasterDeltas(HttpServletResponse response){
		log.debug("handleRollonMasterDeltas");
		ThreadLocalStorage localStorage = NSpringFactory.getInstance().getThreadLocalStorage();
		User currentUser = localStorage.getCurrentUser();
		try{
			if (!synchronizationManager.syncUsers(currentUser.getId())){
				log.error("Error synchronizing users");
				Envelope env = Envelope.newBuilder().setStatus(Envelope.Status.SUCCESS).setPayload(
				        SyncResult.newBuilder().setStatus(SyncResult.Status.FAILED).build().toByteString()).build();
				env.writeTo(response.getOutputStream());
				return;
			}
			Object[] result = synchronizationManager.processMasterDeltas(currentUser.getId());
			int ok = (Integer) result[0];
			int warn = (Integer) result[1];
			int error = (Integer) result[2];
			boolean hasMore = (Boolean) result[3];
			Envelope env = Envelope.newBuilder().setStatus(Envelope.Status.SUCCESS).setPayload(
			        SyncResult.newBuilder().setStatus(SyncResult.Status.OK).setOkCount(ok).setWarnCount(warn).setErrorCount(
			                error).setHasMoreToProcess(hasMore).build().toByteString()).build();
			env.writeTo(response.getOutputStream());
			log.debug("Result sent");
		} catch (IOException e){
			log.error("Error requesting deltas from master", e);
			sendFail(response);
		}
	}

	private void handleGetMasterDeltasCount(HttpServletResponse response){
		log.debug("handleGetMasterDeltasCount");
		ThreadLocalStorage localStorage = NSpringFactory.getInstance().getThreadLocalStorage();
		User currentUser = localStorage.getCurrentUser();
		Long count = synchronizationManager.getDeltaCountForUser(currentUser.getId());
		log.debug("Got count: " + count);
		if (count == null){
			log.error("Deltas count == null");
			sendFail(response);
			return;
		}
		try{
			Envelope env = Envelope.newBuilder().setStatus(Envelope.Status.SUCCESS).setPayload(
			        SyncResult.newBuilder().setStatus(SyncResult.Status.OK).setTotalCount(count.intValue()).build()
			                .toByteString()).build();
			env.writeTo(response.getOutputStream());
			log.debug("Result sent");
		} catch (IOException e){
			log.error("Error send delta count for user");
			sendFail(response);
		}
	}

	private void handleGetCountToRollon(HttpServletResponse response){
		log.debug("handleGetCountToRollon");
		ThreadLocalStorage localStorage = NSpringFactory.getInstance().getThreadLocalStorage();
		User currentUser = localStorage.getCurrentUser();
		try{
			synchronizationManager.sendPrepareDatRequestToMaster(currentUser.getId());
			Integer count = null;
			if (synchronizationManager.commitUncommittedDeltas(currentUser.getId()))
				count = synchronizationManager.getDeltaCountToPull(currentUser.getId());
			else
				log.error("Failed to commit uncommitted deltas");
			log.debug("Count: " + count);
			Envelope message = Envelope.newBuilder().setStatus(Envelope.Status.SUCCESS).setPayload(
			        SyncResult.newBuilder().setStatus(count == null ? SyncResult.Status.FAILED : SyncResult.Status.OK)
			                .setTotalCount(count == null ? 0 : count).build().toByteString()).build();
			message.writeTo(response.getOutputStream());
			log.debug("Result sent");
		} catch (IOException e){
			log.error("Error get deltas count from master", e);
			sendFail(response);
		}
	}

	private void handleLogoutMaster(HttpServletResponse response){
		try{
			ThreadLocalStorage localStorage = NSpringFactory.getInstance().getThreadLocalStorage();
			User currentUser = localStorage.getCurrentUser();
			boolean result = synchronizationManager.makeLogout(currentUser.getId());
			log.debug("Logout result: " + result);
			Envelope env = Envelope.newBuilder().setStatus(Envelope.Status.SUCCESS).setPayload(
			        SyncResult.newBuilder().setStatus(result ? SyncResult.Status.OK : SyncResult.Status.FAILED).build()
			                .toByteString()).build();
			env.writeTo(response.getOutputStream());
			log.debug("result sent");
		} catch (IOException e){
			log.error("Error make logout from master", e);
			sendFail(response);
		}
	}

	/*
	 * called from offline client to get delta count to push to master
	 */
	private void handleGetDeltasCountToPush(HttpServletResponse response){
		log.debug("handleGetDeltasCountToPush");
		try{
			DeltaHeaderService service = NSpringFactory.getInstance().getDeltaHeaderService();
			service.prepareDataForMaster();
			// TODO
			// SynchronizationManager.getInstance().prepareDeltasData();
			Integer deltaCount = synchronizationManager.getDeltaCountToPush();
			log.debug("Got delta count to push" + deltaCount);
			SyncResult sr = SyncResult.newBuilder().setStatus(
			        deltaCount == null ? SyncResult.Status.FAILED : SyncResult.Status.OK).setTotalCount(
			        deltaCount == null ? 0 : deltaCount).build();
			Envelope env = Envelope.newBuilder().setStatus(Envelope.Status.SUCCESS).setPayload(sr.toByteString()).build();
			env.writeTo(response.getOutputStream());
			log.debug("Response sent");
		} catch (IOException e){
			log.error("Error send delta count to push", e);
			sendFail(response);
		}
	}

	/*
	 * called on master to handle received offline deltas
	 */
	private void handleOfflineDeltas(HttpServletResponse response, Deltas message){
		log.debug("handleOfflineDeltas");
		ThreadLocalStorage localStorage = NSpringFactory.getInstance().getThreadLocalStorage();
		User currentUser = localStorage.getCurrentUser();

		List<NRequest.DeltaHeader> rDeltas = message.getDeltasList();
		log.debug("Delta list count: " + rDeltas.size());
		List<DeltaHeader> deltas = new ArrayList<DeltaHeader>();
		for (NRequest.DeltaHeader rdh : rDeltas){
			List<NRequest.DeltaParam> rParams = rdh.getParamsList();
			Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
			for (NRequest.DeltaParam ndp : rParams){
				DeltaParamIdentifier identifier = DeltaParamIdentifier.getById(ndp.getName());
				if (identifier == null)
					identifier = new DeltaParamIdentifier(ndp.getName());
				params.put(identifier, new DeltaParam(identifier, ndp.getValue()));
				if (log.isDebugEnabled())
					log.debug("Extracted: " + identifier);
			}

			DeltaHeader dh = new DeltaHeader(DeltaType.getById(rdh.getDeltaType()), currentUser, params);
			dh.setId(rdh.getId());
			dh.setTimestamp(new Date(rdh.getTimestamp()));
			dh.setSyncStatus(SyncStatus.fromInt(rdh.getSyncStatus()));
			dh.setSync(rdh.getIsSync());
			deltas.add(dh);
		}
		log.debug("Extracted deltas count: " + deltas.size());
		synchronizationManager.processOfflineDeltas(deltas);

		NResponse.ProcessedDeltas.Builder builder = ProcessedDeltas.newBuilder();
		for (DeltaHeader dh : deltas)
			builder.addProcessed(NResponse.ProcessedDelta.newBuilder().setId(dh.getId()).setStatus(
			        dh.getSyncStatus().intValue()).build());
		SyncResult sr = SyncResult.newBuilder().setStatus(SyncResult.Status.OK).setProcessDeltas(builder.build()).build();
		Envelope env = Envelope.newBuilder().setStatus(Envelope.Status.SUCCESS).setPayload(sr.toByteString()).build();
		try{
			env.writeTo(response.getOutputStream());
			log.debug("Result sent");
		} catch (IOException ex){
			log.error("Error send response to offline", ex);
			sendFail(response);
		}
	}

	/*
	 * called on offline to push deltas to master
	 */
	private void handlePushDeltasToMaster(HttpServletResponse response){
		log.debug("handlePushDeltasToMaster");
		try{
			ThreadLocalStorage localStorage = NSpringFactory.getInstance().getThreadLocalStorage();
			User currentUser = localStorage.getCurrentUser();
			ServletOutputStream os = response.getOutputStream();
			Object[] results = synchronizationManager.pushAllChangedToMaster(currentUser.getId());
			boolean hasMore = synchronizationManager.getDeltaCountToPush() > 0;
			int ok = (Integer) results[0];
			int warn = (Integer) results[1];
			int error = (Integer) results[2];
			log.info("OK: " + ok);
			log.info("WARN: " + warn);
			log.info("ERROR: " + error);
			log.debug("Has more: " + hasMore);
			SyncResult sResult = SyncResult.newBuilder().setStatus(SyncResult.Status.OK).setOkCount(ok).setWarnCount(warn)
			        .setErrorCount(error).setHasMoreToProcess(hasMore).build();
			Envelope env = Envelope.newBuilder().setStatus(Envelope.Status.SUCCESS).setPayload(sResult.toByteString())
			        .build();
			env.writeTo(os);
			log.debug("Result sent");
		} catch (IOException e){
			log.error("Error pusinh deltas to master", e);
			sendFail(response);
		}
	}

	/*
	 * called on offline to check availability of master
	 */
	private void handleCheckConnectivity(HttpServletResponse response){
		log.debug("handleCheckConnectivity");
		try{
			ThreadLocalStorage localStorage = NSpringFactory.getInstance().getThreadLocalStorage();
			User currentUser = localStorage.getCurrentUser();
			Status result = synchronizationManager.checkConnectionToMasterServer(currentUser.getId());
			log.debug("Result: " + result);
			SyncResult sMessage = SyncResult.newBuilder().setStatus(result).build();
			Envelope message = Envelope.newBuilder().setStatus(Envelope.Status.SUCCESS).setPayload(sMessage.toByteString())
			        .build();
			message.writeTo(response.getOutputStream());
			log.debug("Result sent");
		} catch (IOException e){
			log.error("Error check connectivity to master", e);
		}
	}

	@Override
	protected UserActivityType getActivityType(){
		// not used
		return null;
	}

	@Override
	protected List<LogParam> getActivityParams(){
		// not used
		return null;
	}

	/*
	 * common fail
	 */
	private void sendFail(HttpServletResponse response){
		try{
			Envelope.newBuilder().setStatus(Envelope.Status.FAILED).build().writeTo(response.getOutputStream());
		} catch (IOException ex){
			log.error("Error sending fail to client", ex);
		}
	}

	/*
	 * called on master to try to lock tables
	 */
	private void handleCheckTableLock(HttpServletResponse response){
		boolean result = synchronizationManager.checkTableLock();
		try{
			Envelope.newBuilder().setStatus(Envelope.Status.SUCCESS).setPayload(
			        SyncResult.newBuilder().setStatus(result ? SyncResult.Status.OK : SyncResult.Status.FAILED).build()
			                .toByteString()).build().writeTo(response.getOutputStream());
		} catch (IOException e){
			log.error("Error send response of table locking", e);
			sendFail(response);
		}
	}

	private void handlePrepareDataRequest(HttpServletResponse response){
		ThreadLocalStorage localStorage = NSpringFactory.getInstance().getThreadLocalStorage();
		User currentUser = localStorage.getCurrentUser();
		synchronizationManager.prepareDataForUser(currentUser.getId());
		Envelope env = Envelope.newBuilder().setStatus(Envelope.Status.SUCCESS).setPayload(
		        SyncResult.newBuilder().setStatus(Status.OK).build().toByteString()).build();
		try{
			log.debug("send ok to prepare request");
			env.writeTo(response.getOutputStream());
			log.debug("Sent");
		} catch (IOException ex){
			log.error("error send ok answer to prepare request");
			sendFail(response);
		}
	}
}
