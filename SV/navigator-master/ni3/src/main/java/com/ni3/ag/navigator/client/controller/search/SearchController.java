/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.controller.search;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Timer;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.domain.DBObjectComparator;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.domain.query.Order;
import com.ni3.ag.navigator.client.domain.query.Query;
import com.ni3.ag.navigator.client.domain.query.Section;
import com.ni3.ag.navigator.client.gateway.GraphGateway;
import com.ni3.ag.navigator.client.gateway.impl.HttpGraphGatewayImpl;
import com.ni3.ag.navigator.client.gui.MainPanel;
import com.ni3.ag.navigator.client.gui.Ni3;
import com.ni3.ag.navigator.client.gui.common.Ni3OptionPane;
import com.ni3.ag.navigator.client.gui.datalist.MatrixSortOrder;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.model.Ni3Document;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.shared.constants.QueryType;
import org.apache.log4j.Logger;

public class SearchController{
	private MainPanel mainFrame; // TODO remove references to MainPanel
	private Ni3Document doc;

	public SearchController(Ni3Document doc){
		this.doc = doc;
		// TODO remove
		mainFrame = SystemGlobals.MainFrame;
	}

	public void combineSearch(final Query query, final boolean newSearch, final boolean addToQueryList){
		long timeout = UserSettings.getLongAppletProperty("SearchCancelWindowShowTimeout", 5000);
		new Thread(new SearchThread(query, newSearch, addToQueryList, timeout, this)).start();
	}

