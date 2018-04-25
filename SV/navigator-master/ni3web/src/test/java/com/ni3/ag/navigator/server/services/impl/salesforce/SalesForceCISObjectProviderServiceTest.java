package com.ni3.ag.navigator.server.services.impl.salesforce;

import java.util.List;

import com.ni3.ag.navigator.server.cache.SrcIdToFakeIdCacheImpl;
import com.ni3.ag.navigator.server.dao.ObjectConnectionDAO;
import com.ni3.ag.navigator.server.domain.ObjectConnection;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.util.PrivateAccessor;
import junit.framework.TestCase;

public class SalesForceCISObjectProviderServiceTest extends TestCase{
	private SalesForceCISObjectProviderService service = new SalesForceCISObjectProviderService();
	private ObjectConnection oc = new ObjectConnection();

	@Override
	public void setUp() throws Exception{
		service.setObjectConnectionDAO(new ObjectConnectionDAO(){
			@Override
			public List<ObjectConnection> getObjectConnections(int schema){
				return null;
			}

			@Override
			public List<ObjectConnection> getConnectionForToType(int toEntityId){
				return null;
			}

			@Override
			public List<ObjectConnection> getConnectionForFromType(int fromEntityId){
				return null;
			}

			@Override
			public ObjectConnection getConnectionForFromToTypes(int fromType, int toType, int edgeType){
				return null;
			}
		});
		service.setSrcIdToIdCache(new SrcIdToFakeIdCacheImpl(){
			@Override
			public String getSrcId(Integer id){
				switch (id){
					case 1:
						return "srcid";
					case 2:
						return "006F000000H2TE1IAN";
				}
				return null;
			}
		});
	}

	public void testGenerateQuery(){
		ObjectDefinition fromObject = new ObjectDefinition();
		ObjectDefinition toObject = new ObjectDefinition();
		fromObject.setName("Account");
		toObject.setName("Contact");
		oc.setFromObject(fromObject);
		oc.setToObject(toObject);
		int id = 1;
		String expected = "SELECT Id, AccountId FROM Contact WHERE Id != null and AccountId = 'srcid'";
		String result = (String) PrivateAccessor.invokePrivateMethod(service, "generateQuery", oc, id, null);
		assertEquals(expected, result);

		expected = "SELECT Id, AccountId FROM Contact WHERE Id = 'srcid' and AccountId != null";
		result = (String) PrivateAccessor.invokePrivateMethod(service, "generateQuery", oc, null, id);
		assertEquals(expected, result);

		fromObject.setName("Account");
		toObject.setName("Opportunity");
		id = 2;
		expected = "SELECT Id, AccountId FROM Opportunity WHERE Id != null and AccountId = '006F000000H2TE1IAN'";
		result = (String) PrivateAccessor.invokePrivateMethod(service, "generateQuery", oc, id, null);
		assertEquals(expected, result);

		expected = "SELECT Id, AccountId FROM Opportunity WHERE Id = '006F000000H2TE1IAN' and AccountId != null";
		result = (String) PrivateAccessor.invokePrivateMethod(service, "generateQuery", oc, null, id);
		assertEquals(expected, result);
	}
}
