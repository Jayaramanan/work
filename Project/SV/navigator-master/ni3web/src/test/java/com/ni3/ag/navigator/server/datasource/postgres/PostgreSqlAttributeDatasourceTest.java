package com.ni3.ag.navigator.server.datasource.postgres;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.type.PredefinedType;
import com.ni3.ag.navigator.server.util.PrivateAccessor;
import junit.framework.TestCase;

public class PostgreSqlAttributeDatasourceTest extends TestCase{
	public void testGenerateRowMaxRowSumMaxRowSumMinSQL(){
		final PostgreSqlAttributeDatasource datasource = new PostgreSqlAttributeDatasource();
		datasource.setTableName("usr_referrals_kunden");
		final String expected = "select max(rowMax), max(rowSum), min(rowSum) from " +
			"(select greatest(region, col9, col10, col11) as rowMax, region+col9+col10+col11 as rowSum from (select coalesce(pa1.value::integer, 0) as region, coalesce(col9, 0) as col9, coalesce(col10, 0) as col10, coalesce(col11, 0) as col11 from usr_referrals_kunden " +
			"left join cht_predefinedattributes pa1 on pa1.id = region) as subselect) as subsubselect";

		final List<Attribute> attributes = new ArrayList<Attribute>();

		Attribute a = new Attribute();
		attributes.add(a);
		a.setPredefined(PredefinedType.Predefined);
		a.setName("region");

		a = new Attribute();
		attributes.add(a);
		a.setName("col9");

		a = new Attribute();
		attributes.add(a);
		a.setName("col10");

		a = new Attribute();
		attributes.add(a);
		a.setName("col11");

		final String result = (String) PrivateAccessor.invokePrivateMethod(datasource, "generateRowMaxRowSumMaxRowSumMinSQL", attributes);
		assertEquals(expected, result);
	}


}
