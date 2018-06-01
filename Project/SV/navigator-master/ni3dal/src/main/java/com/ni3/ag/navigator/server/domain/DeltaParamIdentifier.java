package com.ni3.ag.navigator.server.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the class that identifies all the possible parameters for deltas
 */
public class DeltaParamIdentifier{
	// Create edge delta params
	public static final DeltaParamIdentifier CreateEdgeNewId = new DeltaParamIdentifier("CreateEdgeNewId");
	public static final DeltaParamIdentifier CreateEdgeObjectDefinitionId = new DeltaParamIdentifier(
	        "CreateEdgeObjectDefinitionId");
	public static final DeltaParamIdentifier CreateEdgeSchemaId = new DeltaParamIdentifier("CreateEdgeSchemaId");
	public static final DeltaParamIdentifier CreateEdgeFromId = new DeltaParamIdentifier("CreateEdgeFromId");
	public static final DeltaParamIdentifier CreateEdgeToId = new DeltaParamIdentifier("CreateEdgeToId");
	public static final DeltaParamIdentifier CreateEdgeFavoriteId = new DeltaParamIdentifier("CreateEdgeFavoriteId");

	// Create node delta params
	public static final DeltaParamIdentifier CreateNodeNewId = new DeltaParamIdentifier("CreateNodeNewId");
	public static final DeltaParamIdentifier CreateNodeObjectDefinitionId = new DeltaParamIdentifier(
	        "CreateNodeObjectDefinitionId");
	public static final DeltaParamIdentifier CreateNodeSchemaId = new DeltaParamIdentifier("CreateNodeSchemaId");

	// Update object delta params
	public static final DeltaParamIdentifier UpdateEdgeObjectId = new DeltaParamIdentifier("UpdateEdgeObjectId");
	public static final DeltaParamIdentifier UpdateEdgeObjectDefinitionId = new DeltaParamIdentifier(
	        "UpdateEdgeObjectDefinitionId");
	public static final DeltaParamIdentifier UpdateEdgeSchemaId = new DeltaParamIdentifier("UpdateEdgeSchemaId");
	public static final DeltaParamIdentifier UpdateEdgeFavoriteId = new DeltaParamIdentifier("UpdateEdgeFavoriteId");

	// Update node delta params
	public static final DeltaParamIdentifier UpdateNodeObjectDefinitionId = new DeltaParamIdentifier(
	        "UpdateNodeObjectDefinitionId");
	public static final DeltaParamIdentifier UpdateNodeObjectId = new DeltaParamIdentifier("UpdateNodeObjectId");
	public static final DeltaParamIdentifier UpdateNodeSchemaId = new DeltaParamIdentifier("UpdateNodeSchemaId");

	// Update node metaphor delta params
	public static final DeltaParamIdentifier UpdateNodeMetaphorObjectId = new DeltaParamIdentifier(
	        "UpdateNodeMetaphorObjectId");
	public static final DeltaParamIdentifier UpdateNodeMetaphorNewMetaphor = new DeltaParamIdentifier(
	        "UpdateNodeMetaphorNewMetaphor");

	// Update node geo coordinates delta params
	public static final DeltaParamIdentifier UpdateNodeCoordsObjectId = new DeltaParamIdentifier("UpdateNodeCoordsObjectId");
	public static final DeltaParamIdentifier UpdateNodeCoordsLon = new DeltaParamIdentifier("UpdateNodeCoordsLon");
	public static final DeltaParamIdentifier UpdateNodeCoordsLat = new DeltaParamIdentifier("UpdateNodeCoordsLat");

	// Merge node delta params
	public static final DeltaParamIdentifier MergeNodeSchemaId = new DeltaParamIdentifier("MergeNodeSchemaId");
	public static final DeltaParamIdentifier MergeNodeObjectDefinitionId = new DeltaParamIdentifier(
	        "MergeNodeObjectDefinitionId");
	public static final DeltaParamIdentifier MergeNodeFromId = new DeltaParamIdentifier("MergeNodeFromId");
	public static final DeltaParamIdentifier MergeNodeToId = new DeltaParamIdentifier("MergeNodeToId");
	public static final DeltaParamIdentifier MergeNodeAttributeIDs = new DeltaParamIdentifier("MergeNodeAttributeIDs");
	public static final DeltaParamIdentifier MergeNodeEdgeIDs = new DeltaParamIdentifier("MergeNodeEdgeIDs");

