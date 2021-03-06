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

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author zyclonite
 */
public class PlayerLocation implements Serializable {

    private String _id;
    private String _channel;
    private Date _timestamp;
    private String _identity;
    private Long _world_id;
    private Long _map_id;
    private Long _profession;
    private Boolean commander;
    private Long _team_color_id;
    private Double _x;
    private Double _y;
    private Double _z;

    public String getId() {
        return _id;
    }

    public void setId(final String id) {
        this._id = id;
    }

    public String getChannel() {
        return _channel;
    }

    public void setChannel(final String channel) {
        this._channel = channel;
    }

    public Date getTimestamp() {
        return _timestamp;
    }

    public void setTimestamp(final Date timestamp) {
        this._timestamp = timestamp;
    }

    public String getIdentity() {
        return _identity;
    }

    public void setIdentity(final String identity) {
        this._identity = identity;
    }

    public Long getWorld_id() {
        return _world_id;
    }

    public void setWorld_id(final Long world_id) {
        this._world_id = world_id;
    }

    public Long getMap_id() {
        return _map_id;
    }

    public void setMap_id(final Long map_id) {
        this._map_id = map_id;
    }

    public Double getX() {
        return _x;
    }

    public void setX(final Double x) {
        this._x = x;
    }

    public Double getY() {
        return _y;
    }

    public void setY(final Double y) {
        this._y = y;
    }

    public Double getZ() {
        return _z;
    }

    public void setZ(final Double z) {
        this._z = z;
    }

    /**
     * @return the _profession
     */
    public Long getProfession() {
        return _profession;
    }

    /**
     * @param _profession the _profession to set
     */
    public void setProfession(Long _profession) {
        this._profession = _profession;
    }

    /**
     * @return the commander
     */
    public Boolean getCommander() {
        return commander;
    }

    /**
     * @param commander the commander to set
     */
    public void setCommander(Boolean commander) {
        this.commander = commander;
    }

    /**
     * @return the _team_color_id
     */
    public Long getTeam_color_id() {
        return _team_color_id;
    }

    /**
     * @param _team_color_id the _team_color_id to set
     */
    public void setTeam_color_id(Long _team_color_id) {
        this._team_color_id = _team_color_id;
    }
}