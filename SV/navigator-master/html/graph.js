function GraphController() {
    this.filteredOutIds = [];
    this.valueCounts = [];
    this.displayedValueCounts = [];
    this.animationFreezed = false;
    this.nodesFreezed = false;
    this.graphCommandPanel = new GraphCommandPanel();
}

GraphController.prototype.constructor = GraphController;

var w = 960, h = 700;
var visibleNodes = [];
var visibleNodesByType = [];
var visibleLinks = [];
var rWidth = rHeight = 5000;
var rStart = 2200;

var graphPanel;
var graphSvg;
var force;
var tooltip;
var zoomHandler;
var currTranslate = [0, 0], currScale = 1;

GraphController.prototype.init = function () {
    zoomHandler = d3.behavior.zoom();
    graphPanel = d3.select("#graphPanel");

    var self = this;
    graphPanel.on("dblclick", function () {
        self.freezeNodes(!self.nodesFreezed);
        self.nodesFreezed = !self.nodesFreezed;
    });

    graphSvg = graphPanel
        .append("svg:svg")
        .attr("width", "100%")
        .attr("height", "100%")
        .append('svg:g')
        .attr("id", "g1")
        .call(zoomHandler.on("zoom", this.zoom))
        .on("dblclick.zoom", null)
        .on("click", function () {
            if (app.ni3Model.isNodeCreateMode()) {
                if (self.edgeError) {
                    self.edgeError = undefined;
                    return;
                }
                app.ni3Container.createEditDialog.showCreateEditDialog(app.ni3Model.getSchema().entities, null, true);
            }
        })
        .append('svg:g')
        .attr("id", "g2");

    graphSvg.append('svg:rect')
        .attr('width', rWidth)
        .attr('height', rHeight)
        .attr('x', -rStart)
        .attr('y', -rStart)
        .attr('fill', 'white');

    d3.select("#g1").
        on("mousedown", function () {
            if (!app.ni3Model.isNodeCreateMode())
                return;
            if (!self.nodeUnderCursor)
                return;
            self.tempEdge = {
                fromNode:self.nodeUnderCursor,
                edgeUI:d3.select("#g1")
                    .append("line")
                    .attr("id", "tempEdge")
                    .attr("x1", d3.event.layerX)
                    .attr("y1", d3.event.layerY)
                    .attr("x2", d3.event.layerX)
                    .attr("y2", d3.event.layerY)
                    .style("stroke", "gray"),
                coords:{x:d3.event.layerX, y:d3.event.layerY}
            };
        })
        .on("mouseup", function () {
            if (!app.ni3Model.isNodeCreateMode())
                return;
            if (!self.tempEdge)
                return;
            self.tempEdge.edgeUI.remove();
            if (self.nodeUnderCursor) {
                self.tempEdge.toNode = self.nodeUnderCursor;
                if (self.isEdgePossible(self.tempEdge.fromNode, self.tempEdge.toNode))
                    app.ni3Container.createEditDialog.showCreateEditDialog(
                        app.ni3Model.getEdgeEntitiesForFromTo(self.tempEdge.fromNode.objectDefinitionId,
                            self.tempEdge.toNode.objectDefinitionId),
                        null,
                        false,
                        [self.tempEdge.fromNode, self.tempEdge.toNode]);
            }
            self.tempEdge = undefined;
        })
        .on("mousemove", function () {
            if (!app.ni3Model.isNodeCreateMode())
                return;
            if (!self.tempEdge)
                return;
            //edge is following cursor with small gap (2x2 pix) to prevent
            //node.mouseout event false firing
            // (cursor alternates being over node and edge so node.mouseover/node.mouseout events are false fired)
            var x = d3.event.layerX;
            var y = d3.event.layerY;
            x += x > self.tempEdge.coords.x ? -2 : 2;
            y += y > self.tempEdge.coords.y ? -2 : 2;
            self.tempEdge.coords = {x:x, y:y};
            ///////////////////////////////////////////
            self.tempEdge.edgeUI
                .attr("x2", x)
                .attr("y2", y);
            d3.event.cancelBubble = true;
        });

    force = d3.layout.force().size([ w, h ]).distance(50);

    //TODO merge context menu definitions? and handlers?
    $.contextMenu({
        selector:'line.link',
        callback:app.ni3Container.graphController.edgeContextMenuHandler,
        items:{
            "edit":{name:"Edit", icon:"edit"},
            "delete":{name:"Delete", icon:"delete"}
        }
    });
    $.contextMenu({
        selector:'g.node',
        callback:app.ni3Container.graphController.nodeContextMenuHandler,
        items:{
            "edit":{name:"Edit", icon:"edit"},
            "remove":{name:"Remove", icon:"remove"},
            "delete":{name:"Delete", icon:"delete"},
            "sep1":"---------",
            "expand":{name:"Expand", icon:"expand"}
        }
    });
    this.graphCommandPanel.init();
    this.graphCommandPanel.setNodeSpaceSliderHandler(function (value) {
        force.distance(value * 10);
        force.start();
    });
};

