package com.ni3.ag.navigator.shared.domain;

public class UserSetting{

	private Integer id;
	private String section;
	private String property;
	private String value;

	public Integer getId(){
		return id;
	}

	public String getProperty(){
		return property;
	}

	public String getSection(){
		return section;
	}

	public String getValue(){
		return value;
	}

	public void setId(final Integer id){
		this.id = id;
	}

	public void setProperty(final String property){
		this.property = property;
	}

	public void setSection(final String section){
		this.section = section;
	}

	public void setValue(final String value){
		this.value = value;
	}

	@Override
	public String toString(){
		return "UserSetting [id=" + id + ", section=" + section + ", property=" + property + ", value=" + value + "]";
	}

}
