/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.domain;

import java.util.HashMap;
import java.util.Map;

import com.ni3.ag.navigator.shared.domain.DeltaType;

public class ActivityMessage{

	private static Map<DeltaType, String> messages;

	public static final String NAME_PARAM = "{name}";
	public static final String USER_PARAM = "{user}";
	public static final String FROM_PARAM = "{from}";
	public static final String TO_PARAM = "{to}";

	private static void initMessages(){
		messages = new HashMap<DeltaType, String>();
		messages.put(DeltaType.FAVORITE_CREATE, "MsgActivityCreateFavorite");
		messages.put(DeltaType.FAVORITE_DELETE, "MsgActivityDeleteFavorite");
		messages.put(DeltaType.FAVORITE_UPDATE, "MsgActivityUpdateFavorite");
		messages.put(DeltaType.FAVORITE_COPY, "MsgActivityCopyFavorite");

		messages.put(DeltaType.FAVORITE_FOLDER_CREATE, "MsgActivityCreateFavoriteFolder");
		messages.put(DeltaType.FAVORITE_FOLDER_DELETE, "MsgActivityDeleteFavoriteFolder");
		messages.put(DeltaType.FAVORITE_FOLDER_UPDATE, "MsgActivityUpdateFavoriteFolder");

		messages.put(DeltaType.NODE_CREATE, "MsgActivityCreateNode");
		messages.put(DeltaType.NODE_UPDATE, "MsgActivityUpdateNode");
		messages.put(DeltaType.EDGE_CREATE, "MsgActivityCreateEdge");
		messages.put(DeltaType.EDGE_UPDATE, "MsgActivityUpdateEdge");
		messages.put(DeltaType.OBJECT_DELETE, "MsgActivityDeleteObject");
		messages.put(DeltaType.NODE_MERGE, "MsgActivityMergeNode");
	}

	public static String getMessage(DeltaType type){
		if (messages == null){
			initMessages();
		}
		return messages.get(type);
	}

	/**
	 * @param deltaType
	 * @return
	 */
	public static String getShortMessage(DeltaType deltaType){
		String message = null;
		switch (deltaType){
			case EDGE_CREATE:
				message = "MsgActivityCreateEdgeShort";
				break;
			case EDGE_UPDATE:
				message = "MsgActivityUpdateEdgeShort";
				break;
			default:
				message = getMessage(deltaType);
				break;
		}
		return message;
	}
}
