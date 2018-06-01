package com.ni3.ag.adminconsole.domain;

import java.util.Date;
import java.util.List;

public class DeltaHeader{
	// This constant represents an action that should be ignored.
	public static final DeltaHeader DO_NOTHING = new DeltaHeader();

	public static final String COLUMN_CREATOR = "creator";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_STATUS = "syncStatus";

	private long id;
	private DeltaType deltaType;
	private Date timestamp;
	private SyncStatus syncStatus;
	private Integer creator;
	private Boolean sync;
	private List<DeltaParam> params;

	public DeltaHeader(){
		sync = false;
		syncStatus = SyncStatus.New;
		timestamp = new Date();
	}

	public DeltaHeader(final DeltaType deltaType, final Integer creator){
		this.deltaType = deltaType;
		this.creator = creator;
		sync = false;
		syncStatus = SyncStatus.New;
		timestamp = new Date();
	}

	public Integer getCreator(){
		return creator;
	}

	public DeltaType getDeltaType(){
		return deltaType;
	}

	public Integer getHACK_sync(){
		return sync ? 1 : 0;
	}

	public long getId(){
		return id;
	}

	public SyncStatus getSyncStatus(){
		return syncStatus;
	}

	public Date getTimestamp(){
		return timestamp;
	}

	public Boolean isSync(){
		return sync;
	}

	public void setCreator(final Integer creator){
		this.creator = creator;
	}

	public void setDeltaType(final DeltaType deltaType){
		this.deltaType = deltaType;
	}

	public void setHACK_sync(final Integer HACK_sync){
		sync = HACK_sync == 1;
	}

	public void setId(final long id){
		this.id = id;
	}

	public void setSync(final Boolean sync){
		this.sync = sync;
	}

	public void setSyncStatus(final SyncStatus syncStatus){
		this.syncStatus = syncStatus;
	}

	public void setTimestamp(final Date timestamp){
		this.timestamp = timestamp;
	}

	public List<DeltaParam> getParams(){
		return params;
	}

	public void setParams(List<DeltaParam> params){
		this.params = params;
	}

	@Override
	public String toString(){
		final StringBuilder sb = new StringBuilder();
		sb.append("DeltaHeader");
		sb.append("{id=").append(id);
		sb.append(", deltaType=").append(deltaType);
		sb.append(", timestamp=").append(timestamp);
		sb.append(", syncStatus=").append(syncStatus);
		sb.append(", creator=").append(creator);
		sb.append(", sync=").append(sync);
		sb.append(", deltaParameters=").append(params);
		sb.append('}');
		return sb.toString();
	}
}
