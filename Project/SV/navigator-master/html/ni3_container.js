//TODO move this variables to Ni3Container or Document
var w = 960, h = 700;
var metaphorUrl = "http://ni3-navigator.net/metaphor";
var imageUrl = "http://ni3-navigator.net/images";
var metaphorSet = "Default";
var CHARTS_NONE = 0;
var CHARTS_PIE = 2;
var loadedChart;

var tooltip;

function Ni3Container() {
    this.graphController = new GraphController();
    this.chartSelectDialog = new ChartSelectDialog();
    this.createEditDialog = new CreateEditDialog();
    this.dynamicChartDialog = new DynamicChartDialog();
    this.filterPanel = new FilterPanel();
    this.advancedSearchDialog = new AdvancedSearchDialog();
    this.matrix = new Matrix();
    this.mapPanel = new MapPanel();
    this.aboutDialog = new AboutDialog();
    this.changeSchemaDialog = new ChangeSchemaDialog();
    this.favoritesDialog = new FavoritesDialog();
}

Ni3Container.prototype.constructor = Ni3Container;

Ni3Container.prototype.init = function () {
    this.initLayout();
    this.loadSettings();
    this.loadAllSchemas();
};

Ni3Container.prototype.initPanels = function () {
    initToolbar();
    this.graphController.init();
    this.mapPanel.initMap();
};

Ni3Container.prototype.removeNode = function (node) {
    var objects = app.ni3Model.getDataObjects();
    var index = null;
    objects[node.objectDefinitionId].forEach(function (obj, i) {
        if (obj.id == node.id)
            index = i;
    });
    if (index)
        objects[node.objectDefinitionId].splice(index, 1);
    app.ni3Container.matrix.updateMatrixTableData(app.ni3Model.getDataObjects());
};

Ni3Container.prototype.showNoResultsMessage = function () {
    alert("Nothing found by given criteria");
};

Ni3Container.prototype.searchAndShow = function (searchStr) {
    if (app.ni3Model.isNewSearchMode()) {
        this.graphController.clearGraph();
        app.ni3Model.clearDataObjects();
        this.mapPanel.clearMap();
    }

    this.filterPanel.resetFilter();
    this.simpleSearch(searchStr);
};


Ni3Container.prototype.initLayout = function () {
    d3.select("body")
        .append("div")
        .attr("id", "toolbar");
    d3.select("body")
        .append("div")
        .attr("id", "vsplitter");

    d3.select("#vsplitter")
        .append("div")
        .attr("id", "hsplitter")
        .append("div")
        .attr("id", "filterPanel");
    d3.select("#hsplitter")
        .append("div")
        .attr("id", "graphMapSplitter");

    d3.select("#graphMapSplitter")
        .append("div")
        .attr("id", "commandGraphSplitter");

    d3.select("#commandGraphSplitter")
        .append("div")
        .attr("id", "commandPanel");

    d3.select("#commandGraphSplitter")
        .append("div")
        .attr("id", "graphPanel");

    d3.select("#vsplitter")
        .append("div")
        .attr("id", "matrixPanel");

    d3.select("#graphMapSplitter")
        .append("div")
        .attr("id", "mapPanel")
        .attr("class", "map");

    tooltip = d3.select("body")
        .append("div")
        .attr("class", "tooltip")
        .style("position", "absolute")
        .style("z-index", "1000")
        .style("visibility", "hidden");

    jQuery(function ($) {
        $('#hsplitter').split({orientation:'vertical', initial_position:0.15});
        $('#graphMapSplitter').split({orientation:'vertical'});
        $('#commandGraphSplitter').split({orientation:'vertical', limit:50}).position(50);
        $('#vsplitter').split({orientation:'horizontal', initial_position:0.6});
    });

    this.filterPanel.initPanels();
};

