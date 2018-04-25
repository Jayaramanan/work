/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.Formula;
import com.ni3.ag.adminconsole.domain.InMatrixType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.server.dao.ObjectAttributeDAO;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class ObjectAttributeImporter extends AbstractImporter{

	public final static String PREDEFINED_ATTRIBUTE_VALUE_FORMULA = "formula";
	public final static String PREDEFINED_ATTRIBUTE_VALUE_LIST = "predefined";
	public static final String PREDEFINED_ATTRIBUTE_VALUE_FORMULA_LIST = "formula_predefined";

	private ObjectAttributeDAO objectAttributeDAO;

	private AbstractImporter predefinedAttributeImporter;
	private AbstractImporter formulaImporter;

	private ACValidationRule userTableNameRule;

	private NamedNodeMap attrs;
	private String oaName;

	private static final Logger log = Logger.getLogger(ObjectAttributeImporter.class);

	private Boolean getBoolean(Node n){
		String value = n.getTextContent();
		return Integer.parseInt(value) != 0;
	}

	protected Map<String, AbstractImporter> getTagToEmbeddedImporterMapping(){
		Map<String, AbstractImporter> map = new HashMap<String, AbstractImporter>();
		map.put("predefined", predefinedAttributeImporter);
		map.put("formula", formulaImporter);
		return map;
	}

	public void setUserTableNameRule(ACValidationRule userTableNameRule){
		this.userTableNameRule = userTableNameRule;
	}

	public void setObjectAttributeDAO(ObjectAttributeDAO objectAttributeDAO){
		this.objectAttributeDAO = objectAttributeDAO;
	}

	public void setPredefinedAttributeImporter(AbstractImporter predefinedAttributeImporter){
		this.predefinedAttributeImporter = predefinedAttributeImporter;
	}

	public void setFormulaImporter(AbstractImporter formulaImporter){
		this.formulaImporter = formulaImporter;
	}

	private void importAttributeFormat(ObjectAttribute update){
		Node n = attrs.getNamedItem("labelBold");
		if (n != null)
			update.setLabelBold(getBoolean(n));
		n = attrs.getNamedItem("labelItalic");
		if (n != null)
			update.setLabelItalic(getBoolean(n));
		n = attrs.getNamedItem("labelUnderline");
		if (n != null)
			update.setLabelUnderline(getBoolean(n));
		n = attrs.getNamedItem("contentBold");
		if (n != null)
			update.setContentBold(getBoolean(n));
		n = attrs.getNamedItem("contentItalic");
		if (n != null)
			update.setContentItalic(getBoolean(n));
		n = attrs.getNamedItem("contentUnderline");
		if (n != null)
			update.setContentUnderline(getBoolean(n));
		n = attrs.getNamedItem("format");
		if (n != null)
			update.setFormat(n.getTextContent());
		n = attrs.getNamedItem("minValue");
		if (n != null)
			update.setMinValue(n.getTextContent());
		n = attrs.getNamedItem("maxValue");
		if (n != null)
			update.setMaxValue(n.getTextContent());
		n = attrs.getNamedItem("editFormat");
		if (n != null)
			update.setEditFormat(n.getTextContent());
		n = attrs.getNamedItem("formatValidCharacters");
		if (n != null)
			update.setFormatValidCharacters(n.getTextContent());
		n = attrs.getNamedItem("formatInvalidCharacters");
		if (n != null)
			update.setFormatInvalidCharacters(n.getTextContent());
	}

	private void importAttributeSorts(ObjectAttribute update){
		Pattern sortPtn = Pattern.compile("[0-9]+");
		Node n = attrs.getNamedItem("sort");
		update.setSort(Integer.valueOf(n.getTextContent()));
		n = attrs.getNamedItem("matrixSort");
		if (n != null && sortPtn.matcher(n.getTextContent()).matches())
			update.setMatrixSort(Integer.parseInt(n.getTextContent()));
		n = attrs.getNamedItem("labelSort");
		if (n != null && sortPtn.matcher(n.getTextContent()).matches())
			update.setLabelSort(Integer.parseInt(n.getTextContent()));
		n = attrs.getNamedItem("filterSort");
		if (n != null && sortPtn.matcher(n.getTextContent()).matches())
			update.setFilterSort(Integer.parseInt(n.getTextContent()));
		n = attrs.getNamedItem("searchSort");
		if (n != null && sortPtn.matcher(n.getTextContent()).matches())
			update.setSearchSort(Integer.parseInt(n.getTextContent()));
	}

	private void importAttributePredefinedType(ObjectAttribute update){
		Node predefined = attrs.getNamedItem("predefined");
		Integer predefType = Formula.NOT_PREDEFINED;
		if (predefined != null){
			if (PREDEFINED_ATTRIBUTE_VALUE_FORMULA.equals(predefined.getTextContent()))
				predefType = Formula.FORMULA_BASED;
			else if (PREDEFINED_ATTRIBUTE_VALUE_LIST.equals(predefined.getTextContent()))
				predefType = Formula.PREDEFINED;
			else if (PREDEFINED_ATTRIBUTE_VALUE_FORMULA_LIST.equals(predefined.getTextContent()))
				predefType = Formula.FORMULA_PREDEFINED;
		}
		update.setPredefined_(predefType);
	}

	private void importAttributeConfiguration(ObjectAttribute update){
		String label = attrs.getNamedItem("label").getTextContent();
		update.setLabel(label);
		String datatype = attrs.getNamedItem("dataType").getTextContent();
		DataType dtype = DataType.fromLabel(datatype);
		update.setDataType(dtype);
		Node n = attrs.getNamedItem("description");
		if (n != null)
			update.setDescription(n.getTextContent());
		n = attrs.getNamedItem("inMetaphor");
		if (n != null)
			update.setInMetaphor(getBoolean(n));
		n = attrs.getNamedItem("displayFilter");
		if (n != null)
			update.setInFilter(getBoolean(n));
		n = attrs.getNamedItem("inLabel");
		if (n != null)
			update.setInLabel(getBoolean(n));
		n = attrs.getNamedItem("inTooltip");
		if (n != null)
			update.setInToolTip(getBoolean(n));
		n = attrs.getNamedItem("inSimpleSearch");
		if (n != null)
			update.setInSimpleSearch(getBoolean(n));
		n = attrs.getNamedItem("inExport");
		if (n != null)
			update.setInExport(getBoolean(n));
		n = attrs.getNamedItem("inAdvancedSearch");
		if (n != null)
			update.setInAdvancedSearch(getBoolean(n));
		n = attrs.getNamedItem("dataFilter");
		if (n != null)
			update.setInPrefilter(getBoolean(n));
		n = attrs.getNamedItem("inMatrix");
		if (n != null){
			String inMatrixStr = n.getTextContent();
			for (InMatrixType inMT : InMatrixType.values())
				if (inMT.name().equals(inMatrixStr)){
					update.setInMatrix(inMT.getValue());
					break;
				}
		}
		n = attrs.getNamedItem("aggregable");
		if (n != null)
			update.setAggregable(getBoolean(n));
		n = attrs.getNamedItem("multivalue");
		if (n != null)
			update.setIsMultivalue(getBoolean(n));
		n = attrs.getNamedItem("inContext");
		if (n != null)
			update.setInContext(getBoolean(n));
	}

	@Override
	public Object getObjectFromXML(Node node){
		attrs = node.getAttributes();

		ObjectDefinition parent = (ObjectDefinition) this.parent;
		log.debug("parent name: " + parent.getName() + "; parent: " + parent);

		this.oaName = attrs.getNamedItem("name").getTextContent();
		log.debug("importing object attribute `" + oaName + "`");
		ObjectAttribute update = getObjectAttributeByName(parent, oaName);
		if (update == null){
			update = new ObjectAttribute();
			update.setObjectDefinition(parent);
			update.setName(oaName);
			if (parent.getObjectAttributes() == null)
				parent.setObjectAttributes(new ArrayList<ObjectAttribute>());
			parent.getObjectAttributes().add(update);
		}

		importAttributeConfiguration(update);
		importAttributePredefinedType(update);
		importAttributeFormat(update);
		importAttributeSorts(update);

		setNextSorts(update, (ObjectDefinition) parent);

		return update;
	}

	private void setNextSorts(ObjectAttribute attribute, ObjectDefinition object){
		int nextSort = 1;
		int nextLabelSort = 1;
		int nextFilterSort = 1;
		int nextSearchSort = 1;
		int nextMatrixSort = 1;
		if (object != null && object.getObjectAttributes() != null){
			for (ObjectAttribute attr : object.getObjectAttributes()){
				if (attr.getSort() != null && attr.getSort().intValue() >= nextSort){
					nextSort = attr.getSort() + 1;
				}
				if (attr.getLabelSort() != null && attr.getLabelSort().intValue() >= nextLabelSort){
					nextLabelSort = attr.getLabelSort() + 1;
				}
				if (attr.getFilterSort() != null && attr.getFilterSort().intValue() >= nextFilterSort){
					nextFilterSort = attr.getFilterSort() + 1;
				}
				if (attr.getSearchSort() != null && attr.getSearchSort().intValue() >= nextSearchSort){
					nextSearchSort = attr.getSearchSort() + 1;
				}
				if (attr.getMatrixSort() != null && attr.getMatrixSort().intValue() >= nextMatrixSort){
					nextMatrixSort = attr.getMatrixSort() + 1;
				}
			}
		}
		if (attribute.getSort() == null)
			attribute.setSort(nextSort);
		if (attribute.getLabelSort() == null)
			attribute.setLabelSort(nextLabelSort);
		if (attribute.getFilterSort() == null)
			attribute.setFilterSort(nextFilterSort);
		if (attribute.getSearchSort() == null)
			attribute.setSearchSort(nextSearchSort);
		if (attribute.getMatrixSort() == null)
			attribute.setMatrixSort(nextMatrixSort);

	}

	@Override
	protected void persist(Object o){
		objectAttributeDAO.saveOrUpdate((ObjectAttribute) o);
		objectAttributeDAO.merge((ObjectAttribute) o);
	}

	@Override
	protected boolean validateObject(Object o, Node node){
		NamedNodeMap attrs = node.getAttributes();
		Node dataTypeAttr = attrs.getNamedItem("dataType");
		if (o != null){
			ObjectAttribute attr = (ObjectAttribute) o;
			SchemaAdminModel model = new SchemaAdminModel();
			ObjectDefinition od = (ObjectDefinition) this.parent;
			model.setCurrentObjectDefinition(od);
			if (od.getObjectAttributes() == null)
				od.setObjectAttributes(new ArrayList<ObjectAttribute>());

			if (attr.getDataType() == null){
				errorContainer.addError(TextID.MsgInvalidDataType, new String[] { dataTypeAttr.getTextContent() });
			}
			userTableNameRule.performCheck(model);
			return true;
		}
		return false;
	}

	@Override
	public String[] getMandatoryXMLAttributes(){
		return new String[] { "name", "label", "dataType" };
	}
}
