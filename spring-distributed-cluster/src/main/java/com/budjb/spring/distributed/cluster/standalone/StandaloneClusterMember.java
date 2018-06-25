package com.budjb.spring.distributed.cluster.standalone;


import com.budjb.spring.distributed.cluster.ClusterMember;

/**
 * An implementation of {@link ClusterMember} used in conjunction with {@link StandaloneClusterManager}.
 */
public class StandaloneClusterMember extends ClusterMember {
    /**
     * Constructor.
     *
     * @param id ID of the member.
     */
    public StandaloneClusterMember(String id) {
        super(id);
    }
}
