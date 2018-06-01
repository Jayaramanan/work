package com.ni3.ag.navigator.server.jobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.DeltaHeaderDAO;
import com.ni3.ag.navigator.server.dao.DeltaHeaderUserDAO;
import com.ni3.ag.navigator.server.dao.ObjectUserGroupDAO;
import com.ni3.ag.navigator.server.dao.UserGroupDAO;
import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.server.domain.DeltaParam;
import com.ni3.ag.navigator.server.domain.DeltaParamIdentifier;
import com.ni3.ag.navigator.server.domain.SyncStatus;
import com.ni3.ag.navigator.server.services.UserService;
import com.ni3.ag.navigator.shared.domain.DeltaType;
import com.ni3.ag.navigator.shared.domain.User;

public class DeltaUserRouterJob implements StatefulJob{

	private static final Logger log = Logger.getLogger(DeltaUserRouterJob.class);

	final DeltaHeaderUserDAO deltaHeaderUserDao = NSpringFactory.getInstance().getDeltaHeaderUserDAO();
	final DeltaHeaderDAO deltaHeaderDao = NSpringFactory.getInstance().getDeltaHeaderDAO();
	final UserGroupDAO userGroupDao = NSpringFactory.getInstance().getUserGroupDao();
	final ObjectUserGroupDAO objectUserGroupDao = NSpringFactory.getInstance().getObjectUserGroupDao();
	final UserService userService = NSpringFactory.getInstance().getUserService();

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException{
		log.info("Job Started");
		process();
		log.info("Job Finished");
	}

	private Map<Integer, Integer> loadUserMap(){
		final Map<Integer, Integer> userGroups = userGroupDao.getUserGroups();
		final List<User> users = userService.getOfflineUsers();
		final Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		for (final User user : users){
			result.put(user.getId(), userGroups.get(user.getId()));
		}

		return result;
	}

	private boolean objectAccessible(final DeltaHeader delta, final Integer userId, final Map<Integer, Integer> groups,
	        Map<Integer, List<Integer>> readAcl){
		Map<DeltaType, DeltaParamIdentifier> typeIdMap = new HashMap<DeltaType, DeltaParamIdentifier>();
		typeIdMap.put(DeltaType.EDGE_CREATE, DeltaParamIdentifier.CreateEdgeObjectDefinitionId);
		typeIdMap.put(DeltaType.NODE_CREATE, DeltaParamIdentifier.CreateNodeObjectDefinitionId);
		typeIdMap.put(DeltaType.EDGE_UPDATE, DeltaParamIdentifier.UpdateEdgeObjectDefinitionId);
		typeIdMap.put(DeltaType.NODE_UPDATE, DeltaParamIdentifier.UpdateNodeObjectDefinitionId);
		typeIdMap.put(DeltaType.OBJECT_DELETE, DeltaParamIdentifier.DeleteObjectObjectDefinitionId);
		typeIdMap.put(DeltaType.NODE_MERGE, DeltaParamIdentifier.MergeNodeObjectDefinitionId);

		boolean accessible = delta.getCreator().getId().equals(userId);
		if (!accessible){
			switch (delta.getDeltaType()){
				//TODO update node metaphor and coords should be routed to users,
				// which have access to tihs objects
				case NODE_UPDATE_METAPHOR:
					accessible = true;
					break;
				case NODE_UPDATE_COORDS:
					accessible = true;
					break;
				case SETTING_UPDATE:
					// ignore, owner is checked already
					break;
				case EDGE_CREATE:
				case NODE_CREATE:
				case EDGE_UPDATE:
				case NODE_UPDATE:
				case OBJECT_DELETE:
				case NODE_MERGE:

					final DeltaParam deltaParam = delta.getDeltaParameters().get(typeIdMap.get(delta.getDeltaType()));
					if (deltaParam != null){
						final Integer groupId = groups.get(userId);
						if (groupId != null){
							final List<Integer> allowedTypes = readAcl.get(groupId);
							if (allowedTypes != null){
								accessible = allowedTypes.contains(deltaParam.getValueAsInteger());
							}
						}
					} else
						log.error("Error - delta param ObjectDefinitionId not found for coresponding deltaType "
						        + delta.getDeltaType());
					break;
				case FAVORITE_CREATE:
				case FAVORITE_COPY:
				case FAVORITE_UPDATE:
				case FAVORITE_DELETE:
				case FAVORITE_FOLDER_CREATE:
				case FAVORITE_FOLDER_UPDATE:
				case FAVORITE_FOLDER_DELETE:
				case GEO_ANALYTICS_SAVE:
				case GEO_ANALYTICS_DELETE:
					accessible = groups.containsKey(userId);
					break;
				default:
					break;
			}
		}
		return accessible;
	}

	//TODO rewrite this shit completely
	//e.g. favorite should be routed only to same and user's group members it is is group
	//not to all which has offline
	private void process(){
		final List<DeltaHeader> deltas = deltaHeaderDao.getUnprocessedDeltas(-1);
		if (deltas != null && !deltas.isEmpty()){
			final Map<Integer, List<Integer>> readAcl = objectUserGroupDao.getReadACL();
			final Map<Integer, Integer> groups = loadUserMap();
			final Set<Integer> users = groups.keySet();

			for (final DeltaHeader delta : deltas){
				log.debug("Processing: " + delta);
				for (final Integer userId : users){
					boolean accessible = objectAccessible(delta, userId, groups, readAcl) && shouldBeRouted(delta, userId);
					log.debug("Processing for user " + userId + " -> " + accessible);
					if (accessible){
						log.debug("Saving user delta");
						saveUserDelta(delta, userId);
					}
				}
				delta.setSyncStatus(SyncStatus.Processed);
				deltaHeaderDao.markProcessed(delta);
			}
		}
	}

	private void saveUserDelta(final DeltaHeader delta, final Integer userId){
		deltaHeaderUserDao.create(delta, userId);
	}

	boolean shouldBeRouted(final DeltaHeader delta, final Integer userId){
		boolean ret = false;

		if (delta.isSync()){
			// delta from Offline client
			if (!delta.getCreator().getId().equals(userId)){
				ret = true;
			} else{
				// Own change: process only Edit actions
				switch (delta.getDeltaType()){
					case EDGE_UPDATE:
					case NODE_UPDATE:
					case NODE_MERGE:
					case FAVORITE_UPDATE:
					case FAVORITE_FOLDER_UPDATE:
					case SETTING_UPDATE:
						ret = true;
						break;
				}
			}
		} else{
			ret = true;
		}

		return ret;
	}

}
