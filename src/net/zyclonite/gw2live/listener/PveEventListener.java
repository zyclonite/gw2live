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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import net.zyclonite.gw2live.model.LiveEvent;
import net.zyclonite.gw2live.model.PveEvent;
import net.zyclonite.gw2live.service.VertX;
import net.zyclonite.gw2live.util.LocalCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;

/**
 *
 * @author zyclonite
 */
public class PveEventListener implements EntryListener<Integer, PveEvent> {

    private static final Log LOG = LogFactory.getLog(PveEventListener.class);
    private final VertX vertx;
    private final ObjectMapper mapper;
    private final Object sync = new Object();

    public PveEventListener() {
        vertx = VertX.getInstance();
        mapper = new ObjectMapper();
    }

    @Override
    public void entryAdded(final EntryEvent<Integer, PveEvent> event) {
        final PveEvent pveevent = event.getValue();
        update(pveevent);
    }

    @Override
    public void entryRemoved(final EntryEvent<Integer, PveEvent> event) {
    }

    @Override
    public void entryUpdated(final EntryEvent<Integer, PveEvent> event) {
        final PveEvent pveevent = event.getValue();
        update(pveevent);
    }

    public void update(final PveEvent pveevent) {
        final Long world_id = pveevent.getWorld_id();
        synchronized (sync) {
            if (!LocalCache.PVE_EVENT_LISTENERS.containsKey(world_id)) {
                final Handler<Message<Buffer>> worldListener = new WorldEventListener(world_id.toString());
                LocalCache.PVE_EVENT_LISTENERS.put(world_id, worldListener);
                vertx.getEventBus().registerHandler(LocalCache.EVENTS_PVE_PREFIX + world_id, worldListener);
            }
        }
        try {
            final LiveEvent message = new LiveEvent();
            message.setType("pveevent");
            message.setData(pveevent);
            final String json = mapper.writeValueAsString(message);
            vertx.getEventBus().publish(LocalCache.EVENTS_PVE_PREFIX + world_id, new Buffer().appendString(json));
        } catch (JsonProcessingException ex) {
            LOG.warn("could not translate event object to json " + ex.getMessage());
        }
    }

    @Override
    public void entryEvicted(final EntryEvent<Integer, PveEvent> event) {
    }
}
