/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.controller.favorites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JOptionPane;

import com.ni3.ag.navigator.shared.domain.User;
import org.apache.log4j.Logger;

import com.ni3.ag.navigator.client.controller.HistoryManager;
import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.controller.HistoryManager.HistoryItem;
import com.ni3.ag.navigator.client.controller.charts.SNA;
import com.ni3.ag.navigator.client.controller.geoanalytics.GeoAnalyticsController;
import com.ni3.ag.navigator.client.controller.graph.GraphController;
import com.ni3.ag.navigator.client.domain.Context;
import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.domain.DataFilter;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.Favorite;
import com.ni3.ag.navigator.client.domain.Folder;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.domain.cache.IconCache;
import com.ni3.ag.navigator.client.domain.query.Query;
import com.ni3.ag.navigator.client.gateway.FavoritesFolderGateway;
import com.ni3.ag.navigator.client.gateway.FavoritesGateway;
import com.ni3.ag.navigator.client.gateway.GraphGateway;
import com.ni3.ag.navigator.client.gateway.ObjectManagementGateway;
import com.ni3.ag.navigator.client.gateway.SettingsGateway;
import com.ni3.ag.navigator.client.gateway.impl.HttpFavoritesFolderGatewayImpl;
import com.ni3.ag.navigator.client.gateway.impl.HttpFavoritesGatewayImpl;
import com.ni3.ag.navigator.client.gateway.impl.HttpGraphGatewayImpl;
import com.ni3.ag.navigator.client.gateway.impl.HttpObjectManagementGatewayImpl;
import com.ni3.ag.navigator.client.gateway.impl.HttpSettingsGatewayImpl;
import com.ni3.ag.navigator.client.gui.Ni3;
import com.ni3.ag.navigator.client.gui.common.Ni3OptionPane;
import com.ni3.ag.navigator.client.gui.favorites.DlgCreateFavoritesFolder;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.GraphPanelSettings;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.gui.map.MapSettings;
import com.ni3.ag.navigator.client.model.FavoritesModel;
import com.ni3.ag.navigator.client.model.GraphCollection;
import com.ni3.ag.navigator.client.model.Ni3Document;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.shared.constants.QueryType;
import com.ni3.ag.navigator.shared.domain.FavoriteMode;

public class FavoritesController{
	private static final Logger log = Logger.getLogger(FavoritesController.class);
	private static final int OLD_FAVORITE_ERROR = 2;
	private static final String FAVORITE_OUT_OF_DATE_MESSAGE = "MsgFavoriteOutOfDate";
	private static final String TITLE_WARNING = "Warning";
	private static final String MSG_PLEASE_CONTACT_SYSADMIN = "MsgFavoritePleaseContactSysAdmin";
	private static final String MSG_COULD_NOT_LOAD_FAV = "MsgCouldNotLoadFavorite";
	private Ni3Document doc;
	private FavoritesGateway favoritesGateway;
	private FavoritesFolderGateway favoritesFolderGateway;

	public FavoritesController(Ni3Document doc){
		this.doc = doc;
		favoritesGateway = new HttpFavoritesGatewayImpl();
		favoritesFolderGateway = new HttpFavoritesFolderGatewayImpl();
	}

	public void initFavoritesWithFolders(int schemaId){
		FavoritesFolderGateway favoritesFolderGateway = new HttpFavoritesFolderGatewayImpl();
		List<Folder> favoritesFolders = favoritesFolderGateway.getFolders(schemaId);

		FavoritesModel favoritesModel = new FavoritesModel(schemaId);
		favoritesModel.addFolders(favoritesFolders);

		FavoritesGateway favoritesGateway = new HttpFavoritesGatewayImpl();
		List<Favorite> allFavorites = favoritesGateway.getAllFavorites(schemaId);
		for (Favorite fav : allFavorites){
			favoritesModel.addFavorite(fav);
		}

		doc.setFavoritesModel(favoritesModel);
	}

	public void moveFavoriteToFolder(Favorite favorite, Folder folder){
		favorite.setFolder(folder);
		favorite.setGroupFavorite(folder.isGroupFolder());

		favoritesGateway.updateFavorite(favorite);

		doc.updateFavorites();
	}

