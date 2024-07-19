package org.sycamore.llmhub.infrastructure.cache;

/**
 * 缓存加载器
 *
 *  
 */
@FunctionalInterface
public interface CacheLoader<T> {

    /**
     * 加载缓存
     */
    T load();
}
