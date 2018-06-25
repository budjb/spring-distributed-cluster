package com.budjb.spring.distributed.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * A (slightly opinionated) base implementation of {@link ClusterManager}. This implementation assumes that
 * distributed properties are contained in some distributed {@link Map}.
 *
 * @param <M> type of the cluster member.
 */
public abstract class AbstractClusterManager<M extends ClusterMember> implements ClusterManager<M> {
    /**
     * Cluster properties.
     */
    private final ClusterConfigurationProperties clusterConfigurationProperties;

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(AbstractClusterManager.class);

    /**
     * Constructor.
     *
     * @param clusterConfigurationProperties Scheduler configuration properties.
     */
    protected AbstractClusterManager(ClusterConfigurationProperties clusterConfigurationProperties) {
        this.clusterConfigurationProperties = clusterConfigurationProperties;
    }

    /**
     * Submits the given instruction to the given cluster member.
     *
     * @param clusterMember Cluster member to run the instruction on.
     * @param instruction   Instruction to run.
     * @param <T>           The return type of the instruction.
     * @return Results of the instruction.
     */
    protected abstract <T> Future<? extends T> submitInstruction(M clusterMember, Instruction<? extends T> instruction);

    /**
     * Returns the map that backs distributed properties.
     *
     * @return The map that backs distributed properties.
     */
    protected abstract Map<String, Object> getProperties();

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Map<M, T> submitInstruction(Instruction<? extends T> instruction) throws ExecutionException, InterruptedException {
        Map<M, Instruction<? extends T>> instructions = new HashMap<>();

        for (M member : getClusterMembers()) {
            instructions.put(member, instruction);
        }

        return submitInstructions(instructions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Map<M, T> submitInstructions(Map<M, ? extends Instruction<? extends T>> instructions) throws ExecutionException, InterruptedException {
        Map<M, Future<? extends T>> futures = new HashMap<>();

        for (Map.Entry<M, ? extends Instruction<? extends T>> assignment : instructions.entrySet()) {
            if (assignment.getValue() != null) {
                futures.put(assignment.getKey(), submitInstruction(assignment.getKey(), assignment.getValue()));
            }
        }

        long end = System.currentTimeMillis() + clusterConfigurationProperties.getInstructionTimeout();

        Map<M, T> results = new HashMap<>();

        while (futures.size() > 0 && System.currentTimeMillis() < end) {
            Iterator<Map.Entry<M, Future<? extends T>>> iterator = futures.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<M, Future<? extends T>> entry = iterator.next();
                if (entry.getValue().isDone()) {
                    results.put(entry.getKey(), entry.getValue().get());
                    iterator.remove();
                }
            }
        }

        if (futures.size() > 0) {
            for (Map.Entry<M, Future<? extends T>> entry : futures.entrySet()) {
                log.error("Cluster member " + entry.getKey().toString() + " did not complete its instructions within " + clusterConfigurationProperties.getInstructionTimeout() + " milliseconds");
            }
        }

        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProperty(String name) {
        return getProperty(name, String.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getProperty(String name, Class<T> clazz) {
        return getProperty(name, clazz, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String name, Class<T> clazz, T defaultValue) {
        Map<String, Object> properties = getProperties();

        if (!properties.containsKey(name)) {
            return defaultValue;
        }

        Object value = properties.get(name);

        if (!clazz.isInstance(value)) {
            return defaultValue;
        }

        return (T) value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProperty(String name, Object value) {
        getProperties().put(name, value);
    }

    /**
     * Returns the cluster configuration properties.
     *
     * @return The cluster configuration properties.
     */
    protected ClusterConfigurationProperties getClusterConfigurationProperties() {
        return clusterConfigurationProperties;
    }
}
