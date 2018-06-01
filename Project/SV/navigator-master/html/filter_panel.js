function FilterPanel() {
    this.filterTree = undefined;
    this.dataFilterTree = undefined;

    this.initPanels = function () {
        var tabs = ["DisplayFilter", "DataFilter"];
        var tabNames = ["Display filter", "Data filter"];
        this.tree = d3.select("#filterPanel")
            .append("div")
            .attr("id", "filterTreePanel")
            .append("ul")
            .attr("id", "filterTabsHeader")
            .selectAll("li")
            .data(tabs)
            .enter()
            .append("li")
            .append("a")
            .attr("href", function (tab) {
                return "#ftab_" + tab;
            })
            .append("span")
            .text(function (d, i) {
                return tabNames[i];
            });

        var filterTabs = d3.select("#filterTreePanel")
            .selectAll("div")
            .data(tabs)
            .enter()
            .append("div")
            .attr("id", function (tab) {
                return "ftab_" + tab;
            });

        $("#filterTreePanel").tabs();

        filterTabs.append("div")
            .attr("id", function (tab) {
                return "fTreeHeader_" + tab;
            });
        filterTabs.append("div")
            .attr("id", function (tab) {
                return "fTree_" + tab;
            });

        var refThis = this;

        var filterTreeHeader = d3.select("#fTreeHeader_DisplayFilter");
        var dataFilterTreeHeader = d3.select("#fTreeHeader_DataFilter");

        filterTreeHeader.append("input")
            .attr("type", "button")
            .attr("value", "Reset")
            .on("click", refThis.resetFilter);

        dataFilterTreeHeader.append("input")
            .attr("type", "button")
            .attr("value", "Reset")
            .on("click", refThis.resetDataFilter);

        dataFilterTreeHeader.append("input")
            .attr("type", "button")
            .attr("value", "Apply")
            .on("click", refThis.applyDataFilter);

        var filterTab = d3.select("#fTree_DisplayFilter");
        var dataFilterTab = d3.select("#fTree_DataFilter");

        this.filterTree = new FilterTree(filterTab, false);
        this.dataFilterTree = new FilterTree(dataFilterTab, true);
    };

    this.updateFilterTreeData = function () {
        this.filterTree.updateTreeData();
        this.dataFilterTree.updateTreeData();
    };

    this.updateCounts = function () {
        this.filterTree.updateCounts();
    };

    this.resetFilter = function () {
        app.ni3Container.filterPanel.filterTree.resetFilter();
        var filter = app.ni3Model.getDisplayFilter();
        filter.clear();
        app.ni3Container.graphController.filterVisibleObjects(filter);
    };

    this.resetDataFilter = function () {
        app.ni3Container.filterPanel.dataFilterTree.resetFilter();
        app.ni3Model.getDataFilter().clear();
        app.ni3Container.reloadNodes();
    };

    this.applyDataFilter = function () {
        var values = app.ni3Container.filterPanel.dataFilterTree.getFilteredOutValues();
        var dataFilter = app.ni3Model.getDataFilter();
        dataFilter.clear();
        dataFilter.addFilteredOutValues(values);
        app.ni3Container.graphController.applyDataFilterToGraph(dataFilter);
        app.ni3Container.matrix.applyDataFilterToMatrix(dataFilter);
    };
}