/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.shared.language.TextID;

public class ErrorEntry implements Serializable{

	private static final long serialVersionUID = 1L;
	private TextID id;
	private List<String> errors;

	public ErrorEntry(){

	}

	public ErrorEntry(TextID id, List<String> params){
		setId(id);
		setErrors(params);
	}

	public ErrorEntry(TextID id, String[] params){
		setId(id);
		setErrors(params);
	}

	public ErrorEntry(TextID id){
		setId(id);
		setErrors(new String[]{});
	}

	public TextID getId(){
		return id;
	}

	public void setId(TextID id){
		this.id = id;
	}

	public List<String> getErrors(){
		return errors;
	}

	public void setErrors(List<String> errors){
		this.errors = errors;
	}

	public void setErrors(String[] errs){
		errors = new ArrayList<String>();
		for (int i = 0; i < errs.length; i++)
			errors.add(errs[i]);
	}

	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
		if (id != null){
			str.append(id);
			if (errors != null && !errors.isEmpty()){
				str.append(": ");
				for (String error : errors){
					str.append(error).append(";");
				}
			}
		}

		return str.toString();
	}

	@Override
	public boolean equals(Object o){
		if (this == o) return true;
		if (!(o instanceof ErrorEntry)) return false;

		ErrorEntry that = (ErrorEntry) o;

		if (errors != null ? !errors.equals(that.errors) : that.errors != null) return false;
		if (id != that.id) return false;

		return true;
	}
}
