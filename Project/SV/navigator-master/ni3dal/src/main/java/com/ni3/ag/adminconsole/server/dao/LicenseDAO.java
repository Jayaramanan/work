/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.License;

public interface LicenseDAO{
	List<License> getLicenses();

	List<License> getLicenseByProduct(String product);

	License merge(License l);

	void saveOrUpdateAll(List<License> licenses);

	void delete(License license);

	License getLicense(License license);

	License saveOrUpdate(License license);

	void deleteAll(List<License> licenses);
}
