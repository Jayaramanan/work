/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;

import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;

public class AttributeEditModelTest extends ACTestCase {

    public void testGenerateTreeModel() {
    	SessionData.getInstance().setDbName("NI3");
        assertNotNull(new AttributeEditModel().generateTreeModel());
        AttributeEditModel model = new AttributeEditModel();
        List<ObjectDefinition> schemas = getSchemas();
        model.setSchemaList(schemas);
        TreeModel tModel = model.generateTreeModel();
        assertTrue(tModel.getChildCount(tModel.getRoot()) == schemas.size());
	}

    public void testGenerateTableModel() {
        assertNotNull(new AttributeEditModel().generateTableModel());
        AttributeEditModel model= new AttributeEditModel();
        List<ObjectDefinition> schemas = getSchemas();
        ObjectDefinition current = schemas.get(0).getObjectDefinitions().get(0);
        model.setCurrentObjectDefinition(current);
        TableModel tModel = model.generateTableModel();
        assertTrue(tModel.getRowCount() == current.getObjectAttributes().size());
        tModel.setValueAt("hello", 0, 0);
        assertEquals(tModel.getValueAt(0, 0), "hello");
        tModel.setValueAt("  invalid format string   ", 0, 20);
        assertEquals(tModel.getValueAt(0, 20), "invalid format string");
        tModel.setValueAt("   ", 0, 20);
        assertNull(tModel.getValueAt(0, 20));
        tModel.setValueAt("", 0, 20);
        assertNull(tModel.getValueAt(0, 20));
    }

    public List<ObjectDefinition> getSchemas() {
        int id = 1;
        ArrayList res = new ArrayList();

        ObjectDefinition objectDefinition1 = new ObjectDefinition();
        objectDefinition1.setParentObject(null);
        objectDefinition1.setId(id++);
        objectDefinition1.setName("Schema1");

        List objectDefinitions1 = new ArrayList();
        ObjectDefinition child1 = new ObjectDefinition();
        child1.setId(id++);
        child1.setName("ObjectDefinition1");
        child1.setParentObject(objectDefinition1);
        child1.setObjectAttributes(generateObjectAttributes(child1, 1, 10));
        objectDefinitions1.add(child1);

        ObjectDefinition child2 = new ObjectDefinition();
        child2.setId(id++);
        child2.setName("ObjectDefinition2");
        child2.setParentObject(objectDefinition1);
        child2.setObjectAttributes(generateObjectAttributes(child2, 50, 10));
        objectDefinitions1.add(child2);

        ObjectDefinition child3 = new ObjectDefinition();
        child3.setId(id++);
        child3.setName("ObjectDefinition3");
        child3.setParentObject(objectDefinition1);
        objectDefinitions1.add(child3);

        objectDefinition1.setObjectDefinitions(objectDefinitions1);
        res.add(objectDefinition1);

        ObjectDefinition objectDefinition2 = new ObjectDefinition();
        objectDefinition2.setParentObject(null);
        objectDefinition2.setId(id++);
        objectDefinition2.setName("Schema2");
        List objectDefinitions2 = new ArrayList();

        ObjectDefinition child4 = new ObjectDefinition();
        child4.setId(id++);
        child4.setName("ObjectDefinition1");
        child4.setParentObject(objectDefinition2);
        child4.setObjectAttributes(generateObjectAttributes(child4, 100, 10));
        objectDefinitions2.add(child4);

        ObjectDefinition child5 = new ObjectDefinition();
        child5.setId(id++);
        child5.setName("ObjectDefinition2");
        child5.setParentObject(objectDefinition2);
        objectDefinitions2.add(child5);

        ObjectDefinition child6 = new ObjectDefinition();
        child6.setId(id++);
        child6.setName("ObjectDefinition3");
        child6.setParentObject(objectDefinition2);
        objectDefinitions2.add(child6);

        objectDefinition2.setObjectDefinitions(objectDefinitions2);
        res.add(objectDefinition2);

        return res;
    }

    private List<ObjectAttribute> generateObjectAttributes(ObjectDefinition parent, int baseId, int count) {
        ArrayList<ObjectAttribute> attrs = new ArrayList<ObjectAttribute>();
        for (int i = 0; i < count; i++) {
            ObjectAttribute oa = new ObjectAttribute(parent);
            oa.setId(baseId + i);
            oa.setSort(1);
            oa.setName("attr" + oa.getId());
            oa.setLabel("label" + oa.getId());
            oa.setPredefined(Boolean.TRUE);
            oa.setDescription(oa.getName());
            DataType dt = new DataType();
            dt.setId(1);
            dt.setName("txt");
            oa.setDataType(dt);
            oa.setInFilter(true);
            oa.setInLabel(true);
            oa.setInToolTip(true);
            oa.setInSearch(true);
            oa.setInAdvancedSearch(true);
            oa.setInMetaphor(true);
            //TODO set usefull values
            //	oa.setInGraphLabel(true);//
            //	private Boolean labelBold;
            //	private Boolean labelItalic;
            //	private Boolean labelUnderline;
            //	private Boolean contentBold;
            //	private Boolean contentItalic;
            //	private Boolean contentUnderline;
            //	private Date created;
            //	private User createdBy;
            //	private String managing;
            //	private String inTable;
            //	private Boolean inExport;
            //	private Boolean inSimpleSearch;
            //	private String exportLabel;
            //	private Boolean inPrefilter;
            //	private String format;
            //	private String valueDescription;
            //	private Integer labelSort;
            //	private Integer filterSort;
            //	private Integer searchSort;
            attrs.add(oa);
        }
        return attrs;
    }
}