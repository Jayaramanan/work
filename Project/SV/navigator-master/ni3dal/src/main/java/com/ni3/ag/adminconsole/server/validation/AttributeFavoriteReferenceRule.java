/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ni3.ag.adminconsole.domain.Favorites;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.server.dao.FavoritesDAO;
import com.ni3.ag.adminconsole.server.service.util.XMLHelper;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class AttributeFavoriteReferenceRule implements ACValidationRule{

	private static Logger log = Logger.getLogger(AttributeFavoriteReferenceRule.class);
	private static final String QUERY_TAG = "Query";
	private static final String SECTION_TAG = "Section";
	private static final String CONDITION_TAG = "Condition";

	private static final String ENTITY_ID_ATTRIBUTE = "EntityID";
	private static final String ATTR_ID_ATTRIBUTE = "AttrID";

	private FavoritesDAO favoritesDAO;
	private List<ErrorEntry> errors;

	public void setFavoritesDAO(FavoritesDAO favoritesDAO){
		this.favoritesDAO = favoritesDAO;
	}

	public boolean performCheck(AbstractModel m){
		log.debug("perform check for attribute references from favorites");
		errors = new ArrayList<ErrorEntry>();
		SchemaAdminModel model = (SchemaAdminModel) m;

		List<ObjectAttribute> oAttrs = model.getAttributesToDelete();
		if (oAttrs == null || oAttrs.isEmpty())
			return true;
		ObjectDefinition od = oAttrs.get(0).getObjectDefinition();
		String odId = od.getId().toString();

		List<Favorites> favorites = favoritesDAO.getFavorites(od.getSchema(), Favorites.QUERY_MODE);
		for (ObjectAttribute oa : oAttrs){
			Set<Favorites> refFavorites = getReferencedFavorites(oa, odId, favorites);
			if (!refFavorites.isEmpty())
				addErrorMessage(oa, refFavorites);
		}

		return errors.isEmpty();
	}

	Set<Favorites> getReferencedFavorites(ObjectAttribute oa, String odId, List<Favorites> favorites){
		Set<Favorites> refFavorites = new HashSet<Favorites>();
		for (Favorites fav : favorites){
			Document doc = XMLHelper.loadDocument(fav.getData());
			if (doc == null)
				continue;
			NodeList queries = doc.getElementsByTagName(QUERY_TAG);
			for (int q = 0; queries != null && q < queries.getLength(); q++){
				Node queryNode = queries.item(q);
				if (isReferencedFromQuery(oa, odId, queryNode)){
					refFavorites.add(fav);
				}
			}
		}
		return refFavorites;
	}

	private boolean isReferencedFromQuery(ObjectAttribute oa, String odId, Node queryNode){
		NodeList sections = ((Element) queryNode).getElementsByTagName(SECTION_TAG);
		for (int s = 0; sections != null && s < sections.getLength(); s++){
			Node sectionNode = sections.item(s);
			NamedNodeMap attributes = sectionNode.getAttributes();
			Node entityAttr = attributes.getNamedItem(ENTITY_ID_ATTRIBUTE);

			if (entityAttr == null || entityAttr.getTextContent() == null || !entityAttr.getTextContent().equals(odId)){
				continue;
			}
			NodeList conditions = ((Element) sectionNode).getElementsByTagName(CONDITION_TAG);
			for (int c = 0; conditions != null && c < conditions.getLength(); c++){
				Node conditionNode = conditions.item(c);
				attributes = conditionNode.getAttributes();
				Node attrIdAttr = attributes.getNamedItem(ATTR_ID_ATTRIBUTE);
				if (attrIdAttr == null || attrIdAttr.getTextContent() == null)
					continue;
				String attrId = attrIdAttr.getTextContent();
				if (attrId != null && attrId.equals(oa.getId().toString())){
					return true;
				}
			}
		}
		return false;
	}

	private void addErrorMessage(ObjectAttribute attribute, Set<Favorites> refFavorites){

		log.debug("form error message");
		StringBuffer sb = new StringBuffer();
		for (Favorites fav : refFavorites){
			sb.append(", ");
			sb.append(fav.getName());
		}
		String[] params = new String[2];
		params[0] = attribute.getName();
		params[1] = sb.toString().substring(2);
		log.warn("attribute `" + attribute.getName() + "` is referenced from favorites: " + params[1]);
		errors.add(new ErrorEntry(TextID.MsgAttributeReferencedFromFavorite, params));
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}