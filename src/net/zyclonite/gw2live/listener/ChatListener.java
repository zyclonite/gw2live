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
import net.zyclonite.gw2live.model.ChatMessage;
import net.zyclonite.gw2live.threads.ChatMessageDispatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author zyclonite
 */
public class ChatListener implements MessageListener<ChatMessage> {

    private static final Log LOG = LogFactory.getLog(ChatListener.class);
    private final ExecutorService es;

    public ChatListener() {
        es = Executors.newFixedThreadPool(5);
        LOG.debug("chatlistener initialized");
    }

    @Override
    public void onMessage(final Message<ChatMessage> message) {
        final ChatMessage chatmessage = message.getMessageObject();
        es.submit(new ChatMessageDispatcher(chatmessage));
    }
}
