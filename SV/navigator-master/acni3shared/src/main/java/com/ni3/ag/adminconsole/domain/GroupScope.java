/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class GroupScope implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	public static final char SCOPE_USED_CHAR = 'S';
	private Integer group;
	private String nodeScope;
	private String edgeScope;

    public GroupScope() {
    }

    public GroupScope(Integer group, String nodeScope, String edgeScope) {
        this.group = group;
        this.nodeScope = nodeScope;
        this.edgeScope = edgeScope;
    }

    public String getNodeScope(){
		return nodeScope;
	}

	public void setNodeScope(String nodeScope){
		this.nodeScope = nodeScope;
	}

	public String getEdgeScope(){
		return edgeScope;
	}

	public void setEdgeScope(String edgeScope){
		this.edgeScope = edgeScope;
	}

	public Integer getGroup(){
		return group;
	}

	public void setGroup(Integer group){
		this.group = group;
	}

    @Override
    protected GroupScope clone() throws CloneNotSupportedException {
        return (GroupScope) super.clone();
    }

    public GroupScope cloneFor(Group group) {
        final GroupScope groupScope;
        try {
            groupScope = clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("GroupScope clone() failed", e);
        }
        groupScope.setGroup(group.getId());
        return groupScope;
    }

	@Override
	public boolean equals(java.lang.Object o){
		if (o == null)
			return false;
		if (!(o instanceof GroupScope))
			return false;
		if (o == this)
			return true;
		GroupScope gs = (GroupScope) o;
		if (getGroup() == null || gs.getGroup() == null)
			return false;
		return getGroup().equals(gs.getGroup());
	}
}
