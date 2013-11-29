/*
 * gw2live - GuildWars 2 Dynamic Map
 * 
 * Website: http://gw2map.com
 *
 * Copyright 2013   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
package net.zyclonite.gw2live;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import net.zyclonite.gw2live.handler.AdminRestHandler;
import net.zyclonite.gw2live.handler.PostHandler;
import net.zyclonite.gw2live.handler.RestHandler;
import net.zyclonite.gw2live.handler.SockJsHandler;
import net.zyclonite.gw2live.handler.StatRestHandler;
import net.zyclonite.gw2live.listener.ChatListener;
import net.zyclonite.gw2live.listener.ClusterListener;
import net.zyclonite.gw2live.listener.PlayerLocationListener;
import net.zyclonite.gw2live.listener.StatisticUpdateListener;
import net.zyclonite.gw2live.model.Subscriber;
import net.zyclonite.gw2live.service.HazelcastCache;
import net.zyclonite.gw2live.service.VertX;
import net.zyclonite.gw2live.timer.BootstrapTimer;
import net.zyclonite.gw2live.timer.LessFrequentTimer;
import net.zyclonite.gw2live.timer.LiveEventTimer;
import net.zyclonite.gw2live.util.AppConfig;
import net.zyclonite.gw2live.util.LocalCache;
import net.zyclonite.gw2live.util.StaticDataLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.logging.julbridge.JULLog4jBridge;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

/**
 *
 * @author zyclonite
 */
public class Application {

    private static final Log LOG = LogFactory.getLog(Application.class);
    private final CountDownLatch stopLatch = new CountDownLatch(1);
    private final VertX vertx;
    private final HazelcastCache hazelcast;
    private final AppConfig config;
    private final Object sync = new Object();

    public Application() {
        config = AppConfig.getInstance();
        vertx = VertX.getInstance();
        hazelcast = HazelcastCache.getInstance();
        hazelcast.getCluster().addMembershipListener(new ClusterListener());
        hazelcast.getChatTopic().addMessageListener(new ChatListener());
        hazelcast.getPlayerLocationTopic().addMessageListener(new PlayerLocationListener());

        LocalCache.PVE_ENABLED = config.getBoolean("application.pve-enabled", false);
        LocalCache.WVW_ENABLED = config.getBoolean("application.wvw-enabled", false);
        
        loadStaticData();
        bootstrapApplication();
        initHandlers();
        initStatements();
    }

    private void loadStaticData() {
        final StaticDataLoader loader = new StaticDataLoader();
        loader.loadData();
        LOG.debug("Static data loaded");
    }

    private void bootstrapApplication() {
        vertx.setTimer(1000, new BootstrapTimer());
    }

    private void initHandlers() {
        vertx.registerPostHandler("/rest/:endpoint", new PostHandler());
        vertx.registerGetHandler("/rest/:endpoint", new RestHandler());
        vertx.registerGetHandler("/rest/:endpoint/lang/:lang", new RestHandler());
        vertx.registerGetHandler("/rest/:endpoint/world/:world", new RestHandler());
        vertx.registerGetHandler("/rest/:endpoint/world/:world/map/:map", new RestHandler());
        vertx.registerGetHandler("/rest/:endpoint/match/:match", new RestHandler());
        vertx.registerGetHandler("/rest/:endpoint/match/:match/map/:map", new RestHandler());
        vertx.registerGetHandler("/rest/:endpoint/guildid/:guildid", new RestHandler());
        vertx.registerGetHandler("/rest/:endpoint/guildname/:guildname", new RestHandler());
        vertx.registerGetHandler("/rest/:endpoint/channel/:channel", new RestHandler());
        vertx.registerGetHandler("/rest/:endpoint/event/:event", new RestHandler());
        vertx.registerGetHandler("/rest/:endpoint/map/:map", new RestHandler());
        vertx.registerGetHandler("/stats/:endpoint", new StatRestHandler());
        vertx.registerGetHandler("/stats/:endpoint/:id", new StatRestHandler());
        vertx.registerGetHandler("/stats/:endpoint/:id/:date", new StatRestHandler());
        vertx.registerGetHandler("/stats/:endpoint/:id/:date/:limit", new StatRestHandler());
        vertx.registerGetHandler("/admin/:endpoint", new AdminRestHandler());
        vertx.registerSockJsHandler("/stream", new SockJsHandler());
    }

