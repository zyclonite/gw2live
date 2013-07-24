/*
 * gw2live - GuildWars 2 Dynamic Map
 * 
 * Website: http://gw2map.com
 *
 * Copyright 2013   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
package net.zyclonite.gw2live.service;

import com.hazelcast.config.Config;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.Interfaces;
import com.hazelcast.config.Join;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Member;
import com.hazelcast.core.MultiMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import net.zyclonite.gw2live.model.ChatMessage;
import net.zyclonite.gw2live.model.PlayerLocation;
import net.zyclonite.gw2live.model.PveEvent;
import net.zyclonite.gw2live.model.Subscriber;
import net.zyclonite.gw2live.model.WvwEvent;
import net.zyclonite.gw2live.util.AppConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author zyclonite
 */
public class HazelcastCache {

    private static final Log LOG = LogFactory.getLog(HazelcastCache.class);
    private static HazelcastCache instance;
    private static HazelcastInstance cache;

    static {
        instance = new HazelcastCache();
    }

    private HazelcastCache() {
        final AppConfig config = AppConfig.getInstance();
        final Map<String, MapConfig> mapconfigs = new HashMap<>();
        GroupConfig groupconfig = new GroupConfig();
        groupconfig.setName(config.getString("cluster.name", "gw2live"));
        groupconfig.setPassword(config.getString("cluster.password", "gw2live"));
        final MapConfig mapconfig = new MapConfig();
        mapconfig.getMaxSizeConfig().setMaxSizePolicy("cluster_wide_map_size");
        mapconfig.getMaxSizeConfig().setSize(0);
        mapconfig.setEvictionPolicy(MapConfig.DEFAULT_EVICTION_POLICY);
        mapconfig.setBackupCount(1);
        mapconfigs.put("*-cache", mapconfig);
        final NetworkConfig nwconfig = new NetworkConfig();
        if(config.containsKey("cluster.interface")) {
            final Interfaces interfaces = new Interfaces();
            interfaces.addInterface(config.getString("cluster.interface"));
            interfaces.setEnabled(true);
            nwconfig.setInterfaces(interfaces);
        }
        nwconfig.setPort(config.getInteger("cluster.port", 5801));
        nwconfig.setPortAutoIncrement(true);
        final MulticastConfig mcconfig = new MulticastConfig();
        mcconfig.setEnabled(true);
        mcconfig.setMulticastGroup(config.getString("cluster.multicast.group", "224.2.2.3"));
        mcconfig.setMulticastPort(config.getInteger("cluster.multicast.port", 58011));
        mcconfig.setMulticastTimeToLive(MulticastConfig.DEFAULT_MULTICAST_TTL);
        mcconfig.setMulticastTimeoutSeconds(MulticastConfig.DEFAULT_MULTICAST_TIMEOUT_SECONDS);
        final Join join = new Join();
        join.setMulticastConfig(mcconfig);
        nwconfig.setJoin(join);
        final ExecutorConfig execconfig = new ExecutorConfig();
        execconfig.setCorePoolSize(4);
        execconfig.setKeepAliveSeconds(60);
        execconfig.setMaxPoolSize(20);
        final Map<String, ExecutorConfig> execmap = new HashMap<>();
        execmap.put("default", execconfig);
        final Config hconfig = new Config();
        hconfig.setInstanceName("gw2live");
        hconfig.setGroupConfig(groupconfig);
        hconfig.setMapConfigs(mapconfigs);
        hconfig.setNetworkConfig(nwconfig);
        hconfig.setExecutorConfigMap(execmap);
        hconfig.setProperty("hazelcast.shutdownhook.enabled", "false");
        hconfig.setProperty("hazelcast.wait.seconds.before.join", "0");
        hconfig.setProperty("hazelcast.rest.enabled", "false");
        hconfig.setProperty("hazelcast.memcache.enabled", "false");
        hconfig.setProperty("hazelcast.mancenter.enabled", "false");
        hconfig.setProperty("hazelcast.logging.type", "log4j");
        cache = Hazelcast.newHazelcastInstance(hconfig);

        LOG.debug("Hazelcast initialized");
    }

    public String getNodeId() {
        return cache.getCluster().getLocalMember().getUuid();
    }

    public Cluster getCluster() {
        return cache.getCluster();
    }

    public Set<Member> getMembers() {
        return cache.getCluster().getMembers();
    }

    public ExecutorService getExecutorService() {
        return cache.getExecutorService();
    }

    public IMap<Integer, PveEvent> getPveCacheMap() {
        return cache.getMap("pve-event-cache");
    }

    public IMap<Integer, WvwEvent> getWvwCacheMap() {
        return cache.getMap("wvw-event-cache");
    }

    public MultiMap<String, Subscriber> getChannelMap() {
        return cache.getMultiMap("channel-cache");
    }

    public ITopic<ChatMessage> getChatTopic() {
        return cache.getTopic("chattopic");
    }

    public ITopic<PlayerLocation> getPlayerLocationTopic() {
        return cache.getTopic("pltopic");
    }

    public static HazelcastCache getInstance() {
        return instance;
    }
}