GraphController.prototype.isEdgePossible = function (fromNode, toNode) {
    if (fromNode.id == toNode.id) {
        alert("Cyclic edges are not allowed");
        this.edgeError = true;
        return false;
    }
    var objectConnections = app.ni3Model.getSchema().objectConnections;
    for (var i = 0; i < objectConnections.length; i++) {
        var oc = objectConnections[i];
        if (oc.fromObject == fromNode.objectDefinitionId && oc.toObject == toNode.objectDefinitionId)
            return true;
    }
    alert("Edge is not allowed between this two types of nodes");
    return false;
};

GraphController.prototype.saveAnimationState = function () {
    this.savedAnimationState = this.animationFreezed;
};

GraphController.prototype.restoreAnimationState = function () {
    if (this.savedAnimationState)
        this.freezeAnimation(this.savedAnimationState);
    this.savedAnimationState = undefined;
};

GraphController.prototype.freezeAnimation = function (flag) {
    if (flag)
        force.stop();
    else
        force.start();
    this.animationFreezed = flag;
};

GraphController.prototype.enableDrag = function (flag) {
    visibleNodes.forEach(function (node) {
        node.draggable = flag ? undefined : flag;
    });
};

GraphController.prototype.freezeNodes = function (flag) {
    visibleNodes.forEach(function (node) {
        node.fixed = flag ? true : node.level == 0;
    });
};

GraphController.prototype.zoom = function () {
    currTranslate = d3.event.translate;
    currScale = d3.event.scale;
    graphSvg.attr("transform", "translate(" + currTranslate + ")" + " scale(" + currScale + ")");
};

GraphController.prototype.translateToStart = function () {
    currTranslate = [0, 0];
    currScale = 1;
    zoomHandler.translate(currTranslate).scale(currScale);
    var g2 = d3.select("#g2");
    var transform = g2.attr("transform");
    g2.attr("transform", null);
};

GraphController.prototype.clearNodeMetaphors = function () {
    graphSvg.selectAll("g.node").remove();
};

