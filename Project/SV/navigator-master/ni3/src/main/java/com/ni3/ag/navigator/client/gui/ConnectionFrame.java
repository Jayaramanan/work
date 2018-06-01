/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.apache.log4j.Logger;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.domain.Value;
import com.ni3.ag.navigator.client.domain.cache.IconCache;
import com.ni3.ag.navigator.client.gateway.GraphGateway;
import com.ni3.ag.navigator.client.gateway.ObjectManagementGateway;
import com.ni3.ag.navigator.client.gateway.impl.HttpGraphGatewayImpl;
import com.ni3.ag.navigator.client.gateway.impl.HttpObjectManagementGatewayImpl;
import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.model.Ni3Document;
import com.ni3.ag.navigator.client.util.SpringUtilities;

@SuppressWarnings("serial")
public class ConnectionFrame extends Ni3Dialog implements ActionListener{
	private static final Logger log = Logger.getLogger(ConnectionFrame.class);
	private static DBObject lastObj = null;

	private JComboBox type;
	private DBObjectPanel dBPane;

	private int cItems;
	private String items[] = new String[130];
	private int entities[] = new int[130];

	private Edge edge;

	private MainPanel parent;

	private int increment = 1;
	private int operation;

	private boolean locked;
	private boolean success = false;

	public Node fromID[];
	public Node toID[];

	public ConnectionFrame(MainPanel parent){
		locked = false;

		cItems = 0;
		this.parent = parent;
		edge = null;
		setTitle(UserSettings.getWord("Create connection"));
		operation = 1;

//		ImageIcon frameIcon = IconCache.getImageIcon("molecule.png");
//		if (frameIcon != null)
//			setIconImage(frameIcon.getImage());
	}

	public ConnectionFrame(MainPanel parent, Edge e){
		this(parent, e, false);
	}

	public ConnectionFrame(MainPanel parent, Edge e, boolean readOnly){
		setTitle(UserSettings.getWord("Edit connection"));
		operation = 2;

		if (readOnly){
			setTitle("Details");
		}

		locked = e.status != 0;

		cItems = 0;
		this.parent = parent;
		edge = e;

		fromID = new Node[1];
		fromID[0] = e.from;

		toID = new Node[1];
		toID[0] = e.to;

		initComponents(readOnly);

//		ImageIcon frameIcon = IconCache.getImageIcon("molecule.png");
//		if (frameIcon != null)
//			setIconImage(frameIcon.getImage());
	}

	void changeObjectType(Entity ent){
		FontMetrics fontMetrics = null;
		try{
			fontMetrics = getFontMetrics(getFont());
		} catch (Exception e){
			log.error("Error getting font metrics, font = " + getFont(), e);
		}
		dBPane.MakeAttributesPanel(ent, ent.getReadableAttributes(false), new CheckConnectionType(fromID, toID), false,
				operation, locked, fontMetrics, getGraphics());
		dBPane.setBorder(BorderFactory.createEmptyBorder());
		Dimension d = getSize();
		d.width += increment;
		increment *= -1;
		setSize(d);
		doLayout();
		repaint();
	}