Ni3Container.prototype.getIconName = function (dbObject) {
    var iconName;
    if (dbObject != undefined && dbObject.metaphor != undefined) {
        var metaphor = dbObject.metaphor;
        if (metaphor.assignedIcon != undefined) {
            iconName = metaphor.assignedIcon.iconName;
        } else {
            var mSets = metaphor.metaphorSets;
            var icons = metaphor.metaphors;
            for (var i = 0; i < mSets.length; i++) {
                if (mSets[i] == metaphorSet) {
                    iconName = icons[i].iconName;
                    break;
                }
            }
        }
    }
    return iconName;
};

Ni3Container.prototype.getFullIconName = function (dbObject) {
    return metaphorUrl + "/" + this.getIconName(dbObject);
};

//TODO move to graph?
Ni3Container.prototype.showTooltip = function (node) {
    tooltip.style("visibility", "visible")
        .style("top", (d3.event.pageY - 10) + "px")
        .style("left", (d3.event.pageX + 10) + "px")
        .html(app.ni3Container.getTooltipText(node));
};

//TODO move to graph?
Ni3Container.prototype.hideTooltip = function () {
    tooltip.style("visibility", "hidden");
};

Ni3Container.prototype.getTooltipText = function (node) {
    var tooltipHtml = "";

    var od = app.ni3Model.getEntityById(node.objectDefinitionId);
    if (od != undefined) {
        tooltipHtml += "<TABLE border=0 CELLSPACING=0 CELLPADDING=0>";
        var attributes = od.attributes;
        for (var i = 0; i < attributes.length; i++) {
            var attribute = attributes[i];
            if (attribute.inToolTip && attribute.canRead) {
                var value = this.getDisplayValue(node.dbObject, attribute);
                if (value != undefined) {
                    tooltipHtml += "<TR><TD valign=\"top\">";

                    tooltipHtml += this.formatLabel(attribute);
                    tooltipHtml += ":&nbsp;</TD><TD>";

                    tooltipHtml += this.formatValue(value, attribute);

                    tooltipHtml += "</TD></TR>";
                }
            }
        }
        tooltipHtml += "</TABLE>";
    }
    return tooltipHtml;
};

//TODO replace tags with CSS
Ni3Container.prototype.formatLabel = function (attribute) {
    var labelPrefix = "";
    var labelSuffix = "";
    if (attribute.labelBold) {
        labelPrefix += "<B>";
        labelSuffix += "</B>";
    }
    if (attribute.labelUnderline) {
        labelPrefix += "<U>";
        labelSuffix = "</U>" + labelSuffix;
    }
    if (attribute.labelItalic) {
        labelPrefix += "<I>";
        labelSuffix = "</I>" + labelSuffix;
    }
    return labelPrefix + attribute.label + labelSuffix;
};

//TODO replace tags with CSS
Ni3Container.prototype.formatValue = function (value, attribute) {
    var labelPrefix = "";
    var labelSuffix = "";
    if (attribute.contentBold) {
        labelPrefix += "<B>";
        labelSuffix += "</B>";
    }
    if (attribute.contentUnderline) {
        labelPrefix += "<U>";
        labelSuffix = "</U>" + labelSuffix;
    }
    if (attribute.contentItalic) {
        labelPrefix += "<I>";
        labelSuffix = "</I>" + labelSuffix;
    }
    return labelPrefix + value + labelSuffix;
};

Ni3Container.prototype.getDisplayValue = function (dbObject, attribute) {
    var value = this.getValue(dbObject, attribute.id);
    var displayValue;
    if (value != undefined) {
        if (attribute.predefined) {
            if (attribute.multivalue) {
                var valIds = this.getMultivalues(value);
                var values = [];
                for (var i = 0; i < valIds.length; i++) {
                    var prValue = this.getPredefinedValue(valIds[i], attribute);
                    if (prValue != undefined) {
                        values.push(prValue.label);
                    }
                }
                displayValue = values.join(";");
            } else {
                var pValue = this.getPredefinedValue(value, attribute);
                if (pValue != undefined) {
                    displayValue = pValue.label;
                }
            }
        } else {
            if (attribute.multivalue) {
                displayValue = this.getMultivalues(value).join(";");
            } else {
                displayValue = value;
            }
        }
    }

    return displayValue;
};

