/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import java.util.Map;

import com.ni3.ag.adminconsole.shared.language.TextID;

public interface ACVisibilityService{
	public Map<TextID, Boolean> getLicenseAccesses(Integer userId);
}
