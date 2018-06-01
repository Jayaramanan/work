function ChartSelectDialog() {
}

ChartSelectDialog.prototype.constructor = ChartSelectDialog;

ChartSelectDialog.prototype.createChartSelectDialog = function () {
    this.chartSelectDialog = d3.select("body")
        .append("div");
    this.chartSelectDialog.attr("id", "chartSelect")
        .attr("title", "Select chart")
        .append("ul").attr("id", "chartsList");
    this.chartSelectDialog.append("ul").attr("id", "dynamicChart")
        .append("li").append("a")
        .attr("href", "#")
        .text("Create custom chart")
        .on("click", function(){
            $(app.ni3Container.chartSelectDialog.chartSelectDialog).dialog("close");
            app.ni3Container.dynamicChartDialog.show();
        });
};

ChartSelectDialog.prototype.show = function () {
    if (this.chartSelectDialog == undefined)
        this.createChartSelectDialog();
    this.chartSelectDialog = $("#chartSelect");
    this.chartSelectDialog.dialog({
        autoOpen:false,
        height:300,
        width:300,
        modal:true,
        buttons:{
            Cancel:function () {
                $(this).dialog("close");
            }
        },
        open:function () {
            app.ni3Container.chartSelectDialog.loadChartList($(this));
        }
    });
    this.chartSelectDialog.dialog("open");
};


ChartSelectDialog.prototype.loadChartList = function (dialog) {
    var msg = new request.Charts();
    msg.action = request.Charts.Action.GET_CHARTS;
    msg.schemaId = app.ni3Model.getSchemaId();
    gateway.sendRequest(msg, "ChartsServlet", app.ni3Container.chartSelectDialog.loadChartListHandler, null, dialog);
};

ChartSelectDialog.prototype.loadChartListHandler = function (payload, dialog) {
    var protoCharts = new response.Charts();
    protoCharts.ParseFromStream(payload);
    var charts = d3.select(app.ni3Container.chartSelectDialog.chartSelectDialog[0])
        .select("#chartsList")
        .selectAll("li").data(protoCharts.charts);

    charts.enter()
        .append("li")
        .append("a").attr("href", "#")
        .on("click", function () {
            var chart = this.__data__;
            dialog.dialog("close");
            app.ni3Container.loadChart(chart.id);
        })
        .text(function (ch) {
            return ch.name;
        });
    charts.exit().remove();
};
