/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.ObjectConnectionModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class ConnectionUniqueValidationRule implements ACValidationRule{

	private List<ErrorEntry> errors;

	@Override
	public boolean performCheck(AbstractModel aModel){
		errors = new ArrayList<ErrorEntry>();
		ObjectConnectionModel model = (ObjectConnectionModel) aModel;
		List<ObjectConnection> connections = model.getCurrentObject().getObjectConnections();

		boolean contains = false;
		if (connections != null && connections.size() > 0)
			for (int i = 0; i < connections.size() && !contains; i++){
				ObjectConnection conn = connections.get(i);
				contains = containsDuplicates(conn, connections);
			}
		return !contains;
	}

	private boolean containsDuplicates(ObjectConnection connection, List<ObjectConnection> connections){
		boolean contains = false;
		for (int i = 0; i < connections.size(); i++){
			ObjectConnection connection1 = connections.get(i);
			if (connection != connection1 && connection.getConnectionType().equals(connection1.getConnectionType())
			        && connection.getFromObject().equals(connection1.getFromObject())
			        && connection.getToObject().equals(connection1.getToObject())){
				errors.add(new ErrorEntry(TextID.MsgDuplicateConnections, new String[] {
				        connection1.getConnectionType().getLabel(), connection1.getFromObject().getName(),
				        connection1.getToObject().getName() }));
				contains = true;
				break;
			}
		}
		return contains;
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
