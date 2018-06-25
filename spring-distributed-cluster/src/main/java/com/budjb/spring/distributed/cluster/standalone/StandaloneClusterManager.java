package com.budjb.spring.distributed.cluster.standalone;

import com.budjb.spring.distributed.cluster.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * An implementation of {@link ClusterManager} that only contains a single node. While this class may seem to violate
 * the purpose of the library, this implementation is useful for local developing and testing.
 */
public class StandaloneClusterManager extends AbstractClusterManager implements ApplicationContextAware {
    /**
     * Executor service.
     */
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Array of cluster members.
     * <p>
     * This will only ever contain one, but we maintain a list to meet the API contract.
     */
    private final List<ClusterMember> clusterMembers;

    /**
     * Properties map.
     */
    private final Map<String, Object> properties = new HashMap<>();

    /**
     * Bean factory.
     */
    private AutowireCapableBeanFactory beanFactory;

    /**
     * Constructor.
     *
     * @param clusterConfigurationProperties Scheduler configuration properties.
     */
    public StandaloneClusterManager(ClusterConfigurationProperties clusterConfigurationProperties) {
        this(clusterConfigurationProperties, new StandaloneClusterMember("localhost"));
    }

    /**
     * Constructor.
     *
     * @param clusterConfigurationProperties Scheduler configuration properties.
     * @param member                         Cluster member.
     */
    public StandaloneClusterManager(ClusterConfigurationProperties clusterConfigurationProperties, StandaloneClusterMember member) {
        super(clusterConfigurationProperties);
        this.clusterMembers = new ArrayList<>();
        this.clusterMembers.add(member);
    }

    /**
     * Sets the bean factory used to autowire and initialize instructions.
     *
     * @param beanFactory Bean factory instance.
     */
    public void setBeanFactory(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected <T> Future<? extends T> submitInstruction(ClusterMember clusterMember, Instruction<? extends T> instruction) {
        beanFactory.autowireBean(instruction);
        instruction = (Instruction<? extends T>) beanFactory.initializeBean(instruction, instruction.getClass().getName());
        return executorService.submit(instruction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ClusterMember> getClusterMembers() {
        return clusterMembers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        setBeanFactory(applicationContext.getAutowireCapableBeanFactory());
    }
}
