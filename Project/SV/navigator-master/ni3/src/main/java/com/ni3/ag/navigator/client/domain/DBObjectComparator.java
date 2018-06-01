/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.domain;

import java.util.Comparator;

import com.ni3.ag.navigator.client.gui.datalist.MatrixSortOrder;
import com.ni3.ag.navigator.client.gui.datalist.MatrixSortOrder.SortColumn;

public class DBObjectComparator implements Comparator<DBObject>{

	private MatrixSortOrder order;

	public DBObjectComparator(MatrixSortOrder order){
		this.order = order;
	}

	@Override
	public int compare(DBObject firstObj, DBObject secondObj){
		int ret = 0;

		if (order == null || firstObj == null || secondObj == null)
			return 0;

		if (firstObj.getEntity().ID != secondObj.getEntity().ID){
			return firstObj.getEntity().getSort() > secondObj.getEntity().getSort() ? 1 : -1;
		}

		for (SortColumn sort : order.getSorts()){
			final Attribute attribute = sort.getAttr();
			if (sort.getColumn() == -2 || sort.getEntityId() != firstObj.getEntity().ID){
				continue;
			}

			if (sort.getColumn() == -1){
				if (firstObj.getNumericMetaphor() != null){
					ret = firstObj.getNumericMetaphor() != null ? firstObj.getNumericMetaphor().compareTo(
							secondObj.getNumericMetaphor()) : -1;
				} else{
					final String firstIcon = firstObj.getIconName();
					ret = firstIcon != null ? firstIcon.compareTo(secondObj.getIconName()) : -1;
				}
			} else if (attribute.predefined){
				String i1 = "", i2 = "";

				if (firstObj.getValue(attribute.ID) != null)
					i1 = ((Value) (firstObj.getValue(attribute.ID))).getLabel();

				if (secondObj.getValue(attribute.ID) != null)
					i2 = ((Value) (secondObj.getValue(attribute.ID))).getLabel();

				ret = UserSettings.getCollator().compare(i1, i2);
			} else{
				ret = attribute.getDataType().compare(firstObj.getValue(attribute.ID), secondObj.getValue(attribute.ID));
			}

			if (ret != 0){
				ret = ret * (sort.isAsc() ? 1 : -1);
				break;
			}
		}
		return ret;
	}
}
