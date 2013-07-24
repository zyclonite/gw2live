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
import net.zyclonite.gw2live.model.LiveEvent;
import net.zyclonite.gw2live.model.PlayerLocation;
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
public class PLMessageDispatcher implements Callable<Boolean> {

    private static final Log LOG = LogFactory.getLog(PLMessageDispatcher.class);
    private final ObjectMapper mapper;
    private final EventBus eb;
    private final HazelcastCache hcache;
    private final PlayerLocation playerlocation;
    private final EsperEngine esper;

    public PLMessageDispatcher(final PlayerLocation playerlocation) {
        final VertX vertx = VertX.getInstance();
        mapper = new ObjectMapper();
        eb = vertx.getEventBus();
        hcache = HazelcastCache.getInstance();
        esper = EsperEngine.getInstance();
        this.playerlocation = playerlocation;
    }

    private Boolean dispatchChatMessage() {
        final Collection<Subscriber> members = hcache.getChannelMap().get(playerlocation.getChannel());
        for (final Subscriber subscriber : members) {
            if (hcache.getNodeId().equals(subscriber.getNodeId())) {
                final LiveEvent liveevent = new LiveEvent();
                liveevent.setType("playerlocation");
                liveevent.setData(playerlocation);
                try {
                    final String json = mapper.writeValueAsString(liveevent);
                    eb.publish(subscriber.getConnection(), new Buffer().appendString(json));
                    LOG.trace("Published playerlocation event to actorid: " + subscriber.getConnection() + " in channel: " + playerlocation.getChannel());
                } catch (JsonProcessingException ex) {
                    LOG.warn("Could not send playerlocation event to channel member: " + ex.getMessage(), ex);
                }
            }
        }
        esper.sendEvent(playerlocation);
        return true;
    }

    @Override
    public Boolean call() throws Exception {
        return dispatchChatMessage();
    }
}