	// Delete object delta params
	public static final DeltaParamIdentifier DeleteObjectObjectId = new DeltaParamIdentifier("DeleteObjectObjectId");
	public static final DeltaParamIdentifier DeleteObjectSchemaId = new DeltaParamIdentifier("DeleteObjectSchemaId");
	public static final DeltaParamIdentifier DeleteObjectObjectDefinitionId = new DeltaParamIdentifier(
	        "DeleteObjectObjectDefinitionId");

	// Create favorite delta params
	public static final DeltaParamIdentifier CreateFavoriteNewId = new DeltaParamIdentifier("CreateFavoriteNewId");
	public static final DeltaParamIdentifier CreateFavoriteDocument = new DeltaParamIdentifier("CreateFavoriteDocument");
	public static final DeltaParamIdentifier CreateFavoriteName = new DeltaParamIdentifier("CreateFavoriteName");
	public static final DeltaParamIdentifier CreateFavoriteDescription = new DeltaParamIdentifier(
	        "CreateFavoriteDescription");
	public static final DeltaParamIdentifier CreateFavoriteMode = new DeltaParamIdentifier("CreateFavoriteMode");
	public static final DeltaParamIdentifier CreateFavoriteGroupFolder = new DeltaParamIdentifier(
	        "CreateFavoriteGroupFolder");
	public static final DeltaParamIdentifier CreateFavoriteFolderId = new DeltaParamIdentifier("CreateFavoriteFolderId");
	public static final DeltaParamIdentifier CreateFavoriteLayout = new DeltaParamIdentifier("CreateFavoriteLayout");
	public static final DeltaParamIdentifier CreateFavoriteSchemaId = new DeltaParamIdentifier("CreateFavoriteSchemaId");
	public static final DeltaParamIdentifier CreateFavoriteDBVersion = new DeltaParamIdentifier("CreateFavoriteDBVersion");

	// Update favorite delta params
	public static final DeltaParamIdentifier UpdateFavoriteId = new DeltaParamIdentifier("UpdateFavoriteId");
	public static final DeltaParamIdentifier UpdateFavoriteMode = new DeltaParamIdentifier("UpdateFavoriteMode");
	public static final DeltaParamIdentifier UpdateFavoriteDescription = new DeltaParamIdentifier(
	        "UpdateFavoriteDescription");
	public static final DeltaParamIdentifier UpdateFavoriteName = new DeltaParamIdentifier("UpdateFavoriteName");
	public static final DeltaParamIdentifier UpdateFavoriteFolderId = new DeltaParamIdentifier("UpdateFavoriteFolderId");
	public static final DeltaParamIdentifier UpdateFavoriteDocument = new DeltaParamIdentifier("UpdateFavoriteDocument");
	public static final DeltaParamIdentifier UpdateFavoriteLayout = new DeltaParamIdentifier("UpdateFavoriteLayout");
	public static final DeltaParamIdentifier UpdateFavoriteGroupFolder = new DeltaParamIdentifier(
	        "UpdateFavoriteGroupFolder");
	public static final DeltaParamIdentifier UpdateFavoriteSchemaId = new DeltaParamIdentifier("UpdateFavoriteSchemaId");

	// Delete favorite delta params
	public static final DeltaParamIdentifier DeleteFavoriteId = new DeltaParamIdentifier("DeleteFavoriteId");
	public static final DeltaParamIdentifier DeleteFavoriteSchemaId = new DeltaParamIdentifier("DeleteFavoriteSchemaId");

	// Copy favorite delta params
	public static final DeltaParamIdentifier CopyFavoriteNewId = new DeltaParamIdentifier("CopyFavoriteNewId");
	public static final DeltaParamIdentifier CopyFavoriteSchemaId = new DeltaParamIdentifier("CopyFavoriteSchemaId");

	// Create favorite folder delta params
	public static final DeltaParamIdentifier CreateFavoriteFolderNewId = new DeltaParamIdentifier(
	        "CreateFavoriteFolderNewId");
	public static final DeltaParamIdentifier CreateFavoriteFolderName = new DeltaParamIdentifier("CreateFavoriteFolderName");
	public static final DeltaParamIdentifier CreateFavoriteFolderSchemaId = new DeltaParamIdentifier(
	        "CreateFavoriteFolderSchemaId");
	public static final DeltaParamIdentifier CreateFavoriteFolderParentFolderId = new DeltaParamIdentifier(
	        "CreateFavoriteFolderParentFolderId");
	public static final DeltaParamIdentifier CreateFavoriteFolderGroupFolder = new DeltaParamIdentifier(
	        "CreateFavoriteFolderGroupFolder");
	public static final DeltaParamIdentifier CreateFavoriteFolderSort = new DeltaParamIdentifier("CreateFavoriteFolderSort");

