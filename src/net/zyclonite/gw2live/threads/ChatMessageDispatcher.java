/*
 * gw2live - GuildWars 2 Dynamic Map
 * 
 * Website: http://gw2map.com
 *
 * Copyright 2013   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
package net.zyclonite.gw2live.threads;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.concurrent.Callable;
import net.zyclonite.gw2live.model.ChatMessage;
import net.zyclonite.gw2live.model.LiveEvent;
import net.zyclonite.gw2live.model.Subscriber;
import net.zyclonite.gw2live.service.EsperEngine;
import net.zyclonite.gw2live.service.HazelcastCache;
import net.zyclonite.gw2live.service.VertX;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.EventBus;

/**
 *
 * @author zyclonite
 */
public class ChatMessageDispatcher implements Callable<Boolean> {

    private static final Log LOG = LogFactory.getLog(ChatMessageDispatcher.class);
    private final ObjectMapper mapper;
    private final EventBus eb;
    private final HazelcastCache hcache;
    private final ChatMessage chatmessage;
    private final EsperEngine esper;

    public ChatMessageDispatcher(final ChatMessage chatmessage) {
        final VertX vertx = VertX.getInstance();
        mapper = new ObjectMapper();
        eb = vertx.getEventBus();
        hcache = HazelcastCache.getInstance();
        esper = EsperEngine.getInstance();
        this.chatmessage = chatmessage;
    }

    private Boolean dispatchChatMessage() {
        final Collection<Subscriber> members = hcache.getChannelMap().get(chatmessage.getChannel());
        for (final Subscriber subscriber : members) {
            if (hcache.getNodeId().equals(subscriber.getNodeId())) {
                final LiveEvent liveevent = new LiveEvent();
                liveevent.setType("chatevent");
                liveevent.setData(chatmessage);
                try {
                    final String json = mapper.writeValueAsString(liveevent);
                    eb.publish(subscriber.getConnection(), new Buffer().appendString(json));
                    LOG.trace("Published chat event to actorid: " + subscriber.getConnection() + " in channel: " + chatmessage.getChannel());
                } catch (JsonProcessingException ex) {
                    LOG.warn("Could not send chat event to channel member: " + ex.getMessage(), ex);
                }
            }
        }
        chatmessage.setMessage(null);//remove payload to save memory in esper
        esper.sendEvent(chatmessage);
        return true;
    }

    @Override
    public Boolean call() throws Exception {
        return dispatchChatMessage();
    }
}