GraphController.prototype.updateGraph = function () {
    var refThis = this;
    force.nodes(visibleNodes)
        .links(visibleLinks)
        .gravity(0)
        .friction(.6)
        .start();

    var links = graphSvg.selectAll("line.link")
        .data(visibleLinks, function (d) {
            return d.id;
        });
    links.enter()
        .insert("svg:line", "g.node")
        .attr("class", "link")
        .attr("x1", function (d) {
            return d.source.x;
        })
        .attr("y1", function (d) {
            return d.source.y;
        })
        .attr("x2", function (d) {
            return d.target.x;
        })
        .attr("y2", function (d) {
            return d.target.y;
        })
        .attr("id", function (d) {
            return d.id;
        })
        .on("mouseover", app.ni3Container.showTooltip)
        .on("mouseout", app.ni3Container.hideTooltip)
        .style("stroke", function () {
            return "gray";
        });

    if (app.ni3Model.getChartMode() == CHARTS_NONE) {
        var gNodes = graphSvg.selectAll("g.node")
            .data(visibleNodes,function (d) {
                return d.id;
            }).enter();
        this.createNodeGroups(gNodes);
    }
    else {
        visibleNodesByType.forEach(function (objects, entityId) {
            var gNodes = graphSvg.selectAll("g.node")
                .data(objects,function (d) {
                    return d.id;
                }).enter();
            refThis.createNodeGroups(gNodes, entityId);
        });
    }

    var nodes = graphSvg.selectAll("g.node")
        .data(visibleNodes, function (d) {
            return d.id;
        });
    nodes.selectAll("text.counter").text(function (d) {
        return refThis.getCounterText(d);
    });

    var self = this;
    force.on("tick", function () {
        links.attr("x1", function (d) {
            return d.source.x;
        })
            .attr("y1", function (d) {
                return d.source.y;
            })
            .attr("x2", function (d) {
                return d.target.x;
            })
            .attr("y2", function (d) {
                return d.target.y;
            });
        if (self.nodesFreezed) {
            self.arrangeNewNodesManually(nodes);
        }
        nodes.each(function (d) {
            d.justAdded = undefined;
        });
        nodes.attr("transform", function (d) {
            return "translate(" + d.x + "," + d.y + ")";
        });
    });


    nodes.exit().remove();
    links.exit().remove();

    this.filterVisibleObjects(app.ni3Model.getDisplayFilter());

    this.optimizeGraph(nodes);
};

GraphController.prototype.optimizeGraph = function (nodes) {
    var optimize = false;
    nodes.each(function (node) {
        if (node.toOptimize) {
            node.fixed = false;
            optimize = true;
        }
    });

    if (optimize) {
        d3.timer(function () {
            nodes.each(function (node) {
                if (node.toOptimize) {
                    node.fixed = true;
                    node.toOptimize = undefined;
                }
            });
            return true;
        }, 3000); // cancel optimization after 3 seconds
    }
};

GraphController.prototype.arrangeNewNodesManually = function (nodes) {
    nodes.each(function (d) {
        if (!d.justAdded)
            return;
        //manually placing nodes in ~100 pix radius around parent
        // (d3 makes new nodes coords == parent coords so using nodes coords as base)
        d.x = d.px = d.x + (200 * Math.random() - 100);
        d.y = d.py = d.y + (200 * Math.random() - 100);
    });

};

GraphController.prototype.updateVisibility = function () {
    graphSvg.selectAll("g.node")
        .style("display", function (d) {
            return app.ni3Container.graphController.isFilteredOut(d.id) ? "none" : null;
        });

    graphSvg.selectAll("line.link")
        .style("display", function (d) {
            return app.ni3Container.graphController.isFilteredOut(d.id) ? "none" : null;
        });
};

//TODO merge context menu handler methods if do the same?
GraphController.prototype.nodeContextMenuHandler = function (key) {
    //TODO:FIXME dirty hack - access node assigned data via property __data__??
    var node = this[0].__data__;
    switch (key) {
        case "edit":
            app.ni3Container.createEditDialog.showCreateEditDialog(app.ni3Model.getSchema().entities, node.dbObject, true);
            break;
        case "remove":
            app.ni3Container.graphController.removeNode(node, true, false);
            break;
        case "delete":
            app.ni3Container.graphController.removeNode(node, true, true);
            app.ni3Container.removeNode(node);
            deleteObject(node.id, node.objectDefinitionId);
            break;
        case "expand":
            app.ni3Container.graphController.expand([node]);
            break;
        default:
            alert("Unhandled action: " + key + " targetNode: " + node.id);
            break;
    }
};

GraphController.prototype.edgeContextMenuHandler = function (key) {
    //TODO:FIXME dirty hack - access node assigned data via property __data__??
    var edge = this[0].__data__;
    switch (key) {
        case "edit":
            app.ni3Container.createEditDialog.showCreateEditDialog(app.ni3Model.getSchema().entities, edge.dbObject, false);
            break;
        case "delete":
            app.ni3Container.graphController.removeEdge(edge, true);
            deleteObject(edge.id, edge.objectDefinitionId);
            break;
        default:
            alert("Unhandled action: " + key + " targetEdge: " + edge.id);
            break;
    }
};

