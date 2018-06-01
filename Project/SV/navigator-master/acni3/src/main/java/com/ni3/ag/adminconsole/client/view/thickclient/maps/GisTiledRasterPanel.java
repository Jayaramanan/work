/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.maps;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class GisTiledRasterPanel extends GisPanel{
	private static Logger log = Logger.getLogger(GisTiledRasterPanel.class);
	private static Point oldNpt = new Point(0, 0);
	private int cBuffer, lastcBuffer;
	private GISTile buffer[];
	private GetRasterAreaThread thrd;
	private boolean dragInProgress;
	private int reloadCycle;
	private HashMap<String, GISTile> tilePool;

	public GisTiledRasterPanel(MapJobView parent){
		super(parent);

		reloadCycle = 0;
		cBuffer = lastcBuffer = 0;
		buffer = new GISTile[100];
		dragInProgress = false;
		tilePool = new HashMap<String, GISTile>(256);

	}

	boolean isRasterAreaInBuffer(int x1, int y1, int x2, int y2, double newZoomF){
		if (drawnRect.contains(x1, y1, x2 - x1, y2 - y1)){
			return true;
		}

		return false;

	}

	void getRasterArea(int x1, int y1, int x2, int y2, int w, int h, int LayerID, double newZoomF){
		if (drawnRect.contains(x1, y1, x2 - x1, y2 - y1) && bufZoomF == newZoomF){
			return;
		}

		bufZoomF = newZoomF;

		if (!getAreaInProgress && !areaNotDrawn){
			getAreaInProgress = true;

			thrd = new GetRasterAreaThread();

			thrd.x1 = x1;
			thrd.x2 = x2;
			thrd.y1 = y1;
			thrd.y2 = y2;
			thrd.w = w;
			thrd.h = h;
			thrd.LayerID = LayerID;
			thrd.newZoomF = newZoomF;

			stopGetAreaThread = false;
			thrd.start();
		}
	}

	class GetRasterAreaThread extends Thread{
		int x1, y1, x2, y2, LayerID;
		int w, h;
		double newZoomF;

		public void run(){
			try{
				getAreaInProgress = true;

				reloadCycle++;
				newZoomF = 1 / newZoomF;

				int wd = 0;
				int ht = 0;

				int minLastUsed = Integer.MAX_VALUE;
				GISTile tile;
				Set<String> keys = tilePool.keySet();
				for (String s : keys){
					tile = tilePool.get(s);
					tile.imageInUse = false;
					if (tile.getLastUsed() < minLastUsed)
						minLastUsed = tile.getLastUsed();
				}

				URLEx url;

				url = new URLEx(serverUrl + "/GetArea?MapID=" + mapId + "&Rct=" + x1 + "," + y1 + "," + x2 + "," + y2
				        + "&Zoomf=" + (int) (REAL_WORLD_SCALE_FACTOR * newZoomF));
				log.debug(serverUrl + "/GetArea?MapID=" + mapId + "&Rct=" + x1 + "," + y1 + "," + x2 + "," + y2 + "&Zoomf="
				        + (int) (REAL_WORLD_SCALE_FACTOR * newZoomF));

				if (stopGetAreaThread){
					cBuffer = 0;
					getAreaInProgress = false;
					return;
				}

				int cRasters = 0;
				String Rasters[] = new String[500];

				for (int ii = 0; ii < cBuffer; ii++)
					buffer[ii] = null;

				cBuffer = lastcBuffer = 0;

				if (url.getConnection() == null || !url.HTTPRead()) // GIS Server unreachable
				{
					cBuffer = 0;
					getAreaInProgress = false;
					return;
				}

				while ((Rasters[cRasters] = url.readLine()) != null){
					if (stopGetAreaThread){
						cBuffer = 0;
						getAreaInProgress = false;
						return;
					}

					if ("-".equals(Rasters[cRasters]))
						break;

					buffer[cBuffer] = tilePool.get(Rasters[cRasters]);

					if (buffer[cBuffer] == null){
						cRasters++;
					} else{
						buffer[cBuffer].loadImage();
						buffer[cBuffer].setLastUsed(reloadCycle);
						cBuffer++;
					}
				}
				url.close();

				for (int n = 0; n < cRasters; n++){
					if (stopGetAreaThread){
						cBuffer = 0;
						getAreaInProgress = false;
						return;
					}

					tile = tilePool.get(Rasters[n]);
					if (tile == null)
						tile = new GISTile(Rasters[n], null);
					tile.getRasterFromServer(serverUrl);
					buffer[cBuffer] = tile;

					buffer[cBuffer].loadImage();
					buffer[cBuffer].setLastUsed(reloadCycle);
					tilePool.put(Rasters[n], tile);
					Rasters[n] = null;
					cBuffer++;
				}

				keys = tilePool.keySet();
				for (String s : keys){
					tile = tilePool.get(s);
					tile.imageInUse = false;
				}

				for (int n = 0; n < cBuffer; n++)
					buffer[n].imageInUse = true;

				for (String s : keys){
					tile = tilePool.get(s);
					if (!tile.imageInUse)
						tile.disposeImage();
				}

				while (tilePool.size() > 150){
					int ctoRemove = 0;
					String toRemove[] = new String[100];

					keys = tilePool.keySet();
					for (String s : keys){
						tile = tilePool.get(s);

						if (!tile.imageInUse && tile.getLastUsed() == minLastUsed){
							log.debug("Remove from cache " + tile.getRasterID() + "-" + tile.getTileID());
							toRemove[ctoRemove] = s;
							ctoRemove++;
						}
					}

					for (int ii = 0; ii < ctoRemove; ii++)
						tilePool.remove(toRemove[ii]);

					minLastUsed++;
				}

				for (int n = 0; n < cBuffer; n++){
					if (buffer[n].getBufRect().x < x1)
						x1 = buffer[n].getBufRect().x;

					if (buffer[n].getBufRect().y < y1)
						y1 = buffer[n].getBufRect().y;

					if (buffer[n].getBufRect().x + buffer[n].getBufRect().width > x2)
						x2 = buffer[n].getBufRect().x + buffer[n].getBufRect().width;

					if (buffer[n].getBufRect().y + buffer[n].getBufRect().height > y2)
						y2 = buffer[n].getBufRect().y + buffer[n].getBufRect().height;
				}

				bufRect.x = x1 - wd;
				bufRect.y = y1 - ht;
				bufRect.width = (x2 - x1) + wd * 2;
				bufRect.height = (y2 - y1) + ht * 2;

				buffXwd = wd;
				buffYht = ht;

				areaNotDrawn = true;
				getAreaInProgress = false;
			} catch (java.lang.OutOfMemoryError error){
				log.error(error);
				JOptionPane.showMessageDialog(null, "Java VM is out of memory. Ni3 will terminate.");
				log.error("*******************************************************\nJava VM is out of memory. Ni3 will terminate.*******************************************************\n");
				Runtime.getRuntime().exit(1);
			}

		}
	}

	@Override
	public void renderView(Graphics g2, Dimension dim){
		renderRasterView(g2, dim);
	}

	public void renderRasterView(Graphics g2, Dimension dim){
		Graphics2D g = (Graphics2D) g2;

		if (gisSet == null || mapId < 0)
			return;

		if (inRubberBand || graphDirty){
			d = getSize();

			if (d.width == 0 || d.height == 0)
				return;

			if (offscreensize == null)
				offscreensize = new Dimension(0, 0);

			if ((offscreen == null) || (d.width != offscreensize.width) || (d.height != offscreensize.height)){
				areaNotDrawn = false;
				gisSet.setOffscreenArea(new Rectangle(0, 0, 0, 0));

				if (offgraphics != null)
					gisSet.setAt(offgraphics.getTransform());
				else{
					if (gisSet != null && gisSet.getAt() != null)
						setPredefinedZoom(REAL_WORLD_SCALE_FACTOR / gisSet.getAt().getScaleX());
					else
						gisSet.setAt(null);
				}

				offscreen = createImage(d.width, d.height);
				if (offscreenGIS == null){
					offscreenGIS = createImage(dimensionGisBuffer.width, dimensionGisBuffer.height);
					offgraphicsGIS = (Graphics2D) offscreenGIS.getGraphics();
				}
				offscreenGraph = createImage(d.width, d.height);
				offscreensize = new Dimension(d.width, d.height);

				if (offgraphics != null)
					offgraphics.dispose();

				if (offgraphicsGraph != null)
					offgraphicsGraph.dispose();

				offgraphics = (Graphics2D) offscreen.getGraphics();
				offgraphicsGraph = (Graphics2D) offscreenGraph.getGraphics();
				offgraphics.setFont(getFont());
				offgraphicsGraph.setFont(getFont());

				if (gisSet.getAt() != null){
					offgraphics.setTransform(gisSet.getAt());
					offgraphicsGraph.setTransform(gisSet.getAt());
					offgraphicsGIS.setTransform(gisSet.getAt());
				}
			}

			gisSet.setAt(offgraphics.getTransform());

			Point pt = new Point(0, 0);

			try{
				log.debug("start " + npt);
				gisSet.getAt().inverseTransform(pt, npt);

				pt.x = d.width;
				pt.y = d.height;
				gisSet.getAt().inverseTransform(pt, npt2);

				gisSet.setVisibleRect(new Rectangle(npt.x, npt.y, npt2.x - npt.x, npt2.y - npt.y));

				if (!oldNpt.equals(npt)){
					oldNpt = new Point(npt);
				}
			} catch (NoninvertibleTransformException e1){
				log.error(e1);
			}

			boolean newlyDrawn = false;

			boolean inBuffer = isRasterAreaInBuffer(npt.x, npt.y, npt2.x, npt2.y, 1.0 / gisSet.getAt().getScaleX());
			boolean b2 = drawnZoomf != 1.0 / gisSet.getAt().getScaleX();
			log.debug("getAreaInProgres=" + getAreaInProgress + ", areaNotDrawn=" + areaNotDrawn + ", dragInProgress="
			        + dragInProgress + ", inBuffer=" + inBuffer + ",zoom=" + b2);
			if (!getAreaInProgress
			        && !areaNotDrawn
			        && (!isRasterAreaInBuffer(npt.x, npt.y, npt2.x, npt2.y, 1.0 / gisSet.getAt().getScaleX()) || drawnZoomf != 1.0 / gisSet.getAt()
			                .getScaleX())){
				if (!dragInProgress){
					getRasterArea(npt.x, npt.y, npt2.x, npt2.y, d.width, d.height, -1, gisSet.getAt().getScaleX());
					log.debug("return");
					return;
				}
			}

			if (!dragInProgress)
				getRasterArea(npt.x, npt.y, npt2.x, npt2.y, d.width, d.height, -1, gisSet.getAt().getScaleX());

			// TODO: remove empty while loop
			while (getAreaInProgress);

			if ((!getAreaInProgress && areaNotDrawn) || getAreaInProgress){
				drawInProgress = true;

				if (lastcBuffer != cBuffer){
					AffineTransform atx = new AffineTransform(offgraphics.getTransform());
					atx.translate(buffXwd, buffYht);
					offgraphicsGIS.setTransform(atx);

					offgraphicsGIS.setColor(backgroundColor);
					offgraphicsGIS.fillRect(Integer.MIN_VALUE / 2, Integer.MIN_VALUE / 2, Integer.MAX_VALUE,
					        Integer.MAX_VALUE);

					for (int ii = 0; ii < cBuffer; ii++)
						if (buffer[ii].getImage() != null){
							offgraphicsGIS.drawImage(buffer[ii].getImage().getImage(), buffer[ii].getBufRect().x,
							        buffer[ii].getBufRect().y,
							        (int) (buffer[ii].getImage().getImage().getWidth(null) / gisSet.getAt().getScaleX()),
							        (int) (buffer[ii].getImage().getImage().getHeight(null) / gisSet.getAt().getScaleX()), null);

							if (Utility.DEBUG){
								offgraphicsGIS.setColor(Color.black);
								offgraphicsGIS.drawRect(buffer[ii].getBufRect().x, buffer[ii].getBufRect().y, (int) (buffer[ii].getImage()
								        .getImage().getWidth(null) / gisSet.getAt().getScaleX()), (int) (buffer[ii].getImage()
								        .getImage().getHeight(null) / gisSet.getAt().getScaleX()));

								offgraphicsGIS.setFont(new Font("Arial", Font.PLAIN, 1000));
								offgraphicsGIS.drawString(new Integer(buffer[ii].getTileID()).toString(),
								        buffer[ii].getBufRect().x + 50, buffer[ii].getBufRect().y + 500);
							}
						}

					lastcBuffer = cBuffer;
				}

				if (!getAreaInProgress){
					areaNotDrawn = false;
					if (cBuffer == 0){
						offgraphicsGIS.setColor(backgroundColor);
						offgraphicsGIS.fillRect(npt.x, npt.y, (int) (d.width / gisSet.getAt().getScaleX()),
						        (int) (d.height / gisSet.getAt().getScaleX()));
					}
				}

				newlyDrawn = true;
				drawInProgress = false;
				drawnZoomf = 1.0 / gisSet.getAt().getScaleX();

				drawnRect.x = npt.x;
				drawnRect.y = npt.y;
				drawnRect.width = npt2.x - npt.x;
				drawnRect.height = npt2.y - npt.y;
			}

			if (newlyDrawn || npt.x != gisSet.getOffscreenArea().x || npt.y != gisSet.getOffscreenArea().y
			        || gisSet.getOffscreenArea().width != (npt2.x - npt.x) || gisSet.getOffscreenArea().height != (npt2.y - npt.y)){
				Point xpt = new Point(npt.x, npt.y);
				Point xgpt = new Point();
				offgraphicsGIS.getTransform().transform(xpt, xgpt);

				offgraphics.setColor(Color.white);
				offgraphics.fillRect(npt.x, npt.y, npt2.x - npt.x, npt2.y - npt.y);
				AffineTransform anull = new AffineTransform();

				offgraphics.setTransform(anull);
				offgraphics.drawImage(offscreenGIS, -xgpt.x, -xgpt.y, null);
				offgraphics.setTransform(gisSet.getAt());

				int x = gisSet.getOffscreenArea().x;
				gisSet.setOffscreenArea(new Rectangle(x, npt.y, npt2.x - npt.x, npt2.y - npt.y));
			}

			AffineTransform atg = g.getTransform();

			Point gpt = new Point();
			try{
				pt = new Point(0, 0);
				atg.inverseTransform(pt, gpt);
			} catch (NoninvertibleTransformException e1){
				log.error(e1);
			}

			offgraphicsGraph.setColor(backgroundColor);
			offgraphicsGraph.fillRect(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);

			offgraphicsGraph.setTransform(atg);
			offgraphicsGraph.drawImage(offscreen, gpt.x, gpt.y, null);

			offgraphicsGraph.setTransform(gisSet.getAt());

			offgraphicsGraph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			offgraphicsGraph.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
			        RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
			offgraphicsGraph.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

			if (inRubberBand){
				int wdt = (int) (4 / gisSet.getAt().getScaleX());
				BasicStroke pen = new BasicStroke(wdt);

				offgraphicsGraph.setStroke(pen);

				if (rubberBandSelect)
					offgraphicsGraph.setColor(Color.gray);
				else
					offgraphicsGraph.setColor(Color.red);

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

				offgraphicsGraph.drawRect(x, y, w, h);
			}
		}
		g.drawImage(offscreenGraph, 0, 0, null);

		String s;
		s = "1:" + (long) (REAL_WORLD_SCALE_FACTOR / gisSet.getAt().getScaleX());
		g.setFont(new Font("Arial", Font.BOLD, 14));
		g.setColor(Color.white);

		for (int xo = -1; xo < 2; xo += 2)
			for (int yo = -1; yo < 2; yo += 2)
				g.drawString(s, 20 + xo, 20 + yo);

		g.setColor(Color.black);
		g.drawString(s, 20, 20);

		if (showMetricScale)
			drawDistanceLineMeter(g, gisSet.getAt().getScaleX());

		if (showImperialScale)
			drawDistanceLineImperial(g, gisSet.getAt().getScaleX());

		if (!getAreaInProgress)
			graphDirty = false;
		log.debug("end " + npt);
	}

	public void mouseDragged(MouseEvent e){
		if (!(inRubberBand && (e.getModifiersEx() == (InputEvent.CTRL_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK) || e
		        .getModifiersEx() == (InputEvent.ALT_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK))) && !getAreaInProgress){
			dragInProgress = true;
		}

		super.mouseDragged(e);
	}

	public void mouseReleased(MouseEvent e){
		dragInProgress = false;
		super.mouseReleased(e);
	}
}
