package com.ni3.ag.navigator.server.domain;

//TODO use ObjectDefinitionGroup instead
@Deprecated
public class GroupObjectPermissions{
	private Integer objectId;
	private int groupId;
	private boolean canRead;
	private boolean canCreate;
	private boolean canUpdate;
	private boolean canDelete;

	public Integer getObjectId(){
		return objectId;
	}

	public void setObjectId(Integer objectId){
		this.objectId = objectId;
	}

	public boolean isCanRead(){
		return canRead;
	}

	public void setCanRead(boolean canRead){
		this.canRead = canRead;
	}

	public boolean isCanCreate(){
		return canCreate;
	}

	public void setCanCreate(boolean canCreate){
		this.canCreate = canCreate;
	}

	public boolean isCanUpdate(){
		return canUpdate;
	}

	public void setCanUpdate(boolean canUpdate){
		this.canUpdate = canUpdate;
	}

	public boolean isCanDelete(){
		return canDelete;
	}

	public void setCanDelete(boolean canDelete){
		this.canDelete = canDelete;
	}

	public void setGroupId(int groupId){
		this.groupId = groupId;
	}

	public int getGroupId(){
		return groupId;
	}

	@Override
	public String toString(){
		return "GroupObjectPermissions [objectId=" + objectId + ", canRead=" + canRead + ", canCreate=" + canCreate
				+ ", canUpdate=" + canUpdate + ", canDelete=" + canDelete + "]";
	}
}
