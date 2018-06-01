package com.ni3.ag.navigator.server.domain;

import com.ni3.ag.navigator.shared.domain.DeltaType;
import com.ni3.ag.navigator.shared.domain.User;
import java.util.Date;
import java.util.Map;

public class DeltaHeader{
	// This constant represents an action that should be ignored.
	public static final DeltaHeader DO_NOTHING = new DeltaHeader();

	private long id;
	private DeltaType deltaType;
	private Date timestamp;
	private SyncStatus syncStatus;
	private User creator;
	private boolean isSync;

	private Map<DeltaParamIdentifier, DeltaParam> deltaParameters;

	private DeltaHeader(){
	}

	public DeltaHeader(final DeltaType deltaType, final User creator, final Map<DeltaParamIdentifier, DeltaParam> parameters){
		this.deltaType = deltaType;
		this.creator = creator;
		isSync = false;
		syncStatus = SyncStatus.New;
		timestamp = new Date();
		deltaParameters = parameters;
	}

	public DeltaHeader(final Long i){
		id = i;
	}

	public DeltaHeader(long id, int status){
		this.id = id;
		syncStatus = SyncStatus.fromInt(status);
	}

	public User getCreator(){
		return creator;
	}

	public Map<DeltaParamIdentifier, DeltaParam> getDeltaParameters(){
		return deltaParameters;
	}

	public DeltaType getDeltaType(){
		return deltaType;
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

	public boolean isSync(){
		return isSync;
	}

	public void setCreator(final User creator){
		this.creator = creator;
	}

	public void setDeltaParameters(final Map<DeltaParamIdentifier, DeltaParam> deltaParameters){
		this.deltaParameters = deltaParameters;
	}

	public void setDeltaType(final DeltaType deltaType){
		this.deltaType = deltaType;
	}

	public void setId(final long id){
		this.id = id;
	}

	public void setSync(final boolean isSync){
		this.isSync = isSync;
	}

	public void setSyncStatus(final SyncStatus syncStatus){
		this.syncStatus = syncStatus;
	}

	public void setTimestamp(final Date timestamp){
		this.timestamp = timestamp;
	}

	public boolean processingError(){
		return SyncStatus.Error.equals(syncStatus);
	}

	@Override
	public String toString(){
		return "DeltaHeader [id=" + id + ", deltaType=" + deltaType + ", timestamp=" + timestamp + ", syncStatus="
		        + syncStatus + ", creator=" + creator + ", isSync=" + isSync + ", deltaParameters=" + deltaParameters + "]";
	}
}
