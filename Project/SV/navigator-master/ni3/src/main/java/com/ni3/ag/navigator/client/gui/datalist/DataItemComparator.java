/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.datalist;

import java.util.Comparator;

import com.ni3.ag.navigator.client.controller.charts.SNA.SNAAttribute;
import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.domain.DBObjectComparator;
import com.ni3.ag.navigator.client.gui.datalist.MatrixSortOrder.SortColumn;

public class DataItemComparator implements Comparator<DataItem>{
	private MatrixSortOrder order;
	private DBObjectComparator dbObjectComparator;

	public DataItemComparator(MatrixSortOrder order){
		super();
		this.order = order;
		dbObjectComparator = new DBObjectComparator(order);
	}

	@Override
	public int compare(DataItem first, DataItem second){
		int ret = 0;

		if (order == null)
			return 0;

		final DBObject firstObj = first.obj;
		final DBObject secondObj = second.obj;

		if (firstObj.getEntity().ID != secondObj.getEntity().ID){
			return firstObj.getEntity().getSort() > secondObj.getEntity().getSort() ? 1 : -1;
		}

		for (SortColumn sort : order.getSorts()){
			if (sort.getEntityId() != firstObj.getEntity().ID){
				continue;
			}
			if (sort.getColumn() == -2){
				if (first.isDisplayed() != second.isDisplayed()){
					ret = first.isDisplayed() ? 1 : -1;
					ret = sort.isAsc() ? ret : -ret;
				}
				if (ret != 0){
					break;
				}
			} else{
				if (sort.getAttr() != null && sort.getAttr().isSnaAttribute()){
					ret = compareSnaValues(first, second, sort);
				} else{
					ret = dbObjectComparator.compare(firstObj, secondObj);
				}
				break;
			}
		}
		return ret;
	}

	private int compareSnaValues(DataItem first, DataItem second, SortColumn sort){
		final SNAAttribute snaAttr = sort.getAttr().getSnaAttribute();
		Number fromValue = first.getNode() != null ? first.getNode().getSnaValue(snaAttr) : null;
		Number toValue = second.getNode() != null ? second.getNode().getSnaValue(snaAttr) : null;
		int ret = fromValue != null ? Double.compare(fromValue.doubleValue(), toValue != null ? toValue.doubleValue() : 0)
				: -1;
		return sort.isAsc() ? ret : -ret;
	}

}