GraphController.prototype.createNodeGroups = function (gNodes, entityId) {
    var self = app.ni3Container.graphController;
    gNodes = gNodes.append("svg:g")
        .attr("id", function (d) {
            return d.id;
        })
        .attr("transform", function (d) {
            return "translate(" + d.x + "," + d.y + ")";
        })
        .attr("class", "node")
        .on("click", null)
        .on("dblclick", this.dblClick)
        .on("mouseover", function (node) {
            if (app.ni3Model.isNodeCreateMode())
                self.nodeUnderCursor = node;
            else
                app.ni3Container.showTooltip(node);
        })
        .on("mouseout", function () {
            if (app.ni3Model.isNodeCreateMode())
                self.nodeUnderCursor = undefined;
            else
                app.ni3Container.hideTooltip();
        })
        .call(force.drag);
    switch (app.ni3Model.getChartMode()) {
        case CHARTS_NONE:
            this.createNodeImages(gNodes);
            break;
        case CHARTS_PIE:
            var chartedEntities = [];
            for (var i = 0; i < loadedChart.objectCharts.length; i++)
                chartedEntities.push(loadedChart.objectCharts[i].objectId);
            if (chartedEntities.indexOf(entityId) == -1)
                this.createNodeImages(gNodes);
            else {
                var pieLayout = d3.layout.pie().sort(null);
                var arc = d3.svg.arc().outerRadius(20);
                gNodes.each(function (htmlNode, index) {
                    var data = GraphController.makeDataForNode(visibleNodesByType[entityId][index]);
                    if (data == null)
                    //if we fill circle with none color - it can't be dragged, so filling with graph's background color (white)
                        d3.select(this).append("svg:circle").attr("r", 10).attr("fill", "white").attr("stroke", "black").attr("stroke-width", "1");
                    else
                        d3.select(this).selectAll("path").data(pieLayout(data.values))
                            .enter().append("svg:path")
                            .attr("fill", function (d, i) {
                                return data.colors[i];
                            })
                            .attr("d", arc);
                });
            }
            break;
    }
    gNodes.append("svg:text")
        .attr("class", "counter")
        .attr("dx", 12)
        .attr("dy", -12)
        .text(function (d) {
            return app.ni3Container.graphController.getCounterText(d);
        });
};

//TODO: optimize this shit
GraphController.makeDataForNode = function (node) {
    var objectChart = null;
    for (var i = 0; i < loadedChart.objectCharts.length; i++) {
        if (loadedChart.objectCharts[i].objectId == node.objectDefinitionId)
            objectChart = loadedChart.objectCharts[i];
    }
    if (objectChart == null) {
        console.error("cannot find object chart for entity: " + node.objectDefinitionId + "|" + node.id);
        return null;
    }
    var data = {};
    data.values = [];
    data.colors = [];
    for (i = 0; i < objectChart.chartAttributes.length; i++) {
        var objectChartAttribute = objectChart.chartAttributes[i];
        for (var j = 0; j < node.dbObject.dataPair.length; j++) {
            var dataPair = node.dbObject.dataPair[j];
            if (dataPair.attributeId == objectChartAttribute.attributeId) {
                var val = +dataPair.value;
                if (val > 0) {
                    data.values.push(val);
                    data.colors.push(objectChartAttribute.rgb);
                }
            }
        }
    }
    if (data.values.length == 0)
        return null;
    return data;
};

GraphController.prototype.createNodeImages = function (gNodes) {
    gNodes.append("svg:image")
        .attr("class", "circle")
        .attr("x", "-8px")
        .attr("y", "-8px")
        .attr("width", "16px")
        .attr("height", "16px")
        .attr("xlink:href", function (d) {
            return app.ni3Container.getFullIconName(d.dbObject);
        });
};

GraphController.prototype.dblClick = function (node) {
    var event = d3.event;
    event.cancelBubble = true;
    var count = app.ni3Container.graphController.getExpandCounter(node);
    if (count > 0) {
        app.ni3Container.graphController.expand([node]);
    } else {
        app.ni3Container.graphController.contract(node);
    }
};