	public boolean initComponents(boolean readOnly){
		int maxCount = 0;

		JLabel jLabel1 = new JLabel();
		type = new javax.swing.JComboBox();
		JButton buttonOK = new JButton();
		JButton buttonCancel = new JButton();
		JButton buttonClear = new JButton();

		if (edge == null)
			dBPane = new DBObjectPanel(null, parent.Doc, false);
		else{
			dBPane = new DBObjectPanel(edge.Obj, parent.Doc, edge.status != 0);
			changeObjectType(edge.Obj.getEntity());
			maxCount = dBPane.count;
		}

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		jLabel1.setText(UserSettings.getWord("ConnectionType"));

		boolean ret = false;

		if (edge == null){
			DefaultComboBoxModel CBModel = new DefaultComboBoxModel();
			int selIndex;

			selIndex = 0;
			for (Entity e1 : parent.Doc.DB.schema.definitions){
				if (parent.Doc.getTopicID() == 0 && e1.isContextEdge())
					continue;

				if ((e1.isEdge()) && ((operation == 1 && e1.CanCreate) || (operation == 2 && e1.CanUpdate))
						&& parent.Doc.DB.schema.edgeMetaphor.canCreate(fromID, toID, e1.ID)){
					ret = true;

					entities[cItems] = e1.ID;
					items[cItems] = e1.Name;

					CBModel.addElement(items[cItems]);

					if (edge != null){
						if (entities[cItems] == edge.Obj.getEntity().ID)
							selIndex = cItems;
					}

					int count = 0;
					for (Attribute a : e1.getReadableAttributes(false)){
						if (a.isDisplayableOnEdit(locked))
							count++;
					}

					if (count > maxCount)
						maxCount = count;

					cItems++;
				}
			}

			if (!ret)
				return false;

			maxCount = Math.max(10, maxCount);

			type.setModel(CBModel);

			type.addActionListener(new java.awt.event.ActionListener(){
				public void actionPerformed(java.awt.event.ActionEvent evt){
					int index = type.getSelectedIndex();

					if (index > -1){
						Entity ent = parent.Doc.DB.schema.getEntity(entities[index]);

						FontMetrics fontMetrics = null;
						try{
							fontMetrics = getFontMetrics(getFont());
						} catch (Exception e){
							log.error("Error getting font metrics, font = " + getFont(), e);
						}
						dBPane.MakeAttributesPanel(ent, ent.getReadableAttributes(), new CheckConnectionType(fromID, toID),
								false, operation, locked, fontMetrics, getGraphics());
						dBPane.setBorder(BorderFactory.createEmptyBorder());
						Dimension d = getSize();
						d.width += increment;
						increment *= -1;
						setSize(d);
						doLayout();
						repaint();
					}
				}
			});

			if (selIndex < cItems)
				type.setSelectedIndex(selIndex);

		}

		buttonOK.setText(UserSettings.getWord("OK"));
		buttonOK.addActionListener(this);
		buttonOK.setActionCommand("OK");

		buttonCancel.setText(UserSettings.getWord("Cancel"));
		buttonCancel.addActionListener(this);
		buttonCancel.setActionCommand("Cancel");

		buttonClear.setText(UserSettings.getWord("Clear"));
		buttonClear.addActionListener(this);
		buttonClear.setActionCommand("Clear");

		getContentPane().setLayout(new BorderLayout());

		if (edge == null){
			final JPanel panel1 = new JPanel();
			panel1.setPreferredSize(new Dimension(200, 40));
			panel1.setMinimumSize(new Dimension(200, 40));
			panel1.setLayout(new SpringLayout());

			type.setPreferredSize(new Dimension(100, 25));
			type.setMinimumSize(new Dimension(100, 25));

			JLabel jLabel0 = new JLabel(UserSettings.getWord("ConnectionObjectType"));

			if (cItems > 0){
				panel1.add(jLabel0);
				panel1.add(type);
				SpringUtilities.makeCompactGrid(panel1, 1, 2, // rows, cols
						6, 6, // initX, initY
						6, 6); // xPad, yPad
				getContentPane().add(panel1, BorderLayout.NORTH);
			}

		}

		{
			dBPane.setPreferredSize(new Dimension(350, (int) (maxCount * 25.3)));
			setMinimumSize(new Dimension(350, (int) (maxCount * 25.3)));
			setPreferredSize(new Dimension(350, 125 + (int) (maxCount * 25.3)));
			setSize(new Dimension(350, (int) (maxCount * 25.3)));
			getContentPane().add(dBPane, BorderLayout.CENTER);
		}

		{
			final JPanel panel1 = new JPanel();
			panel1.setPreferredSize(new Dimension(350, 40));
			panel1.setMinimumSize(new Dimension(350, 40));
			getContentPane().add(panel1, BorderLayout.SOUTH);

			if (!readOnly){
				panel1.add(buttonOK);
			}
			panel1.add(buttonCancel);

			if (!readOnly) {
				panel1.add(buttonClear);
			}
		}

		pack();

		return true;
	}

	private void onOK(){
		onEnterAction();
	}

