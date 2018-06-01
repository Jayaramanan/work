package com.ni3.ag.navigator.server.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

import com.ni3.ag.navigator.shared.proto.NResponse;

public class ExceptionFilter implements Filter{
	private static Logger log = Logger.getLogger(ExceptionFilter.class);

	@Override
	public void destroy(){
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
	        throws IOException, ServletException{
		try{
			filterChain.doFilter(request, response);
		} catch (final Throwable e){
			log.error(e.getMessage(), e);
			if (!response.isCommitted()){
				response.resetBuffer();
			} else{
				log.error("HTTP Response is already commited, client will probably get gibberish");
			}

			final NResponse.Envelope.Builder builder = NResponse.Envelope.newBuilder();
			final NResponse.Envelope responseEnvelope = builder.setStatus(NResponse.Envelope.Status.FAILED)
			        .setErrorMessage(e.getMessage()).build();
			responseEnvelope.writeTo(response.getOutputStream());
		}
	}

	@Override
	public void init(final FilterConfig arg0) throws ServletException{
	}

}
