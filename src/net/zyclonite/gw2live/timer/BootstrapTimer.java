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

import net.zyclonite.gw2live.Application;
import net.zyclonite.gw2live.listener.StatisticUpdateListener;
import net.zyclonite.gw2live.util.LocalCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vertx.java.core.Handler;

/**
 *
 * @author zyclonite
 */
public class BootstrapTimer extends UpdateTimer implements Handler<Long> {

    private static final Log LOG = LogFactory.getLog(BootstrapTimer.class);

    public BootstrapTimer() {
        super();
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
        updateDB();
        LOG.debug("Boostrapping db DONE");
        final long elapsedTime = System.currentTimeMillis() - startTime;
        LOG.info("Bootstrapping the database took " + elapsedTime + " ms");
    }

    private void bootstrapCache() {
        final long startTime = System.currentTimeMillis();
        LOG.debug("Boostrapping cache...");
        prefillCache();
        updateCache();
        LOG.debug("Boostrapping cache DONE");
        final long elapsedTime = System.currentTimeMillis() - startTime;
        LOG.info("Bootstrapping the cache took " + elapsedTime + " ms");
    }

    private void startStatisticListeners() {
        for (final StatisticUpdateListener statement : LocalCache.STATEMENTS) {
            statement.start();
        }
    }
}
