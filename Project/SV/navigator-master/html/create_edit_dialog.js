function AttributeRow(attribute, value) {
    this.attribute = attribute;
    this.value = value;
}

function CreateEditDialog() {
    this.dialogTable = undefined;
    this.dlg = undefined;
    this.errorMsg = undefined;
    this.entitySelect = undefined;
    this.currentEntity = undefined;
    this.currentDbObject = undefined;
    this.dataRows = undefined;

    this.showCreateEditDialog = function (allEntities, dbObject, isNode, params) {
        this.isNode = isNode;
        this.currentDbObject = dbObject;
        this.currentEntity = dbObject != null ? app.ni3Model.getEntityById(dbObject.entityId) : null;
        this.params = params;

        if (this.dlg == undefined) {
            var dialog = d3.select("body")
                .append("div")
                .attr("id", "dialogDiv")
                .attr("title", "Create/edit");

            this.errorMsg = dialog.append("p")
                .attr("id", "errorMsg");

            var entityRow = dialog.append("table")
                .attr("id", "entityRowTable")
                .append("tr");
            entityRow.append("td")
                .text("Type");
            this.entitySelect = entityRow.append("td")
                .append("select")
                .attr("id", "entitySelect");

            this.dialogTable = dialog.append("table")
                .attr("id", "dialogTable");
        }

        this.fillEntityCombo(allEntities, isNode);
        this.entitySelect.attr("disabled", function () {
            return dbObject != null ? "disabled" : null;
        });

        this.initTableData(this.currentEntity, dbObject);
        this.showDialog();
    };

    this.showDialog = function () {
        this.dlg = $("#dialogDiv");
        this.dlg.dialog({
            autoOpen:false,
            height:400,
            width:400,
            modal:true,
            buttons:{
                Ok:function () {
                    if (app.ni3Container.createEditDialog.validateInput()) {
                        app.ni3Container.createEditDialog.storeData();
                        app.ni3Container.createEditDialog.cleanData();
                        $(this).dialog("close");
                    }
                },
                Cancel:function () {
                    app.ni3Container.createEditDialog.cleanData();
                    $(this).dialog("close");
                }
            },
            close:function () {
                app.ni3Container.createEditDialog.cleanData();
            }
        });

        this.dlg.dialog("open");
    };

    this.cleanTableData = function () {
        this.dialogTable.selectAll("tr")
            .remove();
    };

    this.cleanData = function () {
        this.cleanTableData();
        this.errorMsg.text("");
        this.entitySelect.selectAll("option")
            .remove();
    };

    this.getEntities = function (allEntities, isNode) {
        var entities = [];
        for (var i = 0; i < allEntities.length; i++) {
            var entity = allEntities[i];
            if ((entity.objectTypeId == 2 && isNode) || (entity.objectTypeId == 4 && !isNode)) {
                entities.push(entity);
            }
        }
        return entities;
    };

    this.fillEntityCombo = function (allEntities, isNode) {
        var entities = this.getEntities(allEntities, isNode);
        this.entitySelect.selectAll("option")
            .data(entities)
            .enter()
            .append("option")
            .attr("value", function (d) {
                return d.id;
            })
            .text(function (d) {
                return d.name;
            });

        if (this.currentEntity == null && entities.length > 0) {
            this.currentEntity = entities[0];
        }
        if (this.currentEntity != null) {
            this.entitySelect.property("value", function () {
                return app.ni3Container.createEditDialog.currentEntity.id;
            });
        }
        this.entitySelect.attr("onchange", "CreateEditDialog.onEntityChanged(this)");
    };

    this.initTableData = function (entity, dbObject) {
        this.dataRows = this.createRows(entity, dbObject);
        this.dialogTable.selectAll("tr")
            .data(this.dataRows, function (d) {
                return d.attribute.id;
            })
            .enter()
            .append("tr")
            .append("td")
            .attr("class", function(d){
                return d.attribute.editUnlock == 3 ? "mandatoryField" : null;
            })
            .text(function (d) {
                return d.attribute.label;
            });

        var rows = this.dialogTable.selectAll("tr");

        this.createTextRows(rows);
        this.createPredefinedRows(rows);
        this.createMultivalueRows(rows);
        this.createMultiPredefinedRows(rows);
    };

    this.createRows = function (entity, dbObject) {
        var rows = [];
        var attributes = entity.attributes;
        for (var i = 0; i < attributes.length; i++) {
            var attr = attributes[i];
            if (attr.canRead && attr.editUnlock > 1) {
                var value = null;
                if (dbObject != null) {
                    value = app.ni3Container.getValue(dbObject, attr.id);
                    if (attr.multivalue) {
                        value = app.ni3Container.getMultivalues(value);
                    }
                }
                var attribute = new AttributeRow(attr, value);
                rows.push(attribute);
            }
        }
        return rows;
    };

    this.createMultivalueRows = function (rows) {
        var multivalueRows = rows.select(function (d) {
            return (d.attribute.predefined == 0 && d.attribute.multivalue) ? this : null;
        });

        multivalueRows.append("td")
            .append("textarea")
            .attr("class", "multivalueTextArea")
            .attr("rows", 3)
            .attr("wrap", "off")
            .attr("onblur", function (d, i) {
                return "CreateEditDialog.onMultivalueChanged(" + i + ",this)";
            })
            .property("value", function (d) {
                return d.value != null ? d.value.join("\n") : "";
            });
    };

    this.createMultiPredefinedRows = function (rows) {
        var multiPredefinedRows = rows.select(function (d) {
            return (d.attribute.predefined > 0 && d.attribute.multivalue) ? this : null;
        });
        var divs = multiPredefinedRows.append("td")
            .append("div")
            .attr("class", "multiPredefinedDiv")
            .selectAll("div")
            .data(function (d) {
                return app.ni3Container.createEditDialog.getValues(d.attribute, false);
            })
            .enter()
            .append("div");
        divs.append("input")
            .attr("type", "checkbox")
            .property("checked", function (d) {
                var value = this.parentNode.parentNode.__data__.value;
                return value != null && Utility.contains(value, d.id);
            })
            .attr("onclick", "CreateEditDialog.onMultiPredefinedChanged(this)");
        divs.append("label")
            .text(function (d) {
                return d.label;
            });
    };

    this.createPredefinedRows = function (rows) {
        var predefinedRows = rows.select(function (d) {
            return (d.attribute.predefined > 0 && !d.attribute.multivalue) ? this : null;
        });
        var selects = predefinedRows.append("td")
            .append("select");
        selects.selectAll("option")
            .data(function (d) {
                return app.ni3Container.createEditDialog.getValues(d.attribute, true);
            })
            .enter()
            .append("option")
            .attr("value", function (d) {
                return d.id;
            })
            .text(function (d) {
                return d.label;
            });
        selects.property("value", function (d) {
            return d.value;
        })
            .attr("onchange", function (d, i) {
                return "CreateEditDialog.onValueChanged(" + i + ", this)";
            });
    };

    this.createTextRows = function (rows) {
        var textRows = rows.select(function (d) {
            return (d.attribute.predefined == 0 && !d.attribute.multivalue) ? this : null;
        });
        textRows.append("td")
            .append("input")
            .attr("type", "text")
            .attr("value", function (row) {
                return row.value;
            })
            .attr("onblur", function (d, i) {
                return "CreateEditDialog.onValueChanged(" + i + ",this)";
            });
    };

    this.getValues = function (attribute, withEmpty) {
        var result = [];
        if (withEmpty) {
            result.push(new Object({id:null, label:""})); // add empty choice
        }
        var values = attribute.values;
        for (var i = 0; i < values.length; i++) {
            if (values[i].toUse) {
                result.push(values[i]);
            }
        }
        return result;
    };

    this.validateInput = function () {
        for (var i = 0; i < this.dataRows.length; i++) {
            var value = this.getStoreValue(this.dataRows[i]);
            var attribute = this.dataRows[i].attribute;
            if(attribute.editUnlock == 3 && (!value || value == "")){
                alert("Empty value is not allowed for attribute " + attribute.label);
                return false;
            }
        }
        return true;
    };

    this.storeData = function () {
        var dbObject = this.currentDbObject;
        if (dbObject == null) {
            dbObject = new Object({entityId:this.currentEntity.id});
            dbObject.dataPair = [];
        }

        for (var i = 0; i < this.dataRows.length; i++) {
            var value = this.getStoreValue(this.dataRows[i]);
            var attribute = this.dataRows[i].attribute;
            var existingIndex = this.getIndexOfData(dbObject.dataPair, attribute.id);
            if (existingIndex >= 0) {
                dbObject.dataPair[existingIndex].value = value;
            } else {
                var dataPair = new response.DataPair();
                dataPair.attributeId = attribute.id;
                dataPair.value = value;
                dbObject.dataPair.push(dataPair);
            }
        }

        if (this.isNode) {
            if (this.currentDbObject == null) {
                insertNode(dbObject, this.params);
            } else {
                updateNode(dbObject);
            }
        } else{
            if (this.currentDbObject == null) {
                insertEdge(this.params[0], this.params[1], dbObject);
            } else {
                updateEdge(dbObject);
            }
        }
    };

    this.getStoreValue = function (row) {
        var value = row.value;
        var attribute = row.attribute;
        if (attribute.multivalue) {
            if (value != null && value.length > 0) {
                value = value.map(function (v) {
                    v = (v + "").trim();
                    return v.length > 0 ? "{" + v + "}" : "";
                }).join("");
            }
        }
        return value;
    };

    this.getIndexOfData = function (arr, attributeId) {
        var result = -1;
        for (var i = 0; i < arr.length; i++) {
            if (arr[i].attributeId == attributeId) {
                result = [i];
            }
        }
        return result;
    };

    this.changeEntity = function (select) {
        this.currentEntity = app.ni3Model.getEntityById(select.value);
        this.cleanTableData();
        this.initTableData(this.currentEntity, this.currentDbObject);
    };

    this.changeValue = function (rowIndex, elem) {
        this.dataRows[rowIndex].value = elem.value;
    };

    this.changeMultivalue = function (rowIndex, elem) {
        var row = this.dataRows[rowIndex];
        row.value = elem.value.trim().split("\n");
    };

    this.changeMultiPredefined = function (cb) {
        var row = cb.parentNode.parentNode.__data__;
        var checked = cb.checked;
        var id = cb.__data__.id;
        var values = row.value == null ? [] : row.value;
        var index = Utility.indexOf(values, id);
        if (checked && index < 0) {
            values.push(id);
        } else if (!checked && index >= 0) {
            values.splice(index, 1);
        }
        row.value = values;
    };
}

CreateEditDialog.onEntityChanged = function (select) {
    app.ni3Container.createEditDialog.changeEntity(select);
};

CreateEditDialog.onValueChanged = function (rowIndex, elem) {
    app.ni3Container.createEditDialog.changeValue(rowIndex, elem)
};

CreateEditDialog.onMultivalueChanged = function (rowIndex, elem) {
    app.ni3Container.createEditDialog.changeMultivalue(rowIndex, elem)
};

CreateEditDialog.onMultiPredefinedChanged = function (cb) {
    app.ni3Container.createEditDialog.changeMultiPredefined(cb);
};


