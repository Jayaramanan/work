/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.filters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ni3.ag.navigator.server.filters.util.GZipResponseWrapper;

public class GZipFilter implements Filter{
	private static final Logger log = Logger.getLogger(GZipFilter.class);

	@Override
	public void destroy(){
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException{
		if (req instanceof HttpServletRequest){
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;

			if (acceptsGZipping(request)){
				if (log.isDebugEnabled()){
					log.debug(request.getRequestURL() + " response should be compressed");
				}
				ByteArrayOutputStream compressed = new ByteArrayOutputStream();
				GZIPOutputStream gzout = new GZIPOutputStream(compressed);

				// Handle the request
				final GZipResponseWrapper wrapper = new GZipResponseWrapper(response, gzout);

				chain.doFilter(request, wrapper);

				wrapper.flush();
				gzout.close();

				byte[] compressedBytes = compressed.toByteArray();

				response.setHeader("Content-Encoding", "gzip");
				response.setContentLength(compressedBytes.length);

				response.getOutputStream().write(compressedBytes);
			} else{
				chain.doFilter(req, res);
				log.debug(request.getRequestURL() + " response not compressed");
			}
		}
	}

	/**
	 * Checks if request accepts compression.
	 */
	protected boolean acceptsGZipping(HttpServletRequest request){
		String ae = request.getHeader("accept-encoding");
		if (ae != null && ae.indexOf("gzip") != -1){
			return true;
		}
		return false;
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException{
	}

}
