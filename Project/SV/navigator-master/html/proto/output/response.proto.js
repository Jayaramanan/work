"use strict";
/** @suppress {duplicate}*/var response;
if (typeof(response)=="undefined") {response = {};}

response.Envelope = PROTO.Message("response.Envelope",{
	Status: PROTO.Enum("response.Envelope.Status",{
		SUCCESS :1,
		FAILED :2,
		SESSION_EXPIRED :3,
		INVALID_SCHEMA :4	}),
	status: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return response.Envelope.Status;},
		id: 1
	},
	errorMessage: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 2
	},
	payload: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bytes;},
		id: 3
	}});
response.User = PROTO.Message("response.User",{
	userId: {
		options: {default_value:-1},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	userName: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 2
	},
	firstName: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 3
	},
	lastName: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 4
	},
	password: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 5
	},
	sid: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 6
	}});
response.Login = PROTO.Message("response.Login",{
	Status: PROTO.Enum("response.Login.Status",{
		SUCCESS :1,
		INVALID_CREDENTIALS :2,
		ERROR_GET_SALT :3	}),
	status: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return response.Login.Status;},
		id: 1
	},
	sessionId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 2
	},
	user: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return response.User;},
		id: 3
	},
	salt: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 4
	},
	groupId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 5
	},
	instance: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 6
	}});
response.Schemas = PROTO.Message("response.Schemas",{
	schemas: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.Schema;},
		id: 1
	}});
response.Schema = PROTO.Message("response.Schema",{
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
	entities: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.Entity;},
		id: 3
	}});
response.Entity = PROTO.Message("response.Entity",{
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
	objectTypeId: {
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
	description: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 5
	},
	canRead: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 6
	},
	canCreate: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 7
	},
	canUpdate: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 8
	},
	canDelete: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 9
	},
	attributes: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.Attribute;},
		id: 10
	},
	contexts: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.Context;},
		id: 11
	},
	urlOperations: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.UrlOperation;},
		id: 12
	}});
response.UrlOperation = PROTO.Message("response.UrlOperation",{
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	label: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 2
	},
	url: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 3
	},
	sort: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 4
	}});
response.Attribute = PROTO.Message("response.Attribute",{
	EditOption: PROTO.Enum("response.Attribute.EditOption",{
		NOT_VISIBLE :0,
		READ_ONLY :1,
		READ_WRITE :2,
		MANDATORY :3	}),
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	name: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 2
	},
	label: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 3
	},
	description: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 4
	},
	predefined: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 5
	},
	inFilter: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 6
	},
	inLabel: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 7
	},
	inToolTip: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 8
	},
	inAdvancedSearch: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 10
	},
	inSimpleSearch: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 11
	},
	inMatrix: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 12
	},
	inMetaphor: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 13
	},
	inExport: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 14
	},
	inPrefilter: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 15
	},
	inContext: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 16
	},
	dataTypeId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 17
	},
	sort: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 18
	},
	sortLabel: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 19
	},
	sortFilter: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 20
	},
	sortSearch: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 21
	},
	sortMatrix: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 22
	},
	labelBold: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 23
	},
	labelItalic: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 24
	},
	labelUnderline: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 25
	},
	contentBold: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 26
	},
	contentItalic: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 27
	},
	contentUnderline: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 28
	},
	format: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 29
	},
	editFormat: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 30
	},
	validCharacters: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 31
	},
	invalidCharacters: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 32
	},
	minValue: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 33
	},
	maxValue: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 34
	},
	regularExpression: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 35
	},
	valueDescription: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 36
	},
	multivalue: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 37
	},
	aggregable: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 38
	},
	canRead: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 39
	},
	editLock: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return response.Attribute.EditOption;},
		id: 40
	},
	editUnlock: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return response.Attribute.EditOption;},
		id: 41
	},
	formula: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 42
	},
	values: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.AttributeValue;},
		id: 43
	}});
response.AttributeValue = PROTO.Message("response.AttributeValue",{
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	parentId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 2
	},
	label: {
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
	},
	sort: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 5
	},
	haloColor: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 6
	},
	haloColorSelected: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 7
	},
	toUse: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 8
	}});
response.Context = PROTO.Message("response.Context",{
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	name: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 2
	},
	pkAttributeId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 3
	},
	relatedAttributes: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return PROTO.int32;},
		id: 4
	}});
