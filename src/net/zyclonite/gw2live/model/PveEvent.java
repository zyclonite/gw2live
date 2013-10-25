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
import java.util.Objects;
import org.mongojack.ObjectId;

/**
 *
 * @author zyclonite
 */
@JsonIgnoreProperties({"_id"})
public class PveEvent implements Serializable {

    private Long _world_id;
    private Long _map_id;
    @ObjectId
    private String _event_id;
    private String _state;
    private Date _timestamp;

    public Long getWorld_id() {
        return _world_id;
    }

    public void setWorld_id(final Long id) {
        this._world_id = id;
    }

    public Long getMap_id() {
        return _map_id;
    }

    public void setMap_id(final Long id) {
        this._map_id = id;
    }

    public String getEvent_id() {
        return _event_id;
    }

    public void setEvent_id(final String id) {
        this._event_id = id;
    }

    public String getState() {
        return _state;
    }

    public void setState(final String state) {
        this._state = state;
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
        hash = 79 * hash + Objects.hashCode(this._world_id);
        hash = 79 * hash + Objects.hashCode(this._map_id);
        hash = 79 * hash + Objects.hashCode(this._event_id);
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
        final PveEvent other = (PveEvent) obj;
        //check first for stuff that is not in hashCode()
        if (!Objects.equals(this._state, other._state)) {
            return false;
        }
        if (!Objects.equals(this._world_id, other._world_id)) {
            return false;
        }
        if (!Objects.equals(this._map_id, other._map_id)) {
            return false;
        }
        if (!Objects.equals(this._event_id, other._event_id)) {
            return false;
        }
        return true;
    }
}