	public void updateFavoriteName(Favorite favorite, String newName){
		favorite.setName(newName);

		favoritesGateway.updateFavorite(favorite);

		if (doc.getCurrentFavorite() != null && doc.getCurrentFavorite().getId() == favorite.getId()){
			doc.setCurrentFavorite();
		}
		doc.updateFavorites();
	}

	public void deleteFavorite(Favorite favorite){
		boolean isCurrent = favorite.getId() == doc.getFavoritesID();
		favoritesGateway.deleteFavorite(favorite);
		doc.getFavoritesModel().removeFavorite(favorite);

		if (isCurrent){
			doc.clearCurrentFavorite();
		}

		doc.updateFavorites();
	}

	public void updateFolder(Folder folder){
		favoritesFolderGateway.updateFolder(folder);
		doc.updateFavorites();
	}

	public void deleteFolder(Folder folder){
		boolean isCurrent = doc.getFavoritesModel().isFavoriteInFolder(doc.getFavoritesID(), folder.getId());

		favoritesFolderGateway.deleteFolder(folder);
		doc.getFavoritesModel().removeFolder(folder);

		if (isCurrent){ // current favorite was removed with folder
			doc.clearCurrentFavorite();
		}

		doc.updateFavorites();
	}

	public Folder createFolder(String name, Folder parentFolder){
		Folder newFolder = new Folder();
		IconCache images = new IconCache();
		newFolder.setIcon(images.getImageIcon(IconCache.MENU_FOLDER));

		newFolder.setName(name);
		newFolder.setGroupFolder(parentFolder.isGroupFolder());
		newFolder.setParentFolder(parentFolder);
		newFolder.setSchemaID(doc.SchemaID);

		final FavoritesModel favoritesModel = doc.getFavoritesModel();
		int sort = favoritesModel.getNewSort(parentFolder.getId());
		newFolder.setSort(sort);

		int newId = favoritesFolderGateway.createFolder(newFolder);

		if (newId < 0){
			JOptionPane.showMessageDialog(null, UserSettings.getWord("ErrCreateFolder"));
			return null;
		}
		newFolder.setId(newId);

		favoritesModel.addFolder(newFolder);

		doc.updateFavorites();

		return newFolder;
	}

	public void sortFoldersByName(Folder selectedFolder){
		List<Folder> subFolders = new ArrayList<Folder>();
		// get all subfolders (1 level only)
		for (Folder f : doc.getFavoritesModel().getFolders()){
			if (f.getParentFolderID() == selectedFolder.getId()){
				subFolders.add(f);
			}
		}
		// sort them by name
		Collections.sort(subFolders, new Comparator<Folder>(){
			@Override
			public int compare(Folder o1, Folder o2){
				return o1.getName().compareTo(o2.getName());
			}
		});
		// set sort and send update to server
		for (int i = 0; i < subFolders.size(); i++){
			final Folder subFolder = subFolders.get(i);
			if (subFolder.getSort() != i + 1){
				subFolder.setSort(i + 1);
				updateFolder(subFolder);
			}
		}

		doc.updateFavorites();
	}

	public void moveFolderToFolder(Folder folder, Folder targetFolder, int newIndex, boolean isDown){
		final FavoritesModel favoritesModel = doc.getFavoritesModel();

		folder.setSort(1);
		Folder currentOnIndex = favoritesModel.getByIndex(targetFolder, newIndex);

		if (currentOnIndex == null)
			currentOnIndex = favoritesModel.lastChildForFolder(targetFolder, folder);
		if (currentOnIndex != null){
			folder.setSort(currentOnIndex.getSort());
			// for all folders of this parent except now processed folder sort
			// should be increased
			for (Folder f : favoritesModel.getFolders()){
				if (isDown){
					if (f.getParentFolderID() == targetFolder.getId() && f.getSort() >= folder.getSort()
							&& f.getId() != currentOnIndex.getId()){
						f.setSort(f.getSort() + 1);
						updateFolder(f);
					}
				} else{
					if (f.getParentFolderID() == targetFolder.getId() && f.getSort() >= folder.getSort()
							&& f.getId() != folder.getId()){
						f.setSort(f.getSort() + 1);
						updateFolder(f);
					}
				}
			}
		}

		folder.setParentFolder(targetFolder);

		updateFolder(folder);

		favoritesModel.sortFolders();
	}

