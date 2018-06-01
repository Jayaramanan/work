/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useradmin;

import java.util.List;

import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.EditingOption;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupPrefilter;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectGroup;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.SchemaGroup;

public class GroupPrivilegesUpdater{

	private List<Schema> schemas;

	public GroupPrivilegesUpdater(List<Schema> schemas){
		this.schemas = schemas;
	}

	public void setCanReadObject(ObjectDefinition od, Group group, boolean value, boolean force){
		ObjectGroup found = null;
		for (ObjectGroup oug : od.getObjectGroups()){
			if (oug.getGroup().equals(group)){
				found = oug;
				break;
			}
		}

		if (found == null){
			// create access record if not exist
			found = new ObjectGroup(od, group);
			od.getObjectGroups().add(found);
		}
		found.setCanRead(value);

		if (!value || force){
			found.setCanUpdate(value);
			found.setCanCreate(value);
			found.setCanDelete(value);
		} else
			return;

		for (ObjectAttribute attr : od.getObjectAttributes()){
			setCanReadAttribute(attr, group, value, force, found.isCanUpdate());
			// setCanReadLockedAttribute(attr, group, value, force, found.isCanUpdate());
		}
	}

	public void setCanReadAttribute(ObjectAttribute oa, Group group, boolean value, boolean force, boolean canUpdate){
		AttributeGroup found = null;
		for (AttributeGroup ag : oa.getAttributeGroups()){
			if (ag.getGroup().equals(group)){
				found = ag;
				break;
			}
		}

		if (found == null){
			// create access record if not exist
			found = new AttributeGroup(oa, group);
			oa.getAttributeGroups().add(found);
		}
		found.setCanRead(value);
		if (!value || force){
			if (!value){
				found.setEditingOption(EditingOption.NotVisible);
				found.setEditingOptionLocked(EditingOption.NotVisible);
			} else if (canUpdate && !ObjectAttribute.isSystemNotEditableAttribute(oa)){
				if (found.getEditingOption() == null
				        || found.getEditingOption().getValue() < EditingOption.Editable.getValue()){
					found.setEditingOption(EditingOption.Editable);
				}
				if (found.getEditingOptionLocked() == null
				        || found.getEditingOptionLocked().getValue() < EditingOption.Editable.getValue()){
					found.setEditingOptionLocked(EditingOption.Editable);
				}
			}
		}

		if(value)
			for (PredefinedAttribute pa : oa.getPredefinedAttributes()){
				setCanReadPredefined(pa, group, value);
			}
	}

	public void setCanReadPredefined(PredefinedAttribute node, Group group, boolean value){
		GroupPrefilter found = null;
		for (GroupPrefilter pag : node.getPredefAttributeGroups()){
			if (pag.getGroup().equals(group)){
				found = pag;
				break;
			}
		}

		if (value && found != null)
			node.getPredefAttributeGroups().remove(found);
		else if (!value && found == null)
			node.getPredefAttributeGroups().add(new GroupPrefilter(group, node));
	}

	public void setCanReadSchema(Schema schema, Group group, boolean value, boolean force){
		SchemaGroup found = null;
		for (SchemaGroup sg : schema.getSchemaGroups()){
			if (sg.getGroup().equals(group)){
				found = sg;
				break;
			}
		}

		if (found == null){
			// create access record if not exist
			found = new SchemaGroup(schema, group);
			schema.getSchemaGroups().add(found);
		}
		found.setCanRead(value);

		if (!value || force){
			for (ObjectDefinition object : schema.getObjectDefinitions()){
				setCanReadObject(object, group, value, force);
			}
		}
	}

	/**
	 * Converts selected tree node to model object
	 * 
	 * @param node
	 *            selected tree node
	 * @return found model object or given tree node if none found
	 */
	public Object convertSelectedObjectToModel(Object node){
		if (node instanceof Schema){
			int index = schemas.indexOf(node);
			node = schemas.get(index);
		} else if (node instanceof ObjectDefinition){
			for (Schema s : schemas){
				int index = s.getObjectDefinitions().indexOf(node);
				if (index > -1){
					node = s.getObjectDefinitions().get(index);
					break;
				}
			}
		} else if (node instanceof ObjectAttribute){
			for (Schema s : schemas){
				boolean found = false;
				for (ObjectDefinition od : s.getObjectDefinitions()){
					int index = od.getObjectAttributes().indexOf(node);
					if (index > -1){
						node = od.getObjectAttributes().get(index);
						found = true;
						break;
					}
				}
				if (found)
					break;
			}
		} else if (node instanceof PredefinedAttribute){
			for (Schema s : schemas){
				boolean found = false;
				for (ObjectDefinition od : s.getObjectDefinitions()){
					for (ObjectAttribute oa : od.getObjectAttributes()){
						int index = oa.getPredefinedAttributes().indexOf(node);
						if (index > -1){
							node = oa.getPredefinedAttributes().get(index);
							found = true;
							break;
						}
					}
					if (found)
						break;
				}
				if (found)
					break;
			}
		}
		return node;
	}

	public void setSchemas(List<Schema> list){
		this.schemas = list;
	}

	public void resetLockedToUnlockedState(Group group){
		for (Schema schema : schemas){
			for (ObjectDefinition od : schema.getObjectDefinitions()){
				for (ObjectAttribute attr : od.getObjectAttributes()){
					for (AttributeGroup ag : attr.getAttributeGroups()){
						if (ag.getGroup().equals(group)){
							ag.setEditingOptionLocked(ag.getEditingOption());
						}
					}
				}
			}
		}
	}
}
