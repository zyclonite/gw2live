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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import net.zyclonite.gw2live.Application;
import net.zyclonite.gw2live.threads.PveEventMatcher;
import net.zyclonite.gw2live.threads.WvwEventMatcher;
import net.zyclonite.gw2live.util.LocalCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vertx.java.core.Handler;

/**
 *
 * @author zyclonite
 */
public class LiveEventTimer implements Handler<Long> {

    private static final Log LOG = LogFactory.getLog(LiveEventTimer.class);
    private boolean stillRunning = false;
    private final ExecutorService es;

    public LiveEventTimer() {
        es = Executors.newFixedThreadPool(2);
        LOG.debug("Timer initialized");
    }

    @Override
    public void handle(final Long timerId) {
        if(!LocalCache.MASTER) {
            Application.switchSlave();
        }
        if (stillRunning) {
            LOG.warn("Last update did not finish in time - skipping this one");
        } else {
            stillRunning = true;
            final long startTime = System.currentTimeMillis();
            final Future<Boolean> pveready = es.submit(new PveEventMatcher());
            final Future<Boolean> wvwready = es.submit(new WvwEventMatcher());
            try {
                if (!pveready.get() || !wvwready.get()) {
                    LOG.warn("one of the tasks did not finish");
                }
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
            }
            final long elapsedTime = System.currentTimeMillis() - startTime;
            LOG.info("LiveEventTimer update took " + elapsedTime + " ms");
            stillRunning = false;
        }
    }
}
