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
    var AppRouter, world, matchtype, nickname, channel, language, loadWorldNames, init, app_router, selectWorld, fixnavbar, pve_enabled, wvw_enabled;
    world = "0000";
    matchtype = "wvw";
    language = "en";
    pve_enabled = true;
    wvw_enabled = true;
    if (localStorage["nickname"]) {
        nickname = localStorage["nickname"];
    }else{
        nickname = "noname_"+Math.round(Math.random() * (999 - 100) + 100);
    }
    channel = ""+new Date().getTime();

    loadWorldNames = function() {
        var usworldselect, euworldselect;
        usworldselect = $('#usworldselect');
        euworldselect = $('#euworldselect');
        usworldselect.html('');
        euworldselect.html('');
        return $.getJSON('rest/pveworldnames/lang/' + language)
                .done(function(items) {
            $.each(items, function(index, world) {
                var firstchar = world.id.substring(0, 1);
                if (firstchar === "1") {
                    usworldselect.append('<li id="li' + world.id + '" class="worldselect"><a href="#' + world.id + '">' + world.name + '</a></li>');
                }
                if (firstchar === "2") {
                    euworldselect.append('<li id="li' + world.id + '" class="worldselect"><a href="#' + world.id + '">' + world.name + '</a></li>');
                }
            });
            $('.worldselect').click(selectWorld);
        });
    };

    loadAppConfig = function() {
        return $.getJSON('rest/appconfig')
                .done(function(config) {
            pve_enabled = config.pve;
            wvw_enabled = config.wvw;
        });
    };

    fixnavbar = function() {
        if (world === "0000") {
            return;
        }
        $('.worldselect').removeClass('active');
        $('#li' + world).addClass('active');
        $('.language > button').removeClass('active').removeAttr("disabled");
        $('#l' + language).addClass('active');
        $('.matchtype').removeClass('active').removeAttr("disabled");
        $('#m' + matchtype).addClass('active');
    };

    init = function() {
        var type;
        if (world === "0000") {
            return;
        }
        if (matchtype === "wvw") {
            type = gw2map.LiveModi.WVW;
            $('#warroomConfig').removeClass("disabled").removeAttr("disabled");
            app_router.navigate("wvw" + world + "/c" + channel + "/l" + language, false);
        } else {
            type = gw2map.LiveModi.PVE;
            $('#warroomConfig').addClass("disabled").attr("disabled", "disabled");
            app_router.navigate("pve" + world + "/l" + language, false);
        }
        ga('send', 'pageview');
        gw2map.init("#content", world, type, language, nickname, channel);
    };

    selectWorld = function(event) {
        event.preventDefault();
        $('.worldselect').removeClass('active');
        $(this).addClass('active');
        world = $(this).children('a').attr('href').substring(1);
        init();
        if ($('.nav-collapse').hasClass('in')) {
            $('.nav-collapse').collapse('hide');
        }
    };

    $('.language > button').click(function(event) {
        event.preventDefault();
        $('.language > button').removeClass('active').removeAttr("disabled");
        $(this).addClass('active').attr("disabled", "disabled");
        language = $(this).attr('id').substring(1);
        init();
        loadWorldNames();
        if ($('.nav-collapse').hasClass('in')) {
            $('.nav-collapse').collapse('hide');
        }
    });
    $('.matchtype').click(function(event) {
        var newmatchtype;
        event.preventDefault();
        newmatchtype = $(this).children('a').attr('href').substring(1);
        if(matchtype !== newmatchtype) {
            $('.matchtype').removeClass('active').removeAttr("disabled");
            $(this).addClass('active').attr("disabled", "disabled");
            matchtype = newmatchtype;
            init();
            if ($('.nav-collapse').hasClass('in')) {
                $('.nav-collapse').collapse('hide');
            }
        }
    });
    $('#warroomBtn').click(function() {
        nickname = $('#nickname').val();
        localStorage["nickname"] = nickname;
        channel = $('#channel').val();
        gw2map.subscribeChat(nickname, channel);
        if (world !== "0000") {
            app_router.navigate(matchtype + world + "/c" + channel + "/l" + language, false);
        }
        $("#warroomModal").modal('hide');
    });
    $('#feedbackBtn').click(function() {
        $.ajax({
            type: "POST",
            url: "feedback.php",
            data: $('form.feedback').serialize(),
            success: function(msg) {
                $("#thanks").html(msg).toggle().delay(5000).fadeOut(1000);
                $("form.feedback").trigger("reset");
                $("#feedbackModal").modal('hide');
            },
            error: function() {
                alert("An error occured sending your feedback!");
            }
        });
    });
    $('.worldselect').click(selectWorld);
    AppRouter = Backbone.Router.extend({
        routes: {
            "wvw:wrld/c:channel/l:lang": "loadWvwWorldAndChannelAndLanguage",
            "wvw:wrld/c:channel": "loadWvwWorldAndChannel",
            "wvw:wrld/l:lang": "loadWvwWorldAndLanguage",
            "wvw:wrld": "loadWvwWorld",
            "pve:wrld/l:lang": "loadPveWorldAndLanguage",
            "pve:wrld": "loadPveWorld"
        },
        loadWvwWorldAndChannelAndLanguage: function(wrld, chnnl, lang) {
            if(!wvw_enabled)
                return;
            matchtype = "wvw";
            language = lang;
            world = wrld;
            channel = chnnl;
            $('#channel').val(chnnl);
            init();
            fixnavbar();
        },
        loadWvwWorldAndChannel: function(wrld, chnnl) {
            if(!wvw_enabled)
                return;
            matchtype = "wvw";
            language = "en";
            world = wrld;
            channel = chnnl;
            $('#channel').val(chnnl);
            init();
            fixnavbar();
        },
        loadWvwWorldAndLanguage: function(wrld, lang) {
            if(!wvw_enabled)
                return;
            matchtype = "wvw";
            language = lang;
            world = wrld;
            init();
            fixnavbar();
        },
        loadWvwWorld: function(wrld) {
            if(!wvw_enabled)
                return;
            matchtype = "wvw";
            language = "en";
            world = wrld;
            init();
            fixnavbar();
        },
        loadPveWorldAndLanguage: function(wrld, lang) {
            if(!pve_enabled)
                return;
            matchtype = "pve";
            language = lang;
            world = wrld;
            init();
            fixnavbar();
        },
        loadPveWorld: function(wrld) {
            if(!pve_enabled)
                return;
            matchtype = "pve";
            language = "en";
            world = wrld;
            init();
            fixnavbar();
        }
    });

    $(".feedback > input,select,textarea").jqBootstrapValidation();

    $.when(loadWorldNames(), loadAppConfig(), myDate.calibrate()).done(function() {
        $('#nickname').val(nickname);
        $('#channel').val(channel);
        if(!wvw_enabled){
            matchtype = "pve";
            $('#mwvw').remove();
        }
        if(!pve_enabled) {
            $('#mpve').remove();
        }
        fixnavbar();
        app_router = new AppRouter;
        Backbone.history.start();
    });
});