Ni3Container.prototype.getPredefinedValue = function (value, attribute) {
    var result;
    var values = attribute.values;
    for (var i = 0; i < values.length; i++) {
        if (value == values[i].id) {
            result = values[i];
            break;
        }
    }
    return result;
};

Ni3Container.prototype.getValue = function (dbObject, attributeId) {
    var value;
    if (dbObject != undefined) {
        var data = dbObject.dataPair;
        for (var i = 0; i < data.length; i++) {
            if (attributeId == data[i].attributeId) {
                value = data[i].value;
                break;
            }
        }
    }
    return value;
};

Ni3Container.prototype.getMultivalues = function (value) {
    var values = [];
    if (value != null && value != undefined && value.length > 0) {
        values = value.split("}{").map(function (v) {
            return v.replace(/[{}]/g, "");
        });
    }
    return values;
};

Ni3Container.prototype.getAttribute = function (entityId, attributeId) {
    var result;
    var entity = app.ni3Model.getEntityById(entityId);
    var attributes = entity.attributes;
    for (var i = 0; i < attributes.length; i++) {
        if (attributes[i].id == attributeId) {
            result = attributes[i];
            break;
        }
    }
    return result;
};

Ni3Container.prototype.simpleSearchHandler = function (payload, putOnGraphCount) {
    var search = new response.SimpleSearch();
    search.ParseFromStream(payload);
    var objects = search.object;
    if (objects.length == 0)
        app.ni3Container.showNoResultsMessage();
    app.ni3Model.fillDataObjects(objects);
    app.ni3Container.matrix.updateMatrixTableData(app.ni3Model.getDataObjects());

    if (putOnGraphCount != undefined && putOnGraphCount != "None" && objects.length > 0) {
        app.ni3Container.putNodesOnGraph(objects, putOnGraphCount);
    }

    showDefaultCursor();
};

Ni3Container.prototype.putNodesOnGraph = function (objects, putOnGraphCount) {
    if (app.ni3Model.isDataSorted()) {
        var comparator = new DbObjectComparator(app.ni3Model.getSortColumns());
        var dbObjects = [];
        for (var i = 0; i < objects.length; i++) {
            dbObjects.push(objects[i]);
        }
        dbObjects.sort(comparator);
        objects = dbObjects;
    }
    var nodesToShow = [];
    if (putOnGraphCount == "All" || objects.length < putOnGraphCount) {
        nodesToShow = objects;
    } else {
        var nodeEntities = app.ni3Model.getNodeEntities();
        for (var e = 0; e < nodeEntities.length; e++) {
            var eId = nodeEntities[e].id;
            var count = 0;
            for (var n = 0; n < objects.length; n++) {
                if (eId == objects[n].entityId) {
                    nodesToShow.push(objects[n]);
                    if (++count >= putOnGraphCount) {
                        break;
                    }
                }
            }
        }
    }
    this.getNodes(nodesToShow);
};

Ni3Container.prototype.advancedSearchHandler = function (payload, params) {
    var type = params[0];
    if (type == NODE_SEARCH_QUERY_TYPE) {
        var putOnGraphCount = params[2];
        app.ni3Container.simpleSearchHandler(payload, putOnGraphCount);
    } else {
        var search = new response.SimpleSearch();
        search.ParseFromStream(payload);
        var objects = search.object;
        if (objects.length > Ni3Model.MAX_OBJECTS_TO_DISPLAY)
            alert("Too many nodes to display ion result");
        else
            app.ni3Container.getNodesWithEdgesByEdges(objects, type == CONNECTED_NODE_SEARCH_QUERY_TYPE ? null : params[1]);
    }
};

Ni3Container.prototype.getNodesWithEdgesByEdges = function (edges, nodeEntityId) {
    var msg = new request.Graph();
    msg.action = request.Graph.Action.GET_NODES_BY_EDGES;
    msg.schemaId = app.ni3Model.getSchemaId();
    msg.dataFilter = app.ni3Model.getDataFilter().getAsMessage();
    for (var i = 0; i < edges.length; i++) {
        msg.objectIds.push(edges[i].id);
    }

    showWaitCursor();
    gateway.sendRequest(msg, "GraphServlet", this.nodesWithEdgesByEdgesHandler, null, nodeEntityId);
};

