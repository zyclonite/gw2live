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
import java.util.Date;
import net.zyclonite.gw2live.listener.StatisticUpdateListener;
import net.zyclonite.gw2live.service.MongoDB;
import net.zyclonite.gw2live.util.AppConfig;
import net.zyclonite.gw2live.util.LocalCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

/**
 *
 * @author zyclonite
 */
public class StatRestHandler implements Handler<HttpServerRequest> {

    private static final Log LOG = LogFactory.getLog(StatRestHandler.class);
    private final ObjectMapper mapper;
    private final MongoDB db;
    private final String crossdomainpolicy;

    public StatRestHandler() {
        mapper = new ObjectMapper();
        db = MongoDB.getInstance();
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
        switch (endpoint) {
            case "list":
                req.response().putHeader("Cache-Control", "max-age=21600");//cache for 6h
                final JsonArray response = new JsonArray();
                for (final StatisticUpdateListener statement : LocalCache.STATEMENTS) {
                    final JsonObject service = new JsonObject();
                    service.putString("name", statement.getName().toLowerCase());
                    final JsonArray out = new JsonArray();
                    for (final String value : statement.getOutput()) {
                        out.addString(value);
                    }
                    service.putArray("output", out);
                    response.addObject(service);
                }
                output = response.encode();
                break;
            case "get":
                req.response().putHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                req.response().putHeader("Pragma", "no-cache");
                if (req.params().contains("id")) {
                    try {
                        if (req.params().contains("date")) {
                            Date date = new Date();
                            try {
                                date = new Date(Long.parseLong(req.params().get("date")));
                            } catch (NumberFormatException e) {
                            }
                            if (req.params().contains("limit")) {
                                int limit = 1000;
                                try {
                                    limit = Integer.parseInt(req.params().get("limit"));
                                } catch (NumberFormatException e) {
                                }
                                output = mapper.writeValueAsString(db.findStats(req.params().get("id").toLowerCase(), date, limit));
                            } else {
                                output = mapper.writeValueAsString(db.findStats(req.params().get("id").toLowerCase(), date));
                            }
                        } else {
                            output = mapper.writeValueAsString(db.findStats(req.params().get("id").toLowerCase()));
                        }
                    } catch (JsonProcessingException ex) {
                        output = "{\"error\":\"server error\"}";
                        LOG.warn("could not translate object to json " + ex.getMessage());
                    }
                } else {
                    output = "{\"error\":\"missing id parameter\"}";
                }
                break;
            default:
                output = "{\"error\":\"wrong endpoint\"}";
        }
        req.response().end(output);
    }
}
