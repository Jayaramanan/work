/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserEdition;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.license.NavigatorModule;
import com.ni3.ag.adminconsole.server.dao.GroupDAO;
import com.ni3.ag.adminconsole.server.dao.UserDAO;
import com.ni3.ag.adminconsole.shared.service.def.LicenseService;
import com.ni3.ag.adminconsole.shared.service.def.NavigatorLicenseService;

public class NavigatorLicenseServiceImpl implements NavigatorLicenseService{
	private static final Logger log = Logger.getLogger(NavigatorLicenseServiceImpl.class);
	private GroupDAO groupDAO;
	private UserDAO userDAO;
	private LicenseService licenseService;

	public void setLicenseService(LicenseService licenseService){
		this.licenseService = licenseService;
	}

	public void setGroupDAO(GroupDAO groupDAO){
		this.groupDAO = groupDAO;
	}

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	@Override
	public List<Group> getGroups(){
		List<Group> groups = groupDAO.getGroups();
		for (Group group : groups){
			for (User user : group.getUsers()){
				Hibernate.initialize(user.getUserEditions());
			}
		}
		return groups;
	}

	@Override
	public List<LicenseData> getNavigatorLicenseData(){
		return licenseService.getLicenseDataByProductName(LicenseData.NAVIGATOR_PRODUCT);
	}

	@Override
	public Group reloadGroup(Integer id){
		Group group = groupDAO.getGroup(id);
		for (User user : group.getUsers()){
			Hibernate.initialize(user.getUserEditions());
			Hibernate.initialize(user.getGroups());
		}
		return group;
	}

	@Override
	public void updateGroups(List<Group> groups){
		List<User> unassignedUsers = userDAO.getUnassignedUsers();
		if (unassignedUsers != null && !unassignedUsers.isEmpty()){
			for (User user : unassignedUsers){
				user.getUserEditions().clear();
			}
			userDAO.saveOrUpdateAll(unassignedUsers);
		}
		groupDAO.saveOrUpdateAll(groups);
	}

	@Override
	public void updateUsers(List<User> users){
		userDAO.saveOrUpdateAll(users);
	}

	private Map<NavigatorModule, Integer> getNavigatorModuleCount(List<LicenseData> licenses){
		Map<NavigatorModule, Integer> map = new HashMap<NavigatorModule, Integer>();
		for (LicenseData ldata : licenses){
			for (NavigatorModule module : NavigatorModule.values()){
				Integer modCount = map.get(module);
				int count = modCount == null ? 0 : modCount;
				Integer licModuleCount = (Integer) ldata.get(module.getValue());
				int licCount = licModuleCount == null ? 0 : licModuleCount;
				map.put(module, licCount + count);
			}
		}
		return map;
	}

	public Map<NavigatorModule, Integer> checkExpiringLicenseModules(){
		List<LicenseData> expiredLicenses = licenseService.getExpiredLicenseData();
		List<LicenseData> expiringLicenses = licenseService.getExpiringLicenseData();
		List<LicenseData> operatedLicenses = getNavigatorLicenseData();
		List<LicenseData> nonExpiringLicenses = new ArrayList<LicenseData>();
		nonExpiringLicenses.addAll(operatedLicenses);
		nonExpiringLicenses.removeAll(expiringLicenses);
		nonExpiringLicenses.removeAll(expiredLicenses);
		List<Group> groups = getGroups();
		List<User> users = new ArrayList<User>();
		for (Group group : groups)
			users.addAll(group.getUsers());
		return checkExpiringLicenseModules(users, expiredLicenses, expiringLicenses, nonExpiringLicenses);
	}

