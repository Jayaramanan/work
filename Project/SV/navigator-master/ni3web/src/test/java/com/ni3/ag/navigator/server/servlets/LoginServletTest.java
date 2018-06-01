package com.ni3.ag.navigator.server.servlets;

import javax.servlet.http.HttpServletRequest;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.UserDAO;
import com.ni3.ag.navigator.server.domain.Group;
import com.ni3.ag.navigator.shared.domain.User;
import com.ni3.ag.navigator.shared.proto.NResponse.Login.Builder;
import junit.framework.TestCase;

public class LoginServletTest extends TestCase{
	private static NSpringFactory daoFactory;
	static {
		NSpringFactory.init();
		daoFactory = NSpringFactory.getInstance();
	}

	@SuppressWarnings("serial")
	private final class LoginServletExtension extends LoginServlet{
		private final SessionHolder holder;

		private LoginServletExtension(final SessionHolder holder){
			this.holder = holder;
		}

		@Override
		void fillUserInResponse(final Builder loginResponse, final User user, final Group g){
		}

		@Override
		String registerUserSession(final HttpServletRequest request, final boolean isSID, final User user){
			holder.isSid = isSID;
			holder.user = user;

			return "test-session-id";
		}
	}

	private class SessionHolder{
		Boolean isSid;
		User user;
	}

	@Override
	protected void setUp() throws Exception{
	}

	public void testprocessChangePassword(){
		final LoginServlet servlet = new LoginServlet();
		assertFalse(servlet.processChangePassword(1, "", ""));

		assertTrue(servlet.processChangePassword(1, "fakeuserpass", "newpass"));
		final UserDAO userDao = daoFactory.getUserDao();
		final User user = userDao.get(1);
		assertEquals("newpass", user.getPassword());
	}

	public void testprocessLoginWithPassword(){
		final SessionHolder holder = new SessionHolder();
		final LoginServlet servlet = new LoginServletExtension(holder);

		final String sessionId = servlet.processLoginWithUsernamePassword(null, null, "u1", "fakeuserpass");
		assertEquals("test-session-id", sessionId);
		assertEquals(Integer.valueOf(1), holder.user.getId());
		assertEquals(Boolean.FALSE, holder.isSid);
		assertEquals("u1", holder.user.getUserName());
		assertEquals("fakesid", holder.user.getSID());
	}

	public void testprocessLoginWithSID(){
		final SessionHolder holder = new SessionHolder();
		final LoginServlet servlet = new LoginServletExtension(holder);

		final String sessionId = servlet.processLoginWithSID(null, null, "fakesid");
		assertEquals("test-session-id", sessionId);
		assertEquals(Boolean.TRUE, holder.isSid);
		assertEquals(Integer.valueOf(1), holder.user.getId());
		assertEquals("u1", holder.user.getUserName());
	}

	public void testprocessLoginWithSSO(){
		final SessionHolder holder = new SessionHolder();
		final LoginServlet servlet = new LoginServletExtension(holder);

		final String sessionId = servlet.processLoginWithSSO(null, null, "1");
		assertEquals("test-session-id", sessionId);
		assertEquals(Boolean.TRUE, holder.isSid);
		assertEquals(Integer.valueOf(1), holder.user.getId());
		assertEquals("u1", holder.user.getUserName());
	}

}
