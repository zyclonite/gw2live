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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author zyclonite
 */
public class GwMapResult {

    private Map<String, GwMap> _maps = new HashMap<>();

    public Map<String, GwMap> getMaps() {
        return _maps;
    }
    
    public List<GwMap> getMapList() {
        final List<GwMap> result = new ArrayList<>();
        for (final Entry<String, GwMap> details : _maps.entrySet()) {
            final GwMap map = details.getValue();
            map.setMap_id(details.getKey().toString());
            result.add(map);
        }
        return result;
    }
}
