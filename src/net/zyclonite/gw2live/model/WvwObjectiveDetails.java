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

/**
 *
 * @author zyclonite
 */
@JsonIgnoreProperties({"_id"})
public class WvwObjectiveDetails {

    private Long _id;
    private Long _income;

    public Long getId() {
        return _id;
    }

    public void setId(final Long id) {
        this._id = id;
    }

    public Long getIncome() {
        return _income;
    }

    public void setIncome(final Long income) {
        this._income = income;
    }
}
