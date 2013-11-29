/*
 * gw2live - GuildWars 2 Dynamic Map
 * 
 * Website: http://gw2map.com
 *
 * Copyright 2013   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
$(function() {
    var contentdiv, margin, width, height, oldwidth, x, y, color, xAxis, yAxis, line, svgcontainer, svg, date;

    contentdiv = $("#admincontainer");
    margin = {top: 20, right: 80, bottom: 30, left: 50};
    width = contentdiv.width() - margin.left - margin.right;
    height = Math.round(contentdiv.width() / 2) - margin.top - margin.bottom;
    oldwidth = contentdiv.width();

    x = d3.time.scale()
            .range([0, width]);

    y = d3.scale.log()
            .range([height, 0]);

    color = d3.scale.category10();

    xAxis = d3.svg.axis()
            .scale(x)
            .orient("bottom");

    yAxis = d3.svg.axis()
            .scale(y)
            .orient("left")
            .ticks(10, function(d, i) {
        return d;
    });

    line = d3.svg.line()
            .interpolate("basis")
            .x(function(d) {
        return x(d.timestamp);
    })
            .y(function(d) {
        return y(d.events);
    });

    svgcontainer = d3.select("#admincontainer").append("svg")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom);
    svg = svgcontainer.append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    $(window).resize(function() {
        var targetscale, newwidth, newheight;

        targetscale = contentdiv.width() / oldwidth;
        newwidth = contentdiv.width() - margin.left - margin.right;
        newheight = Math.round(contentdiv.width() / 2) - margin.top - margin.bottom;

        svg.transition()
                .duration(400)
                .attr("transform", "scale(" + targetscale + ") translate(" + margin.left + "," + margin.top + ")");
        svgcontainer.attr("width", newwidth + margin.left + margin.right)
                .attr("height", newheight + margin.top + margin.bottom);
    }).trigger("resize");

    date = new Date().getTime();
    d3.json("stats/get/eventcounter/" + date + "/200", function(error, data) {
        var eventtypes, event;

        color.domain(d3.keys(data[0].keyvalues));

        data.forEach(function(d) {
            d.timestamp = new Date(d.timestamp);
            for(var k in d.keyvalues) {
                if(d.keyvalues[k] <= 0) {
                    d.keyvalues[k] = 1;
                }
            }
        });

        eventtypes = color.domain().map(function(name) {
            return {
                name: name,
                values: data.map(function(d) {
                    return {timestamp: d.timestamp, events: +d.keyvalues[name]};
                })
            };
        });

        x.domain(d3.extent(data, function(d) {
            return d.timestamp;
        }));

        y.domain([
            1,
            d3.max(eventtypes, function(c) {
                return d3.max(c.values, function(v) {
                    return v.events;
                });
            }) * 2
        ]);

        svg.append("g")
                .attr("class", "x axis")
                .attr("transform", "translate(0," + height + ")")
                .call(xAxis);

        svg.append("g")
                .attr("class", "y axis")
                .call(yAxis)
                .append("text")
                .attr("transform", "rotate(-90)")
                .attr("y", 6)
                .attr("dy", ".71em")
                .style("text-anchor", "end")
                .text("Events (per min)");

        event = svg.selectAll(".event")
                .data(eventtypes)
                .enter().append("g")
                .attr("class", "event");

        event.append("path")
                .attr("class", "line")
                .attr("d", function(d) {
            return line(d.values);
        })
                .style("stroke", function(d) {
            return color(d.name);
        });

        event.append("text")
                .datum(function(d) {
            return {name: d.name, value: d.values[0]};
        })
                .attr("transform", function(d) {
            return "translate(" + x(d.value.timestamp) + "," + y(d.value.events) + ")";
        })
                .attr("x", 3)
                .attr("dy", ".35em")
                .text(function(d) {
            return d.name;
        });
    });
});