response.SyncResult = PROTO.Message("response.SyncResult",{
	Status: PROTO.Enum("response.SyncResult.Status",{
		OK :1,
		FAILED :2,
		CONNECTION_ERROR :3,
		LOCK_ERROR :4	}),
	status: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return response.SyncResult.Status;},
		id: 1
	},
	totalCount: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 2
	},
	hasMoreToProcess: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 3
	},
	processDeltas: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return response.ProcessedDeltas;},
		id: 4
	},
	deltas: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return response.Deltas;},
		id: 5
	},
	errorCount: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 6
	},
	userIds: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return PROTO.int32;},
		id: 7
	},
	groupIds: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return PROTO.int32;},
		id: 8
	},
	userGroups: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.UserGroup;},
		id: 9
	},
	icons: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.Icon;},
		id: 10
	},
	okCount: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 11
	},
	warnCount: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 12
	}});
response.Icon = PROTO.Message("response.Icon",{
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	iconName: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 2
	},
	iconData: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bytes;},
		id: 3
	}});
response.UserGroup = PROTO.Message("response.UserGroup",{
	userId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	groupId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 2
	}});
response.ProcessedDelta = PROTO.Message("response.ProcessedDelta",{
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
response.ProcessedDeltas = PROTO.Message("response.ProcessedDeltas",{
	processed: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.ProcessedDelta;},
		id: 1
	}});
response.Deltas = PROTO.Message("response.Deltas",{
	deltas: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.Delta;},
		id: 1
	}});
response.Delta = PROTO.Message("response.Delta",{
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
	creatorId: {
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
	deltaParams: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.DeltaParam;},
		id: 7
	}});
response.DeltaParam = PROTO.Message("response.DeltaParam",{
	name: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 1
	},
	value: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 2
	}});
response.ActivityStream = PROTO.Message("response.ActivityStream",{
	activities: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.Activity;},
		id: 1
	}});
response.Activity = PROTO.Message("response.Activity",{
	deltaType: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	user: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return response.User;},
		id: 2
	},
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int64;},
		id: 3
	},
	objectId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 4
	},
	objectName: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 5
	},
	timestamp: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int64;},
		id: 6
	}});
response.Reports = PROTO.Message("response.Reports",{
	reports: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.Report;},
		id: 1
	},
	reportPrint: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bytes;},
		id: 2
	}});
response.Report = PROTO.Message("response.Report",{
	ReportType: PROTO.Enum("response.Report.ReportType",{
		DYNAMIC :1,
		STATIC :2	}),
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	name: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 2
	},
	type: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return response.Report.ReportType;},
		id: 3
	},
	preview: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bytes;},
		id: 4
	}});
response.ThematicData = PROTO.Message("response.ThematicData",{
	polygons: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.GisPolygon;},
		id: 1
	}});
response.GisGeometry = PROTO.Message("response.GisGeometry",{
	polygons: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.GisPolygon;},
		id: 1
	}});
response.GisPolygon = PROTO.Message("response.GisPolygon",{
	gisId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	polygon: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return response.GisRing;},
		id: 2
	},
	exclusions: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.GisRing;},
		id: 3
	}});
response.GeoAnalytics = PROTO.Message("response.GeoAnalytics",{
	folders: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.GeoThematicFolder;},
		id: 1
	},
	territories: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.GeoTerritory;},
		id: 2
	},
	clusters: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.GeoThematicCluster;},
		id: 3
	},
	geometryIds: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return PROTO.int32;},
		id: 4
	}});
response.GeoThematicFolder = PROTO.Message("response.GeoThematicFolder",{
	id: {
		options: {},
		multiplicity: PROTO.required,
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
	thematicMaps: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.GeoThematicMap;},
		id: 4
	}});
response.GeoThematicMap = PROTO.Message("response.GeoThematicMap",{
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
	groupId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
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
		type: function(){return response.GeoThematicCluster;},
		id: 6
	}});
response.GeoThematicCluster = PROTO.Message("response.GeoThematicCluster",{
	id: {
		options: {},
		multiplicity: PROTO.required,
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
response.GeoTerritory = PROTO.Message("response.GeoTerritory",{
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	name: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 2
	},
	sum: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.Double;},
		id: 3
	},
	nodeCount: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 4
	}});
response.GIS = PROTO.Message("response.GIS",{
	map: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return response.GisMap;},
		id: 1
	},
	territories: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.GisTerritory;},
		id: 2
	},
	maps: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.GisMap;},
		id: 3
	},
	overlays: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.GisOverlay;},
		id: 4
	},
	overlayGeometryIds: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return PROTO.int32;},
		id: 5
	}});