	// Update favorite folder delta params
	public static final DeltaParamIdentifier UpdateFavoriteFolderFolderId = new DeltaParamIdentifier(
	        "UpdateFavoriteFolderFolderId");
	public static final DeltaParamIdentifier UpdateFavoriteFolderName = new DeltaParamIdentifier("UpdateFavoriteFolderName");
	public static final DeltaParamIdentifier UpdateFavoriteFolderParentFolderId = new DeltaParamIdentifier(
	        "UpdateFavoriteFolderParentFolderId");
	public static final DeltaParamIdentifier UpdateFavoriteFolderSort = new DeltaParamIdentifier("UpdateFavoriteFolderSort");
	public static final DeltaParamIdentifier UpdateFavoriteFolderSchemaId = new DeltaParamIdentifier(
	        "UpdateFavoriteFolderSchemaId");

	// Delete favorite folder delta params
	public static final DeltaParamIdentifier DeleteFavoriteFolderObjId = new DeltaParamIdentifier(
	        "DeleteFavoriteFolderObjId");
	public static final DeltaParamIdentifier DeleteFavoriteFolderSchemaId = new DeltaParamIdentifier(
	        "DeleteFavoriteFolderSchemaId");

	// Update settings delta params
	public static final DeltaParamIdentifier UpdateSettingsPropertyName = new DeltaParamIdentifier(
	        "UpdateSettingsPropertyName");
	public static final DeltaParamIdentifier UpdateSettingsPropertyValue = new DeltaParamIdentifier(
	        "UpdateSettingsPropertyValue");

	// create geo analytics
	public static final DeltaParamIdentifier SaveGeoAnalyticsId = new DeltaParamIdentifier("SaveGeoAnalyticsId");
	public static final DeltaParamIdentifier SaveGeoAnalyticsName = new DeltaParamIdentifier("SaveGeoAnalyticsName");
	public static final DeltaParamIdentifier SaveGeoAnalyticsFolderId = new DeltaParamIdentifier("SaveGeoAnalyticsFolderId");
	public static final DeltaParamIdentifier SaveGeoAnalyticsGroupId = new DeltaParamIdentifier("SaveGeoAnalyticsGroupId");
	public static final DeltaParamIdentifier SaveGeoAnalyticsLayerId = new DeltaParamIdentifier("SaveGeoAnalyticsLayerId");
	public static final DeltaParamIdentifier SaveGeoAnalyticsAttribute = new DeltaParamIdentifier("SaveGeoAnalyticsAttribute");

	//delete geo analytics
	public static final DeltaParamIdentifier DeleteGeoAnalyticsId = new DeltaParamIdentifier("DeleteGeoAnalyticsId");

	//Get or create geo analytics map
	public static final DeltaParamIdentifier GetOrCreateGeoAnalyticsMapId = new DeltaParamIdentifier
			("GetOrCreateGeoAnalyticsMapId");
	public static final DeltaParamIdentifier GetOrCreateGeoAnalyticsMapSchemaId = new DeltaParamIdentifier
			("GetOrCreateGeoAnalyticsMapSchemaId");
	public static final DeltaParamIdentifier GetOrCreateGeoAnalyticsMapMapId = new DeltaParamIdentifier
			("GetOrCreateGeoAnalyticsMapMapId");

	private static final Map<String, DeltaParamIdentifier> entryMap = new HashMap<String, DeltaParamIdentifier>();

