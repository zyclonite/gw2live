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
public class GuildEmblem {

    private Long _background_id;
    private Long _foreground_id;
    private List<String> _flags;
    private Long _background_color_id;
    private Long _foreground_primary_color_id;
    private Long _foreground_secondary_color_id;

    public Long getBackground_id() {
        return _background_id;
    }

    public void setBackground_id(final Long id) {
        this._foreground_id = id;
    }

    public Long getForeground_id() {
        return _foreground_id;
    }

    public void setForeground_id(final Long id) {
        this._background_id = id;
    }

    public List<String> getFlags() {
        return _flags;
    }

    public void setFlags(final List<String> flags) {
        this._flags = flags;
    }

    public Long getBackground_color_id() {
        return _background_color_id;
    }

    public void setBackground_color_id(final Long id) {
        this._background_color_id = id;
    }

    public Long getForeground_primary_color_id() {
        return _foreground_primary_color_id;
    }

    public void setForeground_primary_color_id(final Long id) {
        this._foreground_primary_color_id = id;
    }

    public Long getForeground_secondary_color_id() {
        return _foreground_secondary_color_id;
    }

    public void setForeground_secondary_color_id(final Long id) {
        this._foreground_secondary_color_id = id;
    }
}
