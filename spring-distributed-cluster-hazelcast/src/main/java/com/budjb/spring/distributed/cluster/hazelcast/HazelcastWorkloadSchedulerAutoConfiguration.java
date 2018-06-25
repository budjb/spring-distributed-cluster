package com.budjb.spring.distributed.cluster.hazelcast;

import com.budjb.spring.distributed.cluster.ClusterConfigurationProperties;
import com.budjb.spring.distributed.cluster.ClusterManager;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class HazelcastWorkloadSchedulerAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    ClusterManager clusterManager(HazelcastInstance hazelcastInstance, ClusterConfigurationProperties clusterConfigurationProperties) {
        return new HazelcastClusterManager(hazelcastInstance, clusterConfigurationProperties);
    }
}
