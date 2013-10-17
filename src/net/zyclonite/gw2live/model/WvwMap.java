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
public class WvwMap {

    private String _type;
    private List<Long> _scores;
    private List<WvwObjective> _objectives;
    private List<Bonuses> _bonuses;

    public String getType() {
        return _type;
    }

    public void setType(final String type) {
        this._type = type;
    }

    public List<Long> getScores() {
        return _scores;
    }

    public void setScores(final List<Long> scores) {
        this._scores = scores;
    }

    public List<WvwObjective> getObjectives() {
        return _objectives;
    }

    public void setObjectives(final List<WvwObjective> objectives) {
        this._objectives = objectives;
    }

    public List<Bonuses> getBonuses() {
        return _bonuses;
    }

    public void setBonuses(final List<Bonuses> bonuses) {
        this._bonuses = bonuses;
    }
}
