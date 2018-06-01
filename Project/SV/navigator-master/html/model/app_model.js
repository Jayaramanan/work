function Ni3Model() {
    this.serverUrl = null;
    this.schemas = [];
    this.settings = [];

    this.newSearchMode = true;
    this.nodeCreateMode = false;

    this.schema = null;
    this.chartMode = CHARTS_NONE;
    this.loadedChart = null;
    this.displayFilter = new Filter();
    this.dataFilter = new Filter();
    this.dataObjects = [];
    this.sortColumns = [];

    this.clearModel = function () {
        this.schema = null;
        this.chartMode = CHARTS_NONE;
        this.loadedChart = null;
        this.displayFilter.clear();
        this.dataFilter.clear();
        this.dataObjects = [];
    };

    this.getAllSchemas = function () {
        return this.schemas;
    };

    this.setAllSchemas = function (schemas) {
        this.schemas = schemas;
    };

    this.setSchema = function (schema) {
        this.schema = schema;
    };

    this.getSchema = function () {
        return this.schema;
    };

    this.getSchemaId = function () {
        return this.schema != null ? this.schema.id : -1;
    };

    this.setSettings = function (settings) {
        return this.settings = settings;
    };

    this.getSettingValue = function (property, defaultValue) {
        var result = this.settings[property];
        return result != undefined ? result : defaultValue;
    };

    this.setServerUrl = function (serverUrl) {
        this.serverUrl = serverUrl;
    };

    this.getServerUrl = function () {
        return this.serverUrl;
    };

    this.getChartMode = function () {
        return this.chartMode;
    };

    this.setChartMode = function (newMode) {
        this.chartMode = newMode;
    };

    this.getDisplayFilter = function () {
        return this.displayFilter;
    };

    this.getDataFilter = function () {
        return this.dataFilter;
    };

    this.isNewSearchMode = function () {
        return this.newSearchMode;
    };

    this.setNewSearchMode = function (newSearchMode) {
        this.newSearchMode = newSearchMode;
    };

    this.isNodeCreateMode = function () {
        return this.nodeCreateMode;
    };

    this.setNodeCreateMode = function (nodeCreateMode) {
        this.nodeCreateMode = nodeCreateMode;
    };

    this.getEntitiesByType = function (type) {
        var nodeEntities = [];
        for (var i = 0; i < this.schema.entities.length; i++) {
            var entity = this.schema.entities[i];
            if (entity.objectTypeId == type)
                nodeEntities.push(entity);
        }
        return nodeEntities;
    };

    this.getNodeEntities = function () {
        return this.getEntitiesByType(2);
    };

    this.getEdgeEntities = function () {
        var result = this.getEntitiesByType(4);
        return result.concat(this.getEntitiesByType(6));
    };

    this.getEntityById = function (id) {
        var result;
        var entities = this.schema.entities;
        for (var i = 0; i < entities.length; i++) {
            if (entities[i].id == id) {
                result = entities[i];
                break;
            }
        }
        return result;
    };

    this.getDataObjects = function () {
        return this.dataObjects;
    };

    this.clearDataObjects = function () {
        this.dataObjects = [];
    };

    this.fillDataObjects = function (dbObjects) {
        for (var o = 0; o < dbObjects.length; o++) {
            var dbObject = dbObjects[o];
            var key = "" + dbObject.entityId;
            if (this.dataObjects[key] == undefined) {
                this.dataObjects[key] = new Array();
            }
            var index = Utility.indexOfId(this.dataObjects[key], dbObject.id);
            if (index < 0) {
                this.dataObjects[key].push(dbObject);
            } else {
                this.dataObjects[key][index] = dbObject;
            }
        }
        if (this.sortColumns.length > 0) {
            var comparator = new DbObjectComparator(this.sortColumns);
            for (var key in this.dataObjects) {
                this.dataObjects[key].sort(comparator);
            }
        }
    };

    this.getAttributeByName = function (entityId, attributeNames) {
        var entity = this.getEntityById(entityId);
        var resultAttributes = new Array(attributeNames.length);
        for (var i = 0; i < attributeNames.length; i++) {
            var name = attributeNames[i];
            for (var j = 0; j < entity.attributes.length; j++) {
                var attr = entity.attributes[j];
                if (attr.name.toLowerCase() == name) {
                    resultAttributes[i] = attr;
                    break;
                }
            }
        }
        return resultAttributes;
    };

    this.getEdgeEntitiesForFromTo = function (fromEntity, toEntity) {
        var result = [];
        for (var i = 0; i < this.schema.objectConnections.length; i++) {
            var oc = this.schema.objectConnections[i];
            if (oc.fromObject == fromEntity && oc.toObject == toEntity) {
                var edgeEntity = this.getEntityById(oc.connectionObject);
                if (result.indexOf(edgeEntity) == -1)
                    result.push(edgeEntity);
            }
        }
        return result;
    };

    this.setSortColumns = function (sortColumns) {
        this.sortColumns = sortColumns;
        this.sortSortColumns();
    };

    this.sortSortColumns = function () {
        this.sortColumns.sort(function (a, b) {
            var result = 0;
            if (a.entity.id == b.entity.id) {
                result = a.sortNr > b.sortNr ? 1 : (a.sortNr < b.sortNr ? -1 : 0);
            } else {
                result = a.entity.sort > b.entity.sort ? 1 : (a.entity.sort < b.entity.sort ? -1 : 0);
            }
            return result;
        });
    };

    this.getSortColumns = function () {
        return this.sortColumns;
    };

    this.getSortColumnsByEntityId = function (entityId) {
        var result = [];
        for (var i = 0; i < this.sortColumns.length; i++) {
            if (this.sortColumns[i].entity.id == entityId) {
                result.push(this.sortColumns[i]);
            }
        }
        return result;
    };

    this.isDataSorted = function () {
        return this.sortColumns.length > 0;
    };
}

