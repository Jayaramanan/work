function EntitiesSelectionDialog(okHandler, cancelHandler, showSecondNode) {
    this.okHandler = okHandler;
    this.cancelHandler = cancelHandler;
    this.showSecondNode = showSecondNode;
}

EntitiesSelectionDialog.prototype.constructor = EntitiesSelectionDialog;

EntitiesSelectionDialog.prototype.show = function () {
    if (!this.isCreated())
        this.createEntitiesSelectionDialog();
    this.callCancel = true;
    var panel = this;
    this.entitiesSelectionDialog = $("#entitiesSelectionDialog");
    this.entitiesSelectionDialog.dialog({
        autoOpen:false,
        height:200,
        width:450,
        modal:true,
        buttons:{
            Ok:function () {
                var nodeEntities = [];
                d3.select(panel.entitiesSelectionDialog[0]).selectAll("select.nodeEntitySelector").each(function () {
                    nodeEntities.push(this.childNodes[this.selectedIndex].__data__);
                });
                var edgeCombo = $("select.edgeEntitySelector")[0];
                var edgeEntity = edgeCombo.childNodes[edgeCombo.selectedIndex].__data__;
                panel.callCancel = false;
                panel.okHandler(nodeEntities[0], edgeEntity, nodeEntities[1]);
                $(this).dialog("close");
            },
            Cancel:function () {
                panel.cancelHandler();
                $(this).dialog("close");
            }
        },
        close:function () {
            if (panel.callCancel)
                panel.cancelHandler();
        }
    });
    d3.select(this.entitiesSelectionDialog[0]).selectAll("select.nodeEntitySelector")[0][1].style.display = this.showSecondNode ? null : "none";
    this.entitiesSelectionDialog.dialog("open");
};

EntitiesSelectionDialog.prototype.isCreated = function () {
    var dlg = d3.select("#entitiesSelectionDialog");
    return !dlg.empty();
};

EntitiesSelectionDialog.prototype.createEntitiesSelectionDialog = function () {
    var layout = d3.select("body")
        .append("div")
        .attr("id", "entitiesSelectionDialog")
        .append("table")
        .append("tr");
    layout.append("td").selectAll("select")
        .data(["nodeEntitySelector", "edgeEntitySelector", "nodeEntitySelector"])
        .enter()
        .append("select")
        .attr("class", function (id) {
            return id;
        });
    var nodeEntities = app.ni3Model.getNodeEntities();
    layout.selectAll("select.nodeEntitySelector").selectAll("option")
        .data(nodeEntities)
        .enter()
        .append("option")
        .attr("value", function (ent) {
            return ent.id;
        })
        .text(function (ent) {
            return ent.name;
        });
    var edgeEntities = app.ni3Model.getEdgeEntities();
    layout.selectAll("select.edgeEntitySelector").selectAll("option")
        .data(edgeEntities)
        .enter()
        .append("option")
        .attr("value", function (ent) {
            return ent.id;
        })
        .text(function (ent) {
            return ent.name;
        });
};