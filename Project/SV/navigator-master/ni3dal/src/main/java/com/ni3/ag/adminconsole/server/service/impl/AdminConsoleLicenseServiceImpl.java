/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserEdition;
import com.ni3.ag.adminconsole.license.ACModuleDescription;
import com.ni3.ag.adminconsole.license.AdminConsoleModule;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.server.dao.GroupDAO;
import com.ni3.ag.adminconsole.server.dao.UserDAO;
import com.ni3.ag.adminconsole.shared.service.def.AdminConsoleLicenseService;
import com.ni3.ag.adminconsole.shared.service.def.ChecksumEncoder;
import com.ni3.ag.adminconsole.shared.service.def.LicenseService;

public class AdminConsoleLicenseServiceImpl implements AdminConsoleLicenseService{
	private static final Logger log = Logger.getLogger(AdminConsoleLicenseServiceImpl.class);
	private UserDAO userDAO;
	private GroupDAO groupDAO;
	private LicenseService licenseService;
	private ChecksumEncoder checksumEncoder;

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	public void setGroupDAO(GroupDAO groupDAO){
		this.groupDAO = groupDAO;
	}

	public void setLicenseService(LicenseService licenseService){
		this.licenseService = licenseService;
	}

	public void setChecksumEncoder(ChecksumEncoder checksumEncoder){
		this.checksumEncoder = checksumEncoder;
	}

	@Override
	public List<User> getAdministrators(){
		Group adminGroup = groupDAO.getGroupByName(Group.ADMINISTRATORS_GROUP_NAME);
		List<User> users = adminGroup.getUsers();
		for (User user : users){
			Hibernate.initialize(user.getUserEditions());
		}
		log.debug("Found administrator count: " + users.size());
		return users;
	}

	@Override
	public void updateUsers(List<User> users){
		userDAO.saveOrUpdateAll(users);
	}

	@Override
	public List<ACModuleDescription> getModuleDescriptions(){
		List<ACModuleDescription> mDescriptions = new ArrayList<ACModuleDescription>();
		for (AdminConsoleModule acModule : AdminConsoleModule.values()){
			mDescriptions.add(new ACModuleDescription(acModule));
		}

		List<LicenseData> licenseDataList = licenseService.getLicenseDataByProductName(LicenseData.ACNi3WEB_PRODUCT);
		for (ACModuleDescription mDescription : mDescriptions){
			int maxUserCount = licenseService.getMaxUserCount(licenseDataList, mDescription.getModule().getValue());
			int maxNonExpiringUserCount = licenseService.getMaxNonExpiringUserCount(licenseDataList, mDescription
			        .getModule().getValue());
			mDescription.setUserCount(maxUserCount);
			mDescription.setMaxNonExpiringUserCount(maxNonExpiringUserCount);
		}

		return mDescriptions;
	}

	@Override
	public void checkLicenseModules(){
		List<ACModuleDescription> moduleDescriptions = getModuleDescriptions();
		List<User> users = getAdministrators();
		boolean changed = false;

		changed = checkModuleChecksum(users);

		for (ACModuleDescription md : moduleDescriptions){
			changed |= checkLicenseModules(users, md);
		}

		if (changed){
			userDAO.saveOrUpdateAll(users);
		}
	}

	private boolean checkModuleChecksum(List<User> users){
		boolean changed = false;
		for (User user : users){
			List<UserEdition> ueToDelete = new ArrayList<UserEdition>();
			for (UserEdition ue : user.getUserEditions()){
				String checksum = ue.getChecksum();
				String checksumCorrect = checksumEncoder.encode(user.getId(), ue.getEdition());
				if (checksum == null || !checksum.equals(checksumCorrect)){
					log.info("Checksum is incorrect for access to module " + ue.getEdition());
					ueToDelete.add(ue);
				}
			}
			if (!ueToDelete.isEmpty()){
				log.info("Removing " + ueToDelete.size() + " accesses to modules for user" + user.getUserName());
				user.getUserEditions().removeAll(ueToDelete);

				changed = true;
			}
		}
		return changed;
	}

	boolean checkLicenseModules(List<User> users, ACModuleDescription md){
		int expiringCount = 0;
		boolean changed = false;
		String moduleValue = md.getModule().getValue();
		for (User user : users){
			for (UserEdition ue : user.getUserEditions()){
				if (ue.getEdition().equals(moduleValue)){
					md.setUsedUserCount(md.getUsedUserCount() + 1);
					if (ue.isExpiring())
						expiringCount++;
				}
			}
		}

		// checking "expiring" flags
		int expectedExpiringCount = Math.max(md.getUsedUserCount() - md.getMaxNonExpiringUserCount(), 0);
		int difference = expectedExpiringCount - expiringCount;
		if (difference != 0){
			log.debug("Expected expiring count = " + expectedExpiringCount + ", real expiring count = " + expiringCount);
			adjustExpiringModules(users, moduleValue, difference);
			changed = true;
		}

		// remove excessive modules
		difference = md.getUsedUserCount() - md.getUserCount();
		if (difference > 0){
			log.debug("Used user count = " + md.getUsedUserCount() + ", available user count = " + md.getUserCount());
			log.debug("Removing excessive expiring module accesses");
			difference = removeExcessiveModules(users, moduleValue, difference, true);
			if (difference > 0){
				log.debug("Not enough module accesses was marked as expiring. Removing non expiring module accesses");
				removeExcessiveModules(users, moduleValue, difference, false);
			}
			changed = true;
		}

		return changed;
	}

	void adjustExpiringModules(List<User> users, String moduleValue, int difference){
		for (User user : users){
			for (UserEdition ue : user.getUserEditions()){
				if (ue.getEdition().equals(moduleValue)){
					if (difference > 0 && !ue.isExpiring()){
						log.debug("Setting module as expiring. Module = " + moduleValue + ", user = " + user.getUserName());
						ue.setIsExpiring(true);
						difference--;
					} else if (difference < 0){
						log.debug("Setting module as non expiring. Module = " + moduleValue + ", user = "
						        + user.getUserName());
						ue.setIsExpiring(false);
						difference++;
					}
					break;
				}
			}
			if (difference == 0)
				break;
		}
	}

	int removeExcessiveModules(List<User> users, String module, int difference, boolean onlyExpiring){
		for (User user : users){
			for (UserEdition ue : user.getUserEditions()){
				if (ue.getEdition().equals(module)){
					if (!onlyExpiring || ue.isExpiring()){
						log.info("Removing excessive module access according to license limits. Module = " + module
						        + ", user = " + user.getUserName());
						user.getUserEditions().remove(ue);
						difference--;
					}
					break;
				}
			}
			if (difference == 0)
				break;
		}
		return difference;
	}
}
