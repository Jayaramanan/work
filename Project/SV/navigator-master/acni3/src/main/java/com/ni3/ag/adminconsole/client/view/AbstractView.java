/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view;

public interface AbstractView{

	void initializeComponents();

	void setVisible(boolean isVisible);

	/**
	 * Views should reset its change resetable components (if any) here
	 */
	void resetEditedFields();

	boolean isChanged();

	void restoreSelection();
}
