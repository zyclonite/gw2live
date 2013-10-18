/*
 * gw2live - GuildWars 2 Dynamic Map
 * 
 * Website: http://gw2map.com
 *
 * Copyright 2013   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Manuel Bauer
 */
var Livemode, configuration, gw2map, subscription, browserEvents, myDate;

myDate = new Object();
myDate.offset = 0;

myDate.calibrate = function() {
    return $.getJSON(configuration.rootUrl + "rest/servertime")
            .done(function(t) {
        myDate.set(t);
    });
};
myDate.set = function(UTC_msec) {
    if (!isFinite(UTC_msec))
        return;
    myDate.offset = UTC_msec - new Date().valueOf();
};
myDate.serverNow = function() {
    var time = new Date();
    time.setTime(myDate.offset + time.getTime());
    return time;
};
myDate.localTime = function(serverTimestamp) {
    var time = new Date(serverTimestamp);
    time.setTime(time.getTime() - myDate.offset);
    return time;
};
myDate.localNow = function () {
    return new Date();
};


browserEvents = new Object();
browserEvents.timers = [];
browserEvents.flush = function() {
    browserEvents.timers.forEach(function(t) {
        clearTimeout(t);
    });
};
browserEvents.setLoading = function() {
    $("body").append("<div class=\"modal-backdrop fade in\"><img class=\"loader\" src=\"img/loading.gif\"/></div>");
};
browserEvents.setNotLoading = function() {
    $('.modal-backdrop').remove();
};

subscription = new Object();
subscription.sock = null;
subscription.connected = false;
// CONSTANTS
Livemode = new Object();
Livemode.PVE = 0;
Livemode.WVW = 1;

STATE = new Object();
STATE.STANDBY = 0;
STATE.INITIATING = 1;
STATE.READY = 2;

// configuration 
configuration = new Object();
configuration.COUTNRATIO = 0.7;
configuration.s_width = 1366;
configuration.s_height = 990;
configuration.container = "#body";
configuration.rootUrl = "http://gw2map.com/";
configuration.streamUrl = "http://gw2map.com/stream";
configuration.wikiUrl = "/proxy.ashx?source=1&param=";
configuration.wikiEnabled = false;
configuration.currentMode = Livemode.WVW;
configuration.worldId = 2006;
configuration.lang = "en";
configuration.protectiontime = 5 * 60 * 1000;
configuration.channel = null;
configuration.nickname = null;


gw2map = new Object();
gw2map.state = STATE.STANDBY;
gw2map.LiveModi = Livemode;

