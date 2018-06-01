/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.remoting;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import javax.swing.SwingUtilities;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.controller.LoginController;
import com.ni3.ag.adminconsole.client.gui.MainPanel2;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.AbstractView;
import com.ni3.ag.adminconsole.client.view.ErrorRenderer;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.remoting.TransferConstants;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;

public class ACHttpInvokerProxyFactoryBean extends HttpInvokerProxyFactoryBean{

	private final static Logger log = Logger.getLogger(ACHttpInvokerProxyFactoryBean.class);

	private AbstractController controller;

	public void setController(AbstractController controller){
		this.controller = controller;
	}

	@Override
	protected RemoteInvocationResult executeRequest(RemoteInvocation invocation, MethodInvocation originalInvocation)
	        throws Exception{
		return checkExecuteRequest(invocation, originalInvocation);
	}

	void setAttributes(RemoteInvocation invocation){
		SessionData sessionData = SessionData.getInstance();
		Map<?, ?> attrs = invocation.getAttributes();
		if (attrs != null)
			attrs.clear();
		if (sessionData.getCurrentDatabaseInstance() != null && sessionData.getCurrentDatabaseInstance().isInited())
			invocation.addAttribute(TransferConstants.DB_INSTANCE_ID, sessionData.getCurrentDatabaseInstanceId());
		invocation.addAttribute(TransferConstants.SESSION_ID, sessionData.getSessionId());
		invocation.addAttribute(TransferConstants.USER_ID, sessionData.getUserId());
	}

	private void forwardToSchemaAdmin(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				MainPanel2.forwardToSchemaAdmin(true);
			}
		});
	}

	void showConcurrencyError(){
		if (getController() == null)
			log.warn("can not find error panel to render concurency errors!");
		Component view = getController().getView();
		if (!(view instanceof AbstractView) || !(view instanceof ErrorRenderer))
			log.warn("can not find error panel to render concurency errors!");
		ErrorRenderer er = (ErrorRenderer) view;
		List<ErrorEntry> errors = new ArrayList<ErrorEntry>();
		errors.add(new ErrorEntry(TextID.MsgPleaseRefresh));
		er.renderErrors(errors);
		getController().reloadData();
	}

	private RemoteInvocationResult checkExecuteRequest(RemoteInvocation invocation, MethodInvocation originalInvocation)
	        throws Exception{
		setAttributes(invocation);
		RemoteInvocationResult result = super.executeRequest(invocation, originalInvocation);

		if (result.getValue() instanceof ErrorEntry){
			ErrorEntry ee = (ErrorEntry) result.getValue();

			if (ee.getId().equals(TextID.MsgPleaseRefresh)){
				showConcurrencyError();
				result = new RemoteInvocationResult(null);
			} else{

				SessionData sdata = SessionData.getInstance();

				LoginController loginController = (LoginController) ACSpringFactory.getInstance().getBean("loginController");
				loginController.reportError(ee);
				loginController.run();

				DatabaseInstance current = sdata.getCurrentDatabaseInstance();
				if (!loginController.isSuccess()){
					sdata.setDatabaseInstanceConnected(current, false);
					forwardToSchemaAdmin();
					throw new ACException(ee.getId());
				} else{
					sdata.setDatabaseInstanceConnected(current, true);
					result = checkExecuteRequest(invocation, originalInvocation);
				}
			}

		}
		return result;
	}

	public AbstractController getController(){
		return controller;
	}
}
