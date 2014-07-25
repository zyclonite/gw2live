/*
 * gw2live - GuildWars 2 Dynamic Map
 * 
 * Website: http://gw2map.com
 *
 * Copyright 2013   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
package net.zyclonite.gw2live.handler;

import java.util.Date;
import java.util.List;
import net.zyclonite.gw2live.model.ChatMessage;
import net.zyclonite.gw2live.model.Subscriber;
import net.zyclonite.gw2live.service.HazelcastCache;
import net.zyclonite.gw2live.service.MongoDB;
import net.zyclonite.gw2live.util.LocalCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.VoidHandler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSSocket;

/**
 *
 * @author zyclonite
 */
public class SockJsHandler implements Handler<SockJSSocket> {

    private static final Log LOG = LogFactory.getLog(SockJsHandler.class);
    private static final int QUEUEMAXSIZE = 100;
    private final List<Subscriber> subscribers;
    private final HazelcastCache hcache;
    private final MongoDB db;

    public SockJsHandler() {
        hcache = HazelcastCache.getInstance();
        db = MongoDB.getInstance();
        subscribers = LocalCache.SUBSCRIBER;
        LOG.debug("SockJsHandler initialized");
    }

    @Override
    public void handle(final SockJSSocket sock) {
        LOG.debug("Handle connection " + sock.writeHandlerID());
        sock.setWriteQueueMaxSize(QUEUEMAXSIZE);
        final Subscriber subscriber = new Subscriber(sock.writeHandlerID(), hcache.getNodeId());
        subscribers.add(subscriber);
        sock.dataHandler(new Handler<Buffer>() {
            @Override
            public void handle(final Buffer inData) {
                LOG.trace("got data");
                final JsonObject json = new JsonObject(inData.getString(0, inData.length()));
                final String messageType = json.getString("type");
                if (messageType != null) {

                    switch (messageType) {
                        case "pve":
                        case "wvw":
                        case "channel":
                            try {
                                final JsonObject message = new JsonObject();

                                final String subscriptionId = json.getString("subscription_id");
                                if (messageType.equals("channel")) {
                                    if (subscriptionId.matches("[0-9]+") && subscriptionId.length() == 13) {
                                        final String nickname = json.getString("nickname");
                                        if (nickname != null) {
                                            subscriber.setNickname(nickname);
                                        }
                                        if (subscriber.getChannelId() != null) {
                                            hcache.getChannelMap().remove(subscriber.getChannelId(), subscriber);
                                        }
                                        hcache.getChannelMap().put(subscriptionId, subscriber);
                                        LOG.debug("added channel member " + subscriber.getConnection() + " to channel " + subscriptionId + " (membercount: " + hcache.getChannelMap().get(subscriptionId).size() + ")");
                                        subscriber.setChannelId(subscriptionId);
                                        message.putString("message", "success");
                                    } else {
                                        if (subscriber.getChannelId() != null) {
                                            hcache.getChannelMap().remove(subscriber.getChannelId(), subscriber);
                                        }
                                        subscriber.setChannelId(null);
                                        subscriber.setNickname(null);
                                        message.putString("message", "unsubscribed");
                                    }
                                } else {
                                    subscriber.setSubscriptionId(subscriptionId);
                                    message.putString("message", "success");
                                }

                                final JsonObject response = new JsonObject();
                                response.putString("type", "status");
                                message.putString("subscription_id", subscriptionId);
                                message.putString("type", messageType);
                                response.putObject("data", message);

                                final Buffer outData = new Buffer();
                                outData.appendString(response.encode());
                                sock.write(outData);
                                LOG.debug("subscribed to " + messageType + " " + subscriptionId);
                            } catch (Exception e) {
                                LOG.warn("Could not process subscription message!", e);
                            }
                            break;
                        case "chat":
                            try {
                                final ChatMessage chatmessage = new ChatMessage();
                                if (subscriber.getNickname() == null) {
                                    chatmessage.setSender(subscriber.getConnection());
                                } else {
                                    chatmessage.setSender(subscriber.getNickname());
                                }
                                chatmessage.setChannel(subscriber.getChannelId());
                                chatmessage.setMessage(json.getString("message"));
                                chatmessage.setIcon(json.getString("icon"));
                                try {
                                    chatmessage.setX(json.getInteger("x"));
                                } catch (ClassCastException e) {
                                    chatmessage.setX(0.0);
                                }
                                try {
                                    chatmessage.setY(json.getInteger("y"));
                                } catch (ClassCastException e) {
                                    chatmessage.setY(0.0);
                                }
                                chatmessage.setTimestamp(new Date());
                                hcache.getChatTopic().publish(chatmessage);
                                db.saveChatMessage(chatmessage);
                                LOG.debug("got chat message: " + chatmessage.getMessage() + " x=" + chatmessage.getX() + " y=" + chatmessage.getY() + " icon=" + chatmessage.getIcon() + " channel=" + chatmessage.getChannel());
                            } catch (Exception e) {
                                LOG.warn("Could not process chat message!", e);
                            }
                            break;
                    }
                } else {
                    LOG.debug("message had no type flag");
                }
            }
        });
        sock.endHandler(new VoidHandler() {
            @Override
            public void handle() {
                if (subscriber.getChannelId() != null) {
                    hcache.getChannelMap().remove(subscriber.getChannelId(), subscriber);
                }
                subscribers.remove(subscriber);
                LOG.debug("removed connection " + sock.writeHandlerID());
            }
        });
    }
}
