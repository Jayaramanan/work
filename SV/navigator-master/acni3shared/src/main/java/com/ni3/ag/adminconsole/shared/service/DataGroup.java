/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service;

import com.ni3.ag.adminconsole.shared.language.TextID;

public enum DataGroup{
	Attributes(TextID.Attributes), Metaphors(TextID.Metaphors), Schema(TextID.Schemas), Users(TextID.Users);

	private TextID id;

	DataGroup(TextID id){
		this.id = id;
	}

	public TextID getId(){
		return id;
	}

}
