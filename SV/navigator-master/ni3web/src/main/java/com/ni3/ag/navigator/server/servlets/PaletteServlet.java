package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.domain.Palette;
import com.ni3.ag.navigator.server.services.PaletteService;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;

public class PaletteServlet extends Ni3Servlet{

	@Override
	protected void doInternalPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	        IOException{
		InputStream is = request.getInputStream();
		NRequest.Palette protoRequest = NRequest.Palette.parseFrom(is);
		NResponse.Envelope.Builder responseBuilder = NResponse.Envelope.newBuilder();
		switch (protoRequest.getAction()){
			case GET_PALETTE:
				handleGetPalette(protoRequest, responseBuilder);
				break;
		}
		responseBuilder.setStatus(NResponse.Envelope.Status.SUCCESS);
		NResponse.Envelope env = responseBuilder.build();
		env.writeTo(response.getOutputStream());
	}

	private void handleGetPalette(NRequest.Palette protoRequest, NResponse.Envelope.Builder responseBuilder){
		NResponse.Palette.Builder builder = NResponse.Palette.newBuilder();
		PaletteService service = NSpringFactory.getInstance().getPaletteService();
		List<Palette> palettes = service.getPalettes(protoRequest.getId());
		for (Palette p : palettes)
			builder.addColors(NResponse.Color.newBuilder().setSequence(p.getSequence()).setColor(p.getColor()));
		responseBuilder.setPayload(builder.build().toByteString());
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
