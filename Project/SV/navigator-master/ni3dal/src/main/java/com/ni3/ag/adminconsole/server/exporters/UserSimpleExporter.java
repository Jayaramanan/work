/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters;

import java.util.List;

import org.apache.log4j.Logger;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.ni3.ag.adminconsole.domain.User;

public class UserSimpleExporter extends AbstractExporter<WritableSheet, List<User>>{
	public static final String SEET_NAME = "Users";
	private static final String[] headers = new String[] { "First name", "Last name", "User name", "Group",
	        "E-mail address", "Active" };
	private static final Logger log = Logger.getLogger(UserSimpleExporter.class);
	private static final String TRUE_LABEL = "true";
	private static final String FALSE_LABEL = "false";
	private static final Ni3ExcelStyleSheet styleSheet = DefaultNi3ExcelStyleSheet.getInstance();
	private int currentRow = 0;

	@Override
	protected void makeDecoration(WritableSheet target, List<User> dataContainer){
		currentRow = 0;
		super.makeDecoration(target, dataContainer);
		try{
			for (int i = 0; i < headers.length; i++){
				Label label = setCellString(i, currentRow, headers[i], target);
				label.setCellFormat(styleSheet.getTableHeaderStyle());
			}
		} catch (RowsExceededException e){
			log.error("", e);
		} catch (WriteException e){
			log.error("", e);
		}
		currentRow++;
	}

	@Override
	protected void makeObjectExport(WritableSheet target, List<User> dataContainer){
		try{
			for (User u : dataContainer){
				setCellString(0, currentRow, u.getFirstName(), target);
				setCellString(1, currentRow, u.getLastName(), target);
				setCellString(2, currentRow, u.getLastName(), target);
				setCellString(3, currentRow, getUserGroup(u), target);
				setCellString(4, currentRow, getUserEmail(u), target);
				setCellString(5, currentRow, getUserActive(u), target);
				currentRow++;
			}
		} catch (Exception e){
			log.error("", e);
		}
	}

	private Label setCellString(int col, int row, String str, WritableSheet target) throws RowsExceededException,
	        WriteException{
		Label label = new Label(col, row, str);
		target.addCell(label);
		int width = target.getColumnView(col).getSize();
		if (width > 256)
			width >>= 8;
		if (str.length() > width)
			width = str.length();
		target.setColumnView(col, width);
		return label;
	}

	private String getUserActive(User u){
		return u.getActive() ? TRUE_LABEL : FALSE_LABEL;
	}

	private String getUserEmail(User u){
		return u.geteMail() == null ? "" : u.geteMail();
	}

	private String getUserGroup(User u){
		if (u.getGroups() == null)
			return "";
		if (u.getGroups().isEmpty())
			return "";
		return u.getGroups().get(0).getName();
	}

}
