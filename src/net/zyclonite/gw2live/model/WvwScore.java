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
@JsonIgnoreProperties({"_id"})
public class WvwScore {

    private String _match_id;
    private String _map_type;
    private List<Long> _scores;

    public String getMatch_id() {
        return _match_id;
    }

    public void setMatch_id(final String id) {
        this._match_id = id;
    }

    public String getMap_type() {
        return _map_type;
    }

    public void setMap_type(final String maptype) {
        this._map_type = maptype;
    }

    public List<Long> getScores() {
        return _scores;
    }

    public void setScores(final List<Long> scores) {
        this._scores = scores;
    }
}
