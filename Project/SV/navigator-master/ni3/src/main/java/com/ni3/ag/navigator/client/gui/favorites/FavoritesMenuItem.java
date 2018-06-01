/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.favorites;

import javax.swing.JRadioButtonMenuItem;

import com.ni3.ag.navigator.client.domain.Favorite;

public class FavoritesMenuItem extends JRadioButtonMenuItem{
	private static final long serialVersionUID = 7870539840535544293L;
	private Favorite favorite;

	public FavoritesMenuItem(Favorite favorite){
		super(favorite.getName(), favorite.getIcon());
		this.favorite = favorite;
	}

	public Favorite getFavorite(){
		return favorite;
	}

}
