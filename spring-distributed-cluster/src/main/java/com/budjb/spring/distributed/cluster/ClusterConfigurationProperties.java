package com.budjb.spring.distributed.cluster;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("cluster")
public class ClusterConfigurationProperties {
    /**
     * How long the cluster manager should wait for instructions to finish before giving up.
     */
    private long instructionTimeout = 120000L;

    public long getInstructionTimeout() {
        return instructionTimeout;
    }

    public void setInstructionTimeout(long instructionTimeout) {
        this.instructionTimeout = instructionTimeout;
    }
}
