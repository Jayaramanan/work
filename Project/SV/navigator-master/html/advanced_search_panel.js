function AdvancedSearchPanel(prefixName) {
    this.searchAttributes = [];
    this.searchLimit = 5000;
    this.putOnGraphCount = "None";
    this.limitOptions = [100, 500, 1000, 5000];
    this.sortNrOptions = ["", 1];
    this.sortDirectionOptions = ["Asc", "Desc"];
    this.prefixName = prefixName;
}

AdvancedSearchPanel.prototype.constructor = AdvancedSearchPanel;

AdvancedSearchPanel.prototype.createOptionPanel = function (placeHolderPanelId) {
    var optionPanel = d3.select(placeHolderPanelId)
        .append("div")
        .attr("class", "searchOptionPanel");
    optionPanel.append("label")
        .text("Fetch first");
    var select = optionPanel.append("select")
        .attr("id", this.prefixName + "_limitSelect")
        .attr("class", "searchLimitSelect");
    select.selectAll("option")
        .data(this.limitOptions)
        .enter()
        .append("option")
        .attr("value", function (d) {
            return d;
        })
        .text(function (d) {
            return d;
        });
    select.property("value", this.searchLimit)
        .attr("onchange", "AdvancedSearchPanel.onSearchLimitChanged(this)");

    return optionPanel;
};

AdvancedSearchPanel.prototype.generateEntityTabs = function (entities, placeHolderPanelId) {
    var prefix = this.prefixName;
    var nodeSearchTabs = d3.select(placeHolderPanelId)
        .append("div")
        .attr("id", prefix + "Tabs");
    var nodeSearchTabsItems = nodeSearchTabs.append("ul").attr("id", prefix + "TabsHeader")
        .selectAll("li").data(entities);
    nodeSearchTabsItems.enter()
        .append("li")
        .append("a").attr("href", function (ent, i) {
            return "#" + prefix + "_" + ent.id + "_" + i;
        })
        .append("span").text(function (ent) {
            return ent.name;
        });
    nodeSearchTabsItems.exit().remove();
    nodeSearchTabsItems = nodeSearchTabs.selectAll("div").data(entities);
    nodeSearchTabsItems.enter()
        .append("div")
        .attr("id", function (ent, i) {
            return prefix + "_" + ent.id + "_" + i;
        });
    nodeSearchTabsItems.exit().remove();
    $(nodeSearchTabs[0]).tabs();

    for (var nodeIndex = 0; nodeIndex < entities.length; nodeIndex++) {
        var entity = entities[nodeIndex];
        var nodePanel = d3.select("#" + prefix + "Tabs").select("#" + prefix + "_" + entity.id + "_" + nodeIndex);
        var nodeTable = nodePanel.append("table").attr("class", "searchNodeTable");
        var tableHeader = nodeTable.append("thead");
        var tableBody = nodeTable.append("tbody");

        var inSearchAttributes = this.getInSearchAttributes(entity, nodeIndex);

        tableHeader
            .append("tr")
            .selectAll("th")
            .data(AdvancedSearchDialog.searchTableHeader).enter()
            .append("th").text(function (name) {
                return name;
            });

        var rows = tableBody.selectAll("tr")
            .data(inSearchAttributes)
            .enter()
            .append("tr")
            .attr("id", function (attr) {
                return prefix + "_searchTableRow_" + attr[1].id;
            });

        this.generateAttributeRows(rows);
    }
};

AdvancedSearchPanel.prototype.generateAttributeRows = function (rows) {
    var inputElements = rows.append("td");
    inputElements.append("input")
        .attr("type", "checkbox")
        .attr("class", "searchAttributeCheck")
        .attr("onclick", "onNodeSearchAttributeCheckbox(this)");
    inputElements.append("span").text(function (pair) {
        return pair[1].label;
    });
    rows.append("td")
        .attr("class", "searchOperationTableCell");
    rows.append("td")
        .attr("class", "searchInputTableCell");
    this.generateSorts(rows);
    rows.append("td")
        .attr("class", "searchSortDirectionTableCell");
};

AdvancedSearchPanel.prototype.generateSorts = function (rows) {
    rows.append("td")
        .attr("class", "searchSortNrTableCell")
        .append("select")
        .attr("class", "searchSortNrSelect")
        .attr("onchange", "AdvancedSearchPanel.onSortNrChanged(this)")
        .selectAll("option")
        .data(this.sortNrOptions)
        .enter()
        .append("option")
        .attr("value", function (d) {
            return d;
        })
        .text(function (d) {
            return d;
        });
};

