package com.budjb.spring.distributed.cluster

import com.budjb.spring.distributed.cluster.support.TestClusterManager
import com.budjb.spring.distributed.cluster.support.TestClusterMember
import com.budjb.spring.distributed.cluster.support.TestInstruction
import spock.lang.Specification

import java.util.concurrent.CompletableFuture

class AbstractClusterManagerSpec extends Specification {
    ClusterConfigurationProperties clusterProperties
    TestClusterManager clusterManager

    def setup() {
        clusterProperties = new ClusterConfigurationProperties()
        clusterManager = new TestClusterManager(clusterProperties)
    }

    def 'When only an instruction is submitted, it is submitted to all cluster members'() {
        setup:
        TestClusterMember a = new TestClusterMember('a')
        TestClusterMember b = new TestClusterMember('b')

        clusterManager.setClusterMembers([a, b])

        Instruction instruction = Mock(Instruction)

        when:
        clusterManager.submitInstruction(instruction)

        then:
        clusterManager.instructions == [
            (a): [instruction],
            (b): [instruction]
        ]
    }

    def 'When an instruction takes too long, an error is logged and its results are not returned'() {
        setup:
        clusterProperties.instructionTimeout = 1L
        TestClusterMember a = new TestClusterMember('a')
        TestClusterMember b = new TestClusterMember('b')

        clusterManager.setClusterMembers([a, b])

        CompletableFuture future = new CompletableFuture()
        TestInstruction instructionA = new TestInstruction()
        instructionA.future = future
        TestInstruction instructionB = new TestInstruction()

        when:
        Map<ClusterMember, Void> results = clusterManager.submitInstructions([(a): instructionA, (b): instructionB])

        then:
        results.containsKey(b)
        !results.containsKey(a)
    }
}
