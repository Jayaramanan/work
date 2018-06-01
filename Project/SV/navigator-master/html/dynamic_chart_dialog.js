function DynamicChartDialog() {
}

DynamicChartDialog.prototype.constructor = DynamicChartDialog;

DynamicChartDialog.prototype.show = function () {
    if(this.dynamicChartDialog == undefined)
        this.createDialog();
    else
        this.clearSelection();
    this.dynamicChartDialog = $("#dynamicChartDialog");
    this.dynamicChartDialog.dialog({
        autoOpen:false,
        height:300,
        width:650,
        modal:true,
        buttons:{
            Ok: function(){
                $(this).dialog("close");
                app.ni3Container.dynamicChartDialog.doCharts();
            },
            Cancel:function () {
                $(this).dialog("close");
            }
        }
    });
    this.dynamicChartDialog.dialog("open");
};

DynamicChartDialog.prototype.clearSelection = function(){
    this.selectedAttributes = [];
    d3.select("#dynamicChartDialog").selectAll("input.dynamicChartAttributeCheckBox").property("checked", false);
    d3.selectAll("td.dynamicSliceColor").remove();
};

DynamicChartDialog.prototype.createDialog = function(){
    this.dynamicChartDialog = d3.select("body")
        .append("div");
    this.dynamicChartDialog.attr("id", "dynamicChartDialog")
        .attr("title", "Select attributes for chart");
    var nodeEntities = this.createEntityTabs();
    this.fillTabsWithAttributes(nodeEntities);
    this.selectedAttributes = [];
};

DynamicChartDialog.prototype.fillTabsWithAttributes = function (nodeEntities) {
    var dynamicNodeTables = d3.select("#dynamicTabsPanel").selectAll("table.dynamicTable");
    var dynamicNodeTablesHeader = dynamicNodeTables.append("thead");
    var headerRow = dynamicNodeTablesHeader.append("tr");
    headerRow.append("th");
    headerRow.append("th").text("Attribute");
    dynamicNodeTables.append("tbody");
    nodeEntities.forEach(function(entity){
        var attributes = d3.select("#dynamicTable_" + entity.name)
            .select("tbody")
            .selectAll("tr").data(app.ni3Container.dynamicChartDialog.getAttributesForDynamicCharts(entity))
            .enter().append("tr");

        attributes.append("td").attr("class", "dynamicChartAttribute")
            .append("input")
            .attr("class", "dynamicChartAttributeCheckBox")
            .attr("type", "checkbox")
            .attr("onclick", "app.ni3Container.dynamicChartDialog.checkboxStateChanged(this);");
        attributes.append("td").text(function(attribute){
            return attribute.label;
        });
    });
};

DynamicChartDialog.prototype.createEntityTabs = function () {
    var nodeEntities = app.ni3Model.getNodeEntities();


    d3.select("#dynamicChartDialog")
        .append("div")
        .attr("id", "dynamicTabsPanel")
        .style("font-size", "80%")
        .append("ul")
        .attr("id", "dynamicTabsHeader");

    var tabsHeader = d3.select("#dynamicTabsHeader")
        .selectAll("li")
        .data(nodeEntities);

    tabsHeader.enter()
        .append("li")
        .append("a")
        .attr("href", function (ent) {
            return "#dynamicTab_" + Utility.stringAsIdentifier(ent.name);
        })
        .append("span")
        .text(function (ent) {
            return ent.name;
        });

    var tabs = d3.select("#dynamicTabsPanel")
        .selectAll("div")
        .data(nodeEntities);
    tabs.enter()
        .append("div")
        .attr("id", function (ent) {
            return "dynamicTab_" + Utility.stringAsIdentifier(ent.name);
        })
        .attr("class", "dynamicTableDiv")
        .append("table")
        .attr("id", function (ent) {
            return "dynamicTable_" + Utility.stringAsIdentifier(ent.name);
        })
        .attr("class", "dynamicTable");

    $("#dynamicTabsPanel").tabs();
    return nodeEntities;
};

//noinspection JSUnusedGlobalSymbols
DynamicChartDialog.prototype.checkboxStateChanged = function(attributeCheckbox){
    //this.selectedAttributes contains tupples with data about selected attributes <entity, attribute, generated_random_color>
    var entity = attributeCheckbox.parentNode.parentNode.parentNode.parentNode.parentNode.__data__;
    var attribute = attributeCheckbox.__data__;
    if(attributeCheckbox.checked){
        var randColor = '#'+Math.floor(Math.random()*16777215).toString(16);
        app.ni3Container.dynamicChartDialog.selectedAttributes.push([entity, attribute, randColor]);
        d3.select(attributeCheckbox.parentNode.parentNode)
            .append("td")
            .attr("class", "dynamicSliceColor").attr("bgcolor", randColor).attr("width", "20");
    } else{
        var index = -1;
        app.ni3Container.dynamicChartDialog.selectedAttributes.forEach(function(tupple, i){
            if(tupple[0] == entity && tupple[1] == attribute)
                index = i;
        });
        app.ni3Container.dynamicChartDialog.selectedAttributes.splice(index, 1);
        d3.select(attributeCheckbox.parentNode.parentNode).select("td.dynamicSliceColor").remove();
    }
};

DynamicChartDialog.prototype.getAttributesForDynamicCharts = function(entity){
    var validAttributes = [];
    var systemNames = ["lon", "lat", "id", "iconname"];
    for(var i = 0; i < entity.attributes.length; i++){
        var attr = entity.attributes[i];
        if(!attr.predefined &&
                (attr.dataTypeId == 2 || attr.dataTypeId == 4) && //numeric??
                systemNames.indexOf(attr.name) == -1 &&  //not system??
                attr.aggregable && !attr.inContext)
            validAttributes.push(attr);
    }
    return validAttributes;
};

DynamicChartDialog.prototype.doCharts = function () {
    if(this.selectedAttributes.length == 0)
        return;
    loadedChart = new response.Chart();
    loadedChart.chartId = -1;
    for(var i = 0; i < this.selectedAttributes.length; i++){
        var selectedTupple = this.selectedAttributes[i];
        var entity = selectedTupple[0];
        var attribute = selectedTupple[1];
        var color = selectedTupple[2];

        var index = -1;
        for(var j = 0; j < loadedChart.objectCharts.length; j++){
            if(loadedChart.objectCharts[j].objectId == entity.id){
                index = j;
                break;
            }
        }
        if(index == -1){
            index = loadedChart.objectCharts.length;
            var newObjectChart = new response.ObjectChart();
            newObjectChart.objectId = entity.id;
            loadedChart.objectCharts.push(newObjectChart);
        }

        var newChartAttribute = new response.ChartAttribute();
        newChartAttribute.attributeId = attribute.id;
        newChartAttribute.rgb = color;
        loadedChart.objectCharts[index].chartAttributes.push(newChartAttribute);
    }
    app.ni3Container.processLoadedChart();
};