AdvancedSearchPanel.prototype.generateInputForRow = function (tableRow, pair) {
    var panel = this;
    var entity = pair[0];
    var attribute = pair[1];
    panel.generateOperationSelector(tableRow, entity, attribute);
    var inputElement = tableRow.select("td.searchInputTableCell");
    if (attribute.predefined) {
        inputElement = inputElement.append("select")
            .attr("multiple", "1");
        for (var i = 0; i < attribute.values.length; i++) {
            var value = attribute.values[i];
            inputElement.append("option")
                .attr("value", "" + value.id)
                .text(value.label);
        }
    } else {
        if (attribute.multivalue) {
            inputElement = inputElement.append("textarea");
        } else {
            inputElement = inputElement.append("input");
        }
    }
    inputElement.attr("class", "searchToken");
};

AdvancedSearchPanel.prototype.generateOperationSelector = function (tableRow, entity, attribute) {
    var operationSelector = tableRow.select(".searchOperationTableCell")
        .append("select")
        .attr("class", "searchOperation");
    var operations;
    if (attribute.predefined) {
        if (attribute.multivalue) {
            operations = ["AtLeastOne", "All", "NoneOf"];
        } else {
            operations = ["=", "<>"];
        }
    } else {
        if (attribute.multivalue) {
            operations = ["=", "<>", "~"];
        } else {
            switch (attribute.dataTypeId) {
                case TEXT:
                case URL:
                    operations = ["=", "<>", "~"];
                    break;
                case INT:
                case DECIMAL:
                case DATE:
                    operations = [">=", "=", "<=", "<>"];
                    break;
                case BOOL:
                    operations = ["=", "<>"];
                    break;
            }
        }
    }
    var self = this;
    operationSelector.selectAll("option").data(operations).enter()
        .append("option")
        .attr("value", function (op) {
            return op;
        })
        .text(function (op) {
            return self.getOperationLabel(op);
        })
};

AdvancedSearchPanel.prototype.getOperationLabel = function (key) {
    var value = key;
    if (key == "AtLeastOne") {
        value = "At least one";
    } else if (key == "NoneOf") {
        value = "None of";
    }
    return value;
};

function isNumeric(input) {
    return (input - 0) == input && input.length > 0;
}

function isInt(value) {
    return isNumeric(value) && value % 1 == 0;
}

//TODO optimize
var dateReg = /\d{1,2}[\./]{1}\d{1,2}[\./]{1}\d{4}/;
function isDate(value) {
    var match = dateReg.exec(value);
    if (!(match instanceof  Array))
        return false;
    if (match.length != 1)
        return false;
    return match[0] == value;
}

AdvancedSearchPanel.prototype.validateValue = function (entity, attribute, value) {
    if (attribute.predefined) {
        if (!(value instanceof Array))
            return "invalid value `" + value + "` for attribute " + entity.name + "." + attribute.label;
        else if (value.length == 0)
            return "nothing selected for attribute " + entity.name + "." + attribute.label;
        else
            return null;
    } else {
        if (attribute.multivalue) {
            if (value && value.length != 0)
                return null;
        } else {
            switch (attribute.dataTypeId) {
                case TEXT:
                case URL:
                    if (value && value.length != 0)
                        return null;
                    break;
                case INT:
                    if (isInt(value))
                        return null;
                    break;
                case DECIMAL:
                    if (isNumeric(value))
                        return null;
                    break;
                case BOOL:
                    var sValue = "" + value;
                    sValue = sValue.toLowerCase();
                    if (["1", "0", "true", "false", "t", "f", "yes", "no", "y", "n"].indexOf(sValue) != -1)
                        return null;
                    break;
                case DATE:
                    if (isDate(value))
                        return null;
                    break;
            }
        }
    }
    return "invalid value `" + value + "` for attribute " + entity.name + "." + attribute.label;
};

AdvancedSearchPanel.prototype.getDataFromInputElement = function (attribute, inputElement) {
    if (attribute.predefined) {
        var values = [];
        inputElement.selectAll("option").each(function () {
            if (this.selected)
                values.push(this.value);
        });
        return values;
    } else {
        if (attribute.multivalue) {
            return inputElement.node().value;
        } else {
            return inputElement.node().value;
        }
    }
};

