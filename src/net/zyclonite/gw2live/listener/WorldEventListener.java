/*
 * gw2live - GuildWars 2 Dynamic Map
 * 
 * Website: http://gw2map.com
 *
 * Copyright 2013   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
package net.zyclonite.gw2live.listener;

import java.util.List;
import net.zyclonite.gw2live.model.Subscriber;
import net.zyclonite.gw2live.service.HazelcastCache;
import net.zyclonite.gw2live.service.VertX;
import net.zyclonite.gw2live.util.LocalCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;

/**
 *
 * @author zyclonite
 */
public class WorldEventListener implements Handler<Message<Buffer>> {

    private static final Log LOG = LogFactory.getLog(WorldEventListener.class);
    private final List<Subscriber> subscribers;
    private final EventBus eb;
    private final String id;
    private final HazelcastCache hcache;

    public WorldEventListener(final String id) {
        final VertX vertx = VertX.getInstance();
        this.id = id;
        hcache = HazelcastCache.getInstance();
        subscribers = LocalCache.SUBSCRIBER;
        eb = vertx.getEventBus();
        LOG.debug("WorldEventListener for world/match: " + id + " initialized");
    }

    @Override
    public void handle(final Message<Buffer> message) {
        for (final Subscriber subscriber : subscribers) {
            if (id.equals(subscriber.getSubscriptionId()) && hcache.getNodeId().equals(subscriber.getNodeId())) {
                eb.publish(subscriber.getConnection(), message.body());
                LOG.trace("Published events to actorid: " + subscriber.getConnection() + " subscribed to world/match: " + id);
            }
        }
    }
}
