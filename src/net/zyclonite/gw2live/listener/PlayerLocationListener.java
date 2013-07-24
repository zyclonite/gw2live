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

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.zyclonite.gw2live.model.PlayerLocation;
import net.zyclonite.gw2live.threads.PLMessageDispatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author zyclonite
 */
public class PlayerLocationListener implements MessageListener<PlayerLocation> {

    private static final Log LOG = LogFactory.getLog(PlayerLocationListener.class);
    private final ExecutorService es;

    public PlayerLocationListener() {
        es = Executors.newFixedThreadPool(5);
        LOG.debug("playerlocationlistener initialized");
    }

    @Override
    public void onMessage(final Message<PlayerLocation> message) {
        final PlayerLocation playerlocation = message.getMessageObject();
        es.submit(new PLMessageDispatcher(playerlocation));
    }
}
