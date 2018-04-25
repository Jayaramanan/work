/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Metaphor;
import com.ni3.ag.adminconsole.domain.MetaphorData;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.NodeMetaphorModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class NodeMetaphorUniqValidationRule implements ACValidationRule{

	private List<ErrorEntry> errors;

	NodeMetaphorUniqValidationRule(){
	}

	private ErrorEntry buildErrorString(Metaphor metaphor){
		StringBuffer sb = new StringBuffer();
		sb.append(metaphor.getMetaphorSet());
		for (MetaphorData md : metaphor.getMetaphorData()){
			if (md.getData() == null)
				continue;
			sb.append(", ");
			sb.append(md.getData().getLabel());
		}
		ErrorEntry error = new ErrorEntry(TextID.MsgDuplicateNodeMetaphors, new String[] { sb.toString() });

		return error;
	}

	private boolean equalObjects(Object o1, Object o2){
		if (o1 == null && o2 == null)
			return true;
		if (o1 != null && o2 != null && o1.equals(o2))
			return true;
		return false;
	}

	@Override
	public boolean performCheck(AbstractModel model){
		errors = new ArrayList<ErrorEntry>();

		if (model == null)
			return true;
		List<Metaphor> metaphors = ((NodeMetaphorModel) model).getCurrentObjectDefinition().getMetaphors();
		HashSet<Integer> testFalied = new HashSet<Integer>();

		boolean notAllFieldsChechked = false;

		for (int i = 0; i < metaphors.size(); i++){
			if (testFalied.contains(i))// already fail the test - do not check it twice
				continue;

			Metaphor nm1 = metaphors.get(i);
			if (!notAllFieldsChechked && (nm1.getIcon() == null || nm1.getPriority() == null)){
				errors.add(new ErrorEntry(TextID.MsgFillAllMandatoryFields));
				notAllFieldsChechked = true;
			}

			for (int j = 1; j < metaphors.size(); j++){
				if (i == j)
					continue;

				Metaphor nm2 = metaphors.get(j);
				boolean equal = isEqual(nm1, nm2);
				if (equal){
					errors.add(buildErrorString(nm1));
					testFalied.add(j);// test failed for j object - add it to set - do not test it secondly
				}
			}
		}
		return errors.isEmpty();
	}

	boolean isEqual(Metaphor nm1, Metaphor nm2){
		if (!equalObjects(nm1.getMetaphorSet(), nm2.getMetaphorSet())
		        || nm1.getMetaphorData().size() != nm2.getMetaphorData().size())
			return false;
		if (nm1.getMetaphorData().isEmpty() && nm2.getMetaphorData().isEmpty())
			return true;

		boolean equal = false;
		for (int k = 0; k < nm1.getMetaphorData().size(); k++){
			MetaphorData md1 = nm1.getMetaphorData().get(k);
			equal = false;
			for (int n = 0; n < nm2.getMetaphorData().size(); n++){
				MetaphorData md2 = nm2.getMetaphorData().get(n);
				if (md1.getAttribute().equals(md2.getAttribute())){
					equal = md1.getData() != null && md1.getData().equals(md2.getData());
					break;
				}
			}
			if (!equal)
				break;
		}
		return equal;
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