	private void nodeSearch(final Query query, final boolean newSearch, final boolean addToQueryList){
		final List<DBObject> searchResult = doc.DB.combineSearchNodes(query, doc);

		if (searchResult == null){
			return;
		}

		if (newSearch){
			doc.clearSearchResult(false);

			if (addToQueryList){
				doc.clearGraph(true, true);
			}

			mainFrame.resetDisplayFilter();
		}

		if (addToQueryList){
			if (newSearch){
				doc.clearQueryStack();
			}

			doc.addToQueryStack(query);
		}

		if (searchResult.size() > 0){
			sortDbObjects(searchResult, query.getSections());
			mainFrame.itemsPanel.setSearchOrder(query);
			mainFrame.itemsPanel.setSearchResults(searchResult);
		} else if (addToQueryList){
			mainFrame.showNoResultWindow(MainPanel.NO_SEARCH_RESULT);
		}

		if (query.getCopyNToGraph() > 0){
			if (Math.min(query.getCopyNToGraph(), searchResult.size()) > doc.DB.getMaximumNodeCount()){
				mainFrame.stopAnimation(28);
				Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("Too many search result to copy on graph")
						+ "\n" + UserSettings.getWord("Too many search to copy line2"), UserSettings
						.getWord("Too many search result to copy title"), JOptionPane.INFORMATION_MESSAGE);
			} else{
				List<Integer> dbRoots = getNodeIdsToPutOnGraph(query, searchResult, query.getCopyNToGraph());
				GraphGateway graphGateway = new HttpGraphGatewayImpl();
				List<Node> nodes = graphGateway.getNodes(dbRoots, doc.SchemaID, doc.DB.getDataFilter());
				if (nodes == null){
					doc.undoredoManager.back();
				} else{
					doc.showSubgraph(nodes, true);
				}
			}
			doc.dispatchEvent(Ni3ItemListener.MSG_GraphDirty, Ni3ItemListener.SRC_Graph, null, null);
		}
	}

	private void sortDbObjects(List<DBObject> dbObjects, List<Section> sections){
		MatrixSortOrder so = new MatrixSortOrder();
		for (Section section : sections){
			List<Order> orders = section.getOrder();
			if (orders != null && !orders.isEmpty()){
				for (Order order : orders){
					so.addSort(-3, order.attr, order.ent.ID, order.asc);
				}
			}
		}
		if (!so.getSorts().isEmpty()){
			DBObjectComparator comparator = new DBObjectComparator(so);
			Collections.sort(dbObjects, comparator);
		}
	}

	List<Integer> getNodeIdsToPutOnGraph(Query query, List<DBObject> dbObjects, int count){
		List<Integer> result = new ArrayList<Integer>();
		for (Section section : query.getSections()){
			List<Integer> ids = getFirstNIds(section.getEnt().ID, dbObjects, count);
			result.addAll(ids);
		}
		return result;
	}

	List<Integer> getFirstNIds(int entityId, List<DBObject> dbObjects, int max){
		List<Integer> result = new ArrayList<Integer>();
		int count = 0;
		for (int i = 0; i < dbObjects.size() && count < max; i++){
			if (dbObjects.get(i).getEntity().ID == entityId){
				result.add(dbObjects.get(i).getId());
				count++;
			}
		}
		return result;
	}

	private List<Integer> networkSearch(final Query query, final boolean newSearch, final boolean addToQueryList){
		if (newSearch){
			doc.clearSearchResult(false);
			if (addToQueryList){
				doc.clearGraph(true, true);
			}
			mainFrame.resetDisplayFilter();
		}

		return doc.DB.combineSearchNetwork(query, doc);
	}

	private void processNetworkSearchResults(List<Integer> data, final Query query, final boolean newSearch,
			final boolean addToQueryList){
		if (addToQueryList){
			if (newSearch){
				doc.clearQueryStack();
			}

			doc.addToQueryStack(query);
		}

		if (data.size() > doc.DB.getMaximumNodeCount()){
			Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("Too many search result to copy on graph")
					+ "\n" + UserSettings.getWord("Too many search to copy line2"), UserSettings
					.getWord("Too many search result to copy title"), JOptionPane.INFORMATION_MESSAGE);
		} else if (data.size() > 0){
			mainFrame.commandPanel.filtersPanel.setOrphans(false);
			doc.filter.setNoOrphans(false);

			GraphGateway graphGateway = new HttpGraphGatewayImpl();
			List<Edge> edges = graphGateway.getEdges(data, doc.SchemaID, doc.DB.getDataFilter());
			if (edges == null){
				doc.undoredoManager.back();
			} else{
				doc.showSubgraph(edges, true);
			}
			mainFrame.itemsPanel.setSearchOrder(query);

			doc.dispatchEvent(Ni3ItemListener.MSG_GraphDirty, Ni3ItemListener.SRC_Graph, null, null);
		} else if (addToQueryList){
			mainFrame.showNoResultWindow(MainPanel.NO_SEARCH_RESULT);
		}
	}

	private static class SearchCancelDialog extends JDialog{
		private JButton cancelButton;
		private JLabel label;

		private SearchCancelDialog(JFrame mainF){
			super(mainF);
			init();
		}

		private SearchCancelDialog(){
			super();
			init();
			forceRepaint();
		}

		private void init(){
			cancelButton = new JButton(UserSettings.getWord("Cancel"));
			SpringLayout layout = new SpringLayout();
			setLayout(layout);

			layout.putConstraint(SpringLayout.NORTH, cancelButton, -35, SpringLayout.SOUTH, getContentPane());
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, cancelButton, 0, SpringLayout.HORIZONTAL_CENTER,
					getContentPane());
			getContentPane().add(cancelButton);

			label = new JLabel(UserSettings.getWord("MsgPerformSearchPressCancelToAbort"));
			layout.putConstraint(SpringLayout.NORTH, label, 15, SpringLayout.NORTH, getContentPane());
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, label, 0, SpringLayout.HORIZONTAL_CENTER, getContentPane());
			getContentPane().add(label);

			setModal(false);
			setTitle(UserSettings.getWord("Search"));
			setSize(400, 150);
			setAlwaysOnTop(true);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		}

		public void setCancelListener(AbstractAction abstractAction){
			cancelButton.addActionListener(abstractAction);
		}

		public void enableCancel(boolean flag){
			cancelButton.setEnabled(flag);
		}

		public void forceRepaint(){
			label.paintImmediately(label.getBounds());
			cancelButton.paintImmediately(cancelButton.getBounds());
		}
	}

	private static class SearchThread implements Runnable{
		private final Logger log = Logger.getLogger(SearchThread.class);
		private SearchController searchController;
		private Query query;
		private boolean newSearch;
		private boolean addToQueryList;
		private SearchCancelDialog cancelDialog;
		private Timer cancelWindowShowTimeoutTimer;
		private boolean canceled;
		private long cancelWindowShowTimeout;

		private SearchThread(Query query, boolean newSearch, boolean addToQueryList, long cancelWindowShowTimeout,
				SearchController searchController){
			this.query = query;
			this.newSearch = newSearch;
			this.addToQueryList = addToQueryList;
			this.cancelWindowShowTimeout = cancelWindowShowTimeout;
			this.searchController = searchController;

			cancelWindowShowTimeoutTimer = new Timer();
			if (Ni3.AppletMode)
				cancelDialog = new SearchCancelDialog();
			else
				cancelDialog = new SearchCancelDialog((JFrame) Ni3.mainF);
			cancelDialog.setCancelListener(new AbstractAction(){
				@Override
				public void actionPerformed(ActionEvent e){
					log.debug("Cancel button pressed");
					log.debug("Set state to canceled");
					canceled = true;
					log.debug("Hiding cancel window");
					hideCancelWindow();
					log.debug("Reenable main window");
					SearchThread.this.searchController.mainFrame.stopAnimation(0);
				}
			});
		}

		@Override
		public void run(){
			scheduleCancelWindowShow();
			searchController.mainFrame.startAnimation();

			if (newSearch){
				invoke(new Runnable(){
					@Override
					public void run(){
						searchController.doc.dispatchEvent(Ni3ItemListener.MSG_DynamicAttributesCleared,
								Ni3ItemListener.SRC_Unknown, null, null);
					}
				});
			}

			switch (query.getType()){
				case NODE: {
					invoke(new Runnable(){
						@Override
						public void run(){
							searchController.nodeSearch(query, newSearch, addToQueryList);
						}
					});
				}
					break;
				case LINKED_NODES:
				case NODE_WITH_CONNECTIONS:
					final List<Integer> data = searchController.networkSearch(query, newSearch, addToQueryList);
					if (canceled){
						log.debug("got search results, but state is canceled - ignoring");
						return;
					}
					cancelCancelWindowShow();
					log.debug("Got results of search - processing...");
					invoke(new Runnable(){
						@Override
						public void run(){
							searchController.processNetworkSearchResults(data, query, newSearch, addToQueryList);
						}
					});
					break;
			}

			log.debug("Canceling window");
			cancelCancelWindowShow();
			invoke(new Runnable(){
				@Override
				public void run(){
					searchController.mainFrame.itemsPanel.setToFirstNonEmpty();
					searchController.mainFrame.itemsPanel.showMatrix();
				}
			});
			log.debug("Reenabling main window");
			log.debug("Hiding search cancel window");
			hideCancelWindow();
			searchController.mainFrame.stopAnimation(0);
		}

		private void invoke(Runnable runnable){
			try{
				SwingUtilities.invokeAndWait(runnable);
			} catch (InterruptedException e){
				log.error("ERROR", e);
			} catch (InvocationTargetException e){
				log.error("ERROR", e);
			}
		}

		private void hideCancelWindow(){
			cancelDialog.setVisible(false);
		}

		private void cancelCancelWindowShow(){
			if (query.getType() == QueryType.NODE)
				return;
			cancelWindowShowTimeoutTimer.cancel();
			cancelDialog.enableCancel(false);
		}

		private void scheduleCancelWindowShow(){
			if (query.getType() == QueryType.NODE)
				return;
			cancelWindowShowTimeoutTimer.schedule(new TimerTask(){
				@Override
				public void run(){
					cancelDialog.enableCancel(true);
					cancelDialog.setVisible(true);
				}
			}, cancelWindowShowTimeout);
		}
	}
}
