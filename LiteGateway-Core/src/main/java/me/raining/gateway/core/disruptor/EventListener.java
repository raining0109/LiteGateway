package me.raining.gateway.core.disruptor;

/**
 * @author raining
 * @version 1.0.0
 * @description 事件监听器 监听接口
 */
public interface EventListener<E> {

    void onEvent(E event);

    /**
     * @param ex
     * @param sequence 异常执行顺序
     * @param event
     */
    void onException(Throwable ex, long sequence, E event);

}
