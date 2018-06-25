package com.budjb.spring.distributed.cluster;

import com.budjb.spring.distributed.cluster.standalone.StandaloneClusterManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DistributedClusterAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    ClusterManager clusterManager(ClusterConfigurationProperties clusterConfigurationProperties) {
        return new StandaloneClusterManager(clusterConfigurationProperties);
    }

    @Bean
    ClusterConfigurationProperties clusterConfigurationProperties() {
        return new ClusterConfigurationProperties();
    }
}
