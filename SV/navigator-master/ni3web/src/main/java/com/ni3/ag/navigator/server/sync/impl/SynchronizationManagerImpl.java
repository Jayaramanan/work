/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.sync.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.DaoException;
import com.ni3.ag.navigator.server.dao.DeltaHeaderDAO;
import com.ni3.ag.navigator.server.dao.DeltaHeaderUserDAO;
import com.ni3.ag.navigator.server.dao.GroupDAO;
import com.ni3.ag.navigator.server.dao.IconDAO;
import com.ni3.ag.navigator.server.dao.UncommittedDeltasDAO;
import com.ni3.ag.navigator.server.dao.UserDAO;
import com.ni3.ag.navigator.server.dao.UserGroupDAO;
import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.server.domain.DeltaParam;
import com.ni3.ag.navigator.server.domain.DeltaParamIdentifier;
import com.ni3.ag.navigator.server.domain.Group;
import com.ni3.ag.navigator.server.domain.Icon;
import com.ni3.ag.navigator.server.domain.SyncStatus;
import com.ni3.ag.navigator.server.gateway.SyncGateway;
import com.ni3.ag.navigator.server.services.DeltaHeaderService;
import com.ni3.ag.navigator.server.services.DeltaProcessor;
import com.ni3.ag.navigator.server.sync.SynchronizationManager;
import com.ni3.ag.navigator.server.util.ServerSettings;
import com.ni3.ag.navigator.shared.domain.DeltaType;
import com.ni3.ag.navigator.shared.domain.User;
import com.ni3.ag.navigator.shared.gateway.LoginGateway;
import com.ni3.ag.navigator.shared.login.LoginResult;
import com.ni3.ag.navigator.shared.proto.NResponse;
import com.ni3.ag.navigator.shared.proto.NResponse.Envelope;
import com.ni3.ag.navigator.shared.proto.NResponse.SyncResult;
import com.ni3.ag.navigator.shared.proto.NResponse.UserGroup;
import com.ni3.ag.navigator.shared.proto.NResponse.SyncResult.Status;

public class SynchronizationManagerImpl extends JdbcDaoSupport implements SynchronizationManager{
	private static final Logger log = Logger.getLogger(SynchronizationManagerImpl.class);
	private static final int SIZE_10_Kb = 10 * 1024;
	private static final String USER_ID = "UserID";
	private static final String MASTER_SERVER_URL = "com.ni3.ag.navigator.offline.masterServerURL";
	private static final String USER_NAME = "USER_NAME";
	private static final String PASSWORD = "PASSWORD";

	private static final int DELTA_CHUNK_COUNT = 100;

	private static final String JSESSIONID_PREFIX = "JSESSIONID=";

	private String sessionID = null;

	private IconDAO iconDAO;

	public void setIconDAO(IconDAO iconDAO){
		this.iconDAO = iconDAO;
	}

	private Map<String, String> collectParams(Integer userId){
		UserDAO userDAO = NSpringFactory.getInstance().getUserDao();
		try{
			log.debug("collecting all parameters for synchronization");
			Map<String, String> params = new HashMap<String, String>();
			params.put(USER_ID, userId.toString());
			log.debug(USER_ID + "=" + userId);

			Properties prop = new Properties();
			prop.load(ServerSettings.class.getResourceAsStream("/Ni3Web.properties"));
			String s = (String) prop.get(MASTER_SERVER_URL);
			if (s == null){
				log.error("Property `" + MASTER_SERVER_URL + "` not found");
				return null;
			}
			if (s.endsWith("/"))
				s = s.substring(0, s.length() - 1);
			if (s.endsWith("/servlet"))
				s = s.substring(0, s.lastIndexOf("/servlet"));

			params.put(MASTER_SERVER_URL, s);
			log.debug(MASTER_SERVER_URL + "=" + s);

			User u = userDAO.get(userId);

			params.put(USER_NAME, u.getUserName());
			log.debug(USER_NAME + "=" + u.getUserName());
			params.put(PASSWORD, u.getPassword());
			log.debug(PASSWORD + "=" + u.getPassword());

			return params;
		} catch (IOException e){
			log.error("Failed to collect properties and parameters for synchronization", e);
		}
		return null;
	}

