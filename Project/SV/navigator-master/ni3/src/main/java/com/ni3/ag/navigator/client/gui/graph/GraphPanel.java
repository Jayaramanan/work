/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import com.ni3.ag.navigator.client.controller.HtmlDataFormatter;
import com.ni3.ag.navigator.client.controller.LicenseValidator;
import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.controller.ObjectPopupListener;
import com.ni3.ag.navigator.client.domain.ChartFilter;
import com.ni3.ag.navigator.client.domain.ChartParams;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.domain.cache.IconCache;
import com.ni3.ag.navigator.client.gui.ImageSelection;
import com.ni3.ag.navigator.client.gui.MainPanel;
import com.ni3.ag.navigator.client.gui.ObjectPopupMenu;
import com.ni3.ag.navigator.client.gui.graph.painter.EdgePainter;
import com.ni3.ag.navigator.client.gui.graph.painter.NodePainter;
import com.ni3.ag.navigator.client.gui.util.StringTransferable;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.client.util.Utility;
import com.ni3.ag.navigator.shared.domain.ChartType;

@SuppressWarnings("serial")
public class GraphPanel extends BasicGraphPanel implements MouseListener, MouseMotionListener, MouseWheelListener,
		ActionListener, Ni3ItemListener, ChangeListener{
	private static final Logger log = Logger.getLogger(GraphPanel.class);

	class DTListener implements DropTargetListener{
		/**
		 * Called by drop Checks the flavors and operations
		 * 
		 * @param e
		 *            the DropTargetDropEvent object
		 * @return the chosen DataFlavor or null if none match
		 */
		private DataFlavor chooseDropFlavor(final DropTargetDropEvent e){
			if (e.isLocalTransfer() && e.isDataFlavorSupported(StringTransferable.localStringFlavor)){
				return StringTransferable.localStringFlavor;
			}
			DataFlavor chosen = null;
			if (e.isDataFlavorSupported(StringTransferable.plainTextFlavor)){
				chosen = StringTransferable.plainTextFlavor;
			} else if (e.isDataFlavorSupported(StringTransferable.localStringFlavor)){
				chosen = StringTransferable.localStringFlavor;
			} else if (e.isDataFlavorSupported(DataFlavor.stringFlavor)){
				chosen = DataFlavor.stringFlavor;
			}
			return chosen;
		}

		/**
		 * start "drag under" feedback on component invoke acceptDrag or rejectDrag based on isDragOk
		 */
		@Override
		public void dragEnter(final DropTargetDragEvent e){
			if (!isDragOk(e)){
				e.rejectDrag();
				return;
			}
			e.acceptDrag(e.getDropAction());
		}

		@Override
		public void dragExit(final DropTargetEvent e){
		}

		/**
		 * continue "drag under" feedback on component invoke acceptDrag or rejectDrag based on isDragOk
		 */
		@Override
		public void dragOver(final DropTargetDragEvent e){
			if (!isDragOk(e)){
				e.rejectDrag();
				return;
			}
			e.acceptDrag(e.getDropAction());
		}

		/**
		 * perform action from getSourceActions on the transferrable invoke acceptDrop or rejectDrop invoke dropComplete
		 * if its a local (same JVM) transfer, use StringTransferable.localStringFlavor find a match for the flavor
		 * check the operation get the transferable according to the chosen flavor do the transfer
		 */
		@Override
		public void drop(final DropTargetDropEvent e){
			final DataFlavor chosen = chooseDropFlavor(e);
			if (chosen == null){
				System.err.println("No flavor match found");
				e.rejectDrop();
				return;
			}
			// the actual operation
			// int da = e.getDropAction();
			// the actions that the source has specified with
			// DragGestureRecognizer
			final int sa = e.getSourceActions();

			if ((sa & acceptableActions) == 0){
				System.err.println("No action match found");
				e.rejectDrop();
				return;
			}

			Object data;
			try{
				/*
				 * the source listener receives this action in dragDropEnd. if the action is
				 * DnDConstants.ACTION_COPY_OR_MOVE then the source receives MOVE!
				 */
				e.acceptDrop(acceptableActions);

				data = e.getTransferable().getTransferData(chosen);
				if (data == null){
					throw new NullPointerException();
				}
			} catch (final Throwable t){
				System.err.println("Couldn't get transfer data: " + t.getMessage());
				t.printStackTrace();
				e.dropComplete(false);
				return;
			}

			e.dropComplete(true);
		}

		@Override
		public void dropActionChanged(final DropTargetDragEvent e){
			if (!isDragOk(e)){
				e.rejectDrag();
				return;
			}
			e.acceptDrag(e.getDropAction());
		}

		/**
		 * Called by isDragOk Checks to see if the flavor drag flavor is acceptable
		 * 
		 * @param e
		 *            the DropTargetDragEvent object
		 * @return whether the flavor is acceptable
		 */
		private boolean isDragFlavorSupported(final DropTargetDragEvent e){
			boolean ok = false;
			if (e.isDataFlavorSupported(StringTransferable.plainTextFlavor)){
				ok = true;
			} else if (e.isDataFlavorSupported(StringTransferable.localStringFlavor)){
				ok = true;
			} else if (e.isDataFlavorSupported(DataFlavor.stringFlavor)){
				ok = true;
			}
			return ok;
		}

		/**
		 * Called by dragEnter and dragOver Checks the flavors and operations
		 * 
		 * @param e
		 *            the event object
		 * @return whether the flavor and operation is ok
		 */
		private boolean isDragOk(final DropTargetDragEvent e){
			if (!isDragFlavorSupported(e)){
				Utility.debugToConsole("isDragOk:no flavors chosen");
				return false;
			}

			// the actions specified when the source
			// created the DragGestureRecognizer
			// int sa = e.getSourceActions();

			// the docs on DropTargetDragEvent rejectDrag says that
			// the dropAction should be examined
			// int da = e.getDropAction();
			//
			// // we're saying that these actions are necessary
			// if ((da & GraphPanel.this.acceptableActions) == 0)
			// return false;
			return true;
		}

	}

	public static String graphNodeLabelFont;

	public static int graphNodeLabelHeight;
	public static String graphEdgeLabelFont;

	public static int graphEdgeLabelHeight;

	public int prevPanX, prevPanY;
	public boolean inactive;

	final LicenseValidator validator = LicenseValidator.getInstance();

	private JSlider nodeSpaceSlider;
	private JSlider alphaSlider;
	private final JToggleButton graphFreezeButton;

	private Icon iconStopped;
	private Icon iconRefreshing;

	/**
	 * the actions supported by this drop target
	 */
	private final int acceptableActions = DnDConstants.ACTION_COPY;
	@SuppressWarnings("unused")
	private final DropTarget dropTarget;

	private final DropTargetListener dtListener;

	private ObjectPopupMenu objectPopupMenu;

	Node pick, lastpick;

	boolean pickfixed;

	private Image offscreenImage;
	private Dimension offscreenImageSize;
	private Graphics2D offscreenImageGraphics;
	private boolean freezeRedraw;
	private HtmlDataFormatter htmlFormatter;

	/**
	 * This variable is required to detect whether there was dragging between mouse pressed and mouse released events.
	 * It is set to true during dragging.
	 */
	boolean dragDropDetected = false;

	public GraphPanel(final MainPanel parent){
		super(parent);

		copyToClipboard = false;
		htmlFormatter = new HtmlDataFormatter();

		Doc.registerListener(this);

		setBackground(Color.white);

		addMouseListener(this);
		addMouseWheelListener(this);
		addMouseMotionListener(this);

		dtListener = new DTListener();

		// component, ops, listener, accepting
		dropTarget = new DropTarget(this, acceptableActions, dtListener, true);

		inactive = false;

		setLayout(null);

		nodeSpaceSlider = new JSlider(SwingConstants.VERTICAL, UserSettings.getIntAppletProperty("NodeSpaceSliderMinValue",
				0), UserSettings.getIntAppletProperty("NodeSpaceSliderMaxValue", 30), 15);
		nodeSpaceSlider.setMinimumSize(new Dimension(15, 50));
		nodeSpaceSlider.setPreferredSize(new Dimension(15, 120));
		nodeSpaceSlider.setPaintTicks(false);
		nodeSpaceSlider.setSnapToTicks(false);
		nodeSpaceSlider.setMajorTickSpacing(1);
		nodeSpaceSlider.setInverted(false);
		nodeSpaceSlider.setName("NodeSpaceSlider");
		nodeSpaceSlider.setValue(UserSettings.getIntAppletProperty("NodeSpaceSliderValue", 10));
		nodeSpaceSlider.addChangeListener(this);
		nodeSpaceSlider.setToolTipText(UserSettings.getWord("Node space slider"));

		alphaSlider = new JSlider(SwingConstants.VERTICAL, 1, 100, 75);
		alphaSlider.setToolTipText(UserSettings.getWord("Space between nodes"));
		alphaSlider.setMinimumSize(new Dimension(15, 50));
		alphaSlider.setPreferredSize(new Dimension(15, 120));
		alphaSlider.setPaintTicks(false);
		alphaSlider.setSnapToTicks(false);
		alphaSlider.setMajorTickSpacing(1);
		alphaSlider.setInverted(false);
		alphaSlider.setName("AlphaSlider");
		alphaSlider.setValue(75);
		alphaSlider.addChangeListener(this);
		alphaSlider.setToolTipText(UserSettings.getWord("Alpha slider"));
		alphaSlider.setVisible(UserSettings.getBooleanAppletProperty("AlphaSliderVisible", false));

		final JButton zoomInButton = new JButton(IconCache.getImageIcon(IconCache.ZOOM_IN));
		zoomInButton.setMinimumSize(new Dimension(24, 24));
		zoomInButton.setPreferredSize(new Dimension(24, 24));
		zoomInButton.setToolTipText(UserSettings.getWord("Zoom in"));
		zoomInButton.setActionCommand("ZoomIn");
		zoomInButton.addActionListener(this);

		final JButton zoomOutButton = new JButton(IconCache.getImageIcon(IconCache.ZOOM_OUT));
		zoomOutButton.setMinimumSize(new Dimension(24, 24));
		zoomOutButton.setPreferredSize(new Dimension(24, 24));
		zoomOutButton.setToolTipText(UserSettings.getWord("Zoom out"));
		zoomOutButton.setActionCommand("ZoomOut");
		zoomOutButton.addActionListener(this);

		iconStopped = IconCache.getImageIcon(IconCache.REFRESH_STOPPED);
		iconRefreshing = IconCache.getImageIcon(IconCache.REFRESH_ACTIVE);

		graphFreezeButton = new JToggleButton(IconCache.getImageIcon(IconCache.REFRESH_ACTIVE));
		graphFreezeButton.setMinimumSize(new Dimension(24, 24));
		graphFreezeButton.setMaximumSize(new Dimension(24, 24));
		graphFreezeButton.setPreferredSize(new Dimension(24, 24));
		graphFreezeButton.setActionCommand("GraphFreeze");
		graphFreezeButton.addActionListener(this);
		graphFreezeButton.setToolTipText(UserSettings.getWord("Freeze graph layout"));

		final JPanel SliderPanel = new JPanel(new GridLayout(1, 2));

		SliderPanel.setMinimumSize(new Dimension(22, 10));
		SliderPanel.setPreferredSize(new Dimension(22, 100));
		SliderPanel.setMaximumSize(new Dimension(50, 150));

		SliderPanel.add(nodeSpaceSlider);
		SliderPanel.add(alphaSlider);

		toolbar = new JToolBar(SwingConstants.VERTICAL);
		toolbar.setRollover(true);

		toolbar.setMargin(new Insets(0, 0, 0, 0));
		toolbar.add(zoomInButton);
		toolbar.add(zoomOutButton);
		toolbar.add(graphFreezeButton);
		toolbar.add(SliderPanel);

		toolbar.setFloatable(false);

		setTransferHandler(new ImageSelection());
		final boolean showCounter = UserSettings.getBooleanAppletProperty("ShowNodeExpandCounter", true);
		Doc.getGraphVisualSettings().setShowContractedEdgeCounter(showCounter);
	}

	@Override
	public void actionPerformed(final ActionEvent e){
		if ("GraphFreeze".equals(e.getActionCommand())){
			parentMP.setGraphDontRelax(!parentMP.dontRelax);
		} else if ("ZoomIn".equals(e.getActionCommand())){
			ZoomChange(1.1, false);
		} else if ("ZoomOut".equals(e.getActionCommand())){
			ZoomChange(0.9, false);
		}
	}

	public java.awt.Rectangle GetBoundingRectangle(final double growPercent){
		int minx, miny, maxx, maxy;

		minx = miny = Integer.MAX_VALUE;
		maxx = maxy = Integer.MIN_VALUE;

		for (final Node n : Doc.Subgraph.getNodes()){
			if (n.getX() * n.getY() != 0.0){
				final int metaphorWidth = n.getScaledMetaphorWidth(false);
				final int metaphorHeight = n.getScaledMetaphorHeight(false);
				if (n.getX() + metaphorWidth / 2 > maxx){
					maxx = (int) (n.getX() + metaphorWidth / 2);
				}

				if (n.getY() + metaphorHeight / 2 > maxy){
					maxy = (int) (n.getY() + metaphorHeight / 2);
				}

				if (n.getX() - metaphorWidth / 2 < minx){
					minx = (int) (n.getX() - metaphorWidth / 2);
				}

				if (n.getY() - metaphorHeight / 2 < miny){
					miny = (int) (n.getY() - metaphorHeight / 2);
				}
			}
		}

		final Rectangle ret = new Rectangle(minx, miny, maxx - minx, maxy - miny);

		if (minx != Integer.MAX_VALUE){
			ret.grow((int) (ret.width * growPercent), (int) (ret.height * growPercent));
		}

		return ret;
	}

	@Override
	public int getListenerType(){
		return Ni3ItemListener.SRC_Graph;
	}

	@Override
	public String getToolTipText(final MouseEvent e){
		Doc.clearInFocusEdges();
		if (inactive){
			return "";
		}

		final Set<Edge> inFocusEdges = Doc.getInFocusEdges();

		final Point pt = screenToGeoCoord(e.getX(), e.getY());
		final Node node = findNode(pt.x, pt.y);

		if (node != null){
			inFocusEdges.addAll(node.inEdges);
			inFocusEdges.addAll(node.outEdges);

			if (node.Obj != null){
				return htmlFormatter.getObjectTooltip(node.Obj);
			} else{
				log.warn("Missing data for node ID=" + node.ID);
				return "";
			}

		} else{
			final Edge edge = Doc.Subgraph.findEdge(pt.x, pt.y);

			if (edge != null){
				inFocusEdges.add(edge);

				if (edge.Obj != null){
					return htmlFormatter.getObjectTooltip(edge.Obj);
				}
			}
		}

		if (!inFocusEdges.isEmpty()){
			Doc.setInFocusEdges(inFocusEdges);
		}

		return "";
	}

	// 1.1 event handling
	@Override
	public void mouseClicked(final MouseEvent e){
	}

	@Override
	public void mouseDragged(final MouseEvent e){
		dragDropDetected = true;
		synchronized (Doc.Subgraph){
			final int mod = e.getModifiersEx();

			if (edgeConnectionInProgress){
				final Point pt = screenToGeoCoord(e.getX(), e.getY());
				rubberBandRectangle.width = pt.x - rubberBandRectangle.x;
				rubberBandRectangle.height = pt.y - rubberBandRectangle.y;

				if (secondPick != null){
					secondPick.selectedTo = false;
				}

				secondPick = findNode(pt.x, pt.y);

				if (secondPick != null){
					secondPick.selectedTo = true;
				}
				Doc.updateNodeSelection();
			} else if (e.getModifiersEx() == (InputEvent.SHIFT_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK)){
				final Point pt = screenToGeoCoord(e.getX(), e.getY());

				if (inRubberBand){
					rubberBandRectangle.width = pt.x - rubberBandRectangle.x;
					rubberBandRectangle.height = pt.y - rubberBandRectangle.y;
				} else{
					inRubberBand = true;
					rubberBandSelect = true;
					rubberBandRectangle.x = pt.x;
					rubberBandRectangle.y = pt.y;
					rubberBandRectangle.width = 0;
					rubberBandRectangle.height = 0;
				}
			} else{
				if (pick == null){
					final double zoomf = offscreenImageGraphics.getTransform().getScaleX();

					offscreenImageGraphics.translate(((e.getX() - prevPanX) / zoomf), ((e.getY() - prevPanY) / zoomf));
					prevPanX = e.getX();
					prevPanY = e.getY();
				} else{
					if (inactive){
						return;
					}

					final Point pt = screenToGeoCoord(e.getX(), e.getY());
					double dx, dy;

					dx = pt.x - prevPanX;
					dy = pt.y - prevPanY;

					prevPanX = pt.x;
					prevPanY = pt.y;

					Doc.Subgraph.getGraphLayoutManager().moveNode(pick,
							(mod & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK, dx, dy);
				}

				forceRepaint();
			}

			e.consume();
		}
	}

	@Override
	public void mouseEntered(final MouseEvent e){
	}

	@Override
	public void mouseExited(final MouseEvent e){
	}

	@Override
	public void mouseMoved(final MouseEvent e){
		final Point pt = screenToGeoCoord(e.getX(), e.getY());
		Node nodeUnderCursor = findNode(pt.x, pt.y);
		Doc.setGraphPointedNode(nodeUnderCursor);
	}

	@Override
	public void mousePressed(final MouseEvent e){
		numMouseButtonsDown++;
		// addMouseMotionListener(this);

		synchronized (Doc.Subgraph){
			if (inactive){
				return;
			}

			final Point pt = screenToGeoCoord(e.getX(), e.getY());

			selectedNode = lastpick = findNode(pt.x, pt.y);

			if (Doc.isExpressEditMode() && selectedNode != null && isEdgeDataChangeEnabled()){
				edgeConnectionInProgress = true;
				rubberBandRectangle.x = pt.x;
				rubberBandRectangle.y = pt.y;
				rubberBandRectangle.width = 0;
				rubberBandRectangle.height = 0;

				Doc.Subgraph.clearSelection();

				selectedNode.selectedFrom = true;

				secondPick = null;

				Doc.updateNodeSelection();

				return;
			}

			if (selectedNode == null){
				selectedEdge = Doc.Subgraph.findEdge(pt.x, pt.y);
			} else{
				selectedEdge = null;
			}

			if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1){
				if (selectedNode == null && selectedEdge == null){
					parentMP.setGraphDontRelax(!parentMP.dontRelax);
					e.consume();
					return;
				} else if (selectedNode != null){
					parentMP.suspended = true;
					while (parentMP.inProcessing){
						Utility.sleep(1);
					}

					if (selectedNode.getExternalRelatives() > 0){
						graphController.expandNodeOneLevel(selectedNode, false, false);
					} else{
						graphController.contractNode(selectedNode);
					}

					parentMP.suspended = false;
				}
			}

			prevPanX = e.getX();
			prevPanY = e.getY();
			pick = null;

			// TODO: Refactor this mind blowing if statement into something comprehensible
			if (e.getButton() == MouseEvent.BUTTON1){
				if (e.getModifiersEx() == (InputEvent.ALT_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK)){
					final int selectedTo = Doc.Subgraph.countSelectedTo();
					final int selectedFrom = Doc.Subgraph.countSelectedFrom();

					if (selectedNode != null
							&& (selectedNode.selectedFrom || selectedFrom == 0 || selectedTo < 2 || (selectedTo == 2 && selectedNode.selectedTo))){
						selectedNode.selectedFrom = !selectedNode.selectedFrom;
						selectedNode.selectedTo = false;
						selectedNode.selected = false;
						Doc.dispatchEvent(MSG_GraphDirty, SRC_Graph, null, null);
					}
				} else if (e.getModifiersEx() == (InputEvent.SHIFT_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK)){
					if (selectedNode != null){
						invertNodeSelection(selectedNode);
					}
				} else{
					pick = selectedNode;
					if (pick != null){
						pickfixed = pick.fixed;
						pick.fixed = true;
						// pick.setX(pt.x);
						// pick.setY(pt.y);
						prevPanX = pt.x;
						prevPanY = pt.y;

						Doc.dispatchEvent(MSG_GraphDirty, SRC_Graph, null, null);
					}
				}
			} else if (e.getButton() == MouseEvent.BUTTON3 && (selectedNode != null || selectedEdge != null)){
				if (selectedNode != null){
					if (e.getModifiersEx() == (InputEvent.ALT_DOWN_MASK | InputEvent.BUTTON3_DOWN_MASK)){
						final int selectedTo = Doc.Subgraph.countSelectedTo();
						final int selectedFrom = Doc.Subgraph.countSelectedFrom();

						if (selectedNode.selectedTo || selectedTo == 0 || selectedFrom < 2
								|| (selectedFrom == 2 && selectedNode.selectedFrom)){
							selectedNode.selectedTo = !selectedNode.selectedTo;
							selectedNode.selectedFrom = false;
							selectedNode.selected = false;
							Doc.dispatchEvent(MSG_GraphDirty, SRC_Graph, null, null);
						}
					} else if (UserSettings.getBooleanAppletProperty("ContextMenu_Node_InUse", true)){
						ObjectPopupMenu popupMenu = getObjectPopupMenu();
						popupMenu.createPopupMenuItems(selectedNode);
						popupMenu.show(this, e.getX(), e.getY());
					}
				}
				if (selectedEdge != null && selectedEdge.Obj != null && selectedEdge.status == 0){
					ObjectPopupMenu popupMenu = getObjectPopupMenu();
					popupMenu.createPopupMenuItems(selectedEdge);
					popupMenu.show(this, e.getX(), e.getY());
				}
			} else if (SystemGlobals.isSiebelIntegrationModeEnabled && e.getButton() == MouseEvent.BUTTON3
					&& selectedNode == null && selectedEdge == null){
				showSiebelPopupMenu(new Point(e.getX(), e.getY()));
			}

		}

		setGraphDirty(true);
		Doc.updateNodeSelection();

		e.consume();
	}

	public void invertNodeSelection(Node node){
		node.selected = !node.selected;
		node.selectedFrom = false;
		node.selectedTo = false;
		Doc.dispatchEvent(MSG_GraphDirty, SRC_Graph, null, null);
	}

	private ObjectPopupMenu getObjectPopupMenu(){
		if (objectPopupMenu == null){
			ObjectPopupListener listener = new ObjectPopupListener(parentMP);
			objectPopupMenu = new ObjectPopupMenu(listener, Doc);
		}
		return objectPopupMenu;
	}

	@Override
	public void mouseReleased(final MouseEvent e){
		numMouseButtonsDown--;
		// removeMouseMotionListener(this);

		final Point pt = screenToGeoCoord(e.getX(), e.getY());

		if (Doc.isExpressEditMode() && !edgeConnectionInProgress && !inRubberBand && pick == null && selectedEdge == null
				&& !dragDropDetected && isNodeDataChangeEnabled()){
			parentMP.nodeCreate(pt.x, pt.y, Double.NaN, Double.NaN);
			return;
		}

		if (edgeConnectionInProgress){
			edgeConnectionInProgress = false;

			if (Doc.isExpressEditMode() && isEdgeDataChangeEnabled()){

				if (secondPick != null){
					secondPick.selectedTo = false;
				}

				secondPick = findNode(pt.x, pt.y);

				if (secondPick != null){
					secondPick.selectedTo = true;
					parentMP.edgeCreate();
				}
				Doc.Subgraph.clearSelection();
				Doc.updateNodeSelection();
			}
		}

		if (inRubberBand){
			inRubberBand = false;
			if (rubberBandSelect){
				int w, h;

				w = rubberBandRectangle.width;
				if (rubberBandRectangle.width < 0){
					rubberBandRectangle.x += w;
					rubberBandRectangle.width = -w;
				}

				h = rubberBandRectangle.height;
				if (rubberBandRectangle.height < 0){
					rubberBandRectangle.y += rubberBandRectangle.height;
					rubberBandRectangle.height = -h;
				}

				for (final Node n : Doc.Subgraph.getNodes()){
					if (n.IsNodeInRectangle(rubberBandRectangle.x, rubberBandRectangle.y, rubberBandRectangle.getWidth(),
							rubberBandRectangle.getHeight())){
						n.selected = true;
					}
				}

				Utility.debugToConsole("Dimension " + rubberBandRectangle.x + "," + rubberBandRectangle.y + ","
						+ rubberBandRectangle.width + "," + rubberBandRectangle.height);
				Doc.updateNodeSelection();
			} else{
				ZoomToRectangle(rubberBandRectangle);
			}
		}

		if (inactive){
			return;
		}

		synchronized (Doc.Subgraph){
			if (e.getButton() == MouseEvent.BUTTON1){
				synchronized (Doc.Subgraph.getNodes()){
					if (pick != null){
						pick.fixed = pickfixed;
						pick.setX(pt.x);
						pick.setY(pt.y);
					}
				}

				if (numMouseButtonsDown == 0){
					pick = null;
				}

				forceRepaint();
			}

			e.consume();
		}
		dragDropDetected = false;
	}

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e){
		final int notches = e.getWheelRotation();
		Doc.getGraphVisualSettings().setZoomTo(1 - notches * 0.05);
		final AffineTransform at = SmoothZoom(offscreenImageGraphics.getTransform(), offscreenImageGraphics.getTransform()
				.getScaleX()
				* Doc.getGraphVisualSettings().getZoomTo(), e.getX(), e.getY());

		offscreenImageGraphics.setTransform(at);

		// TODO is it correct?
		// Doc.GraphSet.OrgX = 0;
		// Doc.GraphSet.OrgY = 0;
		// Doc.GraphSet.ZoomTo = 1.0;

		forceRepaint();
	}

	@Override
	public void onClearSubgraph(){
		setGraphDirty(true);
		Doc.getGraphVisualSettings().reset();
		if (offscreenImageGraphics != null){
			AffineTransform at = Doc.getGraphVisualSettings().getTransform();
			offscreenImageGraphics.setTransform(at);
			Dimension dim = getSize();
			try{
				at.inverseTransform(new Point(0, 0), Doc.getGraphVisualSettings().getFirstPoint());
				at.inverseTransform(new Point(dim.width, dim.height), Doc.getGraphVisualSettings().getSecondPoint());
			} catch (NoninvertibleTransformException e){
				log.error("Error invert point", e);
			}
		}
	}

	@Override
	public void paint(final Graphics g2){
		renderView(g2, getSize(), false, null);
	}

	@Override
	public int print(final Graphics g, final PageFormat pf, final int pageIndex, final Dimension d) throws PrinterException{
		if (pageIndex == 0){
			renderView(g, d, true, pf);
			return Printable.PAGE_EXISTS;
		} else{
			return Printable.NO_SUCH_PAGE;
		}
	}

	public void renderGraph(final Graphics2D g, final Dimension dim, final boolean Print){
		Doc.getGraphVisualSettings().setCanvasSize(dim);

		if (Doc.getGraphVisualSettings().isZeroSize())
			return;

		if (Print){
			ZoomChange((double) dim.width / getSize().width, true);
		}

		AffineTransform at;
		if ((offscreenImage == null) || !Doc.getGraphVisualSettings().getCanvasSize().equals(offscreenImageSize.getSize())){
			if (offscreenImageGraphics != null)
				at = offscreenImageGraphics.getTransform();
			else
				at = Doc.getGraphVisualSettings().getTransform();

			offscreenImage = createImage(dim.width, dim.height);
			offscreenImageSize = dim;
			if (offscreenImageGraphics != null){
				offscreenImageGraphics.dispose();
			}
			offscreenImageGraphics = (Graphics2D) offscreenImage.getGraphics();
			offscreenImageGraphics.setFont(getFont());

			if (at != null){
				offscreenImageGraphics.setTransform(at);
			}
		}

		offscreenImageGraphics.setColor(getBackground());

		at = offscreenImageGraphics.getTransform();
		Doc.getGraphVisualSettings().setTransform(new AffineTransform(at));

		Doc.getGraphVisualSettings().setFirstPoint(new Point());
		Doc.getGraphVisualSettings().setSecondPoint(new Point());

		try{
			at.inverseTransform(new Point(0, 0), Doc.getGraphVisualSettings().getFirstPoint());
			at.inverseTransform(new Point(dim.width, dim.height), Doc.getGraphVisualSettings().getSecondPoint());
		} catch (final NoninvertibleTransformException e1){
			log.error("Error transform point", e1);
		}

		offscreenImageGraphics.fillRect(Doc.getGraphVisualSettings().getFirstPoint().x - 1, Doc.getGraphVisualSettings()
				.getFirstPoint().y - 1, Doc.getGraphVisualSettings().getSecondPoint().x
				- Doc.getGraphVisualSettings().getFirstPoint().x + 1, Doc.getGraphVisualSettings().getSecondPoint().y
				- Doc.getGraphVisualSettings().getFirstPoint().y + 1);

		offscreenImageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		offscreenImageGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		offscreenImageGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		if (Doc.Subgraph.getNodes().size() <= Doc.DB.getMaximumNodeCount()){
			Node.nextBlinkPhase();

			List<Node> tempNodes = new ArrayList<Node>();
			synchronized (Doc.Subgraph.getNodes()){
				tempNodes.addAll(Doc.Subgraph.getNodes());
			}
			Collections.sort(tempNodes, new Comparator<Node>(){
				@Override
				public int compare(Node o1, Node o2){
					return o1.Obj != null && o2.Obj != null && o1.Obj.getMetaphor() != null && o2.Obj.getMetaphor() != null
							? o1.Obj.getMetaphor().getPriority() - o2.Obj.getMetaphor().getPriority() : 0;
				}
			});
			for (final Node n : tempNodes){
				if (Doc.isPolygonNode(n.ID)){
					getNodePainter().paintPolygon(n, offscreenImageGraphics, true, Doc.getPolygonAlpha(),
							Doc.getPolyColor(n.ID));
				} else if (Doc.isPolylineNode(n.ID)){
					getNodePainter().paintPolygon(n, offscreenImageGraphics, false, Doc.getPolygonAlpha(),
							Doc.getPolyColor(n.ID));
				}
			}

			offscreenImageGraphics.setColor(Color.black);
			offscreenImageGraphics.setFont(new Font(graphEdgeLabelFont, Font.BOLD, graphEdgeLabelHeight));

			int edgesDrawnCount = 0;
			int edgesTotalCount = 0;
			final EdgePainter ePainter = getEdgePainter();
			List<Edge> tempEdges = new ArrayList<Edge>();
			synchronized (Doc.Subgraph.getEdges()){
				tempEdges.addAll(Doc.Subgraph.getEdges());
			}

			final CommandPanelSettings cpSettings = Doc.getCommandPanelSettings();
			for (final Edge e : tempEdges){
				if (ePainter.paint(e, offscreenImageGraphics, cpSettings.isShowDirectedEdges(), pick, cpSettings
						.isShowEdgeLabels(), cpSettings.isShowEdgeThickness(), 1.0, Doc.getInPathEdges().contains(e))){
					edgesDrawnCount++;
				}
				edgesTotalCount++;
			}

			setEdgeCounts(edgesDrawnCount, edgesTotalCount);

			offscreenImageGraphics.setFont(new Font(graphNodeLabelFont, Font.BOLD, graphNodeLabelHeight));

			int nodesDrawnCount = 0;
			int nodesTotal = 0; // NAV-799

			Doc.Subgraph.recalculateMaxHaloR();

			final NodePainter nPainter = getNodePainter();
			for (final Node n : tempNodes){
				if (n.Obj == null)
					continue;
				ChartFilter cFilter = Doc.getChartFilter(n);
				ChartParams cParams = Doc.getChartParams(n);
				if (nPainter.paint(n, offscreenImageGraphics, 1.0, n == pick, cpSettings.isShowNodeLabels(), Doc
						.getGraphVisualSettings().isShowContractedEdgeCounter(), cFilter, cParams)){
					nodesDrawnCount++;
				}
				nodesTotal++; // NAV-799
			}

			setNodeCounts(nodesDrawnCount, nodesTotal);
		}

		if (inRubberBand){
			if (rubberBandSelect){
				offscreenImageGraphics.setColor(Color.gray);
			} else{
				offscreenImageGraphics.setColor(Color.red);
			}

			int x, y, w, h;

			x = rubberBandRectangle.x;
			w = rubberBandRectangle.width;
			if (rubberBandRectangle.width < 0){
				x += rubberBandRectangle.width;
				w = -w;
			}

			y = rubberBandRectangle.y;
			h = rubberBandRectangle.height;
			if (rubberBandRectangle.height < 0){
				y += rubberBandRectangle.height;
				h = -h;
			}

			offscreenImageGraphics.drawRect(x, y, w, h);
		} else if (edgeConnectionInProgress){
			offscreenImageGraphics.setColor(Color.red);

			offscreenImageGraphics.drawLine(rubberBandRectangle.x, rubberBandRectangle.y, rubberBandRectangle.x
					+ rubberBandRectangle.width, rubberBandRectangle.y + rubberBandRectangle.height);
		}

		g.drawImage(offscreenImage, 0, 0, null);

		if (copyToClipboard){
			copyToClipboard = false;
			getTransferHandler().exportToClipboard(this, Toolkit.getDefaultToolkit().getSystemClipboard(),
					TransferHandler.COPY);
		}
	}

	/**
	 * @param edgesDrawnCount
	 * @param edgesTotalCount
	 */
	private void setEdgeCounts(int edgesDrawnCount, int edgesTotalCount){
		if (edgesDrawnCount != Doc.getEdgeCount() || edgesTotalCount != Doc.getEdgeTotalCount()){
			Doc.setEdgeCounts(edgesDrawnCount, edgesTotalCount);
		}
	}

	/**
	 * @param nodesDrawnCount
	 * @param nodesTotal
	 */
	private void setNodeCounts(int nodesDrawnCount, int nodesTotal){
		if (nodesDrawnCount != Doc.getNodeCount() || nodesTotal != Doc.getNodeTotalCount()){
			Doc.setNodeCounts(nodesDrawnCount, nodesTotal);
		}
	}

	@Override
	public void renderView(final Graphics g2, final Dimension dim, final boolean Print, final PageFormat pf){
		if (freezeRedraw){
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, dim.width, dim.height);
		} else{
			final Graphics2D g = (Graphics2D) g2;

			if (!isGraphDirty() && !isInRubber()){
				g.drawImage(offscreenImage, 0, 0, null);
			} else{
				renderGraph(g, dim, Print);
			}

			if (Print){
				ZoomChange(getSize().width / (double) dim.width, true);
			}

			setGraphDirty(false);
		}
	}

	private Point screenToGeoCoord(final int x, final int y){
		final Point pt = new Point(x, y);
		final Point npt = new Point();

		try{
			if (offscreenImageGraphics != null){
				offscreenImageGraphics.getTransform().inverseTransform(pt, npt);
			}
		} catch (final NoninvertibleTransformException e1){
			e1.printStackTrace();
		}
		return npt;
	}

	public void setGraphDontRelax(final boolean DontRelax){
		if (DontRelax){
			graphFreezeButton.setIcon(iconStopped);
		} else{
			graphFreezeButton.setIcon(iconRefreshing);
		}
	}

	private void setGraphPanelSettings(final GraphPanelSettings set){
		if (offscreenImageGraphics != null){
			offscreenImageGraphics.setTransform(Doc.getGraphVisualSettings().getTransform());
		}
	}

	@Override
	public void stateChanged(final ChangeEvent e){
		final JSlider source = (JSlider) e.getSource();

		if ("NodeSpaceSlider".equals(source.getName())){
			Doc.Subgraph.NodeSpace = source.getValue() / 10.0;
		} else if ("AlphaSlider".equals(source.getName())){
			Doc.setPolygonAlpha(source.getValue() / 100.0f);
			Doc.dispatchEvent(MSG_GraphDirty, SRC_MainPanel, null, null);
			setGraphPanelSettings(Doc.getGraphVisualSettings());
			setGraphDirty(true);
			forceRepaint();
		}
	}

	public void ZoomChange(final double Factor, final boolean TopLeft){
		if (Doc.getGraphVisualSettings().getZoomTo() == 0.0){
			Doc.getGraphVisualSettings().setZoomTo(Factor);
		} else{
			Doc.getGraphVisualSettings().setZoomTo(Doc.getGraphVisualSettings().getZoomTo() * Factor);
		}

		AffineTransform at;

		if (!TopLeft){
			at = SmoothZoom(offscreenImageGraphics.getTransform(), offscreenImageGraphics.getTransform().getScaleX()
					* Doc.getGraphVisualSettings().getZoomTo(), (int) (Doc.getGraphVisualSettings().getCanvasSize()
					.getWidth() / 2), (int) (Doc.getGraphVisualSettings().getCanvasSize().getHeight() / 2));
		} else{
			at = SmoothZoom(offscreenImageGraphics.getTransform(), offscreenImageGraphics.getTransform().getScaleX()
					* Doc.getGraphVisualSettings().getZoomTo(), 0, 0);
		}

		offscreenImageGraphics.setTransform(at);

		Doc.getGraphVisualSettings().setZoomTo(1.0);

		forceRepaint();
	}

	public void ZoomToGraphExtents(){
		ZoomToRectangle(GetBoundingRectangle(0.2));
	}

	public void ZoomToRectangle(final Rectangle r){
		double sx, sy;

		if (r.x == Integer.MAX_VALUE){
			return;
		}
		sx = Doc.getGraphVisualSettings().getCanvasSize().getWidth() / r.width;
		sy = Doc.getGraphVisualSettings().getCanvasSize().getHeight() / r.height;

		double zoomfExtent = Math.min(sx, sy) * 1.1;

		final AffineTransform At = new AffineTransform();
		At.scale(zoomfExtent, zoomfExtent);
		At.translate(-r.x - r.width / 2 + Doc.getGraphVisualSettings().getCanvasSize().getWidth() / (zoomfExtent * 2), -r.y
				- r.height / 2 + Doc.getGraphVisualSettings().getCanvasSize().getHeight() / (zoomfExtent * 2));

		final Point p = new Point(r.x + 1000, r.y + 1000);
		final Point p1 = new Point(r.x, r.y);
		At.transform(p, p1);

		Doc.getGraphVisualSettings().setTransform(At);
		offscreenImageGraphics.setTransform(At);

		forceRepaint();
	}

	@Override
	public void event(int eventCode, int sourceID, Object source, Object param){
		super.event(eventCode, sourceID, source, param);
		switch (eventCode){
			case MSG_GraphPanelSettingsChanged:
				if (param instanceof GraphPanelSettings){
					GraphPanelSettings settings = (GraphPanelSettings) param;
					setGraphPanelSettings(settings);
				}
				break;
			case MSG_FavoriteLoaded:
				double value = Doc.Subgraph.NodeSpace;
				nodeSpaceSlider.setValue((int) (value * 10));
				break;
			case MSG_PolygonModelChanged:
				float alpha = Doc.getPolygonAlpha();
				alphaSlider.setValue((int) (alpha * 100));
				break;
		}
	}

	public void nextPaintState(){
		getEdgePainter().nextPaintState();
	}

	private boolean isNodeDataChangeEnabled(){
		return validator.isNodeDataChangeEnabled()
				&& UserSettings.getBooleanAppletProperty("Toolbar_CreateNode_InUse", true);
	}

	private boolean isEdgeDataChangeEnabled(){
		return validator.isEdgeDataChangeEnabled()
				&& UserSettings.getBooleanAppletProperty("Toolbar_CreateEdge_InUse", true);
	}

	@Deprecated
	public Image getOffscreenImage(){
		return offscreenImage;
	}

	public Node findNode(final double x, final double y){
		Node ret = null;
		for (final Node n : Doc.Subgraph.getDisplayedNodes()){
			ChartType chartType = n.hasChart() ? Doc.getChartParams(n).getChartType() : null;
			if (n.IsPointInNode(chartType, x, y)){
				ret = n;
			}
		}
		return ret;
	}

	public void setFreezeRedraw(boolean freezeRedraw){
		this.freezeRedraw = freezeRedraw;
	}
}