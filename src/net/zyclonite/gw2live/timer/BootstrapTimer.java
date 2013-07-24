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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import net.zyclonite.gw2live.Application;
import net.zyclonite.gw2live.listener.PveEventListener;
import net.zyclonite.gw2live.listener.StatisticUpdateListener;
import net.zyclonite.gw2live.listener.WvwEventListener;
import net.zyclonite.gw2live.model.PveEvent;
import net.zyclonite.gw2live.model.WvwEvent;
import net.zyclonite.gw2live.service.Gw2Client;
import net.zyclonite.gw2live.service.HazelcastCache;
import net.zyclonite.gw2live.service.MongoDB;
import net.zyclonite.gw2live.threads.PveEventMatcher;
import net.zyclonite.gw2live.threads.WvwEventMatcher;
import net.zyclonite.gw2live.util.LocalCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mongojack.DBCursor;
import org.vertx.java.core.Handler;

/**
 *
 * @author zyclonite
 */
public class BootstrapTimer implements Handler<Long> {

    private static final Log LOG = LogFactory.getLog(BootstrapTimer.class);
    private final IMap<Integer, PveEvent> pveEventCache;
    private final IMap<Integer, WvwEvent> wvwEventCache;
    private final Gw2Client client;
    private final MongoDB db;
    private final ExecutorService es;

    public BootstrapTimer() {
        final HazelcastCache hz = HazelcastCache.getInstance();
        pveEventCache = hz.getPveCacheMap();
        wvwEventCache = hz.getWvwCacheMap();
        client = Gw2Client.getInstance();
        db = MongoDB.getInstance();
        es = Executors.newFixedThreadPool(2);
    }

    @Override
    public void handle(final Long timerId) {
        if (LocalCache.MASTER) {
            bootstrapDB();
            bootstrapCache();
            startStatisticListeners();
            Application.switchMaster();
        }
        registerListeners();
    }

    private void bootstrapDB() {
        final long startTime = System.currentTimeMillis();
        LOG.debug("Boostrapping db...");
        for (final String lang : LocalCache.LANGUAGES) {
            db.savePveEventNames(client.getPveEventNames(lang), lang);
            db.savePveMapNames(client.getPveMapNames(lang), lang);
            db.savePveWorldNames(client.getPveWorldNames(lang), lang);
            db.saveWvwObjectiveNames(client.getWvwObjectiveNames(lang), lang);
        }
        db.savePveEventDetails(client.getPveEventDetails().getEventDetails());
        db.saveWvwMatches(client.getWvwMatches().getWvw_matches());
        db.saveMaps(client.getMaps().getMapList());
        LOG.debug("Boostrapping db DONE");
        final long elapsedTime = System.currentTimeMillis() - startTime;
        LOG.info("Bootstrapping the database took " + elapsedTime + " ms");
    }

    private void bootstrapCache() {
        final long startTime = System.currentTimeMillis();
        LOG.debug("Boostrapping cache...");
        final DBCursor<PveEvent> pveevents = db.findPveEvents();
        pveevents.batchSize(5000);
        for (final PveEvent pveevent : pveevents) {
            pveEventCache.put(pveevent.hashCode(), pveevent);
        }
        pveevents.close();

        final DBCursor<WvwEvent> wvwevents = db.findWvwEvents();
        wvwevents.batchSize(5000);
        for (final WvwEvent wvwevent : wvwevents) {
            wvwEventCache.put(wvwevent.hashCode(), wvwevent);
        }
        wvwevents.close();

        final Future<Boolean> pveready = es.submit(new PveEventMatcher());
        final Future<Boolean> wvwready = es.submit(new WvwEventMatcher());
        try {
            if (!pveready.get() || !wvwready.get()) {
                LOG.warn("one of the tasks did not finish");
            }
        } catch (InterruptedException | ExecutionException ex) {
            LOG.error(ex);
        }
        LOG.debug("Boostrapping cache DONE");
        final long elapsedTime = System.currentTimeMillis() - startTime;
        LOG.info("Bootstrapping the cache took " + elapsedTime + " ms");
    }

    private void registerListeners() {
        pveEventCache.addEntryListener(new PveEventListener(), true);
        wvwEventCache.addEntryListener(new WvwEventListener(), true);
    }

    private void startStatisticListeners() {
        for (final StatisticUpdateListener statement : LocalCache.STATEMENTS) {
            statement.start();
        }
    }
}
