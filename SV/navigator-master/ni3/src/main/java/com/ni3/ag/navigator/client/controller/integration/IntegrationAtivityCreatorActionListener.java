/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.controller.integration;

import java.applet.AppletContext;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.gui.Ni3;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.model.Ni3Document;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.client.util.URLEx;
import com.ni3.ag.navigator.shared.constants.RequestParam;
import com.ni3.ag.navigator.shared.constants.ServletName;

public class IntegrationAtivityCreatorActionListener implements ActionListener{

	private static final String PLACEHOLDER_PARAM = "@text@";
	private static final String FUNCTION_PATH_PARAM = "siebelIntegrationJSFunctionName";
	private Node selectedNode;
	private Ni3Document doc;

	// 1 - focus on an object, 2 - create activity
	private int type = 1;

	public IntegrationAtivityCreatorActionListener(Node selectedNode, Ni3Document doc, int type){
		this.type = type;
		this.selectedNode = selectedNode;
		this.doc = doc;
	}

	public void actionPerformed(ActionEvent e){
		if (selectedNode == null)
			return;

		System.out.println("Selected node = " + selectedNode.ID);

		Ni3 theApp = SystemGlobals.theApp;
		if (theApp == null)
			return;

		AppletContext appletContext = theApp.getAppletContext();
		URL invokeUrl;
		String URL = theApp.getParameter(FUNCTION_PATH_PARAM);
		try{
			if (URL == null || URL.isEmpty()){
				System.out.println(FUNCTION_PATH_PARAM + "parameter is not specified in applet. ");
				System.out.println("Using default function name copyFromAppletToHtml('" + PLACEHOLDER_PARAM + "'");
				URL = "copyFromAppletToHtml('" + PLACEHOLDER_PARAM + "')";
			}

			URL = URL.replaceAll(PLACEHOLDER_PARAM, getParameterString());

			URL = "javascript:" + URL;
			System.out.println("Invoking " + URL);
			invokeUrl = new URL(null, URL, new URLStreamHandler(){
				@Override
				protected URLConnection openConnection(URL u) throws IOException{
					return null;
				}
			});
			appletContext.showDocument(invokeUrl);
		} catch (MalformedURLException e1){
			System.out.println("Could not invoke javascript " + URL + ". Please check " + FUNCTION_PATH_PARAM
			        + " property of the applet.");
			e1.printStackTrace();
		}
	}

	private String getParameterString(){
		/**
		 * 18:20:42,437 INFO MainPanel:378 - Received focus node id = 141 Selected node = 74 Object id list = Invoking
		 * javascript:copyFromAppletToHtml22('123test') Selected node = 281 Object id list = Invoking
		 * javascript:copyFromAppletToHtml22('123test') Selected node = 74 Object id list = 141,281,74
		 */
		List<DBObject> selected = doc.Subgraph.getSelected();
		if (!selected.isEmpty()){
			return type + "#" + getConvertedIdString(selected);
		} else{
			return type + "#" + getSrcIdById(selectedNode.ID);
		}
	}

	private String getConvertedIdString(List<DBObject> selected){
		String res = "";

		for (DBObject dbo : selected){
			String converted = getSrcIdById(dbo.getId());
			res += converted;
			res += ",";
		}
		if (res.endsWith(",")){
			res = res.substring(0, res.length() - 1);
		}
		return res;
	}

	private String getSrcIdById(int id){
		String str = SystemGlobals.ServerURL + ServletName.IdToSrcIdConvertionServlet.getUrl();
		URLEx url = new URLEx(str);
		//TODO: change to ID param
		url.addParam(RequestParam.SRCID, id);
		url.closeOutput(null);
		String line = url.readLine();
		url.close();
		return line;
	}

}
