function GraphCommandPanel(){
}

GraphCommandPanel.prototype.constructor = GraphCommandPanel;

GraphCommandPanel.prototype.init = function(){
    this.commandPanel = d3.select("#commandPanel");
    this.commandPanel.append("div")
        .attr("title", "Node spacer")
        .attr("id", "nodeSpaceSlider");
    this.commandPanel.append("div")
        .attr("title", "Alpha slider")
        .attr("id", "alphaSlider");

    var self = this;

    $("#nodeSpaceSlider").slider({
        animate: true,
        min: 0,
        max: 10,
        orientation: "vertical",
        value: 5,
        slide: function(event, ui){
            if(self.nodeSpaceSliderHandler)
                self.nodeSpaceSliderHandler(ui.value);
        }});

    $("#alphaSlider").slider({
        animate: true,
        min: 0,
        max: 10,
        orientation: "vertical",
        value: 5,
        slide: function(event, ui){
            if(self.alphaSliderHandler)
                self.alphaSliderHandler(ui.value);
        } });
};

GraphCommandPanel.prototype.setAlphaSliderHandler = function(handler){
    this.alphaSliderHandler = handler;
};

GraphCommandPanel.prototype.setNodeSpaceSliderHandler = function(handler){
    this.nodeSpaceSliderHandler = handler;
};