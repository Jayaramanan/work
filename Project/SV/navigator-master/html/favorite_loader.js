function FavoriteLoader(favorite){
    this.favorite = favorite;

    if (window.DOMParser)
    {
        var parser=new DOMParser();
        this.xmlDoc=parser.parseFromString(favorite.data,"text/xml");
        this.ok = !(this.xmlDoc.childNodes[0].nodeType == 7 /*processing instruction node*/
            && this.xmlDoc.childNodes[1].tagName == "parsererror");
    }
    else // Internet Explorer
    {
        this.ms = true;
        this.xmlDoc=new ActiveXObject("Microsoft.XMLDOM");
        this.xmlDoc.async=false;
        this.ok = this.xmlDoc.loadXML(favorite.data);
    }
    if(!this.ok)
        return;
    this.parseHeader();
    if(this.ni3.Mode == 1)
        this.parseGraph();
    else if(this.ni3.Mode == 2)
        this.parseSearchSections();
}

FavoriteLoader.prototype.constructor = FavoriteLoader;

FavoriteLoader.prototype.isOk = function(){
    return this.ok;
};

FavoriteLoader.prototype.parseHeader = function(){
    this.ni3 = {};
    var ni3Node = this.xmlDoc.childNodes[0];
    for(var i = 0; i < ni3Node.attributes.length; i++){
        var attr = ni3Node.attributes[i];
        this.ni3[attr.nodeName] = attr.nodeValue;
    }
};

FavoriteLoader.prototype.parseSearchSections = function () {
    this.ni3.query = {};
    this.ni3.query.sections = [];

    var queryTag = this.xmlDoc.childNodes[0].getElementsByTagName("Query")[0];
    for(var i = 0; i < queryTag.attributes.length; i++){
        var attr = queryTag.attributes[i];
        this.ni3.query[attr.name] = attr.value;
    }

    var sectionTags = queryTag.getElementsByTagName("Section");
    for(i = 0; i < sectionTags.length; i++){
        var sectionTag = sectionTags[i];
        var entityId = +this.getAttribute(sectionTag, "EntityID");
        var conditions = [];
        var conditionTags = sectionTag.getElementsByTagName("Condition");
        for(var j = 0; j < conditionTags.length; j++){
            var conditionTag = conditionTags[j];
            conditions.push({
                attributeId: +this.getAttribute(conditionTag, "AttrID"),
                operation: this.getAttribute(conditionTag, "Operation"),
                value: this.getAttribute(conditionTag, "Value")
            });
        }
        this.ni3.query.sections.push({entityId: entityId, conditions: conditions});
    }
};

FavoriteLoader.prototype.parseGraph = function () {
    this.ni3.graph = {};
    var graphNode = this.xmlDoc.childNodes[0].getElementsByTagName("Graph")[0];
    for(var i = 0; i < graphNode.attributes.length; i++){
        var attr = graphNode.attributes[i];
        this.ni3.graph[attr.nodeName] = attr.nodeValue;
    }
    this.ni3.graph.nodeIdList = this.getAsArray(graphNode, "Nodes", "List");
    this.ni3.graph.manuallyExpandedIdList = this.getAsArray(graphNode, "ExpandedManualy", "List");
    this.ni3.graph.edgeIdList = this.getAsArray(graphNode, "Edges", "List");
    this.ni3.graph.rootIdList = this.getAsArray(graphNode, "Roots", "List");
};

FavoriteLoader.prototype.getAsArray = function (node, tagName, attributeName) {
    var listString = this.getAttribute(node.getElementsByTagName(tagName)[0], attributeName);
    if(listString == "")
        return [];
    else
        return listString.split(",");
};

FavoriteLoader.prototype.getAttribute = function(tag, name){
    for(var i = 0; i < tag.attributes.length; i++){
        var attr = tag.attributes[i];
        if(attr.nodeName == name){
            return attr.value;
        }
    }
    return null;
};


FavoriteLoader.prototype.getMode = function(){
    return +this.ni3["Mode"];
};

FavoriteLoader.prototype.getNodeIdList = function(){
    return this.ni3.graph.nodeIdList;
};

FavoriteLoader.prototype.getManuallyExpandedIdList = function(){
    return this.ni3.graph.manuallyExpandedIdList;
};

FavoriteLoader.prototype.getEdgeIdList = function(){
    return this.ni3.graph.edgeIdList;
};

FavoriteLoader.prototype.getRootIdList = function(){
    return this.ni3.graph.rootIdList;
};