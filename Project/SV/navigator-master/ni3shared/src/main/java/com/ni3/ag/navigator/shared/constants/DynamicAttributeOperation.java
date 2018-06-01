/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */

package com.ni3.ag.navigator.shared.constants;

public enum DynamicAttributeOperation{
	Sum, Avg, Min, Max;

	@Override
	public String toString(){
		return name();
	};

}
