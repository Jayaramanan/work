/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.navigator.client.gateway.SchemaGateway;
import com.ni3.ag.navigator.client.gateway.impl.HttpSchemaGatewayImpl;
import com.ni3.ag.navigator.client.util.PrimitiveIntMap;

public class Schema{
	public int ID;
	public ArrayList<Entity> definitions;
	public static PrimitiveIntMap PredefinedAttributesValue;
	public static PrimitiveIntMap PredefinedAttributesSort;
	public EdgeMetaphor edgeMetaphor;
	private Entity commonEntity;

	public Schema(final int groupID, final int SchemaID, final int LanguageID){
		ID = SchemaID;

		edgeMetaphor = new EdgeMetaphor(ID);

		definitions = new ArrayList<Entity>(10);
		PredefinedAttributesValue = new PrimitiveIntMap(500);
		PredefinedAttributesSort = new PrimitiveIntMap(500);

		loadSchema(groupID, LanguageID);
	}

	public static Value getValue(final int ID){
		return (Value) (PredefinedAttributesValue.get(ID));
	}

	public Entity getEntity(final int entityID){
		if (entityID == Entity.COMMON_ENTITY_ID){
			return getCommonEntity();
		}
		for (final Entity e : definitions){
			if (e.ID == entityID){
				return e;
			}
		}

		return null;
	}

	private Entity getCommonEntity(){
		if (commonEntity == null){
			commonEntity = new Entity(Entity.COMMON_ENTITY_ID);
		}
		return commonEntity;
	}

	private void loadSchema(final int groupID, final int LanguageID){
		SchemaGateway gateway = new HttpSchemaGatewayImpl();
		final com.ni3.ag.navigator.shared.proto.NResponse.Schema schema = gateway.getSchema(ID, groupID, LanguageID);
		for (com.ni3.ag.navigator.shared.proto.NResponse.Entity entity : schema.getEntitiesList()){
			final Entity definition = new Entity(entity, entity.getUrlOperationsList(), this);
			boolean ToAdd = true;
			for (final Entity e1 : definitions){
				if (e1.ID == definition.ID){
					if (definition.CanCreate){
						e1.CanCreate = true;
					}
					if (definition.CanUpdate){
						e1.CanUpdate = true;
					}
					if (definition.CanRead){
						e1.CanRead = true;
					}
					if (definition.CanDelete){
						e1.CanDelete = true;
					}

					ToAdd = false;
				}
			}

			if (ToAdd){
				definitions.add(definition);
			}
		}
	}

	public List<Entity> getReadableNodes(){
		List<Entity> result = new ArrayList<Entity>();
		for (Entity ent : definitions){
			if (ent.CanRead && ent.isNode())
				result.add(ent);
		}
		return result;
	}
}
