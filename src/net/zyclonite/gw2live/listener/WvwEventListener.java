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
import net.zyclonite.gw2live.model.WvwEvent;
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
public class WvwEventListener implements EntryListener<Integer, WvwEvent> {

    private static final Log LOG = LogFactory.getLog(WvwEventListener.class);
    private final VertX vertx;
    private final ObjectMapper mapper;
    private final Object sync = new Object();

    public WvwEventListener() {
        vertx = VertX.getInstance();
        mapper = new ObjectMapper();
    }

    @Override
    public void entryAdded(final EntryEvent<Integer, WvwEvent> event) {
        final WvwEvent wvwevent = event.getValue();
        update(wvwevent);
    }

    @Override
    public void entryRemoved(final EntryEvent<Integer, WvwEvent> event) {
    }

    @Override
    public void entryUpdated(final EntryEvent<Integer, WvwEvent> event) {
        final WvwEvent wvwevent = event.getValue();
        update(wvwevent);
    }

    public void update(final WvwEvent wvwevent) {
        final String match_id = wvwevent.getMatch_id();
        synchronized (sync) {
            if (!LocalCache.WVW_EVENT_LISTENERS.containsKey(match_id)) {
                final Handler<Message<Buffer>> worldListener = new WorldEventListener(match_id);
                LocalCache.WVW_EVENT_LISTENERS.put(match_id, worldListener);
                vertx.getEventBus().registerHandler(LocalCache.EVENTS_WVW_PREFIX + match_id, worldListener);
            }
        }
        try {
            final LiveEvent message = new LiveEvent();
            message.setType("wvwevent");
            message.setData(wvwevent);
            final String json = mapper.writeValueAsString(message);
            vertx.getEventBus().publish(LocalCache.EVENTS_WVW_PREFIX + match_id, new Buffer().appendString(json));
        } catch (JsonProcessingException ex) {
            LOG.warn("could not translate event object to json " + ex.getMessage());
        }
    }

    @Override
    public void entryEvicted(final EntryEvent<Integer, WvwEvent> event) {
    }
}
