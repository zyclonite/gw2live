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

import java.lang.management.MemoryUsage;

/**
 *
 * @author zyclonite
 */
public class MemInfo {

    private MemoryUsage usage;
    private MemoryUsage peakUsage;
    private MemoryUsage collectionUsage;

    public MemoryUsage getUsage() {
        return usage;
    }

    public void setUsage(final MemoryUsage usage) {
        this.usage = usage;
    }

    public MemoryUsage getPeakUsage() {
        return peakUsage;
    }

    public void setPeakUsage(final MemoryUsage usage) {
        this.peakUsage = usage;
    }

    public MemoryUsage getCollectionUsage() {
        return collectionUsage;
    }

    public void setCollectionUsage(final MemoryUsage usage) {
        this.collectionUsage = usage;
    }
}
