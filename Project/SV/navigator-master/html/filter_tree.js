function FilterTreeEntity(id, label, sort, treeAttributes) {
    this.id = id;
    this.label = label;
    this.sort = sort;
    this.treeAttributes = treeAttributes;
}

function FilterTreeAttribute(id, label, sort, treeAttributeValues) {
    this.id = id;
    this.label = label;
    this.sort = sort;
    this.treeAttributeValues = treeAttributeValues;
    this.checked = true;
}

function FilterTreeAttributeValue(id, label) {
    this.id = id;
    this.label = label;
    this.checked = true;
}

function FilterTree(container, isDataFilter) {
    this.tree = undefined;
    this.isDataFilter = isDataFilter;
    this.treeId = isDataFilter ? "dataFilterTree" : "filterTree";
    this.treeEntities = [];
    this.container = container;

    this.updateTreeData = function () {
        this.clearTree();
        this.tree = this.container.append("ul")
            .attr("id", this.treeId)
            .attr("class", "filterUl")
            .style("display", "none");

        var onClick = this.isDataFilter ? "FilterTree.onDataFilterValueClicked(this)" : "FilterTree.onFilterValueClicked(this)";
        this.treeEntities = this.getEntities(app.ni3Model.getSchema());
        var entityList = this.tree.selectAll("li")
            .data(this.treeEntities, function (ent) {
                return ent.id;
            })
            .enter()
            .append("li");
        entityList.append("label")
            .attr("class", "treeEntityLabel")
            .text(function (d) {
                return d.label;
            });
        var attrLists = entityList.append("ul")
            .attr("class", "filterUl")
            .selectAll("li")
            .data(function (ent) {
                return ent.treeAttributes;
            }, function (attr) {
                return attr.id;
            })
            .enter()
            .append("li");
        attrLists.append("input")
            .attr("type", "checkbox")
            .property("checked", function (attr) {
                return attr.checked;
            })
            .attr("onclick", onClick);
        attrLists.append("label")
            .text(function (attr) {
                return attr.label;
            });
        var valueLists = attrLists.append("ul")
            .attr("class", "filterUl")
            .selectAll("li")
            .data(function (attr) {
                return attr.treeAttributeValues;
            }, function (av) {
                return av.id;
            })
            .enter()
            .append("li");
        valueLists.append("input")
            .attr("type", "checkbox")
            .attr("class", "treeValueCheckbox")
            .property("checked", function (av) {
                return av.checked;
            })
            .attr("onclick", onClick);
        valueLists.append("label")
            .attr("class", "treeValueLabel")
            .text(function (av) {
                return av.label;
            });

        $("#" + this.treeId).checkboxTree({
            initializeChecked:'collapsed',
            initializeUnchecked:'expanded',
            collapseDuration:200,
            expandDuration:200,
            onCheck:{
                ancestors:'checkIfFull',
                descendants:'check'
            },
            onUncheck:{
                ancestors:'uncheck'
            }
        });

        this.tree.style("display", null);
    };

    this.clearTree = function () {
        if (this.tree != undefined) {
            this.tree.remove();
            this.tree = undefined;
        }
    };

    this.getAttributeValues = function (attribute) {
        var values = [];
        for (var i = 0; i < attribute.values.length; i++) {
            var pAttr = attribute.values[i];
            if (pAttr.toUse) {
                values.push(new FilterTreeAttributeValue(pAttr.id, pAttr.label));
            }
        }
        return values;
    };

    this.getAttributes = function (entity) {
        var attributes = [];
        for (var i = 0; i < entity.attributes.length; i++) {
            var attr = entity.attributes[i];
            if (attr.canRead && attr.predefined > 0 && (this.isDataFilter ? attr.inPrefilter : attr.inFilter)) {
                var pValues = this.getAttributeValues(attr);
                if (pValues.length > 0) {
                    var treeAttr = new FilterTreeAttribute(attr.id, attr.label, attr.sortFilter, pValues);
                    attributes.push(treeAttr);
                }
            }
        }
        attributes.sort(function (a, b) {
            return a.sort - b.sort;
        });
        return attributes;
    };

    this.getEntities = function (schema) {
        var entities = [];
        for (var i = 0; i < schema.entities.length; i++) {
            var entity = schema.entities[i];
            var attributes = this.getAttributes(entity);
            if (attributes.length > 0) {
                entities.push(new FilterTreeEntity(entity.id, entity.name, entity.sort, attributes))
            }
        }
        entities.sort(function (a, b) {
            return a.sort - b.sort;
        });
        return entities;
    };

    this.resetFilter = function () {
        $("#" + this.treeId).checkboxTree('checkAll');
        for (var e = 0; e < this.treeEntities.length; e++) {
            var ent = this.treeEntities[e];
            for (var a = 0; a < ent.treeAttributes.length; a++) {
                var attr = ent.treeAttributes[a];
                attr.checked = true;
                for (var v = 0; v < attr.treeAttributeValues.length; v++) {
                    attr.treeAttributeValues[v].checked = true;
                }
            }
        }
    };

    this.updateCounts = function () {
        this.tree.selectAll("label.treeEntityLabel")
            .text(function (ent) {
                var text = ent.label;
                var count = app.ni3Container.graphController.getObjectCount(ent.id);
                if (count > 0) {
                    var displayedCount = app.ni3Container.graphController.getDisplayedObjectCount(ent.id);
                    text += " (" + displayedCount + "/" + count + ")";
                }
                return text;
            });

        var valueCounts = app.ni3Container.graphController.valueCounts;
        var displayedValueCounts = app.ni3Container.graphController.displayedValueCounts;
        this.tree.selectAll("label.treeValueLabel")
            .text(function (av) {
                var text = av.label;
                if (valueCounts[av.id] != undefined && valueCounts[av.id] > 0) {
                    text += " (" + displayedValueCounts[av.id] + "/" + valueCounts[av.id] + ")";
                }
                return text;
            });
    };

    this.getFilteredOutValues = function () {
        var values = [];
        this.tree.selectAll("input.treeValueCheckbox").each(function (d) {
            if (!d.checked) {
                values.push(d.id);
            }
        });
        return values;
    };
}

FilterTree.onFilterValueClicked = function (cb) {
    var data = cb.__data__;
    var checked = cb.checked;
    var filter = app.ni3Model.getDisplayFilter();
    var values = [];
    if (data instanceof FilterTreeAttributeValue) {
        data.checked = checked;
        values.push(data.id)
    } else if (data instanceof FilterTreeAttribute) {
        for (var i = 0; i < data.treeAttributeValues.length; i++) {
            var value = data.treeAttributeValues[i];
            value.checked = checked;
            values.push(value.id);
        }
    }
    if (checked) {
        filter.removeFilteredOutValues(values);
    } else {
        filter.addFilteredOutValues(values);
    }

    app.ni3Container.graphController.filterVisibleObjects(filter);
};

FilterTree.onDataFilterValueClicked = function (cb) {
    var data = cb.__data__;
    var checked = cb.checked;
    if (data instanceof FilterTreeAttributeValue) {
        data.checked = checked;
    } else if (data instanceof FilterTreeAttribute) {
        for (var i = 0; i < data.treeAttributeValues.length; i++) {
            var value = data.treeAttributeValues[i];
            value.checked = checked;
        }
    }
};