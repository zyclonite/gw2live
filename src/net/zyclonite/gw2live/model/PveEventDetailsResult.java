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
public class PveEventDetailsResult {

    private Map<String, PveEventDetails> _events = new HashMap<>();

    public Map<String, PveEventDetails> getEvents() {
        return _events;
    }
    
    public List<PveEventDetails> getEventDetails() {
        final List<PveEventDetails> result = new ArrayList<>();
        for (final Entry<String, PveEventDetails> details : _events.entrySet()) {
            final PveEventDetails detail = details.getValue();
            detail.setEvent_id(details.getKey());
            result.add(detail);
        }
        return result;
    }
}
