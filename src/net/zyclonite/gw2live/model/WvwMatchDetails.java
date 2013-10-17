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

import java.util.List;

/**
 *
 * @author zyclonite
 */
public class WvwMatchDetails {

    private String _match_id;
    private List<Long> _scores;
    private List<WvwMap> _maps;
    private List<Bonuses> bonuses;

    public String getMatch_id() {
        return _match_id;
    }

    public void setMatch_id(final String id) {
        this._match_id = id;
    }

    public List<Long> getScores() {
        return _scores;
    }

    public void setScores(final List<Long> scores) {
        this._scores = scores;
    }

    public List<WvwMap> getMaps() {
        return _maps;
    }

    public void setMaps(final List<WvwMap> maps) {
        this._maps = maps;
    }

    public List<Bonuses> getBonuses() {
        return bonuses;
    }

    public void setBonuses(final List<Bonuses> bonuses) {
        this.bonuses = bonuses;
    }
}
