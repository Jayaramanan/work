var nodeLayer;
var lineLayer;
var icons = [];
var map;
const MAX_LON = 180;
const MAX_LAT = 90;

function initMap() {
    map = new L.Map('mapPanel');

    var osm = new L.TileLayer('http://a.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution:null,
        maxZoom:15
    });

    // add the layer to the map, set the view to a given place and zoom
    map.addLayer(osm).setView(new L.LatLng(51.505, -0.09), 6);

    nodeLayer = new L.layerGroup();
    lineLayer = new L.layerGroup();
    map.addLayer(nodeLayer);
    map.addLayer(lineLayer);
    nodeLayer.addLayer(new L.Marker(new L.LatLng(39.77, -105.23)).bindPopup("This is Golden, CO."));
}

function fillLonLatForNodes(nodes) {
    for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        if (node.dbObject.lon == undefined) {
            fillLonLat(node.dbObject);
        }
    }
}

function fillLonLat(dbObject) {
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
}

function clearMap() {
    lineLayer.clearLayers();
    nodeLayer.clearLayers();
}

function showNodesAndEdgesOnMap(nodes, edges) {
    fillLonLatForNodes(nodes);
    clearMap();
    showEdgesOnMap(edges);
    showNodesOnMap(nodes);
}

function contractNodesOnMap(nodes, edges){
    showNodesAndEdgesOnMap(nodes, edges);
}

function showEdgesOnMap(edges) {
    for (var i = 0; i < edges.length; i++) {
        var edge = edges[i];
        var lonFrom = edge.source.dbObject.lon;
        var latFrom = edge.source.dbObject.lat;
        var lonTo = edge.target.dbObject.lon;
        var latTo = edge.target.dbObject.lat;
        if (lonFrom != undefined && lonTo != undefined && latFrom != undefined && latTo != undefined
            && latFrom != 0.0 && latTo != 0.0 && (lonFrom != lonTo || latFrom != latTo)) {
            var points = new Array(
                new L.LatLng(latFrom, lonFrom),
                new L.LatLng(latTo, lonTo)
            );
            var line = new L.Polyline(points, {color:'gray', weight: 1, opacity: 0.8});
            line.edge = edge;
            lineLayer.addLayer(line);
        }
    }
}

function showNodesOnMap(nodes) {
    for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        var lon = node.dbObject.lon;
        var lat = node.dbObject.lat;
        if (lon != undefined && lat != undefined && lat != 0.0) {
            var iconName = app.ni3Container.getIconName(node.dbObject);
            var olIcon = icons[iconName];
            if (olIcon == undefined) {
                olIcon = new L.Icon({
                    iconUrl:metaphorUrl + "/" + iconName,
                    iconSize:new L.Point(16, 16)});
//                olIcon = new LeafIcon(metaphorUrl + "/" + iconName);
                icons[iconName] = olIcon;
            }
            //var lonLat = getLonLat(lon, lat);
            var marker = new L.Marker(new L.LatLng(lat, lon), {icon:olIcon});
            marker.node = node;
            nodeLayer.addLayer(marker);
            addEvents(marker);
        }
    }
    var bounds = getBoundingRectangle(nodes);
    if (bounds != undefined) {
        map.fitBounds(bounds);
    }
}

function getBoundingRectangle(nodes) {
    var minLon = MAX_LON;
    var maxLon = -MAX_LON;
    var minLat = MAX_LAT;
    var maxLat = -MAX_LAT;
    for (var i = 0; i < nodes.length; i++) {
        var n = nodes[i];
        var lon = n.dbObject.lon;
        var lat = n.dbObject.lat;
        if (lon != undefined && lat != undefined && lat != 0.0) {
            minLon = Math.min(minLon, Math.max(lon, -MAX_LON));
            maxLon = Math.max(maxLon, Math.min(lon, MAX_LON));
            minLat = Math.min(minLat, Math.max(lat, -MAX_LAT));
            maxLat = Math.max(maxLat, Math.min(lat, MAX_LAT));
        }
    }
    var bounds;
    if (minLat < MAX_LAT) {
        bounds = new L.LatLngBounds(new L.latLng(minLat, minLon), new L.latLng(maxLat, maxLon));
    }
    return bounds;
}

function addEvents(marker) {
    marker.on('mouseover', function (evt) {
        showTooltipOnMap(marker.node, evt.originalEvent.clientX, evt.originalEvent.clientY);
    });
    marker.on('mouseout', function (evt) {
        app.ni3Container.hideTooltip();
    });
}

function showTooltipOnMap(node, x, y) {
    tooltip.style("visibility", "visible")
        .style("top", (y - 10) + "px")
        .style("left", (x + 10) + "px")
        .html(app.ni3Container.getTooltipText(node));
}