    private void initStatements() {
        final Object obj = config.getProperty("statistics.statements.statement.name");
        if (obj instanceof Collection) {
            final int size = ((Collection) obj).size();
            for (int i = 0; i < size; i++) {
                final String name = config.getString("statistics.statements.statement(" + i + ").name");
                final String epl = config.getString("statistics.statements.statement(" + i + ").epl");
                final String[] output = config.getString("statistics.statements.statement(" + i + ").output").split(",");
                addStatement(name, epl, output);
            }
            LOG.debug("loaded " + size + " statements");
        } else if (obj instanceof String) {
            final String name = config.getString("statistics.statements.statement.name");
            final String epl = config.getString("statistics.statements.statement.epl");
            final String[] output = config.getString("statistics.statements.statement.output").split(",");
            addStatement(name, epl, output);
            LOG.debug("loaded one statement");
        } else {
            LOG.debug("no statements configured");
        }
    }

    private void addStatement(final String name, final String epl, final String[] output) {
        final StatisticUpdateListener statement = new StatisticUpdateListener(name, output, epl);
        LocalCache.STATEMENTS.add(statement);
    }
    
    private void sendAllConnectedUsers(final String message){
        final JsonObject response = new JsonObject();
        final JsonObject json = new JsonObject();
        json.putString("message", message);
        response.putString("type", "broadcast");
        response.putObject("data", json);
        final EventBus eb = vertx.getEventBus();
        for (final Subscriber subscriber : LocalCache.SUBSCRIBER) {
            if (hazelcast.getNodeId().equals(subscriber.getNodeId())) {
                eb.publish(subscriber.getConnection(), new Buffer().appendString(response.encode()));
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JULLog4jBridge.assimilate();
        DOMConfigurator.configureAndWatch(System.getProperty("user.dir") + "/log4j.xml", 60000);
        final Application main = new Application();
        LOG.info("Application started");
        main.addShutdownHook();
        main.block();
    }

    private void block() {
        while (true) {
            try {
                stopLatch.await();
                break;
            } catch (InterruptedException e) {
                //Ignore
            }
        }
    }

    private void unblock() {
        stopLatch.countDown();
    }

    public static synchronized void switchSlave() {
        for (final Long timer : LocalCache.TIMERS) {
            VertX.getInstance().cancelTimer(timer);
        }
        LocalCache.TIMERS.clear();
        for (final StatisticUpdateListener statement : LocalCache.STATEMENTS) {
            statement.stop();
        }
        LOG.debug("Timers cleared");
    }

    public static synchronized void switchMaster() {
        final AppConfig config = AppConfig.getInstance();
        LocalCache.TIMERS.add(VertX.getInstance().setPeriodic(config.getInt("application.timers.liveupdates", 10) * 1000, new LiveEventTimer()));
        LocalCache.TIMERS.add(VertX.getInstance().setPeriodic(config.getInt("application.timers.contentupdates", 30) * 1000, new LessFrequentTimer()));
        for (final StatisticUpdateListener statement : LocalCache.STATEMENTS) {
            statement.start();
        }
        LOG.debug("Timers started");
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOG.info("Application shutting down...");
                sendAllConnectedUsers("Server is going down for an update, please try to reconnect in some minutes");
                LOG.info("sent shutdown broadcast to all users");
                synchronized (sync) {
                    for (final Map.Entry<Long, Handler<Message<Buffer>>> entry : LocalCache.PVE_EVENT_LISTENERS.entrySet()) {
                        vertx.getEventBus().unregisterHandler(LocalCache.EVENTS_PVE_PREFIX + entry.getKey(), entry.getValue());
                    }
                    LocalCache.PVE_EVENT_LISTENERS.clear();
                    for (final Map.Entry<String, Handler<Message<Buffer>>> entry : LocalCache.WVW_EVENT_LISTENERS.entrySet()) {
                        vertx.getEventBus().unregisterHandler(LocalCache.EVENTS_WVW_PREFIX + entry.getKey(), entry.getValue());
                    }
                    LocalCache.WVW_EVENT_LISTENERS.clear();
                    switchSlave();
                }
                vertx.shutdown();
            }
        });
    }
}
