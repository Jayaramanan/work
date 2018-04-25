"use strict";
/** @suppress {duplicate}*/var request;
if (typeof(request)=="undefined") {request = {};}

request.Login = PROTO.Message("request.Login",{
	Action: PROTO.Enum("request.Login.Action",{
		LOGIN_BY_PASSWORD :1,
		LOGIN_BY_SID :2,
		LOGIN_BY_SSO :3,
		RESET_PASSWORD :4,
		CHANGE_PASSWORD :5,
		LOGOUT :6,
		GET_SALT_FOR_USER :7	}),
	action: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return request.Login.Action;},
		id: 1
	},
	userId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 2
	},
	userName: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 3
	},
	password: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 4
	},
	sid: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 5
	},
	sso: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 6
	},
	newPassword: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 7
	},
	email: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 8
	},
	sync: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 9
	}});
request.FavoriteManagement = PROTO.Message("request.FavoriteManagement",{
	Action: PROTO.Enum("request.FavoriteManagement.Action",{
		CREATE :1,
		DELETE :2,
		UPDATE :3,
		COPY :4,
		GET_ALL_FOR_SCHEMA :5,
		GET_FAVORITE_DATA :6,
		VALIDATE_VERSION :7	}),
	action: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return request.FavoriteManagement.Action;},
		id: 1
	},
	favorite: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return request.Favorite;},
		id: 2
	},
	schemaId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 3
	},
	id: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 4
	}});
request.FavoritesFolderManagement = PROTO.Message("request.FavoritesFolderManagement",{
	Action: PROTO.Enum("request.FavoritesFolderManagement.Action",{
		GET_ALL_FOLDERS :1,
		CREATE :2,
		DELETE :3,
		UPDATE :4	}),
	action: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return request.FavoritesFolderManagement.Action;},
		id: 1
	},
	schemaId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 2
	},
	folder: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return request.FavoritesFolder;},
		id: 3
	}});
request.Favorite = PROTO.Message("request.Favorite",{
	id: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 1
	},
	description: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 2
	},
	schemaId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 3
	},
	data: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 4
	},
	layout: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 5
	},
	name: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 6
	},
	creatorId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 7
	},
	folderId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 8
	},
	groupFavorite: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 9
	},
	mode: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 10
	}});
request.FavoritesFolder = PROTO.Message("request.FavoritesFolder",{
	id: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 1
	},
	name: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 2
	},
	schemaId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 3
	},
	sort: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 4
	},
	parentFolderId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 5
	},
	groupFolder: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 6
	}});
request.Synchronize = PROTO.Message("request.Synchronize",{
	Action: PROTO.Enum("request.Synchronize.Action",{
		CHECK_TABLE_LOCK :1,
		CHECK_CONNECTIVITY :2,
		GET_COUNT_TO_PUSH :3,
		PUSH_DELTAS :4,
		PROCESS_OFFLINE_DELTAS :5,
		LOGOUT_MASTER :6,
		GET_COUNT_TO_ROLLON :7,
		GET_MASTER_DELTAS_COUNT :8,
		ROLLON_MASTER_DELTAS :9,
		GET_MASTER_DELTAS :10,
		COMMIT_PROCESSED_DELTAS :11,
		GET_MASTER_USERS :12,
		CALL_SYNC_IMAGES :14,
		GET_MASTER_ICONS :15,
		PREPARE_DATA :16	}),
	action: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return request.Synchronize.Action;},
		id: 1
	},
	deltas: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return request.Deltas;},
		id: 2
	},
	processedIds: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return request.ProcessedDeltas;},
		id: 3
	},
	metaphorPath: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 4
	}});
request.Schema = PROTO.Message("request.Schema",{
	Action: PROTO.Enum("request.Schema.Action",{
		GET_SCHEMA_DATA :1,
		GET_SCHEMAS :2,
		GET_CONNECTIONS :3,
		GET_METAPHOR_SETS :4,
		GET_PREFILTER_DATA :5	}),
	action: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return request.Schema.Action;},
		id: 1
	},
	definitionId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 2
	},
	schemaId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 3
	},
	languageId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 4
	},
	metaphorSetName: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 5
	}});