	protected Map<NavigatorModule, Integer> checkExpiringLicenseModules(List<User> users, List<LicenseData> expiredLicenses,
	        List<LicenseData> expiringLicenses, List<LicenseData> nonExpiringLicenses){
		log.debug("starting expiring user edition check");

		Map<NavigatorModule, Integer> actualHighlightCountMap = new HashMap<NavigatorModule, Integer>();

		Map<NavigatorModule, Integer> expiredNavigatorModuleCount = getNavigatorModuleCount(expiredLicenses);
		Map<NavigatorModule, Integer> expiringNavigatorModuleCount = getNavigatorModuleCount(expiringLicenses);
		Map<String, Integer> realExpiringNavigatorModuleCount = getRealExpiringNavigatorModuleCount(users);
		Map<NavigatorModule, Integer> nonExpiringNavigatorModuleCount = getNavigatorModuleCount(nonExpiringLicenses);
		Map<String, Integer> usedModuleMap = getUsedModuleCount(users);

		for (NavigatorModule module : NavigatorModule.values()){
			log.debug("    checking module: " + module.getValue());
			Integer nonExpiringModuleCount = nonExpiringNavigatorModuleCount.get(module);
			Integer expectedHighlightCount = expiringNavigatorModuleCount.get(module);
			Integer dbHighlightCount = realExpiringNavigatorModuleCount.get(module.getValue());
			Integer usedModuleCount = usedModuleMap.get(module.getValue());
			Integer expiredModuleCount = expiredNavigatorModuleCount.get(module);

			if (nonExpiringModuleCount == null)
				nonExpiringModuleCount = 0;
			if (expectedHighlightCount == null)
				expectedHighlightCount = 0;
			if (dbHighlightCount == null)
				dbHighlightCount = 0;
			if (usedModuleCount == null)
				usedModuleCount = 0;
			if (expiredModuleCount == null)
				expiredModuleCount = 0;

			// without expired
			int totalModuleCount = expectedHighlightCount + nonExpiringModuleCount;
			int unassignedModuleCount = totalModuleCount - usedModuleCount;
			if (unassignedModuleCount < 0)
				unassignedModuleCount = 0;
			int actualHighlightCount = expectedHighlightCount - unassignedModuleCount;
			log.debug("    total user edition count without expired licenses: " + totalModuleCount);
			log.debug("    unassigned edition count without expired licenses: " + unassignedModuleCount);
			log.debug("    actual highligh count without expired licenses	: " + actualHighlightCount);

			actualHighlightCountMap.put(module, actualHighlightCount);

			// with expired
			int totalModuleCount2 = expectedHighlightCount + nonExpiringModuleCount + expiredModuleCount;
			int unassignedModuleCount2 = totalModuleCount2 - usedModuleCount;
			if (unassignedModuleCount < 0)
				unassignedModuleCount = 0;
			int actualHighlightCount2 = expectedHighlightCount - unassignedModuleCount2 + expiredModuleCount;
			log.debug("    total user edition count	: " + totalModuleCount2);
			log.debug("    unassigned edition count	: " + unassignedModuleCount2);
			log.debug("    actual highligh count	: " + actualHighlightCount2);

			if (dbHighlightCount < actualHighlightCount2){
				log.debug("    marking editions as expiring: " + (actualHighlightCount2 - dbHighlightCount));
				markEditionsExpiring(true, users, actualHighlightCount2 - dbHighlightCount, module.getValue());
			} else if (dbHighlightCount > actualHighlightCount2){
				log.debug("    unmarking editions as expiring: " + (dbHighlightCount - actualHighlightCount2));
				markEditionsExpiring(false, users, dbHighlightCount - actualHighlightCount2, module.getValue());
			}
			log.debug("    marking editions as expired (deleting): " + (actualHighlightCount2 - actualHighlightCount));
			markEditionsExpired(users, actualHighlightCount2 - actualHighlightCount, module.getValue());
		}
		for (User user : users){
			List<UserEdition> editions = user.getUserEditions();
			List<UserEdition> cloneEditions = new ArrayList<UserEdition>();
			cloneEditions.addAll(editions);
			for (UserEdition edition : cloneEditions){
				if (edition.isToDelete())
					editions.remove(edition);
			}
		}

		updateUsers(users);
		log.debug("finished expiring user edition check");

		return actualHighlightCountMap;
	}

	private void markEditionsExpired(List<User> users, int count, String module){
		int fixCount = 0;
		for (int i = 0; i < users.size() && fixCount < count; i++){
			User user = users.get(i);
			List<UserEdition> editions = user.getUserEditions();
			for (int j = 0; j < editions.size() && fixCount < count; j++){
				UserEdition edition = editions.get(j);
				if (edition.isExpiring() && edition.getEdition().equals(module)){
					edition.setToDelete(true);
					fixCount++;
				}
			}
		}
	}

	private void markEditionsExpiring(boolean mark, List<User> users, int newExpiryModules, String module){
		int fixCount = 0;
		for (int i = 0; i < users.size() && fixCount < newExpiryModules; i++){
			User user = users.get(i);
			List<UserEdition> editions = user.getUserEditions();
			for (int j = 0; j < editions.size() && fixCount < newExpiryModules; j++){
				UserEdition edition = editions.get(j);
				boolean condition = mark ? !edition.isExpiring() : edition.isExpiring();
				if (condition && edition.getEdition().equals(module)){
					edition.setIsExpiring(!edition.isExpiring());
					fixCount++;
				}
			}
		}
	}

	private Map<String, Integer> getRealExpiringNavigatorModuleCount(List<User> users){
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (NavigatorModule module : NavigatorModule.values())
			map.put(module.getValue(), 0);
		for (User user : users)
			for (UserEdition edition : user.getUserEditions()){
				if (edition.isExpiring()){
					Integer modCount = map.get(edition.getEdition());
					int count = modCount == null ? 0 : modCount;
					map.put(edition.getEdition(), count + 1);
				}
			}
		return map;
	}

	private Map<String, Integer> getUsedModuleCount(List<User> users){
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (NavigatorModule module : NavigatorModule.values())
			map.put(module.getValue(), 0);
		for (User user : users)
			for (UserEdition edition : user.getUserEditions()){
				Integer modCount = map.get(edition.getEdition());
				int count = modCount == null ? 0 : modCount;
				map.put(edition.getEdition(), count + 1);
			}
		return map;
	}

	@Override
	public Map<NavigatorModule, Integer> getAvailableEditionCount(){
		List<LicenseData> expiredLicenses = licenseService.getExpiredLicenseData();
		List<LicenseData> operatedLicenses = getNavigatorLicenseData();
		List<LicenseData> nonExpiringLicenses = new ArrayList<LicenseData>();
		nonExpiringLicenses.addAll(operatedLicenses);
		nonExpiringLicenses.removeAll(expiredLicenses);
		List<Group> groups = getGroups();
		List<User> users = new ArrayList<User>();
		for (Group group : groups)
			users.addAll(group.getUsers());

		Map<NavigatorModule, Integer> counts = getNavigatorModuleCount(nonExpiringLicenses);
		Map<String, Integer> usedCounts = getUsedModuleCount(users);
		Map<NavigatorModule, Integer> result = new HashMap<NavigatorModule, Integer>();
		for(NavigatorModule nm : NavigatorModule.values()){
			Integer count = counts.get(nm);
			if(count == null)
				count = 0;
			Integer usedCount = usedCounts.get(nm.getValue());
			if(usedCount == null)
				usedCount = 0;
			result.put(nm, count - usedCount);
		}
		return result;
	}

}