AdvancedSearchPanel.prototype.accept = function (geoSearchCriteria) {
    app.ni3Model.setSortColumns(this.getSortColumns());

    var searchRequest = new request.Search();
    searchRequest.action = request.Search.Action.PERFORM_ADVANCED_SEARCH;
    searchRequest.schemaId = app.ni3Model.getSchemaId();
    searchRequest.queryType = this.entityAttributeMapping.type;
    for (var i = 0; i < this.entityAttributeMapping.entityIds.length; i++) {
        var key = this.entityAttributeMapping.entityIds[i];
        var searchSection = new request.SearchSection();
        var conditions = this.entityAttributeMapping[key];
        searchSection.entity = conditions.entity.id;
        conditions.conditions.forEach(function (condition) {
            var searchCondition = new request.SearchCondition();
            searchCondition.attributeId = condition[0].id;
            searchCondition.operation = condition[1];
            searchCondition.term = condition[2];
            searchSection.condition.push(searchCondition);
        });
        searchRequest.section.push(searchSection);

        var orderColumns = app.ni3Model.getSortColumnsByEntityId(conditions.entity.id);
        for (var k = 0; k < orderColumns.length; k++) {
            var searchOrder = new request.SearchOrder();
            searchOrder.attributeId = orderColumns[k].attribute.id;
            searchOrder.asc = orderColumns[k].asc;
            searchSection.order.push(searchOrder);
        }
    }
    searchRequest.limit = this.searchLimit;
    searchRequest.preFilter = app.ni3Model.getDataFilter().getAsMessage();
    if(geoSearchCriteria){
        searchRequest.geoSearchCriteria = geoSearchCriteria;
    }
    showWaitCursor();
    var firstEntityId = this.entityAttributeMapping[this.entityAttributeMapping.entityIds[0]].entity.id;

    if (app.ni3Model.isNewSearchMode()) {
        app.ni3Container.graphController.clearGraph();
        app.ni3Model.clearDataObjects();
        app.ni3Container.mapPanel.clearMap();
    }

    gateway.sendRequest(searchRequest, "SearchServlet", app.ni3Container.advancedSearchHandler, null, [searchRequest.queryType, firstEntityId, this.putOnGraphCount]);
    this.entityAttributeMapping = undefined;
};

AdvancedSearchPanel.prototype.collectData = function () {
    var valueMapping = {
        entityIds:[]
    };
    for (var i = 0; i < this.searchAttributes.length; i++) {
        var pair = this.searchAttributes[i];
        var entity = pair[0];
        var attribute = pair[1];
        var nodeIndex = pair[2];
        var row = d3.select("#" + this.prefixName + "_searchTableRow_" + attribute.id);

        var operation = row.select(".searchOperation").node().value;
        var input = row.select(".searchToken");
        var value = this.getDataFromInputElement(attribute, input);

        var key = "" + entity.id + nodeIndex;
        if (valueMapping[key] == undefined) {
            valueMapping.entityIds.push(key);
            valueMapping[key] = {};
            valueMapping[key].entity = entity;
            valueMapping[key].conditions = [];
        }
        valueMapping[key].conditions.push([attribute, operation, value]);
    }
    return valueMapping;
};

AdvancedSearchPanel.prototype.validate = function () {
    if (this.entityAttributeMapping.entityIds.length == 0) {
        this.showError("No criteria for search");
        return false;
    }
    for (var i = 0; i < this.entityAttributeMapping.entityIds.length; i++) {
        var entityId = this.entityAttributeMapping.entityIds[i];
        var entity = this.entityAttributeMapping[entityId].entity;
        var attributeValues = this.entityAttributeMapping[entityId].conditions;
        for (var j = 0; j < attributeValues.length; j++) {
            var error = this.validateValue(entity, attributeValues[j][0], attributeValues[j][2]);
            if (error != null) {
                this.showError(error);
                return false;
            }
        }
    }
    return true;
};

AdvancedSearchPanel.prototype.showError = function (error) {
    alert(error);
};

