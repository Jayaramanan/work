function MatrixEntity(id, name, sort, attributes) {
    this.id = id;
    this.name = name;
    this.sort = sort;
    this.attributes = attributes;
}

function MatrixAttribute(id, label, sort, predefined, multivalue) {
    this.id = id;
    this.label = label;
    this.sort = sort;
    this.predefined = predefined;
    this.multivalue = multivalue;
}

function MatrixCell(dbObject, attribute) {
    this.dbObject = dbObject;
    this.attribute = attribute;
}

function Matrix() {
    this.SELECTION_ATTR_ID = -2;
    this.METAPHOR_ATTR_ID = -1;
    this.matrixEntities = undefined;
}

Matrix.prototype.getMatrixStructure = function () {
    var tabs = [];
    var entities = app.ni3Model.getSchema().entities;
    for (var i = 0; i < entities.length; i++) {
        var entity = entities[i];
        if (entity.objectTypeId == 2) {
            var attributes = this.getInMatrixAttributes(entity.attributes);
            var entityTab = new MatrixEntity(entity.id, entity.name, entity.sort, attributes);
            tabs.push(entityTab);
        }
    }
    tabs.sort(function (a, b) {
        return a.sort - b.sort;
    });

    return tabs;
};

Matrix.prototype.getMatrixEntityById = function (entityId) {
    var result;
    for (var i = 0; i < this.matrixEntities.length; i++) {
        if (this.matrixEntities[i].id == entityId) {
            result = this.matrixEntities[i];
            break;
        }
    }
    return result;
};

Matrix.prototype.getInMatrixAttributes = function (attributes) {
    var inMatrixAttrs = [];
    for (var i = 0; i < attributes.length; i++) {
        var attr = attributes[i];
        if (attr.canRead && attr.inMatrix > 0) {
            var attribute = new MatrixAttribute(attr.id, attr.label, attr.sortMatrix, attr.predefined, attr.multivalue);
            inMatrixAttrs.push(attribute);
        }
    }
    inMatrixAttrs.sort(function (a, b) {
        return a.sort - b.sort
    });
    var selectionAttr = new MatrixAttribute(this.SELECTION_ATTR_ID, "", -2, false, false);
    var metaphorAttr = new MatrixAttribute(this.METAPHOR_ATTR_ID, "", -1, false, false);
    inMatrixAttrs.unshift(selectionAttr, metaphorAttr);
    return inMatrixAttrs;
};

Matrix.prototype.initMatrix = function () {
    if (this.matrixEntities != undefined) {
        this.clearMatrixTabs();
    }

    this.matrixEntities = this.getMatrixStructure();
    this.initMatrixTabs(this.matrixEntities);
};

Matrix.prototype.clearMatrixTabs = function () {
    d3.select("#matrixPanel")
        .select("div")
        .remove();
};

Matrix.prototype.initMatrixTabs = function (matrixEntities) {
    var tabsPanel = d3.select("#matrixPanel")
        .append("div")
        .attr("id", "tabsPanel");
    tabsPanel.append("ul")
        .attr("id", "tabsHeader")
        .selectAll("li")
        .data(matrixEntities)
        .enter()
        .append("li")
        .append("a")
        .attr("href", function (ent) {
            return "#tab_" + Utility.stringAsIdentifier(ent.name);
        })
        .append("span")
        .text(function (ent) {
            return ent.name;
        });

    var tabs = tabsPanel
        .selectAll("div")
        .data(matrixEntities);
    tabs.enter()
        .append("div")
        .attr("id", function (ent) {
            return "tab_" + Utility.stringAsIdentifier(ent.name);
        })
        .attr("class", "matrixTableDiv")
        .append("table")
        .attr("id", function (ent) {
            return "tbl_" + Utility.stringAsIdentifier(ent.name);
        })
        .attr("class", "matrixTable");

    $("#tabsPanel").tabs();

    this.initMatrixTables();
};

Matrix.prototype.initMatrixTables = function () {
    var matrixTables = d3.selectAll("table.matrixTable");
    var thead = matrixTables.append("thead");

    matrixTables.append("tbody")
        .attr("class", "matrixData");

    thead.append("tr")
        .selectAll("th")
        .data(function (ent) {
            return ent.attributes;
        })
        .enter()
        .append("th")
        .text(function (attr) {
            return attr.label;
        });
};

