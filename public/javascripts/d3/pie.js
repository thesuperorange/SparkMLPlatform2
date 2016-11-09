/**
 * Created by 1603039 on 2016/7/12.
 */
function pie() {


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


            var width = 300,
                height = 300;

            var canvas1 = d3.select(this)
                .append("svg")
                .attr("width",width)
                .attr("height",height);

            var group = canvas1.append("g")
                .attr("transform","translate(150,150)");

            var r = 100;

            var p = Math.PI * 2;

            var arc = d3.svg.arc()
                .innerRadius(0)
                .outerRadius(r);

            var pie = d3.layout.pie()
                .value(function(d) { return d});

            var tip = d3.tip()
                .attr('class', 'd3-tip')
                .offset([-10, 0])
                .html(function (d,i) {
                    return "<strong>Median_Price:</strong> <span style='color:cornflowerblue'>" + d.value + "</span>";
                });

            canvas1.call(tip);

            var arcs = group.selectAll(".arc")
                .data(pie(yElement))
                .enter()
                .append("g")
                .attr("class","arc");


            var colors = d3.scale.category20()



            arcs.append("path")
                // .attr("transform","translate(250,250)")
                .attr("d",arc)
                .attr("fill",function (d){return colors(d.value); } )
                .on("mouseover",function(d){
                    tip.show(d)
                    var target=d3.select(this);
                    var d=target.datum();
                    var dgre=(d.endAngle-d.startAngle)/2+d.startAngle;
                    var dis=width/((width+40)/20); //distance
                    var x=d3.round(Math.sin(dgre),15)*dis;
                    var y=-d3.round(Math.cos(dgre),15)*dis;

                    target.transition()
                        .duration(500)
                        .attr("transform", "translate("+(x)+", "+(y)+")")
                        .ease("bounce") ;
                })
                .on("mouseout",function(d){
                    tip.hide(d)
                    var dgre=(d.endAngle-d.startAngle)/2+d.startAngle;
                    var dis=width/((width+40)/20); //distance
                    var x=-d3.round(Math.sin(dgre),15)*dis;
                    var y=d3.round(Math.cos(dgre),15)*dis;
                    d3.select(this)
                        .transition()
                        .duration(500)
                        .attr("transform","translate(0,0)");
                    //back center
                })
            //.on('mouseout', tip.hide);

            arcs.append("text")
                .attr("transform",function(d){ return "translate(" + arc.centroid(d) + ")" ;   })

                //.attr("transform",("translate(160,-35)"))
                .attr("text-anchor","middle")
                .attr("fill","white")
                .text(function(d,i){return xElement[i]; } );

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