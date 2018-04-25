/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation;

import java.util.List;

import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;

/**
 * This interface should be used for any validation rule
 */
public interface ACValidationRule{

	/**
	 * Checks a model for errors
	 * 
	 * @param model
	 *            model to check
	 * @return true on success, false otherwise
	 */
	boolean performCheck(AbstractModel model);

	/**
	 * 
	 * @return list of error codes with parameters which need to be injected in the text string
	 */
	List<ErrorEntry> getErrorEntries();
}
