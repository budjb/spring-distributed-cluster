package com.budjb.spring.distributed.cluster.hazelcast;

import com.budjb.spring.distributed.cluster.ClusterMember;
import com.hazelcast.core.Member;

/**
 * A cluster member backed by Hazelcast.
 */
public class HazelcastClusterMember extends ClusterMember {
    /**
     * Hazelcast cluster member.
     */
    private final Member member;

    /**
     * Constructor.
     *
     * @param member Hazelcast member.
     */
    public HazelcastClusterMember(Member member) {
        super(member.getUuid());
        this.member = member;
    }

    /**
     * Returns the Hazelcast member.
     *
     * @return the Hazelcast member.
     */
    public Member getMember() {
        return member;
    }
}
