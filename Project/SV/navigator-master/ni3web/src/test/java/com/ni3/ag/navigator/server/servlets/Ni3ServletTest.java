package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.domain.DeltaHeader;

public class Ni3ServletTest extends TestCase{

	public void setUp(){
		NSpringFactory.init();
	}

	public void testAbstractMethodSequenceInDoPost(){
		final StringBuilder actual = new StringBuilder();
		final String expected = "1234";
		Ni3Servlet ni3Servlet = new Ni3Servlet(){
			private static final long serialVersionUID = 1L;

			@Override
			void createUserActivityLog(HttpServletRequest request){
				actual.append("2");

			}

			@Override
			protected DeltaHeader getTransactionDeltaForRequest(){
				actual.append("3");
				return DeltaHeader.DO_NOTHING;
			}

			@Override
			protected void doInternalPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			        IOException{
				actual.append("1");

			}

			@Override
			protected List<LogParam> getActivityParams(){
				actual.append("2");
				return null;
			}

			protected void doAfterPost(HttpServletRequest request){
				actual.append("4");
			}

			void createLog(HttpServletRequest request){
				getActivityParams();
			}

			@Override
			protected UserActivityType getActivityType(){
				return null;
			}
		};

		try{
			ni3Servlet.doPost(null, new HttpServletResponse(){

				@Override
				public void setLocale(Locale arg0){
				}

				@Override
				public void setContentType(String arg0){
				}

				@Override
				public void setContentLength(int arg0){
				}

				@Override
				public void setCharacterEncoding(String arg0){
				}

				@Override
				public void setBufferSize(int arg0){
				}

				@Override
				public void resetBuffer(){
				}

				@Override
				public void reset(){
				}

				@Override
				public boolean isCommitted(){
					return false;
				}

				@Override
				public PrintWriter getWriter() throws IOException{
					return null;
				}

				@Override
				public ServletOutputStream getOutputStream() throws IOException{
					return null;
				}

				@Override
				public Locale getLocale(){
					return null;
				}

				@Override
				public String getContentType(){
					return null;
				}

				@Override
				public String getCharacterEncoding(){
					return null;
				}

				@Override
				public int getBufferSize(){
					return 0;
				}

				@Override
				public void flushBuffer() throws IOException{
				}

				@Override
				public void setStatus(int arg0, String arg1){
				}

				@Override
				public void setStatus(int arg0){
				}

				@Override
				public void setIntHeader(String arg0, int arg1){
				}

				@Override
				public void setHeader(String arg0, String arg1){
				}

				@Override
				public void setDateHeader(String arg0, long arg1){
				}

				@Override
				public void sendRedirect(String arg0) throws IOException{
				}

				@Override
				public void sendError(int arg0, String arg1) throws IOException{
				}

				@Override
				public void sendError(int arg0) throws IOException{
				}

				@Override
				public String encodeUrl(String arg0){
					return null;
				}

				@Override
				public String encodeURL(String arg0){
					return null;
				}

				@Override
				public String encodeRedirectUrl(String arg0){
					return null;
				}

				@Override
				public String encodeRedirectURL(String arg0){
					return null;
				}

				@Override
				public boolean containsHeader(String arg0){
					return false;
				}

				@Override
				public void addIntHeader(String arg0, int arg1){
				}

				@Override
				public void addHeader(String arg0, String arg1){
				}

				@Override
				public void addDateHeader(String arg0, long arg1){
				}

				@Override
				public void addCookie(Cookie arg0){
				}
			});
		} catch (ServletException e){
			fail();
		} catch (IOException e){
			fail();
		}
		assertEquals(expected, actual.toString());

	}

}
