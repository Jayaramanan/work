function insertNode(dbObject, coords) {
    var msg = new request.ObjectManagement();
    msg.action = request.ObjectManagement.Action.INSERT_NODE;
    msg.entityId = dbObject.entityId;
    msg.schemaId = app.ni3Model.getSchemaId();
    fillObjectValues(msg, dbObject, coords);

    gateway.sendRequest(msg, "ObjectManagementServlet", insertNodeHandler, null, dbObject);
}

function insertEdge(fromNode, toNode, dbObject){
    var req = new request.ObjectManagement();
    req.action = request.ObjectManagement.Action.INSERT_EDGE;
    req.entityId = dbObject.entityId;
    req.schemaId = app.ni3Model.getSchemaId();
    fillObjectValues(req, dbObject);
    req.fromId = fromNode.id;
    req.toId = toNode.id;
    showWaitCursor();
    gateway.sendRequest(req, "ObjectManagementServlet", insertEdgeHandler, null, [fromNode, toNode]);
}

function updateEdge(dbObject){
    var msg = new request.ObjectManagement();
    msg.action = request.ObjectManagement.Action.UPDATE_EDGE;
    msg.objectId = dbObject.id;
    msg.entityId = dbObject.entityId;
    msg.schemaId = app.ni3Model.getSchemaId();
    fillObjectValues(msg, dbObject);

    gateway.sendRequest(msg, "ObjectManagementServlet", updateNodeHandler, null, dbObject);
}

function updateNode(dbObject) {
    var msg = new request.ObjectManagement();
    msg.action = request.ObjectManagement.Action.UPDATE_NODE;
    msg.objectId = dbObject.id;
    msg.entityId = dbObject.entityId;
    msg.schemaId = app.ni3Model.getSchemaId();
    fillObjectValues(msg, dbObject);

    gateway.sendRequest(msg, "ObjectManagementServlet", updateNodeHandler, null, dbObject);
}

function insertNodeHandler(payload, dbObject) {
    var om = new response.ObjectManagement();
    om.ParseFromStream(payload);
    var nodeId = om.id;

    var m = [];
    m["" + dbObject.entityId] = [nodeId];
    app.ni3Container.getDbObjects(m, true);
}

function insertEdgeHandler(payload, params){
    var om = new response.ObjectManagement();
    om.ParseFromStream(payload);
    var edgeId = om.id;
    var req = new request.Graph();
    req.action = request.Graph.Action.GET_EDGES;
    req.schemaId = app.ni3Model.getSchemaId();
    req.dataFilter = app.ni3Model.getDataFilter().getAsMessage();
    req.objectIds = [edgeId];
    gateway.sendRequest(req, "GraphServlet", getEdgeHandler, null, params);
}

function getEdgeHandler(payload, params) {
    var resp = new response.Graph();
    resp.ParseFromStream(payload);
    showDefaultCursor();
    var edge = resp.edges[0];
    app.ni3Container.graphController.showEdge(edge, params[0], params[1]);
}

function updateNodeHandler(payload, dbObject) {
    var om = new response.ObjectManagement();
    om.ParseFromStream(payload);
    var m = [];
    m["" + dbObject.entityId] = [dbObject.id];
    app.ni3Container.getDbObjects(m, false);
}

function fillObjectValues(msg, dbObject, coords) {
    var entity = app.ni3Model.getEntityById(dbObject.entityId);
    var attributes = entity.attributes;
    for (var i = 0; i < attributes.length; i++) {
        var attribute = attributes[i];
        if (coords != null && coords != undefined && (attribute.name == "lat" || attribute.name == "lon")) {
            msg.attributeIds.push(attribute.id);
            msg.values.push(attribute.name == "lat" ? coords.lat : coords.lon);
        } else {
            var value = app.ni3Container.getValue(dbObject, attribute.id);
            if ((value != null && value != undefined) || attribute.editUnlock > 0) {
                msg.attributeIds.push(attribute.id);
                msg.values.push((value != null && value != undefined) ? value : "");
            }
        }
    }
}

function deleteObject(objectId, entityId){
    var msg = new request.ObjectManagement();
    msg.action = request.ObjectManagement.Action.DELETE;
    msg.objectId = objectId;
    msg.entityId = entityId;
    gateway.sendRequest(msg, "ObjectManagementServlet", null, null);
}