GraphController.prototype.getMissingMap = function () {
    var map = new Array();
    var allObjects = [ visibleNodes, visibleLinks ];
    for (var o = 0; o < allObjects.length; o++) {
        var objects = allObjects[o];
        for (var i = 0; i < objects.length; i++) {
            var obj = objects[i];
            if (obj.dbObject == undefined) {
                var key = "" + obj.objectDefinitionId;
                if (map[key] == undefined) {
                    map[key] = new Array();
                }
                map[key].push(obj.id);
            }
        }
    }

    return map;
};

GraphController.prototype.fillDbObjects = function (dbObjects) {
    var allObjects = [ visibleNodes, visibleLinks ];
    for (var o = 0; o < allObjects.length; o++) {
        var objects = allObjects[o];
        for (var i = 0; i < objects.length; i++) {
            var obj = objects[i];
            for (var k = 0; k < dbObjects.length; k++) {
                if (dbObjects[k].id == obj.id) {
                    obj.dbObject = dbObjects[k];
                    break;
                }
            }
        }
    }
    app.ni3Model.fillDataObjects(dbObjects);
    app.ni3Container.matrix.updateMatrixTableData(app.ni3Model.getDataObjects());
    if (app.ni3Model.getChartMode() != CHARTS_NONE)
        this.makeVisibleNodesMapping();
    app.ni3Container.mapPanel.showNodesAndEdgesOnMap(visibleNodes, visibleLinks);
    this.updateGraph();
};

GraphController.prototype.showEdge = function (edge, fromNode, toNode) {
    edge.source = fromNode;
    edge.target = toNode;
    fromNode.childrenCount++;
    toNode.parentCount++;
    visibleLinks.push(edge);
    var map = app.ni3Container.graphController.getMissingMap();
    app.ni3Container.getDbObjects(map, false);
};

GraphController.prototype.showNodes = function (nodes) {
    if (nodes != null) {
        var gpWidth = graphPanel.style("width");
        gpWidth = gpWidth.replace("px", "") / currScale;
        var gpHeight = graphPanel.style("height");
        gpHeight = gpHeight.replace("px", "") / currScale;
        for (var i = 0; i < nodes.length; i++) {
            if (Utility.containsId(visibleNodes, nodes[i].id)) {
                continue;
            }
            var node = nodes[i];
            node.level = 0;
            node.x = node.px = Math.floor((Math.random() * gpWidth * .9) - currTranslate[0] + 10);
            node.y = node.py = Math.floor((Math.random() * gpHeight * .9) - currTranslate[1] + 10);
            node.fixed = true;
            visibleNodes.push(node);
        }
    }
};

GraphController.prototype.makeFixedByEntityId = function (nodeEntityId, nodes) {
    for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        if (node.objectDefinitionId == nodeEntityId)
            continue;
        node.fixed = false;
        node.level = 1;
        node.toOptimize = undefined;
    }
};

GraphController.prototype.expand = function (nodes) {
    app.ni3Container.getNodesWithEdges(nodes);
};

GraphController.prototype.expandNodes = function (nodes, links) {
    if (nodes.length == 1) {
        this.expandNode(nodes[0], links, nodes[0].level + 1);
    } else {
        var newLevel = this.getMaxLevel() + 1;
        for (var n = 0; n < nodes.length; n++) {
            var node = nodes[n];
            var nLinks = [];
            for (var e = 0; e < links.length; e++) {
                if (links[e].fromNode.id == node.id || links[e].toNode.id == node.id) {
                    nLinks.push(links[e]);
                }
            }
            this.expandNode(node, nLinks, newLevel);
        }
    }
};

GraphController.prototype.expandNode = function (node, links, newLevel) {
    var index = Utility.indexOfId(visibleNodes, node.id);
    node = visibleNodes[index];

    for (var i = 0; i < links.length; i++) {
        var link = links[i];
        //TODO move contains method to Utility as all other??
        if (!(app.ni3Container.contains(visibleLinks, link))) {
            if (link.fromNode.id == node.id) {
                var to = Utility.getById(visibleNodes, link.toNode.id);
                if (to == null) {
                    to = link.toNode;
                    to.level = newLevel;
                    to.fixed = this.nodesFreezed;
                    to.justAdded = true;
                    visibleNodes.push(to);
                }
                link.target = to;
                link.source = node;
                visibleLinks.push(link);
            } else if (link.toNode.id == node.id) {
                var from = Utility.getById(visibleNodes, link.fromNode.id);
                if (from == null) {
                    from = link.fromNode;
                    from.level = newLevel;
                    from.fixed = this.nodesFreezed;
                    from.justAdded = true;
                    visibleNodes.push(from);
                }
                link.target = node;
                link.source = from;
                visibleLinks.push(link);
            }
        }
    }
};

