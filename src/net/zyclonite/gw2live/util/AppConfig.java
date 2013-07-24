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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author zyclonite
 */
public class AppConfig extends XMLConfiguration {

    private static final Log LOG = LogFactory.getLog(AppConfig.class);
    private static AppConfig instance;
    private static final String configFile = "config.xml";

    static {
        instance = new AppConfig(configFile);
    }

    private AppConfig(final String fileName) {
        super();
        this.setReloadingStrategy(new FileChangedReloadingStrategy());
        this.setDelimiterParsingDisabled(true);
        init(fileName);
    }

    private void init(final String fileName) {
        setFileName(fileName);
        try {
            load();
            LOG.info("Configuration loaded");
        } catch (ConfigurationException ex) {
            LOG.error("Configuration not loaded!");
        }
    }

    public static AppConfig getInstance() {
        return instance;
    }
}
