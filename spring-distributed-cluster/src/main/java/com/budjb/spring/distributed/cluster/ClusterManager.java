package com.budjb.spring.distributed.cluster;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Describes a class that allows interaction with other running application nodes
 * that are considered clustered. This functionality serves to help synchronize
 * state between application nodes so that meaningful and stateful load balancing
 * may occur.
 *
 * @param <CM> Cluster member implementation.
 */
public interface ClusterManager<CM extends ClusterMember> {
    /**
     * Returns the set of cluster members that will share workloads.
     *
     * @return the set of cluster members that will share workloads.
     */
    List<CM> getClusterMembers();

    /**
     * Retrieves a distributed property.
     * <p>
     * Note that just as with accessing shared state between threads, access to properties should be synchronized
     * across all cluster members through some sort of locking mechanism.
     *
     * @param name Name of the property.
     * @return The value of the property, or {@code null} if it is not set.
     */
    String getProperty(String name);

    /**
     * Retrieves a distributed property.
     * <p>
     * The value of the property, if it exists, must be of the requested type. If it does not exist, or the value
     * does match the type, {@code null} is returned.
     * <p>
     * Note that just as with accessing shared state between threads, access to properties should be synchronized
     * across all cluster members through some sort of locking mechanism.
     *
     * @param name  Name of the property.
     * @param clazz Required class type of the property.
     * @param <T>   Requested value type.
     * @return The value of the property, or {@code null} if it is not set or is not of the correct type.
     */
    <T> T getProperty(String name, Class<T> clazz);

    /**
     * Retrieves a distributed property.
     * <p>
     * The value of the property, if it exists, must be of the requested type. If it does not exist, or the value
     * does match the type, the default value is returned.
     * <p>
     * Note that just as with accessing shared state between threads, access to properties should be synchronized
     * across all cluster members through some sort of locking mechanism.
     *
     * @param name         Name of the property.
     * @param clazz        Required class type of the property.
     * @param defaultValue Default value to return if the property isn't set or is of the wrong type.
     * @param <T>          Requested value type.
     * @return The value of the property, or the default value if it is not set or is not of the correct type.
     */
    <T> T getProperty(String name, Class<T> clazz, T defaultValue);

    /**
     * Sets a distributed property and syncs it with all cluster members.
     * <p>
     * Note that just as with accessing shared state between threads, access to properties should be synchronized
     * across all cluster members through some sort of locking mechanism.
     *
     * @param name  Name of the property.
     * @param value Value of the property.
     */
    void setProperty(String name, Object value);

    /**
     * Submits instructions to members of the cluster.
     *
     * @param instructions instructions to run on cluster members.
     * @param <T>          The return type of the instruction.
     * @return the results of the instructions, mapped to the member that generated them.
     * @throws ExecutionException   when an error during execution occurs.
     * @throws InterruptedException when the processes is interrupted.
     */
    <T> Map<CM, ? extends T> submitInstructions(Map<CM, ? extends Instruction<? extends T>> instructions) throws ExecutionException, InterruptedException;

    /**
     * Submits an instruction to all cluster members.
     *
     * @param instruction instructions to run on all cluster members.
     * @param <T>         The return type of the instruction.
     * @return the results of the instructions, mapped to the member that generated them.
     * @throws ExecutionException   when an error during execution occurs.
     * @throws InterruptedException when the processes is interrupted.
     */
    <T> Map<CM, T> submitInstruction(Instruction<? extends T> instruction) throws ExecutionException, InterruptedException;
}