Ni3Container.prototype.nodesWithEdgesByEdgesHandler = function (payload, nodeEntityId) {
    var graph = new response.Graph();
    graph.ParseFromStream(payload);
    var self = app.ni3Container;
    if (graph.nodes.length == 0)
        self.showNoResultsMessage();

    var graphController = self.graphController;
    graphController.saveAnimationState();
    graphController.showNodes(graph.nodes);

    for (var i = 0; i < graph.nodes.length; i++) {
        graph.nodes[i].toOptimize = true;
    }
    if (nodeEntityId)
        graphController.makeFixedByEntityId(nodeEntityId, graph.nodes);

    graphController.expandNodes(graph.nodes, graph.edges);
    var map = graphController.getMissingMap();
    self.getDbObjects(map, false);
    graphController.restoreAnimationState();
    showDefaultCursor();
};

Ni3Container.prototype.nodeHandler = function (payload, dbObjects) {
    var graph = new response.Graph();
    graph.ParseFromStream(payload);
    var nodes = graph.nodes;
    app.ni3Container.graphController.showNodes(nodes);
    app.ni3Container.graphController.fillDbObjects(dbObjects);
};

Ni3Container.prototype.reloadNodesHandler = function (payload) {
    if (visibleNodes.length > 0) {
        var graph = new response.Graph();
        graph.ParseFromStream(payload);
        var nodes = graph.nodes;
        for (var i = 0; i < nodes.length; i++) {
            var visNode = Utility.getById(visibleNodes, nodes[i].id);
            if (visNode != null) {
                visNode.childrenCount = nodes[i].childrenCount;
                visNode.parentCount = nodes[i].parentCount;
            }
        }
    }
    app.ni3Container.graphController.updateGraph();
    app.ni3Container.mapPanel.showNodesAndEdgesOnMap(visibleNodes, visibleLinks);
    app.ni3Container.matrix.updateMatrixSelection();
};

Ni3Container.prototype.nodeWithEdgeHandler = function (payload, nodes) {
    var graph = new response.Graph();
    graph.ParseFromStream(payload);
    if (graph != null) {
        var links = graph.edges;
        app.ni3Container.graphController.expandNodes(nodes, links);
        var map = app.ni3Container.graphController.getMissingMap();
        app.ni3Container.getDbObjects(map, false);
    }
    showDefaultCursor();
};

Ni3Container.prototype.dbObjectsHandler = function (payload, putOnGraph) {
    showDefaultCursor();
    var search = new response.SimpleSearch();
    search.ParseFromStream(payload);
    var objects = search.object;

    app.ni3Container.graphController.fillDbObjects(objects);
    if (putOnGraph) {
        app.ni3Container.getNodes(objects);
    }
};

Ni3Container.prototype.schemaDataHandler = function (payload, schemaId) {
    var sch = new response.Schema();
    sch.ParseFromStream(payload);
    sch.id = schemaId;
    app.ni3Model.setSchema(sch);
    app.ni3Container.getObjectConnections(schemaId);
};

Ni3Container.prototype.getObjectConnections = function (schemaId) {
    var req = new request.Schema();
    req.action = request.Schema.Action.GET_CONNECTIONS;
    req.schemaId = schemaId;
    gateway.sendRequest(req, "SchemaServlet", app.ni3Container.objectConnectionsDataHandler, null);
};

Ni3Container.prototype.objectConnectionsDataHandler = function (payload) {
    var ocs = new response.ObjectConnections();
    ocs.ParseFromStream(payload);
    app.ni3Model.getSchema().objectConnections = ocs.objectConnections;
    app.ni3Container.matrix.initMatrix();
    app.ni3Container.filterPanel.updateFilterTreeData();
};

