package com.ni3.ag.navigator.server.session;

import com.ni3.ag.navigator.shared.domain.User;

public class ThreadLocalStorageImpl implements ThreadLocalStorage{
	private ThreadLocal<User> currentUser = new ThreadLocal<User>();

	public void setCurrentUser(User user){
		currentUser.set(user);
	}

	public User getCurrentUser(){
		return currentUser.get();
	}

	public void removeCurrentUser(){
		currentUser.remove();
	}
}
