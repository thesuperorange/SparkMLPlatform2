/**
 * Created by 1603039 on 2016/7/29.
 */
function c2486() {
    function chart(selection){
        selection.each(function (data) {
            console.log(data)

            var xElement=[];
            var yElement=[],
                i=0,
                neuatalLine,
                name = [];

            for (var property in data)
            {
                name.push(property)
                if(i==0){
                    xElement.push(data[property])
                }
                else if(i==1){
                    yElement.push(data[property])
                }
                else if(i==2){
                    yElement.push(data[property])
                }
                else if(i==3){
                    yElement.push(data[property])
                }
                else if(i==4){
                    neuatalLine=data[property];
                }
                i++;
            }
            console.log(yElement,name)
            var height = 500,
                width = 700,
                padding = 70;
            var canvas = d3.select(this).append("svg")
                .attr("height",height)
                .attr("width",width);

            var x = d3.scale.linear()
                .domain([d3.min(xElement[0]),d3.max(xElement[0])])
                //.domain([0,1])
                .range([padding,width-padding]);

            var y = d3.scale.linear()
                .domain([d3.min(yElement[0]),d3.max(yElement[0])])
                //.domain([0,5])
                .range([height-padding,padding]);

            var xAxis = d3.svg.axis()
                .scale(x)
                .orient("bottom");

            var yAxis = d3.svg.axis()
                .scale(y)
                .orient("left");
            var color = d3.scale.ordinal()
                .domain([0, 1,2])
                .range(['orange', 'steelblue','#810015']);

            var colorfill = d3.scale.ordinal()
                .domain([0, 1,2])
                .range(['#F5EEA0', 'lightblue','#C23545']);

            var div = d3.select("body").append("div")
                .attr("class", "tooltip")
                .style("opacity", 0);


            var linefunc = d3.svg.line()
                .x(function (d, i) {
                    return x(xElement[0][i])
                })
                .y(function (d) {
                    return y(d);
                });

            var area = d3.svg.area()
                .x(function (d, i) {
                    return x(xElement[0][i]);
                })
                .y0(height - padding)
                .y1(function (d) {
                    return y(d);
                });
            for(var j=0;j<3;j++) {
                var line = canvas.append("path")
                    .attr("d", linefunc(yElement[j]))
                    .attr("stroke", function(d){return color(j);})
                    .attr("stroke-width", 2)
                    .attr("fill", "none");

                canvas.append("path")
                    .datum(yElement[j])
                    .attr("class", "area")
                    .attr("d", area)
                    .attr("fill",function(d){if(j<2)return color(j);else return "none";});
            }
            for( j=0;j<3;j++) {
                canvas.selectAll(".cir"+j)
                    .data(yElement[j])
                    .enter()
                    .append("g")
                    .attr("class","cir"+j)
                    .append("circle")
                    .attr("stroke", function(d){return color(j);})
                    .attr("cx", function (d, i) {
                        return x(xElement[0][i]);
                    })
                    .attr("cy", function (d) {
                        return y(d);
                    })
                    .attr("r", 3)
                    .attr("fill", function(d){return colorfill(j);})
                    .on("mouseover", function (d, i) {
                        d3.select(this).
                            transition()
                            .duration(300)
                            .attr("r",5);
                        div.transition()
                            .duration(200)
                            .style("opacity", .9)

                        div.html(  /*name[0] +*/d3.format(".3f")( xElement[0][i])+ "<br/>" +d3.format(".3f")(d ))
                            .style("left", (d3.event.pageX) +10+ "px")
                            .style("top", (d3.event.pageY - 28) + "px");
                    })
                    .on("mouseout", function (d) {
                        d3.select(this)
                            .transition()
                            .duration(300)
                            .attr("r", 3);
                        div.transition()
                            .duration(200)
                            .style("opacity", 0);
                    });

            }
            var legend = canvas.selectAll('.legend')
                .data(color.domain())
                .enter()
                .append('g')
                .attr('class', 'legend')
                .attr('transform', function(d, i) {

                    return 'translate(' + 500 + ',' +(5+ i*20) + ')';
                });
            legend.append('rect')
                .attr('width', 50)
                .attr('height', 3)
                .style('fill', color)
                .style('stroke', color);

            legend.append('text')
                .text(function(d,i){return name[i+1];})
                .attr("transform","translate(80,5)");

            canvas.append("g")
                .append("line")
                .style("stroke-dasharray", ("3, 3"))
                .attr("x1",x(neuatalLine))
                .attr("x2",x(neuatalLine))
                .attr("y1",padding)
                .attr("y2",height-padding)
                .attr("stroke","black")
                .attr("stroke-width",2)


            canvas.append("g")
                .attr("class","axis")
                .call(xAxis)
                .attr("transform","translate(0,430)");

            canvas.append("g")
                .attr("class","axis")
                .call(yAxis)
                .attr("transform","translate(70,0)");
            canvas.append("text")
                .attr("class","title")
                .text(name[0])
                .attr("transform","translate(290,490)")

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
