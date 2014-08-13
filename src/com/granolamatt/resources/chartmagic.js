
var dataMax = 100;
function myChart() {

    d3.select("#layer1")
            .selectAll("path.line")
            .remove();
    d3.select("#layer1")
            .append("svg:path")
            .attr("class", "line");

    d3.select("#button_panel")
            .on("mousedown", function() {
        var Url = "/logging/data";
        d3.json(Url, ProcessRequest);
    });
}


function ProcessRequest(error, data) {
    var panelSVG = d3.select("#chart_panel");
    var panelX = ~~panelSVG.attr("x");
    var panelY = ~~panelSVG.attr("y");
    var panelW = ~~panelSVG.attr("width");
    var panelH = ~~panelSVG.attr("height");
    var barW = panelW / (data.length - 1);
    var lineFunction = d3.svg.line().x(function(d) {
        return panelX + d.x * barW;
    })
            .y(function(d) {
        return panelY + panelH - d.y / dataMax * panelH;
    })
            .interpolate("linear"); // or monotone

    d3.select("path.line")
            .transition()
            .duration(200)
            .attr("d", lineFunction(data))
            .attr("fill", "none")
            .attr("stroke", "black")
            .attr("stroke-width", "3");
}