request.DeltaParam = PROTO.Message("request.DeltaParam",{
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int64;},
		id: 1
	},
	name: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 2
	},
	value: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 3
	}});
request.DeltaHeader = PROTO.Message("request.DeltaHeader",{
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int64;},
		id: 1
	},
	deltaType: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 2
	},
	timestamp: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int64;},
		id: 3
	},
	syncStatus: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 4
	},
	creator: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 5
	},
	isSync: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 6
	},
	params: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return request.DeltaParam;},
		id: 7
	}});
request.Deltas = PROTO.Message("request.Deltas",{
	deltas: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return request.DeltaHeader;},
		id: 1
	}});
request.ProcessedDelta = PROTO.Message("request.ProcessedDelta",{
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int64;},
		id: 1
	},
	status: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 2
	}});
request.ProcessedDeltas = PROTO.Message("request.ProcessedDeltas",{
	processed: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return request.ProcessedDelta;},
		id: 1
	}});
request.ActivityStream = PROTO.Message("request.ActivityStream",{
	count: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	schemaId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 2
	},
	lastId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int64;},
		id: 3
	}});
request.Reports = PROTO.Message("request.Reports",{
	Action: PROTO.Enum("request.Reports.Action",{
		GET_ALL :1,
		GET_PRINT :2	}),
	action: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return request.Reports.Action;},
		id: 1
	},
	report: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return request.Report;},
		id: 2
	}});
request.Report = PROTO.Message("request.Report",{
	ReportFormat: PROTO.Enum("request.Report.ReportFormat",{
		XLS :1,
		PDF :2,
		HTML :3	}),
	reportFormat: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return request.Report.ReportFormat;},
		id: 1
	},
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 2
	},
	graphImage: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bytes;},
		id: 3
	},
	mapImage: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bytes;},
		id: 4
	},
	logoImage: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bytes;},
		id: 5
	},
	data: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return request.ReportData;},
		id: 6
	}});
request.ReportData = PROTO.Message("request.ReportData",{
	entityId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	name: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.string;},
		id: 2
	},
	hasMetaphor: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 3
	},
	isNumericMetaphor: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 4
	},
	attributes: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return request.ReportAttribute;},
		id: 5
	},
	rows: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return request.ReportRow;},
		id: 6
	}});
request.ReportAttribute = PROTO.Message("request.ReportAttribute",{
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	name: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.string;},
		id: 2
	},
	label: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.string;},
		id: 3
	},
	isDynamic: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 4
	}});
request.ReportRow = PROTO.Message("request.ReportRow",{
	values: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return PROTO.string;},
		id: 1
	},
	metaphor: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bytes;},
		id: 2
	},
	index: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 3
	}});
request.GeoAnalytics = PROTO.Message("request.GeoAnalytics",{
	Action: PROTO.Enum("request.GeoAnalytics.Action",{
		GET_FOLDERS_WITH_THEMATIC_MAPS :1,
		GET_THEMATIC_MAP_WITH_CLUSTERS :2,
		GET_GEO_TERRITORIES :3,
		GET_GEOMETRY_IDS_BY_THEMATIC_MAP :4,
		GET_THEMATIC_DATA_BY_GIS_IDS :5,
		GET_GEO_TERRITORIES_BY_LAYER :6,
		GET_GEO_TERRITORIES_FOR_DYNAMIC_ATTRIBUTE :7,
		GET_THEMATIC_MAP_BY_NAME :8,
		SAVE_THEMATIC_MAP_WITH_CLUSTERS :9,
		DELETE_THEMATIC_MAP :10,
		GET_DEFAULT_FOLDER_ID :11	}),
	Source: PROTO.Enum("request.GeoAnalytics.Source",{
		DATABASE :1,
		NODE_SET :2	}),
	action: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return request.GeoAnalytics.Action;},
		id: 1
	},
	attributeId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 2
	},
	source: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return request.GeoAnalytics.Source;},
		id: 3
	},
	gisTerritoryId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 4
	},
	thematicMap: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return request.GeoThematicMap;},
		id: 5
	},
	thematicMapId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 6
	},
	entityId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 7
	},
	schemaId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 8
	},
	gisIds: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return PROTO.int32;},
		id: 9
	},
	nodeIds: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return PROTO.int32;},
		id: 10
	},
	values: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return PROTO.Double;},
		id: 11
	}});
