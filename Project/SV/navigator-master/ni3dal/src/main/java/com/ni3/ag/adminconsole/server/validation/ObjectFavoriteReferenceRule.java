/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.validation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ni3.ag.adminconsole.domain.Favorites;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.server.dao.FavoritesDAO;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class ObjectFavoriteReferenceRule implements ACValidationRule{

	private static Logger log = Logger.getLogger(ObjectFavoriteReferenceRule.class);
	private static final String QUERY_TAG = "Query";
	private static final String SECTION_TAG = "Section";
	private static final String CONDITION_TAG = "Condition";

	private static final String ENTITY_ID_ATTRIBUTE = "EntityID";

	private FavoritesDAO favoritesDAO;
	private List<ErrorEntry> errors;

	public void setFavoritesDAO(FavoritesDAO favoritesDAO){
		this.favoritesDAO = favoritesDAO;
	}

	public boolean performCheck(AbstractModel m){
		log.debug("perform check for object definition reference from favorites");
		errors = new ArrayList<ErrorEntry>();
		SchemaAdminModel model = (SchemaAdminModel) m;
		ObjectDefinition od = model.getCurrentObjectDefinition();
		if (od == null || od.getObjectAttributes() == null)
			return true;
		String odId = od.getId().toString();
		List<Favorites> favorites = favoritesDAO.getFavorites(od.getSchema(), Favorites.QUERY_MODE);
		Set<Favorites> refFavorites = getReferencedFavorites(odId, favorites);
		if (!refFavorites.isEmpty())
			addErrorMessage(od, refFavorites);

		return errors.isEmpty();
	}

	Set<Favorites> getReferencedFavorites(String odId, List<Favorites> favorites){
		Set<Favorites> refFavorites = new HashSet<Favorites>();
		for (Favorites fav : favorites){
			Document doc = loadDocument(fav.getData());
			if (doc == null)
				continue;
			NodeList queries = doc.getElementsByTagName(QUERY_TAG);
			for (int q = 0; queries != null && q < queries.getLength(); q++){
				Node queryNode = queries.item(q);
				if (isReferencedFromQuery(odId, queryNode)){
					refFavorites.add(fav);
				}
			}
		}
		return refFavorites;
	}

	private boolean isReferencedFromQuery(String odId, Node queryNode){
		NodeList sections = ((Element) queryNode).getElementsByTagName(SECTION_TAG);
		for (int s = 0; sections != null && s < sections.getLength(); s++){
			Node sectionNode = sections.item(s);
			NamedNodeMap attributes = sectionNode.getAttributes();
			Node entityAttr = attributes.getNamedItem(ENTITY_ID_ATTRIBUTE);

			if (entityAttr == null || entityAttr.getTextContent() == null || !entityAttr.getTextContent().equals(odId)){
				continue;
			}
			NodeList conditions = ((Element) sectionNode).getElementsByTagName(CONDITION_TAG);
			if (conditions != null && conditions.getLength() > 0)
				return true;
		}
		return false;
	}

	private void addErrorMessage(ObjectDefinition od, Set<Favorites> refFavorites){
		log.debug("form error message");
		StringBuffer sb = new StringBuffer();
		for (Favorites fav : refFavorites){
			sb.append(", ");
			sb.append(fav.getName());
		}
		String[] params = new String[2];
		params[0] = od.getName();
		params[1] = sb.toString().substring(2);
		log.warn("object definition `" + od.getName() + "` is referenced from favorites: " + params[1]);
		errors.add(new ErrorEntry(TextID.MsgObjectReferencedFromFavorite, params));
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

	private Document loadDocument(String xml){
		Document doc = null;
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes());

			doc = db.parse(bis);
		} catch (ParserConfigurationException ex){
			log.error("Error parsing xml " + xml, ex);
		} catch (SAXException ex){
			log.error("Error parsing xml " + xml, ex);
		} catch (IOException ex){
			log.error("Error parsing xml " + xml, ex);
		}
		return doc;
	}
}