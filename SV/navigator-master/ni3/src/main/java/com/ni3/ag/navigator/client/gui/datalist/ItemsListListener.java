/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.datalist;

import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.gui.datalist.DataItem;

public interface ItemsListListener{
	public void itemSelected(DBObject node, int index, int ClickCount, int Modifier);

	public void itemChecked(DataItem item, int index, boolean Status);
}
