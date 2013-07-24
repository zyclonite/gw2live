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
import org.mongojack.ObjectId;

/**
 *
 * @author zyclonite
 */
@JsonIgnoreProperties({"_id"})
public class GuildDetails {

    @ObjectId
    private String _guild_id;
    private String _guild_name;
    private String _tag;
    private GuildEmblem _emblem;

    public String getGuild_id() {
        return _guild_id;
    }

    public void setGuild_id(final String id) {
        this._guild_id = id;
    }

    public String getGuild_name() {
        return _guild_name;
    }

    public void setGuild_name(final String name) {
        this._guild_name = name;
    }

    public String getTag() {
        return _tag;
    }

    public void setTag(final String tag) {
        this._tag = tag;
    }

    public GuildEmblem getEmblem() {
        return _emblem;
    }

    public void setEmblem(final GuildEmblem emblem) {
        this._emblem = emblem;
    }
}