	protected void onEnterAction(){
		if (!dBPane.validateForm(DlgNodePropertiesAction.EDIT)){
			return;
		}

		ObjectManagementGateway objGateway = new HttpObjectManagementGatewayImpl();

		DBObject obj = dBPane.fillObj(false);

		lastObj = dBPane.fillObj(true);

		Attribute ctAttr = obj.getEntity().getAttribute("ConnectionType");
		String vConnectionType;
		if (ctAttr != null){
			Object value = obj.getValue(ctAttr.ID);
			if (value instanceof Value)
				vConnectionType = ((Value) value).getId() + "";
			else
				vConnectionType = (String) value;
		} else
			vConnectionType = "";

		if (vConnectionType == null)
			vConnectionType = Integer.toString(obj.getEntity().getAttribute("ConnectionType").getValues().get(0).getId());

		final Ni3Document doc = parent.Doc;
		final int favoritesID = doc.getFavoritesID();

		final GraphGateway graphGateway = new HttpGraphGatewayImpl();
		for (Node ID1 : fromID){
			for (Node ID2 : toID){
				if (edge == null){

					obj.setId(-1);

					objGateway.insertEdge(obj, favoritesID, ID1.ID, ID2.ID);
				} else{
					edge.setConnectionType(Integer.valueOf(vConnectionType));
					edge.setStrength(edge.getStrength(), true);
					obj.setId(edge.ID);
					objGateway.updateEdge(obj, favoritesID, edge.status != 0);
				}

				final List<Edge> edges = graphGateway.getEdges(Arrays.asList(obj.getId()), doc.SchemaID, doc.DB
						.getDataFilter());
				if (edges != null && !edges.isEmpty()){
					doc.Subgraph.addResultToGraph(edges);
					doc.DB.reloadObject(obj);

					Edge e = doc.Subgraph.findEdge(edges.get(0).ID);
					if (e != null){
						e.Obj = obj;
						e.refreshLabel();

						doc.DB.reloadNode(e.from, doc.Subgraph);
						doc.DB.getFavoritesContextData(doc.getTopicID(), e.from);
						e.from.refreshLabel();

						doc.DB.reloadNode(e.to, doc.Subgraph);
						doc.DB.getFavoritesContextData(doc.getTopicID(), e.to);
						e.to.refreshLabel();
					}
				}
			}
		}
		doc.Subgraph.clearSelection();
		doc.Subgraph.filter(doc.filter, doc.getFavoritesID());
		parent.repaint();
		doc.Subgraph.setMultiEdgeIndexes();

		success = true;
		this.dispose();
	}

	private void onCancel(){
		parent.Doc.Subgraph.clearSelection();
		parent.repaint();
		this.dispose();
	}

	private void onClear(){
		dBPane.clearPanel();
	}

	public void restoreLast(){
		if (lastObj != null)
			dBPane.restoreObj(lastObj);
	}

	public void actionPerformed(ActionEvent ae){
		String action = ae.getActionCommand();
		if ("OK".equals(action))
			onOK();
		else if ("Cancel".equals(action))
			onCancel();
		else if ("Clear".equals(action))
			onClear();
	}

	class CheckConnectionType extends CheckValueIntegrity{
		Node fromID[], toID[];

		public CheckConnectionType(Node FromID[], Node ToID[]){
			this.fromID = FromID;
			this.toID = ToID;
		}

		public boolean checkValue(Entity ent, Attribute a, Value v){
			if (!a.name.equalsIgnoreCase(Attribute.CONNECTIONTYPE_ATTRIBUTE_NAME))
				return true;

			boolean AddItem = true;
			int CT = v.getId();
			// TODO this code makes too many calls to server. Should be refactored to make one call
			for (Node ID1 : fromID){
				for (Node ID2 : toID){
					if (!parent.Doc.DB.schema.edgeMetaphor.validateConnection(ID1.Type, ID2.Type, CT)){
						AddItem = false;
						break;
					}
				}

				if (!AddItem)
					break;
			}

			return AddItem;
		}
	}

	public boolean isSuccess(){
		return success;
	}
}
