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
import net.zyclonite.gw2live.service.Gw2Client;
import net.zyclonite.gw2live.service.MongoDB;
import net.zyclonite.gw2live.util.LocalCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vertx.java.core.Handler;

/**
 *
 * @author zyclonite
 */
public class LessFrequentTimer implements Handler<Long> {

    private static final Log LOG = LogFactory.getLog(LessFrequentTimer.class);
    private final Gw2Client client;
    private final MongoDB db;
    private boolean stillRunning = false;

    public LessFrequentTimer() {
        client = Gw2Client.getInstance();
        db = MongoDB.getInstance();
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
            for (final String lang : LocalCache.LANGUAGES) {
                db.savePveEventNames(client.getPveEventNames(lang), lang);
                db.savePveMapNames(client.getPveMapNames(lang), lang);
                db.savePveWorldNames(client.getPveWorldNames(lang), lang);
                db.saveWvwObjectiveNames(client.getWvwObjectiveNames(lang), lang);
            }
            db.savePveEventDetails(client.getPveEventDetails().getEventDetails());
            db.saveWvwMatches(client.getWvwMatches().getWvw_matches());
            final long elapsedTime = System.currentTimeMillis() - startTime;
            LOG.info("LessFrequentTimer update took " + elapsedTime + " ms");
            stillRunning = false;
        }
    }
}
