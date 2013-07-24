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
    var subscription;

    subscription = new Object();
    subscription.sock = null;
    subscription.connected = false;

    subscription.init = function() {
        if (subscription.sock === null && subscription.connected === false) {
            subscription.sock = new SockJS("http://127.0.0.1:8383/stream");
            subscription.sock.onopen = function() {
                subscription.connected = true;
                console.log('open');
                var subscribe = new Object();
                subscribe.type = "channel";
                subscribe.subscription_id = "1234567890123";
                subscribe.nickname = "testnick";
                subscription.send(subscribe);
                console.log('subscribing to chat channel ' + subscribe.subscription_id);
            };

            subscription.sock.onmessage = function(e) {
                console.log('message', e.data);
                $("#chat").prepend('<li>' + e.data + '</li>');
                $("#chat li:gt(9)").fadeOut(500, function() {
                    $(this).remove();
                });
            };

            subscription.sock.onclose = function() {
                subscription.connected = false;
                subscription.stop();
                console.log('close');
                setTimeout(function(){
                    subscription.reconnect();
                }, 5000);
            };
        }
    };
    
    subscription.send = function(message) {
        if (subscription.sock !== null && subscription.connected === true){
            subscription.sock.send(JSON.stringify(message));
        }
    };
    
    subscription.stop = function() {
        if (subscription.sock !== null){
            subscription.sock.close();
        }
        subscription.sock = null;
    };
    
    subscription.reconnect = function() {
        if(subscription.connected === false){
            subscription.init();
            setTimeout(function(){
                subscription.reconnect();
            }, 5000);
        }
    };

    $('#chatevent').click(function() {
        var message;

        message = new Object();
        message.type = "chat";
        message.message = "test message";
        message.icon = "torch";
        message.x = 123;
        message.y = 456;
        subscription.send(message);
        console.log('sent message to channel ' + message.message);
    });
    $('#pveevent').click(function() {
        var subscribe;

        subscribe = new Object();
        subscribe.type = "pve";
        subscribe.subscription_id = "2006";
        subscription.send(subscribe);
        console.log('subscribing to world ' + subscribe.subscription_id);
    });
    $('#wvwevent').click(function() {
        var subscribe;

        subscribe = new Object();
        subscribe.type = "wvw";
        subscribe.subscription_id = "2-2";
        subscription.send(subscribe);
        console.log('subscribing to match ' + subscribe.subscription_id);
    });
    subscription.init();
});
