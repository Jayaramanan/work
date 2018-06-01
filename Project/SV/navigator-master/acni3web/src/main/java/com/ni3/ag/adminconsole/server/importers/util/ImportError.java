package com.ni3.ag.adminconsole.server.importers.util;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;

public class ImportError{
	private ObjectAttribute attr;
	private String label;
	private String srcId;
	private ImportDataError err;

	public ImportError(ObjectAttribute attr, String label, String srcId, ImportDataError err){
		this.attr = attr;
		this.label = label;
		this.srcId = srcId;
		this.err = err;
	}

	public String getLabel(){
    	return label;
    }

	public String getSrcId(){
    	return srcId;
    }

	public ImportDataError getErr(){
    	return err;
    }

	public String getAttrName(){
	    return attr.getName();
    }

	public String getObjectName(){
	    return attr.getObjectDefinition().getName();
    }

}
