/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.graph.painter.impl;

import java.util.ArrayList;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.domain.metaphor.NumericMetaphor;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.gui.graph.ConvexHull;
import com.ni3.ag.navigator.client.gui.graph.painter.MapNodePainter;
import com.ni3.ag.navigator.client.gui.map.MapPanel;
import com.ni3.ag.navigator.client.model.Ni3Document;
import com.ni3.ag.navigator.shared.domain.ChartType;

public class MapNodePainterImpl extends NodePainterImpl implements MapNodePainter{
	private static final Color SELECTED_FROM_COLOR = Color.BLUE;
	private static final Color SELECTED_TO_COLOR = Color.RED;
	private final Color SELECTED_COLOR;

	private MapPanel mapPanel;

	public MapNodePainterImpl(MapPanel mapPanel){
		this.mapPanel = mapPanel;
		SELECTED_COLOR = UserSettings.getColor("NODE_SELECTED_COLOR", Color.red);
	}

	@Override
	public void paint(Node n, Graphics2D g, Ni3Document doc, double scale, double blinkScaleFactor, final boolean showLabel){
		if (n.Obj == null || n.getLat() == 0 && n.getLon() == 0){
			return; // coordinates not set
		}
		final Point mp = mapPanel.getMapPosition();
		final Point position = mapPanel.computePosition(new Point2D.Double(n.getLon(), n.getLat()));
		position.translate(-mp.x, -mp.y);

		int width, height;
		Point startPosition;
		if (!n.hasChart()){
			Image image = getMetaphorImage(n, scale * blinkScaleFactor, doc.isShowNumericMetaphors());
			width = image.getWidth(null);
			height = image.getHeight(null);
			startPosition = getIconPosition(position, width, height, doc.isShowNumericMetaphors());

			Color[] halos = n.getHalos();
			if (halos.length > 0){
				Double typeRadius = Node.maxGraphHaloRadiusPerType.get(n.Type);
				if (typeRadius == null){
					typeRadius = Node.maxGraphHaloR;
				}

				drawHalo(g, position.x, position.y, typeRadius * scale, 4.0, halos);
			}
			if (n.selected){
				// paint node selection rectangle
				fillSelectionRectangle(g, startPosition, width, height);
			}
			// paint metaphor icon
			g.drawImage(image, startPosition.x, startPosition.y, null);
			boolean setting = UserSettings.getBooleanAppletProperty("showLabelsOnMap", true);
			if (showLabel && setting) {
				final FontMetrics fm = g.getFontMetrics();
				Color initial = g.getColor();
				g.setColor(Color.BLACK);
				paintNodeLabel(n, g, fm, null, startPosition.x + 10, startPosition.y + 30);
				g.setColor(initial);
			}
		} else{
			final double chartScale = scale / 1.5 * blinkScaleFactor;
			width = height = (int) (n.getChartR() * chartScale);
			startPosition = getIconPosition(position, width, height, false);

			if (n.selected){
				// paint node selection rectangle
				fillSelectionRectangle(g, startPosition, width, height);
			}
			// paint chart
			calculateChartParams(n, doc.getChartParams(n));
			paintChart(n, g, position.x, position.y, chartScale, true, doc.getChartFilter(n), doc.getChartParams(n));
		}

		if (n.selectedFrom || n.selectedTo){
			drawSelectionRectangle(g, startPosition, width, height, n.selectedFrom);
		}

		boolean fillPoly = doc.isPolygonNode(n.ID);
		if (fillPoly || (doc.isPolylineNode(n.ID) && doc.isBlinkingPolylineVisible()))
			paintPolygon(n, g, fillPoly, doc.getPolygonAlpha(), doc.getPolyColor(n.ID));
	}

	private Image getMetaphorImage(Node n, double scale, boolean showNumericMetaphors){
		Image icon = null;
		if (showNumericMetaphors){
			icon = scaleNumericMetaphor(n, scale);
		}
		if (icon == null){
			icon = scaleImage(n.Obj.getIcon(), scale);
		}
		return icon;
	}

	@Override
	public void paintPolygon(Node n, Graphics2D g, boolean fillPoly, float alpha, final Color polyColor){
		java.util.List<Point> childrenPoints = new ArrayList<Point>(50);
		final Point mp = mapPanel.getMapPosition();

		Point position = mapPanel.computePosition(new Point2D.Double(n.getLon(), n.getLat()));
		position.translate(-mp.x, -mp.y);
		childrenPoints.add(position);

		for (final Edge e : n.inEdges){
			if (e.isActive() && e.from.isActive() && e.from.getLat() != 0 && e.from.getLon() != 0){
				position = mapPanel.computePosition(new Point2D.Double(e.from.getLon(), e.from.getLat()));
				position.translate(-mp.x, -mp.y);
				if (!childrenPoints.contains(position)){
					childrenPoints.add(position);
				}
			}
		}

		for (final Edge e : n.outEdges){
			if (e.isActive() && e.to.isActive() && e.to.getLat() != 0 && e.to.getLon() != 0){
				position = mapPanel.computePosition(new Point2D.Double(e.to.getLon(), e.to.getLat()));
				position.translate(-mp.x, -mp.y);
				if (!childrenPoints.contains(position)){
					childrenPoints.add(position);
				}
			}
		}

		if (childrenPoints.size() < 3)
			return;

		Polygon polygon;
		if (childrenPoints.size() > 3){ // If triangle no work is needed
			polygon = new ConvexHull(childrenPoints).getConvexHull();
		} else{
			int[] x = new int[childrenPoints.size()];
			int[] y = new int[childrenPoints.size()];
			for (int i = 0; i < childrenPoints.size(); i++){
				x[i] = childrenPoints.get(i).x;
				y[i] = childrenPoints.get(i).y;
			}
			polygon = new Polygon(x, y, childrenPoints.size());
		}

		final Composite originalComposite = g.getComposite();
		g.setColor(polyColor);

		if (fillPoly){
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			g.fillPolygon(polygon);
		} else{
			g.setStroke(polyStroke);
			g.drawPolygon(polygon);
		}

		g.setComposite(originalComposite);
	}