response.GisOverlay = PROTO.Message("response.GisOverlay",{
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
	},
	name: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.string;},
		id: 3
	},
	lineColor: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 4
	},
	lineWidth: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 5
	},
	filled: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 6
	},
	version: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 7
	}});
response.GisMap = PROTO.Message("response.GisMap",{
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
	}});
response.GisTerritory = PROTO.Message("response.GisTerritory",{
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	label: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.string;},
		id: 2
	},
	territory: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.string;},
		id: 3
	},
	tableName: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 4
	},
	version: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 5
	}});
response.GisRing = PROTO.Message("response.GisRing",{
	points: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.GisPoint;},
		id: 1
	}});
response.GisPoint = PROTO.Message("response.GisPoint",{
	x: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.Double;},
		id: 1
	},
	y: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.Double;},
		id: 2
	}});
response.Palette = PROTO.Message("response.Palette",{
	colors: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.Color;},
		id: 1
	}});
response.Color = PROTO.Message("response.Color",{
	sequence: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	color: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.string;},
		id: 2
	}});
response.Favorites = PROTO.Message("response.Favorites",{
	Result: PROTO.Enum("response.Favorites.Result",{
		OK :0,
		WARNING_OBSOLETE :1,
		ERROR_OBSOLETE :2,
		ERROR :-1	}),
	favorites: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.Favorite;},
		id: 1
	},
	result: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return response.Favorites.Result;},
		id: 2
	}});
response.Favorite = PROTO.Message("response.Favorite",{
	id: {
		options: {},
		multiplicity: PROTO.required,
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
response.ObjectConnections = PROTO.Message("response.ObjectConnections",{
	objectConnections: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.ObjectConnection;},
		id: 1
	}});
response.ObjectConnection = PROTO.Message("response.ObjectConnection",{
	fromObject: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	toObject: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 2
	},
	connectionObject: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 3
	},
	connectionType: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 4
	},
	lineStyle: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 5
	},
	lineWidth: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.Float;},
		id: 6
	},
	color: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.string;},
		id: 7
	}});
response.Folders = PROTO.Message("response.Folders",{
	folders: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.Folder;},
		id: 1
	}});
response.Folder = PROTO.Message("response.Folder",{
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	parentId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 2
	},
	folderName: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.string;},
		id: 3
	},
	schemaId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 4
	},
	creatorId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 5
	},
	groupFolder: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.bool;},
		id: 6
	},
	sortOrder: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 7
	}});
response.Settings = PROTO.Message("response.Settings",{
	settings: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.Setting;},
		id: 1
	}});
response.Setting = PROTO.Message("response.Setting",{
	userId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	section: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.string;},
		id: 2
	},
	property: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.string;},
		id: 3
	},
	value: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.string;},
		id: 4
	}});
response.LanguageItems = PROTO.Message("response.LanguageItems",{
	items: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.LanguageItem;},
		id: 1
	}});
response.LanguageItem = PROTO.Message("response.LanguageItem",{
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	property: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.string;},
		id: 2
	},
	value: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.string;},
		id: 3
	}});
response.Charts = PROTO.Message("response.Charts",{
	charts: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.Chart;},
		id: 1
	},
	objectCharts: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.ObjectChart;},
		id: 2
	},
	chartAttributes: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.ChartAttribute;},
		id: 3
	},
	minVal: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.Double;},
		id: 4
	},
	maxVal: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.Double;},
		id: 5
	},
	maxSliceVal: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.Double;},
		id: 6
	}});
response.Chart = PROTO.Message("response.Chart",{
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	name: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 2
	},
	comment: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 3
	},
	schemaId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 4
	},
	objectCharts: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.ObjectChart;},
		id: 5
	}});
response.ObjectChart = PROTO.Message("response.ObjectChart",{
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	objectId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 2
	},
	chartId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 3
	},
	minValue: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 4
	},
	maxValue: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 5
	},
	minScale: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.Double;},
		id: 6
	},
	maxScale: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.Double;},
		id: 7
	},
	labelInUse: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.bool;},
		id: 8
	},
	labelFont: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 9
	},
	numberFormat: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 10
	},
	displayOperation: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 11
	},
	chartType: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 12
	},
	isValueDisplayed: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 13
	},
	fontColor: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.string;},
		id: 14
	},
	chartAttributes: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.ChartAttribute;},
		id: 15
	}});
