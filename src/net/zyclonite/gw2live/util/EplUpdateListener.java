/*
 * gw2live - GuildWars 2 Dynamic Map
 * 
 * Website: http://gw2map.com
 *
 * Copyright 2013   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
package net.zyclonite.gw2live.util;

import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.UpdateListener;
import net.zyclonite.gw2live.service.EsperEngine;

/**
 *
 * @author zyclonite
 */
public abstract class EplUpdateListener implements UpdateListener {

    private final EsperEngine esper;
    private final String name;
    private final EPStatement statement;

    public EplUpdateListener(final String name, final String epl) {
        esper = EsperEngine.getInstance();
        this.name = name;
        statement = esper.createEPL(epl);
    }

    public String getName() {
        return name;
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
}
