/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class GroupPrefilter implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Group group;
	private PredefinedAttribute predefinedAttribute;

	GroupPrefilter(){
	}

	public GroupPrefilter(Group group, PredefinedAttribute pAttribute){
		setGroup(group);
		setPredefinedAttribute(pAttribute);
	}

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public Group getGroup(){
		return group;
	}

	public void setGroup(Group group){
		this.group = group;
	}

	public PredefinedAttribute getPredefinedAttribute(){
		return predefinedAttribute;
	}

	public void setPredefinedAttribute(PredefinedAttribute predefinedAttribute){
		this.predefinedAttribute = predefinedAttribute;
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == null)
			return false;
		if (!(o instanceof GroupPrefilter))
			return false;
		if (o == this)
			return true;
		GroupPrefilter gp = (GroupPrefilter) o;
		if (getId() == null || gp.getId() == null)
			return false;
		return getId().equals(gp.getId());
	}

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    protected GroupPrefilter clone(Group group) throws CloneNotSupportedException {
        final GroupPrefilter groupPrefilter = (GroupPrefilter) clone();
        groupPrefilter.setId(null);
        groupPrefilter.setGroup(group);
        return  groupPrefilter;
    }
}
