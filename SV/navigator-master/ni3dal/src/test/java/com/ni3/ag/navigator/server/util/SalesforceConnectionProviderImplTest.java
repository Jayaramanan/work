package com.ni3.ag.navigator.server.util;

import junit.framework.TestCase;

public class SalesforceConnectionProviderImplTest extends TestCase{
	public void testTest(){
//		SalesforceConnectionProviderImpl provider = new SalesforceConnectionProviderImpl();
//		PartnerConnection c = provider.getConnection();

		//Opportunity	-> 006F000000H2TE1IAN
		//Opportunity	-> 006F000000H2TDSIA3

		//Account		-> 001F000000mAS4hIAG

		//Contact		-> 003F000000ytyAHIAY

//		003F000000ytyAHIAY->001F000000mAS4hIAG
//		006F000000H2TDSIA3->001F000000mAS4hIAG
//		006F000000H2TE1IAN->001F000000mAS4hIAG
//		try{
//			QueryResult qr = c.query("SELECT Id, AccountId FROM Contact WHERE Id != null and AccountId != '001F000000mAS4hIAG'");
//			boolean done = false;
//			while (!done){
//				for (SObject obj : qr.getRecords()){
//					String opId = obj.getId();
//					String accId = (String) obj.getField("AccountId");
//					System.out.println(opId + "->" + accId);
////					System.out.println(obj.getId());
////					Iterator<XmlObject> it = obj.getChildren();
////					while(it.hasNext()){
////						XmlObject x = it.next();
////						System.out.println(x);
////					}
////					System.out.println(Arrays.toString(obj.getFieldsToNull()));
////					System.out.println(obj.getType());
////					System.out.println(obj.getName());
////					System.out.println(obj.getValue());
////					System.out.println(obj.getXmlType());
//
////					final XmlObject child = obj.getChild("Account");
////					if (child == null){
////						continue;
////					}
////					final String toIdStr = obj.getId();
////					final String fromIdStr = (String) child.getField("Id");
////					System.out.println(fromIdStr);
//				}
//				if (qr.isDone()){
//					done = true;
//				} else{
//					qr = c.queryMore(qr.getQueryLocator());
//				}
//			}
//		} catch (ConnectionException e){
//			e.printStackTrace();
//		}
	}
}
