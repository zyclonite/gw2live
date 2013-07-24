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
public class KeyValueLanguage {

    @ObjectId
    private String _id;
    private String _name;
    private String _lang;

    public String getId() {
        return _id;
    }

    public void setId(final String id) {
        this._id = id;
    }

    public String getName() {
        return _name;
    }

    public void setName(final String name) {
        this._name = name;
    }

    public String getLang() {
        return _lang;
    }

    public void setLang(final String lang) {
        this._lang = lang;
    }
}
