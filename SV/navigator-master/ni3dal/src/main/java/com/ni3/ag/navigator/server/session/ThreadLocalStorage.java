package com.ni3.ag.navigator.server.session;

import com.ni3.ag.navigator.shared.domain.User;

//TODO implement a storage that would cache users and not go to the database every time. Please note that Admin Console cache invalidation will then have to invalidate this cache as well 
public interface ThreadLocalStorage{

	public void setCurrentUser(User user);

	public User getCurrentUser();

	public void removeCurrentUser();

}
