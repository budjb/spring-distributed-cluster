package com.budjb.spring.distributed.cluster.support

import com.budjb.spring.distributed.cluster.ClusterMember

class TestClusterMember extends ClusterMember {
    /**
     * Constructor.
     *
     * @param id ID of the member.
     */
    TestClusterMember(String id) {
        super(id)
    }
}