request.GeoThematicMap = PROTO.Message("request.GeoThematicMap",{
	id: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 1
	},
	folderId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 2
	},
	name: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 3
	},
	layerId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 4
	},
	attribute: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 5
	},
	clusters: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return request.GeoThematicCluster;},
		id: 6
	}});
request.GeoThematicCluster = PROTO.Message("request.GeoThematicCluster",{
	id: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 1
	},
	fromValue: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.Double;},
		id: 2
	},
	toValue: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.Double;},
		id: 3
	},
	color: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 4
	},
	gisIds: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 5
	},
	description: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 6
	}});
request.GeoMap = PROTO.Message("request.GeoMap",{
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	schemaId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 2
	}});
request.ObjectManagement = PROTO.Message("request.ObjectManagement",{
	Action: PROTO.Enum("request.ObjectManagement.Action",{
		UPDATE_NODE_METAPHOR :1,
		UPDATE_NODE_GEO_COORDS :2,
		DELETE :4,
		INSERT_NODE :5,
		INSERT_EDGE :6,
		UPDATE_NODE :7,
		UPDATE_EDGE :8,
		MERGE_NODE :9,
		SET_CONTEXT :10,
		CLEAR_CONTEXT :11,
		CLONE_CONTEXT :12,
		CHECK_CAN_DELETE_NODE :13	}),
	action: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return request.ObjectManagement.Action;},
		id: 1
	},
	nodeId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 2
	},
	iconName: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 3
	},
	latitude: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.Double;},
		id: 4
	},
	longitude: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.Double;},
		id: 5
	},
	geoCoords: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return request.GeoCoords;},
		id: 6
	},
	schemaId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 7
	},
	objectId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 8
	},
	groupId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 9
	},
	entityId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 10
	},
	contextId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 11
	},
	oldFavoriteId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 12
	},
	favoriteId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 13
	},
	fromId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 14
	},
	toId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 15
	},
	nodeIds: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return PROTO.int32;},
		id: 16
	},
	edgeIds: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return PROTO.int32;},
		id: 17
	},
	attributeIds: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return PROTO.int32;},
		id: 18
	},
	values: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return PROTO.string;},
		id: 19
	}});
request.GeoCoords = PROTO.Message("request.GeoCoords",{
	lon: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.Double;},
		id: 1
	},
	lat: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.Double;},
		id: 2
	}});
request.GIS = PROTO.Message("request.GIS",{
	Action: PROTO.Enum("request.GIS.Action",{
		GET_MAP :1,
		GET_TERRITORIES :2,
		GET_MAPS :3,
		GET_OVERLAYS :4,
		GET_OVERLAY_DATA :5,
		GET_OVERLAY_GEOMETRY :7	}),
	action: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return request.GIS.Action;},
		id: 1
	},
	mapId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 2
	},
	schemaId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 3
	},
	overlayId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 4
	},
	overlayGeometryId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 5
	}});
request.Palette = PROTO.Message("request.Palette",{
	Action: PROTO.Enum("request.Palette.Action",{
		GET_PALETTE :1	}),
	action: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return request.Palette.Action;},
		id: 1
	},
	id: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 2
	}});
request.Settings = PROTO.Message("request.Settings",{
	Action: PROTO.Enum("request.Settings.Action",{
		GET_ALL_SETTINGS :1,
		SAVE_USER_SETTING :2	}),
	action: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return request.Settings.Action;},
		id: 1
	},
	section: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 2
	},
	property: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 3
	},
	value: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 4
	}});
request.Language = PROTO.Message("request.Language",{
	Action: PROTO.Enum("request.Language.Action",{
		GET_TRANSLATIONS :1	}),
	action: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return request.Language.Action;},
		id: 1
	},
	id: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 2
	}});
