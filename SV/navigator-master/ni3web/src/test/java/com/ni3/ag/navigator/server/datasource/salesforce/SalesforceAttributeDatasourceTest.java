package com.ni3.ag.navigator.server.datasource.salesforce;

import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.search.AdvancedCriteria;
import com.ni3.ag.navigator.server.util.PrivateAccessor;
import junit.framework.TestCase;

public class SalesforceAttributeDatasourceTest extends TestCase{

	public void testMakeStringClause(){
		Attribute attribute = new Attribute();
		attribute.setName("attr1");

		String expected = "attr1 LIKE 'term'";
		assertEquals(expected, PrivateAccessor.invokePrivateMethod(new SalesforceAttributeDatasource(), "makeStringClause", attribute, new AdvancedCriteria.Section.Condition(1, "=", "term", false)));

		expected = "attr1 LIKE '%term%'";
		assertEquals(expected, PrivateAccessor.invokePrivateMethod(new SalesforceAttributeDatasource(), "makeStringClause", attribute, new AdvancedCriteria.Section.Condition(1, "~", "term", false)));

		expected = "(NOT attr1 LIKE '%term%')";
		assertEquals(expected, PrivateAccessor.invokePrivateMethod(new SalesforceAttributeDatasource(), "makeStringClause", attribute, new AdvancedCriteria.Section.Condition(1, "!~", "term", false)));

		expected = "(NOT attr1 LIKE 'term')";
		assertEquals(expected, PrivateAccessor.invokePrivateMethod(new SalesforceAttributeDatasource(), "makeStringClause", attribute, new AdvancedCriteria.Section.Condition(1, "<>", "term", false)));
	}
}