GraphController.prototype.contract = function (node) {
    var i = 0;
    while (i < visibleLinks.length) {
        var link = visibleLinks[i];
        var from = link.source;
        var to = link.target;
        if (from.id === node.id && to.level > node.level) {
            visibleLinks.splice(i, 1);
            this.removeNode(to, false);
        } else if (to.id === node.id && from.level > node.level) {
            visibleLinks.splice(i, 1);
            this.removeNode(from, false);
        } else {
            i++;
        }
    }
    this.updateGraph();
    app.ni3Container.mapPanel.contractNodesOnMap(visibleNodes, visibleLinks);
    app.ni3Container.matrix.updateMatrixSelection();
};

GraphController.prototype.removeEdge = function (edge, fireRedraw) {
    var index = visibleLinks.indexOf(edge);
    if (index >= 0) {
        visibleLinks.splice(index, 1);
    }

    var fromNode = null;
    var toNode = null;
    visibleNodes.forEach(function (visibleNode) {
        if (visibleNode.id == edge.fromNode.id)
            fromNode = visibleNode;
        else if (visibleNode.id == edge.toNode.id)
            toNode = visibleNode;
    });
    if (fromNode)
        fromNode.parentCount--;
    if (toNode)
        toNode.childrenCount--;
    if (fireRedraw) {
        app.ni3Container.graphController.updateGraph();
        app.ni3Container.mapPanel.showNodesAndEdgesOnMap(visibleNodes, visibleLinks);
        app.ni3Container.matrix.updateMatrixSelection();
    }
};

GraphController.prototype.removeNodes = function (nodes) {
    for (var i = 0; i < nodes.length; i++) {
        this.removeNode(nodes[i], false);
    }
};

GraphController.prototype.removeEdges = function (edges) {
    for (var i = 0; i < edges.length; i++) {
        var edge = edges[i];
        var index = visibleLinks.indexOf(edge);
        if (index >= 0) {
            visibleLinks.splice(index, 1);
        }
        if (!edge.source.fixed && edge.source.level > edge.target.level) {
            this.removeNode(edge.source);
        } else if (!edge.target.fixed && edge.target.level > edge.source.level) {
            this.removeNode(edge.target);
        }
    }
};

GraphController.prototype.removeNode = function (node, fireRedraw, willDelete) {
    var i = 0;
    while (i < visibleLinks.length) {
        var link = visibleLinks[i];
        var from = link.source;
        var to = link.target;
        if (from.id === node.id || to.id === node.id) {
            if (willDelete)
                app.ni3Container.graphController.removeEdge(link);
            else
                visibleLinks.splice(i, 1);
            var rNode = from.id === node.id ? to : from;
            if (rNode.level > node.level) {
                this.removeNode(rNode);
            }
        } else {
            i++;
        }
    }
    var index = visibleNodes.indexOf(node);
    if (index >= 0) {
        visibleNodes.splice(index, 1);
    }

    if (fireRedraw) {
        app.ni3Container.graphController.updateGraph();
        app.ni3Container.mapPanel.showNodesAndEdgesOnMap(visibleNodes, visibleLinks);
        app.ni3Container.matrix.updateMatrixSelection();
    }
};

GraphController.prototype.getCounterText = function (node) {
    var count = this.getExpandCounter(node);
    return count > 0 ? "+" + count : "";
};

GraphController.prototype.getExpandCounter = function (node) {
    return node.childrenCount + node.parentCount - this.getExpandedLinkCount(node);
};

