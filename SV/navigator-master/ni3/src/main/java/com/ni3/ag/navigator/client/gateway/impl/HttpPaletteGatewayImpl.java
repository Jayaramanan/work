package com.ni3.ag.navigator.client.gateway.impl;

import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.client.gateway.PaletteGateway;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;
import java.io.IOException;

public class HttpPaletteGatewayImpl extends AbstractGatewayImpl implements PaletteGateway{
	@Override
	public NResponse.Palette getPalette(int paletteID){
		NRequest.Palette request = NRequest.Palette.newBuilder().setAction(NRequest.Palette.Action.GET_PALETTE)
		        .setId(paletteID).build();
        try{
            ByteString payload = sendRequest(ServletName.PaletteServlet, request);
			return NResponse.Palette.parseFrom(payload);
		} catch (IOException ex){
            showErrorAndThrow("Error get palette by id " + paletteID, ex);
            return null;
		}
	}
}
