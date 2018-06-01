package com.ni3.ag.navigator.server.servlets.mocks;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

public class ServletOutputStreamImpl extends ServletOutputStream{

	@Override
	public void write(int b) throws IOException{
		throw new UnsupportedOperationException();
	}

}
