/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.Map;
import com.ni3.ag.adminconsole.domain.MapJob;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.remoting.ThreadLocalStorage;
import com.ni3.ag.adminconsole.server.dao.MapJobDAO;
import com.ni3.ag.adminconsole.server.dao.OfflineJobDAO;
import com.ni3.ag.adminconsole.server.dao.SettingDAO;
import com.ni3.ag.adminconsole.server.datasource.ACRoutingDataSource;
import com.ni3.ag.adminconsole.server.datasource.InstanceDescriptor;
import com.ni3.ag.adminconsole.server.mapextraction.MapExtractor;
import com.ni3.ag.adminconsole.server.service.WSCommunicator;
import com.ni3.ag.adminconsole.shared.jobs.MapJobStatus;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.MapJobModel;
import com.ni3.ag.adminconsole.shared.service.def.MapJobService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import com.ni3.ag.adminconsole.validation.ErrorContainerImpl;

public class MapJobServiceImpl extends WSCommunicator implements MapJobService{
	private static final Logger log = Logger.getLogger(MapJobServiceImpl.class);

	private MapJobDAO mapJobDAO;
	private OfflineJobDAO offlineJobDAO;
	private SettingDAO settingDAO;
	private DataSource dataSource;
	private MapExtractor mapExtractor;
	private ACValidationRule mapJobDeleteRule;

	public void setMapExtractor(MapExtractor mapExtractor){
		this.mapExtractor = mapExtractor;
	}

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}

	public void setMapJobDAO(MapJobDAO mapJobDAO){
		this.mapJobDAO = mapJobDAO;
	}

	public void setOfflineJobDAO(OfflineJobDAO offlineJobDAO){
		this.offlineJobDAO = offlineJobDAO;
	}

	@Override
	public List<MapJob> getMapJobs(){
		return mapJobDAO.getAllJobs();
	}

	@Override
	public List<User> getUsers(){
		return offlineJobDAO.getThickClientUsers();
	}

	public void setSettingDAO(SettingDAO settingDAO){
		this.settingDAO = settingDAO;
	}

	public void setMapJobDeleteRule(ACValidationRule mapJobDeleteRule){
		this.mapJobDeleteRule = mapJobDeleteRule;
	}

	@Override
	public void applyMapJobs(List<MapJob> jobsToUpdate, List<MapJob> jobsToDelete) throws ACException{
		if (jobsToUpdate != null && !jobsToUpdate.isEmpty()){
			for (MapJob job : jobsToUpdate){
				if (job.getId() != null){
					MapJob current = mapJobDAO.getMapJob(job.getId());
					if (current.getId() != null && !MapJobStatus.Scheduled.getValue().equals(current.getStatus())){
						if (MapJobStatus.Scheduled.getValue().equals(job.getStatus()))
							throw new ACException(TextID.MsgPleaseRefresh);
						else
							continue;
					}
				}

				mapJobDAO.merge(job);
			}
		}

		if (jobsToDelete != null && !jobsToDelete.isEmpty()){
			for (MapJob job : jobsToDelete){
				job = mapJobDAO.getMapJob(job.getId()); // reload job, cause it could be already changed
				if (MapJobStatus.Compressing.getValue().equals(job.getStatus())
				        || MapJobStatus.CopyingToMapPath.getValue().equals(job.getStatus())
				        || MapJobStatus.CopyingToModulesPath.getValue().equals(job.getStatus())
				        || MapJobStatus.ProcessingMaps.getValue().equals(job.getStatus())){
					continue;
				}
				mapJobDAO.delete(job);
			}
		}
	}

	@Override
	public Map getMap(Integer mapId){
		Map map = mapJobDAO.getMap(mapId);
		if (map == null)
			return null;
		return mapJobDAO.getMap(mapId);
	}

	@Override
	public String getRasterServerUrl(){
		String dbid = ThreadLocalStorage.getInstance().getCurrentDatabaseInstanceId();
		ACRoutingDataSource acds = (ACRoutingDataSource) dataSource;
		java.util.Map<String, InstanceDescriptor> instanceMap = acds.getDatasourceDescriptors();
		InstanceDescriptor id = instanceMap.get(dbid);
		return id.getRasterServer();
	}

	@Override
	public String getSetting(Integer userId, String section, String prop){
		return settingDAO.getSetting(userId, section, prop);
	}

	@Override
	public void processJobs() throws ACException{
		List<MapJob> jobs = mapJobDAO.getScheduledMapJobs();
		if (jobs.isEmpty()){
			return;
		}
		log.debug("Map extraction jobs found: " + jobs.size());
		mapExtractor.init();
		for (MapJob job : jobs){
			mapExtractor.extractMap(job);
		}
		mapExtractor.dispose();
	}

	@Override
	public void processJob(MapJob job){
		mapExtractor.init();
		log.debug("starting extraction");
		mapExtractor.extractMap(job);
		log.debug("extraction completed");
		mapExtractor.dispose();
	}

	@Override
	public ErrorContainer validateDeleteJob(MapJob job){
		job = mapJobDAO.getMapJob(job.getId());
		MapJobModel model = new MapJobModel();
		model.setCurrentJob(job);

		ErrorContainer ec = new ErrorContainerImpl();
		List<ErrorEntry> errors = ec.getErrors();
		mapJobDeleteRule.performCheck(model);
		errors.addAll(mapJobDeleteRule.getErrorEntries());

		return ec;
	}
}
