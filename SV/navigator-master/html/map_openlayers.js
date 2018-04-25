function MapPanel() {
    this.nodeLayer = undefined;
    this.lineLayer = undefined;
    this.icons = [];
    this.map = undefined;
    this.MAX_LON = 180;
    this.MAX_LAT = 90;
}

MapPanel.prototype.initMap = function () {
    this.map = new OpenLayers.Map("mapPanel");

    this.map.numZoomLevels = app.ni3Model.getSettingValue("Tile_Server_Max_Zoom", 15);
    var tileServerUrl = "http://a.tile.openstreetmap.org/";//app.ni3Model.getSettingValue("Tile_Server_Url", "http://eu1.ni3.net/tiles/");
    var osm = new OpenLayers.Layer.OSM("OSM", [tileServerUrl + "${z}/${x}/${y}.png"]);
    osm.tileOptions.crossOriginKeyword = null;
    osm.attribution = null;
    this.map.addLayer(osm);
    var lonLat = this.getLonLat(24.9342, 60.2017);
    this.map.setCenter(lonLat, 6);

    this.lineLayer = new OpenLayers.Layer.Vector("Lines");
    this.nodeLayer = new OpenLayers.Layer.Markers("Nodes");
    this.map.addLayers([this.lineLayer, this.nodeLayer]);

    this.addNodeCreateEvent();
    this.addGeoSearchControl();
};

MapPanel.prototype.addGeoSearchControl = function () {
    var self = this;
    var control = new OpenLayers.Control();
    OpenLayers.Util.extend(control, {
        draw:function () {
            this.box = new OpenLayers.Handler.Box(control,
                {"done":this.geoSearch},
                {keyMask:OpenLayers.Handler.MOD_CTRL});
            this.box.boxDivClassName = 'olHandlerBoxSearchBox';
            this.box.activate();
        },

        geoSearch:function (bounds) {
            if (bounds.left == undefined) {
                return;
            }
            var coordsFrom = self.map.getLonLatFromPixel({x:bounds.left, y:bounds.bottom});
            var coordsTo = self.map.getLonLatFromPixel({x:bounds.right, y:bounds.top});
            var lonLatFrom = self.getGeoLonLat(coordsFrom);
            var lonLatTo = self.getGeoLonLat(coordsTo);
            app.ni3Container.advancedSearchDialog.show([lonLatFrom.lon, lonLatTo.lon, lonLatFrom.lat, lonLatTo.lat]);
        }
    });
    this.map.addControl(control);
};

MapPanel.prototype.getLonLat = function (lon, lat) {
    var lonLat = new OpenLayers.LonLat(lon, lat)
        .transform(
        new OpenLayers.Projection("EPSG:4326"), // from WGS 1984
        this.map.getProjectionObject() // to Spherical Mercator
    );
    return lonLat;
};

MapPanel.prototype.getGeoLonLat = function (mLonLat) {
    var lonLat = mLonLat.transform(
        this.map.getProjectionObject(), // from Spherical Mercator
        new OpenLayers.Projection("EPSG:4326") //to from WGS 1984
    );
    return lonLat;
};

MapPanel.prototype.fillLonLatForNodes = function (nodes) {
    for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        if (node.dbObject.lon == undefined) {
            this.fillLonLat(node.dbObject);
        }
    }
};

MapPanel.prototype.fillLonLat = function (dbObject) {
    var entity = app.ni3Model.getEntityById(dbObject.entityId);
    if (entity != undefined) {
        var attributes = entity.attributes;
        for (var i = 0; i < attributes.length; i++) {
            var attribute = attributes[i];
            if (attribute.name == "lon")
                dbObject.lon = app.ni3Container.getValue(dbObject, attribute.id);
            else if (attribute.name == "lat")
                dbObject.lat = app.ni3Container.getValue(dbObject, attribute.id);
        }
    }
};

