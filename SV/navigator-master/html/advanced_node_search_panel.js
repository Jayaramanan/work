var TEXT = 1;
var INT = 2;
var BOOL = 3;
var DECIMAL = 4;
var URL = 5;
var DATE = 6;
var NODE_SEARCH_QUERY_TYPE = 1;

function AdvancedNodeSearchPanel() {
}

AdvancedNodeSearchPanel.prototype = new AdvancedSearchPanel("nodeSearch");

AdvancedNodeSearchPanel.prototype.constructor = AdvancedNodeSearchPanel;

AdvancedNodeSearchPanel.prototype.init = function () {
    var entities = app.ni3Model.getNodeEntities();
    var optionPanel = this.createOptionPanel("#searchTypeTab_node");
    var copyFirstOptions = ["None", "All", 10, 20, 50, 100];
    optionPanel.append("label")
        .text("Copy first");
    var select = optionPanel.append("select")
        .attr("id", "nodeSearch_putOnGraphSelect")
        .attr("class", "searchPutOnGraphSelect");
    select.selectAll("option")
        .data(copyFirstOptions)
        .enter()
        .append("option")
        .attr("value", function (d) {
            return d;
        })
        .text(function (d) {
            return d;
        });
    select.property("value", this.putOnGraphCount)
        .attr("onchange", "AdvancedNodeSearchPanel.onPutOnGraphChanged(this)");

    optionPanel.append("label")
        .text("results to graph");
    this.generateEntityTabs(entities, "#searchTypeTab_node");
};

AdvancedNodeSearchPanel.prototype.beforeShow = function () {
    //this panel do not require any preShow actions
};

AdvancedNodeSearchPanel.prototype.validateAccept = function () {
    this.entityAttributeMapping = this.collectData();
    this.entityAttributeMapping.type = NODE_SEARCH_QUERY_TYPE;
    return this.validate();
};

AdvancedNodeSearchPanel.prototype.clear = function () {
    this.clearSearchCriteria();
};

AdvancedNodeSearchPanel.onPutOnGraphChanged = function (select) {
    app.ni3Container.advancedSearchDialog.advancedNodeSearchPanel.putOnGraphCount = select.value;
};
