/*
 * gw2live - GuildWars 2 Dynamic Map
 * 
 * Website: http://gw2map.com
 *
 * Copyright 2013   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
package net.zyclonite.gw2live.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.HashMap;
import java.util.Map;
import net.zyclonite.gw2live.model.JvmInfo;
import net.zyclonite.gw2live.model.MemInfo;
import net.zyclonite.gw2live.service.HazelcastCache;
import net.zyclonite.gw2live.util.AppConfig;
import net.zyclonite.gw2live.util.LocalCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;

/**
 *
 * @author zyclonite
 */
public class AdminRestHandler implements Handler<HttpServerRequest> {

    private static final Log LOG = LogFactory.getLog(AdminRestHandler.class);
    private final ObjectMapper mapper;
    private final String crossdomainpolicy;
    private final HazelcastCache hcache;

    public AdminRestHandler() {
        mapper = new ObjectMapper();
        hcache = HazelcastCache.getInstance();
        final AppConfig config = AppConfig.getInstance();
        crossdomainpolicy = config.getString("webservice.cross-domain-policy", "*");
    }

    @Override
    public void handle(final HttpServerRequest req) {
        req.response().setStatusCode(200);
        req.response().putHeader("Content-Type", "application/json; charset=utf-8");
        req.response().putHeader("Access-Control-Allow-Origin", crossdomainpolicy);
        final String endpoint = req.params().get("endpoint");
        LOG.debug("got request path: " + req.path());
        String output;
        try {
            switch (endpoint) {
                case "subscriber":
                    req.response().putHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                    req.response().putHeader("Pragma", "no-cache");
                    output = mapper.writeValueAsString(LocalCache.SUBSCRIBER);
                    break;
                case "master":
                    req.response().putHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                    req.response().putHeader("Pragma", "no-cache");
                    output = mapper.writeValueAsString(LocalCache.MASTER);
                    break;
                case "cluster":
                    req.response().putHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                    req.response().putHeader("Pragma", "no-cache");
                    output = mapper.writeValueAsString(hcache.getCluster().getMembers());
                    break;
                case "jvm":
                    req.response().putHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                    req.response().putHeader("Pragma", "no-cache");
                    final JvmInfo jvm = new JvmInfo();
                    jvm.setThreadCount(ManagementFactory.getThreadMXBean().getThreadCount());
                    jvm.setCommitedHeap(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getCommitted());
                    jvm.setUsedHeap(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed());
                    jvm.setCommitedNonHeap(ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getCommitted());
                    jvm.setUsedNonHeap(ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed());
                    final Map<String, MemInfo> memory = new HashMap<>();
                    for(final MemoryPoolMXBean bean : ManagementFactory.getMemoryPoolMXBeans()){
                        final MemInfo meminfo = new MemInfo();
                        meminfo.setUsage(bean.getUsage());
                        meminfo.setPeakUsage(bean.getPeakUsage());
                        meminfo.setCollectionUsage(bean.getCollectionUsage());
                        memory.put(bean.getName(), meminfo);
                    }
                    jvm.setMemInfo(memory);
                    output = mapper.writeValueAsString(jvm);
                    break;
                case "pvecache":
                    req.response().putHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                    req.response().putHeader("Pragma", "no-cache");
                    output = mapper.writeValueAsString(hcache.getPveCacheMap().getLocalMapStats());
                    break;
                case "wvwcache":
                    req.response().putHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                    req.response().putHeader("Pragma", "no-cache");
                    output = mapper.writeValueAsString(hcache.getWvwCacheMap().getLocalMapStats());
                    break;
                case "channelcache":
                    req.response().putHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                    req.response().putHeader("Pragma", "no-cache");
                    output = mapper.writeValueAsString(hcache.getChannelMap().getLocalMultiMapStats());
                    break;
                default:
                    output = "{\"error\":\"wrong endpoint\"}";
            }
        } catch (JsonProcessingException ex) {
            output = "{\"error\":\"server error\"}";
            LOG.warn("could not translate object to json " + ex.getMessage());
        }
        req.response().end(output);
    }
}
