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
import java.util.Map;

/**
 *
 * @author zyclonite
 */
@JsonIgnoreProperties({"_id"})
public class StatsItem {

    private Date _timestamp;
    private Map<String, String> _keyvalues;

    public Date getTimestamp() {
        return _timestamp;
    }

    public void setTimestamp(final Date timestamp) {
        this._timestamp = timestamp;
    }

    public Map<String, String> getKeyvalues() {
        return _keyvalues;
    }

    public void setKeyvalues(final Map<String, String> keyvalues) {
        this._keyvalues = keyvalues;
    }
}
