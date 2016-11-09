/**
 * Created by 1603039 on 2016/7/26.
 */



function scattermatrix() {
    function chart(selection){
        selection.each(function (data) {
            var width = 1000,
                size = 230,
                padding = 20;

            var x = d3.scale.linear()
                .range([padding / 2, size - padding / 2]);

            var y = d3.scale.linear()
                .range([size - padding / 2, padding / 2]);

            var xAxis = d3.svg.axis()
                .scale(x)
                .orient("bottom")
                .ticks(6);

            var yAxis = d3.svg.axis()
                .scale(y)
                .orient("left")
                .ticks(6);

            var color = d3.scale.category10();
            console.log(data)
            var name ,
                traits = [],
                numbers = [];
            for (var property in data)
            {
                traits.push(property);
                numbers.push(data[property]);
                var length = data[property].length
            }
            name = traits[0]
            traits.shift();

            var domainByTrait = {},
            //traits = d3.keys(data[0]).filter(function(d) { return d !== "species"; }),
                n = traits.length;
            console.log(name)

            traits.forEach(function(traits) {
                domainByTrait[traits] = d3.extent(data[traits], function(d) { return d; });
            });

            console.log(domainByTrait)

            xAxis.tickSize(size * n);
            yAxis.tickSize(-size * n);

            var brush = d3.svg.brush()
                .x(x)
                .y(y)
                .on("brushstart", brushstart)
                .on("brush", brushmove)
                .on("brushend", brushend);

            var svg = d3.select(this).append("svg")
                .attr("width", size * n + padding+30)
                .attr("height", size * n + padding+30)
                .append("g")
                .attr("transform", "translate(" + padding + "," + padding / 2 + ")");

            svg.selectAll(".x.axis")
                .data(traits)
                .enter().append("g")
                .attr("class", "x axs")
                .attr("transform", function(d, i) { return "translate(" + (n - i - 1) * size + ",0)"; })
                .each(function(d) { x.domain(domainByTrait[d]); d3.select(this).call(xAxis); });

            svg.selectAll(".y.axis")
                .data(traits)
                .enter().append("g")
                .attr("class", "y axs")
                .attr("transform", function(d, i) { return "translate(0," + i * size + ")"; })
                .each(function(d) { y.domain(domainByTrait[d]); d3.select(this).call(yAxis); });

            var cell = svg.selectAll(".cell")
                .data(cross(traits, traits))
                .enter().append("g")
                .attr("class", "cell")
                .attr("transform", function(d) { return "translate(" + (n - d.i - 1) * size + "," + d.j * size + ")"; })
                .each(plot);
            // Titles for the diagonal.
            cell.filter(function(d) { return d.i === d.j; }).append("text")
                .attr("x", padding)
                .attr("y", padding)
                .attr("dy", ".71em")
                .text(function(d) { return d.x; });

            cell.call(brush);

            function plot(p) {
                var cell = d3.select(this);

                x.domain(domainByTrait[p.x]);
                y.domain(domainByTrait[p.y]);

                cell.append("rect")
                    .attr("class", "frame")
                    .attr("x", padding / 2)
                    .attr("y", padding / 2)
                    .attr("width", size - padding)
                    .attr("height", size - padding);

                cell.selectAll("circle")
                    .data(data[p.x])
                    .enter().append("circle")
                    .attr("cx", function(d) {return x(d); })
                    .attr("cy", function(d,i){return y(data[p.y][i])} )
                    .attr("r", 4)
                    .style("fill", function(d,i) { return color(data[name][i]); });
            }

            var brushCell;

            function brushstart(p) {
                if (brushCell !== this) {
                    d3.select(brushCell).call(brush.clear());
                    x.domain(domainByTrait[p.x]);
                    y.domain(domainByTrait[p.y]);
                    brushCell = this;
                }
            }

            function brushmove(p) {
                var e = brush.extent();
                var k=0,
                    m=0;
                svg.selectAll("circle").classed("hidden", function(d,i) {
                    if(i%length==0){
                        k++;
                        m=k*length;
                    }
                    var l=length+(i-m);
                    return e[0][0] > data[p.x][l] || data[p.x][l] > e[1][0]
                        || e[0][1] > data[p.y][l] || data[p.y][l] > e[1][1];
                });
            }

            // If the brush is empty, select all circles.
            function brushend() {
                if (brush.empty()) svg.selectAll(".hidden").classed("hidden", false);
            }


            function cross(a, b) {
                var c = [], n = a.length, m = b.length, i, j;
                for (i = -1; ++i < n;) for (j = -1; ++j < m;) c.push({x: a[i], i: i, y: b[j], j: j});

                return c;
            }

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
/**
 * Created by superorange on 8/4/16.
 */