function onNodeSearchAttributeCheckbox(inputElement) {
    var nodeSearchPanel = app.ni3Container.advancedSearchDialog.currentSearchType.handler;
    var newState = inputElement.checked;
    var pair = inputElement.__data__;
    var row = d3.select(inputElement.parentNode.parentNode);
    if (newState) {
        nodeSearchPanel.generateInputForRow(row, pair);
        nodeSearchPanel.searchAttributes.push(pair);
    } else {
        nodeSearchPanel.removeInputs(row);
        var index = nodeSearchPanel.searchAttributes.indexOf(pair);
        if (index >= 0)
            nodeSearchPanel.searchAttributes.splice(index, 1);
    }
}

AdvancedSearchPanel.prototype.getInSearchAttributes = function (entity, index) {
    var inSearchAttributes = [];
    for (var i = 0; i < entity.attributes.length; i++)
        if (entity.attributes[i].inAdvancedSearch)
            inSearchAttributes.push([entity, entity.attributes[i], index]);
    return inSearchAttributes.sort(function (item1, item2) {
        return item1[1].sortSearch - item2[1].sortSearch;
    });
};

AdvancedSearchPanel.prototype.clearSearchCriteria = function () {
    var panel = d3.select("#" + this.prefixName + "Tabs");
    panel.selectAll("input.searchAttributeCheck").property("checked", false);
    this.removeInputs(panel);
    this.searchAttributes = [];
};

AdvancedSearchPanel.prototype.removeInputs = function (container) {
    container.selectAll(".searchOperation").remove();
    container.selectAll(".searchToken").remove();
    container.selectAll(".searchSortNrSelect")
        .property("value", "")
        .selectAll("option")
        .data(this.sortNrOptions)
        .exit().remove();
    container.selectAll(".searchSortDirectionSelect").remove();
};

AdvancedSearchPanel.prototype.addSortDirectionForRow = function (row) {
    var cell = row.select("td.searchSortDirectionTableCell");
    var select = cell.select("select");
    if (select.empty()) {
        cell.append("select")
            .attr("class", "searchSortDirectionSelect")
            .selectAll("option")
            .data(this.sortDirectionOptions)
            .enter()
            .append("option")
            .attr("value", function (d) {
                return d;
            })
            .text(function (d) {
                return d;
            });
    }
};

AdvancedSearchPanel.prototype.removeSortDirectionForRow = function (row) {
    row.select(".searchSortDirectionSelect").remove();
};

AdvancedSearchPanel.prototype.recalculateSorts = function (row, newValue) {
    var table = d3.select(row.node().parentNode);
    var allSorts = table.selectAll("select.searchSortNrSelect");
    var count = 0;
    allSorts.each(function () {
        if (this.value != "")
            count++;
    });

    if (newValue == "") {
        allSorts.filter(function () {
            return this.value != "" && this.value > count;
        }).property("value", count);
    }

    var data = (count == 0) ? this.sortNrOptions : this.sortNrOptions.concat(d3.range(2, count + 2));

    var options = allSorts.selectAll("option")
        .data(function () {
            return (this.parentNode.value == "" || data.length == 2) ? data : data.slice(0, -1);
        });
    options.enter()
        .append("option")
        .attr("value", function (d) {
            return d;
        })
        .text(function (d) {
            return d;
        });
    options.exit().remove();
};

AdvancedSearchPanel.prototype.getSortColumns = function () {
    var panel = d3.select("#" + this.prefixName + "Tabs");
    var selects = panel.selectAll("select.searchSortNrSelect").filter(function () {
        return this.value != "";
    });
    var sColumns = [];
    selects.each(function (row) {
        var entity = row[0];
        var attribute = row[1];
        var sortNr = this.value;
        var directionSelect = d3.select(this.parentNode.parentNode).select("select.searchSortDirectionSelect");
        var asc = directionSelect.node().value == "Asc";
        var sortColumn = new SortColumn(entity, attribute, asc, sortNr);
        sColumns.push(sortColumn);
    });
    return sColumns;
};

AdvancedSearchPanel.onSearchLimitChanged = function (select) {
    app.ni3Container.advancedSearchDialog.currentSearchType.handler.searchLimit = select.value;
};

AdvancedSearchPanel.onSortNrChanged = function (select) {
    var currentPanel = app.ni3Container.advancedSearchDialog.currentSearchType.handler;
    var value = select.value;
    var row = d3.select(select.parentNode.parentNode);
    if (value == "") {
        currentPanel.removeSortDirectionForRow(row);
    } else {
        currentPanel.addSortDirectionForRow(row);
    }
    currentPanel.recalculateSorts(row, value);
};