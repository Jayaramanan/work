package com.ni3.ag.navigator.server.dao.mock;

import com.ni3.ag.navigator.shared.domain.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.server.dao.UserDAO;

public class UserDAOMock implements UserDAO{

	private Map<Integer, User> users = new HashMap<Integer, User>();
	private Map<String, User> sidUsers = new HashMap<String, User>();
	private Map<String, User> ssoUsers = new HashMap<String, User>();

	public UserDAOMock(){
		reset();
	}

	private void reset(){

		User u = new User();
		u.setId(1);
		u.setPassword("fakeuserpass");
		u.setSID("fakesid");
		u.setActive(true);
		u.setHasOfflineClient(true);
		u.setUserName("u1");

		users.put(u.getId(), u);
		sidUsers.put(u.getSID(), u);
		ssoUsers.put(u.getUserName(), u);

		u = new User();
		u.setId(2);
		u.setPassword("fakeuserpass");
		u.setSID("fakesidINACTIVE");
		u.setUserName("u2");
		u.setPassword("u2");
		u.setHasOfflineClient(true);

		users.put(u.getId(), u);
		sidUsers.put(u.getSID(), u);
		ssoUsers.put(u.getUserName(), u);

		u = new User();
		u.setId(3);
		u.setPassword("fakeuserpass3");
		u.setSID("fakesid3");
		u.setUserName("u3");
		u.setPassword("u3");

		users.put(u.getId(), u);
		sidUsers.put(u.getSID(), u);
		ssoUsers.put(u.getUserName(), u);

		u = new User();
		u.setId(4);
		u.setSID("fakesid4");
		u.setUserName("u4");
		u.setPassword("u4");
		u.setActive(true);

		users.put(u.getId(), u);
		sidUsers.put(u.getSID(), u);
		ssoUsers.put(u.getUserName(), u);

		u = new User();
		u.setId(5);
		u.setSID("fakesid4");
		u.setUserName("u5");
		u.setPassword("u5");
		u.setActive(true);
		u.seteMail("a@b.c");

		users.put(u.getId(), u);
		sidUsers.put(u.getSID(), u);
		ssoUsers.put(u.getUserName(), u);

	}

	@Override
	public List<User> findByEmail(final String email){
		final List<User> result = new ArrayList<User>();
		for (final User user : users.values()){
			if (user.geteMail().toLowerCase().equals(email)){
				result.add(user);
			}
		}
		return result;
	}

	@Override
	public void update(User user){
		// To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public User get(final Integer id){
		return users.get(id);
	}

	@Override
	public User getBySID(final String sid){
		return sidUsers.get(sid);
	}

	@Override
	public User getByUsername(final String SSO){
		return ssoUsers.get(SSO);
	}

	@Override
	public User getByUsernamePassword(final String username, final String password){
		reset();
		if (username == null){
			return null;
		}
		if (password == null){
			return null;
		}
		for (final User u : users.values()){
			if (username.equals(u.getUserName()) && password.equals(u.getPassword())){
				return u;
			}
		}
		return null;
	}

	@Override
	public void save(final User user){
		users.put(user.getId(), user);
	}

	@Override
	public List<User> getUsers(){
		return Collections.unmodifiableList(new ArrayList<User>(users.values()));
	}

	@Override
	public List<User> getOfflineUsers(){
		final List<User> result = new ArrayList<User>();
		for (final User u : users.values()){
			if (u.getHasOfflineClient() != null && u.getHasOfflineClient()){
				result.add(u);
			}
		}
		return result;
	}

}
