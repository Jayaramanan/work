/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

/**
 * This interface tells ACTable that it's background should not be changed; it's border should be changed instead
 * 
 * @author Mihail Agranat
 * 
 */
public interface StrongTableCellRenderer extends TableCellRenderer{

	public void setBorder(Border b);
}