	@Override
	public boolean checkTableLock(){
		log.info("checking, that table is not locked");
		Connection cc = null;
		Statement st = null;
		String sql = null;
		boolean success = false;
		try{
			cc = getJdbcTemplate().getDataSource().getConnection();
			cc.setAutoCommit(false);
			st = cc.createStatement();
			sql = "lock table cis_objects IN EXCLUSIVE MODE NOWAIT";
			if (log.isDebugEnabled()){
				log.debug("executing: " + sql);
			}
			st.executeUpdate(sql);
			log.debug("successfully locked");
			success = true;
		} catch (SQLException e){
			log.error("Cannot lock table cis_objects, it's already locked. Sql = " + sql);
		} finally{
			if (st != null){
				try{
					st.close();
				} catch (SQLException e){
					log.error("error closing statement " + st, e);
				}
			}
			if (cc != null)
				try{
					cc.close();
				} catch (SQLException e){
					log.error("error closing connection " + st, e);
				}
		}

		return success;
	}

	private boolean makeLogin(Map<String, String> paramsMap){
		if (log.isDebugEnabled()){
			log.debug("try to login to master server");
		}
		LoginGateway loginGateway = NSpringFactory.getInstance().getLoginGateway();
		sessionID = null;
		LoginResult loginResult = loginGateway.loginWithUserNamePassword(paramsMap.get(MASTER_SERVER_URL), paramsMap
				.get(USER_NAME), paramsMap.get(PASSWORD), true);
		if (loginResult == null || !loginResult.isOk())
			return false;

		sessionID = JSESSIONID_PREFIX + loginResult.getSessionId();

		log.debug("SESSION ID: " + sessionID);
		return sessionID != null;
	}

	@Override
	public boolean makeLogout(Integer userId){
		Map<String, String> paramsMap = collectParams(userId);
		LoginGateway loginGateway = NSpringFactory.getInstance().getLoginGateway();
		return loginGateway.logout(paramsMap.get(MASTER_SERVER_URL), sessionID);
	}

	@Override
	public Object[] pushAllChangedToMaster(Integer userId) throws IOException{
		log.info("Sending all changes to master server");
		log.debug("Resolving all parameters need for synchronization");
		Map<String, String> paramsMap = collectParams(userId);
		if (paramsMap == null){
			log.error("Master server URL == null");
			return null;
		}
		DeltaHeaderDAO deltaDAO = NSpringFactory.getInstance().getDeltaHeaderDAO();
		DeltaHeaderService service = NSpringFactory.getInstance().getDeltaHeaderService();

		List<DeltaHeader> deltas = service.getUnprocessedForMaster(DELTA_CHUNK_COUNT);
		log.debug("Deltas count: " + deltas.size());
		SyncGateway deltasGateway = NSpringFactory.getInstance().getSyncGateway();
		Envelope result = deltasGateway.sendDeltas(deltas, paramsMap.get(MASTER_SERVER_URL), sessionID);
		SyncResult sr = SyncResult.parseFrom(result.getPayload());
		List<NResponse.ProcessedDelta> processedDeltas = sr.getProcessDeltas().getProcessedList();
		int ok = 0, warn = 0, error = 0;
		deltas.clear();
		for (NResponse.ProcessedDelta pd : processedDeltas){
			if (SyncStatus.Processed.intValue() == pd.getStatus())
				ok++;
			else if (SyncStatus.ProcessedWithWarning.intValue() == pd.getStatus())
				warn++;
			else if (SyncStatus.Error.intValue() == pd.getStatus())
				error++;
			deltas.add(new DeltaHeader(pd.getId(), pd.getStatus()));
		}
		log.debug("Trying to mark deltas as processed");
		deltaDAO.markProcessed(deltas);
		return new Object[] { ok, warn, error };
	}

