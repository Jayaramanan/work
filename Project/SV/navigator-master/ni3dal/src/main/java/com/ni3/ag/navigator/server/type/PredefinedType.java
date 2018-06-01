/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.type;

public enum PredefinedType{
	NotPredefined(0), Predefined(1), Formula(2), FormulaPredefined(3);

	int id;

	PredefinedType(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public static PredefinedType getById(int id){
		for (PredefinedType pd : values()){
			if (pd.getId() == id)
				return pd;
		}
		return NotPredefined;
	}
}
