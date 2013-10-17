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
public class Bonuses {

    private String _type;
    private String _owner;

    public String getType() {
        return _type;
    }

    public void setType(final String type) {
        this._type = type;
    }

    public String getOwner() {
        return _owner;
    }

    public void setOwner(final String owner) {
        this._owner = owner;
    }
}
