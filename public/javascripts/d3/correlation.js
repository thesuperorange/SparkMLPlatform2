/**
 * Created by superorange on 8/22/16.
 */
/**
 * Created by 1603039 on 2016/7/19.
 */

function heatmap() {

    function chart(selection){
        selection.each(function (data) {


            var h = 0,
                w = 0,
                tick = 0,
                height ,
                width ,
                padding = 20;

            var s = [],name = [];
            for (var property in data)
            {
                name.push(property);
                s.push(data[property]);
                tick++;
            }

            if(tick<15) {
                h = tick*50;
                w = tick*50;
                height = h-padding;
                width = w-padding;
                for(var i =0;i<tick;i++) {
                    s[i].splice(0,i+1);
                }

                var color = d3.scale.linear()
                    .domain([-1, 0,1])
                    .range(['blue','white', 'red']);
                var div = d3.select("body").append("div")
                    .attr("class", "tooltip")
                    .style("opacity", 0);

                var svg = d3.select(this).append("svg")
                    .attr("height",h+20)
                    .attr("width",w+20);

                var gradient = svg
                    .append("linearGradient")
                    .attr("y1", padding)
                    .attr("y2", height)
                    .attr("x1", "0")
                    .attr("x2", "0")
                    .attr("id", "gradient")
                    .attr("gradientUnits", "userSpaceOnUse");

                gradient.append("stop")
                    .attr("offset", "0")
                    .attr("stop-color", "red");

                gradient.append("stop")
                    .attr("offset", "0.5")
                    .attr("stop-color", "white");

                gradient.append("stop")
                    .attr("offset", "1")
                    .attr("stop-color", "blue");


                var text = svg.append("g").selectAll("text")
                    .data(name)
                    .enter()
                    .append("text")
                    .attr("x", function (d, i) {
                        return ((w - padding) / tick) * i + 5
                    })
                    .attr("y", function (d, i) {
                        return ((h) / tick) * i + ((h) / tick) / 2
                    })
                    .text(function (d) {
                        return d
                    });

                for (var l = 0; l < tick; l++) {
                    var numbers = svg.append("g").selectAll(".numbers")
                        .data(s[l])
                        .enter()
                        .append("text")
                        .attr("class", "numbers")

                        .attr("x", function (d, i) {
                            console.log(d);
                            return ((w - padding) / tick) * i + ((w - padding) / tick) * (l + 1) + 5

                        })
                        .attr("y", function (d, i) {

                            return ((h) / tick) * l + ((h) / tick) / 2
                        })
                        .text(function (d) {
                            return d
                        });
                }
                for (l = 0; l < tick; l++) {
                    var circle = svg.selectAll(".balls")
                        .data(s[l])
                        .enter()
                        .append("circle")
                        .attr("l",l)
                        .attr("cy", function (d, i) {
                            return (h / tick) * i + (h / tick) * 1.5 + (h / tick) * l
                        })
                        .attr("cx", function (d, i) {
                            return ((w - padding) / tick) * l + ((w - padding) / tick) / 2;
                        })
                        .attr("r", function (d) {
                            return Math.abs(d) * 10
                        })
                        .attr("fill", function (d) {
                            return color(d)
                        })
                        .on("mouseover", function (d, i) {
                            var dex = d3.select(this).attr("l")
                            div.transition()
                                .duration(200)
                                .style("opacity", .9);
                            div.html(d + "<br>" + name[dex] +" " + name[parseInt(dex)+i+1])
                                .style("left", (d3.event.pageX) + "px")
                                .style("top", (d3.event.pageY - 28) + "px");
                        })
                        .on("mouseout", function (d) {
                            div.transition()
                                .duration(200)
                                .style("opacity", 0);
                        });
                }
            }
            else{
                h = 500+tick;
                w = 500+tick;
                height = h-padding;
                width = w-padding;
                for(var i =0;i<tick;i++) {
                    s[i].splice(0,i+1);
                }

                var color = d3.scale.linear()
                    .domain([-1, 0,1])
                    .range(['blue','white', 'red']);
                var div = d3.select("body").append("div")
                    .attr("class", "tooltip")
                    .style("opacity", 0);

                var svg = d3.select(this).append("svg")
                    .attr("height",h+20)
                    .attr("width",w+20);

                var gradient = svg
                    .append("linearGradient")
                    .attr("y1", padding)
                    .attr("y2", height)
                    .attr("x1", "0")
                    .attr("x2", "0")
                    .attr("id", "gradient")
                    .attr("gradientUnits", "userSpaceOnUse");

                gradient.append("stop")
                    .attr("offset", "0")
                    .attr("stop-color", "red");

                gradient.append("stop")
                    .attr("offset", "0.5")
                    .attr("stop-color", "white");

                gradient.append("stop")
                    .attr("offset", "1")
                    .attr("stop-color", "blue");


                for (var l = 0; l < tick; l++) {
                    var circle = svg.selectAll(".balls")
                        .data(s[l])
                        .enter()
                        .append("circle")
                        .attr("l",l)
                        .attr("cy", function (d, i) {
                            return ((h) / tick) * l + ((h) / tick) / 2
                        })
                        .attr("cx", function (d, i) {
                            return ((w - padding) / tick) * i + ((w - padding) / tick) * (l + 1) + (w-padding)/(2*tick)
                        })
                        .attr("r", (w-padding)/(2*tick))
                        .attr("fill", function (d) {
                            return color(d)
                        })
                        .on("mouseover", function (d, i) {
                            var dex = d3.select(this).attr("l")
                            div.transition()
                                .duration(200)
                                .style("opacity", .9);
                            div.html(d + "<br>" + name[dex] +" " + name[parseInt(dex)+i+1])
                                .style("left", (d3.event.pageX) + "px")
                                .style("top", (d3.event.pageY - 28) + "px");
                        })
                        .on("mouseout", function (d) {
                            div.transition()
                                .duration(200)
                                .style("opacity", 0);
                        });
                }
                var count = 0;
                for (l = 0; l < tick; l++) {
                    var circle = svg.selectAll(".balls")
                        .data(s[l])
                        .enter()
                        .append("circle")
                        .attr("l",l)
                        .attr("cy", function (d, i) {
                            return (h / tick) * i + (h / tick) * 1.5 + (h / tick) * l
                        })
                        .attr("cx", function (d, i) {
                            return ((w - padding) / tick) * l + ((w - padding) / tick) / 2;
                        })
                        .attr("r", (w - padding) / (2 * tick))
                        .attr("fill", function (d) {
                            return color(d)
                        })
                        .on("mouseover", function (d, i) {

                            var dex = d3.select(this).attr("l")
                            div.transition()
                                .duration(200)
                                .style("opacity", .9);
                            div.html(d + "<br>" + name[dex] +" " + name[parseInt(dex)+i+1])
                                .style("left", (d3.event.pageX) + "px")
                                .style("top", (d3.event.pageY - 28) + "px");
                        })
                        .on("mouseout", function (d) {
                            div.transition()
                                .duration(200)
                                .style("opacity", 0);
                        });
                    count++;
                }
            }
            var yax = d3.scale.linear()
                .domain([-1,1])
                .range([height,padding]);
            var yAxis = d3.svg.axis()
                .scale(yax)
                .orient("right");

            var x= d3.scale.linear()
                .domain([0,tick])
                .range([0, width]);

            var y= d3.scale.linear()
                .domain([0,tick])
                .range([h,0]);

            var sideBar = svg.append("g")
                .append("rect")
                .attr("x",width)
                .attr("y",padding)
                .attr("width",padding/2)
                .attr("height",height-padding)
                .attr("fill","none")
                .attr("stroke","lightgrey")
                .attr("fill","url(#gradient)");

            svg.append("g")
                .attr("class", "gradian")
                .call(yAxis)
                .attr("transform", "translate("+width+",0)");

            function make_x_axis() {
                return d3.svg.axis()
                    .scale(x)
                    .orient("bottom")
                    .ticks(tick)
            }

            function make_y_axis() {
                return d3.svg.axis()
                    .scale(y)
                    .orient("left")
                    .ticks(tick)
            }

            svg.append("g")
                .attr("class", "grid")
                .attr("transform", "translate(0," + h + ")")
                .call(make_x_axis()
                    .tickSize(-h, 0, 0)
                    .tickFormat("")
                );

            svg.append("g")
                .attr("class", "grid")
                .attr("transform","translate(0,0)")
                .call(make_y_axis()
                    .tickSize(-width, 0, 0)
                    .tickFormat("")
                )
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