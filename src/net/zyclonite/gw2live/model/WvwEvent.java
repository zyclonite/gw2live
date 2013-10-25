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
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author zyclonite
 */
@JsonIgnoreProperties({"_id"})
public class WvwEvent implements Serializable {

    private String _match_id;
    private String _map_type;
    private Long _objective_id;
    private String _owner;
    private String _owner_guild;
    private List<Long>_map_scores;
    private List<Long> _match_scores;
    private Date _timestamp;

    public String getMatch_id() {
        return _match_id;
    }

    public void setMatch_id(final String matchId) {
        this._match_id = matchId;
    }

    public String getMap_type() {
        return _map_type;
    }

    public void setMap_type(final String type) {
        this._map_type = type;
    }

    public Long getObjective_id() {
        return _objective_id;
    }

    public void setObjective_id(final Long objId) {
        this._objective_id = objId;
    }

    public String getOwner() {
        return _owner;
    }

    public void setOwner(final String owner) {
        this._owner = owner;
    }

    public String getOwner_guild() {
        return _owner_guild;
    }

    public void setOwner_guild(final String ownerGuild) {
        this._owner_guild = ownerGuild;
    }

    public List<Long> getMap_scores() {
        return _map_scores;
    }

    public void setMap_scores(final List<Long> scores) {
        this._map_scores = scores;
    }

    public List<Long> getMatch_scores() {
        return _match_scores;
    }

    public void setMatch_scores(final List<Long> scores) {
        this._match_scores = scores;
    }

    public Date getTimestamp() {
        return _timestamp;
    }

    public void setTimestamp(final Date timestamp) {
        this._timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this._match_id);
        hash = 79 * hash + Objects.hashCode(this._map_type);
        hash = 79 * hash + Objects.hashCode(this._objective_id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WvwEvent other = (WvwEvent) obj;
        //check first for stuff that is not in hashCode()
        if (!Objects.equals(this._owner, other._owner)) {
            return false;
        }
        if (!Objects.equals(this._owner_guild, other._owner_guild)) {
            return false;
        }
        if (!Objects.equals(this._match_id, other._match_id)) {
            return false;
        }
        if (!Objects.equals(this._map_type, other._map_type)) {
            return false;
        }
        if (!Objects.equals(this._objective_id, other._objective_id)) {
            return false;
        }
        return true;
    }
}
