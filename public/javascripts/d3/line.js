/**
 * Created by 1603039 on 2016/7/21.
 */
function line() {
  function chart(selection){
    selection.each(function (data) {

      var name = [],
          numbers = [];
      for (var property in data)
      {
        name.push(property);
        numbers.push(data[property]);
      }
      console.log(numbers)
      var height = 500,
          width = 500,
          padding = 50;

      var canvas = d3.select(this).append("svg")
          .attr("height",height)
          .attr("width",width);

      var x = d3.scale.ordinal()
          .domain( name )
          .rangeBands([padding , width-padding]);

      var y = d3.scale.linear()
          .domain([d3.min(numbers),d3.max(numbers)])
          .range([height-padding,padding]);

      var xAxis = d3.svg.axis()
          .scale(x)
          .orient("bottom")
          .tickSize(0)
          .innerTickSize(-(height-100))
          .tickFormat("");
      var yAxis = d3.svg.axis()
          .scale(y)
          .innerTickSize(-(width-100))
          .orient("left");

      var linefunc = d3.svg.line()
          .x(function(d,i){return x(d)})
          .y(function(d,i){return y(numbers[i]);});


      var  gap = x.rangeBand()/2;
      var line = canvas.append("path")
          .attr("d",linefunc(name))
          .attr("transform","translate("+gap+",0)")
          .attr("stroke", "steelblue")
          .attr("stroke-width", 2)
          .attr("fill", "none");

      var totalLength = line.node().getTotalLength();

      line.attr("stroke-dasharray", totalLength + " " + totalLength)
          .attr("stroke-dashoffset", totalLength)
          .transition()
          .duration(2000)
          .ease("linear")
          .attr("stroke-dashoffset", 0);

      var div = d3.select("body").append("div")
          .attr("class", "tooltip")
          .style("opacity", 0);

      canvas.append("g")
          .attr("class","axis")
          .call(xAxis)
          .attr("transform","translate(0,450)");

      canvas.append("g")
          .attr("class","axis")
          .call(yAxis)
          .attr("transform","translate(50,0)");

      var dot = canvas.selectAll("circle")
          .data(numbers)
          .enter()
          .append("g")
          .append("circle")
          .attr("cx",function(d,i){ return x(name[i]) ;})
          .attr("cy",function(d){return y(d);})
          .attr("r",4)
          .attr("transform","translate("+gap+",0)")
          .attr("fill","#EEEEEE")
          .attr("stroke","steelblue")
          .on("mouseover", function(d,i) {
            div.transition()
                .duration(200)
                .style("opacity", .9);
            div .html( name[i]+"<br/>"  + d)
                .style("left", (d3.event.pageX) + "px")
                .style("top", (d3.event.pageY - 28) + "px");
          })
          .on("mouseout", function(d) {
            div.transition()
                .duration(200)
                .style("opacity", 0);
          });



      canvas.append("text")
          .attr("text-anchor", "middle")
          .attr("transform", "translate("+ (width-padding*2) +","+(height-(padding/2))+")")
          .style("font-size","20px")
          .text("Features");
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