GraphController.prototype.getExpandedLinkCount = function (node) {
    var count = 0;
    visibleLinks.forEach(function (link) {
        if (link.fromNode.id === node.id || link.toNode.id === node.id) {
            count++;
        }
    });
    return count;
};

GraphController.prototype.getDisplayedNode = function (id) {
    return Utility.getById(visibleNodes, id);
};

GraphController.prototype.clearGraph = function () {
    visibleNodes = [];
    visibleLinks = [];
    this.filteredOutIds = [];
    this.updateGraph();
    this.translateToStart();
};

GraphController.prototype.makeVisibleNodesMapping = function () {
    visibleNodesByType = [];
    visibleNodes.forEach(function (node) {
        if (node.dbObject == undefined) {
            console.log("invalid db object: " + node.id);
            return;
        }
        if (visibleNodesByType[node.dbObject.entityId] == undefined) {
            visibleNodesByType[node.dbObject.entityId] = [];
        }
        visibleNodesByType[node.dbObject.entityId].push(node);
    });
};

GraphController.prototype.filterVisibleObjects = function (filter) {
    this.filteredOutIds = [];
    if (filter.filteredOut.length > 0) {
        for (var n = 0; n < visibleNodes.length; n++) {
            var node = visibleNodes[n];
            if (filter.isObjectFilteredOut(node.dbObject)) {
                this.filteredOutIds.push(node.id);
            }
        }

        for (var lk = 0; lk < visibleLinks.length; lk++) {
            var link = visibleLinks[lk];
            if (filter.isObjectFilteredOut(link.dbObject)) {
                this.filteredOutIds.push(link.id);
            }
        }

        if (this.filteredOutIds.length > 0)
            this.filterOrphans();
    }

    this.updateVisibility();
    app.ni3Container.mapPanel.updateObjectVisibilityOnMap();
    this.calculateValueUsages();
    app.ni3Container.filterPanel.updateCounts();
    app.ni3Container.matrix.updateMatrixSelection();
};

GraphController.prototype.filterOrphans = function () {
    var filtered = false;
    for (var i = 0; i < visibleLinks.length; i++) {
        var lnk = visibleLinks[i];
        if (!this.isFilteredOut(lnk.id)) {
            if (this.isFilteredOut(lnk.source.id) || this.isFilteredOut(lnk.target.id)) {
                this.filteredOutIds.push(lnk.id);
                filtered = true;
            }
        }
    }

    for (var n = 0; n < visibleNodes.length; n++) {
        var node = visibleNodes[n];
        if (!this.isFilteredOut(node.id) && !node.fixed) {
            var links = this.getLinksForNode(node);
            var hasVisibleLinks = false;
            for (var lk = 0; lk < links.length; lk++) {
                var link = links[lk];
                var rNode = link.source.id == node.id ? link.target : link.source;
                if (!this.isFilteredOut(link.id) && rNode.level < node.level) {
                    hasVisibleLinks = true;
                    break;
                }
            }
            if (!hasVisibleLinks) {
                this.filteredOutIds.push(node.id);
                filtered = true;
            }
        }
    }

    if (filtered) {
        this.filterOrphans(); // recursively filter
    }
};

GraphController.prototype.getLinksForNode = function (node) {
    var links = [];
    visibleLinks.forEach(function (link) {
        if (link.source.id == node.id || link.target.id == node.id) {
            links.push(link);
        }
    });

    return links;
};

GraphController.prototype.getMaxLevel = function () {
    var maxLevel = 0;
    visibleNodes.forEach(function (node) {
        if (node.level > maxLevel) {
            maxLevel = node.level;
        }
    });
    return maxLevel;
};

GraphController.prototype.isFilteredOut = function (id) {
    return Utility.contains(this.filteredOutIds, id);
};

