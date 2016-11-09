/**
 * Created by 1603039 on 2016/7/12.
 */
//function bar(data) {


//}
function bar() {
    function chart(selection){
        selection.each(function (data) {
            var xElement=[];
            var yElement=[],
                i=0;
            for (var property in data)
            {

                for (var k in data[property])
                {

                    if(i%2==0){
                        xElement.push(data[property][k])
                    }
                    else{
                        yElement.push(data[property][k])
                    }
                    i++;
                }
            }
            console.log(xElement,yElement)
            var max = d3.max(yElement, function (d) {
                return d
            });

            var width = 300,
                height = 300,
                padding = 60;

            var x = d3.scale.ordinal()
                .domain(xElement)
                .rangeRoundBands([5, width - 60], 0.1, 0.1);

            var y = d3.scale.linear()
                .domain([5, max])
                .range([height - 60, 5]);

            var xAxis = d3.svg.axis()
                .scale(x)
                .orient("bottom");

            var yAxis = d3.svg.axis()
                .scale(y)
                .orient("left")

            var canvas = d3.select(this)
                .append("svg")
                .attr("width", width+padding)
                .attr("height", height);

            var tip = d3.tip()
                .attr('class', 'd3-tip')
                .offset([-10, 0])
                .html(function (d,i) {
                    return "<strong>Median_Price:</strong> <span style='color:cornflowerblue'>" + yElement[i] + "</span>";
                });

            canvas.call(tip);

            var color = d3.scale.category20b();

            var div = d3.select("body").append("div")
                .attr("class", "tooltip")
                .style("opacity", 0);

            var bars = canvas.selectAll(".bar")
                .data(yElement)
                .enter()
                .append("rect")
                .attr("class", "bar")
                .attr("x", function (d,i) {
                    return x(xElement[i])
                })
                .attr("width", x.rangeBand())
                .attr("y", function (d) {
                    return y(d);
                })
                .attr("height", function (d) {
                    return 240 - y(d);
                })
                .attr("transform", "translate(60,0)")
                .attr("fill", function (d) {
                    return color(d)
                })
                .on('mouseover', tip.show)
                .on('mouseout', tip.hide);



            canvas.append("g")
                .attr("class", "axis")
                .attr("transform", "translate(60,240)")
                .call(xAxis);

            canvas.append("g")
                .attr("class", "axis")
                .attr("transform", "translate(60,0)")
                .call(yAxis);

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