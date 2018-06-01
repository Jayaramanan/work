function AdvancedSearchDialog() {
    this.advancedNodeSearchPanel = new AdvancedNodeSearchPanel();
    this.advancedConnectedNodeSearchPanel = new AdvancedConnectedNodeSearchPanel();
    this.advancedNodesWithEdgesSearchPanel = new AdvancedNodesWithEdgesSearchPanel();

    this.searchTypes = [
        {id:"node", name:"Node search", handler:this.advancedNodeSearchPanel},
        {id:"connected_node", name:"Connected node search", handler:this.advancedConnectedNodeSearchPanel},
        {id:"node_with_edges", name:"Node with edges search", handler:this.advancedNodesWithEdgesSearchPanel}
    ];
    this.currentSearchType = this.searchTypes[0];
}

AdvancedSearchDialog.searchTableHeader = ["Attribute", "Operation", "Values", "Sort", "A/D"];

AdvancedSearchDialog.prototype.constructor = AdvancedSearchDialog;

AdvancedSearchDialog.prototype.createAdvancedSearchDialog = function () {
    this.advancedSearchDiv = d3.select("body")
        .append("div");
    this.advancedSearchDiv.attr("id", "advancedSearchDialog")
        .attr("title", "Advanced search");
    this.createContents();
};

AdvancedSearchDialog.prototype.createContents = function () {
    this.advancedSearchDiv
        .append("div")
        .attr("id", "advancedSearchTypeTabs")
        .style("font-size", "80%")
        .append("ul")
        .attr("id", "advancedSearchTypeTabsHeader");

    d3.select("#advancedSearchTypeTabsHeader")
        .selectAll("li")
        .data(this.searchTypes).enter()
        .append("li")
        .append("a")
        .attr("href", function (item) {
            return "#searchTypeTab_" + item.id;
        })
        .append("span")
        .text(function (item) {
            return item.name;
        });

    d3.select("#advancedSearchTypeTabs")
        .selectAll("div")
        .data(this.searchTypes).enter()
        .append("div")
        .attr("id", function (item) {
            return "searchTypeTab_" + item.id;
        });

    $("#advancedSearchTypeTabs").tabs({
        select:this.onActiveTabChanged
    });
    this.searchTypes.forEach(
        function(searchType){
            searchType.handler.init();
        });
};

AdvancedSearchDialog.prototype.onActiveTabChanged = function (event, ui) {
    var panel = app.ni3Container.advancedSearchDialog;
    panel.currentSearchType = ui.tab.__data__;
    panel.currentSearchType.handler.beforeShow();
};

AdvancedSearchDialog.prototype.removeAllComponents = function () {
    if (this.advancedSearchDiv != undefined) {
        this.advancedSearchDiv.remove();
        this.advancedSearchDiv = undefined;
        this.advancedSearchDialog = undefined;
        this.advancedNodeSearchPanel.clear();
        d3.select("#entitiesSelectionDialog").remove();
    }
};

AdvancedSearchDialog.prototype.clearSearchCriteria = function () {
    this.currentSearchType.handler.clear();
};

AdvancedSearchDialog.prototype.show = function (geoSearchCriteria) {
    if (this.advancedSearchDialog == undefined)
        this.createAdvancedSearchDialog();
    this.advancedSearchDialog = $("#advancedSearchDialog");
    this.advancedSearchDialog.dialog({
        autoOpen:false,
        height:500,
        width:600,
        modal:true,
        buttons:{
            Ok:function () {
                if (!app.ni3Container.advancedSearchDialog.currentSearchType.handler.validateAccept())
                    return;
                $(this).dialog("close");
                app.ni3Container.advancedSearchDialog.currentSearchType.handler.accept(geoSearchCriteria);
            },
            Cancel:function () {
                $(this).dialog("close");
            },
            Clear:function(){
                app.ni3Container.advancedSearchDialog.clearSearchCriteria();
            }
        }
    });
    this.currentSearchType.handler.beforeShow();
    this.advancedSearchDialog.dialog("open");
};