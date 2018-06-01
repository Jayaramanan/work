/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters;

import com.ni3.ag.adminconsole.validation.ACException;

/**
 * AbstractExporter generalizes the process of export to any format, all exporters should extend it
 * 
 * @author M
 * 
 */
public abstract class AbstractExporter<T, DC> {

	/**
	 * The main exporter method
	 * 
	 * @param target
	 *            the object to export to (excel sheet, xml file etc.)
	 * @param dataContainer
	 *            the object that contains data to export
	 */
	public void export(T target, DC dataContainer) throws ACException{
		makeDecoration(target, dataContainer);
		makeObjectExport(target, dataContainer);
		makeAfterDecoration(target, dataContainer);
	}

	/**
	 * Prepares target document before export (e.g. decorates it). Default implementation doesn't care about this.
	 * 
	 * @param target
	 *            the object to export to (excel sheet, xml file etc.)
	 * @param dataContainer
	 *            the object that contains data to export
	 */
	protected void makeDecoration(T target, DC dataContainer){
	}

	/**
	 * Exports data from dataContainer to target. Should be overridden by implementations.
	 * 
	 * @param target
	 *            the object to export to (excel sheet, xml file etc.)
	 * @param dataContainer
	 *            the object that contains data to export
	 */
	protected abstract void makeObjectExport(T target, DC dataContainer) throws ACException;

	/**
	 * Prepares target document after export is done (e.g. decorates it). Default implementation doesn't care about
	 * this.
	 * 
	 * @param target
	 * @param dataContainer
	 */
	protected void makeAfterDecoration(T target, DC dataContainer){
	}

}
