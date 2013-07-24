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
import org.mongojack.ObjectId;

/**
 *
 * @author zyclonite
 */
@JsonIgnoreProperties({"_id"})
public class GwMap {

    @ObjectId
    private String _map_id;
    private String _map_name;
    private Long _min_level;
    private Long _max_level;
    private Long _default_floor;
    private List<Long> _floors;
    private Long _region_id;
    private String _region_name;
    private Long _continent_id;
    private String _continent_name;
    private List<List<Long>> _map_rect;
    private List<List<Long>> _continent_rect;

    public String getMap_id() {
        return _map_id;
    }

    public void setMap_id(final String id) {
        this._map_id = id;
    }

    public String getMap_name() {
        return _map_name;
    }

    public void setMap_name(final String name) {
        this._map_name = name;
    }

    public Long getMin_level() {
        return _min_level;
    }

    public void setMin_level(final Long level) {
        this._min_level = level;
    }

    public Long getMax_level() {
        return _max_level;
    }

    public void setMax_level(final Long level) {
        this._max_level = level;
    }

    public Long getDefault_floor() {
        return _default_floor;
    }

    public void setDefault_floor(final Long floor) {
        this._default_floor = floor;
    }

    public List<Long> getFloors() {
        return _floors;
    }

    public void setFloors(final List<Long> floors) {
        this._floors = floors;
    }

    public Long getRegion_id() {
        return _region_id;
    }

    public void setRegion_id(final Long id) {
        this._region_id = id;
    }

    public String getRegion_name() {
        return _region_name;
    }

    public void setRegion_name(final String name) {
        this._region_name = name;
    }

    public Long getContinent_id() {
        return _continent_id;
    }

    public void setContinent_id(final Long id) {
        this._continent_id = id;
    }

    public String getContinent_name() {
        return _continent_name;
    }

    public void setContinent_name(final String name) {
        this._continent_name = name;
    }

    public List<List<Long>> getMap_rect() {
        return _map_rect;
    }

    public void setMap_rect(final List<List<Long>> rect) {
        this._map_rect = rect;
    }

    public List<List<Long>> getContinent_rect() {
        return _continent_rect;
    }

    public void setContinent_rect(final List<List<Long>> rect) {
        this._continent_rect = rect;
    }
}
