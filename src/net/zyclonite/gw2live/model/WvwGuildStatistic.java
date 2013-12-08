/*
 * gw2live - GuildWars 2 Dynamic Map
 * 
 * Website: http://gw2map.com
 *
 * Copyright 2013   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
package net.zyclonite.gw2live.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;

/**
 *
 * @author zyclonite
 */
@JsonIgnoreProperties({"_id"})
public class WvwGuildStatistic {

    private String _match_id;
    private String _map_type;
    private Long _objective_id;
    private String _guild_id;
    private Date _timestamp;
    private Long _holdtime;

    public String getMatch_id() {
        return _match_id;
    }

    public void setMatch_id(final String _match_id) {
        this._match_id = _match_id;
    }

    public String getMap_type() {
        return _map_type;
    }

    public void setMap_type(final String _map_type) {
        this._map_type = _map_type;
    }

    public Long getObjective_id() {
        return _objective_id;
    }

    public void setObjective_id(final Long _objective_id) {
        this._objective_id = _objective_id;
    }

    public String getGuild_id() {
        return _guild_id;
    }

    public void setGuild_id(final String _guild_id) {
        this._guild_id = _guild_id;
    }

    public Date getTimestamp() {
        return _timestamp;
    }

    public void setTimestamp(final Date _timestamp) {
        this._timestamp = _timestamp;
    }

    public Long getHoldtime() {
        return _holdtime;
    }

    public void setHoldtime(final Long _holdtime) {
        this._holdtime = _holdtime;
    }
}