Matrix.prototype.updateMatrixTableData = function (dbObjectMap) {
    var refThis = this;
    var tableData = d3.selectAll("tbody.matrixData");
    var rows = tableData.selectAll("tr")
        .data(function (ent) {
            var dbObjects = dbObjectMap["" + ent.id];
            return dbObjects != undefined ? dbObjects : [];
        }, function (d) {
            return d.id;
        });
    rows.enter()
        .append("tr");

    var cells = tableData.selectAll("tr")
        .selectAll("td")
        .data(function (dbObject) {
            return refThis.getMatrixCells(dbObject);
        })
        .enter()
        .append("td");

    cells.filter(function (d, i) {
        return i == 0;
    })
        .attr("class", "selectionColumn")
        .append("input")
        .attr("type", "checkbox")
        .attr("onclick", "Matrix.handleCheckboxChange(this)");

    cells.filter(function (d, i) {
        return i == 1;
    })
        .attr("class", "metaphorColumn")
        .append("img");

    rows.exit().remove();
    if (app.ni3Model.isDataSorted()) {
        rows.order();
    }

    this.updateExistingRows();
    this.updateMatrixSelection();
};

Matrix.prototype.updateExistingRows = function () {
    var refThis = this;
    var cells = d3.selectAll("tbody.matrixData")
        .selectAll("tr")
        .selectAll("td");

    cells.filter(function (d, i) {
        return i > 1;
    })
        .text(function (d) {
            return refThis.getMatrixTableCellValue(d.dbObject, d.attribute);
        });

    cells.filter(function (d, i) {
        return i == 1;
    })
        .select("img")
        .attr("src", function (d) {
            return app.ni3Container.getFullIconName(d.dbObject);
        });
};

Matrix.prototype.updateMatrixSelection = function () {
    var checkboxes = d3.selectAll("td.selectionColumn").select("input");
    checkboxes.property("checked", function (d) {
        var node = app.ni3Container.graphController.getDisplayedNode(d.dbObject.id);
        return node != null && !app.ni3Container.graphController.isFilteredOut(d.dbObject.id);
    });
    this.updateTabHeader();
};

Matrix.prototype.updateTabHeader = function () {
    var tabNames = d3.select("#tabsHeader").selectAll("span");
    tabNames.text(function (ent) {
        var dbObjects = app.ni3Model.getDataObjects()["" + ent.id];
        var name = ent.name;
        if (dbObjects != undefined && dbObjects.length > 0) {
            var displayedCount = app.ni3Container.graphController.getDisplayedObjectCount(ent.id);
            name += " (" + displayedCount + "/" + dbObjects.length + ")";
        }
        return name;
    });
};

Matrix.prototype.getMatrixCells = function (dbObject) {
    var entity = this.getMatrixEntityById(dbObject.entityId);
    var result = [];
    for (var i = 0; i < entity.attributes.length; i++) {
        result.push(new MatrixCell(dbObject, entity.attributes[i]));
    }
    return result;
};

Matrix.prototype.getMatrixTableCellValue = function (dbObject, attribute) {
    var value;
    if (attribute.predefined) {
        var attr = app.ni3Container.getAttribute(dbObject.entityId, attribute.id);
        value = app.ni3Container.getDisplayValue(dbObject, attr);
    } else {
        value = app.ni3Container.getDisplayValue(dbObject,
            {id:attribute.id, multivalue:attribute.multivalue, predefined:attribute.predefined});
    }
    if (value == undefined)
        value = "";
    return value;
};

Matrix.prototype.applyDataFilterToMatrix = function (dataFilter) {
    var dataObjects = app.ni3Model.getDataObjects();
    var nodeEntities = app.ni3Model.getNodeEntities();
    var filtered = false;
    for (var e = 0; e < nodeEntities.length; e++) {
        var dbo = dataObjects[nodeEntities[e].id];
        if (dbo != undefined) {
            var n = 0;
            while (n < dbo.length) {
                if (dataFilter.isObjectFilteredOut(dbo[n])) {
                    dbo.splice(n, 1);
                    filtered = true;
                } else {
                    n++;
                }
            }
        }
    }
    if (filtered) {
        this.updateMatrixTableData(dataObjects);
    }
};

Matrix.handleCheckboxChange = function (cb) {
    var row = cb.parentNode.parentNode;
    var dbObject = row.__data__;
    var displayedNode = app.ni3Container.graphController.getDisplayedNode(dbObject.id);
    var checked = cb.checked;
    if (checked != (displayedNode != undefined)) {
        if (checked) {
            app.ni3Container.getNodes([dbObject]);
        } else {
            app.ni3Container.graphController.removeNode(displayedNode, true);
        }
    }
};