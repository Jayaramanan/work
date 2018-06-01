/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import junit.framework.TestCase;

import java.util.LinkedList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GroupTest extends TestCase{
	Group group;

	@Override
	protected void setUp() throws Exception{
		group = new Group();
	}

	public void testEquals(){
		Group g1 = new Group();
		Group g2 = new Group();
		assertTrue(g1.equals(g1));
		assertTrue(g2.equals(g2));
		assertFalse(g1.equals(g2));
		assertFalse(g2.equals(g1));
		g1.setId(1);
		assertFalse(g1.equals(g2));
		assertFalse(g2.equals(g1));
		g2.setId(2);
		assertFalse(g1.equals(g2));
		assertFalse(g2.equals(g1));
		g2.setId(1);
		assertTrue(g1.equals(g2));
		assertTrue(g2.equals(g1));
	}

	public void testCompareToWithNulls(){
		group.setName(null);
		assertEquals(0, group.compareTo(null));
	}

	public void testCompareToWithNullArg(){
		group.setName("Group");
		assertTrue(group.compareTo(null) > 0);
	}

	public void testCompareToWithNullGroup(){
		group.setName(null);
		Group arg = new Group();
		arg.setName("ArgGroup");
		assertTrue(group.compareTo(arg) < 0);
	}

	public void testCompareToWithNullArgGroup(){
		group.setName("Group");
		Group arg = new Group();
		arg.setName(null);
		assertTrue(group.compareTo(arg) > 0);
	}

	public void testCompareToEquals(){
		group.setName("Group");
		Group arg = new Group();
		arg.setName("Group");
		assertEquals(0, group.compareTo(arg));
	}

	public void testCompareToArgIsLess(){
		group.setName("AGroup");
		Group arg = new Group();
		arg.setName("BGroup");
		assertTrue(group.compareTo(arg) < 0);
	}

	public void testCompareToArgIsGreater(){
		group.setName("BGroup");
		Group arg = new Group();
		arg.setName("AGroup");
		assertTrue(group.compareTo(arg) > 0);
	}

    public void testCloneChartGroupsToNull() throws CloneNotSupportedException {
        Group actualGroup = new Group();
        Group sourceGroup = mock(Group.class);
        actualGroup.cloneChartGroupsTo(sourceGroup);
        assertNull(actualGroup.getChartGroups());
    }

    public void testCloneAttributeGroupsToToNull() throws CloneNotSupportedException {
        Group actualGroup = new Group();
        Group sourceGroup = mock(Group.class);
        actualGroup.cloneAttributeGroupsTo(sourceGroup);
        assertNull(actualGroup.getAttributeGroups());
    }

    public void testCloneObjectGroupsToToNull() throws CloneNotSupportedException {
        Group actualGroup = new Group();
        Group sourceGroup = mock(Group.class);
        actualGroup.cloneObjectGroupsTo(sourceGroup);
        assertNull(actualGroup.getObjectGroups());
    }

    public void testCloneSchemaGroupsToToNull() throws CloneNotSupportedException {
        Group actualGroup = new Group();
        Group sourceGroup = mock(Group.class);
        actualGroup.cloneSchemaGroupsTo(sourceGroup);
        assertNull(actualGroup.getSchemaGroups());
    }

    public void testCloneChartGroupsTo() throws CloneNotSupportedException {
        Group actualGroup = new Group();

        Group sourceGroup = new Group();
        final LinkedList<ChartGroup> chartGroups = new LinkedList<ChartGroup>();
        final ChartGroup chartGroup = mock(ChartGroup.class);
        final Chart chart = new Chart();
        when(chartGroup.getChart()).thenReturn(chart);
        final ChartGroup clonedChartGroup = new ChartGroup();
        when(chartGroup.clone(chart, actualGroup)).thenReturn(clonedChartGroup);
        
        chartGroups.add(chartGroup);
        sourceGroup.setChartGroups(chartGroups);

        sourceGroup.cloneChartGroupsTo(actualGroup);

        assertNotNull(actualGroup.getChartGroups());
        assertEquals(1, actualGroup.getChartGroups().size());
        verify(chartGroup).clone(chart, actualGroup);
        assertEquals(clonedChartGroup, actualGroup.getChartGroups().get(0));
    }

    public void testCloneAttributeGroupsTo() throws CloneNotSupportedException {
        Group actualGroup = new Group();

        final ObjectAttribute objectAttribute = new ObjectAttribute();

        final AttributeGroup clonedAttributeGroup = new AttributeGroup();

        final AttributeGroup attributeGroup = mock(AttributeGroup.class);
        when(attributeGroup.getObjectAttribute()).thenReturn(objectAttribute);
        when(attributeGroup.clone(objectAttribute, actualGroup)).thenReturn(clonedAttributeGroup);

        final LinkedList<AttributeGroup> attributeGroups = new LinkedList<AttributeGroup>();
        attributeGroups.add(attributeGroup);

        Group sourceGroup = new Group();
        sourceGroup.setAttributeGroups(attributeGroups);

        sourceGroup.cloneAttributeGroupsTo(actualGroup);

        assertNotNull(actualGroup.getAttributeGroups());
        assertEquals(1, actualGroup.getAttributeGroups().size());
        verify(attributeGroup).clone(objectAttribute, actualGroup);
        assertEquals(clonedAttributeGroup, actualGroup.getAttributeGroups().get(0));
    }

    public void testCloneObjectGroupsTo() throws CloneNotSupportedException {
        Group actualGroup = new Group();

        final ObjectDefinition objectDefinition = new ObjectDefinition();

        final ObjectGroup clonedObjectGroup = new ObjectGroup();

        final ObjectGroup objectGroup = mock(ObjectGroup.class);
        when(objectGroup.getObject()).thenReturn(objectDefinition);
        when(objectGroup.clone(objectDefinition, actualGroup)).thenReturn(clonedObjectGroup);

        final LinkedList<ObjectGroup> objectGroups = new LinkedList<ObjectGroup>();
        objectGroups.add(objectGroup);

        Group sourceGroup = new Group();
        sourceGroup.setObjectGroups(objectGroups);

        sourceGroup.cloneObjectGroupsTo(actualGroup);

        assertNotNull(actualGroup.getObjectGroups());
        assertEquals(1, actualGroup.getObjectGroups().size());
        verify(objectGroup).clone(objectDefinition, actualGroup);
        assertEquals(clonedObjectGroup, actualGroup.getObjectGroups().get(0));
    }

    public void testCloneSchemaGroupsTo() throws CloneNotSupportedException {
        Group actualGroup = new Group();

        final Schema schema = new Schema();

        final SchemaGroup clonedSchemaGroup = new SchemaGroup();

        final SchemaGroup schemaGroup = mock(SchemaGroup.class);
        when(schemaGroup.getSchema()).thenReturn(schema);
        when(schemaGroup.clone(schema, actualGroup)).thenReturn(clonedSchemaGroup);

        final LinkedList<SchemaGroup> schemaGroups = new LinkedList<SchemaGroup>();
        schemaGroups.add(schemaGroup);

        Group sourceGroup = new Group();
        sourceGroup.setSchemaGroups(schemaGroups);

        sourceGroup.cloneSchemaGroupsTo(actualGroup);

        assertNotNull(actualGroup.getSchemaGroups());
        assertEquals(1, actualGroup.getSchemaGroups().size());
        verify(schemaGroup).clone(schema, actualGroup);
        assertEquals(clonedSchemaGroup, actualGroup.getSchemaGroups().get(0));
    }

    public void testCloneGroupPrefiltersTo() throws CloneNotSupportedException {
        Group actualGroup = new Group();

        final PredefinedAttribute predefinedAttribute = new PredefinedAttribute();

        final GroupPrefilter clonedAttributeGroup = new GroupPrefilter();

        final GroupPrefilter groupPrefilter = mock(GroupPrefilter.class);
        when(groupPrefilter.getPredefinedAttribute()).thenReturn(predefinedAttribute);
        when(groupPrefilter.clone(actualGroup)).thenReturn(clonedAttributeGroup);

        final LinkedList<GroupPrefilter> groupPrefilters = new LinkedList<GroupPrefilter>();
        groupPrefilters.add(groupPrefilter);

        Group sourceGroup = new Group();
        sourceGroup.setPredefAttributeGroups(groupPrefilters);

        sourceGroup.cloneGroupPrefiltersTo(actualGroup);

        assertNotNull(actualGroup.getPredefAttributeGroups());
        assertEquals(1, actualGroup.getPredefAttributeGroups().size());
        verify(groupPrefilter).clone(actualGroup);
        assertEquals(clonedAttributeGroup, actualGroup.getPredefAttributeGroups().get(0));
    }

    public void testCloneDeep() throws CloneNotSupportedException {
        Group group = mock(Group.class);

        when(group.cloneDeep("newName")).thenCallRealMethod();
        when(group.getNodeScope()).thenReturn('a');
        when(group.getEdgeScope()).thenReturn('b');

        final Group actualGroup = group.cloneDeep("newName");

        assertEquals("newName", actualGroup.getName());
        assertEquals("a", actualGroup.getNodeScope().toString());
        assertEquals("b", actualGroup.getEdgeScope().toString());
        verify(group).cloneAttributeGroupsTo(actualGroup);
        verify(group).cloneChartGroupsTo(actualGroup);
        verify(group).cloneObjectGroupsTo(actualGroup);
        verify(group).cloneSchemaGroupsTo(actualGroup);
        verify(group).cloneGroupPrefiltersTo(actualGroup);
    }
}
