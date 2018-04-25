/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.protobuf.ByteString;
import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.GroupDAO;
import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.server.domain.DeltaParam;
import com.ni3.ag.navigator.server.domain.DeltaParamIdentifier;
import com.ni3.ag.navigator.server.domain.Group;
import com.ni3.ag.navigator.server.services.ActivityStreamService;
import com.ni3.ag.navigator.server.session.ThreadLocalStorage;
import com.ni3.ag.navigator.shared.domain.User;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NRequest.ActivityStream;
import com.ni3.ag.navigator.shared.proto.NResponse;
import com.ni3.ag.navigator.shared.proto.NResponse.ActivityStream.Builder;
import org.apache.log4j.Logger;

public class ActivityStreamServlet extends Ni3Servlet{

	private static final Logger log = Logger.getLogger(ActivityStreamServlet.class);
	private static final long serialVersionUID = 1L;

	private ActivityStreamService activityStreamService = NSpringFactory.getInstance().getActivityStreamService();

	@Override
	protected void doInternalPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException{
		final ActivityStream activityRequest = NRequest.ActivityStream.parseFrom(request.getInputStream());
		final Builder activityResponse = NResponse.ActivityStream.newBuilder();

		final ThreadLocalStorage storage = NSpringFactory.getInstance().getThreadLocalStorage();
		final GroupDAO groupDAO = NSpringFactory.getInstance().getGroupDao();
		final Group group = groupDAO.getByUser(storage.getCurrentUser().getId());

		final List<DeltaHeader> deltas = activityStreamService.getLastDeltas(activityRequest.getCount(),
				activityRequest.getLastId(), activityRequest.getSchemaId(), group.getId());

		final List<NResponse.Activity> activities = new ArrayList<NResponse.Activity>();
		for (DeltaHeader delta : deltas){
			final Map<DeltaParamIdentifier, DeltaParam> params = delta.getDeltaParameters();
			final Integer objectId = activityStreamService.getObjectId(delta, params);
			if (objectId != null){
				final String name = activityStreamService.getObjectName(delta, objectId);
				final NResponse.Activity.Builder act = NResponse.Activity.newBuilder();
				act.setId(delta.getId());
				act.setTimestamp(delta.getTimestamp().getTime());
				act.setObjectId(objectId);
				act.setObjectName(name);
				act.setDeltaType(delta.getDeltaType().intValue());
				final User creator = delta.getCreator();
				final NResponse.User.Builder user = NResponse.User.newBuilder();
				user.setUserId(creator.getId());
				user.setFirstName(creator.getFirstName());
				user.setLastName(creator.getLastName());
				user.setUserName(creator.getUserName());
				act.setUser(user.build());
				activities.add(act.build());
			}
		}
		log.debug("Got last activities: " + activities.size());
		activityResponse.addAllActivities(activities);

		final ByteString payload = activityResponse.build().toByteString();
		final NResponse.Envelope.Builder envelope = NResponse.Envelope.newBuilder();
		envelope.setStatus(NResponse.Envelope.Status.SUCCESS);
		envelope.setPayload(payload);
		envelope.build().writeTo(response.getOutputStream());
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