Ni3Model.prototype.constructor = Ni3Model;

Ni3Model.MAX_OBJECTS_TO_DISPLAY = 1000;

function Filter() {
    this.filteredOut = [];
    this.addFilteredOutValues = function (values) {
        for (var i = 0; i < values.length; i++) {
            if (!Utility.contains(this.filteredOut, values[i])) {
                this.filteredOut.push(values[i]);
            }
        }
    };

    this.clear = function () {
        this.filteredOut = [];
    };

    this.removeFilteredOutValues = function (values) {
        for (var i = 0; i < values.length; i++) {
            var index = Utility.indexOf(this.filteredOut, values[i]);
            if (index >= 0) {
                this.filteredOut.splice(index, 1);
            }
        }
    };

    this.isObjectFilteredOut = function (dbObject) {
        if (this.filteredOut.length == 0)
            return false;

        var isFilteredOut = false;
        var attributes = app.ni3Model.getEntityById(dbObject.entityId).attributes;
        for (var i = 0; i < attributes.length; i++) {
            var attribute = attributes[i];
            if (!attribute.predefined || !attribute.canRead) {
                continue;
            }
            var value = app.ni3Container.getValue(dbObject, attribute.id);
            if (value != null && value != undefined) {
                if (attribute.multivalue) {
                    var multivalues = app.ni3Container.getMultivalues(value);
                    if (multivalues.length > 0) {
                        var allFiltered = true;
                        for (var v = 0; v < multivalues.length; v++) {
                            if (!Utility.contains(this.filteredOut, multivalues[v])) {
                                allFiltered = false;
                                break;
                            }
                        }
                        if (allFiltered) {
                            isFilteredOut = true;
                            break;
                        }
                    }
                } else {
                    if (Utility.contains(this.filteredOut, value)) {
                        isFilteredOut = true;
                        break;
                    }
                }
            }
        }

        return isFilteredOut;
    };

    this.getAsMessage = function () {
        var msg = new request.Filter();
        for (var i = 0; i < this.filteredOut.length; i++) {
            msg.valueId.push(this.filteredOut[i]);
        }
        return msg;
    };
}

function SortColumn(entity, attribute, asc, sortNr) {
    this.entity = entity;
    this.attribute = attribute;
    this.asc = asc;
    this.sortNr = sortNr;
}

function DbObjectComparator(sortColumns) {
    var sColumns = sortColumns;

    return function (firstObj, secondObj) {
        var result = 0;
        if (firstObj.entityId != secondObj.entityId) {
            result = app.ni3Model.getEntityById(firstObj.entityId).sort > app.ni3Model.getEntityById(secondObj.entityId).sort ? 1 : -1;
        } else {
            for (var c = 0; c < sColumns.length; c++) {
                var sortColumn = sColumns[c];
                if (sortColumn.entity.id != firstObj.entityId)
                    continue;
                var firstValue = app.ni3Container.getDisplayValue(firstObj, sortColumn.attribute);
                var secondValue = app.ni3Container.getDisplayValue(secondObj, sortColumn.attribute);
                if (firstValue == undefined) {
                    firstValue = "";
                }
                if (secondValue == undefined) {
                    secondValue = "";
                }
                result = firstValue < secondValue ? -1 : (firstValue > secondValue ? 1 : 0);
                if (result != 0) {
                    result = result * (sortColumn.asc ? 1 : -1);
                    break;
                }
            }
        }
        return result;
    }
}