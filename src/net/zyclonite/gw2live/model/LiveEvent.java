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
public class LiveEvent {

    private String _type;
    private Object _data;

    public String getType() {
        return _type;
    }

    public void setType(final String type) {
        this._type = type;
    }

    public Object getData() {
        return _data;
    }

    public void setData(final Object data) {
        this._data = data;
    }
}
