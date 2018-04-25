package com.ni3.ag.navigator.server.dao;

import com.ni3.ag.navigator.shared.domain.User;
import java.util.List;

public interface UserDAO{

	User get(Integer id);

	User getBySID(String sid);

	User getByUsername(String username);

	User getByUsernamePassword(String username, String password);

	List<User> findByEmail(String email);

	void update(User user);

	List<User> getUsers();

	void save(User u);

	List<User> getOfflineUsers();
}