	public boolean checkTableLocksOnMasterServer(Map<String, String> params) throws IOException{
		SyncGateway checker = NSpringFactory.getInstance().getSyncGateway();
		return checker.checkConnectivity(params.get(MASTER_SERVER_URL), sessionID);
	}

	@Override
	public Status checkConnectionToMasterServer(int userId) throws IOException{
		Map<String, String> paramsMap = collectParams(userId);
		if (!makeLogin(paramsMap)){
			log.error("Failed login to master server");
			return SyncResult.Status.CONNECTION_ERROR;
		}
		if (!checkTableLocksOnMasterServer(paramsMap)){
			log.error("Master database is locked");
			return SyncResult.Status.LOCK_ERROR;
		}
		return SyncResult.Status.OK;
	}

	@Override
	public Integer getDeltaCountToPush(){
		DeltaHeaderDAO deltaDAO = NSpringFactory.getInstance().getDeltaHeaderDAO();
		return deltaDAO.getUnprocessedCount();
	}

	@Override
	public void processOfflineDeltas(List<DeltaHeader> deltas){
		DeltaProcessor processor = NSpringFactory.getInstance().getDeltaProcessor();
		log.debug("Calling delta processor");
		processor.processDeltas(deltas, false);
	}

	@Override
	public Integer getDeltaCountToPull(int userId) throws IOException{
		Map<String, String> params = collectParams(userId);
		SyncGateway checker = NSpringFactory.getInstance().getSyncGateway();
		return checker.getMasterDeltaCount(params.get(MASTER_SERVER_URL), sessionID);
	}

	@Override
	public Long getDeltaCountForUser(Integer userId){
		DeltaHeaderUserDAO dao = NSpringFactory.getInstance().getDeltaHeaderUserDAO();
		return dao.getUnprocessedCountForUser(userId);
	}

	@Override
	public Object[] processMasterDeltas(int userId) throws IOException{
		SyncGateway syncGateway = NSpringFactory.getInstance().getSyncGateway();

		Map<String, String> envParams = collectParams(userId);
		SyncResult sr = syncGateway.getDeltasFromMaster(envParams.get(MASTER_SERVER_URL), sessionID);
		if (sr == null){
			log.error("Error getting deltas from master");
			return new Object[] { 0, 0, 0, false };
		}
		List<NResponse.Delta> rDeltas = sr.getDeltas().getDeltasList();
		log.debug("Received detlas: " + rDeltas.size());
		List<DeltaHeader> deltas = new ArrayList<DeltaHeader>();
		for (NResponse.Delta rd : rDeltas){
			Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
			for (NResponse.DeltaParam rdp : rd.getDeltaParamsList()){
				DeltaParamIdentifier identifier = DeltaParamIdentifier.getById(rdp.getName());
				if (identifier == null)
					identifier = new DeltaParamIdentifier(rdp.getName());
				params.put(identifier, new DeltaParam(identifier, rdp.getValue()));
			}

			DeltaHeader dh = new DeltaHeader(DeltaType.getById(rd.getDeltaType()), new User(rd.getCreatorId()), params);
			dh.setId(rd.getId());
			dh.setTimestamp(new Date(rd.getTimestamp()));
			dh.setSyncStatus(SyncStatus.fromInt(rd.getSyncStatus()));
			dh.setSync(rd.getIsSync());
			deltas.add(dh);
		}
		log.debug("Extracted delta count: " + deltas.size());
		DeltaProcessor processor = NSpringFactory.getInstance().getDeltaProcessor();
		processor.processDeltas(deltas, true);
		log.debug("Processed delta count: " + deltas.size());
		syncGateway.commitProcessedDeltasToMaster(deltas, envParams.get(MASTER_SERVER_URL), sessionID);
		NSpringFactory.getInstance().getUncommittedDeltasDAO().clearUncommitted();

		int ok = 0, warn = 0, error = 0;

		for (DeltaHeader dh : deltas)
			if (SyncStatus.Processed.equals(dh.getSyncStatus()))
				ok++;
			else if (SyncStatus.ProcessedWithWarning.equals(dh.getSyncStatus()))
				warn++;
			else if (SyncStatus.Error.equals(dh.getSyncStatus()))
				error++;
		boolean hasMore = sr.getHasMoreToProcess();

		log.info("OK: " + ok);
		log.info("WARN: " + warn);
		log.info("ERROR: " + error);
		log.info("HashMore: " + hasMore);

		return new Object[] { ok, warn, error, hasMore };
	}

