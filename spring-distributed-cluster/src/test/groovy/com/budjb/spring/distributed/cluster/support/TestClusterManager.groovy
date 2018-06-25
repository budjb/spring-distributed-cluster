package com.budjb.spring.distributed.cluster.support

import com.budjb.spring.distributed.cluster.AbstractClusterManager
import com.budjb.spring.distributed.cluster.ClusterConfigurationProperties
import com.budjb.spring.distributed.cluster.ClusterMember
import com.budjb.spring.distributed.cluster.Instruction

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

class TestClusterManager extends AbstractClusterManager<TestClusterMember> {
    List<TestClusterMember> clusterMembers = []
    Map<ClusterMember, List<Instruction<?>>> instructions = [:]
    Map<String, Object> properties = [:]

    TestClusterManager(ClusterConfigurationProperties clusterConfigurationProperties) {
        super(clusterConfigurationProperties)
    }

    @Override
    <T> Future<T> submitInstruction(TestClusterMember clusterMember, Instruction<? extends T> instruction) {
        if (!instructions.containsKey(clusterMember)) {
            instructions.put(clusterMember, [])
        }

        instructions.get(clusterMember).add(instruction)

        if (instruction instanceof TestInstruction && instruction.future != null) {
            return instruction.future
        }
        else {
            CompletableFuture<T> future = new CompletableFuture<>()
            future.complete(null)
            return future
        }
    }

    @Override
    protected Map<String, Object> getProperties() {
        return properties
    }
}
