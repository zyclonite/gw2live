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

import java.util.Map;

/**
 *
 * @author zyclonite
 */
public class JvmInfo {

    private int threadCount;
    private long committedHeap;
    private long usedHeap;
    private long committedNonHeap;
    private long usedNonHeap;
    private Map<String, MemInfo> memory;

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(final int count) {
        this.threadCount = count;
    }

    public long getCommitedHeap() {
        return committedHeap;
    }

    public void setCommitedHeap(final long count) {
        this.committedHeap = count;
    }

    public long getUsedHeap() {
        return usedHeap;
    }

    public void setUsedHeap(final long count) {
        this.usedHeap = count;
    }

    public long getCommitedNonHeap() {
        return committedNonHeap;
    }

    public void setCommitedNonHeap(final long count) {
        this.committedNonHeap = count;
    }

    public long getUsedNonHeap() {
        return usedNonHeap;
    }

    public void setUsedNonHeap(final long count) {
        this.usedNonHeap = count;
    }

    public Map<String, MemInfo> getMemInfo() {
        return memory;
    }

    public void setMemInfo(final Map<String, MemInfo> memory) {
        this.memory = memory;
    }
}