	@Override
	public Object[] getDeltasToPull(Integer userId){
		DeltaHeaderUserDAO deltaHeaderOutDAO = NSpringFactory.getInstance().getDeltaHeaderUserDAO();
		int count = deltaHeaderOutDAO.getUnprocessedCountForUser(userId).intValue();
		DeltaHeaderService headerService = NSpringFactory.getInstance().getDeltaHeaderService();
		log.debug("Unprocessed count for user: " + userId + " = " + count);
		List<DeltaHeader> deltas = headerService.getUnprocessedForUser(userId, 100);
		log.debug("Extracted deltas for user: " + deltas.size());
		return new Object[] { deltas, deltas.size() < count };
	}

	@Override
	public void commitDeltas(List<DeltaHeader> deltas){
		DeltaHeaderUserDAO dao = NSpringFactory.getInstance().getDeltaHeaderUserDAO();
		dao.markUserDeltasAsProcessed(deltas);
	}

	@Override
	public boolean syncUsers(int userId) throws IOException{
		UserDAO userDAO = NSpringFactory.getInstance().getUserDao();
		GroupDAO groupDAO = NSpringFactory.getInstance().getGroupDao();
		UserGroupDAO userGroupDAO = NSpringFactory.getInstance().getUserGroupDao();
		SyncGateway syncGateway = NSpringFactory.getInstance().getSyncGateway();

		Map<String, String> envParams = collectParams(userId);
		SyncResult sr = syncGateway.getUsersAndGroups(envParams.get(MASTER_SERVER_URL), sessionID);
		if (sr == null)
			return false;
		List<Integer> serverUserIds = sr.getUserIdsList();
		List<Integer> serverGroupIds = sr.getGroupIdsList();
		Map<Integer, Integer> serverUserGroupMapping = getResponseUserGroupMapping(sr);

		for (int id : serverGroupIds){
			if (groupDAO.get(id) == null){
				Group g = new Group(id);
				log.debug("saving new group: " + id);
				groupDAO.save(g);
			}
		}

		for (int id : serverUserIds){
			if (userDAO.get(id) == null){
				User u = new User(id);
				u.setFirstName("fake");
				u.setLastName("fake");
				u.setUserName("fake");
				u.setPassword("000");
				u.setSID("" + id);
				u.seteMail("invalid");
				u.setActive(false);
				u.setHasOfflineClient(false);
				userDAO.save(u);

				int grId = serverUserGroupMapping.get(id);
				userGroupDAO.save(id, grId);

				log.debug("saving new user and mapping");
			}
		}

		return true;
	}

	private Map<Integer, Integer> getResponseUserGroupMapping(SyncResult sr){
		List<UserGroup> userGroups = sr.getUserGroupsList();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (UserGroup ug : userGroups)
			map.put(ug.getUserId(), ug.getGroupId());
		return map;
	}

