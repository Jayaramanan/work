/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.util.EscapeChars;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.dto.ErrorEntry;

public class DefaultErrorRenderer{
	private final static int MAX_ERROR_MSG_LEN = 500;

	public String getMessageText(List<ErrorEntry> errors){
		List<String> msgs = new ArrayList<String>();
		for (int i = 0; i < errors.size(); i++){
			ErrorEntry err = errors.get(i);
			msgs.add(Translation.get(err.getId(), err.getErrors()));
		}
		return createMessageText(msgs);
	}

	private String createMessageText(List<String> messages){
		if (messages == null || messages.isEmpty()){
			return "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<html><body>");
		for (String msg : messages){
			sb.append(EscapeChars.forHTML(msg)).append("<br>");
			if (sb.length() > MAX_ERROR_MSG_LEN){
				sb.append("...<br>");
				break;
			}
		}
		sb.append("</body></html>");
		return sb.toString();
	}
}
