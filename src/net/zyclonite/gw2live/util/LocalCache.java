/*
 * gw2live - GuildWars 2 Dynamic Map
 * 
 * Website: http://gw2map.com
 *
 * Copyright 2013   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
package net.zyclonite.gw2live.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.zyclonite.gw2live.listener.StatisticUpdateListener;
import net.zyclonite.gw2live.model.Subscriber;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;

/**
 *
 * @author zyclonite
 */
public class LocalCache {

    public final static Map<Long, Handler<Message<Buffer>>> PVE_EVENT_LISTENERS = new ConcurrentHashMap<>();
    public final static Map<String, Handler<Message<Buffer>>> WVW_EVENT_LISTENERS = new ConcurrentHashMap<>();
    public final static List<Long> TIMERS = Collections.synchronizedList(new ArrayList<Long>());
    public final static List<Subscriber> SUBSCRIBER = Collections.synchronizedList(new ArrayList<Subscriber>());
    public final static List<StatisticUpdateListener> STATEMENTS = new ArrayList<>();
    public static boolean MASTER = true;
    public final static String[] LANGUAGES = {"en", "de", "es", "fr"};
    public final static String EVENTS_PVE_PREFIX = "events.pve.";
    public final static String EVENTS_WVW_PREFIX = "events.wvw.";
}