	@Override
	public Object[] getUsersAndGroups(){
		UserDAO userDAO = NSpringFactory.getInstance().getUserDao();
		GroupDAO groupDAO = NSpringFactory.getInstance().getGroupDao();
		UserGroupDAO userGroupDAO = NSpringFactory.getInstance().getUserGroupDao();

		List<User> users = userDAO.getUsers();
		List<Group> groups = groupDAO.getGroups();
		Map<Integer, Integer> userGroups = userGroupDAO.getUserGroups();
		log.debug("Extracted user count: " + users.size());
		log.debug("Extracted group count: " + groups.size());
		log.debug("Extracted userGorup count: " + userGroups.size());
		return new Object[] { users, groups, userGroups };
	}

	@Override
	public int syncImages(int userId, String path, String paramPath) throws IOException{
		log.debug("synchronizing images");
		Map<String, String> paramsMap = collectParams(userId);
		paramsMap.put("path", paramPath);
		paramsMap.put("local_path", path);
		log.debug("metaphors path: " + path);
		if (!validatePath(path))
			return -1;
		log.debug("path validated");
		if (!makeLogin(paramsMap)){
			log.error("Error connecting to master server and login");
			return -1;
		}

		List<Icon> icons = getMasterIcons(paramsMap);
		log.debug("got master icons: " + icons);
		if (icons == null)
			return -1;
		List<Icon> localIcons = getMyIcons();
		log.debug("got local icons: " + localIcons);
		if (localIcons == null)
			return -1;
		Set<Integer> invalidIds = new HashSet<Integer>();
		validateMyIcons(path, localIcons, invalidIds);
		log.debug("checked existence of images locally");
		log.debug("starting download of missing images from master");
		int err = downloadMissingIcons(localIcons, invalidIds, icons, paramsMap);
		makeLogout(userId);
		return err;
	}

	private int downloadMissingIcons(List<Icon> localIcons, Set<Integer> invalidIds, List<Icon> icons,
			Map<String, String> paramsMap) throws IOException{
		log.debug("making local id set");
		Set<Integer> localIds = new HashSet<Integer>();
		for (Icon i : localIcons)
			localIds.add(i.getId());
		int errorCount = 0;
		for (Icon i : icons){
			log.debug("Checking icon (" + i.getId() + ") " + i.getIconName());
			if (localIds.contains(i.getId()))
				continue;
			log.debug("missing - lets download it");
			if (!downloadIcon(i, paramsMap)){
				errorCount++;
				log.error("Error downloading icon " + i.getIconName() + " | " + i.getId());
				continue;
			}
			if (!invalidIds.contains(i.getId())){
				log.debug("icon was missing in both - table and metaphor directory");
				if (!iconDAO.saveIcon(i)){
					errorCount++;
					log.error("Error saving icon data in local database");
					continue;
				}
			}
			localIcons.add(i);
		}
		return errorCount;
	}

	@SuppressWarnings("deprecation")
	private boolean downloadIcon(Icon i, Map<String, String> paramsMap){
		FileOutputStream fos = null;
		InputStream is = null;
		try{
			URL url = new URL(paramsMap.get(MASTER_SERVER_URL));
			String unusedPath = url.getPath();
			String str = url.toString();
			str = str.substring(0, str.indexOf(unusedPath));
			str += paramsMap.get("path");
			if (log.isDebugEnabled()){
				log.debug("URL: " + str);
			}
			if (!str.endsWith("/"))
				str += "/";
			str += URLEncoder.encode(i.getIconName());
			if (log.isDebugEnabled()){
				log.debug("Full URL: " + str);
			}
			URL urlex = new URL(str);
			is = urlex.openStream();
			if (is == null)
				throw new IOException("Response InputStream == null");
			String destFile = paramsMap.get("local_path");
			if (!destFile.endsWith(File.separator))
				destFile += File.separator;
			destFile += i.getIconName();
			if (log.isDebugEnabled()){
				log.debug("Destination file: " + destFile);
			}
			File f = new File(destFile);
			if (!f.exists()){
				fos = new FileOutputStream(destFile);
				byte[] buf = new byte[SIZE_10_Kb];
				while (true){
					int count = is.read(buf, 0, buf.length);
					if (count <= 0)
						break;
					fos.write(buf, 0, count);
				}
				fos.flush();
				if (log.isDebugEnabled()){
					log.debug("downloaded file from" + str + ", saved to " + destFile);
				}
			}
		} catch (IOException e){
			log.error("Error downloading and saving image", e);
			return false;
		} finally{
			if (is != null)
				try{
					is.close();
				} catch (IOException e){
					log.error("Error closing response infput stream");
				}
			if (fos != null)
				try{
					fos.close();
				} catch (IOException e){
					log.error("Error closing file output stream");
				}
		}
		return true;
	}