	public void saveAsDocument(){
		int previousFavoriteId = 0;
		FavoriteMode previousMode = null;
		if (doc.getCurrentFavorite() != null){
			previousFavoriteId = doc.getCurrentFavorite().getId();
			previousMode = doc.getCurrentFavorite().getMode();
		}

		if (doc.getCurrentChartId() == SNA.SNA_CHART_ID){// SNA enabled
			Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("SNA_will_not_be_saved"), UserSettings
					.getWord("Warning"), JOptionPane.WARNING_MESSAGE);
		}

		final DlgCreateFavoritesFolder dlg = new DlgCreateFavoritesFolder(doc, true, doc.getCurrentFavorite());

		if (doc.getCurrentFavorite() != null){
			dlg.setMode(doc.getCurrentFavorite().getMode());
		} else{
			dlg.setMode(FavoriteMode.FAVORITE);
		}

		dlg.setVisible(true);

		if (!dlg.isCancel()){
			Favorite favorite = doc.getFavoritesModel().getFavoriteByName(dlg.getFavoriteName(),
					dlg.getSelectedFolder().getId());
			if (favorite == null){
				favorite = (doc.getFavoritesModel().createFavorite(dlg.getFavoriteName(), dlg.getSelectedFolder(), dlg
						.getMode(), doc.SchemaID));
			} else{
				final int ret = Ni3OptionPane.showConfirmDialog(Ni3.mainF, UserSettings
						.getWord("Do you want to replace existing favorite"), UserSettings
						.getWord("Replace favorite confirmation"), JOptionPane.YES_NO_OPTION,
						JOptionPane.INFORMATION_MESSAGE);

				if (ret != 0){
					return;
				}
			}

			if (dlg.getMode() == FavoriteMode.QUERY && !doc.getQueries().isEmpty()
					&& containsSimpleSearchQuery(doc.getQueries())){
				Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("MsgSimpleSearchNotSavedWithQuery"),
						UserSettings.getWord("Warning"), JOptionPane.WARNING_MESSAGE);
			}

			favorite.setDescription(dlg.getFavoriteDescription());
			favorite.setMode(dlg.getMode());
			favorite.setGroupFavorite(dlg.getSelectedFolder().isGroupFolder());
			if (doc.getThematicMapID() <= 0 && doc.getGeoLegendData() != null && !doc.getGeoLegendData().isEmpty()){
				GeoAnalyticsController controller = GeoAnalyticsController.getInstance(doc);
				int tmId = controller.saveThematicMapForFavorite(doc.getGeoAnalyticsLayer(), doc.getGeoLegendAttribute(),
						doc.getGeoLegendData(), doc.DB.schema.ID);
				if (tmId > 0){
					doc.setThematicMapID(tmId);
				}
			}

