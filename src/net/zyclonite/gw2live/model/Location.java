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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;

/**
 *
 * @author zyclonite
 */
@JsonInclude(Include.NON_NULL)
public class Location {

    private String _type;//cylinder, sphere, poly
    private List<Double> _center;
    private Double _height;
    private Double _radius;
    private Double _rotation;
    private List<Long> _z_range;
    private List<List<Double>> _points;

    public String getType() {
        return _type;
    }

    public void setType(final String type) {
        this._type = type;
    }

    public List<Double> getCenter() {
        return _center;
    }

    public void setCenter(final List<Double> center) {
        this._center = center;
    }

    public Double getHeight() {
        return _height;
    }

    public void setHeight(final Double height) {
        this._height = height;
    }

    public Double getRadius() {
        return _radius;
    }

    public void setRadius(final Double radius) {
        this._radius = radius;
    }

    public Double getRotation() {
        return _rotation;
    }

    public void setRotation(final Double rotation) {
        this._rotation = rotation;
    }

    public List<Long> getZ_range() {
        return _z_range;
    }

    public void setZ_range(final List<Long> range) {
        this._z_range = range;
    }

    public List<List<Double>> getPoints() {
        return _points;
    }

    public void setPoints(final List<List<Double>> points) {
        this._points = points;
    }
}
