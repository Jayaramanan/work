/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.maps;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.MapJob;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.jobs.MapJobStatus;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class MapJobTableModel extends ACTableModel{

	private static final long serialVersionUID = -3482642134981780022L;
	private static final BigDecimal minX = new BigDecimal(-180);
	private static final BigDecimal maxX = new BigDecimal(180);
	private static final BigDecimal minY = new BigDecimal(-90);
	private static final BigDecimal maxY = new BigDecimal(90);

	private List<MapJob> jobs = new ArrayList<MapJob>();
	private Map<Integer, String> userZooms;

	public MapJobTableModel(){
		addColumn(Translation.get(TextID.User), true, User.class, true);
		addColumn(Translation.get(TextID.X1), true, BigDecimal.class, true);
		addColumn(Translation.get(TextID.X2), true, BigDecimal.class, true);
		addColumn(Translation.get(TextID.Y1), true, BigDecimal.class, true);
		addColumn(Translation.get(TextID.Y2), true, BigDecimal.class, true);
		addColumn(Translation.get(TextID.Scale), true, String.class, true);
		addColumn(Translation.get(TextID.TimeStart), true, Date.class, false);
		addColumn(Translation.get(TextID.TimeEnd), false, Date.class, false);
		addColumn(Translation.get(TextID.JobStatus), false, MapJobStatus.class, false);
		addColumn(Translation.get(TextID.TriggeredBy), false, User.class, false);
	}

	public MapJobTableModel(List<MapJob> jobs, Map<Integer, String> map){
		this();
		this.jobs = jobs;
		this.userZooms = map;
	}

	public int getRowCount(){
		return jobs.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		MapJob job = jobs.get(rowIndex);
		switch (columnIndex){
			case 0:
				return job.getUser();
			case 1:
				return job.getX1();
			case 2:
				return job.getX2();
			case 3:
				return job.getY1();
			case 4:
				return job.getY2();
			case 5:
				return job.getScale();
			case 6:
				return job.getTimeStart();
			case 7:
				return job.getTimeEnd();
			case 8:
				return MapJobStatus.getStatus(job.getStatus());
			case 9:
				return job.getTriggeredBy();

			default:
				return null;
		}
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex){
		value = validateValue(value);
		super.setValueAt(value, rowIndex, columnIndex);
		MapJob job = jobs.get(rowIndex);
		boolean needRefresh = false;
		switch (columnIndex){
			case 0:
				User user = (User) value;
				if (user != null && !user.equals(job.getUser())){
					setPredefinedZooms(job, user);
					needRefresh = true;
				}
				job.setUser(user);
				break;
			case 1:
				job.setX1(validateLongitude(value));
				break;
			case 2:
				job.setX2(validateLongitude(value));
				break;
			case 3:
				job.setY1(validateLatitude(value));
				break;
			case 4:
				job.setY2(validateLatitude(value));
				break;
			case 5:
				String scale = validateScale((String) value, job.getUser());
				job.setScale(scale);
				break;
			case 6:
				job.setTimeStart((Date) value);
				break;

			default:
				break;
		}
		if (needRefresh){
			fireTableRowsUpdated(rowIndex, rowIndex);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex){
		if (columnIndex > 6){
			return false;
		} else{
			MapJob job = jobs.get(rowIndex);
			return MapJobStatus.Scheduled.getValue().equals(job.getStatus());
		}
	}

	public MapJob getSelectedJob(int rowIndex){
		if (rowIndex >= 0 && rowIndex < jobs.size()){
			return jobs.get(rowIndex);
		}
		return null;
	}

	public int indexOf(MapJob job){
		return jobs.indexOf(job);
	}

	BigDecimal validateLongitude(Object value){
		if (value == null){
			return null;
		}
		BigDecimal lon = (BigDecimal) value;
		if (lon.compareTo(minX) < 0){
			lon = minX;
		} else if (lon.compareTo(maxX) > 0){
			lon = maxX;
		}
		return lon;
	}

	BigDecimal validateLatitude(Object value){
		if (value == null){
			return null;
		}
		BigDecimal lat = (BigDecimal) value;
		if (lat.compareTo(minY) < 0){
			lat = minY;
		} else if (lat.compareTo(maxY) > 0){
			lat = maxY;
		}
		return lat;
	}

	private void setPredefinedZooms(MapJob job, User user){
		job.setScale(userZooms.get(user.getId()));
	}

	String validateScale(String scale, User user){
		if (scale == null || user == null || userZooms.get(user.getId()) == null){
			return null;
		}
		scale = scale.replace(" ", "");
		String[] scales = scale.split(",");
		String pZoom = userZooms.get(user.getId());

		String[] pZooms = pZoom.split(",");
		String result = "";

		for (String pz : pZooms){
			for (String sc : scales){
				if (sc.equals(pz)){
					if (!result.isEmpty()){
						result += ",";
					}
					result += sc;
					break;
				}
			}
		}
		return result;
	}

	@Override
	public String getToolTip(int row, int column){
		if (isCellEditable(row, column)){
			return null;
		}
		if (column > 6){
			return Translation.get(TextID.ReadonlyFilledAutomatically);
		} else{
			return Translation.get(TextID.ReadonlyProcessedJob);
		}
	}

	public void setData(List<MapJob> jobs, Map<Integer, String> map){
		this.jobs = jobs;
		this.userZooms = map;
	}
}
