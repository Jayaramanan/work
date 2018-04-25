/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.search;

import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;

import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.domain.cache.IconCache;
import com.ni3.ag.navigator.client.domain.query.Query;
import com.ni3.ag.navigator.client.domain.query.Section;
import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;
import com.ni3.ag.navigator.client.model.Ni3Document;
import com.ni3.ag.navigator.shared.constants.QueryType;

@SuppressWarnings("serial")
public class DlgCombineSearch extends Ni3Dialog implements ActionListener{

	private static final int RET_CANCEL = 0;
	public static final int RET_OK = 1;

	private Ni3Document doc;
	private EntitySearchPane dBPane;

	private List<Entity> entities;

	private int maxResults;

	private JTabbedPane tab;
	private JComboBox copyFirstCombo;
	private JComboBox maxResultsCombo;
	private JLabel maxPage, maxPage2;

	private int returnStatus = RET_CANCEL;

	private Object objs[] = { UserSettings.getWord("None"), UserSettings.getWord("All"), 10, 20, 50, 100 };
	private Integer maxRes[] = { 100, 500, 1000, 5000 };
	private boolean copyAllToGraph;

	public QueryType searchMode;
	public int copyFirstNToGraph;

	public DlgCombineSearch(Ni3Document doc, boolean copyAllToGraph){
		super();
		this.copyAllToGraph = copyAllToGraph;
		copyFirstNToGraph = 0;
		searchMode = QueryType.NODE;

		this.doc = doc;

		setSize(650, 450);

		setTitle(UserSettings.getWord("Advanced search"));

		initComponents();
	}

