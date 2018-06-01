package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.LanguageDAO;
import com.ni3.ag.navigator.shared.domain.LanguageItem;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;

public class LanguageServlet extends Ni3Servlet{
	@Override
	protected void doInternalPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		InputStream is = request.getInputStream();
		NRequest.Language protoRequest = NRequest.Language.parseFrom(is);
		NResponse.Envelope.Builder responseBuilder = NResponse.Envelope.newBuilder();
		switch (protoRequest.getAction()){
			case GET_TRANSLATIONS:
				handleGetTranslations(protoRequest, responseBuilder);
				break;
		}
		responseBuilder.setStatus(NResponse.Envelope.Status.SUCCESS);
		responseBuilder.build().writeTo(response.getOutputStream());
	}

	private void handleGetTranslations(NRequest.Language protoRequest, NResponse.Envelope.Builder responseBuilder){
		LanguageDAO languageDAO = NSpringFactory.getInstance().getLanguageDAO();
		List<LanguageItem> items = languageDAO.getTranslations(protoRequest.getId());
		NResponse.LanguageItems.Builder itemsBuilder = NResponse.LanguageItems.newBuilder();
		for(LanguageItem li : items){
			itemsBuilder.addItems(NResponse.LanguageItem.newBuilder().setId(li.getId())
								 .setProperty(li.getProperty())
								 .setValue(li.getValue()));
		}
		responseBuilder.setPayload(itemsBuilder.build().toByteString());
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
