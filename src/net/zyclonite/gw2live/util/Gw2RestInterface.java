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

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import net.zyclonite.gw2live.model.GuildDetails;
import net.zyclonite.gw2live.model.GwMapResult;
import net.zyclonite.gw2live.model.KeyValueLanguage;
import net.zyclonite.gw2live.model.PveEventDetailsResult;
import net.zyclonite.gw2live.model.PveEventResult;
import net.zyclonite.gw2live.model.WvwMatchDetails;
import net.zyclonite.gw2live.model.WvwMatchResult;

/**
 *
 * @author zyclonite
 */
public interface Gw2RestInterface {

    @Path("/events.json")
    @GET
    PveEventResult getPveEvents(@QueryParam("world_id") String world_id, @QueryParam("map_id") String map_id);

    @Path("/event_names.json")
    @GET
    List<KeyValueLanguage> getPveEventNames(@QueryParam("lang") String lang);

    @Path("/map_names.json")
    @GET
    List<KeyValueLanguage> getPveMapNames(@QueryParam("lang") String lang);

    @Path("/world_names.json")
    @GET
    List<KeyValueLanguage> getPveWorldNames(@QueryParam("lang") String lang);

    @Path("/wvw/matches.json")
    @GET
    WvwMatchResult getWvwMatches();

    @Path("/wvw/match_details.json")
    @GET
    WvwMatchDetails getWvwMatchDetails(@QueryParam("match_id") String match_id);

    @Path("/wvw/objective_names.json")
    @GET
    List<KeyValueLanguage> getWvwObjectiveNames(@QueryParam("lang") String lang);

    @Path("/guild_details.json")
    @GET
    GuildDetails getGuildDetails(@QueryParam("guild_id") String guild_id);

    @Path("/event_details.json")
    @GET
    PveEventDetailsResult getPveEventDetails(@QueryParam("event_id") String event_id, @QueryParam("lang") String lang);

    @Path("/maps.json")
    @GET
    GwMapResult getMaps(@QueryParam("map_id") String event_id, @QueryParam("lang") String lang);
    
    @Path("/needsauthentication.json")//only as an example for oauth request
    @GET
    GwMapResult getAuthResource(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @QueryParam("someparam") String someparam);
}
