/**
 * Created by 1603039 on 2016/7/19.
 */



function histogram() {

    function chart(selection){
        selection.each(function (map) {
            var height=400,
                width = 400,
                padding = 30;


            var max = map.max;
            var min = map.min;
            var x =  d3.scale.linear()
                .domain([min,max])
                .range([padding,width-padding]);




            var i = 0;

            var count = map.value.length;
            console.log(count);



            var y = d3.scale.linear()
                .domain(["0",d3.max(map.value)])
                .range([height-padding,padding]);

            var color = d3.scale.category20b();

            var xAxis = d3.svg.axis()
                .scale(x)
                .orient("bottom")
                .ticks(count/2);
            var yAxis = d3.svg.axis()
                .scale(y)
                .orient("left");

            var canvas2 = d3.select(this).append("svg")
                .attr("width",width+30)
                .attr("height",height +30)
                .append("g");

            var tip = d3.tip()
                .attr('class', 'd3-tip')
                .offset([-10, 0])
                .html(function (d) {
                    return "<strong>Numbers in group:</strong> <span style='color:cornflowerblue'>" + d + "</span>";
                });

            canvas2.call(tip);
            var bars = canvas2.selectAll(".bar")
                .data(map.value)
                .enter()
                .append("g")
                .attr("class","bar")


            bars.append("rect")
                .attr("x",function(d,i){ return (x(min+((max-min)/parseFloat(count)))-padding)*i+padding ;})
                .attr("y",function(d,i){return y(d);})
                .attr("width", x(min+((max-min)/parseFloat(count)))-padding)
                .attr("height", function(d){return height-padding-y(d);})
                .attr("fill","steelblue")
                .attr("transform","translate(10,0)")
                .on("mouseover",tip.show)
                .on("mouseout",tip.hide);
            canvas2.append("g")
                .attr("class","axis")
                .call(xAxis)
                .attr("transform","translate(10,"+(height-padding)+")");

            canvas2.append("g")
                .attr("class","axis")
                .call(yAxis)
                .attr("transform","translate(40, 0)");

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