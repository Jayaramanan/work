/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters.xml;

import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.ni3.ag.adminconsole.domain.Formula;
import com.ni3.ag.adminconsole.domain.InMatrixType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.server.dao.PredefinedAttributeDAO;
import com.ni3.ag.adminconsole.server.importers.xml.ObjectAttributeImporter;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;

public class ObjectAttributeExporter extends XMLSchemaExporter<Element, ObjectAttribute>{
	private final static Logger log = Logger.getLogger(ObjectAttributeExporter.class);
	private PredefinedAttributeDAO predefinedDAO;
	private XMLSchemaExporter<Element, PredefinedAttribute> predefinedExporter;

	public void setPredefinedDAO(PredefinedAttributeDAO predefinedDAO){
		this.predefinedDAO = predefinedDAO;
	}

	public void setPredefinedExporter(XMLSchemaExporter<Element, PredefinedAttribute> predefinedExporter){
		this.predefinedExporter = predefinedExporter;
	}

	@Override
	protected boolean validateDataContainer(ObjectAttribute oa){
		if (oa.getDataType() == null)
			validationErrors.add(new ErrorEntry(TextID.MsgNoPropertySet, new String[] { oa.getName() + " => datatype" }));
		else
			return true;
		return false;
	}

	private String getBoolean(boolean b){
		return b ? "1" : "0";
	}

	private void exportAttributeFormat(Element oaElem, ObjectAttribute oa){
		oaElem.setAttribute("labelBold", getBoolean(oa.isLabelBold()));
		oaElem.setAttribute("labelItalic", getBoolean(oa.isLabelItalic()));
		oaElem.setAttribute("labelUnderline", getBoolean(oa.isLabelUnderline()));
		oaElem.setAttribute("contentBold", getBoolean(oa.isContentBold()));
		oaElem.setAttribute("contentItalic", getBoolean(oa.isContentItalic()));
		oaElem.setAttribute("contentUnderline", getBoolean(oa.isContentUnderline()));
		oaElem.setAttribute("format", oa.getFormat());
		oaElem.setAttribute("editFormat", oa.getEditFormat());
		oaElem.setAttribute("formatValidCharacters", oa.getFormatValidCharacters());
		oaElem.setAttribute("formatInvalidCharacters", oa.getFormatInvalidCharacters());
		oaElem.setAttribute("minValue", oa.getMinValue());
		oaElem.setAttribute("maxValue", oa.getMaxValue());
	}

	private void exportAttributeSorts(Element oaElem, ObjectAttribute oa){
		oaElem.setAttribute("sort", String.valueOf(oa.getSort()));
		oaElem.setAttribute("matrixSort", String.valueOf(oa.getMatrixSort()));
		oaElem.setAttribute("labelSort", String.valueOf(oa.getLabelSort()));
		oaElem.setAttribute("filterSort", String.valueOf(oa.getFilterSort()));
		oaElem.setAttribute("searchSort", String.valueOf(oa.getSearchSort()));
	}

	private void exportAttributeConfiguration(Element oaElem, ObjectAttribute oa){
		oaElem.setAttribute("name", oa.getName());
		oaElem.setAttribute("label", oa.getLabel());
		oaElem.setAttribute("description", oa.getDescription());
		oaElem.setAttribute("dataType", oa.getDataType().getTextId().getKey());
		oaElem.setAttribute("inMetaphor", getBoolean(oa.isInMetaphor()));
		if (oa.isFormulaAttribute() && oa.isPredefined())
			oaElem.setAttribute("predefined", ObjectAttributeImporter.PREDEFINED_ATTRIBUTE_VALUE_FORMULA_LIST);
		else if (oa.isFormulaAttribute())
			oaElem.setAttribute("predefined", ObjectAttributeImporter.PREDEFINED_ATTRIBUTE_VALUE_FORMULA);
		else if (oa.isPredefined())
			oaElem.setAttribute("predefined", ObjectAttributeImporter.PREDEFINED_ATTRIBUTE_VALUE_LIST);

		oaElem.setAttribute("displayFilter", getBoolean(oa.isInFilter()));
		oaElem.setAttribute("inLabel", getBoolean(oa.isInLabel()));
		oaElem.setAttribute("inTooltip", getBoolean(oa.isInToolTip()));
		oaElem.setAttribute("inSimpleSearch", getBoolean(oa.isInSimpleSearch()));
		oaElem.setAttribute("inAdvancedSearch", getBoolean(oa.isInAdvancedSearch()));
		oaElem.setAttribute("inExport", getBoolean(oa.isInExport()));
		oaElem.setAttribute("dataFilter", getBoolean(oa.isInPrefilter()));
		if (oa.getInMatrix() != null){
			InMatrixType imt = InMatrixType.getInMatrixType(oa.getInMatrix());
			oaElem.setAttribute("inMatrix", imt.name());
		}

		oaElem.setAttribute("aggregable", getBoolean(oa.isAggregable()));
		oaElem.setAttribute("multivalue", getBoolean(oa.getIsMultivalue()));

		oaElem.setAttribute("inContext", getBoolean(oa.isInContext()));
	}

	@Override
	protected void makeObjectExport(Element target, ObjectAttribute oa) throws ACException{
		Element oaElem = document.createElement("objectAttribute");
		log.debug("exporting object attribute `" + oa.getName() + "`");

		exportAttributeConfiguration(oaElem, oa);
		exportAttributeSorts(oaElem, oa);
		exportAttributeFormat(oaElem, oa);
		target.appendChild(oaElem);

		if (oa.isPredefined()){
			List<PredefinedAttribute> predefs = predefinedDAO.getPredefinedAttributes(oa);
			for (PredefinedAttribute predef : predefs)
				predefinedExporter.export(document, oaElem, predef);
		}
		Formula f = oa.getFormula();
		if (f != null){
			log.debug("exporting formula for object attribute `" + oa.getName() + "`");

			Element formulaElem = document.createElement("formula");
			formulaElem.setTextContent(f.getFormula());
			oaElem.appendChild(formulaElem);
		}

	}
}
