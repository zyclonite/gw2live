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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Date;
import net.zyclonite.gw2live.model.PlayerLocation;
import net.zyclonite.gw2live.service.HazelcastCache;
import net.zyclonite.gw2live.util.AppConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;

/**
 *
 * @author zyclonite
 */
public class PostHandler implements Handler<HttpServerRequest> {

    private static final Log LOG = LogFactory.getLog(PostHandler.class);
    private final ObjectMapper mapper;
    private final HazelcastCache hcache;
    private final String crossdomainpolicy;

    public PostHandler() {
        hcache = HazelcastCache.getInstance();
        mapper = new ObjectMapper();
        final AppConfig config = AppConfig.getInstance();
        crossdomainpolicy = config.getString("webservice.cross-domain-policy", "*");
    }

    @Override
    public void handle(final HttpServerRequest req) {
        req.response().setStatusCode(200);
        req.response().putHeader("Content-Type", "application/json; charset=utf-8");
        req.response().putHeader("Access-Control-Allow-Origin", crossdomainpolicy);
        LOG.debug("got POST request path: " + req.path());
        final String endpoint = req.params().get("endpoint");
        req.dataHandler(new Handler<Buffer>() {
            @Override
            public void handle(final Buffer buffer) {
                String output;
                try {
                    switch (endpoint) {
                        case "playerlocation":
                            req.response().putHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                            req.response().putHeader("Pragma", "no-cache");
                            final PlayerLocation location = mapper.readValue(buffer.getBytes(), PlayerLocation.class);
                            location.setTimestamp(new Date());
                            hcache.getPlayerLocationTopic().publish(location);
                            output = "{\"message\":\"success\"}";
                            break;
                        default:
                            output = "{\"error\":\"wrong endpoint\"}";
                    }
                } catch (IOException ex) {
                    output = "{\"error\":\"server error\"}";
                    LOG.warn("could not translate object to json " + ex.getMessage());
                }
                req.response().end(output);
            }
        });
    }
}
