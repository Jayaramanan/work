package com.ni3.ag.navigator.client.gui;

import javax.swing.*;

import com.ni3.ag.navigator.client.controller.LicenseValidator;
import com.ni3.ag.navigator.client.controller.ObjectPopupListener;
import com.ni3.ag.navigator.client.controller.integration.IntegrationAtivityCreatorActionListener;
import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.domain.Value;
import com.ni3.ag.navigator.client.gui.common.Ni3ClickableMenu;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.GraphObject;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.model.Ni3Document;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.shared.domain.UrlOperation;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ObjectPopupMenu extends JPopupMenu{
	private static final long serialVersionUID = -8468424298866405529L;
	private static final Logger log = Logger.getLogger(ObjectPopupMenu.class);
	private ObjectPopupListener listener;
	private Ni3Document doc;
	private LicenseValidator validator;

	public ObjectPopupMenu(ObjectPopupListener listener, Ni3Document doc){
		this.listener = listener;
		this.doc = doc;
		validator = LicenseValidator.getInstance();
	}

	public void createPopupMenuItems(GraphObject selectedObject){
		listener.setSelectedObject(selectedObject);
		removeAll();

		if (selectedObject instanceof Node && UserSettings.getBooleanAppletProperty("ContextMenu_Node_InUse", true)){
			createNodeContextMenu((Node) selectedObject);
			if (UserSettings.getBooleanAppletProperty("URL_In_ContextMenu_InUse", false)) {
				createURLOperationsMenu(this, selectedObject.Obj);
			}
		} else if (selectedObject instanceof Edge
				&& UserSettings.getBooleanAppletProperty("ContextMenu_Connection_InUse", true)){
			createEdgeContextMenu((Edge) selectedObject);
			if (UserSettings.getBooleanAppletProperty("URL_In_ContextMenu_InUse", false)) {
				createURLOperationsMenu(this, selectedObject.Obj);
			}
		}
	}

	public void createPopupMenuItems(GraphObject selectedObject, List<GraphObject> allNodes){
		listener.setSelectedObject(selectedObject);
		removeAll();

		if (selectedObject instanceof Node && UserSettings.getBooleanAppletProperty("ContextMenu_Node_InUse", true)){
			createNodeContextMenu((Node) selectedObject, allNodes);
			if (UserSettings.getBooleanAppletProperty("URL_In_ContextMenu_InUse", false)) {
				createURLOperationsMenu(this, selectedObject.Obj);
			}
		}
	}

	private void createEdgeContextMenu(Edge selectedEdge){
		JMenuItem item;
		if (UserSettings.getBooleanAppletProperty("ContextMenu_Details_InUse", true)){
			item = new JMenuItem(getWord("Details"));
			item.setActionCommand("Details");
			item.addActionListener(listener);
			add(item);
		}
		if (validator.isEdgeDataChangeEnabled() && selectedEdge.Obj.getEntity().CanUpdate
				&& (!selectedEdge.Obj.getEntity().isContextEdge() || selectedEdge.favoritesID == doc.getTopicID())
				&& UserSettings.getBooleanAppletProperty("ContextMenu_Connection_Edit_InUse", true)){
			item = new JMenuItem(UserSettings.getWord("Edit"));
			item.setActionCommand("Edit");
			item.addActionListener(listener);
			add(item);
		}

		if (validator.isEdgeDataChangeEnabled() && selectedEdge.Obj.getEntity().CanDelete
				&& (!selectedEdge.Obj.getEntity().isContextEdge() || selectedEdge.favoritesID == doc.getTopicID())
				&& UserSettings.getBooleanAppletProperty("ContextMenu_Connection_Delete_InUse", true)){
			item = new JMenuItem(UserSettings.getWord("Delete"));
			item.setActionCommand("Delete");
			item.addActionListener(listener);
			add(item);
		}

		if ((selectedEdge.Obj.getEntity().isContextEdge() && selectedEdge.favoritesID != doc.getTopicID())
				&& UserSettings.getBooleanAppletProperty("ContextMenu_Connection_JumpToTopic_InUse", true)){
			item = new JMenuItem(UserSettings.getWord("Open topic"));
			item.setActionCommand("JumpToTopic");
			item.addActionListener(listener);
			add(item);
		}
	}

	private Component[] createNodeContextMenu(Node selectedNode){
		return createNodeContextMenu(selectedNode, new ArrayList<GraphObject>());
	}

	private Component[] createNodeContextMenu(Node selectedNode, List<GraphObject> allNodes){
		JMenuItem item;
		if (UserSettings.getBooleanAppletProperty("ContextMenu_Details_InUse", true)){
			item = new JMenuItem(getWord("Details"));
			item.setActionCommand("Details");
			item.addActionListener(listener);
			add(item);
		}

		if (UserSettings.getBooleanAppletProperty("ContextMenu_Node_Remove_InUse", true)){
			item = new JMenuItem(getWord("Remove"));
			item.setActionCommand("Remove");
			item.addActionListener(listener);
			add(item);
		}

		if (validator.isNodeDataChangeEnabled()
				&& UserSettings.getBooleanAppletProperty("ContextMenu_Node_Delete_InUse", true)){
			if (selectedNode != null && selectedNode.status == 0 && selectedNode.Obj.getEntity().CanDelete){
				item = new JMenuItem(getWord("Delete"));
				item.setActionCommand("Delete");
				item.addActionListener(listener);
				add(item);
			}
		}

		if (validator.isNodeDataChangeEnabled()
				&& UserSettings.getBooleanAppletProperty("ContextMenu_Node_Edit_InUse", true)
				&& selectedNode.Obj.getEntity().CanUpdate){
			item = new JMenuItem(getWord("Edit"));
			item.setActionCommand("Edit");
			item.addActionListener(listener);
			add(item);
		}

		if (validator.isNodeDataChangeEnabled()
				&& UserSettings.getBooleanAppletProperty("ContextMenu_Node_Replicate_InUse", true)
				&& selectedNode.Obj.getEntity().CanUpdate){
			item = new JMenuItem(getWord("Replicate"));
			item.setActionCommand("Replicate");
			item.addActionListener(listener);
			add(item);
		}

		if (UserSettings.getBooleanAppletProperty("ContextMenu_Node_Refocus_InUse", true)
				|| UserSettings.getBooleanAppletProperty("ContextMenu_Node_RefocusAsIs_InUse", true)){
			JMenu refocusMenu = new Ni3ClickableMenu("Refocus", this);
			refocusMenu.addActionListener(listener);
			refocusMenu.setActionCommand("Refocus");

			if (UserSettings.getBooleanAppletProperty("ContextMenu_Node_Refocus_InUse", true)){
				item = new JMenuItem(getWord("Refocus 1 degree"));
				item.setActionCommand("Refocus");
				item.addActionListener(listener);
				refocusMenu.add(item);
			}

			if (UserSettings.getBooleanAppletProperty("ContextMenu_Node_RefocusAsIs_InUse", true)){
				item = new JMenuItem(getWord("Refocus as-is"));
				item.setActionCommand("RefocusAsIs");
				item.addActionListener(listener);
				refocusMenu.add(item);
			}

			add(refocusMenu);
		}

		if (UserSettings.getBooleanAppletProperty("ContextMenu_Node_ShowPolygon", true)){
			final JCheckBoxMenuItem item2 = new JCheckBoxMenuItem(getWord("Polygon"), doc.isPolygonNode(selectedNode.ID));
			item2.setActionCommand("Polygon");
			item2.addActionListener(listener);
			add(item2);
		}

		if (UserSettings.getBooleanAppletProperty("ContextMenu_Node_ShowPolyline", true)){
			final JCheckBoxMenuItem item2 = new JCheckBoxMenuItem(getWord("Polyline"), doc.isPolylineNode(selectedNode.ID));
			item2.setActionCommand("Polyline");
			item2.addActionListener(listener);
			add(item2);
		}

		if ((selectedNode.getExternalRelatives() > 0 || selectedNode.contractedRelativesCount > 0)){
			if (UserSettings.getBooleanAppletProperty("ContextMenu_Node_Expand_InUse", true)){
				item = new JMenuItem(getWord("Expand"));
				item.setActionCommand("Expand");
				item.addActionListener(listener);
				add(item);
			}

			if (UserSettings.getBooleanAppletProperty("ContextMenu_Node_SelectiveExpand_InUse", true)){
				item = new JMenuItem(getWord("Selective Expand"));
				item.setActionCommand("SelectiveExpand");
				item.addActionListener(listener);
				add(item);
			}
		}

		if ((selectedNode.isLeading() && selectedNode.inEdges.size() + selectedNode.outEdges.size() > 0)
				|| (!selectedNode.isLeading() && selectedNode.inEdges.size() + selectedNode.outEdges.size() > 1)
				|| selectedNode.isExpandedManualy()){
			if (UserSettings.getBooleanAppletProperty("ContextMenu_Node_Expand_InUse", true)){
				item = new JMenuItem(getWord("Contract"));
				item.setActionCommand("Contract");
				item.addActionListener(listener);
				add(item);
			}
		}

		if (SystemGlobals.isSiebelIntegrationModeEnabled){
			String activityLabel = getWord("Integration: Create a new activity");
			// empty string is a way to remove the whole item from the menu
			if (!activityLabel.isEmpty()){
				final JMenuItem siebelMenuItem = new JMenuItem(activityLabel);
				siebelMenuItem.addActionListener(new IntegrationAtivityCreatorActionListener(selectedNode, doc, 2));
				add(siebelMenuItem);
			}

			String focusLabel = getWord("Integration: Focus on an object");
			// empty string is a way to remove the whole item from the menu
			if (!focusLabel.isEmpty()){
				final JMenuItem siebelMenuItem2 = new JMenuItem(focusLabel);
				siebelMenuItem2.addActionListener(new IntegrationAtivityCreatorActionListener(selectedNode, doc, 1));
				add(siebelMenuItem2);
			}
		}
		// NAV-559
		if (validator.isNodeDataChangeEnabled()
				&& UserSettings.getBooleanAppletProperty("ContextMenu_Node_Asign_Image_InUse", true)){
			addSeparator();
			item = new JMenuItem(getWord("Assign Image"));
			item.setActionCommand("ImageSelector");
			item.addActionListener(listener);
			add(item);
		}

		if (doc.Subgraph.getGraphLayoutManager().options != null){
			boolean addSeparator = true;
			String s;
			ButtonGroup grp = new ButtonGroup();
			for (int n = 0; n < doc.Subgraph.getGraphLayoutManager().options.length; n++){
				if (doc.Subgraph.getGraphLayoutManager().isOptionEnabled(selectedNode, n)){
					if (addSeparator){
						addSeparator();
						addSeparator = false;
					}

					s = doc.Subgraph.getGraphLayoutManager().options[n];
					item = new JRadioButtonMenuItem(getWord(s), doc.Subgraph.getGraphLayoutManager().isOptionSelected(
							selectedNode, n));
					item.setActionCommand("*L" + s);
					item.addActionListener(listener);
					grp.add(item);
					add(item);
				}
			}
		}

		if (allNodes != null && allNodes.size() > 1){
			JMenu viewAllMenu = new JMenu(getWord("View All Nodes"));
			for (GraphObject o : allNodes){
				final Node oneOfAll = (Node) o;
				Image image = oneOfAll.Obj.getMetaphor().getIcon();
				String description = oneOfAll.Obj.toString();


				BufferedImage resized = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = resized.createGraphics();
				g.drawImage(image, 0, 0, 20, 20, null);

				ImageIcon imageIcon = new ImageIcon(resized);
				JMenuItem jMenuItem = new JMenu(description);
				jMenuItem.setIcon(imageIcon);
				//jMenuItem.setActionCommand("oneOfAll");
				ObjectPopupListener secondLevelListener = new ObjectPopupListener(listener.parentMP);
				secondLevelListener.setSelectedObject(oneOfAll);
				ObjectPopupMenu secondLevel = new ObjectPopupMenu(secondLevelListener, doc);
				Component[] comp = secondLevel.createNodeContextMenu(oneOfAll);
				for (Component c : comp){
					jMenuItem.add(c);
				}
				//jMenuItem.setModel(model);
//				jMenuItem.addActionListener(new ActionListener() {
//					@Override
//					public void actionPerformed(ActionEvent e) {

						//listener.parentMP.nodeEdit(oneOfAll);
//					}
//				});
				viewAllMenu.add(jMenuItem);

			}

			add(viewAllMenu);
		}
		return getComponents();
	}

	public void createURLOperationsMenu(final JPopupMenu popup, final DBObject obj){
		JMenuItem item;

		if (obj.getEntity().getUrlOperations().length > 0){
			popup.addSeparator();
		}

		JMenu parent = null;
		String level = "0";

		for (final UrlOperation url : obj.getEntity().getUrlOperations()){
			if (parent != null && url.getSort().length() == level.length()){
				if (level.length() > 1){
					parent = (JMenu) parent.getParent();
				} else{
					parent = null;
				}
			}

			if ("0".equals(url.getLabel())){
				if (parent != null){
					parent.addSeparator();
				} else{
					popup.addSeparator();
				}
			} else if ("0".equals(url.getUrl())){
				final JMenu parent2 = new JMenu(url.getLabel());

				level = url.getSort();

				if (parent != null){
					parent.add(parent2);
				} else{
					popup.add(parent2);
				}

				parent = parent2;
			} else{
				item = new JMenuItem(getWord(url.getLabel()));
				item.setActionCommand(resolveUrl(obj, url.getUrl()));
				item.addActionListener(listener);

				if (parent != null){
					parent.add(item);
				} else{
					popup.add(item);
				}
			}
		}

		final boolean first = true;
		for (final Attribute a : obj.getEntity().getReadableAttributes()){
			if (a.isURLAttribute()){
				final Object url = obj.getValue(a.ID);

				if (url == null)
					continue;
				if (first){
					popup.addSeparator();
				}

				if (a.multivalue){
					try{
						for (final Object s : (Object[]) url){
							String filtered;
							if (a.predefined)
								filtered = ((Value) s).getValue();
							else
								filtered = (String) s;
							splitUrls(popup, filtered);

						}
					} catch (final Exception e){
						log.error(e.getMessage(), e);
					}
				} else{
					String filtered;
					if (a.predefined)
						filtered = ((Value) url).getValue();
					else
						filtered = (String) url;
					if (filtered.length() > 0){
						splitUrls(popup, filtered);
					}
				}
			}
		}
	}

	private String resolveUrl(DBObject obj, String Url){
		for (final Attribute a : obj.getEntity().getReadableAttributes()){
			if (Url.contains("@" + a.ID + "@")){
				Url = Url.replaceAll("@" + a.ID + "@", (String) obj.getValue(a.ID));
			}
		}

		return Url;
	}

	private void splitUrls(final JPopupMenu popup, final String urlStr){
		JMenuItem item;
		String[] urls = urlStr.split(";");
		for (String u : urls){
			if (u != null && !u.isEmpty()){
				item = new JMenuItem(getWord(u.trim()));
				item.setActionCommand(u.trim());
				item.addActionListener(listener);
				popup.add(item);
			}
		}
	}

	private String getWord(String word){
		return UserSettings.getWord(word);
	}
}
