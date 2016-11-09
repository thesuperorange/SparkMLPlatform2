/**
 * Created by superorange on 9/1/16.
 */
/**
 * Created by 1603039 on 2016/7/12.
 */
//function bar(data) {


//}
function bar() {
    function chart(selection){

        selection.each(function (data) {

            function color_self(n) {
                var colores_g = ["#ff6a80", "#ffc50d", "#088da5", "#038ef0", "#ff870d",  "#66aa00", "#b82e2e", "#316395", "#994499", "#22aa99", "#aaaa11", "#6633cc", "#e67300", "#8b0707", "#651067", "#329262", "#5574a6", "#3b3eac"];
                return colores_g[n % colores_g.length];
            }

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
            var min = d3.min(yElement, function (d) {
                return d
            });

            var width = 300,
                height = 300,
                padding = 60;

            var y = d3.scale.ordinal()
                .domain(xElement)
                .rangeRoundBands([0,height],0.2);


            var x = d3.scale.linear()
                .domain([min, max])
                .range([5, width - padding]);


            var xAxis = d3.svg.axis()
                .scale(x)
                .orient("top")
                .innerTickSize(-(height))

            var yAxis = d3.svg.axis()
                .scale(y)
                .orient("left")

            var canvas = d3.select(this)
                .append("svg")
                .attr("width", width+padding)
                .attr("height", height+padding);

            var tip = d3.tip()
                .attr('class', 'd3-tip')
                .offset([0,20])
                .html(function (d,i) {
                    return  yElement[i] ;
                });

            canvas.call(tip);

            var color = d3.scale.category20b();

            var div = d3.select("body").append("div")
                .attr("class", "tooltip")
                .style("opacity", 0);


            canvas.append("g")
                .attr("class", "axis")
                .attr("transform", "translate("+padding+",20)")
                .call(xAxis);

            canvas.append("g")
                .attr("class", "axis")
                .attr("transform", "translate("+padding+",20)")
                .call(yAxis);

            var bars = canvas.selectAll(".bar")
                .data(yElement)
                .enter().append("rect")
                .attr("class", "bar")
                .attr("y", function (d,i) { return y(xElement[i]) })
                .attr("height", y.rangeBand())
                .attr("x", function (d) {     return x(Math.min(0, d));         })
                .attr("width", function (d) {    return Math.abs(x(d) - x(0));        })
                .attr("transform", "translate("+padding+",20)")
                .attr("fill", function(d,i) { return color_self(i); })
                .on('mouseover', tip.show)
                .on('mouseout', tip.hide);


            /*
             .attr("class", function(d) { return "bar bar--" + (d.value < 0 ? "negative" : "positive"); })
             .attr("x", function(d) { return x(Math.min(0, d.value)); })
             .attr("y", function(d) { return y(d.name); })
             .attr("width", function(d) { return Math.abs(x(d.value) - x(0)); })
             .attr("height", y.rangeBand());
             */




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
