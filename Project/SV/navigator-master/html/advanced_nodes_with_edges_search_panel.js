var NODES_WITH_EDGES_SEARCH_QUERY_TYPE = 3;

function AdvancedNodesWithEdgesSearchPanel() {
    this.nodeEntity = null;
    this.edgeEntity = null;
    this.entitiesSelectionDialog = new EntitiesSelectionDialog(this.entitySelectorOkHandler, this.entitySelectorCancelHandler, false);
}

AdvancedNodesWithEdgesSearchPanel.prototype = new AdvancedSearchPanel("nodeWithEdgeSearch");

AdvancedNodesWithEdgesSearchPanel.prototype.__supper = AdvancedSearchPanel.prototype;

AdvancedNodesWithEdgesSearchPanel.prototype.constructor = AdvancedNodesWithEdgesSearchPanel;

AdvancedNodesWithEdgesSearchPanel.prototype.init = function () {
    var optionPanel = this.createOptionPanel("#searchTypeTab_node_with_edges");
    optionPanel.append("a")
        .attr("class", "searchChangeTypeA")
        .attr("href", "#")
        .on("click", this.onSelectObjects)
        .text("Change object types");
};

AdvancedNodesWithEdgesSearchPanel.prototype.onSelectObjects = function () {
    app.ni3Container.advancedSearchDialog.advancedNodesWithEdgesSearchPanel.showEntitiesSelectionDialog();
};

AdvancedNodesWithEdgesSearchPanel.prototype.beforeShow = function () {
    if (this.nodeEntity == null)
        this.showEntitiesSelectionDialog();
};

AdvancedNodesWithEdgesSearchPanel.prototype.showEntitiesSelectionDialog = function () {
    this.entitiesSelectionDialog.show();
};

AdvancedNodesWithEdgesSearchPanel.prototype.entitySelectorOkHandler = function (leftNodeEntity, edgeEntity) {
    var self = app.ni3Container.advancedSearchDialog.advancedNodesWithEdgesSearchPanel;
    self.nodeEntity = leftNodeEntity;
    self.edgeEntity = edgeEntity;
    self.regenerateTabs();
};

AdvancedNodesWithEdgesSearchPanel.prototype.entitySelectorCancelHandler = function () {
    var self = app.ni3Container.advancedSearchDialog.advancedNodesWithEdgesSearchPanel;
    self.nodeEntity = null;
    self.edgeEntity = null;
    self.clearTabs();
};

AdvancedNodesWithEdgesSearchPanel.prototype.regenerateTabs = function () {
    this.clearTabs();
    var entities = [this.nodeEntity, this.edgeEntity];
    this.generateEntityTabs(entities, "#searchTypeTab_node_with_edges");
};

AdvancedNodesWithEdgesSearchPanel.prototype.clearTabs = function () {
    d3.select("#searchTypeTab_node_with_edges").select("#nodeWithEdgeSearchTabs").remove();
    this.searchAttributes = [];
};

AdvancedNodesWithEdgesSearchPanel.prototype.validateAccept = function () {
    this.entityAttributeMapping = this.collectData();
    this.entityAttributeMapping.type = NODES_WITH_EDGES_SEARCH_QUERY_TYPE;
    return this.validate();
};

AdvancedNodesWithEdgesSearchPanel.prototype.accept = function (geoSearchCriteria) {
    var entities = [this.nodeEntity, this.edgeEntity];
    this.__supper.entityAttributeMapping = this.entityAttributeMapping;
    var valueMapping = this.__supper.entityAttributeMapping;
    var newEntityIds = [];
    for (var i = 0; i < 2; i++) {
        var key = "" + entities[i].id + i;
        var index = valueMapping.entityIds.indexOf(key);
        if (index == -1) {
            newEntityIds.push(key);
            valueMapping[key] = {};
            valueMapping[key].entity = entities[i];
            valueMapping[key].conditions = [];
        } else
            newEntityIds.push(this.__supper.entityAttributeMapping.entityIds[index]);
    }
    valueMapping.entityIds = newEntityIds;
    this.__supper.searchLimit = this.searchLimit;
    this.__supper.accept(geoSearchCriteria);
};

AdvancedNodesWithEdgesSearchPanel.prototype.clear = function () {
    this.clearSearchCriteria();
};