	static{
		entryMap.put(CreateEdgeNewId.getIdentifier(), CreateEdgeNewId);
		entryMap.put(CreateEdgeObjectDefinitionId.getIdentifier(), CreateEdgeObjectDefinitionId);
		entryMap.put(CreateEdgeSchemaId.getIdentifier(), CreateEdgeSchemaId);
		entryMap.put(CreateEdgeFromId.getIdentifier(), CreateEdgeFromId);
		entryMap.put(CreateEdgeToId.getIdentifier(), CreateEdgeToId);
		entryMap.put(CreateEdgeFavoriteId.getIdentifier(), CreateEdgeFavoriteId);
		entryMap.put(CreateNodeNewId.getIdentifier(), CreateNodeNewId);
		entryMap.put(CreateNodeObjectDefinitionId.getIdentifier(), CreateNodeObjectDefinitionId);
		entryMap.put(CreateNodeSchemaId.getIdentifier(), CreateNodeSchemaId);
		entryMap.put(UpdateEdgeObjectId.getIdentifier(), UpdateEdgeObjectId);
		entryMap.put(UpdateEdgeObjectDefinitionId.getIdentifier(), UpdateEdgeObjectDefinitionId);
		entryMap.put(UpdateEdgeSchemaId.getIdentifier(), UpdateEdgeSchemaId);
		entryMap.put(UpdateEdgeFavoriteId.getIdentifier(), UpdateEdgeFavoriteId);
		entryMap.put(UpdateNodeObjectDefinitionId.getIdentifier(), UpdateNodeObjectDefinitionId);
		entryMap.put(UpdateNodeObjectId.getIdentifier(), UpdateNodeObjectId);
		entryMap.put(UpdateNodeSchemaId.getIdentifier(), UpdateNodeSchemaId);
		entryMap.put(UpdateNodeMetaphorObjectId.getIdentifier(), UpdateNodeMetaphorObjectId);
		entryMap.put(UpdateNodeMetaphorNewMetaphor.getIdentifier(), UpdateNodeMetaphorNewMetaphor);
		entryMap.put(UpdateNodeCoordsObjectId.getIdentifier(), UpdateNodeCoordsObjectId);
		entryMap.put(UpdateNodeCoordsLon.getIdentifier(), UpdateNodeCoordsLon);
		entryMap.put(UpdateNodeCoordsLat.getIdentifier(), UpdateNodeCoordsLat);
		entryMap.put(MergeNodeSchemaId.getIdentifier(), MergeNodeSchemaId);
		entryMap.put(MergeNodeObjectDefinitionId.getIdentifier(), MergeNodeObjectDefinitionId);
		entryMap.put(MergeNodeFromId.getIdentifier(), MergeNodeFromId);
		entryMap.put(MergeNodeToId.getIdentifier(), MergeNodeToId);
		entryMap.put(MergeNodeAttributeIDs.getIdentifier(), MergeNodeAttributeIDs);
		entryMap.put(MergeNodeEdgeIDs.getIdentifier(), MergeNodeEdgeIDs);
		entryMap.put(DeleteObjectObjectId.getIdentifier(), DeleteObjectObjectId);
		entryMap.put(DeleteObjectSchemaId.getIdentifier(), DeleteObjectSchemaId);
		entryMap.put(DeleteObjectObjectDefinitionId.getIdentifier(), DeleteObjectObjectDefinitionId);
		entryMap.put(CreateFavoriteNewId.getIdentifier(), CreateFavoriteNewId);
		entryMap.put(CreateFavoriteDocument.getIdentifier(), CreateFavoriteDocument);
		entryMap.put(CreateFavoriteName.getIdentifier(), CreateFavoriteName);
		entryMap.put(CreateFavoriteDescription.getIdentifier(), CreateFavoriteDescription);
		entryMap.put(CreateFavoriteMode.getIdentifier(), CreateFavoriteMode);
		entryMap.put(CreateFavoriteGroupFolder.getIdentifier(), CreateFavoriteGroupFolder);
		entryMap.put(CreateFavoriteFolderId.getIdentifier(), CreateFavoriteFolderId);
		entryMap.put(CreateFavoriteLayout.getIdentifier(), CreateFavoriteLayout);
		entryMap.put(CreateFavoriteSchemaId.getIdentifier(), CreateFavoriteSchemaId);
		entryMap.put(CreateFavoriteDBVersion.getIdentifier(), CreateFavoriteDBVersion);
		entryMap.put(UpdateFavoriteId.getIdentifier(), UpdateFavoriteId);
		entryMap.put(UpdateFavoriteMode.getIdentifier(), UpdateFavoriteMode);
		entryMap.put(UpdateFavoriteDescription.getIdentifier(), UpdateFavoriteDescription);
		entryMap.put(UpdateFavoriteName.getIdentifier(), UpdateFavoriteName);
		entryMap.put(UpdateFavoriteFolderId.getIdentifier(), UpdateFavoriteFolderId);
		entryMap.put(UpdateFavoriteDocument.getIdentifier(), UpdateFavoriteDocument);
		entryMap.put(UpdateFavoriteLayout.getIdentifier(), UpdateFavoriteLayout);
		entryMap.put(UpdateFavoriteGroupFolder.getIdentifier(), UpdateFavoriteGroupFolder);
		entryMap.put(UpdateFavoriteSchemaId.getIdentifier(), UpdateFavoriteSchemaId);
		entryMap.put(DeleteFavoriteId.getIdentifier(), DeleteFavoriteId);
		entryMap.put(DeleteFavoriteSchemaId.getIdentifier(), DeleteFavoriteSchemaId);
		entryMap.put(CopyFavoriteNewId.getIdentifier(), CopyFavoriteNewId);
		entryMap.put(CopyFavoriteSchemaId.getIdentifier(), CopyFavoriteSchemaId);
		entryMap.put(CreateFavoriteFolderNewId.getIdentifier(), CreateFavoriteFolderNewId);
		entryMap.put(CreateFavoriteFolderName.getIdentifier(), CreateFavoriteFolderName);
		entryMap.put(CreateFavoriteFolderSchemaId.getIdentifier(), CreateFavoriteFolderSchemaId);
		entryMap.put(CreateFavoriteFolderParentFolderId.getIdentifier(), CreateFavoriteFolderParentFolderId);
		entryMap.put(CreateFavoriteFolderGroupFolder.getIdentifier(), CreateFavoriteFolderGroupFolder);
		entryMap.put(CreateFavoriteFolderSort.getIdentifier(), CreateFavoriteFolderSort);
		entryMap.put(UpdateFavoriteFolderFolderId.getIdentifier(), UpdateFavoriteFolderFolderId);
		entryMap.put(UpdateFavoriteFolderName.getIdentifier(), UpdateFavoriteFolderName);
		entryMap.put(UpdateFavoriteFolderParentFolderId.getIdentifier(), UpdateFavoriteFolderParentFolderId);
		entryMap.put(UpdateFavoriteFolderSort.getIdentifier(), UpdateFavoriteFolderSort);
		entryMap.put(UpdateFavoriteFolderSchemaId.getIdentifier(), UpdateFavoriteFolderSchemaId);
		entryMap.put(DeleteFavoriteFolderObjId.getIdentifier(), DeleteFavoriteFolderObjId);
		entryMap.put(DeleteFavoriteFolderSchemaId.getIdentifier(), DeleteFavoriteFolderSchemaId);
		entryMap.put(UpdateSettingsPropertyName.getIdentifier(), UpdateSettingsPropertyName);
		entryMap.put(UpdateSettingsPropertyValue.getIdentifier(), UpdateSettingsPropertyValue);
		entryMap.put(SaveGeoAnalyticsId.getIdentifier(), SaveGeoAnalyticsId);
		entryMap.put(DeleteGeoAnalyticsId.getIdentifier(), DeleteGeoAnalyticsId);
		entryMap.put(GetOrCreateGeoAnalyticsMapId.getIdentifier(), GetOrCreateGeoAnalyticsMapId);
		entryMap.put(GetOrCreateGeoAnalyticsMapSchemaId.getIdentifier(), GetOrCreateGeoAnalyticsMapSchemaId);
		entryMap.put(GetOrCreateGeoAnalyticsMapMapId.getIdentifier(), GetOrCreateGeoAnalyticsMapMapId);
		entryMap.put(SaveGeoAnalyticsName.getIdentifier(), SaveGeoAnalyticsName);
		entryMap.put(SaveGeoAnalyticsFolderId.getIdentifier(), SaveGeoAnalyticsFolderId);
		entryMap.put(SaveGeoAnalyticsGroupId.getIdentifier(), SaveGeoAnalyticsGroupId);
		entryMap.put(SaveGeoAnalyticsLayerId.getIdentifier(), SaveGeoAnalyticsLayerId);
		entryMap.put(SaveGeoAnalyticsAttribute.getIdentifier(), SaveGeoAnalyticsAttribute);
	}

	private String identifier;

	public DeltaParamIdentifier(String id){
		identifier = id;
	}

	public static DeltaParamIdentifier getById(String id){
		return entryMap.get(id);
	}

	@Override
	public boolean equals(Object obj){
		if (obj == null)
			return false;
		if (!obj.getClass().equals(getClass()))
			return false;
		return this.identifier.equals(((DeltaParamIdentifier) obj).getIdentifier());
	}

	public boolean isFixedParam(){
		return entryMap.containsKey(identifier);
	}

	public String getIdentifier(){
		return identifier;
	}

	@Override
	public String toString(){
		return "DeltaParamIdentifier [identifier=" + identifier + "]";
	}
}
