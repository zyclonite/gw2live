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
import java.util.List;

/**
 *
 * @author zyclonite
 */
@JsonIgnoreProperties({"_id", "name"})
public class PveEventDetails {

    private String _event_id;
    private Long _level;
    private Long _map_id;
    private List<String> _flags;
    private Location _location;

    public String getEvent_id() {
        return _event_id;
    }

    public void setEvent_id(final String id) {
        this._event_id = id;
    }

    public Long getLevel() {
        return _level;
    }

    public void setLevel(final Long level) {
        this._level = level;
    }

    public Long getMap_id() {
        return _map_id;
    }

    public void setMap_id(final Long id) {
        this._map_id = id;
    }

    public List<String> getFlags() {
        return _flags;
    }

    public void setFlags(final List<String> flags) {
        this._flags = flags;
    }

    public Location getLocation() {
        return _location;
    }

    public void setLocation(final Location location) {
        this._location = location;
    }
}
