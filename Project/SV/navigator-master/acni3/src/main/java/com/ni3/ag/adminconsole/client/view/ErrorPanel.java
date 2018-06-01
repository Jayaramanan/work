/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ni3.ag.adminconsole.client.util.EscapeChars;

public class ErrorPanel extends JPanel{
	private final static int MAX_ERROR_MSG_LEN = 500;
	private JLabel errorMsgLabel;

	public ErrorPanel(){
		super();
		errorMsgLabel = new ErrorLabel();
		add(errorMsgLabel);
	}

	public void setErrorLabelName(String name){
		errorMsgLabel.setName(name);
	}

	public void setErrorMessages(List<String> messages){
		errorMsgLabel.setText(createMessageText(messages));
	}

	public String createMessageText(List<String> messages){
		if (messages == null || messages.isEmpty()){
			return "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<html><body>");
		for (String msg : messages){
			sb.append(EscapeChars.forHTML(msg)).append("<br>");
			if (sb.length() > MAX_ERROR_MSG_LEN){
				sb.append("...<br>");
				break;
			}
		}
		sb.append("</body></html>");
		return sb.toString();
	}

	public void clearErrorMessage(){
		errorMsgLabel.setText("");
	}

	public class ErrorLabel extends JLabel{

		public ErrorLabel(){
			super();
			setForeground(Color.RED);
			setFont(new Font(getFont().getName(), Font.BOLD, 14));
		}
	}
}
