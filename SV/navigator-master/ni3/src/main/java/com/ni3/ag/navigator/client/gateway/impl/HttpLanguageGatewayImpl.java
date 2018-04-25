package com.ni3.ag.navigator.client.gateway.impl;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.client.gateway.LanguageGateway;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.domain.LanguageItem;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;

public class HttpLanguageGatewayImpl extends AbstractGatewayImpl implements LanguageGateway{
	@Override
	public List<LanguageItem> getTranslations(int languageID) {
        NRequest.Language request = NRequest.Language.newBuilder().setAction(NRequest.Language.Action.GET_TRANSLATIONS)
                .setId(languageID).build();
        try {
            ByteString payload = sendRequest(ServletName.LanguageServlet, request);
            NResponse.LanguageItems protoLanguageItems = NResponse.LanguageItems.parseFrom(payload);
            List<NResponse.LanguageItem> protoItems = protoLanguageItems.getItemsList();
            List<LanguageItem> items = new ArrayList<LanguageItem>();
            for (NResponse.LanguageItem pi : protoItems) {
                LanguageItem li = new LanguageItem();
                li.setId(pi.getId());
                li.setProperty(pi.getProperty());
                li.setValue(pi.getValue());
                items.add(li);
            }
            return items;
        } catch (IOException ex) {
            showErrorAndThrow("Error get languages from server (id = " + languageID + ")", ex);
            return null;
        }
    }
}
