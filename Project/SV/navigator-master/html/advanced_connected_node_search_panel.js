var CONNECTED_NODE_SEARCH_QUERY_TYPE = 2;

function AdvancedConnectedNodeSearchPanel() {
    this.rightEntity = null;
    this.leftEntity = null;
    this.edgeEntity = null;
    this.entitiesSelectionDialog = new EntitiesSelectionDialog(this.entitySelectorOkHandler, this.entitySelectorCancelHandler, true);
}

AdvancedConnectedNodeSearchPanel.prototype = new AdvancedSearchPanel("connectedNodeSearch");

AdvancedConnectedNodeSearchPanel.prototype.__supper = AdvancedSearchPanel.prototype;

AdvancedConnectedNodeSearchPanel.prototype.constructor = AdvancedConnectedNodeSearchPanel;

AdvancedConnectedNodeSearchPanel.prototype.init = function () {
    var optionPanel = this.createOptionPanel("#searchTypeTab_connected_node");
    optionPanel.append("a")
        .attr("class", "searchChangeTypeA")
        .attr("href", "#")
        .on("click", this.onSelectObjects)
        .text("Change object types");
};

AdvancedConnectedNodeSearchPanel.prototype.onSelectObjects = function () {
    app.ni3Container.advancedSearchDialog.advancedConnectedNodeSearchPanel.showEntitiesSelectionDialog();
};

AdvancedConnectedNodeSearchPanel.prototype.beforeShow = function () {
    if (this.rightEntity == null)
        this.showEntitiesSelectionDialog();
};

AdvancedConnectedNodeSearchPanel.prototype.showEntitiesSelectionDialog = function () {
    this.entitiesSelectionDialog.show();
};

AdvancedConnectedNodeSearchPanel.prototype.entitySelectorOkHandler = function (leftNodeEntity, edgeEntity, rightNodeEntity) {
    var self = app.ni3Container.advancedSearchDialog.advancedConnectedNodeSearchPanel;
    self.rightEntity = rightNodeEntity;
    self.leftEntity = leftNodeEntity;
    self.edgeEntity = edgeEntity;
    self.regenerateTabs();
};

AdvancedConnectedNodeSearchPanel.prototype.entitySelectorCancelHandler = function () {
    var self = app.ni3Container.advancedSearchDialog.advancedConnectedNodeSearchPanel;
    self.rightEntity = null;
    self.leftEntity = null;
    self.edgeEntity = null;
    self.clearTabs();
};

AdvancedConnectedNodeSearchPanel.prototype.regenerateTabs = function () {
    this.clearTabs();
    var entities = [this.leftEntity, this.edgeEntity, this.rightEntity];
    this.generateEntityTabs(entities, "#searchTypeTab_connected_node");
};

AdvancedConnectedNodeSearchPanel.prototype.clearTabs = function () {
    d3.select("#searchTypeTab_connected_node").select("#connectedNodeSearchTabs").remove();
    this.searchAttributes = [];
};

AdvancedConnectedNodeSearchPanel.prototype.validateAccept = function () {
    this.entityAttributeMapping = this.collectData("connectedNodeSearch");
    this.entityAttributeMapping.type = CONNECTED_NODE_SEARCH_QUERY_TYPE;
    return this.validate();
};

AdvancedConnectedNodeSearchPanel.prototype.accept = function (geoSearchCriteria) {
    var entities = [this.leftEntity, this.edgeEntity, this.rightEntity];
    this.__supper.entityAttributeMapping = this.entityAttributeMapping;
    var valueMapping = this.__supper.entityAttributeMapping;
    var newEntityIds = [];
    for (var i = 0; i < 3; i++) {
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

AdvancedConnectedNodeSearchPanel.prototype.clear = function () {
    this.clearSearchCriteria();
};