Ni3Container.prototype.reloadNodes = function () {
    var msg = new request.Graph();
    msg.action = request.Graph.Action.GET_NODES;
    msg.schemaId = app.ni3Model.getSchemaId();
    msg.dataFilter = app.ni3Model.getDataFilter().getAsMessage();
    for (var i = 0; i < visibleNodes.length; i++) {
        msg.objectIds.push(visibleNodes[i].id);
    }

    gateway.sendRequest(msg, "GraphServlet", this.reloadNodesHandler, null);
};

Ni3Container.prototype.getNodes = function (dbObjects) {
    var msg = new request.Graph();
    msg.action = request.Graph.Action.GET_NODES;
    msg.schemaId = app.ni3Model.getSchemaId();
    msg.dataFilter = app.ni3Model.getDataFilter().getAsMessage();
    for (var i = 0; i < dbObjects.length; i++) {
        msg.objectIds.push(dbObjects[i].id);
    }

    gateway.sendRequest(msg, "GraphServlet", this.nodeHandler, null, dbObjects);
};

Ni3Container.prototype.getNodesWithEdges = function (nodes) {
    var msg = new request.Graph();
    msg.action = request.Graph.Action.GET_NODES_WITH_EDGES;
    msg.schemaId = app.ni3Model.getSchemaId();
    msg.dataFilter = app.ni3Model.getDataFilter().getAsMessage();
    for (var i = 0; i < nodes.length; i++) {
        msg.objectIds.push(nodes[i].id);
    }

    showWaitCursor();
    gateway.sendRequest(msg, "GraphServlet", this.nodeWithEdgeHandler, null, nodes);
};

Ni3Container.prototype.simpleSearch = function (searchStr) {
    var msg = new request.Search();
    msg.action = request.Search.Action.PERFORM_SIMPLE_SEARCH;
    msg.schemaId = app.ni3Model.getSchemaId();
    msg.term = searchStr;
    msg.preFilter = app.ni3Model.getDataFilter().getAsMessage();

    showWaitCursor();
    gateway.sendRequest(msg, "SearchServlet", this.simpleSearchHandler, null);
};

Ni3Container.prototype.getDbObjects = function (missingMap, putOnGraph) {
    var msg = new request.Search();
    msg.action = request.Search.Action.PERFORM_GET_LIST;
    msg.schemaId = app.ni3Model.getSchemaId();
    for (var key in missingMap) {
        var missingMsg = new request.Missing();
        var missing = missingMap[key];
        missingMsg.entityId = key;
        for (var k in missing) {
            missingMsg.id.push(missing[k]);
        }
        msg.missing.push(missingMsg);
    }

    gateway.sendRequest(msg, "SearchServlet", this.dbObjectsHandler, null, putOnGraph);
};

Ni3Container.prototype.getSchemaData = function (schemaId) {
    var msg = new request.Schema();
    msg.action = request.Schema.Action.GET_SCHEMA_DATA;
    msg.schemaId = schemaId;

    gateway.sendRequest(msg, "SchemaServlet", this.schemaDataHandler, null, schemaId);
};

Ni3Container.prototype.contains = function (arr, obj) {
    for (var i = 0; i < arr.length; i++) {
        if (arr[i].id === obj.id) {
            return true;
        }
    }
    return false;
};

Ni3Container.prototype.loadChart = function (chartIdToLoad) {
    showWaitCursor();
    var msg = new request.Charts();
    msg.action = request.Charts.Action.GET_CHART_WITH_PARAMETERS;
    msg.chartId = chartIdToLoad;
    gateway.sendRequest(msg, "ChartsServlet", this.loadChartHandler, null, chartIdToLoad);
};

Ni3Container.prototype.loadChartHandler = function (payload, params) {
    loadedChart = new response.Chart();
    loadedChart.ParseFromStream(payload);
    loadedChart.chartId = params;
    app.ni3Container.processLoadedChart();
    showDefaultCursor();
};

Ni3Container.prototype.processLoadedChart = function () {
    app.ni3Model.setChartMode(CHARTS_PIE);
    app.ni3Container.graphController.makeVisibleNodesMapping();
    app.ni3Container.graphController.clearNodeMetaphors();
    app.ni3Container.graphController.updateGraph();
    pinChartsButton(true);
};

