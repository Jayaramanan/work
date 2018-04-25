package com.ni3.ag.adminconsole.server.validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ni3.ag.adminconsole.domain.*;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.server.dao.ObjectDefinitionDAO;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class AttributeInMetaphorUserRule implements ACValidationRule{
	private ObjectDefinitionDAO objectDefinitionDAO;
	private List<ErrorEntry> errors = new ArrayList<ErrorEntry>();

	public void setObjectDefinitionDAO(ObjectDefinitionDAO objectDefinitionDAO){
		this.objectDefinitionDAO = objectDefinitionDAO;
	}

	private List<ObjectAttribute> getDeletedAttributes(ObjectDefinition od, ObjectDefinition oldDefinition){
		List<ObjectAttribute> result = new ArrayList<ObjectAttribute>();
		if (oldDefinition.getObjectAttributes() == null || oldDefinition.getObjectAttributes() == null)
			return result;
		for (ObjectAttribute oa : oldDefinition.getObjectAttributes())
			if (!od.getObjectAttributes().contains(oa))
				result.add(oa);
		return result;
	}


	@Override
	public boolean performCheck(AbstractModel amodel){
		errors.clear();
		SchemaAdminModel model = (SchemaAdminModel) amodel;
		ObjectDefinition od = model.getCurrentObjectDefinition();
		if (od.getId() == null)//new object?
			return true;
		ObjectDefinition oldObject = objectDefinitionDAO.getObjectDefinition(od.getId());
		List<ObjectAttribute> deletedAttributes = getDeletedAttributes(od, oldObject);
		if (deletedAttributes.isEmpty()){
			objectDefinitionDAO.evict(oldObject);
			return true;
		}
		for (ObjectAttribute oa : deletedAttributes){
			if (!oa.isPredefined())
				continue;
			if (!oa.isInMetaphor())
				continue;
			checkPredefinedValuesInMetaphor(oa);
		}
		objectDefinitionDAO.evict(oldObject);
		return errors.isEmpty();
	}

	private void checkPredefinedValuesInMetaphor(ObjectAttribute oa){
		if (oa.getPredefinedAttributes() == null || oa.getPredefinedAttributes().isEmpty())
			return;
		for (PredefinedAttribute pa : oa.getPredefinedAttributes()){
			checkPredefinedUsedInMetaphor(pa);
		}
	}

	private void checkPredefinedUsedInMetaphor(PredefinedAttribute pa){
		Set<PredefinedAttribute> errorSet = new HashSet<PredefinedAttribute>();
		ObjectDefinition od = pa.getObjectAttribute().getObjectDefinition();
		for (Metaphor m : od.getMetaphors()){
			if (m.getMetaphorData() == null || m.getMetaphorData().isEmpty())
				continue;
			for (MetaphorData md : m.getMetaphorData())
				if (md.getData().equals(pa) && !errorSet.contains(pa)){
					errors.add(new ErrorEntry(TextID.MsgPredefinedValueUsedInMetaphor,
							new String[]{pa.getObjectAttribute().getLabel(), pa.getLabel(), m.getMetaphorSet()}));
					errorSet.add(pa);
				}
		}
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}
}
