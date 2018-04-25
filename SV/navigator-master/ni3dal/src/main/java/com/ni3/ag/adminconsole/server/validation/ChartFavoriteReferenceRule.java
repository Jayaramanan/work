/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.Favorites;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.server.dao.FavoritesDAO;
import com.ni3.ag.adminconsole.server.service.util.XMLHelper;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.ChartModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class ChartFavoriteReferenceRule implements ACValidationRule{

	private static Logger log = Logger.getLogger(ChartFavoriteReferenceRule.class);
	private static final String NI3_TAG = "NI3";

	private static final String CHART_ID_ATTRIBUTE = "ChartID";

	private FavoritesDAO favoritesDAO;
	private List<ErrorEntry> errors;

	public void setFavoritesDAO(FavoritesDAO favoritesDAO){
		this.favoritesDAO = favoritesDAO;
	}

	public boolean performCheck(AbstractModel m){
		log.debug("perform check for chart reference from favorites");
		errors = new ArrayList<ErrorEntry>();
		ChartModel model = (ChartModel) m;
		if (!model.isChartSelected())
			return true;
		Chart chart = (Chart) model.getCurrentObject();
		Schema schema = chart.getSchema();
		List<Favorites> favorites = favoritesDAO.getFavorites(schema, null);
		Set<Favorites> refFavorites = getReferencedFavorites(chart.getId().toString(), favorites);
		if (!refFavorites.isEmpty())
			addErrorMessage(chart, refFavorites);

		return errors.isEmpty();
	}

	Set<Favorites> getReferencedFavorites(String chartId, List<Favorites> favorites){
		Set<Favorites> refFavorites = new HashSet<Favorites>();
		for (Favorites fav : favorites){
			Document doc = XMLHelper.loadDocument(fav.getData());
			if (doc == null)
				continue;
			NodeList ni3Nodes = doc.getElementsByTagName(NI3_TAG);
			for (int q = 0; ni3Nodes != null && q < ni3Nodes.getLength(); q++){
				Node ni3Node = ni3Nodes.item(q);
				NamedNodeMap attributes = ni3Node.getAttributes();
				Node chartAttr = attributes.getNamedItem(CHART_ID_ATTRIBUTE);

				if (chartAttr != null && chartAttr.getTextContent() != null && chartAttr.getTextContent().equals(chartId)){
					refFavorites.add(fav);
					break;
				}
			}
		}
		return refFavorites;
	}

	private void addErrorMessage(Chart chart, Set<Favorites> refFavorites){
		log.debug("form error message");
		StringBuffer sb = new StringBuffer();
		for (Favorites fav : refFavorites){
			sb.append(", ");
			sb.append(fav.getName());
		}
		String[] params = new String[2];
		params[0] = chart.getName();
		params[1] = sb.toString().substring(2);
		log.warn("chart `" + chart.getName() + "` is referenced from favorites: " + params[1]);
		errors.add(new ErrorEntry(TextID.MsgChartReferencedFromFavorite, params));
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}
}