	private void initComponents(){

		JPanel split = new JPanel();
		split.setLayout(new BorderLayout());
		split.setPreferredSize(new Dimension(600, 850));

		getContentPane().setPreferredSize(new Dimension(600, 850));
		getContentPane().setLayout(new BorderLayout());

		JPanel typeOfSearch = new JPanel();
		typeOfSearch.setLayout(new BoxLayout(typeOfSearch, BoxLayout.X_AXIS));
		JButton btn;
		JToggleButton rbtn;

		ButtonGroup group = new ButtonGroup();

		rbtn = new JToggleButton(IconCache.getImageIcon(IconCache.SEARCH_SINGLE_NODE), true);
		rbtn.setToolTipText(UserSettings.getWord("SearchSingleNode"));
		rbtn.setActionCommand("SearchSingleNode");
		rbtn.addActionListener(this);
		typeOfSearch.add(rbtn);
		group.add(rbtn);

		rbtn = new JToggleButton(IconCache.getImageIcon(IconCache.SEARCH_LINKED_NODES));
		rbtn.setToolTipText(UserSettings.getWord("SearchLinkedNodes"));
		rbtn.setActionCommand("SearchLinkedNodes");
		rbtn.addActionListener(this);
		typeOfSearch.add(rbtn);
		group.add(rbtn);

		rbtn = new JToggleButton(IconCache.getImageIcon(IconCache.SEARCH_NODES_WITH_CONNECTIONS));
		rbtn.setToolTipText(UserSettings.getWord("SearchNodeWithConnections"));
		rbtn.setActionCommand("SearchNodeWithConnections");
		rbtn.addActionListener(this);
		typeOfSearch.add(rbtn);
		group.add(rbtn);

		maxPage = new JLabel("    " + UserSettings.getWord("Copy first") + "  ");
		typeOfSearch.add(maxPage);

		copyFirstCombo = new JComboBox(objs);
		copyFirstCombo.setName("CopyFirstComboBox");
		copyFirstCombo.setMaximumSize(new Dimension(50, 25));
		typeOfSearch.add(copyFirstCombo);
		if (copyAllToGraph)
			copyFirstCombo.setSelectedIndex(1);
		else
			copyFirstCombo.setSelectedIndex(0);

		maxPage2 = new JLabel(" " + UserSettings.getWord("results to Graph") + "  ");
		typeOfSearch.add(maxPage2);

		typeOfSearch.add(new JLabel("    " + UserSettings.getWord("Fetch first ") + "  "));
		maxResultsCombo = new JComboBox(maxRes);
		maxResultsCombo.setName("FetchFirstComboBox");
		maxResultsCombo.setMaximumSize(new Dimension(50, 25));
		maxResultsCombo.setSelectedIndex(3);
		typeOfSearch.add(maxResultsCombo);

		getContentPane().add(split, BorderLayout.CENTER);
		getContentPane().add(typeOfSearch, BorderLayout.NORTH);

		tab = new JTabbedPane();
		tab.setName("ObjectTabbedPane");

		split.add(tab, BorderLayout.CENTER);

		setMode1Pane();

		JPanel okcancel = new JPanel();

		btn = new JButton(UserSettings.getWord("OK"));
		okcancel.add(btn);

		ActionListener okListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				okButtonActionPerformed();
			}
		};
		btn.addActionListener(okListener);

		KeyStroke enterStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		getRootPane().registerKeyboardAction(okListener, enterStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

		btn = new JButton(UserSettings.getWord("Cancel"));
		okcancel.add(btn);

		ActionListener cancelListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				cancelButtonActionPerformed();
			}
		};
		btn.addActionListener(cancelListener);

		KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		getRootPane().registerKeyboardAction(cancelListener, escapeStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

		btn = new JButton(UserSettings.getWord("Clear"));
		okcancel.add(btn);
		btn.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				clearButtonActionPerformed();
			}
		});

		split.add(okcancel, BorderLayout.SOUTH);

	}

	private void setTabs(Entity... es){
		entities.clear();
		tab.removeAll();
		for (Entity e : es){
			entities.add(e);
			addEntity(e);
		}
	}

	private void addEntity(Entity ent){
		dBPane = new EntitySearchPane(doc);
		tab.add(ent.Name, dBPane);
		dBPane.setPreferredSize(new Dimension(50, 50));
		dBPane.makeAttributesPanel(ent, null);
	}

	private void updateCopyToGraph(){
		int index = copyFirstCombo.getSelectedIndex();
		switch (index){
			case 0:
				copyFirstNToGraph = 0;
				break;

			case 1:
				copyFirstNToGraph = 1000000;
				break;

			default:
				copyFirstNToGraph = (Integer) objs[index];
		}
	}

	private void updateMaxResults(){
		maxResults = maxRes[maxResultsCombo.getSelectedIndex()];
	}

	private void okButtonActionPerformed(){
		Query q = getQuery();

		if (q != null)
			doClose(RET_OK);
	}

	/** @return the return status of this dialog - one of RET_OK or RET_CANCEL */
	public int getReturnStatus(){
		return returnStatus;
	}

	private void cancelButtonActionPerformed(){
		doClose(RET_CANCEL);
	}

	public void clearInputs(){
		clearButtonActionPerformed();
	}

	private void clearButtonActionPerformed(){
		int n;
		int l = tab.getTabCount();
		for (n = 0; n < l; n++){
			EntitySearchPane db = (EntitySearchPane) tab.getComponentAt(n);
			db.clearPanel();
		}
	}

	public Query getQuery(){
		Query query = new Query("", doc.DB.schema);
		updateCopyToGraph();
		updateMaxResults();

		query.setType(searchMode);
		query.setCopyNToGraph(copyFirstNToGraph);
		query.setMaxResults(maxResults);

		int l = tab.getTabCount();
		for (int n = 0; n < l; n++){
			EntitySearchPane db = (EntitySearchPane) tab.getComponentAt(n);
			Section sec = db.getQuerySection();

			if (sec != null){
				query.add(sec);
			} else{
				return null;
			}
		}

		return query;
	}

	private void doClose(int retStatus){
		returnStatus = retStatus;
		setVisible(false);
		dispose();
	}

	@Override
	public void setVisible(boolean b){
		if (b){
			returnStatus = RET_CANCEL;
		}
		super.setVisible(b);
	}

	private void setMode1Pane(){
		searchMode = QueryType.NODE;

		entities = doc.DB.schema.getReadableNodes();

		tab.removeAll();

		for (Entity ent : entities){
			if (ent.CanRead && ent.isNode()){
				addEntity(ent);
			}
		}

		maxPage.setVisible(true);
		maxPage2.setVisible(true);
		copyFirstCombo.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e){
		if ("SearchSingleNode".equals(e.getActionCommand())){
			setMode1Pane();
		} else if ("SearchLinkedNodes".equals(e.getActionCommand())){
			DlgLinkedNodes dlg = new DlgLinkedNodes(doc, 2);

			Point pt = getLocation();
			pt.x += 30;
			pt.y += 30;
			dlg.setLocation(pt);
			dlg.setVisible(true);

			if (dlg.getReturnStatus() == RET_OK){
				setTabs(dlg.entity1, dlg.entity2, dlg.entity3);
				searchMode = QueryType.LINKED_NODES;

				maxPage.setVisible(false);
				maxPage2.setVisible(false);
				copyFirstCombo.setVisible(false);
			}
		} else if ("SearchNodeWithConnections".equals(e.getActionCommand())){
			DlgLinkedNodes dlg = new DlgLinkedNodes(doc, 3);

			Point pt = getLocation();
			pt.x += 30;
			pt.y += 30;
			dlg.setLocation(pt);
			dlg.setVisible(true);

			if (dlg.getReturnStatus() == RET_OK){
				setTabs(dlg.entity1, dlg.entity2);
				searchMode = QueryType.NODE_WITH_CONNECTIONS;

				maxPage.setVisible(false);
				maxPage2.setVisible(false);
				copyFirstCombo.setVisible(false);
			}
		}
	}

}
