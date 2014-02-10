Number.prototype.toHHMMSS = function () {
    var sec_num = Math.floor(this / 1000);
    var hours   = Math.floor(sec_num / 3600);
    var minutes = Math.floor((sec_num - (hours * 3600)) / 60);
    var seconds = sec_num - (hours * 3600) - (minutes * 60);

    var time    = '<strong>'+hours+'</strong> hours <strong>'+minutes+'</strong> minutes <strong>'+seconds+'</strong> seconds';
    return time;
};

$(function() {
	var AppRouter, worlds;
	
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

	getTopGuilds = function(matchid) {
		$.getJSON('rest/topguilds/match/'+matchid)
				.done(function(items) {
			var content = $('#content');
			content.html('');
			$.each(items, function(index, guild) {
				content.append('<tr><td><h5>'+(index+1)+'</h5></td><td><img src="http://gw2map.com/emblem/?size=40&guild_id='+guild._id+'" alt=""/></td><td id="'+guild._id+'"></td><td class="right"><strong>'+guild.count+'</strong> obj</td><td class="right">'+guild.holdtime.toHHMMSS()+'</td></tr>');
				$.getJSON('rest/guilddetails/guildid/'+guild._id)
						.done(function(gitems) {
					$.each(gitems, function(gindex, guilddetail) {
						var guilddiv = $('#'+guilddetail.guild_id);
						guilddiv.html('<strong>'+guilddetail.guild_name+'</strong> ['+guilddetail.tag+']');
					});
				});
			});
		});
	};

	selectMatch = function(event) {
                ga('send', 'pageview');
		event.preventDefault();
		match = $(this).children('a').attr('href').substring(1);
		title = $(this).children('a').html();
		getTopGuilds(match);
		app_router.navigate(match, false);
		$('#currentmatch').html(title);
	};

	getWorldNames = function(lang) {
		return $.getJSON('rest/pveworldnames/lang/' + lang)
				.done(function(n) {
			worlds.worldNames = n;
		});
	};

	getMatches = function() {
		matchselect = $('#wvwmatchselect');
		matchselect.html('');
		return $.getJSON('rest/wvwmatches')
				.done(function(matches) {
			matches.forEach(function(match) {
				red = worlds.getWorldName(match.red_world_id);
				green = worlds.getWorldName(match.green_world_id);
				blue = worlds.getWorldName(match.blue_world_id);
				matchselect.append('<li class="matchselect"><a id="match-' + match.wvw_match_id + '" href="#' + match.wvw_match_id + '">' + red + ' <small>vs</small> ' + green + ' <small>vs</small> ' + blue + '</a></li>');
			});
			$('.matchselect').click(selectMatch);
		});
	};

	AppRouter = Backbone.Router.extend({
		routes: {
			":match": "loadMatch"
        },
        loadMatch: function(match) {
			getTopGuilds(match);
			title = $('#match-'+match).html();
			$('#currentmatch').html(title);
			//console.log(title);
        }
	});
	$.when(getWorldNames('en')).done(function() {
		$.when(getMatches()).done(function() {
			app_router = new AppRouter;
			Backbone.history.start();
		});
	});
});
