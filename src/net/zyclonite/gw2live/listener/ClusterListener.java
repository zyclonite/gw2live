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

import com.hazelcast.core.Member;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import java.util.UUID;
import net.zyclonite.gw2live.Application;
import net.zyclonite.gw2live.service.HazelcastCache;
import net.zyclonite.gw2live.util.LocalCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author zyclonite
 */
public final class ClusterListener implements MembershipListener {

    private static final Log LOG = LogFactory.getLog(ClusterListener.class);
    private final HazelcastCache hcache;
    private final long hash;

    public ClusterListener() {
        hcache = HazelcastCache.getInstance();
        final UUID ownid = UUID.fromString(hcache.getNodeId());
        hash = ownid.getLeastSignificantBits();
        LocalCache.MASTER = checkMeMaster();
        LOG.debug("Listener initialized - iAmMaster: " + LocalCache.MASTER);
    }

    @Override
    public void memberAdded(MembershipEvent event) {
        LocalCache.MASTER = checkMeMaster();
        LOG.debug("cluster member added - iAmMaster: " + LocalCache.MASTER);
        clusterChanged();
    }

    @Override
    public void memberRemoved(MembershipEvent event) {
        LocalCache.MASTER = checkMeMaster();
        LOG.debug("cluster member removed - iAmMaster: " + LocalCache.MASTER);
        clusterChanged();
    }

    public boolean checkMeMaster() {
        for (final Member member : hcache.getMembers()) {
            final UUID memberId = UUID.fromString(member.getUuid());
            if (hash > memberId.getLeastSignificantBits()) {
                return false;
            }
        }
        //master has lowest number in cluster
        return true;
    }
    
    private void clusterChanged(){
        if(LocalCache.MASTER){
            if(LocalCache.TIMERS.isEmpty()) {
                Application.switchMaster();
            }
        }else{
            Application.switchSlave();
        }
    }

    @Override
    public void memberAttributeChanged(MemberAttributeEvent mae) {
        
    }
}
