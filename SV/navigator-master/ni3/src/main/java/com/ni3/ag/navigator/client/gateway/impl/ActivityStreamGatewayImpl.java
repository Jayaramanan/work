/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gateway.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.client.domain.Activity;
import com.ni3.ag.navigator.client.gateway.ActivityStreamGateway;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.domain.DeltaType;
import com.ni3.ag.navigator.shared.domain.User;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NRequest.ActivityStream;
import com.ni3.ag.navigator.shared.proto.NRequest.ActivityStream.Builder;
import com.ni3.ag.navigator.shared.proto.NResponse;

public class ActivityStreamGatewayImpl extends AbstractGatewayImpl implements ActivityStreamGateway{
	@Override
	public List<Activity> getLastActivities(int count, int schemaID, long lastId){
		NResponse.ActivityStream response = null;

		final Builder builder = NRequest.ActivityStream.newBuilder();
		builder.setCount(count);
		builder.setSchemaId(schemaID);
		builder.setLastId(lastId);

		final ActivityStream request = builder.build();
        try{
            ByteString payload = sendRequest(ServletName.ActivityStreamServlet, request);
			response = NResponse.ActivityStream.parseFrom(payload);
		} catch (IOException e){
            showErrorAndThrow("No connection to server", e);
		}

		List<Activity> activities = new ArrayList<Activity>();
		for (com.ni3.ag.navigator.shared.proto.NResponse.Activity act : response.getActivitiesList()){
			Activity activity = new Activity();
			activity.setId(act.getId());
			activity.setDateTime(new Date(act.getTimestamp()));
			activity.setObjectId(act.getObjectId());
			activity.setObjectName(act.getObjectName());
			activity.setDeltaType(DeltaType.getById(act.getDeltaType()));
			com.ni3.ag.navigator.shared.proto.NResponse.User aUser = act.getUser();
			User user = new User();
			user.setId(aUser.getUserId());
			user.setFirstName(aUser.getFirstName());
			user.setLastName(aUser.getLastName());
			user.setUserName(aUser.getLastName());
			activity.setUser(user);
			activities.add(activity);
		}
		return activities;
	}

}
