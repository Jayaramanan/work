/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view;

import java.util.List;

import com.ni3.ag.adminconsole.dto.ErrorEntry;

public interface ErrorRenderer{
	void renderErrors(List<ErrorEntry> errors);
}
