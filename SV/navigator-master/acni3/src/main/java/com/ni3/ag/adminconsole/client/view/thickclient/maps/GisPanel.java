/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.maps;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
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

import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.Map;

public class GisPanel extends BasicGraphPanel implements MouseListener, MouseMotionListener, MouseWheelListener,
        ChangeListener, ActionListener{
	private static final long serialVersionUID = 3581404452243003126L;
	private static final Logger log = Logger.getLogger(GisPanel.class);
	public static final double REAL_WORLD_SCALE_FACTOR = 4500.00;
	public static final boolean showMetricScale = true;
	public static final boolean showImperialScale = true;
	public static final boolean staniBre = false;
	public static final double edgeBufferFactor = 0.15;
	private static int cPredefinedZooms = 11;
	private static double predefinedZooms[] = new double[] { 50000, 75000, 150000, 300000, 500000, 1000000, 2000000,
	        3000000, 5000000, 7500000, 10000000, 0 };

	private JSlider zoomSlider;
	private JButton zoomInButton;
	private JButton zoomOutButton;
	private GisLayer gisLayers[];
	private double fromX, toX, fromY, toY;
	private boolean dontZoomOnSlider = false;
	private int prevPanX, prevPanY, currentZoomIndex;

	volatile boolean getAreaInProgress, drawInProgress, areaNotDrawn;
	Color backgroundColor = new Color(120, 181, 255);
	double zoomfExtent, bufZoomF, drawnZoomf;
	boolean stopGetAreaThread = false;
	int buffXwd, buffYht;
	Dimension offscreensize, d, dimensionGisBuffer;
	Graphics2D offgraphics, offgraphicsGraph, offgraphicsGIS;
	Image offscreen, offscreenGraph, offscreenGIS;
	Rectangle bufRect, boundRect, drawnRect;
	Point npt = new Point();
	Point npt2 = new Point();
	GISPanelSettings gisSet;

	MapJobView parent;
	int mapId;
	String serverUrl;

	public GisPanel(MapJobView parent){
		super();
		this.parent = parent;

		setLayout(new GridLayout(1, 1));
		toolbar = new JToolBar(JToolBar.VERTICAL);

		toolbar.setRollover(true);
		createZoomSlider();

		d = new Dimension(0, 0);
		dimensionGisBuffer = new Dimension(4000, 2500);

	}

	public boolean needFullMapRefresh(int mapId, String serverUrl){
		if (this.mapId != mapId || (serverUrl != null && !serverUrl.equals(this.serverUrl))){
			return true;
		}
		return false;
	}

	public void initEmptyMap(Map map, String serverUrl){

		URLEx url = new URLEx(serverUrl + "/GetArea?MapID=" + map.getId() + "&MapLimits=T");
		log.debug(serverUrl + "/GetArea?MapID=" + map.getId() + "&MapLimits=T");

		String limits = url.readLine();

		if (!"-".equals(limits) && limits != null){
			StringTokenizerEx tokenizer = new StringTokenizerEx(limits, ",", false);
			int x1, x2, y1, y2;
			x1 = tokenizer.nextIntegerToken();
			y1 = tokenizer.nextIntegerToken();
			x2 = tokenizer.nextIntegerToken();
			y2 = tokenizer.nextIntegerToken();

			Rectangle rect = getBoundingRectangle(x1, x2, y1, y2, -0.2);

			if (rect != null)
				zoomToRectangle(rect);
		}

		url.close();
	}

	public void initMap(Map map, String serverUrl){
		this.mapId = map != null ? map.getId() : -1;
		this.serverUrl = serverUrl;
		zoomedToArea = false;
		boundRect = new Rectangle(0, 0, 0, 0);
		bufRect = new Rectangle(0, 0, 0, 0);
		drawnRect = new Rectangle(0, 0, 0, 0);

		bufZoomF = -10.0;
		drawnZoomf = -10.0;
		drawInProgress = false;
		getAreaInProgress = false;
		areaNotDrawn = true;

		currentZoomIndex = 0;

		gisSet = new GISPanelSettings();
		initListeners();
	}

	private void initListeners(){
		removeMouseListener(this);
		removeMouseWheelListener(this);
		removeComponentListener(this);
		zoomSlider.removeChangeListener(this);
		zoomInButton.removeActionListener(this);
		zoomOutButton.removeActionListener(this);

		addMouseListener(this);
		addMouseWheelListener(this);
		addComponentListener(this);
		zoomSlider.addChangeListener(this);
		zoomInButton.addActionListener(this);
		zoomOutButton.addActionListener(this);
	}

	public void setPredefinedZooms(double[] zooms){
		dontZoomOnSlider = true;
		predefinedZooms = zooms;
		if (cPredefinedZooms != zooms.length - 1){
			if (currentZoomIndex < 0)
				currentZoomIndex = 0;
			cPredefinedZooms = zooms.length - 1;
			if (currentZoomIndex >= cPredefinedZooms)
				currentZoomIndex = cPredefinedZooms - 1;
			zoomSlider.setMaximum(cPredefinedZooms - 1);
		}
		dontZoomOnSlider = false;
	}

	public void createZoomSlider(){
		// Vertical Slider 1

		zoomSlider = new JSlider(JSlider.VERTICAL, 0, cPredefinedZooms - 1, currentZoomIndex);
		zoomSlider.setMinimumSize(new Dimension(10, 10));
		zoomSlider.setPreferredSize(new Dimension(10, 300));
		zoomSlider.setPaintTicks(true);
		zoomSlider.setSnapToTicks(true);
		zoomSlider.setMajorTickSpacing(1);
		zoomSlider.setInverted(true);
		zoomSlider.setName("ZoomSlider");

		zoomInButton = new JButton(ImageLoader.loadIcon("/images/SearchPlus24.png"));
		zoomInButton.setMinimumSize(new Dimension(24, 24));
		zoomInButton.setPreferredSize(new Dimension(24, 24));
		zoomInButton.setToolTipText("Zoom in");
		zoomInButton.setActionCommand("ZoomIn");

		zoomOutButton = new JButton(ImageLoader.loadIcon("/images/SearchMinus24.png"));
		zoomOutButton.setMinimumSize(new Dimension(24, 24));
		zoomOutButton.setPreferredSize(new Dimension(24, 24));
		zoomOutButton.setToolTipText("Zoom out");
		zoomOutButton.setActionCommand("ZoomOut");

		toolbar.setMargin(new Insets(0, 0, 0, 0));
		toolbar.add(zoomInButton);
		toolbar.add(zoomSlider);
		toolbar.add(zoomOutButton);

		toolbar.setFloatable(false);
	}

	@Override
	public void renderView(Graphics g2, Dimension dim){
		renderVectorView(g2, dim);
	}

	public void renderVectorView(Graphics g2, Dimension dim){

	}

	public void drawDistanceLineMeter(Graphics2D g, double zoomf){
		String razmera;
		int duzina;

		double distance = (int) (60 * 0.000176388 * (REAL_WORLD_SCALE_FACTOR / zoomf));
		if (distance < 1000){
			distance = 1000;
		}

		if (distance < 10000){
			distance = (int) (distance / 1000) * 1000;
		} else if (distance < 100000){
			distance = (int) (distance / 10000) * 10000;
		} else{
			distance = (int) (distance / 100000) * 100000;
		}

		duzina = (int) (distance / (0.000176388 * REAL_WORLD_SCALE_FACTOR / zoomf));
		while (duzina > 200){
			duzina /= 2;
			distance /= 2.0;
		}

		if (distance < 1000)
			razmera = ((int) distance) + " m";
		else
			razmera = ((int) distance / 1000) + " km";

		BasicStroke bs = new BasicStroke(5);
		g.setColor(Color.black);
		g.setStroke(bs);
		g.drawLine(20, getHeight() - 20, 20 + duzina, getHeight() - 20);
		g.drawLine(20, getHeight() - 20, 20, getHeight() - 10);
		g.drawLine(20 + duzina, getHeight() - 20, 20 + duzina, getHeight() - 10);

		g.setColor(Color.white);
		for (int xo = -1; xo < 2; xo += 2)
			for (int yo = -1; yo < 2; yo += 2)
				g.drawString(razmera, 25 + xo, getHeight() - 2 + yo);

		g.setColor(Color.black);
		g.drawString(razmera, 25, getHeight() - 2);

		bs = new BasicStroke(1);
		g.setColor(Color.white);
		g.setStroke(bs);
		g.drawLine(20, getHeight() - 20, 20 + duzina, getHeight() - 20);
		g.drawLine(20, getHeight() - 20, 20, getHeight() - 10);
		g.drawLine(20 + duzina, getHeight() - 20, 20 + duzina, getHeight() - 10);
	}

	public void drawDistanceLineImperial(Graphics2D g, double zoomf){
		String razmera;
		int duzina;

		double distance = (int) (60 * 0.000176388 * (REAL_WORLD_SCALE_FACTOR / zoomf));
		if (distance < 1609){
			distance = 1609;
		}

		if (distance < 16090){
			distance = (int) (distance / 1609) * 1609;
		} else if (distance < 160900){
			distance = (int) (distance / 16090) * 16090;
		} else{
			distance = (int) (distance / 160900) * 160900;
		}

		duzina = (int) (distance / (0.000176388 * REAL_WORLD_SCALE_FACTOR / zoomf));
		while (duzina > 200){
			duzina /= 2;
			distance /= 2.0;
		}

		if (distance < 1609)
			razmera = (int) (distance / 0.304) + " feet";
		else
			razmera = (int) (distance / 1609) + " ml";

		BasicStroke bs = new BasicStroke(5);
		g.setColor(Color.black);
		g.setStroke(bs);
		g.drawLine(20, getHeight() - 20, 20 + duzina, getHeight() - 20);
		g.drawLine(20, getHeight() - 20, 20, getHeight() - 30);
		g.drawLine(20 + duzina, getHeight() - 20, 20 + duzina, getHeight() - 30);

		g.setColor(Color.white);
		for (int xo = -1; xo < 2; xo += 2)
			for (int yo = -1; yo < 2; yo += 2)
				g.drawString(razmera, 25 + xo, getHeight() - 25 + yo);

		g.setColor(Color.black);
		g.drawString(razmera, 25, getHeight() - 25);

		bs = new BasicStroke(1);
		g.setColor(Color.white);
		g.setStroke(bs);
		g.drawLine(20, getHeight() - 20, 20 + duzina, getHeight() - 20);
		g.drawLine(20, getHeight() - 20, 20, getHeight() - 30);
		g.drawLine(20 + duzina, getHeight() - 20, 20 + duzina, getHeight() - 30);
	}

	public double setPredefinedZoom(double targetZoom){
		currentZoomIndex = cPredefinedZooms - 1;

		for (int n = 0; n < cPredefinedZooms; n++)
			if (targetZoom <= (predefinedZooms[n] + predefinedZooms[n + 1]) / 2.0){
				currentZoomIndex = n;
				break;
			}

		setSliderIndex(currentZoomIndex);

		return predefinedZooms[currentZoomIndex];
	}

	void setSliderIndex(int index){
		dontZoomOnSlider = true;
		zoomSlider.setValue(index);
	}

	@Override
	public void stateChanged(ChangeEvent e){
		JSlider source = (JSlider) e.getSource();

		if ("ZoomSlider".equals(source.getName()) && !source.getValueIsAdjusting() && !dontZoomOnSlider){
			int val = (int) source.getValue();
			currentZoomIndex = val;
			zoomToIndex(-1, -1);
		}
		dontZoomOnSlider = false;

		graphDirty = true;
	}

	@Override
	public void actionPerformed(ActionEvent ae){
		String action = ae.getActionCommand();

		if ("ZoomOut".equals(action)){
			int index = zoomSlider.getValue();
			if (index < zoomSlider.getMaximum()){
				graphDirty = true;
				zoomSlider.setValue(index + 1);
			}
		} else if ("ZoomIn".equals(action)){
			int index = zoomSlider.getValue();
			if (index > zoomSlider.getMinimum()){
				graphDirty = true;
				zoomSlider.setValue(index - 1);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e){
	}

	@Override
	public void mouseEntered(MouseEvent e){
	}

	@Override
	public void mouseExited(MouseEvent e){
	}

	@Override
	public void mousePressed(MouseEvent e){
		if (e.getButton() != MouseEvent.BUTTON1){
			return;
		}
		log.debug("mouse pressed");
		addMouseMotionListener(this);

		Point pt = screenToGeoCoord(e.getX(), e.getY());

		if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1){
			int mdf = e.getModifiersEx();

			if (mdf == (InputEvent.CTRL_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK)){
				inRubberBand = true;
				rubberBandSelect = true;
				rubberBandRectangle.x = pt.x;
				rubberBandRectangle.y = pt.y;
				rubberBandRectangle.width = 0;
				rubberBandRectangle.height = 0;
			} else if (mdf == (InputEvent.ALT_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK)){
				inRubberBand = true;
				rubberBandSelect = false;
				rubberBandRectangle.x = pt.x;
				rubberBandRectangle.y = pt.y;
				rubberBandRectangle.width = 0;
				rubberBandRectangle.height = 0;
			}

			prevPanX = e.getX();
			prevPanY = e.getY();
		}

	}

	@Override
	public void mouseReleased(MouseEvent e){
		if (e.getButton() != MouseEvent.BUTTON1){
			return;
		}
		log.debug("mouse released");
		removeMouseMotionListener(this);

		if (inRubberBand){
			inRubberBand = false;
			if (rubberBandSelect){
				int w, h;

				w = rubberBandRectangle.width;
				if (rubberBandRectangle.width < 0){
					rubberBandRectangle.x += w;;
					rubberBandRectangle.width = -w;
				}

				h = rubberBandRectangle.height;
				if (rubberBandRectangle.height < 0){
					rubberBandRectangle.y += rubberBandRectangle.height;
					rubberBandRectangle.height = -h;
				}

				Point from = new Point(rubberBandRectangle.x, rubberBandRectangle.y);
				Point to = new Point(rubberBandRectangle.x + rubberBandRectangle.width, rubberBandRectangle.y
				        + rubberBandRectangle.height);
				setSelectionCoordinates(from, to);

			} else{
				zoomToRectangle(rubberBandRectangle);
			}
		}

		forceRepaint(true);
		e.consume();
	}

	@Override
	public void mouseDragged(MouseEvent e){
		log.debug("mouse dragged");

		if (inRubberBand
		        && (e.getModifiersEx() == (InputEvent.CTRL_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK) || e.getModifiersEx() == (InputEvent.ALT_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK))){
			Point pt = screenToGeoCoord(e.getX(), e.getY());
			rubberBandRectangle.width = pt.x - rubberBandRectangle.x;
			rubberBandRectangle.height = pt.y - rubberBandRectangle.y;
			forceRepaint();
		} else{
			double zoomf = offgraphics.getTransform().getScaleX();

			offgraphics.translate(((e.getX() - prevPanX) / zoomf), ((e.getY() - prevPanY) / zoomf));
			prevPanX = e.getX();
			prevPanY = e.getY();

			inRubberBand = false;
			forceRepaint();
		}

		e.consume();
	}

	@Override
	public void mouseMoved(MouseEvent e){
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e){
		int notches = e.getWheelRotation();

		int prevZI = currentZoomIndex;
		currentZoomIndex += Math.signum(notches);
		if (currentZoomIndex < 0)
			currentZoomIndex = 0;
		if (currentZoomIndex >= cPredefinedZooms)
			currentZoomIndex = cPredefinedZooms - 1;

		if (prevZI != currentZoomIndex){
			zoomToIndex(e.getX(), e.getY());
		}
	}

	private void setSelectionCoordinates(Point from, Point to){
		fromX = WGS84Conversion.convertMToLongitude(from.getX(), from.getY());
		toX = WGS84Conversion.convertMToLongitude(to.getX(), to.getY());
		fromY = WGS84Conversion.convertMToLatitude(from.getY());
		toY = WGS84Conversion.convertMToLatitude(to.getY());
		parent.setCoordinatesFromMap(fromX, toX, fromY, toY);
		log.debug("fromX=" + fromX + ", toX=" + toX + ", fromY=" + fromY + ", toY=" + toY);
	}

	public double[] getSelectionCoordinates(){
		return new double[] { fromX, toX, fromY, toY };
	}

	Point screenToGeoCoord(int x, int y){
		Point pt = new Point(x, y);
		Point npt = new Point();

		try{
			if (gisSet.getAt() != null)
				gisSet.getAt().inverseTransform(pt, npt);
		} catch (NoninvertibleTransformException e1){
			log.error(e1);
		}
		return npt;
	}

	public void zoomToIndex(int x, int y){
		double zoomTo = 1 / (predefinedZooms[currentZoomIndex] / REAL_WORLD_SCALE_FACTOR);

		if (x == -1){
			x = d.width / 2;
			y = d.height / 2;
		}

		AffineTransform at = smoothZoom(offgraphics.getTransform(), zoomTo, x, y);

		offgraphics.setTransform(at);

		setSliderIndex(currentZoomIndex);
		forceRepaint(true);
	}

	public void zoomToCoords(double x1, double x2, double y1, double y2){
		int gx1 = (int) (WGS84Conversion.convertLongitudeToM(x1, y1));
		int gy1 = (int) (WGS84Conversion.convertLatitudeToM(y1));
		int gx2 = (int) (WGS84Conversion.convertLongitudeToM(x2, y2));
		int gy2 = (int) (WGS84Conversion.convertLatitudeToM(y2));
		Rectangle rect = getBoundingRectangle(gx1, gx2, gy1, gy2, 0.2);

		if (rect != null){
			zoomToRectangle(rect);
			double zoomf = REAL_WORLD_SCALE_FACTOR * 1 / zoomfExtent;
			predefinedZooms[cPredefinedZooms] = predefinedZooms[cPredefinedZooms - 1];
			for (currentZoomIndex = 0; currentZoomIndex < cPredefinedZooms; currentZoomIndex++){
				if (zoomf < (predefinedZooms[currentZoomIndex] + predefinedZooms[currentZoomIndex + 1]) / 2.0)
					break;
			}

			if (currentZoomIndex == cPredefinedZooms)
				currentZoomIndex = cPredefinedZooms - 1;

			setSliderIndex(currentZoomIndex);
		}
	}

	public void zoomToRectangle(Rectangle r){
		double sx, sy;

		if (offgraphics != null){
			boolean dontCentre = false;
			sx = (double) d.getWidth() / r.width;
			sy = (double) d.getHeight() / r.height;

			zoomfExtent = Math.min(sx, sy); // * 1.1;

			if (Math.abs(r.width / 2.0 - (d.getWidth() / zoomfExtent) / 2.0) > r.width)
				dontCentre = true;

			if (Math.abs(r.height / 2.0 + (d.getHeight() / zoomfExtent) / 2.0) > r.height)
				dontCentre = true;

			double targetZoom = REAL_WORLD_SCALE_FACTOR / zoomfExtent;

			targetZoom = setPredefinedZoom(targetZoom);
			zoomfExtent = REAL_WORLD_SCALE_FACTOR / targetZoom;

			gisSet.setAt(new AffineTransform());
			gisSet.getAt().scale(zoomfExtent, zoomfExtent);
			double mx, my;

			if (dontCentre){
				mx = r.x;
				my = r.y;
			} else{
				mx = r.x + r.width / 2.0 - (d.getWidth() / zoomfExtent) / 2.0;
				my = r.y - r.height / 2.0 + (d.getHeight() / zoomfExtent) / 2.0;
			}

			gisSet.getAt().translate(-mx, -my);

			offgraphics.setTransform(gisSet.getAt());
		}
		forceRepaint(true);
	}

	public void forceRepaint(boolean directCall){
		if (directCall){
			graphDirty = true;
			renderView(getGraphics(), getSize());
		}
		forceRepaint();
	}

	public Rectangle getBoundingRectangle(int minx, int maxx, int miny, int maxy, double growPercent){
		boolean isGeocoded = true;
		zoomedToArea = true;

		Rectangle ret = null;
		if (isGeocoded){
			ret = new Rectangle(minx, miny, Math.max(maxx - minx, 4500), Math.max(maxy - miny, 4500));
			ret.grow((int) (ret.width * growPercent), (int) (ret.height * growPercent));

			boundRect.x = ret.x;
			boundRect.y = ret.y;
			boundRect.width = ret.width;
			boundRect.height = ret.height;
		}

		return ret;
	}

	public void refresh(){
		gisSet.getOffscreenArea().x = gisSet.getOffscreenArea().y = gisSet.getOffscreenArea().width = gisSet
		        .getOffscreenArea().height = 0;
		forceRepaint();
	}

	class GISLayerLabel{
		int DispClass;
		String Font;
		double Size;
		int Bold;
		int Shadow;
		boolean HaveBackgroundBox;
		Color Background;
		Color Foreground;
	}

	class GISLayerLine{
		Color BgColor;
		double BgWidth;
		int BgStyle;
		Color FgColor;
		double FgWidth;
		int FgStyle;
	}

	class GISLayerSymbol{
		int DispClass;
		char Type;
		int Size;
	}

	class GISLongestSegment{
		int ID;
		double len;
	}

	class GisLayer{
		int LayerID;
		int type;
		double ZoomMin, ZoomMax;
		double ZoomTextMin, ZoomTextMax;
		int DispClassMin, DispClassMax;
		boolean SameLabels;
		Color col;

		boolean inBuffer;

		int cSymbol;
		GISLayerSymbol Symbol[];
		GISLayerLine Line;

		int cLabel;
		GISLayerLabel Labels[];

		void loadLine(String s){
			StringTokenizerEx tokenizer = new StringTokenizerEx(s, "/", false);
			Line = new GISLayerLine();

			Line.FgColor = Utility.createColor(tokenizer.nextToken());
			Line.FgWidth = (int) (Double.valueOf(tokenizer.nextToken()) * ZoomMin / 1000.0 * REAL_WORLD_SCALE_FACTOR);
			Line.FgStyle = Integer.valueOf(tokenizer.nextToken());

			Line.BgColor = Utility.createColor(tokenizer.nextToken());
			Line.BgWidth = (int) (Double.valueOf(tokenizer.nextToken()) * ZoomMin / 1000.0 * REAL_WORLD_SCALE_FACTOR);
			Line.BgStyle = Integer.valueOf(tokenizer.nextToken());
		}

		void loadSymbol(String s){
			StringTokenizerEx tokenizer = new StringTokenizerEx(s, "/", false);

			Symbol[cSymbol] = new GISLayerSymbol();

			Symbol[cSymbol].DispClass = Integer.valueOf(tokenizer.nextToken());
			Symbol[cSymbol].Type = tokenizer.nextToken().charAt(0);
			Symbol[cSymbol].Size = (int) (Double.valueOf(tokenizer.nextToken()) * ZoomMin / 1000.0 * REAL_WORLD_SCALE_FACTOR);

			cSymbol++;
		}

		void loadSymbology(String s){
			if (type == 1)
				loadLine(s);
			else if (type == 0){
				cSymbol = 0;
				StringTokenizerEx tokenizer = new StringTokenizerEx(s, "#", false);
				while (tokenizer.hasMoreTokens()){
					tokenizer.nextToken();
					cSymbol++;
				}

				Symbol = new GISLayerSymbol[cSymbol];

				cSymbol = 0;
				tokenizer = new StringTokenizerEx(s, "#", false);
				while (tokenizer.hasMoreTokens()){
					loadSymbol(tokenizer.nextToken());
				}
			}
		}

		void loadLabel(String s){
			if (s.length() == 0 || "null".equals(s) || "-".equals(s))
				return;

			Labels[cLabel] = new GISLayerLabel();

			StringTokenizerEx tokenizer = new StringTokenizerEx(s, "/", false);
			Labels[cLabel].DispClass = Integer.valueOf(tokenizer.nextToken());
			Labels[cLabel].Font = tokenizer.nextToken();
			Labels[cLabel].Size = (int) (Double.valueOf(tokenizer.nextToken()) * ZoomMin
			        * (REAL_WORLD_SCALE_FACTOR / 1000.0) * 0.40);
			Labels[cLabel].Bold = Integer.valueOf(tokenizer.nextToken());
			Labels[cLabel].Shadow = Integer.valueOf(tokenizer.nextToken());

			String ln = tokenizer.nextToken();
			Labels[cLabel].HaveBackgroundBox = !(ln.charAt(0) == '-');

			if (Labels[cLabel].HaveBackgroundBox)
				Labels[cLabel].Background = Utility.createColor(ln);

			Labels[cLabel].Foreground = Utility.createColor(tokenizer.nextToken());

			cLabel++;
		}

		void loadLabels(String s){
			cLabel = 0;
			StringTokenizerEx tokenizer = new StringTokenizerEx(s, "#", false);
			while (tokenizer.hasMoreTokens()){
				tokenizer.nextToken();
				cLabel++;
			}

			Labels = new GISLayerLabel[cLabel];

			cLabel = 0;
			tokenizer = new StringTokenizerEx(s, "#", false);
			while (tokenizer.hasMoreTokens()){
				loadLabel(tokenizer.nextToken());
			}
		}
	}

	public static double[] getPredefinedZooms(){
		return predefinedZooms;
	}
}