			final boolean success = saveDocument(previousFavoriteId, previousMode, favorite);
			if (success){
				doc.updateFavorites();
			}
		}
	}

	boolean containsSimpleSearchQuery(List<Query> queries){
		boolean result = false;
		for (Query query : queries){
			if (query.getType() == QueryType.SIMPLE){
				result = true;
				break;
			}
		}
		return result;
	}

	public boolean rename(Object obj, String newValue){
		boolean result = false;
		if (newValue == null || newValue.trim().isEmpty()){
			return false;
		}
		final FavoritesModel favoritesModel = doc.getFavoritesModel();
		if (obj instanceof Folder && ((Folder) obj).getId() > 0){
			Folder folder = (Folder) obj;
			if (favoritesModel.isUniqueFavoriteFolderName(newValue, folder.getId(), folder.getParentFolderID())){
				folder.setName(newValue);
				updateFolder(folder);
				result = true;
			} else{
				JOptionPane.showMessageDialog(null, UserSettings.getWord("MsgDuplicateFavoritesFolderName",
						new Object[] { newValue }));
			}
		} else if (obj instanceof Favorite){
			Favorite fav = (Favorite) obj;
			if (favoritesModel.isUniqueFavoriteName((String) newValue, fav.getId(), fav.getFolderId())){
				updateFavoriteName(fav, (String) newValue);
				result = true;
			} else{
				JOptionPane.showMessageDialog(null, UserSettings.getWord("MsgDuplicateFavoriteName",
						new Object[] { newValue }));
			}
		}
		if (result){
			doc.updateFavorites();
		}
		return result;
	}

	public Favorite duplicate(final Favorite fvt, final Folder targetFolder){
		final String newName = getFavoriteCopyName(fvt.getName(), targetFolder.getId());
		int favoriteFolderId = targetFolder.getId();
		if (favoriteFolderId == Folder.ROOT_FOLDER_ID || favoriteFolderId == Folder.MY_ROOT_FOLDER_ID
				|| favoriteFolderId == Folder.GROUP_ROOT_FOLDER_ID){
			favoriteFolderId = 0;
		}

		Favorite newFavorite = new Favorite();
		newFavorite.setName(newName);
		newFavorite.setFolderId(favoriteFolderId);
		newFavorite.setGroupFavorite(targetFolder.isGroupFolder());
		newFavorite.setSchemaId(fvt.getSchemaId());

		final int newFavoriteId = favoritesGateway.copyFavorite(fvt.getId(), newFavorite);
		if (newFavoriteId < 0){
			Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("MsgErrCreateFavoriteFailed"));
			return null;
		}

		newFavorite.setId(newFavoriteId);
		newFavorite.setMode(fvt.getMode());
		newFavorite.setFolder(targetFolder);
		newFavorite.setDescription(fvt.getDescription());
		newFavorite.setCreatorId(SystemGlobals.getUserId());
		doc.getFavoritesModel().addFavorite(newFavorite);

		if (newFavorite.getMode() == FavoriteMode.TOPIC){
			doc.undoredoManager.cloneContextEdges(fvt.getId(), newFavorite.getId());

			for (final Entity ent : doc.DB.schema.definitions){
				final Context c = ent.getContext("Favorites");
				if (c != null){
					ObjectManagementGateway objGateway = new HttpObjectManagementGatewayImpl();
					objGateway.cloneContext(ent.getSchema().ID, c.ID, fvt.getId(), newFavorite.getId());
				}
			}
		}
		doc.updateFavorites();
		return newFavorite;
	}

	public void setDefaultFavorite(Favorite favorite){
		SettingsGateway settingsGateway = new HttpSettingsGatewayImpl();

		//default on startup
		settingsGateway.saveUserSetting("Applet", "DefaultFavorite", String.valueOf(favorite.getId()));
		settingsGateway.saveUserSetting("Applet", "Scheme", String.valueOf(favorite.getSchemaId()));

		//default for a specific schema
		settingsGateway.saveUserSetting("Applet", "DefaultFavorite_" + favorite.getSchemaId(), String.valueOf(favorite.getId()));

		//update local client cache
		UserSettings.saveSettingLocally("DefaultFavorite_" + favorite.getSchemaId(), String.valueOf(favorite.getId()));
	}

	private Favorite saveDocument(final int previousFavoriteId, final FavoriteMode previousMode, final Favorite favorite,
			final HistoryItem hi){
		String xml = hi.toXML(favorite.getMode());
		favorite.setData(xml);
		favorite.setLayout(hi.getLayout());
		if (favorite.getId() == 0){
			final int newFavoriteId = favoritesGateway.createFavorite(favorite);
			if (newFavoriteId < 0){
				Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("MsgErrCreateFavoriteFailed"));
				return favorite;
			}

			favorite.setId(newFavoriteId);
			favorite.setCreatorId(SystemGlobals.getUserId());
			doc.getFavoritesModel().addFavorite(favorite);
		} else{
			favoritesGateway.updateFavorite(favorite);
		}
		favorite.setData(null);
		favorite.setLayout(null);

		if (previousMode != null && previousMode == FavoriteMode.TOPIC
				&& (favorite.getId() != previousFavoriteId || favorite.getMode() != FavoriteMode.TOPIC)){
			ObjectManagementGateway objGateway = new HttpObjectManagementGatewayImpl();
			objGateway.clearContext(favorite);
		}

		if (favorite.getMode() == FavoriteMode.TOPIC){
			if (favorite.getId() != previousFavoriteId){
				doc.undoredoManager.saveContextEdges(hi.getGraph(), previousFavoriteId, favorite.getId());
				doc.clearTopicEdges();
				doc.undoredoManager.getTopicEdges(favorite.getId(), hi.getGraph());
				doc.DB.getSubgraphData(hi.getGraph());
			}

			doc.undoredoManager.saveContextData(favorite.getId(), hi.getGraph());
		}

		return favorite;
	}

	private boolean saveDocument(final int previousFavoriteID, final FavoriteMode previousMode, final Favorite favorite){
		final int chartID = doc.getCurrentChartId() == -2 ? 0 : doc.getCurrentChartId();

		final HistoryManager.HistoryItem hi = doc.undoredoManager.new HistoryItem(doc.SchemaID, chartID, doc.getMapID(), doc
				.getThematicMapID(), doc.Subgraph, doc.DS, doc.getGraphVisualSettings(), doc.getMapSettings(), doc.filter,
				doc.DB.getDataFilter(), doc.getQueries(), favorite.getMode(), doc.getMetaphorSet(), doc
						.isShowNumericMetaphors(), doc.getSelectedOverlayIds());
		hi.setChartParams(doc.getChartParams());
		hi.setMatrixSort(doc.getMatrixSort());
		hi.setPolyModel(doc.getPolygonModel());
		hi.setCommandPanelSettings(doc.getCommandPanelSettings());
		hi.setInPathEdges(doc.getInPathEdges());
		final Favorite ret = saveDocument(previousFavoriteID, previousMode, favorite, hi);
		doc.setCurrentFavorite(ret);

		if (ret == null){
			return false;
		}

		doc.setCurrentFavorite();

		return true;
	}

	private void load(final Favorite favorite, final int chartID, final int mapID, final int thematicMapID,
			final GraphCollection graph, final List<DBObject> DS, final GraphPanelSettings gpset,
			final MapSettings mapSettings, final DataFilter filter, final DataFilter prefilter, final List<Query> queries){

		if (!validateFavoriteLoad(favorite))
			return;

		FavoritesGateway favoritesGateway = new HttpFavoritesGatewayImpl();
		Favorite loadedFavorite = favoritesGateway.loadFavoriteData(favorite.getId());

		final HistoryManager.HistoryItem hi = doc.undoredoManager.new HistoryItem(favorite.getSchemaId(), chartID, mapID,
				thematicMapID, graph, DS, gpset, mapSettings, filter, prefilter, queries, FavoriteMode.FAVORITE, null, doc
						.isShowNumericMetaphors(), null);

		String document = loadedFavorite.getData();
		hi.fromXML(document, doc.DB.schema);

		doc.DB.setDataFilter(hi.getPrefilter());
		loadMissingData(hi.getGraph(), doc.SchemaID, hi.getPrefilter());

		doc.DB.getSubgraphData(graph);

		hi.setLayout(loadedFavorite.getLayout());
		hi.getGraph().checkAccessRights(hi.getDS(), doc.DB.getDataFilter());

		final int newChartID = hi.getChartID();
		doc.undoredoManager.restoreState(hi, true, false);

		final int maxLevel = hi.getGraph().MaxLevel;

		favorite.setMode(hi.getMode());

		if (favorite.getMode() == FavoriteMode.TOPIC){
			doc.undoredoManager.getTopicEdges(favorite.getId(), hi.getGraph());
		}

		final List<Node> nodes = new ArrayList<Node>(hi.getGraph().getNodes());
		if (hi.getGraph().isLeadingNodesOnly() && favorite.getMode() != FavoriteMode.QUERY && hi.getGraph().MaxLevel > 0){
			for (int lvl = 0; lvl < maxLevel; lvl++){
				final List<Integer> toExpand = new ArrayList<Integer>();
				boolean atLeastOne = false;
				final GraphController graphController = new GraphController(SystemGlobals.MainFrame);
				for (Node n : nodes){
					if (n.Obj != null && n.getLevel() == lvl){
						if (n.getSelectiveExpandDataFilter() != null){
							graphController.selectiveExpand(n, n.getSelectiveExpandDataFilter(), false);
							atLeastOne = true;
						} else if (n.isExpandedManualy()){
							toExpand.add(n.ID);
							atLeastOne = true;
						}
					}
				}

				if (!toExpand.isEmpty()){
					graphController.expandNodesOneLevel(toExpand, false, lvl + 1, true);
				} else if (!atLeastOne && lvl == 0){
					graphController.expandOneLevel(false, true);
				}
			}
		}

		doc.DS = doc.DB.prepareSubgraph(graph, false);
		graph.layoutManagerChanged = true;

		if (favorite.getMode() == FavoriteMode.TOPIC){
			doc.undoredoManager.loadContextData(favorite.getId(), graph);
		}

		doc.Subgraph.filter(filter, favorite.getId());

		doc.setChartParams(hi.getChartParams());
		doc.setChart(newChartID, true, false);

		doc.dispatchEvent(Ni3ItemListener.MSG_NewSubgraph, Ni3ItemListener.SRC_Doc, null, graph);
		doc.dispatchEvent(Ni3ItemListener.MSG_SubgraphChanged, Ni3ItemListener.SRC_Doc, null, graph);
	}

	private void loadMissingData(GraphCollection graph, final int schemaId, final DataFilter dataFilter){
		GraphGateway graphGateway = new HttpGraphGatewayImpl();
		final List<Node> nodes = graphGateway.getNodes(graph.getNodeIds(), schemaId, dataFilter);
		log.debug("Loaded nodes: " + nodes.size());
		for (Node node : nodes){
			Node favNode = graph.findNode(node.ID);
			node.copyGeometry(favNode);
		}
		final List<Edge> edges = graphGateway.getEdges(graph.getEdgeIds(), schemaId, dataFilter);
		log.debug("Loaded edges: " + edges.size());
		final List<Node> roots = new ArrayList<Node>(graph.getRoots());

		graph.clear();
		graph.addResultToGraph(nodes);
		graph.addResultToGraph(edges);
		boolean hasRoots = false;
		for (Node root : roots){
			final Node existing = graph.findNode(root.ID);
			if (existing != null){
				graph.setRootNode(existing);
				hasRoots = true;
			} else{
				log.warn("Favorite contains reference to possibly deleted node: " + root.ID);
			}
		}
		if (!hasRoots && !roots.isEmpty()){
			showNoRootsMessage();
		}
	}

	public void loadDocument(final int ID, final int SchemaID){
		doc.clearGraph(false, true);
		doc.clearSearchResult(false);

		doc.startFavoriteLoad();

		if (doc.SchemaID != SchemaID){
			doc.changeSchema(SchemaID, false);
		}

		doc.setCurrentFavorite(doc.getFavoritesModel().getFavoriteByID(ID));
		log.debug("Default favorite is " + doc.getCurrentFavorite() + " id=" + ID);

		if (doc.getCurrentFavorite() != null){
			doc.clearQueryStack();
			load(doc.getCurrentFavorite(), doc.getCurrentChartId(), doc.getMapID(), doc.getThematicMapID(), doc.Subgraph,
					doc.DS, doc.getGraphVisualSettings(), doc.getMapSettings(), new DataFilter(), new DataFilter(), doc
							.getQueries());

			doc.Subgraph.setMultiEdgeIndexes();

			doc.setCurrentFavorite();
		} else{
			doc.clearCurrentFavorite();
			log.warn("Requested favorite not found id=" + ID);
		}

		doc.finishFavoriteLoad();
	}

	private boolean validateFavoriteLoad(Favorite favorite){
		final int result = favoritesGateway.validateFavoriteVersion(favorite.getId());

		if (result == -1){
			Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord(MSG_COULD_NOT_LOAD_FAV) + "\n(favorite: id="
					+ favorite.getId() + ", name=" + favorite.getName() + ")\n"
					+ UserSettings.getWord(MSG_PLEASE_CONTACT_SYSADMIN).replace("\\n", "\n"), UserSettings
					.getWord(TITLE_WARNING), JOptionPane.ERROR_MESSAGE);
			log.error("Error validating favorite " + favorite.getId());
			return false;
		} else if (result == OLD_FAVORITE_ERROR){
			Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord(FAVORITE_OUT_OF_DATE_MESSAGE) + "\n(favorite: "
					+ "id=" + favorite.getId() + ", name=" + favorite.getName() + ")\n"
					+ UserSettings.getWord(MSG_PLEASE_CONTACT_SYSADMIN).replace("\\n", "\n"), UserSettings
					.getWord(TITLE_WARNING), JOptionPane.ERROR_MESSAGE);
			log.error("Loading out of date favorite: id=" + favorite.getId() + " name=" + favorite.getName());
			return false;
		}
		return true;
	}

	String getFavoriteCopyName(final String favName, final int folderID){
		final String initName = "Copy of " + favName;

		int index = 0;
		String newName = initName;
		while (true){
			final Favorite fav = doc.getFavoritesModel().getFavoriteByName(newName, folderID);
			if (fav == null){
				break;
			}

			newName = initName + " (" + ++index + ")";
		}
		return newName;
	}

	private void showNoRootsMessage(){
		Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("MsgRootsFromFavoriteNotAvailable"));
	}
}
