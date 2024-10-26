package me.raining.gateway.core.disruptor;

/**
 * @author raining
 * @version 1.0.0
 * @description
 */
public interface ParallelQueue<E> {

    /**
     * 添加元素
     */
    void add(E event);

    void add(E... event);

    /**
     * 添加多个元素 返回是否添加成功的标志
     */
    boolean tryAdd(E event);

    boolean tryAdd(E... event);

    /**
     * 启动
     */
    void start();

    /**
     * 销毁
     */
    void shutDown();

    /**
     * 判断是否已经销毁
     */
    boolean isShutDown();
}
