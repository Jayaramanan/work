var toolbarRow;
var chartActive = false;

function initToolbar() {
    var toolbar = d3.select("#toolbar")
        .append("table")
        .attr("id", "toolbarTable");
    toolbarRow = toolbar.append("tr");

    toolbar.style("display", "none");

    addButton("changeSchemaButton", "Schema.png", doShowChangeSchemaDialog);

    addSeparator();
    addButton("backButton", "left.png", doBack);
    addButton("forwardButton", "right.png", doForward);

    addSeparator();
    addButton("saveButton", "save24.png", doSave);
    addButton("favoritesButton", "favorites.png", doFavorites);

    addSeparator();
    addButton("expandAll", "Expand.png", doExpandAll);
    addButton("contractAll", "Contract.png", doContractAll);

    addSeparator();
    addButton("isolateButton", "Isolate24a.png", doIsolate);
    addButton("removeButton", "remove.png", doRemove);
    addButton("findPathButton", "FindPath24.png", doFindPath);
    addButton("clearHighlightsButton", "ClearHighlights24.png", doClearHighlights);

    addSeparator();
    addButton("reloadButton", "ReloadGraph24.png", doReload);
    addButton("clearButton", "ClearGraph24.png", doClear);

    addSeparator();
    addButton("dynamicAttributeButton", "Sum24.png", doAddDynamicAttribute);

    addChartsToggleButton();

    addSeparator();
    addCreateToggleButton();

    addSeparator();
    addSearchTextField();
    addButton("searchButton", "Search24.png", doSimpleSearch);
    addButton("combineSearchButton", "ManAtWork.png", doCombineSearch);

    addSearchTypeRadioButtons();

    addSeparator();
    addButton("geoAnalyticsButton", "GeoAnalytics.png", doShowGeoAnalyticsDialog);

    addSeparator();
    addButton("activityStreamButton", "ActivityStream.png", doShowActivityStream);

    addSeparator();
    addButton("aboutButton", "Info.png", doShowAboutDialog);

    toolbar.style("display", null);
}

function addChartsToggleButton() {
    toolbarRow.append("td")
        .append("input")
        .attr("type", "image")
        .attr("id", "chartsButton")
        .attr("src", imageUrl + "/" + "charts.png")
        .on("click", doCharts)
        .on("mouseover", function () {
            this.style.background = "lightgray";
        })
        .on("mouseout", function () {
            if (!chartActive) {
                this.style.background = "";
            }
        });
}

function pinChartsButton(flag) {
    chartActive = flag;
    if (chartActive)
        $("#chartsButton").attr("style", "background: lightgray");
    else
        $("#chartsButton").attr("style", "");
}


function addButton(id, iconName, action) {
    toolbarRow.append("td")
        .append("input")
        .attr("type", "image")
        .attr("id", id)
        .attr("class", "toolbarButton")
        .attr("src", imageUrl + "/" + iconName)
        .on("click", action)
        .on("mouseover", function () {
            this.style.background = "lightgray";
        })
        .on("mouseout", function () {
            this.style.background = "";
        });
}

function addCreateToggleButton() {
    toolbarRow.append("td")
        .append("input")
        .attr("type", "image")
        .attr("id", "createButton")
        .attr("src", imageUrl + "/" + "CreateNode.png")
        .on("click", toggleNodeCreateMode)
        .on("mouseover", function () {
            this.style.background = "lightgray";
        })
        .on("mouseout", function () {
            if (!app.ni3Model.isNodeCreateMode()) {
                this.style.background = "";
            }
        });
}

function addSearchTextField() {
    toolbarRow.append("td")
        .append("form") // prevent default action for IE
        .attr("action", "javascript:void(null);")
        .attr("method", "post")
        .append("input")
        .attr("type", "text")
        .attr("id", "searchField")
        .on("keyup", function () {
            if (d3.event.keyCode == 13) {
                doSimpleSearch();
            }
        });
}

function addSearchTypeRadioButtons() {
    var cell = toolbarRow.append("td");
    cell.append("input")
        .attr("type", "radio")
        .attr("name", "rbSearch")
        .attr("id", "newSearch")
        .attr("value", "newSearch")
        .property("checked", true)
        .on("click", function (d) {
            app.ni3Model.setNewSearchMode(true);
        });
    cell.append("label")
        .attr("class", "toolbarLabel")
        .attr("for", "newSearch")
        .text("New search");
    cell.append("br");
    cell.append("input")
        .attr("type", "radio")
        .attr("name", "rbSearch")
        .attr("id", "addToSearch")
        .attr("value", "addToSearch")
        .on("click", function (d) {
            app.ni3Model.setNewSearchMode(false);
        });
    cell.append("label")
        .attr("class", "toolbarLabel")
        .attr("for", "addToSearch")
        .text("Add to search");
}

function addSeparator() {
    toolbarRow.append("td")
        .attr("class", "separator");
}

function doBack() {
//    alert("back");
}

function doForward() {
    alert("forward");
}

function doSave() {
    app.ni3Container.saveDocument();
}

function doFavorites(){
    app.ni3Container.favoritesDialog.show();
}

function doIsolate() {

}

function doRemove() {

}

function doFindPath() {

}

function doClearHighlights() {

}

function doReload() {

}

function doClear() {

}

function doAddDynamicAttribute() {

}

function doSimpleSearch() {
    var searchField = toolbarRow.select("#searchField");
    app.ni3Container.searchAndShow(searchField[0][0].value);
}

function doCombineSearch() {
    app.ni3Container.advancedSearchDialog.show();
}

function doShowGeoAnalyticsDialog() {

}

function doShowActivityStream() {

}

function toggleNodeCreateMode() {
    app.ni3Model.setNodeCreateMode(!app.ni3Model.isNodeCreateMode());
    var graph = app.ni3Container.graphController;
    graph.freezeNodes(app.ni3Model.isNodeCreateMode());
    graph.enableDrag(!app.ni3Model.isNodeCreateMode());
}

function doCharts() {
    if (app.ni3Model.getChartMode() == CHARTS_NONE) {
        app.ni3Container.chartSelectDialog.show();
    } else {
        pinChartsButton(false);
        app.ni3Model.setChartMode(CHARTS_NONE);
        app.ni3Container.graphController.clearNodeMetaphors();
        app.ni3Container.graphController.updateGraph();
    }
}

function doShowAboutDialog() {
    app.ni3Container.aboutDialog.show();
}

function doShowChangeSchemaDialog() {
    app.ni3Container.changeSchemaDialog.show();
}

function doExpandAll() {
    app.ni3Container.graphController.expandAll();
}

function doContractAll() {
    app.ni3Container.graphController.contractAll();
}