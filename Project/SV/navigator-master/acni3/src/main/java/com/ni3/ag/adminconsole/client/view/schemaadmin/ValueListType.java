/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.client.view.schemaadmin;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.domain.Formula;
import com.ni3.ag.adminconsole.shared.language.TextID;

public enum ValueListType{
	FormulaBased(TextID.Formula, Formula.FORMULA_BASED), NotPredefined(TextID.NotPredefined, Formula.NOT_PREDEFINED), Predefined(
	        TextID.Predefined, Formula.PREDEFINED), FormulaPredefined(TextID.FormulaPredefined, Formula.FORMULA_PREDEFINED);
	private TextID textId;
	private Integer valueList;

	ValueListType(TextID tid, Integer valueListDbType){
		this.textId = tid;
		this.valueList = valueListDbType;
	}

	public static ValueListType valueOf(Integer valueList){
		for (ValueListType vlt : values()){
			if (vlt.valueList.equals(valueList))
				return vlt;
		}
		return null;
	}

	@Override
	public String toString(){
		return Translation.get(textId);
	}

	public Integer getType(){
		return valueList;
	}
}
