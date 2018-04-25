/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.ni3.ag.navigator.client.controller.favorites.FavoritesController;
import com.ni3.ag.navigator.client.domain.*;
import com.ni3.ag.navigator.client.gateway.ActivityStreamGateway;
import com.ni3.ag.navigator.client.gateway.GraphGateway;
import com.ni3.ag.navigator.client.gateway.impl.ActivityStreamGatewayImpl;
import com.ni3.ag.navigator.client.gateway.impl.HttpGraphGatewayImpl;
import com.ni3.ag.navigator.client.gui.ActivityStreamFrame;
import com.ni3.ag.navigator.client.gui.MainPanel;
import com.ni3.ag.navigator.client.gui.common.Ni3OptionPane;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.model.Ni3Document;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.client.util.URLEx;
import com.ni3.ag.navigator.shared.constants.RequestParam;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.domain.DeltaType;
import com.ni3.ag.navigator.shared.domain.User;

public class ActivityStreamManager{

	private static final String FAVORITE_PREFIX = "fv_";
	private static final String EDGE_PREFIX = "ed_";
	private static final String NODE_PREFIX = "nd_";
	private static final String SHOW_ON_STARTUP_PROPERTY = "ActivityStream_ShowOnStartup";
	private static final int count = 30;
	private static final DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");

	private final List<Activity> loadedActivities = new ArrayList<Activity>();
	private final ActivityStreamGateway gateway = new ActivityStreamGatewayImpl();
	private final MainPanel mainPanel;

	private ActivityStreamFrame dlg;
	private GraphGateway graphGateway;

