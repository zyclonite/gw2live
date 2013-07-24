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
import java.util.List;
import net.zyclonite.gw2live.model.GuildDetails;
import net.zyclonite.gw2live.service.Gw2Client;
import net.zyclonite.gw2live.service.MongoDB;
import net.zyclonite.gw2live.util.AppConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;

/**
 *
 * @author zyclonite
 */
public class RestHandler implements Handler<HttpServerRequest> {

    private static final Log LOG = LogFactory.getLog(RestHandler.class);
    private final ObjectMapper mapper;
    private final MongoDB db;
    private final Gw2Client client;
    private final String crossdomainpolicy;

    public RestHandler() {
        mapper = new ObjectMapper();
        db = MongoDB.getInstance();
        client = Gw2Client.getInstance();
        final AppConfig config = AppConfig.getInstance();
        crossdomainpolicy = config.getString("webservice.cross-domain-policy", "*");
    }

    @Override
    public void handle(final HttpServerRequest req) {
        req.response().setStatusCode(200);
        req.response().putHeader("Content-Type", "application/json; charset=utf-8");
        req.response().putHeader("Access-Control-Allow-Origin", crossdomainpolicy);
        LOG.debug("got GET request path: " + req.path());
        final String endpoint = req.params().get("endpoint");
        String output;
        try {
            switch (endpoint) {
                case "servertime":
                    req.response().putHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                    req.response().putHeader("Pragma", "no-cache");
                    output = mapper.writeValueAsString(new Date());
                    break;
                case "pveevents":
                    req.response().putHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                    req.response().putHeader("Pragma", "no-cache");
                    if (req.params().contains("world")) {
                        if (req.params().contains("map")) {
                            output = mapper.writeValueAsString(db.findPveEvents(Long.parseLong(req.params().get("world")), Long.parseLong(req.params().get("map"))));
                        } else {
                            output = mapper.writeValueAsString(db.findPveEvents(Long.parseLong(req.params().get("world"))));
                        }
                    } else {
                        output = "{\"error\":\"missing world parameter\"}";
                        //output = mapper.writeValueAsString(db.findPveEvents());
                    }
                    break;
                case "wvwevents":
                    req.response().putHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                    req.response().putHeader("Pragma", "no-cache");
                    if (req.params().contains("match")) {
                        if (req.params().contains("map")) {
                            output = mapper.writeValueAsString(db.findWvwEvents(req.params().get("match"), req.params().get("map")));
                        } else {
                            output = mapper.writeValueAsString(db.findWvwEvents(req.params().get("match")));
                        }
                    } else {
                        output = "{\"error\":\"missing match parameter\"}";
                        //output = mapper.writeValueAsString(db.findWvwEvents());
                    }
                    break;
                case "pveeventnames":
                    req.response().putHeader("Cache-Control", "max-age=600");//cache for 10min
                    if (req.params().contains("lang")) {
                        output = mapper.writeValueAsString(db.findPveEventNames(req.params().get("lang")));
                    } else {
                        output = mapper.writeValueAsString(db.findPveEventNames());
                    }
                    break;
                case "pvemapnames":
                    req.response().putHeader("Cache-Control", "max-age=3600");//cache for 1h
                    if (req.params().contains("lang")) {
                        output = mapper.writeValueAsString(db.findPveMapNames(req.params().get("lang")));
                    } else {
                        output = mapper.writeValueAsString(db.findPveMapNames());
                    }
                    break;
                case "pveworldnames":
                    req.response().putHeader("Cache-Control", "max-age=3600");//cache for 1h
                    if (req.params().contains("lang")) {
                        output = mapper.writeValueAsString(db.findPveWorldNames(req.params().get("lang")));
                    } else {
                        output = mapper.writeValueAsString(db.findPveWorldNames());
                    }
                    break;
                case "wvwmatches":
                    req.response().putHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                    req.response().putHeader("Pragma", "no-cache");
                    output = mapper.writeValueAsString(db.findWvwMatches());
                    break;
                case "wvwscores":
                    req.response().putHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                    req.response().putHeader("Pragma", "no-cache");
                    if (req.params().contains("match")) {
                        output = mapper.writeValueAsString(db.findWvwScores(req.params().get("match")));
                    } else {
                        output = mapper.writeValueAsString(db.findWvwScores());
                    }
                    break;
                case "wvwmapnames":
                    req.response().putHeader("Cache-Control", "max-age=3600");//cache for 1h
                    if (req.params().contains("lang")) {
                        output = mapper.writeValueAsString(db.findWvwMapNames(req.params().get("lang")));
                    } else {
                        output = mapper.writeValueAsString(db.findWvwMapNames());
                    }
                    break;
                case "wvwobjectivenames":
                    req.response().putHeader("Cache-Control", "max-age=3600");//cache for 1h
                    if (req.params().contains("lang")) {
                        output = mapper.writeValueAsString(db.findWvwObjectiveNames(req.params().get("lang")));
                    } else {
                        output = mapper.writeValueAsString(db.findWvwObjectiveNames());
                    }
                    break;
                case "wvwobjectivelongnames":
                    req.response().putHeader("Cache-Control", "max-age=3600");//cache for 1h
                    if (req.params().contains("lang")) {
                        output = mapper.writeValueAsString(db.findWvwObjectiveLongNames(req.params().get("lang")));
                    } else {
                        output = mapper.writeValueAsString(db.findWvwObjectiveLongNames());
                    }
                    break;
                case "wvwobjectivedetails":
                    req.response().putHeader("Cache-Control", "max-age=3600");//cache for 1h
                    output = mapper.writeValueAsString(db.findWvwObjectiveDetails());
                    break;
                case "guilddetails":
                    req.response().putHeader("Cache-Control", "max-age=3600");//cache for 1h
                    if (req.params().contains("guildid")) {
                        final List<GuildDetails> guilds = db.findGuildDetailsById(req.params().get("guildid"));
                        if (guilds.isEmpty()) {
                            final GuildDetails guild = client.getGuildDetails(req.params().get("guildid"));
                            if (guild != null) {
                                guilds.add(guild);
                                db.saveGuildDetails(guilds);
                            }
                        }
                        output = mapper.writeValueAsString(guilds);
                    } else {
                        if (req.params().contains("guildname")) {
                            output = mapper.writeValueAsString(db.findGuildDetailsById(req.params().get("guildname")));
                        } else {
                            output = "{\"error\":\"missing guildid or guildname parameter\"}";
                        }
                    }
                    break;
                case "wvwcoordinates":
                    req.response().putHeader("Cache-Control", "max-age=21600");//cache for 6h
                    output = mapper.writeValueAsString(db.findWvwCoordinates());
                    break;
                case "pvecoordinates":
                    req.response().putHeader("Cache-Control", "max-age=21600");//cache for 6h
                    output = mapper.writeValueAsString(db.findPveCoordinates());
                    break;
                case "chatmessages":
                    req.response().putHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                    req.response().putHeader("Pragma", "no-cache");
                    if (req.params().contains("channel")) {
                        output = mapper.writeValueAsString(db.findChatMessages(req.params().get("channel")));
                    } else {
                        output = mapper.writeValueAsString(db.findChatMessages());
                    }
                    break;
                case "pveeventdetails":
                    req.response().putHeader("Cache-Control", "max-age=3600");//cache for 1h
                    if (req.params().contains("map")) {
                        output = mapper.writeValueAsString(db.findPveEventDetails(Long.parseLong(req.params().get("map"))));
                    } else if (req.params().contains("event")) {
                        output = mapper.writeValueAsString(db.findPveEventDetails(req.params().get("event")));
                    } else {
                        output = mapper.writeValueAsString(db.findPveEventDetails());
                    }
                    break;
                case "maps":
                    req.response().putHeader("Cache-Control", "max-age=21600");//cache for 1h
                    if (req.params().contains("map")) {
                        output = mapper.writeValueAsString(db.findMap(req.params().get("map")));
                    } else {
                        output = "{\"error\":\"missing map parameter\"}";
                    }
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
