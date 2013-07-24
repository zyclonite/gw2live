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

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author zyclonite
 */
public class Subscriber implements Serializable {

    private final String connection;
    private final String nodeid;
    private String subscriptionid;
    private String channelid;
    private String nickname;

    public Subscriber(final String connection, final String nodeid) {
        this.connection = connection;
        this.nodeid = nodeid;
    }

    public String getConnection() {
        return connection;
    }

    public String getNodeId() {
        return nodeid;
    }

    public String getSubscriptionId() {
        return subscriptionid;
    }

    public void setSubscriptionId(final String subscriptionid) {
        this.subscriptionid = subscriptionid;
    }

    public String getChannelId() {
        return channelid;
    }

    public void setChannelId(final String channelid) {
        this.channelid = channelid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.connection);
        hash = 79 * hash + Objects.hashCode(this.nodeid);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Subscriber other = (Subscriber) obj;
        if (!Objects.equals(this.connection, other.connection)) {
            return false;
        }
        if (!Objects.equals(this.nodeid, other.nodeid)) {
            return false;
        }
        return true;
    }
}