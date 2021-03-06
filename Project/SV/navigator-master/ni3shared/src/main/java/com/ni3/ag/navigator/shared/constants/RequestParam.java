/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.shared.constants;

public enum RequestParam{
	//@formatter:off
	Action,
	User,
	Pwd,
	SID,
	UserID,
	GroupsID,
	SessionId,
	Current,
	New,
	EMail,
	SCHID,
	ReloadSchema,
	ReloadGraph,
	Arguments,
	Procedure,
	LargeText,
	Recurse,
	Info,
	ObjID,
	EID,
	DS,
	ContextKey,
	ContextID,
	ContextKeyOld,
	ContextKeyNew,
	Term,
	Exact,
	Query,
	GSC,
	cObj,
	FromID,
	ToID,
	ConnectionType,
	LanguageID,
	ObjVal,
	Prefilter,
	XLSExport,
	Nodes,
	Edges,
	DateFormat,
	P1,
	P2,
	P3,
	P4,
	P5,
	P6,
	FolderID,
	Name,
	GroupFavorites,
	doc,
	descr,
	layout,
	Mode,
	MapID,
	TerritoryStructure,
	GetLonLat,
	TerritoryID,
	GISIDs,
	INs,
	AttributeID,
	AttrValueFrom,
	AttrValueTo,
	getClusters,
	requiredClusters,
	TotalPerTerritoryID,
	IDs,
	min,
	max,
	avg,
	getDistinct,
	getGisIDAVG,
	getGisAggregationForDA,
	getHistogram,
	HistogramPerTerritory,
	ignoreZero,
	SysFilter,
	MaxNodeNumber,
	IDOnly,
	Area,
	Degree,
	GetEdges,
	GetNodes,
	GetFavoritesEdges,
	favoritesID,
	EdgeID,
	ObjectType,
	Type,
	Strength,
	Directed,
	InPath,
	GroupID,
	NodeID,
	DeleteTopic,
	TopicID,
	ReloadNode,
	RelinkEdge,
	UpdateEdge,
	FindPathFrom,
	MaxPathLength,
	PathLengthOverrun,
	FindPathTo,
	ChildrenOf,
	FathersToo,
	BushOf,
	Metaphor,
	ValidateLicense,
	GetModules,
	loopback,
	NewObjID,
	sync,
	Reports,
	Report,
	Preview,
	Def,
	Log,
	propertyName,
	SRCID,
	path,
	IDS,
	sqlStart,
	sqlPart,
	ExecutePreparedProcedure,
	sqlParam,
	IsNode,
	Part,
	ParentFolderID,
	Sort,
	GroupFolder,
	ReportPrint,
	DynamicEntityID,
	DynamicAttributeID,
	DynamicList,
	DynamicOperation,
	EntityID,
	AttributeIDs,
	EdgeIDs,
	WithDeleted;
	// @formatter:on

	@Override
	public String toString(){
		return name();
	}
}