MapPanel.prototype.removeInvisibleNodes = function (nodes) {
    var m = 0;
    while (m < this.nodeLayer.markers.length) {
        var visible = false;
        var marker = this.nodeLayer.markers[m];
        for (var i = 0; i < nodes.length; i++) {
            if (marker.node.id == nodes[i].id) {
                visible = true;
                break;
            }
        }
        if (visible) {
            m++;
        } else {
            this.nodeLayer.removeMarker(marker);
        }
    }
};

MapPanel.prototype.removeInvisibleEdges = function (edges) {
    var toRemove = [];
    for (var i = 0; i < this.lineLayer.features.length; i++) {
        var line = this.lineLayer.features[i];
        var visible = false;
        for (var e = 0; e < edges.length; e++) {
            if (line.edge.id == edges[e].id) {
                visible = true;
                break;
            }
        }
        if (!visible) {
            toRemove.push(line);
        }
    }
    this.lineLayer.removeFeatures(toRemove)
};

MapPanel.prototype.isNodeOnMap = function (node) {
    var found = false;
    for (var i = 0; i < this.nodeLayer.markers.length; i++) {
        var marker = this.nodeLayer.markers[i];
        if (marker.node != undefined && marker.node.id == node.id) {
            found = true;
            break;
        }
    }
    return found;
};

MapPanel.prototype.isEdgeOnMap = function (edge) {
    var found = false;
    for (var i = 0; i < this.lineLayer.features.length; i++) {
        var line = this.lineLayer.features[i];
        if (line.edge != undefined && line.edge.id == edge.id) {
            found = true;
            break;
        }
    }
    return found;
};

MapPanel.prototype.clearMap = function () {
    this.lineLayer.removeAllFeatures();
    this.nodeLayer.clearMarkers();
};

MapPanel.prototype.showNodesAndEdgesOnMap = function (nodes, edges) {
    this.fillLonLatForNodes(nodes);
    this.removeInvisibleEdges(edges);
    this.removeInvisibleNodes(nodes);
    this.showEdgesOnMap(edges);
    this.showNodesOnMap(nodes);
};

MapPanel.prototype.contractNodesOnMap = function (nodes, edges) {
    this.removeInvisibleNodes(nodes);
    this.removeInvisibleEdges(edges);
};

MapPanel.prototype.showEdgesOnMap = function (edges) {
    var style = {
        strokeColor:"gray",
        strokeWidth:0.5
    };

    var lines = [];
    for (var i = 0; i < edges.length; i++) {
        var edge = edges[i];
        if (this.isEdgeOnMap(edge))
            continue;

        var lonFrom = edge.source.dbObject.lon;
        var latFrom = edge.source.dbObject.lat;
        var lonTo = edge.target.dbObject.lon;
        var latTo = edge.target.dbObject.lat;
        if (lonFrom != undefined && lonTo != undefined && latFrom != undefined && latTo != undefined
            && latFrom != 0.0 && latTo != 0.0 && (lonFrom != lonTo || latFrom != latTo)) {
            var lonLatFrom = this.getLonLat(lonFrom, latFrom);
            var lonLatTo = this.getLonLat(lonTo, latTo);
            var points = new Array(
                new OpenLayers.Geometry.Point(lonLatFrom.lon, lonLatFrom.lat),
                new OpenLayers.Geometry.Point(lonLatTo.lon, lonLatTo.lat)
            );
            var line = new OpenLayers.Geometry.LineString(points);
            var lineFeature = new OpenLayers.Feature.Vector(line, null, style);
            lineFeature.edge = edge;
            lines.push(lineFeature);
        }
    }
    if (lines.length > 0) {
        this.lineLayer.addFeatures(lines);
    }
};