	public ActivityStreamManager(MainPanel mainPanel){
		this.mainPanel = mainPanel;
		graphGateway = new HttpGraphGatewayImpl();

		dlg = new ActivityStreamFrame();
		dlg.addRefreshButtonListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				initData();
			}
		});
		dlg.addShowMoreButtonListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				loadMoreData();
			}
		});

		dlg.getHtmlPane().addHyperlinkListener(new HyperlinkListener(){
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e){
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
					if (showObject(e.getDescription())){
						dlg.setVisible(false);
					}
				}
			}
		});

		final boolean showOnStartup = UserSettings.getBooleanAppletProperty(SHOW_ON_STARTUP_PROPERTY, false);
		dlg.setShowOnStartup(showOnStartup);
		dlg.addShowOnStartupCheckboxListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e){
				if (e.getSource() instanceof JCheckBox){
					updateShowOnStartupSetting(((JCheckBox) e.getSource()).isSelected());
				}
			}
		});
	}

	private void loadMoreData(){
		long lastId = -1;
		if (!loadedActivities.isEmpty()){
			Activity last = loadedActivities.get(loadedActivities.size() - 1);
			lastId = last.getId();
		}
		List<Activity> activities = gateway.getLastActivities(count, mainPanel.Doc.SchemaID, lastId);
		fillActivityMessages(activities);
		loadedActivities.addAll(activities);
		updateHTML(false);
	}

	public void showActivityStream(){
		initData();
		dlg.showIt();
	}

	private void initData(){
		loadedActivities.clear();
		List<Activity> activities = gateway.getLastActivities(count, mainPanel.Doc.SchemaID, -1);
		fillActivityMessages(activities);
		loadedActivities.addAll(activities);
		updateHTML(true);
	}

	private void updateHTML(boolean scrollUp){
		String html = generateHtml(loadedActivities);
		dlg.getHtmlPane().setText(html);
		if (scrollUp)
			dlg.getHtmlPane().setCaretPosition(0);
	}

	private void fillActivityMessages(List<Activity> activities){
		for (Activity activity : activities){
			String message = getActivityMessage(activity);
			activity.setText(message);
		}
	}

	private String getActivityMessage(Activity activity){
		String message = null;
		String am = ActivityMessage.getMessage(activity.getDeltaType());
		if (am != null){
			message = UserSettings.getWord(am);

			if (DeltaType.EDGE_CREATE == activity.getDeltaType() || DeltaType.EDGE_UPDATE == activity.getDeltaType()){
				String[] names = getEdgeIdentifiers(activity);
				if (names[1] == null || names[2] == null){
					am = ActivityMessage.getShortMessage(activity.getDeltaType());
					message = UserSettings.getWord(am);
				} else{
					message = message.replace(ActivityMessage.FROM_PARAM, names[1]);
					message = message.replace(ActivityMessage.TO_PARAM, names[2]);
				}
				message = message.replace(ActivityMessage.NAME_PARAM, names[0]);
			} else{
				message = message.replace(ActivityMessage.NAME_PARAM, getObjectName(activity));
			}

			message = message.replace(ActivityMessage.USER_PARAM, getUserDisplayText(activity.getUser()));
		}
		return message;
	}

	private String getUserDisplayText(User user){
		String uText = "";
		if (user != null){
			uText += user.getFirstName();
			uText += " ";
			uText += user.getLastName();
		}
		return uText;
	}

	private String getObjectName(Activity activity){
		String name = null;
		switch (activity.getDeltaType()){
			case FAVORITE_CREATE:
			case FAVORITE_UPDATE:
			case FAVORITE_COPY:
				name = getFavoriteName(activity, true);
				break;
			case FAVORITE_FOLDER_CREATE:
			case FAVORITE_FOLDER_UPDATE:
				name = getFavoriteName(activity, false);
				break;
			case NODE_CREATE:
			case NODE_UPDATE:
			case NODE_MERGE:
				name = getNodeName(activity);
				break;
			default:
				break;
		}

		return name != null ? name.trim() : "";
	}

	private String getFavoriteName(Activity activity, boolean isFavorite){
		final String result;
		if (activity.getObjectName() != null && !activity.getObjectName().isEmpty()){
			if (isFavorite){
				result = getLinkName(activity.getObjectId(), activity.getObjectName(), FAVORITE_PREFIX);
			} else{
				result = activity.getObjectName();
			}
		} else{
			result = getDeletedLabel();
		}
		return result;
	}

	private String getNodeName(Activity activity){
		final int objectId = activity.getObjectId();
		String name;
		final DBObject dbObject = mainPanel.Doc.DB.getObject(objectId, false, true);
		if (dbObject != null){
			name = getLinkName(objectId, dbObject.getLabel(), NODE_PREFIX);
		} else{
			name = getDeletedLabel();
		}
		return name;
	}

	private String[] getEdgeIdentifiers(Activity activity){
		final int edgeId = activity.getObjectId();
		final String deletedLabel = getDeletedLabel();
		final DBObject edgeObject = mainPanel.Doc.DB.getObject(edgeId, false, true);

		String edgeName;
		String fromName = null;
		String toName = null;

		if (edgeObject != null){
			edgeName = getLinkName(edgeId, edgeObject.getLabel(), EDGE_PREFIX);
			Object fromValue = edgeObject.getValueByAttributeName(Attribute.FROMID_ATTRIBUTE_NAME);
			if (fromValue instanceof Integer){
				final DBObject fromObject = mainPanel.Doc.DB.getObject((Integer) fromValue, false, true);
				if (fromObject != null){
					fromName = getLinkName(fromObject.getId(), fromObject.getLabel(), NODE_PREFIX);
				}
			}
			Object toValue = edgeObject.getValueByAttributeName(Attribute.TOID_ATTRIBUTE_NAME);
			if (toValue instanceof Integer){
				final DBObject toObject = mainPanel.Doc.DB.getObject((Integer) toValue, false, true);
				if (toObject != null){
					toName = getLinkName(toObject.getId(), toObject.getLabel(), NODE_PREFIX);
				}
			}
		} else{
			edgeName = deletedLabel;
		}

		return new String[] { edgeName, fromName, toName };
	}

	private String getDeletedLabel(){
		return UserSettings.getWord("DeletedLabel").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

	private String getLinkName(int identifier, String name, String prefix){
		return "<a href=\"" + prefix + identifier + "\">" + name + "</a>";
	}

	private String generateHtml(List<Activity> activities){
		StringBuilder html = new StringBuilder();
		html.append("<html><body>");
		html.append("<table border=1 width=\"100%\">");
		for (Activity activity : activities){
			String time = dateFormat.format(activity.getDateTime());
			html.append("<tr>");
			html.append("<td>").append(time).append("</td>");
			html.append("<td>").append(activity.getText()).append("</td>");
			html.append("</tr>");
		}
		html.append("</table></body></html>");
		return html.toString();
	}

	private boolean showObject(String objStr){
		boolean result = true;
		if (objStr != null && objStr.length() > 3){
			int objectId = Integer.parseInt(objStr.substring(3));

			final Ni3Document doc = mainPanel.Doc;
			if (objStr.startsWith(EDGE_PREFIX)){
				final DBObject edge = doc.DB.getObject(objectId, false, false);
				if (edge == null){
					Ni3OptionPane.showMessageDialog(dlg, UserSettings.getWord("MsgEdgeWasDeleted"), UserSettings
							.getWord("Error"), JOptionPane.WARNING_MESSAGE);
					result = false;
				} else{
					Object fromValue = edge.getValueByAttributeName(Attribute.FROMID_ATTRIBUTE_NAME);
					Object toValue = edge.getValueByAttributeName(Attribute.FROMID_ATTRIBUTE_NAME);
					if (fromValue instanceof Integer && toValue instanceof Integer){
						List<Integer> list = new ArrayList<Integer>();
						list.add((Integer) fromValue);
						list.add((Integer) toValue);
						list.add(edge.getId());

						doc.clearGraph(true, true);

						List<Edge> edges = graphGateway.getEdges(list, doc.SchemaID, doc.DB.getDataFilter());
						if (edges != null){
							doc.showSubgraph(edges, true);
						}
					}
				}
			} else if (objStr.startsWith(NODE_PREFIX)){
				final DBObject dbObject = doc.DB.getObject(objectId, false, false);
				if (dbObject == null){
					Ni3OptionPane.showMessageDialog(dlg, UserSettings.getWord("MsgNodeWasDeleted"), UserSettings
							.getWord("Error"), JOptionPane.WARNING_MESSAGE);
					result = false;
				} else{
					List<Integer> list = new ArrayList<Integer>();
					list.add(objectId);

					doc.clearGraph(true, true);

					final List<Node> nodes = graphGateway.getNodes(list, doc.SchemaID, doc.DB.getDataFilter());

					if (nodes != null){
						doc.showSubgraph(nodes, true);
					}
				}
			} else if (objStr.startsWith(FAVORITE_PREFIX)){
				mainPanel.favoritesMenu.refreshFavorites();
				Favorite fav = doc.getFavoritesModel().getFavoriteByID(objectId);
				if (fav != null){
					new FavoritesController(doc).loadDocument(fav.getId(), doc.SchemaID);
				}
			}
		}
		return result;
	}

	protected void updateShowOnStartupSetting(boolean value){
		URLEx url = new URLEx(ServletName.SettingsProvider);
		url.addParam(RequestParam.Action, "createOrUpdate");
		url.addParam(RequestParam.P1, value);
		url.addParam(RequestParam.P2, SystemGlobals.getUserId());
		url.addParam(RequestParam.P3, SHOW_ON_STARTUP_PROPERTY);
		url.addParam(RequestParam.P4, "Applet");
		url.closeOutput(null);
		url.readLine();
		url.close();
	}
}
