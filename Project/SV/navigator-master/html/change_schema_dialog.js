function ChangeSchemaDialog() {
}

ChangeSchemaDialog.prototype.constructor = ChangeSchemaDialog;

ChangeSchemaDialog.prototype.createChangeSchemaDialog = function () {
    var schemas = app.ni3Model.getAllSchemas();
    this.changeSchemaDiv = d3.select("body")
        .append("div");
    var schemaDivs = this.changeSchemaDiv.attr("id", "changeSchemaDialog")
        .attr("title", "Change schema")
        .selectAll("div")
        .data(schemas)
        .enter()
        .append("div");
    schemaDivs.append("input")
        .attr("type", "radio")
        .attr("name", "rbSchema")
        .property("checked", function (schema) {
            return schema.id == app.ni3Model.getSchemaId();
        });
    schemaDivs.append("label")
        .text(function (schema) {
            return schema.name;
        });
};

ChangeSchemaDialog.prototype.show = function () {
    if (this.changeSchemaDialog == undefined)
        this.createChangeSchemaDialog();
    this.changeSchemaDialog = $("#changeSchemaDialog");
    this.changeSchemaDialog.dialog({
        autoOpen:false,
        height:250,
        width:300,
        modal:true,
        buttons:{
            Ok:function () {
                app.ni3Container.changeSchemaDialog.onOk();
                $(this).dialog("close");
            },
            Cancel:function () {
                $(this).dialog("close");
            }
        },
        open:function () {
            app.ni3Container.changeSchemaDialog.updateSelectedSchema();
        }
    });
    this.changeSchemaDialog.dialog("open");
};

ChangeSchemaDialog.prototype.updateSelectedSchema = function () {
    this.changeSchemaDiv.selectAll("input")
        .property("checked", function (schema) {
            return schema.id == app.ni3Model.getSchemaId();
        });
};

ChangeSchemaDialog.prototype.onOk = function () {
    var schemaId = app.ni3Model.getSchemaId();
    this.changeSchemaDiv.selectAll("input").each(function (schema) {
        if (this.checked) {
            schemaId = schema.id;
        }
    });
    app.ni3Container.changeSchema(schemaId);
};