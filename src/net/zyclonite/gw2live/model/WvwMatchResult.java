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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zyclonite
 */
public class WvwMatchResult {

    private List<WvwMatch> _wvw_matches = new ArrayList<>();

    public List<WvwMatch> getWvw_matches() {
        return _wvw_matches;
    }
}
