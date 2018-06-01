/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.OfflineJob;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.jobs.OfflineJobStatus;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class OfflineJobTableModel extends ACTableModel{

	private static final long serialVersionUID = -3482642134981780022L;
	private List<OfflineJob> jobs = new ArrayList<OfflineJob>();
	private List<Group> groups;

	public OfflineJobTableModel(){
		addColumn(Translation.get(TextID.Users), true, String.class, true);
		addColumn(Translation.get(TextID.ExportFirstDegreeObjects), true, Boolean.class, false);
		addColumn(Translation.get(TextID.TimeStart), true, Date.class, false);
		addColumn(Translation.get(TextID.TimeEnd), false, Date.class, false);
		addColumn(Translation.get(TextID.JobStatus), false, OfflineJobStatus.class, false);
		addColumn(Translation.get(TextID.TriggeredBy), false, User.class, false);
	}

	public OfflineJobTableModel(List<OfflineJob> jobs, List<Group> groups){
		this();
		this.jobs = jobs;
		this.groups = groups;
	}

	public int getRowCount(){
		return jobs.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		OfflineJob job = jobs.get(rowIndex);
		switch (columnIndex){
			case 0:
				return userIdsToUserNames(job.getUserIds());
			case 1:
				return job.getWithFirstDegreeObjects();
			case 2:
				return job.getTimeStart();
			case 3:
				return job.getTimeEnd();
			case 4:
				return OfflineJobStatus.getStatus(job.getStatus());
			case 5:
				return job.getTriggeredBy();

			default:
				return null;
		}
	}

	String userIdsToUserNames(String userIds){
		if (userIds == null || userIds.isEmpty())
			return null;
		String result = "";
		String[] ids = userIds.split(",");
		for (String id : ids){
			for (Group group : groups){
				for (User user : group.getUsers()){
					if (user.getId().toString().equals(id)){
						result += user.getUserName() + ",";
						break;
					}
				}
			}
		}
		return correctString(result);
	}

	String userNamesToUserIds(String userNames){
		if (userNames == null || userNames.isEmpty())
			return null;
		String result = "";
		String[] names = userNames.replace(" ", "").split(OfflineJob.USER_ID_SEPARATOR);
		for (String name : names){
			for (Group group : groups){
				for (User user : group.getUsers()){
					if (user.getUserName().equals(name)){
						result += user.getId() + OfflineJob.USER_ID_SEPARATOR;
						break;
					}
				}
			}
		}
		return correctString(result);
	}

	String correctString(String str){
		if (str == null || str.isEmpty())
			return null;
		return str.substring(0, str.length() - 1);
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex){
		super.setValueAt(value, rowIndex, columnIndex);
		OfflineJob job = jobs.get(rowIndex);
		switch (columnIndex){
			case 0:
				job.setUserIds(userNamesToUserIds((String) value));
				break;
			case 1:
				job.setWithFirstDegreeObjects((Boolean) value);
				break;
			case 2:
				job.setTimeStart((Date) value);
				break;

			default:
				break;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex){
		if (columnIndex > 2){
			return false;
		} else{
			OfflineJob job = jobs.get(rowIndex);
			return OfflineJobStatus.Scheduled.getValue().equals(job.getStatus());
		}
	}

	public OfflineJob getSelectedJob(int rowIndex){
		if (rowIndex >= 0 && rowIndex < jobs.size()){
			return jobs.get(rowIndex);
		}
		return null;
	}

	public int indexOf(OfflineJob job){
		return jobs.indexOf(job);
	}

	@Override
	public String getToolTip(int row, int column){
		if (isCellEditable(row, column)){
			return null;
		}
		if (column > 2){
			return Translation.get(TextID.ReadonlyFilledAutomatically);
		} else{
			return Translation.get(TextID.ReadonlyProcessedJob);
		}
	}

	public void setData(List<OfflineJob> jobs, List<Group> groups){
		this.jobs = jobs;
		this.groups = groups;
	}
}
