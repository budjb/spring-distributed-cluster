package com.budjb.spring.distributed.cluster;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DistributedClusterAutoConfiguration {
    @Bean
    ClusterConfigurationProperties clusterConfigurationProperties() {
        return new ClusterConfigurationProperties();
    }
}
