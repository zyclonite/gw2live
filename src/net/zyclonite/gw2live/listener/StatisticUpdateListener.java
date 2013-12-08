/*
 * gw2live - GuildWars 2 Dynamic Map
 * 
 * Website: http://gw2map.com
 *
 * Copyright 2013   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
package net.zyclonite.gw2live.listener;

import com.espertech.esper.client.EventBean;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.zyclonite.gw2live.model.StatsItem;
import net.zyclonite.gw2live.service.MongoDB;
import net.zyclonite.gw2live.util.EplUpdateListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author zyclonite
 */
public class StatisticUpdateListener extends EplUpdateListener {

    private static final Log LOG = LogFactory.getLog(StatisticUpdateListener.class);
    private final String[] output;
    private final MongoDB db;

    public StatisticUpdateListener(final String name, final String[] output, final String epl) {
        super(name, epl);
        db = MongoDB.getInstance();
        db.initStatsCollections(name.toLowerCase());
        this.output = output;
        LOG.debug("StatisticUpdateListener initialized for " + name);
    }

    public String[] getOutput() {
        return output;
    }

    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        //TODO: needs threading (nonblocking) + push to eventbus
        final List<StatsItem> items = new ArrayList<>();
        final Date now = new Date();
        for (final EventBean event : newEvents) {
            final StatsItem statsitem = new StatsItem();
            statsitem.setTimestamp(now);
            final Map<String, String> keyvalue = new HashMap<>();
            for (final String value : output) {
                keyvalue.put(value, event.get(value).toString());
            }
            statsitem.setKeyvalues(keyvalue);
            items.add(statsitem);
        }
        db.saveStats(getName().toLowerCase(), items);
    }
}
