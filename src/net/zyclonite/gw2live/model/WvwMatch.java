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
public class WvwMatch {

    private String _wvw_match_id;
    private long _red_world_id;
    private long _blue_world_id;
    private long _green_world_id;
    private Date _start_time;
    private Date _end_time;

    public String getWvw_match_id() {
        return _wvw_match_id;
    }

    public void setWvw_match_id(final String id) {
        this._wvw_match_id = id;
    }

    public long getRed_world_id() {
        return _red_world_id;
    }

    public void setRed_world_id(final long id) {
        this._red_world_id = id;
    }

    public long getGreen_world_id() {
        return _green_world_id;
    }

    public void setGreen_world_id(final long id) {
        this._green_world_id = id;
    }

    public long getBlue_world_id() {
        return _blue_world_id;
    }

    public void setBlue_world_id(final long id) {
        this._blue_world_id = id;
    }

    public Date getStart_time() {
        return _start_time;
    }

    public void setStart_time(final Date date) {
        this._start_time = date;
    }

    public Date getEnd_time() {
        return _end_time;
    }

    public void setEnd_time(final Date date) {
        this._end_time = date;
    }
}