GraphController.prototype.calculateValueUsages = function () {
    this.valueCounts = [];
    this.displayedValueCounts = [];
    var schema = app.ni3Model.getSchema();
    for (var e = 0; e < schema.entities.length; e++) {
        var entity = schema.entities[e];
        for (var a = 0; a < entity.attributes.length; a++) {
            var attr = entity.attributes[a];
            if (attr.canRead && attr.predefined > 0 && attr.inFilter) {
                for (var n = 0; n < visibleNodes.length; n++) {
                    var value = app.ni3Container.getValue(visibleNodes[n].dbObject, attr.id);
                    this.incrementValueUsage(visibleNodes[n].id, value, attr.multivalue);
                }

                for (var lk = 0; lk < visibleLinks.length; lk++) {
                    var val = app.ni3Container.getValue(visibleLinks[lk].dbObject, attr.id);
                    this.incrementValueUsage(visibleLinks[lk].id, val, attr.multivalue);
                }
            }
        }
    }
};

GraphController.prototype.incrementValueUsage = function (id, value, multivalue) {
    if (value != null && value != undefined) {
        if (!multivalue) {
            value = [value];
        }
        for (var i = 0; i < value.length; i++) {
            if (this.valueCounts[value[i]] == undefined) {
                this.valueCounts[value[i]] = 0;
                this.displayedValueCounts[value[i]] = 0;
            }
            this.valueCounts[value[i]]++;
            if (!this.isFilteredOut(id)) {
                this.displayedValueCounts[value[i]]++;
            }
        }
    }
};

GraphController.prototype.getDisplayedObjectCount = function (entityId) {
    var count = 0;
    var entity = app.ni3Model.getEntityById(entityId);
    if (entity.objectTypeId == 2) { // node
        for (var n = 0; n < visibleNodes.length; n++) {
            if (visibleNodes[n].objectDefinitionId == entityId && !this.isFilteredOut(visibleNodes[n].id)) {
                count++;
            }
        }
    } else {
        for (var lk = 0; lk < visibleLinks.length; lk++) {
            if (visibleLinks[lk].objectDefinitionId == entityId && !this.isFilteredOut(visibleLinks[lk].id)) {
                count++;
            }
        }
    }
    return count;
};

GraphController.prototype.getObjectCount = function (entityId) {
    var count = 0;
    var entity = app.ni3Model.getEntityById(entityId);
    if (entity.objectTypeId == 2) { // node
        for (var n = 0; n < visibleNodes.length; n++) {
            if (visibleNodes[n].objectDefinitionId == entityId) {
                count++;
            }
        }
    } else {
        for (var lk = 0; lk < visibleLinks.length; lk++) {
            if (visibleLinks[lk].objectDefinitionId == entityId) {
                count++;
            }
        }
    }
    return count;
};

GraphController.prototype.applyDataFilterToGraph = function (dataFilter) {
    var nodesToRemove = [];
    for (var n = 0; n < visibleNodes.length; n++) {
        if (dataFilter.isObjectFilteredOut(visibleNodes[n].dbObject)) {
            nodesToRemove.push(visibleNodes[n]);
        }
    }
    if (nodesToRemove.length > 0) {
        this.removeNodes(nodesToRemove)
    }

    var edgesToRemove = [];
    for (var i = 0; i < visibleLinks.length; i++) {
        if (dataFilter.isObjectFilteredOut(visibleLinks[i].dbObject)) {
            edgesToRemove.push(visibleLinks[i]);
        }
    }
    if (edgesToRemove.length > 0) {
        this.removeEdges(edgesToRemove);
    }

    app.ni3Container.reloadNodes();
};

GraphController.prototype.expandAll = function () {
    var nodesToExpand = [];
    for (var i = 0; i < visibleNodes.length; i++) {
        if (this.getExpandCounter(visibleNodes[i]) > 0) {
            nodesToExpand.push(visibleNodes[i]);
        }
    }
    if (nodesToExpand.length > 0) {
        this.expand(nodesToExpand);
    }
};

GraphController.prototype.contractAll = function () {
    var nodesToRemove = [];
    var maxLevel = this.getMaxLevel();
    if (maxLevel > 0) {
        visibleNodes.forEach(function (node) {
            if (node.level == maxLevel) {
                nodesToRemove.push(node);
            }
        });
    }

    if (nodesToRemove.length > 0) {
        this.removeNodes(nodesToRemove);
        this.updateGraph();
        contractNodesOnMap(visibleNodes, visibleLinks);
        app.ni3Container.matrix.updateMatrixSelection();
    }
};