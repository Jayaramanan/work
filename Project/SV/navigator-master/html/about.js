// web client version
var version = "3.5.0022";

var aboutText = "Ni3 Navigator (c) version @VER<br>Copyright (c) 2006-2012 <a href=\"http://www.ni3.net\" target=\"_blank\">Ni3 AG</a>";


function AboutDialog() {
}

AboutDialog.prototype.show = function () {
    if (this.aboutDialog == undefined) {
        this.aboutDialog = d3.select("body")
            .append("div");
        this.aboutDialog.attr("id", "aboutDialog")
            .attr("title", "Info")
            .append("label")
            .html(aboutText.replace("@VER", version));

        this.aboutDialog = $("#aboutDialog");
        this.aboutDialog.dialog({
            autoOpen:false,
            height:200,
            width:300,
            modal:true,
            buttons:{
                Close:function () {
                    $(this).dialog("close");
                }
            }
        });
    }

    this.aboutDialog.dialog("open");
};