request.Charts = PROTO.Message("request.Charts",{
	Action: PROTO.Enum("request.Charts.Action",{
		GET_CHARTS :1,
		GET_OBJECT_CHARTS :2,
		GET_CHART_ATTRIBUTES :3,
		GET_CHART_WITH_PARAMETERS :4,
		GET_CHART_LIMITS :5	}),
	action: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return request.Charts.Action;},
		id: 1
	},
	schemaId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 2
	},
	chartId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 3
	},
	arguments: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 4
	},
	attributeIds: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return PROTO.int32;},
		id: 5
	},
	entity: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 6
	}});
request.Icons = PROTO.Message("request.Icons",{
	Action: PROTO.Enum("request.Icons.Action",{
		GET_ICON_BY_NAME :1,
		GET_ICON_NAMES :2	}),
	action: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return request.Icons.Action;},
		id: 1
	},
	iconName: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 2
	}});
request.License = PROTO.Message("request.License",{
	Action: PROTO.Enum("request.License.Action",{
		GET_LICENSE :1	}),
	action: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return request.License.Action;},
		id: 1
	}});
request.Search = PROTO.Message("request.Search",{
	Action: PROTO.Enum("request.Search.Action",{
		PERFORM_SIMPLE_SEARCH :1,
		PERFORM_ADVANCED_SEARCH :2,
		PERFORM_GET_LIST :3,
		PERFORM_GET_LIST_CONTEXT :4,
		PERFORM_GET_LIST_UNKNOWN :5	}),
	action: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return request.Search.Action;},
		id: 1
	},
	term: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 2
	},
	schemaId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 3
	},
	preFilter: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return request.Filter;},
		id: 4
	},
	geoSearchCriteria: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 5
	},
	queryType: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 6
	},
	limit: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 7
	},
	contextId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 8
	},
	contextKey: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 9
	},
	includeDeleted: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 10
	},
	section: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return request.SearchSection;},
		id: 11
	},
	missing: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return request.Missing;},
		id: 12
	}});
request.Filter = PROTO.Message("request.Filter",{
	valueId: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return PROTO.int32;},
		id: 3
	}});
request.Missing = PROTO.Message("request.Missing",{
	entityId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 1
	},
	id: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return PROTO.int32;},
		id: 2
	}});
request.SearchSection = PROTO.Message("request.SearchSection",{
	entity: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	condition: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return request.SearchCondition;},
		id: 2
	},
	order: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return request.SearchOrder;},
		id: 3
	}});
request.SearchCondition = PROTO.Message("request.SearchCondition",{
	attributeId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	operation: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.string;},
		id: 2
	},
	term: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.string;},
		id: 3
	}});
request.SearchOrder = PROTO.Message("request.SearchOrder",{
	attributeId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	asc: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.bool;},
		id: 2
	}});
request.CalculateDynamicAttributes = PROTO.Message("request.CalculateDynamicAttributes",{
	dynamicAttribute: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return request.DynamicAttribute;},
		id: 1
	}});
request.DynamicAttribute = PROTO.Message("request.DynamicAttribute",{
	fakeId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	fromEntity: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 2
	},
	fromAttribute: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 3
	},
	operation: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.string;},
		id: 4
	},
	schemaId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 5
	},
	id: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return PROTO.int32;},
		id: 6
	}});
request.Graph = PROTO.Message("request.Graph",{
	Action: PROTO.Enum("request.Graph.Action",{
		GET_NODES :1,
		GET_EDGES :2,
		GET_NODES_WITH_EDGES :3,
		RELOAD_NODE :4,
		FIND_PATH :5,
		GET_FAVORITES_EDGES :6,
		GET_NODES_BY_EDGES :7	}),
	action: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return request.Graph.Action;},
		id: 1
	},
	schemaId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 2
	},
	dataFilter: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return request.Filter;},
		id: 3
	},
	nodeId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 4
	},
	nodeToId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 5
	},
	maxNodeCount: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 6
	},
	maxPathLenght: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 7
	},
	pathLengthOverrun: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 8
	},
	favoriteId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 9
	},
	objectIds: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return PROTO.int32;},
		id: 10
	}});