	private void drawSelectionRectangle(Graphics2D g, Point iconPosition, int width, int height, boolean from){
		g.setStroke(new BasicStroke(1));
		g.setColor(from ? SELECTED_FROM_COLOR : SELECTED_TO_COLOR);
		g.drawRect(iconPosition.x - 5, iconPosition.y - 5, width + 10, height + 10);
	}

	private void fillSelectionRectangle(Graphics2D g, Point iconPosition, int width, int height){
		g.setStroke(new BasicStroke(1));
		g.setColor(SELECTED_COLOR);
		g.fillRect(iconPosition.x - 5, iconPosition.y - 5, width + 10, height + 10);
	}

	Point getIconPosition(Point position, int width, int height, boolean showNumericMetaphors){
		final Point p = new Point();
		if (showNumericMetaphors){
			p.x = position.x - (width / 3);
			p.y = position.y - height;
		} else{
			p.x = position.x - (width / 2);
			p.y = position.y - (height / 2);
		}
		return p;
	}

	private Image scaleImage(Image initialIcon, double scale){
		int w = initialIcon.getWidth(null);
		int h = initialIcon.getHeight(null);
		Image icon = initialIcon;
		if (!(initialIcon instanceof BufferedImage)){
			icon = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			icon.getGraphics().drawImage(initialIcon, 0, 0, null);
		}
		return icon.getScaledInstance((int) (w * scale), (int) (h * scale), Image.SCALE_SMOOTH);
	}

	private Image scaleNumericMetaphor(Node n, double scale){
		final NumericMetaphor metaphor = n.Obj.getNumericMetaphor();
		final int number = metaphor != null ? metaphor.getIndex() : 0;
		final Image image = NumericMetaphor.INITIAL_IMAGE;
		if (number == 0 || image == null){
			return null;
		}
		final int width = (int) (image.getWidth(null) * scale);
		final int height = (int) (image.getHeight(null) * scale);
		final Image scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics = (Graphics2D) scaledImage.getGraphics();

		final AffineTransform at = AffineTransform.getScaleInstance(scale, scale);
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setColor(NumericMetaphor.FONT_COLOR);
		graphics.setFont(NumericMetaphor.FONT);
		graphics.drawImage(image, at, null);

		final FontMetrics fm = graphics.getFontMetrics();
		final int stringWidth = fm.stringWidth(String.valueOf(number));
		final int x = (image.getWidth(null) - stringWidth) / 2;

		graphics.setTransform(at);
		graphics.drawString(String.valueOf(number), x, 10);

		graphics.dispose();
		return scaledImage;

	}

	@Override
	public boolean isPointOnNode(Node node, Point point, boolean showNumericMetaphors, ChartType chartType, double scale){
		if (node.getLat() == 0 && node.getLon() == 0){
			return false; // coordinates not set
		}
		final Point mp = mapPanel.getMapPosition();
		final double lon = node.getLon();
		final double lat = node.getLat();
		final Point position = mapPanel.computePosition(new Point2D.Double(lon, lat));
		position.translate(-mp.x, -mp.y);
		final Rectangle rect;
		if (node.hasChart()){
			rect = getChartBoundingRectangle(node, chartType, position, scale);
		} else{
			rect = getIconBoundingRectangle(node, showNumericMetaphors, position, scale);
		}
		final boolean inPoint = rect.contains(point);

		return inPoint;
	}

	private Rectangle getIconBoundingRectangle(Node node, boolean showNumericMetaphors, Point position, double scale){
		final Image image = getMetaphorImage(node, scale, showNumericMetaphors);
		final int width = (int) (image.getWidth(null) * scale);
		final int height = (int) (image.getHeight(null) * scale);

		final Point iconPosition = getIconPosition(position, width, height, showNumericMetaphors);

		final Rectangle r = new Rectangle(iconPosition.x, iconPosition.y, width, height);
		return r;
	}

	private Rectangle getChartBoundingRectangle(Node n, ChartType chartType, Point position, double scale){
		double chartScale = scale / 1.5;
		Rectangle rect;
		final int width = (int) (n.getMetaphorWidth() * chartScale);
		final int height = (int) (n.getMetaphorHeight() * chartScale);
		if (chartType == ChartType.Bar){
			int addHeight = (int) (n.getChartNegativePartHeight() * chartScale);
			rect = new Rectangle((int) position.x, (int) position.y - height, width, height + addHeight);
		} else{
			Point pos = getIconPosition(position, width, height, false);
			rect = new Rectangle(pos.x, pos.y, width, height);
		}
		return rect;
	}
}