gw2map.init = function(container, worldId, mode, lang, nickname, channel) {

    // nasty globals... subject to later refactoring
    var mapWvw, mapPve, eventbox, detailsPanel, application, wvwEvents, pveEvents, worlds, centered, moveTorch;
  
    moveTorch = function(d) {
        var dragTarget;

        dragTarget = d3.select("#torch_container_" + d.id);

        d.x += d3.mouse(dragTarget.node())[0];
        d.y += d3.mouse(dragTarget.node())[1];

        dragTarget.attr("transform", "translate(" + d.x + "," + d.y + ")");
    };

    resize = function() {
        var targetscale;

        targetscale = $(window).width() / configuration.s_width;

        d3.select("#stage").transition()
                .duration(400)
                .attr("transform", "scale(" + targetscale + ")");

        d3.select("#main")
                .attr("width", $(window).width())
                .attr("height", Math.round($(window).width() / configuration.s_width * configuration.s_height));

        if (application !== undefined && application.Map !== undefined)
            application.Map.zoom(null);
    };

    $(window).scroll(function() {
        var distance, footerheight;
        footerheight = $('#footer').height();
        if ($(window).scrollTop() + $(window).height() >= $(document).height() - footerheight) {
            distance = $(window).scrollTop() + $(window).height() - $(document).height() + footerheight;
            if (distance > footerheight) {
                distance = footerheight;
            }
            $("#eventbox").css("bottom", distance);
        } else {
            $("#eventbox").css("bottom", "0px");
        }

    });

    $(window).resize(function() {
        this.resize();

    }).trigger("resize");

    /// WVW MAP
    mapWvw = new Object();
    mapWvw.ready = false;
    mapWvw.coordinateTranslations = { "a": 5196, "b": 8479, "scale": 3.97 };
    mapWvw.headingCoordinates = [
        { "id": "red", "x": 668, "y": 70, "ix": 1 },
        { "id": "green", "x": 216, "y": 380, "ix": 2 },
        { "id": "blue", "x": 1120, "y": 300, "ix": 3 },
        { "id": "center", "x": 668, "y": 550, "ix": 4 }];

    //TODO: initial torchcoordinates
    mapWvw.torches = [
       { "id": 1, "icon": "shield", "x": 50, "y": 220, "text": "Defend!" },
       { "id": 2, "icon": "flag", "x": 100, "y": 220, "text": "Capture!" },
       { "id": 3, "icon": "fight", "x": 150, "y": 220, "text": "Attack!" },
       { "id": 4, "icon": "cross", "x": 200, "y": 220, "text": "Meet!" }];

    mapWvw.init = function() {
        var bg;

        this.svg = d3.select(configuration.container)
                .append("svg")
                .attr("id", "main")
                .attr("width", configuration.s_width)
                .attr("height", configuration.s_height);

        this.g = this.svg.append("g")
                .attr("id", "stage");

        bg = this.g.append("g")
                .attr("id", "backgr");

        bg.append("image")
                .attr("width", configuration.s_width)
                .attr("height", configuration.s_height)
                .attr("xlink:href", "img/maps/gw2wvwmap.jpg")
                .on("click", this.zoom);

        mapWvw.headingCoordinates.forEach(function(e) {
            var mapheader, text;

            text = e.id === "red" ? worlds.getWorldName(wvwEvents.myMatch.red_world_id) :
                    e.id === "green" ? worlds.getWorldName(wvwEvents.myMatch.green_world_id) :
                    e.id === "blue" ? worlds.getWorldName(wvwEvents.myMatch.blue_world_id) :
                    wvwEvents.wvwMapNames[3].name;

            mapheader = bg.append("g")
                    .data([e])
                    .attr("id", e.id + "MapHeader")
                    .style("pointer-events", "all")
                    .attr("transform", "translate (" + e.x + "," + e.y + ")")
                    .on("click", this.zoom);
            mapheader.append("text")
                    .attr("transform", "translate (1,1)")
                    .attr("class", "wvwMapHeading")
                    .text(text);
            mapheader.append("text")
                    .style("fill", e.id === "center" ? "darkgoldenrod" : "dark" + e.id)
                    .attr("class", "wvwMapHeading")
                    .text(text);
            mapWvw.addMapScore(mapheader, e.ix);
        });

        this.r = this.g.append("g")
                .attr("id", "regions");
        //.on("mousemove", this.mouseMove);


        this.ready = true;
        wvwEvents.paths.forEach(function(o) {
            mapWvw.addObjective(o);
        });

        mapWvw.addTorches();

        mapWvw.addTotalScore();
        
        wvwEvents.registerPlayerRemoveTimer();

        // for browsers which don't call resize at the beginning
        resize();
    };

    mapWvw.translate = function(data) {
        data.x = (data.x-mapWvw.coordinateTranslations.a)/mapWvw.coordinateTranslations.scale/2;
        data.y = (data.y-mapWvw.coordinateTranslations.b)/mapWvw.coordinateTranslations.scale/2;
    };

    mapWvw.addObjective = function(o) {
        var imgsize, container;

        if (!this.ready)
            this.init();

        o.objective = wvwEvents.getObjective(o.id);
        o.type = wvwEvents.getObjectiveType(o.id);
        o.name = wvwEvents.getObjectiveName(o.id);

        wvwEvents.calculateTimer(o.objective);

        imgsize = 32;

        container = this.r.append("g")
                .attr("id", "container" + o.id)
                .attr("transform", "translate(" + o.x + ", " + o.y + ")");

        container.append("text")
                .attr("transform", "translate(0,-16)")
                .attr("text-anchor", "middle")
                .attr("class", "objectiveHeading")
                .text(o.name);

        container.append("image")
                .data([o])
                .attr("id", "o_image_" + o.id)
                .attr("xlink:href", "img/wvwicons/" + o.type +
                "_" + o.objective.owner + ".png")
                .attr("width", imgsize)
                .attr("height", imgsize)
                .attr("transform", "translate(-" + imgsize / 2 + ",-" + imgsize / 2 + ")")
                .on("click", this.zoom);

        container.append("image")
                .data([o])
                .attr("id", "o_imagef_" + o.id)
                .attr("xlink:href", "img/wvwicons/forbidden.png")
                .attr("width", imgsize)
                .attr("height", imgsize)
                .attr("transform", "translate(-" + imgsize / 2 + ",-" + imgsize / 2 + ")")
                .style("dispplay", "none")
                .on("click", this.zoom);

        container.append("image")
                .data([o])
                .attr("id", "o_imageg_" + o.id)
                .attr("xlink:href", "img/wvwicons/shield.png")
                .attr("width", imgsize)
                .attr("height", imgsize)
                .attr("transform", "translate(-" + imgsize / 2 + ",-" + imgsize / 2 + ")")
                .style("display", "none")
                .on("click", this.zoom);

        mapWvw.appendObjectiveGuild(o.objective);
        mapWvw.appendObjectiveState(o.objective);

        //.call(d3.behavior.drag().on("drag", move));
    };

    mapWvw.addPlayer = function (p) {
        var container, imgsize, factor, k, scale;

        imgsize = 10;
        scale = "translate(-" + imgsize / 2 + ",-" + imgsize / 2 + ")";

        if (centered !== null) {
            factor = ($(window).width() / configuration.s_width);
            k = 4 * factor;
            scale = "translate(-" + ((imgsize/2) / (k - 1)) + ",-" + ((imgsize/2) / (k - 1)) + ")scale(" + 1 / (k - 1) + ")";
        }

        container = mapWvw.r.append("g")
            .attr("id", "player_container_" + p.id)
            .attr("transform", "translate(" + p.x + ", " + p.y + ")");

        container.append("image")
            .data([p])
            .attr("id", "player_image_" + p.id)
            .attr("class", "players")
            .attr("xlink:href", "img/wvwicons/player.png")
            .attr("width", imgsize)
            .attr("height", imgsize)
            .attr("transform", scale)
            .on("click", mapWvw.zoom);
    };

    mapWvw.updatePlayer = function (p) {
        //{"channel":"1234567890123","timestamp":1372420130001,"world_id":2006,"map_id":38,"x":240.6915,"y":167.127,"z":-135.5829,"identity":"zyclonite.1258"}
        var container, data, icon;
        
        mapWvw.translate(p);

        container = d3.select("#player_container_" + p.id);
        
        if(container.empty()){
            mapWvw.addPlayer(p);
            return;
        }
        
        icon = container.select("#player_image_" + p.id);

        data = icon.data();
        data[0].x = p.x;
        data[0].y = p.y;
        data[0].z = p.z;
        data[0].timestamp = p.timestamp;
        data[0].world_id = p.world_id;
        data[0].map_id = p.map_id;

        icon.data(data);
        container.transition()
            .duration(500)
            .attr("transform", "translate(" + p.x + "," + p.y + ")");

    };

    mapWvw.removeOldPlayers = function () {
        var allplayers, now;
        
        now = myDate.serverNow();
        
        allplayers = d3.selectAll(".players");
        allplayers.each(function(d, i){
            if(d.timestamp < (now - 5 * 60 * 1000)){
                d3.select("#player_container_"+d.id).remove();
            }
        });
    };
    
    mapWvw.addTorches = function () {
        var container, imgsize;

        imgsize = 32;
        
        mapWvw.torches.forEach(function (t) {
            container = mapWvw.r.append("g")
                .attr("id", "torch_container_" + t.id)
                .attr("transform", "translate(" + t.x + ", " + t.y + ")");

            t.oldx = t.x;
            t.oldy = t.y;
            container.append("image")
                .data([t])
                .attr("id", "torch_image_" + t.id)
                .attr("xlink:href", "img/wvwicons/torch_" + t.icon + ".png")
                .attr("width", imgsize)
                .attr("height", imgsize)
                .attr("transform", "translate(-" + imgsize / 2 + ",-" + imgsize / 2 + ")")
                .on("click", mapWvw.zoom)
                .call(d3.behavior.drag().on("drag", moveTorch)
                    .on("dragend", wvwEvents.sendTorch));
        });
    };

    mapWvw.updateTorch = function (t) {
    //{"channel":"1234567890123","timestamp":1371565333222,"sender":"testnick","message":"test message","icon":"4","x":123.0,"y":456.0}}
        var container, data, icon;

        container = d3.select("#torch_container_" + t.icon);
        icon = container.select("#torch_image_" + t.icon);
        
        data = icon.data();
        data[0].x = t.x;
        data[0].y = t.y;
        data[0].oldx = t.x;
        data[0].oldy = t.y;
        data[0].text = t.message;

        icon.data(data);
        container.transition()
            .duration(500)
            .attr("transform", "translate(" + t.x + "," + t.y + ")");

    };

    mapWvw.replayTorch = function(d) {
        var elem, container, id, x, y;

        elem = $(this);
        id = elem.attr("data_id");
        x = elem.attr("data_x");
        y = elem.attr("data_y");

        container = d3.select("#torch_container_" + id);
        container.transition()
            .duration(500)
            .attr("transform", "translate(" + x + "," + y + ")");
        return false;
    };

    mapWvw.addTotalScore = function() {
        var data, pie, arc, container, chart, arcs;

        data = wvwEvents.getScores(0);

        pie = d3.layout.pie()
                .sort(null)
                .value(function(d) {
            return d.value;
        }); // here

        arc = d3.svg.arc()
                .outerRadius(80)
                .innerRadius(60);

        container = mapWvw.g.append("g")
                .attr("id", "container_score")
                .attr("transform", "translate(120,120)");
        container.append("circle")
                .attr("class", "title_bg")
                .attr("r", 60);

        container.append("text")
                .attr("text-anchor", "middle")
                .attr("class", "mapHeading")
                .attr("transform", "translate(0,-25)")
                .text("Total Scores");

        data.forEach(function(d) {
            container.append("text")
                    .attr("id", "total_score_label_" + d.title)
                    .attr("text-anchor", "middle")
                    .attr("transform", "translate(0," + (15 * (data.indexOf(d) + 1)) + ")")
                    .style("fill", d.title)
                    .text(d.value);
        });

        chart = container.append("g")
                .attr("id", "chart_scores");

        arcs = chart.selectAll(".arc")
                .data(pie(data))
                .enter().append("g")
                .attr("class", "arc");

        arcs.append("path")
                .attr("d", arc)
                //.attr("id", function (d) { return e.id + "_arc_" + d.data.title }) // here
                .style("fill", function(d) {
            return "dark" + d.data.title;
        }); // here
    };

    mapWvw.updateTotalScore = function(o) {
        var data, olddata, arc, pie, chart;

        data = wvwEvents.getTotalScoresFromObjective(o);
        olddata = wvwEvents.getScores(0);

        if ((data[0] === olddata[0]) && (data[1] === olddata[1]) && (data[2] === olddata[2])) {
            return;
        }

        wvwEvents.setScores(0, data);

        data.forEach(function(d) {
            d3.select("#total_score_label_" + d.title)
                    .text(d.value);
        });

        arc = d3.svg.arc()
                .outerRadius(80)
                .innerRadius(60);

        pie = d3.layout.pie()
                .sort(null)
                .value(function(d) {
            return d.value;
        });

        chart = d3.select("#chart_scores");

        chart.selectAll("path")
                .data(pie(data))
                .attr("d", arc);

        mapWvw.totalScoreTween();
    };

    mapWvw.totalScoreTween = function() {
        var chart;

        chart = d3.select("#chart_scores").transition()
                .duration(2000)
                .attrTween("transform", function(d, x, y) {
            return d3.interpolateString("rotate(0)", "rotate(360)");
        });
    };

    mapWvw.addMapScore = function(container, id) {
        var barHeight, maxBarWidth, data, barValue, sortedData, yScale, y, yText, x, chart, barsContainer;

        valueLabelWidth = 40; // space reserved for value labels (right)
        barHeight = 7; // height of one bar
        maxBarWidth = 70; // width of the bar with the max value

        data = wvwEvents.getScores(id);

        barValue = function(d) {
            return parseFloat(d.value);
        };

        // sorting
        sortedData = data.sort(function(a, b) {
            return d3.descending(barValue(a), barValue(b));
        });

        // scales
        yScale = d3.scale.ordinal().domain(d3.range(0, sortedData.length)).rangeBands([0, sortedData.length * barHeight]);
        y = function(d, i) {
            return yScale(i);
        };
        yText = function(d, i) {
            return y(d, i) + yScale.rangeBand() / 2;
        };
        x = d3.scale.linear().domain([0, d3.max(sortedData, barValue)]).range([0, maxBarWidth]);


        chart = container.append("g")
                .attr("id", "map_score_" + id)
                .attr("class", "chart")
                .attr("width", maxBarWidth)
                .attr("height", barHeight * data.length);


        // bars
        barsContainer = chart.append('g')
                .attr('transform', 'translate(12,-20)');
        barsContainer.selectAll("rect").data(sortedData).enter().append("rect")
                .attr('y', y)
                .attr('height', yScale.rangeBand())
                .attr('width', function(d) {
            return x(barValue(d));
        })
                .attr('stroke', 'white')
                .style("fill", function(d) {
            return "dark" + d.title;
        });

        // bar value labels
        barsContainer.selectAll("text").data(sortedData).enter().append("text")
                .attr("x", function(d) {
            return x(barValue(d));
        })
                .attr("y", yText)
                .attr("dx", 3) // padding-left
                .attr("dy", ".35em") // vertical-align: middle
                .attr("text-anchor", "start") // text-align: right
                .attr("class", "chartText")
                .text(function(d) {
            return d3.round(barValue(d), 2);
        });
    };

    mapWvw.updateMapScore = function(o) {
        var container, color, ix;

        if (o.map_type === "RedHome") {
            color = "red";
            ix = 1;
        }
        else if (o.map_type === "GreenHome") {
            color = "green";
            ix = 2;
        }
        else if (o.map_type === "BlueHome") {
            color = "blue";
            ix = 3;
        }
        else if (o.map_type === "Center") {
            color = "center";
            ix = 4;
        }

        wvwEvents.setScores(ix, wvwEvents.getMapScoresFromObjective(o));

        container = d3.select("#" + color + "MapHeader");
        d3.select("#map_score_" + ix).remove();

        mapWvw.addMapScore(container, ix);


    };

    mapWvw.appendObjectiveState = function(o) {
        if (o.timeState === wvwEvents.timeState.INVULNERABLE) {
            d3.select("#o_imagef_" + o.objective_id)
                    .style("display", "block");
        } else {
            d3.select("#o_imagef_" + o.objective_id)
                    .style("display", "none");
        }
    };

    mapWvw.appendObjectiveGuild = function(o) {
        if (o.owner_guild !== null) {
            d3.select("#o_imageg_" + o.objective_id)
                    .style("display", "block");
        } else {
            d3.select("#o_imageg_" + o.objective_id)
                    .style("display", "none");
        }
    };

    mapWvw.updateObjective = function(o) {
        var obj, data;

        if (!this.ready)
            return;
        o.type = wvwEvents.getObjectiveType(o.id);

        obj = d3.select("#o_image_" + o.objective_id);

        data = obj.data();
        data[0].objective = o;

        obj.attr("xlink:href", "img/wvwicons/" +
                ((o.type === "Castle" || o.type === "Tower" || o.type === "Keep") ? o.type : "Camp") +
                "_" + o.owner + ".png")
                .data(data);

        wvwEvents.calculateTimer(o);
        mapWvw.appendObjectiveGuild(o);
        mapWvw.appendObjectiveState(o);
        mapWvw.pulse(o);
    };

    mapWvw.appendEvent = function(o) {

        eventbox.append("<div class=\"e_" + o.owner + "\">" + eventbox.date() + wvwEvents.getObjectiveName(o.objective_id) + " belongs to " + o.owner + (o.owner_guild !== null ? " and is claimed by : <div id=\"" + o.owner_guild + "\"> loading ... </div>" : " and hasn't been claimed") + "</div>");

        if (o.owner_guild !== null) {
            wvwEvents.getGuild(o.owner_guild, function(g) {
                $("#" + o.owner_guild).html(g.guild_name);
            });
        }
    };

    mapWvw.pulse = function(o) {
        var duration, tween;

        duration = 3000;

        tween = d3.select("#container" + o.objective_id)
                .append("circle")
                .attr("r", 1)
                .attr("fill", o.owner)
                .style("opacity", 1);

        tween.transition()
                .duration(duration)
                .attr("r", 300)
                .style("opacity", 0.01);

        tween.transition()
                .delay(duration + 100)
                .remove();
    };

    mapWvw.zoom = function(d) {
        var x, y, k, factor, targetscale;

        factor = ($(window).width() / configuration.s_width);

        if (d && centered !== this) {

            k = 4 * factor;
            x = d.x + configuration.s_width / (k * 10);
            y = d.y + configuration.s_height / (k * 10);
            centered = this;

            // TODO: scroll back to origin position in zoomout
            $("html, body").animate({scrollTop: 0}, 1000);

            mapWvw.g.transition()
                    .duration(1000)
                    .attr("transform", "translate(" + configuration.s_width * factor / 2 + "," + configuration.s_height * factor / 2 + ")scale(" + k + ")translate(" + -x + "," + -y + ")")
                    .style("stroke-width", 1.5 / k + "px");

            mapWvw.r.selectAll("image").each(function(){
                var image, width, height;
                image = d3.select(this);
                width = image.attr('width');
                height = image.attr('height');
                image.transition()
                        .duration(1000)
                        .attr("transform", "translate(-" + ((width/2) / (k - 1)) + ",-" + ((height/2) / (k - 1)) + ")scale(" + 1 / (k - 1) + ")");
            });
            if (d.objective !== undefined) {
                detailsPanel.scopeWvwEvent(d.objective);
            }
        }

        else {
            if (centered !== null) {
                detailsPanel.unScope();
                centered = null;

                targetscale = $(window).width() / configuration.s_width;

                d3.select("#stage").transition()
                        .duration(1000)
                        .attr("transform", "scale(" + targetscale + ")");

                mapWvw.r.selectAll("image").each(function(){
                    var image, width, height;
                    image = d3.select(this);
                    width = image.attr('width');
                    height = image.attr('height');
                    image.transition()
                            .duration(1000)
                            .attr("transform", "translate(-" + (width/2) + ",-" + (height/2) + ")scale(1)");
                });
            }
        }
    };

    mapPve = new Object();
    mapPve.ready = false;
    mapPve.coordinateTranslations = { "a": 148, "b": 6656, "scale": 11.94 };
    mapPve.init = function() {

        $(configuration.container).append("<div id=\"infobox\"></div>");

        this.svg = d3.select(configuration.container)
                .append("svg")
                .attr("id", "main")
                .attr("width", configuration.s_width)
                .attr("height", configuration.s_height);


        this.g = this.svg.append("g")
                .attr("id", "stage")
                .on("mousemove", this.mouseMove);

        this.g.append("path")
                .attr("width", configuration.s_width)
                .attr("height", configuration.s_height);


        this.g.append("image")
                .attr("width", configuration.s_width)
                .attr("height", configuration.s_height)
                .attr("xlink:href", "img/maps/gw2pvemap.jpg")
                .on("click", this.zoom);

        this.ready = true;

        pveEvents.paths.forEach(function(e) {
            mapPve.addEventCircle(e);
        });

        // for browsers which don't call resize at the beginning
        resize();
    };

    mapPve.translate = function(x, y) {
        xnew = (x-mapPve.coordinateTranslations.a)/mapPve.coordinateTranslations.scale;
        ynew = (y-mapPve.coordinateTranslations.b)/mapPve.coordinateTranslations.scale;
        return {"x": xnew, "y": ynew};
    };

    mapPve.addEventCircle = function(e) {
        var name, count, pie, color, arc, container, chart, arcs;

        if (!this.ready)
            this.init();

        name = pveEvents.getMapName(e.id, pveEvents.mapNames);

        count = pveEvents.countMapEvents(e.id);

        pie = d3.layout.pie()
                .sort(null)
                .value(function(d) {
            return d.value;
        }); // here

        color = d3.scale.ordinal()
                .range(["#98abc5", "#8a89a6", "#7b6888"]);

        arc = d3.svg.arc()
                .outerRadius(count.overallSize * configuration.COUTNRATIO)
                .innerRadius(0);

        container = this.g.append("g")
                .attr("id", "container" + e.id)
                .attr("transform", "translate(" + e.x + ", " + e.y + ")");

        container.append("text")
                .attr("transform", "translate(0, -" + count.overallSize + ")")
                .attr("text-anchor", "middle")
                .attr("class", "mapHeading")
                .text(name);

        chart = container.append("g")
                .data([e])
                .attr("id", "chart" + e.id)
                .on("click", this.zoom);
        //.call(d3.behavior.drag().on("drag", move));   // TODO

        arcs = chart.selectAll(".arc")
                .data(pie(count.sizes))
                .enter().append("g")
                .attr("class", "arc");

        arcs.append("path")
                .attr("d", arc)
                .attr("id", function(d) {
            return e.id + "_arc_" + d.data.title;
        }) // here
                .style("fill", function(d) {
            return color(d.data.title);
        }) // here
                .attr("opacity", 0.7)
                .style('stroke', 'white')
                .on("mouseover", this.mouseOverChartSlice)
                .on("mouseout", this.mouseOutChartSlice);

        //this.grow(e.id);
    };

    mapPve.grow = function(id) {
        var p;

        p = this.g.select("#chart" + id);
        p.transition()
                .duration(500)
                .attr("transform", "scale(3)")
                .style('stroke', 'red');
        p.transition()
                .delay(500)
                .duration(500)
                .attr("transform", "scale(1)")
                .style('stroke', 'white');

    };

    mapPve.updateChart = function(id) {
        var count, arc, pie, chart;

        count = pveEvents.countMapEvents(id);

        arc = d3.svg.arc()
                .outerRadius(count.overallSize * configuration.COUTNRATIO)
                .innerRadius(0);

        pie = d3.layout.pie()
                .sort(null)
                .value(function(d) {
            return d.value;
        });


        chart = this.g.select("#chart" + id);

        chart.selectAll("path")
                .data(pie(count.sizes))
                .attr("d", arc);

        this.grow(id);
    };

    mapPve.zoom = function(d) {

        var x, y, k, factor, targetscale;

        if (d && centered !== d) {
            factor = ($(window).width() / configuration.s_width);

            k = 4 * factor;
            x = d.x + configuration.s_width / (k * 10);
            y = d.y + configuration.s_height / (k * 10);

            if (centered !== null) {
                d3.select(centered)
                        .transition()
                        .duration(750)
                        .attr("transform", "translate(0,0)scale(1)");
            }

            centered = this;

            // TODO: scroll back to origin position in zoomout
            $("html, body").animate({scrollTop: 0}, 1000);

            detailsPanel.scopePveMap(d.id);
            d3.select(this)
                    .transition()
                    .duration(750)
                    .attr("transform", "translate(-100,100)scale(2)");
            mapPve.g.transition()
                    .duration(1000)
                    .attr("transform", "translate(" + configuration.s_width * factor / 2 + "," + configuration.s_height * factor / 2 + ")scale(" + k + ")translate(" + -x + "," + -y + ")")
                    .style("stroke-width", 1.5 / k + "px");
        }
        else {
            if (centered !== null) {
                detailsPanel.unScope();
                d3.select(centered)
                        .transition()
                        .duration(750)
                        .attr("transform", "translate(0,0)scale(1)");
                centered = null;
                targetscale = $(window).width() / configuration.s_width;

                d3.select("#stage").transition()
                        .duration(1000)
                        .attr("transform", "scale(" + targetscale + ")");
            }
        }
    };

    mapPve.mouseOverChartSlice = function(d) {
        d3.select(this)
                .transition()
                .duration(500)
                .attr("transform", "scale(2)")
                .attr("opacity", "1")
                .transition()
                .duration(1500)
                .attr("transform", "scale(1)")
                .attr("opacity", "0.6");

        d3.select("#infobox").style("display", "block")
                .text(d.data.title + ": " + d.data.value);
    };

    mapPve.mouseOutChartSlice = function(d) {
        d3.select("#infobox").style("display", "none");
    };

    mapPve.mouseMove = function(d) { // should be handled out of the svg if infobox not IN svg - does not work with scrolling atm
        var targetscale, infobox, coord;

        targetscale = $(window).width() / configuration.s_width;
        infobox = d3.select("#infobox");
        coord = d3.mouse(this);
        infobox.style("left", Math.round(coord[0] * targetscale) + 15 + "px");
        infobox.style("top", Math.round(coord[1] * targetscale) + "px");
    };

    mapPve.appendEvent = function(d) {
        eventbox.append("<div class=\"e_" + d.state + "\">" + eventbox.date() + pveEvents.getMapName(d.map_id) + ": " + pveEvents.getEventName(d.event_id) + ": " + d.state + "</div>");

        if (centered !== null && centered.id === d.map_id) {
            detailsPanel.scopePveMap(d.map_id);

        }
    };

    eventbox = new Object();
    eventbox.date = function() {
        var currentTime, hours, minutes, seconds;

        currentTime = myDate.localNow();
        hours = currentTime.getHours();
        minutes = currentTime.getMinutes();
        seconds = currentTime.getSeconds();

        if (hours < 10) {
            hours = "0" + hours;
        }
        if (minutes < 10) {
            minutes = "0" + minutes;
        }
        if (seconds < 10) {
            seconds = "0" + seconds;
        }
        return hours + ":" + minutes + ':' + seconds + ' ';
    };

    eventbox.init = function() {
        $(configuration.container).append("<div id=\"eventbox\"><ul class=\"unstyled\"></ul></div>");
        $("#eventbox").perfectScrollbar().click(eventbox.toggle);
    };

    eventbox.toggle = function(e) {
        if ($(this).attr("collapsed") === "yes") {
            eventbox.expand();
        } else {
            eventbox.collapse();
        }
    };

    eventbox.collapse = function() {
        var ebox;
        ebox = $("#eventbox");
        ebox.attr("collapsed", "yes");
        ebox.scrollTop(0).perfectScrollbar('update');
        ebox.animate({height: "18px"}, 300);
    };

    eventbox.expand = function() {
        var ebox;
        ebox = $("#eventbox");
        ebox.attr("collapsed", "no");
        ebox.animate({height: "120px"}, 300, function() {
            ebox.scrollTop(0).perfectScrollbar('update');
        });
    };

    eventbox.append = function(m) {

        $("#eventbox > ul").prepend('<li>' + m + '</li>');
        $("#eventbox > ul > li:gt(15)").fadeOut(500, function() {
            $(this).remove();
        });
        $("#eventbox").perfectScrollbar('update');
    };

    detailsPanel = new Object();
    detailsPanel.init = function() {
        $(configuration.container).append("<div id=\"details\" class=\"details\"><div id=\"detailscontent\" ></div></div>");
        detailsPanel.element = $("#detailscontent");
        detailsPanel.element.click(function () {
            $("#details").fadeOut(500);
        });
        $("#details").perfectScrollbar();
        eventbox.init();
    };

    detailsPanel.date = function(forDate) {
        var time, hours, minutes, seconds, day, month;

        time = myDate.localTime(forDate);
        hours = time.getHours();
        minutes = time.getMinutes();
        seconds = time.getSeconds();
        day = time.getDate();
        month = time.getMonth();

        if (hours < 10) {
            hours = "0" + hours;
        }
        if (minutes < 10) {
            minutes = "0" + minutes;
        }
        if (seconds < 10) {
            seconds = "0" + seconds;
        }
        return day + "." + month + ". " + hours + ":" + minutes + ':' + seconds;
    };

    detailsPanel.scopePveMap = function(id) {

        $("#detailscontent").html("<h1>" + pveEvents.getMapName(id) + "</h1>" + "<h2>Active</h2><ul id=\"e_Active\" class=\"e_Active unstyled\"></ul><h2>Warmup</h2><ul id=\"e_Warmup\" class=\"e_Warmup unstyled\"></ul><h2>Preparation</h2><ul id=\"e_Preparation\" class=\"e_Preparation unstyled\"></ul>");
        pveEvents.getMapEvents(id).forEach(function(e) {
            $("#e_" + e.state).append("<li id=\"event_details_" + e.event_id + "\">" + pveEvents.getEventName(e.event_id) + "</li>");
            $("#event_details_" + e.event_id).bind("click", function() {
                //detailsPanel.onClickEvent(e);
            });

        });

        $("#details").fadeIn(500, function() {
            $("#details").perfectScrollbar('update');
        });
        eventbox.collapse();
    };

    detailsPanel.scopeWvwEvent = function(o) {
        var detailscontent;
        detailscontent = $("#detailscontent");
        detailscontent.html(
                "<h1 class=\"" + o.owner + "\">" + wvwEvents.getObjectiveName(o.objective_id) + "</h1>" +
                "<h3>(" + wvwEvents.getObjectiveType(o.objective_id) + ")</h3>" +
                "<h3>since " + detailsPanel.date(o.timestamp) + "</h3>");

        if (o.owner_guild !== null) {
            wvwEvents.getGuild(o.owner_guild, function(g) {
                detailscontent.append("<h2>(" + g.guild_name + ")</h2>");
                $("#details").fadeIn(500);
            });
        } else {
            $("#details").fadeIn(500);
        }
        eventbox.collapse();
    };

    detailsPanel.unScope = function() {
        var details;
        details = $("#details");
        details.fadeOut(500, function() {
            details.scrollTop(0).perfectScrollbar('update');
        });
        eventbox.expand();
    };

    detailsPanel.onClickEvent = function(e) {
        var result;

        result = "<p><h3>Details:</h3><br/></p>";
        detailsPanel.fetchWikiData(e);

        $("#event_details_" + e.event_id).append(result).unbind();
    };

    detailsPanel.fetchWikiData = function(e) {
        var robot, result;

        robot = pveEvents.getEventName(e.event_id).replace(/ /g, "_").replace(".", "");
        result = "<p><h3>Wiki Information</h3></p>";
        result += "<p> More Information to this event available at <a href=\"http://wiki.guildwars2.com/wiki/" + robot + "\">the GW2 Wiki</a></p>";

        if (configuration.wikiEnabled) {
            $.ajax(configuration.wikiUrl + robot,
                    function(data) {

                        $(data).find('div').each(function() {
                            var loc, lvl;

                            if ($(this).attr("class") === "infobox quest") {

                                // fetch location
                                result += "Location: ";
                                loc = $(this).find('dd').first().find("a")[0].innerHTML;
                                result = result + loc + "<br/>";

                                result += "Level: ";
                                lvl = $(this).find('dd')[2].innerHTML;
                                result += lvl + "<br/>";

                                // append wiki info
                                $("#event_details_" + e.event_id).append(result);
                            }
                        });
                    });
        }
    };

    application = new Object();
    application.state = STATE.STANDBY;
    application.init = function() {

        application.state = STATE.INITIATING;

        if (configuration.currentMode === Livemode.PVE) {
            this.Map = mapPve;
            this.loadBootstrapData(this.loadPveBootstrapData);
        }
        else if (configuration.currentMode === Livemode.WVW) {
            this.Map = mapWvw;
            this.loadBootstrapData(this.loadWvwBootstrapData);
        }
    };

    application.loadBootstrapData = function(callback) {

        if (worlds.worldNames === null) {
            worlds.worldNames = undefined;
            $.getJSON(configuration.rootUrl + "rest/pveworldnames/lang/" + configuration.lang)
                    .done(function(n) {
                worlds.worldNames = n;
                callback();
            });
        } else {
            callback();
        }
    };

    application.loadPveBootstrapData = function() {

        if (pveEvents.worldEvents === null) {
            pveEvents.worldEvents = undefined;
            $.getJSON(configuration.rootUrl + "rest/pveevents/world/" + configuration.worldId)
                    .done(function(e) {
                pveEvents.worldEvents = e;
                application.loadPveBootstrapData();
            });
        }

        if (pveEvents.paths === null) {
            pveEvents.paths = undefined;
            $.getJSON(configuration.rootUrl + "rest/pvecoordinates")
                    .done(function(e) {
                pveEvents.paths = e;
                application.loadPveBootstrapData();
            });
        }

        if (pveEvents.mapNames === null) {
            pveEvents.mapNames = undefined;
            $.getJSON(configuration.rootUrl + "rest/pvemapnames/lang/" + configuration.lang)
                    .done(function(e) {
                pveEvents.mapNames = e;
                application.loadPveBootstrapData();
            });
        }

        if (pveEvents.eventNames === null) {
            pveEvents.eventNames = undefined;
            $.getJSON(configuration.rootUrl + "rest/pveeventnames/lang/" + configuration.lang)
                    .done(function(e) {
                pveEvents.eventNames = e;
                application.loadPveBootstrapData();
            });
        }

        if (application.state === STATE.INITIATING &&
                (pveEvents.mapNames !== null && pveEvents.mapNames !== undefined) &&
                (pveEvents.paths !== null && pveEvents.paths !== undefined) &&
                (pveEvents.worldEvents !== null && pveEvents.worldEvents !== undefined) &&
                (pveEvents.eventNames !== null && pveEvents.eventNames !== undefined)) {

            application.state = STATE.READY;
            application.ready();

        }
    };

    application.loadWvwBootstrapData = function() {

        if (wvwEvents.matches === null) {
            wvwEvents.matches = undefined;
            $.getJSON(configuration.rootUrl + "rest/wvwmatches")
                    .done(function(e) {
                wvwEvents.matches = e;
                wvwEvents.matches.forEach(function(m) {
                    if (m.green_world_id === configuration.worldId || m.red_world_id === configuration.worldId || m.blue_world_id === configuration.worldId) {
                        wvwEvents.myMatch = m;
                    }
                });

                $.getJSON(configuration.rootUrl + "rest/wvwevents/match/" + wvwEvents.myMatch.wvw_match_id)
                        .done(function(e) {
                    wvwEvents.worldEvents = e;
                    application.loadWvwBootstrapData();
                });
                $.getJSON(configuration.rootUrl + "rest/wvwscores/match/" + wvwEvents.myMatch.wvw_match_id)
                        .done(function(e) {
                    wvwEvents.scores = e;
                    application.loadWvwBootstrapData();
                });
            });
        }

        if (wvwEvents.objectiveTypes === null) {
            wvwEvents.objectiveTypes = undefined;
            $.getJSON(configuration.rootUrl + "rest/wvwobjectivedetails")
                    .done(function(e) {
                wvwEvents.objectiveTypes = e;
                application.loadWvwBootstrapData();
            });
        }

        if (wvwEvents.wvwMapNames === null) {
            wvwEvents.wvwMapNames = undefined;
            $.getJSON(configuration.rootUrl + "rest/wvwmapnames/lang/" + configuration.lang)
                    .done(function(e) {
                wvwEvents.wvwMapNames = e;
                application.loadWvwBootstrapData();
            });
        }

        if (wvwEvents.paths === null) {
            wvwEvents.paths = undefined;
            $.getJSON(configuration.rootUrl + "rest/wvwcoordinates")
                    .done(function(e) {
                wvwEvents.paths = e;
                application.loadWvwBootstrapData();
            });
        }


        if (wvwEvents.objectiveNames === null) {
            wvwEvents.objectiveNames = undefined;
            $.getJSON(configuration.rootUrl + "rest/wvwobjectivelongnames/lang/" + configuration.lang)
                    .done(function(e) {
                wvwEvents.objectiveNames = e;
                application.loadWvwBootstrapData();
            });
        }

        if (application.state === STATE.INITIATING &&
                (wvwEvents.objectiveNames !== null && wvwEvents.objectiveNames !== undefined) &&
                (wvwEvents.worldEvents !== null && wvwEvents.worldEvents !== undefined) &&
                (wvwEvents.scores !== null && wvwEvents.scores !== undefined) &&
                (wvwEvents.objectiveTypes !== null && wvwEvents.objectiveTypes !== undefined) &&
                (wvwEvents.paths !== null && wvwEvents.paths !== undefined) &&
                (wvwEvents.wvwMapNames !== null && wvwEvents.wvwMapNames !== undefined)) {

            application.state = STATE.READY;
            this.ready();

        }
    };

    application.ready = function() {

        this.Map.init();
        detailsPanel.init();
        subscription.init(subscription.subscribe);
        gw2map.state = STATE.READY;
        browserEvents.setNotLoading();

    };

    subscription.subscribe = function () {
        var att;
        att = new Object();
        if (configuration.currentMode === Livemode.PVE) {
            att.type = "pve";
            att.subscription_id = "" + configuration.worldId;
            subscription.unsubscribeChat();
        }
        else {
            att.type = "wvw";
            att.subscription_id = wvwEvents.myMatch.wvw_match_id;

            if (configuration.nickname !== null &&
                configuration.channel !== null) {
                subscription.subscribeChat();
            }

        }
        subscription.send(att);

    };

    subscription.unsubscribeChat = function () {
        var subscribe = new Object();
        subscribe.type = "channel";
        subscribe.subscription_id = "none";
        subscribe.nickname = "none";
        subscription.send(subscribe);
    };

    subscription.subscribeChat = function () {

        var subscribe = new Object();
        subscribe.type = "channel";
        subscribe.subscription_id = configuration.channel;
        subscribe.nickname = configuration.nickname;
        subscription.send(subscribe);
    };

    subscription.send = function(message) {
        if (subscription.sock !== null && subscription.connected === true) {
            subscription.sock.send(JSON.stringify(message));
        } else {
            setTimeout(function() {
                subscription.send(message);
            }, 1000);
        }
    };

    subscription.stop = function() {
        if (subscription.sock !== null) {
            subscription.sock.close();
        }
        subscription.sock = null;
    };

    subscription.reconnect = function() {
        if (subscription.connected === false) {
            subscription.init(subscription.subscribe);
            setTimeout(function() {
                subscription.reconnect();
            }, 5000);
        }
    };

    subscription.pveEventHandler = function(e) {
        pveEvents.appendEvent(e);
        mapPve.updateChart(e.map_id);
        mapPve.appendEvent(e);
    };

    subscription.wvwEventHandler = function(e) {
        mapWvw.updateObjective(e);
        mapWvw.updateTotalScore(e);
        mapWvw.updateMapScore(e);
        mapWvw.appendEvent(e);
    };

    subscription.broadcastEventHandler = function(e) {
        //{"type":"broadcast","data":{"message":"Server is going down for an update, please try to reconnect in some minutes"}}
        eventbox.append("<div class=\"e_Fail\">" + e.message + "</div>");
    };

    subscription.chatEventHandler = function(e) {
        //{"type":"chatevent","data":{"channel":"1234567890123","timestamp":1371565333222,"sender":"testnick","message":"test message","icon":"torch","x":123.0,"y":456.0}}
        eventbox.append(eventbox.date() + e.sender + ": " + e.message + " <i class=\"icon-retweet icon-white pull-right\" style=\"margin-right: 15px;\" id=\"torchevent" + e.timestamp + "\" data_id=\"" + e.icon + "\" data_x=\"" + e.x + "\" data_y=\"" + e.y + "\"/>");
        $("#torchevent" + e.timestamp).click(mapWvw.replayTorch);
        mapWvw.updateTorch(e);
    };

    subscription.playerLocationEventHandler = function(e) {
        //{"type":"playerlocation","data":{"channel":"1234567890123","id":<guid>,"timestamp":1371565333222,"identity":"charname","world_id":1001,"map_id":10,"x":1,"y":2,"z":3}}
        //adds player if not existing, remove player if timestamp older than 5min, update playerlocation on message
        mapWvw.updatePlayer(e);//TODO
    };

    subscription.init = function(callback) {
        if (subscription.sock === null && subscription.connected === false) {
            subscription.sock = new SockJS(configuration.streamUrl);
            subscription.sock.onopen = function() {
                subscription.connected = true;

                //var subscribe = new Object();
                //subscribe.type = "channel";
                //subscribe.subscription_id = "1234567890123";
                //subscribe.nickname = "testnick";
                //subscription.send(subscribe);
                //console.log('subscribing to chat channel ' + subscribe.subscription_id);

                callback();
            };

            subscription.sock.onmessage = function(e) {
                var obj;

                if (application.state !== STATE.READY)
                    return;

                obj = JSON.parse(e.data);
                if (configuration.currentMode === Livemode.PVE && obj.type === "pveevent") {
                    subscription.pveEventHandler(obj.data);
                }
                else if (configuration.currentMode === Livemode.WVW && obj.type === "wvwevent") {
                    subscription.wvwEventHandler(obj.data);
                }
                else if (configuration.currentMode === Livemode.WVW && obj.type === "chatevent") {
                    subscription.chatEventHandler(obj.data);
                }
                else if (configuration.currentMode === Livemode.WVW && obj.type === "playerlocation") {
                    subscription.playerLocationEventHandler(obj.data);
                }
                else if (obj.type === "broadcast") {
                    subscription.broadcastEventHandler(obj.data);
                }
            };

            subscription.sock.onclose = function() {
                if(subscription.connected === true) {
                    setTimeout(function() {
                        subscription.reconnect();
                    }, 5000);
                }
                subscription.connected = false;
                subscription.stop();
                //console.log('close');
            };
        } else {
            callback();
        }
    };

    wvwEvents = new Object();
    wvwEvents.timeState = new Object();
    wvwEvents.timeState.INVULNERABLE = 0;
    wvwEvents.timeState.COMMODITY = 1;

    wvwEvents.guildCache = [];
    wvwEvents.removePlayerTimer;

    wvwEvents.getObjectiveName = function(id) {
        var name;

        id = id + "";
        wvwEvents.objectiveNames.forEach(function(e) {
            if (e.id === id) {
                name = e.name;
                return;
            }
        });

        return name;
    };

    wvwEvents.getObjectiveType = function(id) {
        var result;

        id = parseInt(id,10);
        wvwEvents.objectiveTypes.forEach(function(e) {
            if (e.id === id) {
                result = e.type;
                return;
            }
        });

        return result;
    };

    wvwEvents.getObjective = function(id) {
        var result;

        wvwEvents.worldEvents.forEach(function(e) {
            if (e.objective_id === parseInt(id, 10)) {
                result = e;
                return;
            }
        });
        return result;
    };

    wvwEvents.getGuild = function(id, callback) {
        var result;

        result = null;

        wvwEvents.guildCache.forEach(function(g) {
            if (g.guild_id === id)
                result = g;
        });

        if (result !== null) {
            callback(result);
        } else {

            $.getJSON(configuration.rootUrl + "rest/guilddetails/guildid/" + id)
                    .done(function(n) {
                if (n[0].guild_id !== null) {
                    wvwEvents.guildCache.push(n[0]);
                    callback(n[0]);
                }
            });
        }
    };

    wvwEvents.getScores = function(id) {
        var result;

        result = [
            {title: "Red", value: wvwEvents.scores[id].scores[0]},
            {title: "Blue", value: wvwEvents.scores[id].scores[1]},
            {title: "Green", value: wvwEvents.scores[id].scores[2]}];

        return result;
    };

    wvwEvents.setScores = function(id, data) {
        wvwEvents.scores[id].scores[0] = data[0].value;
        wvwEvents.scores[id].scores[1] = data[1].value;
        wvwEvents.scores[id].scores[2] = data[2].value;
    };

    wvwEvents.getTotalScoresFromObjective = function(o) {
        var result;

        result = [
            {title: "Red", value: o.match_scores[0]},
            {title: "Blue", value: o.match_scores[1]},
            {title: "Green", value: o.match_scores[2]}];

        return result;

    };

    wvwEvents.getMapScoresFromObjective = function(o) {
        var result;

        result = [
            {title: "Red", value: o.map_scores[0]},
            {title: "Blue", value: o.map_scores[1]},
            {title: "Green", value: o.map_scores[2]}];

        return result;
    };

    wvwEvents.calculateTimer = function(o) {
        var date, remainingTime;

        if(o.type !== "Camp" || o.type !== "Keep" || o.type !== "Tower" || o.type !== "Castle") {
            return;
        }
        
        date = myDate.serverNow();
        remainingTime = (o.timestamp + configuration.protectiontime) - date.getTime();

        if (remainingTime > 1 && o.timeState !== wvwEvents.timeState.INVULNERABLE) {
            o.timeState = wvwEvents.timeState.INVULNERABLE;

            browserEvents.timers.push(setTimeout(function() {
                o.timeState = wvwEvents.timeState.COMMODITY;
                mapWvw.appendObjectiveState(o);
                mapWvw.pulse(o);
            }, remainingTime));
        }
        else {
            o.timeState = wvwEvents.timeState.COMMODITY;
        }
    };

    wvwEvents.registerPlayerRemoveTimer = function(){
        wvwEvents.removePlayerTimer = setInterval(mapWvw.removeOldPlayers, 10000);
    };
    
    wvwEvents.flush = function() {
        clearInterval(wvwEvents.removePlayerTimer);
        wvwEvents.objectiveNames = null;
        wvwEvents.matches = null;
        wvwEvents.worldEvents = null;
        wvwEvents.myMatch = null;

        if (wvwEvents.paths === undefined)
            wvwEvents.paths = null;
        if (wvwEvents.objectiveTypes === undefined)
            wvwEvents.objectiveTypes = null;
        if (wvwEvents.wvwMapNames === undefined)
            wvwEvents.wvwMapNames = null;
    };

    wvwEvents.sendTorch = function (d) {
        var torch;

        if(d.oldx === d.x && d.oldy === d.y){
            return;
        }
        
        d.oldx = d.x;
        d.oldy = d.y;
        
        torch = new Object();
        torch.type = "chat";
        torch.message = d.text;
        torch.icon = ""+d.id;
        torch.x = d.x;
        torch.y = d.y;

        subscription.send(torch);
    };

    pveEvents = new Object();
    pveEvents.countMapEvents = function(map) {
        var count, result;

        count = new Object();

        count.active = 0;
        count.preperation = 0;
        count.warmup = 0;
        mapid = parseInt(map, 10);

        pveEvents.worldEvents.forEach(function(e) {
            if (e.map_id === mapid) {
                if (e.state === "Active")
                    count.active++;
                else if (e.state === "Warmup")
                    count.warmup++;
                else if (e.state === "Preparation")
                    count.preperation++;
            }
        });
        result = new Object();

        result.count = count;

        result.sizes = [
            {title: "Active", value: count.active},
            {title: "Preparation", value: count.preperation},
            {title: "Warmup", value: count.warmup}];

        result.overallSize = count.active + count.preperation + count.warmup;

        return result;
    };

    pveEvents.getMapName = function(mapid) {
        var name;

        mapid = mapid + "";
        pveEvents.mapNames.forEach(function(e) {
            if (e.id === mapid) {
                name = e.name;
                return;
            }
        });

        return name;
    };

    pveEvents.getEventName = function(eventid) {
        var name;

        pveEvents.eventNames.forEach(function(e) {
            if (e.id === eventid) {
                name = e.name;
                return;
            }
        });

        return name;
    };

    pveEvents.appendEvent = function(event) {
        var found;

        found = false;
        pveEvents.worldEvents.forEach(function(e) {
            if (event.event_id === e.event_id) {
                e.state = event.state;
                found = true;
            }
        });
        if (!found)
            pveEvents.worldEvents.push(event);

    };

    pveEvents.getMapEvents = function(map) {
        var result;

        result = [];
        mapId = parseInt(map, 10);

        pveEvents.worldEvents.forEach(function(e) {
            if (mapId === e.map_id) {
                result.push(e);
            }
        });

        return result;
    };

    pveEvents.flush = function() {
        pveEvents.mapNames = null;
        pveEvents.worldEvents = null;
        pveEvents.eventNames = null;

        if (pveEvents.paths === undefined)
            pveEvents.paths = null;
    };

    worlds = new Object();
    worlds.getWorldName = function(id) {
        var result;

        id = id + "";
        worlds.worldNames.forEach(function(n) {
            if (n.id === id)
                result = n.name;
        });
        return result;
    };

    //runtime
    if (gw2map.state !== STATE.INITIATING) {

        gw2map.state = STATE.INITIATING;

        browserEvents.setLoading();

        configuration.container = container;
        configuration.worldId = parseInt(worldId, 10);
        configuration.lang = lang === null ? "en" : lang;
        configuration.currentMode = mode;
        configuration.channel = channel;
        configuration.nickname = nickname;

        worlds.worldNames = null;
        pveEvents.flush();
        wvwEvents.flush();
        browserEvents.flush();

        mapPve.ready = false;
        mapWvw.ready = false;
        $(container).html("");

        application.init();
    }
};

gw2map.subscribeChat = function (nickname, channel) {
    configuration.channel = channel;
    configuration.nickname = nickname;

    if (gw2map.state === STATE.READY) {
        subscription.subscribeChat();
    }
};
