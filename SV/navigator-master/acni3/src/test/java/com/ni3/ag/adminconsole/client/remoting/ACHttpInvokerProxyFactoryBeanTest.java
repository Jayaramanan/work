/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.remoting;

import java.awt.Component;
import java.util.List;

import org.springframework.remoting.support.RemoteInvocation;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.client.view.ViewMock;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.remoting.TransferConstants;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;

public class ACHttpInvokerProxyFactoryBeanTest extends ACTestCase{
	private ACHttpInvokerProxyFactoryBean bean;

	public void setUp(){
		bean = new ACHttpInvokerProxyFactoryBean();
		bean.setController(generateTestController());
	}

	public void testShowConcurrencyError(){
		bean.showConcurrencyError();
		ViewMock view = (ViewMock) bean.getController().getView();
		List<ErrorEntry> errors = view.getErrors();
		ErrorEntry error = new ErrorEntry(TextID.MsgPleaseRefresh);
		for (ErrorEntry ee : errors)
			assertEquals(error.getId(), ee.getId());
	}

	public void testSetAttributes(){
		RemoteInvocation ri = new RemoteInvocation();
		bean.setAttributes(ri);
		assertNull(ri.getAttribute(TransferConstants.DB_INSTANCE_ID));
		assertNull(ri.getAttribute(TransferConstants.SESSION_ID));
		assertNull(ri.getAttribute(TransferConstants.USER_ID));
		DatabaseInstance dbi = new DatabaseInstance("test_db");
		dbi.setInited(true);
		SessionData.getInstance().setCurrentDatabaseInstance(dbi);
		bean.setAttributes(ri);
		assertEquals(ri.getAttribute(TransferConstants.DB_INSTANCE_ID), "test_db");
		User u = new User();
		u.setId(1);
		SessionData.getInstance().setUser(u);
		bean.setAttributes(ri);
		assertEquals(ri.getAttribute(TransferConstants.USER_ID), u.getId());
		SessionData.getInstance().setSessionId("qwerty");
		bean.setAttributes(ri);
		assertEquals(ri.getAttribute(TransferConstants.SESSION_ID), "qwerty");
	}

	private AbstractController generateTestController(){
		return new AbstractController(){

			private ViewMock view = new ViewMock();

			@Override
			protected void populateDataToView(AbstractModel model, Component view){

			}

			@Override
			protected void populateDataToModel(AbstractModel model, Component view){

			}

			@Override
			protected void initializeListeners(AbstractModel model, Component view){

			}

			@Override
			public Component getView(){
				return view;
			}

			@Override
			public void setView(Component c){

			}

			@Override
			public AbstractModel getModel(){
				return null;
			}

			@Override
			public void setModel(AbstractModel m){

			}

			@Override
			public void reloadData(){

			}

			@Override
			public void clearData(){

			}

			@Override
			public boolean save(){
				return false;
			}

			@Override
			public void reloadCurrent(){

			}

		};
	}
}
