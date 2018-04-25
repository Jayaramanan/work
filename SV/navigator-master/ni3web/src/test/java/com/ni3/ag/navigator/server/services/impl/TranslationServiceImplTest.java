/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.server.dao.LanguageDAO;
import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.domain.PredefinedAttribute;
import com.ni3.ag.navigator.server.domain.Schema;
import com.ni3.ag.navigator.server.type.PredefinedType;
import com.ni3.ag.navigator.shared.domain.LanguageItem;
import junit.framework.TestCase;

public class TranslationServiceImplTest extends TestCase{
	private TranslationServiceImpl service;
	private List<PredefinedAttribute> values;
	private Schema schema;

	@Override
	protected void setUp() throws Exception{
		service = new TranslationServiceImpl();
		schema = new Schema();
		schema.setId(1);
		schema.setDefinitions(new ArrayList<ObjectDefinition>());
		ObjectDefinition entity = new ObjectDefinition(schema);
		entity.setId(11);
		entity.setAttributes(new ArrayList<Attribute>());
		schema.getDefinitions().add(entity);
		Attribute attribute = new Attribute(111);
		attribute.setPredefined(PredefinedType.Predefined);
		entity.getAttributes().add(attribute);
		values = generatePredefinedAttributes(5);

		attribute.setValues(values);

		service.setLanguageDAO(new LanguageDAO(){
			@Override
			public List<LanguageItem> getTranslations(int id){
				List<LanguageItem> items = new ArrayList<LanguageItem>();
				final LanguageItem item = new LanguageItem();
				item.setProperty("label_2");
				item.setValue("label_2_trl");
				items.add(item);

				final LanguageItem item2 = new LanguageItem();
				item2.setProperty("label_7");
				item2.setValue("label_7_trl");
				items.add(item2);
				return items;
			}
		});
	}

	public void testTranslateSchema(){
		Schema result = service.translateSchema(schema, 1);
		Attribute attr = result.getDefinitions().get(0).getAttributes().get(0);
		assertEquals("label_1", attr.getValues().get(0).getLabelTrl());
		assertEquals("label_2_trl", attr.getValues().get(1).getLabelTrl());
		assertEquals("label_3", attr.getValues().get(2).getLabelTrl());
		assertEquals("label_4", attr.getValues().get(3).getLabelTrl());
		assertEquals("label_5", attr.getValues().get(4).getLabelTrl());
	}

	public void testFillTranslationMap(){
		final Map<String, String> map = service.fillTranslationMap(1);
		assertEquals(2, map.size());
		assertEquals("label_2_trl", map.get("label_2"));
		assertEquals("label_7_trl", map.get("label_7"));
	}

	private List<PredefinedAttribute> generatePredefinedAttributes(int count){
		List<PredefinedAttribute> pAttributes = new ArrayList<PredefinedAttribute>();
		for (int i = 1; i <= count; i++){
			PredefinedAttribute pa = new PredefinedAttribute();
			pa.setValue("value_" + i);
			pa.setLabel("label_" + i);
			pAttributes.add(pa);
		}
		return pAttributes;
	}
}
