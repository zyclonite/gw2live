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

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import net.zyclonite.gw2live.model.GuildDetails;
import net.zyclonite.gw2live.model.GwMapResult;
import net.zyclonite.gw2live.model.KeyValueLanguage;
import net.zyclonite.gw2live.model.PveEventDetailsResult;
import net.zyclonite.gw2live.model.PveEventResult;
import net.zyclonite.gw2live.model.WvwMatchDetails;
import net.zyclonite.gw2live.model.WvwMatchResult;
import net.zyclonite.gw2live.util.AccessTokenResponse;
import net.zyclonite.gw2live.util.AppConfig;
import net.zyclonite.gw2live.util.Gw2RestInterface;
import net.zyclonite.gw2live.util.HttpEngine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

/**
 *
 * @author zyclonite
 */
public class Gw2Client {

    private static final Log LOG = LogFactory.getLog(Gw2Client.class);
    private static Gw2Client instance;
    private static AppConfig config;
    private final Gw2RestInterface gw2resource;
    private final ResteasyClient client;

    static {
        instance = new Gw2Client();
    }

    private Gw2Client() {
        config = AppConfig.getInstance();
        final ClientHttpEngine engine = HttpEngine.getHttpEngine();
        client = new ResteasyClientBuilder()
                .httpEngine(engine)
                .disableTrustManager()
                .build();
        final WebTarget target = client.target(config.getString("endpoints.gw2rest"));
        gw2resource = ProxyBuilder.builder(Gw2RestInterface.class, target).build();
        LOG.debug("Gw2Client initialized");
    }

    public void authenticateUser(final String username, final String password) {
        final WebTarget target = client.target(config.getString("endpoints.oauth"));
        target.register(new BasicAuthentication(username, password));
        final Form form = new Form().param("grant_type", "client_credentials");
        final AccessTokenResponse res = target.request().post(Entity.form(form), AccessTokenResponse.class);
        //TODO store user specific key to cache
        //gw2resource.getAuthResource("Bearer " + res.getToken(), "test");
    }

    public PveEventResult getPveEvents(final String worldid, final String mapid) {
        PveEventResult result = new PveEventResult();
        try {
            result = gw2resource.getPveEvents(worldid, mapid);
            LOG.debug(result.getEvents().size() + " pve-events found");
        } catch (Exception e) {
            LOG.warn("getPveEvents " + e.getMessage());
        }
        return result;
    }

    public List<KeyValueLanguage> getPveEventNames(final String lang) {
        List<KeyValueLanguage> result = new ArrayList<>();
        try {
            result = gw2resource.getPveEventNames(lang);
            LOG.debug(result.size() + " " + lang + " pve-event-names found");
        } catch (Exception e) {
            LOG.warn("getPveEventNames " + e.getMessage());
        }
        return result;
    }

    public List<KeyValueLanguage> getPveMapNames(final String lang) {
        List<KeyValueLanguage> result = new ArrayList<>();
        try {
            result = gw2resource.getPveMapNames(lang);
            LOG.debug(result.size() + " " + lang + " pve-map-names found");
        } catch (Exception e) {
            LOG.warn("getPveMapNames " + e.getMessage());
        }
        return result;
    }

    public List<KeyValueLanguage> getPveWorldNames(final String lang) {
        List<KeyValueLanguage> result = new ArrayList<>();
        try {
            result = gw2resource.getPveWorldNames(lang);
            LOG.debug(result.size() + " " + lang + " pve-world-names found");
        } catch (Exception e) {
            LOG.warn("getPveWorldNames " + e.getMessage());
        }
        return result;
    }

    public WvwMatchResult getWvwMatches() {
        WvwMatchResult result = new WvwMatchResult();
        try {
            result = gw2resource.getWvwMatches();
            LOG.debug(result.getWvw_matches().size() + " wvw-matches found");
        } catch (Exception e) {
            LOG.warn("getWvwMatches " + e.getMessage());
        }
        return result;
    }

    public WvwMatchDetails getWvwMatchDetails(final String matchid) {
        WvwMatchDetails result = new WvwMatchDetails();
        try {
            result = gw2resource.getWvwMatchDetails(matchid);
            LOG.debug("Details for matchid: " + result.getMatch_id() + " found");
        } catch (Exception e) {
            LOG.warn("getWvwMatchDetails " + e.getMessage());
        }
        return result;
    }

    public List<KeyValueLanguage> getWvwObjectiveNames(final String lang) {
        List<KeyValueLanguage> result = new ArrayList<>();
        try {
            result = gw2resource.getWvwObjectiveNames(lang);
            LOG.debug(result.size() + " " + lang + " wvw-objective-names found");
        } catch (Exception e) {
            LOG.warn("getWvwObjectiveNames " + e.getMessage());
        }
        return result;
    }

    public GuildDetails getGuildDetails(final String guildid) {
        GuildDetails result = null;
        try {
            result = gw2resource.getGuildDetails(guildid);
            LOG.trace("Details for guildid: " + result.getGuild_id() + " found");
        } catch (Exception e) {
            LOG.warn("getGuildDetails " + e.getMessage());
        }
        return result;
    }

    public PveEventDetailsResult getPveEventDetails() {
        PveEventDetailsResult result = new PveEventDetailsResult();
        try {
            result = gw2resource.getPveEventDetails(null, null);
            LOG.debug(result.getEvents().size() + " pve-event-details found");
        } catch (Exception e) {
            LOG.warn("getPveEventDetails " + e.getMessage());
        }
        return result;
    }

    public GwMapResult getMaps() {
        GwMapResult result = new GwMapResult();
        try {
            result = gw2resource.getMaps(null, null);
            LOG.debug(result.getMaps().size() + " maps found");
        } catch (Exception e) {
            LOG.warn("getMaps " + e.getMessage());
        }
        return result;
    }

    public static Gw2Client getInstance() {
        return instance;
    }
}
