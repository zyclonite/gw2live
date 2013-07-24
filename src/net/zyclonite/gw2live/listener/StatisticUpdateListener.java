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

import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.zyclonite.gw2live.model.StatsItem;
import net.zyclonite.gw2live.service.EsperEngine;
import net.zyclonite.gw2live.service.MongoDB;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author zyclonite
 */
public class StatisticUpdateListener implements UpdateListener {

    private static final Log LOG = LogFactory.getLog(StatisticUpdateListener.class);
    private final EsperEngine esper;
    private final String name;
    private final String[] output;
    private final EPStatement statement;
    private final MongoDB db;

    public StatisticUpdateListener(final String name, final String[] output, final String epl) {
        esper = EsperEngine.getInstance();
        db = MongoDB.getInstance();
        db.initStatsCollections(name.toLowerCase());
        this.name = name;
        this.output = output;
        statement = esper.createEPL(epl);
        LOG.debug("StatisticUpdateListener initialized for " + name);
    }

    public String getName() {
        return name;
    }

    public String[] getOutput() {
        return output;
    }

    public void start() {
        if (statement.getUpdateListeners().hasNext()) {
            statement.start();
        } else {
            statement.addListener(this);
        }
    }

    public void stop() {
        statement.stop();
    }

    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        //TODO: needs threading (nonblocking) + push to eventbus
        final List<StatsItem> items = new ArrayList<>();
        for (final EventBean event : newEvents) {
            final StatsItem statsitem = new StatsItem();
            statsitem.setTimestamp(new Date());
            final Map<String, String> keyvalue = new HashMap<>();
            for (final String value : output) {
                keyvalue.put(value, event.get(value).toString());
            }
            statsitem.setKeyvalues(keyvalue);
            items.add(statsitem);
        }
        db.saveStats(name.toLowerCase(), items);
    }
}
