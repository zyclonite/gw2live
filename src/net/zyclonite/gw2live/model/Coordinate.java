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
import org.mongojack.ObjectId;

/**
 *
 * @author zyclonite
 */
@JsonIgnoreProperties({"_id"})
public class Coordinate {

    @ObjectId
    private String _id;
    private Double _x;
    private Double _y;

    public String getId() {
        return _id;
    }

    public void setId(final String id) {
        this._id = id;
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
}