MapPanel.prototype.showNodesOnMap = function (nodes) {
    var size = new OpenLayers.Size(16, 16);
    var offset = new OpenLayers.Pixel(-(size.w / 2), -(size.h / 2));
    for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        if (this.isNodeOnMap(node))
            continue;

        var lon = node.dbObject.lon;
        var lat = node.dbObject.lat;
        if (lon != undefined && lat != undefined && lon != 0.0) {
            var iconName = app.ni3Container.getIconName(node.dbObject);
            var mIcon = this.getMarkerIcon(iconName, size, offset);
            var lonLat = this.getLonLat(lon, lat);
            var marker = new OpenLayers.Marker(lonLat, mIcon);
            marker.node = node;
            this.nodeLayer.addMarker(marker);
            this.addEvents(marker);
        }
    }
    var bounds = this.getBoundingRectangle(nodes);
    if (bounds != undefined) {
        this.map.zoomToExtent(bounds);
    }
};

MapPanel.prototype.getMarkerIcon = function (iconName, size, offset) {
    var mIcon = this.icons[iconName];
    if (mIcon == undefined) {
        mIcon = new OpenLayers.Icon(metaphorUrl + "/" + iconName, size, offset);
        this.icons[iconName] = mIcon;
    } else {
        mIcon = mIcon.clone();
    }
    return mIcon;
};

MapPanel.prototype.getBoundingRectangle = function (nodes) {
    var minLon = this.MAX_LON;
    var maxLon = -this.MAX_LON;
    var minLat = this.MAX_LAT;
    var maxLat = -this.MAX_LAT;
    for (var i = 0; i < nodes.length; i++) {
        var n = nodes[i];
        var lon = n.dbObject.lon;
        var lat = n.dbObject.lat;
        if (lon != undefined && lat != undefined && lat != 0.0) {
            minLon = Math.min(minLon, Math.max(lon, -this.MAX_LON));
            maxLon = Math.max(maxLon, Math.min(lon, this.MAX_LON));
            minLat = Math.min(minLat, Math.max(lat, -this.MAX_LAT));
            maxLat = Math.max(maxLat, Math.min(lat, this.MAX_LAT));
        }
    }
    var bounds;
    if (minLat < this.MAX_LAT) {
        bounds = new OpenLayers.Bounds();
        bounds.extend(this.getLonLat(minLon, minLat));
        bounds.extend(this.getLonLat(maxLon, maxLat));
    }
    return bounds;
};

MapPanel.prototype.addEvents = function (marker) {
    var self = this;
    marker.events.register('mouseover', marker, function (evt) {
        self.showTooltipOnMap(marker.node, evt.clientX, evt.clientY);
        OpenLayers.Event.stop(evt);
    });
    marker.events.register('mouseout', marker, function (evt) {
        app.ni3Container.hideTooltip();
        OpenLayers.Event.stop(evt);
    });
};

MapPanel.prototype.addNodeCreateEvent = function () {
    var map = this.map;
    var self = this;
    map.events.register("click", map, function (e) {
            if (app.ni3Model.isNodeCreateMode()) {
                var coords = map.getLonLatFromPixel(e.xy);
                var geoCoords = self.getGeoLonLat(coords);
                console.log("lat, lon: " + geoCoords.lat + "," + geoCoords.lon);
                app.ni3Container.createEditDialog.showCreateEditDialog(app.ni3Model.getSchema().entities, null, true, geoCoords);
            }
        }
    );
};

MapPanel.prototype.showTooltipOnMap = function (node, x, y) {
    tooltip.style("visibility", "visible")
        .style("top", (y - 10) + "px")
        .style("left", (x + 10) + "px")
        .html(app.ni3Container.getTooltipText(node));
};

MapPanel.prototype.updateObjectVisibilityOnMap = function () {
    var visible;
    for (var n = 0; n < this.nodeLayer.markers.length; n++) {
        var marker = this.nodeLayer.markers[n];
        visible = !app.ni3Container.graphController.isFilteredOut(marker.node.id);
        marker.display(visible);
    }

    for (var i = 0; i < this.lineLayer.features.length; i++) {
        var line = this.lineLayer.features[i];
        visible = !app.ni3Container.graphController.isFilteredOut(line.edge.id);
        line.style.display = visible ? null : "none";
        this.lineLayer.drawFeature(line, line.style);
    }
};
