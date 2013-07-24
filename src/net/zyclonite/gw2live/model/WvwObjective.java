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

/**
 *
 * @author zyclonite
 */
public class WvwObjective {

    private long _id;
    private String _owner;
    private String _owner_guild;

    public long getId() {
        return _id;
    }

    public void setId(final long id) {
        this._id = id;
    }

    public String getOwner() {
        return _owner;
    }

    public void setOwner(final String owner) {
        this._owner = owner;
    }

    public String getOwner_guild() {
        return _owner_guild;
    }

    public void setOwner_guild(final String owner_guild) {
        this._owner_guild = owner_guild;
    }
}