Ni3Container.prototype.loadSettings = function () {
    var msg = new request.Settings();
    msg.action = request.Settings.Action.GET_ALL_SETTINGS;
    gateway.sendRequest(msg, "SettingsServlet", this.loadSettingsHandler, null);
};

Ni3Container.prototype.loadSettingsHandler = function (payload) {
    var resp = new response.Settings();
    resp.ParseFromStream(payload);
    var rSettings = resp.settings;
    var settings = [];
    for (var i = 0; i < rSettings.length; i++) {
        var rs = rSettings[i];
        settings[rs.property] = rs.value;
    }

    app.ni3Model.setSettings(settings);

    app.ni3Container.initPanels();

    var schemaId = app.ni3Model.getSettingValue("Scheme");
    app.ni3Container.getSchemaData(schemaId);
};

Ni3Container.prototype.loadAllSchemas = function () {
    var msg = new request.Schema();
    msg.action = request.Schema.Action.GET_SCHEMAS;
    gateway.sendRequest(msg, "SchemaServlet", this.loadSchemasHandler, null);
};

Ni3Container.prototype.loadSchemasHandler = function (payload) {
    var resp = new response.Schemas();
    resp.ParseFromStream(payload);
    var schemas = resp.schemas;

    app.ni3Model.setAllSchemas(schemas);
};

Ni3Container.prototype.changeSchema = function (schemaId) {
    if (schemaId != app.ni3Model.getSchemaId()) {
        this.advancedSearchDialog.removeAllComponents();
        this.graphController.clearGraph();
        this.mapPanel.clearMap();
        app.ni3Model.clearModel();
        this.getSchemaData(schemaId);
    }
};


Ni3Container.prototype.loadFavorite = function(favorite){
    showWaitCursor();

    pinChartsButton(false);
    app.ni3Model.setChartMode(CHARTS_NONE);
    this.graphController.clearGraph();
    app.ni3Model.clearDataObjects();
    this.mapPanel.clearMap();
    this.filterPanel.resetFilter();

    var self = this;
    var req = new request.FavoriteManagement();
    req.action = request.FavoriteManagement.Action.GET_FAVORITE_DATA;
    req.id = favorite.id;
    gateway.sendRequest(req, "FavoritesManagementServlet", function(payload){
        var res = new response.Favorite();
        res.ParseFromStream(payload);
        favorite.data = res.data;
        favorite.layout = res.layout;
        self.restoreFavorite(favorite);
    }, null);
};

Ni3Container.prototype.restoreFavorite = function(favorite){
    var favoriteLoader = new FavoriteLoader(favorite);
    if(!favoriteLoader.isOk()){
        showDefaultCursor();
        alert("Error loading favorite document");
        return;
    }
    switch(favoriteLoader.getMode()){
        case 1:
            this.restoreSimpleFavorite(favoriteLoader);
            break;
        case 2:
            this.restoreQueryFavorite(favoriteLoader);
            break;
        case 3:
            this.restoreTopicFavorite(favoriteLoader);
            break;
    }
    showDefaultCursor();
};

Ni3Container.prototype.restoreTopicFavorite = function (favoriteLoader) {
};

Ni3Container.prototype.restoreQueryFavorite = function (favoriteLoader) {
    showWaitCursor();
    var searchRequest = new request.Search();
    searchRequest.action = request.Search.Action.PERFORM_ADVANCED_SEARCH;
    searchRequest.schemaId = app.ni3Model.getSchemaId();
    searchRequest.queryType = favoriteLoader.ni3.query["Type"];

    for (var i = 0; i < favoriteLoader.ni3.query.sections.length; i++) {
        var searchSection = new request.SearchSection();
        var querySection = favoriteLoader.ni3.query.sections[i];
        var conditions = querySection.conditions;
        searchSection.entity = querySection.entityId;
        conditions.forEach(function (condition) {
            var searchCondition = new request.SearchCondition();
            searchCondition.attributeId = condition.attributeId;
            searchCondition.operation = condition.operation;
            searchCondition.term = condition.value;
            searchSection.condition.push(searchCondition);
        });
        searchRequest.section.push(searchSection);
    }
    searchRequest.limit = favoriteLoader.ni3.query["MaxResults"]
    var countPutToGraph = +favoriteLoader.ni3.query["CopyNToGraph"];
    gateway.sendRequest(searchRequest, "SearchServlet", app.ni3Container.advancedSearchHandler, null,
        [+favoriteLoader.ni3.query["Type"],
            null,
            countPutToGraph ? countPutToGraph : "None"]);
};

