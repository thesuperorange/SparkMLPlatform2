/**
 * Created by 1603039 on 2016/7/29.
 */
function Roc() {
    function chart(selection){
        selection.each(function (data) {
            console.log(data)

            var xElement=[];
            var yElement=[],
                i=0,
                name = [];

            for (var property in data)
            {
                name.push(property)
                if(i%2!=0){
                    xElement.push(data[property])
                }
                else{
                    yElement.push(data[property])
                }
                i++;
            }
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
            var linefunc = d3.svg.line()
                .x(function(d){return x(d)})
                .y(function(d,i){return y(yElement[0][i]);})


            var line = canvas.append("path")
                .attr("d",linefunc(xElement[0]))
                //.attr("transform","translate("+gap+",0)")
                .attr("stroke", "#22AA4E")
                .attr("stroke-width", 2)
                .attr("fill", "none");
            var area = d3.svg.area()
                .x(function(d) { return x(d); })
                .y0(height-padding)
                .y1(function(d,i) { return y(yElement[0][i]); });
            canvas.append("path")
                .datum(xElement[0])
                .attr("class", "area")
                .attr("d", area)
                .attr("fill","#A2F5B2");
            canvas.selectAll("circle")
                .data(xElement[0])
                .enter()
                .append("g")
                .append("circle")
                .attr("cx",function(d){ return x(d) ;})
                .attr("cy",function(d,i){return y(yElement[0][i]);})
                .attr("r",3)
                .attr("fill","#C2F5CC")
                .attr("stroke","#22AA4E")
                .on("mouseover", function(d,i) {
                    d3.select(this).
                        transition()
                        .duration(300)
                        .attr("r",5);
                    div.transition()
                        .duration(200)
                        .style("opacity", .9);
                    div .html( "TPR:"+ d3.format(".3f")(yElement[0][i])+"<br/>" + "FPR:"  + d3.format(".3f")(d))
                        .style("left", (d3.event.pageX)+10 + "px")
                        .style("top", (d3.event.pageY - 28) + "px");
                })
                .on("mouseout", function(d) {
                    d3.select(this).
                        transition()
                        .duration(300)
                        .attr("r",3);
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
                .text(name[1])
                .attr("transform","translate(250,490)")
            canvas.append("text")
                .attr("class","title")
                .text(name[0])
                .attr("transform","translate(30,250) rotate(-90)")
        });
    }

    chart.width = function(value) {
        if (!arguments.length) return width;
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
