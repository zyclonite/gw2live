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

import com.hazelcast.core.IMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import net.zyclonite.gw2live.model.PveEvent;
import net.zyclonite.gw2live.service.EsperEngine;
import net.zyclonite.gw2live.service.Gw2Client;
import net.zyclonite.gw2live.service.HazelcastCache;
import net.zyclonite.gw2live.service.MongoDB;

/**
 *
 * @author zyclonite
 */
public class PveEventMatcher implements Callable<Boolean> {

    private final IMap<Integer, PveEvent> pveEventCache;
    private final Gw2Client client;
    private final MongoDB db;
    private final EsperEngine esper;
    private final Date timestamp;

    public PveEventMatcher() {
        final HazelcastCache hz = HazelcastCache.getInstance();
        pveEventCache = hz.getPveCacheMap();
        client = Gw2Client.getInstance();
        db = MongoDB.getInstance();
        esper = EsperEngine.getInstance();
        timestamp = new Date();
    }

    private Boolean getPveEvents() {
        final List<PveEvent> pveevents = new ArrayList<>();
        final List<PveEvent> events = client.getPveEvents(null, null).getEvents();
        for (final PveEvent event : events) {
            final PveEvent oldEvent = pveEventCache.get(event.hashCode());
            if (!event.equals(oldEvent)) {
                event.setTimestamp(timestamp);
                pveEventCache.putAsync(event.hashCode(), event);
                esper.sendEvent(event);
                pveevents.add(event);
            }
        }
        db.savePveEvents(pveevents);
        return true;
    }

    @Override
    public Boolean call() throws Exception {
        return getPveEvents();
    }
}