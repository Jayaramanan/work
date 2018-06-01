package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.services.DynamicAttributeService;
import com.ni3.ag.navigator.server.session.ThreadLocalStorage;
import com.ni3.ag.navigator.shared.domain.DBObject;
import com.ni3.ag.navigator.shared.domain.DynamicAttributeDescriptor;
import com.ni3.ag.navigator.shared.domain.User;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;

public class DynamicAttributesServlet extends Ni3Servlet{
	@Override
	protected void doInternalPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException{
		NRequest.CalculateDynamicAttributes request =
				NRequest.CalculateDynamicAttributes.parseFrom(httpRequest.getInputStream());
		NResponse.Envelope.Builder response = NResponse.Envelope.newBuilder();
		handleCalculateRequest(request, response);
		response.setStatus(NResponse.Envelope.Status.SUCCESS);
		response.build().writeTo(httpResponse.getOutputStream());
	}

	private void handleCalculateRequest(NRequest.CalculateDynamicAttributes request, NResponse.Envelope.Builder response){
		ThreadLocalStorage localStorage = NSpringFactory.getInstance().getThreadLocalStorage();
		User currentUser = localStorage.getCurrentUser();
		DynamicAttributeService dynamicAttributeService = NSpringFactory.getInstance().getDynamicAttributeService();
		List<DBObject> results = new ArrayList<DBObject>();
		for (NRequest.DynamicAttribute protoDynAttr : request.getDynamicAttributeList()){
			DynamicAttributeDescriptor dad = new DynamicAttributeDescriptor();
			dad.setFromAttribute(protoDynAttr.getFromAttribute());
			dad.setFromEntity(protoDynAttr.getFromEntity());
			dad.setOperation(protoDynAttr.getOperation());
			dad.setIds(protoDynAttr.getIdList());
			dad.setSchema(protoDynAttr.getSchemaId());
			dad.setFakeId(protoDynAttr.getFakeId());
			results.addAll(dynamicAttributeService.getDynamicValues(currentUser, dad));
		}

		NResponse.SimpleSearch.Builder builder = NResponse.SimpleSearch.newBuilder();
		for (DBObject obj : results){
			NResponse.DBObject.Builder protoObject = NResponse.DBObject.newBuilder();
			protoObject.setId(obj.getId());
			protoObject.setEntityId(obj.getEntityId());
			Map<Integer, String> data = obj.getData();
			for (int attrId : data.keySet())
				protoObject.addDataPair(NResponse.DataPair.newBuilder().setAttributeId(attrId)
						.setValue(data.get(attrId)));
			builder.addObject(protoObject);
		}
		response.setPayload(builder.build().toByteString());
	}

	@Override
	protected UserActivityType getActivityType(){
		return null;
	}

	@Override
	protected List<LogParam> getActivityParams(){
		return null;
	}
}
