/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ACButton extends JButton{

	private static final long serialVersionUID = 1L;

	public ACButton(Mnemonic key){
		super();
		super.setMnemonic(key.getKey());
	}

	public ACButton(Mnemonic key, Action a){
		super(a);
		super.setMnemonic(key.getKey());
	}

	public ACButton(Mnemonic key, Icon icon){
		super(icon);
		super.setMnemonic(key.getKey());
	}

	public ACButton(Mnemonic key, TextID id, Icon icon){
		super(Translation.get(id), icon);
		super.setMnemonic(key.getKey());
	}

	public ACButton(Mnemonic key, TextID id){
		super(Translation.get(id));
		super.setMnemonic(key.getKey());
	}

	public ACButton(){
		super();
	}

	public ACButton(Action a){
		super(a);
	}

	public ACButton(Icon icon){
		super(icon);
	}

	public ACButton(TextID id, Icon icon){
		super(Translation.get(id), icon);
	}

	public ACButton(TextID id){
		super(Translation.get(id));
	}

	public void setHotKey(Mnemonic key){
		super.setMnemonic(key.getKey());
	}

	@Override
	@Deprecated
	/*
	 * @deprecated Use setHotKey instead
	 */
	public void setMnemonic(char mnemonic){
		super.setMnemonic(mnemonic);
	}

	@Override
	@Deprecated
	/*
	 * @deprecated Use setHotKey instead
	 */
	public void setMnemonic(int mnemonic){
		super.setMnemonic(mnemonic);
	}

	public void setToolTipText(String text){
		Mnemonic m = Mnemonic.fromKey(getMnemonic());
		if (m != null)
			text += " ( " + m.toString() + " ) ";
		super.setToolTipText(text);
	}
}
