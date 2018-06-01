/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.DeltaHeader;
import com.ni3.ag.adminconsole.domain.DeltaParam;
import com.ni3.ag.adminconsole.domain.DeltaParamIdentifier;
import com.ni3.ag.adminconsole.domain.DeltaType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.server.dao.DeltaHeaderDAO;
import com.ni3.ag.adminconsole.server.importers.UserDataTable;
import com.ni3.ag.adminconsole.server.service.DeltaService;

public class DeltaServiceImpl implements DeltaService{

	private DeltaHeaderDAO deltaHeaderDAO;

	public void setDeltaHeaderDAO(DeltaHeaderDAO deltaHeaderDAO){
		this.deltaHeaderDAO = deltaHeaderDAO;
	}

	@Override
	public DeltaHeader getDeltaHeader(DeltaType type, Integer userId, Integer objectId, ObjectDefinition entity,
	        UserDataTable data, int row){
		DeltaHeader header = new DeltaHeader(type, userId);
		header.setParams(new ArrayList<DeltaParam>());
		fillValuesParams(header, data, row);
		switch (type){
			case EDGE_CREATE:
				final String fromId = getValue(data, row, ObjectAttribute.FROM_ID_ATTRIBUTE_NAME);
				final String toId = getValue(data, row, ObjectAttribute.TO_ID_ATTRIBUTE_NAME);
				fillInsertEdgeParams(header, objectId, fromId, toId, entity);
				break;
			case EDGE_UPDATE:
				fillUpdateEdgeParams(header, objectId, entity);
				break;
			case NODE_CREATE:
				fillInsertNodeParams(header, objectId, entity);
				break;
			case NODE_UPDATE:
				fillUpdateNodeParams(header, objectId, entity);
				break;
			default:
				break;
		}
		return header;
	}

	private void fillValuesParams(DeltaHeader header, UserDataTable data, int row){
		final List<DeltaParam> params = header.getParams();
		for (int col = 0; col < data.getAttributes().size(); col++){
			ObjectAttribute attribute = data.getAttributes().get(col);
			String value = data.getValue(row, col) != null ? data.getValue(row, col).toString() : "";
			// remove starting and ending ' signs (previously added for strings).
			if (value.startsWith("'") && value.endsWith("'") && value.length() > 1){
				value = value.substring(1, value.length() - 1);
			}
			params.add(new DeltaParam(header, String.valueOf(attribute.getId()), value));
		}
	}

	public String getValue(UserDataTable data, int row, String attributeName){
		String value = null;
		for (int col = 0; col < data.getAttributes().size(); col++){
			ObjectAttribute attr = data.getAttributes().get(col);
			if (attr == null)
				continue;
			if (attributeName.equalsIgnoreCase(attr.getName())){
				value = (String) data.getValue(row, col);
				break;
			}
		}
		return value;
	}

	private void fillInsertNodeParams(DeltaHeader header, Integer newId, ObjectDefinition entity){
		List<DeltaParam> params = header.getParams();
		params.add(new DeltaParam(header, DeltaParamIdentifier.CreateNodeNewId, "" + newId));
		params.add(new DeltaParam(header, DeltaParamIdentifier.CreateNodeObjectDefinitionId, "" + entity.getId()));
		params.add(new DeltaParam(header, DeltaParamIdentifier.CreateNodeSchemaId, "" + entity.getSchema().getId()));
	}

	private void fillUpdateNodeParams(DeltaHeader header, Integer objectId, ObjectDefinition entity){
		List<DeltaParam> params = header.getParams();
		params.add(new DeltaParam(header, DeltaParamIdentifier.UpdateNodeObjectId, "" + objectId));
		params.add(new DeltaParam(header, DeltaParamIdentifier.UpdateNodeObjectDefinitionId, "" + entity.getId()));
		params.add(new DeltaParam(header, DeltaParamIdentifier.UpdateNodeSchemaId, "" + entity.getSchema().getId()));
	}

	private void fillInsertEdgeParams(DeltaHeader header, Integer newId, String fromId, String toId, ObjectDefinition entity){
		List<DeltaParam> params = header.getParams();
		params.add(new DeltaParam(header, DeltaParamIdentifier.CreateEdgeNewId, "" + newId));
		params.add(new DeltaParam(header, DeltaParamIdentifier.CreateEdgeFromId, fromId));
		params.add(new DeltaParam(header, DeltaParamIdentifier.CreateEdgeToId, toId));
		params.add(new DeltaParam(header, DeltaParamIdentifier.CreateEdgeFavoriteId, "0"));
		params.add(new DeltaParam(header, DeltaParamIdentifier.CreateEdgeObjectDefinitionId, "" + entity.getId()));
		params.add(new DeltaParam(header, DeltaParamIdentifier.CreateEdgeSchemaId, "" + entity.getSchema().getId()));
	}

	private void fillUpdateEdgeParams(DeltaHeader header, Integer objectId, ObjectDefinition entity){
		List<DeltaParam> params = header.getParams();
		params.add(new DeltaParam(header, DeltaParamIdentifier.UpdateEdgeObjectId, "" + objectId));
		params.add(new DeltaParam(header, DeltaParamIdentifier.UpdateEdgeObjectDefinitionId, "" + entity.getId()));
		params.add(new DeltaParam(header, DeltaParamIdentifier.UpdateEdgeSchemaId, "" + entity.getSchema().getId()));
		params.add(new DeltaParam(header, DeltaParamIdentifier.UpdateEdgeFavoriteId, "0"));
	}

	@Override
	public void saveAll(List<DeltaHeader> deltaHeaders){
		deltaHeaderDAO.saveOrUpdateAll(deltaHeaders);
	}

}
