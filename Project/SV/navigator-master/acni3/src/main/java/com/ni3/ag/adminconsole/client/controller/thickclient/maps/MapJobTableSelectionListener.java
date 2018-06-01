/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.maps;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.view.thickclient.maps.GisPanel;
import com.ni3.ag.adminconsole.client.view.thickclient.maps.MapJobView;
import com.ni3.ag.adminconsole.domain.Map;
import com.ni3.ag.adminconsole.domain.MapJob;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.MapJobModel;

public class MapJobTableSelectionListener implements ListSelectionListener{

	MapJobController controller;
	private static final Logger log = Logger.getLogger(MapJobTableSelectionListener.class);

	public MapJobTableSelectionListener(MapJobController controller){
		this.controller = controller;
	}

	public void valueChanged(ListSelectionEvent e){
		if (e.getValueIsAdjusting()){
			return;
		}
		MapJobView view = controller.getView();
		MapJobModel model = controller.getModel();
		MapJob job = view.getSelectedJob();
		GisPanel gisPanel = view.getGisPanel();
		String rasterServer = model.getRasterServer();
		Map map = model.getMap();
		DatabaseInstance di = model.getCurrentDatabaseInstance();
		if (di != null && !di.isConnected())
			return;
		if (rasterServer == null || rasterServer.trim().isEmpty() || !controller.isRasterServerReachable()){
			List<ErrorEntry> errors = new ArrayList<ErrorEntry>();
			errors.add(new ErrorEntry(TextID.MsgRasterServerUnreachable, new String[] { rasterServer }));
			view.renderErrors(errors);
			return;
		}
		if (map == null)
			return;
		if (job != null && job.getX1() != null && job.getX2() != null && job.getY1() != null && job.getY2() != null
		        && job.getScale() != null && !job.getScale().isEmpty()){
			double x1 = job.getX1().doubleValue();
			double x2 = job.getX2().doubleValue();
			double y1 = job.getY1().doubleValue();
			double y2 = job.getY2().doubleValue();
			log.debug("zooming to coords: x1=" + x1 + ",x2=" + x2 + ",y1=" + y1 + ",y2=" + y2);
			if (gisPanel.needFullMapRefresh(map.getId(), rasterServer)){
				gisPanel.initMap(map, rasterServer);
				gisPanel.forceRepaint(true);
			}
			gisPanel.setPredefinedZooms(parseScale(job.getScale()));
			gisPanel.forceRepaint(true);
			gisPanel.zoomToCoords(x1, x2, y1, y2);
		} else{
			if (gisPanel.needFullMapRefresh(map.getId(), rasterServer)){
				gisPanel.initMap(map, rasterServer);
				gisPanel.forceRepaint(true);
				gisPanel.setPredefinedZooms(GisPanel.getPredefinedZooms());
				gisPanel.forceRepaint(true);
				gisPanel.initEmptyMap(map, rasterServer);
			}
		}
	}

	public double[] parseScale(String scale){
		String[] zooms = scale.split(",");
		double[] result = new double[zooms.length + 1];
		for (int i = 0; i < zooms.length; i++){
			result[i] = Double.valueOf(zooms[i]);
		}
		return result;
	}
}
