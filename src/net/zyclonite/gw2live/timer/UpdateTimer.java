/*
 * gw2live - GuildWars 2 Dynamic Map
 * 
 * Website: http://gw2map.com
 *
 * Copyright 2013   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
package net.zyclonite.gw2live.timer;

import com.hazelcast.core.IMap;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import net.zyclonite.gw2live.listener.PveEventListener;
import net.zyclonite.gw2live.listener.WvwEventListener;
import net.zyclonite.gw2live.model.PveEvent;
import net.zyclonite.gw2live.model.WvwEvent;
import net.zyclonite.gw2live.service.EsperEngine;
import net.zyclonite.gw2live.service.Gw2Client;
import net.zyclonite.gw2live.service.HazelcastCache;
import net.zyclonite.gw2live.service.MongoDB;
import net.zyclonite.gw2live.threads.PveEventMatcher;
import net.zyclonite.gw2live.threads.WvwEventMatcher;
import net.zyclonite.gw2live.util.LocalCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mongojack.DBCursor;

/**
 *
 * @author zyclonite
 */
public class UpdateTimer {

    private final ExecutorService es;
    private static final Log LOG = LogFactory.getLog(UpdateTimer.class);
    private final MongoDB db;
    private final Gw2Client client;
    private final EsperEngine esper;
    private final IMap<Integer, PveEvent> pveEventCache;
    private final IMap<Integer, WvwEvent> wvwEventCache;

    public UpdateTimer() {
        final HazelcastCache hz = HazelcastCache.getInstance();
        es = Executors.newFixedThreadPool(2);
        db = MongoDB.getInstance();
        client = Gw2Client.getInstance();
        pveEventCache = hz.getPveCacheMap();
        wvwEventCache = hz.getWvwCacheMap();
        esper = EsperEngine.getInstance();
    }

    protected void prefillCache() {
        if (LocalCache.PVE_ENABLED) {
            final DBCursor<PveEvent> pveevents = db.findPveEvents();
            pveevents.batchSize(5000);
            for (final PveEvent pveevent : pveevents) {
                pveEventCache.put(pveevent.hashCode(), pveevent);
                esper.sendEvent(pveevent);
            }
            pveevents.close();
        }

        if (LocalCache.WVW_ENABLED) {
            final DBCursor<WvwEvent> wvwevents = db.findWvwEvents();
            wvwevents.batchSize(5000);
            for (final WvwEvent wvwevent : wvwevents) {
                wvwEventCache.put(wvwevent.hashCode(), wvwevent);
                esper.sendEvent(wvwevent);
            }
            wvwevents.close();
        }

    }

    protected void updateCache() {
        final Collection<Future<Boolean>> threads = new LinkedList<>();
        if (LocalCache.PVE_ENABLED) {
            threads.add(es.submit(new PveEventMatcher()));
        }
        if (LocalCache.WVW_ENABLED) {
            threads.add(es.submit(new WvwEventMatcher()));
        }
        int finished = 0;
        for (final Future<Boolean> thread : threads) {
            try {
                if (thread.get()) {
                    finished++;
                }
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex, ex);
            }
        }
        if (finished < threads.size()) {
            LOG.warn(finished + " of " + threads.size() + " tasks did not finish");
        }
    }

    protected void updateDB() {
        for (final String lang : LocalCache.LANGUAGES) {
            if (LocalCache.PVE_ENABLED) {
                db.savePveEventNames(client.getPveEventNames(lang), lang);
                db.savePveMapNames(client.getPveMapNames(lang), lang);
            }
            if (LocalCache.PVE_ENABLED || LocalCache.WVW_ENABLED) {
                db.savePveWorldNames(client.getPveWorldNames(lang), lang);
            }
            if (LocalCache.WVW_ENABLED) {
                db.saveWvwObjectiveNames(client.getWvwObjectiveNames(lang), lang);
            }
        }
        if (LocalCache.PVE_ENABLED) {
            db.savePveEventDetails(client.getPveEventDetails().getEventDetails());
        }
        if (LocalCache.WVW_ENABLED) {
            db.saveWvwMatches(client.getWvwMatches().getWvw_matches());
        }
        if (LocalCache.PVE_ENABLED || LocalCache.WVW_ENABLED) {
            db.saveMaps(client.getMaps().getMapList());
        }
    }

    protected void registerListeners() {
        if (LocalCache.PVE_ENABLED) {
            pveEventCache.addEntryListener(new PveEventListener(), true);
        }
        if (LocalCache.WVW_ENABLED) {
            wvwEventCache.addEntryListener(new WvwEventListener(), true);
        }
    }
}