	private void validateMyIcons(String path, List<Icon> localIcons, Set<Integer> invalidIds){
		Icon[] icons = localIcons.toArray(new Icon[localIcons.size()]);
		for (Icon icon : icons){
			if (log.isDebugEnabled()){
				log.debug("Check if " + icon.getIconName() + " exists");
			}

			File f = new File(path + icon.getIconName());
			if (!f.exists()){
				log.warn("Icon " + icon.getIconName() + " is in cht_icons but missing in path");
				if (log.isDebugEnabled()){
					log.debug("storing id as invalid in invalidIds set " + icon.getId());
				}
				invalidIds.add(icon.getId());
				localIcons.remove(icon);
			}
		}
	}

	private List<Icon> getMasterIcons(Map<String, String> paramsMap) throws IOException{
		SyncGateway syncGateway = NSpringFactory.getInstance().getSyncGateway();
		SyncResult sr = syncGateway.getMasterIcons(paramsMap.get(MASTER_SERVER_URL), sessionID);
		if (sr == null)
			return null;
		List<Icon> icons = new ArrayList<Icon>();
		for (NResponse.Icon ri : sr.getIconsList())
			icons.add(new Icon(ri.getId(), ri.getIconName()));
		return icons;
	}

	@Override
	public List<Icon> getMyIcons(){
		return iconDAO.getIcons();
	}

	private boolean validatePath(String path){
		if (path == null || path.isEmpty()){
			log.error("Invalid parameter `path` value: " + path);
			return false;
		}
		File f = new File(path);
		if (!f.exists()){
			log.error("path does not exists: " + path);
			return false;
		}
		return true;
	}

	@Override
	public boolean commitUncommittedDeltas(int userId){
		Map<String, String> params = collectParams(userId);
		SyncGateway syncGateway = NSpringFactory.getInstance().getSyncGateway();
		UncommittedDeltasDAO uncommittedDeltasDAO = NSpringFactory.getInstance().getUncommittedDeltasDAO();
		try{
			List<DeltaHeader> deltas = uncommittedDeltasDAO.getUncommittedDeltas();
			if (deltas.isEmpty())
				return true;
			syncGateway.commitProcessedDeltasToMaster(deltas, params.get(MASTER_SERVER_URL), sessionID);
			uncommittedDeltasDAO.clearUncommitted();
			return true;
		} catch (DaoException e){
			log.error("Error committing uncommitted deltas", e);
			return false;
		} catch (IOException e){
			log.error("Error committing uncommitted deltas", e);
			return false;
		}
	}

	@Override
	public void sendPrepareDatRequestToMaster(int userId) throws IOException{
		Map<String, String> params = collectParams(userId);
		SyncGateway syncGateway = NSpringFactory.getInstance().getSyncGateway();
		syncGateway.sendPrepareDataRequest(userId, params.get(MASTER_SERVER_URL), sessionID);
	}

	@Override
	public void prepareDataForUser(Integer id){
		DeltaHeaderService service = NSpringFactory.getInstance().getDeltaHeaderService();
		service.prepareDataForUser(id);
	}
}