response.ChartAttribute = PROTO.Message("response.ChartAttribute",{
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	objectChartId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 2
	},
	attributeId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 3
	},
	rgb: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.string;},
		id: 4
	}});
response.Icons = PROTO.Message("response.Icons",{
	iconBytes: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bytes;},
		id: 1
	},
	iconNames: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return PROTO.string;},
		id: 2
	}});
response.MetaphorSets = PROTO.Message("response.MetaphorSets",{
	metaphorSets: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return PROTO.string;},
		id: 1
	}});
response.NodeIcons = PROTO.Message("response.NodeIcons",{
	nodeIcon: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.NodeIcon;},
		id: 1
	}});
response.NodeIcon = PROTO.Message("response.NodeIcon",{
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
	}});
response.Creator = PROTO.Message("response.Creator",{
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	}});
response.ObjectManagement = PROTO.Message("response.ObjectManagement",{
	id: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 1
	}});
response.Prefilter = PROTO.Message("response.Prefilter",{
	item: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.PrefilterItem;},
		id: 1
	}});
response.PrefilterItem = PROTO.Message("response.PrefilterItem",{
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	groupId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 2
	},
	schemaId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 3
	},
	objectDefinitionId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 4
	},
	attributeId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 5
	},
	predefinedId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 6
	}});
response.ObjectDeleteAccessResult = PROTO.Message("response.ObjectDeleteAccessResult",{
	result: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.bool;},
		id: 1
	}});
response.License = PROTO.Message("response.License",{
	valid: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.bool;},
		id: 1
	},
	baseModule: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.bool;},
		id: 2
	},
	dataCaptureModule: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 3
	},
	chartsModule: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 4
	},
	mapsModule: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 5
	},
	geoAnalyticsModule: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 6
	},
	remoteClientModule: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 7
	},
	reportsModule: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.bool;},
		id: 8
	}});
response.SimpleSearch = PROTO.Message("response.SimpleSearch",{
	object: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.DBObject;},
		id: 1
	}});
response.DBObject = PROTO.Message("response.DBObject",{
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	entityId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 2
	},
	metaphor: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return response.NodeMetaphor;},
		id: 3
	},
	dataPair: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.DataPair;},
		id: 4
	}});
response.DataPair = PROTO.Message("response.DataPair",{
	attributeId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	value: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.string;},
		id: 2
	}});
response.NodeMetaphor = PROTO.Message("response.NodeMetaphor",{
	assignedIcon: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return response.MetaphorIcon;},
		id: 1
	},
	metaphorSets: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return PROTO.string;},
		id: 2
	},
	metaphors: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.MetaphorIcon;},
		id: 3
	}});
response.MetaphorIcon = PROTO.Message("response.MetaphorIcon",{
	iconName: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.string;},
		id: 1
	},
	priority: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 2
	}});
response.Graph = PROTO.Message("response.Graph",{
	Result: PROTO.Enum("response.Graph.Result",{
		OK :1,
		TOO_MUCH_NODES :2,
		PATH_NOT_FOUND :3	}),
	result: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return response.Graph.Result;},
		id: 1
	},
	nodes: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.Node;},
		id: 2
	},
	edges: {
		options: {},
		multiplicity: PROTO.repeated,
		type: function(){return response.Edge;},
		id: 3
	}});
response.Node = PROTO.Message("response.Node",{
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	objectDefinitionId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 2
	},
	childrenCount: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 3
	},
	parentCount: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 4
	},
	creatorId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 5
	},
	creatorGroupId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 6
	},
	status: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 7
	}});
response.Edge = PROTO.Message("response.Edge",{
	id: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 1
	},
	objectDefinitionId: {
		options: {},
		multiplicity: PROTO.required,
		type: function(){return PROTO.int32;},
		id: 2
	},
	connectionType: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 3
	},
	strength: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.Double;},
		id: 4
	},
	inPath: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 5
	},
	directed: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 6
	},
	favoriteId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 7
	},
	creatorId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 8
	},
	creatorGroupId: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 9
	},
	status: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return PROTO.int32;},
		id: 10
	},
	fromNode: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return response.Node;},
		id: 11
	},
	toNode: {
		options: {},
		multiplicity: PROTO.optional,
		type: function(){return response.Node;},
		id: 12
	}});