Ni3Container.prototype.restoreSimpleFavorite = function (favoriteLoader) {
    showWaitCursor();

    var msg = new request.Graph();
    msg.action = request.Graph.Action.GET_NODES_WITH_EDGES;
    msg.schemaId = app.ni3Model.getSchemaId();
    msg.dataFilter = app.ni3Model.getDataFilter().getAsMessage();
    msg.objectIds = favoriteLoader.getNodeIdList();

    var self = this;
    gateway.sendRequest(msg, "GraphServlet", function(payload){
        var graph = new response.Graph();
        graph.ParseFromStream(payload);
        var rootNodes = Utility.filterById(graph.nodes, favoriteLoader.getRootIdList());
        self.graphController.showNodes(rootNodes);
//        var manuallyExpandedNodes = Utility.filterById(rootNodes, favoriteLoader.getManuallyExpandedIdList());
        var edgeList = Utility.filterById(graph.edges, favoriteLoader.getEdgeIdList());
        self.graphController.expandNodes(rootNodes, edgeList);
        var manuallyExpandedNodes = Utility.filterById(graph.nodes, favoriteLoader.getManuallyExpandedIdList());
        self.graphController.expandNodes(manuallyExpandedNodes, edgeList);
        var map = app.ni3Container.graphController.getMissingMap();
        app.ni3Container.getDbObjects(map, false);
        showWaitCursor();
        if (+favoriteLoader.ni3["ChartID"] > 0)
            setTimeout(function () {
                self.loadChart(+favoriteLoader.ni3["ChartID"]);
            }, 2000);
    }, null);
};

Ni3Container.prototype.saveDocument = function(){
    var name = prompt("Enter new favorite name");
    if(!name)
        return;
    var req = new request.FavoriteManagement();
    req.action = request.FavoriteManagement.Action.GET_ALL_FOR_SCHEMA;
    req.schemaId = app.ni3Model.getSchemaId();
    var self = this;
    gateway.sendRequest(req, "FavoritesManagementServlet", function(payload){
        var favorites = new response.Favorites();
        favorites.ParseFromStream(payload);
        favorites = favorites.favorites;
        for(var i = 0; i < favorites.length; i++){
            if(favorites[i].name.toUpperCase() == name.toUpperCase()){
                var existingId = favorites[i].id;
                break;
            }
        }
        if(existingId){
            var rewrite = confirm("Rewrite existing favorite with name: " + name);
            if(!rewrite)
                return;
        }
        self.generateAndSaveFavorite(existingId);
    }, null);
};

Ni3Container.prototype.generateAndSaveFavorite = function (existingId) {
    var ni3 = {};
    ni3["version"] = '3.00';
    ni3["SchemaID"] = app.ni3Model.getSchemaId();
    ni3["ChartID"] = app.ni3Model.getChartMode() == CHARTS_NONE ? 0 : loadedChart.chartId;
    ni3["MapID"] = 2;
    ni3["ThematicDataSetID"] = 0;
    ni3["Mode"] = 1;
    ni3["MetaphorSet"] = 'Default';
    ni3["NumericMetaphors"] = false;
    ni3.Graph = this.graphController.saveGraph();
    ni3.GraphPanelSettings = {};
    ni3.MapSettings = {};
    ni3.Filter = {};
    ni3.Prefilter = {};
    ni3.CommandPanel = {};
    var serializer = new FavoriteSerializer(ni3);
    var xml = serializer.toXML();
    if(existingId)
    //TODO rewrite favorite with id
        ;else
    //TODO save new favorite
        ;

};
