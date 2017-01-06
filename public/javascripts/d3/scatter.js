/**
 * Created by user on 2016/8/31.
 */
/**
 * Created by 1603039 on 2016/7/21.
 */

/*$(document).ready(function()
 {
 var line = 0,
 diag = 0;

 $( ".line" ).click(function() {
 if(line%2==0) {
 d3.select("#scatter").select("svg").append("line")
 .attr("class","penis")
 .attr("x1", 50)
 .attr("x2", 450)
 .attr("y1", 237)
 .attr("y2", 237)
 .attr("stroke", "#666666");
 }
 if(line%2==1) {
 $(".penis").remove();
 }
 line++;
 });
 $( ".diag" ).click(function() {
 if(diag%2==0) {
 d3.select("#scatter").select("svg").append("line")
 .attr("class","penis")
 .attr("x1", 50)
 .attr("x2", 450)
 .attr("y1", 450)
 .attr("y2", 50)
 .attr("stroke", "#666666");
 }
 if(diag%2==1) {
 $(".penis").remove();
 }
 diag++;
 });

 });*/
function scatter(tag) {


    function chart(selection){
        selection.each(function (data) {

            var xElement=[];
            var yElement=[],
                i=0,
                name = [];

            for (var property in data)
            {
                name.push(property)
                if(i%2==0){
                    xElement.push(data[property])
                }
                else{
                    yElement.push(data[property])
                }
                i++;
                console.log(xElement,yElement)
            }
            console.log(xElement,yElement)
            var height = 500,
                width = 500,
                padding = 50;
            var canvas = d3.select(this).append("svg")
                .attr("height",height)
                .attr("width",width);

            var x = d3.scale.linear()
                .domain([d3.min(xElement[0]),d3.max(xElement[0])])
                .range([padding,width-padding]);

            var y = d3.scale.linear()
                .domain([d3.min(yElement[0]),d3.max(yElement[0])])
                .range([height-padding,padding]);

            var xAxis = d3.svg.axis()
                .scale(x)
                .orient("bottom");

            var yAxis = d3.svg.axis()
                .scale(y)
                .orient("left");
            var div = d3.select("body").append("div")
                .attr("class", "tooltip")
                .style("opacity", 0);

            canvas.selectAll("circle")
                .data(xElement[0])
                .enter()
                .append("g")
                .append("circle")
                .attr("cx",function(d,i){console.log(d); return x(d) ;})
                .attr("cy",function(d,i){return y(yElement[0][i]);})
                .attr("r",4)
                .attr("fill","#d0191e")

                .on("mouseover", function(d,i) {
                    d3.select(this).
                        transition()
                        .duration(300)
                        .attr("r",7);
                    div.transition()
                        .duration(200)
                        .style("opacity", .9);
                    div .html( "res:"+yElement[0][i]+"<br/>" + "sat:"  + d)
                        .style("left", (d3.event.pageX) + "px")
                        .style("top", (d3.event.pageY - 28) + "px");
                })
                .on("mouseout", function(d) {
                    d3.select(this).
                        transition()
                        .duration(300)
                        .attr("r",4);
                    div.transition()
                        .duration(200)
                        .style("opacity", 0);
                });


            canvas.append("g")
                .attr("class","axis")
                .call(xAxis)
                .attr("transform","translate(0,450)");

            canvas.append("g")
                .attr("class","axis")
                .call(yAxis)
                .attr("transform","translate(50,0)");
            canvas.append("text")
                .attr("class","title")
                .text(name[0])
                .attr("transform","translate(250,490)")
            canvas.append("text")
                .attr("class","title")
                .text(name[1])
                .attr("transform","translate(30,250) rotate(-90)")
            if(tag==1) {
                d3.select(this).select("svg").append("line")
                    .attr("x1", 50)
                    .attr("x2", 450)
                    .attr("y1", 450)
                    .attr("y2", 50)
                    .attr("stroke", "#ffc1b2")
                    .attr("stroke-width",2);
            }
            if(tag==2) {
                d3.select(this).select("svg").append("line")
                    .attr("x1", 50)
                    .attr("x2", 450)
                    .attr("y1", 237)
                    .attr("y2", 237)
                    .attr("stroke", "#ffc1b2")
                    .attr("stroke-width",2);
            }

        });
    }

    chart.width = function(value) {
        if (!arguments.length) return height;
        width = value;
        return chart;
    };

    chart.height = function(value) {
        if (!arguments.length) return margin;
        height = value;
        return chart;
    };

    chart.barPadding = function(value) {
        if (!arguments.length) return barPadding;
        barPadding = value;
        return chart;
    };

    chart.fillColor = function(value) {
        if (!arguments.length) return fillColor;
        fillColor = value;
        return chart;
    };

    return chart;
}
