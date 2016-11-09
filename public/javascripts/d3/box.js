
function boxplot(i) {
    function chart(selection){
        selection.each(function (boxplotData) {
            var j=0;
            var maxsort = [];
            var minsort = [];
            for(j=0;j<i;j++){
                maxsort[j] = d3.max(boxplotData[j][1]);
                minsort[j] = d3.min(boxplotData[j][1]);
            }
            console.log(maxsort,minsort)
            var max = d3.max(maxsort)
            var min = d3.min(minsort)

            console.log(i)

            var canvas3 = d3.select(this).append("svg")
                .attr("width", 300)
                .attr("height", 300)
                .append("g")

            var boxscale = d3.scale.linear()
                .domain([min, max])
                .range([250, 50]);
            var colors = d3.scale.category20b();

            var xboxscale = d3.scale.ordinal()
                .domain( boxplotData.map(function(d) { return d[0] } ) )
                .rangeRoundBands([5 , 250],0.7,0.3);

            var yBox = d3.svg.axis()
                .scale(boxscale)
                .orient("left");
            var xBox = d3.svg.axis()
                .scale(xboxscale)
                .orient("bottom");

            for(j=0;j<i;j++) {
                var box = canvas3.append("rect")

                    .attr("x", xboxscale(boxplotData[j][0]) + 30)
                    .attr("y", boxscale(boxplotData[j][1][3]))
                    .attr("width", xboxscale.rangeBand())
                    .attr("height", boxscale(boxplotData[j][1][1]) - boxscale(boxplotData[j][1][3]))
                    .attr("stroke", "#333333")
                    .attr("stroke-width",2)
                    .attr("fill", "#FF7060");
                console.log("Q"+j)
                var medianLine = canvas3.selectAll(".Q"+j)
                    .data([boxplotData[j][1][0], boxplotData[j][1][2], boxplotData[j][1][4]])
                    .enter()
                    .append("line")
                    .attr("class","Q"+j)
                    .attr("x1", xboxscale(boxplotData[j][0]) + 30)
                    .attr("x2", xboxscale(boxplotData[j][0]) + xboxscale.rangeBand() + 30)
                    .attr("y1", function (d) {
                        return boxscale(d);
                    })
                    .attr("y2", function (d) {
                        return boxscale(d);
                    })
                    .attr("stroke", "#333333")
                    .attr("stroke-width",2);

                var whiskers1 = canvas3.append("line")
                    .attr("x1", xboxscale(boxplotData[j][0]) + 30 + xboxscale.rangeBand() / 2)
                    .attr("x2", xboxscale(boxplotData[j][0]) + 30 + xboxscale.rangeBand() / 2)
                    .attr("y1", boxscale(boxplotData[j][1][4]))
                    .attr("y2", boxscale(boxplotData[j][1][3]))
                    .style("stroke-dasharray", ("3, 3"))
                    .attr("stroke", "#333333")
                    .attr("stroke-width",2);
                var whiskers2 = canvas3.append("line")
                    .attr("x1", xboxscale(boxplotData[j][0]) + 30 + xboxscale.rangeBand() / 2)
                    .attr("x2", xboxscale(boxplotData[j][0]) + 30 + xboxscale.rangeBand() / 2)
                    .attr("y1", boxscale(boxplotData[j][1][1]))
                    .attr("y2", boxscale(boxplotData[j][1][0]))
                    .style("stroke-dasharray", ("3, 3"))
                    .attr("stroke", "#333333")
                    .attr("stroke-width",2);
            }

            canvas3.append("g")
                .attr("class", "axis")
                .call(yBox)
                .attr("transform", "translate(30,0)")
            canvas3.append("g")
                .attr("class", "axis")
                .call(xBox)
                .attr("transform", "translate(30,260)")

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

